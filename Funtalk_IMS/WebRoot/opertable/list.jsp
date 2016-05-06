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
<title>数据配置</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/myext.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/Select.js"></script>
</head>
<body>


<script type="text/javascript">
Ext.onReady(function(){
	Ext.QuickTips.init();//显示tip
    var sm = new Ext.grid.CheckboxSelectionModel();//多选框控件
    var defaultpagesize=20;//在这里定义默认页面大小
    var query;//查询信息
    //数据集控件
    var store = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({url:'<%=path%>/opertable/GenerateJsonData.jspa?askType=<ww:property value="askType"/>'}),
        reader: new Ext.data.JsonReader({
        	totalProperty: 'totalCount',
            root: 'rows',
            fields: [
            'primaryKey',
            <ww:iterator value="localDataTableCols" status="colstatus">
            	'<ww:property value="columnname"/>'<ww:if test="!#colstatus.last">,</ww:if>
            </ww:iterator>
            <ww:if test="localDataTable.isconfig ==2">,'t_audit_flag'</ww:if>
            ]
        }),
        /////////baseParams:{sort:1,dir:'asc'},//默认首字段排序
        remoteSort: true,//远程排序
        pruneModifiedRecords:true,
        listeners:{
        	beforeload:{fn:checkchangedata},
        	add :{fn:firesave},
        	update:{fn:firesave},
        	remove:{fn:firesave}
        }
    });
    
	//表格列信息
    var cm = new Ext.grid.ColumnModel([
    	new Ext.grid.RowNumberer(),
		sm,
		{
           id:'primaryKey',
           header: "主键",
           dataIndex: 'primaryKey',
           hidden: true
        },
		<ww:iterator value="localDataTableCols" status="colstatus">
    			{
    			<ww:if test="edittype != 2">
    			<ww:if test="linktableid != null && linktableid != \"\" && linktableid != \"0\"">//下拉框
				editor: new Ext.ux.Andrie.Select({
				id:'cb_<ww:property value="columnname"/>',
				<ww:if test="ismuti==1">
					multiSelect:true,
				</ww:if>
				<ww:else>
					multiSelect:false,
				</ww:else>
					store: new Ext.data.SimpleStore({
						fields: ['value', 'descp'],
          				data: <ww:property value="linkTables[#colstatus.index]"/>
          				}),
          			displayField:'descp',
	        		valueField:'value',
	        		hiddenName:'<ww:property value="columnname"/>',
	        		mode:'local',
	        		<ww:if test=" isindex == 1 || isnull==0">
	        			allowBlank: false,
	        			blankText:'该字段不能为空',
	        		</ww:if>
	        		triggerAction:'all',
	        		listClass: 'x-combo-list-small',
	        		editable:false
	        		}),
	        		renderer: function(value,record){
    					var combo = Ext.getCmp('cb_<ww:property value="columnname"/>');
    					var returnValue = value;
   						var valueField = combo.valueField;
    					var idx = combo.store.findBy(function(record) {
        					if(record.get(valueField) == value) {
            				returnValue = record.get(combo.displayField);
            				return true;
        				}
    					});

    					return returnValue;
	        		},
				</ww:if>
    			<ww:elseif test="columntype == 1">//1-数字类型
    			editor: new Ext.form.NumberField({
	        		<ww:if test=" isindex == 1 || isnull==0">
	        			allowBlank: false,
	        		</ww:if>
               		allowNegative: true
           		}),
    			</ww:elseif>
    			<ww:elseif test="columntype == 2">//2-文本类型
    				editor: new Ext.form.TriggerField({
	        		<ww:if test=" isindex == 1 || isnull==0">
	        			allowBlank: false,
	        		</ww:if>
	        		triggerClass:'x-form-date-trigger',
	        		onTriggerClick : function(e){//弹出详细对话框
	        			var cn=window.showModalDialog('detail.jsp',this.value,'dialogHeight:500px;dialogWidth:510px;scroll:no;center:Yes;status:no')
	        			if (cn != null)
							this.setValue(cn);
						},
	        			selectOnFocus:true
           			}),
    			renderer:function(value,p){
 					p.attr = 'ext:qtip="' + value.replace(/\"/g,'\'\'') + '"';
    				return value;
    			},
    			</ww:elseif>
    			<ww:elseif test="columntype == 3">//2-日期类型
					editor: new Ext.form.DateField({
	        		<ww:if test=" isindex == 1 || isnull==0">
	        			allowBlank: false,
	        		</ww:if>
                		format: 'Ymd H:i:s',
                		vtypeText:'请输入正确的日期格式'
            		}),
            		renderer: function(value){
            			if (Date.parseDate(value,'Ymd H:i:s')!=null)
            			{
            				return value;
            			}
            			else
            			{
            				df = new Date(value);
            				return df.format('Ymd H:i:s');
            			}
            		},
            	
    			</ww:elseif>
    			</ww:if>
    			header: '<ww:property value="columncomment"/>',
    			dataIndex: '<ww:property value="columnname"/>',
    			tooltip:'<ww:property value="columncomment"/>',
    			width:100
    			}
    			<ww:if test="!#colstatus.last">,</ww:if>
    		</ww:iterator>
    		<ww:if test="localDataTable.tableid==1 &&#session['currentUser'].getUsername()=='admin'">
			,{//局数据总表的特殊处理
           		id:'colconfig',
           		header: "字段配置",
           		dataIndex: 'TABLEID',
           		width:100,
           		renderer:function(value){
           		if (value=='') return;
           		var checkid = "checkid_"+value;
           		var chkhtml='<img id="'+checkid+'" ext:qtip="" src="../images/ajax-loader.gif">'
           		var chkstr = '';
           		Ext.Ajax.request({
					url: '<%=path%>/opertable/OperTable!checkTable.jspa?askType='+value+'',
					success: function(request){
						var message = request.responseText;
						if (message.substr(0,1)=='0')
						{
							chkstr=message;
						}else
						{
							chkstr=message;
						}
					},
					failure: function(){
						chkstr="未知错误.";
					},
					callback :function(){
						if (chkstr.substr(0,1)=='0')
						{
							Ext.getDom(checkid).src= '../images/check_ok.gif';
							Ext.getDom(checkid).qtip=chkstr.substr(1);
						}else
						{
							Ext.getDom(checkid).src= '../images/check_error.gif';
							Ext.getDom(checkid).qtip=chkstr;
						}
					}
				});
           		return "<a onclick=openwin('字段配置',800,500,'<%=path%>/opertable/tableconfig.jsp?tableid="+value+"') href=#>配置</a>"+chkhtml;
				}
			}
			</ww:if>
    		<ww:if test="localDataTable.isconfig ==2 ">
			,{//审核特殊处理
           		id:'t_audit_flag',
           		header: "审核",
           		dataIndex: 't_audit_flag',
           		width:100,
           		renderer:function(value,metadata ,record){
           			<ww:if test="#request['audit'] != null">
           			if (value=='0')//未审核数据
           				return "<a onclick=auditOne('"+record.get('primaryKey')+"') href=#>审核</a>";
           			else if (value=='1')//审核数据
           				return "<a onclick=cancelAuditOne('"+record.get('primaryKey')+"') href=#>取消审核</a>";
           			</ww:if>
           			<ww:else>
           			if (value=='0')//未审核数据
           				return "未审核";
           			else if (value=='1')//审核数据
           				return "已审核";
           			</ww:else>
				}
			}
			</ww:if>
        ]);
    cm.defaultSortable = true;

	//可编辑表格控件
	var gridTitle='<ww:property value="localDataTable.tablecomment"/>(<ww:property value="localDataTable.tablename"/>)';
    var grid = new Ext.grid.EditorGridPanel({
        width:<ww:property value="localDataTableCols.size*100+100"/>,
        autoHeight:true,
        autoScroll:true,
        id:'edit-grid',
        clicksToEdit:1,
        el:'topic-grid',
        title:gridTitle,
        store: store,
        cm: cm,
        trackMouseOver:false,
        sm: sm,
        tbar: new Ext.PagingToolbar({
            pageSize: defaultpagesize,
            store: store,
            displayInfo: true,
            displayMsg: '共{2}条,显示第{0}条到{1}条记录',
            emptyMsg: "无数据显示",
            items:[
            	{id:"ps",xtype:"numberfield",maxLength:3,width:25,value:defaultpagesize},'-',
                {text:"确定",id:'returnbutton',hidden:true,tooltip:'确定',handler:returndata},'-',
                {text:"查询",tooltip:'查询',iconCls:'search',handler:openquery},'-',
                <ww:if test="#request['add'] != null">
            	{text:"添加",tooltip:'添加',iconCls:'add',handler:insertdata},'-',
            	{text:"复制添加",tooltip:'复制添加',handler:replicateinsertdata},'-',
            	</ww:if>
            	<ww:if test="#request['add'] != null || #request['update'] != null">
            	{text:"保存",tooltip:'保存',iconCls:'save',id:'savebutton',handler:savedata},'-',
            	</ww:if>
            	<ww:if test="#request['del'] != null">
            	{text:"删除",tooltip:'删除',iconCls:'delete',handler:deldata},'-',
            	</ww:if>
            	<ww:if test="#request['import'] != null">
            	{text:"",tooltip:'导入',iconCls:'import',handler:bacthImport},'-',
            	</ww:if>
            	{text:"",tooltip:'导出到EXCEL',iconCls:'export',handler:toExcel},'-'
            ]
        })
    });
    
    if (grid.width < document.body.offsetWidth)
    {
    	//grid.width=817;
    	grid.width = document.body.offsetWidth-17;
    }
    	
    grid.render();
    
    store.removeAll();
    store.baseParams.sort=1;
    store.baseParams.dir="asc";
    
    store.load({params:{start:0, limit:defaultpagesize}});//加载数据
 
	grid.on("validateedit",function( e ){//处理日期类型,返回string 防止点击后未作任何修改时标识为修改后数据
		if (Ext.isDate(e.value)){
			e.value=e.value.format('Ymd H:i:s');
		}
    })
    
    //自定义页面大小
    function resetPagesize(){
    	defaultpagesize=Ext.getCmp("ps").getValue();
    	store.load({params:{start:0,limit:defaultpagesize}});
    	grid.getTopToolbar().pageSize=defaultpagesize;    	
    }
    Ext.get("ps").on("keyup",function (obj,e ){//相应回车事件改变页面大小
    	if(window.event.keyCode==13 && defaultpagesize != Ext.getCmp("ps").getValue() )
    	{
			resetPagesize();
    	}
    })
    Ext.get("ps").on("blur",function (obj){//相应失去焦点事件改变页面大小
    	if ( defaultpagesize != Ext.getCmp("ps").getValue() )
    		{
    			resetPagesize();
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
        	<ww:iterator value="localDataTableCols" status="colstatus">
    			{
    			<ww:if test="isquery != 1">
    				xtype:'hidden',
    			</ww:if>
    			<ww:elseif test="linktableid != null && linktableid != \"\" && linktableid != \"0\"">//下拉框
					xtype:'select',
					<ww:if test="ismuti==1">
					multiSelect:true,
					</ww:if>
					<ww:else>
					multiSelect:false,
					</ww:else>
					store: new Ext.data.SimpleStore({
						fields: ['value', 'descp'],
          				data: <ww:property value="linkTables[#colstatus.index]"/>
          				}),
          			displayField:'descp',
	        		valueField:'value',
	        		hiddenName:'<ww:property value="columnname"/>',
	        		mode:'local',
	        		triggerAction:'all',
	        		listClass: 'x-combo-list-small',
	        		editable:false,
				</ww:elseif>
    			<ww:elseif test="columntype == 1">//1-数字类型
    				xtype:'numberfield',
    			</ww:elseif>
    			<ww:elseif test="columntype == 2">//2-文本类型
    				xtype:'textfield',
    			</ww:elseif>
    			<ww:elseif test="columntype == 3">//2-日期类型
    				xtype:'datefield',
    				format:'Ymd H:i:s',
    				vtypeText:'请输入正确的日期格式',
    				width:140,
    			</ww:elseif>
    				fieldLabel: "<ww:property value="columncomment"/>",
    				name: '<ww:property value="columnname"/>',
    				listeners:{ specialkey : function(field, e) {
						if (e.getKey() == Ext.EventObject.ENTER) {
							querydata();
						}
					}
 					}
    			}
    			<ww:if test="!#colstatus.last">,</ww:if>
    		</ww:iterator>
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
    
    function checkchangedata(){//提示用户需要提交修改后的记录
    	if (store.getModifiedRecords().length>0)
    	{
    		Ext.Msg.show({
    			title:'修改',
    			msg: '您修改/添加了数据后尚未保存,是否要保存?',
    			buttons: Ext.MessageBox.YESNO,
    			fn: function(btn) {
        			if (btn =='yes')//提交所作的修改,并继续
        			{
        				savedata();
        				return false;
        			}
        			if (btn =='no')//放弃所做的修改,并继续
        			{
        				store.rejectChanges();
        				store.reload();
        				return true;
        			}
    			}
			});
			return false;
    	}
    }
    
    function firesave(store,record,operation ){//修改数据提交
    	if (store.getModifiedRecords().length>0){
    		Ext.getCmp('savebutton').setText('<font color=red>保存</font>');//按钮字体置红
    	}else{
    		Ext.getCmp('savebutton').setText('保存');//按钮字体变为正常颜色
    	}
    }
    
    
    grid.on("beforeedit",function( e ){//权限控制时使用,不可编辑表格
    	if (e.record.get('t_audit_flag')=='1')//审核后的数据不能修改
    	{
    		return false;
    	}
    	<ww:if test="#request['update'] == null">
    		if (e.record.get('primaryKey')!='')
    		{
    			return false;//权限控制,只有查询权限,不可编辑表格
    		}
    	</ww:if>
    })
    
    
    var rs = Ext.data.Record.create([
    	{name: 'primaryKey',type: 'string'},
    	<ww:iterator value="localDataTableCols" status="colstatus">
    	{name: '<ww:property value="columnname"/>',type: 'string'}<ww:if test="!#colstatus.last">,</ww:if>
    	</ww:iterator>
      ]);

      
    function insertdata(){//添加记录
        var p = new rs({
        primaryKey:'',
    	<ww:iterator value="localDataTableCols" status="colstatus">
    	<ww:property value="columnname"/>:'<ww:property value="defaultvalue"/>'<ww:if test="!#colstatus.last">,</ww:if>
    	</ww:iterator>
      });
                grid.stopEditing();
                store.insert(0, p);
                grid.startEditing(0, 3);
	}
	
	
	function replicateinsertdata(){//复制添加
		var rs1 = grid.getSelectionModel().getSelections();// 返回值为 Record 类型的数组
        if(rs1.length==0)
        {
        	Ext.Msg.alert('提示','请选择要复制添加的记录');
        	return;//判断记录集是否为空，为空返回
        }
        grid.stopEditing();
        /** 最后一行添加时使用
        var p = new rs({
        primaryKey:'',
    	<ww:iterator value="localDataTableCols" status="colstatus">
    	<ww:property value="columnname"/>:'<ww:property value="defaultvalue"/>'<ww:if test="!#colstatus.last">,</ww:if>
    	</ww:iterator>
      });
        store.add(p);//先加入一个空行,添加完后删除该空行,这种方式才能正常的添加,框架bug?
        **/
        for(i=0;i<rs1.length;i++){
            var p2 = new rs({
            primaryKey:'',
    		<ww:iterator value="localDataTableCols" status="colstatus">
    		<ww:property value="columnname"/>:rs1[i].get('<ww:property value="columnname"/>')<ww:if test="!#colstatus.last">,</ww:if>
    		</ww:iterator>
      		});
	        store.insert(0, p2);
	    }
        //store.remove(p);
        
        grid.startEditing(0, 3);
	}
	

	function deldata(){//删除数据
		var rs1 = grid.getSelectionModel().getSelections();// 返回值为 Record 类型的数组
        if(rs1.length==0)
        {
        	Ext.Msg.alert('提示','请选择要删除的记录');
        	return;//判断记录集是否为空，为空返回
        }
        for(i=0;i<rs1.length;i++)
        {
        	if (rs1[i].get('t_audit_flag')=='1')//审核后的数据不能修改
    		{
    			Ext.Msg.alert('提示','审核后的数据不能删除!');
    			return false;
    		}        	
        }

        Ext.MessageBox.confirm('提示', '你真的要删除该信息吗?', 
        	function(btn) {
            	if(btn == 'yes') 
                {
                	var count=0;
                	for (i=0;i<rs1.length;i++)
                	{
                		if (rs1[i].get('primaryKey')!=""){
                			Ext.Ajax.request({
								url: '<%=path%>/opertable/OperTable!delete.jspa?askType=<ww:property value="askType"/>',
								success: function(request){
                                  	store.remove(rs1[count++]);
                                },
								failure: function(){
                                    Ext.Msg.alert('错误','删除时出现未知错误.');   
                                },
								params: {query:rs1[i].get('primaryKey')} 
							});
	            		}else{
	            			store.remove(rs1[count++]);//没有primaryKey的直接在表格删除.
	            		}
	            	}
	            	//store.reload();
                }
            });
	}
	

	function savedata(){//添加 修改后保存数据
		if (store.getModifiedRecords().length>0){		
			//验证数据
			var rsall = store.getRange()
			for(i = 0;i < rsall.length; i++)
			{
				if (rsall[i].dirty)//修改过的数据
				{
					for(j=3;j<cm.getColumnCount();j++)
					{
					if (cm.getCellEditor(j,i)!=null){
						cm.getCellEditor(j,i).field.setValue(rsall[i].get(cm.getDataIndex(j)));//必须先填充值否则isValid一直为fasle
						if( !cm.getCellEditor(j,i).field.isValid() )
						{
							//Ext.Msg.alert是异步方式执行的,所以将后续动作放到function中!
							Ext.Msg.alert('提示','"'+cm.getColumnHeader(j)+'"字段验证错误,请检查!',function(){grid.startEditing(i,j);});
							return ;
						}
					}
					}
				}
			}
			var rs1 = store.getModifiedRecords();// 返回值为 Record 类型的数组
			var json = []; 
			for (i=0;i<rs1.length;i++){
				json.push(rs1[i].data);
			}

			Ext.Ajax.request({
				url: '<%=path%>/opertable/OperTable!save.jspa?askType=<ww:property value="askType"/>',
				waitMsg: '正在处理,请稍后.....',
				success: function(request){
					var message = request.responseText;
					if (message.substr(0,1)=='0')
					{
						Ext.Msg.alert('提示','保存成功');
						store.commitChanges();
						store.reload();
					}else
					{
						Ext.Msg.alert('提示','保存出错,'+message.substr(1));
					}
				},
				failure: function(){
					Ext.Msg.alert('错误','保存时出现未知错误.');   
				},
				params: {query:(Ext.encode(json))} 
				});
		}else{
			Ext.Msg.alert('提示','没有可以保存的记录');
		}
	}
	
	//外部搜索调用,dialogArguments格式:a1:a2:a3;a1=s为单选,a1=m为多选;a2=控件的value,多选以','号隔开;a3为value所属局数据列名.
	var diaArg = window.dialogArguments;
	var retval = [];
	if (diaArg !=null)
	{
		diaArg = diaArg.split(':');
		diaArg[2] = diaArg[2].toUpperCase();//全部转换为大写
		if (diaArg[1] !='')
			retval = diaArg[1].split(',');//获取值
		if ( diaArg[0] == 's' ) //设置为单选;
			sm.singleSelect=true;
		Ext.getCmp('returnbutton').setVisible(true);//显示确认按钮
		store.on('load',function(obj,records,options){//加载数据时自动选中
			var selectRecord = [];
			for(i=0;i<records.length;i++)
			{
				if ( retval.indexOf(records[i].data[diaArg[2]]) >=0 )
					selectRecord.push(records[i]);
			}
			sm.selectRecords(selectRecord);
		});
		sm.on('rowselect',function(csm,rowIndex,record){
			if ( diaArg[0] == 's' ) //如果是单选清空数据
				retval = [];
			if (retval.indexOf(record.data[diaArg[2]]) == -1)
				retval.push(record.data[diaArg[2]]);
		});
		sm.on('rowdeselect',function(csm,rowIndex,record){
			retval.remove(record.data[diaArg[2]]);
		});
	}
	function returndata(){
		window.returnValue = retval.join(',');
		window.close();
	}
//Excel导出调用
function toExcel(){
	var ub ={
	sort:store.baseParams.sort,
	dir:store.baseParams.dir,
	query:store.baseParams.query
	}
	var url='/opertable/OperTable!toExcel.jspa?askType=<ww:property value="askType"/>&'+Ext.urlEncode(ub);
	location.href = url;
}

});

function auditOne(primaryKey){//审核
	Ext.Ajax.request({
		url: '<%=path%>/opertable/OperTable!audit.jspa?askType=<ww:property value="askType"/>',
		success: function(request){
			Ext.Msg.alert('提示','审核数据成功.'); 
			Ext.getCmp('edit-grid').getStore().reload();
		},
		failure: function(){
			Ext.Msg.alert('错误','审核出现未知错误.');   
		},
		params: {query:primaryKey} 
	});
}
function cancelAuditOne(primaryKey){//取消审核
	Ext.Ajax.request({
		url: '<%=path%>/opertable/OperTable!cancelAudit.jspa?askType=<ww:property value="askType"/>',
		success: function(request){
			Ext.Msg.alert('提示','取消审核数据成功.'); 
			Ext.getCmp('edit-grid').getStore().reload();
		},
		failure: function(){
			Ext.Msg.alert('错误','审核出现未知错误.');   
		},
		params: {query:primaryKey} 
	});
}

function openwin(title,width,height,_href){
	var xframe="<iframe id=x_frame src='"+_href+"' MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0></iframe>";
	var xwindow = new Ext.Window({//弹出窗口
		title: title,
        width: width,
        height:height,
       	draggable:true,
       	closable:true,
       	shadow:true,
       	animCollapse:true,
       	maximizable:true,
       	autoScroll:true,
       	maskDisabled:false,
       	x:0,
       	y:0,
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
}

//批量导入
function bacthImport() {
	var _href = "<%=path%>/opertable/OperTable!toImport.jspa?askType=<ww:property value='askType'/>";
	openwin('导入',800,650,_href);
	}
</script>
<div id="topic-grid"></div>
</body>
</html>