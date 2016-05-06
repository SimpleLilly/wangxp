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

    /** ��ѯ���ñ�ı��� */
    public String QUERY_TABLE = "t_xlsrep_info";
    /** ��ѯ�������ñ�ı��� */
    public String WHERE_TABLE = "t_xlsrep_where";
    /** Ĭ�����ݿ����� */
    public DataConnection dbconn = null;

    /************** ���캯�� **********************************************/
    public RepQueryBean()
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
     * @return   0 query_id, 1 name, 2 main_sql, 3 model_file, 4 issheet, 5 is_guidang, 6 issheet, 7 table_sql, 8 db_str
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, model_file, issheet, is_guidang, issheet, table_sql, db_str "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL, DBStr);
        if ( (val==null) || (val.size() <= 0) )
        {
            System.out.println("��ȡ��ѯ��Ϣʱ����[query_id="+ query_id + "]");
            return null;
        }
        this.RepDB = ((String[]) val.get(0))[8];  //ȷ�������ĸ����ݿ�

        return (String[]) val.get(0);
    }

    /**
     * ��ȡ��ѯ���
     * @param   query_id ��ѯ���
     * @param   param ��ѯ����
     * @return  ��ѯ���Vector, 0Ϊ����List, 1Ϊ��ѯ����List��2Ϊ��ѯ��ϢString[]
     */
    public Vector GetQueryData(String query_id, List param)
    {
        int i;
        String sql, tmpStr;

        //��ʼ�����ؽ��
        Vector val = new Vector();
        for(i=0; i<3; i++)
            val.addElement("");

        //��ȡ��ѯ��Ϣ
        String r[] = GetQueryInfo(query_id);
        if((r==null)||(r.length<=0))
            return null;

        String mainsql = r[2];
        String tablesql = r[7]+"";

        //�滻��ѯ����
        sql = QryWhereBean.ReplaceByParam(mainsql, param);
        String table = QryWhereBean.ReplaceByParam(tablesql, param);

        //�滻��̬��
        String strFlag = "$[TABLE_NAME]";
        if(sql.indexOf(strFlag)>=0)
        {
            sql = QryWhereBean.RepDynamicTable(sql, strFlag, table, dbconn, RepDB);
            if(sql.indexOf(strFlag)>=0)
            {
                System.out.println("�滻��̬�����table_sql=["+table+"]");
                return null;
            }
        }

        //��ȡ��ѯ����
        if(sql.trim().toLowerCase().indexOf("select")!=0)
        {
            System.out.println("�Ƿ�SQL[sql="+sql+"]");
            return null;
        }
        List data = dbconn.queryNotBind(sql, RepDB);
        if((data==null)||(data.size()<=0))
        {
            System.out.println("��ȡ��ѯ�������[sql="+sql+"]");
            return null;
        }
        val.setElementAt( data, 0 );

        /*��ѯ����*/
        val.setElementAt( QryWhereBean.GetParamList(param), 1 );

        //��ѯ��Ϣ
        r[3] = QryWhereBean.ReplaceByParam(r[3], param);
        val.setElementAt( r, 2 );

        System.out.println("\n��ѯ���Ϊ:["+sql+"]\n");
        
        return val;
    }

    /*******************  ������ʽ��  **************************************/

    /**
     * ������ʱ��������
     * @param report_name ��������
     * @param month �·�
     * @param source_seq Դ�������к�
     * @param report_type ��������
     * @param owner ����Ա
     * @param memo ����ԭ��
     * @param dataStr ��������
     ** @return ������ 0�ɹ� -1 ʧ��
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
            //��ȡ���ʱ������к�
            sSQL = "select modify_table_data_seq.nextval report_seq from dual";
            al = dbconn.queryNotBind(sSQL, RepDB);
            report_seq = "1111";//(String)hash.get("REPORT_SEQ");

            //����һ�����ʱ�����Ϣ
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
            return -1;  //��������
        }
    }

    //�鿴�������Ƿ�鵵


    /************** main����,������ **********************************************/
    public static void main(String args[])
    {
    }
}

