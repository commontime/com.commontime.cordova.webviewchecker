package com.commontime.plugin;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class WebViewChecker extends CordovaPlugin {

    public final static String PACKAGE_NAME_BUNDLE_KEY = "packageName";
    public final static String REQUIRED_VERSION_BUNDLE_KEY = "requiredChromeVersion";
    private final String REQUIRED_VERSION_PREFS_KEY = "requiredVersion";
    private String CHROME_PACKAGE_NAME = "com.android.chrome";
    private String SYSTEM_WEBVIEW_PACKAGE_NAME = "com.google.android.webview";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void pluginInitialize()
    {
        super.pluginInitialize();

        try
        {
            ApplicationInfo ai = cordova.getActivity().getPackageManager().getApplicationInfo(cordova.getActivity().getPackageName(), PackageManager.GET_META_DATA);
            Bundle aBundle = ai.metaData;

            String requiredVersion = aBundle.getString(REQUIRED_VERSION_PREFS_KEY);

            String currentVersion = WebViewCheckerUtil.getPackageVersion(cordova.getActivity().getPackageManager(), getPackageNameBasedOnOS());

            boolean isWebViewEnabled = WebViewCheckerUtil.isPackageEnabled(cordova.getActivity().getPackageManager(), getPackageNameBasedOnOS());

            int versionCompareResult = WebViewCheckerUtil.compareVersions(currentVersion, requiredVersion);

            if (!isWebViewEnabled || versionCompareResult == -1)
            {
                Intent i = new Intent(cordova.getActivity(), WebViewCheckerEnableUpdateActivity.class);
                i.putExtra(PACKAGE_NAME_BUNDLE_KEY, getPackageNameBasedOnOS());
                i.putExtra(REQUIRED_VERSION_BUNDLE_KEY, requiredVersion);
                cordova.startActivityForResult(this, i, 1);
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(String action, final JSONArray data, final CallbackContext callbackContext) throws JSONException
    {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1)
        {
            cordova.getActivity().finish();
        }
    }

    private String getPackageNameBasedOnOS()
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return CHROME_PACKAGE_NAME;
        }
        else
        {
            if (WebViewCheckerUtil.isPackageInstalled(cordova.getActivity().getPackageManager(), CHROME_PACKAGE_NAME))
            {
                return CHROME_PACKAGE_NAME;
            }
            else
            {
                return SYSTEM_WEBVIEW_PACKAGE_NAME;
            }
        }
    }
}