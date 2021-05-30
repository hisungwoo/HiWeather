
package org.ganache.hiweather.model;

import com.squareup.moshi.Json;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Items {

    @Json(name = "item")
    private List<Item> item = null;

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }

}
