package com.mzaxd.noodles.util;

import com.mzaxd.noodles.domain.search.SearchCategory;
import com.mzaxd.noodles.domain.search.SearchItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ThinkBook
 */
public class SearchUtil {

    public static ArrayList<SearchItem> getInstanceSearchItem() {
        ArrayList<SearchItem> searchItems = new ArrayList<>();
        searchItems.add(SearchItem.HOST);
        searchItems.add(SearchItem.VM);
        searchItems.add(SearchItem.CONTAINER);
        return searchItems;
    }

    public static ArrayList<SearchItem> getStatisticsSearchItem() {
        ArrayList<SearchItem> searchItems = new ArrayList<>();
        searchItems.add(SearchItem.DASHBOARD);
        searchItems.add(SearchItem.AUDIT_LOG);
        return searchItems;
    }

    public static ArrayList<SearchItem> getSettingSearchItem() {
        ArrayList<SearchItem> searchItems = new ArrayList<>();
        searchItems.add(SearchItem.PANEL_MANAGEMENT);
        searchItems.add(SearchItem.ACCOUNT_SETTING);
        return searchItems;
    }

    public static ArrayList<SearchItem> getOtherSearchItem() {
        ArrayList<SearchItem> searchItems = new ArrayList<>();
        searchItems.add(SearchItem.NOTIFICATION);
        searchItems.add(SearchItem.HELP);
        return searchItems;
    }

    public static ArrayList<SearchCategory> getSearchCategory() {
        return new ArrayList<>(Arrays.asList(
                SearchCategory.INSTANCE,
                SearchCategory.STATISTICS,
                SearchCategory.SETTING,
                SearchCategory.OTHER));
    }
}
