package com.mhy.bizlog.core;

import com.mhy.bizlog.util.PropertyUtils;

public class BizlogStatisticsContext {
    public static String getStatisticsDateStart(){
        return PropertyUtils.getProperty("bizlog","statisticsDateStart");
    }
    public static String getStatisticsDateEnd(){
        return PropertyUtils.getProperty("bizlog","statisticsDateEnd");
    }
    public static String getWorkDir(){
        return PropertyUtils.getProperty("bizlog","workDir");
    }
}
