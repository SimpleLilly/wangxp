<%@ page language="java" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="ww" uri="webwork"%>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><ww:text name="title"/></title>

<script src="<%=path%>/jquery/jquery-1.2.6.js"></script>

<link href="<%=path%>/css/anhui_login.css" rel="stylesheet" type="text/css">

</head>

<body>

<div id="login">
 <div class="left01">
  <div class="lt_left_bg"></div>
    <div class="lt_left_bg_02">
      <div class="input01">
      	<form  name="form1" action="<%=path%>/rightmanage/login.jspa" method="post" >
      	       <table border=0>	
      	       		<tr><td><ww:text name="index.jsp.username"/>：</td><td><input name="userName" id="userName" type="text" size="20" class="input_bolder" value="admin"></td></tr>
      	       		<tr><td><ww:text name="index.jsp.password"/>：</td><td><input name="passWord" type="password" size="20" class="input_bolder" value=""></td></tr>
      	            <tr>
      	                <td colspan=2>
      	                    <div class="input02">
              				    <input type="button" id="btn_login" class="Button" value="登录"  onClick="login_do()">&nbsp;&nbsp;
              				    <input type="button" id="btn_login" class="Button" value="清空"  onClick="login_reset()">              				    
          				    </div>
      	                </td>
      	            </tr>
      	       </table>

          		
          		<input type=hidden name="agentIp" value="<%=request.getRemoteAddr()%>">
      	</form>
      </div>
    </div>
  </div>

</div>
</body>

</html>






<script language=javascript>
	<ww:iterator value="actionMessages">
		alert("<ww:property/>");
	</ww:iterator>
</script>

<script language="javascript">
$(document).ready(function (){

	$("#userName")[0].focus();

});



	function login_do()
	{
	    if ( document.form1.userName.value == "" )
	    {
	        alert("<ww:text name="index.jsp.alertNoUsername"/>");
	        document.form1.userName.focus();
	        return false;
	    }
	    else if ( document.form1.passWord.value == "" )
	    {
	        alert("<ww:text name="index.jsp.alertNoPassword"/>！");
	        document.form1.passWord.focus();
	        return false;
	    }
	    
	    $("#btn_login").fadeOut("slow"); 
	    
	    document.form1.submit();
	}
		
	document.onkeydown = function onkeydown(){
	     if(window.event.keyCode==13){
	        login_do();
	     }
	 }
	
	function login_reset()
	{
	    document.form1.reset();
	}	
</script> 
