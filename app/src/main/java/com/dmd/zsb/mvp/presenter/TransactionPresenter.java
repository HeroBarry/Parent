package com.dmd.zsb.mvp.presenter;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/13.
 */
public interface TransactionPresenter {
    void onTransaction(int event_tag, JSONObject jsonObject);
}
