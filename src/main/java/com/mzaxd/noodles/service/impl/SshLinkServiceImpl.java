package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.Container;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.domain.entity.SshLink;
import com.mzaxd.noodles.domain.vo.ContainerVo;
import com.mzaxd.noodles.domain.vo.HostMachineVo;
import com.mzaxd.noodles.service.ContainerService;
import com.mzaxd.noodles.service.HostMachineService;
import com.mzaxd.noodles.service.SshLinkService;
import com.mzaxd.noodles.mapper.SshLinkMapper;
import com.mzaxd.noodles.util.BeanCopyUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author 13439
 * @description 针对表【ssh_link】的数据库操作Service实现
 * @createDate 2023-02-24 20:30:25
 */
@Service
public class SshLinkServiceImpl extends ServiceImpl<SshLinkMapper, SshLink>
        implements SshLinkService {

    @Lazy
    @Resource
    private HostMachineService hostMachineService;

    @Lazy
    @Resource
    private ContainerService containerService;

    @Override
    public ResponseResult getInstanceInfo(Long sshId) {
        return ResponseResult.okResult(getInstanceInfoBySshId(sshId));
    }

    @Override
    public Object getInstanceInfoBySshId(Long sshId) {
        LambdaQueryWrapper<HostMachine> hostMachineLambdaQueryWrapper = new LambdaQueryWrapper<>();
        hostMachineLambdaQueryWrapper.eq(HostMachine::getSshId, sshId);
        HostMachine hostMachine = hostMachineService.getOne(hostMachineLambdaQueryWrapper);
        if (Objects.nonNull(hostMachine)) {
            HostMachineVo hostMachineVo = BeanCopyUtils.copyBean(hostMachine, HostMachineVo.class);
            return hostMachineVo;
        }
        LambdaQueryWrapper<Container> containerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        containerLambdaQueryWrapper.eq(Container::getSshId, sshId);
        Container container = containerService.getOne(containerLambdaQueryWrapper);
        if (Objects.nonNull(container)) {
            ContainerVo containerVo = BeanCopyUtils.copyBean(container, ContainerVo.class);
            return containerVo;
        }
        return null;
    }
}




