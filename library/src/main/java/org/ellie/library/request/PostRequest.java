package org.ellie.library.request;

import org.ellie.library.utils.HttpUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostRequest extends Request<PostRequest> {

    private Map<String, File> mFiles;

    public PostRequest file(String key, File file) {
        if (mFiles == null) {
            mFiles = new HashMap<>();
        }
        mFiles.put(key, file);
        return this;
    }

    public PostRequest files(Map<String, File> files) {
        if (files == null || files.isEmpty()) {
            return this;
        }

        if (mFiles == null) {
            mFiles = new HashMap<>();
        }
        for (String key : files.keySet()) {
            mFiles.put(key, files.get(key));
        }
        return this;
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (mFiles == null || mFiles.isEmpty()) {
            return HttpUtil.buildFormBody(mParams).build();
        } else {
            return HttpUtil.buildMultipartBody(mParams, mFiles).build();
        }
    }

    @Override
    protected okhttp3.Request buildRequest(RequestBody requestBody) {
        initBuilder();
        return mBuilder.url(mUrl).post(requestBody).build();
    }
}
