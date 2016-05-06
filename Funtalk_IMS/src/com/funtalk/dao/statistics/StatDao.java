package com.funtalk.dao.statistics;

import java.util.List;
import java.util.Map;

import com.funtalk.common.DBConnection;

public interface StatDao {
	
	public List getDevelopUsers(Map params);
	//List getDevelopUsers(int start, int limit, Map params, String dir, String sort,String querytype);
	public  List getDayDevUsers(Map params);
	public  List getDayDevUsersNew(Map params);
    
    public  List getDayPayFees(Map params);
    
    
    public  List getDayUserBills(Map params);
    
    public  List getMonProUserBills(Map params);
    
    public  List getMonProUserStayMons(Map params);
    
    public  List getMonthEnd(Map params);
    


}
