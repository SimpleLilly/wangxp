<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ page import="java.util.List,java.util.Vector" %>
<%@ page import="com.funtalk.service.systemconfig.ConfigTableBean" %>
<%
	/*
	 * <p>Title: anhuiBSS</p>
	 * <p>Description:�����ݱ����� </p>
	 * <p>Copyright: Copyright (c) 2006</p>
	 * <p>Company: si-tech</p>
	 * @author xuyd
	 */ 
	String path = request.getContextPath();
    String tableId = (String)request.getParameter("tableid");
    //String tableName = (String)request.getParameter("tablename");
    //DataConnection dbconn = new DataConnection();
    //int max_localid = dbconn.functionBindOneInt("select max(localid) from T_LOCAL_DATA_TABLES_COLS");
    ConfigTableBean mybean = new ConfigTableBean();
    String[] a_table_info = mybean.getInfoOfTable(tableId);
    List dbColumn = mybean.getTableColumnInfo(tableId);
    List configColumn = mybean.getConfigTableColumnInfo(tableId);
    Vector v_table_cols = (Vector)mybean.getAllColumnsForTable( "2" ) ;
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<link href="../css/publicStyle_jifei.css" rel="stylesheet" type="text/css">
  <script type='text/javascript' src='<%=path%>/dwr/interface/configTableBean.js'></script>
  <script type='text/javascript' src='<%=path%>/dwr/engine.js'></script>
  <script type='text/javascript' src='<%=path%>/dwr/util.js'></script>
