package com.funtalk.bean;

import com.funtalk.bean.DetailQueryBean;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

/** 基于文件的查询通用类.
 *
 * @author  wangjs
 * @version 1, 2
 * 
 * 由于t_nouse_file_field 字段的索引是userid+ueryid+field_name
 * 所以在配置字段时的字段中文名不能重复
 */
public class FileQueryBean extends DetailQueryBean
{
	private boolean lineFlag=true;////////在线还是离线话单查询,true--在线话单
	
    private String ROUTE_TABLE = "route_control_table";       //路由表

    boolean RoamFlag;   //是否漫游地转换
    Vector  RoamV =  null;   //存储漫游地信息

    /************** 构造函数 **********************************************/
    public FileQueryBean()
    {
        QUERY_TABLE = "t_query_file_info";          //查询配置表
        WHERE_TABLE = "t_query_file_where";         //查询条件配置表
        NOUSE_FIELD_TABLE = "t_nouse_file_field";   //不需要查询的字段表
    }
    
    public FileQueryBean(boolean lineFlag)
    {
        QUERY_TABLE = "t_query_file_info";          //查询配置表
        WHERE_TABLE = "t_query_file_where";         //查询条件配置表
        NOUSE_FIELD_TABLE = "t_nouse_file_field";   //不需要查询的字段表
        
        this.lineFlag=lineFlag;
    }

    /************** 私有函数 **********************************************/

    /**
     * 获取子查询信息
     * @param   sql 输入的原始SQL语句
     * @return  存放子查询信息的Vector (0 查询记录类别,对应cfg文件的record_type； 1 查询条件,对应cfg文件的row_info)
     * modify xuyd 增加返回 2 union　序号　取base_col_info:字段序号
     */
    private List GetSubQryInfo(String sql)
    {
        List val = new ArrayList();
        /*
         * 详单结构不同的业务需在同一界面查
         * 使用union关键字连接
         */
        int i_from,i_where;
        String[] sqlArr = sql.split("union");
        for(int i = 0; i < sqlArr.length; i++){
        	i_from = sqlArr[i].indexOf("from");
            i_where = sqlArr[i].indexOf("where");
            String from = sqlArr[i].substring(i_from+4, i_where);
            //System.out.println("from="+from);
            String where = sqlArr[i].substring(i_where+5).trim();
            //System.out.println("where="+where);
            String[] sub_type = from.split(",");
            for(int j=0; j<sub_type.length; j++)
            {
                String v[] = new String[3];
                v[0] = sub_type[j].trim();
                //System.out.println("sub_type["+j+"]="+from);
                v[1] = where;
                v[2] = ""+i;
                val.add(v);
            }
        } 
        ///////////////////////////////////////////////
/*        int i_from = sql.indexOf("from");
        int i_where = sql.indexOf("where");
        String from = sql.substring(i_from+4, i_where);
        System.out.println("from="+from);
        String where = sql.substring(i_where+5).trim();
        System.out.println("where="+where);
        String[] sub_type = from.split(",");
        for(int i=0; i<sub_type.length; i++)
        {
            String v[] = new String[2];
            v[0] = sub_type[i].trim();
            System.out.println("sub_type["+i+"]="+from);
            v[1] = where;
            val.add(v);
        }*/
        return val;
    }

