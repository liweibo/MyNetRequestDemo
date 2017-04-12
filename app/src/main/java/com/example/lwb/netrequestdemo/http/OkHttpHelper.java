package com.example.lwb.netrequestdemo.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHelper {


    public static final String TAG = "OkHttpHelper";

    private static OkHttpHelper mInstance;
    private OkHttpClient mHttpClient;
    private Gson mGson;

    private Handler mHandler;


    static {
        mInstance = new OkHttpHelper();
    }

    private OkHttpHelper() {

        mHttpClient = new OkHttpClient();
        mHttpClient.connectTimeoutMillis();
        mHttpClient.readTimeoutMillis();
        mHttpClient.writeTimeoutMillis();

        mGson = new Gson();

        mHandler = new Handler(Looper.getMainLooper());

    }

    ;

    public static OkHttpHelper getInstance() {
        return mInstance;
    }


    public void get(String url, BaseCallback callback) {


        Request request = buildGetRequest(url);

        request(request, callback);

    }


    public void post(String url, Map<String, String> param, BaseCallback callback) {

        Request request = buildPostRequest(url, param);
        request(request, callback);
    }


    public void request(final Request request, final BaseCallback callback) {

        callback.onBeforeRequest(request);

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackFailure(callback, call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callbackResponse(callback, response);

                if (response.isSuccessful()) {

                    String resultStr = response.body().string();

                    Log.d(TAG, "result=" + resultStr);

                    if (callback.mType == String.class) {
                        callbackSuccess(callback, response, resultStr);
                    } else {
                        try {

                            Object obj = mGson.fromJson(resultStr, callback.mType);
                            callbackSuccess(callback, response, obj);
                        } catch (com.google.gson.JsonParseException e) { // Json解析的错误
                            callback.onError(response, response.code(), e);
                        }
                    }
                } else {
                    callbackError(callback, response, null);
                }

            }
        });

            }



    private void callbackSuccess(final BaseCallback callback, final Response response, final Object obj) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, obj);
            }
        });
    }


    private void callbackError(final BaseCallback callback, final Response response, final Exception e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(response, response.code(), e);
            }
        });
    }


    private void callbackFailure(final BaseCallback callback, final Call call, final IOException e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(call, e);
            }
        });
    }


    private void callbackResponse(final BaseCallback callback, final Response response) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }


    private Request buildPostRequest(String url, Map<String, String> params) {

        return buildRequest(url, HttpMethodType.POST, params);
    }

    private Request buildGetRequest(String url) {

        return buildRequest(url, HttpMethodType.GET, null);
    }

    //生成request对象 第二个参数是get或者post请求，第三个参数是当第二个参数为post时为post提供的数据
    private Request buildRequest(String url, HttpMethodType methodType, Map<String, String> params) {


        Request.Builder builder = new Request.Builder()
                .url(url);
 //关键就是处理这里 其他是常规用法。
        if (methodType == HttpMethodType.POST) {
            RequestBody body = builderFormData(params);
            builder.post(body);
        } else if (methodType == HttpMethodType.GET) {
            builder.get();
        }


        return builder.build();
    }

//生成post的内容
    private RequestBody builderFormData(Map<String, String> params) {

        FormBody.Builder builder = new FormBody.Builder();

        // FormEncodingBuilder builder = new FormEncodingBuilder();老版本的用法

        if (params != null) {

            for (Map.Entry<String, String> entry : params.entrySet()) {

                builder.add(entry.getKey(), entry.getValue());
            }
        }

        return builder.build();

    }


    enum HttpMethodType {

        GET,
        POST,

    }


}
