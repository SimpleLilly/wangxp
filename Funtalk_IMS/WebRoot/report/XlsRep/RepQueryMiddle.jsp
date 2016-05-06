<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.funtalk.bean.QryWhereBean" %>
<%@ page import="com.funtalk.bean.RepQueryBean" %>
<%@ page import="com.funtalk.bean.ExcelBean" %>
<%@ page import="com.funtalk.pojo.rightmanage.User" %>
<%@ page import="java.util.*" %>

<html> 

<head>
<base target="middle">
<meta http-equiv="Content-Language" content="zh-cn">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title> </title>
</head>

<SCRIPT LANGUAGE="javascript">

  //�鵵
  function SaveToDB()
  {
        document.all["SaveForm"].submit();
        return true;
  }
  //��EXCEL�ļ����ڱ���
  function SaveToFile()
  {
    myactive.SaveToFile();
    return true;
  }
  
  //��EXCEL�ļ����ڱ���
  function PrintFile()
  {
    myactive.PrintExcel();
    return true;
  }

  //�رջ��EXCEL
  function CloseOrOpen()
  {
    if(BCO.value=="��Excel")
    {
        myactive.ReloadExcel();
        BCO.value="�ر�Excel"
    }
    else
    {
        myactive.CloseExcel();
        BCO.value="��Excel"
    }
    return true;
  }

</SCRIPT>

<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="/images/bline.gif" >
 <link href="/css/zi.css" rel="stylesheet" type="text/css">

<%
    User currentUser        =(User)session.getAttribute("currentUser");
    String username 		=currentUser.getUsername();
	//����ѡ��˵���ʡ�û��ɿ����е��У�����ֻ�ɿ�������
	String userType = "0";
	String userLocal = "999";
	////////////////////////////////////////////////////////////////////////////////////////////////////
	String str_exceldata="";
    int i,j;
    String tempV[] = null;
    String queryid = (String) request.getParameter("queryid");
    if((queryid==null)||(queryid.length()==0))
        return;
	
    
    RepQueryBean bean = new RepQueryBean();
    String v[] = bean.GetQueryInfo(queryid);   //��ѯ��Ϣ
    List param = QryWhereBean.GetParamInfo(queryid, bean.WHERE_TABLE, bean.dbconn, bean.DBStr, bean.RepDB,userType,userLocal);
    
    String str_param="";
    
    int param_size = (param==null)?0:param.size();
    for(i=0;i<param_size;i++)
    {
        tempV = (String [])param.get(i);
        String value = request.getParameter(tempV[1]+"_"+i);
        if(tempV[1].equals("F"))
            value += request.getParameter(tempV[1]+"_"+i+"_1");
        tempV[5] = value;
        str_param+=tempV[0]+":"+tempV[5]+"|";

    }
    if(str_param.length()>0)
        str_param=str_param.substring(0,str_param.length()-1 );
	System.out.println("==============>str_param="+str_param);
	
	
    /////////2009-2-13 liaoxb  ��USERID�ӵ�����̶�������---------�˹���Ϊ�������е�
    String lxb_temp[]=new String[7];
    lxb_temp[0]="USERID";
    lxb_temp[1]="T";
    lxb_temp[2]=username;
    lxb_temp[5]=username;
    param.add(lxb_temp);
    /////////////////////////////////////
	
	
	
	
    Vector data = (Vector)bean.GetQueryData(queryid, param);
    
    
    if(data==null)  //û������
    {
        out.print("<br><br><table width=100% align=center><tr><td align=center>");
        out.print("<font color=red>��ǰ��������ʾ��</font></td></tr></table></body></html>");
        return;
    }

    //�������
    List DataV   		= (List)data.elementAt(0);
    //��ѯ����
    List Par 	 		= (List)data.elementAt(1);
    //��ѯ��Ϣ
    String qryInfo[] 	= (String [])data.elementAt(2);

    String ModelFile  	= qryInfo[3];  //ģ���ļ�
    String IsSheet 		= qryInfo[4];     //�Ƿ�ƥ��Sheet
    String IsGuidang 	= qryInfo[5];     //�Ƿ�鵵
    String PString 		= qryInfo[1];     //����ʱ��Ĭ���ļ���
    for(i=0;i<Par.size();i++)
    {
        tempV = (String [])Par.get(i);
        PString = PString + '_'+ tempV[1];
    }

    //�����������Ĭ�ϲ���
    tempV = new String[2];   tempV[0] = "USER";  tempV[1] = username+"";
    Par.add(tempV);
    tempV = new String[2];   tempV[0] = "DATE";  tempV[1] = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    Par.add(tempV);

    //�����ļ�
    String UrlPath = "/report/XlsRep/excel/";
    String UrlFile = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
    UrlFile += "_" + (int)(Math.random()*100000000) + "_" + session.hashCode() + ".xls";
    String XlsPath = request.getRealPath("/") + UrlPath;
    ModelFile = request.getRealPath("/") + "/report/XlsRep/model/" + ModelFile;

    //��ɾ��12Сʱ��ǰ����ʷ�ļ�
    ExcelBean.DeleteTempFile(XlsPath, 12);
    

    boolean flag = false;
    flag = ExcelBean.CreateXlsFile(DataV, Par, ModelFile, XlsPath+UrlFile, IsSheet);
    System.out.println("=============================555555555555555555555555555555555, flag="+flag);
    if(!flag)
    {
        out.print("<br><br><table width=100% align=center><tr><td align=center>");
        out.print("<font color=red>�޷�����Excel�ļ�</font></td></tr></table></body></html>");
        return;
    }
    
    if(request.getParameter("zipFile")!=null && request.getParameter("zipFile").equals("zipFile"))
    {
    	request.setAttribute("xlsFile",XlsPath+UrlFile);
    	request.getRequestDispatcher("/report/XlsRep/zipfiledown.jsp").forward(request, response);
    }
    String hostStr = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
	/////////////////////////////////////////////////
    List list_exceldata=ExcelBean.GetExcelData(ModelFile, XlsPath+UrlFile ); 

    for(int x=0;x<list_exceldata.size();x++ )
    {
       String ar[]=(String [])list_exceldata.get(x);
       str_exceldata+=ar[0]+"@"+ar[1]+"#";
    }   
    str_exceldata=str_exceldata.substring(0, str_exceldata.length()-1 );
    /////////////System.out.println("------->str_exceldata="+str_exceldata);
    System.out.println(XlsPath+UrlFile);
    String filepath = XlsPath+UrlFile;
