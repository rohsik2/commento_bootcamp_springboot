package com.devfun.settingweb_boot.dao;
import java.util.HashMap;
 
import com.devfun.settingweb_boot.dto.StatisticDto;
 
public interface  StatisticMapper {
    public HashMap<String, Object> selectYear(String year, String type, String department);
    public HashMap<String, Object> selectMonth(String yearMonth, String type, String department);
    public HashMap<String, Object> selectDay(String yearMonthDay, String type, String department);
}