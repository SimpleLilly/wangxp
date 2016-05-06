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
	 * 记录日志
	 * @param result
	 */
	private void log(String result){
		 WriteLog log=new WriteLog();
		 String sql = "insert into t_log values(t_log_seq.nextval,sysdate,'"+userName+"','" + result + "')";
		 log.WriteLogDb(sql);
	}
	
    /**
	 * 依据给定的局数据表的id，查询出表的所有信息，包括表名称、表中文名称、是否能在配置向导中进行配置。
	 * 
	 * @param 局数据表id
	 * @return 数组，为表的信息
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
	 * 依据给定的局数据表的id，查询出表的所有字段，包括字段序号、字段名、字段类型，字段名称（中文），是否是索引，是否参与查询，对应的表，是否参与排序
	 * 
	 * @param 局数据表id
	 * @return 集合，由数组组成，每个数组为每一个字段的信息
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
	 * 产生insert语句
	 * 
	 * @param tableName
	 * @param values
	 * @return
	 */
	public String makeInsertStatement(String tableId, String[] values) {

		// 定义存储sql语句的缓冲对象
		StringBuffer sql = new StringBuffer();
		// 得到表信息
		String[] tableInfo = (String[]) getInfoOfTable(tableId);
		// 得到指定表的所有字段信息
		Vector tableCols = (Vector) getAllColumnsForTable(tableId);
		// 定义插入语句中的语句头
		sql.append(" INSERT INTO " + tableInfo[1] + "(");
		for (int i = 0; i < tableCols.size(); i++) {
			String[] col_info = (String[]) tableCols.elementAt(i);
			sql.append(col_info[2] + ", ");
		}
		sql.delete(sql.length() - 2, sql.length());
		sql.append(") VALUES ( ");
		for (int i = 0; i < tableCols.size(); i++) {
			String[] colInfo = (String[]) tableCols.elementAt(i);
			// 循环判断字段类型,添加字段值到语句中
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
	 * 转换字符串
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
	 * 查询套餐信息
	 * @param favType
	 * @return
	 */
	public HashMap getFavInfo(String favType){

		//搜索fav_index
		String favIndexSql = " select FAV_TYPE,FAV_CLASS,FAV_LEVEL,FAV_PLAN,AFFECT_SERVICE,DISCOUNT_ORDER, "
			+" to_char(BEGIN_DATE,'yyyymmdd'),to_char(END_DATE,'yyyymmdd'),TIME_TYPE,QUERY_TYPE,NOTE "
			+" from fav_index where fav_type='"+favType+"'";
		List favIndex = DataConnection.queryNotBind(favIndexSql,"jf");
		logger.info(" 套餐搜索 favIndex size = "+favIndex.size());
		//得到fav_plan
		String favPlan = "";
		if (favIndex.size()>0){
			favPlan = ((String [])favIndex.get(0))[3];
			logger.info(" 套餐搜索 fav_plan "+favPlan);
		}
		
		//搜索voicefav_cfee_plan		
		String voicefavCfeePlanSql = " select FAV_PLAN,SYSTEM_TYPES,USER_BRANDS,OTHER_BRANDS,AREA_CODES, "
			+" LOCATION_CODES,VPMN_CODES,RELATION_CODES,DATE_CODES,CALL_TYPES,ROAM_TYPES,DIAL_TYPES, "
			+" CHAT_TYPES,FEE_TYPES,USER_TYPES,MIN_VALUE,DISCOUNT_RATE,FREE_TYPE,FREE_INDEX,RELATION_INDEX, "
			+" FREE_PLAN,DEAL_TYPE,RATE_PLAN,NOTE "
			+" from voicefav_cfee_plan "
			+" where fav_plan='"+favPlan+"'";
		List voicefavCfeePlan = DataConnection.queryNotBind(voicefavCfeePlanSql,"jf");
		logger.info(" 套餐搜索 voicefavCfeePlan size = "+voicefavCfeePlan.size());
		//搜索voicefav_lfee_plan			
		String voicefavLfeePlanSql = " select FAV_PLAN,SYSTEM_TYPES,USER_BRANDS,OTHER_BRANDS,AREA_CODES, "
			+" LOCATION_CODES,VPMN_CODES,RELATION_CODES,DATE_CODES,CALL_TYPES,ROAM_TYPES,DIAL_TYPES, "
			+" CHAT_TYPES,FEE_TYPES,USER_TYPES,MIN_VALUE,DISCOUNT_RATE,FREE_TYPE,FREE_INDEX,RELATION_INDEX, "
			+" FREE_PLAN,DEAL_TYPE,RATE_PLAN,NOTE "
			+" from voicefav_lfee_plan "
			+" where fav_plan='"+favPlan+"' ";
		List voicefavLfeePlan = DataConnection.queryNotBind(voicefavLfeePlanSql,"jf");
		logger.info(" 套餐搜索 voicefavLfeePlan size = "+voicefavLfeePlan.size());	
		//搜索datafav_fee_plan				
		String datafavFeePlanSql = " select FAV_PLAN,SYSTEM_TYPE,USER_BRANDS,OTHER_BRANDS,VPMN_CODES, "
			+" RELATION_CODES,DATE_CODES,ADD_CONDITIONS,USER_TYPES,MIN_VALUE,DISCOUNT_RATE,FREE_TYPE, "
			+" FREE_INDEX,RELATION_INDEX,FREE_PLAN,DEAL_TYPE,RATE_PLAN,NOTE "
			+" from datafav_fee_plan "
			+" where fav_plan='"+favPlan+"' ";
		List datafavFeePlan = DataConnection.queryNotBind(datafavFeePlanSql,"jf");	
		logger.info(" 套餐搜索 datafavFeePlan size = "+datafavFeePlan.size());	
		//组装数据
		HashMap hm = new HashMap();
		hm.put("fav_index", favIndex);	
		hm.put("voicefav_cfee_plan", voicefavCfeePlan);
		hm.put("voicefav_lfee_plan", voicefavLfeePlan);
		hm.put("datafav_fee_plan", datafavFeePlan);		
		return hm;
	}
	
	/**
	 * 组装添加语句
	 * @param favIndex
	 * @param cfeePlan
	 * @param lfeePlan
	 * @param datafeePlan
	 * @return
	 */
	public List getInsertList(String[] favIndex,String[] cfeePlan,String[] lfeePlan,String[] datafeePlan){
		List list = new ArrayList();
		
		//去掉空格
		favIndex = FavBean.fieldTrim(favIndex);
		cfeePlan = FavBean.fieldTrim(cfeePlan);
		lfeePlan = FavBean.fieldTrim(lfeePlan);
		datafeePlan = FavBean.fieldTrim(datafeePlan);
		 
		//拼装 insert fav_index 字符串
		if (favIndex != null) {
			String sql = makeInsertStatement("36", favIndex);
			list.add(sql);
			strLog+="fav_index = "+sql.replaceAll("'", "''");
		}
		//拼装 insert voicefav_cfee_plan 字符串
		if (cfeePlan != null) {
			int fieldLenth = 24;
			for (int i=0 ;i<cfeePlan.length/fieldLenth ;i++){
				String[] stemp = new String[fieldLenth];
				for(int j=0 ;j<fieldLenth ;j++){
					if (j == 0) 
						stemp[j] = favIndex[3];//设置fav_plan的值
					else
						stemp[j] = cfeePlan[i*fieldLenth+j];
				}
				String sql = makeInsertStatement("41", stemp);
				list.add(sql);
				strLog+="<br>voicefav_cfee_plan = "+sql.replaceAll("'", "''");
			}
		}
		//拼装 insert voicefav_lfee_plan 字符串
		if (lfeePlan != null) {
			int fieldLenth = 24;
			for (int i=0 ;i<lfeePlan.length/fieldLenth ;i++){
				String[] stemp = new String[fieldLenth];
				for(int j=0 ;j<fieldLenth ;j++){
					if (j == 0) 
						stemp[j] = favIndex[3];//设置fav_plan的值
					else
						stemp[j] = lfeePlan[i*fieldLenth+j];
				}
				String sql = makeInsertStatement("42", stemp);
				list.add(sql);
				strLog+="<br>voicefav_lfee_plan = "+sql.replaceAll("'", "''");
			}
		}
		//拼装 insert datafav_fee_plan 字符串
		if (datafeePlan != null) {
			int fieldLenth = 18;
			for (int i=0 ;i<datafeePlan.length/fieldLenth ;i++){
				String[] stemp = new String[fieldLenth];
				for(int j=0 ;j<fieldLenth ;j++){
					if (j == 0) 
						stemp[j] = favIndex[3];//设置fav_plan的值
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
	 * 套餐添加
	 * @param favIndex
	 * @param cfeePlan
	 * @param lfeePlan
	 * @param datafeePlan
	 * @return
	 */
	public String insert(String[] favIndex,String[] cfeePlan,String[] lfeePlan,String[] datafeePlan){
		List list = getInsertList(favIndex,cfeePlan,lfeePlan,datafeePlan);
		//执行数据库批量添加操作，事务功能
        String sqlAr[][] = new String[list.size()][2];
		for (int i=0 ;i<list.size() ;i++){
    		sqlAr[i][0] = (String)list.get(i);
    		sqlAr[i][1] = "1";
		}
		String strResult = DataConnection.userJdbcTrsaction(sqlAr,"jf");
		log(" 该用户添加了套餐 fav_type ="+favIndex[0]+" fav_plan = "+favIndex[3]+"<br>"+strResult+"<br>"+strLog);
		return strResult;
	}
	
	/**
	 * 组装删除语句
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
	 * 删除功能
	 * @param favIndex
	 * @return
	 */
	public String delete(String[] favIndex){
		List list = getDeleteList(favIndex);
		//执行数据库批量删除操作，事务功能
        String sqlAr[][] = new String[list.size()][2];
		for (int i=0 ;i<list.size() ;i++){
    		sqlAr[i][0] = (String)list.get(i);
    		sqlAr[i][1] = "3";
		}
		String strResult = DataConnection.userJdbcTrsaction(sqlAr,"jf");
		log(" 该用户删除了套餐 fav_type ="+favIndex[0]+" fav_plan = "+favIndex[3]+"<br>"+strResult+"<br>"+strLog);
		return strResult;
	}
	
	/**
	 * 更新模块，逻辑是根据fav_type,fav_plan 先删除以前的信息，然后添加新信息
	 * @param favIndex
	 * @param cfeePlan
	 * @param lfeePlan
	 * @param datafeePlan
	 * @return
	 */
	public String update(String[] favIndex,String[] cfeePlan,String[] lfeePlan,String[] datafeePlan){
		List list = new ArrayList();
		//删除原有的记录
		List deletelist = getDeleteList(favIndex);
		for (int i=0;i<deletelist.size(); i++){
			list.add(deletelist.get(i));
		}
		//添加新纪录
		List insertlist = getInsertList(favIndex,cfeePlan,lfeePlan,datafeePlan);
		for (int i=0;i<insertlist.size(); i++){
			list.add(insertlist.get(i));
		}
		//执行数据库批量修改操作，事务功能
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
		log(" 该用户修改了套餐 fav_type = "+favIndex[0]+" fav_plan = "+favIndex[3]+"<br>"+strResult+"<br>"+strLog);
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