    /**
     * 获取字段的具体类型
     * @param   sql 输入的原始SQL语句
     * @param   field 输入字段信息
     * @param 	union 序号
     * @return  存放查询字段的详细信息的Vector (0 中文名 1 是否显示 2是否求和 3字段类型 4字段长度 5 字段序号)
     */
    private List GetFieldType(String sql, List field,int index)
    {
        int i, j, inx;
        String tmpStr, tmpF;
        List val = new ArrayList();
        //System.out.println(sql);
        String[] sqlArr = sql.split("union");
        //System.out.println(sqlArr[index]);
        for(i=0; i<field.size(); i++)
        {

            String v[] = (String []) field.get(i);
            String sumStr = v[2];
            //System.out.println("sumStr"+v[2]);
            sumStr = (sumStr.equals("0"))?"":sumStr;
            //System.out.println("v[0]="+v[0]);
            tmpStr = "\"" + sumStr + v[0] + "\"";
            inx = sqlArr[index].indexOf(tmpStr);
            //boolean flag = false;
            tmpStr = "";
            for(j=inx-1; j>0; j--)
            {
                if( (tmpStr.trim().length()>0)&&((sqlArr[index].charAt(j)==' ')||(sqlArr[index].charAt(j)==',')) )
                    break;
                tmpStr = tmpStr + sqlArr[index].charAt(j);
            }
            tmpStr = tmpStr.trim();
            tmpF = "";
            for(j=tmpStr.length()-1; j>=0; j--)
                tmpF = tmpF + tmpStr.charAt(j);

            inx = tmpF.indexOf("_");
            String type = tmpF.substring(inx+1, inx+2);

            String vv[] = new String[6];
            vv[0] = v[0];
            vv[1] = v[1];
            vv[2] = v[2];
            vv[3] = type;   //类型
            if(type.equals("S"))
                vv[4] = tmpF.substring(inx+2);  //长度
            else
                vv[4] = "0";   //长度
            vv[5] = tmpF.substring(0, inx);   //序号
            val.add(vv);
        }
/*        for(int m = 0; m < val.size(); m++){
        	String[] arr = (String[]) val.get(m);
        	System.out.print(arr[0]+",");
        	System.out.print(arr[1]+",");
        	System.out.print(arr[2]+",");
        	System.out.print(arr[3]+",");
        	System.out.print(arr[4]+",");
        	System.out.println(arr[5]+",");
        	
        }*/
        return val;
    }

    /**
     * 生成配置文件
     * @param   path 配置文件的路径
     * @param   cfgFile 配置文件的文件名
     * @param   subQry 子查询信息
     * @param   field 输入的字段信息
     * @param   fseq 字段顺序调整后的信息
     * @return  true: 成功;  false: 失败
     */
    private boolean CreateCfgFile(String path, String cfgFile, List subQry,
                        List field, List fseq,String mainSql,List oldF)
    {
        int i;
        String col_disp_type="", base_col_info="", tmpStr, segStr="";
        try
        {
            File temp = new File( path, cfgFile);
            FileWriter inFile = new FileWriter(temp);
            BufferedWriter buf = new BufferedWriter(inFile);

            buf.write("head_info:Title");
            buf.write("\nsort_col:1");

            //各个列数据的显示格式
            for(i=0; i<fseq.size(); i++)
            {
                String ar[] = (String[])fseq.get(i);
                String v[] = (String []) field.get(Integer.parseInt(ar[0]));

                segStr = (base_col_info.length()>0)?"|":"";
                if(!(v[2].equals("0")))
                    tmpStr = "f3";
                else if(v[3].equals("D"))
                    tmpStr = "D11";
                else
                    tmpStr = "";
                if(tmpStr.length()>0)
                    col_disp_type = col_disp_type + segStr + tmpStr;

                base_col_info = base_col_info + segStr + v[5];
            }
            //是否漫游地转换
            if(RoamFlag)
            {
                base_col_info = base_col_info + "|" + RoamV.elementAt(1).toString() +
                            "|" + RoamV.elementAt(2).toString();
            }

            buf.write("\ncol_disp_type:" + col_disp_type);

            for(i=0; i<subQry.size(); i++)
            {
                String v[] = (String [])subQry.get(i);
                buf.write("\nbegin:" + (i+1));
                buf.write("\n  record_type:" + v[0]);
                //xuyd add 取union的序号。不同详单结构，字段序号不同
                if(v[2].equals("0"))
                	buf.write("\n  base_col_info:" + base_col_info);
                else
                	buf.write("\n  base_col_info:" + getUnionBase_col_info(fseq,v[2],mainSql,oldF));
                ///////////////////////////////////
                buf.write("\n  row_info:" + v[1]);
                buf.write("\nend:" + (i+1));
            }
            buf.close();
            inFile.close();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println("生成配置文件出错[" + e.toString() + "]");
            return false;
        }
        return true;
    }

