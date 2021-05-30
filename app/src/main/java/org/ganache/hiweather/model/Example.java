
package org.ganache.hiweather.model;

import com.squareup.moshi.Json;

import javax.annotation.Generated;

public class Example {

    @Json(name = "response")
    private Repos response;

    public Repos getResponse() {
        return response;
    }

    public void setResponse(Repos response) {
        this.response = response;
    }

}
