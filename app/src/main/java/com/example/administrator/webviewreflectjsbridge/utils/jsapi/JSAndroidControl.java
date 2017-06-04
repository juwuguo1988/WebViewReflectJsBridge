package com.example.administrator.webviewreflectjsbridge.utils.jsapi;


import android.widget.Toast;
import com.example.administrator.webviewreflectjsbridge.utils.jsbridge.JSBridge;
import org.json.JSONObject;

/**
 * Created by Juwuguo on 2017/5/5.
 */

public class JSAndroidControl {
    public void showContentData(JSBridge jsBridge, Long callId, JSONObject requestParams,String url) {
        Toast.makeText(jsBridge.getActivity(),requestParams.toString(),Toast.LENGTH_LONG).show();
    }

    public void closeActivity(JSBridge jsBridge, Long callId, JSONObject requestParams,String url) {
        jsBridge.getActivity().finish();
    }
}
