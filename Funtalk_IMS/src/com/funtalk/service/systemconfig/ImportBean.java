package com.funtalk.service.systemconfig;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.funtalk.pojo.rightmanage.LocalDataTableCol;
import com.funtalk.common.DataConnection;
import com.funtalk.common.WriteLog;




public class ImportBean extends Thread {
	ArrayList data;

	String Segmentation;

	String url;

	String db;

	String userId;

	String tableName;

	String[] colsName;

	File file;

	String insertSql;

	Connection conn;

	List colsInfo;

	String dateModel;

	String errorFileName;

	public ImportBean() {

	}

	public ImportBean(Connection conn, ArrayList data, String userId,
			String tableName, List colsInfo, List colsList, String dateModel,
			String errorFileName) {

		this.data = data;
		this.conn = conn;
		this.userId = userId;
		this.tableName = tableName;
		String[] colsName = doOrder(colsInfo, colsList);
		this.colsInfo = colsInfo;
		this.insertSql = this.getInsertSql(tableName, colsName);
		this.dateModel = dateModel;
		this.errorFileName = errorFileName;
	}

	public void testT() {

	}

	public String[] doOrder(List cols, List colsList) {

		String[] orderedCols = new String[cols.size()];

		Hashtable ColsHash = new Hashtable();

		for (int i = 0; i < cols.size(); i++) {
			LocalDataTableCol temp = (LocalDataTableCol) cols.get(i);
			ColsHash.put(temp.getColumnseq(), temp.getColumnname());
			//
			if (temp.getEdittype() != null && temp.getEdittype().equals("1")) {
				orderedCols[cols.size() - 1] = temp.getColumnname();
			}
		}

		for (int i = 0; i < colsList.size(); i++) {
			int id = cols.size()
					- Integer.valueOf((String) (colsList.get(i))).intValue();
			orderedCols[i] = (String) ColsHash.get(String.valueOf(id));
		}

		return orderedCols;
	}

