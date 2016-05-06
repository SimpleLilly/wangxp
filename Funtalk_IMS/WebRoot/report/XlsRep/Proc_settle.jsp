<%@ page language="java" contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>

<HTML><HEAD>
<META content="text/html; charset=gb2312" http-equiv=Content-Type>
<link href="../../css/publicStyle_jifei.css" rel="stylesheet" type="text/css">
</HEAD>
<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="/images/bline.gif">

<div id="infoList"> 
 <div class="title">
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
  <TD height=30 vAlign=bottom width="70%"> <DIV align=left>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重新生成结算结果</DIV></TD>
        </tr>
        </table>
  </div>
   </div>  
<br><br><br>

<form name="queryform" method="post">
<div id="minTable">
<div class="bolder">
<TABLE width="100%" border=0 align="center" cellPadding=0 cellSpacing=0>
	<tr><th><strong>重新生成结算结果</strong></th></tr>
	<tr class="grey"><td>&nbsp;</td></tr>
    <tr bgcolor=#f0f0f0 class="font2">
                <td align="center" width='40%' class='font2'>
<%
    int i;
    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
    Date now = new Date();
    String DateStr = df.format(now);
    String yc = DateStr.substring(0,4);
    String mc = DateStr.substring(4,6);
    int mi=Integer.parseInt(mc)-1;
    int yi=Integer.parseInt(yc);
    if(mi==0)
    {
        mi=12;
        yi=yi-1;
    }
    String year=yi+"";
    String month=(mi>=10)?(mi+""):("0"+mi);

    String ystr="年份<select class='input'  name='C_0'>";
    for(i=yi-2; i<=yi+1; i++)
        ystr+="<option value='"+i+"'"+((year.equals(i+""))?(" selected "):"")+">"+i+"</option>";
    out.println(ystr+"</select>");

    String mstr="&nbsp;&nbsp;月份<select class='input' name='C_1'>";
    for(i=1; i<=12; i++)
    {
        String str = (i>=10)?(i+""):("0"+i);
        mstr+="<option value='"+str+"'"+((month.equals(str))?(" selected "):"")+">"+str+"</option>";
    }
    out.println(mstr+"</select>");
%>
</td></tr>

<TR><TD height=100 align=center bgcolor="#f0f0f0"><input type=button value="执行" class="button" onClick="execProc()"></td></TR>
</TABLE>
</div>
</div>
</FORM>

<SCRIPT language=javascript>
<!--
    function execProc()
    {
        if(!(confirm("确定要重新生成"+queryform.C_0.value+"年"+queryform.C_1.value+"月的结算结果吗?")))
        {
            return false;
        }
        
        //进度条
        VAL_URL = "Proc_exec.jsp?C_0="+queryform.C_0.value+"&C_1="+queryform.C_1.value;
        tar = parent.main;
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
        tar.location = VAL_URL;
        tar.jindu();
    }
-->
</SCRIPT>

</BODY></HTML>