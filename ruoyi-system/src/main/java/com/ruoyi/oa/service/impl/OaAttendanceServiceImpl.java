package com.ruoyi.oa.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.oa.mapper.OaAttendanceMapper;
import com.ruoyi.oa.domain.OaAttendance;
import com.ruoyi.oa.service.IOaAttendanceService;

/**
 * 考勤记录 服务实现
 * 
 * @author ruoyi
 */
@Service
public class OaAttendanceServiceImpl implements IOaAttendanceService
{
    @Autowired
    private OaAttendanceMapper oaAttendanceMapper;

    @Override
    public OaAttendance selectOaAttendanceByAttId(Long attId)
    {
        return oaAttendanceMapper.selectOaAttendanceByAttId(attId);
    }

    @Override
    public List<OaAttendance> selectOaAttendanceList(OaAttendance oaAttendance)
    {
        return oaAttendanceMapper.selectOaAttendanceList(oaAttendance);
    }

    @Override
    public int insertOaAttendance(OaAttendance oaAttendance)
    {
        return oaAttendanceMapper.insertOaAttendance(oaAttendance);
    }

    @Override
    public int updateOaAttendance(OaAttendance oaAttendance)
    {
        return oaAttendanceMapper.updateOaAttendance(oaAttendance);
    }

    @Override
    public int deleteOaAttendanceByAttIds(Long[] attIds)
    {
        return oaAttendanceMapper.deleteOaAttendanceByAttIds(attIds);
    }

    @Override
    public int deleteOaAttendanceByAttId(Long attId)
    {
        return oaAttendanceMapper.deleteOaAttendanceByAttId(attId);
    }
}
