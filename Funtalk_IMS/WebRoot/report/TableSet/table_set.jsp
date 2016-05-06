<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.funtalk.bean.TableSetBean" %>
<%@ page import ="java.util.*"%>

<HTML>
<HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<link href="../../css/publicStyle_jifei.css" rel="stylesheet" type="text/css">
<title>参数表配置</title>
</HEAD>
<body>
<%
    //表编号
    String PARA_CODE = (String)request.getParameter("PARA_CODE");

    //变量定义
    String detail_name="", detail_type="", detail_xml="", detail_f_xml="", detail_R_xml="";
    String master_name="", master_type="", master_xml="", master_f_xml="", master_R_xml="";
    String detail_db="",   master_db="";
    String table_caption, parent_table;
    int i,c;

    TableSetBean mybean = new TableSetBean();

    //明细表
    String ar[] = mybean.DetailTableInfo(PARA_CODE);
    if(ar==null)
    {
        out.print("</body></HTML>");
        return;
    }
    detail_name =   ar[0];   //表名
    parent_table =   ar[1];   //父表编号
    detail_type =   "000";     //表类型,可以编辑、添加、删除
    table_caption = ar[4];   //表中文注释
    detail_db = ar[5];        //所在数据库

    //字段表XML
    detail_f_xml = mybean.GetFieldXML(PARA_CODE);
    //参考列
    detail_R_xml = mybean.GetRefColXML(PARA_CODE, detail_db);

    //父表
    if((parent_table!=null)&&(parent_table.length()>0))
    {
        String arp[] = mybean.DetailTableInfo(parent_table);
        if(ar!=null)
        {
            master_name =   arp[0];   //表名
            master_type =   "000";     //表类型,可以编辑、添加、删除
            master_db = arp[5];        //所在数据库
            //字段表XML
            master_f_xml = mybean.GetFieldXML(parent_table);
            //参考列
            master_R_xml = mybean.GetRefColXML(parent_table,master_db);
         }
     }
%>

<SCRIPT LANGUAGE="javascript">
  function savedata()
  {
    SaveForm.sql.value=myactive.UpdateSql;
    if(SaveForm.sql.value=="")
    {
        alert("数据没有修改");
        return false;
    }
    else
    {
        SaveForm.submit();
        return true;
    }
  }

  //页面跳转
  function jumpToPage(pageNum)
  {
      if((pageNum==null)||(pageNum.length<=0))
          return false;
      if(radio1.checked)
          isCheck = "0";
      else
          isCheck = "1";

      url="/LocalDataServlet?operate=71&page=" + pageNum +
              "&askType=<%=PARA_CODE%>&CountPerpage=" + TPage.value + "&isCheck=" + isCheck;;
      window.document.location.href=url;
  }

</SCRIPT>
<div id="infoList"> 
	 <div class="title">
		 <table width="98%" border="0" cellpadding="0" cellspacing="0">
			 <tr>
				 <td width="38%">
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= table_caption %>			   </td>
				 <td  width="62%">

				 </td>
			 </tr>
	   </table>
	 </div>
</div>
<br>
<div id="missionTable">
<div class="bolder">
<table  border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
	<tr><th>&nbsp;</th></tr>
	<tr class="grey"><td  height="30">     <form name="SaveForm"  method="POST" action="save.jsp">
       <div align="center">
         <Input TYPE='HIDDEN' NAME='sql' VALUE=''>
         <Input TYPE='HIDDEN' NAME='table_db' VALUE='<%= ((detail_db==null)?"":detail_db)%>'>
         <Input type="button" class=button2 value="保存至数据库" name="B1" onClick="javascript:savedata()">
       </div>
	</form></td></tr>
  <tr height="400"><td height="400" width="100%"  colspan="2" align="center">
    <OBJECT
      id="myactive"
      classid="clsid:BDE5FFD3-4EE3-445D-82DF-4C1EB9772F43"
      codebase="/activeX/JsParam.cab#version=2,2,9,0"
      width="100%"
      height="100%"
    >
    <param name="Detail_name" Value='<%=detail_name%>'>
    <param name="Detail_type" Value='<%=detail_type%>'>
    <param name="Master_name" Value='<%=master_name%>'>
    <param name="Master_type" Value='<%=master_type%>'>
<%
    //获取表的数据的XML
    List fieldInfo = mybean.GetTableField(detail_name,detail_db);
    List TableData = mybean.GetTableData(detail_name,detail_db,fieldInfo,1, 200000,null);
    String fieldXML = mybean.GetTableFieldXML(detail_name,fieldInfo);

    out.print("<param name=\"Detail_xml\" Value='");
    out.print(fieldXML);
    out.print("</METADATA><ROWDATA>");
    String tmpstr,tmpstr1;

    for(int d=0; d<TableData.size(); d++)
    {
        String[] data_ar = (String[]) TableData.get(d);
        out.print("<ROW ");
        for(i=0; i<fieldInfo.size(); i++)
        {
            String[] field_ar = (String[]) fieldInfo.get(i);
            tmpstr1 = data_ar[i];
            if(tmpstr1 != null)
            {
               tmpstr1 = tmpstr1.trim();
               if(i>0)  out.print(" ");

               if(field_ar[2].indexOf("date")>=0)
                  tmpstr=tmpstr1.substring(0,8)+"T"+tmpstr1.substring(9)+"000";
               else
                  tmpstr=mybean.ReplaceXMLStr(tmpstr1);
               out.print(field_ar[1]+"=\""+tmpstr+"\"");
            }
        }
        out.print("/>");
    }
    out.println("</ROWDATA></DATAPACKET>'>");

    //获取父表的数据的XML
    out.print("<param name=\"Master_xml\" Value='");
    if((parent_table!=null)&&(parent_table.length()>0))
    {
        fieldInfo = mybean.GetTableField(master_name,master_db);
        TableData = mybean.GetTableData(master_name,master_db,fieldInfo,1, 200000,null);
        fieldXML = mybean.GetTableFieldXML(master_name,fieldInfo);

        out.print(fieldXML);
        out.print("</METADATA><ROWDATA>");

        for(int d=0; d<TableData.size(); d++)
        {
            String[] data_ar = (String[]) TableData.get(d);
            out.print("<ROW ");
            for(i=0; i<fieldInfo.size(); i++)
            {
                String[] field_ar = (String[]) fieldInfo.get(i);
                tmpstr1 = data_ar[i];
                if(tmpstr1 != null)
                {
                   tmpstr1 = tmpstr1.trim();
                   if(i>0)  out.print(" ");

                   if(field_ar[2].indexOf("date")>=0)
                       tmpstr=tmpstr1.substring(0,8)+"T"+tmpstr1.substring(9)+"000";
                   else
                       tmpstr=mybean.ReplaceXMLStr(tmpstr1);
                   out.print(field_ar[1]+"=\""+tmpstr+"\"");
                }
            }
            out.print("/>");
        }
        out.print("</ROWDATA></DATAPACKET>");
    }
    out.print("'>");
%>
    <param name="Detail_f_xml" Value='<%=detail_f_xml%>'>
    <param name="Detail_R_xml" Value='<%=detail_R_xml%>'>
    <param name="Master_f_xml" Value='<%=master_f_xml%>'>
    <param name="Master_R_xml" Value='<%=master_R_xml%>'>
    <IFRAME name="FailShow"  src="/activeX/failshow.htm" allowTransparency="true" frameborder=0 scrolling=NO width=100% height=100%></IFRAME>
    </OBJECT>

    <script language="JavaScript">
       myactive.InitForm();
    </script>
  </td></tr>
</table>
</div>
</div>
</BODY>
</HTML>
