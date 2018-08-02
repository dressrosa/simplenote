package com.xiaoyu.modules.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.utils.HostHttpUtil;
import com.xiaoyu.modules.common.dao.HostRecordDao;
import com.xiaoyu.modules.common.entity.HostRecord;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
@Component
public class HostService {

    private static final Logger logger = LoggerFactory.getLogger(HostService.class);

    @Autowired
    private HostRecordDao hostRecordDao;

    public void queryLocation() {
        List<HostRecord> list = null;
        // 分页同步 防止一次性取出量过大
        int pageNum = 1;
        long time = (System.currentTimeMillis() / 1000) - 3600 * 24;
        while (true) {
            PageHelper.startPage(pageNum++, 50, true);
            list = this.hostRecordDao.queryByInterval(time);
            if (list != null && !list.isEmpty()) {
                List<String> ips = new ArrayList<>();
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
}
