package com.funtalk.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class DBConnectionImpl extends HibernateDaoSupport implements DBConnection {
	
	private Log log = LogFactory.getLog(DBConnectionImpl.class);
	
	private static Map instances = new HashMap();
	
	private Map sessionFactoryMap;
	
	private static final String DEFAULT = "default";
	
	public DBConnectionImpl() {
		logger.debug("new DBConnectionImpl()");
	}

	public DBConnectionImpl(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
		logger.debug("new DBConnectionImpl(SessionFactory)");
	}
	
	/*
	 * 多例模式
	 * 根据dbStr保存多个实例
	 */
	public DBConnectionImpl getInstance(String dbStr){
		
		if(dbStr == null || "".equals(dbStr) || "null".equals(dbStr))
			dbStr = DEFAULT;
		
		if (!instances.containsKey(dbStr)) {
			SessionFactory sf = (SessionFactory) sessionFactoryMap.get(dbStr);
			if(sf == null){
				logger.error("sessionFactoryMap config error!dbStr="+dbStr);
			}
			DBConnectionImpl ml = new DBConnectionImpl(sf);
			
			instances.put(dbStr, ml);
			
			return ml;
		}
		logger.debug("DBConnectionImpl  instances.get("+dbStr+")");
		return (DBConnectionImpl) instances.get(dbStr);
	}
	
	public Connection getConnection(String DBStr){
		return getInstance(DBStr).getSession().connection();
	}
	
	public Connection getConnection(){
		return getInstance(DEFAULT).getSession().connection();
	}
	
	/**
	 * 在默认数据库上进行delete操作
	 * 
	 * @param sql
	 *            用于删除操作的SQL语句
	 * @return 是否成功
	 */
	public  boolean deleteNotBind(String sql) {
		return deleteNotBind(sql, DEFAULT);
	}

	/**
	 * 在指定数据库上进行delete操作
	 * 
	 * @param sql
	 *            用于删除操作的SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 是否成功
	 */
	public  boolean deleteNotBind(String sql, String DBStr) {
		try{
			getInstance(DBStr).getSession().createSQLQuery(sql).executeUpdate();
			return true;
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return false;
		}
	}

	/**
	 * 在默认数据库上进行update操作
	 * 
	 * @param sql
	 *            用于update操作的SQL语句
	 * @return 返回update的行数，如果失败，返回-1
	 */
	public int updateNotBind(String sql) {
		return updateNotBind(sql, DEFAULT);
	}

	/**
	 * 在指定数据库上进行update操作
	 * 
	 * @param sql
	 *            用于update操作的SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 返回update的行数，如果失败，返回-1
	 */
	public int updateNotBind(String sql, String DBStr) {
		try{
			return getInstance(DBStr).getSession().createSQLQuery(sql).executeUpdate();
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return -1;
		}
	}

	/**
	 * 在默认数据库上进行insert操作
	 * 
	 * @param sql
	 *            用于insert操作的SQL语句
	 * @return 是否成功
	 */
	public boolean insertNotBind(String sql) {
		return insertNotBind(sql, DEFAULT);
	}

	/**
	 * 在指定数据库上进行insert操作
	 * 
	 * @param sql
	 *            用于insert操作的SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 是否成功
	 */
	public boolean insertNotBind(String sql, String DBStr) {
		try{
			getInstance(DBStr).getSession().createSQLQuery(sql).executeUpdate();
			return  true;
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return false;
		}
	}

	/**
	 * 在默认数据库上进行select操作
	 * 
	 * @param sql
	 *            用于select操作的SQL语句
	 * @return 存放结果数据的List
	 */
	public List queryNotBind(String sql) {
		return queryNotBind(sql, DEFAULT);
	}

	/**
	 * 在指定数据库上进行select操作
	 * 
	 * @param sql
	 *            用于select操作的SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 存放结果数据的List
	 */
	public List queryNotBind(String sql, String DBStr) {
		try{
			return  objectArray2StringArray(getInstance(DBStr).getSession().createSQLQuery(sql).list());
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return null;
		}
	}

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
	public List queryBindPage(String sql, int beginRow, int pageSize) {
		return queryBindPage(sql, beginRow, pageSize, DEFAULT);
	}

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
	public List queryBindPage(String sql, int beginRow, int pageSize, String DBStr) {
		try{
			return  objectArray2StringArray(getInstance(DBStr).getSession().createSQLQuery(sql).setFirstResult(beginRow).setMaxResults(pageSize).list());
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return null;
		}
	}

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
	public List queryBindPageRepNull(String sql, int beginRow, int pageSize, String DBStr) {
		try{
			return  objectArray2StringArrayRepNull(getInstance(DBStr).getSession().createSQLQuery(sql).setFirstResult(beginRow).setMaxResults(pageSize).list());
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return null;
		}
	}
	
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
	public  boolean insertAuditTable(String trySql, String strSql, String dbstr) {
		return true;
	}

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
	public int updateAuditTable(String trySql, String strSql, String dbstr) {
		return -1;
	}

	/**
	 * 在默认数据库上进行批量SQL语句操作，有一条失败，则全部回滚
	 * 
	 * @param sArrSql
	 *            存放SQL语句的数组，元素0存放操作类型(1增加,2修改,3删除)，元素1存放SQL语句
	 * @return 等于0表示成功； substring(0,1)等于1表示失败，其中substring(1)存放出错原因
	 */
	public String userJdbcTrsaction(String[][] sArrSql) {
		return userJdbcTrsaction(sArrSql, DEFAULT);
	}

	/**
	 * 在指定数据库上进行批量SQL语句操作，有一条失败，则全部回滚
	 * 
	 * @param sArrSql
	 *            存放SQL语句的数组，元素1存放操作类型(1增加,2修改,3删除)，元素0存放SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 等于0表示成功； substring(0,1)等于1表示失败，其中substring(1)存放出错原因
	 */
	public String userJdbcTrsaction(String[][] sArrSql, String DBStr) {
		Connection connection = null;
		try {
			
			connection = getInstance(DBStr).getSession().connection();
			Statement stmt = connection.createStatement();
			
			if (sArrSql == null && sArrSql.length < 1)
				return "0";
			connection.setAutoCommit(false);
			for (int i = 0; i < sArrSql.length; i++) {
				stmt.executeUpdate(sArrSql[i][0]);
			}
			connection.commit();
			return "0";
		} catch (Exception ex) {
			try {
				connection.rollback();
				log.debug(ex.getMessage());
			} catch (SQLException ex2) {
				log.debug(ex2.getMessage());
			}
			return "1出错原因:[" + ex.toString() + "]";
		}finally{
			releaseSession(this.getSession());
		}
	}

	/**
	 * 在默认数据库上获取Select字段结构
	 * 
	 * @param sql
	 *            SQL语句
	 * @return 字段结构List,元素0为字段名，元素1为字段类型，元素2为字段长度
	 */
	public List GetFieldInfo(String sql) {
		return GetFieldInfo(sql, DEFAULT);
	}

	/**
	 * 在指定数据库上获取Select字段结构
	 * 
	 * @param sql
	 *            SQL语句
	 * @param DBStr
	 *            数据库定义符，如果为空，则指默认的数据库
	 * @return 字段结构List,元素0为字段名，元素1为字段类型，元素2为字段长度
	 */
	public  List GetFieldInfo(String sql, String DBStr) {
		String DBType = "oracle";//GetDbType(DBStr);
		Connection connection = null;
		connection = getInstance(DBStr).getSession().connection();	
		Statement stmt = null;
		ArrayList val = new ArrayList();
		try {
			stmt = connection.createStatement();
			
			ResultSet RS = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = RS.getMetaData();
			int ColNum = rsmd.getColumnCount();
			for (int i = 1; i <= ColNum; i++) {
				String v[] = new String[3];
				v[0] = rsmd.getColumnName(i);
				v[1] = rsmd.getColumnTypeName(i);
				int Precision = 0;
				if (DBType.equals("sybase"))
					Precision = (v[1].equals("text")) ? 4000 : rsmd.getColumnDisplaySize(i);
				else
					Precision = rsmd.getPrecision(i);
				v[2] = Precision + "";
				logger.debug("GetFieldInfo"+i+" getColumnName:"+v[0]+", getColumnTypeName:"+v[1]+", getPrecision:"+v[2]);
				val.add(v);
			}
			RS.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se) {
				log.debug(se.getMessage());
			}
			releaseSession(this.getSession());
		}
		return val;
	}

	public String executeProcedure(String sql, String start_time) {
		return executeProcedure(sql, start_time, DEFAULT);
	}

	/**
	 * 借用DataConnection的方法
	 */
	public String executeProcedure(String sql, String start_time, String DBStr) {
		CallableStatement cstmt = null;
		String returnVal = "-1";
		Connection connection = null;
		try {
			connection = getInstance(DBStr).getSession().connection();			
			cstmt = connection.prepareCall(sql);
			log.debug("this time:" + start_time);
			cstmt.setString(1, start_time.trim());
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			cstmt.execute();
			returnVal = String.valueOf(cstmt.getInt(2));
			log.debug("this:" + returnVal);
		} catch (SQLException se) {
			log.debug(se.getMessage());
			log.debug("execute procedure is error");
			returnVal = null;
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
				}
			} catch (SQLException se) {
				log.debug(se.getMessage());
			}
			releaseSession(this.getSession());
		}
		return returnVal;
	}

	/**
	 * 无绑定参数，只有一个返回值的select操作
	 */
	public String functionBindOne(String sql) {
		return functionBindOne(sql, DEFAULT);
	}

	public String functionBindOne(String sql, String DBStr) {
		try{
			return this.getSession().createSQLQuery(sql).uniqueResult().toString();	
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return null;
		}
	}

	public int functionBindOneInt(String sql) {
		return functionBindOneInt(sql, DEFAULT);
	}

	public int functionBindOneInt(String sql, String DBStr) {
		try{
			String str =  this.getSession().createSQLQuery(sql).uniqueResult().toString();	
			return Integer.parseInt(str);
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return -1;
		}
	}

	public double functionBindOneDouble(String sql, String DBStr) {
		try{
			String str =  this.getSession().createSQLQuery(sql).uniqueResult().toString();	
			return Double.parseDouble(str);
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return -1;
		}
	}

	/**
	 * 计算分页
	 * 
	 * @param sql
	 * @param begin
	 * @param pageSize
	 * @return
	 */
	public List queryBindPageList(String sql, int begin, int pageSize) {
		return queryBindPageList(sql, begin, pageSize, DEFAULT);
	}

	public List queryBindPageList(String sql, int begin, int pageSize, String DBStr) {
		try{
			return  objectArray2StringArray(getInstance(DBStr).getSession().createSQLQuery(sql).setFirstResult(begin).setMaxResults(pageSize).list());
		}catch(Exception ex){
			log.debug(ex.getMessage());
			return null;
		}
	}

	/**
	 * 计算分页
	 * 
	 * @param sql
	 * @param begin
	 * @param pageSize
	 * @return
	 */
	public Page queryBindPageVo(String sql, int begin, int end) {
		return queryBindPageVo(sql, begin, end, DEFAULT);
	}

	public Page queryBindPageVo(String sql, int begin, int end, String DBStr) {
		try{
			logger.info("=================>begin="+begin);
			logger.info("=================>end="+end);
			
			Page vo = new Page();
			//vo.setRowCount(this.getSession().createSQLQuery(sql).list().size());//这个性能有问题,需要修改
			vo.setRowCount(((Number)getInstance(DBStr).getSession().createSQLQuery("select count(*) from ( "+ sql +" )").uniqueResult()).intValue());
			vo.setList(objectArray2StringArray(getInstance(DBStr).getSession().createSQLQuery(sql).setFirstResult(begin).setMaxResults(end).list()));
			return vo;
		}catch(Exception ex){
			ex.printStackTrace();
			log.debug(ex.getMessage());
			return null;
		}
	}
	
	/**
	 * 对象数组转换成字符串数组
	 * @param listArray
	 * @return
	 */
	public List objectArray2StringArray(List listArray) {
		for (int i = 0; i < listArray.size(); i++) {
			if (listArray.get(i).getClass().isArray()) {
				Object[] oArray = (Object[]) listArray.get(i);
				String[] strArray = new String[oArray.length];
				for (int j = 0; j < oArray.length; j++) {
					if (oArray[j] != null) {
						strArray[j] = oArray[j].toString();
					} else {
						strArray[j] = null;
					}
				}
				listArray.set(i, strArray);
			}else{
				String[] strArray = {""};
				if (listArray.get(i) != null){
					strArray[0] = listArray.get(i).toString();
				}
				listArray.set(i, strArray);
			}
		}
		return listArray;
	}
	
	/**
	 * 对象数组转换成字符串数组
	 * @param listArray
	 * @return
	 */
	public List objectArray2StringArrayRepNull(List listArray) {
		for (int i = 0; i < listArray.size(); i++) {
			if (listArray.get(i).getClass().isArray()) {
				Object[] oArray = (Object[]) listArray.get(i);
				String[] strArray = new String[oArray.length];
				for (int j = 0; j < oArray.length; j++) {
					if (oArray[j] != null) {
						strArray[j] = oArray[j].toString();
					} else {
						strArray[j] = "";
					}
				}
				listArray.set(i, strArray);
			}else{
				String[] strArray = {""};
				if (listArray.get(i) != null){
					strArray[0] = listArray.get(i).toString();
				}
				listArray.set(i, strArray);
			}
		}
		return listArray;
	}
	
	public static void main(String[] args){
		//main();
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		DBConnection conn = (DBConnection)ctx.getBean("DBConnection");
		
		conn.queryBindPageVo(null,1,1,null);
	}

	public Map getSessionFactoryMap() {
		return sessionFactoryMap;
	}

	public void setSessionFactoryMap(Map sessionFactoryMap) {
		this.sessionFactoryMap = sessionFactoryMap;
	}
	
}
