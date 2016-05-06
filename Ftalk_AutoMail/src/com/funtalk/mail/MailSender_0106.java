package com.funtalk.mail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class MailSender_0106 {
	
	
	
	public static void main(String[] args) {
		
	  Connection  conn= null;
		   
	       SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");
	      
	       SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy/MM/dd");
	        
	       SimpleDateFormat formatter2 = new SimpleDateFormat ("dd");
	       
	       SimpleDateFormat formatter3 = new SimpleDateFormat ("yyyy年MM月");

	        Calendar rightNow = Calendar.getInstance();
	        rightNow.setTime(new Date());  // 将当前时间设为日期起始点
	        //rightNow.add(Calendar.YEAR,-1);//日期减1年
	        //rightNow.add(Calendar.MONTH,3);//日期加3个月
	        rightNow.add(Calendar.DAY_OF_YEAR,-1);//日期减1天
	        Date dt1=rightNow.getTime();
	        String reStr = formatter1.format(dt1);
	        System.out.println("--------统计日期------>"+reStr);
	        
		 	   if(formatter2.format(new Date()).toString().equals("01"))
			 		 return;
		  
		
       try{
    	  
	    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	    String connstr="jdbc:oracle:thin:@172.31.8.23:1521:acctdb1";
	    conn= DriverManager.getConnection(connstr, "unitele","bss_bill_xxp1");  
	
	     System.out.println("数据库连接成功！");
	  
        }catch (Exception e){   e.printStackTrace();  return;}
      
      
	    String []str=null;
	    String strsql_1;
	    String strsql_2;

	    Vector result1=new Vector();
	    Vector result2=new Vector();
        Vector v_get=new Vector();
        

	    try
	    {
	      Statement stmt=conn.createStatement();
	      
	 	 if(formatter2.format(new Date()).toString().equals("01")){
	      
	 	  strsql_1=" select f.prov_name,f.city_name,f.remark_num1,f.info_value,f.all_kh_kaika,f.yesday_kh_jihuo,f.month_kh_jihuo,f.all_kh_jihuo,                                                                                                 "+
	      " f.month_kh_stop,f.all_kh_stop,to_number(f.month_kh_czusers),f.user_fee,f.yes_stel_fee,f.all_stel_fee,round(case when f.all_stel_fee>30000 then 0 else 30000-f.all_stel_fee end,2) dixiao_fee                                         "+
	      " from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                             "+
	      " (select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                     "+
	      " count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                    "+
	      " count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') )  then m.acc_nbr else null end) month_kh_jihuo,                                                                     "+
	      " count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <to_char(sysdate, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                    "+
	      " count( distinct (case when m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm')  then m.acc_nbr else null end)) month_kh_stop,                                                                          "+
	      " count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                    "+
	      " (select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                        "+
	      " (select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                               "+
	      " ( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) all_stel_fee,                                                                                                "+
	      " ( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee                                                                                 "+
	      " from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.region_code,x.completed_date,x.create_date,                                                                                                      "+
	      " x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=1 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                               "+
	      " group by m.remark_num1,m.region_code,m.city_code,m.info_value order by m.region_code,m.city_code) f                                                                                                                                  "+
	      " union all                                                                                                                                                                                                                            "+
	      " select '1','2',3,'4',sum(f.all_kh_kaika),sum(f.yesday_kh_jihuo),sum(f.month_kh_jihuo),sum(f.all_kh_jihuo),                                                                                                                           "+
	      " sum(f.month_kh_stop),sum(f.all_kh_stop),sum(f.month_kh_czusers),sum(f.user_fee),sum(f.yes_stel_fee),sum(f.all_stel_fee),sum(round(case when f.all_stel_fee>30000 then 0 else 30000-f.all_stel_fee end,2))                            "+
	      " from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                             "+
	      " (select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                     "+
	      " count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                    "+
	      " count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') )  then m.acc_nbr else null end) month_kh_jihuo,                                                                     "+
	      " count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <to_char(sysdate, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                    "+
	      " count( distinct (case when m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) month_kh_stop,                                                                  "+
	      " count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                    "+
	      " (select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                        "+
	      " (select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                               "+
	      " ( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) all_stel_fee,                                                                                                "+
	      " ( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee                                                                                 "+
	      " from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.region_code,x.completed_date,x.create_date,                                                                                                      "+
	      " x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=1 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                               "+
	      " group by m.remark_num1,m.region_code,m.city_code,m.info_value) f ";
	 	  
	 	 }else{
	 		  

	 		 strsql_1=" select f.prov_name,f.city_name,f.remark_num1,f.info_value,f.all_kh_kaika,f.yesday_kh_jihuo,f.month_kh_jihuo,f.all_kh_jihuo,                                                                                                         "+
	 	  " f.month_kh_stop,f.all_kh_stop,to_number(f.month_kh_czusers),f.user_fee,f.yes_stel_fee,f.all_stel_fee,round(case when f.all_stel_fee>30000 then 0 else 30000-f.all_stel_fee end,2) dixiao_fee                                        "+
	 	  " from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
	 	  " (select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                    "+
	 	  " count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
	 	  " count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(m.create_date,'yyyymmdd') < to_char(sysdate, 'yyyymmdd') ) then m.acc_nbr else null end) month_kh_jihuo,  "+
	 	  " count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <to_char(sysdate, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                   "+
	 	  " count( distinct (case when m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(m.state_date,'yyyymmdd') <to_char(sysdate,'yyyymmdd') then m.acc_nbr else null end)) month_kh_stop, "+
	 	  " count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                     "+
	 	  " (select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
	 	  " (select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
	 	  " ( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) all_stel_fee,                                                                                               "+
	 	  " ( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee                                                                                "+
	 	  " from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.region_code,x.completed_date,x.create_date,                                                                                                     "+
	 	  " x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=1 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
	 	  " group by m.remark_num1,m.region_code,m.city_code,m.info_value order by m.region_code,m.city_code) f                                                                                                                                 "+
	 	  " union all                                                                                                                                                                                                                           "+
	 	  " select '1','2',3,'4',sum(f.all_kh_kaika),sum(f.yesday_kh_jihuo),sum(f.month_kh_jihuo),sum(f.all_kh_jihuo),                                                                                                                          "+
	 	  " sum(f.month_kh_stop),sum(f.all_kh_stop),sum(f.month_kh_czusers),sum(f.user_fee),sum(f.yes_stel_fee),sum(f.all_stel_fee),sum(round(case when f.all_stel_fee>30000 then 0 else 30000-f.all_stel_fee end,2))                           "+
	 	  " from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
	 	  " (select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                    "+
	 	  " count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
	 	  " count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(m.create_date,'yyyymmdd') < to_char(sysdate, 'yyyymmdd') ) then m.acc_nbr else null end) month_kh_jihuo,  "+
	 	  " count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <to_char(sysdate, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                   "+
	 	  " count( distinct (case when m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(m.state_date,'yyyymmdd') <to_char(sysdate,'yyyymmdd') then m.acc_nbr else null end)) month_kh_stop, "+
	 	  " count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                     "+
	 	  " (select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
	 	  " (select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
	 	  " ( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) all_stel_fee,                                                                                               "+
	 	  " ( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee                                                                                "+
	 	  " from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.region_code,x.completed_date,x.create_date,                                                                                                     "+
	 	  " x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=1 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
	 	  " group by m.remark_num1,m.region_code,m.city_code,m.info_value) f";	  
	 		  
	 	  } 
	         
	      
	      System.out.println(formatter.format(new Date())+"------>strsql_1="+strsql_1);
	      ResultSet rs=stmt.executeQuery(strsql_1);
	      while(rs.next())
	      {
	    	 
	         String col_pro_name     =(""+rs.getString(1)).trim();    
	         String col_city_name    =(""+rs.getString(2)).trim();        
	         String col_f_check_type =(""+rs.getString(3)).trim(); 
	         
	         String col_f_check_name="";
	         
	         if(col_f_check_type.equals("0")) 
	        	 col_f_check_name="无考核";
	         else if(col_f_check_type.equals("1")) 
	        	 col_f_check_name="按月";
	         else  
	        	 col_f_check_name="按年";
	         
	         
	         String col_f_no_seg          =(""+rs.getString(4)).trim();       
	         String col_all_kaika         =(""+rs.getString(5)).trim();   
	         String col_yesday_kh_jihuo   =(""+rs.getString(6)).trim();  
	         String col_month_kh_jihuo    =(""+rs.getString(7)).trim();  
	         String col_all_kh_jihuo      =(""+rs.getString(8)).trim();  
	         String col_month_kh_stop     =(""+rs.getString(9)).trim();
	         String col_all_kh_stop       =(""+rs.getString(10)).trim();
	         String col_month_kh_czusers  =(""+rs.getString(11)).trim();
	         String col_user_fee          =(""+rs.getString(12)).trim();
	         String col_yes_settle_fee    =(""+rs.getString(13)).trim();
	         String col_month_settle_fee  =(""+rs.getString(14)).trim(); 
	         String col_month_dxkh_fee    =(""+rs.getString(15)).trim(); 
	         
	         
	         
	                  
	         Vector v_row=new Vector();
	         v_row.add(col_pro_name);
	         v_row.add(col_city_name);
	         v_row.add(col_f_check_name);
	         v_row.add(col_f_no_seg);       
	         v_row.add(col_all_kaika);
	         v_row.add(col_yesday_kh_jihuo);
	         v_row.add(col_month_kh_jihuo);
	         v_row.add(col_all_kh_jihuo);
	         v_row.add(col_month_kh_stop);
	         v_row.add(col_all_kh_stop);	         
	         v_row.add(col_month_kh_czusers);
	         v_row.add(col_user_fee);
	         v_row.add(col_yes_settle_fee);
	         v_row.add(col_month_settle_fee);
	         v_row.add(col_month_dxkh_fee);
	         
	         result1.add(v_row);
	      }
	      rs.close();
	      rs=null;
	      stmt.close();
	      stmt=null;
	      

	    }
	    catch(Exception ex)
	    {
	      System.out.println("------->in getting date fail,"+ex);
	    }
	    
	    String buffer= "<br><p style=\"border-collapse:collapse;font-size:11pt;font-weight:bold\">联通按月考核【"+(result1.size()-1)+"个号段，考核时间："+ formatter3.format(dt1)+"】统计如下：</p>";
	    
		buffer+= "<br><table width=90% align=center border=2px cellspacing=0px  style=\"border-collapse:collapse;font-family: 微软雅黑\">";
		
		buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold\"><td align=center>省份</td>";
		buffer+="<td align=center>地市</td><td align=center>考核方式</td><td align=center>考核号段</td><td align=center>总开卡数</td>";
		buffer+="<td align=center>当日激活</td><td align=center>当月激活</td><td align=center>总激活数</td><td align=center>当月停机</td>";
		buffer+="<td align=center>往月停机</td><td align=center>当月出账用户数</td><td align=center>当月账单</td><td align=center>当日结算</td>";
		buffer+="<td align=center>当月结算</td><td align=center>当月考核</td></tr>";
		
	    for(int i=0;i<result1.size();i++)
	    {
	    	v_get=(Vector)result1.elementAt(i);
	    	
	    	
	    	
	    	
	    	if(i==result1.size()-1)
	    	{
	    		
	    		buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold;color:red\">";
	    		
	
	    		  buffer+="<td align=center >汇总</td><td></td><td></td><td></td>";
	    		   
	    		  for(int k=4;k<v_get.size();k++){
	    			  
	    			buffer+="<td align=center >";  
	   	        	
	   	        	buffer+=(String)v_get.get(k);
	   	        	
	   	        	buffer+="</td>";
	   	        	
	   	        }
	    		
	    	}else{
	    		
	    		buffer+="<tr style=\"border-collapse:collapse;font-size:10pt\">";
	    	
	        for(int j=0;j<v_get.size();j++){
	        	
	        	if(j==3)
	        	buffer+="<td align=center style=\"color:blue;font-weight:bold\" >";
	        	else
	        		buffer+="<td align=center >";
	        	
	        	buffer+=(String)v_get.get(j);
	        	
	        	buffer+="</td>";
	        	
	        }
	        
	        buffer+="</tr>";
	    	
	    	}
	    
	    }
	    
	    buffer+="</table><br>";
	    
	    
	    
	    /// 添加按年考核报表  2016.01.04
	    

		    try
		    {
		      Statement stmt=conn.createStatement();
		      
		    if(formatter2.format(new Date()).toString().equals("01")){
		      
		 		 strsql_2=  
              "select f.prov_name,f.city_name,f.remark_num1,f.info_value,f.all_kh_kaika,f.yesday_kh_jihuo,f.month_kh_jihuo,f.all_kh_jihuo,                                                                                                         "+
              "f.month_kh_stop,f.all_kh_stop,to_number(f.month_kh_czusers),f.user_fee,f.yes_stel_fee,f.month_stel_fee,f.year_stel_fee,round(case when f.year_stel_fee>360000 then 0 else 360000-f.year_stel_fee end,2) dixiao_fee                  "+
              "from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
              "(select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                    "+
              "count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
              "count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') ) then m.acc_nbr else null end) month_kh_jihuo,                                                                     "+
              "count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <to_char(sysdate, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                   "+
              "count( distinct (case when m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm')  then m.acc_nbr else null end)) month_kh_stop,                                                                         "+
              "count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                   "+
              "(select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
              "(select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
              "( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee,                                                                               "+
              "( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) month_stel_fee,                                                                                             "+
              "( select e.all_fee+e.year_all_fee from  tf_b_userfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.f_no_seg=m.info_value ) year_stel_fee                                                                                "+
              "from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.completed_date,x.create_date,                                                                                                                   "+
              "x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=2 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
              "group by m.remark_num1,m.city_code,m.info_value  order by m.city_code) f                                                                                                                                                            "+
              "union all                                                                                                                                                                                                                           "+
              "select '1','2',3,'4',sum(f.all_kh_kaika),sum(f.yesday_kh_jihuo),sum(f.month_kh_jihuo),sum(f.all_kh_jihuo),                                                                                                                          "+
              "sum(f.month_kh_stop),sum(f.all_kh_stop),sum(f.month_kh_czusers),sum(f.user_fee),sum(f.yes_stel_fee),                                                                                                                                "+
              "sum(f.month_stel_fee),sum(f.year_stel_fee),sum(round(case when f.year_stel_fee>360000 then 0 else 360000-f.year_stel_fee end,2))                                                                                                    "+
              "from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
              "(select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                    "+
              "count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
              "count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') ) then m.acc_nbr else null end) month_kh_jihuo,                                                                     "+
              "count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <to_char(sysdate, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                   "+
              "count( distinct (case when m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) month_kh_stop,                                                                         "+
              "count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                   "+
              "(select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
              "(select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
              "( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee,                                                                               "+
              "( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) month_stel_fee,                                                                                             "+
              "( select e.all_fee+e.year_all_fee from  tf_b_userfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.f_no_seg=m.info_value ) year_stel_fee                                                                                "+
              "from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.completed_date,x.create_date,                                                                                                                   "+
              "x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=2 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
              "group by m.remark_num1,m.city_code,m.info_value) f "; 
		 	  	 	  
		 	  }else{
		 		  

		 	  strsql_2=
              "select f.prov_name,f.city_name,f.remark_num1,f.info_value,f.all_kh_kaika,f.yesday_kh_jihuo,f.month_kh_jihuo,f.all_kh_jihuo,                                                                                                         "+
              "f.month_kh_stop,f.all_kh_stop,to_number(f.month_kh_czusers),f.user_fee,f.yes_stel_fee,f.month_stel_fee,f.year_stel_fee,round(case when f.year_stel_fee>360000 then 0 else 360000-f.year_stel_fee end,2) dixiao_fee                  "+
              "from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
              "(select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                    "+
              "count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
              "count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(m.create_date,'yyyymmdd') < to_char(sysdate, 'yyyymmdd') ) then m.acc_nbr else null end) month_kh_jihuo,  "+
              "count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <to_char(sysdate, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                   "+
              "count( distinct (case when m.state in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(m.state_date,'yyyymmdd') <to_char(sysdate,'yyyymmdd') then m.acc_nbr else null end)) month_kh_stop, "+
              "count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                     "+
              "(select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
              "(select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
              "( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee,                                                                               "+
              "( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) month_stel_fee,                                                                                             "+
              "( select e.all_fee+e.year_all_fee from  tf_b_userfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.f_no_seg=m.info_value ) year_stel_fee                                                                                "+
              "from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.completed_date,x.create_date,                                                                                                                   "+
              "x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=2 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
              "group by m.remark_num1,m.city_code,m.info_value  order by m.city_code) f                                                                                                                                                            "+
              "union all                                                                                                                                                                                                                           "+
              "select '1','2',3,'4',sum(f.all_kh_kaika),sum(f.yesday_kh_jihuo),sum(f.month_kh_jihuo),sum(f.all_kh_jihuo),                                                                                                                          "+
              "sum(f.month_kh_stop),sum(f.all_kh_stop),sum(f.month_kh_czusers),sum(f.user_fee),sum(f.yes_stel_fee),                                                                                                                                "+
              "sum(f.month_stel_fee),sum(f.year_stel_fee),sum(round(case when f.year_stel_fee>360000 then 0 else 360000-f.year_stel_fee end,2))                                                                                                    "+
              "from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
              "(select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                    "+
              "count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
              "count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(m.create_date,'yyyymmdd') < to_char(sysdate, 'yyyymmdd') ) then m.acc_nbr else null end) month_kh_jihuo,  "+
              "count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <to_char(sysdate, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                   "+
              "count( distinct (case when m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate,'yyyymm') and to_char(m.state_date,'yyyymmdd') <to_char(sysdate,'yyyymmdd') then m.acc_nbr else null end)) month_kh_stop, "+
              "count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                     "+
              "(select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
              "(select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
              "( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee,                                                                               "+
              "( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) month_stel_fee,                                                                                             "+
              "( select e.all_fee+e.year_all_fee from  tf_b_userfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.f_no_seg=m.info_value ) year_stel_fee                                                                                "+
              "from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.completed_date,x.create_date,                                                                                                                   "+
              "x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=2 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
              "group by m.remark_num1,m.city_code,m.info_value) f ";  
		 		  
		 	  }  
		         
		      
		      System.out.println(formatter.format(new Date())+"------>strsql_2="+strsql_2);
		      ResultSet rs=stmt.executeQuery(strsql_2);
		      while(rs.next())
		      {
		    	 
		         String col_pro_name     =(""+rs.getString(1)).trim();    
		         String col_city_name    =(""+rs.getString(2)).trim();        
		         String col_f_check_type =(""+rs.getString(3)).trim(); 
		         
		         String col_f_check_name="";
		         
		         if(col_f_check_type.equals("0")) 
		        	 col_f_check_name="无考核";
		         else if(col_f_check_type.equals("1")) 
		        	 col_f_check_name="按月";
		         else  
		        	 col_f_check_name="按年";
		         
		         
		         String col_f_no_seg          =(""+rs.getString(4)).trim();       
		         String col_all_kaika         =(""+rs.getString(5)).trim();   
		         String col_yesday_kh_jihuo   =(""+rs.getString(6)).trim();  
		         String col_month_kh_jihuo    =(""+rs.getString(7)).trim();  
		         String col_all_kh_jihuo      =(""+rs.getString(8)).trim();  
		         String col_month_kh_stop     =(""+rs.getString(9)).trim();
		         String col_all_kh_stop       =(""+rs.getString(10)).trim();
		         String col_month_kh_czusers  =(""+rs.getString(11)).trim();
		         String col_user_fee          =(""+rs.getString(12)).trim();
		         String col_yes_settle_fee    =(""+rs.getString(13)).trim();
		         String col_month_settle_fee  =(""+rs.getString(14)).trim(); 
		         String col_year_settle_fee   =(""+rs.getString(15)).trim(); 
		         String col_month_dxkh_fee    =(""+rs.getString(16)).trim(); 
		         
		         
		         
		                  
		         Vector v_row=new Vector();
		         v_row.add(col_pro_name);
		         v_row.add(col_city_name);
		         v_row.add(col_f_check_name);
		         v_row.add(col_f_no_seg);       
		         v_row.add(col_all_kaika);
		         v_row.add(col_yesday_kh_jihuo);
		         v_row.add(col_month_kh_jihuo);
		         v_row.add(col_all_kh_jihuo);
		         v_row.add(col_month_kh_stop);
		         v_row.add(col_all_kh_stop);	         
		         v_row.add(col_month_kh_czusers);
		         v_row.add(col_user_fee);
		         v_row.add(col_yes_settle_fee);
		         v_row.add(col_month_settle_fee);
		         v_row.add(col_year_settle_fee);
		         v_row.add(col_month_dxkh_fee);
		         
		         result2.add(v_row);
		      }
		      rs.close();
		      rs=null;
		      stmt.close();
		      stmt=null;
		      

		    }
		    catch(Exception ex)
		    {
		      System.out.println("------->in getting year settle date fail,"+ex);
		    }
	    
	    
		    buffer+= "<br><p style=\"border-collapse:collapse;font-size:11pt;font-weight:bold\">联通按年考核【"+(result2.size()-1)+"个号段，考核时间：2016年1月--2016年12月】统计如下：</p>";
		    
		    buffer+= "<br><table width=90% align=center border=2px cellspacing=0px  style=\"border-collapse:collapse;font-family: 微软雅黑\">";
			
			buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold\"><td align=center>省份</td>";
			buffer+="<td align=center>地市</td><td align=center>考核方式</td><td align=center>考核号段</td><td align=center>总开卡数</td>";
			buffer+="<td align=center>当日激活</td><td align=center>当月激活</td><td align=center>总激活数</td><td align=center>当月停机</td>";
			buffer+="<td align=center>往月停机</td><td align=center>当月出账用户数</td><td align=center>当月账单</td><td align=center>当日结算</td>";
			buffer+="<td align=center>当月结算</td><td align=center>当年结算</td><td align=center>当年考核</td></tr>";
			
		    for(int i=0;i<result2.size();i++)
		    {
		    	v_get=(Vector)result2.elementAt(i);
		    	
		    	
		    	
		    	
		    	if(i==result2.size()-1)
		    	{
		    		
		    		buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold;color:red\">";
		    		
		
		    		  buffer+="<td align=center >汇总</td><td></td><td></td><td></td>";
		    		   
		    		  for(int k=4;k<v_get.size();k++){
		    			  
		    			buffer+="<td align=center >";  
		   	        	
		   	        	buffer+=(String)v_get.get(k);
		   	        	
		   	        	buffer+="</td>";
		   	        	
		   	        }
		    		
		    	}else{
		    		
		    		buffer+="<tr style=\"border-collapse:collapse;font-size:10pt\">";
		    	
		        for(int j=0;j<v_get.size();j++){
		        	
		        	if(j==3)
		        	buffer+="<td align=center style=\"color:blue;font-weight:bold\" >";
		        	else
		        		buffer+="<td align=center >";
		        	
		        	buffer+=(String)v_get.get(j);
		        	
		        	buffer+="</td>";
		        	
		        }
		        
		        buffer+="</tr>";
		    	
		    	}
		    
		    }
		    
		    buffer+="</table><br>";
		   
	    
	    
	    
	    buffer+="<p style=\"border-collapse:collapse;font-size:10pt;font-weight:bold\"> 备注：此统计数据截止"+reStr+" 24点，下面是报表相关字段解释：<br>总开卡数：号段在联通测已经初始化的用户数量；";
	    buffer+="<br>当日激活：号段当日实名激活返单的用户数量，即当日卖出量；<br>当月激活：号段当月实名激活返单的用户数量，即当月卖出量；<br>总激活数：号段总共实名激活返单的用户数量，即已经卖出量；<br>";
	    buffer+="当月停机：号段当月因欠费停机的用户数量，此部分用户当月仍然产生账单；<br>往月停机：号段往月因欠费停机的用户数量，此部分用户当月不再产生账单；<br>当月出账用户数：号段当月产生账单的用户数量；<br>";
	    buffer+="当月账单：号段当月产生的用户账单金额(单位：元)；<br>当日结算：号段当日需要支付给联通的结算金额(单位：元)；<br>当月结算：号段当月需要支付给联通的结算金额(单位：元)；";
	    buffer+="<br>当月考核：号段当月因未达到联通30000元最低消费而支付的违约金额(单位：元)；<br>当年考核：号段当年因未达到联通360000元最低消费而支付的违约金额(单位：元)；</p>";
	    

	    System.out.println(formatter.format(new Date())+"------>buffer===="+buffer.toString());
      
     
	  Properties props = new Properties();
	
	  
	  props.put("mail.smtp.host", "mail.funtalk.cn");
	  props.put("mail.smtp.auth", "true");

	  Session session = Session.getDefaultInstance(props, new Authenticator() {

	   protected PasswordAuthentication getPasswordAuthentication() {
	    String username = "wangxp1";
	    String password = "fantalk_123456";
	    return new PasswordAuthentication(username,password); 
	   }
	  });
	  
	  
	  ///////String[] to = {"43198341@qq.com"};
	  
	  String[] to = {"liuyl4@funtalk.cn","liyao1@funtalk.cn","wangxp1@funtalk.cn","liuyangzs@funtalk.cn","zhangxiaols@funtalk.cn","xiecx@funtalk.cn","lilei5@funtalk.cn",
              "lijun@funtalk.cn","duzt@funtalk.cn","qifei@funtalk.cn","fengsg@funtalk.cn","shaoys1@funtalk.cn","songlj1@funtalk.cn"};
	  
	  String from = "wangxp1@funtalk.cn";
	  String subject = "联通考核号段统计日报@"+reStr;
	  MimeMessage msg = new MimeMessage(session);
	  try {
	   msg.setFrom(new InternetAddress(from));
	   InternetAddress[] addressTo = new InternetAddress[to.length];
	   for (int i = 0; i < to.length; i++)
	   {
	    addressTo[i] = new InternetAddress(to[i]);
	   }
	   msg.setRecipients(RecipientType.TO, addressTo); 
	   // msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to)); // 发送单个用户
	   msg.setSubject(subject);
 
	   BodyPart messageBodyPart = new MimeBodyPart();
	   	   
	   messageBodyPart.setContent(buffer, "text/html;charset=GBK"); // 网页格式
	   // Fill the message  发送字符串
	  // messageBodyPart.setText(buffer.toString());

	   // Create a multipar message
	   Multipart multipart = new MimeMultipart();
	   // Set text message part
	   multipart.addBodyPart(messageBodyPart);
	   
	   /*// Part two is attachment  发送附件
	  messageBodyPart = new MimeBodyPart();
	   String filename = "file.txt";
	   DataSource source = new FileDataSource(filename);
	   messageBodyPart.setDataHandler(new DataHandler(source));
	   messageBodyPart.setFileName(filename);
	   multipart.addBodyPart(messageBodyPart);
	   
	  */
	   // Send the complete message parts
	   msg.setContent(multipart);
	   Transport transport = session.getTransport("smtp");
	   transport.send(msg);
	   System.out.println(formatter.format(new Date())+"------>E-mail have been sent successfully!");
	  }
	  catch(Exception exc) {
	   System.out.println(exc);
	  }
	  
	  
	 }

}
