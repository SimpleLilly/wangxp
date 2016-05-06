<%@ page contentType="text/html;charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.funtalk.common.DataConnection" %>
<%@ page import="com.funtalk.common.Member" %>

<HTML><HEAD>
<META content="text/html; charset=gb2312" http-equiv=Content-Type>
<link href="../../css/publicStyle_jifei.css" rel="stylesheet" type="text/css">
</HEAD> 

<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="/images/bline.gif">

<form name="listform" method="post" action="AuditRep.jsp">
<div id="infoList"> 
 <div class="title">
<TABLE  border=0 cellPadding=1 cellSpacing=0 width=100% align=center>
<TR><TD height=30 vAlign=bottom width="100%">
<%

	Member currentUser = (Member)session.getAttribute("currentUser");
	
	String userId = currentUser.getUserid();
    int i;
    String str,ystr,mstr,sql,query_id=null,threeSP="",dbStr="DBSETTLE";
    List sp_val=null;
    Vector sp_all=new Vector();
    Vector sp_1=new Vector();
    Vector sp_0=new Vector();

    String year=request.getParameter("C_0");
    String month=request.getParameter("C_1");
    String flag_and_svc = request.getParameter("C_2");
    String settle_type = request.getParameter("settle_type");
    
    if ((settle_type==null)||(settle_type.equals("")))
        settle_type = "0";
        
    String service_class = request.getParameter("service_class");
    if ((service_class==null)||(service_class.equals("")))
        service_class = "ALL";
        
    String[] params = flag_and_svc.split(",");
    String svc = null;
    String flag = null;
    if(params.length >1){
        svc = params[1];
        flag = params[0];
    }else{
    	svc = params[1];
    	flag = params[0];
    }
    	
    String sp_code=request.getParameter("sp_code");
    String audit_flag=request.getParameter("audit_flag");
    //String flag= ( request.getParameter("flag")==null )  ? "1" : request.getParameter("flag");
	String guidang=request.getParameter("guidang");
	
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
    Date now = new Date();
    String DateStr = df.format(now);
    String yc = DateStr.substring(0,4);
    String mc = DateStr.substring(4,6);
    String dc = DateStr.substring(6,8);
    int mi=Integer.parseInt(mc)-1;
    int yi=Integer.parseInt(yc);
    if(mi==0)
    {
        mi=12;
        yi=yi-1;
    }
    if(year==null)   year=yi+"";
    if(month==null)  month=(mi>=10)?(mi+""):("0"+mi);
	
    if(svc==null)
        query_id=null;
    else if(svc.equals("WAP"))
        query_id="101";
    else if(svc.equals("VOICE"))
        query_id="102";
    else if(svc.equals("XL"))
        query_id="105";
    else if(svc.equals("1019"))
        query_id="116";
    else
        query_id="103";
      
    DataConnection dbconn = new DataConnection();
	//�鵵����ҵ��
    if(guidang!=null && guidang.equals("guidang"))
    {
        sql="update sp_info_status set rep_gui_flag='1',gui_date=sysdate,gui_person='"+userId+"' where month='"+
        year+month+"' and v_sp_code in (select v_sp_code from v_dept_sp_code where svc_code='"+svc+"') and rep_audit_flag='1' and rep_gui_flag='0'";
        int num = dbconn.updateNotBind(sql,dbStr);
        out.println("<script>alert('�鵵"+num+"�������SP')</script>");
    }

    //���SP
    if(sp_code!=null && !sp_code.equals(""))
    {
        sql="update sp_info_status set rep_audit_flag='"+audit_flag+"',rep_date=sysdate,rep_person='"+userId+"' where month='"+
              year+month+"' and v_sp_code='"+sp_code+"'";
        dbconn.updateNotBind(sql,dbStr);        
    }
    //SP�����б�
    if(year!=null)
    {
        String str_fc=(settle_type.equals("0"))?"sum(FCy-kh_feey-db_feey)":"sum(FC-kh_fee-db_fee)";      
            
        sql="select a.*,nvl(b.fc,0) from ("+
           "select v_sp_code,max(rep_audit_flag),max(three_flag),max(status),max(rep_gui_flag),"+
           "max(rep_person),max(gui_person),max(sp_name),max(shi_flag) from sp_info_status "+
           "where month='" + year + month + "' and stop_flag ='0' and svc_code='" + svc + "' group by v_sp_code) a,(select sp_code,"+
           str_fc+" fc from v_sp_02_rpt where month='"+year+month+"' and svc_code='"+svc+"' and (service_class='"+service_class+"' or 'ALL'='"+service_class+"') group by sp_code) b "+
           "where a.v_sp_code=b.sp_code(+) and a.v_sp_code not in (select sp_code from sp_other_sm) order by a.v_sp_code";

        sp_val = dbconn.queryNotBind(sql,dbStr);
        if((sp_val!=null)&&(sp_val.size()>0))
        {
           String tmpS = (settle_type.equals("1"))?"ʵ��":"Ӧ��";
           for(i=0; i<sp_val.size(); i++)
           {
               String[] ar= (String[])sp_val.get(i);
               if(settle_type.equals("0"))
               tmpS = "Ӧ��";
               /*String tmpS = "Ӧ��";*/
               else
               {
                   if((ar[8]!=null)&&(ar[8].equals("1")))
                   tmpS = "ʵ��";
                   else
                   tmpS = "Ӧ��";
               }
               sp_all.addElement(ar[0]+","+tmpS+","+ar[7]);
               if(ar[1].equals("1"))
                   sp_1.addElement(ar[0]+","+ar[4]+","+ar[5]+","+ar[6]);
               else
                   sp_0.addElement(ar[0]+","+ar[3]+","+ar[9]+","+ar[8]);

               if(ar[2].equals("1"))
                   threeSP+=ar[0]+",";
           }
        }
    }

    String svc_name="";
	if(svc.equals("SM"))	         svc_name = "����";
	else if(svc.equals("WAP"))	     svc_name = "WAPҵ��";
	else if(svc.equals("VOICE"))     svc_name = "����";
	else if(svc.equals("OTHER"))     svc_name = "���ݲ�ҵ��";
	else if(svc.equals("1019"))      svc_name = "�ͷ���ҵ��";
	else if(svc.equals("XL"))        svc_name = "����ҵ��";
	 
	out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+svc_name);
