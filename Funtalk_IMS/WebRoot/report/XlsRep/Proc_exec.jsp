<%@ page language="java" contentType="text/html; charset=gb2312" %>
<%@ page import="com.funtalk.common.DataConnection" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>


<HTML><HEAD>
<META content="text/html; charset=gb2312" http-equiv=Content-Type>
<link href="/css/yd.css" rel="stylesheet" type="text/css">
</HEAD>
<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="/images/bline.gif">

<%
      String year=request.getParameter("C_0");
      String month=request.getParameter("C_1");
	  DataConnection db = new DataConnection();
      Connection conn = db.getConnection("DBSETTLE");
    
      CallableStatement proc = null;
      proc = conn.prepareCall("{ call p_sp_settle('"+year+month+"',?) }");
      proc.registerOutParameter(1, Types.VARCHAR);
      proc.execute();
      String msg = proc.getString(1);
      String msg_str = null, img=null;
      if((msg!=null)&&(msg.equals("0")))
      {
          msg_str = "执行成功("+year+"年"+month+"月)";
          img = "10.gif";
      }
      else
      {
          msg_str = "执行失败("+year+"年"+month+"月)："+msg;
          img = "11.gif";
      }
      proc.close();
      conn.close();
%>

<table border="0" cellspacing="10" cellpadding="0" align="center" bgcolor="8DAAD8" bordercolor="#000000"><tr>
<br><br><br><br><br>
<td width="60" height="60" bgcolor="#FFFFFF" align="center"><img src="/images/<%=img%>" width="50" height="40"></td>
<td width="250" bgcolor="#FFFFFF" align="center"><%=msg_str%></font></td>
</tr></table>

</BODY>
</HTML>