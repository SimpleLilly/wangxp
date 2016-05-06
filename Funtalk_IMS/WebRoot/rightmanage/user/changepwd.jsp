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
<title>��������</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/ext-all.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=path%>/ext/Select.js"></script>
</head>
<body>
<script type="text/javascript">
Ext.onReady(function(){
	Ext.QuickTips.init();//��ʾtip
	Ext.form.Field.prototype.msgTarget='side';//��ʾ�ķ�ʽ��ö��ֵΪ"qtip","title","under","side",id(Ԫ��id)

	Ext.apply(Ext.form.VTypes,{
    	password:function(val,field){//valָ������ı���ֵ��fieldָ����ı������
			if(field.confirmTo){//confirmTo�������Զ�������ò�����һ��������������������idֵ
				var pwd=Ext.get(field.confirmTo);//ȡ��confirmTo���Ǹ�id��ֵ
				return (val==pwd.getValue());
			}
			return true;
    	}
	});
	Ext.apply(Ext.form.VTypes,{//��֤���ּ���ĸ���!
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
       title:'�޸�����',
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
         fieldLabel:'������',
         allowBlank:false,
         name:'txt_pass'
         },
         {xtype:'textfield',
         id:"pass1",
         inputType:"password",
         fieldLabel:'������',
         vtype:'alphanum2',
         vtypeText:"���������ּ���ĸ����ϣ�",
         allowBlank:false,
	     minLength: 6,   
		 maxLength: 30, 
         name:'txt_pass1'
         },
         {xtype:'textfield',
         id:"pass2",
         inputType:"password",
         fieldLabel:'ȷ��������',
         allowBlank:false,
         name:'txt_pass2',
         vtype:"password",//�Զ������֤����
         vtypeText:"�������벻һ�£�",
         confirmTo:"pass1"//Ҫ�Ƚϵ�����һ���������id
         }
       ],
		buttons: [
			{text: '����',handler:function(){
			var f = form1.getForm();
				if(!f.isValid()){
				Ext.Msg.alert('��ʾ','����������,����!');
				return;
				}
				form1.getForm().submit({
					url: '<%=path%>/rightmanage/changepwd!tochangepwd.jspa',
					success: function(form, action){
						Ext.Msg.alert('�ɹ�',action.result.message);
					},
					failure: function(form, action){
						Ext.Msg.alert('����',action.result.message);   
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