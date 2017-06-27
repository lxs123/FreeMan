package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/4/13.
 */

public class BaiduLbs {
    String modle = getClass().getSimpleName();
    String url = "http://api.map.baidu.com/place/v2/suggestion";
    String mcode = "AE:FB:98:1D:48:2D:21:2D:1C:CB:46:11:56:1B:AF:4F:63:D4:AF:9A;com.toocms.freeman";
    String decUrl = "http://api.map.baidu.com/geocoder/v2/?";

    /**
     * 检索
     *
     * @param query
     * @param region
     * @param apiListener
     */
    public void placeSearch(String query, String region, ApiListener apiListener) {
        RequestParams params = new RequestParams(url);
        params.addQueryStringParameter("query", query);
        params.addQueryStringParameter("region", region);
        params.addQueryStringParameter("output", "json");
        params.addQueryStringParameter("ak", "doG7MP5BWkdnXvBhyPA5i52xxvK3wn2l");
        ApiTool apiTool = new ApiTool();
        apiTool.getApi(params, apiListener);
    }

    /**
     * decompile 根据经纬度返回城市
     */
    public void decompile(String location, ApiListener apiListener) {
        RequestParams params = new RequestParams(decUrl);
        params.addQueryStringParameter("location", location);
        params.addQueryStringParameter("output", "json");
        params.addQueryStringParameter("pois", "1");
        params.addQueryStringParameter("ak", "doG7MP5BWkdnXvBhyPA5i52xxvK3wn2l");
        ApiTool apiTool = new ApiTool();
        apiTool.getApi(params, apiListener);
    }
}
