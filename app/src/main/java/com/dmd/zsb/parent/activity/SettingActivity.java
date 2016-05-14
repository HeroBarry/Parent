package com.dmd.zsb.parent.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dmd.dialog.GravityEnum;
import com.dmd.dialog.MaterialDialog;
import com.dmd.tutor.eventbus.EventCenter;
import com.dmd.tutor.netstatus.NetUtils;
import com.dmd.tutor.utils.OnUploadProcessListener;
import com.dmd.tutor.utils.XmlDB;
import com.dmd.zsb.common.Constants;
import com.dmd.zsb.mvp.presenter.impl.ChangeAvatarPresenterImpl;
import com.dmd.zsb.mvp.view.ChangeAvatarView;
import com.dmd.zsb.parent.R;
import com.dmd.zsb.mvp.presenter.impl.SettingPresenterImpl;
import com.dmd.zsb.mvp.view.SettingView;
import com.dmd.zsb.parent.activity.base.BaseActivity;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements ChangeAvatarView,OnUploadProcessListener{

    private final static int ACTION_PHOTOGRAPH=1;
    private final static int ACTION_ALBUM=2;

    @Bind(R.id.top_bar_back)
    TextView topBarBack;
    @Bind(R.id.top_bar_title)
    TextView topBarTitle;
    @Bind(R.id.tv_setting_nickname)
    TextView tvSettingNickname;
    @Bind(R.id.tv_setting_avatar)
    TextView tvSettingAvatar;
    @Bind(R.id.tv_setting_signature)
    TextView tvSettingSignature;
    @Bind(R.id.tv_setting_brief)
    TextView tvSettingBrief;
    @Bind(R.id.tv_setting_change_password)
    TextView tvSettingChangePassword;
    @Bind(R.id.tv_setting_feedback)
    TextView tvSettingFeedback;
    @Bind(R.id.btn_sign_out)
    Button btnSignOut;
    private SettingPresenterImpl settingPresenter;
    private ChangeAvatarPresenterImpl changeAvatarPresenter;
    private MaterialDialog dialog;
    private File file = null;
    private String picturePath=null;

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_setting;
    }

    @Override
    public void onEventComming(EventCenter eventCenter) {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void initViewsAndEvents() {
        changeAvatarPresenter=new ChangeAvatarPresenterImpl(this,mContext,this);
        ///settingPresenter=new SettingPresenterImpl(SettingActivity.this,this,this);
        topBarTitle.setText(getResources().getText(R.string.setting_title));
    }

    @Override
    public void showTip(String msg) {
        showToast(msg);
    }

    @Override
    protected void onNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return false;
    }

    @Override
    protected boolean isBindEventBusHere() {
        return false;
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            picturePath = cursor.getString(columnIndex);
            cursor.close();
            file = new File(picturePath);
            if (file != null && file.exists()) {

                JSONObject jsonObject=new JSONObject();
                JSONObject formFile=new JSONObject();
                try {
                    jsonObject.put("appkey", Constants.ZSBAPPKEY);
                    jsonObject.put("version", Constants.ZSBVERSION);
                    jsonObject.put("sid", XmlDB.getInstance(mContext).getKeyString("sid","sid"));
                    jsonObject.put("uid", XmlDB.getInstance(mContext).getKeyString("uid","uid"));
                    jsonObject.put("fileMime", "image/png");

                    formFile.put("fileName",file.getName());
                    formFile.put("filePath",file.getAbsolutePath());
                    formFile.put("parameterName","file");
                    formFile.put("contentType","application/octet-stream");


                }catch (JSONException j){

                }
                changeAvatarPresenter.onChangeAvatar(jsonObject,formFile);
            } else {
                Message msg = Message.obtain();
                msg.what = 3;
                msg.arg1 = 10;
                msg.obj = "图片文件未找到";
                handler.sendMessage(msg);
            }
        }
    }

    @OnClick({R.id.top_bar_back, R.id.tv_setting_nickname, R.id.tv_setting_avatar, R.id.tv_setting_signature, R.id.tv_setting_brief, R.id.tv_setting_change_password, R.id.tv_setting_feedback, R.id.btn_sign_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_bar_back:
                finish();
                break;
            case R.id.tv_setting_nickname:
                readyGo(NickNameActivity.class);
                break;
            case R.id.tv_setting_avatar:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
                break;
            case R.id.tv_setting_signature:
                readyGo(SignatureActivity.class);
                break;
            case R.id.tv_setting_brief:
                readyGo(BriefActivity.class);
                break;
            case R.id.tv_setting_change_password:
                readyGo(ChangePasswordActivity.class);
                break;
            case R.id.tv_setting_feedback:
                readyGo(FeedbackActivity.class);
                break;
            case R.id.btn_sign_out:
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("sid", XmlDB.getInstance(mContext).getKeyString("sid","sid"));
                    jsonObject.put("uid", XmlDB.getInstance(mContext).getKeyString("uid","uid"));
                    settingPresenter.onSignOut(1,jsonObject);
                }catch (JSONException j){

                }

                break;
        }
    }

    @Override
    public void onUploadDone(int responseCode, String message) {
        Message msg = Message.obtain();
        msg.what = 3;
        msg.arg1 = responseCode;
        msg.obj = message;
        handler.sendMessage(msg);
    }

    @Override
    public void onUploadProcess(int uploadSize) {
        Message msg = Message.obtain();
        msg.what = 2;
        msg.arg1 = uploadSize;
        msg.obj = "传送中。。。。";
        handler.sendMessage(msg);
    }

    @Override
    public void initUpload(int fileSize) {
        Message msg = Message.obtain();
        msg.what = 1;
        msg.arg1 = fileSize;
        handler.sendMessage(msg);
    }


    @Override
    public void showError(String msg) {
        showToast(msg);
    }

    private Handler handler = new Handler() {
        private  int length=0;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://开始打开进度对话框
                    length=msg.arg1;
                    dialog= new MaterialDialog.Builder(SettingActivity.this)
                            .title("上传进度")
                            .content("请稍后...")
                            .contentGravity(GravityEnum.CENTER)
                            .progress(false, 100, true)
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {

                                }
                            })
                            .showListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {

                                }
                            }).show();
                    break;
                case 2://进度
                    int i=msg.arg1*100/length;
                    if(i%10==0){
                        dialog.setProgress(i);
                    }
                    break;
                case 3://异常 or 结束 or 完成
                    switch (msg.arg1) {
                        case 10: {
                            showToast(msg.obj.toString());
                            break;
                        }
                        case 11: {
                            showToast(msg.obj.toString());
                            break;
                        }
                        case 12: {
                            showToast(msg.obj.toString());
                            break;
                        }
                    }
                    dialog.dismiss();
                    dialog=null;
                    break;
            }
        }
    };
}
