package org.ellie.library.request;

import org.ellie.library.utils.HttpUtil;

import okhttp3.RequestBody;

public class DeleteRequest extends Request<DeleteRequest> {

    @Override
    protected RequestBody buildRequestBody() {
        return HttpUtil.buildFormBody(mParams).build();
    }

    @Override
    protected okhttp3.Request buildRequest(RequestBody requestBody) {
        initBuilder();
        return mBuilder.url(mUrl).delete(requestBody).build();
    }
}
