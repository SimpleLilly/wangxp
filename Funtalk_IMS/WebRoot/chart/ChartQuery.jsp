<%@ page contentType="text/html; charset=GBK" %>
<html>

<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=gb2312">
<title>����ͼչ��</title>
<%
    String queryid = request.getParameter("queryid");
    String qry_url = "ChartQueryMiddle.jsp";
%>
</head>


<frameset rows="78,*"  frameborder="no" name=config>
    <frame name="head" noresize target="middle" src="ChartQueryHead.jsp?qry_url=<%=qry_url%>&queryid=<%=queryid%>">
    <frame name="middle" src="ChartQueryMiddle.jsp">
	<noframes>
   <body>
	<p>����ҳʹ���˿�ܣ��������������֧�ֿ�ܡ�</p>
	</body>
	</noframes>
</frameset>

</html>
