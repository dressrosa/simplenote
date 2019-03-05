package com.xiaoyu.modules.common.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoyu.beacon.autoconfigure.anno.BeaconExporter;
import com.xiaoyu.common.base.CommonQuery;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.modules.common.dao.FileDao;
import com.xiaoyu.modules.common.entity.File;
import com.xiaoyu.modules.common.service.api.IFileService;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
@Service
@BeaconExporter(interfaceName = "com.xiaoyu.modules.common.service.api.IFileService", group = "dev")
public class FileServiceImpl implements IFileService {

    @Autowired
    private FileDao fileDao;

    @Override
    public List<File> queryFilesByBizId(String bizId) {
        if (StringUtils.isBlank(bizId)) {
            return new LinkedList<>();
        }
        CommonQuery q = new CommonQuery();
        q.setBizId(bizId);
        return this.fileDao.findByList(q);
    }

    @Override
    public int saveFiles(List<File> files) {
        if (files.isEmpty()) {
            return 0;
        }
        files.forEach(a -> {
            a.setUuid(IdGenerator.uuid());
        });
        return this.fileDao.batchInsert(files);
    }

    @Override
    public List<File> queryFilesByBizIds(List<String> bizIds) {
        if (bizIds.isEmpty()) {
            return new LinkedList<>();
        }
        CommonQuery q = new CommonQuery();
        q.setBizIds(bizIds);
        return this.fileDao.findByList(q);
    }

}
