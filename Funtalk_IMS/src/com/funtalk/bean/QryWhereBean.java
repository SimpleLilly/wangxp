package com.funtalk.bean;


import java.util.*;

import com.funtalk.common.DataConnection;

/** 查询条件通用类.
 *
 * @author  wangjs
 * @version 1, 2
 */
public class QryWhereBean
{

    /*
    private String TEXT_TYPE="T";        //文本框类型
    private String CBOX_TYPE="C";        //下拉框类型
    private String DAY_TYPE="D";         //日期框
    private String TIME_TYPE="S";        //时间框
    private String DAYTIME_TYPE="F";     //日期时间框
    private String HIDDEN_TYPE="H";     //隐藏框
    */

    /************** 构造函数 **********************************************/
    public QryWhereBean()
    {
    }

    /************** 静态函数 **********************************************/

    /**
     * 替换SQL语句中的动态表名
     * @param   sSQL 输入的原始SQL语句
     * @param   strFlag 需要替换的字符串
     * @param   tableSql 用于获取动态表名的SQL语句
     * @return  替换后的SQL语句
     */
    public static String RepDynamicTable(String sSQL, String strFlag, String tableSql,
                     DataConnection dbconn, String DBStr)
    {
        int i;
        i=sSQL.indexOf(strFlag);
        if(i<0)
            return sSQL;

        List val = dbconn.queryNotBind(tableSql, DBStr);
        if((val==null)||(val.size()<=0))
            return sSQL;

        String repStr = "";
        if(val.size()==1)
            repStr = ((String [])val.get(0))[0];
        else
        {
            repStr = "(";
            for(i=0; i<val.size(); i++)
            {
                String ar[] = (String [])val.get(i);
                if(i>0)
                    repStr = repStr + " union ";
                repStr = repStr + "select * from "+ar[0];
            }
             repStr = repStr + ")";
        }
        return ToolsOfSystem.replace(sSQL, strFlag, repStr);
    }

    /**
     *  获取校验用户输入的脚本
     * @param   param 查询条件
     * @return  校验用户输入的脚本
     */
    public static String GetCheckStr(List param)
    {
        int i;
        String check, str="", where, msg, pname, pname1;

        if(param==null)
            return str;

        for(i=0; i<param.size(); i++)
        {
            String v[] = (String [])param.get(i);
            check = (v[4]+"").trim();
            pname = v[1] + "_" + (i);

            //自动加入对时间的校验
            if(v[1].equals("F"))
            {
                str += "if(!checkTime(F_"+i+"_1.value))\n";
                str += "{\nalert(\"" + v[0] + "的值不是合法时间\");\n";
                str += "F_"+i+"_1.focus();\n";
                str += "return;\n}\n";
            }

            if((check==null)||(check.length()<=0))
                continue;
            int j = check.indexOf("@@");
            if(j>=0)
            {
                where = check.substring(0, j);
                msg = check.substring(j+2);
            }
            else
            {
                where = check;
                msg = "";
            }
            str += "if(" + where + ")\n";
            str += "{\nalert(\"" + msg + "\");\n";
            str += pname+ ".focus();\n";
            str += "return;\n}\n";
        }
        return str;
    }

   /**
     * 获得输入域的字符串
     * @param field 查询条件
     * @param seq 查询条件序号
     * @return 用于显示查询条件的html字符串
     */
    public static String GetInputField(String[] field, int seq)
    {
         String intputString ="";
         String temp =field[1];
         if(temp.equals("T"))
         {
              intputString=field[0]+"<input class='input' type=text name='T_"+seq+"' value='"+field[2]+"'";
              if(field[2].trim().length()!=0)
              {
                  intputString= intputString+" length="+field[2].trim().length()+">";
              }
              else
              {
                   intputString= intputString+" lenth=10>";
              }

         }
         else if (temp.equals("C"))
         {
              intputString=field[0]+"<select class='input'  name='C_"+seq+"'>";
              String defaultValue =field[3];
              String tagValue =field[6];
              String[] values1 = ToolsOfSystem.splitString(defaultValue);
              String[] values2 = ToolsOfSystem.splitString(tagValue);
              for(int i=0;i<values1.length ;i++)
              {
                    intputString=intputString+"<option  value='"+values1[i]+"' "+
                       ((field[2].equals(values1[i]))?"selected":"")+">"+values2[i]+"</option>";
              }
              intputString = intputString+"</select>";
         }
         else if (temp.equals("D"))
         {
              intputString=field[0]+"<script language=JAVASCRIPT> PutDateCtlDate('D_"+seq+"','"+field[2] +"'); </script>";
         }
         else if (temp.equals("S"))
         {
              intputString=field[0]+"<input class='input' type=text name='S_"+seq+"' value='"+field[2]+"'";
              intputString= intputString+" length=8>";

         }
          else if (temp.equals("F"))
         {
              intputString=field[0]+"<script language=JAVASCRIPT> PutDateCtlDate('F_"+seq+"','"+field[2].substring(0,8) +"'); </script>";
              intputString+="<input class='input' type=text name='F_"+seq+"_1' size=6 value='"+field[2].substring(8,14) +"'>";
         }
          else if (temp.equals("H"))
          {
              intputString="<input type=hidden name='H_"+seq+"' value='"+field[2]+"'>";
          }
         return intputString;
    }

