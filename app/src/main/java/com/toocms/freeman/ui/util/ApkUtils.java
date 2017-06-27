package com.toocms.freeman.ui.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @Author LXS
 * @Data 2017/6/24 17:18
 */

public class ApkUtils {
    public ApkUtils() {
    }

    public static void install(Context context, File uriFile) {
        Intent intent = new Intent("android.intent.action.VIEW");
        if(Build.VERSION.SDK_INT >= 24) {
            intent.setFlags(268435456);
            intent.setFlags(1);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", uriFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(uriFile), "application/vnd.android.package-archive");
            intent.setFlags(268435456);
        }

        if(context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            context.startActivity(intent);
        }

    }

    public static void uninstall(Context context, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent intent = new Intent("android.intent.action.DELETE", packageURI);
        context.startActivity(intent);
    }

    public static boolean isAvailable(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List packageInfos = packageManager.getInstalledPackages(0);
        ArrayList packageNames = new ArrayList();
        if(packageInfos != null) {
            for(int i = 0; i < packageInfos.size(); ++i) {
                String packName = ((PackageInfo)packageInfos.get(i)).packageName;
                packageNames.add(packName);
            }
        }

        return packageNames.contains(packageName);
    }

    public static boolean isAvailable(Context context, File file) {
        return isAvailable(context, getPackageName(context, file.getAbsolutePath()));
    }

    public static String getPackageName(Context context, String filePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = packageManager.getPackageArchiveInfo(filePath, 1);
        if(info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return appInfo.packageName;
        } else {
            return null;
        }
    }

    public static String getChannelFromApk(Context context, String channelPrefix) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String key = "META-INF/" + channelPrefix;
        String ret = "";
        ZipFile zipfile = null;

        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration split = zipfile.entries();

            while(split.hasMoreElements()) {
                ZipEntry channel = (ZipEntry)split.nextElement();
                String entryName = channel.getName();
                if(entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException var18) {
            var18.printStackTrace();
        } finally {
            if(zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }

        }

        String[] split1 = ret.split(channelPrefix);
        String channel1 = "";
        if(split1.length >= 2) {
            channel1 = ret.substring(key.length());
        }

        return channel1;
    }
    /**
     * 网页版百度地图 有经纬度
     * @param originLat
     * @param originLon
     * @param originName ->注：必填
     * @param desLat
     * @param desLon
     * @param destination
     * @param region : 当给定region时，认为起点和终点都在同一城市，除非单独给定起点或终点的城市。-->注：必填，不填不会显示导航路线
     * @param appName
     * @return
     */
    public static String getWebBaiduMapUri(String originLat, String originLon, String originName, String desLat, String desLon, String destination, String region, String appName) {
        String uri = "http://api.map.baidu.com/direction?origin=latlng:%1$s,%2$s|name:%3$s" +
                "&destination=latlng:%4$s,%5$s|name:%6$s&mode=driving&region=%7$s&output=html" +
                "&src=%8$s";
        return String.format(uri, originLat, originLon, originName, desLat, desLon, destination, region, appName);
    }
}
