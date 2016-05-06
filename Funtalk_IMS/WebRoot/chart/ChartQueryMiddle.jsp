<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.funtalk.bean.QryWhereBean" %>
<%@ page import="com.funtalk.bean.ChartQueryBean" %>
<%@ page import="com.funtalk.bean.ChartBean" %>
<%@ page import="com.funtalk.common.ToolsOfSystem" %>

<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>

<html>

<head>
<base target="middle">
<meta http-equiv="Content-Language" content="zh-cn">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link href="../css/publicStyle_jifei.css" rel="stylesheet" type="text/css">
<title> </title>
</head>


<script language="JavaScript">
    function mousein(OMO)
    {
        OMO.style.backgroundColor='#FFFFCC';
    }

    function mouseout(OMO)
    {
        OMO.style.backgroundColor='#ffffff';
    }
</script>

<BODY leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="/images/bline.gif" >

<%
	request.setCharacterEncoding("GBK");
	///////Member currentUser = (Member)session.getAttribute("currentUser");
	//地市选择菜单，省用户可看所有地市，地市只可看本地市
	String userType = "0";
	String userLocal = "999";
	String username = "";
    int i,j;
    String queryid = (String) request.getParameter("queryid");
    if((queryid==null)||(queryid.length()==0))
        return;
        

        
        
        
    ChartQueryBean bean = new ChartQueryBean();
    //System.out.println(bean.RepDB);	
    String vvv[] =bean.GetQueryInfo(queryid);   //查询信息

    List param = QryWhereBean.GetParamInfo(queryid, bean.WHERE_TABLE, bean.dbconn, bean.DBStr,bean.RepDB,userType,userLocal);
    //List param = (List)session.getAttribute("param");
    if(param==null)
    {
        return;
    }
    String str_param="";
    for(i=0;i<param.size();i++)
    {
        String temp[] = (String [])param.get(i);
        String value = request.getParameter(temp[1]+"_"+i);
        if(temp[1].equals("F"))
            value += request.getParameter(temp[1]+"_"+i+"_1");
        temp[5] = value;
        str_param+=temp[0]+":"+temp[5]+"|";
        //param.set(i,temp);
    }
    String query_name = vvv[1];
    

    
    
    Vector data = bean.GetQueryData(queryid, param);
    if(data==null)  //没有数据
    {
        out.print("<br><br><table width=100% align=center><tr><td align=center>");
        out.print("<font color=red>当前无数据显示！</font></td></tr></table></body></html>");
        return;
    }

    //结果数据
    List DataV   = (List)data.elementAt(0);
    
    System.out.println("==========>DataV="+DataV);
    System.out.println("==========>DataV.size="+DataV.size());
    
    //查询信息
    String qryInfo[] = (String [])data.elementAt(1);
    String RepNameStr  = qryInfo[3];  //标题
    String chartSize = qryInfo[4]; //图片大小
    String ChartType = qryInfo[5];    //图片类型
    String isMulti = qryInfo[6];      //0单图，1多图
    String XType = qryInfo[7];        //X轴类型
	String[] filedIdx = (String[])data.elementAt(2);
    //查询条件
    String ParamString = "";
    for(i=0; i<param.size(); i++)
    {
        String v[] = (String [])param.get(i);
        //将下拉框value替换为valueName
        String temp = "";
		if( v[1].equals("C") && v[3].indexOf("|") > 0 )
		{
		//System.out.println(v[3]);
			String[] arr = ToolsOfSystem.splitString(v[3]);//v[3].split("|");
			String[] arr2 = ToolsOfSystem.splitString(v[6]);
			
			//String[] arr2 = v[6].split("|");
			for(int k = 0; k < arr.length; k++)
			{
				if(arr[k].equals(v[5]))
				{
					temp = arr2[k];
					break;
				}
			}
		}else{
			temp = v[5];
		}
        ParamString = ParamString + "<b>" + v[0] + ":</b>&nbsp;" + temp + "&nbsp;&nbsp;&nbsp;";
    }
    //生成图片文件
    String UrlPath = "/chart/jpg/";
    String UrlFile = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
    UrlFile += "_" + (int)(Math.random()*100000000) + "_" + session.hashCode() + ".png";
    String JpgPath = request.getSession().getServletContext().getRealPath("/") + UrlPath;

    //先删除12小时以前的历史文件
    ChartBean.DeleteTempFile(JpgPath, 12);
	String[] arr = chartSize.split(",");
    int Width = Integer.parseInt(arr[0]);
    int Height = Integer.parseInt(arr[1]);
    int seriesCount = ((String [])DataV.get(0)).length-1;//需要增加指定series
    boolean flag = false;
    //各业务类型费用比较图　特殊处理
    //if(queryid.equals("9") && DataV.size()>25){
    //	System.out.println("dfdfdf");
    //	System.out.println(DataV.size());
    	//换成多图
    //	isMulti = "1";
    //}
	//单图
    if(isMulti.equals("0"))
    {
        if(ChartType.equals("2"))  //饼图
            flag = ChartBean.createMultiPiesChart(DataV, JpgPath+UrlFile,Width,Height, RepNameStr, new PrintWriter(out));
        else
            flag = ChartBean.createCharts(DataV,filedIdx, JpgPath+UrlFile,ChartType,XType,Width,Height, RepNameStr, "", new PrintWriter(out));
    }
    //多图
    else
    {
        UrlFile = UrlFile.substring(0, UrlFile.length()-4);
        Width = (int)(Width*0.75);
        Height = (int)(Height*0.75);
    //    if(queryid.equals("9")){
    //    	int subSize = DataV.size()/4;
    //    	int beginPos = 0;
	//        for(i=1; i<=4; i++)
	//        {
	//        	System.out.println(DataV.size()/4+1);
	//            List tmpDataV = new ArrayList();
	//            tmpDataV = DataV.subList(beginPos,subSize*i);
	///            beginPos = subSize * i + 1;
	//            for(j=0; j<tmpDataV.size(); j++)
	//            {
	//                String v[] = (String [])tmpDataV.get(j);
	//                for(int m =0; m< v.length; m++){
	//                	System.out.print(v[m]);
	               
	//                }
	//                System.out.println();
	                //String vv[] = new String[2];
	                //vv[0] = v[0];
	                //vv[1] = v[i];
	                //tmpDataV.add(vv);
	//            }
	//            String tmpFile = JpgPath+UrlFile+"_"+i+".png";
	 //           System.out.println(tmpFile);
	 //           if(ChartType.equals("2"))  //饼图
	//                flag = ChartBean.createMultiPiesChart(tmpDataV, tmpFile,Width,Height, RepNameStr, new PrintWriter(out));
	//            else
	//                flag = ChartBean.createCharts(tmpDataV,filedIdx, tmpFile,ChartType,XType,Width,Height, RepNameStr, "", new PrintWriter(out));
	          //      flag = ChartBean.createCharts(DataV,filedIdx, JpgPath+UrlFile,ChartType,XType,Width,Height, RepNameStr, "", new PrintWriter(out));
	//        }
   //     }else{
            System.out.println("seriesCount "+seriesCount);
	        for(i=1; i<=seriesCount; i++)
	        {
	            List tmpDataV = new ArrayList();
	            for(j=0; j<DataV.size(); j++)
	            {
	                String v[] = (String [])DataV.get(j);
	                String vv[] = new String[2];
	                vv[0] = v[0];
	                vv[1] = v[i];
	                tmpDataV.add(vv);
	            }
	            String tmpFile = JpgPath+UrlFile+"_"+i+".png";
	            if(ChartType.equals("2"))  //饼图
	                flag = ChartBean.createMultiPiesChart(tmpDataV, tmpFile,Width,Height, RepNameStr, new PrintWriter(out));
	            else
	                flag = ChartBean.createCharts(tmpDataV,filedIdx, tmpFile,ChartType,XType,Width,Height, RepNameStr, "", new PrintWriter(out));
	        }
    
    //    }
    }
    if(!flag)
    {
        out.print("<br><br><table width=100% align=center><tr><td align=center>");
        out.print("<font color=red>无法生成图片</font></td></tr></table></body></html>");
        return;
    }
