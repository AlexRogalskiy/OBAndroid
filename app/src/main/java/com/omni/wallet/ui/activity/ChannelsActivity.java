package com.omni.wallet.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.listItems.Channel;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.view.popupwindow.ChannelDetailsPopupWindow;
import com.omni.wallet.view.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.MenuPopupWindow;
import com.omni.wallet.view.popupwindow.SelectNodePopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lndmobile.Callback;
import lndmobile.Lndmobile;
import lnrpc.LightningOuterClass;

public class ChannelsActivity extends AppBaseActivity {
    private static final String TAG = ChannelsActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    LinearLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.iv_menu)
    ImageView mMenuIv;
    @BindView(R.id.recycler_channels_list)
    public RecyclerView mRecyclerView;// 通道列表的RecyclerView
    private List<Channel> channelData = new ArrayList<Channel>();
    private MyAdapter mAdapter;

    MenuPopupWindow mMenuPopupWindow;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;
    ChannelDetailsPopupWindow mChannelDetailsPopupWindow;
    SelectNodePopupWindow mSelectNodePopupWindow;

    public static final String KEY_PUBKEY = "pubkeyKey";
    private String pubkey;

    @Override
    protected void getBundleData(Bundle bundle) {
        pubkey = bundle.getString(KEY_PUBKEY);
    }

    @Override
    protected View getStatusBarTopView() {
        return mTopView;
    }

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_white);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_channels;
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(mContext, channelData, R.layout.layout_item_channels_list);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        getItemData();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Lndmobile.listChannels(LightningOuterClass.ListChannelsRequest.newBuilder().build().toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "------------------listChannelsOnError------------------" + e.getMessage());
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        try {
                            LightningOuterClass.ListChannelsResponse resp = LightningOuterClass.ListChannelsResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------listChannelsOnResponse-----------------" + resp.getChannelsList());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Lndmobile.pendingChannels(LightningOuterClass.PendingChannelsRequest.newBuilder().build().toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "------------------pendingChannelsOnError------------------" + e.getMessage());
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        try {
                            LightningOuterClass.PendingChannelsResponse resp = LightningOuterClass.PendingChannelsResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------pendingChannelsOnResponse1-----------------" + resp.getPendingOpenChannelsList());
                            LogUtils.e(TAG, "------------------pendingChannelsOnResponse2-----------------" + resp.getPendingClosingChannelsList());
                            LogUtils.e(TAG, "------------------pendingChannelsOnResponse3-----------------" + resp.getPendingForceClosingChannelsList());
                            LogUtils.e(TAG, "------------------pendingChannelsOnResponse4-----------------" + resp.getWaitingCloseChannelsList());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Lndmobile.closedChannels(LightningOuterClass.ClosedChannelsRequest.newBuilder().build().toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "------------------closedChannelsOnError------------------" + e.getMessage());
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        try {
                            LightningOuterClass.ClosedChannelsResponse resp = LightningOuterClass.ClosedChannelsResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------closedChannelsOnResponse-----------------" + resp.getChannelsList());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 1000);
    }

    private void getItemData() {
        Channel a = new Channel(1, "McDonald", "USDT", "03felksw.............xwqwcs985", 100.00, 600.00);
        Channel b = new Channel(2, "Bob", "BTC", "03felksw.............xwqwcs985", 0.5, 1.566);
        Channel c = new Channel(0, "Carol", "USDT", "03felksw.............xwqwcs985", 500.00, 300.00);
        channelData.add(a);
        channelData.add(b);
        channelData.add(c);
        channelData.add(a);
        channelData.add(b);
        channelData.add(c);
    }

    /**
     * The adapter of assets list
     * 资产列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<Channel> {

        public MyAdapter(Context context, List<Channel> data, int layoutId) {
            super(context, data, layoutId);
        }

        public void convert(ViewHolder holder, final int position, final Channel item) {
            double percent = item.getLocalAmount() / item.getTotalAmount() * 100;
            ProgressBar progressBar = holder.getView(R.id.pv_amount_percent);
            progressBar.setProgress((int) Math.round(percent));

            holder.setText(R.id.tv_node_name, item.getChannelName());
            holder.setText(R.id.tv_token_type, item.getTokenType());
            holder.setText(R.id.tv_pubkey_value, item.getPubkey());
            holder.setText(R.id.tv_local_amount, "" + item.getLocalAmount());
            holder.setText(R.id.tv_remote_amount, "" + item.getRemoteAmount());

            switch (item.getState()) {
                default:
                    holder.setImageResource(R.id.iv_channel_state, R.drawable.bg_state_green);
                    break;
                case 0:
                    holder.setImageResource(R.id.iv_channel_state, R.drawable.bg_state_grey);
                    break;
                case 1:
                    holder.setImageResource(R.id.iv_channel_state, R.drawable.bg_state_green);
                    break;
                case 2:
                    holder.setImageResource(R.id.iv_channel_state, R.drawable.bg_state_red);
                    break;
            }

            switch (item.getTokenType()) {
                default:
                    holder.setImageResource(R.id.im_token_type, R.mipmap.icon_usdt_logo);
                    break;
                case "USDT":
                    holder.setImageResource(R.id.im_token_type, R.mipmap.icon_usdt_logo);
                    break;
                case "BTC":
                    holder.setImageResource(R.id.im_token_type, R.mipmap.icon_btc_logo_small);
                    break;
            }

            if (position == channelData.size() - 1) {
                holder.getView(R.id.v_deliver).setVisibility(View.GONE);
            }
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChannelDetailsPopupWindow = new ChannelDetailsPopupWindow(mContext);
                    mChannelDetailsPopupWindow.show(mParentLayout);
                }
            });
        }
    }

    /**
     * click scan button at top-right in page
     * 点击右上角扫码按钮
     */
    @OnClick(R.id.iv_scan)
    public void clickScan() {
        PermissionUtils.launchCamera(this, new PermissionUtils.PermissionCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                switchActivity(ScanActivity.class);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                LogUtils.e(TAG, "扫码页面摄像头权限拒绝");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                LogUtils.e(TAG, "扫码页面摄像头权限拒绝并且勾选不再提示");
            }
        });
    }

    /**
     * click the menu button at top-right in page
     * 点击右上角菜单按钮
     */
    @OnClick(R.id.iv_menu)
    public void clickMemu() {
        mMenuPopupWindow = new MenuPopupWindow(mContext);
        mMenuPopupWindow.show(mMenuIv);
    }

    /**
     * click the close button at top-right in page
     * 点击右上角关闭按钮
     */
    @OnClick(R.id.iv_close)
    public void clickClose() {
        finish();
    }

    @OnClick(R.id.iv_create_channel)
    public void clickcCreateChannel() {
        mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
        mCreateChannelStepOnePopupWindow.show(mParentLayout, pubkey);
    }

    /**
     * 点击copy图标按钮
     * click copy icon btn
     *
     * @description 中文：点击按钮复制当前address到粘贴板
     * EN：Click copy button,duplicate address to clipboard
     * @author Tong ChangHui
     * @Email tch081092@gmail.com
     */
    @OnClick(R.id.iv_copy)
    public void copyAddress() {
        //接收需要复制到粘贴板的地址
        //Get the address which will copy to clipboard
        String toCopyAddress = "01234e*****bg453123";
        //接收需要复制成功的提示语
        //Get the notice when you copy success
        String toastString = getResources().getString(R.string.toast_copy_address);
        CopyUtil.SelfCopy(ChannelsActivity.this, toCopyAddress, toastString);
    }

    @OnClick(R.id.lv_network_title_content)
    public void clickSelectNode() {
        mSelectNodePopupWindow = new SelectNodePopupWindow(mContext);
        mSelectNodePopupWindow.show(mParentLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMenuPopupWindow != null) {
            mMenuPopupWindow.release();
        }
        if (mCreateChannelStepOnePopupWindow != null) {
            mCreateChannelStepOnePopupWindow.release();
        }
        if (mChannelDetailsPopupWindow != null) {
            mChannelDetailsPopupWindow.release();
        }
    }
}
