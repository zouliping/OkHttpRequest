package org.ellie.library.request;

import org.ellie.library.utils.HttpUtil;

import okhttp3.RequestBody;

public class PutRequest extends Request<PutRequest> {

    @Override
    protected RequestBody buildRequestBody() {
        return HttpUtil.buildFormBody(mParams).build();
    }

    @Override
    protected okhttp3.Request buildRequest(RequestBody requestBody) {
        initBuilder();
        return mBuilder.url(mUrl).put(requestBody).build();
    }

}
