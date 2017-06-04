package com.example.administrator.webviewreflectjsbridge.utils.jsbridge;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import java.lang.reflect.Method;
import org.json.JSONException;
import org.json.JSONObject;

;

/**
 * Created by Juwuguo on 2017/5/5.
 */

public class JSBridge {
    private static final String TAG = "JSBridge";
    public static final String MY_JS_BRIDGE = "jwgjsbridge";
    private Activity activity;
    private WebView mWebView;

    public JSBridge(Activity activity, WebView webView) {
        this.activity = activity;
        this.mWebView = webView;
    }

    public Activity getActivity() {
        return activity;
    }

    /**
     * 配置WebView
     */
    public void configWebView() {
        try {
            WebSettings settings = this.mWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setDatabaseEnabled(true);
            settings.setBuiltInZoomControls(false);
            settings.setDomStorageEnabled(true);
            settings.setAppCacheEnabled(true);
            mWebView.setWebContentsDebuggingEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "configWebView error.");
        }
    }

    /**
     * 由JS发起的对android端的请求
     *
     * @param className  类名
     * @param methodName 方法名
     * @param params     参数
     * @param callID     请求ID
     */
    public void requestAndroid(final String url,final String className, final String methodName,
            final String params, final long callID) {
        this.mWebView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拼接全类名: 包名.jsapi.className
                    String fullClassName = mWebView.getContext().getPackageName() + ".utils.jsapi" + "." + className;
                    Class<?> cls = Class.forName(fullClassName);
                    //JSAPI 方法形参为(JSBridge jsbridge,long callId,JSONObject params)
                    Method declaredMethod = cls.getDeclaredMethod(methodName, JSBridge.class, Long.class, JSONObject.class,String.class);
                    Object instance = cls.newInstance();
                    //将请求参数转换成JSONObject
                    JSONObject requestParams;
                    try {
                        requestParams = new JSONObject(params);
                    } catch (JSONException e) {
                        requestParams = new JSONObject();
                    }
                    //反射调用JSAPI
                    declaredMethod.invoke(instance, JSBridge.this, callID, requestParams,url);

                } catch (Exception e) {
                    reportError(callID);
                }
            }
        });

        Log.e(TAG, "requestAndroid : " + className + " , " + methodName + " , " + params);
    }

    /**
     * 报告JS错误
     *
     * @param callID
     */
    public void reportError(long callID) {
        reportError(callID, null);
    }

    /**
     * 报告JS错误
     *
     * @param callID
     * @param jsonObject
     */
    public void reportError(long callID, JSONObject jsonObject) {
        String params = null;
        if (jsonObject != null) {
            params = jsonObject.toString();
        }
        callbackJS(callID,JSCallBackType.ERROR, params);
    }

    /**
     * 报告JS成功
     *
     * @param callID
     */
    public void reportSuccess(long callID) {
        reportSuccess(callID, null);
    }

    /**
     * 报告JS成功
     *
     * @param callID
     * @param jsonObject
     */
    public void reportSuccess(long callID, JSONObject jsonObject) {
        String params = null;
        if (jsonObject != null) {
            params = jsonObject.toString();
        }
        callbackJS(callID, JSCallBackType.SUCCESS, params);
    }

    /**
     * 回调JS
     *
     * @param callID 请求ID (由JS请求android端时带过来的请求ID)
     * @param type   JSAPI执行成功与否
     * @param params 回传参数
     */
    private void callbackJS(long callID,JSCallBackType type, String params) {
        try {
            if (callID < 0) {
                return;
            }
            //组装回调js
            StringBuilder js = new StringBuilder("javascript:");
            js.append(MY_JS_BRIDGE);
            js.append(".callbackFromNative(");
            js.append(callID);
            js.append(",");
            js.append(type.getValue());

            if (TextUtils.isEmpty(params)) {
                js.append(",{});");
            } else {
                js.append(",");
                js.append(params);
                js.append(");");
            }
            String callbackJS = js.toString();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //4.4及以上使用evaluateJavascript
                this.mWebView.evaluateJavascript(callbackJS, null);
            } else {
                this.mWebView.loadUrl(callbackJS);
            }
            Log.e(TAG, "callbackJS : " + callbackJS);
        } catch (Exception e) {
            //ignore
        }
    }
}
