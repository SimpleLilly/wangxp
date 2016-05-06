package com.funtalk.bean;

import java.util.*;

import javax.servlet.http.*;

import java.text.SimpleDateFormat;
 

import com.funtalk.bean.QryWhereBean;
import com.funtalk.bean.ToolsOfSystem;
import com.funtalk.common.DataConnection;
import com.funtalk.common.WriteLog;
import com.funtalk.pojo.rightmanage.User;

/** 详单查询通用类.
 *
 * @author  wangjs
 * @version 1, 2
 */
public class DetailQueryBean
{
    /** 需要连接的数据库名称 */
    public String DBStr = "";
    /** 查询配置表的表名 */
    public String QUERY_TABLE = "t_query_info";
    /** 查询条件配置表的表名 */
    public String WHERE_TABLE = "t_query_where";
    /** 不需要查询字段表的表名 */
    public String NOUSE_FIELD_TABLE = "t_nouse_field";
    /** 默认数据库连接 */
    public DataConnection dbconn = null;
    
    ///////WriteLog  log = new WriteLog();

    /************** 构造函数 **********************************************/
    public DetailQueryBean()
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
     * 根据用户选定的查询字段修改Selelct项
     * @param   sSQL 输入的原始SQL语句
     * @param   field 用户选定的查询字段信息
     * @return  修改后的SQL语句
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
     * 取各项合计
     * @param   sSQL 输入的原始SQL语句
     * @param   field 用户选定的查询字段信息
     * @return  存放各项合计的List 0名称,1值
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

    /************** 公有函数 **********************************************/

    /**
     * 根据用户选择剔除不需要的字段
     * @param field 所有可查询的字段信息
     * @param query_id 本查询对应的编号
     * @param fieldStr 用户指定的不需要查询的字段列表
     * @return 需要查询的字段信息
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
     * 将用户没有选定的字段保存到数据库表(NOUSE_FIELD_TABLE变量对应的表名)
     * @param query_id 本查询对应的编号
     * @param field 所有可查询的字段信息
     * @return 无
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
     * 获取query_id对应的详细信息
     * @param    query_id 查询编号
     * @return   0 query_id, 1 name, 2 main_sql, 3 title, 4 is_cause, 5 is_log, 6 page_way, 7 table_sql, 8 db_str
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, title, is_cause, is_log, page_way, table_sql, db_str "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL, DBStr);
        if((val==null)||(val.size()<=0))
        {
            System.out.println("获取查询信息时出错[query_id="+ query_id + "]");
            return null;
        }
        return (String [])val.get(0);
    }

    /**
     * 获取SQL语句中的查询条件信息
     * @param   query_id 查询编号
     * @param   is_cause 是否显示查询原因
     * @return  查询条件列表, 0为参数名, 1为参数类型, 2为默认值, 3为下拉框值“|”分割,4校验方法,5用户输入值,6标签值
     */
    public List GetParamInfo(String query_id, boolean is_cause)
    {
        List val = QryWhereBean.GetParamInfo(query_id, WHERE_TABLE, dbconn, DBStr,"","","");

        //查询原因
        if(is_cause)
        {
            String value[] = new String[7];
            value[0] = "查询原因";
            value[1] = "T";
            value[2] = "";
            value[3] = "";
            value[4] = "trim(T_"+val.size()+".value).length<=0@@请输入查询原因";
            value[5] = "";
            value[6] = "";
            val.add(value);
        }
        return val;
    }

