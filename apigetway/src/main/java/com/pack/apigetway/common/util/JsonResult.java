package com.pacific.apigetway.common.util;

import com.alibaba.fastjson.JSON;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * @author Lokiy
 */
public class JsonResult {

    private boolean success = true;
    private String businessCode = "1";
    private String errorCode = "1";
    private String msg = "";
    private Object data;

    public static JsonResult getResult(Object data) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(data);
        return jsonResult;
    }

    public static JsonResult error(String errorCode, MessageSource messageSource, Object... params) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setSuccess(false);
        jsonResult.setBusinessCode("0");
        jsonResult.setErrorCode(errorCode);
        jsonResult.setMsg(messageSource.getMessage(errorCode, params, errorCode, Locale.SIMPLIFIED_CHINESE));
        return jsonResult;
    }

    public static JsonResult error(String errorCode, String errorMsg) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setSuccess(false);
        jsonResult.setBusinessCode("0");
        jsonResult.setErrorCode(errorCode);
        jsonResult.setMsg(errorMsg);
        return jsonResult;
    }

    public static JsonResult errorData(String errorCode, MessageSource messageSource, Object data, Object... params) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setSuccess(false);
        jsonResult.setBusinessCode("0");
        jsonResult.setErrorCode(errorCode);
        jsonResult.setMsg(messageSource.getMessage(errorCode, params, errorCode, Locale.SIMPLIFIED_CHINESE));
        jsonResult.setData(data);
        return jsonResult;
    }

    public static JsonResult error(String errorCode, String errorMsg,Object data) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setSuccess(false);
        jsonResult.setBusinessCode("0");
        jsonResult.setData(data);
        jsonResult.setErrorCode(errorCode);
        jsonResult.setMsg(errorMsg);
        return jsonResult;
    }

    public JsonResult() {
        super();
    }

    public JsonResult(boolean success, String businessCode, String errorCode, String msg, Object data) {
        this.success = success;
        this.businessCode = businessCode;
        this.errorCode = errorCode;
        this.msg = msg;
        this.data = data;
    }

    public JsonResult(boolean success) {
        super();
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public  String toJson() {
        String json = JSON.toJSONString(this);
        if (StringUtils.isEmpty(json)) {
            json = "{\"success\":false,\"errCode\":\"serialize.error\",\"message\":\"serialize.error\",\"data\":\"\"}";
        }
        return json;
    }

    public void setError(String errorCode, MessageSource messageSource, Object... params) {
        this.setSuccess(false);
        this.setBusinessCode("0");
        this.setErrorCode(errorCode);
        this.setMsg(messageSource.getMessage(errorCode, params, errorCode, Locale.SIMPLIFIED_CHINESE));
    }

}