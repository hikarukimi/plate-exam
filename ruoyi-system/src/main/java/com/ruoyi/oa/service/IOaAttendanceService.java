package com.ruoyi.oa.service;

import java.util.List;
import com.ruoyi.oa.domain.OaAttendance;

/**
 * 考勤记录 服务层
 * 
 * @author ruoyi
 */
public interface IOaAttendanceService
{
    /**
     * 查询考勤记录
     * 
     * @param attId 考勤记录主键
     * @return 考勤记录
     */
    public OaAttendance selectOaAttendanceByAttId(Long attId);

    /**
     * 查询考勤记录列表
     * 
     * @param oaAttendance 考勤记录
     * @return 考勤记录集合
     */
    public List<OaAttendance> selectOaAttendanceList(OaAttendance oaAttendance);

    /**
     * 新增考勤记录
     * 
     * @param oaAttendance 考勤记录
     * @return 结果
     */
    public int insertOaAttendance(OaAttendance oaAttendance);

    /**
     * 修改考勤记录
     * 
     * @param oaAttendance 考勤记录
     * @return 结果
     */
    public int updateOaAttendance(OaAttendance oaAttendance);

    /**
     * 批量删除考勤记录
     * 
     * @param attIds 需要删除的考勤记录主键集合
     * @return 结果
     */
    public int deleteOaAttendanceByAttIds(Long[] attIds);

    /**
     * 删除考勤记录信息
     * 
     * @param attId 考勤记录主键
     * @return 结果
     */
    public int deleteOaAttendanceByAttId(Long attId);
}
