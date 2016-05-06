<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="com.funtalk.bean.QryWhereBean" %>
<%@ page import="com.funtalk.bean.RepQueryBean" %>
<%@ page import="com.funtalk.pojo.rightmanage.User" %>
<%@ page import="java.util.*" %>
<html>

<SCRIPT language=JavaScript src='../../js/PopupDate.js'></SCRIPT>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>

<%
	User currentUser        =(User)session.getAttribute("currentUser");
	//地市选择菜单，省用户可看所有地市，地市只可看本地市
	String userType = "0";
	String userLocal = "999";
	
    String qry_url = request.getParameter("qry_url");
    RepQueryBean bean = new RepQueryBean();
    String queryid = request.getParameter("queryid");
    String v[] =bean.GetQueryInfo(queryid);   //查询信息
    //yl modify at 20061025
    String query_name = "";
    if (v == null){
    query_name = "";
    }else{
    query_name = v[1];
    }
    //查询条件
    List p = QryWhereBean.GetParamInfo(queryid, bean.WHERE_TABLE, bean.dbconn, bean.DBStr, bean.RepDB,userType,userLocal);
    int p_size = (p==null)?0:p.size();
%>


<SCRIPT language=javascript>
<!--
    //去掉左右空格
    function trim(str)
    {
        return str.replace(/(^\s*)|(\s*$)/g, "");
    }

    //检查是否为数字
    function checkNumber(str)
    {
        var chars= "0123456789";
        for(var i=0; i<=str.length; i++)
        {
            if(chars.indexOf(str.charAt(i))==-1)
                return false;
        }
        return true;
    }

    //检验时间合法性
    function checkTime(str)
    {
        if((str.length!=6)||!checkNumber(str))
            return false;

        hour  =  parseInt(str.substring(0,2),10);
        minute = parseInt(str.substring(2,4),10);
        second = parseInt(str.substring(4,6),10);

        if( (hour< 0) || (hour > 23 ) ||
            (minute< 0) || (minute > 59 ) ||
            (second< 0) || (second > 59 )  )
            return false;

        return true;
    }

    //设置高度
    function modifyrows(i)
    {
        parent.config.rows= i +20+ ",*";
    }
	function zipfile()
	{
		document.inputform.zipFile.value="zipFile";
		checkData();
	}
    //校验用户输入
    function checkData()
    {
        with(inputform)
        {
        <%
           out.println(QryWhereBean.GetCheckStr(p));
        %>
        }
        
		<%
		if(request.getParameter("newFormWindow") == null ||request.getParameter("newFormWindow").equals("null")){
		 %>
        //进度条
        tar = parent.middle;
        tar.document.write("<SCRIPT language=javascript>");
        tar.document.write("var num=0;");
        tar.document.write("function jindu()");
        tar.document.write("{");
        tar.document.write("num++;");
        tar.document.write("myarray=new Array('#494949','#646464','#747474','#888888','#969696','#A8A8A8','#B6B6B6','#C6C6C6','#D7D7D7','#E1E1E1','#F0F0F0','#F9F9F9');");
        tar.document.write("process.style.color=myarray[num-1];");
        tar.document.write("process.innerText=process.innerText+'■';");
        tar.document.write("if(num<12)");
        tar.document.write("{");
        tar.document.write("setTimeout('jindu()',1000);");
        tar.document.write(" if(num == 11)");
        tar.document.write("{");
        tar.document.write(" num =0;");
        tar.document.write("process.innerText='正在处理您的请求,请稍等!';");
        tar.document.write("}");
        tar.document.write("}");
        tar.document.write("}");
        tar.document.write("</SCRIPT >");
        tar.document.write("<HTML><BODY background='/images/bline.gif'>");
        tar.document.write("<div id=process STYLE='display:black;position:absolute;left:200px;top:120px; '>");
        tar.document.write("正在处理您的请求,请稍等!");
        tar.document.write("</div>");
        tar.document.write("</BODY></HTML>");
        tar.jindu();
		<%}else{ %>
		window.open('about:blank', 'newFormWindow', 'width=1000,height=700,top=0, left=0,resizable=10,scrollbars=yes');
		<%} %>
        //开始查询
        inputform.submit();
    }
-->
</SCRIPT>

<link href="../../css/publicStyle_jifei.css" rel="stylesheet" type="text/css">
<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="/images/bline.gif" onload="modifyrows(50);">

<TABLE id=check0 width=100% border=0 align="center" cellSpacing=0 valign=bottom>
    <TBODY>
         <tr>
         <td>
         	<%if(request.getParameter("newFormWindow")!=null && !request.getParameter("newFormWindow").trim().equals("null")){
         	//System.out.println("request.getParameter(\"newFormWindow\")"+request.getParameter("newFormWindow"));
         	 %>
         	<form name="inputform" action="<%=qry_url%>" method="POST" target="newFormWindow">
         	<%}else{ %>
         	<form name="inputform" action="<%=qry_url%>" method=post target=middle>
         	<%} %>
         	<div id="infoList"> 
 			<div class="title">
          		<table width=100% cellpadding="0" cellspacing="0">
           			<tr>
                		<td width="100%" >　　查询页面/请输入查询条件&nbsp;&nbsp;&nbsp;<b><%=query_name%><b></td>
           			</tr>
           		</table>
           		 </DIV> </DIV>
          </td>
          </tr>
          <tr>
           <td>
           <%
               //显示查询条件
               for(int i=0;i< p_size;i++)
               {
                   String temp[] = (String []) p.get(i);
                   out.print("&nbsp;&nbsp;"+QryWhereBean.GetInputField(temp,i));
               }
            %>

               <input type=hidden name="queryid" value="<%=queryid%>">
               <input type=hidden name="qry_url" value="<%=qry_url%>">
               &nbsp;&nbsp;&nbsp;<input class=button type=button onclick=checkData() value="查询">
               <%if(queryid.equals("151")){
               		out.println(" <input type=hidden name=\"zipFile\" value=\"\">");
               		out.println("&nbsp;&nbsp;&nbsp;<input class=button type=button onclick=zipfile() value=\"打包下载\">");
                }
                %>
               
           	</td>
           	</tr>
           </form>
    </TBODY>
</TABLE>

</body>

</html>

