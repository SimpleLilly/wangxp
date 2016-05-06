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
<title>数据配置</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/ext-all.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=path%>/ext/Select.js"></script>
</head>
<body>
<script type="text/javascript">
Ext.onReady(function(){
	Ext.QuickTips.init();//显示tip
	Ext.form.Field.prototype.msgTarget='side';//提示的方式，枚举值为"qtip","title","under","side",id(元素id)

	Ext.apply(Ext.form.VTypes,{
    	password:function(val,field){//val指这里的文本框值，field指这个文本框组件
			if(field.confirmTo){//confirmTo是我们自定义的配置参数，一般用来保存另外的组件的id值
				var pwd=Ext.get(field.confirmTo);//取得confirmTo的那个id的值
				return (val==pwd.getValue());
			}
			return true;
    	}
	});
	Ext.apply(Ext.form.VTypes,{//验证数字及字母组合!
    	alphanum2:function(val,field){
			if (!/[a-z]/i.test(val) || !/\d/.test(val))
  			{
  				return false;
  			}
			return true;
    	}
	});

	var form1 = new Ext.form.FormPanel({
       id:"passPanel",
       frame:true,
       labelWidth:80,
       title:'修改密码',
       frame:true,
       width:400,
       hideHeaders:true,
       autoHeight:true,
       bodyStyle:"padding:5px 5px 0",
       renderTo :'form',
       items:[
         {xtype:'textfield',
         id:"pass",
         inputType:"password",
         fieldLabel:'旧密码',
         allowBlank:false,
         name:'txt_pass'
         },
         {xtype:'textfield',
         id:"pass1",
         inputType:"password",
         fieldLabel:'新密码',
         vtype:'alphanum2',
         vtypeText:"必须是数字加字母的组合！",
         allowBlank:false,
	     minLength: 6,   
		 maxLength: 30, 
         name:'txt_pass1'
         },
         {xtype:'textfield',
         id:"pass2",
         inputType:"password",
         fieldLabel:'确认新密码',
         allowBlank:false,
         name:'txt_pass2',
         vtype:"password",//自定义的验证类型
         vtypeText:"两次密码不一致！",
         confirmTo:"pass1"//要比较的另外一个的组件的id
         }
       ],
		buttons: [
			{text: '保存',handler:function(){
			var f = form1.getForm();
				if(!f.isValid()){
				Ext.Msg.alert('提示','您输入有误,请检查!');
				return;
				}
				form1.getForm().submit({
					url: '<%=path%>/rightmanage/changepwd!tochangepwd.jspa',
					success: function(form, action){
						Ext.Msg.alert('成功',action.result.message);
					},
					failure: function(form, action){
						Ext.Msg.alert('错误',action.result.message);   
					}
				});
			}}
        ]
    });
    form1.el.center();
})
</script>
<center>
<div id="form"></div>
<center>
</body>
</html>