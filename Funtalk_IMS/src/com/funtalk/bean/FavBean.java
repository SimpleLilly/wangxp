package com.funtalk.bean;

import java.util.*;
import com.funtalk.common.DataConnection;
import com.funtalk.common.WriteLog;
import org.apache.log4j.*;

public class FavBean {
	
	private static Logger logger = Logger.getLogger(FavBean.class);
	private String userName = "";
	private String strLog = "";
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public String getUserName (){
		return userName;
	}
	
	/**
	 * ��¼��־
	 * @param result
	 */
	private void log(String result){
		 WriteLog log=new WriteLog();
		 String sql = "insert into t_log values(t_log_seq.nextval,sysdate,'"+userName+"','" + result + "')";
		 log.WriteLogDb(sql);
	}
	
    /**
	 * ���ݸ����ľ����ݱ��id����ѯ�����������Ϣ�����������ơ����������ơ��Ƿ������������н������á�
	 * 
	 * @param �����ݱ�id
	 * @return ���飬Ϊ�����Ϣ
	 */
	public String[] getInfoOfTable(String tableid) {
		String[] returnVal = new String[5];
		String strSql = " select TABLEID , TABLENAME , TABLECOMMENT , ISCONFIG , ISHELP from T_LOCAL_DATA_TABLES " + " where TABLEID = " + tableid;
		try {
			List al = (List) DataConnection.queryNotBind(strSql,"jf");
			if (!al.isEmpty() && al.size() > 0) {
				returnVal = (String[]) al.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnVal = null;
		}
		return returnVal;
	}

	/**
	 * ���ݸ����ľ����ݱ��id����ѯ����������ֶΣ������ֶ���š��ֶ������ֶ����ͣ��ֶ����ƣ����ģ����Ƿ����������Ƿ�����ѯ����Ӧ�ı��Ƿ��������
	 * 
	 * @param �����ݱ�id
	 * @return ���ϣ���������ɣ�ÿ������Ϊÿһ���ֶε���Ϣ
	 */
	public Vector getAllColumnsForTable(String tableid) {
		Vector returnVal = new Vector();
		String strSql = " select '', COLUMNSEQ , COLUMNNAME ," + " COLUMNTYPE , COLUMNCOMMENT , ISINDEX , ISQUERY ,"
				+ " CONFIGRATION , LINKTABLEID , ISORDER , STYLE , ISNULL , ISMUTI,HELPID,DEFAULTVALUE from T_LOCAL_DATA_TABLES_COLS " + " where TABLEID = "
				+ tableid + " order by COLUMNSEQ ";
		try {
			List al = (List) DataConnection.queryNotBind(strSql,"jf");
			if (!al.isEmpty() && al.size() > 0) {
				for (int i = 0; i < al.size(); i++) {
					String[] h_record = null;
					h_record = (String[]) al.get(i);
					returnVal.addElement(h_record);
				}
			}
		} catch (Exception e) {
			logger.error("Error In getAllColumnsForTable() : " + e.getMessage());
			returnVal = null;
		}
		return returnVal;
	}

	/**
	 * ����insert���
	 * 
	 * @param tableName
	 * @param values
	 * @return
	 */
	public String makeInsertStatement(String tableId, String[] values) {

		// ����洢sql���Ļ������
		StringBuffer sql = new StringBuffer();
		// �õ�����Ϣ
		String[] tableInfo = (String[]) getInfoOfTable(tableId);
		// �õ�ָ����������ֶ���Ϣ
		Vector tableCols = (Vector) getAllColumnsForTable(tableId);
		// �����������е����ͷ
		sql.append(" INSERT INTO " + tableInfo[1] + "(");
		for (int i = 0; i < tableCols.size(); i++) {
			String[] col_info = (String[]) tableCols.elementAt(i);
			sql.append(col_info[2] + ", ");
		}
		sql.delete(sql.length() - 2, sql.length());
		sql.append(") VALUES ( ");
		for (int i = 0; i < tableCols.size(); i++) {
			String[] colInfo = (String[]) tableCols.elementAt(i);
			// ѭ���ж��ֶ�����,����ֶ�ֵ�������
			switch (Integer.parseInt(colInfo[3])) {
			case 1:
				sql.append(transStr2OracleNullStr(values[i]) + " , ");
				break;
			case 2:
				sql.append("'" + values[i] + "' , ");
				break;
			case 3:
				sql.append("to_date('" + values[i] + "','yyyymmdd HH24:MI:SS') , ");
				break;
			}
		}
		String insertSql = sql.substring(0, sql.length() - 2) + ")";
		logger.debug("insertSql===" + insertSql);
		return insertSql;
	}

	/**
	 * ת���ַ���
	 * 
	 * @param sTempStr
	 * @return
	 */
	public String transStr2OracleNullStr(String sTempStr) {
		if (sTempStr == null || sTempStr.equals("") || sTempStr.trim().length() == 0) {
			return "null";
		} else {
			return sTempStr.trim();
		}
	}
	
	/**
	 * ��ѯ�ײ���Ϣ
	 * @param favType
	 * @return
	 */
	public HashMap getFavInfo(String favType){

		//����fav_index
		String favIndexSql = " select FAV_TYPE,FAV_CLASS,FAV_LEVEL,FAV_PLAN,AFFECT_SERVICE,DISCOUNT_ORDER, "
			+" to_char(BEGIN_DATE,'yyyymmdd'),to_char(END_DATE,'yyyymmdd'),TIME_TYPE,QUERY_TYPE,NOTE "
			+" from fav_index where fav_type='"+favType+"'";
		List favIndex = DataConnection.queryNotBind(favIndexSql,"jf");
		logger.info(" �ײ����� favIndex size = "+favIndex.size());
		//�õ�fav_plan
		String favPlan = "";
		if (favIndex.size()>0){
			favPlan = ((String [])favIndex.get(0))[3];
			logger.info(" �ײ����� fav_plan "+favPlan);
		}
		
		//����voicefav_cfee_plan		
		String voicefavCfeePlanSql = " select FAV_PLAN,SYSTEM_TYPES,USER_BRANDS,OTHER_BRANDS,AREA_CODES, "
			+" LOCATION_CODES,VPMN_CODES,RELATION_CODES,DATE_CODES,CALL_TYPES,ROAM_TYPES,DIAL_TYPES, "
			+" CHAT_TYPES,FEE_TYPES,USER_TYPES,MIN_VALUE,DISCOUNT_RATE,FREE_TYPE,FREE_INDEX,RELATION_INDEX, "
			+" FREE_PLAN,DEAL_TYPE,RATE_PLAN,NOTE "
			+" from voicefav_cfee_plan "
			+" where fav_plan='"+favPlan+"'";
		List voicefavCfeePlan = DataConnection.queryNotBind(voicefavCfeePlanSql,"jf");
		logger.info(" �ײ����� voicefavCfeePlan size = "+voicefavCfeePlan.size());
		//����voicefav_lfee_plan			
		String voicefavLfeePlanSql = " select FAV_PLAN,SYSTEM_TYPES,USER_BRANDS,OTHER_BRANDS,AREA_CODES, "
			+" LOCATION_CODES,VPMN_CODES,RELATION_CODES,DATE_CODES,CALL_TYPES,ROAM_TYPES,DIAL_TYPES, "
			+" CHAT_TYPES,FEE_TYPES,USER_TYPES,MIN_VALUE,DISCOUNT_RATE,FREE_TYPE,FREE_INDEX,RELATION_INDEX, "
			+" FREE_PLAN,DEAL_TYPE,RATE_PLAN,NOTE "
			+" from voicefav_lfee_plan "
			+" where fav_plan='"+favPlan+"' ";
		List voicefavLfeePlan = DataConnection.queryNotBind(voicefavLfeePlanSql,"jf");
		logger.info(" �ײ����� voicefavLfeePlan size = "+voicefavLfeePlan.size());	
		//����datafav_fee_plan				
		String datafavFeePlanSql = " select FAV_PLAN,SYSTEM_TYPE,USER_BRANDS,OTHER_BRANDS,VPMN_CODES, "
			+" RELATION_CODES,DATE_CODES,ADD_CONDITIONS,USER_TYPES,MIN_VALUE,DISCOUNT_RATE,FREE_TYPE, "
			+" FREE_INDEX,RELATION_INDEX,FREE_PLAN,DEAL_TYPE,RATE_PLAN,NOTE "
			+" from datafav_fee_plan "
			+" where fav_plan='"+favPlan+"' ";
		List datafavFeePlan = DataConnection.queryNotBind(datafavFeePlanSql,"jf");	
		logger.info(" �ײ����� datafavFeePlan size = "+datafavFeePlan.size());	
		//��װ����
		HashMap hm = new HashMap();
		hm.put("fav_index", favIndex);	
		hm.put("voicefav_cfee_plan", voicefavCfeePlan);
		hm.put("voicefav_lfee_plan", voicefavLfeePlan);
		hm.put("datafav_fee_plan", datafavFeePlan);		
		return hm;
	}
	
	/**
	 * ��װ������
	 * @param favIndex
	 * @param cfeePlan
	 * @param lfeePlan
	 * @param datafeePlan
	 * @return
	 */
	public List getInsertList(String[] favIndex,String[] cfeePlan,String[] lfeePlan,String[] datafeePlan){
		List list = new ArrayList();
		
		//ȥ���ո�
		favIndex = FavBean.fieldTrim(favIndex);
		cfeePlan = FavBean.fieldTrim(cfeePlan);
		lfeePlan = FavBean.fieldTrim(lfeePlan);
		datafeePlan = FavBean.fieldTrim(datafeePlan);
		 
		//ƴװ insert fav_index �ַ���
		if (favIndex != null) {
			String sql = makeInsertStatement("36", favIndex);
			list.add(sql);
			strLog+="fav_index = "+sql.replaceAll("'", "''");
		}
		//ƴװ insert voicefav_cfee_plan �ַ���
		if (cfeePlan != null) {
			int fieldLenth = 24;
			for (int i=0 ;i<cfeePlan.length/fieldLenth ;i++){
				String[] stemp = new String[fieldLenth];
				for(int j=0 ;j<fieldLenth ;j++){
					if (j == 0) 
						stemp[j] = favIndex[3];//����fav_plan��ֵ
					else
						stemp[j] = cfeePlan[i*fieldLenth+j];
				}
				String sql = makeInsertStatement("41", stemp);
				list.add(sql);
				strLog+="<br>voicefav_cfee_plan = "+sql.replaceAll("'", "''");
			}
		}
		//ƴװ insert voicefav_lfee_plan �ַ���
		if (lfeePlan != null) {
			int fieldLenth = 24;
			for (int i=0 ;i<lfeePlan.length/fieldLenth ;i++){
				String[] stemp = new String[fieldLenth];
				for(int j=0 ;j<fieldLenth ;j++){
					if (j == 0) 
						stemp[j] = favIndex[3];//����fav_plan��ֵ
					else
						stemp[j] = lfeePlan[i*fieldLenth+j];
				}
				String sql = makeInsertStatement("42", stemp);
				list.add(sql);
				strLog+="<br>voicefav_lfee_plan = "+sql.replaceAll("'", "''");
			}
		}
		//ƴװ insert datafav_fee_plan �ַ���
		if (datafeePlan != null) {
			int fieldLenth = 18;
			for (int i=0 ;i<datafeePlan.length/fieldLenth ;i++){
				String[] stemp = new String[fieldLenth];
				for(int j=0 ;j<fieldLenth ;j++){
					if (j == 0) 
						stemp[j] = favIndex[3];//����fav_plan��ֵ
					else
						stemp[j] = datafeePlan[i*fieldLenth+j];
				}
				String sql = makeInsertStatement("43", stemp);
				list.add(sql);
				strLog+="<br>datafav_fee_plan = "+sql.replaceAll("'", "''");
			}
		}
		
		return list;
	}
	
	/**
	 * �ײ����
	 * @param favIndex
	 * @param cfeePlan
	 * @param lfeePlan
	 * @param datafeePlan
	 * @return
	 */
	public String insert(String[] favIndex,String[] cfeePlan,String[] lfeePlan,String[] datafeePlan){
		List list = getInsertList(favIndex,cfeePlan,lfeePlan,datafeePlan);
		//ִ�����ݿ�������Ӳ�����������
        String sqlAr[][] = new String[list.size()][2];
		for (int i=0 ;i<list.size() ;i++){
    		sqlAr[i][0] = (String)list.get(i);
    		sqlAr[i][1] = "1";
		}
		String strResult = DataConnection.userJdbcTrsaction(sqlAr,"jf");
		log(" ���û�������ײ� fav_type ="+favIndex[0]+" fav_plan = "+favIndex[3]+"<br>"+strResult+"<br>"+strLog);
		return strResult;
	}
	
	/**
	 * ��װɾ�����
	 * @param favIndex
	 * @return
	 */
	public List getDeleteList(String[] favIndex){
		List list = new ArrayList();
		
		String deleteFavIndex = "delete from fav_index where fav_type ='"+favIndex[0]+"'";
		logger.info("delete= "+deleteFavIndex);
		list.add(deleteFavIndex);
		strLog+="<br>deleteFavIndex = "+deleteFavIndex.replaceAll("'", "''");
		
		String deleteCfeePlan = "delete from voicefav_cfee_plan where fav_plan ='"+favIndex[3]+"'";
		logger.info("delete= "+deleteCfeePlan);
		list.add(deleteCfeePlan);
		strLog+="<br>deleteCfeePlan = "+deleteCfeePlan.replaceAll("'", "''");
		
		String deleteLfeePlan = "delete from voicefav_lfee_plan where fav_plan ='"+favIndex[3]+"'";
		logger.info("delete= "+deleteLfeePlan);		
		list.add(deleteLfeePlan);
		strLog+="<br>deleteLfeePlan = "+deleteLfeePlan.replaceAll("'", "''");
		
		String deleteDfeePlan = "delete from datafav_fee_plan where fav_plan ='"+favIndex[3]+"'";
		logger.info("delete= "+deleteDfeePlan);			
		list.add(deleteDfeePlan);
		strLog+="<br>deleteDfeePlan = "+deleteDfeePlan.replaceAll("'", "''");
		
		return list;
	}
	
	/**
	 * ɾ������
	 * @param favIndex
	 * @return
	 */
	public String delete(String[] favIndex){
		List list = getDeleteList(favIndex);
		//ִ�����ݿ�����ɾ��������������
        String sqlAr[][] = new String[list.size()][2];
		for (int i=0 ;i<list.size() ;i++){
    		sqlAr[i][0] = (String)list.get(i);
    		sqlAr[i][1] = "3";
		}
		String strResult = DataConnection.userJdbcTrsaction(sqlAr,"jf");
		log(" ���û�ɾ�����ײ� fav_type ="+favIndex[0]+" fav_plan = "+favIndex[3]+"<br>"+strResult+"<br>"+strLog);
		return strResult;
	}
	
	/**
	 * ����ģ�飬�߼��Ǹ���fav_type,fav_plan ��ɾ����ǰ����Ϣ��Ȼ���������Ϣ
	 * @param favIndex
	 * @param cfeePlan
	 * @param lfeePlan
	 * @param datafeePlan
	 * @return
	 */
	public String update(String[] favIndex,String[] cfeePlan,String[] lfeePlan,String[] datafeePlan){
		List list = new ArrayList();
		//ɾ��ԭ�еļ�¼
		List deletelist = getDeleteList(favIndex);
		for (int i=0;i<deletelist.size(); i++){
			list.add(deletelist.get(i));
		}
		//����¼�¼
		List insertlist = getInsertList(favIndex,cfeePlan,lfeePlan,datafeePlan);
		for (int i=0;i<insertlist.size(); i++){
			list.add(insertlist.get(i));
		}
		//ִ�����ݿ������޸Ĳ�����������
        String sqlAr[][] = new String[list.size()][2];
		for (int i=0 ;i<list.size() ;i++){
			String temp = (String)list.get(i);
    		sqlAr[i][0] = temp;
    		if (temp.indexOf("INSERT")>=0)
    			sqlAr[i][1] = "1";
    		else if (temp.indexOf("delete")>=0)
    			sqlAr[i][1] = "3";
		}
		String strResult = DataConnection.userJdbcTrsaction(sqlAr,"jf");
		log(" ���û��޸����ײ� fav_type = "+favIndex[0]+" fav_plan = "+favIndex[3]+"<br>"+strResult+"<br>"+strLog);
		return strResult;
	}
	
	public static String[] fieldTrim(String[] field){
		if (field != null){
			for(int i=0; i<field.length; i++){
				field[i] = field[i].trim();
			}
		}
		return field;
	}


}
