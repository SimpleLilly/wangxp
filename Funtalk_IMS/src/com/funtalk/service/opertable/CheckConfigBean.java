package com.funtalk.service.opertable;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;

import com.funtalk.common.DBConnection;
import com.funtalk.common.SpringContextUtil;

/**
 * 验证局数据配置是否正确
 * @author IBM
 *
 */
public class CheckConfigBean {
	
	String DBStr = "";
	
	private DBConnection dbConnection = (DBConnection)SpringContextUtil.getBean("DBConnection");
	
	public int tableId ;
	
	public String tableName;
	
	public CheckConfigBean(){
		
	}
	
	public CheckConfigBean(int tableId){
		this.tableId = tableId;
		tableName = this.getTableName(tableId);
		DBStr = this.getTableDbStr(tableId);
	}
	public CheckConfigBean(String tableName){
		this.tableName = tableName;
		tableId = this.getTableId(tableName);
		DBStr = this.getTableDbStr(tableId);
	}
	
	/**
	 * 检验表是否在配置的数据库中
	 * @return
	 */
	public boolean isThere(){
		String tableDbstr = getTableDbStr(tableId);
		return isThere(tableName,tableDbstr);
	}
	
	
	public boolean isThere(int tableId){
		String tableName = getTableName(tableId);
		return isThere(tableName);
	}
	public boolean isThere(String tableName){
		tableName = tableName.trim();
		try{
		String sql = "select count(*) from tabs where table_name =  upper('"+tableName+"')";
		List list = dbConnection.queryNotBind(sql,DBStr);
		String[] temp = (String[])list.get(0);
		if(temp[0].equals("1")){
			return true;
		}else{
			return false;
		}
		}catch(Exception ex){
			return false;
		}
	}
	public boolean isThere(int tableId,String db){
		DBStr = db;
		return isThere(tableId);
	}
	public boolean isThere(String tableName,String db){
		DBStr = db;
		return isThere(tableName);
	}
	
	/**
	 * 验证配置是否正确 包括多字段、少字段、字段类型配置错误
	 * @return
	 */
	public HashMap checkCols(){
		
		HashMap ht = new HashMap();
		List moreList = moreCols();
		List lessList = lessCols();
		List defferList = defferTypeCols();
		
		if(moreList!=null&&moreList.size()>0){
			ht.put("moreCols",moreList);
			return ht;
		}
		if(lessList!=null&&lessList.size()>0){
			ht.put("lessCols",lessList);
			return ht;
		}
		if(defferList!=null&&defferList.size()>0){
			ht.put("defferTypeCols",defferList);
		}

		return ht;
	}
	
	/**
	 * 多配置的字段
	 * @return
	 */
	private List moreCols(){
		String sqlA = "select upper(columnname) from t_local_data_tables_cols where tableid = "+tableId;
		String sqlB = "select column_name from user_tab_columns where table_name = upper('"+tableName+"')";
		List listA = transList(dbConnection.queryNotBind(sqlA));
		List listB = transList(dbConnection.queryNotBind(sqlB,DBStr));
		return (List)CollectionUtils.subtract(listA, listB);
	}
	/**
	 * 少配置的字段
	 * @return
	 */
	private List lessCols(){
		String sqlA = "select column_name from user_tab_columns where table_name = upper('"+tableName+"') " ;
		String sqlB = "select upper(columnname) from t_local_data_tables_cols where tableid = "+tableId ;
		List listA = transList(dbConnection.queryNotBind(sqlA,DBStr));
		List listB = transList(dbConnection.queryNotBind(sqlB));
		return (List)CollectionUtils.subtract(listA, listB);
	}
	/**
	 * 字段类型配置错误
	 * @return
	 */
	private List defferTypeCols(){
		String sqlA = "select upper(columnname) ||columntype from t_local_data_tables_cols where tableid = "+tableId;
		String sqlB = "select column_name||decode(data_type, 'DATE', '3', 'NUMBER', '1', '2')  from user_tab_columns where table_name = upper('"+tableName+"')";
		List listA = transList(dbConnection.queryNotBind(sqlA));
		List listB = transList(dbConnection.queryNotBind(sqlB,DBStr));
		List listC = (List)CollectionUtils.subtract(listA, listB);
		for (int i=0 ; i<listC.size(); i++){
			String  ret = (String)listC.get(i);
			ret = ret.substring(0, ret.length()-1);
			listC.set(i, ret);
		}
		return listC;
	}
	
