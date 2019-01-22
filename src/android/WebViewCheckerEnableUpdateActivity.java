package com.commontime.plugin;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.TextView;

public class WebViewCheckerEnableUpdateActivity extends Activity
{
    private final String ENABLE_MESSAGE = "Please enable %s to use this app.";
    private final String ENABLE_BTN_TXT = "Enable %s";
    private final String UPDATE_MESSAGE = "Your current version of %s is %s. Please update %s to %s or higher use this app.";
    private final String UPDATE_BTN_TXT = "Update %s";

    private boolean checkVersionOnResume = false;
    private String packageName;
    private String packageLabel;
    private String currentVersion;
    private String requiredVersion;
    private boolean isWebViewEnabled;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(false);

        packageName = getIntent().getStringExtra(WebViewChecker.PACKAGE_NAME_BUNDLE_KEY);
        packageLabel = WebViewCheckerUtil.getPackageLabel(getPackageManager(), packageName);
        if (packageLabel == null) packageLabel = "Android System WebView";
        requiredVersion = getIntent().getStringExtra(WebViewChecker.REQUIRED_VERSION_BUNDLE_KEY);
        currentVersion = WebViewCheckerUtil.getPackageVersion(getPackageManager(), packageName);
        isWebViewEnabled = WebViewCheckerUtil.isPackageEnabled(getPackageManager(), packageName);

        setUi();

        setResult(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUi()
    {
        if (!isWebViewEnabled)
        {
            setContentView(enableView());
        }
        else
        {
            int versionCompareResult = WebViewCheckerUtil.compareVersions(currentVersion, requiredVersion);
            if (versionCompareResult == 1)
            {
                restartApp();
            }
            else
            {
                setContentView(updateView());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume()
    {
        super.onResume();

        if (!checkVersionOnResume) return;

        currentVersion = WebViewCheckerUtil.getPackageVersion(getPackageManager(), packageName);
        isWebViewEnabled = WebViewCheckerUtil.isPackageEnabled(getPackageManager(), packageName);

        setUi();

        checkVersionOnResume = false;
    }

    private View enableView()
    {
        LinearLayout ll = new LinearLayout(getApplicationContext());

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(60,30,60,30);

        TextView textView = new TextView(getApplicationContext());

        textView.setText(String.format(ENABLE_MESSAGE, packageLabel));
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams lp_tv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp_tv.setMargins(0,0,0,50);

        textView.setLayoutParams(lp_tv);

        ll.addView(textView);

        Button button = new Button(getApplicationContext());

        button.setText(String.format(ENABLE_BTN_TXT, packageLabel));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChromeSettingsPage();
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

    private View updateView()
    {
        LinearLayout ll = new LinearLayout(this);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(60,30,60,30);

        TextView textView = new TextView(getApplicationContext());

        textView.setText(String.format(UPDATE_MESSAGE, packageLabel, currentVersion, packageLabel, requiredVersion));
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams lp_tv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp_tv.setMargins(0,0,0,50);

        textView.setLayoutParams(lp_tv);

        ll.addView(textView);

        Button button = new Button(this);

        button.setText(String.format(UPDATE_BTN_TXT, packageLabel));

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
            checkVersionOnResume = true;
        }
        catch (Exception e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            checkVersionOnResume = true;
        }
    }

    private void openChromeSettingsPage()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("package", packageName, null));
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        startActivity(intent);
        checkVersionOnResume = true;
    }

    private void restartApp()
    {
        Intent mStartActivity = getPackageManager().getLaunchIntentForPackage(getPackageName());
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        finish();
    }
}