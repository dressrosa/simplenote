package com.xiaoyu.modules.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;
import com.xiaoyu.beacon.autoconfigure.anno.BeaconExporter;
import com.xiaoyu.common.utils.HostHttpUtil;
import com.xiaoyu.modules.common.dao.HostRecordDao;
import com.xiaoyu.modules.common.entity.HostRecord;
import com.xiaoyu.modules.common.service.api.IHostService;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
@Component
@BeaconExporter(interfaceName = "com.xiaoyu.modules.common.service.api.IHostService", group = "dev")
public class HostServiceImpl implements IHostService {

    private static final Logger logger = LoggerFactory.getLogger(HostServiceImpl.class);

    @Autowired
    private HostRecordDao hostRecordDao;

    @Override
    public void queryLocation() {
        this.doAsynHandle();
    }

    /**
     * 异步处理,防止beacon调用时间过长
     */
    @Async
    private void doAsynHandle() {
        List<HostRecord> list = null;
        // 分页同步 防止一次性取出量过大
        int pageNum = 1;
        long time = (System.currentTimeMillis() / 1000) - 3600 * 24;
        while (true) {
            PageHelper.startPage(pageNum++, 50, true);
            list = this.hostRecordDao.queryByInterval(time);
            if (list != null && !list.isEmpty()) {
                List<String> ips = new ArrayList<>(50);
                for (HostRecord a : list) {
                    ips.add(a.getIp());
                }
                Map<String, String> locations = HostHttpUtil.sendRequest(ips.toArray(new String[0]));
                for (HostRecord a : list) {
                    a.setLocation(locations.get(a.getIp()));
                }
                this.hostRecordDao.batchUpdate(list);
            } else {
                break;
            }
        }
        logger.info("更新访问地域成功");
    }

    @Override
    public int batchInsert(List<HostRecord> list) {
        if (list.isEmpty()) {
            return 0;
        }
        return this.hostRecordDao.batchInsert(list);
    }
}
