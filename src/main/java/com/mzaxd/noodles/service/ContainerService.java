package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.Container;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mzaxd.noodles.domain.vo.ContainerVo;

import java.util.List;
import java.util.Map;

/**
* @author root
* @description 针对表【container】的数据库操作Service
* @createDate 2023-02-02 06:26:00
*/
public interface ContainerService extends IService<Container> {

    /**
     * 返回简要统计信息
     *
     * @author mzaxd
     * @date 2/2/23 6:26 AM
     * @return ResponseResult
     */
    ResponseResult containerListSummaryStatistics();

    /**
     * 容器列表
     *
     * @author mzaxd
     * @date 2/2/23 7:06 AM
     * @param nameLike
     * @param selectedHost
     * @param selectedStatus
     * @param perPage
     * @param currentPage
     * @return ResponseResult
     */
    ResponseResult containerListWithCondition(String nameLike, List<Integer> selectedHost, List<Integer> selectedStatus, Integer perPage, Integer currentPage);

    /**
     * 获取所有宿主机
     *
     * @author mzaxd
     * @date 2/2/23 9:08 AM
     * @return ResponseResult
     */
    ResponseResult getAllHost();

    /**
     * 根据id删除容器
     *
     * @author mzaxd
     * @date 2/2/23 11:07 AM
     * @param id
     * @return ResponseResult
     */
    ResponseResult deleteContainerById(Integer id);

    /**
     * 添加容器
     *
     * @author mzaxd
     * @date 2/2/23 12:02 PM
     * @param vm
     * @return ResponseResult
     */
    ResponseResult addContainer(ContainerVo vm);

    /**
     * 根据id获取容器
     *
     * @author mzaxd
     * @date 2/2/23 12:45 PM
     * @param id
     * @return ResponseResult
     */
    ResponseResult getContainer(Integer id);

    /**
     * 编辑容器
     *
     * @author mzaxd
     * @date 2/2/23 1:15 PM
     * @param container
     * @return ResponseResult
     */
    ResponseResult updateContainer(ContainerVo container);

    /**
     * 获取所有容器
     *
     * @return
     */
    ResponseResult getAllContainer();

    /**
     * 根据物理机id查找容器
     *
     * @param id
     * @param rowPerPage
     * @param currentPage
     * @return
     */
    ResponseResult getAssociatedContainers(Integer id, Integer rowPerPage, Integer currentPage);


    /**
     * 获取容器状态信息
     *
     * @return
     */
    Map<String, Integer> getContainerStateInfo();
}
