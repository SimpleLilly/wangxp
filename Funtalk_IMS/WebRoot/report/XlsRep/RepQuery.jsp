<%@ page contentType="text/html; charset=GBK" %>
<html>

<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=gb2312">
<title>Excel报表</title>
<%
    String queryid = request.getParameter("queryid");
    String qry_url = "RepQueryMiddle.jsp";
    String newFormWindow = request.getParameter("newFormWindow");
%>
</head>

<frameset rows="55,*"  frameborder="no" name=config>
    <frame name="head" noresize target="middle" src="RepQueryHead.jsp?qry_url=<%=qry_url%>&queryid=<%=queryid%>&newFormWindow=<%=newFormWindow%>">
    <frame name="middle" src="RepQueryMiddle.jsp">
	<noframes>
   <body>
	<p>此网页使用了框架，但您的浏览器不支持框架。</p>
	</body>
	</noframes>
</frameset>

</html>
