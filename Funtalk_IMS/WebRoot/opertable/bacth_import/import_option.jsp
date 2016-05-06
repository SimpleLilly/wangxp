<!-- tiantao -->
<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="webwork" prefix="ww"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%
	//String[] a_table_info = (String[])local.getInfoOfTable( askType );
	Hashtable list = new Hashtable();
	list.put("1", "a");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<link rel="stylesheet" href="../../css/publicStyle_jifei.css"
	type="text/css">
<title>title</title>
<script language="javascript">
function submitt(){
	selectAllOptions(document.getElementById('ImportAction_upload_colsList'));
    option.submit();
	
}
</script>
</head>
<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<div id="infoList">
<div class="title">
<TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
	<TR>
		<TD>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;批量导入</TD>
	</TR>
</TABLE>
</div>
</div>
<br>
<ww:form name ="option" action="/localdata/bacth_import/ImportAction_upload.jspa" method="POST" enctype="multipart/form-data">
	<ww:hidden name="askType" value='<%=(String)request.getAttribute("askType") %>'></ww:hidden>
	<table width="700" border="0" cellspacing="0" cellpadding="0"
		align="center">
		
		<tr>
			<td class=Popnav><ww:label>字段顺序:</ww:label></td>
		</tr>
		<tr>
			<td bgcolor="f6f6f6">
			<div align="center">
			<ww:updownselect 
				id ="ImportAction_upload_colsList"
				name="colsList"
				list="updownList" moveUpLabel="上移" moveDownLabel="下移"
				selectAllLabel="全选" size='15' />
			</div>
			</td>
		</tr>
		<br/>
		<tr>
			<td class=Popnav><ww:label>分割符:</ww:label></td>
		</tr>
		<tr>
			<td bgcolor="f6f6f6"><br />
			<div align="center">
			<input type="radio" name="fenge" value="|" checked="checked"/>竖线(|)
			<ww:radio name="fenge"
				list="#{';':'分号(;)',',':'逗号(,)','	':'Tab(	)',' ':'空格( )','other':'其它'}"></ww:radio>
			<ww:textfield name="otherFen" size='5'></ww:textfield></div>
			<br />
			</td>
		</tr>
		<tr>
			<td class=Popnav><ww:label>日期格式:</ww:label></td>
		</tr>
		<tr>
			<td bgcolor="f6f6f6"><br />
			<div align="center">
			<input type="radio" name="dateModel" value="YYYYMMDD" checked="checked"/>YYYYMMDD
			<ww:radio name="dateModel"
				list="#{'YYYY-MM-DD HH:MI:SS':'YYYY-MM-DD HH:MI:SS','YYYYMMDD HH:MI:SS':'YYYYMMDD HH:MI:SS','other':'自定义'}"></ww:radio>
			<ww:textfield name="otherModel" size='20'></ww:textfield></div>
			<br />
			</td>
		</tr>
		<tr>
			<td class=Popnav><ww:label>数据文件:</ww:label></td>
		</tr>
		<tr>
			<td bgcolor="f6f6f6"><br />
			<div align="center"><ww:file name="file" >文件：</ww:file>
			<ww:textfield name="headNum" size='5' value='0' >忽略前</ww:textfield>
			<ww:label>行</ww:label>
			</div>
			</td>
		</tr>
		<tr>
			<td bgcolor="f6f6f6"><br />
			<div align="center">
			<input type="button" value="提交" onClick="submitt()"/>
			</div>
			<br/>
			</td>
		</tr>
	</table>
</ww:form>
</body>
</html>
