-- ----------------------------
-- 请假与考勤 OA 模块 - 初始化数据（字典 / 菜单 / 角色）
-- 依赖：先执行 oa_module.sql 建表
-- 菜单ID 占用 2000~2099（sys_menu auto_increment 起始为 2000，避免与系统菜单冲突）
-- 角色ID 占用 100~101（sys_role auto_increment 起始为 100）
-- ----------------------------

-- ==============================
-- 一、字典数据
-- ==============================
-- 请假类型字典
insert into sys_dict_type values(100, '请假类型', 'oa_leave_type', '0', 'admin', sysdate(), '', null, '请假类型列表');
-- 考勤状态字典
insert into sys_dict_type values(101, '考勤状态', 'oa_att_status', '0', 'admin', sysdate(), '', null, '考勤状态列表');
-- 请假流程状态字典
insert into sys_dict_type values(102, '请假流程状态', 'oa_leave_status', '0', 'admin', sysdate(), '', null, '请假流程状态列表');

insert into sys_dict_data values(100, 1, '年假', 'annual',  'oa_leave_type', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '年假');
insert into sys_dict_data values(101, 2, '事假', 'personal','oa_leave_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '事假');
insert into sys_dict_data values(102, 3, '病假', 'sick',    'oa_leave_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '病假');
insert into sys_dict_data values(103, 4, '调休', 'compoff', 'oa_leave_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '调休');

insert into sys_dict_data values(110, 1, '正常', 'normal', 'oa_att_status', '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '正常出勤');
insert into sys_dict_data values(111, 2, '请假', 'leave',  'oa_att_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '请假');
insert into sys_dict_data values(112, 3, '缺勤', 'absent', 'oa_att_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '缺勤');
insert into sys_dict_data values(113, 4, '迟到', 'late',   'oa_att_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '迟到');

insert into sys_dict_data values(120, 1, '待审批', 'pending',  'oa_leave_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '待部门负责人审批');
insert into sys_dict_data values(121, 2, '已通过', 'approved', 'oa_leave_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '已通过待归档');
insert into sys_dict_data values(122, 3, '已驳回', 'rejected', 'oa_leave_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '已驳回（终态）');
insert into sys_dict_data values(123, 4, '已归档', 'archived', 'oa_leave_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '已归档（终态）');

-- ==============================
-- 二、菜单与按钮权限
-- ==============================
-- 一级目录：OA办公
insert into sys_menu values(2000, 'OA办公', 0, 4, 'oa', null, '', '', 1, 0, 'M', '0', '0', '', 'documentation', 'admin', sysdate(), '', null, 'OA办公目录');

-- 菜单：请假管理
insert into sys_menu values(2010, '请假管理', 2000, 1, 'leave', 'oa/leave/index', '', '', 1, 0, 'C', '0', '0', 'oa:leave:list', 'form', 'admin', sysdate(), '', null, '请假管理菜单');
-- 菜单：考勤管理
insert into sys_menu values(2020, '考勤管理', 2000, 2, 'attendance', 'oa/attendance/index', '', '', 1, 0, 'C', '0', '0', 'oa:attendance:list', 'date', 'admin', sysdate(), '', null, '考勤管理菜单');

-- 请假管理-按钮
insert into sys_menu values(2011, '请假查询', 2010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:leave:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2012, '请假新增', 2010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:leave:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2013, '请假修改', 2010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:leave:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2014, '请假删除', 2010, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:leave:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2015, '请假导出', 2010, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:leave:export',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2016, '请假审批', 2010, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:leave:approve', '#', 'admin', sysdate(), '', null, '部门负责人审批');
insert into sys_menu values(2017, '请假归档', 2010, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:leave:archive', '#', 'admin', sysdate(), '', null, '管理员备案归档');

-- 考勤管理-按钮
insert into sys_menu values(2021, '考勤查询', 2020, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:attendance:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2022, '考勤新增', 2020, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:attendance:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2023, '考勤修改', 2020, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:attendance:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2024, '考勤删除', 2020, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:attendance:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2025, '考勤导出', 2020, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'oa:attendance:export', '#', 'admin', sysdate(), '', null, '');

-- ==============================
-- 三、角色与角色菜单绑定
-- ==============================
-- 普通员工：发起/查询请假、查看考勤；数据范围=仅本人数据(5)
insert into sys_role values(100, '普通员工', 'employee', 4, 5, 1, 1, '0', '0', 'admin', sysdate(), '', null, '普通员工：发起请假、查询本人进度与考勤');
-- 部门负责人：审批本部门请假、查看本部门考勤；数据范围=本部门数据(3)
insert into sys_role values(101, '部门负责人', 'deptleader', 3, 3, 1, 1, '0', '0', 'admin', sysdate(), '', null, '部门负责人：审批本部门请假、查看本部门考勤');

-- 普通员工菜单：OA目录 + 请假(列表/查询/新增/修改/删除/导出) + 考勤(列表/查询/导出)
insert into sys_role_menu values(100, 2000);
insert into sys_role_menu values(100, 2010);
insert into sys_role_menu values(100, 2011);
insert into sys_role_menu values(100, 2012);
insert into sys_role_menu values(100, 2013);
insert into sys_role_menu values(100, 2014);
insert into sys_role_menu values(100, 2015);
insert into sys_role_menu values(100, 2020);
insert into sys_role_menu values(100, 2021);
insert into sys_role_menu values(100, 2025);

-- 部门负责人菜单：OA目录 + 请假(列表/查询/审批/导出) + 考勤(列表/查询/导出)
insert into sys_role_menu values(101, 2000);
insert into sys_role_menu values(101, 2010);
insert into sys_role_menu values(101, 2011);
insert into sys_role_menu values(101, 2016);
insert into sys_role_menu values(101, 2015);
insert into sys_role_menu values(101, 2020);
insert into sys_role_menu values(101, 2021);
insert into sys_role_menu values(101, 2025);
