package com.ruoyi.web.controller.oa;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.oa.domain.OaLeave;
import com.ruoyi.oa.service.IOaLeaveService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 请假申请操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/oa/leave")
public class OaLeaveController extends BaseController
{
    @Autowired
    private IOaLeaveService oaLeaveService;

    /**
     * 查询请假申请列表
     */
    @PreAuthorize("@ss.hasPermi('oa:leave:list')")
    @GetMapping("/list")
    public TableDataInfo list(OaLeave oaLeave)
    {
        startPage();
        List<OaLeave> list = oaLeaveService.selectOaLeaveList(oaLeave);
        return getDataTable(list);
    }

    /**
     * 导出请假申请列表
     */
    @PreAuthorize("@ss.hasPermi('oa:leave:export')")
    @Log(title = "请假申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OaLeave oaLeave)
    {
        List<OaLeave> list = oaLeaveService.selectOaLeaveList(oaLeave);
        ExcelUtil<OaLeave> util = new ExcelUtil<OaLeave>(OaLeave.class);
        util.exportExcel(response, list, "请假申请数据");
    }

    /**
     * 获取请假申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('oa:leave:query')")
    @GetMapping(value = "/{leaveId}")
    public AjaxResult getInfo(@PathVariable("leaveId") Long leaveId)
    {
        return success(oaLeaveService.selectOaLeaveByLeaveId(leaveId));
    }

    /**
     * 新增请假申请（流程发起）
     */
    @PreAuthorize("@ss.hasPermi('oa:leave:add')")
    @Log(title = "请假申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody OaLeave oaLeave)
    {
        return toAjax(oaLeaveService.insertOaLeave(oaLeave));
    }

    /**
     * 修改请假申请（仅待审批可改）
     */
    @PreAuthorize("@ss.hasPermi('oa:leave:edit')")
    @Log(title = "请假申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody OaLeave oaLeave)
    {
        return toAjax(oaLeaveService.updateOaLeave(oaLeave));
    }

    /**
     * 审批请假申请（部门负责人：通过/驳回）
     */
    @PreAuthorize("@ss.hasPermi('oa:leave:approve')")
    @Log(title = "请假审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{pass}")
    public AjaxResult approve(@PathVariable("pass") boolean pass, @RequestBody OaLeave oaLeave)
    {
        return toAjax(oaLeaveService.approveOaLeave(oaLeave, pass));
    }

    /**
     * 归档请假申请（管理员备案，联动写入考勤）
     */
    @PreAuthorize("@ss.hasPermi('oa:leave:archive')")
    @Log(title = "请假归档", businessType = BusinessType.UPDATE)
    @PutMapping("/archive/{leaveId}")
    public AjaxResult archive(@PathVariable("leaveId") Long leaveId)
    {
        return toAjax(oaLeaveService.archiveOaLeave(leaveId));
    }

    /**
     * 删除请假申请
     */
    @PreAuthorize("@ss.hasPermi('oa:leave:remove')")
    @Log(title = "请假申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{leaveIds}")
    public AjaxResult remove(@PathVariable("leaveIds") Long[] leaveIds)
    {
        return toAjax(oaLeaveService.deleteOaLeaveByLeaveIds(leaveIds));
    }
}