%>
  </TD>
</TR>
</TABLE>

  </div>
   </div>
<TABLE  border=0 cellPadding=1 cellSpacing=0 width=100% align=center>
<TR><TD>   
<%
    ystr="&nbsp;&nbsp;���<select class='input'  name='C_0' onchange='myclear()'>";
    for(i=yi-2; i<=yi+1; i++)
        ystr+="<option value='"+i+"'"+((year.equals(i+""))?(" selected "):"")+">"+i+"</option>";
    out.println(ystr+"</select>");

    mstr="&nbsp;&nbsp;�·�<select class='input' name='C_1' onchange='myclear()'>";
    for(i=1; i<=12; i++)
    {
        str = (i>=10)?(i+""):("0"+i);
        mstr+="<option value='"+str+"'"+((month.equals(str))?(" selected "):"")+">"+str+"</option>";
    }
    out.println(mstr+"</select>");
    
%>

   &nbsp;&nbsp;���㷽ʽ<select class='input' name='settle_type' onchange='myclear()'>
   <option value='0' <%=((settle_type.equals("0"))?"selected":"")%> >��Ӧ��</option>
   <option value='1' <%=((settle_type.equals("1"))?"selected":"")%> >��ʵ��</option>
   </select>
   &nbsp;&nbsp;CG��<select class='input' name='service_class' onchange='myclear()'>
   <option value='ALL' <%=((service_class.equals("ALL"))?"selected":"")%> >ȫ��</option>
   <option value='C' <%=((service_class.equals("C"))?"selected":"")%> >CDMA</option>
   <option value='G' <%=((service_class.equals("G"))?"selected":"")%> >GSM</option>
   </select>
  &nbsp;&nbsp;&nbsp;<input class=button type=submit value="��ѯ">&nbsp;&nbsp;&nbsp;

  </TD>
</TR>
<tr><td height=5 colspan=2><HR noShade SIZE=1></TD></TR>
</TABLE>


<TABLE width="98%" border=0 align="center" cellPadding=0 cellSpacing=2 >
  <TR align="center" class=font5>
    <td width="40%"><strong>������<%=sp_all.size()%>����</strong></td>
    <td width="30%"><strong>����ˣ�<%=sp_1.size()%>����</strong></td>
    <td width="30%"><strong>δ��ˣ�<%=sp_0.size()%>����</strong></td>
    </tr>
    <tr>
<td><select  style ='width : 100%'  name='muti1' size=20>
<%
    str="";
    for(i=0; i<sp_all.size(); i++)
    {
        String tmpStr=sp_all.elementAt(i).toString();
        str+="<option value="+tmpStr+">"+tmpStr+"</option>";
    }
    out.println(str);
 %>
</select></td>

<td><select  style ='width : 100%' name='muti2'   size=20>
<%
    str="";
    for(i=0; i<sp_1.size(); i++)
    {
        String tmpStr=sp_1.elementAt(i).toString();
        String[] temp = tmpStr.split(",");
        str+="<option value="+temp[0]+","+temp[1]+">"+temp[0];
        str+=" �����("+temp[2]+")";
        if(temp[1].equals("1")){
        	str+=" �鵵��";
        	str+="("+temp[3]+")";
        }
        str+= "</option>";
    }
    out.println(str);
 %>
</select></td>

