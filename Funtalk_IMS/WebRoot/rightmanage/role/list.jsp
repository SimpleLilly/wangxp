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
<script type="text/javascript">
Ext.onReady(function() {
    var sm = new Ext.grid.CheckboxSelectionModel();//多选框控件
    var defaultpagesize=150;//在这里定义默认页面大小
    //数据集控件
    var store = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({url:'<%=path%>/rightmanage/RolesJsonData.jspa'}),
        reader: new Ext.data.JsonReader({
		    totalProperty: 'totalCount',
		    root: 'rows',
		  fields: [
                'roleid', 
                'rolename',
                'memo',
                'roletype',
				{name:'username',mapping:'userRoles',convert :function(o){
			    	var rStr = '';
			    	for(var i = 0; i < o.length; i++){
			    		rStr += o[i].TUser.username;
			    		if(i < o.length - 1)
			    			rStr +=',';
			    		if((i + 1) % 4 == 0){
			    			rStr +='<br>';
			    		}
			    	}
			    	return rStr;
				}},
				{name:'usernameArr',mapping:'userRoles',convert :function(o){
			    	var rArr = new Array(o.length);
			    	for(var i = 0; i < o.length; i++){
			   
			    		rArr[i] = [o[i].TUser.username,o[i].TUser.username];
			    	}
			    	return rArr;
				}},
				{name:'TRoleLevelsArr',mapping:'TRoleLevels',convert :function(o){
			    	var rArr = new Array(o.length);
			    	for(var i = 0; i < o.length; i++){
			   
			    		rArr[i] = [o[i].roleid,o[i].rolename];
			    	}
			    	return rArr;
				}},{name:'roleLevelname',mapping:'TRoleLevels',convert :function(o){
			    	var rStr = '';
			    	//alert(o.length);
			    	for(var i = 0; i < o.length; i++){
			    		rStr += o[i].rolename;
			    		if(i < o.length - 1)
			    			rStr +=',';
			    		if((i + 1) % 4 == 0){
			    			rStr +='<br>';
			    		}
			    	}
			    	return rStr;
				}}
            ]}),
        baseParams:{sort:'roleid',dir:'asc'},//默认首字段排序
        remoteSort: true
    });
    
    var userListStore = new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									<ww:iterator value="userList" status="colstatus">
						            	['<ww:property value="username"/>','<ww:property value="username"/>']<ww:if test="!#colstatus.last">,</ww:if>
						            </ww:iterator>
						    		]
						});   
	var proleListStore = new Ext.data.SimpleStore({
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
           header: "角色ID",
           dataIndex: 'roleid',
           width: 80,
           sortable: true
        },{
           header: "角色名称",
           dataIndex: 'rolename',
           width: 90,
           sortable: true
        },{
           header: "上级角色",
           dataIndex: 'roleLevelname',
           width: 90,
           sortable: true
        },{
           header: "所属该角色用户",
           dataIndex: 'username',
           width: 220
        },{
           header: "角色类型",
           dataIndex: 'roletype',
           width: 80,
           sortable: true,
           renderer:function(value){
				if (value == '0') {
				    return "省级角色";
				}else {
					return "地市级角色";
				}
           }
        },{
           header: "备注",
           dataIndex: 'memo',
           sortable: true,
           width:120
        }

        ]);
    //cm.defaultSortable = true; 

	//可编辑表格控件
    var grid = new Ext.grid.GridPanel({
        width:800,
        autoHeight:true,
        autoScroll:true,
        id:'roles-grid',
        //clicksToEdit:1,
        el:'roles-grid',
        title:'角色信息管理',
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
                {text:"删除",iconCls:'user_delete',handler:deldata},'-',
                {text:"权限配置",iconCls:'user_edit',handler:openRight},'-'
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
    				fieldLabel: "角色ID",
    				name: 'roleid'
    			},{
    				xtype:'textfield',
    				fieldLabel: "角色名称",
    				name: 'rolename'
    			},{
	                   xtype:'combo',
	                   fieldLabel: '角色类型',
	                   hiddenName : 'roletype',
	                   name: 'roletypecn',
	                   width:125,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									['0','省级角色'],
									['1','地市级角色']
						    		]
						}),
					   displayField:'value',
					   valueField : 'key',
					   value:'',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true
	                   
	               },{
	                   xtype:'combo',
	                   fieldLabel: '上级角色',
	                   hiddenName : 'parentrole',
	                   name: 'prole',
	                   width:125,
	                   store: proleListStore,
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
    			},{
	                   xtype:'combo',
	                   fieldLabel: '所属该角色用户',
	                   hiddenName : 'username',
	                   name: 'user',
	                   width:125,
	                   store: userListStore,
					   displayField:'value',
					   //typeAhead: true,
					   valueField : 'key',
					   value:'',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true//,
					   //selectOnFocus:true,
					   //editable:true
	                   
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
    	query = queryform.getForm().getValues(true);
    	store.baseParams.query = query
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
    				fieldLabel: "角色名称",
    				name: 'rolename',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "角色备注",
    				name: 'memo'
    			},{
	                   xtype:'combo',
	                   fieldLabel: '角色类型',
	                   hiddenName : 'roletype',
	                   name: 'roletypecn',
	                   width:125,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									['0','省级角色'],
									['1','地市级角色']
						    		]
						}),
					   displayField:'value',
					   valueField : 'key',
					   value:'',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true
	                   
	               },{
					xtype:"itemselector",
					name:"proleselector",
					fieldLabel:"上级角色",
					dataFields:["code", "desc"],
					fromData:[
							<ww:iterator value="roleList" status="colstatus">
				            	['<ww:property value="roleid"/>','<ww:property value="rolename"/>']<ww:if test="!#colstatus.last">,</ww:if>
				            </ww:iterator>
						],
					toData:[],
					msWidth:120,
					autoScroll:true,
					msHeight:100,
					valueField:"code",
					displayField:"desc",
					imagePath:"<%=path%>/ext/resources/images/shared/icons/",
					//switchToFrom:true,
					toLegend:"已选栏",
					fromLegend:"可选栏",
					toTBar:[{
						text:"取消选择",
						handler:function(){
							var i = addform.getForm().findField("proleselector");
							i.reset.call(i);
						}
					}]
				},{
					xtype:"itemselector",
					name:"userNames",
					fieldLabel:"角色所属用户",
					dataFields:["code", "desc"],
					fromData:[
							<ww:iterator value="userList" status="colstatus">
				            	['<ww:property value="username"/>','<ww:property value="username"/>']<ww:if test="!#colstatus.last">,</ww:if>
				            </ww:iterator>
						],
					toData:[],
					msWidth:120,
					autoScroll:true,
					msHeight:200,
					valueField:"code",
					displayField:"desc",
					imagePath:"<%=path%>/ext/resources/images/shared/icons/",
					//switchToFrom:true,
					toLegend:"已选栏",
					fromLegend:"可选栏",
					toTBar:[{
						text:"取消选择",
						handler:function(){
							var i = addform.getForm().findField("userNames");
							i.reset.call(i);
						}
					}]
				}
        	]
    });   
    
    var addwindow = new Ext.Window({//添加窗口
        title: '新建角色',
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
			addform.form.submit({
				url:'<%=path%>/rightmanage/addRole.jspa',
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
		var i = addform.getForm().findField("userNames");
		i.reset.call(i);
    }
    
    function addhidden(){//查询窗口的关闭按钮事件
     	addwindow.hide();
    }
    
	function deldata(){//删除数据
		var rs1 = grid.getSelectionModel().getSelections();// 返回值为 Record 类型的数组
        if(rs1.length==0)
        {
        	Ext.Msg.alert('提示','请选择要删除的角色');
        	return;//判断记录集是否为空，为空返回
        }

        Ext.MessageBox.confirm('提示', '你真的要删除该角色信息吗?', 
        	function(btn) {
            	if(btn == 'yes') 
                {
                	var count=0;
                	var ids = '';
                	for (i = 0;i < rs1.length; i++)
                	{
                		ids += rs1[i].get('roleid')+',';
	            	}
           	        Ext.Ajax.request({
						url: '<%=path%>/rightmanage/delRoles.jspa',
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
    function escapeExist(existArr){
    	var userArr = [
							<ww:iterator value="userList" status="colstatus">
				            	['<ww:property value="username"/>','<ww:property value="username"/>']<ww:if test="!#colstatus.last">,</ww:if>
				            </ww:iterator>
						];
		//var existArr = rs1[0].get('usernameArr');	
		
		var retArr = new Array();		
		for(var i = 0,k = 0; i < userArr.length; i++){
			var flag = false;
			for(var j = 0; j < existArr.length; j++){
				if(userArr[i][0] == existArr[j][0]){
					flag = true;
					break;
				}
			}
			if(!flag){
				retArr[k] = userArr[i];
				k++;
			}
		}
		return retArr;
    }
    function proleescapeExist(existArr){
    	var userArr = [
							<ww:iterator value="roleList" status="colstatus">
				            	['<ww:property value="roleid"/>','<ww:property value="rolename"/>']<ww:if test="!#colstatus.last">,</ww:if>
				            </ww:iterator>
						];
		//var existArr = rs1[0].get('usernameArr');	
		
		var retArr = new Array();		
		for(var i = 0,k = 0; i < userArr.length; i++){
			var flag = false;
			for(var j = 0; j < existArr.length; j++){
				if(userArr[i][0] == existArr[j][0]){
					flag = true;
					break;
				}
			}
			if(!flag){
				retArr[k] = userArr[i];
				k++;
			}
		}
		return retArr;
    }
    var editform;
    var editwindow;
    function openedit(){
    	var rs1 = grid.getSelectionModel().getSelections();// 返回值为 Record 类型的数组
        if(rs1.length == 0)
        {
        	Ext.Msg.alert('提示','请选择要编辑的角色');
        	return;//判断记录集是否为空，为空返回
        }else if(rs1.length > 1){
        	Ext.Msg.alert('提示','请选择一个角色编辑');
        	return;
        }
        
	    editform = new Ext.FormPanel({//查询表单
	        frame:true,
	        labelAlign:'right',
	        labelWidth:180,
	        items: [
	    			{
	    				xtype:'hidden',
	    				fieldLabel: "角色id",
	    				name: 'roleid',
	    				value : rs1[0].get('roleid')
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "角色名称",
	    				name: 'rolename',
	    				value : rs1[0].get('rolename'),
	    				allowBlank : false
	    			},{
	                   xtype:'combo',
	                   fieldLabel: '角色类型',
	                   hiddenName : 'roletype',
	                   name: 'roletypecn',
	                   width:125,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									['0','省级角色'],
									['1','地市级角色']
						    		]
						}),
					   displayField:'value',
					   valueField : 'key',
					   value : rs1[0].get('roletype'),
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true
	                   
	               },{
					xtype:"itemselector",
					name:"proleselector",
					fieldLabel:"上级角色",
					dataFields:["code", "desc"],
					fromData:proleescapeExist(rs1[0].get('TRoleLevelsArr')),
					toData:rs1[0].get('TRoleLevelsArr'),//
					msWidth:120,
					autoScroll:true,
					msHeight:100,
					valueField:"code",
					displayField:"desc",
					imagePath:"<%=path%>/ext/resources/images/shared/icons/",
					//switchToFrom:true,
					toLegend:"已选栏",
					fromLegend:"可选栏",
					toTBar:[{
						text:"取消选择",
						handler:function(){
							var i = editform.getForm().findField("proleselector");
							i.reset.call(i);
						}
						}]
					},{
					xtype:"itemselector",
					name:"userNames",
					fieldLabel:"角色所属用户",
					dataFields:["code", "desc"],
					fromData:escapeExist(rs1[0].get('usernameArr')),
					toData:rs1[0].get('usernameArr'),//
					msWidth:120,
					autoScroll:true,
					msHeight:200,
					valueField:"code",
					displayField:"desc",
					imagePath:"<%=path%>/ext/resources/images/shared/icons/",
					//switchToFrom:true,
					toLegend:"已选栏",
					fromLegend:"可选栏",
					toTBar:[{
						text:"取消选择",
						handler:function(){
							var i = editform.getForm().findField("userNames");
							i.reset.call(i);
						}
						}]
					},{
	    				xtype:'textfield',
	    				fieldLabel: "备注",
	    				name: 'memo',
	    				value:rs1[0].get('memo')
	    			}
	        	]
	    });     

	    editwindow = new Ext.Window({//添加窗口
	        title: '编辑角色',
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


    
    function editdata(){//编辑窗口的确认按钮事件
    	if(editform.form.isValid()){
			editform.form.submit({
				url:'<%=path%>/rightmanage/editRole.jspa',
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
    
    //打开权限窗口
    var roleid;
    function openRight(){
    	var rs1 = grid.getSelectionModel().getSelections();// 返回值为 Record 类型的数组
        if(rs1.length == 0)
        {
        	Ext.Msg.alert('提示','请选择要配置权限的角色');
        	return;//判断记录集是否为空，为空返回
        }else if(rs1.length > 1){
        	Ext.Msg.alert('提示','请选择一个角色配置权限');
        	return;
        }
        roleid = rs1[0].get('roleid');
		
        
		var xframe="<iframe id=x_frame src='<%=path%>/rightmanage/rightConfig.jspa?roleid="+roleid+"' MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0></iframe>";
		var xwindow = new Ext.Window({//弹出窗口
			title: '权限配置',
	        width: 700,
	        height:400,
	       	draggable:true,
	       	closable:true,
	       	shadow:true,
	       	animCollapse:true,
	       	maximizable:true,
	       	//autoScroll:true,
	       	maskDisabled:false,
	       	//x:0,
	       	//y:0,
	       	collapsible : true,
	       	modal:true,
	       	resizable:true,
	        html: xframe,
	        plain:true,
	        buttonAlign:'center',
	        listeners:{
	        	resize :{fn:function(obj,width,height){
	        	    var xfram=document.getElementById('x_frame');
	 				xfram.height=height;
	 				xfram.width=width;
	        	}
	        	}
	        }
	    });
	    xwindow.show();
	    var xfram=document.getElementById('x_frame');
	 	xfram.height=xwindow.getSize().height;
	 	xfram.width=xwindow.getSize().width;
	    
	    //rightwindow.on("beforeclose",function(p){//窗口关闭事件
	    //	p.hide();
	    	//treeTabPanel.destroy();
	    //});
	    
        //rightwindow.show();
        //<ww:iterator value="topMenu" status="rowstatus">
		//	treeTabPanel.setActiveTab(<ww:property value="#rowstatus.index"/>);
		//</ww:iterator>
		//treeTabPanel.setActiveTab(0);
    }
    

    
    function treereset(){//重置按钮事件
    	//editform.getForm().reset();
    }
    
    function righthidden(){//关闭按钮事件
     	rightwindow.hide();
     	treeTabPanel.destroy();
    }    
});

</script>
</head>
<body>
<div id="roles-grid"></div>
</body>
</html>