package com.funtalk.common;

import java.sql.*;
import java.util.*;

import com.funtalk.common.DataConnection;
import com.funtalk.common.Local_data_tables;
import com.funtalk.common.Local_data_tables_cols;
 

public class MyTable
{
	private DataConnection dbconn=new DataConnection();
	 
	public MyTable()
	{}
	
	public Connection getConnection()
	{
	  Connection conn=null;
	  try
	  {
	    conn=dbconn.getConnection();	
	  }
	  catch(Exception ex)
	  {
	    System.out.println("in getConnection,"+ex);	
	  }
	  return conn;	
	}
	
	public Local_data_tables query_table(String tableid)//根据tableid求表对象
	{
		Local_data_tables tab=null;
		try
		{
			Connection conn=getConnection();
			Statement stmt=conn.createStatement();
			String strsql="select TABLEID,TABLENAME,TABLECOMMENT,TABLEPARENT,ISCONFIG,ISHELP   from local_data_tables where tableid='"+tableid+"'";
			ResultSet rs=stmt.executeQuery(strsql);
			/////////
			if(rs.next() )
			{
				tab=new Local_data_tables();
        tab.TABLEID=rs.getString(1);
        tab.TABLENAME=rs.getString(2);
        tab.TABLECOMMENT=rs.getString(3);
        tab.TABLEPARENT=rs.getString(4);
        tab.ISCONFIG="";
        tab.ISHELP="";
			}
			/////////
			rs.close();
			rs=null;
			stmt.close();
			stmt=null;
			conn.close();
			conn=null;
		}
		catch(Exception ex)
		{
			System.out.println("in query_table,exception : "+ex);
		}
		return tab;
	}
	
	public Vector query_table_cols(String tableid)//根据tableid得到表的字段对象集
	{
		Vector v=new Vector();
		try
		{
			Connection conn=getConnection();
			Statement stmt=conn.createStatement();
			String strsql="select LOCALID,TABLEID,COLUMNSEQ,COLUMNNAME,COLUMNTYPE,COLUMNCOMMENT,ISINDEX,ISQUERY,CONFIGRATION,LINKTABLEID,ISORDER,STYLE,ISNULL,ISMUTI,HELPID,DEFAULTVALUE from local_data_tables_cols where tableid='"+tableid+"'  order by COLUMNSEQ";
			ResultSet rs=stmt.executeQuery(strsql);
			////////
			while(rs.next())
			{
				Local_data_tables_cols tmp=new Local_data_tables_cols();
        tmp.LOCALID=rs.getString(1);
        tmp.TABLEID=rs.getString(2);
        tmp.COLUMNSEQ=rs.getString(3);
        tmp.COLUMNNAME=rs.getString(4);
        tmp.COLUMNTYPE=rs.getString(5);
        tmp.COLUMNCOMMENT=rs.getString(6);
        tmp.ISINDEX=rs.getString(7);
        tmp.ISQUERY=rs.getString(8);
        tmp.CONFIGRATION=rs.getString(9);
        tmp.LINKTABLEID=rs.getString(10);
        tmp.ISORDER=rs.getString(11);
        tmp.STYLE=rs.getString(12);
        tmp.ISNULL=rs.getString(13);
        tmp.ISMUTI=rs.getString(14);
        tmp.HELPID=rs.getString(15);
        tmp.DEFAULTVALUE=rs.getString(16);
        
        v.add(tmp);
			}
			////////
			rs.close();
			rs=null;
			stmt.close();
			stmt=null;
			conn.close();
			conn=null;
		}
		catch(Exception ex)
		{
			System.out.println("in query_table_cols,exception : "+ex);
		}
		return v;
	}
	
	public String getSelect(String tableid)//根据tableid组成select查询的头,未对isquery处理
	{
		String str="";
		Vector v=query_table_cols(tableid);
		str="select ";
		for(int i=0;i<v.size();i++)
		{
			Local_data_tables_cols tmp=(Local_data_tables_cols)v.elementAt(i);
			String colname=tmp.COLUMNNAME;
			str+=" "+colname+" , ";
		}
		System.out.println("--->str="+str.substring(0,str.length()-2) );
		return str.substring(0,str.length()-2);
	}
	
	public String getTable(String tableid)//根据tableid求表名
	{
		String str="";
		Local_data_tables lt=query_table(tableid);
		str=lt.TABLENAME;
		System.out.println("-->str="+str);
		return str;
	}
	
