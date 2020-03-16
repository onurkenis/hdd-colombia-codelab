package com.dtse.codelabcolombia;

import android.app.Application;

import com.huawei.hianalytics.hms.HiAnalyticsTools;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;

public class MainApplication extends Application {

    public static HiAnalyticsInstance hiAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Analytics Kit Log
        HiAnalyticsTools.enableLog();
        // Generate the Analytics Instance
        hiAnalytics = HiAnalytics.getInstance(this);
        // Enable collection capability
        hiAnalytics.setAnalyticsEnabled(true);
        // Enable Automatically collection capability
        hiAnalytics.setAutoCollectionEnabled(true);
        // Register the HMS service and collect automatic events (account events).
        // Automatic account event collection requires HMS APK 4.0.0.300 or a later version.
        hiAnalytics.regHmsSvcEvent();
    }
}
