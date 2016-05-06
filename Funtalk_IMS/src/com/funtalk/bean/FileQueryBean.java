package com.funtalk.bean;

import com.funtalk.bean.DetailQueryBean;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

/** �����ļ��Ĳ�ѯͨ����.
 *
 * @author  wangjs
 * @version 1, 2
 * 
 * ����t_nouse_file_field �ֶε�������userid+ueryid+field_name
 * �����������ֶ�ʱ���ֶ������������ظ�
 */
public class FileQueryBean extends DetailQueryBean
{
	private boolean lineFlag=true;////////���߻������߻�����ѯ,true--���߻���
	
    private String ROUTE_TABLE = "route_control_table";       //·�ɱ�

    boolean RoamFlag;   //�Ƿ����ε�ת��
    Vector  RoamV =  null;   //�洢���ε���Ϣ

    /************** ���캯�� **********************************************/
    public FileQueryBean()
    {
        QUERY_TABLE = "t_query_file_info";          //��ѯ���ñ�
        WHERE_TABLE = "t_query_file_where";         //��ѯ�������ñ�
        NOUSE_FIELD_TABLE = "t_nouse_file_field";   //����Ҫ��ѯ���ֶα�
    }
    
    public FileQueryBean(boolean lineFlag)
    {
        QUERY_TABLE = "t_query_file_info";          //��ѯ���ñ�
        WHERE_TABLE = "t_query_file_where";         //��ѯ�������ñ�
        NOUSE_FIELD_TABLE = "t_nouse_file_field";   //����Ҫ��ѯ���ֶα�
        
        this.lineFlag=lineFlag;
    }

    /************** ˽�к��� **********************************************/

    /**
     * ��ȡ�Ӳ�ѯ��Ϣ
     * @param   sql �����ԭʼSQL���
     * @return  ����Ӳ�ѯ��Ϣ��Vector (0 ��ѯ��¼���,��Ӧcfg�ļ���record_type�� 1 ��ѯ����,��Ӧcfg�ļ���row_info)
     * modify xuyd ���ӷ��� 2 union����š�ȡbase_col_info:�ֶ����
     */
    private List GetSubQryInfo(String sql)
    {
        List val = new ArrayList();
        /*
         * �굥�ṹ��ͬ��ҵ������ͬһ�����
         * ʹ��union�ؼ�������
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
     * ��ȡ�ֶεľ�������
     * @param   sql �����ԭʼSQL���
     * @param   field �����ֶ���Ϣ
     * @param 	union ���
     * @return  ��Ų�ѯ�ֶε���ϸ��Ϣ��Vector (0 ������ 1 �Ƿ���ʾ 2�Ƿ���� 3�ֶ����� 4�ֶγ��� 5 �ֶ����)
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
            vv[3] = type;   //����
            if(type.equals("S"))
                vv[4] = tmpF.substring(inx+2);  //����
            else
                vv[4] = "0";   //����
            vv[5] = tmpF.substring(0, inx);   //���
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
     * ���������ļ�
     * @param   path �����ļ���·��
     * @param   cfgFile �����ļ����ļ���
     * @param   subQry �Ӳ�ѯ��Ϣ
     * @param   field ������ֶ���Ϣ
     * @param   fseq �ֶ�˳����������Ϣ
     * @return  true: �ɹ�;  false: ʧ��
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

            //���������ݵ���ʾ��ʽ
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
            //�Ƿ����ε�ת��
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
                //xuyd add ȡunion����š���ͬ�굥�ṹ���ֶ���Ų�ͬ
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
            System.out.println("���������ļ�����[" + e.toString() + "]");
            return false;
        }
        return true;
    }

    private String getUnionBase_col_info(List fseq,String uIndex,String mainsql,List oldF) {
    	
    	 String base_col_info="", segStr="";
        //��ѯ�ֶε���ϸ��Ϣ��Vector (0 ������ 1 �Ƿ���ʾ 2�Ƿ���� 3�ֶ����� 4�ֶγ��� 5 �ֶ����)
        //modify xuyd �굥�ṹ��ͬ��ҵ������ͬһ�����
    	// F = GetFieldType(mainsql, F, 0);
        List F = GetFieldType(mainsql, oldF, Integer.parseInt(uIndex));
        //���������ݵ���ʾ��ʽ
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
     * ���ɲ�ѯ�ű���ִ��
     * @param   usercode �û�����
     * @param   start_time ��ʼʱ��(ָ����ʱ��)
     * @param   end_time ��ֹʱ��(ָ����ʱ��)
     * @param   query_code 4λ���ֵĲ�ѯ����
     * @param   shellFile �ű��ļ��Ĵ�·���ļ���
     * @param   outFile ���������ݵĴ�·���ļ���
     * @return  true: �ɹ�;  false: ʧ��
     */
    private boolean ExceQueryShell(String usercode, String start_time, String end_time,
                    String query_code, String shellFile, String outFile)
    {
        //ȡ·����Ϣ
        String routeStr = null;
        
        
        String sSql="";
        
        if(this.lineFlag)
        {
            //����
            sSql="select route_ip_online, route_port_online, route_ip_new_online, route_port_new_online, change_date from  " +
               ROUTE_TABLE + " where trim(route_name) = '"+usercode.substring(0,7)+"'";
        }
        else
        {
            /////////����
        	sSql="select route_ip,query_port,route_ip_new,query_port_new,change_date from  " +
                ROUTE_TABLE + " where trim(route_name) = '"+usercode.substring(0,7)+"'";
        }
        
        System.out.println("================>sSql="+sSql);
        
        List al = (List) dbconn.queryNotBind(sSql);
        if((al==null)||(al.size()<=0))
        {
            System.out.println ("��ȡ·����Ϣ����[sql="+ sSql + "]");
            return false;
        }
        
        String[] ar = (String[]) al.get(0);
        if(start_time.compareTo(ar[4]) < 0 )
            routeStr = ar[0]+" "+ar[1];
        else
            routeStr = ar[2]+" "+ar[3];
       /* //ȡ����,06.11.19 xuyd add
        String longCode = null;
        String strSql="select trim(LONG_CODE_NEW) from h1h2h3_code_allocate where trim(H1H2H3H4) = '"+usercode.substring(0,7)+"'";
        List ls = (List) dbconn.queryNotBind(strSql);
        if((ls==null)||(ls.size()<=0))
        {
            System.out.println ("��ȡ������Ϣ����[sql="+ sSql + "]");
            return false;
        }
        String[] arr = (String[]) ls.get(0);
        longCode = arr[0];*/
        //ִ�нű�
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
            System.out.println ("ִ�нű�����["+ e.toString() + "]");
            return false;
        }
        return true;
    }

