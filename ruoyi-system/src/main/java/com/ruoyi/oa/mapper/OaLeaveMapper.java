package com.ruoyi.oa.mapper;

import java.util.List;
import com.ruoyi.oa.domain.OaLeave;

/**
 * 请假申请 数据层
 * 
 * @author ruoyi
 */
public interface OaLeaveMapper
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
     * 统计某员工在指定日期区间内、未驳回的请假申请数（用于重叠校验）
     * 
     * @param oaLeave 含 userId/startDate/endDate
     * @return 重叠记录数
     */
    public int countOverlapLeave(OaLeave oaLeave);

    /**
     * 新增请假申请
     * 
     * @param oaLeave 请假申请
     * @return 结果
     */
    public int insertOaLeave(OaLeave oaLeave);

    /**
     * 修改请假申请
     * 
     * @param oaLeave 请假申请
     * @return 结果
     */
    public int updateOaLeave(OaLeave oaLeave);

    /**
     * 删除请假申请
     * 
     * @param leaveId 请假申请主键
     * @return 结果
     */
    public int deleteOaLeaveByLeaveId(Long leaveId);

    /**
     * 批量删除请假申请
     * 
     * @param leaveIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOaLeaveByLeaveIds(Long[] leaveIds);
}
