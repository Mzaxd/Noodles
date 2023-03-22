package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.*;
import com.mzaxd.noodles.domain.entity.Container;
import com.mzaxd.noodles.domain.vo.*;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.mapper.HostMachineMapper;
import com.mzaxd.noodles.mapper.NotificationMapper;
import com.mzaxd.noodles.service.ContainerService;
import com.mzaxd.noodles.mapper.ContainerMapper;
import com.mzaxd.noodles.service.HostMachineService;
import com.mzaxd.noodles.service.SshLinkService;
import com.mzaxd.noodles.util.BeanCopyUtils;
import com.mzaxd.noodles.util.SshLinkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author root
 * @description 针对表【container】的数据库操作Service实现
 * @createDate 2023-02-02 06:26:00
 */
@Service
@Slf4j
public class ContainerServiceImpl extends ServiceImpl<ContainerMapper, Container>
        implements ContainerService {

    @Resource
    private ContainerMapper containerMapper;

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private HostMachineMapper hostMachineMapper;

    @Resource
    private SshLinkService sshLinkService;

    @Resource
    private NotificationMapper notificationMapper;

    @Override
    public ResponseResult containerListSummaryStatistics() {
        //设置查询所有在线vm的条件
        LambdaQueryWrapper<Container> runningWrapper = new LambdaQueryWrapper<>();
        runningWrapper.eq(Container::getContainerState, SystemConstant.CONTAINER_STATE_RUNNING);
        //设置查询所有离线vm的条件
        LambdaQueryWrapper<Container> exitedWrapper = new LambdaQueryWrapper<>();
        exitedWrapper.eq(Container::getContainerState, SystemConstant.CONTAINER_STATE_EXITED);
        //设置查询所有未知vm的条件
        LambdaQueryWrapper<Container> pausedWrapper = new LambdaQueryWrapper<>();
        pausedWrapper.eq(Container::getContainerState, SystemConstant.CONTAINER_STATE_UNKNOWN);

        int total = count();
        int running = count(runningWrapper);
        int exited = count(exitedWrapper);
        int paused = count(pausedWrapper);
        HashMap<String, Integer> result = new HashMap<>(4);
        result.put("total", total);
        result.put("running", running);
        result.put("exited", exited);
        result.put("unknown", paused);
        return ResponseResult.okResult(result);

    }

    @Override
    public ResponseResult containerListWithCondition(String nameLike, List<Integer> selectedHost, List<Integer> selectedStatus, Integer perPage, Integer currentPage) {
        //设置查询条件
        LambdaQueryWrapper<Container> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(nameLike), Container::getDescription, nameLike);
        wrapper.in(Objects.nonNull(selectedHost), Container::getHostMachineId, selectedHost);
        wrapper.in(Objects.nonNull(selectedStatus), Container::getContainerState, selectedStatus);
        //分页
        PageHelper.startPage(currentPage, perPage);
        //查询
        List<Container> containers = list(wrapper);
        //封装分页信息
        PageInfo pageInfo = new PageInfo<>(containers, perPage);

        //封装结果返回
        List<ContainerVo> containerListVos = BeanCopyUtils.copyBeanList(containers, ContainerVo.class);

        containerListVos.forEach(containerListVo -> {
            HostMachine hostMachine = hostMachineMapper.selectById(containerListVo.getHostMachineId());
            HostMachineVo hostMachineVo = BeanCopyUtils.copyBean(hostMachine, HostMachineVo.class);
            containerListVo.setHostMachine(hostMachineVo);
        });

        pageInfo.setList(containerListVos);
        return ResponseResult.okResult(pageInfo);
    }

    @Override
    public ResponseResult getAllHost() {
        List<HostMachine> hostMachines = hostMachineService.list();
        List<HostMachineVo> hostMachineVos = BeanCopyUtils.copyBeanList(hostMachines, HostMachineVo.class);
        return ResponseResult.okResult(hostMachineVos);
    }

    @Override
    public ResponseResult deleteContainerById(Integer id) {
        if (removeById(id)) {
            //删除掉线提醒
            LambdaQueryWrapper<Notification> notificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
            notificationLambdaQueryWrapper.eq(Notification::getInstanceId, id);
            notificationLambdaQueryWrapper.eq(Notification::getInstanceType, SystemConstant.INSTANCETYPE_CONTAINER);
            notificationMapper.delete(notificationLambdaQueryWrapper);
            log.info("删除id为{}的虚拟机的所有掉线提醒", id);
            return ResponseResult.okResult("删除成功");
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public ResponseResult addContainer(ContainerVo containerVo) {
        Container container = BeanCopyUtils.copyBean(containerVo, Container.class);
        SshLink sshLink = new SshLink();
        if (SshLinkUtil.isContainerSshLinkParamValid(containerVo)) {
            sshLink.setConsoleType(containerVo.getSshType())
                    .setHost(containerVo.getSshHost())
                    .setPort(containerVo.getSshPort())
                    .setName(containerVo.getSshUser())
                    .setPassword(containerVo.getSshPwd());
            sshLinkService.save(sshLink);
            container.setSshId(sshLink.getId());
        }
        save(container);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getContainer(Integer id) {
        Container container = getById(id);
        ContainerVo containerVo = BeanCopyUtils.copyBean(container, ContainerVo.class);
        if (Objects.nonNull(getById(id).getSshId())) {
            SshLink sshLink = sshLinkService.getById(container.getSshId());
            containerVo.setSshType(sshLink.getConsoleType())
                    .setSshHost(sshLink.getHost())
                    .setSshPort(sshLink.getPort())
                    .setSshUser(sshLink.getName())
                    .setSshPwd(sshLink.getPassword());
        }
        return ResponseResult.okResult(containerVo);
    }

    @Override
    public ResponseResult updateContainer(ContainerVo containerVo) {
        Container container = BeanCopyUtils.copyBean(containerVo, Container.class);
        //改ssh
        SshLink sshLink = null;
        if (Objects.nonNull(getById(containerVo.getId()).getSshId())) {
            sshLink = sshLinkService.getById(getById(containerVo.getId()).getSshId());
        } else {
            sshLink = new SshLink();
        }
        if (SshLinkUtil.isContainerSshLinkParamValid(containerVo)) {
            sshLink.setConsoleType(containerVo.getSshType())
                    .setHost(containerVo.getSshHost())
                    .setPort(containerVo.getSshPort())
                    .setName(containerVo.getSshUser())
                    .setPassword(containerVo.getSshPwd());
            sshLinkService.saveOrUpdate(sshLink);
            container.setSshId(sshLink.getId());
        }
        saveOrUpdate(container);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getAllContainer() {
        List<Container> containers = list();
        List<ContainerSelectVo> containerSelectVos = BeanCopyUtils.copyBeanList(containers, ContainerSelectVo.class);
        return ResponseResult.okResult(containerSelectVos);
    }

    @Override
    public ResponseResult getAssociatedContainers(Integer id, Integer perPage, Integer currentPage) {
        LambdaQueryWrapper<Container> containerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        containerLambdaQueryWrapper.eq(Container::getHostMachineId, id);
        //分页
        PageHelper.startPage(currentPage, perPage);
        List<Container> containers = list(containerLambdaQueryWrapper);
        //封装分页信息
        PageInfo pageInfo = new PageInfo<>(containers, perPage);
        List<ContainerVo> containerVos = BeanCopyUtils.copyBeanList(containers, ContainerVo.class);
        pageInfo.setList(containerVos);
        return ResponseResult.okResult(pageInfo);
    }

    @Override
    public Map<String, Integer> getContainerStateInfo() {
        HashMap<String, Integer> result = new HashMap<>();
        LambdaQueryWrapper<Container> containerWrapper = new LambdaQueryWrapper<>();
        //设置容器总数
        result.put("containerCount", count());
        //设置容器在线总数
        containerWrapper.eq(Container::getContainerState, SystemConstant.CONTAINER_STATE_RUNNING);
        result.put("containerOnlineCount", count(containerWrapper));
        containerWrapper.clear();
        //设置容器离线总数
        containerWrapper.eq(Container::getContainerState, SystemConstant.CONTAINER_STATE_EXITED);
        result.put("containerOfflineCount", count(containerWrapper));
        containerWrapper.clear();
        //设置容器未知总数
        containerWrapper.eq(Container::getContainerState, SystemConstant.CONTAINER_STATE_UNKNOWN);
        result.put("containerUnknownCount", count(containerWrapper));
        containerWrapper.clear();
        return result;
    }
}




