package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 13439
* @description 针对表【tag】的数据库操作Service
* @createDate 2023-02-11 18:26:12
*/
public interface TagService extends IService<Tag> {

    /**
     * 获取所有tag
     *
     * @return
     */
    ResponseResult getAllTag();
}
