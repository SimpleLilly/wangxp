<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragrma", "no-cache");
	response.setDateHeader("Expires", 0);
	
String path = request.getContextPath();
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><ww:text name="title"/></title>
<link rel="stylesheet" type="text/css" href="<%=path%>/css/publicStyle_jf.css">

<link rel="stylesheet" type="text/css" href="<%=path%>/ext/resources/css/xtheme-silverCherry.css" />
<script type="text/javascript" src="<%=path%>/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=path%>/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=path%>/ext/TabCloseMenu.js"></script>

</head>
  
  <body>
<script language="JavaScript">
<ww:property value="extMenuInfo"/>

 var firstroot;//默认菜单
 var treeroot1 = new Ext.tree.AsyncTreeNode({
    text:'',
    id:'treeroot1',
    expanded:true,
    children:eval(firstroot)
    });

Ext.onReady(function() {

 var northPanel = new Ext.Panel({ // 最顶上那块
		region: 'north', 
  		contentEl:'top',
        split: true,
        collapseMode:'mini',//可以折叠
        height: 97,
        minSize: 97,
        maxSize: 97 
 });

 var treePanel = new Ext.Panel({ // 左边菜单
        id:'treePanel',
        title: '<ww:text name="login.jsp.operator"/>:<ww:property value="#session['currentUser'].getUsername()"/>',
        region: 'west',
        contentEl: 'layout_menu',
        split: true,
        border: true,
        containerScroll: true,
        autoScroll:true,
        //collapseMode:'mini',
        collapsible: true,
        width: 200,
        minSize: 200,
        maxSize: 400 
 });

 mainPanel = new Ext.TabPanel({//主内容区域
  id:'mainPanel',
  region:'center',
  autoHeight: false,
  resizeTabs:true, // turn on tab resizing
  enableTabScroll:true,
  resizeTabs:true,
  activeTab: 0,
  items:[{
    id: 'index',
    tabTip: '<ww:text name="login.jsp.indexname"/>',
    title: '<ww:text name="login.jsp.indexname"/>',
    html: '<iframe id="x-index" src="<%=path%>/display.jsp" MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0></iframe>',
    closable:false  
  }],
  minTabWidth: 115,
  plugins: new Ext.ux.TabCloseMenu()
 });

 var viewport = new Ext.Viewport({// 绘制布局
  layout:'border',
  monitorResize:'true',
  items:[northPanel, treePanel, mainPanel]
 });


 
 treePanel = new Ext.tree.TreePanel({
 id:'tt1',
   el:'layout_menu',
   border : false,
   rootVisible:false,
   autoHeight: true,
   autoScroll:true,
   animate:true,//动画效果
   enableDD:false,//true可以拖拽
   containerScroll: true,
   loader: new Ext.tree.TreeLoader(),
   root: treeroot1,
   collapseFirst:true   
 });
 
 treePanel.render();
 treePanel.root.appendChild(eval(firstroot));//这行一定要有,否则菜单有问题

 treePanel.on("click", function(node, e){//菜单点击事件
  e.stopEvent();
  var _attr = node.attributes;
  var _href = _attr.href;
  if(node.isLeaf() && _href){
   var _id = "tree_" + node.id;
   //var _tab = mainPanel.findById(_id);
   var _tab = Ext.getCmp(_id);
   if(_tab) {
    mainPanel.activate(_id);
    return;
   }
	var xframe="<iframe id='x"+_id+"' src='"+_href+"' MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0></iframe>";
   mainPanel.add({
    id: _id,
    tabTip: _attr.text,
    title: node.text,
    html: xframe,
    closable:true
   }).show();
   iframeReSize();
   //if(mainPanel.items.length > 5 )
  // mainPanel.remove(0);
  }
 });
 
 mainPanel.on("resize", function(comp,aw,ah,rw,rh){//主窗口resize事件,实时调整iframe的大小
 iframeReSize();
 })
 
iframeReSize();
Ext.QuickTips.init();//显示tip

<ww:if test="#session['currentUser'].getPwdDate()>=80">
Ext.Msg.alert('<ww:text name="login.jsp.alertTitle"/>','<ww:text name="login.jsp.alertMessage"><ww:param><ww:property value="90-#session['currentUser'].getPwdDate()"/></ww:param></ww:text>');
</ww:if>

});

/******屏幕锁定代码***************/
var nTimeout = 60*30;//10分钟,60为一分钟 
var oTimer = null;  
/* function resetTimer()   
{
	if(oTimer != null)
	{
  		clearTimeout(oTimer);
  	}
	oTimer = setTimeout("repwd()", nTimeout*1000);     
}
document.onmousemove = resetTimer;
document.onkeydown = resetTimer;
window.onload = resetTimer;  */

var form1 = new Ext.form.FormPanel({
	id:"pwdPanel",
	frame:true,
	labelAlign:'left',
	labelWidth:80,
	bodyStyle:"padding:5px 5px 0",
	items:[
		{xtype:'hidden',
		name:'txt_username',
		value:'<ww:property value="#session['currentUser'].getUsername()"/>'
		},
		{xtype:'textfield',
		id:"pass",
		inputType:"password",
		fieldLabel:'<ww:text name="login.jsp.lockPassword"/>',
		allowBlank:false,
		name:'txt_pass',
		listeners:{
            specialkey:function(field,e){
                if (e.getKey()==Ext.EventObject.ENTER){
                    doCheck();
                }
            }
        }
		}
	],
	buttons: [
		{text: '<ww:text name="login.jsp.lockConfirm"/>',id: 'b_confirm'},
		{text: '<ww:text name="login.jsp.lockLogout"/>',handler:function(){
			location.href='<%=path%>/rightmanage/logout.jspa';
		}}
	]
});

