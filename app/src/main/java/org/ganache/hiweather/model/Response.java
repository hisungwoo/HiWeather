
package org.ganache.hiweather.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Response {

    @com.squareup.moshi.Json(name = "header")
    private Header header;
    @com.squareup.moshi.Json(name = "body")
    private Body body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

}
