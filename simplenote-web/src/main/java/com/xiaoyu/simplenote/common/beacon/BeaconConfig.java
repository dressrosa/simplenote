package com.xiaoyu.simplenote.common.beacon;

import java.util.ArrayList;
import java.util.List;

import com.xiaoyu.beacon.spring.config.BeaconReference;
import com.xiaoyu.beacon.starter.BeaconReferConfiguration;
import com.xiaoyu.beacon.starter.anno.BeaconRefer;
import com.xiaoyu.simplenote.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.simplenote.modules.biz.message.service.api.IMessageService;
import com.xiaoyu.simplenote.modules.biz.note.service.api.INoteService;
import com.xiaoyu.simplenote.modules.biz.user.service.api.IUserService;
import com.xiaoyu.simplenote.modules.common.service.api.IFileService;
import com.xiaoyu.simplenote.modules.common.service.api.IHostService;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
@BeaconRefer
public class BeaconConfig extends BeaconReferConfiguration {

    @Override
    protected List<BeaconReference> doFindBeaconRefers() {
        List<BeaconReference> list = new ArrayList<>();
        list.add(new BeaconReference()
                .setInterfaceName(IMessageService.class.getName())
                .setCheck(false)
                .setGroup("dev")
                .setTimeout("9000"));
        list.add(new BeaconReference()
                .setInterfaceName(IArticleService.class.getName())
                .setCheck(false)
                .setGroup("dev")
                .setTimeout("9000"));
        list.add(new BeaconReference()
                .setInterfaceName(IHostService.class.getName())
                .setCheck(false)
                .setGroup("dev")
                .setTimeout("9000"));
        list.add(new BeaconReference()
                .setInterfaceName(IFileService.class.getName())
                .setCheck(false)
                .setGroup("dev")
                .setTimeout("9000"));
        list.add(new BeaconReference()
                .setInterfaceName(IUserService.class.getName())
                .setCheck(false)
                .setGroup("dev")
                .setTimeout("9000"));
        list.add(new BeaconReference()
                .setInterfaceName(INoteService.class.getName())
                .setCheck(false)
                .setGroup("dev")
                .setTimeout("9000"));
        return list;
    }

}