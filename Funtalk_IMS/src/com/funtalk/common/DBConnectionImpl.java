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
	 * ����ģʽ
	 * ����dbStr������ʵ��
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
	 * ��Ĭ�����ݿ��Ͻ���delete����
	 * 
	 * @param sql
	 *            ����ɾ��������SQL���
	 * @return �Ƿ�ɹ�
	 */
	public  boolean deleteNotBind(String sql) {
		return deleteNotBind(sql, DEFAULT);
	}

	/**
	 * ��ָ�����ݿ��Ͻ���delete����
	 * 
	 * @param sql
	 *            ����ɾ��������SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return �Ƿ�ɹ�
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
	 * ��Ĭ�����ݿ��Ͻ���update����
	 * 
	 * @param sql
	 *            ����update������SQL���
	 * @return ����update�����������ʧ�ܣ�����-1
	 */
	public int updateNotBind(String sql) {
		return updateNotBind(sql, DEFAULT);
	}

	/**
	 * ��ָ�����ݿ��Ͻ���update����
	 * 
	 * @param sql
	 *            ����update������SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return ����update�����������ʧ�ܣ�����-1
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
	 * ��Ĭ�����ݿ��Ͻ���insert����
	 * 
	 * @param sql
	 *            ����insert������SQL���
	 * @return �Ƿ�ɹ�
	 */
	public boolean insertNotBind(String sql) {
		return insertNotBind(sql, DEFAULT);
	}

	/**
	 * ��ָ�����ݿ��Ͻ���insert����
	 * 
	 * @param sql
	 *            ����insert������SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return �Ƿ�ɹ�
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
	 * ��Ĭ�����ݿ��Ͻ���select����
	 * 
	 * @param sql
	 *            ����select������SQL���
	 * @return ��Ž�����ݵ�List
	 */
	public List queryNotBind(String sql) {
		return queryNotBind(sql, DEFAULT);
	}

	/**
	 * ��ָ�����ݿ��Ͻ���select����
	 * 
	 * @param sql
	 *            ����select������SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return ��Ž�����ݵ�List
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
	 * ��Ĭ�����ݿ��ϲ�ѯָ�������ļ�¼
	 * 
	 * @param sql
	 *            ���ڲ�ѯ������SQL���
	 * @param beginRow
	 *            ��ʼ��¼�ţ���1��ʼ��>=
	 * @param pageSize
	 * @return Ԫ��0��Ž�����ݵ�List��Ԫ��1����ܼ�¼����
	 */
	public List queryBindPage(String sql, int beginRow, int pageSize) {
		return queryBindPage(sql, beginRow, pageSize, DEFAULT);
	}

	/**
	 * ��ָ�����ݿ��ϲ�ѯָ�������ļ�¼
	 * 
	 * @param sql
	 *            ���ڲ�ѯ������SQL���
	 * @param beginRow
	 *            ��ʼ��¼�ţ���1��ʼ��>=
	 * @param pageSize
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return Ԫ��0��Ž�����ݵ�List��Ԫ��1����ܼ�¼����
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
	 * ��ָ�����ݿ��ϲ�ѯָ�������ļ�¼
	 * 
	 * @param sql
	 *            ���ڲ�ѯ������SQL���
	 * @param beginRow
	 *            ��ʼ��¼�ţ���1��ʼ��>=
	 * @param pageSize
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return Ԫ��0��Ž�����ݵ�List��Ԫ��1����ܼ�¼����
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
	 * ��̽����ʽ����壬�ܲ��룬������ʱ�����
	 * 
	 * @param trySql
	 *            ��ʽ��������
	 * @param strSql
	 *            ��ʱ��..
	 * @param dbstr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return
	 */
	public  boolean insertAuditTable(String trySql, String strSql, String dbstr) {
		return true;
	}

	/**
	 * ��̽����ʽ����壬�ܲ��룬�ٸ�������
	 * 
	 * @param trySql
	 *            ��ʽ��������
	 * @param strSql
	 *            �������
	 * @param dbstr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return
	 */
	public int updateAuditTable(String trySql, String strSql, String dbstr) {
		return -1;
	}

	/**
	 * ��Ĭ�����ݿ��Ͻ�������SQL����������һ��ʧ�ܣ���ȫ���ع�
	 * 
	 * @param sArrSql
	 *            ���SQL�������飬Ԫ��0��Ų�������(1����,2�޸�,3ɾ��)��Ԫ��1���SQL���
	 * @return ����0��ʾ�ɹ��� substring(0,1)����1��ʾʧ�ܣ�����substring(1)��ų���ԭ��
	 */
	public String userJdbcTrsaction(String[][] sArrSql) {
		return userJdbcTrsaction(sArrSql, DEFAULT);
	}

	/**
	 * ��ָ�����ݿ��Ͻ�������SQL����������һ��ʧ�ܣ���ȫ���ع�
	 * 
	 * @param sArrSql
	 *            ���SQL�������飬Ԫ��1��Ų�������(1����,2�޸�,3ɾ��)��Ԫ��0���SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return ����0��ʾ�ɹ��� substring(0,1)����1��ʾʧ�ܣ�����substring(1)��ų���ԭ��
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
			return "1����ԭ��:[" + ex.toString() + "]";
		}finally{
			releaseSession(this.getSession());
		}
	}

	/**
	 * ��Ĭ�����ݿ��ϻ�ȡSelect�ֶνṹ
	 * 
	 * @param sql
	 *            SQL���
	 * @return �ֶνṹList,Ԫ��0Ϊ�ֶ�����Ԫ��1Ϊ�ֶ����ͣ�Ԫ��2Ϊ�ֶγ���
	 */
	public List GetFieldInfo(String sql) {
		return GetFieldInfo(sql, DEFAULT);
	}

	/**
	 * ��ָ�����ݿ��ϻ�ȡSelect�ֶνṹ
	 * 
	 * @param sql
	 *            SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return �ֶνṹList,Ԫ��0Ϊ�ֶ�����Ԫ��1Ϊ�ֶ����ͣ�Ԫ��2Ϊ�ֶγ���
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
	 * ����DataConnection�ķ���
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
	 * �ް󶨲�����ֻ��һ������ֵ��select����
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
	 * �����ҳ
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
	 * �����ҳ
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
			//vo.setRowCount(this.getSession().createSQLQuery(sql).list().size());//�������������,��Ҫ�޸�
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
	 * ��������ת�����ַ�������
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
	 * ��������ת�����ַ�������
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
