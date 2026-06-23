package com.ruoyi.oa.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.oa.domain.OaAttendance;
import com.ruoyi.oa.domain.OaLeave;
import com.ruoyi.oa.mapper.OaAttendanceMapper;
import com.ruoyi.oa.mapper.OaLeaveMapper;
import com.ruoyi.oa.service.IOaLeaveService;

/**
 * 请假申请 服务实现
 * 
 * <p>工作流状态机：pending（待审批）→ approved（已通过）→ archived（已归档），
 * 旁路 rejected（已驳回，终态）。流转规则集中在本类校验。</p>
 * 
 * @author ruoyi
 */
@Service
public class OaLeaveServiceImpl implements IOaLeaveService
{
    /** 流程状态：待审批 */
    private static final String STATUS_PENDING = "pending";
    /** 流程状态：已通过 */
    private static final String STATUS_APPROVED = "approved";
    /** 流程状态：已驳回 */
    private static final String STATUS_REJECTED = "rejected";
    /** 流程状态：已归档 */
    private static final String STATUS_ARCHIVED = "archived";

    /** 考勤状态：请假 */
    private static final String ATT_STATUS_LEAVE = "leave";
    /** 考勤来源：请假联动 */
    private static final String ATT_SOURCE_LINK = "1";

    @Autowired
    private OaLeaveMapper oaLeaveMapper;

    @Autowired
    private OaAttendanceMapper oaAttendanceMapper;

    @Override
    public OaLeave selectOaLeaveByLeaveId(Long leaveId)
    {
        return oaLeaveMapper.selectOaLeaveByLeaveId(leaveId);
    }

    @Override
    @DataScope(deptAlias = "t", userAlias = "t")
    public List<OaLeave> selectOaLeaveList(OaLeave oaLeave)
    {
        return oaLeaveMapper.selectOaLeaveList(oaLeave);
    }

    @Override
    public int insertOaLeave(OaLeave oaLeave)
    {
        if (oaLeave.getStartDate().after(oaLeave.getEndDate()))
        {
            throw new ServiceException("开始日期不能晚于结束日期");
        }
        // 流程发起：以当前登录用户为申请人，初始状态 pending
        oaLeave.setUserId(SecurityUtils.getUserId());
        oaLeave.setUserName(SecurityUtils.getLoginUser().getUser().getNickName());
        oaLeave.setDeptId(SecurityUtils.getDeptId());
        oaLeave.setStatus(STATUS_PENDING);
        oaLeave.setCreateBy(SecurityUtils.getUsername());
        if (oaLeaveMapper.countOverlapLeave(oaLeave) > 0)
        {
            throw new ServiceException("该日期区间已存在请假申请，不可重复提交");
        }
        return oaLeaveMapper.insertOaLeave(oaLeave);
    }

    @Override
    public int updateOaLeave(OaLeave oaLeave)
    {
        OaLeave db = oaLeaveMapper.selectOaLeaveByLeaveId(oaLeave.getLeaveId());
        if (db == null)
        {
            throw new ServiceException("请假申请不存在");
        }
        if (!STATUS_PENDING.equals(db.getStatus()))
        {
            throw new ServiceException("仅待审批状态的申请可修改");
        }
        oaLeave.setUpdateBy(SecurityUtils.getUsername());
        return oaLeaveMapper.updateOaLeave(oaLeave);
    }

    @Override
    public int approveOaLeave(OaLeave oaLeave, boolean pass)
    {
        OaLeave db = oaLeaveMapper.selectOaLeaveByLeaveId(oaLeave.getLeaveId());
        if (db == null)
        {
            throw new ServiceException("请假申请不存在");
        }
        if (!STATUS_PENDING.equals(db.getStatus()))
        {
            throw new ServiceException("仅待审批状态的申请可审批");
        }
        // 审批人须为申请人所在部门负责人（admin 不受限）
        if (!SecurityUtils.getLoginUser().getUser().isAdmin()
                && !db.getDeptId().equals(SecurityUtils.getDeptId()))
        {
            throw new ServiceException("只能审批本部门的请假申请");
        }
        OaLeave update = new OaLeave();
        update.setLeaveId(db.getLeaveId());
        update.setStatus(pass ? STATUS_APPROVED : STATUS_REJECTED);
        update.setApproverId(SecurityUtils.getUserId());
        update.setApproveRemark(oaLeave.getApproveRemark());
        update.setApproveTime(DateUtils.getNowDate());
        update.setUpdateBy(SecurityUtils.getUsername());
        return oaLeaveMapper.updateOaLeave(update);
    }

    @Override
    @Transactional
    public int archiveOaLeave(Long leaveId)
    {
        OaLeave db = oaLeaveMapper.selectOaLeaveByLeaveId(leaveId);
        if (db == null)
        {
            throw new ServiceException("请假申请不存在");
        }
        if (!STATUS_APPROVED.equals(db.getStatus()))
        {
            throw new ServiceException("仅已通过的申请可归档");
        }
        OaLeave update = new OaLeave();
        update.setLeaveId(db.getLeaveId());
        update.setStatus(STATUS_ARCHIVED);
        update.setArchiveBy(SecurityUtils.getUsername());
        update.setArchiveTime(DateUtils.getNowDate());
        update.setUpdateBy(SecurityUtils.getUsername());
        int rows = oaLeaveMapper.updateOaLeave(update);
        // 请假联动：归档时把请假日期区间内的考勤记录写为"请假"
        syncAttendance(db);
        return rows;
    }

    /**
     * 请假归档联动考勤：按 start~end 逐日 upsert 考勤记录（status=leave, source=1）
     */
    private void syncAttendance(OaLeave leave)
    {
        Date day = DateUtils.truncate(leave.getStartDate(), Calendar.DATE);
        Date end = DateUtils.truncate(leave.getEndDate(), Calendar.DATE);
        while (!day.after(end))
        {
            OaAttendance query = new OaAttendance();
            query.setUserId(leave.getUserId());
            query.setAttDate(day);
            OaAttendance exist = oaAttendanceMapper.selectOaAttendanceByUserDate(query);
            if (exist == null)
            {
                OaAttendance att = new OaAttendance();
                att.setUserId(leave.getUserId());
                att.setUserName(leave.getUserName());
                att.setDeptId(leave.getDeptId());
                att.setAttDate(day);
                att.setAttStatus(ATT_STATUS_LEAVE);
                att.setSource(ATT_SOURCE_LINK);
                att.setLeaveId(leave.getLeaveId());
                att.setCreateBy(SecurityUtils.getUsername());
                oaAttendanceMapper.insertOaAttendance(att);
            }
            else
            {
                exist.setAttStatus(ATT_STATUS_LEAVE);
                exist.setSource(ATT_SOURCE_LINK);
                exist.setLeaveId(leave.getLeaveId());
                exist.setUpdateBy(SecurityUtils.getUsername());
                oaAttendanceMapper.updateOaAttendance(exist);
            }
            day = DateUtils.addDays(day, 1);
        }
    }

    @Override
    public int deleteOaLeaveByLeaveIds(Long[] leaveIds)
    {
        return oaLeaveMapper.deleteOaLeaveByLeaveIds(leaveIds);
    }

    @Override
    public int deleteOaLeaveByLeaveId(Long leaveId)
    {
        return oaLeaveMapper.deleteOaLeaveByLeaveId(leaveId);
    }
}
