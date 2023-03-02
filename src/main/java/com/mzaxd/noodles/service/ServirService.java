package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.Servir;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mzaxd.noodles.domain.vo.SaveOrUpdateServirVo;
import com.mzaxd.noodles.domain.vo.ServirListVo;

import java.util.List;

/**
* @author 13439
* @description 针对表【servir】的数据库操作Service
* @createDate 2023-02-11 18:00:24
*/
public interface ServirService extends IService<Servir> {


    /**
     * 根据条件返回所有服务
     *
     * @param nameLike
     * @param selectedTags
     * @param perPage
     * @param currentPage
     * @return
     */
    ResponseResult servirListWithCondition(String nameLike, List<Integer> selectedTags, Integer perPage, Integer currentPage);

    /**
     * 添加服务
     *
     * @param servir
     * @return
     */
    ResponseResult addServir(SaveOrUpdateServirVo servir);

    /**
     * 根据id删除服务
     *
     * @param id
     * @return
     */
    ResponseResult deleteServirById(Integer id);

    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    ResponseResult getServirById(Integer id);

    /**
     * 改
     *
     * @param
     * @return
     */
    ResponseResult updateServir(SaveOrUpdateServirVo saveOrUpdateServirVo);

    /**
     * 获取备注
     *
     * @param id
     * @return
     */
    ResponseResult getRemarkById(Integer id);

}
