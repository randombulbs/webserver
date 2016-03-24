package com.randombulbs.webserver.response;

/**
 * Created by nbhatti on 3/24/16.
 */

import com.randombulbs.webserver.HttpProtocolException;

/**
 * The HTTP response is composed of a status line that has the following structure
 * Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
 * This entity class models that structure
 */
public class StatusLine {
    String version = "OK";
    String reason = "";
    int status = 200;

    /**
     * Create a generic http status response
     * @param version
     * @param code
     * @param reason
     * @throws HttpProtocolException When there is an invalid specification for the status.
     */
    public StatusLine(String version, int code, String reason) throws HttpProtocolException {
        // validate code
        if (code > 999 || code < 100) {
            throw new HttpProtocolException("Server created a bad http status");
        }
    }

}
