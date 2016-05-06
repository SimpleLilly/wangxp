package com.funtalk.bean;

import java.util.*;

import javax.servlet.http.*;

import java.text.SimpleDateFormat;
 

import com.funtalk.bean.QryWhereBean;
import com.funtalk.bean.ToolsOfSystem;
import com.funtalk.common.DataConnection;
import com.funtalk.common.WriteLog;
import com.funtalk.pojo.rightmanage.User;

/** �굥��ѯͨ����.
 *
 * @author  wangjs
 * @version 1, 2
 */
public class DetailQueryBean
{
    /** ��Ҫ���ӵ����ݿ����� */
    public String DBStr = "";
    /** ��ѯ���ñ�ı��� */
    public String QUERY_TABLE = "t_query_info";
    /** ��ѯ�������ñ�ı��� */
    public String WHERE_TABLE = "t_query_where";
    /** ����Ҫ��ѯ�ֶα�ı��� */
    public String NOUSE_FIELD_TABLE = "t_nouse_field";
    /** Ĭ�����ݿ����� */
    public DataConnection dbconn = null;
    
    ///////WriteLog  log = new WriteLog();

    /************** ���캯�� **********************************************/
    public DetailQueryBean()
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
     * �����û�ѡ���Ĳ�ѯ�ֶ��޸�Selelct��
     * @param   sSQL �����ԭʼSQL���
     * @param   field �û�ѡ���Ĳ�ѯ�ֶ���Ϣ
     * @return  �޸ĺ��SQL���
     */
    private String GetSQLByField(String sSQL, List field)
    {
        int i, j, k, k1, k2, len;
        i = sSQL.indexOf("from");
        if(i<0)
            return null;

        String str = sSQL.substring(0, i);
        String fromStr = sSQL.substring(i);
        for(i=field.size()-1; i>=0; i--)
        {
            String v[] = (String [])field.get(i);
            if(v[1].equals("1"))
                continue;

            String tmpStr = "\"";
            if(!(v[2].equals("0")))
                tmpStr = tmpStr + v[2];
            tmpStr = tmpStr + v[0] + "\"";

            len = tmpStr.length();
            j = sSQL.indexOf(tmpStr);
            if(i==0)
            {
                tmpStr = str.substring(j+len);
                k = tmpStr.indexOf(",");
                str = "select "+ tmpStr.substring(k+1);
            }
            else
            {
                k1 = 0;
                k2 = 0;
                for(k=j-1; k>=0; k--)
                {
                    char c = str.charAt(k);
                    if((c==',')&&(k1==0)&&(k2==0))
                        break;
                    if(c=='\'')
                        k1 = (k1==0)?1:0;
                    else if((c=='(')||(c==')'))
                        k2 = (k2==0)?1:0;
                }
                str = str.substring(0,k) + str.substring(j+len);
            }
        }

        return str + " " + fromStr;
    }

