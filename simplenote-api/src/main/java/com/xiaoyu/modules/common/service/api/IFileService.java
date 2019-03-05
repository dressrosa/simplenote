package com.xiaoyu.modules.common.service.api;

import java.util.List;

import com.xiaoyu.modules.common.entity.File;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
public interface IFileService {

    public List<File> queryFilesByBizId(String bizId);

    public List<File> queryFilesByBizIds(List<String> bizIds);

    public int saveFiles(List<File> files);

}
