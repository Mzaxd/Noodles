package com.mzaxd.noodles.mapper;

import com.mzaxd.noodles.domain.entity.HostMachine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author root
* @description 针对表【host_machine】的数据库操作Mapper
* @createDate 2023-01-30 01:24:08
* @Entity com.mzaxd.noodles.domain.entity.HostMachine
*/
public interface HostMachineMapper extends BaseMapper<HostMachine> {

    /**
     * 返回VMList所需的所有数据
     *
     * @param nameLike
     * @param selectedKernel
     * @param selectedHost
     * @param selectedStatus
     * @param perPage
     * @param currentPage
     * @return
     */
    List<HostMachine> vmListWithCondition(String nameLike, List<Integer> selectedKernel, List<Integer> selectedHost, List<Integer> selectedStatus, Integer perPage, Integer currentPage);
}




