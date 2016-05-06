package com.funtalk.mail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;






import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class MailSenderProductStat {
	
	
	public static  void saveDateToChart(String lableTitle,String lableX,String lableY,String filePath,String picName,DefaultCategoryDataset linedataset){
		
		
		
		  try {
		    	

	            //定义图标对象
	            JFreeChart chart = ChartFactory.createLineChart("",// 报表题目，字符串类型
	                          "", // 横轴
	                          "", // 纵轴
	                          linedataset, // 获得数据集
	                          PlotOrientation.VERTICAL,
	                          true, 
	                          false, 
	                          false 
	                          );

	            Font font = new Font("微软雅黑", Font.BOLD, 14);   
	            Font font1 = new Font("微软雅黑", Font.BOLD, 12); 
	            

	            TextTitle title = new TextTitle(lableTitle);     
	            title.setFont(font);     
	            chart.setTitle(title); 
	            
	            //chart.getLegend().setVisible(false); ///  不显示 折线系列名称       
	            
	            LegendTitle legend = chart.getLegend(); // 设置比例图标示，就是那个一组y的value的    
	            
	            legend.setItemFont(font1);

	            // 生成图形
	            CategoryPlot plot = chart.getCategoryPlot();
	            // 图像属性部分
	            plot.setBackgroundPaint(Color.white);
	            plot.setDomainGridlinesVisible(true);  //设置背景网格线是否可见
	            plot.setDomainGridlinePaint(Color.BLACK); //设置背景网格线颜色
	            plot.setRangeGridlinePaint(Color.GRAY);
	            plot.setNoDataMessage("没有数据");//没有数据时显示的文字说明。 
	            // 数据轴属性部分
	            
	            // 横轴 x
	            CategoryAxis domainAxis = plot.getDomainAxis();
	            domainAxis.setLabelFont(font);  
	            domainAxis.setLabel(lableX);
	            
	            // 纵轴 Y
	            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	            rangeAxis.setLabelFont(font);
	            rangeAxis.setLabel(lableY);
	        
	            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	            rangeAxis.setAutoRangeIncludesZero(true);
	            rangeAxis.setUpperMargin(0.20);
	            rangeAxis.setLabelAngle(Math.PI / 2.0); 
	            rangeAxis.setAutoRange(false);

	            // 数据渲染部分 主要是对折线做操作
	            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	            renderer.setBaseItemLabelsVisible(true);
	            
	            renderer.setSeriesPaint(0, Color.black);    //设置折线的颜色
	            renderer.setBaseShapesFilled(true);
	    
	            renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());  
	               
	            /*XYPlot  对应的则是 ： StandardXYItemLabelGenerator */
	            

	            renderer.setBaseItemLabelFont(font1);  //设置提示折点数据形状
	            plot.setRenderer(renderer);
	            
	            
	            
	   		   File filep = new File(filePath);
	   		   if(!filep.exists()) filep.mkdir();
	   		 
	   		   String file=filePath+picName;
	   			
	   		   System.out.println((new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss")).format(new Date())+"------>making file="+file);
	   			
	   	       // 创建文件输出流
	   	       File file_jpg=new File(file);	
	   		   if(file_jpg.exists()&&file_jpg.isFile()){
	   			 file_jpg.delete();
	   			}
	            
				
	          // 输出到哪个输出流
	          ChartUtilities.saveChartAsJPEG(file_jpg, chart, 1000, 350);
		    
		    
		    } catch (IOException e) {
	            e.printStackTrace();
	     }
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
	  Connection  conn= null;
		   
	       SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");
	      
	       SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd");
	        
	       SimpleDateFormat formatter2 = new SimpleDateFormat ("dd");
	       
	       SimpleDateFormat formatter3 = new SimpleDateFormat ("yyyy/MM/dd");
	       


	        Calendar rightNow = Calendar.getInstance();
	        rightNow.setTime(new Date());  // 将当前时间设为日期起始点
	        //rightNow.add(Calendar.YEAR,-1);//日期减1年
	        //rightNow.add(Calendar.MONTH,3);//日期加3个月
	        rightNow.add(Calendar.DAY_OF_YEAR,-1);//日期减1天
	        Date dt1=rightNow.getTime();
	        String reStr = formatter1.format(dt1);
	        
	        String reStr1 = formatter3.format(dt1);
	        
	        System.out.println("--------统计日期------>"+reStr);
	        
	        int m=0;

				
		
       try{
    	  
	    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	    String connstr="jdbc:oracle:thin:@172.31.8.23:1521:acctdb1";
	    conn= DriverManager.getConnection(connstr, "unitele","bss_bill_xxp1");  
	
	     System.out.println("数据库连接成功！");
	  
        }catch (Exception e){  
        	
        	e.printStackTrace();	
        	return;
        	
        }
      
      
	    String []str=null;
	    String strsql_1;
	    String strsql_2;
	    String strsql_3;
	    String strsql_4;
	    String strsql_5;


	    Vector result_1=new Vector();
	    Vector result_2=new Vector();
	    Vector result_3=new Vector();
	    Vector result_4=new Vector();
	    Vector result_5=new Vector();
        Vector v_get=new Vector();
        
        
        

	
	    
	    
	    
	    String buffer=" <style type=\"text/css\"> ";	
	    buffer+=".Tab{ border-collapse:collapse; width:95%;text-align:center;font-family: 微软雅黑}";
	    buffer+=".Tab td{ border:solid 1px #009191;word-break: keep-all;white-space:nowrap;}";
	    buffer+=".p1{ border-collapse:collapse;font-size:11pt;font-weight:bold;font-family: 微软雅黑}";
	    buffer+=".p2{ border-collapse:collapse;font-size:10pt;font-weight:bold;font-family: 微软雅黑}";
	    buffer+="</style>";
	    
	    
	   
	    
	    /// 添加妙+行业卡按地市汇总报表  2016.01.07
	    

	    try
	    {
	      Statement stmt=conn.createStatement();
	 		  

	 	strsql_2=	 	
	 	" select b.service_favour_id product_id,(select f.remark_str2 from  tf_b_gatherfeewxp f where f.stat_day=to_char(sysdate-1,'yyyymmdd') and f.stat_type='2' and f.stat_name=b.service_favour_id ) product_name,                                  "+
	 	" (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=b.city_code and city_level=3) and city_level=2) prov_name,                                                                   "+
	 	" (select city_name from  bs_city_id_t where city_code=b.city_code and city_level=3) city_name,                                                                                                                                                 "+
	 	" nvl(count(distinct  b.acc_nbr),0) all_kaika,                                                                                                                                                                                                  "+
	 	" nvl(count(distinct case when b.state not in('2HM') and to_char(b.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end),0) yesday_kh_jihuo,                                                                      "+
	 	" nvl(count(distinct case when (b.state not in('2HM') and to_char(b.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(b.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then b.acc_nbr else null end),0) month_kh_jihuo,    "+
	 	" nvl(count(distinct case when b.state not in('2HM')  and  to_char(b.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then b.acc_nbr else null end),0) all_kh_jihuo,                                                                   "+
	 	" nvl(count(distinct (case when b.state  in('2HD') and to_char(b.state_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd')  then b.acc_nbr else null end)),0) yesday_kh_stop, "+
	 	" nvl(count(distinct (case when (b.state  in('2HD') and to_char(b.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(b.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd'))  then b.acc_nbr else null end)),0) month_kh_stop,       "+
	 	" nvl(count(distinct (case when b.state not in('2HM','2HA') and to_char(b.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end)),0) all_kh_stop,                                                                  "+
	 	" nvl((select z.user_nums from  tf_b_gatherfeewxp z where z.stat_day=to_char(sysdate-1,'yyyymmdd') and z.stat_type='5' and z.stat_name=b.service_favour_id and z.stat_city=b.city_code),0) usernums_charge,                                     "+
	 	" nvl((select nvl(x.remark_num2,0) from  tf_b_gatherfeewxp x where x.stat_day=to_char(sysdate-1,'yyyymmdd') and x.stat_type='5' and x.stat_name=b.service_favour_id and x.stat_city=b.city_code),0) yesday_charge,                 "+
	 	" nvl((select y.stat_value from  tf_b_gatherfeewxp y where y.stat_day=to_char(sysdate-1,'yyyymmdd') and y.stat_type='5' and y.stat_name=b.service_favour_id and y.stat_city=b.city_code),0) month_charge,                                       "+
	 	" nvl((select c.user_nums from  tf_b_gatherfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.stat_type='4' and c.stat_name=b.service_favour_id and c.stat_city=b.city_code),0) month_kh_czusers,                                    "+
	 	" nvl((select nvl(d.stat_value-d.remark_num1,0) from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and d.stat_type='4' and d.stat_name=b.service_favour_id and d.stat_city=b.city_code ),0) nowday_fee,                   "+
	 	" nvl((select e.stat_value from  tf_b_gatherfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.stat_type='4' and e.stat_name=b.service_favour_id and e.stat_city=b.city_code),0) month_fee,                                           "+
	 	" nvl((select nvl(f.remark_num1,0) from  tf_b_gatherfeewxp f where f.stat_day=to_char(sysdate-1,'yyyymmdd') and f.stat_type='5' and f.stat_name=b.service_favour_id and f.stat_city=b.city_code),0) usernums_month_charge                        "+
	 	" from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'                                                                                                                                                                            "+
	 	" and b.service_favour_id in(1370080) group by b.service_favour_id,b.city_code                                                                                                                                                                  "+
	 	" union all                                                                                                                                                                                                                                     "+
	 	" select 1,'2','3','4',                                                                                                                                                                                                                         "+
	 	" sum(nvl(count(distinct  b.acc_nbr),0)) all_kaika,                                                                                                                                                                                             "+
	 	" sum(nvl(count(distinct case when b.state not in('2HM') and to_char(b.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end),0)) yesday_kh_jihuo,                                                                 "+
	 	" sum(nvl(count(distinct case when (b.state not in('2HM') and to_char(b.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(b.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then b.acc_nbr else null end),0)) month_kh_jihuo,"+ 
	 	" sum(nvl(count(distinct case when b.state not in('2HM')  and  to_char(b.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then b.acc_nbr else null end),0)) all_kh_jihuo,   " +
	 	" sum(nvl(count( distinct (case when b.state  in('2HD') and to_char(b.state_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd')  then b.acc_nbr else null end)),0)) yesday_kh_stop, "+                                                      
	 	" sum(nvl(count( distinct (case when (b.state  in('2HD') and to_char(b.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(b.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then b.acc_nbr else null end)),0)) month_kh_stop,  "+
	 	" sum(nvl(count( distinct (case when b.state not in('2HM','2HA') and to_char(b.state_date,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') then b.acc_nbr else null end)),0)) all_kh_stop,                                                              "+
	 	" to_char(sum(nvl((select z.user_nums from  tf_b_gatherfeewxp z where z.stat_day=to_char(sysdate-1,'yyyymmdd') and z.stat_type='5' and z.stat_name=b.service_favour_id and z.stat_city=b.city_code),0))) usernums_charge,                       "+
	 	" sum(nvl((select nvl(x.remark_num2,0) from  tf_b_gatherfeewxp x where x.stat_day=to_char(sysdate-1,'yyyymmdd') and x.stat_type='5' and x.stat_name=b.service_favour_id and x.stat_city=b.city_code),0)) yesday_charge,            "+
	 	" sum(nvl((select y.stat_value from  tf_b_gatherfeewxp y where y.stat_day=to_char(sysdate-1,'yyyymmdd') and y.stat_type='5' and y.stat_name=b.service_favour_id and y.stat_city=b.city_code),0)) month_charge,                                  "+
	 	" to_char(sum(nvl((select c.user_nums from  tf_b_gatherfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.stat_type='4' and c.stat_name=b.service_favour_id and c.stat_city=b.city_code),0))) month_kh_czusers,                      "+
	 	" sum(nvl((select nvl(d.stat_value-d.remark_num1,0) from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and d.stat_type='4' and d.stat_name=b.service_favour_id and d.stat_city=b.city_code ),0)) nowday_fee,              "+
	 	" sum(nvl((select e.stat_value from  tf_b_gatherfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.stat_type='4' and e.stat_name=b.service_favour_id and e.stat_city=b.city_code),0)) month_fee,                                     "+
	 	" sum(nvl((select nvl(f.remark_num1,0) from  tf_b_gatherfeewxp f where f.stat_day=to_char(sysdate-1,'yyyymmdd') and f.stat_type='5' and f.stat_name=b.service_favour_id and f.stat_city=b.city_code),0)) usernums_month_charge                  "+
	 	" from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'                                                                                                                                                                            "+
	 	" and b.service_favour_id in(1370080) group by b.service_favour_id,b.city_code";
	 		  
	 	 
	         
	      
	      System.out.println(formatter.format(new Date())+"------>strsql_2="+strsql_2);
	      
	      ResultSet rs=stmt.executeQuery(strsql_2);
	      while(rs.next())
	      {
	    	    
	         String col_product_name      =(""+rs.getString(2)).trim();
	         String col_pro_name          =(""+rs.getString(3)).trim();    
	         String col_city_name         =(""+rs.getString(4)).trim();
	         String col_all_kaika         =(""+rs.getString(5)).trim();   
	         String col_yesday_jihuo      =(""+rs.getString(6)).trim();  
	         String col_month_jihuo       =(""+rs.getString(7)).trim();  
	         String col_all_jihuo         =(""+rs.getString(8)).trim();  
	         String col_yesday_stop       =(""+rs.getString(9)).trim();
	         String col_month_stop        =(""+rs.getString(10)).trim();
	         String col_beforemonth_stop  =(""+rs.getString(11)).trim();
	         String col_yesdaycha_users   =(""+rs.getString(12)).trim();
	         String col_yesday_charge     =(""+rs.getString(13)).trim();
	         String col_month_charge      =(""+rs.getString(14)).trim();
	         String col_month_zcusers     =(""+rs.getString(15)).trim();
	         String col_yesday_fee        =(""+rs.getString(16)).trim(); 
	         String col_month_fee         =(""+rs.getString(17)).trim();    
	         String col_monthcha_users    =(""+rs.getString(18)).trim();  
	         
	                  
	         Vector v_row=new Vector();
	         
	         v_row.add(col_product_name);
	         v_row.add(col_pro_name);
	         v_row.add(col_city_name);
	         v_row.add(col_all_kaika);
	         v_row.add(col_yesday_jihuo);
	         v_row.add(col_month_jihuo);
	         v_row.add(col_all_jihuo);
	         v_row.add(col_yesday_stop);
	         v_row.add(col_month_stop);
	         v_row.add(col_beforemonth_stop);	         
	         v_row.add(col_yesday_charge);
	         v_row.add(col_yesdaycha_users);
	         v_row.add(col_month_charge);
	         v_row.add(col_monthcha_users);
	         v_row.add(col_yesday_fee);
	         v_row.add(col_month_fee);
	         v_row.add(col_month_zcusers);


	         result_2.add(v_row);
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
    
    
	    buffer+= "<br><p class=\"p1\">妙+行业卡按地市统计日报【截止"+reStr+"  24点】</p>";
	    
	    buffer+= "<br><table class=\"Tab\">";
		
		buffer+="<tr style=\"font-size:11pt;font-weight:bold\"><td>产品名称</td>";
		buffer+="<td>省份</td><td>地市</td><td>总开卡</td>";
		buffer+="<td>当日激活</td><td>当月激活</td><td>总激活</td><td>当日停机</td><td>当月停机</td>";
		buffer+="<td>总停机</td><td>当日充值(元)</td><td>当日充值用户</td><td>当月充值(元)</td><td>当月充值用户</td><td>当日账单(元)</td><td>当月账单(元)</td><td>当月出账用户</td>";

		
	    for(int i=0;i<result_2.size();i++)
	    {
	    	v_get=(Vector)result_2.elementAt(i);
	    	
	    	
	    	
	      	if(i==result_2.size()-1)
	    	{
	    		
	    		buffer+="<tr style=\"font-size:11pt;font-weight:bold;color:red\">";
	    		
	
	    		  buffer+="<td>汇总</td><td></td><td></td>";
	    		   
	    		  for(int k=3;k<v_get.size();k++){
	    			  
	    			buffer+="<td>";  
	   	        	
	   	        	buffer+=(String)v_get.get(k);
	   	        	
	   	        	buffer+="</td>";
	   	        	
	   	        }
	    		
	    	}else{
	    	
	    		
	    		buffer+="<tr style=\"font-size:10pt\">";
	    	
	        for(int j=0;j<v_get.size();j++){
	        	
	        	if(j==2)
	        	buffer+="<td  style=\"color:blue;font-weight:bold\" >";
	        	else
	        		buffer+="<td>";
	        	
	        	buffer+=(String)v_get.get(j);
	        	
	        	buffer+="</td>";
	        	
	        }
	        
	        buffer+="</tr>";
	    	
	    	}
	    
	    }
	    
	    buffer+="</table><br>";
	    
	    
	    
	    try
	    {
	      Statement stmt=conn.createStatement();
	      
	 	  strsql_3="select trunc(sum(case when to_char(t.state_date,'yyyymmdd')= to_char(sysdate-1,'yyyymmdd') then t.rest_fee end)*0.4,2),"
	 	  		 + "trunc(sum(case when to_char(t.state_date,'yyyymm')= to_char(sysdate-1,'yyyymm') then t.rest_fee end)*0.4,2),"
	 	  		 + "trunc(sum(t.rest_fee)*0.4,2) from tf_b_2hdproduct t where t.prod_code=1370080 and t.rest_fee<0 "; 
	
	 	  
	      System.out.println(formatter.format(new Date())+"------>strsql_3="+strsql_3);
	      
	      ResultSet rs=stmt.executeQuery(strsql_3);
	      while(rs.next())
	      {
	    	 
	    	  double col_day_ownfee       =rs.getDouble(1);    
	    	  double col_month_ownfee     =rs.getDouble(2);
	          double col_all_ownfee       =rs.getDouble(3);
                  
	         Vector v_row=new Vector();
	         v_row.add(col_day_ownfee);
	         v_row.add(col_month_ownfee);
	         v_row.add(col_all_ownfee);
	         
	         result_3.add(v_row);
	      }
	      rs.close();
	      rs=null;
	      stmt.close();
	      stmt=null;
	      

	    }
	    catch(Exception ex)
	    {
	      System.out.println("------->in getting hangyeka ownfee fail,"+ex);
	    }
	    
	    
	    
	    
	    buffer+="<br><p class=\"p2\"> 妙+行业卡当日停机用户欠费金额："+((Vector)result_3.elementAt(0)).get(0).toString()+"元，当月停机用户欠费金额："
	    +((Vector)result_3.elementAt(0)).get(1).toString()+"元，所有停机用户欠费金额："+((Vector)result_3.elementAt(0)).get(2).toString()+"元。";
	    
	    buffer+= "<br>(备注：妙+行业卡欠费数据按欠费总金额的40%统计)<br><hr style=\"height:2px;color:red\" /><br>";
	    
	    
	    
	    
	    
	    // 添加在售产品统计
	    
	    
	    try
	    {
	      Statement stmt=conn.createStatement();
	        

	 		 strsql_1= "select (select f.remark_str2 from  tf_b_gatherfeewxp f where f.stat_day=to_char(sysdate-1,'yyyymmdd') and f.stat_type='2' and f.stat_name=b.service_favour_id ) product_name,             "+
	 					"nvl(count(distinct  b.acc_nbr),0) all_kaika,                                                                                                                                             "+
	 					"nvl(count(distinct case when b.state not in('2HM') and to_char(b.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end),0) yesday_kh_jihuo,                 "+
	 					"nvl(count(distinct case when (b.state not in('2HM') and to_char(b.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(b.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then b.acc_nbr else null end),0) month_kh_jihuo,                  "+
	 					"nvl(count(distinct case when b.state not in('2HM')  and  to_char(b.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then b.acc_nbr else null end),0) all_kh_jihuo,              "+
	 					"nvl(count(distinct (case when b.state  in('2HD') and to_char(b.state_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd')  then b.acc_nbr else null end)),0) yesday_kh_stop,                  "+
	 					"nvl(count(distinct (case when (b.state  in('2HD') and to_char(b.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(b.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd'))  then b.acc_nbr else null end)),0) month_kh_stop,                       "+
	 					"nvl(count(distinct (case when b.state not in('2HM','2HA') and to_char(b.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end)),0) all_kh_stop,            "+
	 					"(select c.user_nums from  tf_b_gatherfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.stat_type='2' and c.stat_name=b.service_favour_id ) month_kh_czusers,                 "+
	 					"(select d.stat_value-d.remark_num1 from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and d.stat_type='2' and d.stat_name=b.service_favour_id ) nowday_fee,        "+
	 					"(select e.stat_value from  tf_b_gatherfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.stat_type='2' and e.stat_name=b.service_favour_id ) month_fee                        "+
	 					"from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'                                                                                    "+
	 					"and b.service_favour_id not in(1300840,1300582,1350274,1300679,1300701,1350036,1300640,1300639,1300720,1300721) group by b.service_favour_id          "+
	 					"union all                                                                                                                                             "+
	 					"select '2',nvl(sum(count(distinct  b.acc_nbr)),0) all_kaika,                                                                                          "+
	 					"nvl(sum(count(distinct case when b.state not in('2HM') and to_char(b.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end)),0) yesday_kh_jihuo,             "+
	 					"nvl(sum(count(distinct case when (b.state not in('2HM') and to_char(b.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(b.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then b.acc_nbr else null end)),0) month_kh_jihuo,               "+
	 					"nvl(sum(count(distinct case when b.state not in('2HM')  and  to_char(b.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then b.acc_nbr else null end)),0) all_kh_jihuo,          "+
	 					"nvl(sum(count(distinct (case when b.state  in('2HD') and  to_char(b.state_date,'yyyymmdd') =to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end))),0)  yesday_kh_stop,            "+
	 					"nvl(sum(count(distinct (case when (b.state  in('2HD') and to_char(b.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(b.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then b.acc_nbr else null end))),0)  month_kh_stop,                  "+
	 					"nvl(sum(count(distinct (case when b.state not in('2HM','2HA') and to_char(b.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd') then b.acc_nbr else null end))),0) all_kh_stop,        "+
	 					"to_char(sum((select c.user_nums from  tf_b_gatherfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.stat_type='2' and c.stat_name=b.service_favour_id ))) month_kh_czusers,    "+
	 					"nvl(sum((select d.stat_value-d.remark_num1 from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and d.stat_type='2' and d.stat_name=b.service_favour_id )),0) nowday_fee,       "+
	 					"nvl(sum((select e.stat_value from  tf_b_gatherfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.stat_type='2' and e.stat_name=b.service_favour_id )),0) month_fee                       "+
	 					"from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'                                                                                      "+
	 					"and b.service_favour_id not in(1300840,1300582,1350274,1300679,1300701,1350036,1300640,1300639,1300720,1300721) group by b.service_favour_id";                        

	 	  
	         
	      
	      System.out.println(formatter.format(new Date())+"------>strsql_1="+strsql_1);
	      ResultSet rs=stmt.executeQuery(strsql_1);
	      while(rs.next())
	      {
	    	 
	         String col_product_name     =(""+rs.getString(1)).trim();                   
	         String col_all_kaika         =(""+rs.getString(2)).trim();   
	         String col_yesday_kh_jihuo   =(""+rs.getString(3)).trim();  
	         String col_month_kh_jihuo    =(""+rs.getString(4)).trim();  
	         String col_all_kh_jihuo      =(""+rs.getString(5)).trim(); 
	         String col_yesday_kh_stop     =(""+rs.getString(6)).trim();
	         String col_month_kh_stop     =(""+rs.getString(7)).trim();
	         String col_all_kh_stop       =(""+rs.getString(8)).trim();
	         String col_month_kh_czusers  =(""+rs.getString(9)).trim();
	         String col_nowday_fee        =(""+rs.getString(10)).trim();
	         String col_month_fee         =(""+rs.getString(11)).trim();      
	         
	         
	                  
	         Vector v_row=new Vector();
	         v_row.add(col_product_name);   
	         v_row.add(col_all_kaika);
	         v_row.add(col_yesday_kh_jihuo);
	         v_row.add(col_month_kh_jihuo);
	         v_row.add(col_all_kh_jihuo);
	         v_row.add(col_yesday_kh_stop);
	         v_row.add(col_month_kh_stop);
	         v_row.add(col_all_kh_stop);	         
	         v_row.add(col_nowday_fee);
	         v_row.add(col_month_fee);
	         v_row.add(col_month_kh_czusers);
	         
	         result_1.add(v_row);
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
	    
	    
	    
	    buffer+= "<p class=\"p1\">按产品统计日报【截止"+reStr+"  24点】</p>";
	    
		 buffer+= "<br><table class=\"Tab\">";
		 buffer+="<tr style=\"font-size:11pt;font-weight:bold\"><td>产品名称</td>";
		 buffer+="<td>总开卡数</td><td>当日激活</td><td>当月激活</td><td>总激活数</td>";
		 buffer+="<td>当日停机</td><td>当月停机</td><td>总停机数</td><td>当日账单</td>";
		 buffer+="<td>当月账单</td><td>当月出账用户</td></tr>";
		
	    for(int i=0;i<result_1.size();i++)
	    {
	    	v_get=(Vector)result_1.elementAt(i);
	    	
	    	
	    	
	    	
	    	if(i==result_1.size()-1)
	    	{
	    		
	    		buffer+="<tr style=\"font-size:11pt;font-weight:bold;color:red\">";
	    		
	
	    		  buffer+="<td>汇总</td>";
	    		   
	    		  for(int k=1;k<v_get.size();k++){
	    			  
	    			buffer+="<td>";  
	   	        	
	   	        	buffer+=(String)v_get.get(k);
	   	        	
	   	        	buffer+="</td>";
	   	        	
	   	        }
	    		
	    	}else{
	    		
	    		buffer+="<tr style=\"font-size:10pt\">";
	    	
	        for(int j=0;j<v_get.size();j++){
	        	
	        		buffer+="<td>";
	        	
	        	buffer+=(String)v_get.get(j);
	        	
	        	buffer+="</td>";
	        	
	        }
	        
	        buffer+="</tr>";
	    	
	    	}
	    
	    }
	    
	    buffer+="</table><br>";
	    
	    
	    // 添加日激活量和日停机量图片
	    
	    buffer+= "<br><img src=\"cid:a\"><br><br><br>";
    
	    
	    buffer+="<p class=\"p2\"> 备注：此统计数据截止"+reStr+" 24点，下面是报表相关字段解释：<br>总开卡数：在联通测已经初始化的用户数量；";
	    buffer+="<br>当日激活：当日实名激活返单的用户数量，即当日卖出量；<br>当月激活：当月实名激活返单的用户数量，即当月卖出量；<br>总激活：总共实名激活返单的用户数量，即已经卖出量；<br>";
	    buffer+="当日停机：当月因欠费停机的用户数量；<br>当月停机：当月因欠费停机的用户数量；<br>总停机：往月因欠费停机的用户数量；<br>";
	    buffer+="当日充值：二次及多次缴费用户当日现金缴费金额；<br>当日充值用户：二次及多次缴费用户当日现金缴费用户数；<br>当月充值：二次及多次缴费用户当月现金缴费金额；<br>";
	    buffer+="当日账单：当日产生的用户账单金额，行业卡按40%统计(单位：元)；<br>当月账单：当月产生的用户账单金额，行业卡按40%统计(单位：元)；<br>当月出账用户数：当月产生账单的用户数量；<br></p>";
	    

	    System.out.println(formatter.format(new Date())+"------>buffer===="+buffer.toString());
	    
	    
	    
	    /* 设置文件基础路径*/
	     
		 File fileRoot=new File(ClassLoader.getSystemResource("").getPath());	
		 System.out.println(formatter.format(new Date())+"------>path2="+fileRoot);
	        
	     String filePath = fileRoot+"/excel/"; // 此路径设置windows和linux通用
	     
	     ///String filePath = fileRoot+"\\excel\\";   ///windows路径          
	     
	        try
		    {
		      Statement stmt=conn.createStatement();
		      
		 	  strsql_5= "select f.stat_type,substr(f.stat_date,-4,4),f.stat_num from                                                                           "+
		 			    "(select '激活量' stat_type,to_char(b.create_date,'yyyymmdd') stat_date,count(distinct b.acc_nbr) stat_num                     "+
		 			    "from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'  and b.state not in('2HM')                              "+
		 			    "and to_char(b.create_date,'yyyymmdd')>=to_char(sysdate-15,'yyyymmdd') and to_char(b.create_date,'yyyymmdd')<to_char(sysdate,'yyyymmdd')  "+
		 			    "group by to_char(b.create_date,'yyyymmdd')            "+
		 			    " union all                                                                                                                  "+
		 			    "select  '停机量' stat_type,to_char(b.state_date,'yyyymmdd') stat_date,count(distinct b.acc_nbr) stat_num                      "+
		 			    "from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'  and b.state  in('2HD')                                 "+
		 			    "and to_char(b.state_date,'yyyymmdd')>=to_char(sysdate-15,'yyyymmdd') and to_char(b.state_date,'yyyymmdd')<to_char(sysdate,'yyyymmdd')  "+
		 			    "group by to_char(b.state_date,'yyyymmdd')) f order by f.stat_type,f.stat_date ";      
		
		         
		      
		      System.out.println(formatter.format(new Date())+"------>strsql_5="+strsql_5);

		      
		      ResultSet rs=stmt.executeQuery(strsql_5);
		      while(rs.next())
		      {
		    	 
		         String col_stat_type       =(""+rs.getString(1)).trim();    
		         String col_stat_date      =(""+rs.getString(2)).trim();
		         String col_stat_value     = (""+rs.getString(3)).trim();

	                  
		         Vector v_row=new Vector();
		         v_row.add(col_stat_type);
		         v_row.add(col_stat_date);
		         v_row.add(col_stat_value);
		         
		         result_5.add(v_row);
		      }
		      rs.close();
		      rs=null;
		      stmt.close();
		      stmt=null;
		      

		    }
		    catch(Exception ex)
		    {
		      System.out.println("------->in getting active or stop user fail,"+ex);
		    }
	        


	        
		    
		    DefaultCategoryDataset activeOrStopData = new DefaultCategoryDataset();
		     
		    
		    for(int i=0;i<result_5.size();i++)
		    {
		    	v_get=(Vector)result_5.elementAt(i);
		    	       
		    		activeOrStopData.addValue(Float.parseFloat((v_get.get(2)).toString()),(v_get.get(0)).toString(),(v_get.get(1)).toString());
		        	        	        
		    }
		    
		    
		     String filePathNameAtAndStop="activeAndStopUser.jpg";
		       
		    
		 
		    saveDateToChart("日激活总量和日停机总量最近15天走势","日期","用户(个)",filePath,filePathNameAtAndStop,activeOrStopData);
		    
	        
	        // if(m==0) return;
	          
	     
		    
		    
	   //统计行业卡欠费明细，生成excel文档 
		    
	   /*	       
	     String filePathNameCredit;
	     String fileNameCredit="行业卡欠费明细@"+reStr+".xls";

		 File filep = new File(filePath);	 
		 if(!filep.exists()) filep.mkdir();
		 
		 filePathNameCredit =filePath+fileNameCredit;			
		 System.out.println(formatter.format(new Date())+"------>path3="+filePathNameCredit);
			
	     File fileCredit=new File(filePathNameCredit);	
		 if(fileCredit.exists()&&fileCredit.isFile()){
			 fileCredit.delete();
		 }
					
	    try
	    {
	      Statement stmt=conn.createStatement();
	      
	 	  strsql_3="select (Select a.city_name from  bs_city_id_t a Where a.city_level=3  And a.city_code=t.city_code), "+
                    "t.service_id,t.rest_fee,to_char(t.state_date,'yyyy/MM/dd  HH24:mm:ss') from tf_b_2hdproduct t "
                    + "where t.prod_code=1370080 and t.rest_fee<0 and to_char(t.state_date,'yyyymm')= to_char(sysdate-1,'yyyymm') order by t.state_date desc ";      
	      
	      System.out.println(formatter.format(new Date())+"------>strsql_3="+strsql_3);

	      
	      ResultSet rs=stmt.executeQuery(strsql_3);
	      while(rs.next())
	      {
	    	 
	         String col_city_name       =(""+rs.getString(1)).trim();    
	         String col_service_id      =(""+rs.getString(2)).trim();
	         double col_rest_fee        = rs.getDouble(3);
	         String col_state_date      =(""+rs.getString(4)).trim();
                  
	         Vector v_row=new Vector();
	         v_row.add(col_city_name);
	         v_row.add(col_service_id);
	         v_row.add(col_rest_fee);
	         v_row.add(col_state_date);
	         
	         result_3.add(v_row);
	      }
	      rs.close();
	      rs=null;
	      stmt.close();
	      stmt=null;
	      

	    }
	    catch(Exception ex)
	    {
	      System.out.println("------->in getting hangyeka 2hd user fail,"+ex);
	    }
	    
	    
        int datarows = result_3.size();
        int datacols=((Vector)result_3.elementAt(0)).size();
	    
	    
        String[][] data = new String[datarows+1][datacols];
        
        data[0][0]="地市";
        data[0][1]="号码";
        data[0][2]="欠费(元)";
        data[0][3]="停机时间";
        
        for(int i=0;i<result_3.size();i++)
	    {
	    	v_get=(Vector)result_3.elementAt(i);
	    	
	        for(int j=0;j<v_get.size();j++){
	        	
	                data[i+1][j] =v_get.get(j).toString();
	        		
	        	if(i<20)  System.out.println(formatter.format(new Date())+"------>data="+data[i+1][j]);      	
	        	
	        }        
	    }        
        
        saveDateToExcel(datarows+1,datacols,data,filePathNameCredit);    //  保存为excel文件
       
*/	 
		 
		 
	    
	    try
	    {
	      Statement stmt=conn.createStatement();
	      
	 	  strsql_4="select t.info_value,t.info_desc from tf_b_funtalkinfo t where t.info_type='3' and rownum=1"; 
	
	         
	      
	      System.out.println(formatter.format(new Date())+"------>strsql_4="+strsql_4);

	      
	      ResultSet rs=stmt.executeQuery(strsql_4);
	      while(rs.next())
	      {
	    	 
	         String col_user_name          =(""+rs.getString(1)).trim();    
	         String col_user_password      =(""+rs.getString(2)).trim();
                  
	         Vector v_row=new Vector();
	         v_row.add(col_user_name);
	         v_row.add(col_user_password);
	         
	         result_4.add(v_row);
	      }
	      rs.close();
	      rs=null;
	      stmt.close();
	      stmt=null;
	      

	    }
	    catch(Exception ex)
	    {
	      System.out.println("------->in getting from user fail,"+ex);
	    }
	    

	  	final String username =((Vector)result_4.elementAt(0)).get(0).toString();
	    final String password =((Vector)result_4.elementAt(0)).get(1).toString();  
	    
	    System.out.println(formatter.format(new Date())+"------>username="+username+"----password="+password);
      
     
	  Properties props = new Properties();
	
	  
	  props.put("mail.smtp.host", "mail.funtalk.cn");
	  props.put("mail.smtp.auth", "true");

	  Session session = Session.getDefaultInstance(props, new Authenticator() {

	   protected PasswordAuthentication getPasswordAuthentication() {  return new PasswordAuthentication(username,password); }
	   
	  });
	  
	  
	  //String[] to = {"wangxp1@funtalk.cn"};
	  
	  // String[] cc = {"43198341@qq.com"};
	  
	  
	   String[] to = {"liuyl4@funtalk.cn"};
	  
	  
	    String[] cc = {"liyao1@funtalk.cn","wangxp1@funtalk.cn","liuyangzs@funtalk.cn","zhangxiaols@funtalk.cn","xiecx@funtalk.cn","lilei5@funtalk.cn",
	                   "lijun@funtalk.cn","duzt@funtalk.cn","qifei@funtalk.cn","fengsg@funtalk.cn","shaoys1@funtalk.cn","songlj1@funtalk.cn","libing1@funtalk.cn","gengrui@funtalk.cn"};
	  
	  String from = "wangxp1@funtalk.cn";
	  String subject = "行业卡统计日报@"+reStr1;
	  Message msg = new MimeMessage(session);
	  try {
	   msg.setFrom(new InternetAddress(from));
	   
	   
	   //设置抄送人员
	   InternetAddress[] addressCc = new InternetAddress[cc.length];
	   for (int i = 0; i < cc.length; i++)
	   {
		   addressCc[i] = new InternetAddress(cc[i]);
	   }
	   msg.setRecipients(Message.RecipientType.CC, addressCc); 
	   
	   
	   ///设置发送人员
	   InternetAddress[] addressTo = new InternetAddress[to.length];
	   
	   for (int i = 0; i < to.length; i++)
	   { addressTo[i] = new InternetAddress(to[i]);  }
	   
	   msg.setRecipients(Message.RecipientType.TO, addressTo); 
	   // msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to)); // 发送单个用户
	   
	  /* InternetAddress[] toListms = new InternetAddress().parse(to); 
	   msg.setRecipients(RecipientType.TO, toListms); 
	   
	   InternetAddress[] ccListms = new InternetAddress().parse(cc); 
	   msg.setRecipients(RecipientType.CC, ccListms);    
	   */
	   msg.setSubject(subject);
 


	   // Create a multipar message
	   Multipart multipart = new MimeMultipart("mixed");

	   
	   //ADD  ATTACHMENT EXCEL FILE
/*	   MimeBodyPart fileBodyPart = new MimeBodyPart();
	   String sourceFile = filePathNameCredit;
	   DataSource source = new FileDataSource(sourceFile);
	   fileBodyPart.setDataHandler(new DataHandler(source));
	   fileBodyPart.setFileName(MimeUtility.encodeWord(fileNameCredit));
	   multipart.addBodyPart(fileBodyPart);*/
	   
	   
	   //ADD PICS
	   MimeBodyPart bodyImg1 = new MimeBodyPart();
       File  sourceImg1=new File(filePath+filePathNameAtAndStop);     
       //byte[] imgbytes = getBytesFromFile(new File(filePath+monthFileName));
       //DataSource picds = new ByteArrayDataSource(imgbytes,"application/octet-stream");
       
       DataHandler dh1 = new DataHandler(new FileDataSource(sourceImg1));//图片路径 
       bodyImg1.setDataHandler(dh1); 
       bodyImg1.setFileName(filePathNameAtAndStop);     
       bodyImg1.setContentID("a"); // 创建图片的一个表示用于显示在邮件中显示 
	   multipart.addBodyPart(bodyImg1);
	   
	   
	   // ADD BODY TEXT
	   BodyPart messageBodyPart = new MimeBodyPart();
	   messageBodyPart.setContent(buffer, "text/html;charset=GBK"); // 网页格式
	   multipart.addBodyPart(messageBodyPart);
	   
 
	   // Send the complete message parts
	   msg.setContent(multipart);
	   Transport transport = session.getTransport("smtp");
	   transport.send(msg);
	   System.out.println(formatter.format(new Date())+"------>E-mail have been sent successfully!");
	  }
	  catch(Exception exc) {
	   System.out.println(exc);
	  }
	  
	  
	  
  	if(conn!=null){ 
  		
  		try{
  			
		conn.close();
		
  		 }catch(Exception e){
  			
  			System.out.println("------->in closing conn fail------>"+e);
  			
  		}		
  	}
}
	
	
	
	
	
	public static void  saveDateToExcel(int datarows, int datacols, String[][] data, String filePathName){
		
        int baseNum = 65535; //每个sheet的记录数，可以修改
        int sheetNum = 0;
        if ((datarows-1)%baseNum == 0)
        {
            sheetNum = (datarows-1)/baseNum;
        }
        else
        {
            sheetNum = (datarows-1)/baseNum+1;
        }

        try
        {
            HSSFWorkbook workbook = new HSSFWorkbook();
            int rowid = 0;
            for (int n=0;n<sheetNum;n++)
            {
                rowid = n*baseNum+1;
                HSSFSheet sheet = workbook.createSheet("sheet"+(n+1));
                
                sheet.setColumnWidth(0, 12*256);
                sheet.setColumnWidth(1, 17*256);
                sheet.setColumnWidth(2, 12*256);
                sheet.setColumnWidth(3, 20*256);
                
                sheet.setDefaultColumnWidth(10*256);
                
                

                
                /// 设置字体
                HSSFFont font2 = workbook.createFont();
                font2.setFontHeightInPoints( (short) 10);
                font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                font2.setFontName("微软雅黑");
                font2.setColor(HSSFFont.COLOR_RED);
                
                 /// 设置样式
                HSSFCellStyle cellStyle_head = workbook.createCellStyle();
                cellStyle_head.setFont(font2);
                cellStyle_head.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                
                
                
                
                HSSFFont font4 = workbook.createFont();
                font4.setFontHeightInPoints( (short) 9);
                font4.setFontName("微软雅黑");
                font4.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                
                HSSFCellStyle cellStyleA = workbook.createCellStyle(); // data
                cellStyleA.setFont(font4);
                cellStyleA.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                
                HSSFCellStyle cellStyleLocal = cellStyleA; // data
                

                // cellStyle.setDataFormat(HSSFDataFormat.getFormat("(#,##0.00_);[Red](#,##0.00)"));
                //cellStyle_data.setDataFormat(HSSFDataFormat.getBuiltinFormat("(#,##0.00_);[Red](#,##0.00)"));

                HSSFRow row;
                HSSFCell cell;

                // data head
                row = sheet.createRow( (short) 0);
                row.setHeightInPoints(20);
                
                for (int j = 0; j < datacols; j++)
                {
                    cell = row.createCell(j);
                    cell.setCellStyle(cellStyle_head);
                    cell.setCellValue(data[0][j]);
                    
                
                }
                
                // data left & data
                for (int i = rowid; i < rowid + baseNum; i++)
                {
                	
                    row = sheet.createRow( (int)(i-n*baseNum));
                    row.setHeightInPoints(15);
                    
                    if (i >= datarows) break;
                    
                    for (int j = 0; j < datacols; j++)
                    {
                    	
                        cell = row.createCell(j);
                        

                        if(j==2){
                        	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        	cell.setCellStyle(cellStyleLocal);
                            cell.setCellValue(Double.parseDouble(data[i][j]));
                        }else{
                        	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    	   cell.setCellStyle(cellStyleLocal);
                           cell.setCellValue(data[i][j]);
                        }
                    }   
                }
             
            }
            
            
            
            OutputStream  outstream=new FileOutputStream(filePathName);
            workbook.write(outstream);
            outstream.flush();
            outstream.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

        }
		
		
		
		
		
	}

}
