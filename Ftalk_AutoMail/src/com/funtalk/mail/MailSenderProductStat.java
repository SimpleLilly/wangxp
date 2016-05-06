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
		    	

	            //����ͼ�����
	            JFreeChart chart = ChartFactory.createLineChart("",// ������Ŀ���ַ�������
	                          "", // ����
	                          "", // ����
	                          linedataset, // ������ݼ�
	                          PlotOrientation.VERTICAL,
	                          true, 
	                          false, 
	                          false 
	                          );

	            Font font = new Font("΢���ź�", Font.BOLD, 14);   
	            Font font1 = new Font("΢���ź�", Font.BOLD, 12); 
	            

	            TextTitle title = new TextTitle(lableTitle);     
	            title.setFont(font);     
	            chart.setTitle(title); 
	            
	            //chart.getLegend().setVisible(false); ///  ����ʾ ����ϵ������       
	            
	            LegendTitle legend = chart.getLegend(); // ���ñ���ͼ��ʾ�������Ǹ�һ��y��value��    
	            
	            legend.setItemFont(font1);

	            // ����ͼ��
	            CategoryPlot plot = chart.getCategoryPlot();
	            // ͼ�����Բ���
	            plot.setBackgroundPaint(Color.white);
	            plot.setDomainGridlinesVisible(true);  //���ñ����������Ƿ�ɼ�
	            plot.setDomainGridlinePaint(Color.BLACK); //���ñ�����������ɫ
	            plot.setRangeGridlinePaint(Color.GRAY);
	            plot.setNoDataMessage("û������");//û������ʱ��ʾ������˵���� 
	            // ���������Բ���
	            
	            // ���� x
	            CategoryAxis domainAxis = plot.getDomainAxis();
	            domainAxis.setLabelFont(font);  
	            domainAxis.setLabel(lableX);
	            
	            // ���� Y
	            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	            rangeAxis.setLabelFont(font);
	            rangeAxis.setLabel(lableY);
	        
	            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	            rangeAxis.setAutoRangeIncludesZero(true);
	            rangeAxis.setUpperMargin(0.20);
	            rangeAxis.setLabelAngle(Math.PI / 2.0); 
	            rangeAxis.setAutoRange(false);

	            // ������Ⱦ���� ��Ҫ�Ƕ�����������
	            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	            renderer.setBaseItemLabelsVisible(true);
	            
	            renderer.setSeriesPaint(0, Color.black);    //�������ߵ���ɫ
	            renderer.setBaseShapesFilled(true);
	    
	            renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());  
	               
	            /*XYPlot  ��Ӧ������ �� StandardXYItemLabelGenerator */
	            

	            renderer.setBaseItemLabelFont(font1);  //������ʾ�۵�������״
	            plot.setRenderer(renderer);
	            
	            
	            
	   		   File filep = new File(filePath);
	   		   if(!filep.exists()) filep.mkdir();
	   		 
	   		   String file=filePath+picName;
	   			
	   		   System.out.println((new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss")).format(new Date())+"------>making file="+file);
	   			
	   	       // �����ļ������
	   	       File file_jpg=new File(file);	
	   		   if(file_jpg.exists()&&file_jpg.isFile()){
	   			 file_jpg.delete();
	   			}
	            
				
	          // ������ĸ������
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
	        rightNow.setTime(new Date());  // ����ǰʱ����Ϊ������ʼ��
	        //rightNow.add(Calendar.YEAR,-1);//���ڼ�1��
	        //rightNow.add(Calendar.MONTH,3);//���ڼ�3����
	        rightNow.add(Calendar.DAY_OF_YEAR,-1);//���ڼ�1��
	        Date dt1=rightNow.getTime();
	        String reStr = formatter1.format(dt1);
	        
	        String reStr1 = formatter3.format(dt1);
	        
	        System.out.println("--------ͳ������------>"+reStr);
	        
	        int m=0;

				
		
       try{
    	  
	    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	    String connstr="jdbc:oracle:thin:@172.31.8.23:1521:acctdb1";
	    conn= DriverManager.getConnection(connstr, "unitele","bss_bill_xxp1");  
	
	     System.out.println("���ݿ����ӳɹ���");
	  
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
	    buffer+=".Tab{ border-collapse:collapse; width:95%;text-align:center;font-family: ΢���ź�}";
	    buffer+=".Tab td{ border:solid 1px #009191;word-break: keep-all;white-space:nowrap;}";
	    buffer+=".p1{ border-collapse:collapse;font-size:11pt;font-weight:bold;font-family: ΢���ź�}";
	    buffer+=".p2{ border-collapse:collapse;font-size:10pt;font-weight:bold;font-family: ΢���ź�}";
	    buffer+="</style>";
	    
	    
	   
	    
	    /// �����+��ҵ�������л��ܱ���  2016.01.07
	    

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
    
    
	    buffer+= "<br><p class=\"p1\">��+��ҵ��������ͳ���ձ�����ֹ"+reStr+"  24�㡿</p>";
	    
	    buffer+= "<br><table class=\"Tab\">";
		
		buffer+="<tr style=\"font-size:11pt;font-weight:bold\"><td>��Ʒ����</td>";
		buffer+="<td>ʡ��</td><td>����</td><td>�ܿ���</td>";
		buffer+="<td>���ռ���</td><td>���¼���</td><td>�ܼ���</td><td>����ͣ��</td><td>����ͣ��</td>";
		buffer+="<td>��ͣ��</td><td>���ճ�ֵ(Ԫ)</td><td>���ճ�ֵ�û�</td><td>���³�ֵ(Ԫ)</td><td>���³�ֵ�û�</td><td>�����˵�(Ԫ)</td><td>�����˵�(Ԫ)</td><td>���³����û�</td>";

		
	    for(int i=0;i<result_2.size();i++)
	    {
	    	v_get=(Vector)result_2.elementAt(i);
	    	
	    	
	    	
	      	if(i==result_2.size()-1)
	    	{
	    		
	    		buffer+="<tr style=\"font-size:11pt;font-weight:bold;color:red\">";
	    		
	
	    		  buffer+="<td>����</td><td></td><td></td>";
	    		   
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
	    
	    
	    
	    
	    buffer+="<br><p class=\"p2\"> ��+��ҵ������ͣ���û�Ƿ�ѽ�"+((Vector)result_3.elementAt(0)).get(0).toString()+"Ԫ������ͣ���û�Ƿ�ѽ�"
	    +((Vector)result_3.elementAt(0)).get(1).toString()+"Ԫ������ͣ���û�Ƿ�ѽ�"+((Vector)result_3.elementAt(0)).get(2).toString()+"Ԫ��";
	    
	    buffer+= "<br>(��ע����+��ҵ��Ƿ�����ݰ�Ƿ���ܽ���40%ͳ��)<br><hr style=\"height:2px;color:red\" /><br>";
	    
	    
	    
	    
	    
	    // ������۲�Ʒͳ��
	    
	    
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
	    
	    
	    
	    buffer+= "<p class=\"p1\">����Ʒͳ���ձ�����ֹ"+reStr+"  24�㡿</p>";
	    
		 buffer+= "<br><table class=\"Tab\">";
		 buffer+="<tr style=\"font-size:11pt;font-weight:bold\"><td>��Ʒ����</td>";
		 buffer+="<td>�ܿ�����</td><td>���ռ���</td><td>���¼���</td><td>�ܼ�����</td>";
		 buffer+="<td>����ͣ��</td><td>����ͣ��</td><td>��ͣ����</td><td>�����˵�</td>";
		 buffer+="<td>�����˵�</td><td>���³����û�</td></tr>";
		
	    for(int i=0;i<result_1.size();i++)
	    {
	    	v_get=(Vector)result_1.elementAt(i);
	    	
	    	
	    	
	    	
	    	if(i==result_1.size()-1)
	    	{
	    		
	    		buffer+="<tr style=\"font-size:11pt;font-weight:bold;color:red\">";
	    		
	
	    		  buffer+="<td>����</td>";
	    		   
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
	    
	    
	    // ����ռ���������ͣ����ͼƬ
	    
	    buffer+= "<br><img src=\"cid:a\"><br><br><br>";
    
	    
	    buffer+="<p class=\"p2\"> ��ע����ͳ�����ݽ�ֹ"+reStr+" 24�㣬�����Ǳ�������ֶν��ͣ�<br>�ܿ�����������ͨ���Ѿ���ʼ�����û�������";
	    buffer+="<br>���ռ������ʵ����������û���������������������<br>���¼������ʵ����������û���������������������<br>�ܼ���ܹ�ʵ����������û����������Ѿ���������<br>";
	    buffer+="����ͣ����������Ƿ��ͣ�����û�������<br>����ͣ����������Ƿ��ͣ�����û�������<br>��ͣ����������Ƿ��ͣ�����û�������<br>";
	    buffer+="���ճ�ֵ�����μ���νɷ��û������ֽ�ɷѽ�<br>���ճ�ֵ�û������μ���νɷ��û������ֽ�ɷ��û�����<br>���³�ֵ�����μ���νɷ��û������ֽ�ɷѽ�<br>";
	    buffer+="�����˵������ղ������û��˵�����ҵ����40%ͳ��(��λ��Ԫ)��<br>�����˵������²������û��˵�����ҵ����40%ͳ��(��λ��Ԫ)��<br>���³����û��������²����˵����û�������<br></p>";
	    

	    System.out.println(formatter.format(new Date())+"------>buffer===="+buffer.toString());
	    
	    
	    
	    /* �����ļ�����·��*/
	     
		 File fileRoot=new File(ClassLoader.getSystemResource("").getPath());	
		 System.out.println(formatter.format(new Date())+"------>path2="+fileRoot);
	        
	     String filePath = fileRoot+"/excel/"; // ��·������windows��linuxͨ��
	     
	     ///String filePath = fileRoot+"\\excel\\";   ///windows·��          
	     
	        try
		    {
		      Statement stmt=conn.createStatement();
		      
		 	  strsql_5= "select f.stat_type,substr(f.stat_date,-4,4),f.stat_num from                                                                           "+
		 			    "(select '������' stat_type,to_char(b.create_date,'yyyymmdd') stat_date,count(distinct b.acc_nbr) stat_num                     "+
		 			    "from  serv b where to_char(b.completed_date,'yyyymmdd')>'20140717'  and b.state not in('2HM')                              "+
		 			    "and to_char(b.create_date,'yyyymmdd')>=to_char(sysdate-15,'yyyymmdd') and to_char(b.create_date,'yyyymmdd')<to_char(sysdate,'yyyymmdd')  "+
		 			    "group by to_char(b.create_date,'yyyymmdd')            "+
		 			    " union all                                                                                                                  "+
		 			    "select  'ͣ����' stat_type,to_char(b.state_date,'yyyymmdd') stat_date,count(distinct b.acc_nbr) stat_num                      "+
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
		       
		    
		 
		    saveDateToChart("�ռ�����������ͣ���������15������","����","�û�(��)",filePath,filePathNameAtAndStop,activeOrStopData);
		    
	        
	        // if(m==0) return;
	          
	     
		    
		    
	   //ͳ����ҵ��Ƿ����ϸ������excel�ĵ� 
		    
	   /*	       
	     String filePathNameCredit;
	     String fileNameCredit="��ҵ��Ƿ����ϸ@"+reStr+".xls";

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
        
        data[0][0]="����";
        data[0][1]="����";
        data[0][2]="Ƿ��(Ԫ)";
        data[0][3]="ͣ��ʱ��";
        
        for(int i=0;i<result_3.size();i++)
	    {
	    	v_get=(Vector)result_3.elementAt(i);
	    	
	        for(int j=0;j<v_get.size();j++){
	        	
	                data[i+1][j] =v_get.get(j).toString();
	        		
	        	if(i<20)  System.out.println(formatter.format(new Date())+"------>data="+data[i+1][j]);      	
	        	
	        }        
	    }        
        
        saveDateToExcel(datarows+1,datacols,data,filePathNameCredit);    //  ����Ϊexcel�ļ�
       
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
	  String subject = "��ҵ��ͳ���ձ�@"+reStr1;
	  Message msg = new MimeMessage(session);
	  try {
	   msg.setFrom(new InternetAddress(from));
	   
	   
	   //���ó�����Ա
	   InternetAddress[] addressCc = new InternetAddress[cc.length];
	   for (int i = 0; i < cc.length; i++)
	   {
		   addressCc[i] = new InternetAddress(cc[i]);
	   }
	   msg.setRecipients(Message.RecipientType.CC, addressCc); 
	   
	   
	   ///���÷�����Ա
	   InternetAddress[] addressTo = new InternetAddress[to.length];
	   
	   for (int i = 0; i < to.length; i++)
	   { addressTo[i] = new InternetAddress(to[i]);  }
	   
	   msg.setRecipients(Message.RecipientType.TO, addressTo); 
	   // msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to)); // ���͵����û�
	   
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
       
       DataHandler dh1 = new DataHandler(new FileDataSource(sourceImg1));//ͼƬ·�� 
       bodyImg1.setDataHandler(dh1); 
       bodyImg1.setFileName(filePathNameAtAndStop);     
       bodyImg1.setContentID("a"); // ����ͼƬ��һ����ʾ������ʾ���ʼ�����ʾ 
	   multipart.addBodyPart(bodyImg1);
	   
	   
	   // ADD BODY TEXT
	   BodyPart messageBodyPart = new MimeBodyPart();
	   messageBodyPart.setContent(buffer, "text/html;charset=GBK"); // ��ҳ��ʽ
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
		
        int baseNum = 65535; //ÿ��sheet�ļ�¼���������޸�
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
                
                

                
                /// ��������
                HSSFFont font2 = workbook.createFont();
                font2.setFontHeightInPoints( (short) 10);
                font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                font2.setFontName("΢���ź�");
                font2.setColor(HSSFFont.COLOR_RED);
                
                 /// ������ʽ
                HSSFCellStyle cellStyle_head = workbook.createCellStyle();
                cellStyle_head.setFont(font2);
                cellStyle_head.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                
                
                
                
                HSSFFont font4 = workbook.createFont();
                font4.setFontHeightInPoints( (short) 9);
                font4.setFontName("΢���ź�");
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
