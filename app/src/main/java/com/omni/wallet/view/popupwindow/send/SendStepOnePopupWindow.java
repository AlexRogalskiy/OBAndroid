package com.omni.wallet.view.popupwindow.send;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.listItems.Friend;
import com.omni.wallet.listItems.FriendGroup;
import com.omni.wallet.ui.activity.ScanActivity;
import com.omni.wallet.utils.GetResourceUtil;
import com.omni.wallet.view.dialog.SendSuccessDialog;
import com.omni.wallet.view.popupwindow.SelectAssetPopupWindow;
import com.omni.wallet.view.popupwindow.SelectSpeedPopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * SendStepOne的弹窗
 */
public class SendStepOnePopupWindow {
    private static final String TAG = SendStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;
    View mView;
    private List<FriendGroup> friendGroups = new ArrayList<>();

    // 初始数据（Initial data）
    String sendFriendName = "Alpha";
    String sendAddress = "1mn8382odjddwedqw323f3d32343f23fweg65er4345yge43t4534gy7";
    String type = "USDT";
    Double sendAmount = 100.00d;
    Double sendValue = 710.23d;
    Double gasFeeAmount = 100.00d;
    Double gasFeeValue = 5.06d;
    Double totalValue = 715.29d;

    Double assetBalance = 500.00d;
    String toAddress = "1mn8382odjddwedqw323f3d32343f23fweg65er4345yge43t4534gy7";
    String toFriendName = "to_friend_name";
    Button speedButton;
    LinearLayout assetLayout;
    SelectSpeedPopupWindow mSelectSpeedPopupWindow;
    SelectAssetPopupWindow mSelectAssetPopupWindow;
    SendSuccessDialog mSendSuccessDialog;

    public SendStepOnePopupWindow(Context context) {
        this.mContext = context;
    }

