package com.mzaxd.noodles.controller;


import com.mzaxd.noodles.constant.UrlConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.service.HostDetectorService;
import com.mzaxd.noodles.util.UrlUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author root
 */
@RestController
@RequestMapping("/detector")
public class DetectorController {

    @Resource
    private HostDetectorService hostDetectorService;

    @GetMapping("/isValidUrl/{protocol}/{ip}/{port}")
    public ResponseResult isValidUrl(@PathVariable String protocol,@PathVariable String ip, @PathVariable String port) {
        return hostDetectorService.isValidUrl(protocol,ip,port);
    }

}
