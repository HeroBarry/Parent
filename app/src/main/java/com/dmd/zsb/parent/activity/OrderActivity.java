package com.dmd.zsb.parent.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dmd.tutor.adapter.ListViewDataAdapter;
import com.dmd.tutor.adapter.ViewHolderBase;
import com.dmd.tutor.adapter.ViewHolderCreator;
import com.dmd.tutor.eventbus.EventCenter;
import com.dmd.tutor.netstatus.NetUtils;
import com.dmd.tutor.utils.XmlDB;
import com.dmd.tutor.widgets.XSwipeRefreshLayout;
import com.dmd.zsb.parent.R;
import com.dmd.zsb.api.ApiConstants;
import com.dmd.zsb.common.Constants;
import com.dmd.zsb.entity.OrderEntity;
import com.dmd.zsb.entity.response.OrderResponse;
import com.dmd.zsb.mvp.presenter.impl.OrderPresenterImpl;
import com.dmd.zsb.mvp.view.OrderView;
import com.dmd.zsb.parent.activity.base.BaseActivity;
import com.dmd.zsb.protocol.response.orderResponse;
import com.dmd.zsb.protocol.table.OrdersBean;
import com.dmd.zsb.utils.UriHelper;
import com.dmd.zsb.widgets.LoadMoreListView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderActivity extends BaseActivity implements OrderView, LoadMoreListView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    @Bind(R.id.my_order_menu_group)
    RadioGroup myOrderMenuGroup;
    @Bind(R.id.fragment_my_order_list_list_view)
    LoadMoreListView fragmentMyOrderListListView;
    @Bind(R.id.fragment_my_order_list_swipe_layout)
    XSwipeRefreshLayout fragmentMyOrderListSwipeLayout;
    @Bind(R.id.my_order_group_menu_incomplete)
    RadioButton myOrderGroupMenuIncomplete;
    @Bind(R.id.my_order_group_menu_recent_completed)
    RadioButton myOrderGroupMenuRecentCompleted;
    @Bind(R.id.top_bar_back)
    TextView topBarBack;
    @Bind(R.id.top_bar_title)
    TextView topBarTitle;

    private OrderPresenterImpl orderPresenter;
    private ListViewDataAdapter<OrdersBean> mListViewAdapter;
    private int page = 1;

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_order;
    }

    @Override
    public void onEventComming(EventCenter eventCenter) {

    }

    @Override
    protected View getLoadingTargetView() {
        return fragmentMyOrderListSwipeLayout;
    }

    @Override
    protected void initViewsAndEvents() {
        topBarTitle.setText("我的订单");
        myOrderGroupMenuIncomplete.setChecked(true);
        orderPresenter = new OrderPresenterImpl(mContext, this);
        if (NetUtils.isNetworkConnected(mContext)) {
            if (null != fragmentMyOrderListSwipeLayout) {
                fragmentMyOrderListSwipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("appkey", Constants.ZSBAPPKEY);
                            jsonObject.put("version", Constants.ZSBVERSION);
                            jsonObject.put("sid", XmlDB.getInstance(mContext).getKeyString("sid", "sid"));
                            jsonObject.put("uid", XmlDB.getInstance(mContext).getKeyString("uid", "uid"));
                            jsonObject.put("page", page);
                            jsonObject.put("rows", ApiConstants.Integers.PAGE_LIMIT);
                            if (myOrderGroupMenuIncomplete.isChecked()) {
                                jsonObject.put("order_status", 1);
                            } else if (myOrderGroupMenuRecentCompleted.isChecked()) {
                                jsonObject.put("order_status", 2);
                            } else {
                                jsonObject.put("order_status", 1);
                            }
                        }catch (JSONException j){

                        }

                        orderPresenter.onOrder(Constants.EVENT_REFRESH_DATA, jsonObject);
                    }
                }, ApiConstants.Integers.PAGE_LAZY_LOAD_DELAY_TIME_MS);
            }
        } else {
            toggleNetworkError(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("appkey", Constants.ZSBAPPKEY);
                        jsonObject.put("version", Constants.ZSBVERSION);
                        jsonObject.put("sid", XmlDB.getInstance(mContext).getKeyString("sid", "sid"));
                        jsonObject.put("uid", XmlDB.getInstance(mContext).getKeyString("uid", "uid"));
                        jsonObject.put("page", page);
                        jsonObject.put("rows", ApiConstants.Integers.PAGE_LIMIT);
                        if (myOrderGroupMenuIncomplete.isChecked()) {
                            jsonObject.put("order_status", 1);
                        } else if (myOrderGroupMenuRecentCompleted.isChecked()) {
                            jsonObject.put("order_status", 2);
                        } else {
                            jsonObject.put("order_status", 1);
                        }
                    }catch (JSONException j){

                    }

                    orderPresenter.onOrder(Constants.EVENT_REFRESH_DATA, jsonObject);
                }
            });
        }
        mListViewAdapter = new ListViewDataAdapter<OrdersBean>(new ViewHolderCreator<OrdersBean>() {
            @Override
            public ViewHolderBase<OrdersBean> createViewHolder(int position) {
                return new ViewHolderBase<OrdersBean>() {
                    ImageView img_header;
                    TextView tv_name, tv_type, tv_sex, tv_appointed_time, tv_charging, tv_curriculum, tv_address, tv_place, tv_state;

                    @Override
                    public View createView(LayoutInflater layoutInflater) {
                        View view = layoutInflater.inflate(R.layout.order_list_item, null);
                        img_header = ButterKnife.findById(view, R.id.img_header);
                        tv_name = ButterKnife.findById(view, R.id.tv_name);
                        tv_type = ButterKnife.findById(view, R.id.tv_type);
                        tv_sex = ButterKnife.findById(view, R.id.tv_sex);
                        tv_appointed_time = ButterKnife.findById(view, R.id.tv_appointed_time);
                        tv_charging = ButterKnife.findById(view, R.id.tv_charging);
                        tv_curriculum = ButterKnife.findById(view, R.id.tv_curriculum);
                        tv_address = ButterKnife.findById(view, R.id.tv_address);
                        tv_place = ButterKnife.findById(view, R.id.tv_place);
                        tv_state = ButterKnife.findById(view, R.id.tv_state);
                        return view;
                    }

                    @Override
                    public void showData(int position, OrdersBean itemData) {
                        Picasso.with(mContext).load(ApiConstants.Urls.API_IMG_BASE_URLS + itemData.img_header).into(img_header);
                        tv_name.setText(itemData.name);
                        tv_type.setText(itemData.type);
                        tv_sex.setText(itemData.sex);
                        tv_appointed_time.setText(itemData.appointed_time);
                        tv_charging.setText(itemData.charging);
                        tv_curriculum.setText(itemData.curriculum);
                        tv_address.setText(itemData.address);
                        tv_place.setText(itemData.state);
                        if (itemData.state.equals("1")) {
                            tv_state.setText("未付款");
                        } else if (itemData.state.equals("2")) {
                            tv_state.setText("已付款");
                        }

                    }
                };
            }
        });

        fragmentMyOrderListListView.setAdapter(mListViewAdapter);
        fragmentMyOrderListListView.setOnLoadMoreListener(this);
        fragmentMyOrderListListView.setOnItemClickListener(this);

        fragmentMyOrderListSwipeLayout.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));
        fragmentMyOrderListSwipeLayout.setOnRefreshListener(this);

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
    public void navigateToOrderDetail(OrderEntity data) {
        Bundle bundle=new Bundle();
        bundle.putSerializable("data",data);
        readyGo(OrderDetailActivity.class,bundle);
    }

    @Override
    public void refreshListData(orderResponse response) {
        if (fragmentMyOrderListSwipeLayout != null)
            fragmentMyOrderListSwipeLayout.setRefreshing(false);
        if (response != null) {
            if (response.orders.size() >= 2) {
                if (mListViewAdapter != null) {
                    mListViewAdapter.getDataList().clear();
                    mListViewAdapter.getDataList().addAll(response.orders);
                    mListViewAdapter.notifyDataSetChanged();
                }
            }
            if (UriHelper.getInstance().calculateTotalPages(response.total_count) > page)
                fragmentMyOrderListListView.setCanLoadMore(true);
            else
                fragmentMyOrderListListView.setCanLoadMore(false);
        }
    }

    @Override
    public void addMoreListData(orderResponse response) {
        if (fragmentMyOrderListListView != null)
            fragmentMyOrderListListView.onLoadMoreComplete();
        if (response != null) {
            if (mListViewAdapter != null) {
                mListViewAdapter.getDataList().addAll(response.orders);
                mListViewAdapter.notifyDataSetChanged();
            }
            if (UriHelper.getInstance().calculateTotalPages(response.total_count) > page)
                fragmentMyOrderListListView.setCanLoadMore(true);
            else
                fragmentMyOrderListListView.setCanLoadMore(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderEntity orderEntity = (OrderEntity) parent.getItemAtPosition(position);
        navigateToOrderDetail(orderEntity);
    }

    @Override
    public void onLoadMore() {
        page = page + 1;
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("appkey", Constants.ZSBAPPKEY);
            jsonObject.put("version", Constants.ZSBVERSION);
            jsonObject.put("sid", XmlDB.getInstance(mContext).getKeyString("sid", "sid"));
            jsonObject.put("uid", XmlDB.getInstance(mContext).getKeyString("uid", "uid"));
            jsonObject.put("page", page);
            jsonObject.put("rows", ApiConstants.Integers.PAGE_LIMIT);
            if (myOrderGroupMenuIncomplete.isChecked()) {
                jsonObject.put("order_status", 1);
            } else if (myOrderGroupMenuRecentCompleted.isChecked()) {
                jsonObject.put("order_status", 2);
            } else {
                jsonObject.put("order_status", 1);
            }
        }catch (JSONException j){

        }

        orderPresenter.onOrder(Constants.EVENT_LOAD_MORE_DATA, jsonObject);
    }

    @Override
    public void onRefresh() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("appkey", Constants.ZSBAPPKEY);
            jsonObject.put("version", Constants.ZSBVERSION);
            jsonObject.put("sid", XmlDB.getInstance(mContext).getKeyString("sid", "sid"));
            jsonObject.put("uid", XmlDB.getInstance(mContext).getKeyString("uid", "uid"));
            jsonObject.put("page", 1);
            jsonObject.put("rows", ApiConstants.Integers.PAGE_LIMIT);
            if (myOrderGroupMenuIncomplete.isChecked()) {
                jsonObject.put("order_status", 1);
            } else if (myOrderGroupMenuRecentCompleted.isChecked()) {
                jsonObject.put("order_status",2);
            } else {
                jsonObject.put("order_status", 1);
            }
        }catch (JSONException j){

        }

        orderPresenter.onOrder(Constants.EVENT_REFRESH_DATA, jsonObject);
    }

    @OnClick({R.id.top_bar_back, R.id.my_order_group_menu_incomplete, R.id.my_order_group_menu_recent_completed})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_bar_back:
                finish();
                break;
            case R.id.my_order_group_menu_incomplete:
                JSONObject incomplete=new JSONObject();
                try {
                    incomplete.put("appkey", Constants.ZSBAPPKEY);
                    incomplete.put("version", Constants.ZSBVERSION);
                    incomplete.put("sid", XmlDB.getInstance(mContext).getKeyString("sid", "sid"));
                    incomplete.put("uid", XmlDB.getInstance(mContext).getKeyString("uid", "uid"));
                    incomplete.put("order_status", 1);
                    incomplete.put("page", 1);
                    incomplete.put("rows", ApiConstants.Integers.PAGE_LIMIT);
                }catch (JSONException j){

                }

                orderPresenter.onOrder(Constants.EVENT_REFRESH_DATA, incomplete);
                break;
            case R.id.my_order_group_menu_recent_completed:
                JSONObject recent_completed=new JSONObject();
                try {
                    recent_completed.put("appkey", Constants.ZSBAPPKEY);
                    recent_completed.put("version", Constants.ZSBVERSION);
                    recent_completed.put("sid", XmlDB.getInstance(mContext).getKeyString("sid", "sid"));
                    recent_completed.put("uid", XmlDB.getInstance(mContext).getKeyString("uid", "uid"));
                    recent_completed.put("order_status", 2);
                    recent_completed.put("page", 1);
                    recent_completed.put("rows", ApiConstants.Integers.PAGE_LIMIT);
                }catch (JSONException j){

                }

                orderPresenter.onOrder(Constants.EVENT_REFRESH_DATA, recent_completed);
                break;
        }
    }

}
