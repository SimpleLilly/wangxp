<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
String path = request.getContextPath();
 %>    
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统日志</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/myext.css" />
<style type="text/css">
<!--
.x-grid3-cell-text-visible .x-grid3-cell-inner{overflow:visible;padding:3px 3px 3px 5px;white-space:normal;}
.x-selectable, .x-selectable * {-moz-user-select: text!important;-khtml4strict-user-select: text!important;}
-->
</style>
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-extends.js"></script>
<script type="text/javascript" src="<%=path%>/ext/Select.js"></script>
<script type="text/javascript">
Ext.onReady(function() {
    var defaultpagesize=15;//在这里定义默认页面大小
    //数据集控件
    var store = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({url:'<%=path%>/rightmanage/LogJsonData.jspa'}),
        reader: new Ext.data.JsonReader({
		    totalProperty: 'totalCount',
		    root: 'rows',
		  fields: [
                'seq', 
                'username',
                'operateTime',
				'operateType',
				'operateSrc',
				'operateDest',
				'memo'
            ]}),
        baseParams:{sort:'seq',dir:'desc'},//默认首字段排序
        remoteSort: true
    });
    
    var opType=[['C','添加'],['U','修改'],['R','查询'],['D','删除'],['A','审核'],['L','登陆']];
	var opDest=[['1','成功'],['0','失败']];
			
	//表格列信息
    var cm = new Ext.grid.ColumnModel([
		{
           header: "编号",
           dataIndex: 'seq',
           width: 40,
           sortable: true
        },{
           header: "用户编号",
           dataIndex: 'username',
           width: 80,
           sortable: true
        },{
           header: "操作日期",
           dataIndex: 'operateTime',
           width: 120,
           sortable: true
        },{
           header: "操作类型",
           dataIndex: 'operateType',
           width: 80,
           renderer:function(value){
			for(i=0;i<opType.length;i++)
			{
				if ( value == opType[i][0] )
					return opType[i][1];
			}
           },
           sortable: true
        },{
           header: "操作源",
           dataIndex: 'operateSrc',
           width: 80,
           sortable: true
        },{
           header: "操作结果",
           dataIndex: 'operateDest',
           width: 60,
           renderer:function(value){
			for(i=0;i<opDest.length;i++)
			{
				if ( value == opDest[i][0] )
					return opDest[i][1];
			}
           },
           sortable: true
        },{
           header: "备注",
           width: 320,
           dataIndex: 'memo',
           sortable: true
        }

        ]);
    //cm.defaultSortable = true; 

	//表格控件
    var grid = new Ext.grid.GridPanel({
        width:800,
        autoHeight:true,
        autoScroll:true,
        id:'users-grid',
        el:'log-grid',
        title:'系统日志',
        cls:'x-grid3-cell-text-visible',
        store: store,
        cm: cm,
        viewConfig: {
      	templates: {
         cell: new Ext.XTemplate(
            '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} x-selectable {css}" style="{style}" tabIndex="0" {cellAttr}>',
            '<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>',
            '</td>'
         )}},
        tbar: new Ext.PagingToolbar({
            pageSize: defaultpagesize,
            store: store,
            displayInfo: true,
            displayMsg: '共{2}条,显示第{0}条到{1}条记录',
            emptyMsg: "无数据显示",
            items:[
            	{id:"ps",xtype:"numberfield",maxLength:3,width:25,value:defaultpagesize},'-',
                {text:"查询",iconCls:'search',handler:openquery}
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
    				fieldLabel: "编号",
    				name: 'seq'
    			},{
    				xtype:'textfield',
    				fieldLabel: "用户编号",
    				name: 'username'
    			},{
    				xtype:'datefield',
    				format:'Ymd',
    				vtypeText:'请输入正确的日期格式',
    				fieldLabel: "操作日期",
    				name: 'operate_time'
    			},{
	                   xtype:'select',
	                   fieldLabel: '操作类型',
	                   hiddenName : 'operate_type',
	                   name: 'operate_type_name',
	                   width:125,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : opType
						}),
					   displayField:'value',
					   valueField : 'key',
					   value:'',
					   mode: 'local',
					   triggerAction: 'all',
					   editable:false,
					   forceSelection:true
	                   
	               },{
    				xtype:'textfield',
    				fieldLabel: "操作源",
    				name: 'operate_src'
    			},{
	                   xtype:'select',
	                   fieldLabel: '操作结果',
	                   hiddenName : 'operate_dest',
	                   name: 'operate_dest_name',
	                   width:125,
	                   editable:false,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : opDest
						}),
					   displayField:'value',
					   valueField : 'key',
					   value:'',
					   mode: 'local',
					   triggerAction: 'all',
					   forceSelection:true
	                   
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
});
</script>
</head>
<body>
<div id="log-grid"></div>
</body>
</html>