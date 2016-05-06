package com.funtalk.common;

import java.util.List;
import java.sql.Connection;


public interface DBConnection {
	
	public Connection getConnection(String DBStr);
	
	public Connection getConnection();
	
	/**
	 * ��Ĭ�����ݿ��Ͻ���delete����
	 * 
	 * @param sql
	 *            ����ɾ��������SQL���
	 * @return �Ƿ�ɹ�
	 */
	public  boolean deleteNotBind(String sql);

	/**
	 * ��ָ�����ݿ��Ͻ���delete����
	 * 
	 * @param sql
	 *            ����ɾ��������SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return �Ƿ�ɹ�
	 */
	public  boolean deleteNotBind(String sql, String DBStr);

	/**
	 * ��Ĭ�����ݿ��Ͻ���update����
	 * 
	 * @param sql
	 *            ����update������SQL���
	 * @return ����update�����������ʧ�ܣ�����-1
	 */
	public int updateNotBind(String sql);

	/**
	 * ��ָ�����ݿ��Ͻ���update����
	 * 
	 * @param sql
	 *            ����update������SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return ����update�����������ʧ�ܣ�����-1
	 */
	public int updateNotBind(String sql, String DBStr);

	/**
	 * ��Ĭ�����ݿ��Ͻ���insert����
	 * 
	 * @param sql
	 *            ����insert������SQL���
	 * @return �Ƿ�ɹ�
	 */
	public boolean insertNotBind(String sql);

	/**
	 * ��ָ�����ݿ��Ͻ���insert����
	 * 
	 * @param sql
	 *            ����insert������SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return �Ƿ�ɹ�
	 */
	public boolean insertNotBind(String sql, String DBStr);

	/**
	 * ��Ĭ�����ݿ��Ͻ���select����
	 * 
	 * @param sql
	 *            ����select������SQL���
	 * @return ��Ž�����ݵ�List
	 */
	public List queryNotBind(String sql) ;

	/**
	 * ��ָ�����ݿ��Ͻ���select����
	 * 
	 * @param sql
	 *            ����select������SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return ��Ž�����ݵ�List
	 */
	public List queryNotBind(String sql, String DBStr);

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
	public List queryBindPage(String sql, int beginRow, int pageSize);

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
	public List queryBindPage(String sql, int beginRow, int pageSize, String DBStr);
	
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
	public List queryBindPageRepNull(String sql, int beginRow, int pageSize, String DBStr);

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
	public  boolean insertAuditTable(String trySql, String strSql, String dbstr);

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
	public int updateAuditTable(String trySql, String strSql, String dbstr);

	/**
	 * ��Ĭ�����ݿ��Ͻ�������SQL����������һ��ʧ�ܣ���ȫ���ع�
	 * 
	 * @param sArrSql
	 *            ���SQL�������飬Ԫ��1��Ų�������(1����,2�޸�,3ɾ��)��Ԫ��0���SQL���
	 * @return ����0��ʾ�ɹ��� substring(0,1)����1��ʾʧ�ܣ�����substring(1)��ų���ԭ��
	 */
	public String userJdbcTrsaction(String[][] sArrSql) ;

	/**
	 * ��ָ�����ݿ��Ͻ�������SQL����������һ��ʧ�ܣ���ȫ���ع�
	 * 
	 * @param sArrSql
	 *            ���SQL�������飬Ԫ��1��Ų�������(1����,2�޸�,3ɾ��)��Ԫ��0���SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return ����0��ʾ�ɹ��� substring(0,1)����1��ʾʧ�ܣ�����substring(1)��ų���ԭ��
	 */
	public String userJdbcTrsaction(String[][] sArrSql, String DBStr) ;

	/**
	 * ��Ĭ�����ݿ��ϻ�ȡSelect�ֶνṹ
	 * 
	 * @param sql
	 *            SQL���
	 * @return �ֶνṹList,Ԫ��0Ϊ�ֶ�����Ԫ��1Ϊ�ֶ����ͣ�Ԫ��2Ϊ�ֶγ���
	 */
	public List GetFieldInfo(String sql) ;

	/**
	 * ��ָ�����ݿ��ϻ�ȡSelect�ֶνṹ
	 * 
	 * @param sql
	 *            SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return �ֶνṹList,Ԫ��0Ϊ�ֶ�����Ԫ��1Ϊ�ֶ����ͣ�Ԫ��2Ϊ�ֶγ���
	 */
	public  List GetFieldInfo(String sql, String DBStr);

	public String executeProcedure(String sql, String start_time);

	public String executeProcedure(String sql, String start_time, String DBStr);

	/**
	 * �ް󶨲�����ֻ��һ������ֵ��select����
	 */
	public String functionBindOne(String sql);

	public String functionBindOne(String sql, String DBStr);

	public int functionBindOneInt(String sql) ;

	public int functionBindOneInt(String sql, String DBStr);

	public double functionBindOneDouble(String sql, String DBStr);

	/**
	 * �����ҳ
	 * 
	 * @param sql
	 * @param begin
	 * @param pageSize
	 * @return
	 */
	public List queryBindPageList(String sql, int begin, int pageSize);

	public List queryBindPageList(String sql, int begin, int pageSize, String DBStr);

	/**
	 * �����ҳ
	 * 
	 * @param sql
	 * @param begin
	 * @param pageSize
	 * @return
	 */
	public Page queryBindPageVo(String sql, int begin, int end);

	public Page queryBindPageVo(String sql, int begin, int end, String DBStr);

}
