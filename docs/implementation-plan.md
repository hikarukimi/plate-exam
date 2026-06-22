# 实施计划：请假与考勤 OA（plate-exam）

> 本文是一次性实施清单，确认后据此动手。术语见 [CONTEXT.md](../CONTEXT.md)，关键决策见 docs/adr/。

## 1. 已锁定的设计前提

- 场景：请假管理 + 考勤管理
- 角色：普通员工 / 部门负责人 / 系统管理员（admin）
- 工作流：状态机驱动，四态 `pending → approved → archived`，旁路 `rejected`（终态）
- 考勤：员工×日期×状态最小表，"请假归档"自动写考勤 + 管理员手动维护
- 实现：手写代码，遵循若依生成器规范（不用生成器、不用 BPMN 引擎）

## 2. 建表 SQL（新增 `sql/oa_module.sql`）

```sql
-- 请假申请表
create table oa_leave (
  leave_id       bigint(20)      not null auto_increment    comment '请假申请ID',
  user_id        bigint(20)      not null                   comment '申请人ID（sys_user）',
  user_name      varchar(30)     default ''                 comment '申请人姓名（冗余便于展示）',
  dept_id        bigint(20)      not null                   comment '申请人部门ID（sys_dept）',
  leave_type     varchar(20)     not null                   comment '请假类型（字典 oa_leave_type）',
  start_date     date            not null                   comment '开始日期',
  end_date       date            not null                   comment '结束日期',
  days           decimal(4,1)    default 0                  comment '请假天数',
  reason         varchar(500)    default ''                 comment '请假事由',
  status         varchar(20)     default 'pending'          comment '流程状态（pending/approved/rejected/archived）',
  approver_id    bigint(20)      default null               comment '审批人ID（部门负责人）',
  approve_remark varchar(500)    default ''                 comment '审批意见',
  approve_time   datetime        default null               comment '审批时间',
  archive_by     varchar(64)     default ''                 comment '归档人',
  archive_time   datetime        default null               comment '归档时间',
  create_by      varchar(64)     default ''                 comment '创建者',
  create_time    datetime        default null               comment '创建时间',
  update_by      varchar(64)     default ''                 comment '更新者',
  update_time    datetime        default null               comment '更新时间',
  remark         varchar(500)    default null               comment '备注',
  primary key (leave_id)
) engine=innodb auto_increment=1 comment = '请假申请表';

-- 考勤记录表
create table oa_attendance (
  att_id         bigint(20)      not null auto_increment    comment '考勤记录ID',
  user_id        bigint(20)      not null                   comment '员工ID（sys_user）',
  user_name      varchar(30)     default ''                 comment '员工姓名',
  dept_id        bigint(20)      not null                   comment '部门ID（sys_dept）',
  att_date       date            not null                   comment '考勤日期',
  att_status     varchar(20)     default 'normal'           comment '考勤状态（字典 oa_att_status：normal/leave/absent/late）',
  source         char(1)         default '0'                comment '来源（0手动 1请假联动）',
  leave_id       bigint(20)      default null               comment '关联请假申请ID（联动来源时）',
  create_by      varchar(64)     default ''                 comment '创建者',
  create_time    datetime        default null               comment '创建时间',
  update_by      varchar(64)     default ''                 comment '更新者',
  update_time    datetime        default null               comment '更新时间',
  remark         varchar(500)    default null               comment '备注',
  primary key (att_id),
  unique key uk_user_date (user_id, att_date)
) engine=innodb auto_increment=1 comment = '考勤记录表';
```

## 3. 初始化 SQL（新增 `sql/oa_init.sql`）