	public void run() {
		// TODO Auto-generated method stub
		ArrayList result = bufferInsert(data, dateModel);
		bufferLog((ArrayList) result.get(0), userId, tableName, true);
		bufferLog((ArrayList) result.get(1), userId, tableName, false);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {

	}

	public ArrayList getBufferData(int num, File file) {
		ArrayList dataList = new ArrayList();
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
			java.io.BufferedReader bufferReader = new BufferedReader(fileReader);
			for (int i = 0; i < 100; i++) {

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dataList;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public ArrayList bufferInsert(ArrayList data, String dateModel) {

		ArrayList result = new ArrayList();
		PreparedStatement pStat = null;
		ArrayList successList = new ArrayList();
		ArrayList backList = new ArrayList();
		String[] exceptions = new String[1];

		try {
			pStat = conn.prepareStatement(insertSql.toString());
		} catch (SQLException e1) {
			exceptions[0] = "����ʧ�ܣ�����ԭ�򣺴���PreparedStatementʧ�ܣ�sql������£�"
					+ insertSql.toString();
			for (int i = 0; i < data.size(); i++) {
				backList.add(exceptions);
			}
			result.add(successList);
			result.add(backList);
			return result;
		}

		inserts: for (int i = 0; i < data.size(); i++) {
			String[] oneCol = (String[]) data.get(i);
			
			if (colsInfo.size() != oneCol.length) {
				oneCol[0] = "ʧ��ԭ�򣺸�����¼�ֶ��������ݿⲻһ�£�ע�⣬����ֶ���ϵͳ�ֶ���ӡ�������¼Ϊ��"
						+ oneCol[0];
				backList.add(oneCol);
				continue;
			}
			//����������Ҫ��С������ʱ��һ���ֶ�Ҫ���������λ
			if(tableName.equals("location_code_info")){
				if(oneCol[0].length()!=5){
					oneCol[0] = "ʧ��ԭ��С����location_code_info���е�С�����루location_code���ֶγ���ӦΪ��λ��������¼Ϊ��"
							+ oneCol[0];
					backList.add(oneCol);
					continue inserts;
				}
			}
			if(tableName.equals("boundary_roam")){
				try {
					if(!this.checkData(oneCol)){
						
						oneCol[0] = "ʧ��ԭ�򣺱�����¼��Ч���ݿ��Ѿ��е�Ч��¼�����ܱ����롣����¼Ϊ��"+ oneCol[0];
						backList.add(oneCol);
						continue inserts;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					oneCol[0] = "ʧ��ԭ��checkData() ���������Ա���ԭ�򡣱���¼Ϊ��"+ oneCol[0];
					backList.add(oneCol);
					continue inserts;
				}
			}
			for (int j = 0; j < colsInfo.size(); j++) {
				LocalDataTableCol oneColInfo = (LocalDataTableCol) colsInfo
						.get(j);
				if (oneColInfo.getColumntype().equals("3")) {
					try {
						java.util.Calendar cal = getDate(dateModel, oneCol[j]);
						Timestamp timeStamp = new Timestamp(cal.getTime()
								.getTime());
						timeStamp.setMonth(timeStamp.getMonth() - 1);
						pStat.setTimestamp(j + 1, timeStamp);
					} catch (SQLException sc) {
						oneCol[0] = "ʧ��ԭ��PreparedStatement.setTimestampʱ�����쳣��������¼Ϊ��"
								+ oneCol[0];
						backList.add(oneCol);
						continue inserts;
					} catch (Exception e) {
						oneCol[0] = "ʧ��ԭ�����ڸ�ʽ����ȷ��ʱ���ʽ�ڵ���ʱ��ѡ�Ĳ�һ�¡�������¼Ϊ��"
								+ oneCol[0];
						backList.add(oneCol);
						continue inserts;
					}

				} else {
					try {
						pStat.setString(j + 1, oneCol[j]);
					} catch (SQLException e) {
						oneCol[0] = "ʧ��ԭ��PreparedStatement.setStringʱ�����쳣��������¼Ϊ��"
								+ oneCol[0];
						backList.add(oneCol);
						e.printStackTrace();
						break inserts;
					}
				}
			}
			try {
				int flag = 0;
				flag = pStat.executeUpdate();

				if (flag == 1) {
					successList.add(oneCol);
				} else {
					backList.add(oneCol);
				}
			} catch (SQLException e2) {
				e2.printStackTrace();
				oneCol[0] = "ʧ��ԭ�򣺲������ݿ�ʧ�ܣ�������Υ�������ݵ�Ψһ�ԡ�������¼Ϊ��" + oneCol[0];
				backList.add(oneCol);
			}
		}
		try {
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			successList = new ArrayList();
			backList = new ArrayList();
			exceptions[0] = "����ʧ�ܣ����ݿ�commitʧ�ܣ�";
			for (int i = 0; i < data.size(); i++) {
				backList.add(exceptions);
			}
			result.add(successList);
			result.add(backList);
			return result;
		}
		result.add(successList);
		result.add(backList);

		return result;
	}

	/**
	 * �ϲ���־�ļ�
	 * 
	 * @param dirName
	 * @param fileName
	 * @return
	 */
	public static int unionFile(String dirName, String fileName) {
		int num = 0;
		System.out.println(dirName);
		System.out.println(fileName);
		File dir = new File(dirName);
		if (dir.isDirectory()) {
			String[] fileNames = dir.list();
			File[] errorFiles = dir.listFiles();
			File errorFile = new File(dirName + fileName + ".txt");

			try {
				java.io.FileWriter fw = new FileWriter(errorFile);
				java.io.BufferedWriter bw = new BufferedWriter(fw);
				java.io.FileReader fr = null;
				java.io.BufferedReader br = null;
				for (int i = 0; i < fileNames.length; i++) {
					if (fileNames[i].indexOf(fileName) >= 0) {
						fr = new FileReader(errorFiles[i]);
						br = new BufferedReader(fr);
						while (true) {
							String temp = br.readLine();

							if (temp == null) {
								break;
							}
							bw.write(temp + "\n");
						}
						br.close();
						fr.close();
					}
				}
				bw.close();
				fw.close();
				fr = new FileReader(errorFile);
				br = new BufferedReader(fr);
				while (true) {
					if (br.readLine() == null) {
						break;
					} else {
						num++;
					}

				}
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		} else {
			return 0;
		}
		return num;
	}

	public Calendar getDate(String model, String value) throws Exception {

		int year, month, day, hour, minute, second;
		java.util.Calendar cal = Calendar.getInstance();

		model = model.toUpperCase();
		year = Integer.valueOf(
				value.substring(model.indexOf("YYYY"),
						model.indexOf("YYYY") + 4)).intValue();
		month = Integer.valueOf(
				value.substring(model.indexOf("MM"), model.indexOf("MM") + 2))
				.intValue();
		day = Integer.valueOf(
				value.substring(model.indexOf("DD"), model.indexOf("DD") + 2))
				.intValue();

		if (model.indexOf("HH") > 1) {
			hour = Integer.valueOf(
					value.substring(model.indexOf("HH"),
							model.indexOf("HH") + 2)).intValue();
		} else {
			hour = 0;
		}
		if (model.indexOf("MI") > 1) {
			minute = Integer.valueOf(
					value.substring(model.indexOf("MI"),
							model.indexOf("MI") + 2)).intValue();
		} else {
			minute = 0;
		}

		if (model.indexOf("SS") > 1) {
			second = Integer.valueOf(
					value.substring(model.indexOf("SS"),
							model.indexOf("SS") + 2)).intValue();
		} else {
			second = 0;
		}
		cal.set(year, month, day, hour, minute, second);

		return cal;
	}

	public boolean bufferLog(ArrayList data, String userId, String tableName,
			boolean flag) {
		StringBuffer logInfo = new StringBuffer();
		String logInfoHead = "";
		if (flag) {
			logInfoHead = "��������" + tableName + "�ɹ�insertһ�У���¼���£�";
		} else {
			logInfoHead = "��������" + tableName + "insert��¼ʧ�ܣ���¼���£�";
		}

		try {
			for (int i = 0; i < data.size(); i++) {
				String[] oneCol = (String[]) data.get(i);
				logInfo.append(logInfoHead);
				for (int j = 0; j < oneCol.length; j++) {
					logInfo.append(oneCol[j] + ",");
				}
				logInfo.delete(logInfo.length() - 1, logInfo.length());
				//logInfo.delete(0, logInfo.length());
				WriteLog.dbLog(userId,"C", "��������", "1", logInfo.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeIntoFile(data, errorFileName);
		return true;
	}

	public void writeIntoFile(ArrayList data, String errorFileName) {
		System.out.println(errorFileName);
		File errorFile = new File(errorFileName + ".txt");
		try {
			java.io.FileWriter fw = new FileWriter(errorFile);
			java.io.BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < data.size(); i++) {
				String[] temp = (String[]) data.get(i);
				String aLine = "";
				for (int j = 0; j < temp.length; j++) {
					aLine += temp[j] + ",";
				}
				aLine = aLine.substring(0, aLine.length() - 1);
				bw.write(aLine + "\t\n");
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �õ�insertSql
	 * 
	 * @param tableName
	 *            ����
	 * @param cols
	 *            �ֶ�������
	 * @return insertSql
	 */
	public String getInsertSql(String tableName, String[] cols) {

		StringBuffer insertSql = new StringBuffer();
		insertSql.append("insert into ");
		insertSql.append(tableName);
		insertSql.append(" (");
		for (int i = 0; i < cols.length; i++) {
			insertSql.append(cols[i] + ",");
		}
		insertSql.delete(insertSql.length() - 1, insertSql.length());
		insertSql.append(") values(");
		for (int i = 0; i < cols.length; i++) {
			insertSql.append("?,");
		}
		insertSql.delete(insertSql.length() - 1, insertSql.length());
		insertSql.append(")");

		System.out.println(insertSql.toString());
		return insertSql.toString();
	}

	/**
	 * ����ļ��Ƿ��Ѿ��ϴ��ɹ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @return
	 */
	public boolean isUpLoaded(String fileName) {

		// ����ϴ��ļ���Ŀ¼
		String url = "E:\\����\\h��������Ŀ\\������\\import\\";

		File upLoadFile = new File(url + fileName);

		if (upLoadFile.isFile()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �õ�һ��20λ��id�ţ�ǰ��λ������Ĳ��������Ա�ʾid�����ͣ���12λΪϵͳʱ�侫ȷ���֣������λΪ�����
	 * 
	 * @param type
	 * @return һ��20��id��
	 */
	public static String getFileName(String type) {
		String id = "";
		java.util.Calendar calendar = Calendar.getInstance();

		String year = String.valueOf(calendar.get(calendar.YEAR));
		String month = String.valueOf(calendar.get(calendar.MARCH) + 1);
		String day = String.valueOf(calendar.get(calendar.DAY_OF_MONTH));
		String hour = String.valueOf(calendar.get(calendar.HOUR));
		String minute = String.valueOf(calendar.get(calendar.MINUTE));

		type = format(type, 5);
		month = format(month, 2);
		day = format(day, 2);
		hour = format(hour, 2);
		minute = format(minute, 2);

		double random = Math.random() * 999000;
		// int temp = random/1;
		String ran = String.valueOf(random);
		ran = format(ran, 3);
		id = type + year + month + day + hour + minute + ran;
		return id;
	}

	/**
	 * λ��������ǰ�߲���0������λ��
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String format(String str, int length) {
		int temp = str.length();
		if (temp > length) {
			str = str.substring(0, length);
		} else {
			for (int i = 0; i < length - temp; i++) {

				str = "0" + str;
			}
		}
		return str;
	}
	
	
	/**
	 * ����������������Ա߽����α����ǰ������ݿ����Ƿ����ظ����ݡ�
	 * flag������0��1��ʱ�����ִ�Сд����ʼ��ͬ��Ҳ��Ϊ��ͬһ����¼
	 * @param oneCol
	 * @return
	 */
	private boolean checkData(String[] oneCol)throws Exception{
		
		
		String checkSql = "select * from  boundary_roam " +
				"where visit_prov_code = '"+oneCol[0]+"' and visit_msc_code = '"+oneCol[1]+"' " +
				"and upper(visit_lac_code) = upper('"+oneCol[2]+"') and upper(visit_cell_id) = upper('"+oneCol[3]+"') " +
				"and home_prov_code = '"+oneCol[4]+"' and home_area_code = '"+oneCol[5]+"' " +
				"and flag = '"+oneCol[9]+"' and time_type = '"+oneCol[12]+"' "+
				"and to_char(end_date,'"+dateModel+"') = '"+oneCol[11]+"'";
		
		if(!oneCol[9].equals("0")&&!oneCol[9].equals("1")){
			List temp = DataConnection.queryNotBind(checkSql);
			if(temp.size()>0){
				return false;
			}
		}
		
		
		return true;
	}

	public static void main(String[] args) {

		
		/**
		String tt = "dkdk|kdkdk|kdkdk|dkdk";
		
		String[] dd = tt.split("\\|");
		
		System.out.println(dd.length);
		
		
		/*
		ImportBean importBean = new ImportBean();
		System.out.println(importBean.isUpLoaded("��������1000.TXT"));

		
		 * String url = "E:\\����\\h��������Ŀ\\������\\import\\";
		 * 
		 * 
		 * File upLoadFile = new File(url+"��������1000.TXT"); java.io.FileReader
		 * fileReader; try { fileReader = new FileReader(upLoadFile);
		 * java.io.BufferedReader bufferReader = new BufferedReader(fileReader);
		 * while(true){ String temp = bufferReader.readLine(); if(temp!=null){
		 * System.out.println(temp); }else{ break; } } } catch
		 * (FileNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */

	}

}
