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
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN2.js"></script>
</head>
<body>
<script type="text/javascript">
	Ext.useShims=true;
Ext.onReady(function(){
	//初始提示框
	Ext.Ajax.timeout = 900000; //300秒
	Ext.QuickTips.init();
	vflag = [];//输入项验证
    var table = new Ext.form.FormPanel({
	    id:'table',
	    layout:'table',
	    renderTo:'report',
	    monitorResize:true,
	    autoHeight:true,
	    title: '<ww:property value="query_name"/>',
	    defaults: {
	        // applied to each contained panel
	        //bodyStyle:'padding:2px'
	    },
	    border : false,
	    bodyBorder  : false,
	    layoutConfig: {
	        columns: 1
	    },
	    items:[
	    new Ext.Toolbar({
	    id :'tb',
	    autoWidth :true,
	    items:[
	            <ww:iterator value="paraInfo" status="colstatus">
	            {xtype:'label',text:'　<ww:property value="paraInfo[#colstatus.index][0]"/>:'},
	            {
                	<ww:if test=" paraInfo[#colstatus.index][1] == \"T\" ">
                    	xtype:'textfield',
                    	width:100,
                	</ww:if>
                	<ww:elseif test=" paraInfo[#colstatus.index][1] == \"S\" ">
                		xtype:'textfield',
                		width:100,
                	</ww:elseif>
                	<ww:elseif test=" paraInfo[#colstatus.index][1] == \"D\" ">
    					xtype:'datefield',
    					format:'Ymd',
    					readOnly:true,
    					vtypeText:'请输入正确的日期格式',
    					width:100,
                	</ww:elseif>
                	<ww:elseif test=" paraInfo[#colstatus.index][1] == \"F\" ">
                		xtype:'datefield',
    					format:'Ymd H:i:s',
    					vtypeText:'请输入正确的日期格式',
    					width:150,
                	</ww:elseif>
                	<ww:elseif test=" paraInfo[#colstatus.index][1] == \"H\" ">
                		xtype:'hidden',
                	</ww:elseif>
                	<ww:elseif test=" paraInfo[#colstatus.index][1] == \"C\" ">
                		xtype:'combo',
                		store: new Ext.data.SimpleStore({
						fields: ['value', 'descp'],
          				data: getArray('<ww:property value="paraInfo[#colstatus.index][3]"/>','<ww:property value="paraInfo[#colstatus.index][6]"/>')
          				}),
          				displayField:'descp',
	        			valueField:'value',
	        			mode:'local',
	        			width:getMaxWidth('<ww:property value="paraInfo[#colstatus.index][6]"/>'),
	        			triggerAction:'all',
	        			listClass: 'x-combo-list-small',
	        			editable:false,
                	</ww:elseif>
                	<ww:elseif test=" paraInfo[#colstatus.index][1] == \"CA\" ">
                		xtype:'combo',
                		store: new Ext.data.SimpleStore({
						fields: ['value', 'descp'],
          				data: getArray('<ww:property value="paraInfo[#colstatus.index][3]"/>','<ww:property value="paraInfo[#colstatus.index][6]"/>')
          				}),
          				displayField:'descp',
	        			valueField:'value',
	        			mode:'local',
	        			width:getMaxWidth('<ww:property value="paraInfo[#colstatus.index][6]"/>'),
	        			triggerAction:'all',
	        			listClass: 'x-combo-list-small',
	        			editable:false,
                	</ww:elseif>
        				listeners:{
        					<ww:if test=" paraInfo[#colstatus.index][1] == \"CA\" ">
        					select:{fn:function(combo,record,index ){
        						var sql = "<ww:property value='paraInfo[#colstatus.index][8]'/>";
        						Ext.Ajax.request({
									url: '/report/query!updateCascade.jspa?queryid=<ww:property value="queryid"/>',
									success: function(request){
										Ext.getCmp('<ww:property value='paraInfo[#colstatus.index][7]'/>_1').clearValue();
                            			Ext.getCmp('<ww:property value='paraInfo[#colstatus.index][7]'/>_1').store.loadData(eval(request.responseText));
										Ext.getCmp('<ww:property value='paraInfo[#colstatus.index][7]'/>_1').validate();
									},
									failure: function(){
									Ext.Msg.alert('错误','报表程序错误.');
									},
									params: {query:combo.getValue(),casql:sql} 
								});
        						}
        					},
        					</ww:if>
        					invalid:{fn:function(obj,msg){
        						vflag.push(obj);
        						}
        					},
        					valid:{fn:function(obj){
        						vflag.remove(obj);
        						}
        					}
        				},
                    id: '<ww:property value="paraInfo[#colstatus.index][1]"/>_<ww:property value="#colstatus.index"/>_1',
                	hiddenName:'<ww:property value="paraInfo[#colstatus.index][1]"/>_<ww:property value="#colstatus.index"/>',
                    value: '<ww:property value="paraInfo[#colstatus.index][2]"/>',
                    allowBlank:false
       			},
            	</ww:iterator>
            	{xtype:'label',width:30},
            	{
			   		id:'qrybot',
			   		name:'qrybot',
			   		xtype:'button',
			   		pressed:true,
		       		text: ' 查询 ',
		       		handler:function(){
		       			if (vflag.length>0){
		       				Ext.Msg.alert('提示','请检查输入项');
		       				return;
		       			}
		       			/*字段验证 数据库里配置check_str ex.  
		       			Ext.getCmp('D_1_1').getValue()>Ext.getCmp('D_2_1').getValue()@@开始时间必须大于结束时间
		       			Ext.getCmp('C_0_1').getValue()<900@@请输入大于900的数
		       			第一位字母为输入框的类型，第二位数字为序号，第三位数字为固定的1
		       			*/
		       			<ww:property value="checkStr" escape="false"/>
		       			Ext.get('tb').setWidth(document.body.offsetWidth-18);
						Ext.getDom('x_show').height=document.body.offsetHeight-55;
 						Ext.getDom('x_show').width=document.body.offsetWidth;
		       			Ext.getDom('x_show').src='./xls/wait.jsp';
		       			Ext.Ajax.request({
							url: '/report/show.jspa?queryid=<ww:property value="queryid"/>',
							success: function(request){
								var message = request.responseText;
								chkstr = message.substr(0,1);
								if (chkstr =='1')
								{
									Ext.get('tb').setWidth(document.body.offsetWidth-18);
									Ext.getDom('x_show').height=document.body.offsetHeight-55;
 									Ext.getDom('x_show').width=document.body.offsetWidth;
									Ext.getDom('x_show').src='./xls/blank.jsp';
									Ext.Msg.alert('提示',message.substr(2));  
								}
								else
								{
									Ext.get('tb').setWidth(document.body.offsetWidth-18);
									Ext.getDom('x_show').height=document.body.offsetHeight-55;
 									Ext.getDom('x_show').width=document.body.offsetWidth;
									Ext.getDom('x_show').src='./xls/show.jsp';
								}
							},
							failure: function(){
								Ext.get('tb').setWidth(document.body.offsetWidth-18);
								Ext.getDom('x_show').height=document.body.offsetHeight-55;
 							    Ext.getDom('x_show').width=document.body.offsetWidth;
								Ext.getDom('x_show').src='./xls/blank.jsp';
								Ext.Msg.alert('错误','报表程序错误.');   
							},
							params: {query:table.getForm().getValues(true)} 
						});
		       		}
		      	}
	    ]
	    }),
	    {
    		id: 'content',
    		autoWidth :true,
    		border : false,
	    	bodyBorder  : false,
    		html: "<iframe id='x_show' src='' MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0></iframe>"
	    }]
	});
	
	Ext.get('tb').setWidth(Ext.get('table').getSize().width);
 
	function getArray(v,t)
	{
		val = v.split('|');
		text = t.split('|');
		retval = [];
		for(i=0;i<val.length;i++)
		{
			retval.push([val[i],text[i]])
		}
		return retval
	}
	function getMaxWidth(t)
	{
		text = t.split('|');
		tempwidth = 10;
		for(i=0;i<text.length;i++)
		{
			len = text[i].replace(/[^\x00-\xff]/g,"**").length
			if (len>tempwidth)
			{
				tempwidth = len;
			}
				
		}
		return tempwidth*7;
	}
	function checkInput()
	{
		<ww:property value="checkStr" escape="false"/>
		return true;
	}
});
</script>
<div id="report"></div>
</body>
</html>