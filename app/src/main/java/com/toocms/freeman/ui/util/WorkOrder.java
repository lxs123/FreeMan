package com.toocms.freeman.ui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.MapUtils;

/**
 * Created by admin on 2017/4/17.
 */

public class WorkOrder {

    private static WorkOrder instance;

    public static final WorkOrder getInstance() {
        if (instance == null) {
            instance = new WorkOrder();
        }
        return instance;
    }

    private WorkOrder() {
        Map<String, Map<String, List<Map<String, String>>>> map = new HashMap<>();
        orderList.put("0", map);
    }

    public void clear() {
        Map<String, Map<String, List<Map<String, String>>>> map = new HashMap<>();
        orderList.put("0", map);
    }

    private HashMap<String, Map<String, Map<String, List<Map<String, String>>>>> orderList = new HashMap<>();

    public Map<String, Map<String, List<Map<String, String>>>> getOrder1() {
        return orderList.get("0");
    }

    public Map<String, List<Map<String, String>>> getOrder2(String level1) {
        Map<String, Map<String, List<Map<String, String>>>> map = orderList.get("0");
        if (MapUtils.isEmpty(map)) return null;
        return map.get(level1);
    }

    public List<Map<String, String>> getOrder3(String level1, String level2) {
        Map<String, Map<String, List<Map<String, String>>>> map = orderList.get("0");
        if (MapUtils.isEmpty(map)) return null;
        Map<String, List<Map<String, String>>> map1 = map.get(level1);
        if (MapUtils.isEmpty(map1)) return null;
        return orderList.get("0").get(level1).get(level2);
    }

    public void addOrder3(String level1, String level2, List<Map<String, String>> list) {
        Map<String, List<Map<String, String>>> map2 = orderList.get("0").get(level1);
        if (MapUtils.isEmpty(map2)) map2 = new HashMap<>();

        map2.put(level2, list);
        orderList.get("0").put(level1, map2);
    }
}
