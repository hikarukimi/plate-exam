package com.ruoyi.oa.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考勤记录对象 oa_attendance
 * 
 * @author ruoyi
 */
public class OaAttendance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 考勤记录ID */
    private Long attId;

    /** 员工ID */
    @Excel(name = "员工ID")
    private Long userId;

    /** 员工姓名 */
    @Excel(name = "员工")
    private String userName;

    /** 部门ID */
    private Long deptId;

    /** 考勤日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "考勤日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date attDate;

    /** 考勤状态 */
    @Excel(name = "考勤状态", dictType = "oa_att_status")
    private String attStatus;

    /** 来源（0手动 1请假联动） */
    @Excel(name = "来源", readConverterExp = "0=手动,1=请假联动")
    private String source;

    /** 关联请假申请ID */
    private Long leaveId;

    public Long getAttId()
    {
        return attId;
    }

    public void setAttId(Long attId)
    {
        this.attId = attId;
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

    @NotNull(message = "考勤日期不能为空")
    public Date getAttDate()
    {
        return attDate;
    }

    public void setAttDate(Date attDate)
    {
        this.attDate = attDate;
    }

    public String getAttStatus()
    {
        return attStatus;
    }

    public void setAttStatus(String attStatus)
    {
        this.attStatus = attStatus;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public Long getLeaveId()
    {
        return leaveId;
    }

    public void setLeaveId(Long leaveId)
    {
        this.leaveId = leaveId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("attId", getAttId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("deptId", getDeptId())
            .append("attDate", getAttDate())
            .append("attStatus", getAttStatus())
            .append("source", getSource())
            .append("leaveId", getLeaveId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
