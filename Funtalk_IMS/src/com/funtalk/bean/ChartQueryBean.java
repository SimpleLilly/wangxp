package com.funtalk.bean;

import java.util.*;

import com.funtalk.bean.QryWhereBean;
import com.funtalk.common.DataConnection;

/** 趋势图查询通用类.
 *
 * @author  wangjs
 * @version 1, 2
 */
public class ChartQueryBean
{
    /** 需要连接的数据库名称 */
    public String DBStr = "";
    
    public String RepDB = null;
    /** 查询配置表的表名 */
    public String QUERY_TABLE = "t_chart_info";
    /** 查询条件配置表的表名 */
    public String WHERE_TABLE = "t_chart_where";
    /** 默认数据库连接 */
    public DataConnection dbconn = null;

    /************** 构造函数 **********************************************/
    public ChartQueryBean()
    {
        try
        {
            dbconn = new DataConnection();
        }
        catch(Exception e)
        {
            System.out.println("初始化失败: " + e.toString());
        }
    }

    /**
     * 获取query_id对应的详细信息
     * @param    query_id 查询编号
     * @return   0 query_id, 1 name, 2 main_sql, 3 title, 4 chart_type, 5 ismulti, 6 xtype, 7 table_sql, 8 db_str
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, title, chart_size, chart_type, ismulti, xtype, table_sql, db_str "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL, DBStr);
        if ( (val==null) || (val.size() <= 0) )
        {
            System.out.println("获取查询信息时出错[query_id="+ query_id + "]");
            return null;
        }
        this.RepDB = ((String[]) val.get(0))[9];  //确定该连哪个数据库
        return (String[]) val.get(0);
    }

    /**
     * 获取查询结果
     * @param   query_id 查询编号
     * @param   param 查询条件
     * @return  查询结果Vector, 0为数据List, 1为查询信息String[]
     */
    public Vector GetQueryData(String query_id, List param)
    {
        int i;  
        String sql, tmpStr; 
 
        //初始化返回结果
        Vector val = new Vector();
        for(i=0; i<3; i++)
            val.addElement("");

        //获取查询信息
        String r[] = GetQueryInfo(query_id);

        if((r==null)||(r.length<=0))
            return null;
 
        String mainsql = r[2];
        String tablesql = r[8]+"";
        String repDB = r[9];  //确定该连哪个数据库

        //替换查询条件
        sql = QryWhereBean.ReplaceByParam(mainsql, param);
        String table = QryWhereBean.ReplaceByParam(tablesql, param);

        //替换动态表
        String strFlag = "$[TABLE_NAME]";
        if(sql.indexOf(strFlag)>=0)
        {
            sql = QryWhereBean.RepDynamicTable(sql, strFlag, table, dbconn, repDB);
            if(sql.indexOf(strFlag)>=0)
            {
                System.out.println("替换动态表出错table_sql=["+table+"]");
                return null;
            }
        }

        //获取查询数据
        List data = dbconn.queryNotBind(sql, repDB);
        if((data==null)||(data.size()<=0))
        {
            System.out.println("获取查询结果出错[sql="+sql+"]");
            return null;
        }
        List colInfo = getColInfo(sql);
        String[] chartInfo = getChartInfo(colInfo);
        val.setElementAt(chartInfo, 2);
        //List dataTitle = dbconn.GetFieldInfo(sql, repDB);
        String title[] = new String[((String[])data.get(0)).length];
        for(i=0; i<title.length; i++)
            title[i] = ((String [])colInfo.get(i))[0];
        data.add(0, title);
        val.setElementAt( data, 0 );

        //查询信息
        r[3] = QryWhereBean.ReplaceByParam(r[3], param);
        val.setElementAt( r, 1 );

        System.out.println("\n查询语句为:["+sql+"]\n");

        return val;
    }

    private String[] getChartInfo(List colInfo) {
    	String[] arr = new String[2];
    	arr[0] = "";
    	arr[1] = "";
		for(int i = 0; i < colInfo.size(); i++)
		{
            String tmpV[] = (String [])colInfo.get(i);

            if(tmpV[1].equalsIgnoreCase("SERIES_"))
            {
            	arr[0] += i;
            	continue;
            }
            if(tmpV[2].equalsIgnoreCase("NUM_"))
            {
            	arr[1] += i + ",";
            }
		}
		return arr;
	}

	private List getColInfo(String sSQL) {
        int i,len,k1=0,k2=0;
        char c;
        String str="";

        len = sSQL.indexOf("from");
        if(len<0)
        {
            System.out.println("获取查询字段时出错");
            return null;
        }

        List val = new ArrayList();
        for(i=0; i<len; i++)
        {
            c = sSQL.charAt(i);
            if(c=='\'')
                k1 = (k1==0)?1:0;
            if(k1==1)
                continue;
            else if(c=='\"')
            {
                k2 = (k2==0)?1:0;
                if(k2==1)
                    str = "";
                else
                {
                    String tmpV[] = new String[3];
                    tmpV[0] = str;  //名称
                    tmpV[1] = "0";  //是否SERIES
                    tmpV[2] = "0";  //是否数据项
                    val.add(tmpV);
                }
                continue;
            }
            if(k2==1)
                str = str + c;
        }
        //分析数据字段//分析有无SERIES
        for(i=0; i<val.size(); i++)
        {
            String tmpV[] = (String [])val.get(i);
            String tmpStr = tmpV[0];
            if((tmpStr.length()>4)&&(tmpStr.substring(0,4).toUpperCase().equals("NUM_")))
            {
                tmpV[0] = tmpStr.substring(4);
                tmpV[2] = tmpStr.substring(0,4);
            }
            if((tmpStr.length()>7)&&(tmpStr.substring(0,7).toUpperCase().equals("SERIES_")))
            {
                tmpV[0] = tmpStr.substring(7);
                tmpV[1] = tmpStr.substring(0,7);
            }
        }

		return val;
	}

	/************** main函数,测试用 **********************************************/
    public static void main(String args[])
    {
    }
}