  public String getCondition(Vector v)  //得到翻页的条件查询SQL语句,包括顺逆序排序
  {
    String str="";
    ////////
    if(v!=null && v.size()>0)
    {
      str="  where  ";
      for(int i=0;i<v.size();i++)
      {
        Vector t=(Vector)v.elementAt(i);
        String col_name=(String)t.elementAt(0);
        String col_value=(String)t.elementAt(1);
        String col_type=(String)t.elementAt(2);
        if(col_type.equals("2") )  //col_type=2表示字符串,1表整型,3表日期型
          str+="   "+col_name+" like '%"+col_value+"%'  and ";
        else if(col_type.equals("1") )
          str+="   "+col_name+" like '%"+col_value+"%'  and ";
        else if(col_type.equals("3") )
          str+="   "+col_name+" = to_date('"+col_value+"','yyyy-mm-dd HH24:MI:SS') and ";//to_date('2005-06-01 08:29:13','yyyy-mm-dd HH24:MI:SS')  
          
      }
      str=str.substring(0, str.length() - 4);//去掉查询中的最后一个and      
    }
    ////////
    
    return str;
  }

  public Vector query(String tableid,String condition,String orderby,String asc)//翻页的条件查询记录集
  {
    Vector v=new Vector();
    System.out.println("-->select="+getSelect(tableid));
    try
    {
    	String strsql=getSelect(tableid)+"  from "+getTable(tableid)+"  "+condition+"  order by "+orderby+"  "+asc;
    	System.out.println("-------->quety:strsql="+strsql);
    	Connection conn=getConnection();
    	Statement stmt=conn.createStatement();
    	ResultSet rs=stmt.executeQuery(strsql);
    	ResultSetMetaData metaData=rs.getMetaData();
    	int column_number=metaData.getColumnCount();
    	////////
    	while(rs.next())
    	{
    		Vector tmp=new Vector();
    		for(int i=0;i<column_number;i++)//如有必要,这里可以加上字段类型判断和修整
    		{
    			String value=rs.getString(i+1);
    			tmp.add(value);
    		}
    		v.add(tmp);
    	}
    	////////
    	rs.close();
    	rs=null;
    	stmt.close();
    	stmt=null;
    	conn.close();
    	conn=null;
    }
    catch(Exception ex)
    {
    	System.out.println("xxx in query,exception : "+ex);
    }
    return v;
  }
  
  public String getUpdateCondition(String myvalue,Vector v_cols)// 根据索引来更新或删除记录的where条件
  {
  	String str="";
  	StringTokenizer st=new StringTokenizer(myvalue,":");
  	Vector v=new Vector();
  	while(st.hasMoreTokens() )
  	{
  		v.add((String)st.nextToken());
  	}
  	////////
  	if(v_cols!=null && v_cols.size()>0 )
  	{
  		str=" where ";
  	  for(int i=0;i<v_cols.size();i++)
  	  {
  		  Local_data_tables_cols lc=(Local_data_tables_cols)v_cols.elementAt(i);//取出每个字段对象
  		  String isindex=lc.ISINDEX;
  		  String col_type=lc.COLUMNTYPE;
  		  String col_name=lc.COLUMNNAME;
  		  
  		  if(isindex.equals("1") )
  		  {
  		  	String col_value=(String)v.elementAt(i);
  			  if(col_type.equals("2") )  //col_type=1表整型,2表示字符串,3表日期型
            str+="   "+col_name+" like '%"+col_value+"%'  and ";
          else if(col_type.equals("1") )
            str+="   "+col_name+" like '%"+col_value+"%'  and ";
          else if(col_type.equals("3") )
            str+="   "+col_name+" = to_date('"+col_value+"','yyyy-mm-dd HH24:MI:SS') and ";//to_date('2005-06-01 08:29:13','yyyy-mm-dd HH24:MI:SS')  
          
  		  }
  	  }
    }
  	////////
  	return str.substring(0,str.length()-4);
  }
  
  public boolean deleteRecord(String tableid,String condition)  //按条件删除某一条记录
  {
  	boolean flag=false;
  	try
  	{
  		String strsql="delete from "+getTable(tableid)+" "+condition;
  		System.out.println("---->strsql="+strsql);
			Connection conn=getConnection();
			Statement stmt=conn.createStatement();
			stmt.executeUpdate(strsql);
			stmt.close();
			stmt=null;
			conn.close();
			conn=null;  
			flag=true;
  	}
  	catch(Exception ex)
  	{
  		System.out.println(ex);
  	}
  	return flag;
  }
  
  public boolean deleteRecord(String tableid,String condition,String dbstr)  //按条件删除某一条记录，会根据dbstr选不同的数据库
  {
  	boolean flag=false;
  	try
  	{
  		String strsql="delete from "+getTable(tableid)+" "+condition;
  		System.out.println("---->strsql="+strsql);
      flag=dbconn.deleteNotBind(strsql,dbstr);
  	}
  	catch(Exception ex)
  	{
  		System.out.println(ex);
  	}
  	return flag;
  }  
  