    /**
     * ȡ����ϼ�
     * @param   sSQL �����ԭʼSQL���
     * @param   field �û�ѡ���Ĳ�ѯ�ֶ���Ϣ
     * @return  ��Ÿ���ϼƵ�List 0����,1ֵ
     */
    private List GetSumList(String sSQL, List field, String qryDbStr)
    {
        int i, j, k, k1, k2, len;
        boolean havaSumFlag = false;
        String tmpStr;
        Vector vName = new Vector();

        i = sSQL.indexOf("from");
        if(i<0)
            return null;

        String str = sSQL.substring(0, i);
        String fromStr = sSQL.substring(i);
        String selectStr = "select ";
        for(i=0; i<field.size(); i++)
        {
        	
            String v[] = (String [])field.get(i);
            if(v[1].equals("0"))
                continue;
            if(v[2].equals("0"))
                continue;
            else
            	havaSumFlag = true;
            vName.addElement(v[0]);

            tmpStr =  "\"" + v[2] + v[0] + "\"";
            len = tmpStr.length();
            j = str.indexOf(tmpStr);
            if(i==0)
            {
                tmpStr = str.substring(0, j)+")";
                selectStr = ToolsOfSystem.replace(tmpStr, "select", "select sum(");
            }
            else
            {
                k1 = 0;
                k2 = 0;
                for(k=j-1; k>=0; k--)
                {
                    char c = str.charAt(k);
                    if((c==',')&&(k1==0)&&(k2==0))
                        break;
                    if(c=='\'')
                        k1 = (k1==0)?1:0;
                    else if((c=='(')||(c==')'))
                        k2 = (k2==0)?1:0;
                }
                String sd = (selectStr.equals("select "))?"":",";
                tmpStr = sd + "sum(" + str.substring(k+1, j) + ")";
                selectStr = selectStr + tmpStr;
            }
        }
        if(!havaSumFlag)
        	return null;
        String sql = selectStr + " " + fromStr;
        List tmpV = dbconn.queryNotBind(sql, qryDbStr);
        if((tmpV==null)||(tmpV.size()<=0))
            return null;
        else
        {
            List sumV = new ArrayList();
            String v[] = (String [])tmpV.get(0);
            for(i=0; i<v.length; i++)
            {
                if((v[i]==null)||(v[i].equals("")))
                    continue;
                String vv[] = new String[2];
                vv[0] = vName.elementAt(i).toString();
                vv[1] = v[i];
                sumV.add(vv);
            }
            return sumV;
        }
    }

    /************** ���к��� **********************************************/

    /**
     * �����û�ѡ���޳�����Ҫ���ֶ�
     * @param field ���пɲ�ѯ���ֶ���Ϣ
     * @param query_id ����ѯ��Ӧ�ı��
     * @param fieldStr �û�ָ���Ĳ���Ҫ��ѯ���ֶ��б�
     * @return ��Ҫ��ѯ���ֶ���Ϣ
     */
    public List RemoveField(List field, String fieldStr)
    {
        if((fieldStr!=null)&&(fieldStr.length()>0))
        {
            String ff[] = fieldStr.split(",");
            for(int i=0; i<ff.length; i++)
            {
                int fi = Integer.parseInt(ff[i]);
                ((String [])field.get(fi))[1] = "0";
            }
        }
        return field;
    }