    /**
     * 根据用户输入的参数值替换SQL语句
     * @param sSQL SQL语句
     * @param param 用户输入的查询条件信息
     * @return 替换后的SQL语句
     */
    public static String ReplaceByParam(String sSQL, List param)
    {
        int i;
        String str = sSQL;
        String name, value , varlueName = "";
        i=0;
        if(param==null)
            return str;

        for(i=0; i<param.size(); i++)
        {
            String v[] = (String [])param.get(i);
            name = v[0];
            value = v[5];
            //趋势图有 ${类型} "${类型}"条件参数需处理。
            if(str.indexOf("\"${"+ value + "}\"") > 0){
				String[] arr = ToolsOfSystem.splitString(v[3]);
				String[] arr2 = ToolsOfSystem.splitString(v[6]);
				for(int k = 0; k < arr.length; k++)
				{
					if(arr[k].equals(value))
					{
						varlueName = arr2[k];
						break;
					}
				}
				str = ToolsOfSystem.replace(str, "\"${"+ name + "}\"", varlueName);
            }
            
            str = ToolsOfSystem.replace(str, "${"+ name + "}", value);
        }
        return str;
    }

    /**
     * 获取查询参数值的List(0名称,1值)
     * @param param 用户输入的查询条件信息
     * @return 查询参数值的List(0名称,1值)
     */
    public static List GetParamList(List param)
    {
        int i;
        String vStr, value;
        List val = new ArrayList();
        if(param==null)
            return val;
        for(i=0; i<param.size(); i++)
        {
            String v[] = (String [])param.get(i);
            for(int k =0; k < v.length; k++)
            {
            	System.out.print(v[k]+" ");
            }
            System.out.println();
            value = v[5];//ToolsOfSystem.GBKToISO(v[5]);
            if((v[6]==null)||(v[6].equals("")))
                vStr = value;
            else
            {
                vStr = "";
                String[] values1 = ToolsOfSystem.splitString(v[3]);
                String[] values2 = ToolsOfSystem.splitString(v[6]);
                for(int j=0; j<values1.length; j++)
                {
                    if(values1[j].equals(value))
                    {
                        vStr = values2[j];
                        break;
                    }
                }
                if(vStr.equals(""))
                    vStr = value;
            }
            String tmpV[] = new String[2];
            tmpV[0] = v[0];
            tmpV[1] = vStr;
            val.add(tmpV);
        }
        return val;
    }

