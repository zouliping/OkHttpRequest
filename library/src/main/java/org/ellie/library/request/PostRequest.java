package org.ellie.library.request;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
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
            return buildFormBody().build();
        } else {
            return buildMultipartBody().build();
        }
    }

    private FormBody.Builder buildFormBody() {
        FormBody.Builder builder = new FormBody.Builder();

        if (mParams != null && !mParams.isEmpty()) {
            for (String key : mParams.keySet()) {
                builder.add(key, mParams.get(key));
            }
        }

        return builder;
    }

    private MultipartBody.Builder buildMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (mParams != null && !mParams.isEmpty()) {
            for (String key : mParams.keySet()) {
                builder.addFormDataPart(key, mParams.get(key));
            }
        }

        for (String key : mFiles.keySet()) {
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), mFiles.get(key));
            builder.addFormDataPart(key, mFiles.get(key).getName(), body);
        }

        return builder;
    }

    @Override
    protected okhttp3.Request buildRequest(RequestBody requestBody) {
        initBuilder();
        return mBuilder.url(mUrl).post(requestBody).build();
    }
}
