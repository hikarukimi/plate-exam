package com.ruoyi.oa.service;

import java.util.List;
import com.ruoyi.oa.domain.OaLeave;

/**
 * 请假申请 服务层
 * 
 * @author ruoyi
 */
public interface IOaLeaveService
{
    /**
     * 查询请假申请
     * 
     * @param leaveId 请假申请主键
     * @return 请假申请
     */
    public OaLeave selectOaLeaveByLeaveId(Long leaveId);

    /**
     * 查询请假申请列表
     * 
     * @param oaLeave 请假申请
     * @return 请假申请集合
     */
    public List<OaLeave> selectOaLeaveList(OaLeave oaLeave);

    /**
     * 提交请假申请（流程发起，初始状态 pending）
     * 
     * @param oaLeave 请假申请
     * @return 结果
     */
    public int insertOaLeave(OaLeave oaLeave);

    /**
     * 修改请假申请（仅 pending 状态可改）
     * 
     * @param oaLeave 请假申请
     * @return 结果
     */
    public int updateOaLeave(OaLeave oaLeave);

    /**
     * 审批请假申请（部门负责人：通过 pending->approved / 驳回 pending->rejected）
     * 
     * @param oaLeave 含 leaveId、approveRemark；pass 决定通过或驳回
     * @param pass 是否通过
     * @return 结果
     */
    public int approveOaLeave(OaLeave oaLeave, boolean pass);

    /**
     * 归档请假申请（管理员备案：approved->archived，并联动写入考勤）
     * 
     * @param leaveId 请假申请主键
     * @return 结果
     */
    public int archiveOaLeave(Long leaveId);

    /**
     * 批量删除请假申请
     * 
     * @param leaveIds 需要删除的请假申请主键集合
     * @return 结果
     */
    public int deleteOaLeaveByLeaveIds(Long[] leaveIds);

    /**
     * 删除请假申请信息
     * 
     * @param leaveId 请假申请主键
     * @return 结果
     */
    public int deleteOaLeaveByLeaveId(Long leaveId);
}