Ext.getCmp("b_confirm").on('click', function(e){ 
	doCheck();
}); 

function doCheck(){
			var f = form1.getForm();
				if(!f.isValid()){
				Ext.Msg.alert('<ww:text name="login.jsp.lockAlertTitle"/>','<ww:text name="login.jsp.lockAlertMessage"/>');
				return;
				}
				form1.getForm().submit({
					url: '<%=path%>/rightmanage/changepwd!validpwd.jspa',
					success: function(form, action){
						f.getEl().dom.reset();
						qwindow.hide();
					},
					failure: function(form, action){
						Ext.Msg.alert('<ww:text name="login.jsp.lockAlertTitle2"/>',action.result.message);   
					}
				});
		}

var qwindow = new Ext.Window({//窗口
        title: '<ww:text name="login.jsp.lockTitle"/>',
        width: 300,
        draggable:false,
        closable:false,
        shadow:true,
        animCollapse:true,
        autoScroll:true,
        maskDisabled:false,
        modal:true,
        resizable:false,
        plain:true,
		items:form1
	});
function repwd()
{
    qwindow.show();
}


 //使iframe适应不通分辨率大小
function iframeReSize(){
 for(var i = 0; i < document.getElementsByTagName('iframe').length; i++){
 	var fram=document.getElementsByTagName('iframe')[i];
 	fram.height=mainPanel.getActiveTab().getSize().height;
 	fram.width=mainPanel.getActiveTab().getSize().width;
 }
 }

function clickAddFavorite(){//加入收藏夹
    var vhref=document.location.href;
    var url =vhref.substring(0,vhref.length-23);
    var title ='<ww:text name="title"/>';
    var ua = navigator.userAgent.toLowerCase();
    if (ua.indexOf("360se") > -1) {
        Ext.Msg.alert('提示',"由于360浏览器功能限制，请按 Ctrl+D手动添加！");
    }
    else if (ua.indexOf("msie 8") > -1) {
        window.external.AddToFavoritesBar(url, title); //IE8
    }
    else if (document.all) {
  try{
   window.external.addFavorite(url, title);
  }catch(e){
	  
	  try{
          window.sidebar.addPanel(title, url, "");
      }catch (e){
    	  Ext.Msg.alert('提示',"您的浏览器不支持自动添加收藏,请按 Ctrl+D手动添加！");
      }
        
  }
    } else {
    	Ext.Msg.alert('提示',"您的浏览器不支持自动添加收藏,请按 Ctrl+D手动添加！");
    }  
    
}

function changebg(obj,funccode,pagecode,funcname){//topmenu点击事件

  obj.className='x-panel-header';
	var ul = obj.parentElement;
	for(var i = 0; i < ul.children.length; i++){
		if(obj == ul.children[i]){
		  obj.className='x-panel-header';
		  Ext.getCmp('tt1').root.firstChild.removeChild(Ext.getCmp('tt1').root.lastChild);
		  Ext.getCmp('tt1').root.appendChild(eval(funccode));
		}else{
		   ul.children[i].className='nonow';
		}
	}
	//topMenu 的pagecode不是'/',有url
	if(pagecode.length > 1){
		Ext.getCmp('treePanel').collapse();
		var mainPanel = Ext.getCmp('mainPanel');
	   var _id = "tree_" + funccode;
	   var _tab = mainPanel.findById(_id);
	   if(_tab) {
	    mainPanel.activate(_id);
	    return;
	   }
		var xframe="<iframe id='x"+_id+"' src='"+pagecode+"' MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0></iframe>";
	   mainPanel.add({
	    id: _id,
	    tabTip: funcname,
	    title: funcname,
	    html: xframe,
	    closable:true
	   }).show();
	   iframeReSize();
		
	}else{
		Ext.getCmp('treePanel').expand();
	}
	
} 
</script>
	<div id="top">
<div class="logo" style="background-image:url('/images/logo.gif')" >
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td><div id="topnav"><!--<a href="?set_locale=zh_HK">繁體</a>-<a href="?set_locale=zh_CN">简体</a>-<a href="?set_locale=en_US">ENGLISH</a>|  --><a href="#"  onClick="clickAddFavorite()"><ww:text name="login.jsp.addFavorite"/></a> | <a href="<%=path%>/rightmanage/logout.jspa"><ww:text name="login.jsp.logout"/></a> </div></td>
    </tr>
</table>
</div>
  <div id="Nav">
  <ww:iterator value="topMenu" status="rowstatus">
   		<ww:if test="#rowstatus.first">
   		<script>
		//显示第一个菜单
		firstroot=<ww:property value="funccode"/>
		</script>
   			<a href="#" class="x-panel-header" onClick="changebg(this,'<ww:property value="funccode"/>','<ww:property value="pagecode"/>','<ww:property value="funcname"/>')"><ww:property value="funcname"/></a>
   		</ww:if>
   		<ww:else>
   			<a href="#" onClick="changebg(this,'<ww:property value="funccode"/>','<ww:property value="pagecode"/>','<ww:property value="funcname"/>')"><ww:property value="funcname"/></a>
   		</ww:else>	
   </ww:iterator>
  </div>
</div>
<div id='layout_menu'></div>

  </body>
</html>
