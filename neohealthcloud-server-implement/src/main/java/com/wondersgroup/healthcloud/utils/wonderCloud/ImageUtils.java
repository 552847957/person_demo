package com.wondersgroup.healthcloud.utils.wonderCloud;

import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.entity.StringResponseWrapper;
import com.wondersgroup.healthcloud.exceptions.Exceptions;

import java.io.IOException;

/**
 * Created by longshasha on 16/5/13.
 */
public class ImageUtils {

    private HttpRequestExecutorManager httpRequestExecutorManager;

    public void setHttpRequestExecutorManager(HttpRequestExecutorManager httpRequestExecutorManager) {
        this.httpRequestExecutorManager = httpRequestExecutorManager;
    }

    public byte[] getImageFromURL(String urlPath) {
        Request request = new Request.Builder().url(urlPath).build();
        try {
            StringResponseWrapper wrapper = (StringResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(StringResponseWrapper.class);
            return wrapper.nativeResponse().body().bytes();
        } catch (IOException ex) {
            throw Exceptions.unchecked(ex);
        }
    }
}
