package com.randombulbs.webserver.response;

import com.randombulbs.webserver.body.HttpMessageBody;
import com.randombulbs.webserver.header.HttpHeader;

import java.io.OutputStream;

/**
 * Created by nbhatti on 3/23/16.
 */

/**
 * This entity class represents an HTTP response.
 */
public class Response {
    public StatusLine status;
    public HttpHeader header;
    public HttpMessageBody body;

    /**
     * Create a response for client
     * @param status The response status
     * @param header key-value pairs for the header
     * @param body The message body
     */
    public Response(StatusLine status, HttpHeader header, HttpMessageBody body) {
        this.status = status;
        this.header = header;
        this.body = body;
    }
}