- **字典**：`oa_leave_type`（年假/事假/病假/调休…）、`oa_att_status`（正常/请假/缺勤/迟到）→ 写入 `sys_dict_type`、`sys_dict_data`
- **菜单**：新增一级菜单"OA办公"，子菜单"请假管理""考勤管理"，及对应按钮权限（`oa:leave:*`、`oa:attendance:*`、`oa:leave:approve`、`oa:leave:archive`）→ 写入 `sys_menu`
- **角色**：新增"普通员工""部门负责人"两个角色 → 写入 `sys_role`、`sys_role_menu`（admin 已是全权）

## 4. 后端文件清单（遵循若依规范）

包根：`ruoyi-system/.../com/ruoyi/oa/`（domain/mapper/service/service/impl），控制器置于 `ruoyi-admin/.../web/controller/oa/`

| 文件 | 职责 |
|---|---|
| `oa/domain/OaLeave.java` | 请假实体（extends BaseEntity） |
| `oa/domain/OaAttendance.java` | 考勤实体 |
| `oa/mapper/OaLeaveMapper.java` + `OaLeaveMapper.xml` | 请假 CRUD + 条件查询 |
| `oa/mapper/OaAttendanceMapper.java` + `OaAttendanceMapper.xml` | 考勤 CRUD + 条件查询 |
| `oa/service/IOaLeaveService.java` + `impl/OaLeaveServiceImpl.java` | **服务3 写操作**：提交、审批(通过/驳回)、归档 +**联动写考勤**；状态机校验 |
| `oa/service/IOaAttendanceService.java` + `impl/OaAttendanceServiceImpl.java` | 考勤增删改 + **服务1 查询**（分页/筛选） |
| `web/controller/oa/OaLeaveController.java` | 请假接口：list/getInfo/add/approve/archive/remove + `@PreAuthorize` |
| `web/controller/oa/OaAttendanceController.java` | 考勤接口：list/getInfo/add/edit/remove + 导出 |

状态机流转规则（写在 `OaLeaveServiceImpl`）：
- `submit`：新建即 `pending`
- `approve(pass)`：仅 `pending→approved`，记审批人/意见/时间
- `approve(reject)`：仅 `pending→rejected`
- `archive`：仅 `approved→archived`，**触发联动**：按 start~end 日期区间 upsert `oa_attendance`（status=leave, source=1）

数据源服务（服务2）：复用若依 `/system/dict/data/type/{dictType}` 接口，前端下拉直接取 `oa_leave_type`、`oa_att_status`，无需新增后端代码。

## 5. 前端文件清单（Vue2 + Element UI）

| 文件 | 职责 |
|---|---|
| `ruoyi-ui/src/api/oa/leave.js` | 请假 API |
| `ruoyi-ui/src/api/oa/attendance.js` | 考勤 API |
| `ruoyi-ui/src/views/oa/leave/index.vue` | 请假列表/发起/进度查询（员工视角）+ 审批/归档按钮（按权限显示） |
| `ruoyi-ui/src/views/oa/attendance/index.vue` | 考勤查询（员工看自己）+ 管理维护（管理员） |

权限按钮通过 `v-hasPermi="['oa:leave:approve']"` 等控制，实现"同一页面按角色显示不同操作"。

## 6. 验证档位（待你最终确认）

- 我写全部代码 + 三个 SQL 文件
- 尝试本地 `mvn compile` 跑通后端（依赖你的 MySQL `ry-vue` 已导入基础表 + 可连接）
- 前端页面写好，**最终 UI 视觉需你启动 `npm run dev` 查看**（除非确认本机 Node 环境就绪我来拉起）

> 执行结果：本机**无 JDK / Maven**（仅 Node v24），无法本地 `mvn compile`。代码已按若依规范完成并通过静态自审，编译与联调需在装有 JDK17+ 与 Maven 的环境执行。考勤手动新增页已改为「员工下拉选择」以正确填充 user_id/dept_id（NOT NULL）。

## 7. 执行顺序

1. SQL（建表 + 字典 + 菜单 + 角色）
2. 后端 domain → mapper → service → controller
3. 前端 api → views
4. 编译验证 + 联调
