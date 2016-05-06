<%@ page language="java" contentType="text/html; charset=GB18030"
    pageEncoding="GB18030"%>
    <%@ page contentType="text/html;charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.funtalk.common.DataConnection" %>
<%@ page import="java.io.*" %>
<%@ page import="com.funtalk.common.Member" %>

<%!

    private BufferedWriter bout = null;

	private boolean writeFile(List data,String filepath){
				
		try {		
			//指定字符编码
			bout=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath),"GBK"));

			for(int i = 0; i < data.size(); i++){
				String[] ar = (String[])data.get(i);
				for(int j = 0; j < ar.length; j++){
					bout.write(ar[j]);
					//费用和业务之间加两个,
					if( j == 3 )
						bout.write(",,");
					if( j != ar.length-1 )
						bout.write(",");
				}
				if( i != data.size()-1)
				bout.write("\n");
			}
			bout.flush();
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	return true;
    }
	
	private boolean sendErpFile(String ftp_p, String Path, String filename)
	{
		String  command = "sh "+Path+"ftp_file.sh "+Path+" "+filename+" "+ftp_p;
        System.out.println ("transfer shell=["+command+"]");
        Process proc = null;
        try
        {
            proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            proc.destroy();
        }
        catch (Exception e) 
        {
		    if(proc!=null)
                proc.destroy();
			e.printStackTrace();
			return false;
		}
		return true; 
	}
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML><HEAD>
<META content="text/html; charset=gb2312" http-equiv=Content-Type>
<link href="/css/yd.css" rel="stylesheet" type="text/css">
</HEAD> 

<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="/images/bline.gif">

<form name="listform" method="post" action="ERPFile.jsp">
<TABLE  border=0 cellPadding=1 cellSpacing=0 class=line15 width=100% align=center>
<TR><TD height=30 vAlign=bottom width="100%">
<%
Member currentUser = (Member)session.getAttribute("currentUser");
String userId = currentUser.getUserid();

    int i;
    String str,ystr,mstr,sql;

    String year=request.getParameter("C_0");
    String month=request.getParameter("C_1");
    String svc=request.getParameter("C_2");
	String create=request.getParameter("create");
	String send=request.getParameter("send");
	
    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
    Date now = new Date();
    String DateStr = df.format(now);
    String yc = DateStr.substring(0,4);
    String mc = DateStr.substring(4,6);
    int mi=Integer.parseInt(mc);
    int yi=Integer.parseInt(yc);
    if(mi==0)
    {
        mi=12;
        yi=yi-1;
    }
    if(year==null)   year=yi+"";
    if(month==null)  month=(mi>=10)?(mi+""):("0"+mi);
	      
    DataConnection dbconn = new DataConnection();
    
	//senderp
	if(send != null && send.equals("send")){
		sql = "select file_name from sp_erp_file where month='"+year+month+"' and svc_code='"+svc+"'";
		List ls = dbconn.queryNotBind(sql);
		if( ls != null && ls.size() > 0 ){
			String[] arr = (String[]) ls.get(0);
			String fileName = arr[0];
		
			//调用脚本					
			List val_ip=dbconn.queryNotBind("select ip,username,password,path from sp_interface_cfg where mod_code='ERP'");
            String ar[] = (String[])val_ip.get(0);
            String ftp_p = ar[0]+" "+ar[1]+" "+ar[2]+" "+ar[3];
			if( sendErpFile(ftp_p, request.getRealPath("/")+"/report/erpfile/", fileName) )
			{
				//插入已发送标志
		        sql="update sp_erp_file set send_flag='1' where month='"+
		        year+month+"' and svc_code='"+svc+"' and send_flag='0'";
		        dbconn.updateNotBind(sql);  
		        //发送记录
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                Date time = new Date();
                String TimeStr = sdf.format(time);
		        sql="insert into sp_erp_file_send (month,svc_code,send_person,send_date) values('"+
		        year+month+"','"+svc+"','"+userId+"',to_date('"+TimeStr+"','yyyymmddHH24MISS'))";
		        dbconn.insertNotBind(sql);
			}
			else
			{
				out.println("<script>alert('发送失败!');</script>");
			}
		}

	}
	//creat file
    if(create!=null && create.equals("create"))
    {
    	//取数据
        List sendData = null;
        /*sql="select cg,t2.erp_code,contract_no,sum(fc) fc,svc,month,sp_name from ( "+
            " select decode(service_class,'C','010301','G','010201','@') cg,a.long_code,b.contract_no, "+
            "    a.fc,decode(a.svc_code,'WAP','WAP','VOICE','丽音','XL','炫铃','短信') svc,a.month,b.sp_name "+
            " from v_sp_clearing_fee a, "+
            "     (select month,v_sp_code,max(sp_name) sp_name,max(contract_no) contract_no from sp_info_status "+
            "     where rep_gui_flag='1' and to_char(gui_date,'yyyymm')='"+year+month+"' " +
            "     and v_sp_code in (select v_sp_code from v_dept_sp_code where svc_code='"+svc+"') "+
            "     group by month,v_sp_code) b "+
            " where a.month=b.month and a.v_sp_code=b.v_sp_code ) t1,city_list_prov t2 "+
            "where t1.long_code=t2.city_code "+
            "group by cg,t2.erp_code,contract_no,svc,month,sp_name ";
            */

        sendData = new ArrayList();//dbconn.queryNotBind(sql);
        String[] strArr = {"a","b","c"};
        String[] strArr2 = {"1","2","3"};
        sendData.add(strArr);
        sendData.add(strArr2);
        if( sendData != null && sendData.size() > 0 ){
        	//写入文件
        	String UrlPath = "/report/erpfile/";
            String serverPath = request.getRealPath("/") + UrlPath;
            String fileName = year + month + "_"+svc + /*"_"+(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +*/ ".txt";
        	if( writeFile(sendData,serverPath+fileName) ){
        		//查询之前有无生成的文件，有先删除
        		/*
        		sql = "select file_name from sp_erp_file where month='"+year+month+"' and svc_code='"+svc+"'";
        		List ls = dbconn.queryNotBind(sql);
        		if( ls != null && ls.size() > 0 ){
        			//String[] arr = (String[]) ls.get(0);
        			//String oldFile = arr[0];
        			//删除原文件
        			//File tempFile = new File(serverPath+oldFile);
		    		  //if( tempFile.exists() )   
		    			//  tempFile.delete();
        	        //sql="update sp_erp_file set file_name='"+fileName+"' where month='"+year+month+"' and svc_code='"+svc+"'";
        	        //dbconn.updateNotBind(sql);
        		}else{//没有，插入表
            		//文件名存入表中，
            		sql="insert into sp_erp_file (month,svc_code,file_name) values ('"+year+month+"','"+svc+"','"+fileName+"')";
                    dbconn.insertNotBind(sql);	
        		}
        		*/
        		              
        	}else{
            	out.println("<script>alert(\"生成文件时失败！\");</script>");
        	}
        }else{
        	out.println("<script>alert(\"  无数据，无法生成！\");</script>");
        }
       
    }
	
    //查询有没生成文件，是否发送
	List fileList = null;
    List sendDateList = null;
    String erpFileName = null;
    String sendFlag = null;
	sql = "select month,svc_code,file_name,send_flag from sp_erp_file where month='"+year+month+"' and svc_code='"+svc+"'";
	fileList = dbconn.queryNotBind(sql);
	if( fileList != null && fileList.size() > 0 ){
		String[] array =  (String[]) fileList.get(0);
		erpFileName = array[2];
		sendFlag = array[3];
		if(sendFlag.equals("1")){
			sql = "select send_date,send_person from sp_erp_file_send where month='"+year+month+"' and svc_code='"+svc+"'";	
			sendDateList = dbconn.queryNotBind(sql);
		}

	}
	
    String svc_name="";
	if(svc.equals("SM"))	         svc_name = "在信";
	else if(svc.equals("WAP"))	     svc_name = "WAP业务";
	else if(svc.equals("VOICE"))     svc_name = "丽音";
	else if(svc.equals("OTHER"))     svc_name = "数据部业务";
	else if(svc.equals("1019"))      svc_name = "客服部业务";
	else if(svc.equals("XL"))        svc_name = "炫铃业务";
	 
	out.println("<b>&nbsp;"+svc_name+"</b>");
	ystr="&nbsp;&nbsp;归档时间：&nbsp;&nbsp;年份<select class='input'  name='C_0' onchange='myclear()'>";
    for(i=yi-2; i<=yi+1; i++)
        ystr+="<option value='"+i+"'"+((year.equals(i+""))?(" selected "):"")+">"+i+"</option>";
    out.println(ystr+"</select>");

    mstr="&nbsp;&nbsp;月份<select class='input' name='C_1' onchange='myclear()'>";
    for(i=1; i<=12; i++)
    {
        str = (i>=10)?(i+""):("0"+i);
        mstr+="<option value='"+str+"'"+((month.equals(str))?(" selected "):"")+">"+str+"</option>";
    }
    out.println(mstr+"</select>");


