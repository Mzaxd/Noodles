package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.constant.UrlConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.*;
import com.mzaxd.noodles.domain.message.Server;
import com.mzaxd.noodles.domain.vo.*;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.exception.SystemException;
import com.mzaxd.noodles.mapper.HostDetectorMapper;
import com.mzaxd.noodles.mapper.HostMachineMapper;
import com.mzaxd.noodles.mapper.OsMapper;
import com.mzaxd.noodles.mapper.SshLinkMapper;
import com.mzaxd.noodles.service.*;
import com.mzaxd.noodles.util.BeanCopyUtils;
import com.mzaxd.noodles.util.SshLinkUtil;
import com.mzaxd.noodles.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author root
 * @description 针对表【host_machine】的数据库操作Service实现
 * @createDate 2023-01-30 01:24:08
 */
@Service
@Slf4j
public class HostMachineServiceImpl extends ServiceImpl<HostMachineMapper, HostMachine>
        implements HostMachineService {


    @Resource
    private RestTemplate restTemplate;

    @Lazy
    @Resource
    private HostDetectorService hostDetectorService;

    @Resource
    private HostDetectorMapper hostDetectorMapper;

    @Resource
    private HostMachineMapper hostMachineMapper;

    @Resource
    private OsMapper osMapper;

    @Resource
    private OsService osService;

    @Lazy
    @Resource
    private ContainerService containerService;

    @Resource
    private SshLinkService sshLinkService;

    @Resource
    private SshLinkMapper sshLinkMapper;

    @Override
    public ResponseResult getHostDrawer() {
        LambdaQueryWrapper<HostMachine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HostMachine::getHostMachineId, SystemConstant.PHYSICAL_MACHINE);
        List<HostMachine> hostMachineList = list(wrapper);
        List<HostMachineDrawerVo> hostMachineDrawerVos = BeanCopyUtils.copyBeanList(hostMachineList, HostMachineDrawerVo.class);
        return ResponseResult.okResult(hostMachineDrawerVos);
    }

    @Override
    public ResponseResult vmListWithCondition(String nameLike, List<Integer> selectedKernel, List<Integer> selectedHost, List<Integer> selectedStatus, Integer perPage, Integer currentPage) {
        //分页
        PageHelper.startPage(currentPage, perPage);
        //查询
        List<HostMachine> hostMachines = hostMachineMapper.vmListWithCondition(nameLike, selectedKernel, selectedHost, selectedStatus, perPage, currentPage);

        //封装分页信息
        PageInfo pageInfo = new PageInfo<>(hostMachines, perPage);

        //封装结果返回
        List<VirtualMachineListVo> virtualMachineListVos = BeanCopyUtils.copyBeanList(hostMachines, VirtualMachineListVo.class);
        virtualMachineListVos.forEach(virtualMachineListVo -> {
            Os os = osMapper.selectById(virtualMachineListVo.getOsId());
            OsVo osVo = BeanCopyUtils.copyBean(os, OsVo.class);
            virtualMachineListVo.setOs(osVo);
        });
        pageInfo.setList(virtualMachineListVos);
        return ResponseResult.okResult(pageInfo);
    }

    @Override
    public ResponseResult vmListSummaryStatistics() {
        //设置查询所有vm的条件
        LambdaQueryWrapper<HostMachine> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        //设置查询所有在线vm的条件
        LambdaQueryWrapper<HostMachine> onlineWrapper = new LambdaQueryWrapper<>();
        onlineWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_ONLINE);
        onlineWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        //设置查询所有离线vm的条件
        LambdaQueryWrapper<HostMachine> offlineWrapper = new LambdaQueryWrapper<>();
        offlineWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_OFFLINE);
        offlineWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        //设置查询所有睡眠vm的条件
        LambdaQueryWrapper<HostMachine> sleepWrapper = new LambdaQueryWrapper<>();
        sleepWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_SLEEP);
        sleepWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);

        int total = count(wrapper);
        int online = count(onlineWrapper);
        int offline = count(offlineWrapper);
        int sleep = count(sleepWrapper);
        HashMap<String, Integer> result = new HashMap<>(4);
        result.put("total", total);
        result.put("online", online);
        result.put("offline", offline);
        result.put("sleep", sleep);
        return ResponseResult.okResult(result);
    }

    @Override
    @Transactional
    public ResponseResult updateVm(VmVo vmVo) {
        //改Os
        Os os = osService.getById(getById(vmVo.getId()).getOsId());
        os.setName(vmVo.getOsName()).setKernel(vmVo.getOsKernel()).setDescription(vmVo.getOsName());
        osService.saveOrUpdate(os);

        //改SshLink
        SshLink sshLink = null;
        if (Objects.nonNull(getById(vmVo.getId()).getSshId())) {
            sshLink = sshLinkService.getById(getById(vmVo.getId()).getSshId());
        } else {
            sshLink = new SshLink();
        }
        sshLink.setHost(vmVo.getSshHost()).setPort(vmVo.getSshPort()).setName(vmVo.getSshUser()).setPassword(vmVo.getSshPwd());
        sshLinkService.saveOrUpdate(sshLink);

        HostMachine vm = BeanCopyUtils.copyBean(vmVo, HostMachine.class);
        vm.setSshId(sshLink.getId());
        saveOrUpdate(vm);
        return ResponseResult.okResult(SystemConstant.SUCCESS_CODE, "修改成功");
    }

    @Override
    @Transactional
    public ResponseResult addVm(VmVo vmvo) {
        HostMachine vm = BeanCopyUtils.copyBean(vmvo, HostMachine.class);
        Os os = new Os();
        os.setName(vmvo.getOsName()).setKernel(vmvo.getOsKernel());
        osService.save(os);
        vm.setOsId(os.getId());
        if (SshLinkUtil.isVmSshLinkParamValid(vmvo)) {
            SshLink sshLink = new SshLink();
            sshLink.setHost(vmvo.getSshHost()).setPort(vmvo.getSshPort()).setName(vmvo.getSshUser()).setPassword(vmvo.getSshPwd());
            sshLinkService.save(sshLink);
            vm.setSshId(sshLink.getId());
        }
        save(vm);
        return ResponseResult.okResult(SystemConstant.SUCCESS_CODE, "添加虚拟机成功");
    }

    @Override
    @Transactional
    public ResponseResult addHost(HostVo host) {
        //判断是否能连上探测器
        String isValidUrl = UrlUtil.getUrl(host.getProtocol(), host.getIp(), host.getPort(), UrlConstant.DETECTOR_IS_TRUE_URL);
        if (!hostDetectorService.isValidUrl(isValidUrl)) {
            throw new RuntimeException("无法连接到探测器");
        }
        //获取远程主机数据
        String getInfoUrl = UrlUtil.getUrl(host.getProtocol(), host.getIp(), host.getPort(), UrlConstant.DETECTOR_GET_INFO);
        Server server = hostDetectorService.detectorGetInfoByUrl(getInfoUrl);

        //存储Os信息
        Os os = new Os();
        String osName = server.getOs().getOsName();
        os.setName(osName).setDescription(osName).setKernel(host.getOsKernel());
        osService.save(os);

        //把数据库其余字段的数据补上
        HostMachine hostMachine = BeanCopyUtils.copyBean(host, HostMachine.class);
        hostMachine.setThreads((long) server.getCpu().getLogicalProcessorCount());
        hostMachine.setMemory((long) server.getMem().getTotal() * 1024);
        hostMachine.setHostMachineId(SystemConstant.HOST_MACHINE_ID_HOST);
        hostMachine.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_ONLINE);
        hostMachine.setOsId(os.getId());


        //存储控制台信息
        if (SshLinkUtil.isHostSshLinkParamValid(host)) {
            SshLink sshLink = new SshLink();
            sshLink.setHost(host.getSshHost()).setPort(host.getSshPort()).setName(host.getSshUser()).setPassword(host.getSshPwd());
            sshLinkService.save(sshLink);
            hostMachine.setSshId(sshLink.getId());
        }

        save(hostMachine);
        //存储探测器信息
        HostDetector hostDetector = new HostDetector();

        String getDetectorIdUrl = UrlUtil.getUrl(host.getProtocol(), host.getIp(), host.getPort(), UrlConstant.DETECTOR_GET_DETECTOR_ID);
        ResponseResult response = restTemplate.getForObject(getDetectorIdUrl, ResponseResult.class);
        String uuid = response.getData().toString();

        hostDetector
                .setHostMachineId(hostMachine.getId())
                .setDetectorUuid(uuid)
                .setDetectorIpAddress(UrlUtil.getAddress(host.getProtocol(), host.getIp(), host.getPort()));
        hostDetectorService.save(hostDetector);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<HostVo> getHostById(Integer id) {
        HostMachine hostMachine = getById(id);
        HostVo hostVo = BeanCopyUtils.copyBean(hostMachine, HostVo.class);
        Os os = osService.getById(hostMachine.getOsId());
        hostVo.setOsKernel(os.getKernel());

        if (Objects.nonNull(hostMachine.getSshId())) {
            SshLink sshLink = sshLinkService.getById(hostMachine.getSshId());
            hostVo.setSshHost(sshLink.getHost()).setSshPort(sshLink.getPort()).setSshUser(sshLink.getName()).setSshPwd(sshLink.getPassword());
        }

        LambdaQueryWrapper<HostDetector> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HostDetector::getHostMachineId, id);
        HostDetector hostDetector = hostDetectorService.getOne(queryWrapper);
        Map<String, String> map = UrlUtil.resolveUrl(hostDetector.getDetectorIpAddress());
        hostVo.setProtocol(map.get(UrlConstant.PROTOCOL));
        hostVo.setIp(map.get(UrlConstant.IP));
        hostVo.setPort(map.get(UrlConstant.PORT));
        return ResponseResult.okResult(hostVo);
    }

    @Override
    @Transactional
    public ResponseResult updateHost(HostVo host) {
        //判断是否能连上探测器
        String isValidUrl = UrlUtil.getUrl(host.getProtocol(), host.getIp(), host.getPort(), UrlConstant.DETECTOR_IS_TRUE_URL);
        if (!hostDetectorService.isValidUrl(isValidUrl)) {
            throw new RuntimeException("无法连接到探测器");
        }
        // 改探测器
        LambdaQueryWrapper<HostDetector> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HostDetector::getHostMachineId, host.getId());
        HostDetector hostDetector = hostDetectorService.getOne(wrapper);
        //改为新的探测器地址
        hostDetector.setDetectorIpAddress(UrlUtil.getAddress(host.getProtocol(), host.getIp(), host.getPort()));

        //获取新的探测器uuid
        String getDetectorIdUrl = UrlUtil.getUrl(host.getProtocol(), host.getIp(), host.getPort(), UrlConstant.DETECTOR_GET_DETECTOR_ID);
        ResponseResult response = restTemplate.getForObject(getDetectorIdUrl, ResponseResult.class);
        String uuid = response.getData().toString();
        hostDetector.setDetectorUuid(uuid);
        hostDetectorService.saveOrUpdate(hostDetector);

        //通过新的地址获取新的数据
        String getInfoUrl = UrlUtil.getUrl(host.getProtocol(), host.getIp(), host.getPort(), UrlConstant.DETECTOR_GET_INFO);
        Server server = hostDetectorService.detectorGetInfoByUrl(getInfoUrl);

        //改Os
        Os os = osService.getById(getById(host.getId()).getOsId());
        String osName = server.getOs().getOsName();
        os.setKernel(host.getOsKernel()).setName(osName).setDescription(osName);
        osService.saveOrUpdate(os);

        SshLink sshLink = null;
        //改SshLink
        if (Objects.nonNull(getById(host.getId()).getSshId())) {
            sshLink = sshLinkService.getById(getById(host.getId()).getSshId());
        } else {
            sshLink = new SshLink();
        }
        if (SshLinkUtil.isHostSshLinkParamValid(host)) {
            sshLink.setHost(host.getSshHost()).setPort(host.getSshPort()).setName(host.getSshUser()).setPassword(host.getSshPwd());
            sshLinkService.saveOrUpdate(sshLink);
        }

        HostMachine hostMachine = BeanCopyUtils.copyBean(host, HostMachine.class);
        hostMachine.setSshId(sshLink.getId());
        hostMachine.setThreads((long) server.getCpu().getLogicalProcessorCount());
        hostMachine.setMemory((long) server.getMem().getTotal() * 1024);
        saveOrUpdate(hostMachine);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult deleteHostById(Integer id) {
        //判断是否还有其他容器关联此VM 如果有则不可以删除
        LambdaQueryWrapper<Container> containerWrapper = new LambdaQueryWrapper<>();
        containerWrapper.eq(Container::getHostMachineId, id);
        List<Container> containerList = containerService.list(containerWrapper);

        if (CollectionUtils.isNotEmpty(containerList)) {
            log.info("删除失败，此物理机有关联的容器");
            throw new SystemException(AppHttpCodeEnum.EXIST_ASSOCIATION_CONTAINER);
        }

        //判断是否还有其他容器关联此VM 如果有则不可以删除
        LambdaQueryWrapper<HostMachine> vmWrapper = new LambdaQueryWrapper<>();
        vmWrapper.eq(HostMachine::getHostMachineId, id);
        List<HostMachine> vmList = list(vmWrapper);

        if (CollectionUtils.isNotEmpty(vmList)) {
            log.info("删除失败，此物理机有关联的虚拟机");
            throw new SystemException(AppHttpCodeEnum.EXIST_ASSOCIATION_VM);
        }
        //删除对应的探测器
        LambdaQueryWrapper<HostDetector> hostDetectorWrapper = new LambdaQueryWrapper<>();
        hostDetectorWrapper.eq(HostDetector::getHostMachineId, id);
        hostDetectorMapper.delete(hostDetectorWrapper);
        //删除对应的os
        osMapper.deleteById(getById(id).getOsId());
        //删除对应的ssh连接信息
        if (Objects.nonNull(getById(id).getSshId())) {
            sshLinkMapper.deleteById(getById(id).getSshId());
        }
        //删除物理机
        HostMachine hostMachine = hostMachineMapper.selectById(id);
        if (Objects.nonNull(hostMachine.getOsId())) {
            log.info("删除hostId为{}的host信息", id);
            osMapper.deleteById(hostMachine.getOsId());
        }
        log.info("删除id为{}的host", id);
        hostMachineMapper.deleteById(id);
        return ResponseResult.okResult(SystemConstant.SUCCESS_CODE, "删除成功");
    }

    @Override
    public ResponseResult getHostDetail(Integer id) {
        HostMachine hostMachine = getById(id);
        HostPanelVo hostPanelVo = BeanCopyUtils.copyBean(hostMachine, HostPanelVo.class);
        LambdaQueryWrapper<HostDetector> hostDetectorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        hostDetectorLambdaQueryWrapper.eq(HostDetector::getHostMachineId, id);
        HostDetector hostDetector = hostDetectorService.getOne(hostDetectorLambdaQueryWrapper);
        Server server = hostDetectorService.detectorGetInfoByUrl(hostDetector.getDetectorIpAddress() + UrlConstant.DETECTOR_GET_INFO);
        //查虚拟机数量
        LambdaQueryWrapper<HostMachine> vmWrapper = new LambdaQueryWrapper<>();
        vmWrapper.eq(HostMachine::getHostMachineId, id);
        int vmCount = count(vmWrapper);
        //查容器数量
        LambdaQueryWrapper<Container> containerWrapper = new LambdaQueryWrapper<>();
        containerWrapper.eq(Container::getHostMachineId, id);
        int containerCount = containerService.count(containerWrapper);
        hostPanelVo.setVmCount(vmCount).setContainerCount(containerCount);
        HashMap<String, Object> result = new HashMap<>();
        result.put("host", hostPanelVo);
        result.put("hostDetail", server);
        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult getAssociatedVms(Integer id, Integer perPage, Integer currentPage) {
        LambdaQueryWrapper<HostMachine> hostMachineLambdaQueryWrapper = new LambdaQueryWrapper<>();
        hostMachineLambdaQueryWrapper.eq(HostMachine::getHostMachineId, id);
        //分页
        PageHelper.startPage(currentPage, perPage);
        List<HostMachine> vms = list(hostMachineLambdaQueryWrapper);
        List<VirtualMachineListVo> vmList = BeanCopyUtils.copyBeanList(vms, VirtualMachineListVo.class);
        //封装分页信息
        PageInfo pageInfo = new PageInfo<>(vmList, perPage);
        vmList.forEach(virtualMachineListVo -> {
            Os os = osService.getById(virtualMachineListVo.getOsId());
            OsVo osVo = BeanCopyUtils.copyBean(os, OsVo.class);
            virtualMachineListVo.setOs(osVo);
        });
        pageInfo.setList(vmList);
        return ResponseResult.okResult(pageInfo);
    }

    @Override
    public ResponseResult getAssociatedDisks(Integer id) {
        LambdaQueryWrapper<HostDetector> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HostDetector::getHostMachineId, id);
        HostDetector hostDetector = hostDetectorService.getOne(lambdaQueryWrapper);
        ResponseResult result = restTemplate.getForObject(hostDetector.getDetectorIpAddress() + UrlConstant.DETECTOR_GET_DISK_INFO, ResponseResult.class);
        return ResponseResult.okResult(result.getData());
    }

    @Override
    public ResponseResult getAssociatedNetworkIfs(Integer id) {
        LambdaQueryWrapper<HostDetector> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HostDetector::getHostMachineId, id);
        HostDetector hostDetector = hostDetectorService.getOne(lambdaQueryWrapper);
        ResponseResult result = restTemplate.getForObject(hostDetector.getDetectorIpAddress() + UrlConstant.DETECTOR_GET_NETWORK_IF_INFO, ResponseResult.class);
        return ResponseResult.okResult(result.getData());
    }

    @Override
    public ResponseResult hostListWithCondition(String nameLike, List<Integer> selectedStatus, Integer perPage, Integer currentPage) {
        //设置查询条件
        LambdaQueryWrapper<HostMachine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        wrapper.like(StringUtils.hasText(nameLike), HostMachine::getDescription, nameLike);
        wrapper.in(Objects.nonNull(selectedStatus), HostMachine::getHostMachineState, selectedStatus);
        //分页
        PageHelper.startPage(currentPage, perPage);
        //查询
        List<HostMachine> hostMachines = list(wrapper);
        //封装分页信息
        PageInfo pageInfo = new PageInfo<>(hostMachines, perPage);
        //封装结果返回
        List<HostMachineListVo> hostMachineListVo = BeanCopyUtils.copyBeanList(hostMachines, HostMachineListVo.class);
        //设置探测器ip地址 设置Os信息
        hostMachineListVo.forEach(hostMachineVo -> {
            Os os = osMapper.selectById(hostMachineVo.getOsId());
            OsVo osVo = BeanCopyUtils.copyBean(os, OsVo.class);
            hostMachineVo.setOs(osVo);
        });

        pageInfo.setList(hostMachineListVo);
        return ResponseResult.okResult(pageInfo);
    }

    @Override
    public ResponseResult deleteVmById(Integer id) {
        //判断是否还有其他容器关联此VM 如果有则不可以删除
        LambdaQueryWrapper<Container> containerWrapper = new LambdaQueryWrapper<>();
        containerWrapper.eq(Container::getHostMachineId, id);
        List<Container> containerList = containerService.list(containerWrapper);

        if (CollectionUtils.isNotEmpty(containerList)) {
            log.info("删除失败，此Vm有关联的容器");
            throw new SystemException(AppHttpCodeEnum.EXIST_ASSOCIATION_CONTAINER);
        }
        HostMachine hostMachine = hostMachineMapper.selectById(id);
        if (Objects.nonNull(hostMachine.getOsId())) {
            log.info("删除vmId为{}的Os信息", id);
            osMapper.deleteById(hostMachine.getOsId());
        }
        //删除对应的os
        osMapper.deleteById(getById(id).getOsId());
        //删除对应的ssh连接信息
        if (Objects.nonNull(getById(id).getSshId())) {
            sshLinkMapper.deleteById(getById(id).getSshId());
        }
        log.info("删除id为{}的vm", id);
        hostMachineMapper.deleteById(id);
        return ResponseResult.okResult(SystemConstant.SUCCESS_CODE, "删除成功");
    }

    @Override
    public ResponseResult getVmById(Integer id) {
        HostMachine hostMachine = hostMachineMapper.selectById(id);
        Os os = osMapper.selectById(hostMachine.getOsId());
        VmVo vmVo = BeanCopyUtils.copyBean(hostMachine, VmVo.class);
        vmVo.setOsName(os.getName()).setOsKernel(os.getKernel());
        if (Objects.nonNull(hostMachine.getSshId())) {
            SshLink sshLink = sshLinkService.getById(hostMachine.getSshId());
            vmVo.setSshHost(sshLink.getHost()).setSshPort(sshLink.getPort()).setSshUser(sshLink.getName()).setSshPwd(sshLink.getPassword());
        }
        return ResponseResult.okResult(vmVo);
    }

    /**
     * 获取所有物理机当前在线信息
     *
     * @return
     */
    @Override
    public Map<String, Integer> getHostStateInfo() {
        HashMap<String, Integer> result = new HashMap<>();
        LambdaQueryWrapper<HostMachine> hostMachineWrapper = new LambdaQueryWrapper<>();
        //设置主机总数
        hostMachineWrapper.eq(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        result.put("hostCount", count(hostMachineWrapper));
        hostMachineWrapper.clear();
        //设置主机在线总数
        hostMachineWrapper.eq(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        hostMachineWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_ONLINE);
        result.put("hostOnlineCount", count(hostMachineWrapper));
        hostMachineWrapper.clear();
        //设置主机离线总数
        hostMachineWrapper.eq(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        hostMachineWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_OFFLINE);
        result.put("hostOfflineCount", count(hostMachineWrapper));
        hostMachineWrapper.clear();
        //设置主机未知总数
        hostMachineWrapper.eq(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        hostMachineWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_UNKNOWN);
        result.put("hostUnknownCount", count(hostMachineWrapper));
        hostMachineWrapper.clear();
        return result;
    }

    /**
     * 获取所有虚拟机当前在线信息
     *
     * @return
     */
    @Override
    public Map<String, Integer> getVmStateInfo() {
        HashMap<String, Integer> result = new HashMap<>();
        LambdaQueryWrapper<HostMachine> hostMachineWrapper = new LambdaQueryWrapper<>();
        //设置虚拟机总数
        hostMachineWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        result.put("vmCount", count(hostMachineWrapper));
        hostMachineWrapper.clear();
        //设置虚拟机在线总数
        hostMachineWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        hostMachineWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_ONLINE);
        result.put("vmOnlineCount", count(hostMachineWrapper));
        hostMachineWrapper.clear();
        //设置虚拟机离线总数
        hostMachineWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        hostMachineWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_OFFLINE);
        result.put("vmOfflineCount", count(hostMachineWrapper));
        hostMachineWrapper.clear();
        //设置虚拟机未知总数
        hostMachineWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        hostMachineWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_UNKNOWN);
        result.put("vmUnknownCount", count(hostMachineWrapper));
        hostMachineWrapper.clear();
        return result;
    }

    @Override
    public ResponseResult startSendDynamicData(Integer hostId) {
        LambdaQueryWrapper<HostDetector> hostDetectorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        hostDetectorLambdaQueryWrapper.eq(HostDetector::getHostMachineId, hostId);
        HostDetector hostDetector = hostDetectorService.getOne(hostDetectorLambdaQueryWrapper);
        ResponseResult result = null;
        try {
            result = restTemplate.getForObject(hostDetector.getDetectorIpAddress() + UrlConstant.DETECTOR_START_SEND_DYNAMIC_DATA, ResponseResult.class);
        } catch (Exception e) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public ResponseResult stopSendDynamicData(Integer hostId) {
        LambdaQueryWrapper<HostDetector> hostDetectorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        hostDetectorLambdaQueryWrapper.eq(HostDetector::getHostMachineId, hostId);
        HostDetector hostDetector = hostDetectorService.getOne(hostDetectorLambdaQueryWrapper);
        ResponseResult result = null;
        try {
            result = restTemplate.getForObject(hostDetector.getDetectorIpAddress() + UrlConstant.DETECTOR_STOP_SEND_DYNAMIC_DATA, ResponseResult.class);
        } catch (Exception e) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return result;
    }


}




