<!-- tiantao -->
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="ww" uri="/webwork" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>报表默认保存路径设置</title>
	<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
	<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
	<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN2.js"></script>
  </head>
  <script type="text/javascript">
  Ext.onReady(function(){
  	    var simple = new Ext.FormPanel({
        labelWidth: 110, // label settings here cascade unless overridden
        url:'',
        frame:true,
        title: '报表默认保存路径设置',
        bodyStyle:'padding:5px 5px 0',
        //width: 350,
        defaults: {width: '100%'},
        defaultType: 'textfield',

        items: [
        	{
            	width: '600',
                fieldLabel: '新的默认保存路径',
                value:'<ww:property value="url"/>',
                id:'url'
            }
        ],

        buttons: [{
        	handler:save,
            text: '保存'
        },{
        	handler:reset,
            text: '重置'
        }]
    });
	function save(){
		var url = Ext.getCmp("url").getValue();
		url = url.replace(/\\/g,"\\\\");
		var conn = Ext.lib.Ajax.getConnectionObject().conn;
        conn.open('POST', '<%=path%>/report/settle!setSavePath.jspa?url='+url,false);
        conn.send(null);
        var res = Ext.decode(conn.responseText);
        if(res.success){
        	Ext.Msg.alert("提示","操作成功!");
        }else{
        	Ext.Msg.alert("提示","操作失败!");
	    }
        
	}
	function reset(){
		Ext.getCmp("url").setValue("D:\\");
	}
	
    simple.render(document.body);
  })
  
  </script>
  <body>
  </body>
</html>