<title>�����ݱ�����</title>
<script languge="javascript">
	function mousein(OMO)
	{	
		OMO.style.backgroundColor='#FFFFCC';
	}


	function mouseout(OMO)
	{	
		OMO.style.backgroundColor='#ffffff';
	}
	function commit()
	{
		//ȡ��ֵ
		var rows_array = getInputValue('inputform');
		//alert(rows_array);
		//for( var i = 0; i < rows_array.length; i++ )
		//{
		//	alert(rows_array[i]);
		//}
		//����JAVA����
		configTableBean.configLocalTable(rows_array,<%=tableId%>,return_flag)

	}
	var return_flag=function(data)
	{
		if(data == "0"){
			alert('���óɹ�!');
			//window.location.href="<%=path%>/opertable/OperTable.jspa?askType=1";
		}else{
			alert(data);
		}
	}
	//��ȡ���ñ�����t_ֵ,ÿ��Ϊһarray�����������еĶ�άarray
	function getInputValue(formname)
	{
		if(formname == 'inputform')
		{
			var textname = 't_';
		}else if(formname == 'dbform')
		{
			var textname = 'o_';
		}
	    //t = "";
	    var row_arr = new Array();
	    var rows_arr = new Array();
		for ( var i=0,j=0,k=0; i< eval(formname).elements.length; i++ )
		{
			var e = eval(formname).elements[i];
			name = e.name;
			if ( name.substring(0,2) == textname )
			{
				row_arr[j] = e.value;
				//t += e.value+", ";
				j++;
				if ( (i+1)%16 == 0 )
				{
					//alert(row_arr);
					rows_arr[k] = row_arr;//t; //
					//��Ҫnewһ������Ȼ��ֻ��������һ������
					row_arr = new Array();
					j = 0;
					k++;
					//t = "";
					//t += "@@";
				}
			}
		}
		//alert(rows_arr);
		return rows_arr;
	}
	function db_reset()
	{
		count = 1;
		//�Ƴ�������
		DWRUtil.removeAllRows('dyntable');
		//ȡ����
		var rows_array = getInputValue('dbform');
		//������

        DWRUtil.addRows( "dyntable", rows_array, cellFuncs,{
      	
			rowCreator:function(options) {    	
			  var row = document.createElement("tr");        
				//cur_row = row;
			  return row;
			},
			cellCreator:function(options) {
			  var td = document.createElement("td");
			  //�Զ�����
			  //td.style.wordBreak="break-all";
			  return td;
			}
		  });
	  }
	  var count = 1;
	  //<input type=\"radio\" id=\"radio"+count+"\" name=\"radiobutton_t\"/> 
	  var cellFuncs = [ 
		function(rows_array) { return "<img src=\"../images/img/nolines_minus.gif\" onclick=\"deleterow(this)\" style=\"cursor:hand\">"; },
		//function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_0\" name=\"t_"+count+"_0\" value=\""+rows_array[0]+"\" size=\"4\"><input type=\"hidden\" id=\"t_"+count+"_1\" name=\"t_"+count+"_1\" value=\""+rows_array[1]+"\">"},
		//function(rows_array) { return rows_array[1]; },
	    function(rows_array) { return "<input type=\"hidden\" id=\"t_"+count+"_0\" name=\"t_"+count+"_0\" value=\""+rows_array[0]+"\"><input type=\"text\" id=\"t_"+count+"_1\" name=\"t_"+count+"_1\" value=\""+rows_array[1]+"\" size=\"4\">"; },
	    function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_2\" name=\"t_"+count+"_2\" value=\""+rows_array[2]+"\" size=\"10\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_3\" name=\"t_"+count+"_3\" value=\""+rows_array[3]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_4\" name=\"t_"+count+"_4\" value=\""+rows_array[4]+"\" size=\"10\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_5\" name=\"t_"+count+"_5\" value=\""+rows_array[5]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_6\" name=\"t_"+count+"_6\" value=\""+rows_array[6]+"\" size=\"10\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_7\" name=\"t_"+count+"_7\" value=\""+rows_array[7]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_8\" name=\"t_"+count+"_8\" value=\""+rows_array[8]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_9\" name=\"t_"+count+"_9\" value=\""+rows_array[9]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_10\" name=\"t_"+count+"_10\" value=\""+rows_array[10]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_11\" name=\"t_"+count+"_11\" value=\""+rows_array[11]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_12\" name=\"t_"+count+"_12\" value=\""+rows_array[12]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_13\" name=\"t_"+(count++)+"_13\" value=\""+rows_array[13]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_14\" name=\"t_"+count+"_14\" value=\""+rows_array[14]+"\" size=\"4\">"; },
		function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_15\" name=\"t_"+count+"_15\" value=\""+rows_array[15]+"\" size=\"4\">"; }

		//function(rows_array) { return "<input type=\"text\" id=\"t_"+count+"_15\" name=\"t_"+(count++)+"_15\" value=\""+rows_array[15]+"\" size=\"4\">"; }
		];
		
		function saveCells(index)
		{

			var obj = document.getElementById('dyntable');
			for ( var i = 0; i < obj.children.length; i++ )
			{
				//eval("var ovalue = document.dbform.o_"+(i+1)+"_"+index+".value");
				//eval("var tvalue = document.inputform.t_"+(i+1)+"_"+index+".value");
				//ovalue = tvalue;
				//alert(myvalue);
				//����hidden
				
				var temp = (i+1)+"_"+index;
				var o_temp = "o_"+temp;
				var t_temp = "t_"+temp;
				var ov_temp = "ov_"+temp;
				//alert(DWRUtil.getValue(t_temp));
			  DWRUtil.setValue(o_temp,DWRUtil.getValue(t_temp));

			  //DWRUtil.setValue('o_1_0',DWRUtil.getValue('t_1_0'));
				//���ý���ֵ
				DWRUtil.setValue(ov_temp,DWRUtil.getValue(t_temp));
				//alert(DWRUtil.getValue("o_"+(i+1)+"_"+index));
				//alert(DWRUtil.getValue('o_1_0'));
			  //alert(DWRUtil.getValue(o_temp));
								
			}
		}
		
		function deleterow(obj)
		{
			 obj.parentElement.parentElement.removeNode( true);
		}
</script>
</head>
<body>
<div id="infoList"> 
 <div class="title">
<TABLE width="100%"  border="0" cellpadding="0" cellspacing="0">
 <TR>
  <TD>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;���ù��� --> �ֶ����� --><%=a_table_info[2]%>(<%=a_table_info[1]%>)         
  </TD>
 </TR>
</TABLE>
  </div>
</div>
  <br>
