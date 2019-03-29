/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;
import com.xiaoyu.simplenote.common.utils.HostHttpUtil;
import com.xiaoyu.simplenote.modules.common.dao.HostRecordDao;
import com.xiaoyu.simplenote.modules.common.entity.HostRecord;

/**
 * 异步任务
 * 
 * @author hongyu
 * @date 2018-09
 * @description 主线任务必须和异步任务分在俩个springbean里面
 */
@Component
public class AsynTask {

    private static final Logger logger = LoggerFactory.getLogger(AsynTask.class);

    @Autowired
    private HostRecordDao hostRecordDao;

    /**
     * 异步处理,防止beacon调用时间过长
     */
    @Async
    public void doAsynHandle() {
        List<HostRecord> list = null;
        // 分页同步 防止一次性取出量过大
        int pageNum = 1;
        long time = (System.currentTimeMillis() / 1000) - 3600 * 24 * 30;
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
}