	/**
	 * 多配的索引字段
	 * @return
	 */
	private List moreIndex(){
		String sqlA = "select upper(columnname) from t_local_data_tables_cols where tableid = "+tableId+" and isindex = '1' ";
		String sqlB = "select columnname from (select t.column_name as columnname from user_ind_columns t,user_indexes i where t.index_name = i.index_name and t.table_name = i.table_name and t.table_name = upper('"+tableName+"') union select cu.column_name as columnname from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and au.table_name = upper('"+tableName+"'))";
		List listA = transList(dbConnection.queryNotBind(sqlA));
		List listB = transList(dbConnection.queryNotBind(sqlB,DBStr));
		return (List)CollectionUtils.subtract(listA, listB);
	}
	
	/**
	 * 少配的索引字段
	 * @return
	 */
	private List lessIndex(){
		String sqlA = "select columnname from (select t.column_name as columnname from user_ind_columns t,user_indexes i where t.index_name = i.index_name and t.table_name = i.table_name and t.table_name = upper('"+tableName+"') union select cu.column_name as columnname from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and au.table_name = upper('"+tableName+"')) ";
		String sqlB = "select upper(columnname) from t_local_data_tables_cols where tableid = "+tableId+" and isindex = '1'";
		List listA = transList(dbConnection.queryNotBind(sqlA,DBStr));
		List listB = transList(dbConnection.queryNotBind(sqlB));
		return (List)CollectionUtils.subtract(listA, listB);
	}
	
	/**
	 * 验证是否索引配置正确
	 * @return
	 */
	public HashMap checkIndex(){
		
		HashMap ht = new HashMap();
		
		List moreList = moreIndex();
		List lessList = lessIndex();
		
		if(moreList!=null&&moreList.size()>0){
			ht.put("moreIndexes", moreList);
		}
		if(lessList!=null&&lessList.size()>0){
			ht.put("lessIndexes", lessList);
		}
		
		return ht;

	}
	
	
	
	
	
	private String getTableName(int tableId){
		String tableName = "";
		String sql = "select tablename from t_local_data_tables where tableid = "+tableId+" ";
		List list = dbConnection.queryNotBind(sql);
		if(list!=null&&list.size()>0){
			String[] temp = (String[])list.get(0);
			tableName = temp[0];
		}
		return tableName;
	}
	
	private int getTableId(String tableName) {
		int tableId = 0;
		String sql = "select tableid from t_local_data_tables where upper(tablename) = upper('"+tableName+"') ";
		List list = dbConnection.queryNotBind(sql);
		if(list!=null&&list.size()>0){
			String[] temp = (String[])list.get(0);
			tableId = Integer.parseInt(temp[0]);
		}
		return tableId;
	}
	
	private String getTableDbStr(int tableId){
		String tableDbStr = "";
		String sql = "select datasource from t_local_data_tables where tableid = " + tableId;
		List list = dbConnection.queryNotBind(sql);
		if(list!=null&&list.size()>0){
			String[] temp = (String[])list.get(0);
			tableDbStr = temp[0];
		}
		return tableDbStr;
	}
	
	public List transList(List list){
		if (list == null ) return new ArrayList();
		for (int i=0 ; i<list.size(); i++){
			String [] ret = (String[])list.get(i);
			list.set(i, ret[0]);
		}
		return list;
	}
	
	
	
	
}
