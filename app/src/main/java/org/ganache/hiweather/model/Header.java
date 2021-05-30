
package org.ganache.hiweather.model;

import javax.annotation.Generated;

public class Header {

    @com.squareup.moshi.Json(name = "resultCode")
    private String resultCode;
    @com.squareup.moshi.Json(name = "resultMsg")
    private String resultMsg;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

}