    /**
     * ��ȡ�ļ�����ȡ��ѯ����
     * @param   path �ļ�·��
     * @param   file �ļ���
     * @param   fseq �ֶ�˳����������Ϣ
     * @param   beginPage ҳ�룬��1��ʼ
     * @param   rowNum ÿҳ������¼��
     * @return ��Ž�����ݵ�Vector(0����List��1�ܼ�¼����2�ϼ���Ϣ)
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
        	//xuyd modify  ��������֧��
        	BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(path+file),"GBK"));

            String title = buf.readLine();  //����
            String count = buf.readLine();  //������
            String sum   = buf.readLine();  //�ϼ�

            String tline = null;
            if(beginRow!=0)  //����beginRow������
            {
                icount = 0;
                while( (tline = buf.readLine()) != null )
                {
                    icount++;
                    if(icount>=beginRow)
                        break;
                }
            }

            //��ʽ��ȡ����
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
                    //�ж����ε�ת��
                    if((j >= fseq.size())&&(RoamFlag))
                    {
                        if(j == fseq.size() )   //��������
                        {
                            if(!(tmpStr.equals("��������")))
                                break;
                        }
                        else if( j == fseq.size() + 1 )   //������Ӫ�̱�ʶ
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
            System.out.println("��ȡ�굥�ļ�����[" + e.toString() + "]");
            return null;
       }
       return val;
    }

    /**
     * ���ݴ��ļ��ж����ĺϼ���Ϣƴд�ϼ���
     * @param   sumStr ���ļ��ж����ĺϼ���Ϣ
     * @param   field �ֶ���Ϣ
     * @return �ϼ���List(0����,1��ֵ)
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
            	//ȥС�����������0 xuyd modify
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
     * ��ȡ�ֶ���ʾ������ַ���
     * @param   field �ֶ���Ϣ
     * @return �ֶ���ʾ������ַ���
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
     * ���������ֶ�˳�򣬱�������Ŀ���ǽ��ϼ��ֶΡ������ֶηŵ���ǰ�棬���ڽӿڳ�����
     * @param   field �ֶ���Ϣ
     * @return ���ʺ���ֶ�˳����Ϣ(0: ���е��ֶε����; 1: ��Ҫ��ʾ���ֶε����)
     */
    private List ModifyFieldSeq(List field)
    {
        int i,j;
        List fSeq = new ArrayList();

        //�ϼ���
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

        //������
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

        //������
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
     * ���������ڼ�days�죬�������ϵͳʱ�䣬��ȡϵͳʱ��
     * @param    dateStr �����ַ���yyyyMMddHHmmss
     * @return   ����֮��������ַ���
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

     //���ε�(�������ҵ��Ĺ����������)
    private boolean GetRoamInfo(List fSeq, List F)
    {
        int i;
        RoamV = new Vector();
        RoamV.addElement("");   //���ε�����ʾ�ֶ��е����
        RoamV.addElement("");   //�����������
        RoamV.addElement("");   //trunkGroup�������ֶ��е����
        RoamV.addElement("");   //trunkGroup����ʾ�ֶ��е����
        for(i=0; i<fSeq.size(); i++)
        {
            String ar[] = (String[])fSeq.get(i);
            String v[] = (String [])F.get(Integer.parseInt(ar[0]));
            if(v[0].equals("���ε�"))
            {
                RoamV.setElementAt(ar[1], 0);
            }
            else if(v[0].equals("���м�"))
            {
                RoamV.setElementAt(ar[1], 3);
            }
        }
        for(i=0; i<F.size(); i++)
        {
            String v[] = (String [])F.get(i);
            if(v[0].equals("��������"))
            {
                String roamTypeSeq = v[5];
                RoamV.setElementAt(roamTypeSeq, 1);
            }
            else if(v[0].equals("���м�"))
            {
                String inRouteSeq = v[5];
                RoamV.setElementAt(inRouteSeq, 2);
            }
        }
        return (RoamV.elementAt(0).toString().length()>0)&&
               (RoamV.elementAt(1).toString().length()>0)&&
               (RoamV.elementAt(2).toString().length()>0);
    }


    /************** ���к��� **********************************************/

    /**
     * ��ȡquery_id��Ӧ����ϸ��Ϣ
     * @param    query_id ��ѯ���
     * @return   0 query_id, 1 name, 2 main_sql, 3 title, 4 is_cause, 5 is_log, 6 page_way, 7 ����
     */
    public String[] GetQueryInfo(String query_id)
    {
        String sSQL = "select query_id, name, main_sql, title, is_cause, is_log, page_way, 'a' "
               + "from "+QUERY_TABLE +" where query_id=" + query_id;

        List val = dbconn.queryNotBind(sSQL);
        if ( (val==null) || (val.size() <= 0) )
        {
            System.out.println("��ȡ��ѯ��Ϣʱ����[query_id="+ query_id + "]");
            return null;
        }
        return (String [])val.get(0);
    }

    /**
     * ��ȡ��ѯ���
     * @param   query_id ��ѯ���
     * @param   fieldStr ����Ҫ��ʾ���ֶ�����б�,��0,3,4
     * @param   param ��ѯ����
     * @param   beginPage ������Ҫ��ѯ�ڼ�ҳ
     * @param   rowNum ÿҳ������¼��
     * @param   session_info ������Ϣ
     * @param   first ���β�ѯ�Ƿ�Ϊ�״β�ѯ
     * @return  ��ѯ���Vector, 0Ϊ����List, 1Ϊ��ѯ����List(0����,1ֵ), 2Ϊ����, 3��ӡֽ����, 4�ܼ�¼��, 5�ϼ���List(0����,1ֵ),6�ֶ�����List(0����,1����,2����)
     */
    public Vector GetQueryData(String query_id, String fieldStr, List param,
              int beginPage, int rowNum, Vector session_info, boolean first)
    {
        int i, j;
        String tmpStr;
        Vector data = null;
        String userName = session_info.elementAt(3).toString();

        //��ʼ�����ؽ��
        Vector val = new Vector();
        for(i=0; i<7; i++)
            val.addElement("");

        //�����ļ���·�����ļ���
        int sessionid = Integer.parseInt(session_info.elementAt(0).toString());
        String fileSep = System.getProperty("file.separator");
        String root_path = session_info.elementAt(2).toString() + fileSep + "query" +fileSep;
              //TXT�ļ�·��
        String dataPath = root_path + "data" + fileSep;
              //TXT�ļ���
        String dataFile = session_info.elementAt(1).toString() + "_" + sessionid + ".txt";
              //���β�ѯ����
        String query_code = (new Integer(9000 + sessionid % 1000)).toString();
              //CFG�ļ�·��
        String cfgPath = root_path + "etc" + fileSep;
              //CFG�ļ���
        String cfgFile = "query_" + query_code + ".cfg";
              //�ű��ļ�
        String shellFile = "sh "+root_path + "bin"+fileSep+"a.sh ";

        //��ȡ��ѯ��Ϣ
        String r[] = GetQueryInfo(query_id);
        if((r==null)||(r.length<=0))
            return null;
        String mainsql = r[2];
        String title = r[3];
        String orient = r[6];

        //��ȡ�����ֶ���Ϣ
        //0Ϊ�ֶ���, 1Ϊ���ֶ��Ƿ���ʾ(1��ʾ��0����ʾ), 2Ϊ�Ƿ��Ǻϼ���(0����, SUM_��)
        List F = GetField(mainsql, null, userName);
        List old_F = F;
        if((F==null)||(F.size()<=0))
            return null;
        //��ѯ�ֶε���ϸ��Ϣ��Vector (0 ������ 1 �Ƿ���ʾ 2�Ƿ���� 3�ֶ����� 4�ֶγ��� 5 �ֶ����)
        //modify xuyd �굥�ṹ��ͬ��ҵ������ͬһ�����
        F = GetFieldType(mainsql, F, 0);

        //�����û�ѡ���޳�����Ҫ���ֶ�,�����浽���ݿ���
        F = RemoveField(F, fieldStr);
        if(first)
            SaveFieldDB(query_id, userName, F);

        //���������ֶ�˳��
        //��������ֶ�˳����Ϣ(0: ���е��ֶε����; 1: ��Ҫ��ʾ���ֶε����)
        List fSeq = ModifyFieldSeq(F);
/*        for(int m = 0; m < fSeq.size(); m++){
        	String[] arr = (String[]) fSeq.get(m);
        	System.out.print("seq ="+arr[0]+",");
        	System.out.println(arr[1]);
        }*/
        //���ε�(�������ҵ��Ĺ����������)
        if(query_id.equals("1"))
            RoamFlag = GetRoamInfo(fSeq, F);
        else
            RoamFlag = false;

        //�����ȡ��һҳ����ֱ�Ӷ�ȡ����
        if(!(first))
        {
            data = GetDataFormFile(dataPath, dataFile, fSeq, beginPage, rowNum);
            if((data==null)||(data.size()<=0))
                return null;
            val.setElementAt( (List)data.elementAt(0), 0 );
            return val;
        }

        //�Ӳ�ѯ��Ϣ(record_type,row_info)
        //modify xuyd �굥�ṹ��ͬ��ҵ������ͬһ�����
        List subQry = GetSubQryInfo(mainsql);

        //�滻��ѯ����
        for(i=0; i<subQry.size(); i++)
        {
            String tmpV[] = (String [])subQry.get(i);
            tmpV[1] = QryWhereBean.ReplaceByParam(tmpV[1], param);
        }

        //���������ļ�
        //modify xuyd �굥�ṹ��ͬ��ҵ������ͬһ�����
        if(CreateCfgFile(cfgPath, cfgFile, subQry, F, fSeq,mainsql,old_F)==false)
            return null;

        //��ȡ��ѯ�ű���ִ��
        String usercode  = ((String [])param.get(0))[5];
        String begintime = ((String [])param.get(1))[5];
        String endtime   = ((String [])param.get(2))[5];
        endtime = AddDate(endtime, 60);  //������ʱ�����60��
        boolean flag = ExceQueryShell(usercode, begintime, endtime, query_code, shellFile, dataPath+dataFile);
        
        if(!flag){
        	System.out.println("----------------exce fail");
            return null;
        }
        //��ȡ��ѯ����
        data = GetDataFormFile(dataPath, dataFile, fSeq, beginPage, rowNum);
        if((data==null)||(data.size()<=0))
            return null;
        
        try {
			String countStr = (""+data.elementAt(1)).trim();
			
			String sumStr = (""+data.elementAt(2)).trim();
			
			val.setElementAt( (List)data.elementAt(0), 0 );

			/*����ǵ�һҳ*/
			if(first)
			{
			     /*��ѯ�����ַ���*/
			    val.setElementAt( QryWhereBean.GetParamList(param), 1 );
			    /*���⼰���ֶ����͡�����*/
			    val.setElementAt( title, 2 );
			    /*��ӡֽ����*/
			    val.setElementAt( orient, 3 );

			    /*�ܼ�¼��*/
			    val.setElementAt( countStr, 4 );
			    /*�ϼ���*/
			    val.setElementAt( GetSumList(sumStr, F), 5 );

			    /*�ֶ�����*/
			    val.setElementAt( GetFieldTitle(F), 6 );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("------->in GetQueryData, error: "+e);
			
			val.setElementAt( new ArrayList(), 0 );
			val.setElementAt( new ArrayList(), 1 );
			val.setElementAt( "�����쳣!"+e.getMessage(), 2 );
			val.setElementAt( "0", 3 );
			val.setElementAt( "0", 4 );
			val.setElementAt( new ArrayList(), 5 );
			val.setElementAt( new ArrayList(), 6 );

		}
        
        
        
        
        return val;
    }

    /************** main����,������ **********************************************/
    public static void main(String args[])
    {
    }

}
