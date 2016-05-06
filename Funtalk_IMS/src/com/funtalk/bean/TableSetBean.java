package com.funtalk.bean;


import java.util.*;
import com.funtalk.common.DataConnection;

/**
 * 对局数据库表进行操作的bean,包括查询、写入、更新、删除
 * <p>Title: jilinboss</p>
 * <p>Description: 思特奇计费结算综合管理系统<</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: si-tech</p>
 * @author
 * @version 1.5
 */

public class TableSetBean {

    /** 数据连接的Bean */
    public DataConnection dbconn = null;
    /** 需要连接的数据库名称 */
    public String DBStr = null;
    /** 参数表名 */
    public String TABLE_TABLE  = "t_table_info";
    /** 字段表名 */
    public String FIELD_TABLE  = "t_table_col_info";

    public TableSetBean()
    {
        dbconn = new DataConnection();
    }

    //获取表的信息
    public String[] DetailTableInfo(String table_id)
    {
        String sql = "select table_name,table_parent,table_id,helpinfo,table_title,db_str from " +
                     TABLE_TABLE + " where table_id=" + table_id;
        try
        {
            List val = dbconn.queryNotBind(sql, DBStr);
            return (String[]) val.get(0);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    //获取表的字段(oracle)
    public List GetTableField(String TableName, String db)
    {
        try
        {
            String sql;
            if(dbconn.GetDbType(db).equals("oracle"))
            {
                sql = "select a.*,nvl(b.column_position,0) from " +
                     "(select column_id,column_name,lower(data_type),data_length,decode(nullable,'N','0','1') "+
                       "from user_tab_columns where table_name='"+TableName.toUpperCase()+"') a, " +
                     "(select column_name,column_position from user_ind_columns where index_name in "+
                       "(select index_name from (select * from user_indexes where Table_name = '"+
                       TableName.toUpperCase()+"' and uniqueness = 'UNIQUE' order by generated desc "+
                       ") where rownum < 2 )) b " +
                    "where a.column_name=b.column_name(+) order by a.column_id ";
                return dbconn.queryNotBind(sql, db);
            }
            else
            {
                sql = "select colid,isnull(c.name, 'NULL') Column_name,isnull(convert(char(30), x.xtname), "+
                  "isnull(convert(char(30), get_xtypename(c.xtype, c.xdbid)), t.name)) ctype, "+
                  "c.length, convert(bit, (c.status & 8)) nullable, '0' " +
                  "from syscolumns c, systypes t, sysxtypes x where c.id = object_id('"+TableName+"') "+
                  "and c.usertype *= t.usertype and c.xtype *= x.xtid order by colid";
                List val = dbconn.queryNotBind(sql, db);
                //主键
                List pkList = dbconn.queryNotBind("sp_helpindex "+TableName, db);
                if(pkList==null)  return val;
                String pk[] = null;
                int i,j;
                for(i=0; i<pkList.size(); i++)
                {
                    String ar[] = (String[])pkList.get(i);
                    if(ar[1].indexOf(", unique")>=0)
                    {
                        pk = ar[2].split(",");
                        //pk = ToolsOfSystem.mySplit(ar[2],",");
                        break;
                    }
                }
                if(pk==null)
                    return val;
                for(i=0; i<val.size(); i++)
                {
                    String ar[] = (String [])val.get(i);
                    for(j=0; j<pk.length; j++)
                    {
                        if(ar[1].equals(pk[j].trim()))
                        {
                            ar[5] = (j+1) + "";
                            break;
                        }
                    }
                }
                return val;
            }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            return null;
        }
    }

    public String GetTableFieldXML(String TableName, List fieldInfo)
    {
        String str="<?xml version=\"1.0\" standalone=\"yes\"?><DATAPACKET Version=\"2.0\"><METADATA><FIELDS>";
        String pk="",tmpstr;
        int i;
        for(i=0; i<fieldInfo.size(); i++)
        {
            String[] field_ar = (String[]) fieldInfo.get(i);
            String fieldType;
            if(field_ar[2].indexOf("char")>=0)
                fieldType = "string";
            else if(field_ar[2].indexOf("text")>=0)
            {
                fieldType = "string";
                field_ar[3] = "4000";
            }
            else if(field_ar[2].indexOf("date")>=0)
                fieldType = "datetime";
            else
                fieldType = "r8";

            str = str + "<FIELD attrname=\""+field_ar[1]+"\" fieldtype=\""+fieldType+"\"";
            if((field_ar[4]).equals("0"))  //不能为空
               str = str + " required=\"true\"";
            if(fieldType.equals("string"))   //字符串
            {
               tmpstr = (field_ar[3].equals("0"))?"10":field_ar[3];
               str=str+" WIDTH=\""+tmpstr+"\"";
            }
            if(Integer.parseInt(field_ar[5])>0)
            {
               str = str + "><PARAM Name=\"PROVFLAGS\" Value=\"7\" Type=\"i4\" Roundtrip=\"True\"/></FIELD>";
               if(pk.length()>0)
                  pk = pk + " ";
               pk = pk + field_ar[0];
            }
            else
               str=str+"/>";
        }
        str=str+"</FIELDS>";

        if(pk.length()>0)
            str=str+"<PARAMS DEFAULT_ORDER=\""+pk+"\" PRIMARY_KEY=\""+pk+"\" LCID=\"1033\"/>";
        else
            str=str+"<PARAMS LCID=\"1033\"/>";

        return str;
    }

    //获取表的数据
    public List GetTableData(String TableName, String db, List field, int beginPos, int endPos,String[] where)
    {
        String sql="";
        int i;

        sql = "select ";
        String DBType = dbconn.GetDbType(db);
        for(i=0; i<field.size(); i++)
        {
            String ar[] = (String[])field.get(i);
            String str = (i==0)?"":",";
            if(ar[2].indexOf("date")>=0)
            {
                if(DBType.equals("oracle"))
                    sql += str + "to_char(" + ar[1] + ",'yyyymmdd hh24:mi:ss')";
                else
                    sql += str + "convert(varchar," + ar[1] + ",112)||' '||convert(varchar," + ar[1] + ",108)";
            }
            else
                sql += str + ar[1];
        }
        sql += " from "+TableName;

        if (where==null)
        {

        }
        //else
          //  sql= this.makeSelectQueryStatement(Tableid, where);
        Vector pv = dbconn.queryBindPage(sql, beginPos, endPos, db);
        return (List)(pv.elementAt(0));
    }

    //获取字段表的XML
    public String GetFieldXML(String table_id)
    {
        List data = null;
        String sSQL = "select * from " + FIELD_TABLE + " where table_id=" + table_id + " order by field_id";
        int i;

        try
        {
            data = dbconn.queryNotBind(sSQL, DBStr);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            return null;
        }
        String str = "<?xml version=\"1.0\" standalone=\"yes\"?><DATAPACKET Version=\"2.0\"><METADATA><FIELDS>"
                 + "<FIELD attrname=\"field_id\" fieldtype=\"string\" WIDTH=\"10\"/>"
                 + "<FIELD attrname=\"table_id\" fieldtype=\"string\" WIDTH=\"10\"/>"
                 + "<FIELD attrname=\"field_title\" fieldtype=\"string\" WIDTH=\"40\"/>"
                 + "<FIELD attrname=\"field_type\" fieldtype=\"string\" WIDTH=\"10\"/>"
                 + "<FIELD attrname=\"ismuti\" fieldtype=\"r8\" />"
                 + "<FIELD attrname=\"defaultvalue\" fieldtype=\"string\" WIDTH=\"50\"/>"
                 + "<FIELD attrname=\"refer_col\" fieldtype=\"string\" WIDTH=\"500\"/>"
                 + "<FIELD attrname=\"remark\" fieldtype=\"string\" WIDTH=\"1000\"/>"
                 + "</FIELDS><PARAMS LCID=\"1033\"/></METADATA><ROWDATA>";
        for(i=0; i<data.size(); i++)
        {
            String ar[] = (String[]) data.get(i);
            str += "<ROW field_id=\""+ar[1].trim()+"\" ";
            str += "table_id=\""     +ar[2].trim()+"\" ";
            str += "field_title=\""  +ReplaceXMLStr(ar[2].trim())+"\" ";
            str += "field_type=\""   +ar[3].trim()+"\" ";
            str += "ismuti=\""       +((ar[4]==null)?"0":ar[4].trim())+"\" ";
            str += "defaultvalue=\"" +((ar[5]==null)?"":ReplaceXMLStr(ar[5].trim()))+"\" ";
            str += "refer_col=\""    +((ar[6]==null)?"":ReplaceXMLStr(ar[6].trim()))+"\" ";
            str += "remark=\""       +((ar[7]==null)?"":ReplaceXMLStr(ar[7].trim()))+"\"/>";
        }
        str = str + "</ROWDATA></DATAPACKET>";
        return str;
    }

    //获取参考列的XML
    public String GetRefColXML(String table_id, String db)
    {
        String detail_R_xml="", tmpSql, str, strData;
        int i,j,c;
        String sql = "select refer_col from " + FIELD_TABLE + " where table_id=" +
                    table_id+" order by field_id";
        try
        {
            List val = dbconn.queryNotBind(sql, DBStr);
            if((val==null)||(val.size()<=0))
                return "";

            for(c=0; c<val.size(); c++)
            {
                String ar[] = (String[]) val.get(c);
                if((ar[0]==null)||(ar[0].trim().equals("")))
                    continue;
                List data = dbconn.queryNotBind(ar[0], db);

                if((data==null)||(data.size()<=0))
                {
                    detail_R_xml += " " + "@@@";
                    continue;
                }
                int colNum = ((String[]) data.get(0)).length;
                int maxLen[] = new int[colNum];

                strData = "</METADATA><ROWDATA>";
                for(i=0; i<data.size(); i++)
                {
                    String[] data_ar = (String[]) data.get(i);
                    strData += "<ROW ";
                    for(j=0; j<data_ar.length; j++)
                    {
                        String s0 = (data_ar[j]==null)?"":data_ar[j].trim();
                        strData += "C"+ j + "=\"" + ReplaceXMLStr(s0) + "\" ";
                        if(maxLen[j] < s0.getBytes().length)   maxLen[j] = s0.getBytes().length;
                    }
                    strData += "/>";
                }
                strData += "</ROWDATA></DATAPACKET>";

                //数据格式
                str="<?xml version=\"1.0\" standalone=\"yes\"?><DATAPACKET Version=\"2.0\"><METADATA><FIELDS>";
                for(i=0; i<colNum; i++)
                {
                    str += "<FIELD attrname=\"C"+i+"\" fieldtype=\"string\" WIDTH=\""+(maxLen[i]+5)+"\"/>";
                }
                str += "</FIELDS><PARAMS LCID=\"1033\"/>";

                detail_R_xml += str + strData + "@@@";
            }
            return detail_R_xml;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            return "";
        }
    }

    //替换XML特殊字符
    public static String ReplaceXMLStr(String str)
    {
        String tmpstr = "";
        for(int i=0;i<str.length();i++)
        {
            char s=str.charAt(i);
            switch(s)
            {
                case '\'': tmpstr+="&amp;apos;";  break;
                case '>':  tmpstr+="&amp;gt;";    break;
                case '<':  tmpstr+="&amp;lt;";    break;
                case '&':  tmpstr+="&amp;";       break;
                case '"':  tmpstr+="&amp;quot;";  break;
                case '\\': tmpstr+="&amp;#092;";  break;
                case 13:   tmpstr+="&amp;#013;";  break;
                case 10:   tmpstr+="&amp;#010;";  break;
                default:   tmpstr+=s;
            }
        }
        return tmpstr;
    }

}