    /**
     * ���û�û��ѡ�����ֶα��浽���ݿ��(NOUSE_FIELD_TABLE������Ӧ�ı���)
     * @param query_id ����ѯ��Ӧ�ı��
     * @param field ���пɲ�ѯ���ֶ���Ϣ
     * @return ��
     */
    public void SaveFieldDB(String query_id, String userName, List field)
    {
        int i, j=0;
        String sSQL="";
        try
        {
            for(i=0; i<field.size(); i++)
            {
                if(((String [])field.get(i))[1].equals("0"))
                    j++;
            }
            String sqlAr[][] = new String[j+1][2];

            sqlAr[0][0] = "delete " + NOUSE_FIELD_TABLE + " where query_id=" +
                          query_id + " and userid='"+userName+"'";
            sqlAr[0][1] = "3";
            j=1;
            for(i=0; i<field.size(); i++)
            {
                String v[] = (String [])field.get(i);
                if(v[1].equals("0"))
                {
                    sqlAr[j][0] = "insert into " + NOUSE_FIELD_TABLE + " values('" +
                       userName + "'," + query_id + ",'" + v[0] + "')";
                    sqlAr[j][1] = "1";
                    j++;
                }
            }
            dbconn.userJdbcTrsaction(sqlAr, DBStr);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }

    /**
     * ��ȡquery_id��Ӧ����ϸ��Ϣ
     * @param    query_id ��ѯ���
     * @return   0 query_id, 1 name, 2 main_sql, 3 title, 4 is_cause, 5 is_log, 6 page_way, 7 table_sql, 8 db_str
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, title, is_cause, is_log, page_way, table_sql, db_str "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL, DBStr);
        if((val==null)||(val.size()<=0))
        {
            System.out.println("��ȡ��ѯ��Ϣʱ����[query_id="+ query_id + "]");
            return null;
        }
        return (String [])val.get(0);
    }

    /**
     * ��ȡSQL����еĲ�ѯ������Ϣ
     * @param   query_id ��ѯ���
     * @param   is_cause �Ƿ���ʾ��ѯԭ��
     * @return  ��ѯ�����б�, 0Ϊ������, 1Ϊ��������, 2ΪĬ��ֵ, 3Ϊ������ֵ��|���ָ�,4У�鷽��,5�û�����ֵ,6��ǩֵ
     */
    public List GetParamInfo(String query_id, boolean is_cause)
    {
        List val = QryWhereBean.GetParamInfo(query_id, WHERE_TABLE, dbconn, DBStr,"","","");

        //��ѯԭ��
        if(is_cause)
        {
            String value[] = new String[7];
            value[0] = "��ѯԭ��";
            value[1] = "T";
            value[2] = "";
            value[3] = "";
            value[4] = "trim(T_"+val.size()+".value).length<=0@@�������ѯԭ��";
            value[5] = "";
            value[6] = "";
            val.add(value);
        }
        return val;
    }

    /**
     * ��ȡSQL����select�ֶ�
     * @param   sSQL ���β�ѯʹ�õ�SQL���
     * @param   query_id ��ѯ���
     * @return  0Ϊ�ֶ���, 1Ϊ���ֶ��Ƿ���ʾ(1��ʾ��0����ʾ), 2Ϊ�Ƿ��Ǻϼ���(0����, SUM_��)
     */
    public List GetField(String sSQL, String query_id, String userName)
    {
        int i,j,len,k1=0,k2=0;
        char c;
        String str="";
        
        len = sSQL.indexOf("from");
        if(len<0)
        {
            System.out.println("��ȡ��ѯ�ֶ�ʱ����[query_id="+ query_id + "]");
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
                    tmpV[1] = "1";  //�Ƿ���ʾ
                    tmpV[2] = "0";  //�Ƿ�ϼ���
                    val.add(tmpV);
                }
                continue;
            }
            if(k2==1)
                str = str + c;
        }

        //��������ֶ�
        for(i=0; i<val.size(); i++)
        {
            String tmpV[] = (String [])val.get(i);
            String tmpStr = tmpV[0];
            if((tmpStr.length()>4)&&(tmpStr.substring(0,4).toUpperCase().equals("SUM_")))
            {
                tmpV[0] = tmpStr.substring(4);
                tmpV[2] = tmpStr.substring(0,4);
            }
        }

        //��ȡ������ʾ���ֶ�
        List nouseField = null;
        if(query_id!=null)
            nouseField = dbconn.queryNotBind("select field_name from "+NOUSE_FIELD_TABLE+
                " where query_id="+query_id+" and userid='"+userName+"'", DBStr);
        if((nouseField==null)||(nouseField.size()<=0))
        {
            for(i=0; i<val.size(); i++)
                ((String[])val.get(i))[1] = "1";
        }
        else
        {
            for(i=0; i<val.size(); i++)
            {
                String tempV[] = (String [])val.get(i);
                for(j=0; j<nouseField.size(); j++)
                {
                    String[] ar = (String[]) nouseField.get(j);
                    if(tempV[0].equals(ar[0]))
                    {
                        tempV[1] = "0";
                        break;
                    }
                }
            }
        }

