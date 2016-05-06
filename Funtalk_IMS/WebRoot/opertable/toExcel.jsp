<%@ page language="java" contentType="text/html; charset=GB2312" pageEncoding="GB2312"%>
<%@ taglib prefix="ww" uri="/webwork" %>
<%
	response.setHeader("Content-disposition","application/msexcel;charset=gb2312;attachment; filename=excel.xls");   
%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=gb2312">
<title>Excel</title>
</head>
<body>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
<tr bgcolor="cccccc">
<ww:iterator value="#request['localDataTableCols']" status="colstatus">
	<td align="center"><ww:property value="columncomment"/>(<ww:property value="columnname"/>)</td>
</ww:iterator>
</tr>
<ww:iterator value="#request['exportList']" status="rowstatus">
<tr>
	<ww:iterator value="#request['exportList'][#rowstatus.index]" status="fieldindex">	
		<td><ww:property value="#request['exportList'][#rowstatus.index][#fieldindex.index]"/></td>
	</ww:iterator>
</tr>
</ww:iterator>
</table>  
</body>
</html>
