package com.yang.java.wechat;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class IService extends AccessibilityService{

    private static final String WX_WEB="com.tencent.mm.plugin.webview.ui.tools.WebViewUI";
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        Log.i("ja","类名:"+event.getClassName());

    }

    @Override
    public void onInterrupt() {

    }


}