        if((val==null)||(val.size()<=0))
        {
            System.out.println("��ȡ��ѯ�ֶ�ʱ����[query_id="+ query_id + "]");
            return null;
        }
        return val;
    }

    /**
     * ��ȡ��ѯ���
     * @param   query_id ��ѯ���
     * @param   fieldStr ����Ҫ��ʾ���ֶ�����б�,��0,3,4
     * @param   param ��ѯ����
     * @param   beginPage ������Ҫ��ѯ�ڼ�ҳ, �ӵ�1ҳ��ʼ
     * @param   rowNum ÿҳ������¼��
     * @param   session_info ������Ϣ
     * @param   first ���β�ѯ�Ƿ�Ϊ�״β�ѯ
     * @return  ��ѯ���Vector, 0Ϊ����List, 1Ϊ��ѯ����List(0����,1ֵ), 2Ϊ����, 3��ӡֽ����, 4�ܼ�¼��, 5�ϼ���List(0����,1ֵ),6�ֶ�����List(0����,1����,2����)
     */
    public Vector GetQueryData(String query_id, String fieldStr, List param,
                     int beginPage, int rowNum, Vector session_info, boolean first)
    {
        int i;
        String sql, tmpStr,table;
        String userName = session_info.elementAt(3).toString();

        //��ʼ�����ؽ��
        Vector val = new Vector();
        for(i=0; i<7; i++)
            val.addElement("");

        //��ȡ��ѯ��Ϣ
        String r[] = GetQueryInfo(query_id);
        if((r==null)||(r.length<=0))
            return null;
        String mainsql = r[2];
        String tablesql = r[7];
        String title = r[3];
        String orient = r[6];
        String qryDBStr = r[8];  //ȷ�������ĸ����ݿ�

        //�滻��ѯ����
        sql = QryWhereBean.ReplaceByParam(mainsql, param);


        //��ȡ�����ֶ���Ϣ
        List F = GetField(mainsql, null, userName);
        if((F==null)||(F.size()<=0))
            return null;

        //�����û�ѡ���޳�����Ҫ���ֶ�,�����浽���ݿ���
        F = RemoveField(F, fieldStr);
        if(first)
            SaveFieldDB(query_id, userName, F);

        //�޳�sql����в���Ҫ���ֶ�
        sql = GetSQLByField(sql, F);

        //�滻��̬��
        String strFlag = "$[TABLE_NAME]";
        if(sql.indexOf(strFlag)>=0)
        {
            table = QryWhereBean.ReplaceByParam(tablesql, param);
            sql = QryWhereBean.RepDynamicTable(sql, strFlag, table, dbconn, qryDBStr);
            if(sql.indexOf(strFlag)>=0)
            {
                System.out.println("�滻��̬�����table_sql=["+table+"]");
                return null;
            }
        }

        //��ȡ��ѯ����
        Vector data = dbconn.queryBindPage(sql, (beginPage-1)*rowNum+1, beginPage*rowNum, qryDBStr);
        if((data==null)||(data.size()<=0))
        {
            System.out.println("��ȡ��ѯ�������[sql="+sql+"]");
            return null;
        }
        List fieldTitle = dbconn.GetFieldInfo(sql, qryDBStr);
        val.setElementAt( (List)data.elementAt(0), 0 );

        /*����ǵ�һҳ*/
        if(first)
        {
            System.out.println("\n��ѯ���Ϊ:["+sql+"]\n");

            /*��ѯ�����ַ���*/
            val.setElementAt( QryWhereBean.GetParamList(param), 1 );
            /*����*/
            val.setElementAt( title, 2 );
            /*��ӡֽ����*/
            val.setElementAt( orient, 3 );
            /*�ܼ�¼��*/
            val.setElementAt( data.elementAt(1).toString(), 4 );
            /*�ϼ���*/
            val.setElementAt( GetSumList(sql, F, qryDBStr), 5 );
            /*�ֶ�����*/
            val.setElementAt( fieldTitle, 6 );
        }
        return val;
    }

    public void ExecDetailQuery( HttpServletRequest request )
    {
        HttpSession session = request.getSession(false);
        //String userName = (String)session.getAttribute("workid"); //�û�
        User currentUser=(User)session.getAttribute("currentUser");
        String userName = currentUser.getUsername();
        String queryid   = request.getParameter("queryid");       //��ѯID
        String page      = request.getParameter("page");          //ҳ��
        String isfirst   = request.getParameter("isfirst");       //�Ƿ��״β�ѯ
        String tmpStr    = request.getParameter("CountPerpage");  //ÿ�β�ѯ�ļ�¼����

        //ÿ�β�ѯ�ļ�¼����
        int CountPerpage;
        try
        {
            CountPerpage = Integer.parseInt(tmpStr);
        }
        catch(Exception e)
        {
            CountPerpage = 100;
        }
        if(CountPerpage<=0)    CountPerpage = 100;

        List   param = null;                //��ѯ����
        Vector MyQueryCondition = null;     //��ѯ���
        String unSelectCols ="";            //��ѯ�ֶ�
        Vector data = null;                 //��ѯ����
        Vector session_info = null;         //������Ϣ
        int i;

        //��ʼ��ѯ����
        if((isfirst!=null)&&(isfirst.equals("1")))  //�״β�ѯ
        {
            //��ȡSession�����Ϣ
            session_info = new Vector();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            session_info.add((new Integer(session.hashCode())).toString());
            session_info.add(simpledateformat.format(date));
            session_info.add(request.getRealPath("/"));
            session_info.add(userName+"");

            //��ѯ����
            String v[] = GetQueryInfo(queryid);
            param = GetParamInfo(queryid, v[4].equals("1"));
            for(i=0;i<param.size();i++)
            {
                String temp[] = (String [])param.get(i);
                String value = request.getParameter(temp[1]+"_"+i);
                if(temp[1].equals("F"))
                    value += request.getParameter(temp[1]+"_"+i+"_1");
                temp[5] = value;
            }
            //�ļ��굥��ѯ������һ�λ�鼸��ҵ�񣬺��롢��ʼ����ʱ�䶼һ��������session����ÿ����
            if(request.getParameter("queryType").equals("1")){
            	session.setAttribute("searchParam", param);
            }

            //δѡ�е��ֶ�
            unSelectCols=request.getParameter("unSelectCols");
            //��ѯ����
            data = GetQueryData(queryid,unSelectCols,param,Integer.parseInt(page),CountPerpage,session_info,true);

            MyQueryCondition = new Vector();
            if(data!=null)
            {
            	String str = "";
            	List ls = (List) data.elementAt(1);
            	for(int k = 0; k < ls.size(); k++)
            	{
            		String[] arr = (String[])ls.get(k);
            		str += arr[0]+":"+arr[1]+"  ";
            	}
                //��д��ѯ��־
                if(v[5].equals("1"))
                {
//                    String strSql = "insert into t_log values(t_log_seq.nextval,sysdate,'" +userName + "','���û�ִ����" + v[1] +":  " + str + "')";
//					log.WriteLogDb(strSql);
//					
					
				    /////////////////////////write log begin
				    try {
						WriteLog.dbLog("Q", "�굥��ѯ", "1", "���û�ִ����"+v[1]+": "+str);//��¼��־
					} catch (Exception e) {
						System.out.println("in ExecDetailQuery, error: "+e);
					}
					/////////////////////////write log end
					
					
                }

                //�����ѯ���
                MyQueryCondition.add(param);
                MyQueryCondition.add(unSelectCols);
                MyQueryCondition.add(session_info);
                for(i=1; i<data.size(); i++)
                    MyQueryCondition.add(data.elementAt(i));
            }
            session.setAttribute("MyQueryCondition",MyQueryCondition);
        }
        else   //���״β�ѯ
        {
             MyQueryCondition = (Vector)session.getAttribute("MyQueryCondition");
             param = (List)MyQueryCondition.get(0);
             unSelectCols=(String)MyQueryCondition.get(1);
             session_info = (Vector)MyQueryCondition.get(2);  //session��Ϣ
             data = GetQueryData(queryid,unSelectCols,param,Integer.parseInt(page),CountPerpage,session_info,false);

             if(data!=null)
             {
                 for(i=1; i<data.size(); i++)
                     data.set(i,MyQueryCondition.get(i+2));
             }
        }

        request.setAttribute("username",userName);
        request.setAttribute("CountPerpage",Integer.toString(CountPerpage));
        request.setAttribute("data",data);
    }

    /************** main����,������ **********************************************/
    public static void main(String args[])
    {
    }
}