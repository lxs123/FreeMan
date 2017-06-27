package com.toocms.freeman.config;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liu
 * @date 2017/1/4 9:43
 */
public class JsonArryToList {
    private static String TAG="JsonArryToList";

    public JsonArryToList() {
    }

    /**
     * 格式 str=["好吧","好吧","好吧"...]
     * json 一维数组转化成list
     * @param str
     * @return
     */
    public static List<String> strList(String str) {
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(str)){
            try {
                JSONArray jsonArray = new JSONArray(str);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        list.add((String) jsonArray.get(i));
                    }
                }
            } catch (Exception e) {
               Log.e(TAG,e.toString());
            }
        }
        return list;
    }
}