%>
  &nbsp;&nbsp;&nbsp;
  
  	<%		    	out.println("<input type=button value= 生成文件 class=\"button\" onClick=\"createfile()\">");
					if(fileList !=null && fileList.size() > 0){
						//if( sendFlag != null && sendFlag.equals("1") ){
						//	out.println("<input type=button value= 发送ERP class=\"button\" onClick=\"alert('   已发送!');\"> <font color='red'>已发送</red>");
						//}else{
							out.println("<input type=button value= 发送ERP class=\"button\" onClick=\"senderp()\">");
						//}
						
					}
    %>

  </TD>
</TR>
<tr><td height=5 colspan=2><HR noShade SIZE=1></TD></TR>
</TABLE>
<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td>&nbsp;</td>
  </tr>
<%
	if(fileList !=null && fileList.size() > 0){
		for(i = 0; i < fileList.size(); i++){
			String[] array = (String[]) fileList.get(i);
			out.println("<tr><td>");
			out.println("ERP文件: <a href=\""+request.getContextPath()+"/report/erpfile/"+array[2]+"\">"+array[2]+"</a>");
			out.println("</td></tr>");
		}
	}
%>
</table>

<%
	if(sendDateList !=null && sendDateList.size() > 0){

	    out.println("<table width=\"90%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">");
		out.println("<tr>");
		out.println("<td>发送时间</td>");
		out.println("<td>发送人</td>");
        out.println("</tr>");

		for(i = 0; i < sendDateList.size(); i++){
			String[] array = (String[]) sendDateList.get(i);
			out.println("<tr><td>"+array[0]+"</td><td>");
			out.println(array[1]);
			out.println("</td></tr>");
		}
		  out.println("</table>");
	}
%>

<!-- hidden 部分 -->
  <input type="hidden" name="C_2" value="<%=svc%>">
  <input type="hidden" name="create" value="">
  <input type="hidden" name="send" value="">
</FORM>

<SCRIPT language=javascript>
function myclear()
{
	listform.submit();
}
//create
function createfile()
{
        listform.create.value="create";
        listform.submit();
}
//send erp
function senderp()
{
        listform.send.value="send";
        listform.submit();
}
</SCRIPT>

</BODY>
</HTML>