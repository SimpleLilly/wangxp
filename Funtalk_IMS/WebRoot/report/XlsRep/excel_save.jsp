<%@ page contentType="text/html;charset=gb2312" %>
<%@ page import="com.funtalk.bean.MyReportBean,com.funtalk.common.*"%>

<HTML>
<HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>报表归档</title>
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
    int val = bean.SaveModifyReport(chname, source_seq, js_month,report_type,model_file, operator,"", ModifyReason, dataStr);
    if(val != 0)
    {
        returnStr = "归档失败!";
    }
    else
    {
        returnStr = "恭喜,归档成功!";
    }    
%>

<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="../../images/bline.gif">
<TABLE  border=0 cellPadding=1 cellSpacing=0 class=line15 width=100% align=center>
<TR>
  <TD height=30 vAlign=bottom width="70%"> <DIV align=left>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;报表归档结果
 </DIV></TD>
  <TD height=30 vAlign=bottom width="30%"> <DIV align=right><input type=image src='../../images/shu3.gif' width=20 height=20  onclick='window.open("/DisplaylistHelp?action=1&helpinfoid=120" , "_blank" , "width=420,height=400,resizable=10,scrollbars=yes")'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</DIV></TD>
</TR>
<tr><td height=5 colspan=2><DIV align=center>
	  <HR noShade SIZE=1 width="1200">
	</DIV></TD>
</TR>
</TABLE>

<TABLE width="95%"  height=200    border=0 align="center" cellPadding=0 cellSpacing=2>
  <TBODY>
  <TR align="center" >
      <td> <img src="../../images/sucess.gif" width="107" height="98" > </td>
  </tr>
  <TR  align="center" >
  <td >
  
  <marquee behavior=alternate width=200><font color=green size=4> <%=returnStr%> </font></marquee>
  
  </td>
  </tr>

   </TBODY>
</TABLE>

  <TABLE border=0 align="center" cellPadding=0 cellSpacing=3 width=95%>
    <TBODY>

      <TR>
      <td align="center">
        </td>
      </TR>
    </TBODY>
  </TABLE>
</BODY>

</HTML>
