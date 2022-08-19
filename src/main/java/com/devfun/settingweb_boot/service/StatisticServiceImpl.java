package com.devfun.settingweb_boot.service;
 
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.devfun.settingweb_boot.dao.StatisticMapper;
 
@Service
public class StatisticServiceImpl implements StatisticService {
    
    
    @Autowired
    private StatisticMapper uMapper;
    
    private String serviceKey = "UGqtfqkb8SGP3PGrQ%2BZeIt78Q6uqVpJ1fhgWSV7SXaqkPxcVH6gdaER3AD242PsNR7ttRmbbRR4D6jQyYDYjcQ%3D%3D";
    public int getWeekdays(String yearMonth) {
    	try {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("solYear","UTF-8") + "=" + URLEncoder.encode("20"+yearMonth.substring(0,2), "UTF-8")); /*연*/
            urlBuilder.append("&" + URLEncoder.encode("solMonth","UTF-8") + "=" + URLEncoder.encode(yearMonth.substring(2), "UTF-8")); /*월*/
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            Document doc = convertStringToXMLDocument( sb.toString() );
             
            NodeList tags = doc.getElementsByTagName("item");
            Set<String> holidays = new HashSet<String>();
            
            // Get holidays and parse to String
            for(int index =0; index<tags.getLength(); index++) {
            	Node tag = tags.item(index);
            	String context = tag.getTextContent();
            	int length = context.length();
            	String date = context.substring(length-9, length-1);
            	holidays.add(date.substring(6,8));
            }
           String firstDate = "20"+yearMonth+"01";
           Calendar cal = Calendar.getInstance();
           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
           java.util.Date first_day = dateFormat.parse(firstDate);
           cal.setTime(dateFormat.parse(firstDate));
           int month = first_day.getMonth();
           int dayOfMonth = 1;
           for(int i =0; i<32; i++) {
        	   dayOfMonth++;
               if(first_day.toString().substring(0,3).equals("Sat") ||
            		   first_day.toString().substring(0,3).equals("Sun")
            		   )
            	   holidays.add(first_day.toString().substring(8,10));
            	   
               
               first_day.setDate(first_day.getDate()+1);
        	   if(first_day.getMonth() != month)
        		   break;
           }
           for(String holiday : holidays) {
        	   System.out.println(holiday);
           }
           return dayOfMonth - holidays.size();
    	}
    	catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
        

        
        return 10;
    }
    
    
    private static Document convertStringToXMLDocument(String xmlString) 
    {
      //Parser that produces DOM object trees from XML content
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       
      //API to obtain DOM Document instance
      DocumentBuilder builder = null;
      try
      {
        //Create DocumentBuilder with default configuration
        builder = factory.newDocumentBuilder();
         
        //Parse the content to Document object
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
        return doc;
      } 
      catch (Exception e) 
      {
        e.printStackTrace();
      }
      return null;
    }
    @Override
    public HashMap<String, Object> yearLoginNum (String year, String department) {
        HashMap<String, Object> retVal = new HashMap<String,Object>();
        if(department.equals("all")) department = "%";

		if(year.length() != 2) {
            retVal.put("is_success", false);
			retVal.put("errorMessage", "Invalid parameter length");
			return retVal;
		}
        try {
            retVal = uMapper.selectYear(year, "L", department);
            if(department.equals("%")) department = "all";
            retVal.put("department", department);
            retVal.put("period", "year");
            retVal.put("year", year);
            retVal.put("is_success", true);
            
        }catch(Exception e) {
        	System.out.println(e.getMessage());
            retVal.put("is_success", false);
            retVal.put("errorMessage", e.getMessage());
        }
        
        return retVal;
    }

	@Override
	public HashMap<String, Object> monthLoginNum(String yearMonth , String department) {
        // TODO Auto-generated method stub
        HashMap<String, Object> retVal = new HashMap<String,Object>();
		if(yearMonth.length() != 4) {
            retVal.put("is_success", false);
			retVal.put("errorMessage", "Invalid parameter length");
			return retVal;
		}

		try {
            if(department.equals("all")) department = "%";
            retVal = uMapper.selectMonth(yearMonth, "L", department);
            if(department.equals("%")) department = "all";
            retVal.put("department", department);
            retVal.put("period", "month");
            retVal.put("n_weekday", getWeekdays(yearMonth));
            retVal.put("yearMonthDate", yearMonth);
            retVal.put("is_success", true);
		}
		catch(Exception e) {
            retVal.put("is_success", false);
            retVal.put("errorMessage", e.getMessage());
			
		}
		return retVal;
	}

	@Override
	public HashMap<String, Object> dayLoginNum(String yearMonthDay, String department) {
        HashMap<String, Object> retVal = new HashMap<String,Object>();
		if(yearMonthDay.length() != 6) {
            retVal.put("is_success", false);
			retVal.put("errorMessage", "Invalid parameter length");
			return retVal;
		}
        try {
            if(department.equals("all")) department = "%";
            retVal = uMapper.selectDay(yearMonthDay,"P", department);
            retVal.put("department", department);
            if(department.equals("%")) department = "all";
            retVal.put("period", "day");
            retVal.put("year", yearMonthDay);
            retVal.put("is_success", true);
            
        }catch(Exception e) {
        	System.out.println(e.getMessage());
            retVal.put("is_success", false);
            retVal.put("errorMessage", e.getMessage());
        }
        
        return retVal;
	}

	@Override
	public HashMap<String, Object> visitNum(String period, String yearMonthDay, String department) {
		HashMap<String, Object> retVal = new HashMap<String,Object>();
        if ((period.equals("year") && yearMonthDay.length() != 2) || 
        		(period.equals("month") && yearMonthDay.length() != 4) || 
        		(period.equals("day") && yearMonthDay.length() != 6))
        {
            retVal.put("is_success", false);
			retVal.put("errorMessage", "Invalid parameter length");
			return retVal;
        }
        try {
            if(department.equals("all")) department = "%";
        	if(period.equals("year"))
        		retVal = uMapper.selectYear(yearMonthDay,"V", department);
        	if(period.equals("month")) {
        		retVal = uMapper.selectMonth(yearMonthDay,"V", department);
            	retVal.put("n_weekday", getWeekdays(yearMonthDay));
        	}
        	if(period.equals("day"))
        		retVal = uMapper.selectDay(yearMonthDay,"V", department);
            if(department.equals("%")) department = "all";
            retVal.put("department", department);
            retVal.put("period", period);
            retVal.put("year", yearMonthDay);
            retVal.put("is_success", true);
            
        }catch(Exception e) {
        	System.out.println(e.getMessage());
            retVal.put("is_success", false);
            retVal.put("errorMessage", e.getMessage());
        }
		
		return retVal;
	}

	@Override
	public HashMap<String, Object> postNum(String period, String yearMonthDay, String department) {
		HashMap<String, Object> retVal = new HashMap<String,Object>();
		if(period.equals("year")) {
			if(yearMonthDay.length() != 2) {
	            retVal.put("is_success", false);
				retVal.put("errorMessage", "Invalid parameter length");
				return retVal;
			}
	        try {

	            if(department.equals("all")) department = "%";
	            retVal = uMapper.selectYear(yearMonthDay,"P", department);
	            retVal.put("department", department);
	            if(department.equals("%")) department = "all";
	            retVal.put("period", period);
	            retVal.put("year", yearMonthDay);
	            retVal.put("is_success", true);
	            
	        }catch(Exception e) {
	        	System.out.println(e.getMessage());
	            retVal.put("is_success", false);
	            retVal.put("errorMessage", e.getMessage());
	        }

		}
		
		else if(period.equals("month")) {
			if(yearMonthDay.length() != 4) {
	            retVal.put("is_success", false);
				retVal.put("errorMessage", "Invalid parameter length");
				return retVal;
			}
	        try {
	            if(department.equals("all")) department = "%";
	            retVal = uMapper.selectMonth(yearMonthDay,"P", department);
	            retVal.put("department", department);
	            retVal.put("n_weekday", getWeekdays(yearMonthDay));
	            if(department.equals("%")) department = "all";
	            retVal.put("period", period);
	            retVal.put("year", yearMonthDay);
	            retVal.put("is_success", true);
	            
	        }catch(Exception e) {
	        	System.out.println(e.getMessage());
	            retVal.put("is_success", false);
	            retVal.put("errorMessage", e.getMessage());
	        }

		}
		else if (period.equals("day")) {
			if(yearMonthDay.length() != 6) {
	            retVal.put("is_success", false);
				retVal.put("errorMessage", "Invalid parameter length");
				return retVal;
			}
	        try {
	            if(department.equals("all")) department = "%";
	            retVal = uMapper.selectDay(yearMonthDay,"P", department);
	            retVal.put("department", department);
	            if(department.equals("%")) department = "all";
	            retVal.put("period", period);
	            retVal.put("year", yearMonthDay);
	            retVal.put("is_success", true);
	            
	        }catch(Exception e) {
	        	System.out.println(e.getMessage());
	            retVal.put("is_success", false);
	            retVal.put("errorMessage", e.getMessage());
	        }

		}
		return retVal;
	}
	
	
    
 
}