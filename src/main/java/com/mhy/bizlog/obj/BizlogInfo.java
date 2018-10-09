package com.mhy.bizlog.obj;


import jxl.Workbook;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class BizlogInfo {
    /**
     * 发送日期
     */
    private Date sendDate;
    /**
     * 日志日期
     */
    private Date logDate;
    /**
     * 日志文件
     */
    private Workbook workbook;

    /**
     * 乐捐数量
     */
    private int donationCount;

    public BizlogInfo(Date sendDate, Date logDate, Workbook workbook) {
        this.sendDate = sendDate;
        this.logDate = logDate;
        this.workbook = workbook;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public String toString(){
        return "发送日期："+sendDate+"  日志日期："+logDate+"   ";
    }
}
