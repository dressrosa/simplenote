package com.xiaoyu.modules.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoyu.beacon.autoconfigure.anno.BeaconExporter;
import com.xiaoyu.modules.common.dao.HostRecordDao;
import com.xiaoyu.modules.common.entity.HostRecord;
import com.xiaoyu.modules.common.service.api.IHostService;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
@Service
@BeaconExporter(interfaceName = "com.xiaoyu.modules.common.service.api.IHostService", group = "dev")
public class HostServiceImpl implements IHostService {

    @Autowired
    private HostRecordDao hostRecordDao;

    @Autowired
    private AsynTask task;

    /**
     * 异步处理,防止beacon调用时间过长
     */
    @Override
    public void queryLocation() {
        task.doAsynHandle();
    }

    @Override
    public int batchInsert(List<HostRecord> list) {
        if (list.isEmpty()) {
            return 0;
        }
        return this.hostRecordDao.batchInsert(list);
    }
}
