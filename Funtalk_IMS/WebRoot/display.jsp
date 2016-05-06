<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragrma", "no-cache");
	response.setDateHeader("Expires", 0);
	
String path = request.getContextPath();
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><ww:text name="title"/></title>
<link rel="stylesheet" type="text/css" href="<%=path%>/css/publicStyle_jf.css">
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<style type="text/css">
<!--
.x-grid3-cell-text-visible .x-grid3-cell-inner{overflow:visible;padding:3px 3px 3px 5px;white-space:normal;}
-->
</style>
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/TabCloseMenu.js"></script>
</head>
<body>
<script language="JavaScript">
Ext.onReady(function() {

 var topPanel = new Ext.Panel({ 
  		contentEl:'top'
 });
 
 var rolename='';//角色名称
 <ww:iterator value="#session['currentUser'].getTUserRoles()" status="colstatus">
 rolename +='<ww:property value="TRole.getRolename()"/>  '
 </ww:iterator>
 
var form1 = new Ext.grid.PropertyGrid({
       id:"userInfoPanel",
       title:'<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.title"/></ww:i18n>',
       collapsible:true,
       frame:true,
       width:400,
       hideHeaders:true,
       autoHeight:true,
       cls:'x-grid3-cell-text-visible',
       renderTo:'user_info',
       source:{
       "1.<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.1"/></ww:i18n>:":"<ww:property value="#session['currentUser'].getUsername()"/>",
       "2.<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.2"/></ww:i18n>:":"<ww:property value="#session['currentUser'].getUsernamecn()"/>",
       "3.<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.3"/></ww:i18n>:":rolename,
       "4.<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.4"/></ww:i18n>:":"<ww:property value="#session['currentUser'].getPhone()"/>",
       "5.<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.5"/></ww:i18n>:":"<ww:property value="#session['currentUser'].getEmail()"/>",
       "6.<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.6"/></ww:i18n>:":"<ww:property value="#session['currentUser'].getCityList().getCityName()"/>",
       "7.<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.7"/></ww:i18n>:":"<ww:date name="#session['currentUser'].getPwdLastModifyDate()" format="yyyy-MM-dd" />",
       "8.<ww:i18n name="i18n_%{#session['ww_locale']}"><ww:text name="displaycount.jsp.8"/></ww:i18n>:":"<ww:property value="#session['currentUser'].getLastLoginInfo()"/>"
       }
    });
    form1.el.center();
	form1.on("beforeedit",function( e ){//只读
		e.cancel = true;
    })
    
 var bottomPanel = new Ext.Panel({
  		contentEl:'bottom'
 });
})
</script>

<div id="main_top">
<table width="90%"  border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td ><p>&nbsp;</p>
    <p><img src="<%=path%>/images/welcome.gif" width="200" height="30"><ww:text name="welcometitle"/> </p></td>
  </tr>
  <tr>
    <td>
    	<table width="100%" height="2px"  border="0" cellspacing="0" cellpadding="0">
      		<tr>
        		<td background="<%=path%>/images/dotline.gif"></td>
      		</tr>
    	</table>
	</td>
  </tr>
</table>
</div>
<div id='user_info'></div>
<div id="bottom">
<table width="90%"  border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td align="right" valign="bottom" height="170px">
    <p>
    <img src="<%=path%>/images/sc_new_img_8.gif" hspace="0" vspace="0">
    <img src="<%=path%>/images/sc_new_img_9.gif" hspace="0" vspace="0">
    <img src="<%=path%>/images/sc_new_img_10.gif" hspace="0" vspace="0">
    </p></td>
  </tr>
</table>
</div>
</body>
</html>
