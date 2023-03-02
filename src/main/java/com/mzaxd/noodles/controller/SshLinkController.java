package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.annotation.SysLog;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.SshLinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 13439
 */
@RestController
@RequestMapping("/sshLink")
public class SshLinkController {

    @Resource
    private SshLinkService sshLinkService;

    @SysLog(operation = OperationEnum.CONSOLE_CONNECT)
    @GetMapping("/getInstanceInfo/{sshId}")
    public ResponseResult getInstanceInfo(@PathVariable("sshId") Long sshId) {
        return sshLinkService.getInstanceInfo(sshId);
    }


}
