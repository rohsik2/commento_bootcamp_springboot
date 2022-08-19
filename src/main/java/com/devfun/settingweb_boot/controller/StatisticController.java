package com.devfun.settingweb_boot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.devfun.settingweb_boot.service.StatisticService;

@Controller
@RequestMapping("/api/statistics")
public class StatisticController{
	
	@Autowired
	private StatisticService service;
	
	
	@RequestMapping("/post/{department}/{period}/{yearMonthDate}")
	@ResponseBody 
    public Map<String, Object> postStatistic(
    		@PathVariable(value = "department")final String department,
    		@PathVariable(value = "period")final String period,
    		@PathVariable(value = "yearMonthDate")final String yearMonthDate) throws Exception{ 
		return service.postNum(period, yearMonthDate, department);
    }
	
	@RequestMapping("/login/{department}/{period}/{yearMonthDate}")
	@ResponseBody 
    public Map<String, Object> loginStatistics(
    		@PathVariable(value = "department")final String department,
    		@PathVariable(value = "period")final String period,
    		@PathVariable(value = "yearMonthDate")final String yearMonthDate) throws Exception{ 
		
		if(period.equals("year")) {
	        return service.yearLoginNum(yearMonthDate, department);
		}
		else if(period.equals("month")) {
			return service.monthLoginNum(yearMonthDate, department);
		}
		else if(period.equals("date")) {
			return service.dayLoginNum(yearMonthDate, department);
		}
		else {
			Map<String, Object> retVal = new HashMap<String, Object>();
			retVal.put("is_success", false);
			retVal.put("errorMessage", "Wrong period type");
			return retVal;
		}
    }
	
	@RequestMapping("/visitors/{department}/{period}/{yearMonthDate}")
	@ResponseBody 
    public Map<String, Object> visitStatistic(
    		@PathVariable(value = "department")final String department,
    		@PathVariable(value = "period")final String period,
    		@PathVariable(value = "yearMonthDate")final String yearMonthDate) throws Exception{ 
	  
		return service.visitNum(period, yearMonthDate, department);
    }
	
	
}