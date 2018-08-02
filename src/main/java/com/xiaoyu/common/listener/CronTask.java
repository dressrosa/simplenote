/*
 * 唯有读书,不慵不扰
 * 
 */
package com.xiaoyu.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.common.service.HostService;

/**
 * 定时任务
 * 
 * @author xiaoyu
 * @date 2018-01
 * @description
 */
@Component
public class CronTask {

    private static final Logger LOG = LoggerFactory.getLogger(CronTask.class);

    @Autowired
    private IArticleService articleService;

    @Autowired
    private HostService hostService;

    /**
     * 每周六凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * 6")
    public void synElastic() {
        LOG.info("定时同步文章开始......");
        this.articleService.synElastic();
        LOG.info("定时同步文章结束......");
    }

    /**
     * 每天0点执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void synHost() {
        LOG.info("定时获取ip地域开始......");
        this.hostService.queryLocation();
        LOG.info("定时获取ip地域结束......");
    }

}
