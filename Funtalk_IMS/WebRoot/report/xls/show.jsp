<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>报表展示</title>
<link href="/css/zi.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/ext-all.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
</head>
<body>
<SCRIPT LANGUAGE="javascript">

  //归档
  function SaveToDB()
  {
        document.all["SaveForm"].submit();
        return true;
  }
  //将EXCEL文件存在本地
  function SaveToFile()
  {
    myactive.SaveToFile();
    return true;
  }
  
  //将EXCEL文件存在本地
  function PrintFile()
  {
    myactive.PrintExcel();
    return true;
  }

  //关闭或打开EXCEL
  function CloseOrOpen()
  {
    if(BCO.value=="打开Excel")
    {
        myactive.ReloadExcel();
        BCO.value="关闭Excel"
    }
    else
    {
        myactive.CloseExcel();
        BCO.value="打开Excel"
    }
    return true;
  }
</SCRIPT>
<table width="100%" border="0" cellspacing="0" cellpadding="3" >
<tr align=center>
   <td width="100%" background="/images/bar_bg.gif" align=left>
      &nbsp;<Input type="button" value="保存在本地" name="B1" onclick="javascript:SaveToFile()">
      &nbsp;<Input type="button" value="关闭Excel" name="BCO" onclick="javascript:CloseOrOpen()">
      &nbsp;<Input type="button" value="&nbsp;打印&nbsp;" name="PE" onclick="javascript:PrintFile()">
      <ww:if test="#session['IsGuidang']==1">
      &nbsp;<Input type="button" value="审核" name="B2" onclick="javascript:SaveToDB()">
      </ww:if>
   </td>
</tr>
<table>

<center>
<!--
<script language="JavaScript">
    //window.location = "<ww:property value="#session['UrlPath']"/>";
</script>
 -->
<table border="0" width="100%" height="90%">
  <tr><td width="100%"  colspan="2" align="center">
    <OBJECT
      id="myactive"
      classid="clsid:8140C99B-072A-4461-9A72-14983C2C3252"
      codebase="/activeX/JSReport.cab#version=2,2,19,1"
	  width="100%"
      height="100%"
    >
    <param name="RepName" Value='<ww:property value="#session['hostStr']"/><ww:property value="#session['UrlPath']"/><ww:property value="#session['UrlFile']"/>'>
    <param name="DataString" Value=''>
    
    <param name="ParamString" Value='<ww:property value="#session['PString']"/>'>
    <param name="SavePath" Value='<ww:property value="#session['url']"/>'>
    <IFRAME name="FailShow"  src="/activeX/failshow.htm" allowTransparency="true" frameborder=0 scrolling=NO width=100% height=100%></IFRAME>
    </OBJECT>

    <script language="JavaScript">
      //显示EXCEL文件
      myactive.InitForm();
    </script>
  </td></tr>

</table>
</center>
<form name="SaveForm"  method="POST" action="excel_auditing.jsp">
<input type='hidden' name="ExcelData" value="<ww:property value="#session['str_exceldata']"/>">

<input type='hidden' name="chname" value="<ww:property value="#session['qryInfo'][1]"/>">
<input type='hidden' name="source_seq" value="">
<input type='hidden' name="js_month" value="<ww:property value="#session['str_param']"/>">
<input type='hidden' name="report_type" value="<ww:property value="#session['qryInfo'][0]"/>">
<input type='hidden' name="model_file" value="<ww:property value="#session['qryInfo'][3]"/>">
<input type='hidden' name="owner" value="<ww:property value="#session['username']"/>">
<input type='hidden' name="filepath" value="<ww:property value="#session['filepath']"/>">
<input type='hidden' name="generate_time" value="">
<input type='hidden' name="address" value="">
<input type='hidden' name="memo" value="">

</form>
</body>
</html>
