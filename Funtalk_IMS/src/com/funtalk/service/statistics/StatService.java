package com.funtalk.service.statistics;

import java.util.Map;

import com.funtalk.common.DBConnection;

public interface StatService {
	
	
     String getDevelopUsers(Map params);
    // String getDevelopUsers(int start, int limit, Map params, String dir, String sort,String querytype);

     public String getDayDevUsers(Map params);
     public String getDayDevUsersNew(Map params);
     
     public String getDayPayFees(Map params);
     
     public String getDayUserBills(Map params);
     
     public String getMonProUserBills(Map params);
     
     public String getMonProUserStayMons(Map params);
     
     public String getMonthEnd(Map params);
     
     
     
}
