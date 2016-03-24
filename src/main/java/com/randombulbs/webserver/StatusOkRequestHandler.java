package com.randombulbs.webserver;

import com.randombulbs.webserver.body.HttpMessageBody;
import com.randombulbs.webserver.header.HttpHeader;
import com.randombulbs.webserver.response.Response;
import com.randombulbs.webserver.response.StatusLine;

import java.util.Map;

/**
 * Created by nbhatti on 3/23/16.
 */

/***
 * This is a simple request handle that responds with 220 OK
 */
public class StatusOkRequestHandler implements IRequestHandler {
    /**
     * Implements the handleRequest interface by returning a 200 OK response.
     */
    @Override
    public Response handleRequest(
            String target,
            String method,
            Map<String, String> headerMap,
            HttpMessageBody body
    ) {
        try {
            HttpHeader header = new HttpHeader();
            Response response = new Response(new StatusLine("Http/1.1", 200, "OK"), header, new HttpMessageBody(""));
            return response;
        } catch (HttpProtocolException ex) {
            ex.printStackTrace();
            //throw ex;
        }
        return null;
    }
}
