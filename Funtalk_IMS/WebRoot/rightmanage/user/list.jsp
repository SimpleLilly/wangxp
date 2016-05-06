<%@ page language="java" contentType="text/html; charset=GB2312"
    pageEncoding="GB2312"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
String path = request.getContextPath();
 %>    
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<title>�û���Ϣ����</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/myext.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-extends.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN.js"></script>
<script type="text/javascript">
Ext.onReady(function() {
    var sm = new Ext.grid.CheckboxSelectionModel();//��ѡ��ؼ�
    var defaultpagesize=15;//�����ﶨ��Ĭ��ҳ���С
    //���ݼ��ؼ�
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
        baseParams:{sort:'username',dir:'asc'},//Ĭ�����ֶ�����
        remoteSort: true
    });
    var cityListStore = new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
						            ///////////['755 ','����'],['999 ','���']
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
	//�������Ϣ
    var cm = new Ext.grid.ColumnModel([
    	new Ext.grid.RowNumberer(),
		sm,
		{
           header: "�û�ID",
           dataIndex: 'username',
           width: 80,
           sortable: true
        },{
           header: "�û�����",
           dataIndex: 'usernamecn',
           width: 80,
           sortable: true
        },{
           header: "�û�������",
           dataIndex: 'cityName',
           width: 80,
           sortable: true
        },{
           header: "��ɫ����",
           dataIndex: 'rolename',
           width: 90,
           sortable: true
        },{
           header: "�û��ֻ���",
           dataIndex: 'phone',
           width: 80,
           sortable: true
        },{
           header: "�û�����",
           dataIndex: 'email',
           width: 110,
           sortable: true
        },{
           header: "�û�״̬",
           dataIndex: 'state',
           
           width: 80,
           sortable: true,
           renderer:function(value){
				if (value == '0') {
				    return "<span style='color:green;font-weight:bold;'>����</span>";
				} else if (value == '1') {
				    return "<span style='color:red;font-weight:bold;'>����</span>";
				} else {
					return "<span style='color:red;font-weight:bold;'>δ֪״̬</span>";
				}
           }
        },{
           header: "��ע",
           dataIndex: 'memo',
           sortable: true
        }

        ]);
    //cm.defaultSortable = true; 

	//�ɱ༭���ؼ�
    var grid = new Ext.grid.GridPanel({
        width:800,
        autoHeight:true,
        autoScroll:true,
        id:'users-grid',
        //clicksToEdit:1,
        el:'users-grid',
        title:'�û���Ϣ����',
        store: store,
        cm: cm,
        //trackMouseOver:false,
        sm: sm,
        tbar: new Ext.PagingToolbar({
            pageSize: defaultpagesize,
            store: store,
            displayInfo: true,
            displayMsg: '��{2}��,��ʾ��{0}����{1}����¼',
            emptyMsg: "��������ʾ",
            items:[
            	{id:"ps",xtype:"numberfield",maxLength:3,width:25,value:defaultpagesize},'-',
                {text:"��ѯ",iconCls:'search',handler:openquery},'-',
                {text:"�½�",iconCls:'user_add',handler:openadd},'-',
                {text:"�༭",iconCls:'user_edit',handler:openedit},'-',
                {text:"ɾ��",iconCls:'user_delete',handler:deldata},'-'
            ]
        })
    });
    
    if (grid.width < document.body.offsetWidth)
    {
    	//grid.width=817;
    	grid.width = document.body.offsetWidth-17;
    }
    
    grid.render();
    store.load({params:{start:0, limit:defaultpagesize}});//��������

    //�Զ���ҳ���С
    Ext.get("ps").on("keyup",function (obj,e ){
    	if(window.event.keyCode==13){
    		defaultpagesize=Ext.getCmp("ps").getValue();
    		store.load({params:{start:0,limit:defaultpagesize}});
    		grid.getTopToolbar().pageSize=defaultpagesize;
    	}
    })

    //�򿪲�ѯ����
    function openquery(){
    	querywindow.show();
    }
    
    var queryform = new Ext.FormPanel({//��ѯ��
        frame:true,
        labelAlign:'right',
        labelWidth:180,
        items: [
    			{
    				xtype:'textfield',
    				fieldLabel: "�û�ID",
    				name: 'username'
    			},{
    				xtype:'textfield',
    				fieldLabel: "�û�����",
    				name: 'usernamecn'
    			},{
	                   xtype:'combo',
	                   fieldLabel: '�û�������',
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
	                   fieldLabel: '��ɫ',
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
    				fieldLabel: "�û��ֻ���",
    				name: 'phone'
    			},{
    				xtype:'textfield',
    				fieldLabel: "�û�����",
    				name: 'email'
    			},{
	                   xtype:'combo',
	                   fieldLabel: '״̬',
	                   hiddenName : 'state',
	                   name: 'statecn',
	                   width:125,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									['0','����'],
									['1','����']
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
    				fieldLabel: "��ע",
    				name: 'memo'
    			}
        	]
    });   
    
    var querywindow = new Ext.Window({//��ѯ����
        title: '��ѯ',
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
        buttons: [{text: 'ȷ��',handler:querydata},{text: '����',handler:queryreset},{text: '�ر�',handler:queryhidden}]
    });
    
    querywindow.on("beforeclose",function(p){//���ڹر��¼�
    	p.hide();
    })
    function querydata(){//��ѯ���ڵ�ȷ�ϰ�ť�¼�
    	query=queryform.getForm().getValues(true);
    	store.baseParams.query=query
    	store.reload({params:{start:0,limit:defaultpagesize}});
    	querywindow.hide();
    }
    function queryreset(){//��ѯ���ڵ����ð�ť�¼�
    	queryform.getForm().getEl().dom.reset();
    }
    function queryhidden(){//��ѯ���ڵĹرհ�ť�¼�
     	querywindow.hide();
    }
    
    //����Ӵ���
    function openadd(){
    	addwindow.show();
    }
    
    var addform = new Ext.FormPanel({//��ѯ��
        frame:true,
        labelAlign:'right',
        labelWidth:180,
        items: [
    			{
    				xtype:'textfield',
    				fieldLabel: "�û�ID",
    				name: 'username',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "�û�����",
    				name: 'usernamecn',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "����",
    				inputType:'password',
    				name: 'userpwd',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "�ظ�����",
    				inputType:'password',
    				name: 'repwd',
    				allowBlank : false
    			},{
	                   xtype:'combo',
	                   fieldLabel: '�û�������',
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
	                   fieldLabel: '��ɫ',
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
    				fieldLabel: "�û��ֻ���",
    				name: 'phone',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "�û�����",
    				name: 'email',
    				allowBlank : false
    			},{
    				xtype:'textfield',
    				fieldLabel: "��ע",
    				name: 'memo'
    			}
        	]
    });   
    
    var addwindow = new Ext.Window({//��Ӵ���
        title: '�½��û�',
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
        buttons: [{text: 'ȷ��',handler:adddata},{text: '����',handler:addreset},{text: '�ر�',handler:addhidden}]
    });
    
    addwindow.on("beforeclose",function(p){//���ڹر��¼�
    	p.hide();
    })
    
    function adddata(){//��Ӵ��ڵ�ȷ�ϰ�ť�¼�
    	if(addform.form.isValid()){
    		if(addform.form.findField('userpwd').getValue() != addform.form.findField('repwd').getValue()){
    			Ext.Msg.alert('��ʾ','������������벻ͬ');
    			return;
    		}
    		
			addform.form.submit({
				url:'<%=path%>/rightmanage/addUser.jspa',
				method:'post',
				waitMsg:'���ڴ���,���Ժ�......',
				success:function(form, action){
					//var sql = action.result.sql;
					store.reload({params:{start:0,limit:defaultpagesize}});
					addwindow.hide();
			    	//sql_window.show();
				},
				failure:function(form, action){
					Ext.Msg.alert('��ʾ','���ʧ��');
				}
			});   	
    	}else{
    		Ext.Msg.alert('��ʾ','��������,����');
    	}
    }
    
    function addreset(){//��ѯ���ڵ����ð�ť�¼�
    	addform.getForm().getEl().dom.reset();
    }
    
    function addhidden(){//��ѯ���ڵĹرհ�ť�¼�
     	addwindow.hide();
    }
    
	function deldata(){//ɾ������
		var rs1 = grid.getSelectionModel().getSelections();// ����ֵΪ Record ���͵�����
        if(rs1.length==0)
        {
        	Ext.Msg.alert('��ʾ','��ѡ��Ҫɾ�����û�');
        	return;//�жϼ�¼���Ƿ�Ϊ�գ�Ϊ�շ���
        }

        Ext.MessageBox.confirm('��ʾ', '�����Ҫɾ�����û���Ϣ��?', 
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
                                  Ext.Msg.alert('����','ɾ��ʱ����δ֪����.');   
                              },
						params: {ids:ids} 
					});
                }
            });
	}    
    
    //�򿪱༭����
    var editform;
    var editwindow;
    function openedit(){
    	var rs1 = grid.getSelectionModel().getSelections();// ����ֵΪ Record ���͵�����
        if(rs1.length == 0)
        {
        	Ext.Msg.alert('��ʾ','��ѡ��Ҫ�༭���û�');
        	return;//�жϼ�¼���Ƿ�Ϊ�գ�Ϊ�շ���
        }else if(rs1.length > 1){
        	Ext.Msg.alert('��ʾ','��ѡ��һ���û��༭');
        	return;
        }
        
	    editform = new Ext.FormPanel({//��ѯ��
	        frame:true,
	        labelAlign:'right',
	        labelWidth:180,
	        items: [
	    			{
	    				xtype:'textfield',
	    				fieldLabel: "�û�ID",
	    				name: 'username',
	    				value : rs1[0].get('username'),
	    				readOnly : true
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "�û�����",
	    				name: 'usernamecn',
	    				value : rs1[0].get('usernamecn'),
	    				allowBlank : false
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "����",
	    				inputType:'password',
	    				name: 'userpwd',
	    				value : '******',
	    				allowBlank : false
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "�ظ�����",
	    				inputType:'password',
	    				name: 'repwd',
	    				value : '******',
	    				allowBlank : false
	    			},{
		                   xtype:'combo',
		                   fieldLabel: '�û�������',
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
		                   fieldLabel: '��ɫ',
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
	    				fieldLabel: "�û��ֻ���",
	    				name: 'phone',
	    				value:rs1[0].get('phone'),
	    				allowBlank : false
	    			},{
	    				xtype:'textfield',
	    				fieldLabel: "�û�����",
	    				name: 'email',
	    				value:rs1[0].get('email'),
	    				allowBlank : false
	    			},{
	                   xtype:'combo',
	                   fieldLabel: '״̬',
	                   hiddenName : 'state',
	                   name: 'statecn',
	                   width:125,
	                   store: new Ext.data.SimpleStore({
						    fields: ['key', 'value'],					    
						    data : [
									['0','����'],
									['1','����']
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
	    				fieldLabel: "��ע",	    				
	    				name: 'memo',
	    				value:rs1[0].get('memo')
	    			}
	        	]
	    });     

	    editwindow = new Ext.Window({//��Ӵ���
	        title: '�༭�û�',
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
	        buttons: [{text: 'ȷ��',handler:editdata},{text: '����',handler:editreset},{text: '�ر�',handler:edithidden}]
	    });
	    
	    editwindow.on("beforeclose",function(p){//���ڹر��¼�
	    	p.hide();
	    });
	    
        editwindow.show();
    }
    

    
    function editdata(){//��Ӵ��ڵ�ȷ�ϰ�ť�¼�
    	if(editform.form.isValid()){
    		if(editform.form.findField('userpwd').getValue() != editform.form.findField('repwd').getValue()){
    			Ext.Msg.alert('��ʾ','������������벻ͬ');
    			return;
    		}
			editform.form.submit({
				url:'<%=path%>/rightmanage/editUser.jspa',
				method:'post',
				waitMsg:'���ڴ���,���Ժ�......',
				success:function(form, action){
					//var sql = action.result.sql;
					store.reload({params:{start:0,limit:defaultpagesize}});
					editwindow.hide();
			    	//sql_window.show();
				},
				failure:function(form, action){
					Ext.Msg.alert('��ʾ','����ʧ��');
				}
			});		    	
    	}else{
    		Ext.Msg.alert('��ʾ','��������,����');
    	}
    }
    
    function editreset(){//��ѯ���ڵ����ð�ť�¼�
    	editform.getForm().reset();
    }
    
    function edithidden(){//��ѯ���ڵĹرհ�ť�¼�
     	editwindow.hide();
    }    
});
</script>
</head>
<body>
<div id="users-grid"></div>
</body>
</html>