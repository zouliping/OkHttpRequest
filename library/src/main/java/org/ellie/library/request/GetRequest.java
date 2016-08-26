package org.ellie.library.request;

import android.text.TextUtils;

import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class GetRequest extends Request<GetRequest> {

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected okhttp3.Request buildRequest(RequestBody requestBody) {
        initBuilder();
        return mBuilder.url(buildUrlParams()).get().build();
    }

    private String buildUrlParams() {
        if (mParams == null || mParams.isEmpty()) {
            return mUrl;
        }

        try {
            HttpUrl.Builder builder = HttpUrl.parse(mUrl).newBuilder();
            for (String key : mParams.keySet()) {
                builder.addQueryParameter(key, mParams.get(key));
            }
            return builder.build().toString();
        } catch (Exception e) {
            return mUrl;
        }
    }
}
