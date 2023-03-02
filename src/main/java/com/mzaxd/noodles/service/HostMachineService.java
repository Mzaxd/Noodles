package com.mzaxd.noodles.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.domain.vo.HostVo;
import com.mzaxd.noodles.domain.vo.VmVo;

import java.util.List;
import java.util.Map;

/**
* @author root
* @description 针对表【host_machine】的数据库操作Service
* @createDate 2023-01-30 01:24:08
*/
public interface HostMachineService extends IService<HostMachine> {

    /**
     * 返回所有host的部分数据
     *
     * @author mzaxd
     * @date 1/30/23 1:25 AM
     * @return ResultSet
     */
    ResponseResult getHostDrawer();

    /**
     * 按照一定条件返回vm的List
     *
     * @author mzaxd
     * @date 1/30/23 10:32 AM
     * @param nameLike
     * @param selectedKernel
     * @param selectedHost
     * @param selectedStatus
     * @param perPage
     * @param currentPage
     * @return ResponseResult
     */
    ResponseResult vmListWithCondition(String nameLike, List<Integer> selectedKernel, List<Integer> selectedHost, List<Integer> selectedStatus, Integer perPage, Integer currentPage);

    /**
     * 返回VM总数和三种状态的数据
     *
     * @author mzaxd
     * @date 1/31/23 10:22 AM
     * @return ResponseResult
     */
    ResponseResult vmListSummaryStatistics();

    /**
     * 修改vm
     *
     * @author mzaxd
     * @date 2/1/23 3:05 AM
     * @param addVmVo
     * @return ResponseResult
     */
    ResponseResult updateVm(VmVo addVmVo);

    /**
     * 根据id删除vm
     *
     * @author mzaxd
     * @date 2/1/23 5:11 AM
     * @param id
     * @return ResponseResult
     */
    ResponseResult deleteVmById(Integer id);

    /**
     * 根据id查找vm
     *
     * @author mzaxd
     * @date 2/1/23 10:33 AM
     * @param id
     * @return ResponseResult
     */
    ResponseResult<VmVo> getVmById(Integer id);

    /**
     * 添加vm
     *
     * @author mzaxd
     * @date 2/1/23 3:05 AM
     * @param vm
     * @return ResponseResult
     */
    ResponseResult addVm(VmVo vm);

    /**
     * 按照一定条件返回物理机的List
     *
     * @author mzaxd
     * @date 2/5/23 1:05 PM
     * @param nameLike
     * @param selectedStatus
     * @param perPage
     * @param currentPage
     * @return ResponseResult
     */
    ResponseResult hostListWithCondition(String nameLike, List<Integer> selectedStatus, Integer perPage, Integer currentPage);

    /**
     * 添加物理机
     *
     * @author mzaxd
     * @date 2/7/23 8:39 AM
     * @param host
     * @return ResponseResult
     */
    ResponseResult addHost(HostVo host);

    /**
     * 根据id返回Host
     *
     * @param id
     * @return
     */
    ResponseResult<HostVo> getHostById(Integer id);

    /**
     * 修改host
     *
     * @param host
     * @return
     */
    ResponseResult updateHost(HostVo host);

    /**
     * 通过id删除host
     *
     * @param id
     * @return
     */
    ResponseResult deleteHostById(Integer id);

    /**
     * 获取主机详细信息
     *
     * @param id
     * @return
     */
    ResponseResult getHostDetail(Integer id);

    /**
     * 获取所有基于此ID的虚拟机
     *
     * @param id
     * @param perPage
     * @param currentPage
     * @return
     */
    ResponseResult getAssociatedVms(Integer id, Integer perPage, Integer currentPage);

    /**
     * 获取所有物理机的磁盘信息
     *
     * @param id
     * @return
     */
    ResponseResult getAssociatedDisks(Integer id);

    /**
     * 获取所有物理机的网络接口信息
     *
     * @param id
     * @return
     */
    ResponseResult getAssociatedNetworkIfs(Integer id);

    /**
     * 获取物理机状态信息
     *
     * @return
     */
    Map<String, Integer> getHostStateInfo();

    /**
     * 获取虚拟机状态信息
     *
     * @return
     */
    Map<String, Integer> getVmStateInfo();

    /**
     * 根据前端发来的hostID通知对应探测器开始发送动态数据
     * @param hostId
     * @return
     */
    ResponseResult startSendDynamicData(Integer hostId);

    /**
     * 根据前端发来的hostID通知对应探测器停止发送动态数据
     * @param hostId
     * @return
     */
    ResponseResult stopSendDynamicData(Integer hostId);
}