    /**
     * @描述： 测试使用数据
     * @description: data for test
     */
    public void friendGroupsData() {
        Friend alice = new Friend("Alice", "1mn8382odjd.........34gy7");
        Friend abbe = new Friend("Abbe", "2nm8382odjd.........dfe689");
        List<Friend> groupA = new ArrayList<Friend>();
        groupA.add(alice);
        groupA.add(abbe);
        FriendGroup friendGroupA = new FriendGroup("A", groupA);

        Friend bob = new Friend("Bob", "1mn8382odjd.........34gy7");
        Friend bill = new Friend("Bill", "2nm8382odjd.........dfe689");
        Friend boss = new Friend("Boss", "2nm8382odjd.........dfe689");
        List<Friend> groupB = new ArrayList<Friend>();
        groupB.add(bob);
        groupB.add(bill);
        groupB.add(boss);
        groupB.add(bob);
        groupB.add(bill);
        groupB.add(boss);
        FriendGroup friendGroupB = new FriendGroup("B", groupB);

        Friend charli = new Friend("Charli", "1mn8382odjd.........34gy7");
        List<Friend> groupC = new ArrayList<Friend>();
        groupC.add(charli);
        FriendGroup friendGroupC = new FriendGroup("C", groupC);

        friendGroups.add(friendGroupA);
        friendGroups.add(friendGroupB);
        friendGroups.add(friendGroupC);
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mView = view;
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_send_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            friendGroupsData();
            /**
             * @description: RecyclerView for send list
             * @描述： send list 的 RecyclerView
             */
            RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_send_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mAdapter = new MyAdapter(mContext, friendGroups, R.layout.layout_item_send_list);
            mRecyclerView.setAdapter(mAdapter);

            /**
             * @description: click scan icon
             * @描述： 点击scan
             */
            rootView.findViewById(R.id.iv_scan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionUtils.launchCamera((Activity) mContext, new PermissionUtils.PermissionCallback() {
                        @Override
                        public void onRequestPermissionSuccess() {
                            mBasePopWindow.dismiss();
                            Intent intent = new Intent(mContext, ScanActivity.class);
                            mContext.startActivity(intent);
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
            });

            TextView toAddressView = rootView.findViewById(R.id.tv_to_address);
            toAddressView.setText(toAddress);
            TextView assetsBalanceView = rootView.findViewById(R.id.tv_asset_balance);
            assetsBalanceView.setText(assetBalance.toString());
            TextView toFriendNameView = rootView.findViewById(R.id.tv_to_friend_name);
            toFriendNameView.setText(toFriendName);
            assetLayout = rootView.findViewById(R.id.layout_asset);
            assetLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectAssetPopupWindow = new SelectAssetPopupWindow(mContext);
                    mSelectAssetPopupWindow.setOnItemClickCallback(new SelectAssetPopupWindow.ItemCleckListener() {
                        @Override
                        public void onItemClick(View view, String item) {
                            ToastUtils.showToast(mContext, "Asset");
                        }
                    });
                    mSelectAssetPopupWindow.show(v);
                }
            });
            speedButton = rootView.findViewById(R.id.btn_speed);
            speedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectSpeedPopupWindow = new SelectSpeedPopupWindow(mContext);
                    mSelectSpeedPopupWindow.setOnItemClickCallback(new SelectSpeedPopupWindow.ItemCleckListener() {
                        @Override
                        public void onItemClick(View view) {
                            switch (view.getId()) {
                                case R.id.tv_slow:
                                    speedButton.setText(R.string.slow);
                                    break;
                                case R.id.tv_medium:
                                    speedButton.setText(R.string.medium);
                                    break;
                                case R.id.tv_fast:
                                    speedButton.setText(R.string.fast);
                                    break;
                            }
                        }
                    });
                    mSelectSpeedPopupWindow.show(v);
                }
            });

            /**
             * @描述: 增加MAX按钮的点击事件，点击将balance的值填入amount输入框中
             * @Description: Add the click event of MAX button, click to fill the balance value into the amount input box
             * @author: Tong ChangHui
             * @E-mail: tch081092@gmail.com
             */

            final EditText amountInputView = rootView.findViewById(R.id.etv_send_amount);
            TextView maxBtnView = rootView.findViewById(R.id.tv_btn_set_amount_max);
            maxBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    amountInputView.setText(assetBalance.toString());
                }
            });

            /**
             * @desc: Click back button then back to step one
             * @描述： 点击back显示step one
             */
            rootView.findViewById(R.id.layout_back_to_one).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_step_one_content).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_step_two_content).setVisibility(View.GONE);
                }
            });
            /**
             * @desc: Click next button then forward to step three
             * @描述： 点击next跳转到step three
             */
            rootView.findViewById(R.id.layout_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_step_two_content).setVisibility(View.GONE);
                    rootView.findViewById(R.id.lv_step_three_content).setVisibility(View.VISIBLE);
                }
            });

            /**
             * @描述: 初始化页面初始数据包括：friendName、address、send amount、send value、gas fee、fee value、total used value
             * @Description: The initial data of the initialization page includes friend name, address, send value, gas fee, fee value, total used value
             * @author: Tong ChangHui
             * @E-mail: tch081092@gmail.com
             */

            TextView friendNameView = rootView.findViewById(R.id.tv_send_friend_name);
            friendNameView.setText(sendFriendName);
            TextView friendAddressView = rootView.findViewById(R.id.tv_send_address);
            friendAddressView.setText(sendAddress);
            ImageView tokenImage = rootView.findViewById(R.id.iv_send_token_image);
            tokenImage.setImageDrawable(mContext.getResources().getDrawable(GetResourceUtil.getTokenImageId(mContext, type)));
            TextView tokenTypeView = rootView.findViewById(R.id.tv_send_token_type);
            tokenTypeView.setText(type);
            TextView tokenTypeView2 = rootView.findViewById(R.id.tv_send_token_type_2);
            tokenTypeView2.setText(type);
            TextView sendAmountView = rootView.findViewById(R.id.tv_send_amount);
            sendAmountView.setText(sendAmount.toString());
            TextView sendAmountValueView = rootView.findViewById(R.id.tv_send_amount_value);
            sendAmountValueView.setText(sendValue.toString());
            TextView feeAmountView = rootView.findViewById(R.id.tv_send_gas_fee_amount);
            feeAmountView.setText(gasFeeAmount.toString());
            TextView feeAmountValueView = rootView.findViewById(R.id.tv_send_gas_fee_amount_value);
            feeAmountValueView.setText(gasFeeValue.toString());
            TextView sendUsedValueView = rootView.findViewById(R.id.tv_send_used_value);
            sendUsedValueView.setText(totalValue.toString());

            /**
             * @desc: Click back button then back to step two
             * @描述： 点击back显示step two
             */
            rootView.findViewById(R.id.layout_back_to_two).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_step_three_content).setVisibility(View.GONE);
                }
            });
            /**
             * @desc: Click confirm button then forward to succeed or failed step
             * @描述： 点击 confirm button 显示成功或者失败的页面
             */
            rootView.findViewById(R.id.layout_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_step_failed_content).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_step_three_content).setVisibility(View.GONE);
                    mBasePopWindow.dismiss();
                    mSendSuccessDialog = new SendSuccessDialog(mContext);
                    mSendSuccessDialog.show();
                }
            });


            /**
             * @备注： 点击cancel 按钮
             * @description: Click cancel button
             */
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAtLocation(mView, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * @描述： send list列表适配器
     * @desc: Adapter for send list
     */
    private class MyAdapter extends CommonRecyclerAdapter<FriendGroup> {

        public MyAdapter(Context context, List<FriendGroup> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final FriendGroup item) {
            holder.setText(R.id.tv_group_name, item.getGroupName());
            holder.getView(R.id.v_deliver).setVisibility(View.INVISIBLE);

            LinearLayout ListContentView = holder.getView(R.id.lv_friend_item_list);

            List<Friend> friendListInGroup = item.getGroupFriend();

            for (int i = 0; i < friendListInGroup.size(); i++) {
                String friendName = friendListInGroup.get(i).getFriendName();
                String address = friendListInGroup.get(i).getAddress();

                RelativeLayout friendItemContain = new RelativeLayout(mContext);
                RelativeLayout.LayoutParams friendItemContainParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                friendItemContain.setLayoutParams(friendItemContainParams);

                LinearLayout friendItemContent = new LinearLayout(mContext);
                LinearLayout.LayoutParams friendItemContentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                friendItemContent.setPadding(0, 20, 0, 20);
                friendItemContent.setOrientation(LinearLayout.HORIZONTAL);
                friendItemContent.setLayoutParams(friendItemContentParams);

                TextView friendNameView = new TextView(mContext);
                LinearLayout.LayoutParams friendNameViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                friendNameView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                friendNameView.setGravity(Gravity.CENTER);
                friendNameView.setTextSize(16);
                friendNameView.setTextColor(mContext.getResources().getColor(R.color.color_99_transparent));
                friendNameView.setText(friendName);
                friendNameView.setLayoutParams(friendNameViewParams);

                TextView friendAddressView = new TextView(mContext);
                LinearLayout.LayoutParams friendAddressViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                friendAddressView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                friendAddressView.setGravity(Gravity.CENTER);
                friendAddressView.setTextSize(16);
                friendAddressView.setTextColor(mContext.getResources().getColor(R.color.color_99_transparent));
                friendAddressView.setText(address);
                friendAddressView.setLayoutParams(friendAddressViewParams);

                friendItemContent.addView(friendNameView);
                friendItemContent.addView(friendAddressView);
                friendItemContain.addView(friendItemContent);
                friendItemContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBasePopWindow.getContentView().findViewById(R.id.lv_step_one_content).setVisibility(View.GONE);
                        mBasePopWindow.getContentView().findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
                    }
                });
                ListContentView.addView(friendItemContain);
            }
        }
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