    private String getUnionBase_col_info(List fseq,String uIndex,String mainsql,List oldF) {
    	
    	 String base_col_info="", segStr="";
        //查询字段的详细信息的Vector (0 中文名 1 是否显示 2是否求和 3字段类型 4字段长度 5 字段序号)
        //modify xuyd 详单结构不同的业务需在同一界面查
    	// F = GetFieldType(mainsql, F, 0);
        List F = GetFieldType(mainsql, oldF, Integer.parseInt(uIndex));
        //各个列数据的显示格式
        for(int i=0; i<fseq.size(); i++)
        {
            String ar[] = (String[])fseq.get(i);
            String v[] = (String []) F.get(Integer.parseInt(ar[0]));

            segStr = (base_col_info.length()>0)?"|":"";

            base_col_info = base_col_info + segStr + v[5];
        }
        //System.out.println(base_col_info);
		return base_col_info;
	}

	/**
     * 生成查询脚本并执行
     * @param   usercode 用户号码
     * @param   start_time 开始时间(指处理时间)
     * @param   end_time 截止时间(指处理时间)
     * @param   query_code 4位数字的查询代码
     * @param   shellFile 脚本文件的带路径文件名
     * @param   outFile 输出结果数据的带路径文件名
     * @return  true: 成功;  false: 失败
     */
    private boolean ExceQueryShell(String usercode, String start_time, String end_time,
                    String query_code, String shellFile, String outFile)
    {
        //取路由信息
        String routeStr = null;
        
        
        String sSql="";
        
        if(this.lineFlag)
        {
            //在线
            sSql="select route_ip_online, route_port_online, route_ip_new_online, route_port_new_online, change_date from  " +
               ROUTE_TABLE + " where trim(route_name) = '"+usercode.substring(0,7)+"'";
        }
        else
        {
            /////////离线
        	sSql="select route_ip,query_port,route_ip_new,query_port_new,change_date from  " +
                ROUTE_TABLE + " where trim(route_name) = '"+usercode.substring(0,7)+"'";
        }
        
        System.out.println("================>sSql="+sSql);
        
        List al = (List) dbconn.queryNotBind(sSql);
        if((al==null)||(al.size()<=0))
        {
            System.out.println ("获取路由信息出错[sql="+ sSql + "]");
            return false;
        }
        
        String[] ar = (String[]) al.get(0);
        if(start_time.compareTo(ar[4]) < 0 )
            routeStr = ar[0]+" "+ar[1];
        else
            routeStr = ar[2]+" "+ar[3];
       /* //取区号,06.11.19 xuyd add
        String longCode = null;
        String strSql="select trim(LONG_CODE_NEW) from h1h2h3_code_allocate where trim(H1H2H3H4) = '"+usercode.substring(0,7)+"'";
        List ls = (List) dbconn.queryNotBind(strSql);
        if((ls==null)||(ls.size()<=0))
        {
            System.out.println ("获取区号信息出错[sql="+ sSql + "]");
            return false;
        }
        String[] arr = (String[]) ls.get(0);
        longCode = arr[0];*/
        //执行脚本
        String  command = shellFile + /*longCode +*/ "" + usercode + " " + query_code + " " +
                          start_time+ " " + end_time+ " " + routeStr + " 6 " + outFile;
        System.out.print("\ncommand="+command+"\n");
        Process proc = null;
        try
        {
            proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            proc.destroy();
        }
        catch (Exception e)
        {
            if(proc!=null)
                proc.destroy();
            System.out.println ("执行脚本出错["+ e.toString() + "]");
            return false;
        }
        return true;
    }

