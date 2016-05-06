<%@ page language="java" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragrma", "no-cache");
response.setDateHeader("Expires", 0);
	
String path = request.getContextPath();

%>
<!DOCTYPE html>
<html lang="zh-CN">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>经营分析系统登录</title>
		<link rel="stylesheet" type="text/css"  href="<%=path%>/css/index.css"/>
		<link rel="stylesheet" type="text/css"  href="<%=path%>/ext-4.2.1/css/ext-theme-neptune-all.css" />
		
		<script type="text/javascript" src="<%=path%>/ext-4.2.1/bootstrap.js"></script>
        <script type="text/javascript" src="<%=path%>/ext-4.2.1/ext-lang-zh_CN.js"></script>

	</head>
	<body>
		<header id="header">
			<div class="container">
				<img class="logo" src="<%=path%>/images/logo.png"/>
				<h2 class="logo-txt">经营分析系统</h2>
			</div>
		</header>
		<div id="login-center" class="login-center">
			<div class="container">
				<div class="login-box">
					<div class="login-header">
						<h2>请登录</h2>
					</div>
					<div class="login-body">
						<form  name="form1" action="<%=path%>/rightmanage/login.jspa" method="post">
							<input class="icon-pic icon-user" type="text" name="userName" id="userName" value="" placeholder="帐号" />
							<input class="icon-pic icon-lockpic" type="password" name="passWord" id="passWord" placeholder="密码" value=""/>
							<input type="button" id="login-btn" name="login-btn" value="登录"  onClick="login_do()"/>
							<input type="reset" id="login-reset" value="清空"/>							
							
							<input type=hidden name="agentIp" value="<%=request.getRemoteAddr()%>">
						</form>
					</div>
				</div>
			</div>
		</div>
	</body>
	
<script language=javascript>


</script>	
	
<script>
	
	
/* var  str1='';
<ww:iterator value="actionMessages">
str1='<ww:property/>';
</ww:iterator>
if(str1!='')
	Ext.Msg.alert('提示',str1);*/


      Ext.onReady(function(){
			var bodyHeight = document.body.clientHeight ;
			var headerHeight = document.getElementById("header").offsetHeight;
			document.getElementById("login-center").style.height = (bodyHeight - headerHeight)+'px';
	
			//Ext.getCmp('userName').focus();
			<ww:iterator value="actionMessages">
            Ext.Msg.alert('提示',"<ww:property/>");
            </ww:iterator>

		});

	    
		function login_do()
			{
			    if ( document.form1.userName.value == "" )
			    {
			        Ext.Msg.alert('提示',"<ww:text name="index.jsp.alertNoUsername"/>");
			        document.form1.userName.focus();
			        return;
			    }
			    else if ( document.form1.passWord.value == "" )
			    {

			        Ext.Msg.alert('提示',"<ww:text name="index.jsp.alertNoPassword"/>！");
			        document.form1.passWord.focus();
			        return;
			    }
			    			    
			   document.form1.submit();
			}

	</script>
</html>