<div id="missionTable">
		  <div class="bolder">
		  	<form name="dbform" method="post" action="">
		  	<table width="100%"  border="0" cellpadding="0" cellspacing="0">
		  		<tr>
                    <th colspan="20">�����ֶ���Ϣ(�ο�����)</th>
		        </tr>
			    <tr>			    
		            <td colspan="20">&nbsp;
						
				  </td>					
              </tr>
  <TR align="center" bgcolor="#CCCCCC" >
    <TD width=3%>&nbsp; </TD>
    <%
    // ѭ����ʾ�ֶ���ʾ����
    if ( v_table_cols != null && v_table_cols.size() > 0 ) {
    	String[] field_info = null;
        for ( int i = 0 ; i < v_table_cols.size() ; i++ ) {
        	//ȥ������
        	if( i == 0 )
        	continue;
            out.print( "<td " );
            field_info = (String[])v_table_cols.elementAt(i) ;
            if ( !field_info[10].equals("") ) {
                //STYLE
                out.print( "class=\"" + field_info[10] + "\">" );
            }

            //COLUMNCOMMENT
            out.print( "<strong>" + field_info[4] + "</strong>" );
            System.out.println("------field_info[4]="+field_info[4]+"---------");
            out.println( "</td>" );
        }      

    }else {
        out.println( "<script>" );
        out.println( "alert(\"�Բ��𣬴˾����ݱ�û�н����ֶε�����,����ϵ�����Ա�������ã�\");" );
        out.println( "</script>" );
    }
		
	out.println("</tr>");
	
	if ( dbColumn != null && dbColumn.size() > 0 ){
		for (int i = 0; i < dbColumn.size(); i++ ){
		    out.println("<tr onmouseover=\"mousein(this);\"  onmouseout=\"mouseout(this);\">");
		    out.println("<td><img src=\"../images/img/nolines_minus.gif\" onclick=\"deleterow(this)\" style=\"cursor:hand\"></td>");
			String[] columns = (String[]) dbColumn.get(i);
			//out.println("<td><div id=\"ov_"+(i+1)+"_0\">"+(max_localid+i+1)+"</div><input type=\"hidden\" id=\"o_"+(i+1)+"_0\" name=\"o_"+(i+1)+"_0\" value=\""+(max_localid+i+1)+"\"><input type=\"hidden\" id=\"o_"+(i+1)+"_1\" name=\"o_"+(i+1)+"_1\" value=\""+tableId+"\"></td>");//���
			//out.println("<td>"+tableId+"</td>");//����
			out.println("<td><input type=\"hidden\" id=\"o_"+(i+1)+"_0\" name=\"o_"+(i+1)+"_0\" value=\""+tableId+"\"><div id=\"ov_"+(i+1)+"_1\">"+columns[0]+"</div><input type=\"hidden\" id=\"o_"+(i+1)+"_1\" name=\"o_"+(i+1)+"_1\" value=\""+columns[0]+"\"></td>");//����
			out.println("<td><div id=\"ov_"+(i+1)+"_2\">"+columns[1]+"</div><input type=\"hidden\" id=\"o_"+(i+1)+"_2\" name=\"o_"+(i+1)+"_2\" value=\""+columns[1]+"\"></td>");//����
			out.println("<td><div id=\"ov_"+(i+1)+"_3\">"+columns[2]+"</div><input type=\"hidden\" id=\"o_"+(i+1)+"_3\" name=\"o_"+(i+1)+"_3\" value=\""+columns[2]+"\"></td>");//������
			out.println("<td><div id=\"ov_"+(i+1)+"_4\">&nbsp</div><input type=\"hidden\" id=\"o_"+(i+1)+"_4\" name=\"o_"+(i+1)+"_4\" value=\"\"></td>");//��ע��
			out.println("<td><div id=\"ov_"+(i+1)+"_5\">"+columns[4]+"</div><input type=\"hidden\" id=\"o_"+(i+1)+"_5\" name=\"o_"+(i+1)+"_5\" value=\""+columns[4]+"\"></td>");//����
			out.println("<td><div id=\"ov_"+(i+1)+"_6\">1</div><input type=\"hidden\" id=\"o_"+(i+1)+"_6\" name=\"o_"+(i+1)+"_6\" value=\"1\"></td>");//��ѯ��Ĭ��1
			out.println("<td><div id=\"ov_"+(i+1)+"_7\">0</div><input type=\"hidden\" id=\"o_"+(i+1)+"_7\" name=\"o_"+(i+1)+"_7\" value=\"0\"></td>");//���á�Ĭ��0
			out.println("<td><div id=\"ov_"+(i+1)+"_8\">&nbsp</div><input type=\"hidden\" id=\"o_"+(i+1)+"_8\" name=\"o_"+(i+1)+"_8\" value=\"\"></td>");//���ӱ�
			out.println("<td><div id=\"ov_"+(i+1)+"_9\">0</div><input type=\"hidden\" id=\"o_"+(i+1)+"_9\" name=\"o_"+(i+1)+"_9\" value=\"0\"></td>");//����
			out.println("<td><div id=\"ov_"+(i+1)+"_10\">style2</div><input type=\"hidden\" id=\"o_"+(i+1)+"_10\" name=\"o_"+(i+1)+"_10\" value=\"style2\"></td>");//��ʽ
			out.println("<td><div id=\"ov_"+(i+1)+"_11\">"+columns[3]+"</div><input type=\"hidden\" id=\"o_"+(i+1)+"_11\" name=\"o_"+(i+1)+"_11\" value=\""+columns[3]+"\"></td>");//Ϊ��
			out.println("<td><div id=\"ov_"+(i+1)+"_12\">&nbsp</div><input type=\"hidden\" id=\"o_"+(i+1)+"_12\" name=\"o_"+(i+1)+"_12\" value=\"\"></td>");//��ѡ
			out.println("<td><div id=\"ov_"+(i+1)+"_13\">&nbsp</div><input type=\"hidden\" id=\"o_"+(i+1)+"_13\" name=\"o_"+(i+1)+"_13\" value=\"\"></td>");//����
			out.println("<td><div id=\"ov_"+(i+1)+"_14\">&nbsp</div><input type=\"hidden\" id=\"o_"+(i+1)+"_14\" name=\"o_"+(i+1)+"_14\" value=\"\"></td>");//ȱʡֵ
			out.println("<td><div id=\"ov_"+(i+1)+"_15\">&nbsp</div><input type=\"hidden\" id=\"o_"+(i+1)+"_15\" name=\"o_"+(i+1)+"_15\" value=\"\"></td>");//ȱʡֵ
			out.println("</tr>");
		}
	}

