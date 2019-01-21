package com.commontime.plugin;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class WebViewCheckerUtil {

    public static String getWebViewVersion(PackageManager pm, String packageName)
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

    public static boolean isWebViewEnabled(PackageManager pm, String packageName)
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