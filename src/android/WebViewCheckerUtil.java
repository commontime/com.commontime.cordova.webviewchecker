package com.commontime.plugin;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.List;

public class WebViewCheckerUtil {

    public static String getPackageVersion(PackageManager pm, String packageName)
    {
        try
        {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            return info.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isPackageInstalled(PackageManager pm, String packageName)
    {
        List<ApplicationInfo> packages;
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(packageName))
                return true;
        }
        return false;
    }

    public static String getPackageLabel(PackageManager pm, String packageName)
    {
        try
        {
            return pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString();
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isPackageEnabled(PackageManager pm, String packageName)
    {
        try
        {
            return pm.getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static int compareVersions(String v1, String v2)
    {
        String[] components1 = v1.split("\\.");
        String[] components2 = v2.split("\\.");
        int length = Math.min(components1.length, components2.length);
        for (int i = 0; i < length; i++)
        {
            int result = new Integer(components1[i]).compareTo(Integer.parseInt(components2[i]));
            if(result != 0)
            {
                return result;
            }
        }
        return Integer.compare(components1.length, components2.length);
    }
}