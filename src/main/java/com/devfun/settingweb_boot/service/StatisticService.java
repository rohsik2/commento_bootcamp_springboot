package com.devfun.settingweb_boot.service;
 
import java.io.IOException;
import java.util.HashMap;
 
public interface StatisticService {
	public int getHolidays(String yearMonth) throws IOException;
    public HashMap<String,Object> yearLoginNum (String year, String department);
    public HashMap<String,Object> monthLoginNum (String yearMonth, String department);
    public HashMap<String,Object> dayLoginNum (String yearMonthDay, String department);
    
    public HashMap<String,Object> visitNum(String period, String yearMonthDay, String department);
    public HashMap<String,Object> postNum(String period, String yearMonthDay, String department);
}