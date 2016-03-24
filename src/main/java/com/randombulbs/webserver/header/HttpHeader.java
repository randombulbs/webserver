package com.randombulbs.webserver.header;

/**
 * Created by nbhatti on 3/23/16.
 */

import com.randombulbs.webserver.HttpProtocolException;

//@todo replace parsing with native implementation and remove the dependency on commons-httpclient in sbt file
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * The HttpHeader class represent http header.
 * The parsing is done as per specification here
 * http://tools.ietf.org/html/rfc7230#section-3.1
 */
public class HttpHeader {
    public static String httpHeaderCharsetString = "ISO-8859-1";
    public static Charset httpHeaderCharset = Charset.forName(httpHeaderCharsetString);

    /**
     *
     * @param is The input stream is parsed to extract http header fields
     * The extraction is done based on following specifications
     *                    header-field   = field-name ":" OWS field-value OWS
     *                    field-name     = token
     *                    field-value    = *( field-content / obs-fold )
     *                    field-content  = field-vchar [ 1*( SP / HTAB ) field-vchar ]
     *                    field-vchar    = VCHAR / obs-text
     *                    obs-fold       = CRLF 1*( SP / HTAB );
     *                    obsolete line folding;
     *                    see Section 3.2.4
     * @return A hashmap of HTTP field-values
     * @throws HttpProtocolException if not as per HTTP protocol
     */
    public static HttpHeader parseHeaders(InputStream is) throws HttpProtocolException, IOException {
        Map<String, String> headerMap = new HashMap<>();

        Header headers[] = HttpParser.parseHeaders(is, httpHeaderCharsetString);
        for (Header he : headers) {
            String fieldName = he.getName().toLowerCase();
            String fieldValue = he.getValue();
            headerMap.put(fieldName, fieldValue);
        }
        return new HttpHeader(headerMap);
    }
    Map<String, String> headers;
    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }
    public HttpHeader() {
        this.headers = new HashMap<>();
    }
    public String add(String key, String value) {
        return this.headers.put(key, value);
    }
    public String get(String key) {
        return this.headers.get(key);
    }
    public Map<String, String> map() {
        return this.headers;
    }
}
