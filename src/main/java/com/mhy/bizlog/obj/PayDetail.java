package com.mhy.bizlog.obj;

public class PayDetail {
    public final static String NOT_SEND_BIZLOG = "日志未发或格式错误";
    public final static String LATE_SEND_BIZLOG = "日志迟发";

    private String reason;
    private int payMoney;
    private String detail;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(int payMoney) {
        this.payMoney = payMoney;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
