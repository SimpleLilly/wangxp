package com.funtalk.common;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.funtalk.pojo.rightmanage.IlogDAO;
import com.funtalk.pojo.rightmanage.Log;
import com.funtalk.pojo.rightmanage.User;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

public class WriteLog {
	/**
	 * ���캯����Ĭ�ϣ� �������ļ��ж�ȡ��Ӧ����־�ļ�·��
	 */
	public WriteLog() {

	}

	public String getCurrentTime() {
		String cur_time = "";
		String cur_year = "";
		String cur_mon = "";
		String cur_day = "";
		String cur_hour = "";
		String cur_minute = "";
		String cur_second = "";
		java.util.Calendar cld = java.util.Calendar.getInstance();
		cur_year = (new Integer(cld.get(Calendar.YEAR)).toString());
		cur_mon = (new Integer(cld.get(Calendar.MONTH) + 1).toString());
		cur_day = (new Integer(cld.get(Calendar.DATE)).toString());
		cur_hour = (new Integer(cld.get(Calendar.HOUR_OF_DAY)).toString());
		cur_minute = (new Integer(cld.get(Calendar.MINUTE)).toString());
		cur_second = (new Integer(cld.get(Calendar.SECOND)).toString());
		if (cur_mon.length() == 1)
			cur_mon = "0" + cur_mon;
		if (cur_day.length() == 1)
			cur_day = "0" + cur_day;
		if (cur_hour.length() == 1)
			cur_hour = "0" + cur_hour;
		if (cur_minute.length() == 1)
			cur_minute = "0" + cur_minute;
		if (cur_second.length() == 1)
			cur_second = "0" + cur_second;
		cur_time = cur_year + "-" + cur_mon + "-" + cur_day + " " + cur_hour
				+ ":" + cur_minute + ":" + cur_second;
		return cur_time;
	}

	public String getCurrentDay() {
		String cur_time = "";
		String cur_year = "";
		String cur_mon = "";
		String cur_day = "";
		java.util.Calendar cld = java.util.Calendar.getInstance();
		cur_year = (new Integer(cld.get(Calendar.YEAR)).toString());
		cur_mon = (new Integer(cld.get(Calendar.MONTH) + 1).toString());
		cur_day = (new Integer(cld.get(Calendar.DATE)).toString());
		if (cur_mon.length() == 1)
			cur_mon = "0" + cur_mon;
		if (cur_day.length() == 1)
			cur_day = "0" + cur_day;
		cur_time = cur_year + cur_mon + cur_day;
		return cur_time;
	}

	/**
	 * д��־��Ϣ�����ݿ�����Ӧ�ı��� �� �˷�����Ҫ�����ݿ��н�����־��ͬʱ��Ӧ�ô������һЩ�Ĳ������������Ա�ȵ�
	 * 
	 * @param String
	 *            type
	 * @return bool value �� true д�ɹ�
	 */
	public boolean WriteLogDb(String strSql) {
		try {
			// return dbconn.insertNotBind(ToolsOfSystem.GBKToISO(strSql) );
			return DataConnection.insertNotBind((strSql));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return false;

	}

	/**
	 * 
	 * @param operateType
	 *            �������� :CURD
	 * @param operateSrc
	 *            ��������:�����ݡ���
	 * @param operateDest
	 *            �������: 1���ɹ�;0��ʧ��
	 * @param memo
	 *            ��ע
	 */
	public static void dbLog(String operateType, String operateSrc,
			String operateDest, String memo) {
		
		String ipInfo="";

		try {
			
			ipInfo = " IPΪ:"+ ServletActionContext.getRequest().getRemoteAddr() + " �������Ϊ: "+ ServletActionContext.getRequest().getRemoteHost();
			
			ActionContext ctx = ActionContext.getContext();		
			Map session = ctx.getSession();
			User currentUser = (User) session.get("currentUser");
			IlogDAO logDao = (IlogDAO) SpringContextUtil.getBean("logDao");
			
			Log log = new Log();
			log.setUsername(currentUser.getUsername());
			log.setOperateTime(new Date());
			log.setOperateType(operateType);
			log.setOperateSrc(operateSrc);
			log.setOperateDest(operateDest);
			log.setMemo(ipInfo + "  -  " + memo);
			logDao.save(log);
		} catch (Exception e) {
			
			System.out.println("in dbLog, this error1: " + e);
		}
		
	}

	/**
	 * 
	 * @param operateType
	 *            �������� :CURD
	 * @param operateSrc
	 *            ��������:�����ݡ���
	 * @param operateDest
	 *            �������: 1���ɹ�;0��ʧ��
	 * @param memo
	 *            ��ע
	 */
	public static void dbLog(String username, String operateType,
			String operateSrc, String operateDest, String memo) {
		try {
			IlogDAO logDao = (IlogDAO) SpringContextUtil.getBean("logDao");
			String ipInfo = " IPΪ:"
					+ ServletActionContext.getRequest().getRemoteAddr()
					+ "   �������Ϊ: "
					+ ServletActionContext.getRequest().getRemoteHost();
			Log log = new Log();

			log.setUsername(username);
			log.setOperateTime(new Date());
			log.setOperateType(operateType);
			log.setOperateSrc(operateSrc);
			log.setOperateDest(operateDest);
			log.setMemo(ipInfo + "  -  " + memo);
			logDao.save(log);
		} catch (Exception e) {
			System.out.println("in dbLog, error2: " + e);
		}
	}

	/**
	 * 
	 * @param operateType
	 *            �������� :CURD
	 * @param operateSrc
	 *            ��������:�����ݡ���
	 * @param operateDest
	 *            �������: 1���ɹ�;0��ʧ��
	 * @param memo
	 *            ��ע
	 */
	public static void dbLog(String username, String operateType,
			String operateSrc, String operateDest, String ipInfo, String memo) {
		try {
			IlogDAO logDao = (IlogDAO) SpringContextUtil.getBean("logDao");
			// String ipInfo ="
			// IPΪ:"+ServletActionContext.getRequest().getRemoteAddr()+" �������Ϊ:
			// "+ServletActionContext.getRequest().getRemoteHost();
			Log log = new Log();

			log.setUsername(username);
			log.setOperateTime(new Date());
			log.setOperateType(operateType);
			log.setOperateSrc(operateSrc);
			log.setOperateDest(operateDest);
			log.setMemo(ipInfo + "  -  " + memo);
			logDao.save(log);
		} catch (Exception e) {
			System.out.println("in dbLog, error2: " + e);
		}
	}

	/**
	 * 
	 * @param operateType
	 *            �������� :CURD
	 * @param operateSrc
	 *            ��������:�����ݡ���
	 * @param operateDest
	 *            �������: 1���ɹ�;0��ʧ��
	 * @param memo
	 *            ��ע
	 */
	public static void dwrLog(String username, String operateType,
			String operateSrc, String operateDest, String ipInfo, String memo) {
		try {
			IlogDAO logDao = (IlogDAO) SpringContextUtil.getBean("logDao");
			// String ipInfo ="
			// IPΪ:"+ServletActionContext.getRequest().getRemoteAddr()+" �������Ϊ:
			// "+ServletActionContext.getRequest().getRemoteHost();
			Log log = new Log();
			log.setUsername(username);
			log.setOperateTime(new Date());
			log.setOperateType(operateType);
			log.setOperateSrc(operateSrc);
			log.setOperateDest(operateDest);
			log.setMemo(ipInfo + "  -  " + memo);
			logDao.save(log);
		} catch (Exception e) {
			System.out.println("in dbLog, error3: " + e);
		}
	}


}