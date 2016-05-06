<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
String path = request.getContextPath();
 %>    
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>角色信息管理</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/myext.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/Multiselect.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all-debug.js"></script>
<script type="text/javascript" src="<%=path%>/ext/Multiselect.js"></script>
<script type="text/javascript" src="<%=path%>/ext/DDView.js"></script>
<script type="text/javascript" src="<%=path%>/ext/TreeCheckNodeUI.js"></script>

</head>
<body>
<div id="treeTabPanel"></div>
</body>
<script type="text/javascript">
Ext.onReady(function() {
    
	<ww:property value="roleNodeDataJs" escape="false"/>
  
    function rightsave(){//保存事件
    	//alert(treeTabPanel.items);
		var arr = Ext.DomQuery.select("input[type=checkbox]")
		//var arr = document.all["check"];
		//alert(arr.length);
		var requestStr = "";
		for(i = 0; i < arr.length; i++){
			//alert(arr[i].value +","+arr[i].checked);
			if(arr[i].checked){
				requestStr += arr[i].value + ";";
			}else{
				//requestStr += arr[i].value + ";0"+ "|";
			}
		}
		//var requestStr = "L1,L212";
		requestStr = requestStr.substring(0,requestStr.length-1);
		//alert(requestStr);
        Ext.Ajax.request({
            url : '<%=path%>/rightmanage/saveRights.jspa',
            params: {roleid:'<ww:property value="roleid"/>',
            		 rights:requestStr
            		},
            success : function(response) {
                var res = Ext.decode(response.responseText);
                if(!res.success){
                	//cumulate_plan.clearInvalid();
                	//cumulate_plan.markInvalid('已有相同的累进计划标识,请重新输入');
                	Ext.Msg.alert('提示','配置角色权限失败');
                }else{
                	Ext.Msg.alert('提示','配置角色权限成功');
                }
            },
            failure : function() {

            }
        });	
		
    }  
});
function noerror(){
	return true;
}
window.onerror=noerror; 
</script>
</html>