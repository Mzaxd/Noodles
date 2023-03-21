package com.mzaxd.noodles.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.search.SearchCategory;
import com.mzaxd.noodles.domain.search.SearchItem;
import com.mzaxd.noodles.util.SearchUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ThinkBook
 */

@RestController
public class OtherController {

    /**
     * 全局搜索功能的接口
     *
     * @param str
     * @return
     */
    @GetMapping("/app-bar/search")
    public ResponseResult searchbar(@RequestParam("q") String str) {
        List<SearchCategory> searchCategories = SearchUtil.getSearchCategory();

        List<Object> result = new ArrayList<>();

        for (SearchCategory category : searchCategories) {
            result.add(category);
            List<SearchItem> searchItems = null;
            switch (category) {
                case INSTANCE:
                    searchItems = SearchUtil.getInstanceSearchItem();
                    break;
                case STATISTICS:
                    searchItems = SearchUtil.getStatisticsSearchItem();
                    break;
                case SETTING:
                    searchItems = SearchUtil.getSettingSearchItem();
                    break;
                case OTHER:
                    searchItems = SearchUtil.getOtherSearchItem();
                    break;
                default:
                    break;
            }
            if (searchItems != null) {
                List<SearchItem> filteredItems = searchItems.stream()
                        .filter(item -> item.getTitle().contains(str))
                        .collect(Collectors.toList());
                if (!filteredItems.isEmpty()) {
                    result.addAll(filteredItems);
                } else {
                    result.remove(result.size() - 1);
                }
            }
        }
        return ResponseResult.okResult(result);
    }
}
