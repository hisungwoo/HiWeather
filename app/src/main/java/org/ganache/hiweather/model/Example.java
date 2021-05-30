
package org.ganache.hiweather.model;

import com.squareup.moshi.Json;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Example {

    @Json(name = "response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
