package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 13439
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @GetMapping("/allTag")
    public ResponseResult getAllTag() {
        return tagService.getAllTag();
    }
}
