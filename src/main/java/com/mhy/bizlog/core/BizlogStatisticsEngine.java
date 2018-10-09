package com.mhy.bizlog.core;

import com.mhy.bizlog.obj.BizlogInfo;
import com.mhy.bizlog.obj.Covenant;
import com.mhy.bizlog.obj.Department;
import com.mhy.bizlog.obj.PayDetail;
import com.mhy.bizlog.util.PropertyUtils;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BizlogStatisticsEngine {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    private Date startDate = null;

    private Date endDate = null;
    /**
     * 工作目录
     */
    private File workDir;

    private Map<String,List<String>> leaveDayMap = new HashMap<String, List<String>>();

    /**
     * 公约人员
     */
    private Map<String,Covenant> covenantMaps = new HashMap<String, Covenant>();


    public void run() {
        init();
        readExcelFiles();
        statisticMoney();
        writeToExcel();
    }

    /**
     * 初始化
     */
    private void init() {
        //开始日期
        String startDate = BizlogStatisticsContext.getStatisticsDateStart();
        try {
            this.startDate = simpleDateFormat.parse(startDate);
            System.out.println("开始时间"+this.startDate);
        } catch (ParseException e) {
            throw new RuntimeException("起始时间转换出错",e);
        }
        //结束日期
        String endDate = BizlogStatisticsContext.getStatisticsDateEnd();
        try {
            this.endDate = simpleDateFormat.parse(endDate);
            System.out.println("结束时间"+this.endDate);
        } catch (ParseException e) {
            throw new RuntimeException("结束时间转换出错",e);
        }
        //工作目录
        String workDir = BizlogStatisticsContext.getWorkDir();
        File file = new File(workDir);
        if(file.exists()){
            this.workDir = file;
            System.out.println("工作目录："+this.workDir);
        }else {
            throw new RuntimeException("工作目录不存在:"+workDir);
        }
        //公约人员初始化
        System.out.println("开始读取人员列表");
        File covenantFile = new File(Thread.currentThread().getContextClassLoader().getResource("covenant.xls").getFile());
        Workbook workbook = null;
        try {
            workbook = Workbook.getWorkbook(covenantFile);
        } catch (IOException e) {
            throw new RuntimeException("读取员工列表失败",e);
        } catch (BiffException e) {
            throw new RuntimeException("读取员工列表失败",e);
        }finally {
            if(workbook==null){
                throw new RuntimeException("找不到员工列表文件");
            }
        }
        //加载公约全部人员部门及名称信息
        Sheet[] sheets = workbook.getSheets();
        if(sheets!=null){
            Sheet covenantSheet = sheets[0];
            Cell[] rowOneCells = covenantSheet.getRow(0);
            for (Cell rowOneCell : rowOneCells) {
                System.out.print(rowOneCell.getContents()+"      ");
            }
            System.out.println();
            int rows = covenantSheet.getRows();
            for(int row=1;row<rows;row++){
                //获取部门名称
                String departparmentSimpleName = covenantSheet.getCell(0,row).getContents();
                if(StringUtils.isBlank(departparmentSimpleName)){
                    continue;
                }
                System.out.print(departparmentSimpleName+"   ");
                Department department = new Department();
                department.setSimpleName(departparmentSimpleName);
                //生成公约人员
                String names = covenantSheet.getCell(1,row).getContents();
                String[] covenantNames = names.split("、");
                System.out.print(names);
                System.out.println();
                for (String covenantName : covenantNames) {
                    Covenant covenant = new Covenant(covenantName,department);
                    covenantMaps.put(covenantName,covenant);
                }
            }
        }else {
            throw new RuntimeException("员工列表没有sheet");
        }
        //设置各员工的工作时间
        for (Map.Entry<String, Covenant> stringCovenantEntry : covenantMaps.entrySet()) {
            Covenant covenant = stringCovenantEntry.getValue();
            covenant.setWorkDates(Arrays.asList(PropertyUtils.getProperty("bizlog","workdates").split(",")));
        }
    }

    /**
     * 读取excel日志文件
     */
    private void readExcelFiles() {
        //获取所有日期文件夹
        File[] dayDirs = this.workDir.listFiles();
        for (File dir : dayDirs) {
            if(dir.isFile()){
                continue;
            }
            String dirName = dir.getName();
            Date sendDate = null;
            try {
                sendDate = simpleDateFormat.parse(dirName);
            } catch (ParseException e) {
                continue;
            }
            if(sendDate.getTime()>this.endDate.getTime()||sendDate.getTime()<this.startDate.getTime()){
                continue;
            }
            //获取日志文件
            File[] bizLogFiles = dir.listFiles();
            for (File bizLogFile : bizLogFiles) {
                if(bizLogFile.isDirectory()){
                    continue;
                }
                String fileName = bizLogFile.getName();
                String[] splits = fileName.split("_");
                //判断日志文件名称是否标准
                if(splits.length!=3){
                    continue;
                }
                String covenantName = splits[1];
                Covenant covenant = covenantMaps.get(covenantName);
                //人员名称错误
                if(covenant==null){
                    continue;
                }
                //机构名称错误
                if(!StringUtils.equals(splits[0],covenant.getDepartment().getSimpleName())){
                    continue;
                }
                Date logDate;
                String date = splits[2].substring(0,splits[2].indexOf("."));
                try {
                    logDate = simpleDateFormat.parse(date);
                }catch (Exception e){
                    continue;
                }
                if(logDate.getTime()>this.endDate.getTime()||logDate.getTime()<this.startDate.getTime()){
                    continue;
                }
                BizlogInfo bizlogInfo;
                try {
                    bizlogInfo = new BizlogInfo(sendDate,logDate,Workbook.getWorkbook(bizLogFile));
                } catch (Exception e) {
                    continue;
                }
                Map<String, BizlogInfo> bizlogInfoMap = covenant.getBizlogInfoMap();
                bizlogInfoMap.put(date,bizlogInfo);
            }
        }
        System.out.println("统计总结果：");
        System.out.println("部门          姓名           日志情况");
        for (Map.Entry<String, Covenant> stringCovenantEntry : this.covenantMaps.entrySet()) {
            Covenant covenant = stringCovenantEntry.getValue();
            System.out.println(covenant.toString());
        }
    }

    /**
     * 计算钱
     */
    private void statisticMoney() {
        int notSendPay = Integer.parseInt(PropertyUtils.getProperty("bizlog","notSendPay"));
        int lateSendPay = Integer.parseInt(PropertyUtils.getProperty("bizlog","lateSendPay"));
        Date today = new Date();
        for (Map.Entry<String, Covenant> stringCovenantEntry : this.covenantMaps.entrySet()) {
            Covenant covenant = stringCovenantEntry.getValue();
            List<String> workDates = covenant.getWorkDates();
            for (String workDate : workDates) {
                BizlogInfo bizlogInfo = covenant.getBizlogInfoMap().get(workDate);
                //未发日志的乐捐
                if(bizlogInfo==null){
                    int payDate;
                    try {
                        payDate = countDate(simpleDateFormat.parse(workDate),today)-1;
                    } catch (ParseException e) {
                        continue;
                    }
                    if(payDate<=0){
                        continue;
                    }
                    PayDetail payDetail = new PayDetail();
                    payDetail.setReason(PayDetail.NOT_SEND_BIZLOG);
                    payDetail.setPayMoney(notSendPay);
                    payDetail.setDetail("未发日报日期："+workDate+"，需乐捐"+notSendPay+"元");
                    covenant.getPayDetails().add(payDetail);
                    continue;
                }
                int latePayDate = countDate(bizlogInfo.getLogDate(),bizlogInfo.getSendDate())-1;
                if(latePayDate<=0){
                    continue;
                }
                PayDetail payDetail = new PayDetail();
                payDetail.setReason(PayDetail.LATE_SEND_BIZLOG);
                int latePayMoney = lateSendPay*latePayDate;
                payDetail.setPayMoney(latePayMoney);
                payDetail.setDetail("迟发日报日期："+simpleDateFormat.format(bizlogInfo.getLogDate())+"，实际发送日期："+simpleDateFormat.format(bizlogInfo.getSendDate())+"，需乐捐"+lateSendPay+"*"+latePayDate+"共计"+latePayMoney+"元");
                covenant.getPayDetails().add(payDetail);
            }
        }
    }

    /**
     * 写入EXCEL
     */
    private void writeToExcel() {
        File bizlogResultExcelFile = new File(PropertyUtils.getProperty("bizlog","bizlogResultExcel"));
        // 创建一个工作簿
        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(bizlogResultExcelFile);
        } catch (IOException e) {
            throw new RuntimeException("创建日志总结文件失败",e);
        }
        // 创建一个工作表
        WritableSheet sheet = workbook.createSheet("sheet1", 0);
        try {
            sheet.addCell(new Label(0,0,"姓名"));
            sheet.addCell(new Label(1,0,"乐捐详情"));
            sheet.addCell(new Label(2,0,"总计"));
            int row = 0;
            for (Map.Entry<String, Covenant> stringCovenantEntry : covenantMaps.entrySet()) {
                row++;
                Covenant covenant = stringCovenantEntry.getValue();
                sheet.addCell(new Label(0,row,covenant.getName()));
                System.out.print(covenant.getName()+"   ");
                List<PayDetail> payDetails = covenant.getPayDetails();
                if(payDetails.size()==0){
                    sheet.addCell(new Label(1,row,"无需乐捐"));
                    sheet.addCell(new Label(2,row,"0"));
                    System.out.print("无需乐捐    0");
                    System.out.println();
                }else {
                    int total = 0;
                    StringBuilder payDetailLabelStr = new StringBuilder();
                    for (PayDetail payDetail : payDetails) {
                        payDetailLabelStr.append("原因：").append(payDetail.getReason()).append("。 ");
                        total += payDetail.getPayMoney();
                        payDetailLabelStr.append(payDetail.getDetail()).append("      &&     ");
                    }
                    sheet.addCell(new Label(1,row,payDetailLabelStr.toString()));
                    sheet.addCell(new Label(2,row,total+""));
                    System.out.print(payDetailLabelStr.toString()+"    ");
                    System.out.print(total);
                    System.out.println();
                }
            }
        } catch (WriteException e) {
            throw new RuntimeException("添加单元格失败",e);
        }
        try {
            workbook.write();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 计算相差天数
     * @param oldDate 早一点的时间
     * @param newDate 新一点的时间
     * @return
     */
    private int countDate(Date oldDate,Date newDate){

        Date date1 = oldDate;
        Date date2 = newDate;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);

        if (year1 != year2) {  //bu同年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
                    timeDistance += 366;
                } else {  //平年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else { //同一年
            return day2 - day1;
        }

    }



}