%>
</table>
</form>
</div>
</div>
<br>
<input type="button" name="button2" value="ʹ�òο�����" class="button3" onClick="db_reset()">
<br>
<div id="missionTable">
		  <div class="bolder">	
		   <form name="inputform" method="post" action="">
		  	<table width="100%"  border="0" cellpadding="0" cellspacing="0">
		  		<tr>
                    <th colspan="20">�����ֶ���Ϣ</th>
		        </tr>
			    <tr>			    
		            <td colspan="20"><div align="center">������⣬������ֶ�ֵ</div>
					
					</td>					
              </tr>
  <TR align="center" bgcolor="#CCCCCC" >
    <TD width=3%>&nbsp; </TD>
    <%
    // ѭ����ʾ�ֶ���ʾ����
    if ( v_table_cols != null && v_table_cols.size() > 0 ) {
    	String[] field_info = null;
        for ( int i = 0 ; i < v_table_cols.size() ; i++ ) {
        	//ȥ������
        	if( i == 0 )
        	continue;
            out.print( "<td onclick='saveCells("+i+")'" );
            field_info = (String[])v_table_cols.elementAt(i) ;
            if ( !field_info[10].equals("") ) {
                //STYLE
                out.print( "class=\"" + field_info[10] + "\" style=\"cursor:hand\">" );
            }
            //COLUMNCOMMENT
            out.print( "<strong>" + field_info[4] + "</strong>" );
            out.println( "</td>" );
        }      

    }else {
        out.println( "<script>" );
        out.println( "alert(\"�Բ��𣬴˾����ݱ�û�н����ֶε�����,����ϵ�����Ա�������ã�\");" );
        out.println( "</script>" );
    }
		
	out.println("</tr>");
	out.println("<tbody id=\"dyntable\">");
	if ( configColumn != null && configColumn.size() > 0 ){
		for (int i = 0; i < configColumn.size(); i++ ){
		    out.println("<tr onmouseover=\"mousein(this);\"  onmouseout=\"mouseout(this);\">");
		    out.println("<td><img src=\"../images/img/nolines_minus.gif\" onclick=\"deleterow(this)\" style=\"cursor:hand\"></td>");
			String[] columns = (String[]) configColumn.get(i);
			//out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_0\" name=\"t_"+(i+1)+"_0\" value=\""+columns[0]+"\" size=\"4\"><input type=\"hidden\" id=\"t_"+(i+1)+"_1\" name=\"t_"+(i+1)+"_1\" value=\""+columns[1]+"\"></td>");//���
			//out.println("<td id=\"t_"+columns[2]+"_1\">"+columns[1]+"</td>");//����
			out.println("<td><input type=\"hidden\" id=\"t_"+(i+1)+"_0\" name=\"t_"+(i+1)+"_0\" value=\""+columns[0]+"\"><input type=\"text\" id=\"t_"+(i+1)+"_1\" name=\"t_"+(i+1)+"_1\" value=\""+(columns[1]!=null?columns[1]:"")+"\" size=\"4\"></td>");//����
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_2\" name=\"t_"+(i+1)+"_2\" value=\""+(columns[2]!=null?columns[2]:"")+"\" size=\"10\"></td>");//����
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_3\" name=\"t_"+(i+1)+"_3\" value=\""+(columns[3]!=null?columns[3]:"")+"\" size=\"4\"></td>");//������
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_4\" name=\"t_"+(i+1)+"_4\" value=\""+(columns[4]!=null?columns[4]:"")+"\" size=\"10\"></td>");//��ע��
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_5\" name=\"t_"+(i+1)+"_5\" value=\""+(columns[5]!=null?columns[5]:"")+"\" size=\"4\"></td>");//����
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_6\" name=\"t_"+(i+1)+"_6\" value=\""+(columns[6]!=null?columns[6]:"")+"\" size=\"4\"></td>");//��ѯ��
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_7\" name=\"t_"+(i+1)+"_7\" value=\""+(columns[7]!=null?columns[7]:"")+"\" size=\"4\"></td>");//����
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_8\" name=\"t_"+(i+1)+"_8\" value=\""+(columns[8]!=null?columns[8]:"")+"\" size=\"4\"></td>");//���ӱ�
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_9\" name=\"t_"+(i+1)+"_9\" value=\""+(columns[9]!=null?columns[9]:"")+"\" size=\"4\"></td>");//����
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_10\" name=\"t_"+(i+1)+"_10\" value=\""+(columns[10]!=null?columns[10]:"")+"\" size=\"4\"></td>");//��ʽ
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_11\" name=\"t_"+(i+1)+"_11\" value=\""+(columns[11]!=null?columns[11]:"")+"\" size=\"4\"></td>");//Ϊ��
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_12\" name=\"t_"+(i+1)+"_12\" value=\""+(columns[12]!=null?columns[12]:"")+"\" size=\"4\"></td>");//��ѡ
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_13\" name=\"t_"+(i+1)+"_13\" value=\""+(columns[13]!=null?columns[13]:"")+"\" size=\"4\"></td>");//����
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_14\" name=\"t_"+(i+1)+"_14\" value=\""+(columns[14]!=null?columns[14]:"")+"\" size=\"4\"></td>");//ȱʡֵ
			out.println("<td><input type=\"text\" id=\"t_"+(i+1)+"_15\" name=\"t_"+(i+1)+"_15\" value=\""+(columns[15]!=null?columns[15]:"")+"\" size=\"4\"></td>");//ȱʡֵ

			out.println("</tr>");
		}
	}
			out.println("</tbody>");
%>
	<tr><td colspan="20"><div align="center">
	  <input type="button" name="button" value="�ύ" class="button" onClick="commit()">
	  <input type="button" name="button3" value="����" class="button" onClick="window.location.reload()">
	</div></td></tr>
</table>
</form>
</div>
</div>
</body>
</html>