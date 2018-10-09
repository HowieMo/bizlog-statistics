package com.mhy.bizlog.obj;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Covenant {
    /**
     * 姓名
     */
    private String name;

    private Department department;
    /**
     * 日志信息
     */
    private Map<String,BizlogInfo> bizlogInfoMap = new HashMap<String, BizlogInfo>();

    private List<String> workDates;

    public Covenant(String name,Department department){
        this.name = name;
        this.department = department;
    }

    private List<PayDetail> payDetails = new ArrayList<PayDetail>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, BizlogInfo> getBizlogInfoMap() {
        return bizlogInfoMap;
    }

    public void setBizlogInfoMap(Map<String, BizlogInfo> bizlogInfoMap) {
        this.bizlogInfoMap = bizlogInfoMap;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<String> getWorkDates() {
        return workDates;
    }

    public void setWorkDates(List<String> workDates) {
        this.workDates = workDates;
    }

    public List<PayDetail> getPayDetails() {
        return payDetails;
    }

    public void setPayDetails(List<PayDetail> payDetails) {
        this.payDetails = payDetails;
    }

    public String toString(){
        StringBuilder bizlogMapStr = new StringBuilder("日志情况：");
        for (Map.Entry<String, BizlogInfo> stringBizlogInfoEntry : this.bizlogInfoMap.entrySet()) {
            bizlogMapStr.append(stringBizlogInfoEntry.getValue().toString());
        }
        return this.department.getSimpleName()+"   "+this.name+"   "+bizlogMapStr.toString();
    }
}
