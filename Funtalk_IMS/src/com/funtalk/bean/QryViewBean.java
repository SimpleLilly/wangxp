
package com.funtalk.bean;

import java.util.*;

import com.funtalk.bean.QryWhereBean;
import com.funtalk.common.DataConnection;

/** ��ͨ��ѯͨ����.
 *
 * @author  wangjs
 * @version 1, 2
 */
public class QryViewBean
{
    /** ��Ҫ���ӵ����ݿ����� */
    public String DBStr = "";
    public String RepDB = "";
    /** ��ѯ���ñ�ı��� */
    public String QUERY_TABLE = "t_qryview_info";
    /** ��ѯ�������ñ�ı��� */
    public String WHERE_TABLE = "t_qryview_where";
    /** Ĭ�����ݿ����� */
    public DataConnection dbconn = null;

    /************** ���캯�� **********************************************/
    public QryViewBean()
    {
        try
        {
            dbconn = new DataConnection();
        }
        catch(Exception e)
        {
            System.out.println("��ʼ��ʧ��: " + e.toString());
        }
    }

    /**
     * ��ȡquery_id��Ӧ����ϸ��Ϣ
     * @param    query_id ��ѯ���
     * @return   0 query_id, 1 name, 2 main_sql, 3 title, 4 pagecount, 5 '', 6 '', 7 table_sql, 8 db_str
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, title, pagecount, '', '', table_sql, db_str "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL, DBStr);
        if ( (val==null) || (val.size() <= 0) )
        {
            System.out.println("��ȡ��ѯ��Ϣʱ����[query_id="+ query_id + "]");
            return null;
        }
        return (String[]) val.get(0);
    }

    /**
     * ��ȡ��ѯ���
     * @param   query_id ��ѯ���
     * @param   param ��ѯ����
     * @return  ��ѯ���Vector, 0Ϊ����List, 1Ϊ��ѯ��ϢString[]
     */
    public Vector GetQueryData(String query_id, List param, int begin, int end)
    {
        int i;
        String sql, tmpStr;

        //��ȡ��ѯ��Ϣ
        String r[] = GetQueryInfo(query_id);
        if((r==null)||(r.length<=0))
            return null;

        String mainsql = r[2];
        String tablesql = r[7]+"";
        String repDB = r[8];  //ȷ�������ĸ����ݿ�

        //�滻��ѯ����
        sql = QryWhereBean.ReplaceByParam(mainsql, param);
        String table = QryWhereBean.ReplaceByParam(tablesql, param);

        //�滻��̬��
        String strFlag = "$[TABLE_NAME]";
        if(sql.indexOf(strFlag)>=0)
        {
            sql = QryWhereBean.RepDynamicTable(sql, strFlag, table, dbconn, repDB);
            if(sql.indexOf(strFlag)>=0)
            {
                System.out.println("�滻��̬�����table_sql=["+table+"]");
                return null;
            }
        }

        //��ȡ��ѯ����
        Vector val = dbconn.queryBindPage(sql, begin, end, repDB);
        if((val==null)||(val.size()<=0))
        {
            System.out.println("��ȡ��ѯ�������[sql="+sql+"]");
            return null;
        }
        List dataTitle = dbconn.GetFieldInfo(sql, repDB);
        List data = (List)val.get(0);
        String title[] = new String[dataTitle.size()];
        for(i=0; i<title.length; i++)
            title[i] = ((String [])dataTitle.get(i))[0];
        data.add(0, title);

        //��ѯ��Ϣ
        r[3] = QryWhereBean.ReplaceByParam(r[3], param);
        val.addElement(r);

        System.out.println("\n��ѯ���Ϊ:["+sql+"]\n");

        return val;
    }

    /************** main����,������ **********************************************/
    public static void main(String args[])
    {
    }
}

