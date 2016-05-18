package com.dmd.zsb.protocol.response;

import com.activeandroid.DataBaseModel;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.dmd.zsb.protocol.table.DemandsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/5/4.
 */
@Table(name = "demandResponse")
public class demandResponse extends Model implements Serializable {

    @Column(name = "errno")
    public int errno;
    @Column(name = "msg")
    public String msg;
    @Column(name = "total_count")
    public int total_count;

    @Column(name = "demands")
    public List<DemandsBean> demands;

    public demandResponse() {
        super();
    }

    public demandResponse(int errno, String msg, int total_count, List<DemandsBean> demands) {
        super();
        this.errno = errno;
        this.msg = msg;
        this.total_count = total_count;
        this.demands = demands;
    }

    public void  fromJson(JSONObject jsonObject)  throws JSONException {
        if (null == jsonObject) {
            return;
        }

        JSONArray subItemArray;
        this.errno = jsonObject.optInt("errno");
        this.total_count = jsonObject.optInt("total_count");
        this.msg = jsonObject.optString("msg");
        subItemArray=jsonObject.optJSONArray("demands");
        if(null != subItemArray) {
            for (int i = 0; i < subItemArray.length(); i++) {
                JSONObject subItemObject = subItemArray.getJSONObject(i);
                DemandsBean subItem = new DemandsBean();
                subItem.fromJson(subItemObject);
                this.demands.add(subItem);
            }
        }
    }
    public JSONObject  toJson() throws JSONException {
        JSONObject localItemObject = new JSONObject();
        JSONArray itemJSONArray = new JSONArray();
        localItemObject.put("errno", errno);
        localItemObject.put("total_count", total_count);
        localItemObject.put("msg", msg);
        for(int i =0; i< demands.size(); i++)
        {
            DemandsBean itemData =demands.get(i);
            JSONObject itemJSONObject = itemData.toJson();
            itemJSONArray.put(itemJSONObject);
        }
        localItemObject.put("demands", itemJSONArray);
        return localItemObject;
    }
}
