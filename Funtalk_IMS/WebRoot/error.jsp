<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="ww" uri="webwork"%>    
<%
String errorMsg=(String)request.getAttribute("errorMsg");

%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" type="text/css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><ww:text name="title"/></title>
</head>
<body>
<%=errorMsg%>
</body>
</html>