%>

<table width="100%" border="0" cellspacing="0" cellpadding="3" >
<tr align=center>
   <td width="100%" background="/images/bar_bg.gif" align=left>
      &nbsp;<Input type="button" value="�����ڱ���" name="B1" onclick="javascript:SaveToFile()">
      &nbsp;<Input type="button" value="�ر�Excel" name="BCO" onclick="javascript:CloseOrOpen()">
      &nbsp;<Input type="button" value="&nbsp;��ӡ&nbsp;" name="PE" onclick="javascript:PrintFile()">
      <%
         if((IsGuidang!=null)&&(IsGuidang.equals("1")))
             out.println("&nbsp;<Input type=\"button\" value=\"���\" name=\"B2\" onclick=\"javascript:SaveToDB()\">");
      %>
   </td>
</tr>
<table>

<center>
<!--
<script language="JavaScript">
    //window.location = "<%=UrlPath+UrlFile%>";
</script>
 -->
<table border="0" width="100%" height="90%">
  <tr><td width="100%"  colspan="2" align="center">
    <OBJECT
      id="myactive"
      classid="clsid:8140C99B-072A-4461-9A72-14983C2C3252"
      codebase="/activeX/JSReport.cab#version=2,2,15,0"
      width="100%"
      height="100%"
    >
    <param name="RepName" Value='<%=hostStr+UrlPath+UrlFile%>'>
    <param name="DataString" Value=''>
    <param name="ParamString" Value='<%=PString%>'>

    <IFRAME name="FailShow"  src="/activeX/failshow.htm" allowTransparency="true" frameborder=0 scrolling=NO width=100% height=100%></IFRAME>
    </OBJECT>

    <script language="JavaScript">
      //��ʾEXCEL�ļ�
      myactive.InitForm();
    </script>
  </td></tr>

</table>
</center>
<form name="SaveForm"  method="POST" action="excel_auditing.jsp">
<input type='hidden' name="ExcelData" value="<%=str_exceldata%>">

<input type='hidden' name="chname" value="<%=qryInfo[1]%>">
<input type='hidden' name="source_seq" value="">
<input type='hidden' name="js_month" value="<%=str_param%>">
<input type='hidden' name="report_type" value="<%=qryInfo[0]%>">
<input type='hidden' name="model_file" value="<%=qryInfo[3]%>">
<input type='hidden' name="owner" value="<%=username%>">
<input type='hidden' name="filepath" value="<%=filepath%>">
<input type='hidden' name="generate_time" value="">
<input type='hidden' name="address" value="">
<input type='hidden' name="memo" value="">

</form>

</body>
</html>
