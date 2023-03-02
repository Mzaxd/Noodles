package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.*;
import com.mzaxd.noodles.domain.message.Server;
import com.mzaxd.noodles.domain.vo.*;
import com.mzaxd.noodles.mapper.ServirMapper;
import com.mzaxd.noodles.mapper.UserMapper;
import com.mzaxd.noodles.service.*;
import com.mzaxd.noodles.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author root
 * @description
 * @createDate 2022-11-26 12:24:08
 */
@Service
@Slf4j
public class ServirServiceImpl extends ServiceImpl<ServirMapper, Servir> implements ServirService {

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private ContainerService containerService;

    @Resource
    private TagService tagService;

    @Resource
    private ServirTagService servirTagService;

    @Resource
    private ServirHostService servirHostService;

    @Resource
    private ServirContainerService servirContainerService;

    @Resource
    private ServirMapper servirMapper;

    @Override
    public ResponseResult servirListWithCondition(String nameLike, List<Integer> selectedTags, Integer perPage, Integer currentPage) {
        //先查出所有选中标签的servir id
        LambdaQueryWrapper<ServirTag> servirTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        servirTagLambdaQueryWrapper.in(Objects.nonNull(selectedTags), ServirTag::getTagId, selectedTags);
        List<ServirTag> list = servirTagService.list(servirTagLambdaQueryWrapper);
        List<Long> servirIds = null;
        if (!CollectionUtils.isEmpty(list)) {
            servirIds = list.stream().map(ServirTag::getServirId).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
        }

        //设置查询条件
        LambdaQueryWrapper<Servir> servirLambdaQueryWrapper = new LambdaQueryWrapper<>();
        servirLambdaQueryWrapper.like(StringUtils.hasText(nameLike), Servir::getDescription, nameLike);
        servirLambdaQueryWrapper.in(Objects.nonNull(servirIds), Servir::getId, servirIds);
        //分页
        PageHelper.startPage(currentPage, perPage);
        //查询
        List<Servir> servirs = list(servirLambdaQueryWrapper);
        //封装分页信息
        PageInfo pageInfo = new PageInfo<>(servirs, perPage);
        //封装结果返回
        List<ServirListVo> servirListVos = BeanCopyUtils.copyBeanList(servirs, ServirListVo.class);
        //关联的host 关联的容器 所有标签
        servirListVos.forEach(servirListVo -> {
            //关联的host
            LambdaQueryWrapper<ServirHost> servirHostWrapper = new LambdaQueryWrapper<>();
            servirHostWrapper.eq(ServirHost::getServirId, servirListVo.getId());
            List<ServirHost> servirHosts = servirHostService.list(servirHostWrapper);
            if (!CollectionUtils.isEmpty(servirHosts)) {
                List<Long> hostIds = servirHosts.stream().map(ServirHost::getHostId).collect(Collectors.toList());
                LambdaQueryWrapper<HostMachine> hostMachineWrapper = new LambdaQueryWrapper<>();
                hostMachineWrapper.in(HostMachine::getId, hostIds);
                List<HostMachine> hostMachines = hostMachineService.list(hostMachineWrapper);
                servirListVo.setHosts(BeanCopyUtils.copyBeanList(hostMachines, ServirHostVo.class));
            }
            //关联的容器
            LambdaQueryWrapper<ServirContainer> servirContainerWrapper = new LambdaQueryWrapper<>();
            servirContainerWrapper.eq(ServirContainer::getServirId, servirListVo.getId());
            List<ServirContainer> servirContainers = servirContainerService.list(servirContainerWrapper);
            if (!CollectionUtils.isEmpty(servirContainers)) {
                List<Long> containerIds = servirContainers.stream().map(ServirContainer::getContainerId).collect(Collectors.toList());
                LambdaQueryWrapper<Container> containerWrapper = new LambdaQueryWrapper<>();
                containerWrapper.in(Container::getId, containerIds);
                List<Container> containers = containerService.list(containerWrapper);
                servirListVo.setContainers(BeanCopyUtils.copyBeanList(containers, ServirContainerVo.class));
            }
            //关联的标签
            LambdaQueryWrapper<ServirTag> servirTagWrapper = new LambdaQueryWrapper<>();
            servirTagWrapper.eq(ServirTag::getServirId, servirListVo.getId());
            List<ServirTag> servirTags = servirTagService.list(servirTagWrapper);
            if (!CollectionUtils.isEmpty(servirTags)) {
                List<Long> tagIds = servirTags.stream().map(ServirTag::getTagId).collect(Collectors.toList());
                LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
                tagWrapper.in(Tag::getId, tagIds);
                List<Tag> tags = tagService.list(tagWrapper);
                servirListVo.setTags(BeanCopyUtils.copyBeanList(tags, TagVo.class));
            }
        });
        pageInfo.setList(servirListVos);
        return ResponseResult.okResult(pageInfo);
    }

