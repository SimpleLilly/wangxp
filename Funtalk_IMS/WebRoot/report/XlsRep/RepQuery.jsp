<%@ page contentType="text/html; charset=GBK" %>
<html>

<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=gb2312">
<title>Excel����</title>
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
	<p>����ҳʹ���˿�ܣ��������������֧�ֿ�ܡ�</p>
	</body>
	</noframes>
</frameset>

</html>