    /**
     * 获取SQL语句中的查询条件信息
     * @param   query_id 查询编号
     * @return  查询条件列表, 0为参数名, 1为参数类型, 2为默认值, 3为下拉框值“|”分割,4校验方法,5用户输入值,6标签值
     */
    public static List GetParamInfo(String query_id, String whereTable, DataConnection dbconn,
           String DBStr, String RepDB ,String userType, String userLocal)
    {
        int i, j;
        List tmpV = null;
        String chnName, whereType, defaultV, refSql;
        String sSQL = "select chn_name, where_type, default_value," +
                      "check_str, ref_sql from " + whereTable + " where " +
                      " query_id = " + query_id + " order by where_seq";
        List val = dbconn.queryNotBind(sSQL, DBStr);
        
        
        if ( (val==null) || (val.size() <= 0) )
            return null;

        List returnVal = new ArrayList();
        for(i=0; i<val.size(); i++)
        {
            String[] ar = (String[]) val.get(i);
            String value[] = new String[7];

            chnName = ar[0];
            whereType = ar[1];
            defaultV = (ar[2]==null)?"":ar[2];
            refSql = (ar[4]==null)?"":ar[4];

            value[0] = chnName;
            value[1] = whereType;

            //取默认值
            if(defaultV.indexOf("select")<0)
                value[2] = defaultV;
            else
            {
                tmpV = dbconn.queryNotBind(defaultV, RepDB);
                if ((tmpV!=null)&&(tmpV.size() > 0) )
                {
                    String[] tmpAr = (String[]) tmpV.get(0);
                    value[2] = tmpAr[0];
                }
            }

            //取下拉框的值
            if(whereType.equals("C"))   //下拉框类型
            {
                String tmpStr1 = "",tmpStr2 = "";
                if(refSql.indexOf("select")<0)
                {
                    String refAr[] = ToolsOfSystem.splitString(refSql);
                    tmpV = new ArrayList();
                    for(int k=0; k<refAr.length; k++)
                    {
                        //tmpV.add(refAr[k].split(","));
                        tmpV.add(ToolsOfSystem.mySplit(refAr[k],","));
                    }
                }
                else
                {

                    //xuyd add 增加下拉框地市用户进入只能选本地市。
                    //select city_code "@userLocal",city_name from city_list_prov order by city_code
                	String sqlWhere = "";
                	String orderBy = "";
                    if(refSql.toUpperCase().indexOf("@USERLOCAL") < 0)
                    {
                        tmpV = dbconn.queryNotBind(refSql, RepDB);
                    }else{
                    	if(userType.equals("0"))
                    	{
                            tmpV = dbconn.queryNotBind(refSql, RepDB);
                    	}else{
                    		if( refSql.toUpperCase().indexOf("WHERE") < 0 ){
                    			sqlWhere = " where (trim("+GetSqlField(refSql,"@userLocal")+")='"+userLocal+"' or long_code='"+userLocal+"') ";
	                    		if(refSql.toUpperCase().indexOf("ORDER BY") < 0)
	                    			refSql =refSql + sqlWhere;		
	                    		else
	                    			refSql = refSql.substring(0, refSql.toUpperCase().indexOf("ORDER BY")-1) + sqlWhere + refSql.substring(refSql.toUpperCase().indexOf("ORDER BY"));
                    		}
                    		else
                    			refSql.replaceAll("where", " where (trim("+GetSqlField(refSql,"@userLocal")+")='"+userLocal+"' or long_code='"+userLocal+"') " );
                    		
                    		tmpV = dbconn.queryNotBind(refSql, RepDB);
                    	}

                    }
                }

                if ((tmpV!=null)&&(tmpV.size() > 0) )
                {
                    for(j=0; j<tmpV.size(); j++)
                    {
                        if(j>0)
                        {
                            tmpStr1 = tmpStr1 + "|";
                            tmpStr2 = tmpStr2 + "|";
                        }
                        String[] tmpAr = (String[]) tmpV.get(j);
                        tmpStr1 = tmpStr1 + tmpAr[0];
                        if((tmpAr.length<=1)||(tmpAr[1]==null)||(tmpAr[1].equals("")))
                            tmpStr2 = tmpStr2 + tmpAr[0];
                        else
                            tmpStr2 = tmpStr2 + tmpAr[1];
                    }
                    value[3] = tmpStr1;
                    value[6] = tmpStr2;
                }
            }
            if (value[3] == null) value[3]="";
            if (value[6] == null) value[6]="";
            if(ar[3]!=null)
                value[4] = ar[3];
            returnVal.add(value);
        }
        return returnVal;
    }
    private static String GetSqlField(String sSQL,String alias)
    {
        int len,k1=0,k2=0,k;
       
        String str="";

        len = sSQL.toUpperCase().indexOf(alias.toUpperCase());
        for(k=len-1; k>=0; k--)
        {
        	//System.out.print(sSQL.charAt(k));
        	if(sSQL.charAt(k)==' ' && k1 == 0 ){
        		k1 = k;
        	}

        	if(sSQL.charAt(k)==' ' && k1 != 0)
        		k2 = k;
        }
        str = sSQL.substring(k2+1,k1);
        //System.out.println(str);
        return str;
    }
    /************** main函数,测试用 **********************************************/
    public static void main(String args[])
    {
    	//QryWhereBean qw = new QryWhereBean();
    	QryWhereBean.GetSqlField("select city_code \"@userLocal\",city_name from city_list_prov","@userLocal");
    }
}

