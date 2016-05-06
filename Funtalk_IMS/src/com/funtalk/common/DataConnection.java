package com.funtalk.common;

import java.sql.*;
import java.util.*;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import sitech.www.frame.jdbc.*;
import sitech.www.frame.util.*;


public class DataConnection{

	/* ���ݿ������ַ� */
	public static String DBString = null;

	public DataConnection() {
		DBString = null;
	}

	/**
	 * ��ȡ����,���DBStrΪ�գ�����Ĭ�����ӣ� ����ʹ��DBStr�������ļ�CommQuery.properties��Ӧ��JDBC����
	 * 
	 * @return Connection
	 */
	public static Connection getConnection() {
		return getConnection(null);
	}

	public static Connection getConnection(String DBStr) {
		Connection conn = null;
		try {
			if ((DBStr == null) || DBStr.equals("") || DBStr.equals("null")) {
				conn = DataSourceUtils.getConnection();
			} else {
				// Properties pr = new Properties();
				// FileInputStream fl = new
				// FileInputStream("/billing/lxbwebapp/WEB-INF/classes/DataConnection.properties");
				// pr.load(fl);
				ResourceBundle bundle = ResourceBundle.getBundle("DataConnection");
				String JNDI = null;
				try {
					JNDI = bundle.getString(DBStr + ".jndi");
					// JNDI=pr.getProperty(DBStr + ".jndi").trim();
				} catch (Exception e) {
					System.out.println("in read DataConnection.properties," + e);
				}
				if ((JNDI != null) && (!(JNDI.trim().equals("")))) {
					InitialContext ic = new InitialContext();
					DataSource ds = (DataSource) ic.lookup(JNDI.trim());
					conn = ds.getConnection();
				} else {
					// String DB_Driver=pr.getProperty(DBStr + ".drivers").trim();
					// String DB_Url =pr.getProperty(DBStr + ".url").trim();
					// String DB_User =pr.getProperty(DBStr + ".user").trim();
					// String DB_Pass =pr.getProperty(DBStr + ".password").trim();
					String DB_Driver = bundle.getString(DBStr + ".drivers");
					String DB_Url = bundle.getString(DBStr + ".url");
					String DB_User = bundle.getString(DBStr + ".user");
					String DB_Pass = bundle.getString(DBStr + ".password");
					Class.forName(DB_Driver).newInstance();
					conn = DriverManager.getConnection(DB_Url, DB_User, DB_Pass);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	/**
	 * �ر� Connection����
	 * 
	 * @param conn
	 */
	private static void closeConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * ��ȡ���ݿ�����
	 * 
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return ���ݿ�����, oracle��sybase
	 */
	public static String GetDbType(String DBStr) {
		if ((DBStr == null) || (DBStr.equals("")))
			return Config.DEFAULT_DB_CLASS;
		else {
			ResourceBundle bundle = ResourceBundle.getBundle("DataConnection");
			String DB_Driver = bundle.getString(DBStr + ".drivers");
			if (DB_Driver.indexOf("oracle") >= 0)
				return "oracle";
			else if (DB_Driver.indexOf("sybase") >= 0)
				return "sybase";
			else
				return "oracle";
		}
	}

	/**
	 * ��Ĭ�����ݿ��Ͻ���delete����
	 * 
	 * @param sql
	 *            ����ɾ��������SQL���
	 * @return �Ƿ�ɹ�
	 */
	public static boolean deleteNotBind(String sql) {
		return deleteNotBind(sql, DBString);
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
	public static boolean deleteNotBind(String sql, String DBStr) {
		Connection conn = null;
		try {
			boolean isSucceed = false;
			if ((DBStr == null) || (DBStr.equals("")))
				isSucceed = SqlDelete.delete(sql);
			else {
				conn = getConnection(DBStr);
				if (conn == null)
					return false;
				isSucceed = SqlDelete.delete(conn, sql);
			}
			return isSucceed;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * ��Ĭ�����ݿ��Ͻ���update����
	 * 
	 * @param sql
	 *            ����update������SQL���
	 * @return ����update�����������ʧ�ܣ�����-1
	 */
	public static int updateNotBind(String sql) {
		return updateNotBind(sql, DBString);
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
	public static int updateNotBind(String sql, String DBStr) {
		Connection conn = null;
		try {
			int rows = 0;
			if ((DBStr == null) || (DBStr.equals("")))
				rows = SqlUpdate.update(sql);
			else {
				conn = getConnection(DBStr);
				if (conn == null)
					return -1;
				rows = SqlUpdate.update(conn, sql);
			}
			return rows;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * ��Ĭ�����ݿ��Ͻ���insert����
	 * 
	 * @param sql
	 *            ����insert������SQL���
	 * @return �Ƿ�ɹ�
	 */
	public static boolean insertNotBind(String sql) {
		return insertNotBind(sql, DBString);
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
	public static boolean insertNotBind(String sql, String DBStr) {
		Connection conn = null;
		try {
			boolean isSucceed = false;
			if ((DBStr == null) || (DBStr.equals("")))
				isSucceed = SqlInsert.insert(sql);
			else {
				conn = getConnection(DBStr);
				if (conn == null)
					return false;
				isSucceed = SqlInsert.insert(conn, sql);
			}
			return isSucceed;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("insert failure");
			return false;
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * ��Ĭ�����ݿ��Ͻ���select����
	 * 
	 * @param sql
	 *            ����select������SQL���
	 * @return ��Ž�����ݵ�List
	 */
	public static List queryNotBind(String sql) {
		return queryNotBind(sql, DBString);
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
	public static List queryNotBind(String sql, String DBStr) {
		Connection conn = null;
		try {
			List list = null;
			if ((DBStr == null) || (DBStr.equals(""))) {
				conn = getConnection();
				list = SqlQuery.findList(conn, sql);
			} else {
				conn = getConnection(DBStr);
				if (conn == null)
					return null;
				list = SqlQuery.findList(conn, sql);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * ��Ĭ�����ݿ��ϲ�ѯָ�������ļ�¼
	 * 
	 * @param sql
	 *            ���ڲ�ѯ������SQL���
	 * @param beginRow
	 *            ��ʼ��¼�ţ���1��ʼ��>=
	 * @param endRow
	 *            ��ֹ��¼�ţ�<=
	 * @return Ԫ��0��Ž�����ݵ�List��Ԫ��1����ܼ�¼����
	 */
	public static Vector queryBindPage(String sql, int beginRow, int endRow) {
		return queryBindPage(sql, beginRow, endRow, DBString);
	}

	/**
	 * ��ָ�����ݿ��ϲ�ѯָ�������ļ�¼
	 * 
	 * @param sql
	 *            ���ڲ�ѯ������SQL���
	 * @param beginRow
	 *            ��ʼ��¼�ţ���1��ʼ��>=
	 * @param endRow
	 *            ��ֹ��¼�ţ�<=
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return Ԫ��0��Ž�����ݵ�List��Ԫ��1����ܼ�¼����
	 */
	public static Vector queryBindPage(String sql, int beginRow, int endRow, String DBStr) {
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ArrayList arraylist;
		Vector val = new Vector();
		try {
			// ʹ��Ĭ�ϵķ���
			// ��ΪOracle���ݿ�ʱ�����List����һ��rownum
			if ((DBStr == null) || (DBStr.equals("")) || (GetDbType(DBStr).equals(GetDbType(null)))) {
				PageVo vo = null;
				if ((DBStr == null) || (DBStr.equals("")))
					vo = SqlQuery.findPage(sql, beginRow, endRow);
				else {
					conn = getConnection(DBStr);
					vo = SqlQuery.findPage(conn, sql, beginRow, endRow);
					closeConnection(conn);
				}
				val.addElement(vo.getList());
				val.addElement(vo.getCount() + "");
				return val;
			}
			// ��ʹ��Ĭ�Ϸ���
			conn = getConnection(DBStr);
			stmt = conn.createStatement();
			stmt.setMaxRows(Config.JDBC_MAX_ROW);
			rs = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int column = metaData.getColumnCount();
			ArrayList list = new ArrayList();
			int index = 0;
			while (rs.next()) {
				if (++index < beginRow)
					continue;
				if (index > endRow)
					break;

				String tmpArray[] = new String[column];
				for (int i = 0; i < column; i++) {
					String name = metaData.getColumnClassName(i + 1);
					if (name.equals("java.sql.Timestamp"))
						tmpArray[i] = DateUtils.dateToString(rs.getTimestamp(i + 1));
					else
						tmpArray[i] = rs.getString(i + 1);
				}

				list.add(tmpArray);
			}
			arraylist = list;
			int rowCount = SqlQuery.getRowCount(conn, sql);
			val.addElement(arraylist);
			val.addElement(rowCount + "");
			return val;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (rs != null)
				JdbcUtils.close(rs, sql);
			if (stmt != null)
				JdbcUtils.close(stmt, sql);
			closeConnection(conn);
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
	public static boolean insertAuditTable(String trySql, String strSql, String dbstr) {

		boolean flag = false;
		Connection conn = getConnection(dbstr);

		try {
			conn.setAutoCommit(false);
			SqlInsert.insert(conn, trySql);
			// �ܲ�����ع�
			conn.rollback();
			try {
				flag = SqlInsert.insert(conn, strSql);
				conn.commit();
				return flag;
			} catch (SQLException e) {
				e.printStackTrace();
				// ��ʱ�������ʧ��
				return flag;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// ��ʽ����岻������������ʱ�����
			return flag;

		} finally {
			closeConnection(conn);
		}
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
	public static int updateAuditTable(String trySql, String strSql, String dbstr) {

		int rows = 0;
		Connection conn = getConnection(dbstr);

		try {
			conn.setAutoCommit(false);
			SqlInsert.insert(conn, trySql);
			// �ܲ�����ع�
			conn.rollback();
			try {
				rows = SqlUpdate.update(conn, strSql);
				conn.commit();
				return rows;
			} catch (SQLException e) {
				e.printStackTrace();
				// ����ʧ��
				return rows;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// ��ʽ����岻�������ٸ���
			return rows;
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * ��Ĭ�����ݿ��Ͻ�������SQL����������һ��ʧ�ܣ���ȫ���ع�
	 * 
	 * @param sArrSql
	 *            ���SQL�������飬Ԫ��0��Ų�������(1����,2�޸�,3ɾ��)��Ԫ��1���SQL���
	 * @return ����0��ʾ�ɹ��� substring(0,1)����1��ʾʧ�ܣ�����substring(1)��ų���ԭ��
	 */
	public static String userJdbcTrsaction(String[][] sArrSql) {
		return userJdbcTrsaction(sArrSql, DBString);
	}

	/**
	 * ��ָ�����ݿ��Ͻ�������SQL����������һ��ʧ�ܣ���ȫ���ع�
	 * 
	 * @param sArrSql
	 *            ���SQL�������飬Ԫ��0��Ų�������(1����,2�޸�,3ɾ��)��Ԫ��1���SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return ����0��ʾ�ɹ��� substring(0,1)����1��ʾʧ�ܣ�����substring(1)��ų���ԭ��
	 */
	public static String userJdbcTrsaction(String[][] sArrSql, String DBStr) {
		int i = 0;
		Connection conn = getConnection(DBStr);
		try {
			if (sArrSql == null && sArrSql.length < 1)
				return "0";

			conn.setAutoCommit(false);
			for (i = 0; i < sArrSql.length; i++) {
				switch (Integer.parseInt(sArrSql[i][1])) {
				case 1: // ����
					SqlInsert.insert(conn, sArrSql[i][0]);
					break;
				case 2: // �޸�
					SqlUpdate.update(conn, sArrSql[i][0]);
					break;
				case 3: // ɾ��
					SqlDelete.delete(conn, sArrSql[i][0]);
					break;
				default:
				}
			}
			conn.commit();
			return "0";
		} catch (SQLException ex) {
			try {
				conn.rollback();
				ex.printStackTrace();
			} catch (SQLException ex2) {
				ex2.printStackTrace();
			}
			return "1�������:[" + sArrSql[i][0] + "] ����ԭ��:[" + ex.toString() + "]";
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * ��Ĭ�����ݿ��ϻ�ȡSelect�ֶνṹ
	 * 
	 * @param sql
	 *            SQL���
	 * @return �ֶνṹList,Ԫ��0Ϊ�ֶ�����Ԫ��1Ϊ�ֶ����ͣ�Ԫ��2Ϊ�ֶγ���
	 */
	public static List GetFieldInfo(String sql) {
		return GetFieldInfo(sql, DBString);
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
	public static List GetFieldInfo(String sql, String DBStr) {
		String DBType = GetDbType(DBStr);
		Connection conn = getConnection(DBStr);
		ArrayList val = new ArrayList();
		try {
			Statement stmt = conn.createStatement();
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
				val.add(v);
			}
			RS.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConnection(conn);
		}
		return val;
	}

	public static String executeProcedure(String sql, String start_time) {
		return executeProcedure(sql, start_time, DBString);
	}

	public static String executeProcedure(String sql, String start_time, String DBStr) {
		CallableStatement cstmt = null;
		String returnVal = "-1";
		java.sql.Connection conn = null;

		try {
			if ((DBStr == null) || (DBStr.equals("")))
				conn = DataSourceUtils.getConnection();
			else
				conn = getConnection(DBStr);
			// //////////////////////////////////////////
			cstmt = conn.prepareCall(sql);
			System.out.print("this time:" + start_time);
			cstmt.setString(1, start_time.trim());
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			cstmt.execute();
			returnVal = String.valueOf(cstmt.getInt(2));
			System.out.print("this:" + returnVal);
		} catch (SQLException se) {
			System.out.println(se.getMessage());
			System.out.println("execute procedure is error");
			returnVal = null;
		} finally {
			closeCallableStatement(cstmt);
			closeConnection(conn);
		}
		return returnVal;
	}

	/**
	 * �ް󶨲�����ֻ��һ������ֵ��select����
	 */
	public static String functionBindOne(String sql) {
		return functionBindOne(sql, DBString);
	}

	public static String functionBindOne(String sql, String DBStr) {
		String sRet = "";
		Connection conn = null;
		try {
			if ((DBStr == null) || (DBStr.equals("")))
				sRet = (String) SqlFunction.find(sql);
			else {
				conn = getConnection(DBStr);
				sRet = (String) SqlFunction.find(conn, sql);
				closeConnection(conn);
			}
		} catch (Exception ex) {
			System.out.println("in functionBindOne," + ex);
		} finally {
			closeConnection(conn);
		}
		return sRet;

	}

	public static int functionBindOneInt(String sql) {
		return functionBindOneInt(sql, DBString);
	}

	public static int functionBindOneInt(String sql, String DBStr) {
		Connection conn = null;
		System.out.println("------>in functionBindOneIn,sql=" + sql);
		int iRet = -1;

		try {
			if ((DBStr == null) || (DBStr.equals("")))
				iRet = Integer.parseInt(SqlFunction.findLong(sql).toString());
			else {
				conn = getConnection(DBStr);
				iRet = Integer.parseInt(SqlFunction.findLong(conn, sql).toString());
				closeConnection(conn);
			}

		} catch (Exception ex) {
			System.out.println("in functionBindOneInt," + ex);
		} finally {
			closeConnection(conn);
		}

		return iRet;
	}

	public static double functionBindOneDouble(String sql, String DBStr) {
		Connection conn = null;
		System.out.println("------>in functionBindOneIn,sql=" + sql);
		double iRet = 0.0;
		Object obj = null;
		try {
			if ((DBStr == null) || (DBStr.equals(""))) {
				obj = SqlFunction.find(sql);
				if (obj != null)
					iRet = Double.parseDouble(obj.toString());
			} else {
				conn = getConnection(DBStr);
				obj = SqlFunction.find(conn, sql);
				if (obj != null)
					iRet = Double.parseDouble(obj.toString());
			}

		} catch (Exception ex) {
			System.out.println("in functionBindOneDouble," + ex);
		} finally {
			closeConnection(conn);
		}

		return iRet;
	}

	/**
	 * �����ҳ
	 * 
	 * @param sql
	 * @param begin
	 * @param end
	 * @return
	 */
	public static List queryBindPageList(String sql, int begin, int end) {
		return queryBindPageList(sql, begin, end, DBString);
	}

	public static List queryBindPageList(String sql, int begin, int end, String DBStr) {
		Connection conn = null;
		PageVo vo = null;
		try {
			if ((DBStr == null) || (DBStr.equals("")))
				vo = SqlQuery.findPage(sql, begin, end);
			else {
				conn = getConnection(DBStr);
				vo = SqlQuery.findPage(conn, sql, begin, end);
				closeConnection(conn);
			}

		} catch (Exception ex) {
			System.out.println("in queryBindPageList," + ex);
		} finally {
			closeConnection(conn);
		}
		List list = vo.getList();
		return list;
	}

	/**
	 * �����ҳ
	 * 
	 * @param sql
	 * @param begin
	 * @param end
	 * @return
	 */
	public static PageVo queryBindPageVo(String sql, int begin, int end) {
		return queryBindPageVo(sql, begin, end, DBString);
	}

	public static PageVo queryBindPageVo(String sql, int begin, int end, String DBStr) {
		PageVo vo = null;
		Connection conn = null;
		try {
			if ((DBStr == null) || (DBStr.equals(""))) {
				conn = getConnection();
				vo = SqlQuery.findPage(conn, sql, begin, end);
			} else {
				conn = getConnection(DBStr);
				vo = SqlQuery.findPage(conn, sql, begin, end);
			}

		} catch (Exception ex) {
			System.out.println("in queryBindPageVo," + ex);
		} finally {
			closeConnection(conn);
		}
		return vo;
	}

	/**
	 * �ر� CallableStatement ����
	 * 
	 * @param cs
	 */
	protected static final void closeCallableStatement(CallableStatement cs) {
		try {
			if (cs != null) {
				cs.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * ��ָ�����ݿ��ϻ�ȡָ���������ֶνṹ,�����������á�
	 * 
	 * @param sql
	 *            SQL���
	 * @param DBStr
	 *            ���ݿⶨ��������Ϊ�գ���ָĬ�ϵ����ݿ�
	 * @return �ֶνṹList,Ԫ��0Ϊ�ֶ�����Ԫ��1Ϊ�ֶ����ͣ�Ԫ��2Ϊ�ֶγ���
	 */
	public static List getTableColumnInfo(String tableName, String DBStr) {
		String sSQL = "select * from  " + tableName + " ttt where 1=2";

		Connection conn = getConnection(DBStr);
		ArrayList val = new ArrayList();
		try {
			Statement stmt = conn.createStatement();
			ResultSet RS = stmt.executeQuery(sSQL);
			ResultSetMetaData rsmd = RS.getMetaData();
			int ColNum = rsmd.getColumnCount();
			for (int i = 1; i <= ColNum; i++) {
				String v[] = new String[3];
				// ����
				v[0] = "" + i;
				// ����
				v[1] = rsmd.getColumnName(i);
				// ������ 0<=>number, 1<=>char, 2<=>date
				int type = rsmd.getColumnType(i);
				switch (type) {
				case Types.NUMERIC:
					v[2] = "0";
					break;
				case Types.DOUBLE:
					v[2] = "0";
					break;
				case Types.CHAR:
					v[2] = "1";
					break;
				case Types.VARCHAR:
					v[2] = "1";
					break;
				case Types.DATE:
					v[2] = "2";
					break;
				default:
					v[2] = "1";
				}
				// ��ע��
				v[3] = rsmd.getColumnLabel(i);
				// ����
				v[1] = rsmd.getColumnTypeName(i);
				int Precision = 0;
				Precision = rsmd.getPrecision(i);
				v[2] = Precision + "";
				val.add(v);
			}
			RS.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConnection(conn);
		}
		return val;
	}

	/** ************ main����,������ ********************************************* */
	public static void main(String[] args) {
	}

}
