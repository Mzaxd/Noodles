package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.annotation.SysLog;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.vo.ContainerVo;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.ContainerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author root
 */
@RestController
@RequestMapping("/container")
public class ContainerController {

    @Resource
    private ContainerService containerService;

    @GetMapping("/containerList/summaryStatistics")
    public ResponseResult containerListSummaryStatistics() {
        return containerService.containerListSummaryStatistics();
    }

    @GetMapping("/containerList")
    public ResponseResult containerListWithCondition(
            @RequestParam(value = "q", required = false) String nameLike,
            @RequestParam(value = "selectedHost", required = false) List<Integer> selectedHost,
            @RequestParam(value = "selectedStatus", required = false) List<Integer> selectedStatus,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("currentPage") Integer currentPage) {
        return containerService.containerListWithCondition(nameLike, selectedHost, selectedStatus, perPage, currentPage);
    }

    @GetMapping("/allHost")
    public ResponseResult hostDrawer() {
        return containerService.getAllHost();
    }

    @SysLog(operation = OperationEnum.CONTAINER_DELETE)
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteContainer(@PathVariable("id") Integer id) {
        return containerService.deleteContainerById(id);
    }

    @SysLog(operation = OperationEnum.CONTAINER_ADD)
    @PostMapping("/addContainer")
    public ResponseResult addContainer(@RequestBody Map<String, ContainerVo> container) {
        return containerService.addContainer(container.get("container"));
    }

    @GetMapping("/{id}")
    public ResponseResult getContainer(@PathVariable("id") Integer id) {
        return containerService.getContainer(id);
    }
    @SysLog(operation = OperationEnum.CONTAINER_UPDATE)
    @PostMapping("/updateContainer")
    public ResponseResult updateVm(@RequestBody Map<String, ContainerVo> container) {
        return containerService.updateContainer(container.get("container"));
    }

    @GetMapping("/allContainer")
    public ResponseResult allContainer() {
        return containerService.getAllContainer();
    }

    @GetMapping("/associatedContainers")
    public ResponseResult getAssociatedContainers(
            @RequestParam("hostId") Integer hostId,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("currentPage") Integer currentPage) {
        return containerService.getAssociatedContainers(hostId, perPage, currentPage);
    }

}
