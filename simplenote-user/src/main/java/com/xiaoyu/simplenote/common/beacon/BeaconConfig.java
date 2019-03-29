package com.xiaoyu.simplenote.common.beacon;

import java.util.ArrayList;
import java.util.List;

import com.xiaoyu.beacon.spring.config.BeaconReference;
import com.xiaoyu.beacon.starter.BeaconReferConfiguration;
import com.xiaoyu.beacon.starter.anno.BeaconRefer;
import com.xiaoyu.simplenote.modules.biz.message.service.api.IMessageService;

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
                .setGroup("dev"));
        return list;
    }

}