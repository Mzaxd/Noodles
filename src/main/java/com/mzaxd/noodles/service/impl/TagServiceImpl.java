package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.Tag;
import com.mzaxd.noodles.domain.vo.TagVo;
import com.mzaxd.noodles.service.TagService;
import com.mzaxd.noodles.mapper.TagMapper;
import com.mzaxd.noodles.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 13439
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2023-02-11 18:26:12
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    @Override
    public ResponseResult getAllTag() {
        List<Tag> tagList = list();
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(tagList, TagVo.class);
        return ResponseResult.okResult(tagVos);
    }
}




