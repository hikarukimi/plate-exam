package com.ruoyi.oa.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 请假申请对象 oa_leave
 * 
 * @author ruoyi
 */
public class OaLeave extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 请假申请ID */
    private Long leaveId;

    /** 申请人ID */
    @Excel(name = "申请人ID")
    private Long userId;

    /** 申请人姓名 */
    @Excel(name = "申请人")
    private String userName;

    /** 申请人部门ID */
    private Long deptId;

    /** 请假类型 */
    @Excel(name = "请假类型", dictType = "oa_leave_type")
    private String leaveType;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 请假天数 */
    @Excel(name = "请假天数")
    private BigDecimal days;

    /** 请假事由 */
    @Excel(name = "请假事由")
    private String reason;

    /** 流程状态（pending待审批 approved已通过 rejected已驳回 archived已归档） */
    @Excel(name = "流程状态")
    private String status;

    /** 审批人ID */
    private Long approverId;

    /** 审批意见 */
    @Excel(name = "审批意见")
    private String approveRemark;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 归档人 */
    private String archiveBy;

    /** 归档时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date archiveTime;

    public Long getLeaveId()
    {
        return leaveId;
    }

    public void setLeaveId(Long leaveId)
    {
        this.leaveId = leaveId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    @NotBlank(message = "请假类型不能为空")
    public String getLeaveType()
    {
        return leaveType;
    }

    public void setLeaveType(String leaveType)
    {
        this.leaveType = leaveType;
    }

    @NotNull(message = "开始日期不能为空")
    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    @NotNull(message = "结束日期不能为空")
    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public BigDecimal getDays()
    {
        return days;
    }

    public void setDays(BigDecimal days)
    {
        this.days = days;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getApproverId()
    {
        return approverId;
    }

    public void setApproverId(Long approverId)
    {
        this.approverId = approverId;
    }

    public String getApproveRemark()
    {
        return approveRemark;
    }

    public void setApproveRemark(String approveRemark)
    {
        this.approveRemark = approveRemark;
    }

    public Date getApproveTime()
    {
        return approveTime;
    }

    public void setApproveTime(Date approveTime)
    {
        this.approveTime = approveTime;
    }

    public String getArchiveBy()
    {
        return archiveBy;
    }

    public void setArchiveBy(String archiveBy)
    {
        this.archiveBy = archiveBy;
    }

    public Date getArchiveTime()
    {
        return archiveTime;
    }

    public void setArchiveTime(Date archiveTime)
    {
        this.archiveTime = archiveTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("leaveId", getLeaveId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("deptId", getDeptId())
            .append("leaveType", getLeaveType())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("days", getDays())
            .append("reason", getReason())
            .append("status", getStatus())
            .append("approverId", getApproverId())
            .append("approveRemark", getApproveRemark())
            .append("approveTime", getApproveTime())
            .append("archiveBy", getArchiveBy())
            .append("archiveTime", getArchiveTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
