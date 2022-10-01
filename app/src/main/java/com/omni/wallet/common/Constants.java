package com.omni.wallet.common;


/**
 * 常量类
 * Created by fa on 2018/7/21.
 */

public class Constants {

    public static final int SMS_CODE_LENGTH = 6;// 验证码长度
    private static final int PASSWORD_MIN_LENGTH = 6;// 密码最小长度
    private static final int PASSWORD_MAX_LENGTH = 16;// 密码最大长度

    public static final int TYPE_AUTH_TRUE = 1;// 认证通过
    public static final int TYPE_AUTH_FALSE = 2;// 认证不通过

    // 提现方式
    public static final String CASH_EXCHANGE_TYPE_BANK_CARD = "3";// 银行卡
    public static final String CASH_EXCHANGE_TYPE_ALI_PAY = "1";// 支付宝
    public static final String CASH_EXCHANGE_TYPE_WE_CHAT = "2";// 微信

    /**
     * 统一的密码错误描述
     */
    public static String getPswErrorTips() {
        return "密码格式应为" + Constants.PASSWORD_MIN_LENGTH + "-" + Constants.PASSWORD_MAX_LENGTH + "位字母或数字";
    }


    public static final String CODE_OWNER_NOT_EXIT = "580";// 业主不存在
}
