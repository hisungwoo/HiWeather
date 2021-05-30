
package org.ganache.hiweather.model;
import javax.annotation.Generated;

public class Body {

    @com.squareup.moshi.Json(name = "dataType")
    private String dataType;
    @com.squareup.moshi.Json(name = "items")
    private Items items;
    @com.squareup.moshi.Json(name = "pageNo")
    private int pageNo;
    @com.squareup.moshi.Json(name = "numOfRows")
    private int numOfRows;
    @com.squareup.moshi.Json(name = "totalCount")
    private int totalCount;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