  public boolean updateRecord(String tableid,Vector destValue,String condition)  //更新某一条记录
  {
  	boolean flag=false;
  	try
  	{
  		String strsql="update "+getTable(tableid)+" set ";
  		for(int i=0;i<destValue.size();i++)
  		{
  			Vector x=(Vector)destValue.elementAt(i);
  			System.out.println("-->x="+x);
  		  String col_name=(String)x.elementAt(0);
  		  String col_value=(String)x.elementAt(1);
  		  String col_type=(String)x.elementAt(2);
  		  
  		  if(col_type.equals("1") )   //col_type=1表整型,2表示字符串,3表日期型
  		  {
          strsql+=col_name+"="+col_value+",";
        }  
  			else if(col_type.equals("2") ) 
  			{
  				strsql+=col_name+"="+"'"+col_value+"',";
  			}
        else if(col_type.equals("3") )
          strsql+=col_name+"="+"to_date('"+col_value+"','yyyy-mm-dd HH24:MI:SS')"+",";//to_date('2005-06-01 08:29:13','yyyy-mm-dd HH24:MI:SS')  
  		}
  		strsql=strsql.substring(0,strsql.length()-1 );
  		strsql+=" "+condition;
  		
  		System.out.println("---->strsql="+strsql);
			Connection conn=getConnection();
			Statement stmt=conn.createStatement();
			stmt.executeUpdate(strsql);
			stmt.close();
			stmt=null;
			conn.close();
			conn=null;  
			flag=true;
  	}
  	catch(Exception ex)
  	{
  		System.out.println("in updateRecord,exception : "+ex);
  	}
  	return flag;
  }
  
  public boolean insertRecord(String tableid,Vector v)//tableid为表编号,v为要插入的值组成的向量
  {
  	boolean flag=false;
  	try
  	{
  		String tablename=getTable(tableid);
  		System.out.println("-->tablename="+tablename);
  		String str="insert into "+tablename+" ";
  		String s1=" ( ";
  		String s2=" values ( ";
  		for(int i=0;i<v.size();i++)
  		{
  			Vector x=(Vector)v.elementAt(i);
  			System.out.println("-->x="+x);
  		  String col_name=(String)x.elementAt(0);
  		  String col_value=(String)x.elementAt(1);
  		  String col_type=(String)x.elementAt(2);
  		  
  		  s1+=col_name+",";
  		  
  		  if(col_type.equals("1") )
  		  {
          s2+=col_value+",";
        }  
  			else if(col_type.equals("2") )  //col_type=2表示字符串,1表整型,3表日期型
  			{
  				s2+="'"+col_value+"',";
  			}
        else if(col_type.equals("3") )
          s2+="to_date('"+col_value+"','yyyy-mm-dd HH24:MI:SS') ,";//to_date('2005-06-01 08:29:13','yyyy-mm-dd HH24:MI:SS')  
  		}
  		s1=s1.substring(0,s1.length()-1)+" ) ";
  		s2=s2.substring(0,s2.length()-1)+" ) " ;
  		str+=s1+s2;
  		
  		System.out.println("------>"+str);
			Connection conn=getConnection();
			Statement stmt=conn.createStatement();
			stmt.executeUpdate(str);
			stmt.close();
			stmt=null;
			conn.close();
			conn=null;  	
			flag=true;	
  	}
  	catch(Exception ex)
  	{
  		System.out.println("in insertRecord,"+ex);
  	}
  	return flag;
  }
	
	public static void main(String [] args)
	{
		MyTable m=new MyTable();
		Vector v=new Vector();
		Vector tmp=new Vector();
		tmp.add("TABLEID");
		tmp.add("7");
		tmp.add("2");
		v.add(tmp);
		////////
		tmp=new Vector();
		tmp.add("TABLENAME");
		tmp.add("city_list");
		tmp.add("2");
		v.add(tmp);	
		////////
		tmp=new Vector();
		tmp.add("TABLECOMMENT");
		tmp.add("城市代码表");
		tmp.add("2");
		v.add(tmp);	
		////////
		tmp=new Vector();
		tmp.add("TABLEPARENT");
		tmp.add("tablename0");
		tmp.add("2");
		v.add(tmp);	
		////////
		tmp=new Vector();
		tmp.add("ISCONFIG");
		tmp.add("0");
		tmp.add("1");
		v.add(tmp);	
		////////
		tmp=new Vector();
	  tmp.add("ISHELP");
		tmp.add("2005-06-01 08:29:13");
		tmp.add("3");
		v.add(tmp);	
		m.insertRecord("7",v);
		
	}
}