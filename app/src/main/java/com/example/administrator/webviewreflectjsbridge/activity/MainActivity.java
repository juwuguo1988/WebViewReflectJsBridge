package com.example.administrator.webviewreflectjsbridge.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.administrator.webviewreflectjsbridge.R;
import com.example.administrator.webviewreflectjsbridge.utils.jsbridge.JSBridge;

public class MainActivity extends AppCompatActivity {

    private WebView wv_web_view;
    private JSBridge jsBridge;
    //自定义JS 请求协议：JSjsbridge:///request?class=指定调用的类名&method=指定调用的方法名&params=指定的参数&callId=指定的请求ID
    private static final String JS_REQUEST_PREFIX = JSBridge.MY_JS_BRIDGE + ":///request?";
    private static final String JS_REQUEST_CLASS_URL = "url";
    private static final String JS_REQUEST_CLASS_KEY = "class";
    private static final String JS_REQUEST_METHOD_KEY = "method";
    private static final String JS_REQUEST_PARAMETERS_KEY = "params";
    private static final String JS_REQUEST_CALL_ID_KEY = "callId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initData();
        initView();
    }

    private void initData() {
        wv_web_view = (WebView) findViewById(R.id.wv_web_view);
        //1.实例化JSBridge，配置WebView
        jsBridge = new JSBridge(this, wv_web_view);
        jsBridge.configWebView();
        //wv_web_view.loadUrl("file:///android_asset/jsdemo_console.html");
        wv_web_view.loadUrl("file:///android_asset/jsdemo_alert.html");
    }


    protected void initView() {
        wv_web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //页面上重定向时，给页面header添加token
/*                Map<String, String> additionalHttpHeaders = new HashMap<>();
                additionalHttpHeaders.put("Authorization", mAccessToken);
                view.loadUrl(url, additionalHttpHeaders);*/
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });

        wv_web_view.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (!TextUtils.isEmpty(title)) {
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (message.startsWith(JS_REQUEST_PREFIX)) {
                    if (jsBridge == null) {
                        result.cancel();
                        return true;
                    }
                    parseJSProtocol(message);
                    result.cancel();
                    return true;
                }
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = consoleMessage.message().toString();
                if (message.startsWith(JS_REQUEST_PREFIX)) {
                    if (jsBridge == null) {
                        return true;
                    }
                    parseJSProtocol(message);
                    return true;
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    /**
     * 解析JS协议
     *
     * @param message: JSjsbridge:///request?url=传递过来的url,class=指定调用的类名&method=指定调用的方法名&params=指定的参数&callId=指定的请求ID
     */
    private void parseJSProtocol(String message) {
        String[] tokens = message.substring(JS_REQUEST_PREFIX.length()).split("&");
        String target = null;
        String method = null;
        String params = null;
        String url = null;
        long callID = -1;

        for (String token : tokens) {
            String[] pair = token.split("=");
            try {
                String key = pair[0];
                String value = Uri.decode(pair[1]);
                if (JS_REQUEST_CLASS_URL.equals(key)) {
                    url = value;
                } else if (JS_REQUEST_CLASS_KEY.equals(key)) {
                    target = value;
                } else if (JS_REQUEST_METHOD_KEY.equals(key)) {
                    method = value;
                } else if (JS_REQUEST_PARAMETERS_KEY.equals(key)) {
                    params = value;
                } else if (JS_REQUEST_CALL_ID_KEY.equals(key)) {
                    callID = Long.parseLong(value);
                }
            } catch (Exception e) {
                // Ignores.
            }
        }

        if (url != null && target != null && method != null && callID >= 0) {
            jsBridge.requestAndroid(url, target, method, params, callID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