%>

<!-- 间隔线 -->
<table width="100%"  border="0" cellpadding="0" cellspacing="0" class="OUTLine">
  <tr>
    <td><div id="HBoder"><img src="../images/blank.gif"></div></td>
  </tr>
</table>
<!-- 间隔线 -->
<div id="infoList">
      <div class="title"><%=ParamString%></div>
 		<div id="missionTable">
		<center>
  <table width="99%" border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#61BBC6" >
    <% if(isMulti.equals("0"))  { %>
    <tr>
      <td colspan="<%=seriesCount+1%>" height="<%=Height+5%>"><p align="center"> <img border="0" src="<%=UrlPath+UrlFile%>" width="<%=Width%>" height="<%=Height%>" usemap="#<%=JpgPath+UrlFile%>"></td>
    </tr>
    <% }
     else
     {
     	 if(queryid.equals("9")){
     	 	 for(i=1; i<=4; i=i+2)
	         {
	             String tmpFile1 = UrlFile+"_"+i+".png";
	             String tmpFile2 = UrlFile+"_"+(i+1)+".png";
	             out.println("<tr><td colspan=\""+(seriesCount+1)+"\"><p align=\"center\">");
	             out.println("<img border=\"0\" src=\""+UrlPath+tmpFile1+"\" width=\""+Width+
	                   "\" height=\""+Height+"\" usemap=\"#"+JpgPath+tmpFile1+"\">");
	             if(i!=seriesCount)
	                 out.println("<img border=\"0\" src=\""+UrlPath+tmpFile2+"\" width=\""+Width+
	                   "\" height=\""+Height+"\" usemap=\"#"+JpgPath+tmpFile2+"\">");
	             out.println("</td></tr>");
	         }
     	 }else{
	     	 for(i=1; i<=seriesCount; i=i+2)
	         {
	             String tmpFile1 = UrlFile+"_"+i+".png";
	             String tmpFile2 = UrlFile+"_"+(i+1)+".png";
	             out.println("<tr><td colspan=\""+(seriesCount+1)+"\"><p align=\"center\">");
	             out.println("<img border=\"0\" src=\""+UrlPath+tmpFile1+"\" width=\""+Width+
	                   "\" height=\""+Height+"\" usemap=\"#"+JpgPath+tmpFile1+"\">");
	             if(i!=seriesCount)
	                 out.println("<img border=\"0\" src=\""+UrlPath+tmpFile2+"\" width=\""+Width+
	                   "\" height=\""+Height+"\" usemap=\"#"+JpgPath+tmpFile2+"\">");
	             out.println("</td></tr>");
	         }
     	 }
     }
  %>


  </table>
  </center>
</div>
</div>
</div>
<div id="infoList">
      <div class="title">明细数据&nbsp;&nbsp;&nbsp;&nbsp; <%=ParamString%></div>
 		<div id="missionTable">
        <table width="100%"  border="0" cellspacing="0" cellpadding="0">
		<%
		      String tmpT[] = (String [])DataV.get(0);
      out.println("<tr>");
      for(i=0; i<tmpT.length; i++)
          out.println("<th><b>"+tmpT[i]+"</b></th>");
      out.println("</tr>");
	  
	  for(i=1; i<DataV.size(); i++)
      {
          String tmpV[] = (String [])DataV.get(i);
          out.println("<tr onmouseover=\"mousein(this);\" onmouseout=\"mouseout(this);\" bgColor=\"#ffffff\">");
          out.println("<td >"+tmpV[0]+"</td>");
          for(j=1; j<tmpV.length; j++)
              out.println("<td>"+tmpV[j]+"</td>");
          out.println("</tr>");
      }
	  %>
        </table>
    </div>
</div>
<br>
<br>
</body>
</html>
