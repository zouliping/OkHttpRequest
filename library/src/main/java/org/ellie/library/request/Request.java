package org.ellie.library.request;

import org.ellie.library.OkHttpRequest;
import org.ellie.library.callback.Callback;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class Request<T extends Request> {

    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
    }

    private int mMethod, mId;
    private long mConnectTimeout, mReadTimeout, mWriteTimeout;
    private Object mTag;

    protected String mUrl;
    protected Map<String, String> mHeaders, mParams;

    protected okhttp3.Request.Builder mBuilder;

    public Request(String url) {
        mUrl = url;
        if (OkHttpRequest.getInstance().getHeaders() != null) {
            mHeaders = OkHttpRequest.getInstance().getHeaders();
        }
        if (OkHttpRequest.getInstance().getParams() != null) {
            mParams = OkHttpRequest.getInstance().getParams();
        }

        mBuilder = new okhttp3.Request.Builder();
    }

    protected void initBuilder() {
        mBuilder.tag(mTag);
        if (mHeaders != null && !mHeaders.isEmpty()) {
            for (String key : mHeaders.keySet()) {
                mBuilder.addHeader(key, mHeaders.get(key));
            }
        }
    }

    public T url(String url) {
        mUrl = url;
        return (T) this;
    }

    public T id(int id) {
        mId = id;
        return (T) this;
    }

    public T connectTimeout(long connectTimeout) {
        mConnectTimeout = connectTimeout;
        return (T) this;
    }

    public T readTimeout(long readTimeout) {
        mReadTimeout = readTimeout;
        return (T) this;
    }

    public T writeTimeout(long writeTimeout) {
        mWriteTimeout = writeTimeout;
        return (T) this;
    }

    public T tag(Object tag) {
        mTag = tag;
        return (T) this;
    }

    public T headers(Map<String, String> headers) {
        if (mHeaders == null) {
            mHeaders = new LinkedHashMap<>();
        }
        for (String key : headers.keySet()) {
            mHeaders.put(key, headers.get(key));
        }
        return (T) this;
    }

    public T header(String key, String value) {
        if (mHeaders == null) {
            mHeaders = new LinkedHashMap<>();
        }
        mHeaders.put(key, value);
        return (T) this;
    }

    public T params(Map<String, String> params) {
        if (mParams == null) {
            mParams = new LinkedHashMap<>();
        }
        for (String key : params.keySet()) {
            mParams.put(key, params.get(key));
        }
        return (T) this;
    }

    public T param(String key, String value) {
        if (mParams == null) {
            mParams = new LinkedHashMap<>();
        }
        mParams.put(key, value);
        return (T) this;
    }

    /**
     * 创建 request body
     */
    protected abstract RequestBody buildRequestBody();

    /**
     * 创建 request
     */
    protected abstract okhttp3.Request buildRequest(RequestBody requestBody);

    /**
     * 子类可对 request body 进行一层封装
     */
    protected RequestBody getRequestBody(RequestBody requestBody, Callback callback) {
        return requestBody;
    }

    /**
     * 获取 request
     */
    protected okhttp3.Request getRequest(Callback callback) {
        return buildRequest(getRequestBody(buildRequestBody(), callback));
    }

    /**
     * 创建 call
     */
    public Call buildCall(Callback callback) {
        if (mConnectTimeout > 0 || mReadTimeout > 0 || mWriteTimeout > 0) {

            mConnectTimeout = mConnectTimeout > 0 ? mConnectTimeout : OkHttpRequest.DEFAULT_CONNECT_TIMEOUT;
            mReadTimeout = mReadTimeout > 0 ? mReadTimeout : OkHttpRequest.DEFAULT_READ_TIMEOUT;
            mWriteTimeout = mWriteTimeout > 0 ? mWriteTimeout : OkHttpRequest.DEFAULT_WRITE_TIMEOUT;

            return OkHttpRequest.getInstance().getOkHttpClient().newBuilder()
                    .connectTimeout(mConnectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(mReadTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(mWriteTimeout, TimeUnit.MILLISECONDS)
                    .build()
                    .newCall(getRequest(callback));
        } else {
            return OkHttpRequest.getInstance().getOkHttpClient().newCall(getRequest(callback));
        }
    }

    /**
     * 同步的请求
     */
    public Response execute() throws IOException {
        return buildCall(null).execute();
    }

    /**
     * 异步的请求
     * @param callback 回调
     */
    public void execute(Callback callback) {
        if (callback == null) {
            callback = Callback.DEFAULT_CALLBACK;
        }
        Call call = buildCall(callback);
        final Callback finalCallback = callback;

        if (mId <= 0) {
            mId = new Random().nextInt();
        }

        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                fail(finalCallback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (call.isCanceled()) {
                    fail(finalCallback, new Exception("the request has been cancelled"));
                    return;
                }

                try {
                    Object data = finalCallback.parseNetworkResponse(mId, response);
                    success(finalCallback, data);
                } catch (Exception e) {
                    fail(finalCallback, e);
                }

            }
        });
    }

    private void fail(final Callback callback, final Exception e) {
        if (callback == null) {
            return;
        }

        OkHttpRequest.getInstance().getHandler().post(new Runnable() {

            @Override
            public void run() {
                callback.onError(mId, e);
            }

        });
    }

    private void success(final Callback callback, final Object data) {
        if (callback == null) {
            return;
        }

        OkHttpRequest.getInstance().getHandler().post(new Runnable() {

            @Override
            public void run() {
                callback.onResponse(mId, data);
            }

        });
    }

}