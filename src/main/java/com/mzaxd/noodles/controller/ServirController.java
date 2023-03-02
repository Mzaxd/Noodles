package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.annotation.SysLog;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.vo.SaveOrUpdateServirVo;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.ServirService;
import com.mzaxd.noodles.service.TagService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 13439
 */
@RestController
@RequestMapping("/servir")
public class ServirController {

    @Resource
    private TagService tagService;

    @Resource
    private ServirService servirService;


    @GetMapping("/tags")
    public ResponseResult getAllTag() {
        return tagService.getAllTag();
    }

    @GetMapping("/servirList")
    public ResponseResult servirListWithCondition(
            @RequestParam(value = "q", required = false) String nameLike,
            @RequestParam(value = "selectedTags", required = false) List<Integer> selectedTags,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("currentPage") Integer currentPage) {
        return servirService.servirListWithCondition(nameLike, selectedTags, perPage, currentPage);
    }

    @SysLog(operation = OperationEnum.SERVIR_ADD)
    @PostMapping("/addServir")
    public ResponseResult addServir(@RequestBody Map<String,SaveOrUpdateServirVo> saveOrUpdateServirVoMap) {
        return servirService.addServir(saveOrUpdateServirVoMap.get("servir"));
    }
    @SysLog(operation = OperationEnum.SERVIR_UPDATE)
    @PostMapping("/updateServir")
    public ResponseResult updateServir(@RequestBody Map<String,SaveOrUpdateServirVo> saveOrUpdateServirVoMap) {
        return servirService.updateServir(saveOrUpdateServirVoMap.get("servir"));
    }

    @SysLog(operation = OperationEnum.SERVIR_DELETE)
    @DeleteMapping("/deleteServir/{id}")
    public ResponseResult deleteServir(@PathVariable("id") Integer id) {
        return servirService.deleteServirById(id);
    }

    @GetMapping("/{id}")
    public ResponseResult getServirDataById(@PathVariable("id") Integer id) {
        return servirService.getServirById(id);
    }

    @GetMapping("/getRemark/{id}")
    public ResponseResult getRemarkById(@PathVariable("id") Integer id) {
        return servirService.getRemarkById(id);
    }


}
