package com.omni.wallet.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.ui.activity.ChannelsActivity;
import com.omni.wallet.ui.activity.ScanActivity;

import java.util.ArrayList;
import java.util.List;

import lndmobile.Callback;
import lndmobile.Lndmobile;
import lndmobile.RecvStream;
import lnrpc.LightningOuterClass;

/**
 * CreateChannelStepOne的弹窗
 */
public class CreateChannelStepOnePopupWindow {
    private static final String TAG = CreateChannelStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    EditText localEdit;
    EditText waterDripEdit;
    EditText remoteEdit;
    EditText vaildPubkeyEdit;
    EditText channelAmountEdit;
    TextView channelAmountTv;
    TextView channelFeeTv;
    Button speedButton;
    Button amountUnitButton;
    SelectSpeedPopupWindow mSelectSpeedPopupWindow;
    SelectAssetUnitPopupWindow mSelectAssetUnitPopupWindow;
    String nodePubkey;
    long assetId;
    int time;
    List<LightningOuterClass.Asset> list = new ArrayList<>();

    public CreateChannelStepOnePopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view, String pubKey) {
        if (mBasePopWindow == null) {
            nodePubkey = pubKey;
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_channel_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            getListAsset();
            localEdit = rootView.findViewById(R.id.edit_local);
            waterDripEdit = rootView.findViewById(R.id.edit_water_drip);
            remoteEdit = rootView.findViewById(R.id.edit_remote);
            speedButton = rootView.findViewById(R.id.btn_speed);
            vaildPubkeyEdit = rootView.findViewById(R.id.edit_vaild_pubkey);
            channelAmountEdit = rootView.findViewById(R.id.edit_channel_amount);
            channelAmountTv = rootView.findViewById(R.id.tv_channel_amount);
            channelFeeTv = rootView.findViewById(R.id.tv_channel_fee);
            amountUnitButton = rootView.findViewById(R.id.btn_amount_unit);

            localEdit.setText(pubKey);
            waterDripEdit.setText(pubKey);
            remoteEdit.setText(pubKey);
            vaildPubkeyEdit.setText(pubKey);
            channelAmountEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    estimateOnChainFee(count, time);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
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
                                    time = 1; // 10 Minutes
                                    break;
                                case R.id.tv_medium:
                                    speedButton.setText(R.string.medium);
                                    time = 6 * 6; // 6 Hours
                                    break;
                                case R.id.tv_fast:
                                    speedButton.setText(R.string.fast);
                                    time = 6 * 24; // 24 Hours
                                    break;
                            }
                        }
                    });
                    mSelectSpeedPopupWindow.show(v);
                }
            });
            amountUnitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectAssetUnitPopupWindow = new SelectAssetUnitPopupWindow(mContext);
                    mSelectAssetUnitPopupWindow.setOnItemClickCallback(new SelectAssetUnitPopupWindow.ItemCleckListener() {
                        @Override
                        public void onItemClick(View view, LightningOuterClass.Asset item) {
                            amountUnitButton.setText(item.getName());
                            assetId = item.getPropertyid();
                        }
                    });
                    mSelectAssetUnitPopupWindow.show(v, list);
                }
            });
            /**
             * @描述： 扫描二维码
             * @desc: scan qrcode
             */
            rootView.findViewById(R.id.layout_scan_qrcode).setOnClickListener(new View.OnClickListener() {
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
            /**
             * @描述： 手动填写
             * @desc: fill in
             */
            rootView.findViewById(R.id.layout_fill_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.GONE);
                    rootView.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.VISIBLE);
                }
            });
            /**
             * @描述： 点击back
             * @desc: click back button
             */
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.GONE);
                }
            });
            /**
             * @描述： 点击create
             * @desc: click create button
             */
            rootView.findViewById(R.id.layout_create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    Intent intent = new Intent(mContext, ChannelsActivity.class);
                    mContext.startActivity(intent);
                    /**
                     * Opening transaction channel
                     * 开通交易通道
                     */
                    byte[] nodeKeyBytes = hexStringToByteArray(nodePubkey);
                    LightningOuterClass.OpenChannelRequest openChannelRequest = LightningOuterClass.OpenChannelRequest.newBuilder()
                            .setNodePubkey(ByteString.copyFrom(nodeKeyBytes))
                            .setTargetConf(Integer.parseInt(channelFeeTv.getText().toString()))
                            .setPrivate(false)
                            .setLocalFundingBtcAmount(Long.parseLong(channelAmountEdit.getText().toString()))
                            .setLocalFundingAssetAmount(Long.parseLong(channelAmountEdit.getText().toString()))
                            .setAssetId((int) assetId)
                            .build();
                    Lndmobile.openChannel(openChannelRequest.toByteArray(), new RecvStream() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------openChannelOnError------------------" + e.getMessage());
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.BatchOpenChannelResponse resp = LightningOuterClass.BatchOpenChannelResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------openChannelOnResponse-----------------" + resp);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            /**
             * @描述： 点击 cancel
             * @desc: click cancel button
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
            mBasePopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    private void getListAsset() {
        Lndmobile.listAsset(LightningOuterClass.ListAssetRequest.newBuilder().build().toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------listAssetonError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    LightningOuterClass.ListAssetResponse resp = LightningOuterClass.ListAssetResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------listAssetonResponse-----------------" + resp);
                    list.clear();
                    list.addAll(resp.getListList());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }


    private void estimateOnChainFee(long amount, int targetConf) {
        String address;
        switch ("regtest") {
            case "testnet":
                address = "tb1qw508d6qejxtdg4y5r3zarvary0c5xw7kxpjzsx";
                break;
            case "regtest":
                address = "bcrt1qsdtedxkv2mdgtstsv9fhyq03dsv9dyu5qmeh2w";
                break;
            default:
                address = "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4"; // Mainnet
        }
        // let LND estimate fee
        LightningOuterClass.EstimateFeeRequest asyncEstimateFeeRequest = LightningOuterClass.EstimateFeeRequest.newBuilder()
                .putAddrToAmount(address, amount)
                .setTargetConf(targetConf)
                .build();

        Lndmobile.estimateFee(asyncEstimateFeeRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onResponse(byte[] bytes) {

            }
        });
    }


    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