    /**
     * 获取SQL语句的select字段
     * @param   sSQL 本次查询使用的SQL语句
     * @param   query_id 查询编号
     * @return  0为字段名, 1为本字段是否显示(1显示，0不显示), 2为是否是合计项(0不是, SUM_是)
     */
    public List GetField(String sSQL, String query_id, String userName)
    {
        int i,j,len,k1=0,k2=0;
        char c;
        String str="";
        
        len = sSQL.indexOf("from");
        if(len<0)
        {
            System.out.println("获取查询字段时出错[query_id="+ query_id + "]");
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
                    tmpV[1] = "1";  //是否显示
                    tmpV[2] = "0";  //是否合计项
                    val.add(tmpV);
                }
                continue;
            }
            if(k2==1)
                str = str + c;
        }

        //分析求和字段
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

        //获取不需显示的字段
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
            System.out.println("获取查询字段时出错[query_id="+ query_id + "]");
            return null;
        }
        return val;
    }

    /**
     * 获取查询结果
     * @param   query_id 查询编号
     * @param   fieldStr 不需要显示的字段序号列表,如0,3,4
     * @param   param 查询条件
     * @param   beginPage 本次需要查询第几页, 从第1页开始
     * @param   rowNum 每页的最多记录数
     * @param   session_info 事务信息
     * @param   first 本次查询是否为首次查询
     * @return  查询结果Vector, 0为数据List, 1为查询条件List(0名称,1值), 2为标题, 3打印纸方向, 4总记录数, 5合计项List(0名称,1值),6字段属性List(0名称,1类型,2长度)
     */
    public Vector GetQueryData(String query_id, String fieldStr, List param,
                     int beginPage, int rowNum, Vector session_info, boolean first)
    {
        int i;
        String sql, tmpStr,table;
        String userName = session_info.elementAt(3).toString();

        //初始化返回结果
        Vector val = new Vector();
        for(i=0; i<7; i++)
            val.addElement("");

        //获取查询信息
        String r[] = GetQueryInfo(query_id);
        if((r==null)||(r.length<=0))
            return null;
        String mainsql = r[2];
        String tablesql = r[7];
        String title = r[3];
        String orient = r[6];
        String qryDBStr = r[8];  //确定该连哪个数据库

        //替换查询条件
        sql = QryWhereBean.ReplaceByParam(mainsql, param);


        //获取所有字段信息
        List F = GetField(mainsql, null, userName);
        if((F==null)||(F.size()<=0))
            return null;

        //根据用户选择剔除不需要的字段,并保存到数据库中
        F = RemoveField(F, fieldStr);
        if(first)
            SaveFieldDB(query_id, userName, F);

        //剔除sql语句中不需要的字段
        sql = GetSQLByField(sql, F);

        //替换动态表
        String strFlag = "$[TABLE_NAME]";
        if(sql.indexOf(strFlag)>=0)
        {
            table = QryWhereBean.ReplaceByParam(tablesql, param);
            sql = QryWhereBean.RepDynamicTable(sql, strFlag, table, dbconn, qryDBStr);
            if(sql.indexOf(strFlag)>=0)
            {
                System.out.println("替换动态表出错table_sql=["+table+"]");
                return null;
            }
        }

        //获取查询数据
        Vector data = dbconn.queryBindPage(sql, (beginPage-1)*rowNum+1, beginPage*rowNum, qryDBStr);
        if((data==null)||(data.size()<=0))
        {
            System.out.println("获取查询结果出错[sql="+sql+"]");
            return null;
        }
        List fieldTitle = dbconn.GetFieldInfo(sql, qryDBStr);
        val.setElementAt( (List)data.elementAt(0), 0 );

        /*如果是第一页*/
        if(first)
        {
            System.out.println("\n查询语句为:["+sql+"]\n");

            /*查询参数字符串*/
            val.setElementAt( QryWhereBean.GetParamList(param), 1 );
            /*标题*/
            val.setElementAt( title, 2 );
            /*打印纸方向*/
            val.setElementAt( orient, 3 );
            /*总记录数*/
            val.setElementAt( data.elementAt(1).toString(), 4 );
            /*合计项*/
            val.setElementAt( GetSumList(sql, F, qryDBStr), 5 );
            /*字段属性*/
            val.setElementAt( fieldTitle, 6 );
        }
        return val;
    }

    public void ExecDetailQuery( HttpServletRequest request )
    {
        HttpSession session = request.getSession(false);
        //String userName = (String)session.getAttribute("workid"); //用户
        User currentUser=(User)session.getAttribute("currentUser");
        String userName = currentUser.getUsername();
        String queryid   = request.getParameter("queryid");       //查询ID
        String page      = request.getParameter("page");          //页码
        String isfirst   = request.getParameter("isfirst");       //是否首次查询
        String tmpStr    = request.getParameter("CountPerpage");  //每次查询的记录条数

        //每次查询的记录条数
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

        List   param = null;                //查询参数
        Vector MyQueryCondition = null;     //查询结果
        String unSelectCols ="";            //查询字段
        Vector data = null;                 //查询数据
        Vector session_info = null;         //事务信息
        int i;

        //开始查询数据
        if((isfirst!=null)&&(isfirst.equals("1")))  //首次查询
        {
            //获取Session相关信息
            session_info = new Vector();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            session_info.add((new Integer(session.hashCode())).toString());
            session_info.add(simpledateformat.format(date));
            session_info.add(request.getRealPath("/"));
            session_info.add(userName+"");

            //查询条件
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
            //文件详单查询，可能一次会查几个业务，号码、开始结束时间都一样。放入session不用每次填
            if(request.getParameter("queryType").equals("1")){
            	session.setAttribute("searchParam", param);
            }

            //未选中的字段
            unSelectCols=request.getParameter("unSelectCols");
            //查询数据
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
                //填写查询日志
                if(v[5].equals("1"))
                {
//                    String strSql = "insert into t_log values(t_log_seq.nextval,sysdate,'" +userName + "','该用户执行了" + v[1] +":  " + str + "')";
//					log.WriteLogDb(strSql);
//					
					
				    /////////////////////////write log begin
				    try {
						WriteLog.dbLog("Q", "详单查询", "1", "该用户执行了"+v[1]+": "+str);//记录日志
					} catch (Exception e) {
						System.out.println("in ExecDetailQuery, error: "+e);
					}
					/////////////////////////write log end
					
					
                }

                //保存查询结果
                MyQueryCondition.add(param);
                MyQueryCondition.add(unSelectCols);
                MyQueryCondition.add(session_info);
                for(i=1; i<data.size(); i++)
                    MyQueryCondition.add(data.elementAt(i));
            }
            session.setAttribute("MyQueryCondition",MyQueryCondition);
        }
        else   //非首次查询
        {
             MyQueryCondition = (Vector)session.getAttribute("MyQueryCondition");
             param = (List)MyQueryCondition.get(0);
             unSelectCols=(String)MyQueryCondition.get(1);
             session_info = (Vector)MyQueryCondition.get(2);  //session信息
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

    /************** main函数,测试用 **********************************************/
    public static void main(String args[])
    {
    }
}