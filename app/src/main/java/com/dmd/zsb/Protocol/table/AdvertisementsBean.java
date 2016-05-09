package com.dmd.zsb.protocol.table;

import com.activeandroid.DataBaseModel;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/9.
 */
@Table(name = "Advertisements")
public class AdvertisementsBean extends DataBaseModel {
    @Column(name = "img_url")
    public String img_url;
    @Column(name = "link")
    public String link;
    @Column(name = "title")
    public String title;
    @Column(name = "description")
    public String description;

    public void  fromJson(JSONObject jsonObject)  throws JSONException
    {
        if(null == jsonObject){
            return ;
        }

        JSONArray subItemArray;

        this.img_url = jsonObject.optString("img_url");
        this.link = jsonObject.optString("link");
        this.title = jsonObject.optString("title");
        this.description = jsonObject.optString("description");
        return ;
    }

    public JSONObject  toJson() throws JSONException
    {
        JSONObject localItemObject = new JSONObject();
        JSONArray itemJSONArray = new JSONArray();
        localItemObject.put("img_url", img_url);
        localItemObject.put("link", link);
        localItemObject.put("title", title);
        localItemObject.put("description", description);
        return localItemObject;
    }
}