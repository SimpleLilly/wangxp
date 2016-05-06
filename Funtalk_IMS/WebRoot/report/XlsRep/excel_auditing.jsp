<%@ page contentType="text/html;charset=gb2312" %>
<%@ page import="com.funtalk.bean.MyReportBean,com.funtalk.common.*"%>
<%@ page import="java.text.SimpleDateFormat"%>

<HTML>
<HEAD> 
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>报表审核</title>
</HEAD>

<%
    String returnStr="";
    String dataStr      = new String(request.getParameter("ExcelData").getBytes("ISO8859_1"),"gb2312");//报表数据
    System.out.println("------>datastr="+dataStr);  
    
    String chname = new String(request.getParameter("chname").getBytes("ISO8859_1"),"gb2312");//源表中文名
    String source_seq   =request.getParameter("source_seq");//源报表序列号
    source_seq="null";
    //System.out.println("------>source_seq="+source_seq);

    String js_month        = new String(request.getParameter("js_month").getBytes("ISO8859_1"),"gb2312");//条件
    
    String report_type=request.getParameter("report_type");
    
    String model_file=request.getParameter("model_file");
    model_file=model_file.trim();
    
    String operator     = request.getParameter("owner");//操作员
    
    String filepath = request.getParameter("filepath"); //报表文件路径
    
    String ModifyReason =" ";//调帐原因
    
    MyReportBean  bean = new MyReportBean();
    //SaveModifyReport(chname, source_seq,js_month,model_file ,issheet,owner, memo, dataStr)
	String UrlPath = "/report/XlsRep/excel/audit/";
    String fileName = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new java.util.Date()) + ".xls";
    String newpath = request.getRealPath("/")+ UrlPath + fileName;
    int val1 = bean.auditingReport(filepath,newpath);
    System.out.println(filepath);
    System.out.println(newpath);
    int val = bean.SaveModifyReport(chname, source_seq, js_month,report_type,model_file, operator,UrlPath + fileName, ModifyReason, dataStr);
    if((val+val1) != 0) 
    {
        //returnStr = "审核失败!";
        out.println("<script language=\"javascript\">alert(\"审核失败!\")</script>");
    }
    else
    {
        //returnStr = "审核成功!";
        out.println("<script language=\"javascript\">alert(\"审核成功!\")</script>");
    }    
%>

<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="../../images/bline.gif">

</BODY>

</HTML>
