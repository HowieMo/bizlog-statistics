package com.mhy.bizlog;

import java.util.ArrayList;
import java.util.List;

public class SortMain {
    public static void main(String[] args) {
        String ipstr = "30135、30148、30136、30141、30140、30181、30191、30120、30139、30134、30185、30130、30130、30116、30131、30146、30193、30184、30187、30194、30288、30190、30260、30283、30119、30223、30192、30273、30274、30271、30272、30299、30297、30104、30103、30277、30275、30276、30278、30129、30137、30145、30143、30199、30204、30108、30168、30197、30289、30158、30207、30132、30229、30138、30228、30227、30209、30202、30203、30205、30300、30236、30222、30234、30225、30230、30233、30231、30210、30232、30266、30284、30287、30117、30185、30208、30198";
        String[] ips = ipstr.split("、");
        List<Integer> ipIntList = new ArrayList<Integer>();
        for (String ip : ips) {
            ipIntList.add(Integer.parseInt(ip));
        }
        for(int i=0;i<ipIntList.size();i++){
            for(int j=i+1;j<ipIntList.size();j++){
                if(ipIntList.get(j)<ipIntList.get(i)){
                    int mid = ipIntList.get(i);
                    ipIntList.set(i,ipIntList.get(j));
                    ipIntList.set(j,mid);
                }
            }
        }
        for(int i=0;i<ipIntList.size();i++){
            if(i>0&&(ipIntList.get(i-1).equals(ipIntList.get(i)))){
                continue;
            }else {
                System.out.print(ipIntList.get(i)+"、");
            }
        }
    }
}
