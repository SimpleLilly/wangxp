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
<title>详细</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/myext.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN.js"></script>
</head>
<body>
<ww:set name="right" value="#session['right']"/>
<script type="text/javascript">
Ext.onReady(function(){
	    var mainPanel = new Ext.form.FormPanel({
    	renderTo:"x-panel",
        title: '详细',
        items: [
        	{
        		xtype:'textarea',
        		id:'ta',
        		hideLabel:true,
        		height:380,
        		width:500,
        		value:window.dialogArguments
        	}
        	],
        buttons: [{
            text: '确定',
            handler:function(){
            	window.returnValue = Ext.getCmp('ta').getValue();
				window.close();
            }
        },{
            text: '取消',
            handler:function(){
				window.close();            
            }
        }]
    });
});

</script>
<div id="x-panel"></div>
</body>
</html>