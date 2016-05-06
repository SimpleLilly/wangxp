<%@ page language="java" contentType="text/html; charset=GB2312" pageEncoding="GB2312"%>
<%@ taglib prefix="ww" uri="/webwork" %>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragrma", "no-cache");
	response.setDateHeader("Expires", 0);
%>
<%
String path = request.getContextPath();
 %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<title>报表展示</title>
<link href="/css/zi.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/ext-all.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN.js"></script>
</head>
<body>
<SCRIPT LANGUAGE="javascript">

</SCRIPT>


<table width="100%" border="0" cellspacing="0" cellpadding="3" >
<tr align=center>
   <td width="100%" align="center" >
      <font size = "5" >报表正在生成,请等待!!!!</font>
   </td>
</tr>
</table>


</body>
</html>