<td><select  style ='width : 100%'  name='muti3'  size=20>
<%
    str="";
    for(i=0; i<sp_0.size(); i++)
    {
        String tmpStr=sp_0.elementAt(i).toString();
        String[] temp = tmpStr.split(",");
        str+="<option value="+temp[0]+","+temp[1]+","+temp[3]+">"+temp[0]+" ��ͬ"+temp[1]+" ������:"+temp[2]+"</option>";
    }
    out.println(str);
 %>
</select></td>
</tr>

<tr><td align="center"><input type=button value= �鿴 class="button" onClick="getRep(1)"></td>
  <td align="center"><input type=button value= �鿴 class="button" onClick="getRep(2)">
   <% if((flag!=null)&&(flag.equals("1")))
         out.println("<input type=button value= ȡ����� class=\"button\" onClick=\"setAudit(0)\">");
   %>
  </td><td align="center"><input type=button value= �鿴 class="button" onClick="getRep(3)">
   <% if((flag!=null)&&(flag.equals("1")))
         out.println("<input type=button value= ��� class=\"button\" onClick=\"setAudit(1)\">");
   %>
   </td>
<tr>
</TABLE>
 <!-- hidden ���� -->
 <input type="hidden" name="sp_code" value="">
 <input type="hidden" name="audit_flag" value="">
 <input type="hidden" name="guidang" value="">

 <input type="hidden" name="flag" value="<%=flag%>">
 <input type="hidden" name="query_id" value="">
  <input type="hidden" name="C_2" value="<%=flag+","+svc%>">

</FORM>

<SCRIPT language=javascript>
<!--
function myclear()
{
	listform.guidang.value="";
	listform.submit();
}

//�鵵
function spguidang()
{
     if(confirm("  ȷ��Ҫ�鵵����������?"))
     {
        listform.guidang.value="guidang";
        listform.submit();
        return true;
     }
     else
        return false;
}

function getValue(v)
{
     if(v.value=="")
     {
        alert("��ѡ��Ҫ�鿴��SP����");
        return false;
     }

     alert(v.value);
}

//���
function setAudit(flag)
{
     listform.guidang.value="";
     var tmp;
     var str;
     if(flag=="1")
     {
         tmp = listform.muti3;
         var arr = tmp.value.split(",");
         if(arr[1] =="����")
         {
             alert("��ͬ���ڣ��������!");
             return false;
         }
         
         if(arr[2] =="1")
         {
             <%
                int n_i = Integer.parseInt(yc)*12+Integer.parseInt(mc);
                int r_i = Integer.parseInt(year)*12+Integer.parseInt(month);
             %>
             if( ((<%=dc%> >=10)&&(<%=n_i%> < <%=r_i%>+2)) ||
                 ((<%=dc%> < 10)&&(<%=n_i%> < <%=r_i%>+3)) )
             {
                alert("��ʵ�ս��㣬���ڻ��������!");
                return false;
             }
         }
         
         str = "���";
         listform.sp_code.value=arr[0];
         
         //��ȡ�����query_id
         var ths,qid;
         ths = <%="\""+threeSP+"\""%>;
         if(ths.indexOf(arr[0]+",")==-1)
             qid = <%="\""+query_id+"\""%>;
         else
             qid = "104";
         listform.query_id.value=qid;
     }
     else
     {
     
         tmp = listform.muti2;
         var arr = tmp.value.split(",");
         if(arr[1] =="1"){
         alert("�ѹ鵵���ݣ�����ȡ�����!");
         return false;
         }
         listform.sp_code.value = arr[0];
         str = "ȡ�����";
     }
     if(tmp.value=="")
     {
        alert("��ѡ��Ҫ"+str+"��SP����");
        return false;
     }
     if(confirm("ȷ��Ҫ"+str+"ѡ�е�SP��?"))
     {
        //listform.sp_code.value=tmp.value;
        listform.audit_flag.value=flag;
        listform.submit();
        return true;
     }
     else
        return false;
}

//�鿴����
function getRep(flag)
{
     var tmp;
     var sp_code, ths, qid;
     if(flag=="1")
         tmp = listform.muti1;
     else if(flag=="2")
         tmp = listform.muti2;
     else
         tmp = listform.muti3;
     var arr =tmp.value.split(",");
     sp_code=arr[0];

     if(sp_code=="")
     {
        alert("��ѡ��SP����");
        return false;
     }
     ths = <%="\""+threeSP+"\""%>;
     if(ths.indexOf(sp_code+",")==-1)
         qid = <%="\""+query_id+"\""%>;
     else
         qid = "104";

     url="RepQueryMiddle.jsp?queryid="+qid+"&C_3="+document.listform.settle_type.value+"&C_0=<%=year%>&C_1=<%=month%>&C_2="+sp_code+"&C_4=<%=service_class%>";

     window.open(url , "_blank" , "resizable=10,scrollbars=yes")
     return true;
}
-->
</SCRIPT>

</BODY>
</HTML>


