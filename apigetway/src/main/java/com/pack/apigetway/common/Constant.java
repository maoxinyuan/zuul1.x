package com.pacific.apigetway.common;

/**
 * @author maoxy
 * @date 20181210
 **/
public class Constant {

    public static final String CURRENT_SERVER_KEY = "currentServer";
    public static final String CURRENT_WEIGHT_KEY = "currentWeight";
    public static final String COLON = ":";

    public final static String REQUEST_BATCH_NO = "batchNo";

    public static final int REDIS_CONN_FAIL_CODE = 10001; // redis conn fail code
    public static final String REDIS_CONN_FAIL_MSG = "redis服务正在切换，请稍后重试!"; // redis conn fail message

}
