<%@ page language="java" contentType="text/html; charset=GB2312"
    pageEncoding="GB2312"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
String path = request.getContextPath();
 %>    
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<title>��ɫ��Ϣ����</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/myext.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/Multiselect.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all-debug.js"></script>
<script type="text/javascript" src="<%=path%>/ext/Multiselect.js"></script>
<script type="text/javascript" src="<%=path%>/ext/DDView.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=path%>/ext/TreeCheckNodeUI.js"></script>
<script type="text/javascript">
Ext.onReady(function() {
  treePanel = new Ext.tree.TreePanel({
   id:'treePanel',
   el:'layout_menu',
   border : false,
   rootVisible:false,
   autoHeight: true,
   autoScroll:true,
   //animate:true,//����Ч��
   //onlyLeafCheckable: true,
   checkModel: "cascade",
   //enableDD:false,//true������ק
   containerScroll: true,
   animate:false,
   loader: new Ext.tree.TreeLoader({
   			dataUrl:'http://localhost:8080/newCommonManage/rightmanage/rightConfig.jspa?roleid=1',
            baseAttrs: { uiProvider: Ext.ux.TreeCheckNodeUI } //��� uiProvider ����
        }),
   collapseFirst:true   
 });
 treePanel.on("check",function(node,checked){
 	//this.fireEvent('checkchange', this.node, cb.checked); 
 	node.attributes.checked = true;
 	alert(node.attributes.checked);
 	//alert(node.text+" = "+checked + " "+node.id + " "+node.attributes.buttons_def)
 }); //ע��"check"�¼�
 /*
treePanel.on('checkchange', function(node, checked) { 
 node.expand(); 
 node.attributes.checked = checked; 
 node.eachChild(function(child) {  
  child.ui.toggleCheck(checked);  
  child.attributes.checked = checked;  
  child.fireEvent('checkchange', child, checked); 
  });
 }, treePanel); */
     // set the root node
 var root = new Ext.tree.AsyncTreeNode({
     text: 'Ext JS',
     draggable:false,
     id:'L1'
 });
 treePanel.setRootNode(root);
 
 treePanel.render();
 //treePanel.root.appendChild(eval(firstroot));//����һ��Ҫ��,����˵�������
 //treePanel.expandAll();
});
</script>
</head>
<body>
<div id="layout_menu"></div>
</body>
</html>