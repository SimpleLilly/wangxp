package com.funtalk.common;

import java.util.List;
import java.sql.Connection;


public interface DBConnection {
	
	public Connection getConnection(String DBStr);
	
	public Connection getConnection();
	
	/**
	 * 在默认数据库上进行delete操作
	 * 
	 * @param sql
	 *            用于删除操作的SQL语句
	 * @return 是否成功
	 */
	public  boolean deleteNotBind(String sql);

	/**
	 * 在指定数据库上进行delete操作
	 * 
	 * @param sql
	 *            用于删除操作的SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 是否成功
	 */
	public  boolean deleteNotBind(String sql, String DBStr);

	/**
	 * 在默认数据库上进行update操作
	 * 
	 * @param sql
	 *            用于update操作的SQL语句
	 * @return 返回update的行数，如果失败，返回-1
	 */
	public int updateNotBind(String sql);

	/**
	 * 在指定数据库上进行update操作
	 * 
	 * @param sql
	 *            用于update操作的SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 返回update的行数，如果失败，返回-1
	 */
	public int updateNotBind(String sql, String DBStr);

	/**
	 * 在默认数据库上进行insert操作
	 * 
	 * @param sql
	 *            用于insert操作的SQL语句
	 * @return 是否成功
	 */
	public boolean insertNotBind(String sql);

	/**
	 * 在指定数据库上进行insert操作
	 * 
	 * @param sql
	 *            用于insert操作的SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 是否成功
	 */
	public boolean insertNotBind(String sql, String DBStr);

	/**
	 * 在默认数据库上进行select操作
	 * 
	 * @param sql
	 *            用于select操作的SQL语句
	 * @return 存放结果数据的List
	 */
	public List queryNotBind(String sql) ;

	/**
	 * 在指定数据库上进行select操作
	 * 
	 * @param sql
	 *            用于select操作的SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 存放结果数据的List
	 */
	public List queryNotBind(String sql, String DBStr);

	/**
	 * 在默认数据库上查询指定条数的记录
	 * 
	 * @param sql
	 *            用于查询操作的SQL语句
	 * @param beginRow
	 *            开始记录号，从1开始，>=
	 * @param pageSize
	 * @return 元素0存放结果数据的List，元素1存放总记录数。
	 */
	public List queryBindPage(String sql, int beginRow, int pageSize);

	/**
	 * 在指定数据库上查询指定条数的记录
	 * 
	 * @param sql
	 *            用于查询操作的SQL语句
	 * @param beginRow
	 *            开始记录号，从1开始，>=
	 * @param pageSize
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 元素0存放结果数据的List，元素1存放总记录数。
	 */
	public List queryBindPage(String sql, int beginRow, int pageSize, String DBStr);
	
	/**
	 * 在指定数据库上查询指定条数的记录
	 * 
	 * @param sql
	 *            用于查询操作的SQL语句
	 * @param beginRow
	 *            开始记录号，从1开始，>=
	 * @param pageSize
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 元素0存放结果数据的List，元素1存放总记录数。
	 */
	public List queryBindPageRepNull(String sql, int beginRow, int pageSize, String DBStr);

	/**
	 * 试探往正式表里插，能插入，再往临时表里插
	 * 
	 * @param trySql
	 *            正式表插入语句
	 * @param strSql
	 *            临时表..
	 * @param dbstr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return
	 */
	public  boolean insertAuditTable(String trySql, String strSql, String dbstr);

	/**
	 * 试探往正式表里插，能插入，再更新数据
	 * 
	 * @param trySql
	 *            正式表插入语句
	 * @param strSql
	 *            更新语句
	 * @param dbstr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return
	 */
	public int updateAuditTable(String trySql, String strSql, String dbstr);

	/**
	 * 在默认数据库上进行批量SQL语句操作，有一条失败，则全部回滚
	 * 
	 * @param sArrSql
	 *            存放SQL语句的数组，元素1存放操作类型(1增加,2修改,3删除)，元素0存放SQL语句
	 * @return 等于0表示成功； substring(0,1)等于1表示失败，其中substring(1)存放出错原因
	 */
	public String userJdbcTrsaction(String[][] sArrSql) ;

	/**
	 * 在指定数据库上进行批量SQL语句操作，有一条失败，则全部回滚
	 * 
	 * @param sArrSql
	 *            存放SQL语句的数组，元素1存放操作类型(1增加,2修改,3删除)，元素0存放SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 等于0表示成功； substring(0,1)等于1表示失败，其中substring(1)存放出错原因
	 */
	public String userJdbcTrsaction(String[][] sArrSql, String DBStr) ;

	/**
	 * 在默认数据库上获取Select字段结构
	 * 
	 * @param sql
	 *            SQL语句
	 * @return 字段结构List,元素0为字段名，元素1为字段类型，元素2为字段长度
	 */
	public List GetFieldInfo(String sql) ;

	/**
	 * 在指定数据库上获取Select字段结构
	 * 
	 * @param sql
	 *            SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 字段结构List,元素0为字段名，元素1为字段类型，元素2为字段长度
	 */
	public  List GetFieldInfo(String sql, String DBStr);

	public String executeProcedure(String sql, String start_time);

	public String executeProcedure(String sql, String start_time, String DBStr);

	/**
	 * 无绑定参数，只有一个返回值的select操作
	 */
	public String functionBindOne(String sql);

	public String functionBindOne(String sql, String DBStr);

	public int functionBindOneInt(String sql) ;

	public int functionBindOneInt(String sql, String DBStr);

	public double functionBindOneDouble(String sql, String DBStr);

	/**
	 * 计算分页
	 * 
	 * @param sql
	 * @param begin
	 * @param pageSize
	 * @return
	 */
	public List queryBindPageList(String sql, int begin, int pageSize);

	public List queryBindPageList(String sql, int begin, int pageSize, String DBStr);

	/**
	 * 计算分页
	 * 
	 * @param sql
	 * @param begin
	 * @param pageSize
	 * @return
	 */
	public Page queryBindPageVo(String sql, int begin, int end);

	public Page queryBindPageVo(String sql, int begin, int end, String DBStr);

}
