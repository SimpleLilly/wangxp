<!-- tiantao -->
<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="webwork" prefix="ww"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<link rel="stylesheet" href="../../css/publicStyle_jifei.css" type="text/css">
<title>title</title>
<script language="javascript">
</script>
</head>
<body leftmargin="2" topmargin="2" marginwidth="0" marginheight="0">
<div id="infoList"> 
 <div class="title">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统配置 --&gt; <font color="ff6600">批量导入</font></td>
  </tr>
</table> </div></div>
<br>

<div id="missionTable">
  <div class="bolder">
  <form name="form1" action="" method="post">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="f6f6f6">
       <tr><th>导入结果</th></tr>
        <tr>
            <td>
            <div align="center"> 
           	成功导入<ww:property value="recNum"/>条数据！
			</div>
			</td>
		</tr>
		<ww:if test="errorNum!=\"0\"">
		 <tr>
            <td>
            <div align="center"> 
           	有<ww:property value="errorNum"/>条记录导入失败！
			</div>
			</td>
		</tr>
		<tr>
			<td>
            <div align="center"> 
           		错误日志文件：<a href="
           		<%	String url = (String)request.getAttribute("errorUrl");
           			String fileName = (String)request.getAttribute("errorFileName"); 
           			out.println("/localdata/bacth_import/error/"+fileName+".txt");
           		%>
           		" target="_blank"><ww:property value="errorFileName"></ww:property></a>
			</div>
			</td>
		</tr>
		</ww:if>
      </table></form>
 </DIV> </DIV>
</body>
</html>
