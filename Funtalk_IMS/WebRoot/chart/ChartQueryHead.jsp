<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="com.funtalk.bean.QryWhereBean" %>
<%@ page import="com.funtalk.bean.ChartQueryBean" %>
<%@ page import="java.util.*" %>

<html>

<SCRIPT language=JavaScript src='/js/PopupDate.js'></SCRIPT>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link href="../css/publicStyle_jifei.css" rel="stylesheet" type="text/css">

<%
	////Member currentUser = (Member)session.getAttribute("currentUser");
	//����ѡ��˵���ʡ�û��ɿ����е��У�����ֻ�ɿ�������
	String userType = "0";
	String userLocal = "999";
	
    String qry_url = request.getParameter("qry_url");

    ChartQueryBean bean = new ChartQueryBean();

    String queryid = request.getParameter("queryid");
    String v[] =bean.GetQueryInfo(queryid);   //��ѯ��Ϣ
    String query_name = v[1];
    //��ѯ����
    List p = QryWhereBean.GetParamInfo(queryid, bean.WHERE_TABLE, bean.dbconn, bean.DBStr,bean.RepDB,userType,userLocal);
    
    int pHeight=0;
    
    if(p!=null)
    {
    	pHeight = (p.size()+1)/3;
    	if(((p.size()+1) % 3)>0)
        	pHeight = pHeight + 1;
    }
    //��ѯ�������ĸ߶�
    pHeight = pHeight * 25 + 45;
%>


<SCRIPT language=javascript>
<!--
    //ȥ�����ҿո�
    function trim(str)
    {
        return str.replace(/(^\s*)|(\s*$)/g, "");
    }

    //����Ƿ�Ϊ����
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

    //����ʱ��Ϸ���
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

    //���ø߶�
    function modifyrows(i)
    {
        parent.config.rows= i + ",*";
    }

    //У���û�����
    function checkData()
    {
        with(inputform)
        {
        <%
           if(p!=null)
           	 out.println(QryWhereBean.GetCheckStr(p));
        %>
        }

        //������
        tar = parent.middle;
        tar.document.write("<SCRIPT language=javascript>");
        tar.document.write("var num=0;");
        tar.document.write("function jindu()");
        tar.document.write("{");
        tar.document.write("num++;");
        tar.document.write("myarray=new Array('#494949','#646464','#747474','#888888','#969696','#A8A8A8','#B6B6B6','#C6C6C6','#D7D7D7','#E1E1E1','#F0F0F0','#F9F9F9');");
        tar.document.write("process.style.color=myarray[num-1];");
        tar.document.write("process.innerText=process.innerText+'��';");
        tar.document.write("if(num<12)");
        tar.document.write("{");
        tar.document.write("setTimeout('jindu()',1000);");
        tar.document.write(" if(num == 11)");
        tar.document.write("{");
        tar.document.write(" num =0;");
        tar.document.write("process.innerText='���ڴ�����������,���Ե�!';");
        tar.document.write("}");
        tar.document.write("}");
        tar.document.write("}");
        tar.document.write("</SCRIPT >");
        tar.document.write("<HTML><BODY background='/images/bline.gif'>");
        tar.document.write("<div id=process STYLE='display:black;position:absolute;left:200px;top:120px; '>");
        tar.document.write("���ڴ�����������,���Ե�!");
        tar.document.write("</div>");
        tar.document.write("</BODY></HTML>");
        tar.jindu();

        //��ʼ��ѯ
        inputform.submit();
    }
-->
</SCRIPT>

</head>
<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="/images/bline.gif" onload="modifyrows(<%=pHeight%>);">

		 <form name="inputform" action="<%=qry_url%>" method=post target=middle>
             <div id="infoList"> 
 			<div class="title">
          <table width=100% cellpadding="0" cellspacing="0">
           <tr>
                <td width="100%">������ѯҳ��/�������ѯ����&nbsp;&nbsp;&nbsp;<b><%=query_name%><b></td>
           </tr>
           		</table>
           		</div>
           		        <div class="listOutLine">
		   <div class="selectBar">
			<%
               //��ʾ��ѯ����
               if(p!=null)
               {
               		for(int i=0;i< p.size();i++)
               		{
                  		String temp[] = (String [])p.get(i);
                   		out.print(QryWhereBean.GetInputField(temp,i)+" &nbsp;");
               		}
               }
			 %>

               <input type=hidden name="queryid" value="<%=queryid%>">
               <input type=hidden name="qry_url" value="<%=qry_url%>">
               <input class=button type=button onclick=checkData() value="��ѯ">

</div></div>
			</div>
           </form>

</body>

</html>

