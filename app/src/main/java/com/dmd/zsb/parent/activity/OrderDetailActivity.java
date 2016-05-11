package com.dmd.zsb.parent.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmd.pay.AliPayActivity;
import com.dmd.pay.entity.PayInfo;
import com.dmd.tutor.eventbus.EventCenter;
import com.dmd.tutor.netstatus.NetUtils;
import com.dmd.tutor.utils.XmlDB;
import com.dmd.zsb.api.ApiConstants;
import com.dmd.zsb.common.Constants;
import com.dmd.zsb.entity.OrderEntity;
import com.dmd.zsb.mvp.presenter.impl.ConfirmPayPresenterImpl;
import com.dmd.zsb.mvp.view.ConfirmPayView;
import com.dmd.zsb.parent.R;
import com.dmd.zsb.parent.activity.base.BaseActivity;
import com.dmd.zsb.protocol.response.confirmpayResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

public class OrderDetailActivity extends BaseActivity implements ConfirmPayView {
    @Bind(R.id.top_bar_back)
    TextView topBarBack;
    @Bind(R.id.top_bar_title)
    TextView topBarTitle;
    @Bind(R.id.img_header)
    ImageView imgHeader;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_appointed_time)
    TextView tvAppointedTime;
    @Bind(R.id.tv_charging)
    TextView tvCharging;
    @Bind(R.id.tv_curriculum)
    TextView tvCurriculum;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_place)
    TextView tvPlace;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.btn_confirm_pay)
    Button btnConfirmPay;

    private OrderEntity data;
    private ConfirmPayPresenterImpl confirmPayPresenter;
    @Override
    protected void getBundleExtras(Bundle extras) {
        data = (OrderEntity) extras.getSerializable("data");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_order_detail;
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
        confirmPayPresenter=new ConfirmPayPresenterImpl(mContext,this);
        Picasso.with(mContext).load(ApiConstants.Urls.API_IMG_BASE_URLS + data.getImg_header()).into(imgHeader);
        tvName.setText(data.getName());
        tvType.setText(data.getType());
        tvSex.setText(data.getSex());
        tvAppointedTime.setText(data.getAppointed_time());
        tvCharging.setText(data.getCharging());
        tvCurriculum.setText(data.getCurriculum());
        tvAddress.setText(data.getAddress());
        tvPlace.setText(data.getPlace());
        if (data.getState().equals("2")) {
            tvState.setText("未付款");
        } else if (data.getState().equals("3")) {
            tvState.setText("已付款");
        }
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


    @OnClick({R.id.top_bar_back, R.id.btn_confirm_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_bar_back:
                finish();
                break;
            case R.id.btn_confirm_pay:
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("appkey", Constants.ZSBAPPKEY);
                    jsonObject.put("version", Constants.ZSBVERSION);
                    jsonObject.put("sid", XmlDB.getInstance(mContext).getKeyString("sid","sid"));
                    jsonObject.put("uid", XmlDB.getInstance(mContext).getKeyString("uid","uid"));
                    jsonObject.put("oid","");
                }catch (JSONException j){

                }

                confirmPayPresenter.onConfirmPay(jsonObject);


                break;
        }
    }

    @Override
    public void setConfirmPayView(confirmpayResponse response) {
        if (response.errno==0){
            PayInfo payInfo=new PayInfo();
            payInfo.setName("课时费用");
            payInfo.setDesc("授课完成，支付费用");
            payInfo.setPrice(Double.parseDouble("0.1"));
            payInfo.setRate(1.0);
            Bundle bundle=new Bundle();
            bundle.putSerializable("payInfo",payInfo);
            readyGoForResult(AliPayActivity.class,110,bundle);
        }else {
            showTip(response.msg);
        }
    }

    @Override
    public void showTip(String msg) {
        showToast(msg);
    }
}
