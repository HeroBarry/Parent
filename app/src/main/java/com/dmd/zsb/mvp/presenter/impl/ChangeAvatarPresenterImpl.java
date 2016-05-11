package com.dmd.zsb.mvp.presenter.impl;

import android.content.Context;

import com.dmd.tutor.utils.OnUploadProcessListener;
import com.dmd.zsb.mvp.interactor.impl.ChangeAvatarInteractorImpl;
import com.dmd.zsb.mvp.listeners.BaseSingleLoadedListener;
import com.dmd.zsb.mvp.presenter.ChangeAvatarPresenter;
import com.dmd.zsb.mvp.view.ChangeAvatarView;
import com.dmd.zsb.protocol.request.changeavatarRequest;
import com.dmd.zsb.protocol.response.changeavatarResponse;
import com.dmd.zsb.protocol.table.FORMFILE;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/5.
 */
public class ChangeAvatarPresenterImpl implements ChangeAvatarPresenter,BaseSingleLoadedListener<changeavatarResponse> {
    private OnUploadProcessListener uploadProcessListener;
    private ChangeAvatarInteractorImpl changeAvatarInteractor;
    private ChangeAvatarView changeAvatarView;
    private Context mContext;

    public ChangeAvatarPresenterImpl(ChangeAvatarView changeAvatarView, Context mContext,OnUploadProcessListener uploadProcessListener) {
        this.changeAvatarView = changeAvatarView;
        this.mContext = mContext;
        changeAvatarInteractor=new ChangeAvatarInteractorImpl(this,uploadProcessListener);
    }

    @Override
    public void onChangeAvatar(JSONObject jsonObject,JSONObject file) {
        changeAvatarInteractor.getCommonSingleData(jsonObject,file);
    }

    @Override
    public void onSuccess(changeavatarResponse response) {
        onError(response.msg);
    }

    @Override
    public void onError(String msg) {
        changeAvatarView.showTip(msg);
    }

    @Override
    public void onException(String msg) {
        onError(msg);
    }
}
