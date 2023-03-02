package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.SshLink;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 13439
* @description 针对表【ssh_link】的数据库操作Service
* @createDate 2023-02-24 20:30:25
*/
public interface SshLinkService extends IService<SshLink> {

    /**
     * 根据sshId返回实例信息
     *
     * @param sshId
     * @return
     */
    ResponseResult getInstanceInfo(Long sshId);


    /**
     * 根据sshId返回对应的Object
     *
     * @param sshId
     * @return
     */
    Object getInstanceInfoBySshId(Long sshId);
}
