
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.funtalk.common.DataConnection" %>
<%@ page import="com.funtalk.bean.ToolsOfSystem" %>
<%@ page import="com.funtalk.pojo.rightmanage.User" %>
<%@ page import="com.funtalk.common.WriteLog" %>


<HTML>
<HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>内容</title>
</HEAD>
<body style="margin:5" background="images/back.gif">

<table border="0" cellspacing="10" cellpadding="0" align="center" bgcolor="8DAAD8" bordercolor="#000000"><tr>
<br><br><br><br><br>

<%
    int i;
    String sql = request.getParameter("sql");
    String dbstr = request.getParameter("table_db");
    if((sql==null)||(sql.equals("")))
    { 
        out.println("<td>数据没有修改</td></tr></table></BODY></HTML>");
        return;
    }

    /////Member currentUser = (Member)session.getAttribute("currentUser");
    User currentUser        =(User)session.getAttribute("currentUser");
    
    if(currentUser==null)
        System.out.println("===========>必须重新登录!");
    
    String userName         = currentUser.getUsername();
    /////String userName = currentUser.getUserid();
    String newSql = sql;//new String(sql.getBytes("ISO8859_1"),"gb2312");
    //String ar[] = newSql.split(";@@@");
    //String ar[] = ToolsOfSystem.mySplit(newSql.substring(0,newSql.length()-4), ";@@@");
    String ar[] = newSql.substring(0,newSql.length()-4).split(";@@@");
    String sqlAr[][] = new String[ar.length][2];
    String logAr[][] = new String[ar.length][2];
    
    
    for(i=0; i<ar.length; i++)
    {
        sqlAr[i][0] = ar[i];
        String tmpStr = ar[i].substring(0,6);
        if(tmpStr.equals("insert"))
            sqlAr[i][1] = "1";
        else if(tmpStr.equals("update"))
            sqlAr[i][1] = "2";
        else
            sqlAr[i][1] = "3";
    }
    DataConnection db = new DataConnection();
    String outStr = db.userJdbcTrsaction(sqlAr,dbstr);
    String img;

    if(outStr.charAt(0)=='0')
    {
       for(i=0; i<ar.length; i++)
       {
           logAr[i][1] = "1";
           //String tmpStr = ar[i].replaceAll("'", "''");
           String tmpStr = ToolsOfSystem.replace(ar[i], "'", "''");
           logAr[i][0] = "insert into t_log values(t_log_seq.nextval,sysdate,'" +
                    userName + "','该用户修改了局数据[" + tmpStr + "]')";
                    
           
           /////////////////////////write log begin
           WriteLog.dbLog(userName,"U","修改配置","1","添加/修改数据:"+logAr[i][0]);
	       /////////////////////////write log end    
                    
       }
       //String ss = db.userJdbcTrsaction(logAr);
       outStr="<font size=\"4\">保存成功!</font>";
       img="10.gif";
    }
    else
    {
       outStr="<font color=\"#FF0000\" size=\"2\">保存失败："+outStr.substring(2)+"</font>";
       img="11.gif";
    }

    out.print("<td width=\"60\" height=\"60\" bgcolor=\"#FFFFFF\" align=\"center\">");
    out.println("<img src=\"/images/"+img+"\" width=\"50\" height=\"40\"></td>");
    out.println("<td width=\"250\" bgcolor=\"#FFFFFF\" align=\"center\">"+outStr+"</td>");

%>

</tr></table>
</BODY>
</HTML>
