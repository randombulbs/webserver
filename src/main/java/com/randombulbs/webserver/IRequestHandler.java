package com.randombulbs.webserver;

/**
 * Created by nbhatti on 3/23/16.
 */

import com.randombulbs.webserver.body.HttpMessageBody;
import com.randombulbs.webserver.response.Response;

import java.io.InputStream;
import java.util.Map;

/**
 * The http request can be handled by implementing this interface
 */
public interface  IRequestHandler {
    public Response handleRequest(
            String target,
            String method,
            Map<String, String> headerMap,
            HttpMessageBody body
    );
}