    /**
     * 读取文件，获取查询数据
     * @param   path 文件路径
     * @param   file 文件名
     * @param   fseq 字段顺序调整后的信息
     * @param   beginPage 页码，从1开始
     * @param   rowNum 每页的最多记录数
     * @return 存放结果数据的Vector(0数据List，1总记录数，2合计信息)
     */
    private Vector GetDataFormFile(String path, String file,
                  List fseq, int beginPage, int rowNum)
    {
        Vector val = new Vector();
        String dataStr = "";
        int beginRow = (beginPage-1)*rowNum;
        int i,j,icount = 0;
        try
        {
            //File temp = new File( path, file);
            //FileReader fr = new FileReader(temp);
            //BufferedReader buf = new BufferedReader(fr);
        	//xuyd modify  加入中文支持
        	BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(path+file),"GBK"));

            String title = buf.readLine();  //标题
            String count = buf.readLine();  //总条数
            String sum   = buf.readLine();  //合计

            String tline = null;
            if(beginRow!=0)  //跳过beginRow行数据
            {
                icount = 0;
                while( (tline = buf.readLine()) != null )
                {
                    icount++;
                    if(icount>=beginRow)
                        break;
                }
            }

            //正式读取数据
            icount = 0;
            Vector tmpData = new Vector();
            while( (tline = buf.readLine()) != null )
            {
                if(icount>=rowNum) break;

                icount++;
                String tmpV[] = new String[fseq.size()];
                String tmpStr = "";
                j = 0;
                for(i=0; i<tline.length(); i++)
                {
                    if(tline.charAt(i)!='|')
                    {
                        tmpStr = tmpStr + tline.charAt(i);
                        continue;
                    }
                    //判断漫游地转换
                    if((j >= fseq.size())&&(RoamFlag))
                    {
                        if(j == fseq.size() )   //漫游类型
                        {
                            if(!(tmpStr.equals("国际漫游")))
                                break;
                        }
                        else if( j == fseq.size() + 1 )   //国际运营商标识
                        {
                            tmpV[Integer.parseInt(RoamV.elementAt(0).toString())] = tmpStr;
                            if(RoamV.elementAt(3).toString().length()>0)
                                tmpV[Integer.parseInt(RoamV.elementAt(3).toString())] = "";
                            break;
                        }
                        tmpStr = "";
                        j++;
                        continue;
                    }

                    String ar[] = (String[])fseq.get(j);
                    tmpV[Integer.parseInt(ar[1])] = tmpStr;
                    tmpStr = "";
                    j++;
                }
                tmpData.add(tmpV);
            }
            val.addElement(tmpData);
            val.addElement(count);
            val.addElement(sum);

