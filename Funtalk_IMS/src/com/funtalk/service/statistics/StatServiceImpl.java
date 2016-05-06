package com.funtalk.service.statistics;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.funtalk.common.DBConnection;
import com.funtalk.dao.statistics.StatDao;
import com.funtalk.common.SpringContextUtil;

public class StatServiceImpl implements StatService{
	

    private static final Log log = LogFactory.getLog(StatServiceImpl.class);

    StatDao statDao;
    

	
	public String getDevelopUsers(Map params) {
		

    	List developUsers = statDao.getDevelopUsers(params);
    	
		StringBuffer buffer = new StringBuffer();
	
			buffer.append("[");
			
			for(int i=0;i<developUsers.size();i++){
				
				String[] reps=(String[])developUsers.get(i);
				buffer.append("{\"prov_name\":\""+reps[0]+"\"");
				buffer.append(",\"city_name\":\""  +reps[1]+"\"");
				buffer.append(",\"month_jihuo\":\"" +reps[2]+"\"");
				buffer.append(",\"month_stop\":\"" +reps[3]+"\"},");
				
			 }
			
			buffer.deleteCharAt(buffer.lastIndexOf(","));		
			buffer.append("]");
		    System.out.println("-------"+buffer.toString());
		    
		    return buffer.toString();
    }
	
	
public String getDayDevUsers(Map params) {
		
	    String stat_id=(String)params.get("stat_id");

    	List developUsers = statDao.getDayDevUsers(params);
    	
		StringBuffer buffer = new StringBuffer();
		
		int nums=developUsers.size();
		
		buffer.append("{\"totalCount\":\""+nums+"\",\"rows\":[");
	
		
			
			for(int i=0;i<developUsers.size();i++){
				
				String[] reps=(String[])developUsers.get(i);
				
				if(stat_id.equals("1")){
					
				   buffer.append("{\"stat_date\":\""+reps[0]+"\"");
				   buffer.append(",\"day_jihuo\":\"" +reps[1]+"\"");
				   buffer.append(",\"day_stop\":\"" +reps[2]+"\"},");
				
				}else{
				
				   buffer.append("{\"city_name\":\""+reps[0]+"\"");
				   
				   if(stat_id.equals("2")){
				      buffer.append(",\"day_nums\":\"" +reps[1]+"\"},");
				   }else{
					   buffer.append(",\"day_nums\":\"" +reps[2]+"\"},");
				   }
				}
				
			 }
			
	
			
			buffer.deleteCharAt(buffer.lastIndexOf(","));		
			buffer.append("]}");
		    System.out.println("-------"+buffer.toString());
		    
		    return buffer.toString();
    }


public String getDayDevUsersNew(Map params) {
	
    String stat_id=(String)params.get("stat_id");

	List developUsers = statDao.getDayDevUsersNew(params);
	
	StringBuffer buffer = new StringBuffer();
	
	int nums=developUsers.size();
	
	buffer.append("{\"totalCount\":\""+nums+"\",\"rows\":[");

	
		
		for(int i=0;i<developUsers.size();i++){
			
			String[] reps=(String[])developUsers.get(i);
				
			
			if(stat_id.equals("3") || stat_id.equals("4")){

				   buffer.append("{\"stat_id\":\"" +reps[0]+"\"");
				   buffer.append(",\"stat_name\":\"" +reps[2]+"\"");
				   buffer.append(",\"stat_value\":\"" +reps[1]+"\"},");
		 }else{
			 
			   buffer.append("{\"stat_date\":\""+reps[0]+"\"");
			   buffer.append(",\"stat_type\":\"" +reps[1]+"\"");
			   buffer.append(",\"stat_id\":\"" +reps[2]+"\"");
			   buffer.append(",\"stat_name\":\"" +reps[4]+"\"");
			   buffer.append(",\"stat_value\":\"" +reps[3]+"\"},");
		 
		 }
		}
		
		buffer.deleteCharAt(buffer.lastIndexOf(","));		
		buffer.append("]}");
	    System.out.println("-------"+buffer.toString());
	    
	    return buffer.toString();
}


public String getDayPayFees(Map params) {
	
    String stat_id=(String)params.get("stat_id");

	List developUsers = statDao.getDayPayFees(params);
	
	StringBuffer buffer = new StringBuffer();
	
	int nums=developUsers.size();
	
	buffer.append("{\"totalCount\":\""+nums+"\",\"rows\":[");

	
		
		for(int i=0;i<developUsers.size();i++){
			
			String[] reps=(String[])developUsers.get(i);
			
			if(stat_id.equals("1")){
				
				
			   buffer.append("{\"stat_date\":\""+reps[0]+"\"");
			   buffer.append(",\"agentadd\":\"" +reps[1]+"\"");
			   buffer.append(",\"agentsub\":\"" +reps[2]+"\"");
			   buffer.append(",\"userfee\":\""+reps[3]+"\"");		   
			   buffer.append(",\"miaoyhk\":\""+reps[4]+"\"");
			   buffer.append(",\"miaoczk\":\""+reps[5]+"\"");
			   buffer.append(",\"weixin\":\""+reps[6]+"\"");
			   buffer.append(",\"kefuczk\":\""+reps[7]+"\"");
			   buffer.append(",\"nanjinggt\":\""+reps[8]+"\"");
			   buffer.append(",\"gaoyang19e\":\""+reps[9]+"\"");
			   buffer.append(",\"otherfee\":\""+reps[10]+"\"");
			   buffer.append(",\"zengfee\":\""+reps[11]+"\"");
			   buffer.append(",\"returnAllFee\":\""+reps[12]+"\"");
			   buffer.append(",\"returnZsFee\":\""+reps[13]+"\"");		   
			   buffer.append(",\"returnXJFee\":\"" +reps[14]+"\"},");
			
			}else{
			
			   buffer.append("{\"stat_date\":\""+reps[0]+"\"");
			   buffer.append(",\"stat_name\":\"" +reps[1]+"\"");
			   	   
			   if(stat_id.equals("userfee")){
			      buffer.append(",\"day_nums\":\"" +reps[2]+"\"},");
			   }else if(stat_id.equals("miaoyhk")){
				   buffer.append(",\"day_nums\":\"" +reps[3]+"\"},");
			   }else if(stat_id.equals("miaoczk")){
				   buffer.append(",\"day_nums\":\"" +reps[4]+"\"},");
			   }else if(stat_id.equals("weixin")){
				   buffer.append(",\"day_nums\":\"" +reps[5]+"\"},");
			   }else if(stat_id.equals("kefuczk")){
				   buffer.append(",\"day_nums\":\"" +reps[6]+"\"},");
			   }else if(stat_id.equals("nanjinggt")){
				   buffer.append(",\"day_nums\":\"" +reps[7]+"\"},");
			   }else if(stat_id.equals("gaoyang19e")){
				   buffer.append(",\"day_nums\":\"" +reps[8]+"\"},");
			   }else if(stat_id.equals("otherfee")){
				   buffer.append(",\"day_nums\":\"" +reps[9]+"\"},");
			   }else if(stat_id.equals("zengfee")){
				   buffer.append(",\"day_nums\":\"" +reps[10]+"\"},");
			   }else if(stat_id.equals("returnAllFee")){
				   buffer.append(",\"day_nums\":\"" +reps[11]+"\"},");
			   }else if(stat_id.equals("returnZsFee")){
				   buffer.append(",\"day_nums\":\"" +reps[12]+"\"},");
			   }else if(stat_id.equals("returnXJFee")){
				   buffer.append(",\"day_nums\":\"" +reps[13]+"\"},");
			   }else{
				   buffer.append(",\"day_nums\":\"0\"},"); 
			   }
			}
			
		 }
		

		
		buffer.deleteCharAt(buffer.lastIndexOf(","));		
		buffer.append("]}");
	    System.out.println("-------"+buffer.toString());
	    
	    return buffer.toString();
}





public String getDayUserBills(Map params) {
	
    String stat_id=(String)params.get("stat_id");

	List userBills = statDao.getDayUserBills(params);
	
	StringBuffer buffer = new StringBuffer();
	
	int nums=userBills.size();
	
	if(stat_id.equals("1"))  nums=nums-1;   //  过滤掉第一条数据，第一条是基础数据，第二条与此做差；
	
	buffer.append("{\"totalCount\":\""+nums+"\",\"rows\":[");

	
		
		for(int i=0;i<userBills.size();i++){
			
			String[] reps=(String[])userBills.get(i);
			
			if(stat_id.equals("1")){
				
			   if(i==0) continue;
			   
			   buffer.append("{\"stat_date\":\""+reps[0]+"\"");
			   buffer.append(",\"day_bill\":\"" +reps[1]+"\"");
			   buffer.append(",\"day_settle\":\"" +reps[2]+"\"");
			   buffer.append(",\"day_yuyin\":\"" +reps[3]+"\"");
			   buffer.append(",\"day_liuliang\":\"" +reps[4]+"\"");
			   buffer.append(",\"day_duanxin\":\"" +reps[5]+"\"");
			   buffer.append(",\"day_yzyuyin\":\"" +reps[6]+"\"");
			   buffer.append(",\"day_yzliuliang\":\"" +reps[7]+"\"");
			   buffer.append(",\"day_yzall\":\"" +reps[8]+"\"");
			   buffer.append(",\"day_laixian\":\"" +reps[9]+"\"");
			   buffer.append(",\"day_dixiao\":\"" +reps[10]+"\"");
			   buffer.append(",\"day_profit\":\"" +reps[11]+"\"},");
			
			}else{
			
			   buffer.append("{\"city_name\":\""+reps[0]+"\"");	   
			   buffer.append(",\"day_settleBills\":\""+reps[1]+"\"");	 
			   buffer.append(",\"day_profits\":\"" +reps[2]+"\"},");

			   }
			
		 }
		
		
		buffer.deleteCharAt(buffer.lastIndexOf(","));		
		buffer.append("]}");
	    System.out.println("-------"+buffer.toString());
	    
	    return buffer.toString();
}




public String getMonProUserBills(Map params) {
	
    String stat_id=(String)params.get("stat_id");

	List userBills = statDao.getMonProUserBills(params);
	
	StringBuffer buffer = new StringBuffer();
	
	int nums=userBills.size();
		
	buffer.append("{\"totalCount\":\""+nums+"\",\"rows\":[");
	
		for(int i=0;i<userBills.size();i++){
			
			String[] reps=(String[])userBills.get(i);
			
			if(stat_id.equals("1")){
							   
				   buffer.append("{\"cycle_id\":\""+reps[0]+"\"");
				   buffer.append(",\"stat_type\":\"" +reps[1]+"\"");
				   buffer.append(",\"stat_id\":\"" +reps[2]+"\"");
				   buffer.append(",\"stat_name\":\"" +reps[3]+"\"");
				   buffer.append(",\"mon_billone\":\"" +reps[4]+"\"");
				   buffer.append(",\"mon_billtwo\":\"" +reps[5]+"\"");
				   buffer.append(",\"mon_yuyin\":\"" +reps[6]+"\"");
				   buffer.append(",\"mon_liuliang\":\"" +reps[7]+"\"");
				   buffer.append(",\"mon_duanxin\":\"" +reps[8]+"\"");
				   buffer.append(",\"mon_yzyuyin\":\"" +reps[9]+"\"");
				   buffer.append(",\"mon_yzliuliang\":\"" +reps[10]+"\"");
				   buffer.append(",\"mon_laixian\":\"" +reps[11]+"\"");
				   buffer.append(",\"mon_dixiao\":\"" +reps[12]+"\"");
				   buffer.append(",\"mon_xzxianjin\":\"" +reps[13]+"\"");
				   buffer.append(",\"mon_xzzengfee\":\"" +reps[14]+"\"");
				   buffer.append(",\"mon_owe\":\"" +reps[15]+"\"");
				   buffer.append(",\"mon_users\":\"" +reps[16]+"\"");
				   buffer.append(",\"mon_settle\":\"" +reps[17]+"\"");
				   buffer.append(",\"mon_jihuo\":\"" +reps[18]+"\"");
				   buffer.append(",\"mon_tingji\":\"" +reps[19]+"\"");
				   buffer.append(",\"mon_arpu\":\"" +reps[20]+"\"");
				   buffer.append(",\"mon_profit1\":\"" +reps[21]+"\"");
				   buffer.append(",\"mon_profit2\":\"" +reps[22]+"\"");
				   buffer.append(",\"mon_rate1\":\"" +reps[23]+"\"");
				   buffer.append(",\"mon_rate2\":\"" +reps[24]+"\"},");
					  
			
			}else{
			
			   buffer.append("{\"city_name\":\""+reps[0]+"\"");	   
			   buffer.append(",\"nowday_bills\":\""+reps[1]+"\"");	 
			   buffer.append(",\"yesday_bills\":\"" +reps[2]+"\"},");

			   }
			
		 }
		
		
		buffer.deleteCharAt(buffer.lastIndexOf(","));		
		buffer.append("]}");
	    System.out.println("-------"+buffer.toString());
	    
	    return buffer.toString();
}



public String getMonProUserStayMons(Map params) {
	
    String stat_id=(String)params.get("stat_id");

	List userBills = statDao.getMonProUserStayMons(params);
	
	StringBuffer buffer = new StringBuffer();
	
	int nums=userBills.size();
		
	buffer.append("{\"totalCount\":\""+nums+"\",\"rows\":[");
	
		for(int i=0;i<userBills.size();i++){
			
			String[] reps=(String[])userBills.get(i);
			
			if(stat_id.equals("1")){
							   
			   buffer.append("{\"product_id\":\""+reps[0]+"\"");
			   buffer.append(",\"product_name\":\"" +reps[1]+"\"");
			   buffer.append(",\"diff_months\":\"" +reps[2]+"\"");
			   buffer.append(",\"diff_nums\":\"" +reps[3]+"\"},");
			
			}else{
			
			   buffer.append("{\"city_name\":\""+reps[0]+"\"");	   	 
			   buffer.append(",\"stat_nums\":\"" +reps[1]+"\"},");

			   }
			
		 }
		
		
		buffer.deleteCharAt(buffer.lastIndexOf(","));		
		buffer.append("]}");
	    System.out.println("-------"+buffer.toString());
	    
	    return buffer.toString();
}



public String getMonthEnd(Map params) {
	
    String stat_id=(String)params.get("stat_id");

	List userBills = statDao.getMonthEnd(params);
	
	StringBuffer buffer = new StringBuffer();
	
	int nums=userBills.size();
		
	buffer.append("{\"totalCount\":\""+nums+"\",\"rows\":[");
	
		for(int i=0;i<userBills.size();i++){
			
			String[] reps=(String[])userBills.get(i);
			
			if(stat_id.equals("1")){
							   
			   buffer.append("{\"cycle_id\":\""+reps[0]+"\"");
			   buffer.append(",\"stat_type\":\"" +reps[1]+"\"");
			   buffer.append(",\"stat_id\":\"" +reps[2]+"\"");
			   buffer.append(",\"stat_name\":\"" +reps[3]+"\"");
			   buffer.append(",\"mon_billone\":\"" +reps[4]+"\"");
			   buffer.append(",\"mon_billtwo\":\"" +reps[5]+"\"");
			   buffer.append(",\"mon_yuyin\":\"" +reps[6]+"\"");
			   buffer.append(",\"mon_liuliang\":\"" +reps[7]+"\"");
			   buffer.append(",\"mon_duanxin\":\"" +reps[8]+"\"");
			   buffer.append(",\"mon_yzyuyin\":\"" +reps[9]+"\"");
			   buffer.append(",\"mon_yzliuliang\":\"" +reps[10]+"\"");
			   buffer.append(",\"mon_laixian\":\"" +reps[11]+"\"");
			   buffer.append(",\"mon_dixiao\":\"" +reps[12]+"\"");
			   buffer.append(",\"mon_xzxianjin\":\"" +reps[13]+"\"");
			   buffer.append(",\"mon_xzzengfee\":\"" +reps[14]+"\"");
			   buffer.append(",\"mon_owe\":\"" +reps[15]+"\"");
			   buffer.append(",\"mon_users\":\"" +reps[16]+"\"");
			   buffer.append(",\"mon_settle\":\"" +reps[17]+"\"");
			   buffer.append(",\"mon_jihuo\":\"" +reps[18]+"\"");
			   buffer.append(",\"mon_tingji\":\"" +reps[19]+"\"");
			   buffer.append(",\"mon_arpu\":\"" +reps[20]+"\"");
			   buffer.append(",\"mon_profit1\":\"" +reps[21]+"\"");
			   buffer.append(",\"mon_profit2\":\"" +reps[22]+"\"");
			   buffer.append(",\"mon_rate1\":\"" +reps[23]+"\"");
			   buffer.append(",\"mon_rate2\":\"" +reps[24]+"\"},");
			   
			  
			
			}else{
			
			   buffer.append("{\"city_name\":\""+reps[0]+"\"");	   
			   buffer.append(",\"nowday_bills\":\""+reps[1]+"\"");	 
			   buffer.append(",\"yesday_bills\":\"" +reps[2]+"\"},");

			   }
			
		 }
		
		
		buffer.deleteCharAt(buffer.lastIndexOf(","));		
		buffer.append("]}");
	    System.out.println("-------"+buffer.toString());
	    
	    return buffer.toString();
}


	
	public void setStatDao(StatDao statDao){
		
		this.statDao=statDao;
	}

}
