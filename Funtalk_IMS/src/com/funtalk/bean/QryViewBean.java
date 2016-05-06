
package com.funtalk.bean;

import java.util.*;

import com.funtalk.bean.QryWhereBean;
import com.funtalk.common.DataConnection;

/** 普通查询通用类.
 *
 * @author  wangjs
 * @version 1, 2
 */
public class QryViewBean
{
    /** 需要连接的数据库名称 */
    public String DBStr = "";
    public String RepDB = "";
    /** 查询配置表的表名 */
    public String QUERY_TABLE = "t_qryview_info";
    /** 查询条件配置表的表名 */
    public String WHERE_TABLE = "t_qryview_where";
    /** 默认数据库连接 */
    public DataConnection dbconn = null;

    /************** 构造函数 **********************************************/
    public QryViewBean()
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
     * @return   0 query_id, 1 name, 2 main_sql, 3 title, 4 pagecount, 5 '', 6 '', 7 table_sql, 8 db_str
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, title, pagecount, '', '', table_sql, db_str "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL, DBStr);
        if ( (val==null) || (val.size() <= 0) )
        {
            System.out.println("获取查询信息时出错[query_id="+ query_id + "]");
            return null;
        }
        return (String[]) val.get(0);
    }

    /**
     * 获取查询结果
     * @param   query_id 查询编号
     * @param   param 查询条件
     * @return  查询结果Vector, 0为数据List, 1为查询信息String[]
     */
    public Vector GetQueryData(String query_id, List param, int begin, int end)
    {
        int i;
        String sql, tmpStr;

        //获取查询信息
        String r[] = GetQueryInfo(query_id);
        if((r==null)||(r.length<=0))
            return null;

        String mainsql = r[2];
        String tablesql = r[7]+"";
        String repDB = r[8];  //确定该连哪个数据库

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
        Vector val = dbconn.queryBindPage(sql, begin, end, repDB);
        if((val==null)||(val.size()<=0))
        {
            System.out.println("获取查询结果出错[sql="+sql+"]");
            return null;
        }
        List dataTitle = dbconn.GetFieldInfo(sql, repDB);
        List data = (List)val.get(0);
        String title[] = new String[dataTitle.size()];
        for(i=0; i<title.length; i++)
            title[i] = ((String [])dataTitle.get(i))[0];
        data.add(0, title);

        //查询信息
        r[3] = QryWhereBean.ReplaceByParam(r[3], param);
        val.addElement(r);

        System.out.println("\n查询语句为:["+sql+"]\n");

        return val;
    }

    /************** main函数,测试用 **********************************************/
    public static void main(String args[])
    {
    }
}