            buf.close();
            //fr.close();
       }
       catch (Exception e)
       {
            System.out.println("读取详单文件出错[" + e.toString() + "]");
            return null;
       }
       return val;
    }

    /**
     * 根据从文件中读到的合计信息拼写合计项
     * @param   sumStr 从文件中读到的合计信息
     * @param   field 字段信息
     * @return 合计项List(0名称,1数值)
     */
    private List GetSumList(String sumStr, List field)
    {
        int i, j=0, k;
        List val = new ArrayList();
        String tmpStr;
        String sumAr[] = ToolsOfSystem.splitString(sumStr);
        for(i=0; i<field.size(); i++)
        {
            String v[] = (String [])field.get(i);
            if(v[1].equals("0"))  continue;
            if(v[2].equals("0"))  continue;

            if(sumAr[j].indexOf(".")>0)
            {
            	//去小数点后无意义0 xuyd modify
                for(k=sumAr[j].length()-1; k>0; k--)
                {
                    if((sumAr[j].charAt(k)!='0')||(sumAr[j].charAt(k)=='.'))
                        break;
                }

                tmpStr = sumAr[j].substring(0,k+1);
                if(tmpStr.charAt(tmpStr.length()-1)=='.')
                	tmpStr = tmpStr.substring(0,tmpStr.length()-1);
            }
            else
                tmpStr = sumAr[j];

            String vv[] = new String[2];
            vv[0] = v[0];
            vv[1] = tmpStr;
            val.add(vv);
            j++;
        }
        return val;
    }

    /**
     * 获取字段显示标题的字符串
     * @param   field 字段信息
     * @return 字段显示标题的字符串
     */
    private List GetFieldTitle(List field)
    {
        List val = new ArrayList();
        String tmpStr;
        for(int i=0; i<field.size(); i++)
        {
            String v[] = (String [])field.get(i);
            if(v[1].equals("0"))
                continue;
            String vv[] = new String[3];
            tmpStr = v[2];
            tmpStr = (tmpStr.equals("0"))?"":tmpStr;
            vv[0] = tmpStr + v[0];
            tmpStr = v[3];
            if(tmpStr.equals("N"))
            {
                vv[1] = "r8";
                vv[2] = "0";
            }
            else if(tmpStr.equals("D"))
            {
                vv[1] = "string";
                vv[2] = "20";
            }
            else
            {
                vv[1] = "string";
                vv[2] = v[4];
            }
            val.add(vv);
        }
        return val;
    }

    /**
     * 重新整理字段顺序，本函数的目的是将合计字段、日期字段放到最前面，便于接口程序处理
     * @param   field 字段信息
     * @return 调帐后的字段顺序信息(0: 所有的字段的序号; 1: 需要显示的字段的序号)
     */
    private List ModifyFieldSeq(List field)
    {
        int i,j;
        List fSeq = new ArrayList();

        //合计项
        j=0;
        for(i=0; i<field.size(); i++)
        {
            String v[] = (String [])field.get(i);
            if(v[1].equals("0"))  continue;
            if(!(v[2].equals("0")))
            {
                String[] ar = new String[2];
                ar[0] = ""+ i;
                ar[1] = ""+ j;
                fSeq.add(ar);
            }
            j++;
        }

        //日期项
        j=0;
        for(i=0; i<field.size(); i++)
        {
            String v[] = (String [])field.get(i);
            if(v[1].equals("0"))  continue;
            if(v[3].equals("D"))
            {
                String[] ar = new String[2];
                ar[0] = ""+ i;
                ar[1] = ""+ j;
                fSeq.add(ar);
            }
            j++;
        }

        //其他项
        j=0;
        for(i=0; i<field.size(); i++)
        {
            String v[] = (String [])field.get(i);
            if(v[1].equals("0"))  continue;
            if(( v[2].equals("0"))&&
                (!v[3].equals("D")) )
            {
                String[] ar = new String[2];
                ar[0] = ""+ i;
                ar[1] = ""+ j;
                fSeq.add(ar);
            }
            j++;
        }
        return fSeq;
    }

    /**
     * 将输入日期加days天，如果大于系统时间，则取系统时间
     * @param    dateStr 日期字符串yyyyMMddHHmmss
     * @return   加完之后的日期字符串
     */
    private String AddDate(String dateStr, int days)
    {
        SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d=null;
        Date now = new Date();
        try
        {
            d = form.parse(dateStr);
            long ld = d.getTime()/1000;
            d.setTime( (ld + 3600*24*days)*1000);
            if(d.after(now))
                d = now;
            return form.format(d);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            return dateStr;
        }
    }

     //漫游地(针对语音业务的国际漫游情况)
    private boolean GetRoamInfo(List fSeq, List F)
    {
        int i;
        RoamV = new Vector();
        RoamV.addElement("");   //漫游地在显示字段中的序号
        RoamV.addElement("");   //漫游类型序号
        RoamV.addElement("");   //trunkGroup在所有字段中的序号
        RoamV.addElement("");   //trunkGroup在显示字段中的序号
        for(i=0; i<fSeq.size(); i++)
        {
            String ar[] = (String[])fSeq.get(i);
            String v[] = (String [])F.get(Integer.parseInt(ar[0]));
            if(v[0].equals("漫游地"))
            {
                RoamV.setElementAt(ar[1], 0);
            }
            else if(v[0].equals("入中继"))
            {
                RoamV.setElementAt(ar[1], 3);
            }
        }
        for(i=0; i<F.size(); i++)
        {
            String v[] = (String [])F.get(i);
            if(v[0].equals("漫游类型"))
            {
                String roamTypeSeq = v[5];
                RoamV.setElementAt(roamTypeSeq, 1);
            }
            else if(v[0].equals("入中继"))
            {
                String inRouteSeq = v[5];
                RoamV.setElementAt(inRouteSeq, 2);
            }
        }
        return (RoamV.elementAt(0).toString().length()>0)&&
               (RoamV.elementAt(1).toString().length()>0)&&
               (RoamV.elementAt(2).toString().length()>0);
    }


    /************** 公有函数 **********************************************/

    /**
     * 获取query_id对应的详细信息
     * @param    query_id 查询编号
     * @return   0 query_id, 1 name, 2 main_sql, 3 title, 4 is_cause, 5 is_log, 6 page_way, 7 不用
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, title, is_cause, is_log, page_way, 'a' "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL);
        if ( (val==null) || (val.size() <= 0) )
        {
            System.out.println("获取查询信息时出错[query_id="+ query_id + "]");
            return null;
        }
        return (String [])val.get(0);
    }

    /**
     * 获取查询结果
     * @param   query_id 查询编号
     * @param   fieldStr 不需要显示的字段序号列表,如0,3,4
     * @param   param 查询条件
     * @param   beginPage 本次需要查询第几页
     * @param   rowNum 每页的最多记录数
     * @param   session_info 事务信息
     * @param   first 本次查询是否为首次查询
     * @return  查询结果Vector, 0为数据List, 1为查询条件List(0名称,1值), 2为标题, 3打印纸方向, 4总记录数, 5合计项List(0名称,1值),6字段属性List(0名称,1类型,2长度)
     */
    public Vector GetQueryData(String query_id, String fieldStr, List param,
              int beginPage, int rowNum, Vector session_info, boolean first)
    {
        int i, j;
        String tmpStr;
        Vector data = null;
        String userName = session_info.elementAt(3).toString();

        //初始化返回结果
        Vector val = new Vector();
        for(i=0; i<7; i++)
            val.addElement("");

        //各类文件的路径及文件名
        int sessionid = Integer.parseInt(session_info.elementAt(0).toString());
        String fileSep = System.getProperty("file.separator");
        String root_path = session_info.elementAt(2).toString() + fileSep + "query" +fileSep;
              //TXT文件路径
        String dataPath = root_path + "data" + fileSep;
              //TXT文件名
        String dataFile = session_info.elementAt(1).toString() + "_" + sessionid + ".txt";
              //本次查询代码
        String query_code = (new Integer(9000 + sessionid % 1000)).toString();
              //CFG文件路径
        String cfgPath = root_path + "etc" + fileSep;
              //CFG文件名
        String cfgFile = "query_" + query_code + ".cfg";
              //脚本文件
        String shellFile = "sh "+root_path + "bin"+fileSep+"a.sh ";

        //获取查询信息
        String r[] = GetQueryInfo(query_id);
        if((r==null)||(r.length<=0))
            return null;
        String mainsql = r[2];
        String title = r[3];
        String orient = r[6];

        //获取所有字段信息
        //0为字段名, 1为本字段是否显示(1显示，0不显示), 2为是否是合计项(0不是, SUM_是)
        List F = GetField(mainsql, null, userName);
        List old_F = F;
        if((F==null)||(F.size()<=0))
            return null;
        //查询字段的详细信息的Vector (0 中文名 1 是否显示 2是否求和 3字段类型 4字段长度 5 字段序号)
        //modify xuyd 详单结构不同的业务需在同一界面查
        F = GetFieldType(mainsql, F, 0);

        //根据用户选择剔除不需要的字段,并保存到数据库中
        F = RemoveField(F, fieldStr);
        if(first)
            SaveFieldDB(query_id, userName, F);

        //重新整理字段顺序
        //调整后的字段顺序信息(0: 所有的字段的序号; 1: 需要显示的字段的序号)
        List fSeq = ModifyFieldSeq(F);
/*        for(int m = 0; m < fSeq.size(); m++){
        	String[] arr = (String[]) fSeq.get(m);
        	System.out.print("seq ="+arr[0]+",");
        	System.out.println(arr[1]);
        }*/
        //漫游地(针对语音业务的国际漫游情况)
        if(query_id.equals("1"))
            RoamFlag = GetRoamInfo(fSeq, F);
        else
            RoamFlag = false;

        //如果是取下一页，则直接读取数据
        if(!(first))
        {
            data = GetDataFormFile(dataPath, dataFile, fSeq, beginPage, rowNum);
            if((data==null)||(data.size()<=0))
                return null;
            val.setElementAt( (List)data.elementAt(0), 0 );
            return val;
        }

        //子查询信息(record_type,row_info)
        //modify xuyd 详单结构不同的业务需在同一界面查
        List subQry = GetSubQryInfo(mainsql);

        //替换查询条件
        for(i=0; i<subQry.size(); i++)
        {
            String tmpV[] = (String [])subQry.get(i);
            tmpV[1] = QryWhereBean.ReplaceByParam(tmpV[1], param);
        }

        //生成配置文件
        //modify xuyd 详单结构不同的业务需在同一界面查
        if(CreateCfgFile(cfgPath, cfgFile, subQry, F, fSeq,mainsql,old_F)==false)
            return null;

        //获取查询脚本并执行
        String usercode  = ((String [])param.get(0))[5];
        String begintime = ((String [])param.get(1))[5];
        String endtime   = ((String [])param.get(2))[5];
        endtime = AddDate(endtime, 60);  //将结束时间后延60天
        boolean flag = ExceQueryShell(usercode, begintime, endtime, query_code, shellFile, dataPath+dataFile);
        
        if(!flag){
        	System.out.println("----------------exce fail");
            return null;
        }
        //获取查询数据
        data = GetDataFormFile(dataPath, dataFile, fSeq, beginPage, rowNum);
        if((data==null)||(data.size()<=0))
            return null;
        
        try {
			String countStr = (""+data.elementAt(1)).trim();
			
			String sumStr = (""+data.elementAt(2)).trim();
			
			val.setElementAt( (List)data.elementAt(0), 0 );

			/*如果是第一页*/
			if(first)
			{
			     /*查询参数字符串*/
			    val.setElementAt( QryWhereBean.GetParamList(param), 1 );
			    /*标题及各字段类型、长度*/
			    val.setElementAt( title, 2 );
			    /*打印纸方向*/
			    val.setElementAt( orient, 3 );

			    /*总记录数*/
			    val.setElementAt( countStr, 4 );
			    /*合计项*/
			    val.setElementAt( GetSumList(sumStr, F), 5 );

			    /*字段属性*/
			    val.setElementAt( GetFieldTitle(F), 6 );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("------->in GetQueryData, error: "+e);
			
			val.setElementAt( new ArrayList(), 0 );
			val.setElementAt( new ArrayList(), 1 );
			val.setElementAt( "出现异常!"+e.getMessage(), 2 );
			val.setElementAt( "0", 3 );
			val.setElementAt( "0", 4 );
			val.setElementAt( new ArrayList(), 5 );
			val.setElementAt( new ArrayList(), 6 );

		}
        
        
        
        
        return val;
    }

    /************** main函数,测试用 **********************************************/
    public static void main(String args[])
    {
    }

}
