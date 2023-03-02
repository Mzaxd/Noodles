package com.mzaxd.noodles.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.mzaxd.noodles.annotation.SysLog;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.domain.vo.HostVo;
import com.mzaxd.noodles.domain.vo.VmVo;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.HostMachineService;
import com.mzaxd.noodles.util.WebUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author root
 */
@RestController
@RequestMapping("/host")
public class HostController {

    @Resource
    private HostMachineService hostMachineService;

    @GetMapping("/drawer")
    public ResponseResult hostDrawer() {
        return hostMachineService.getHostDrawer();
    }

    @GetMapping("/vmList")
    public ResponseResult vmListWithCondition(
            @RequestParam(value = "q", required = false) String nameLike,
            @RequestParam(value = "selectedKernel", required = false) List<Integer> selectedKernel,
            @RequestParam(value = "selectedHost", required = false) List<Integer> selectedHost,
            @RequestParam(value = "selectedStatus", required = false) List<Integer> selectedStatus,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("currentPage") Integer currentPage) {
        return hostMachineService.vmListWithCondition(nameLike, selectedKernel, selectedHost, selectedStatus, perPage, currentPage);
    }

    @GetMapping("/vmList/summaryStatistics")
    public ResponseResult vmListSummaryStatistics() {
        return hostMachineService.vmListSummaryStatistics();
    }

    @SysLog(operation = OperationEnum.VM_ADD)
    @PostMapping("/addVm")
    public ResponseResult addVm(@RequestBody Map<String, VmVo> vmVo) {
        return hostMachineService.addVm(vmVo.get("vm"));
    }

    @SysLog(operation = OperationEnum.VM_UPDATE)
    @PostMapping("/updateVm")
    public ResponseResult updateVm(@RequestBody Map<String, VmVo> vmVo) {
        return hostMachineService.updateVm(vmVo.get("vm"));
    }

    @SysLog(operation = OperationEnum.VM_DELETE)
    @DeleteMapping("/deleteVm/{id}")
    public ResponseResult deleteVm(@PathVariable("id") Integer id) {
        return hostMachineService.deleteVmById(id);
    }

    @SysLog(operation = OperationEnum.HOST_DELETE)
    @DeleteMapping("/deleteHost/{id}")
    public ResponseResult deleteHost(@PathVariable("id") Integer id) {
        return hostMachineService.deleteHostById(id);
    }

    @GetMapping("/vm/{id}")
    public ResponseResult<VmVo> getVmById(@PathVariable("id") Integer id) {
        return hostMachineService.getVmById(id);
    }

    @SysLog(operation = OperationEnum.HOST_UPDATE)
    @PostMapping("/updateHost")
    public ResponseResult updateHost(@RequestBody Map<String, HostVo> hostVo) {
        return hostMachineService.updateHost(hostVo.get("host"));
    }

    @GetMapping("/host/{id}")
    public ResponseResult<HostVo> getHostById(@PathVariable("id") Integer id) {
        return hostMachineService.getHostById(id);
    }

    @GetMapping("/hostList")
    public ResponseResult vmListWithCondition(
            @RequestParam(value = "q", required = false) String nameLike,
            @RequestParam(value = "selectedStatus", required = false) List<Integer> selectedStatus,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("currentPage") Integer currentPage) {
        return hostMachineService.hostListWithCondition(nameLike, selectedStatus, perPage, currentPage);
    }

    @SysLog(operation = OperationEnum.HOST_ADD)
    @PostMapping("/addHost")
    public ResponseResult addHost(@RequestBody Map<String, HostVo> hostVo) {
        return hostMachineService.addHost(hostVo.get("host"));
    }

    @GetMapping("/hostDetail/{id}")
    public ResponseResult getHostDetail(@PathVariable("id") Integer id) {
        return hostMachineService.getHostDetail(id);
    }

    @GetMapping("/associatedVms")
    public ResponseResult getAssociatedVms(
            @RequestParam("hostId") Integer hostId,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("currentPage") Integer currentPage) {
        return hostMachineService.getAssociatedVms(hostId, perPage, currentPage);
    }

    @GetMapping("/associatedDisks/{id}")
    public ResponseResult getAssociatedDisks(@PathVariable("id") Integer id){
        return hostMachineService.getAssociatedDisks(id);
    }

    @GetMapping("/associatedNetworkIfs/{id}")
    public ResponseResult getAssociatedNetworkIfs(@PathVariable("id") Integer id) {
        return hostMachineService.getAssociatedNetworkIfs(id);
    }

    @GetMapping("/startSendDynamicData/{id}")
    public ResponseResult startSendDynamicData(@PathVariable("id") Integer hostId) {
        return hostMachineService.startSendDynamicData(hostId);
    }

    @GetMapping("/stopSendDynamicData/{id}")
    public ResponseResult stopSendDynamicData(@PathVariable("id") Integer hostId) {
        return hostMachineService.stopSendDynamicData(hostId);
    }

}
