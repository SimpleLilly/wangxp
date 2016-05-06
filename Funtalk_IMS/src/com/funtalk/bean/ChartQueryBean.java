package com.funtalk.bean;

import java.util.*;

import com.funtalk.bean.QryWhereBean;
import com.funtalk.common.DataConnection;

/** ����ͼ��ѯͨ����.
 *
 * @author  wangjs
 * @version 1, 2
 */
public class ChartQueryBean
{
    /** ��Ҫ���ӵ����ݿ����� */
    public String DBStr = "";
    
    public String RepDB = null;
    /** ��ѯ���ñ�ı��� */
    public String QUERY_TABLE = "t_chart_info";
    /** ��ѯ�������ñ�ı��� */
    public String WHERE_TABLE = "t_chart_where";
    /** Ĭ�����ݿ����� */
    public DataConnection dbconn = null;

    /************** ���캯�� **********************************************/
    public ChartQueryBean()
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
     * @return   0 query_id, 1 name, 2 main_sql, 3 title, 4 chart_type, 5 ismulti, 6 xtype, 7 table_sql, 8 db_str
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, title, chart_size, chart_type, ismulti, xtype, table_sql, db_str "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL, DBStr);
        if ( (val==null) || (val.size() <= 0) )
        {
            System.out.println("��ȡ��ѯ��Ϣʱ����[query_id="+ query_id + "]");
            return null;
        }
        this.RepDB = ((String[]) val.get(0))[9];  //ȷ�������ĸ����ݿ�
        return (String[]) val.get(0);
    }

    /**
     * ��ȡ��ѯ���
     * @param   query_id ��ѯ���
     * @param   param ��ѯ����
     * @return  ��ѯ���Vector, 0Ϊ����List, 1Ϊ��ѯ��ϢString[]
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
        String tablesql = r[8]+"";
        String repDB = r[9];  //ȷ�������ĸ����ݿ�

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
        List data = dbconn.queryNotBind(sql, repDB);
        if((data==null)||(data.size()<=0))
        {
            System.out.println("��ȡ��ѯ�������[sql="+sql+"]");
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

        //��ѯ��Ϣ
        r[3] = QryWhereBean.ReplaceByParam(r[3], param);
        val.setElementAt( r, 1 );

        System.out.println("\n��ѯ���Ϊ:["+sql+"]\n");

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
            System.out.println("��ȡ��ѯ�ֶ�ʱ����");
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
                    tmpV[0] = str;  //����
                    tmpV[1] = "0";  //�Ƿ�SERIES
                    tmpV[2] = "0";  //�Ƿ�������
                    val.add(tmpV);
                }
                continue;
            }
            if(k2==1)
                str = str + c;
        }
        //���������ֶ�//��������SERIES
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

	/************** main����,������ **********************************************/
    public static void main(String args[])
    {
    }
}

