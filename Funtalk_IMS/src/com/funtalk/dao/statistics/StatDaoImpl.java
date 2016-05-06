package com.funtalk.dao.statistics;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.funtalk.common.DBConnection;
import com.funtalk.common.SpringContextUtil;


public class StatDaoImpl  extends HibernateDaoSupport implements StatDao{
	
	private DBConnection dbConnection = (DBConnection)SpringContextUtil.getBean("DBConnection");
	
	public List getDevelopUsers(Map params){
		
		List users = null;
		
		
		String stat_time=(String)params.get("stat_time");
		try {
			
		String hql= "select (select city_name from  bs_city_id_t                                                                                                                                                                                                     "+
					"where region_code=(select subcompany_code from bs_city_id_t where city_code=b.city_code and city_level=3) and city_level=2) prov_name,                                                                                                          "+
					"(select city_name from  bs_city_id_t where city_code=b.city_code and city_level=3) city_name,                                                                                                                                                   "+
					"nvl(count(distinct case when b.state not in('2HM') and to_char(b.create_date,'yyyy-mm')='"+stat_time+"' then b.serv_id else null end),0) month_jihuo,                                                                           "+
					"nvl(count( distinct (case when b.state  in('2HD') and to_char(b.state_date,'yyyy-mm')='"+stat_time+"'  then b.serv_id else null end)),0) month_stop                                                                            "+
					"from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'  group by b.city_code";                                                                                                                                                      
					
					/*
					"union all                                                                                                                                                                                                                                       "+
					"select '合计' prov_name,'' city_name,                                                                                                                                                                                                                               "+
					"sum(nvl(count(distinct case when b.state not in('2HM') and to_char(b.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end),0)) yesday_jihuo,                                                                      "+
					"sum(nvl(count( distinct (case when b.state  in('2HD') and to_char(b.state_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd')  then b.acc_nbr else null end)),0)) yesday_stop,                                                                       "+
					"sum(nvl(count(distinct case when (b.state not in('2HM') and to_char(b.create_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(b.create_date,'yyyymmdd') < to_char(sysdate, 'yyyymmdd') ) then b.acc_nbr else null end),0)) month_jihuo,  "+
					"sum(nvl(count( distinct (case when b.state  in('2HD') and to_char(b.state_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(b.state_date,'yyyymmdd') <to_char(sysdate,'yyyymmdd') then b.acc_nbr else null end)),0)) month_stop              "+
					"from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'  group by b.city_code";
		*/
		
		  System.out.println("----hql------------>"+hql.toString());
		
			users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			//	users = getSession().createSQLQuery(hql).setFirstResult(start).setMaxResults(limit).list();  // 此查询的结果为Object[] 格式，需要进行如下转换：objectArray2StringArrayRepNull
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	public List getDayDevUsers(Map params){
		
		List users = null;
		String hql="";
		
		String stat_time=(String)params.get("stat_time");
		String stat_type=(String)params.get("stat_type");
		String stat_id=(String)params.get("stat_id");
		
		StringBuffer statDateStr=new StringBuffer();
		Date dt1;
		
		try {
			
		if(stat_id.equals("1")){
			
	      if(stat_type.equals("huanbi")){
	    	  
			// state : 2HM  预登录    2HD  停机
		   hql= "select a.stat_date stat_date,nvl(a.stat_ct,0) stat_jh,                                                                                "+
				"nvl(b.stat_ct,0) stat_tj  from                                                          "+
				"(select to_char(create_date,'yyyymmdd') stat_date,count(distinct serv_id) stat_ct                                             "+
				"from  serv  where  state not in('2HM')                                                                                        "+
				"and create_date>=to_date('"+stat_time+"','yyyymmdd')-19 and create_date<=to_date('"+stat_time+"','yyyymmdd')+1                "+
				"group by to_char(create_date,'yyyymmdd')) a,                                                                                  "+
				"(select to_char(state_date,'yyyymmdd') stat_date,count(distinct serv_id) stat_ct                                              "+
				"from  serv  where  state  in('2HD')                                                                                           "+
				"and state_date>=to_date('"+stat_time+"','yyyymmdd')-19 and state_date<=to_date('"+stat_time+"','yyyymmdd')+1                  "+
				"group by to_char(state_date,'yyyymmdd')) b  where a.stat_date=b.stat_date   order by a.stat_date  ";

		   
	          }else if(stat_type.equals("tongbi")){
	        	  
	        	  
	        	    statDateStr.append("'"+stat_time+"',");
				
			        SimpleDateFormat sdf =   new SimpleDateFormat("yyyyMMdd");
			        Date statDate= sdf.parse(stat_time);
			
			        Calendar calDate = Calendar.getInstance();  
			        calDate.setTime(statDate);  
			
			        for(int i=1;i<=12;i++){
			
				     calDate.add(Calendar.MONTH, -1);  
	                 dt1 = calDate.getTime();	             
	                 statDateStr.append("'"+sdf.format(dt1)+"',");
			        }
			  
			  statDateStr.deleteCharAt(statDateStr.lastIndexOf(","));
	  
			  
			  hql="select x.statDate,nvl(x.statNums,0)  stat_jh,nvl(y.statNums,0) stat_tj  from                                      "+
			      "(select to_char(a.create_date,'yyyymmdd') statDate,count(*) statNums  from  serv  a  where       "+
			      "a.state not in('2HM') and to_char(a.create_date,'yyyymmdd') in                                   "+
			      "("+statDateStr.toString()+") group by   to_char(a.create_date,'yyyymmdd')) x,                    "+
			      "(select to_char(b.service_stop_date,'yyyymmdd') statDate,count(*) statNums  from  serv  b  where    "+
			      "b.state not in('2HA','2HM') and to_char(b.service_stop_date,'yyyymmdd') in                          "+
			      "("+statDateStr.toString()+") group by   to_char(b.service_stop_date,'yyyymmdd')) y                  "+
			      "where x.statDate=y.statDate(+) order by x.statDate ";               
		
		       }
		
		}else{
			
		  hql= "select (select city_name from  bs_city_id_t where city_code=b.city_code and city_level=3) city_name,                                                                                                                                                   "+
			   "nvl(count(distinct case when b.state not in('2HM') and to_char(b.create_date,'yyyymmdd')='"+stat_time+"' then b.serv_id else null end),0) day_jihuo,                                                                           "+
			   "nvl(count( distinct (case when b.state  in('2HA','2HD') and to_char(b.service_stop_date,'yyyymmdd')='"+stat_time+"'  then b.serv_id else null end)),0) day_stop                                                                            "+
			   "from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'  group by b.city_code";	
			
			
		}
		  System.out.println("--stat_type-->"+stat_type+",----hql------->"+hql.toString());
		
			users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	public List getDayDevUsersNew(Map params){
		
		List users = null;
		String hql="";
		
		String stat_time=(String)params.get("stat_time");
		String stat_type=(String)params.get("stat_type");
		String stat_id=(String)params.get("stat_id");
		String stat_name="";
		
		StringBuffer statDateStr=new StringBuffer();
		Date dt1;
		
		try {
			
		if(stat_id.equals("1")){
	    	  
	    	  ///按地市统计日激活量和日停机量   // state : 2HM  预登录    2HD  停机
	       
	       hql=            "select to_char(create_date, 'yyyymmdd') stat_date,'1' STAT_TYPE,city_code stat_id,count(distinct serv_id) stat_value,          "+
	    				   "(select city_name from  bs_city_id_t where city_code=a.city_code and city_level=3 and rownum=1) stat_name                      "+
	    				   " from serv a where state not in ('2HM')                                                                                        "+
	    				   "           and create_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19                                                        "+
	    				   "           and create_date <= to_date('"+stat_time+"', 'yyyymmdd') + 1                                                         "+
	    				   "         group by to_char(create_date, 'yyyymmdd'), city_code                                                                  "+
	    				   "UNION ALL                                                                                                                      "+
	    				   "select to_char(service_stop_date, 'yyyymmdd') stat_date,'2' STAT_TYPE,city_code STAT_ID,count(distinct serv_id) stat_value,    "+
	    				   "(select city_name from  bs_city_id_t where city_code=b.city_code and city_level=3 and rownum=1) stat_name                      "+
	    				   "from serv b  where state not in ('2HA', '2HM')                                                                                 "+
	    				   "           and service_stop_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19                                                  "+
	    				   "           and service_stop_date <= to_date('"+stat_time+"', 'yyyymmdd') + 1                                                   "+
	    				   "         group by to_char(service_stop_date, 'yyyymmdd'), city_code                                                            "+
	    				   "                                                                                                                               "+
	    				   "UNION ALL                                                                                                                      "+
	    				   "select to_char(create_date, 'yyyymmdd') stat_date,'3' STAT_TYPE,TO_CHAR(service_favour_id) STAT_ID,                            "+
	    				   "count(distinct serv_id) stat_value,                                                                                            "+
	    				   "(select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum=1) stat_name           "+
	    				   "          from serv                                                                                                            "+
	    				   "         where state not in ('2HM')                                                                                            "+
	    				   "           and create_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19                                                        "+
	    				   "           and create_date <= to_date('"+stat_time+"', 'yyyymmdd') + 1                                                         "+
	    				   "         group by to_char(create_date, 'yyyymmdd'), service_favour_id                                                          "+
	    				   "UNION ALL                                                                                                                      "+
	    				   "select to_char(service_stop_date, 'yyyymmdd') stat_date,'4' STAT_TYPE,TO_CHAR(service_favour_id) STAT_ID,                      "+
	    				   "count(distinct serv_id) stat_value,                                                                                            "+
	    				   "(select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum=1) stat_name           "+
	    				   "          from serv                                                                                                            "+
	    				   "         where state not in ('2HA', '2HM')                                                                                     "+
	    				   "           and service_stop_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19                                                  "+
	    				   "           and service_stop_date <= to_date('"+stat_time+"', 'yyyymmdd') + 1                                                   "+
	    				   "         group by to_char(service_stop_date, 'yyyymmdd'), service_favour_id                                                    ";
	    		   
	    		     
 		   

		
		}else if(stat_id.equals("2")){
			
		      stat_name=(String)params.get("stat_name");
		    	  
		    	  ///按地市统计日激活量和日停机量   // state : 2HM  预登录    2HD  停机
		       
		       hql= "select to_char(create_date, 'yyyymmdd') stat_date,'3' STAT_TYPE,                                                                     "+
		    		"TO_CHAR(service_favour_id) STAT_ID,count(distinct serv_id) stat_value,                                                               "+
		    		"(select remark_str1 from tf_b_funtalkinfo where info_type = 4 and info_value = service_favour_id  and rownum = 1) stat_name          "+
		    		" from serv where state not in ('2HM')  and create_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19                                  "+
		    	    " and create_date <= to_date('"+stat_time+"', 'yyyymmdd') + 1                                                                         "+
		    		" and city_code in(select city_code from bs_city_id_t   where city_name ='"+stat_name+"'    and city_level = 3  and rownum = 1)       "+
		    		" group by to_char(create_date, 'yyyymmdd'), service_favour_id                                                                        "+    		
   				   " UNION ALL                                                                                                                            "+
				   " select to_char(service_stop_date, 'yyyymmdd') stat_date,'4' STAT_TYPE,TO_CHAR(service_favour_id) STAT_ID,                            "+
				   " count(distinct serv_id) stat_value,                                                                                                  "+
				   " (select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum=1) stat_name                 "+
				   " from serv   where state not in ('2HA', '2HM')                                                                                        "+
				   " and service_stop_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19  and service_stop_date <= to_date('"+stat_time+"', 'yyyymmdd') +1 "+
		    	   " and city_code in(select city_code from bs_city_id_t   where city_name ='"+stat_name+"' and city_level = 3  and rownum = 1)           "+
				   " group by to_char(service_stop_date, 'yyyymmdd'), service_favour_id                                                                   ";
			

		}else if(stat_id.equals("3")){
			
		      stat_name=(String)params.get("stat_name");
		    	  
		    	  ///按地市统计日激活量   // state : 2HM  预登录    2HD  停机
		       
		       hql= "select TO_CHAR(service_favour_id) STAT_ID,count(distinct serv_id) stat_value,                                                        "+
		    		"(select remark_str1 from tf_b_funtalkinfo where info_type = 4 and info_value = service_favour_id  and rownum = 1) stat_name          "+
		    		" from serv where state not in ('2HM')  and create_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19                                  "+
		    	    " and create_date <= to_date('"+stat_time+"', 'yyyymmdd') + 1                                                                         "+
		    		" and city_code in(select city_code from bs_city_id_t   where city_name ='"+stat_name+"'    and city_level = 3  and rownum = 1)       "+
		    		" group by  service_favour_id                                                                        ";
			

		}else if(stat_id.equals("4")){
			
		      stat_name=(String)params.get("stat_name");
		    	  
		    	  ///按地市统计日停机量   // state : 2HM  预登录    2HD  停机
		       
		       hql=" select TO_CHAR(service_favour_id) STAT_ID, count(distinct serv_id) stat_value,                                                                                                  "+
				   " (select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum=1) stat_name                 "+
				   " from serv   where state not in ('2HA', '2HM')                                                                                        "+
				   " and service_stop_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19  and service_stop_date <= to_date('"+stat_time+"', 'yyyymmdd') +1 "+
		    	   " and city_code in(select city_code from bs_city_id_t   where city_name ='"+stat_name+"' and city_level = 3  and rownum = 1)           "+
				   " group by  service_favour_id                                                                                                          ";
			

		}
		
		
		  System.out.println("--stat_type-->"+stat_type+",----hql------->"+hql.toString());
		
			users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	
	
	public List getDayPayFees(Map params){
		
		List users = null;
		String hql="";
		
		String stat_time=(String)params.get("stat_time");
		String stat_type=(String)params.get("stat_type");
		String stat_id=(String)params.get("stat_id");
		
		StringBuffer statBlanceStr=new StringBuffer();
		StringBuffer statAgentStr=new StringBuffer();
		Date dt1;
		
		
		try {
			
		//  stat_id =1 : 第一页的查询，目前页面只有环比查询，后面环比和同比都支持；    stat_id in(miaoyhk,kefuczk,miaoczk,weixin,.....) : 第二页的明细查询；
		if(stat_id.equals("1")){
			
			  if(stat_type.equals("huanbi")){
				  
			  
			//pay_kind not in(5,60)  剔除代理商开卡缴费,妙商城代理商扣款     此处统计代理商账户减少是为了监控返销的情况
		    hql= "  select x.stat_date,nvl(y.agentAdd,0) agentAdd,nvl(y.agentSub,0) agentSub ,x.allFee,x.miaoyhk,x.miaoczk,x.weixin,                               "+
		    		"x.kefuczk,x.nanjinggt,x.gaoyang19e,x.otherfee,x.zengfee,x.returnAllFee,x.returnZsFee,x.returnXJFee   from                                     "+
		    		"(Select to_char(oper_date,'yyyymmdd') stat_date,                                                                                              "+
		    		"nvl(sum(case when t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) allFee,                                               "+
		    		"nvl(sum(case when t.bank_id=9800 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) miaoyhk,                          "+
		    		"nvl(sum(case when t.bank_id=9801 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) kefuczk,                          "+
		    		"nvl(sum(case when t.bank_id=9802 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) miaoczk,                          "+
		    		"nvl(sum(case when t.bank_id=9804 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) weixin,                           "+
		    		"nvl(sum(case when t.bank_id=9810 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) nanjinggt,                        "+
		    		"nvl(sum(case when t.bank_id=1013 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) gaoyang19e,                       "+		    				    		
		    		"nvl(sum(case when t.bank_id not in(9800,9801,9802,9804,9810,1013) and  t.account_type not in (2,8) and t.amount>0  then t.amount end)/100,0) otherfee,  "+
		    		"nvl(sum(case when t.account_type in (2,8) and t.amount>0 then t.amount end)/100,0) zengfee,                                                   "+
		    		"nvl(sum(case when t.amount<0  then t.amount end )/100,0) returnAllFee,                                                                        "+
		    		"nvl(sum(case when t.amount<0  and t.account_type in (2,8) then t.amount end )/100,0) returnZsFee,                                             "+
		    		"nvl(sum(case when t.amount<0  and  t.account_type not in (2,8) then t.amount end )/100,0) returnXJFee                                         "+
		    		"from  balance_source t                                                                                                                        "+
		    		"Where  t.oper_date> to_date('"+stat_time+"','yyyymmdd')-19 and  t.oper_date<=to_date('"+stat_time+"','yyyymmdd')+1                                      "+
		    		"group by to_char(oper_date,'yyyymmdd')) x,(select to_char(operate_date,'yyyymmdd') stat_date,                                                 "+
		    		"nvl(sum(case when b.pay_flag=1 then a.pay_fee end)/100,0)  agentSub,                                                                          "+
		    		"nvl(sum(case when b.pay_flag=2 then a.pay_fee end)/100,0) agentAdd                                                                            "+
		    		"from  bd_dealer_charge_his_t@bill2crm a, bd_pay_kind_t@bill2crm b                                                                             "+
		    		"where operate_date > to_date('"+stat_time+"','yyyymmdd')-19 and  operate_date<=to_date('"+stat_time+"','yyyymmdd')+1                                    "+
		    		"and a.pay_kind=b.pay_kind and a.pay_kind not in(5,60)  group by to_char(operate_date,'yyyymmdd')) y                                           "+
		    		"where x.stat_date=y.stat_date(+)  order by x.stat_date     ";
		    
			  }else if(stat_type.equals("tongbi")){
				  
				  
				  SimpleDateFormat sdf =   new SimpleDateFormat("yyyyMMdd");
				  Date statDate= sdf.parse(stat_time);
				
				  Calendar calDate = Calendar.getInstance();  
				  calDate.setTime(statDate);  
				  
				  
				  statBlanceStr.append("  oper_date between to_date('"+stat_time+"','yyyymmdd')  and  to_date('"+stat_time+"','yyyymmdd')+1 ");
				  statAgentStr.append("   (operate_date between to_date('"+stat_time+"','yyyymmdd')  and  to_date('"+stat_time+"','yyyymmdd')+1 ");
				
				  for(int i=1;i<=12;i++){
				
					 calDate.add(Calendar.MONTH, -1);  
		             dt1 = calDate.getTime();	             
		             statBlanceStr.append(" or oper_date between to_date('"+sdf.format(dt1)+"','yyyymmdd') and to_date('"+sdf.format(dt1)+"','yyyymmdd')+1  ");
		             statAgentStr.append(" or operate_date between to_date('"+sdf.format(dt1)+"','yyyymmdd') and to_date('"+sdf.format(dt1)+"','yyyymmdd')+1  ");
				   }
				  
				  statAgentStr.append(" ) "); 
				  
				  
				  
				  hql= "  select x.stat_date,nvl(y.agentAdd,0) agentAdd,nvl(y.agentSub,0) agentSub ,x.allFee,x.miaoyhk,x.miaoczk,x.weixin,                               "+
				    		"x.kefuczk,x.otherfee,x.zengfee,x.returnAllFee,x.returnZsFee,x.returnXJFee   from                                                              "+
				    		"(Select to_char(oper_date,'yyyymmdd') stat_date,                                                                                              "+
				    		"nvl(sum(case when t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) allFee,                                               "+
				    		"nvl(sum(case when t.bank_id=9800 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) miaoyhk,                          "+
				    		"nvl(sum(case when t.bank_id=9801 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) kefuczk,                          "+
				    		"nvl(sum(case when t.bank_id=9802 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) miaoczk,                          "+
				    		"nvl(sum(case when t.bank_id=9804 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) weixin,                           "+
				    		"nvl(sum(case when t.bank_id=9810 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) nanjinggt,                        "+
				    		"nvl(sum(case when t.bank_id=1013 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) gaoyang19e,                       "+
				    		"nvl(sum(case when t.bank_id not in(9800,9801,9802,9804,9810,1013) and  t.account_type not in (2,8) and t.amount>0  then t.amount end)/100,0) otherfee,  "+
				    		"nvl(sum(case when t.account_type in (2,8) and t.amount>0 then t.amount end)/100,0) zengfee,                                                   "+
				    		"nvl(sum(case when t.amount<0  then t.amount end )/100,0) returnAllFee,                                                                        "+
				    		"nvl(sum(case when t.amount<0  and t.account_type in (2,8) then t.amount end )/100,0) returnZsFee,                                             "+
				    		"nvl(sum(case when t.amount<0  and  t.account_type not in (2,8) then t.amount end )/100,0) returnXJFee                                         "+
				    		"from  balance_source t                                                                                                                        "+
				    		"Where  "+statBlanceStr+"  group by to_char(oper_date,'yyyymmdd')) x,(select to_char(operate_date,'yyyymmdd') stat_date,                                                 "+
				    		"nvl(sum(case when b.pay_flag=1 then a.pay_fee end)/100,0)  agentSub,                                                                          "+
				    		"nvl(sum(case when b.pay_flag=2 then a.pay_fee end)/100,0) agentAdd                                                                            "+
				    		"from  bd_dealer_charge_his_t@bill2crm a, bd_pay_kind_t@bill2crm b                                                                             "+
				    		"where  "+statAgentStr+"  and a.pay_kind=b.pay_kind and a.pay_kind not in(5,60)  group by to_char(operate_date,'yyyymmdd')) y                                           "+
				    		"where x.stat_date=y.stat_date(+)  order by x.stat_date ";	  
				  
				  
			  }
		
		}else{
			
		  hql= "Select to_char(t.oper_date,'yyyymmdd') stat_date,(select city_name from  bs_city_id_t where city_code=t.city_code and city_level=3) city_name,                                            "+
				  "nvl(sum(case when t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) allFee,                                              "+
				  "nvl(sum(case when t.bank_id=9800 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) miaoyhk,                         "+
				  "nvl(sum(case when t.bank_id=9802 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) miaoczk,                         "+
				  "nvl(sum(case when t.bank_id=9804 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) weixin,                          "+
				  "nvl(sum(case when t.bank_id=9801 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) kefuczk,                         "+
				  "nvl(sum(case when t.bank_id=9810 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) nanjinggt,                         "+
				  "nvl(sum(case when t.bank_id=1013 and  t.account_type not in (2,8) and t.amount>0 then t.amount end )/100,0) gaoyang19e,                         "+
				  "nvl(sum(case when t.bank_id not in(9800,9801,9802,9804,9810,1013) and  t.account_type not in (2,8) and t.amount>0  then t.amount end)/100,0) otherfee, "+
				  "nvl(sum(case when t.account_type in (2,8) and t.amount>0 then t.amount end)/100,0) zengfee,                                                  "+
				  "nvl(sum(case when t.amount<0  then t.amount end )/100,0) returnAllFee,                                                                       "+
				  "nvl(sum(case when t.amount<0  and t.account_type in (2,8) then t.amount end )/100,0) returnZsFee,                                            "+
				  "nvl(sum(case when t.amount<0  and  t.account_type not in (2,8) then t.amount end )/100,0) returnXJFee                                        "+
				  "from  balance_source t                                                                                                                       "+
				  "Where  t.oper_date>= to_date('"+stat_time+"','yyyymmdd')-1 and  t.oper_date<to_date('"+stat_time+"','yyyymmdd')+1                              "+
				  "group by to_char(t.oper_date,'yyyymmdd'),t.city_code                                                                                                                         ";	
			
			
		}
		  System.out.println("--stat_type-->"+stat_type+",----hql------->"+hql.toString());
		
			users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	
	public List getDayUserBills(Map params){
		
		List users = null;
		String hql="";
		
		String stat_time=(String)params.get("stat_time");
		String stat_type=(String)params.get("stat_type");
		String stat_id=(String)params.get("stat_id");
		
		StringBuffer statDateStr=new StringBuffer();
		Date dt1;
		

		
		
		try {
			
			if(stat_id.equals("1")){
			
		    if(stat_type.equals("huanbi")){
			
		       hql=   "select x.stat_day,y.real_bill,x.settlebill,y.yuyin,y.liuliang,y.duanxin,y.yzyuyin,y.yzliuliang,(y.yzyuyin+y.yzliuliang) yzall,   "+
		       		  "y.laixian,y.dixiao,(y.real_bill-x.settlebill) profit from                                                                        "+
			    	  "(select a.stat_day,sum(a.all_fee-a.ysday_all_fee) settlebill from  tf_b_userfeewxp a  where a.stat_type=0 and                     "+
			    	  "to_date(a.stat_day,'yyyymmdd') >=to_date('"+stat_time+"','yyyymmdd')-20  and to_date(a.stat_day,'yyyymmdd') <=to_date('"+stat_time+"','yyyymmdd')  "+
			    	  "group by a.stat_day)  x, (select stat_day,(real_bill- case when substr(stat_day, 7, 2)='01' then 0 else  nvl(LEAD(real_bill)over(order by stat_day desc),0) end) real_bill,                                       "+
                      "(yuyin- case when substr(stat_day, 7, 2)='01' then 0 else nvl(LEAD(yuyin)over(order by stat_day desc),0) end) yuyin,                                                                    "+
                      "(liuliang- case when substr(stat_day, 7, 2)='01' then 0 else nvl(LEAD(liuliang)over(order by stat_day desc),0) end) liuliang,                                                           "+
                      "(duanxin- case when substr(stat_day, 7, 2)='01' then 0 else nvl(LEAD(duanxin)over(order by stat_day desc),0) end) duanxin,                                                              "+
                      "(yzyuyin- case when substr(stat_day, 7, 2)='01' then 0 else nvl(LEAD(yzyuyin)over(order by stat_day desc),0) end) yzyuyin,                                                              "+
                      "(yzliuliang- case when substr(stat_day, 7, 2)='01' then 0 else nvl(LEAD(yzliuliang)over(order by stat_day desc),0) end) yzliuliang,                                                     "+
                      "(yzlaixian- case when substr(stat_day, 7, 2)='01' then 0 else nvl(LEAD(yzlaixian)over(order by stat_day desc),0) end) laixian,                                                          "+
                      "case when substr(stat_day, 7, 2)='01' then dixiao else (nvl(LEAD(dixiao)over(order by stat_day desc),0)-dixiao ) end dixiao                                                                  "+
                      "from (select t.stat_day stat_day,sum(real_bill) real_bill,sum(yuyin) yuyin,sum(liuliang) liuliang,sum(duanxin) duanxin,          "+
                      "sum(yzyuyin) yzyuyin,sum(yzliuliang) yzliuliang,sum(yzlaixian) yzlaixian,sum(dixiao)  dixiao                                     "+
                      "from tf_b_prouserbill t  where to_date(t.stat_day,'yyyymmdd') >=to_date('"+stat_time+"','yyyymmdd')-20                           "+      
                      "and to_date(t.stat_day,'yyyymmdd') <=to_date('"+stat_time+"','yyyymmdd') group by t.stat_day)) y                                 "+    		
		    		  "where x.stat_day=y.stat_day  order by x.stat_day ";

			
		       }else if(stat_type.equals("tongbi")){
		    	   
		   	    statDateStr.append("'"+stat_time+"',");
			
			  SimpleDateFormat sdf =   new SimpleDateFormat("yyyyMMdd");
			  Date statDate= sdf.parse(stat_time);
			
			  Calendar calDate = Calendar.getInstance();  
			  calDate.setTime(statDate);  
			
			  for(int i=1;i<=12;i++){
			
				 calDate.add(Calendar.MONTH, -1);  
	             dt1 = calDate.getTime();	             
	             statDateStr.append("'"+sdf.format(dt1)+"',");
			   }
			  
			  statDateStr.deleteCharAt(statDateStr.lastIndexOf(","));
			  
			  
			  hql="select x.stat_day,y.userbill,x.settlebill,(y.userbill-x.settlebill) profit from                                   "+
			      "(select a.stat_day,sum(a.all_fee-a.ysday_all_fee) settlebill from  tf_b_userfeewxp a  where a.stat_type=0 and     "+
				  "a.stat_day in("+statDateStr.toString()+")                                                                         "+
				  "group by a.stat_day)  x,                                                                                          "+
				  "(select a.stat_day,sum(a.stat_value-a.remark_num1) userbill from  tf_b_gatherfeewxp a  where a.stat_type=1 and    "+
				  "a.stat_day in("+statDateStr.toString()+")                                                                         "+
				  "group by a.stat_day) y  where x.stat_day=y.stat_day(+)  order by x.stat_day                                       ";	    
		
		       }
			  
		  }else if(stat_id.equals("2")){
			
			hql="select (select city_name from  bs_city_id_t where city_code=x.stat_city and city_level=3) city_name,x.userBills,(x.userBills-y.settleBills) profits  "+
			    "from (select a.stat_city,sum(a.stat_value-a.remark_num1) userBills from  tf_b_gatherfeewxp a                                   "+
			    "where a.stat_type=1 and a.stat_day ='"+stat_time+"'  group by a.stat_city) x,                                                  "+
			    "(select a.city_code ,sum(a.all_fee-a.ysday_all_fee) settleBills from  tf_b_userfeewxp a                                        "+
			    "where a.stat_type=0 and  a.stat_day='"+stat_time+"'  group by a.city_code) y  where x.stat_city=y.city_code(+)                 ";
		
		  }else if(stat_id.equals("3")){
			
			hql="select a.city_name,sum(a.all_fee-a.ysday_all_fee) settlebill from  tf_b_userfeewxp a  "+
				"where a.stat_type=0 and  a.stat_day='"+stat_time+"'  group by a.city_name ";
							
		}
									
			  
		
		  System.out.println("--stat_type-->"+stat_type+",----hql------->"+hql.toString());
		
			users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	public List getMonProUserBills(Map params){
		
		List users = null;
		String hql="";
		String strFields="";
		
		String stat_time=(String)params.get("stat_time");
		String stat_type=(String)params.get("stat_type");
		String stat_id=(String)params.get("stat_id");
		String stat_id1=(String)params.get("stat_id1");
		
		Map<String,String> fieldsMap = new HashMap();
		
		fieldsMap.put("mon_jihuo", "jihuo");
		fieldsMap.put("mon_tingji", "tingji");
		fieldsMap.put("mon_billtwo", "remarknum1");
		fieldsMap.put("mon_users","remarknum2");
		fieldsMap.put("mon_xzxianjin", "xzxijin");	
		fieldsMap.put("mon_xzzengfee", "xzzengfee");
		fieldsMap.put("mon_owe", "qianfee");
		fieldsMap.put("mon_settle", "remarknum4");	
		fieldsMap.put("mon_yuyin", "yuyin");		
		fieldsMap.put("mon_liuliang", "liuliang");
		fieldsMap.put("mon_duanxin", "duanxin");
		fieldsMap.put("mon_laixian", "laixian");
		fieldsMap.put("mon_dixiao", "dixiao");
		
		
		try {
			
			if(stat_id.equals("1")){  /// 一级页面的查询
		    
					/// stattype =1 账期产品明细     =2 账期汇总        1选取的是主要产品，2汇总的所有金额，和gather的金额一致； billall: 40%统计行业卡  remarknum1:100%统计行业卡
		          hql=" select cycleid,'1' stattype,statid,(select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=statid and rownum=1) stat_name,"+
                      " nvl(billall,0),nvl(remarknum1,0),nvl(yuyin,0)+nvl(yzyuyin,0),nvl(liuliang,0)+nvl(yzliuliang,0),nvl(duanxin,0),nvl(yzyuyin,0),nvl(yzliuliang,0),nvl(laixian,0),"+
                      " nvl(dixiao,0),nvl(xzxijin,0),nvl(xzzengfee,0),nvl(qianfee,0),nvl(remarknum2,0),nvl(remarknum4,0),nvl(jihuo,0),nvl(tingji,0),ROUND(nvl(billall,0)/DECODE(nvl(remarknum2,1),0,1,nvl(remarknum2,1)),1), "+
                      " nvl(billall,0)-nvl(remarknum4,0),nvl(xzxijin,0)-nvl(remarknum4,0),"+
                      " ROUND((nvl(billall,0)-nvl(remarknum4,0))/DECODE(nvl(remarknum4,1),0,1,nvl(remarknum4,1)),2)*100||'%',ROUND((nvl(xzxijin,0)-nvl(remarknum4,0))/DECODE(nvl(remarknum4,1),0,1,nvl(remarknum4,1)),2)*100||'%'"+
                      " from TF_B_FTALKMONTHSTAT t where t.stattype=2 and to_date(cycleid,'yyyymm') > add_months(to_date('"+stat_time+"', 'yyyymm'),-15) "+
                      " and to_date(cycleid,'yyyymm')<=to_date('"+stat_time+"', 'yyyymm') and t.statid in(select info_value from tf_b_funtalkinfo where info_type=4 and remark_num1=1) "+
                      " union all select cycleid,'2' stattype,'all','all',"+
                      " sum(nvl(billall,0)),sum(nvl(remarknum1,0)),sum(nvl(yuyin,0))+sum(nvl(yzyuyin,0)),sum(nvl(liuliang,0))+sum(nvl(yzliuliang,0)),sum(nvl(duanxin,0)),"+
                      " sum(nvl(yzyuyin,0)),sum(nvl(yzliuliang,0)),sum(nvl(laixian,0)),sum(nvl(dixiao,0)),"+
                      " sum(nvl(xzxijin,0)),sum(nvl(xzzengfee,0)),sum(nvl(qianfee,0)),sum(nvl(remarknum2,0)),sum(nvl(remarknum4,0)),sum(nvl(jihuo,0)),sum(nvl(tingji,0)),ROUND(sum(nvl(billall,0))/DECODE(sum(nvl(remarknum2,0)),0,1,sum(nvl(remarknum2,0))),1), "+
                      " sum(nvl(billall,0))-sum(nvl(remarknum4,0)),sum(nvl(xzxijin,0))-sum(nvl(remarknum4,0)),"+
                      " ROUND((sum(nvl(billall,0))-sum(nvl(remarknum4,0)))/DECODE(sum(nvl(remarknum4,0)),0,1,sum(nvl(remarknum4,0))),2)*100||'%',ROUND((sum(nvl(xzxijin,0))-sum(nvl(remarknum4,0)))/DECODE(sum(nvl(remarknum4,0)),0,1,sum(nvl(remarknum4,0))),2)*100||'%'"+
                      " from TF_B_FTALKMONTHSTAT t where t.stattype=2 and to_date(cycleid,'yyyymm') > add_months(to_date('"+stat_time+"', 'yyyymm'),-15) "+
                      " and to_date(cycleid,'yyyymm')<=to_date('"+stat_time+"', 'yyyymm') and t.statid in(select info_value from tf_b_funtalkinfo where info_type=4 and remark_num1=1) group by t.cycleid";
			
			
			}else{ ///  二级页面的查询，分汇总和产品两大类
				
				if(stat_id1.equals("mon_arpu")){
					
					strFields="ROUND(nvl(sum(billall),0)/DECODE(nvl(sum(remarkstr3),1),0,1,nvl(sum(remarkstr3),1)),1)";
				}else if(stat_id1.equals("mon_profit1")){
					
					strFields="case when sum(nvl(remarknum4,0))=0 then 0 else sum(nvl(billall,0))-sum(nvl(remarknum4,0)) end ";
				}else if(stat_id1.equals("mon_profit2")){
					
					strFields="case when sum(nvl(remarknum4,0))=0 then 0 else sum(nvl(xzxijin,0))-sum(nvl(remarknum4,0)) end ";
					
				}else{
					strFields="nvl(sum("+fieldsMap.get(stat_id1)+"),0)";			
				}
				
				if(stat_id.equals("all")){
				
			        hql="select b.remarkstr2,nvl(b.nowstat,0), nvl(b.prestat,0) from (             "+
						"select a.cycleid,remarknum2,remarkstr2,nowstat,                           "+
						"LAG(nowstat) over(partition by remarknum2  order by cycleid) prestat      "+
						"from (select t.cycleid,t.remarknum2,t.remarkstr2,"+strFields+" nowstat from TF_B_FTALKMONTHSTAT t     "+
						"where t.stattype=3 and t.cycleid in(to_char(add_months(to_date('"+stat_time+"','yyyymm'),-1),'yyyymm'),'"+stat_time+"')      "+
						"group by t.cycleid,t.remarknum2,t.remarkstr2) a ) b  where b.cycleid='"+stat_time+"'";
		
			
		        }else{
		    	
		           hql= "select b.remarkstr2,nvl(b.nowstat,0), nvl(b.prestat,0) from (             "+
						"select a.cycleid,remarknum2,remarkstr2,nowstat,                           "+
						"LAG(nowstat) over(partition by remarknum2  order by cycleid) prestat      "+
						"from (select t.cycleid,t.remarknum2,t.remarkstr2,"+strFields+" nowstat from TF_B_FTALKMONTHSTAT t     "+
						"where t.stattype=3 and  t.statid='"+stat_id+"' and t.cycleid in(to_char(add_months(to_date('"+stat_time+"','yyyymm'),-1),'yyyymm'),'"+stat_time+"')      "+
						"group by t.cycleid,t.remarknum2,t.remarkstr2) a ) b  where b.cycleid='"+stat_time+"'";
								
		       }
		   }						
			
		 System.out.println("--stat_type-->"+stat_type+",----hql------->"+hql.toString());
		
	     users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	
	
	
	
	public List getMonProUserStayMons(Map params){
		
		List users = null;
		String hql="";
		
		String stat_time=(String)params.get("stat_time");
		String stat_type=(String)params.get("stat_type");
		String stat_id=(String)params.get("stat_id");
		String stat_diffmons=(String)params.get("stat_diffmons");
		
		try {
			
			if(stat_id.equals("1")){
			
				/// 在网用户按 -9标识统计
		          hql=" select a.service_favour_id,( select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum = 1) favour_name,  "+
		          	  "  a.diffmonths+1 diffmonths,count(*) statnums from                                                                     "+
		              "  (select t.service_favour_id, case when (t.state='2HA' or t.service_stop_date is null) then -10 else      "+
		              "  trunc(months_between(t.service_stop_date,t.create_date),0) end diffmonths  from serv t                    "+
		              "  where t.create_date >= to_date('201409', 'yyyymm')                                                "+
		              "  and t.create_date < last_day(to_date('"+stat_time+"', 'yyyymm'))+1                                       "+
		              "  and t.state not in ('2HM', '2HN')                                                                        "+
		              "  and t.service_favour_id  in (select info_value  from tf_b_funtalkinfo   where info_type = 4  and remark_num1 = 1)) a        "+
		              "  where a.diffmonths <20 group by a.service_favour_id,a.diffmonths order by a.service_favour_id,a.diffmonths";
			  
			
		    }else{
			
			   hql= " select (select city_name from  bs_city_id_t where city_code=t.city_code and city_level=3) city_name,count(*) stat_nums   from serv t  where t.create_date >= to_date('201409', 'yyyymm')  "+
		            " and t.create_date < last_day(to_date('"+stat_time+"', 'yyyymm')) + 1  and t.state not in ('2HM', '2HN','2HA') and t.service_stop_date is not null "+
		            " and trunc(months_between(t.service_stop_date, t.create_date), 0)+1="+stat_diffmons+"   and t.service_favour_id in('"+stat_id+"') group by t.city_code  order by count(*)";
					
		}
									
			
		 System.out.println("--stat_type-->"+stat_type+",----hql------->"+hql.toString());
		
	     users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	public List getMonthEnd(Map params){
		
		List users = null;
		String hql="";
		
		String stat_time=(String)params.get("stat_time");
		String stat_type=(String)params.get("stat_type");
		String stat_id=(String)params.get("stat_id");		
		
		StringBuffer statDateStr=new StringBuffer();
		Date dt1;
		

		
		
		try {
			
			if(stat_id.equals("1")){
			
				/// stattype =1 账期地市明细     =2 账期汇总
		          hql=" select cycleid,'1' stattype,statid,(select city_name from  bs_city_id_t where city_code=statid and city_level=3) city_name,"+
                      " nvl(billall,0),nvl(remarknum1,0),nvl(yuyin,0)+nvl(yzyuyin,0),nvl(liuliang,0)+nvl(yzliuliang,0),nvl(duanxin,0),nvl(yzyuyin,0),nvl(yzliuliang,0),nvl(laixian,0),"+
                      " nvl(dixiao,0),nvl(xzxijin,0),nvl(xzzengfee,0),nvl(qianfee,0),nvl(remarknum2,0),nvl(remarknum4,0),nvl(jihuo,0),nvl(tingji,0),ROUND(nvl(billall,0)/DECODE(nvl(remarknum2,1),0,1,nvl(remarknum2,1)),1), "+
                      " nvl(billall,0)-nvl(remarknum4,0),nvl(xzxijin,0)-nvl(remarknum4,0),ROUND((nvl(billall,0)-nvl(remarknum4,0))/DECODE(nvl(remarknum4,1),0,1,nvl(remarknum4,1)),2)*100||'%',ROUND((nvl(xzxijin,0)-nvl(remarknum4,0))/DECODE(nvl(remarknum4,1),0,1,nvl(remarknum4,1)),2)*100||'%'"+
                      " from TF_B_FTALKMONTHSTAT t where t.stattype=1 and to_date(cycleid,'yyyymm') > add_months(to_date('"+stat_time+"', 'yyyymm'),-15) "+
                      " and to_date(cycleid,'yyyymm')<=to_date('"+stat_time+"', 'yyyymm') "+
                      " union all select cycleid,'2' stattype,'all','all',"+
                      " sum(nvl(billall,0)),sum(nvl(remarknum1,0)),sum(nvl(yuyin,0))+sum(nvl(yzyuyin,0)),sum(nvl(liuliang,0))+sum(nvl(yzliuliang,0)),sum(nvl(duanxin,0)),"+
                      " sum(nvl(yzyuyin,0)),sum(nvl(yzliuliang,0)),sum(nvl(laixian,0)),sum(nvl(dixiao,0)),"+
                      " sum(nvl(xzxijin,0)),sum(nvl(xzzengfee,0)),sum(nvl(qianfee,0)),sum(nvl(remarknum2,0)),sum(nvl(remarknum4,0)),sum(nvl(jihuo,0)),sum(nvl(tingji,0)),ROUND(sum(nvl(billall,0))/DECODE(sum(nvl(remarknum2,0)),0,1,sum(nvl(remarknum2,0))),1), "+
                      " sum(nvl(billall,0))-sum(nvl(remarknum4,0)),sum(nvl(xzxijin,0))-sum(nvl(remarknum4,0)),ROUND((sum(nvl(billall,0))-sum(nvl(remarknum4,0)))/DECODE(sum(nvl(remarknum4,0)),0,1,sum(nvl(remarknum4,0))),2)*100||'%',ROUND((sum(nvl(xzxijin,0))-sum(nvl(remarknum4,0)))/DECODE(sum(nvl(remarknum4,0)),0,1,sum(nvl(remarknum4,0))),2)*100||'%'"+
                      " from TF_B_FTALKMONTHSTAT t where t.stattype=1 and to_date(cycleid,'yyyymm') > add_months(to_date('"+stat_time+"', 'yyyymm'),-15) "+
                      " and to_date(cycleid,'yyyymm')<=to_date('"+stat_time+"', 'yyyymm') group by t.cycleid";
			  			
		    }else{
			
			   hql= "";
		}
									
			
		 System.out.println("--stat_type-->"+stat_type+",----hql------->"+hql.toString());
		
	     users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	
	public List getMonthDevUsers(Map params){
		
		List users = null;
		String hql="";
		
		String stat_time=(String)params.get("stat_time");
		String stat_type=(String)params.get("stat_type");
		String stat_id=(String)params.get("stat_id");
		String stat_name="";
		
		StringBuffer statDateStr=new StringBuffer();
		Date dt1;
		
		try {
			
		if(stat_id.equals("1")){
	    	  
	    	  ///按地市统计月激活量和月停机量,按产品统计月激活量和月停机量   // state : 2HM  预登录    2HD  停机
	       
	       hql=            "select to_char(create_date, 'yyyymm') stat_date,'1' STAT_TYPE,city_code stat_id,count(distinct serv_id) stat_value,            "+
	    				   "(select city_name from  bs_city_id_t where city_code=a.city_code and city_level=3 and rownum=1) stat_name                      "+
	    				   " from serv a where state not in ('2HM')                                                                                        "+
	    				   "           and create_date >= add_months(to_date('"+stat_time+"', 'yyyymm'),-11)                                               "+
	    				   "           and create_date < add_months(to_date('"+stat_time+"', 'yyyymm'),1)                                                  "+
	    				   "         group by to_char(create_date, 'yyyymm'), city_code                                                                    "+
	    				   "UNION ALL                                                                                                                      "+
	    				   "select to_char(service_stop_date, 'yyyymm') stat_date,'2' STAT_TYPE,city_code STAT_ID,count(distinct serv_id) stat_value,      "+
	    				   "(select city_name from  bs_city_id_t where city_code=b.city_code and city_level=3 and rownum=1) stat_name                      "+
	    				   "from serv b  where state not in ('2HA', '2HM')                                                                                 "+
	    				   "           and service_stop_date >= add_months(to_date('"+stat_time+"', 'yyyymm'),-11)                                         "+
	    				   "           and service_stop_date < add_months(to_date('"+stat_time+"', 'yyyymmdd'),1)                                          "+
	    				   "         group by to_char(service_stop_date, 'yyyymm'), city_code                                                              "+
	    				   "                                                                                                                               "+
	    				   "UNION ALL                                                                                                                      "+
	    				   "select to_char(create_date, 'yyyymm') stat_date,'3' STAT_TYPE,TO_CHAR(service_favour_id) STAT_ID,                              "+
	    				   "count(distinct serv_id) stat_value,                                                                                            "+
	    				   "(select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum=1) stat_name           "+
	    				   "          from serv                                                                                                            "+
	    				   "         where state not in ('2HM')                                                                                            "+
	    				   "           and create_date >= add_months(to_date('"+stat_time+"', 'yyyymm'),-11)                                               "+
	    				   "           and create_date < add_months(to_date('"+stat_time+"', 'yyyymm'),1)                                                  "+
	    				   "         group by to_char(create_date, 'yyyymm'), service_favour_id                                                            "+
	    				   "UNION ALL                                                                                                                      "+
	    				   "select to_char(service_stop_date, 'yyyymm') stat_date,'4' STAT_TYPE,TO_CHAR(service_favour_id) STAT_ID,                        "+
	    				   "count(distinct serv_id) stat_value,                                                                                            "+
	    				   "(select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum=1) stat_name           "+
	    				   "          from serv                                                                                                            "+
	    				   "         where state not in ('2HA', '2HM')                                                                                     "+
	    				   "           and service_stop_date >= add_months(to_date('"+stat_time+"', 'yyyymm'),-11)                                         "+
	    				   "           and service_stop_date < add_months(to_date('"+stat_time+"', 'yyyymm'),1)                                            "+
	    				   "         group by to_char(service_stop_date, 'yyyymm'), service_favour_id                                                      ";
	    		   
	    		     
 		   

		
		}else if(stat_id.equals("2")){
			
		      stat_name=(String)params.get("stat_name");
		    	  
		    	  ///按地市统计日激活量和日停机量   // state : 2HM  预登录    2HD  停机
		       
		       hql= "select to_char(create_date, 'yyyymmdd') stat_date,'3' STAT_TYPE,                                                                     "+
		    		"TO_CHAR(service_favour_id) STAT_ID,count(distinct serv_id) stat_value,                                                               "+
		    		"(select remark_str1 from tf_b_funtalkinfo where info_type = 4 and info_value = service_favour_id  and rownum = 1) stat_name          "+
		    		" from serv where state not in ('2HM')  and create_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19                                  "+
		    	    " and create_date <= to_date('"+stat_time+"', 'yyyymmdd') + 1                                                                         "+
		    		" and city_code in(select city_code from bs_city_id_t   where city_name ='"+stat_name+"'    and city_level = 3  and rownum = 1)       "+
		    		" group by to_char(create_date, 'yyyymmdd'), service_favour_id                                                                        "+    		
   				   " UNION ALL                                                                                                                            "+
				   " select to_char(service_stop_date, 'yyyymmdd') stat_date,'4' STAT_TYPE,TO_CHAR(service_favour_id) STAT_ID,                            "+
				   " count(distinct serv_id) stat_value,                                                                                                  "+
				   " (select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum=1) stat_name                 "+
				   " from serv   where state not in ('2HA', '2HM')                                                                                        "+
				   " and service_stop_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19  and service_stop_date <= to_date('"+stat_time+"', 'yyyymmdd') +1 "+
		    	   " and city_code in(select city_code from bs_city_id_t   where city_name ='"+stat_name+"' and city_level = 3  and rownum = 1)           "+
				   " group by to_char(service_stop_date, 'yyyymmdd'), service_favour_id                                                                   ";
			

		}else if(stat_id.equals("3")){
			
		      stat_name=(String)params.get("stat_name");
		    	  
		    	  ///按地市统计日激活量   // state : 2HM  预登录    2HD  停机
		       
		       hql= "select TO_CHAR(service_favour_id) STAT_ID,count(distinct serv_id) stat_value,                                                        "+
		    		"(select remark_str1 from tf_b_funtalkinfo where info_type = 4 and info_value = service_favour_id  and rownum = 1) stat_name          "+
		    		" from serv where state not in ('2HM')  and create_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19                                  "+
		    	    " and create_date <= to_date('"+stat_time+"', 'yyyymmdd') + 1                                                                         "+
		    		" and city_code in(select city_code from bs_city_id_t   where city_name ='"+stat_name+"'    and city_level = 3  and rownum = 1)       "+
		    		" group by  service_favour_id                                                                        ";
			

		}else if(stat_id.equals("4")){
			
		      stat_name=(String)params.get("stat_name");
		    	  
		    	  ///按地市统计日停机量   // state : 2HM  预登录    2HD  停机
		       
		       hql=" select TO_CHAR(service_favour_id) STAT_ID, count(distinct serv_id) stat_value,                                                                                                  "+
				   " (select remark_str1 from tf_b_funtalkinfo where info_type=4 and info_value=service_favour_id and rownum=1) stat_name                 "+
				   " from serv   where state not in ('2HA', '2HM')                                                                                        "+
				   " and service_stop_date >= to_date('"+stat_time+"', 'yyyymmdd') - 19  and service_stop_date <= to_date('"+stat_time+"', 'yyyymmdd') +1 "+
		    	   " and city_code in(select city_code from bs_city_id_t   where city_name ='"+stat_name+"' and city_level = 3  and rownum = 1)           "+
				   " group by  service_favour_id                                                                                                          ";
			

		}
		
		
		  System.out.println("--stat_type-->"+stat_type+",----hql------->"+hql.toString());
		
			users = dbConnection.queryNotBind(hql,"ACCOUNT");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	
	
	
	

}
