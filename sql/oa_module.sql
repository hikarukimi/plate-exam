-- ----------------------------
-- 请假与考勤 OA 模块 - 业务表
-- 依赖：sys_user / sys_dept（基础系统表）
-- ----------------------------

-- ----------------------------
-- 请假申请表
-- ----------------------------
drop table if exists oa_leave;
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
  status         varchar(20)     default 'pending'          comment '流程状态（pending待审批/approved已通过/rejected已驳回/archived已归档）',
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

-- ----------------------------
-- 考勤记录表
-- ----------------------------
drop table if exists oa_attendance;
create table oa_attendance (
  att_id         bigint(20)      not null auto_increment    comment '考勤记录ID',
  user_id        bigint(20)      not null                   comment '员工ID（sys_user）',
  user_name      varchar(30)     default ''                 comment '员工姓名',
  dept_id        bigint(20)      not null                   comment '部门ID（sys_dept）',
  att_date       date            not null                   comment '考勤日期',
  att_status     varchar(20)     default 'normal'           comment '考勤状态（字典 oa_att_status：normal正常/leave请假/absent缺勤/late迟到）',
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
