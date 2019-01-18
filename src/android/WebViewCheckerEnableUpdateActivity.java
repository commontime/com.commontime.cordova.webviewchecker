package com.commontime.plugin;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WebViewCheckerEnableUpdateActivity extends Activity
{
    private final String ENABLE_MESSAGE = "Please enable Chrome to use this app.";
    private final String ENABLE_BTN_TXT = "Enable Chrome";
    private final String UPDATE_MESSAGE = "Please update Chrome to use this app.";
    private final String UPDATE_BTN_TXT = "Update Chrome";

    private boolean goneToStore = false;
    private boolean goneToAppSettings = false;
    private String packageName;
    private String requiredVersion;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(false);

        packageName = getIntent().getStringExtra(WebViewChecker.CHROME_PACKAGE_NAME_BUNDLE_KEY);
        requiredVersion = getIntent().getStringExtra(WebViewChecker.REQUIRED_VERSION_BUNDLE_KEY);

        boolean isWebViewEnabled = WebViewCheckerUtil.isWebViewEnabled(getPackageManager(), packageName);

        if (!isWebViewEnabled)
        {
            setContentView(enableView());
        }
        else
        {
            setContentView(updateView());
        }

        setResult(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume()
    {
        super.onResume();

        if (!goneToAppSettings && !goneToStore) return;

        String currentVersion = WebViewCheckerUtil.getWebViewVersion(getPackageManager(), packageName);
        int versionCompareResult = WebViewCheckerUtil.compareVersions(currentVersion, requiredVersion);
        if (versionCompareResult == 1)
        {
            System.exit(0);
        }
    }

    private View enableView()
    {
        LinearLayout ll = new LinearLayout(getApplicationContext());

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tv = new TextView(getApplicationContext());

        tv.setText(ENABLE_MESSAGE);

        tv.setPadding(5,5,5,5);

        RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        tv.setLayoutParams(lp_tv);

        Button button = new Button(getApplicationContext());

        button.setText(ENABLE_BTN_TXT);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChromeSettingsPage();
            }
        });

        RelativeLayout.LayoutParams lp_button = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        button.setLayoutParams(lp_button);

        ll.addView(tv);
        ll.addView(button);

        return ll;
    }

    private View updateView()
    {
        LinearLayout ll = new LinearLayout(this);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tv = new TextView(getApplicationContext());

        tv.setText(UPDATE_MESSAGE);

        LinearLayout.LayoutParams lp_tv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp_tv.setMargins(10,10,10,10);

        tv.setLayoutParams(lp_tv);

        ll.addView(tv);

        Button button = new Button(this);

        button.setText(UPDATE_BTN_TXT);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGooglePlayPage();
            }
        });

        LinearLayout.LayoutParams lp_button = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        button.setLayoutParams(lp_button);

        ll.addView(button);

        return ll;
    }

    private void openGooglePlayPage()
    {
        Context context = getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
        goneToStore = true;
    }

    private void openChromeSettingsPage()
    {
        Context context = getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("package", packageName, null));
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        try
        {
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        goneToAppSettings = true;
    }
}