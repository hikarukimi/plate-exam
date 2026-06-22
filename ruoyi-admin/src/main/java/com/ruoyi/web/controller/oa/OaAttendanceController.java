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
import com.ruoyi.oa.domain.OaAttendance;
import com.ruoyi.oa.service.IOaAttendanceService;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 考勤记录操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/oa/attendance")
public class OaAttendanceController extends BaseController
{
    @Autowired
    private IOaAttendanceService oaAttendanceService;

    /**
     * 查询考勤记录列表
     */
    @PreAuthorize("@ss.hasPermi('oa:attendance:list')")
    @GetMapping("/list")
    public TableDataInfo list(OaAttendance oaAttendance)
    {
        startPage();
        List<OaAttendance> list = oaAttendanceService.selectOaAttendanceList(oaAttendance);
        return getDataTable(list);
    }

    /**
     * 导出考勤记录列表
     */
    @PreAuthorize("@ss.hasPermi('oa:attendance:export')")
    @Log(title = "考勤记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OaAttendance oaAttendance)
    {
        List<OaAttendance> list = oaAttendanceService.selectOaAttendanceList(oaAttendance);
        ExcelUtil<OaAttendance> util = new ExcelUtil<OaAttendance>(OaAttendance.class);
        util.exportExcel(response, list, "考勤记录数据");
    }

    /**
     * 获取考勤记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('oa:attendance:query')")
    @GetMapping(value = "/{attId}")
    public AjaxResult getInfo(@PathVariable("attId") Long attId)
    {
        return success(oaAttendanceService.selectOaAttendanceByAttId(attId));
    }

    /**
     * 新增考勤记录
     */
    @PreAuthorize("@ss.hasPermi('oa:attendance:add')")
    @Log(title = "考勤记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody OaAttendance oaAttendance)
    {
        oaAttendance.setSource("0");
        oaAttendance.setCreateBy(SecurityUtils.getUsername());
        return toAjax(oaAttendanceService.insertOaAttendance(oaAttendance));
    }

    /**
     * 修改考勤记录
     */
    @PreAuthorize("@ss.hasPermi('oa:attendance:edit')")
    @Log(title = "考勤记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody OaAttendance oaAttendance)
    {
        oaAttendance.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(oaAttendanceService.updateOaAttendance(oaAttendance));
    }

    /**
     * 删除考勤记录
     */
    @PreAuthorize("@ss.hasPermi('oa:attendance:remove')")
    @Log(title = "考勤记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{attIds}")
    public AjaxResult remove(@PathVariable("attIds") Long[] attIds)
    {
        return toAjax(oaAttendanceService.deleteOaAttendanceByAttIds(attIds));
    }
}
