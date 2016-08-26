package org.ellie.library;

import android.os.Handler;
import android.os.Looper;

import org.ellie.library.request.GetRequest;
import org.ellie.library.request.Request;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpRequest {

    public static final long DEFAULT_CONNECT_TIMEOUT = 60000;
    public static final long DEFAULT_READ_TIMEOUT = 60000;
    public static final long DEFAULT_WRITE_TIMEOUT = 60000;

    private volatile static OkHttpRequest mInstance;
    private OkHttpClient.Builder mClientBuilder;
    private Handler mHandler;

    private Map<String, String> mHeaders, mParams;

    private OkHttpRequest() {
        mClientBuilder = new OkHttpClient.Builder()
        .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
        .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpRequest getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpRequest.class) {
                mInstance = new OkHttpRequest();
            }
        }

        return mInstance;
    }

    /**
     * 设置全局连接超时时间
     */
    public OkHttpRequest setConnectTimeout(long connectTimeout) {
        mClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 设置全局读取超时时间
     */
    public OkHttpRequest setReadTimeout(long readTimeout) {
        mClientBuilder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 设置全局写超时时间
     */
    public OkHttpRequest setWriteTimeout(long writeTimeout) {
        mClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 在 debug 模式下, 输出 log
     * @param isDebug 是否 debug
     */
    public OkHttpRequest addLogInterceptor(boolean isDebug) {
        if (isDebug) {
            mClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        return this;
    }

    /**
     * 添加拦截器
     */
    public OkHttpRequest addInterceptor(Interceptor interceptor) {
        mClientBuilder.addInterceptor(interceptor);
        return this;
    }

    /**
     * 设置统一的 headers
     * @param headers 通用 headers
     */
    public OkHttpRequest addHeaders(Map<String, String> headers) {
        mHeaders = headers;
        return this;
    }

    /**
     * 设置统一的 params
     * @param params 通用 params
     */
    public OkHttpRequest addParams(Map<String, String> params) {
        mParams = params;
        return this;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return mClientBuilder;
    }

    public OkHttpClient getOkHttpClient() {
        return mClientBuilder.build();
    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 取消指定 tag 的请求
     */
    public void cancel(Object tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public static Request get(String url) {
        return new GetRequest(url);
    }
}
