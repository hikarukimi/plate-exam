import request from '@/utils/request'

// 查询请假申请列表
export function listLeave(query) {
  return request({
    url: '/oa/leave/list',
    method: 'get',
    params: query
  })
}

// 查询请假申请详细
export function getLeave(leaveId) {
  return request({
    url: '/oa/leave/' + leaveId,
    method: 'get'
  })
}

// 新增请假申请（流程发起）
export function addLeave(data) {
  return request({
    url: '/oa/leave',
    method: 'post',
    data: data
  })
}

// 修改请假申请
export function updateLeave(data) {
  return request({
    url: '/oa/leave',
    method: 'put',
    data: data
  })
}

// 审批请假申请（pass=true 通过 / false 驳回）
export function approveLeave(pass, data) {
  return request({
    url: '/oa/leave/approve/' + pass,
    method: 'put',
    data: data
  })
}

// 归档请假申请（管理员备案）
export function archiveLeave(leaveId) {
  return request({
    url: '/oa/leave/archive/' + leaveId,
    method: 'put'
  })
}

// 删除请假申请
export function delLeave(leaveId) {
  return request({
    url: '/oa/leave/' + leaveId,
    method: 'delete'
  })
}
