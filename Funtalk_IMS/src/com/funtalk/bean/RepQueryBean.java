package com.funtalk.bean;

import java.util.*;

import com.funtalk.bean.QryWhereBean;
import com.funtalk.common.DataConnection;

/**
 *
 * @author  wangjs
 * @version 1, 2
 */
public class RepQueryBean
{
    public String DBStr = null;
    public String RepDB = null;

    /** 查询配置表的表名 */
    public String QUERY_TABLE = "t_xlsrep_info";
    /** 查询条件配置表的表名 */
    public String WHERE_TABLE = "t_xlsrep_where";
    /** 默认数据库连接 */
    public DataConnection dbconn = null;

    /************** 构造函数 **********************************************/
    public RepQueryBean()
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
     * @return   0 query_id, 1 name, 2 main_sql, 3 model_file, 4 issheet, 5 is_guidang, 6 issheet, 7 table_sql, 8 db_str
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, model_file, issheet, is_guidang, issheet, table_sql, db_str "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL, DBStr);
        if ( (val==null) || (val.size() <= 0) )
        {
            System.out.println("获取查询信息时出错[query_id="+ query_id + "]");
            return null;
        }
        this.RepDB = ((String[]) val.get(0))[8];  //确定该连哪个数据库

        return (String[]) val.get(0);
    }

    /**
     * 获取查询结果
     * @param   query_id 查询编号
     * @param   param 查询条件
     * @return  查询结果Vector, 0为数据List, 1为查询参数List，2为查询信息String[]
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
        String tablesql = r[7]+"";

        //替换查询条件
        sql = QryWhereBean.ReplaceByParam(mainsql, param);
        String table = QryWhereBean.ReplaceByParam(tablesql, param);

        //替换动态表
        String strFlag = "$[TABLE_NAME]";
        if(sql.indexOf(strFlag)>=0)
        {
            sql = QryWhereBean.RepDynamicTable(sql, strFlag, table, dbconn, RepDB);
            if(sql.indexOf(strFlag)>=0)
            {
                System.out.println("替换动态表出错table_sql=["+table+"]");
                return null;
            }
        }

        //获取查询数据
        if(sql.trim().toLowerCase().indexOf("select")!=0)
        {
            System.out.println("非法SQL[sql="+sql+"]");
            return null;
        }
        List data = dbconn.queryNotBind(sql, RepDB);
        if((data==null)||(data.size()<=0))
        {
            System.out.println("获取查询结果出错[sql="+sql+"]");
            return null;
        }
        val.setElementAt( data, 0 );

        /*查询参数*/
        val.setElementAt( QryWhereBean.GetParamList(param), 1 );

        //查询信息
        r[3] = QryWhereBean.ReplaceByParam(r[3], param);
        val.setElementAt( r, 2 );

        System.out.println("\n查询语句为:["+sql+"]\n");
        
        return val;
    }

    /*******************  保存调帐结果  **************************************/

    /**
     * 保存调帐报表的数据
     * @param report_name 报表名称
     * @param month 月份
     * @param source_seq 源报表序列号
     * @param report_type 报表类型
     * @param owner 操作员
     * @param memo 调帐原因
     * @param dataStr 报表数据
     ** @return 处理结果 0成功 -1 失败
     */
    public int SaveModifyReport(String report_name, String month, String source_seq,
          String report_type, String owner, String memo, String dataStr)
    {
        String sSQL, report_seq;
        int i, j ,k;
        List al;
        HashMap hash;

        try
        {
            //获取调帐报表序列号
            sSQL = "select modify_table_data_seq.nextval report_seq from dual";
            al = dbconn.queryNotBind(sSQL, RepDB);
            report_seq = "1111";//(String)hash.get("REPORT_SEQ");

            //插入一条调帐报表信息
            sSQL = "insert into modify_report_info values('"+report_name+
                   "','"+month+"',"+report_seq+","+source_seq+",'"+
                   report_type+"','"+owner+"',sysdate,'','')";
            dbconn.insertNotBind(sSQL, RepDB);

            //String sheetArray[] = dataStr.split("#");
            String sheetArray[] = ToolsOfSystem.mySplit(dataStr,"#");
            for(i=0; i<sheetArray.length; i++)
            {
                //String dataArray[] = sheetArray[i].split("@");
                //String groupArray[] = dataArray[1].split(":");
                String dataArray[] = ToolsOfSystem.mySplit(sheetArray[i],"@");
                String groupArray[] = ToolsOfSystem.mySplit(dataArray[1],":");
                for(j=0; j<groupArray.length; j++)
                {
                    sSQL = "insert into modify_table_data_table values('"+
                       report_name+"','"+month+"',"+report_seq+",'"+dataArray[0]+
                       "',"+j+",'"+groupArray[j]+"')";
                    dbconn.insertNotBind(sSQL, RepDB);
                }
            }
            return 0;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            return -1;  //操作出错
        }
    }

    //查看本报表是否归档


    /************** main函数,测试用 **********************************************/
    public static void main(String args[])
    {
    }
}