    @Override
    @Transactional
    public ResponseResult addServir(SaveOrUpdateServirVo saveOrUpdateServirVo) {
        Servir servir = BeanCopyUtils.copyBean(saveOrUpdateServirVo, Servir.class);
        save(servir);
        //关联host
        if (Objects.nonNull(saveOrUpdateServirVo.getHostIds())) {
            List<ServirHost> servirHosts = saveOrUpdateServirVo.getHostIds().stream().map(id -> {
                ServirHost servirHost = new ServirHost();
                servirHost.setServirId(servir.getId());
                servirHost.setHostId(Long.valueOf(id));
                return servirHost;
            }).collect(Collectors.toList());
            servirHostService.saveBatch(servirHosts);
        }

        //关联Container
        if (Objects.nonNull(saveOrUpdateServirVo.getContainerIds())) {
            List<ServirContainer> servirContainers = saveOrUpdateServirVo.getContainerIds().stream().map(id -> {
                ServirContainer servirContainer = new ServirContainer();
                servirContainer.setServirId(servir.getId());
                servirContainer.setContainerId(Long.valueOf(id));
                return servirContainer;
            }).collect(Collectors.toList());
            servirContainerService.saveBatch(servirContainers);
        }

        //关联Tag
        List<ServirTag> servirTags = saveOrUpdateServirVo.getTagIds().stream().map(id -> {
            ServirTag servirTag = new ServirTag();
            servirTag.setServirId(servir.getId());
            servirTag.setTagId(Long.valueOf(id));
            return servirTag;
        }).collect(Collectors.toList());
        servirTagService.saveBatch(servirTags);

        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult updateServir(SaveOrUpdateServirVo saveOrUpdateServirVo) {
        Servir servir = BeanCopyUtils.copyBean(saveOrUpdateServirVo, Servir.class);
        saveOrUpdate(servir);

        if (Objects.nonNull(saveOrUpdateServirVo.getHostIds())) {
            //删除所有服务关联的host
            LambdaQueryWrapper<ServirHost> servirHostWrapper = new LambdaQueryWrapper<>();
            servirHostWrapper.eq(ServirHost::getServirId, servir.getId());
            servirHostService.getBaseMapper().delete(servirHostWrapper);
            //关联新的host
            List<ServirHost> servirHosts = saveOrUpdateServirVo.getHostIds().stream().map(id -> {
                ServirHost servirHost = new ServirHost();
                servirHost.setServirId(servir.getId());
                servirHost.setHostId(Long.valueOf(id));
                return servirHost;
            }).collect(Collectors.toList());
            servirHostService.saveBatch(servirHosts);
        }

        if (Objects.nonNull(saveOrUpdateServirVo.getContainerIds())) {
            //删除所有服务关联的容器
            LambdaQueryWrapper<ServirContainer> servirContainerWrapper = new LambdaQueryWrapper<>();
            servirContainerWrapper.eq(ServirContainer::getServirId, servir.getId());
            servirContainerService.getBaseMapper().delete(servirContainerWrapper);
            //关联新的容器
            List<ServirContainer> servirContainers = saveOrUpdateServirVo.getContainerIds().stream().map(id -> {
                ServirContainer servirContainer = new ServirContainer();
                servirContainer.setServirId(servir.getId());
                servirContainer.setContainerId(Long.valueOf(id));
                return servirContainer;
            }).collect(Collectors.toList());
            servirContainerService.saveBatch(servirContainers);
        }

        if (!CollectionUtils.isEmpty(saveOrUpdateServirVo.getTagIds())) {
            //删除所有服务关联的标签
            LambdaQueryWrapper<ServirTag> servirTagWrapper = new LambdaQueryWrapper<>();
            servirTagWrapper.eq(ServirTag::getServirId, servir.getId());
            servirTagService.getBaseMapper().delete(servirTagWrapper);
            //关联新的标签
            List<ServirTag> servirTags = saveOrUpdateServirVo.getTagIds().stream().map(id -> {
                ServirTag servirTag = new ServirTag();
                servirTag.setServirId(servir.getId());
                servirTag.setTagId(Long.valueOf(id));
                return servirTag;
            }).collect(Collectors.toList());
            servirTagService.saveBatch(servirTags);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getRemarkById(Integer id) {
        Servir servir = getById(id);
        return ResponseResult.okResult(servir.getRemark());
    }

    @Override
    @Transactional
    public ResponseResult deleteServirById(Integer id) {
        log.info("删除id为{}的服务", id);

        //删除所有服务关联的host
        LambdaQueryWrapper<ServirHost> servirHostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        servirHostLambdaQueryWrapper.eq(ServirHost::getServirId, id);
        servirHostService.getBaseMapper().delete(servirHostLambdaQueryWrapper);

        //删除所有服务关联的容器
        LambdaQueryWrapper<ServirContainer> containerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        containerLambdaQueryWrapper.eq(ServirContainer::getServirId, id);
        servirContainerService.getBaseMapper().delete(containerLambdaQueryWrapper);

        //删除所有服务关联的标签
        LambdaQueryWrapper<ServirTag> servirTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        servirTagLambdaQueryWrapper.eq(ServirTag::getServirId, id);
        servirTagService.getBaseMapper().delete(servirTagLambdaQueryWrapper);

        //删除本体
        servirMapper.deleteById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getServirById(Integer id) {
        Servir servir = getById(id);
        ServirListVo servirVo = BeanCopyUtils.copyBean(servir, ServirListVo.class);

        LambdaQueryWrapper<ServirHost> servirHostWrapper = new LambdaQueryWrapper<>();
        servirHostWrapper.eq(ServirHost::getServirId, id);
        List<ServirHost> servirHosts = servirHostService.list(servirHostWrapper);
        if (!CollectionUtils.isEmpty(servirHosts)) {
            List<Long> hostIds = servirHosts.stream().map(ServirHost::getHostId).collect(Collectors.toList());
            LambdaQueryWrapper<HostMachine> hostMachineWrapper = new LambdaQueryWrapper<>();
            hostMachineWrapper.in(HostMachine::getId, hostIds);
            List<HostMachine> hostMachines = hostMachineService.list(hostMachineWrapper);
            servirVo.setHosts(BeanCopyUtils.copyBeanList(hostMachines, ServirHostVo.class));
        }
        //关联的容器
        LambdaQueryWrapper<ServirContainer> servirContainerWrapper = new LambdaQueryWrapper<>();
        servirContainerWrapper.eq(ServirContainer::getServirId, id);
        List<ServirContainer> servirContainers = servirContainerService.list(servirContainerWrapper);
        if (!CollectionUtils.isEmpty(servirContainers)) {
            List<Long> containerIds = servirContainers.stream().map(ServirContainer::getContainerId).collect(Collectors.toList());
            LambdaQueryWrapper<Container> containerWrapper = new LambdaQueryWrapper<>();
            containerWrapper.in(Container::getId, containerIds);
            List<Container> containers = containerService.list(containerWrapper);
            servirVo.setContainers(BeanCopyUtils.copyBeanList(containers, ServirContainerVo.class));
        }
        //关联的标签
        LambdaQueryWrapper<ServirTag> servirTagWrapper = new LambdaQueryWrapper<>();
        servirTagWrapper.eq(ServirTag::getServirId, id);
        List<ServirTag> servirTags = servirTagService.list(servirTagWrapper);
        if (!CollectionUtils.isEmpty(servirTags)) {
            List<Long> tagIds = servirTags.stream().map(ServirTag::getTagId).collect(Collectors.toList());
            LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.in(Tag::getId, tagIds);
            List<Tag> tags = tagService.list(tagWrapper);
            servirVo.setTags(BeanCopyUtils.copyBeanList(tags, TagVo.class));
        }
        return ResponseResult.okResult(servirVo);
    }


}




