package org.ellie.library.callback;

import okhttp3.Response;

public abstract class Callback<T> {

    /**
     * 在子线程中执行,将 response 转化为所需的数据类型
     */
    public abstract T parseNetworkResponse(int id, Response response);

    /**
     * 在 UI 线程执行,返回结果
     */
    public abstract void onResponse(int id, T data);

    /**
     * 在 UI 线程执行,返回错误
     */
    public abstract void onError(int id, Exception e);

    /**
     * 在 UI 线程执行,返回当前进度,可用于上传和下载
     */
    public void onProgress(int id, float progress, long current, long total) {
    }

}
