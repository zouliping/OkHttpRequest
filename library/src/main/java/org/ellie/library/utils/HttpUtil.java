package org.ellie.library.utils;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class HttpUtil {

    public static FormBody.Builder buildFormBody(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }

        return builder;
    }

    public static MultipartBody.Builder buildMultipartBody(Map<String, String> params, Map<String, File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key));
            }
        }

        for (String key : files.keySet()) {
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), files.get(key));
            builder.addFormDataPart(key, files.get(key).getName(), body);
        }

        return builder;
    }

}
