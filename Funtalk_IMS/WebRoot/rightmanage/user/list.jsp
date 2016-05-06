<%@ page language="java" contentType="text/html; charset=GB2312"
    pageEncoding="GB2312"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
String path = request.getContextPath();
 %>    
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<title>用户信息管理</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/myext.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-extends.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN.js"></script>
<script type="text/javascript">
Ext.onReady(function() {
    var sm = new Ext.grid.CheckboxSelectionModel();//多选框控件
    var defaultpagesize=15;//在这里定义默认页面大小
    //数据集控件
    var store = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({url:'<%=path%>/rightmanage/UsersJsonData.jspa'}),
        reader: new Ext.data.JsonReader({
		    totalProperty: 'totalCount',
		    root: 'rows',
		  fields: [
                'username', 
                'usernamecn',
                {name:'cityName',mapping:'cityList.cityName'},
                {name:'longCode',mapping:'cityList.longCode'},             
                {name:'rolename',mapping:'TUserRoles',convert :function(o){
			    	var rStr = '';
			    	for(var i = 0; i < o.length; i++){
			    		rStr += o[i].TRole.rolename;
			    		if(i < o.length - 1)
			    			rStr +='<br>';
			    	}
			    	return rStr;  
				}},
				{name:'roleid',mapping:'TUserRoles',convert :function(o){
			    	var rStr = '';
			    	for(var i = 0; i < o.length; i++){
			    		rStr += o[i].TRole.roleid;
			    		if(i < o.length - 1)
			    			rStr +=',';
			    	}
			    	return rStr;  
				}},   
				'phone',
				'email',
				'state',
				'memo'
            ]}),
        baseParams:{sort:'username',dir:'asc'},//默认首字段排序
        remoteSort: true
    });
    var cityListStore = new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
						            ///////////['755 ','深圳'],['999 ','香港']
									<ww:iterator value="cityList" status="colstatus">
						            ['<ww:property value="longCode"/>','<ww:property value="cityName"/>']<ww:if test="!#colstatus.last">,</ww:if>
						            </ww:iterator>
						    		]
						});
	var roleListStore = new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									<ww:iterator value="roleList" status="colstatus">
						            	['<ww:property value="roleid"/>','<ww:property value="rolename"/>']<ww:if test="!#colstatus.last">,</ww:if>
						            </ww:iterator>
						    		]
						});					
	//表格列信息
    var cm = new Ext.grid.ColumnModel([
    	new Ext.grid.RowNumberer(),
		sm,
		{
           header: "用户ID",
           dataIndex: 'username',
           width: 80,
           sortable: true
        },{
           header: "用户名称",
           dataIndex: 'usernamecn',
           width: 80,
           sortable: true
        },{
           header: "用户所属地",
           dataIndex: 'cityName',
           width: 80,
           sortable: true
        },{
           header: "角色名称",
           dataIndex: 'rolename',
           width: 90,
           sortable: true
        },{
           header: "用户手机号",
           dataIndex: 'phone',
           width: 80,
           sortable: true
        },{
           header: "用户邮箱",
           dataIndex: 'email',
           width: 110,
           sortable: true
        },{
           header: "用户状态",
           dataIndex: 'state',
           
           width: 80,
           sortable: true,
           renderer:function(value){
				if (value == '0') {
				    return "<span style='color:green;font-weight:bold;'>正常</span>";
				} else if (value == '1') {
				    return "<span style='color:red;font-weight:bold;'>锁定</span>";
				} else {
					return "<span style='color:red;font-weight:bold;'>未知状态</span>";
				}
           }
        },{
           header: "备注",
           dataIndex: 'memo',
           sortable: true
        }

        ]);
    //cm.defaultSortable = true; 

	//可编辑表格控件
    var grid = new Ext.grid.GridPanel({
        width:800,
        autoHeight:true,
        autoScroll:true,
        id:'users-grid',
        //clicksToEdit:1,
        el:'users-grid',
        title:'用户信息管理',
        store: store,
        cm: cm,
        //trackMouseOver:false,
        sm: sm,
        tbar: new Ext.PagingToolbar({
            pageSize: defaultpagesize,
            store: store,
            displayInfo: true,
            displayMsg: '共{2}条,显示第{0}条到{1}条记录',
            emptyMsg: "无数据显示",
            items:[
            	{id:"ps",xtype:"numberfield",maxLength:3,width:25,value:defaultpagesize},'-',
                {text:"查询",iconCls:'search',handler:openquery},'-',
                {text:"新建",iconCls:'user_add',handler:openadd},'-',
                {text:"编辑",iconCls:'user_edit',handler:openedit},'-',
                {text:"删除",iconCls:'user_delete',handler:deldata},'-'
            ]
        })
    });
    
    if (grid.width < document.body.offsetWidth)
    {
    	//grid.width=817;
    	grid.width = document.body.offsetWidth-17;
    }
    
    grid.render();
    store.load({params:{start:0, limit:defaultpagesize}});//加载数据

    //自定义页面大小
    Ext.get("ps").on("keyup",function (obj,e ){
    	if(window.event.keyCode==13){
    		defaultpagesize=Ext.getCmp("ps").getValue();
    		store.load({params:{start:0,limit:defaultpagesize}});
    		grid.getTopToolbar().pageSize=defaultpagesize;
    	}
    })

    //打开查询窗口
    function openquery(){
    	querywindow.show();
    }
    
    var queryform = new Ext.FormPanel({//查询表单
        frame:true,
        labelAlign:'right',
        labelWidth:180,
        items: [
    			{
    				xtype:'textfield',
    				fieldLabel: "用户ID",
    				name: 'username'
    			},{
    				xtype:'textfield',
    				fieldLabel: "用户名称",
    				name: 'usernamecn'
    			},{
	                   xtype:'combo',
	                   fieldLabel: '用户所属地',
	                   hiddenName : 'longCode',
	                   name: 'cityName',
	                   width:125,
	                   store: cityListStore,
					   displayField:'value',
					   //typeAhead: true,
					   valueField : 'key',
					   value:'',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true//,
					   //selectOnFocus:true,
					   //editable:true
	                   
	               },{
	                   xtype:'combo',
	                   fieldLabel: '角色',
	                   hiddenName : 'roleid',
	                   name: 'rolename',
	                   width:125,
	                   store: roleListStore,
					   displayField:'value',
					   //typeAhead: true,
					   valueField : 'key',
					   value:'',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true//,
					   //selectOnFocus:true,
					   //editable:true
	                   
	               },{
    				xtype:'textfield',
    				fieldLabel: "用户手机号",
    				name: 'phone'
    			},{
    				xtype:'textfield',
    				fieldLabel: "用户邮箱",
    				name: 'email'
    			},{
	                   xtype:'combo',
	                   fieldLabel: '状态',
	                   hiddenName : 'state',
	                   name: 'statecn',
	                   width:125,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									['0','正常'],
									['1','锁定']
						    		]
						}),
					   displayField:'value',
					   //typeAhead: true,
					   valueField : 'key',
					   value:'',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true//,
					   //selectOnFocus:true,
					   //editable:true
	                   
	               },{
    				xtype:'textfield',
    				fieldLabel: "备注",
    				name: 'memo'
    			}
        	]
    });   
    
    var querywindow = new Ext.Window({//查询窗口
        title: '查询',
        width: 400,
        draggable:true,
        closable:true,
        shadow:true,
        animCollapse:true,
        autoScroll:true,
        maskDisabled:false,
        modal:true,
        resizable:false,
        plain:true,
        buttonAlign:'center',
		items:queryform,
        buttons: [{text: '确认',handler:querydata},{text: '重置',handler:queryreset},{text: '关闭',handler:queryhidden}]
    });
    
    querywindow.on("beforeclose",function(p){//窗口关闭事件
    	p.hide();
    })
    function querydata(){//查询窗口的确认按钮事件
    	query=queryform.getForm().getValues(true);
    	store.baseParams.query=query
    	store.reload({params:{start:0,limit:defaultpagesize}});
    	querywindow.hide();
    }
    function queryreset(){//查询窗口的重置按钮事件
    	queryform.getForm().getEl().dom.reset();
    }
    function queryhidden(){//查询窗口的关闭按钮事件
     	querywindow.hide();
    }
    
    //打开添加窗口
    function openadd(){
    	addwindow.show();
    }
    
    var addform = new Ext.FormPanel({//查询表单
        frame:true,
        labelAlign:'right',
        labelWidth:180,
        items: [
    			{
    				xtype:'textfield',
    				fieldLabel: "用户ID",
    				name: 'username',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "用户名称",
    				name: 'usernamecn',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "密码",
    				inputType:'password',
    				name: 'userpwd',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "重复密码",
    				inputType:'password',
    				name: 'repwd',
    				allowBlank : false
    			},{
	                   xtype:'combo',
	                   fieldLabel: '用户所属地',
	                   hiddenName : 'longCode',
	                   name: 'cityName',
	                   width:125,
	                   store: cityListStore,
					   displayField:'value',
					   //typeAhead: true,
					   valueField : 'key',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true,
    				   allowBlank : false//,
					   //selectOnFocus:true,
					   //editable:true
	                   
	               },{
	                   xtype:'combo',
	                   fieldLabel: '角色',
	                   hiddenName : 'roleids',
	                   name: 'rolename',
	                   width:125,
	                   store: roleListStore,
	                   //multiSelect:true,
					   displayField:'value',
					   //typeAhead: true,
					   valueField : 'key',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true,
    				   allowBlank : false//,
					   //selectOnFocus:true,
					   //editable:true
	                   
	               },{
    				xtype:'textfield',
    				fieldLabel: "用户手机号",
    				name: 'phone',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "用户邮箱",
    				name: 'email',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "备注",
    				name: 'memo'
    			}
        	]
    });   
    
    var addwindow = new Ext.Window({//添加窗口
        title: '新建用户',
        width: 500,
        draggable:true,
        closable:true,
        shadow:true,
        animCollapse:true,
        autoScroll:true,
        maskDisabled:false,
        modal:true,
        resizable:false,
        plain:true,
        buttonAlign:'center',
		items:addform,
        buttons: [{text: '确认',handler:adddata},{text: '重置',handler:addreset},{text: '关闭',handler:addhidden}]
    });
    
    addwindow.on("beforeclose",function(p){//窗口关闭事件
    	p.hide();
    })
    
    function adddata(){//添加窗口的确认按钮事件
    	if(addform.form.isValid()){
    		if(addform.form.findField('userpwd').getValue() != addform.form.findField('repwd').getValue()){
    			Ext.Msg.alert('提示','两次输入的密码不同');
    			return;
    		}
    		
			addform.form.submit({
				url:'<%=path%>/rightmanage/addUser.jspa',
				method:'post',
				waitMsg:'正在处理,请稍候......',
				success:function(form, action){
					//var sql = action.result.sql;
					store.reload({params:{start:0,limit:defaultpagesize}});
					addwindow.hide();
			    	//sql_window.show();
				},
				failure:function(form, action){
					Ext.Msg.alert('提示','添加失败');
				}
			});   	
    	}else{
    		Ext.Msg.alert('提示','输入有误,请检查');
    	}
    }
    
    function addreset(){//查询窗口的重置按钮事件
    	addform.getForm().getEl().dom.reset();
    }
    
    function addhidden(){//查询窗口的关闭按钮事件
     	addwindow.hide();
    }
    
	function deldata(){//删除数据
		var rs1 = grid.getSelectionModel().getSelections();// 返回值为 Record 类型的数组
        if(rs1.length==0)
        {
        	Ext.Msg.alert('提示','请选择要删除的用户');
        	return;//判断记录集是否为空，为空返回
        }

        Ext.MessageBox.confirm('提示', '你真的要删除该用户信息吗?', 
        	function(btn) {
            	if(btn == 'yes') 
                {
                	var count=0;
                	var ids = '';
                	for (i = 0;i < rs1.length; i++)
                	{
                		ids += rs1[i].get('username')+',';
	            	}
           	        Ext.Ajax.request({
						url: '<%=path%>/rightmanage/delUsers.jspa',
						success: function(request){
                                	for (i = 0;i < rs1.length; i++){
                                		store.remove(rs1[i]);
                                	}
                              },
						failure: function(){
                                  Ext.Msg.alert('错误','删除时出现未知错误.');   
                              },
						params: {ids:ids} 
					});
                }
            });
	}    
    
    //打开编辑窗口
    var editform;
    var editwindow;
    function openedit(){
    	var rs1 = grid.getSelectionModel().getSelections();// 返回值为 Record 类型的数组
        if(rs1.length == 0)
        {
        	Ext.Msg.alert('提示','请选择要编辑的用户');
        	return;//判断记录集是否为空，为空返回
        }else if(rs1.length > 1){
        	Ext.Msg.alert('提示','请选择一个用户编辑');
        	return;
        }
        
	    editform = new Ext.FormPanel({//查询表单
	        frame:true,
	        labelAlign:'right',
	        labelWidth:180,
	        items: [
	    			{
	    				xtype:'textfield',
	    				fieldLabel: "用户ID",
	    				name: 'username',
	    				value : rs1[0].get('username'),
	    				readOnly : true
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "用户名称",
	    				name: 'usernamecn',
	    				value : rs1[0].get('usernamecn'),
	    				allowBlank : false
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "密码",
	    				inputType:'password',
	    				name: 'userpwd',
	    				value : '******',
	    				allowBlank : false
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "重复密码",
	    				inputType:'password',
	    				name: 'repwd',
	    				value : '******',
	    				allowBlank : false
	    			},{
		                   xtype:'combo',
		                   fieldLabel: '用户所属地',
		                   hiddenName : 'longCode',
		                   name: 'cityName',
		                   width:125,
		                   store: cityListStore,
						   displayField:'value',
						   //typeAhead: true,
						   valueField : 'key',
						   value : rs1[0].get('longCode'),
						   mode: 'local',
						   triggerAction: 'all',
						   forceSelection:true,
	    				   allowBlank : false//,
						   //selectOnFocus:true,
						   //editable:true
		                   
		               },{
		                   xtype:'combo',
		                   fieldLabel: '角色',
		                   hiddenName : 'roleids',
		                   name: 'rolename',
		                   width:125,
		                   store: roleListStore,
						   displayField:'value',
						   //typeAhead: true,
						   valueField : 'key',
						   value:rs1[0].get('roleid'),
						   mode: 'local',
						   triggerAction: 'all',
						   forceSelection:true,
	    				   allowBlank : false//,
						   //selectOnFocus:true,
						   //editable:true
		                   
		               },{
	    				xtype:'textfield',
	    				fieldLabel: "用户手机号",
	    				name: 'phone',
	    				value:rs1[0].get('phone'),
	    				allowBlank : false
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "用户邮箱",
	    				name: 'email',
	    				value:rs1[0].get('email'),
	    				allowBlank : false
	    			},{
	                   xtype:'combo',
	                   fieldLabel: '状态',
	                   hiddenName : 'state',
	                   name: 'statecn',
	                   width:125,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									['0','正常'],
									['1','锁定']
						    		]
						}),
					   displayField:'value',
					   //typeAhead: true,
					   valueField : 'key',
					   value:rs1[0].get('state'),
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true//,
					   //selectOnFocus:true,
					   //editable:true
	                   
	               },{
	    				xtype:'textfield',
	    				fieldLabel: "备注",	    				
	    				name: 'memo',
	    				value:rs1[0].get('memo')
	    			}
	        	]
	    });     

	    editwindow = new Ext.Window({//添加窗口
	        title: '编辑用户',
	        width: 500,
	        draggable:true,
	        closable:true,
	        shadow:true,
	        animCollapse:true,
	        autoScroll:true,
	        maskDisabled:false,
	        modal:true,
	        resizable:false,
	        plain:true,
	        buttonAlign:'center',
			items:editform,
	        buttons: [{text: '确认',handler:editdata},{text: '重置',handler:editreset},{text: '关闭',handler:edithidden}]
	    });
	    
	    editwindow.on("beforeclose",function(p){//窗口关闭事件
	    	p.hide();
	    });
	    
        editwindow.show();
    }
    

    
    function editdata(){//添加窗口的确认按钮事件
    	if(editform.form.isValid()){
    		if(editform.form.findField('userpwd').getValue() != editform.form.findField('repwd').getValue()){
    			Ext.Msg.alert('提示','两次输入的密码不同');
    			return;
    		}
			editform.form.submit({
				url:'<%=path%>/rightmanage/editUser.jspa',
				method:'post',
				waitMsg:'正在处理,请稍候......',
				success:function(form, action){
					//var sql = action.result.sql;
					store.reload({params:{start:0,limit:defaultpagesize}});
					editwindow.hide();
			    	//sql_window.show();
				},
				failure:function(form, action){
					Ext.Msg.alert('提示','更新失败');
				}
			});		    	
    	}else{
    		Ext.Msg.alert('提示','输入有误,请检查');
    	}
    }
    
    function editreset(){//查询窗口的重置按钮事件
    	editform.getForm().reset();
    }
    
    function edithidden(){//查询窗口的关闭按钮事件
     	editwindow.hide();
    }    
});
</script>
</head>
<body>
<div id="users-grid"></div>
</body>
</html>