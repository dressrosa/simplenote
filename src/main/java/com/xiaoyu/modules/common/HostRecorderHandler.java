/**
 * 
 */
package com.xiaoyu.modules.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xiaoyu.core.template.DefaultAbstractQueueTemplate;
import com.xiaoyu.modules.common.dao.HostRecordDao;
import com.xiaoyu.modules.common.entity.HostRecord;

/**
 * 处理请求ip
 * 
 * @author hongyu
 * @date 2018-08
 * @description
 */
@Component
public class HostRecorderHandler extends DefaultAbstractQueueTemplate {

    public HostRecorderHandler() {
        super("xiaoyu.ip");
    }

    private static final List<HostRecord> Host_Cache = new ArrayList<>(100);

    @Autowired
    private HostRecordDao hostRecordDao;

    @Override
    public void handleMessage(String message) {
        // message: uri;ip;time
        String[] str = message.split(";");
        String uri = str[0];
        String ip = str[1];
        String time = str[2];
        HostRecord re = new HostRecord()
                .setUri(uri)
                .setIp(ip)
                .setCreateDate(Long.valueOf(time)/1000);
        synchronized (Host_Cache) {
            Host_Cache.add(re);
            int size = Host_Cache.size();
            if (size > 99) {
                this.hostRecordDao.batchInsert(Host_Cache);
                Host_Cache.clear();
            }
        }
    }

}
