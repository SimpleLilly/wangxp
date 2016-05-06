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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.TextAnchor;

import com.sun.istack.internal.ByteArrayDataSource;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class MailSender {
	
	
	
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
	            
	            chart.getLegend().setVisible(false); ///  ����ʾ ����ϵ������ (�ս���)           
	           
	      		///System.out.println(formatter.format(new Date())+"------>chart.Subtitle="+ chart.getLegend(0).visible);


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
	      
	       SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy/MM/dd");
	        
	       SimpleDateFormat formatter2 = new SimpleDateFormat ("dd");
	       
	       SimpleDateFormat formatter3 = new SimpleDateFormat ("yyyy��MM��");

	        Calendar rightNow = Calendar.getInstance();
	        rightNow.setTime(new Date());  // ����ǰʱ����Ϊ������ʼ��
	        //rightNow.add(Calendar.YEAR,-1);//���ڼ�1��
	        //rightNow.add(Calendar.MONTH,3);//���ڼ�3����
	        rightNow.add(Calendar.DAY_OF_YEAR,-1);//���ڼ�1��
	        Date dt1=rightNow.getTime();
	        String reStr = formatter1.format(dt1);
	        System.out.println("--------ͳ������------>"+reStr);
	        
		   //   if(formatter2.format(new Date()).toString().equals("01"))
		   //	 		 return;
	        
	        
			 int m=1;
		 	   

		  
		
       try{
    	  
	    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	    String connstr="jdbc:oracle:thin:@172.31.8.23:1521:acctdb1";
	    conn= DriverManager.getConnection(connstr, "unitele","bss_bill_xxp1");  
	
	     System.out.println("���ݿ����ӳɹ���");
	  
        }catch (Exception e){   e.printStackTrace();  return;}
       
       
       
       
      
      
	    String []str=null;
	    String strsql_1;
	    String strsql_2;
	    String strsql_3;
	    String strsql_4;
	    String strsql_5;

	    Vector result1=new Vector();
	    Vector result2=new Vector();
	    Vector result3=new Vector();
	    Vector result4=new Vector();
	    Vector result5=new Vector();
        Vector v_get=new Vector();
        
        
        

  	     String monthFileName="month_settle.jpg";
  	     String yearFileName="yaer_settle.jpg";
  	     
  	    String  filePathMonthFile="";
  	    String  filePathYearFile="";
  	     
  	     
  		 File fileRoot=new File(ClassLoader.getSystemResource("").getPath());	
		    
  		 System.out.println(formatter.format(new Date())+"------>fileRoot="+fileRoot);      //strsql_1=C:\workspace\Ftalk_AutoMail\bin
  	         
  	     // String filePath = fileRoot+"\\excel\\";  ///windows·��
  	     	     
	     String filePath = fileRoot+"/excel/";  // linux ·��
         
        
        try
	    {
	      Statement stmt=conn.createStatement();
	      
	      
	 	  strsql_4= " select y.remark_num1,substr(x.stat_day,-4,4),trunc(sum(x.all_fee-x.ysday_all_fee),0) from tf_b_userfeewxp x, tf_b_funtalkinfo y "
	 	  		  + " where y.info_type='1' and y.remark_num1 in(1,2) and x.f_no_seg=y.info_value and x.stat_day>=to_char(sysdate-15,'yyyymmdd') "
	 	  		  + "group by y.remark_num1,x.stat_day  order by y.remark_num1,x.stat_day";
	 	  
	 	  
	          
	      System.out.println(formatter.format(new Date())+"------>strsql_4="+strsql_4);
	      ResultSet rs=stmt.executeQuery(strsql_4);
	      
	      while(rs.next())
	      {
	    	 String col_stat_type     =(""+rs.getString(1)).trim(); 
	         String col_stat_day      =(""+rs.getString(2)).trim();    
	         String col_stat_value    =(""+rs.getString(3)).trim();        
	           
	                  
	         Vector v_row=new Vector();
	         v_row.add(col_stat_type);
	         v_row.add(col_stat_day);
	         v_row.add(col_stat_value);

	         result4.add(v_row);
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
	       
	    
	    
	    DefaultCategoryDataset lineMonthdata = new DefaultCategoryDataset();
	    DefaultCategoryDataset lineYeardata  = new DefaultCategoryDataset();
	    
	    String series = "����";  
	    
	    for(int i=0;i<result4.size();i++)
	    {
	    	v_get=(Vector)result4.elementAt(i);
	    
	    	if((v_get.get(0)).equals("1"))
	    		
	    	    lineMonthdata.addValue(Float.parseFloat((v_get.get(2)).toString()), series,(v_get.get(1)).toString());
	    	
	    	else	       
	    		lineYeardata.addValue(Float.parseFloat((v_get.get(2)).toString()), series,(v_get.get(1)).toString());
	        	        	        
	    }
	       
	    
	 
	    saveDateToChart("��ͨ���¿������кŶ��ս����ܽ�����15������","����","����(Ԫ)",filePath,monthFileName,lineMonthdata);
	    saveDateToChart("��ͨ���꿼�����кŶ��ս����ܽ�����15������","����","����(Ԫ)",filePath,yearFileName,lineYeardata);
	    
	    
	    
	    String buffer=" <style type=\"text/css\"> ";	
	    buffer+=".Tab{ border-collapse:collapse; width:95%;text-align:center;font-family: ΢���ź�}";
	    buffer+=".Tab td{ border:solid 1px #009191;word-break: keep-all;white-space:nowrap;}";
	    buffer+=".p1{ border-collapse:collapse;font-size:11pt;font-weight:bold;font-family: ΢���ź�}";
	    buffer+=".p2{ border-collapse:collapse;font-size:10pt;font-weight:bold;font-family: ΢���ź�}";
	    buffer+="</style>";
	    

        
         
        

	    try
	    {
	      Statement stmt=conn.createStatement();
	      
	      
	 	  strsql_1=" select f.prov_name,f.city_name,f.remark_num1,f.info_value,f.all_kh_kaika,f.yesday_kh_jihuo,f.month_kh_jihuo,f.all_kh_jihuo,                                                                                                 "+
	      " f.month_kh_stop,f.all_kh_stop,to_number(f.month_kh_czusers),f.user_fee,f.yes_stel_fee,f.all_stel_fee,round(case when f.all_stel_fee>30000 then 0 else 30000-f.all_stel_fee end,2) dixiao_fee                                         "+
	      " from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                             "+
	      " (select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                     "+
	      " count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                    "+
	      " count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd') )  then m.acc_nbr else null end) month_kh_jihuo,"+
	      " count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                 "+
	      " count( distinct (case when (m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then m.acc_nbr else null end)) month_kh_stop,     "+
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
	      " count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd'))  then m.acc_nbr else null end) month_kh_jihuo, "+
	      " count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                 "+
	      " count( distinct (case when (m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then m.acc_nbr else null end)) month_kh_stop,     "+
	      " count( distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                    "+
	      " (select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                        "+
	      " (select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                               "+
	      " ( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) all_stel_fee,                                                                                                "+
	      " ( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee                                                                                 "+
	      " from (select y.remark_num1,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.region_code,x.completed_date,x.create_date,                                                                                                      "+
	      " x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=1 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                               "+
	      " group by m.remark_num1,m.region_code,m.city_code,m.info_value) f ";
	 	  
	 	  
	         
	      
	      System.out.println(formatter.format(new Date())+"------>strsql_1="+strsql_1);
	      ResultSet rs=stmt.executeQuery(strsql_1);
	      while(rs.next())
	      {
	    	 
	         String col_pro_name     =(""+rs.getString(1)).trim();    
	         String col_city_name    =(""+rs.getString(2)).trim();        
	                 
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
	         v_row.add(col_f_no_seg);       
	         v_row.add(col_all_kaika);
	         v_row.add(col_yesday_kh_jihuo);
	         v_row.add(col_month_kh_jihuo);
	         v_row.add(col_all_kh_jihuo);
	         v_row.add(col_month_kh_stop);
	         v_row.add(col_all_kh_stop);	         
	         v_row.add(col_user_fee);
	         v_row.add(col_month_kh_czusers);
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
	    
	    

	    
	    buffer+= "<br><p class=\"p1\">��ͨ���¿���"+(result1.size()-1)+"���Ŷ�ͳ���ձ�����ֹ"+reStr+" 24�㡿</p>";
	    
		buffer+= "<br><table  class=\"Tab\">";
		
		buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold\"><td align=center>ʡ��</td>";
		buffer+="<td align=center>����</td><td align=center>���˺Ŷ�</td><td align=center>�ܿ���</td>";
		buffer+="<td align=center>���ռ���</td><td align=center>���¼���</td><td align=center>�ܼ�����</td><td align=center>����ͣ��</td>";
		buffer+="<td align=center>����ͣ��</td><td align=center>�����˵�</td><td align=center>���³����û�</td><td align=center>���ս���</td>";
		buffer+="<td align=center>���½���</td><td align=center>���¿���</td></tr>";
		
	    for(int i=0;i<result1.size();i++)
	    {
	    	v_get=(Vector)result1.elementAt(i);
	    	
	    	
	    	
	    	
	    	if(i==result1.size()-1)
	    	{
	    		
	    		buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold;color:red\">";
	    		
	
	    		  buffer+="<td align=center >����</td><td></td><td></td>";
	    		   
	    		  for(int k=3;k<v_get.size();k++){
	    			  
	    			buffer+="<td align=center >";  
	   	        	
	   	        	buffer+=(String)v_get.get(k);
	   	        	
	   	        	buffer+="</td>";
	   	        	
	   	        }
	    		
	    	}else{
	    		
	    		buffer+="<tr style=\"border-collapse:collapse;font-size:10pt\">";
	    	
	        for(int j=0;j<v_get.size();j++){
	        	
	        	if(j==2)
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
	    
	    
	    buffer+= "<br><img src=\"cid:a\">";
	      
	    
	    // if(m==1) return;
	    
	    /// ��Ӱ��꿼�˱���  2016.01.04
	    

		    try
		    {
		      Statement stmt=conn.createStatement();
		      
		 	  strsql_2=  
              "select f.prov_name,f.city_name,f.remark_num1,f.info_value,f.remark_num2,f.all_kh_kaika,f.yesday_kh_jihuo,f.month_kh_jihuo,f.all_kh_jihuo,                                                                                           "+
              "f.month_kh_stop,f.all_kh_stop,to_number(f.month_kh_czusers),f.user_fee,f.yes_stel_fee,f.month_stel_fee,f.year_stel_fee,round(case when f.year_stel_fee>360000 then 0 else 360000-f.year_stel_fee end,2) dixiao_fee                  "+
              "from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
              "(select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.remark_num2,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                      "+
              "count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
              "count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd') ) then m.acc_nbr else null end) month_kh_jihuo,"+
              "count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                "+
              "count(distinct (case when (m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then m.acc_nbr else null end)) month_kh_stop,                                                                         "+
              "count(distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                   "+
              "(select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
              "(select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
              "( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee,                                                                               "+
              "( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) month_stel_fee,                                                                                             "+
              "( select e.all_fee+e.year_all_fee from  tf_b_userfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.f_no_seg=m.info_value ) year_stel_fee                                                                                "+
              "from (select y.remark_num1,y.remark_num2,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.completed_date,x.create_date,                                                                                                                   "+
              "x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=2 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
              "group by m.remark_num1,m.remark_num2,m.city_code,m.info_value  order by m.remark_num2,m.city_code) f                                                                                                                                                            "+
              "union all                                                                                                                                                                                                                           "+
              "select '1','2',3,'4',5,sum(f.all_kh_kaika),sum(f.yesday_kh_jihuo),sum(f.month_kh_jihuo),sum(f.all_kh_jihuo),                                                                                                                          "+
              "sum(f.month_kh_stop),sum(f.all_kh_stop),sum(f.month_kh_czusers),sum(f.user_fee),sum(f.yes_stel_fee),                                                                                                                                "+
              "sum(f.month_stel_fee),sum(f.year_stel_fee),sum(round(case when f.year_stel_fee>360000 then 0 else 360000-f.year_stel_fee end,2))                                                                                                    "+
              "from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
              "(select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.remark_num2,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                    "+
              "count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
              "count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then m.acc_nbr else null end) month_kh_jihuo,                                                                     "+
              "count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                   "+
              "count(distinct (case when (m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then m.acc_nbr else null end)) month_kh_stop,                                                                         "+
              "count(distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                   "+
              "(select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
              "(select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
              "( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee,                                                                               "+
              "( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) month_stel_fee,                                                                                             "+
              "( select e.all_fee+e.year_all_fee from  tf_b_userfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.f_no_seg=m.info_value ) year_stel_fee                                                                                "+
              "from (select y.remark_num1,y.remark_num2,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.completed_date,x.create_date,                                                                                                                   "+
              "x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=2 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
              "group by m.remark_num1,m.remark_num2,m.city_code,m.info_value) f "; 
		
		         
		      
		      System.out.println(formatter.format(new Date())+"------>strsql_2="+strsql_2);
		      
		      ResultSet rs=stmt.executeQuery(strsql_2);
		      while(rs.next())
		      {
		    	 
		         String col_pro_name          =(""+rs.getString(1)).trim();    
		         String col_city_name         =(""+rs.getString(2)).trim();
		         String col_f_no_seg          =(""+rs.getString(4)).trim();   
		         String col_kh_begindate      =(""+rs.getString(5)).trim(); 
		         String col_all_kaika         =(""+rs.getString(6)).trim();   
		         String col_yesday_kh_jihuo   =(""+rs.getString(7)).trim();  
		         String col_month_kh_jihuo    =(""+rs.getString(8)).trim();  
		         String col_all_kh_jihuo      =(""+rs.getString(9)).trim();  
		         String col_month_kh_stop     =(""+rs.getString(10)).trim();
		         String col_all_kh_stop       =(""+rs.getString(11)).trim();
		         String col_month_kh_czusers  =(""+rs.getString(12)).trim();
		         String col_user_fee          =(""+rs.getString(13)).trim();
		         String col_yes_settle_fee    =(""+rs.getString(14)).trim();
		         String col_month_settle_fee  =(""+rs.getString(15)).trim(); 
		         String col_year_settle_fee   =(""+rs.getString(16)).trim(); 
		         String col_month_dxkh_fee    =(""+rs.getString(17)).trim(); 
		         
		         
		         
		                  
		         Vector v_row=new Vector();
		         v_row.add(col_pro_name);
		         v_row.add(col_city_name);
		         v_row.add(col_f_no_seg);       
		         v_row.add(col_kh_begindate);  
		         v_row.add(col_all_kaika);
		         v_row.add(col_yesday_kh_jihuo);
		         v_row.add(col_month_kh_jihuo);
		         v_row.add(col_all_kh_jihuo);
		         v_row.add(col_month_kh_stop);
		         v_row.add(col_all_kh_stop);	         
		         v_row.add(col_user_fee);
		         v_row.add(col_month_kh_czusers);
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
		    
		    
		    
		    
	    
	    
		    buffer+= "<br><hr style=\"height:2px;color:red\" /><br><p class=\"p1\">��ͨ���꿼��"+(result2.size()-1)+"���Ŷ�ͳ���ձ�����ֹ"+reStr+" 24�㡿</p>";
		    
		    buffer+= "<br><table class=\"Tab\">";
			
			buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold\"><td align=center>ʡ��</td>";
			buffer+="<td align=center>����</td><td align=center>���˺Ŷ�</td><td align=center>���˿�ʼʱ��</td><td align=center>�ܿ���</td>";
			buffer+="<td align=center>���ռ���</td><td align=center>���¼���</td><td align=center>�ܼ�����</td><td align=center>����ͣ��</td>";
			buffer+="<td align=center>����ͣ��</td><td align=center>�����˵�</td><td align=center>���³����û�</td><td align=center>���ս���</td>";
			buffer+="<td align=center>���½���</td><td align=center>�������</td><td align=center>���꿼��</td></tr>";
			
		    for(int i=0;i<result2.size();i++)
		    {
		    	v_get=(Vector)result2.elementAt(i);
		    	
		    	
		    	
		    	
		    	if(i==result2.size()-1)
		    	{
		    		
		    		buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold;color:red\">";
		    		
		
		    		  buffer+="<td align=center >����</td><td></td><td></td><td></td>";
		    		   
		    		  for(int k=4;k<v_get.size();k++){
		    			  
		    			buffer+="<td align=center >";  
		   	        	
		   	        	buffer+=(String)v_get.get(k);
		   	        	
		   	        	buffer+="</td>";
		   	        	
		   	        }
		    		
		    	}else{
		    		
		    		buffer+="<tr style=\"border-collapse:collapse;font-size:10pt\">";
		    	
		        for(int j=0;j<v_get.size();j++){
		        	
		        	if(j==2)
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
		    
		    buffer+= "<br><img src=\"cid:b\">";
		    
		    
		    //////// 2016.5.5 ��ӷǿ��˺Ŷ�ͳ��
		    
		    try
		    {
		      Statement stmt=conn.createStatement();
		      
		 	  strsql_5=  
              "select f.prov_name,f.city_name,f.remark_num1,f.info_value,f.remark_num2,f.all_kh_kaika,f.yesday_kh_jihuo,f.month_kh_jihuo,f.all_kh_jihuo,                                                                                           "+
              "f.month_kh_stop,f.all_kh_stop,to_number(f.month_kh_czusers),f.user_fee,f.yes_stel_fee,f.month_stel_fee,f.year_stel_fee,round(case when f.year_stel_fee>360000 then 0 else 360000-f.year_stel_fee end,2) dixiao_fee                  "+
              "from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
              "(select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.remark_num2,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                      "+
              "count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
              "count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd') ) then m.acc_nbr else null end) month_kh_jihuo,"+
              "count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                "+
              "count(distinct (case when (m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then m.acc_nbr else null end)) month_kh_stop,                                                                         "+
              "count(distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                   "+
              "nvl((select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value),0) month_kh_czusers,                                                                                       "+
              "nvl((select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value),0) user_fee,                                                                                              "+
              "nvl(( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ),0) yes_stel_fee,                                                                               "+
              "nvl(( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ),0) month_stel_fee,                                                                                             "+
              "nvl(( select e.all_fee+e.year_all_fee from  tf_b_userfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.f_no_seg=m.info_value ),0) year_stel_fee                                                                                "+
              "from (select y.remark_num1,y.remark_num2,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.completed_date,x.create_date,                                                                                                                   "+
              "x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=0 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
              "group by m.remark_num1,m.remark_num2,m.city_code,m.info_value  order by m.remark_num2,m.city_code) f                                                                                                                                                            "+
              "union all                                                                                                                                                                                                                           "+
              "select '1','2',3,'4',5,sum(f.all_kh_kaika),sum(f.yesday_kh_jihuo),sum(f.month_kh_jihuo),sum(f.all_kh_jihuo),                                                                                                                          "+
              "sum(f.month_kh_stop),sum(f.all_kh_stop),sum(f.month_kh_czusers),sum(f.user_fee),sum(f.yes_stel_fee),                                                                                                                                "+
              "sum(f.month_stel_fee),sum(f.year_stel_fee),sum(round(case when f.year_stel_fee>360000 then 0 else 360000-f.year_stel_fee end,2))                                                                                                    "+
              "from (select (select city_name from  bs_city_id_t where region_code=(select subcompany_code from bs_city_id_t where city_code=m.city_code and city_level=3) and city_level=2) prov_name,                                            "+
              "(select city_name from  bs_city_id_t where city_code=m.city_code and city_level=3) city_name,m.remark_num1,m.remark_num2,m.info_value,count(distinct  m.acc_nbr) all_kh_kaika,                                                                    "+
              "count(distinct case when m.state not in('2HM') and to_char(m.create_date,'yyyymmdd')=to_char(sysdate-1,'yyyymmdd') then m.acc_nbr else null end) yesday_kh_jihuo,                                                                   "+
              "count(distinct case when (m.state not in('2HM') and to_char(m.create_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.create_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then m.acc_nbr else null end) month_kh_jihuo,                                                                     "+
              "count(distinct case when m.state not in('2HM')  and  to_char(m.create_date, 'yyyymmdd') <=to_char(sysdate-1, 'yyyymmdd') then m.acc_nbr else null end) all_kh_jihuo,                                                                   "+
              "count(distinct (case when (m.state  in('2HD') and to_char(m.state_date,'yyyymm')=to_char(sysdate-1,'yyyymm') and to_char(m.state_date,'yyyymmdd')<=to_char(sysdate-1,'yyyymmdd')) then m.acc_nbr else null end)) month_kh_stop,                                                                         "+
              "count(distinct (case when m.state not in('2HM','2HA') and to_char(m.state_date,'yyyymm')<to_char(sysdate-1,'yyyymm') then m.acc_nbr else null end)) all_kh_stop,                                                                   "+
              "(select d.user_nums from  tf_b_gatherfeewxp d where d.stat_day=to_char(sysdate-1,'yyyymmdd') and  d.stat_name=m.info_value) month_kh_czusers,                                                                                       "+
              "(select a.stat_value from  tf_b_gatherfeewxp a where a.stat_day=to_char(sysdate-1,'yyyymmdd') and  a.stat_name=m.info_value) user_fee,                                                                                              "+
              "( select c.all_fee-c.ysday_all_fee from  tf_b_userfeewxp c where c.stat_day=to_char(sysdate-1,'yyyymmdd') and c.f_no_seg=m.info_value ) yes_stel_fee,                                                                               "+
              "( select b.all_fee from  tf_b_userfeewxp b where b.stat_day=to_char(sysdate-1,'yyyymmdd') and b.f_no_seg=m.info_value ) month_stel_fee,                                                                                             "+
              "( select e.all_fee+e.year_all_fee from  tf_b_userfeewxp e where e.stat_day=to_char(sysdate-1,'yyyymmdd') and e.f_no_seg=m.info_value ) year_stel_fee                                                                                "+
              "from (select y.remark_num1,y.remark_num2,y.info_value,x.city_code,x.acc_nbr,x.service_favour_id,x.completed_date,x.create_date,                                                                                                                   "+
              "x.state,x.state_date from serv x,tf_b_funtalkinfo y  where y.info_type=1 and y.remark_num1=0 and y.info_value=substr(x.acc_nbr,0,7)) m                                                                                              "+
              "group by m.remark_num1,m.remark_num2,m.city_code,m.info_value) f "; 
		
		         
		      
		      System.out.println(formatter.format(new Date())+"------>strsql_5="+strsql_5);
		      
		      ResultSet rs=stmt.executeQuery(strsql_5);
		      while(rs.next())
		      {
		    	 
		         String col_pro_name          =(""+rs.getString(1)).trim();    
		         String col_city_name         =(""+rs.getString(2)).trim();
		         String col_f_no_seg          =(""+rs.getString(4)).trim();   
		         String col_kh_begindate      =(""+rs.getString(5)).trim(); 
		         String col_all_kaika         =(""+rs.getString(6)).trim();   
		         String col_yesday_kh_jihuo   =(""+rs.getString(7)).trim();  
		         String col_month_kh_jihuo    =(""+rs.getString(8)).trim();  
		         String col_all_kh_jihuo      =(""+rs.getString(9)).trim();  
		         String col_month_kh_stop     =(""+rs.getString(10)).trim();
		         String col_all_kh_stop       =(""+rs.getString(11)).trim();
		         String col_month_kh_czusers  =(""+rs.getString(12)).trim();
		         String col_user_fee          =(""+rs.getString(13)).trim();
		         String col_yes_settle_fee    =(""+rs.getString(14)).trim();
		         String col_month_settle_fee  =(""+rs.getString(15)).trim(); 
		         String col_year_settle_fee   =(""+rs.getString(16)).trim(); 
		         String col_month_dxkh_fee    =(""+rs.getString(17)).trim(); 
		         
		         
		         
		                  
		         Vector v_row=new Vector();
		         v_row.add(col_pro_name);
		         v_row.add(col_city_name);
		         v_row.add(col_f_no_seg);
		         v_row.add(col_all_kaika);
		         v_row.add(col_yesday_kh_jihuo);
		         v_row.add(col_month_kh_jihuo);
		         v_row.add(col_all_kh_jihuo);
		         v_row.add(col_month_kh_stop);
		         v_row.add(col_all_kh_stop);	         
		         v_row.add(col_user_fee);
		         v_row.add(col_month_kh_czusers);
		         v_row.add(col_yes_settle_fee);
		         v_row.add(col_month_settle_fee);
		         
		         result5.add(v_row);
		      }
		      rs.close();
		      rs=null;
		      stmt.close();
		      stmt=null;
		      

		    }
		    catch(Exception ex)
		    {
		      System.out.println("------->in getting no checkable date fail,"+ex);
		    }
 
	    
		    buffer+= "<br><hr style=\"height:2px;color:red\" /><br><p class=\"p1\">��ͨ�ǿ���"+(result5.size()-1)+"���Ŷ�ͳ���ձ�����ֹ"+reStr+" 24�㡿</p>";
		    
		    buffer+= "<br><table class=\"Tab\">";
			
			buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold\"><td align=center>ʡ��</td>";
			buffer+="<td align=center>����</td><td align=center>�Ŷ�</td><td align=center>�ܿ���</td>";
			buffer+="<td align=center>���ռ���</td><td align=center>���¼���</td><td align=center>�ܼ�����</td><td align=center>����ͣ��</td>";
			buffer+="<td align=center>����ͣ��</td><td align=center>�����˵�</td><td align=center>���³����û�</td><td align=center>���ս���</td>";
			buffer+="<td align=center>���½���</td></tr>";
			
		    for(int i=0;i<result5.size();i++)
		    {
		    	v_get=(Vector)result5.elementAt(i);
    	
		    	
		    	if(i==result5.size()-1)
		    	{
		    		
		    		buffer+="<tr style=\"border-collapse:collapse;font-size:11pt;font-weight:bold;color:red\">";
		    		
		    		  buffer+="<td align=center >����</td><td></td><td></td><td></td>";
		    		   
		    		  for(int k=4;k<v_get.size();k++){
		    			  
		    			buffer+="<td align=center >";  
		   	        	
		   	        	buffer+=(String)v_get.get(k);
		   	        	
		   	        	buffer+="</td>";
		   	        	
		   	        }
		    		
		    	}else{
		    		
		    		buffer+="<tr style=\"border-collapse:collapse;font-size:10pt\">";
		    	
		        for(int j=0;j<v_get.size();j++){
		        	
		        	if(j==2)
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
		    
		       
	    
	    buffer+="<p class=\"p2\"> ��ע����ͳ�����ݽ�ֹ"+reStr+" 24�㣬�����Ǳ�������ֶν��ͣ�<br>�ܿ��������Ŷ�����ͨ���Ѿ���ʼ�����û�������";
	    buffer+="<br>���ռ���Ŷε���ʵ����������û���������������������<br>���¼���Ŷε���ʵ����������û���������������������<br>�ܼ��������Ŷ��ܹ�ʵ����������û����������Ѿ���������<br>";
	    buffer+="����ͣ�����Ŷε�����Ƿ��ͣ�����û��������˲����û�������Ȼ�����˵���<br>����ͣ�����Ŷ�������Ƿ��ͣ�����û��������˲����û����²��ٲ����˵���<br>���³����û����Ŷε��²����˵����û�������<br>";
	    buffer+="�����˵����Ŷε��²������û��˵����(��λ��Ԫ)��<br>���ս��㣺�Ŷε�����Ҫ֧������ͨ�Ľ�����(��λ��Ԫ)��<br>���½��㣺�Ŷε�����Ҫ֧������ͨ�Ľ�����(��λ��Ԫ)��";
	    buffer+="<br>���¿��ˣ��Ŷε�����δ�ﵽ��ͨ30000Ԫ������Ѷ�֧����ΥԼ���(��λ��Ԫ)��<br>���꿼�ˣ��Ŷε�����δ�ﵽ��ͨ360000Ԫ������Ѷ�֧����ΥԼ���(��λ��Ԫ)��</p>";
	    

	    System.out.println(formatter.format(new Date())+"------>buffer===="+buffer.toString());
	    
	    
	    try
	    {
	      Statement stmt=conn.createStatement();
	      
	 	  strsql_3="select t.info_value,t.info_desc from tf_b_funtalkinfo t where t.info_type='3' and rownum=1"; 
	
	         
	      
	      System.out.println(formatter.format(new Date())+"------>strsql_3="+strsql_3);

	      
	      ResultSet rs=stmt.executeQuery(strsql_3);
	      while(rs.next())
	      {
	    	 
	         String col_user_name          =(""+rs.getString(1)).trim();    
	         String col_user_password      =(""+rs.getString(2)).trim();
                  
	         Vector v_row=new Vector();
	         v_row.add(col_user_name);
	         v_row.add(col_user_password);
	         
	         result3.add(v_row);
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
	    

	  	final String username =((Vector)result3.elementAt(0)).get(0).toString();
	    final String password =((Vector)result3.elementAt(0)).get(1).toString();  
	    
	    System.out.println(formatter.format(new Date())+"------>username="+username+"----password="+password);
     
	  Properties props = new Properties();
	  
	  props.put("mail.smtp.host", "mail.funtalk.cn");
	  props.put("mail.smtp.auth", "true");

	  Session session = Session.getDefaultInstance(props, new Authenticator() {

	   protected PasswordAuthentication getPasswordAuthentication() {  return new PasswordAuthentication(username,password); }
	   
	  });
	  
	  
	  //String[] to = {"wangxp1@funtalk.cn","43198341@qq.com"};
	  
	  String[] to = {"liuyl4@funtalk.cn","liyao1@funtalk.cn","wangxp1@funtalk.cn","liuyangzs@funtalk.cn","zhangxiaols@funtalk.cn","xiecx@funtalk.cn","lilei5@funtalk.cn",
	                 "lijun@funtalk.cn","duzt@funtalk.cn","qifei@funtalk.cn","fengsg@funtalk.cn","shaoys1@funtalk.cn","songlj1@funtalk.cn","libing1@funtalk.cn","gengrui@funtalk.cn"};
	  
	  String from = "wangxp1@funtalk.cn";
	  String subject = "��ͨ���˺Ŷ�ͳ���ձ�@"+reStr;
	  MimeMessage msg = new MimeMessage(session);
	  try {
	   msg.setFrom(new InternetAddress(from));
	   InternetAddress[] addressTo = new InternetAddress[to.length];
	   for (int i = 0; i < to.length; i++)
	   {
	    addressTo[i] = new InternetAddress(to[i]);
	   }
	   msg.setRecipients(RecipientType.TO, addressTo); 
	   // msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to)); // ���͵����û�
	   msg.setSubject(subject);
	   
	   
	   // Create a multipar message
	   Multipart multipart = new MimeMultipart("mixed");
	   
       /*
        * JavaMail API��������ϢֻΪ�ı�,�κ���ʽ����Ϣ�����������Ը�MimeMessage��һ����. �����ı���Ϣ,��Ϊ�ļ����������ڵ����ʼ���Ϣ��һ�����Ǻ��ձ��. 
        * JavaMail APIͨ��ʹ��DataHandler����,�ṩһ���������ǰ������ı�BodyPart����ļ�㷽��.
        */ 
	   
	   MimeBodyPart bodyImg1 = new MimeBodyPart();
       File  sourceImg1=new File(filePath+monthFileName);
       
       //byte[] imgbytes = getBytesFromFile(new File(filePath+monthFileName));
       //DataSource picds = new ByteArrayDataSource(imgbytes,"application/octet-stream");
       
       DataHandler dh1 = new DataHandler(new FileDataSource(sourceImg1));//ͼƬ·�� 
       bodyImg1.setDataHandler(dh1); 
       bodyImg1.setFileName(monthFileName);
       // ����ͼƬ��һ����ʾ������ʾ���ʼ�����ʾ 
       bodyImg1.setContentID("a"); 
       
	   
	   multipart.addBodyPart(bodyImg1);
       
	   MimeBodyPart bodyImg2 = new MimeBodyPart();
       File  sourceImg2=new File(filePath+yearFileName);
       DataHandler dh2 = new DataHandler(new FileDataSource(sourceImg2));//ͼƬ·�� 
       bodyImg2.setDataHandler(dh2); 
       // ����ͼƬ��һ����ʾ������ʾ���ʼ�����ʾ 
       bodyImg2.setContentID("b"); 
       
	   multipart.addBodyPart(bodyImg2);

 
	   BodyPart messageBodyPart = new MimeBodyPart();
	   	   
	   messageBodyPart.setContent(buffer, "text/html;charset=GBK"); // ��ҳ��ʽ

	   multipart.addBodyPart(messageBodyPart);
	   

	   
	   
	   /*// Part two is attachment  ���͸���
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
	  
	  
	  	if(conn!=null){ 
	  		
	  		try{
	  			
			conn.close();
			
	  		 }catch(Exception e){
	  			
	  			System.out.println("------->in closing conn fail------>"+e);
	  			
	  		}
	  		
	  	}
	  
	}
	
	
	
	  public static byte[] getBytesFromFile(File fileName){
		  
	        try {
	        	
	            InputStream stream = new FileInputStream(fileName);
	            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
	            byte[] b = new byte[1000];
	            int n;
	            while ((n = stream.read(b)) != -1) {
	                out.write(b, 0, n);
	            }
	            stream.close();
	            out.close();
	            
	            return out.toByteArray();
	     
	            
	        } catch (IOException e){
	        }
	        
	        return null;

	    }
	 

}
