package com.funtalk.dao.statistics;

import java.util.List;
import java.util.Map;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import com.funtalk.common.DBConnection;
import com.funtalk.common.SpringContextUtil;


public class StatAssistDaoImpl  extends HibernateDaoSupport implements StatAssistDao{
	
	private DBConnection dbConnection = (DBConnection)SpringContextUtil.getBean("DBConnection");
	
	public List upLoadSettleExcel(Map params){
		
		List users = null;
		
		
		String stat_time=(String)params.get("stat_time");
		try {
			
		String hql= "";                                                                                            
		
		  System.out.println("----hql------------>"+hql.toString());
		
			users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	

}
