/**
 * <p>Title:OperTableBean.java</p>
 * <p>Description: </p>
 * <p>Company: si-tech</p>
 * @author xuyadong
 * May 13, 2008
 */
package com.funtalk.service.opertable;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.funtalk.common.DBConnection;
import com.funtalk.common.DCIConnection;
import com.funtalk.common.Page;
import com.funtalk.common.SpringContextUtil;
import com.funtalk.common.ToolsOfSystem;
import com.funtalk.common.WriteLog;
import com.funtalk.pojo.rightmanage.LocalDataTable;
import com.funtalk.pojo.rightmanage.LocalDataTableCol;


public class OperTableBean {

	private Log logger = LogFactory.getLog(OperTableBean.class);
	
	private LocalDataTable localDataTable;

	private List localDataTableCols;
	
	private DCIConnection DCI = new DCIConnection();
	
	private DBConnection dbConnection = (DBConnection)SpringContextUtil.getBean("DBConnection");
	
	private static final String AUDIT_TABLE = "2";
	
	public Page getAllLocalDataByQuery(String tableid, String[] value, int start,int pageSize, String sort, String dir) {
		
        // 得到表信息
        localDataTable = getLocalDataTable(tableid);
        // 得到指定表的所有字段信息
        localDataTableCols = getlocalDataTableCols(tableid);
        
        String strSql = null;
        
        if (value != null)
        {
        	if ("DCI".equals(localDataTable.getDatasource())){
        		strSql = makeDCISelectQueryStatement(value);
        	}else{
        		strSql = makeSelectQueryStatement(value);
        	}
        }
        else
        {
        	if ("DCI".equals(localDataTable.getDatasource())){
        		strSql = makeDCISelectAllStatementForQuery();
        	}else{
        		strSql = makeSelectAllStatement();
        	}
        }
        
        
        Page page = new Page();
        try
        {
            if (strSql != null && !strSql.equals(""))
            {
            	if ("DCI".equals(localDataTable.getDatasource())){//操作内存数据库
            		List list =DCI.select(strSql);
            		page.setRowCount(list.size());
            		if (start>list.size()) start = list.size();
            		int end = start + pageSize;
            		if (end>list.size()) end = list.size();
            		page.setList(list.subList(start, end));
            	}
            	else
            	{
                	if (sort!=null && dir!=null){
                		strSql += " order by "+sort+" "+dir;
                	}
                	page = dbConnection.queryBindPageVo(strSql, start, pageSize, localDataTable.getDatasource());
            	}
            }
        }
        catch (Exception e)
        {
            logger.error("Error In getAllLocalData() : " + e.getMessage());

            page.setList(new ArrayList());

        }
        return page;
	}
	
    /**
     * 执行局数据表记录的查询操作，此方法返回局数据表中的所有记录。
     * @param 表id int 类型
     * @return 返回集合，如果在查询过程中异常，返回null值
     */
    public List getAllLocalData(String tableid) 
    {
    	List list = null;
        // 得到表信息
        localDataTable = getLocalDataTable(tableid);
        // 得到指定表的所有字段信息
        localDataTableCols = getlocalDataTableCols(tableid);
        
        String strSql = makeSelectAllStatement();

        logger.info("------>in getAllLocalData,select all statement = [ " + strSql + "]");

        try
        {

        	list = dbConnection.queryNotBind(strSql, localDataTable.getDatasource());
        }
        catch (Exception e)
        {
            logger.error("Error In getAllLocalData() : " + e.getMessage());
        }
        return list;
    }
	
    /**
     * 产生局数据表记录的查询sql语句。
     * @param 表id ， String 类型
     * @return 返回产生的sql语句
     */
    private String makeSelectAllStatement() 
    {
        //配置为2的为需要审核的表
        if(localDataTable.getIsconfig().equals(AUDIT_TABLE))
            return "select * from ("+makeSelectAllStatementForQuery() + 
            " union all" + makeSelectAllStatementForQuery("0")+")";// + 
            //getOrderString();
        else    
        	//从生成的语句中去除掉最后一个字符，即“AND”后，返回，函数执行完毕
            return makeSelectAllStatementForQuery(); //+ getOrderString();
    }
	/**
     * 产生局数据表记录的查询sql语句。
     * @param 表id ， String 类型
     * @return 返回产生的sql语句
     */
    private String makeSelectAllStatementForQuery() 
    {
    	return makeSelectAllStatementForQuery(null);
    }
    
    /**
     * 产生局数据表记录的查询sql语句。
     * @param 表id ， String 类型
     * @return 返回产生的sql语句
     */
    private String makeSelectAllStatementForQuery(String auditflag) {

        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str = new StringBuffer();

        // 定义插入语句中的语句头
        sql_str.append(" SELECT ");

        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段

            for (int i = 0; i < localDataTableCols.size(); i++)
            {

            	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);

                // 循环判断字段类型,添加字段值到查询语句中
                switch (Integer.parseInt(col_info.getColumntype()))
                {

                    case 1:
                    case 2:
                        sql_str.append(col_info.getColumnname() + " , ");
                        break;
                    case 3:
                        sql_str.append("to_char(" + col_info.getColumnname() +
                                       ",'yyyymmdd HH24:MI:SS') , ");
                        break;
                }

            }

        }

        String tableName = "";
        String auditString = "";
        if(localDataTable.getIsconfig().equals(AUDIT_TABLE)){
            if(auditflag == null || auditflag.equals("") || auditflag.equals("1")){
            	tableName = localDataTable.getTablename();
            	auditString =",1 ";
            }
            else{
            	tableName = localDataTable.getTablename() + "_TEMP";	
            	auditString =",0 ";
            }
        }else{
        	tableName = localDataTable.getTablename();
        }

        // 从生成的语句中去除掉最后一个字符，即“AND”后，返回，函数执行完毕
        return sql_str.substring(0, sql_str.length() - 2) + auditString +" FROM " +        
        tableName;
    }    
    /**
     */
    private String makeSelectQueryStatement(String[] values) {
               
        // 定义存储sql语句中where子句的缓冲对象
        StringBuffer sql_where = new StringBuffer();

        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段
            for (int i = 0, j = 0; i < localDataTableCols.size(); i++)
            {
            	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);
                if (col_info.getIsquery().equals("1"))
                { // if column is able to query 循环判断字段类型,添加字段值到WHERE子句中

                    if (values[j] == null || values[j].equals("") ||
                        values[j].equals("null"))

                    {
                        j++;
                        continue;
                    }

                    switch (Integer.parseInt(col_info.getColumntype()))
                    {

                        case 1:
                            sql_where.append(" AND  " + col_info.getColumnname() + "=" +
                                             values[j++]);
                            break;
                        case 2:
                           /* //modified by niking
                            if (tableid.equalsIgnoreCase("139") &&
                                (values[j].indexOf("%") != -1) &&
                                (values[j].indexOf("%") != 0))
                            {
                                //取得以查询串开头的匹配串
                                String sNK_temp = values[j].substring(0,
                                    values[j].indexOf("%"));
                                System.out.println("sNK_temp=" + sNK_temp);
                                sql_where.append(" AND  " + col_info.getColumnname() +
                                                 " like " + "'" +
                                                 //ToolsOfSystem.ISOToGBK(
                                    sNK_temp + "%' ");
                                j++;
                            }
                            else*/
                            {
                                sql_where.append(" AND  " + col_info.getColumnname() +
                                                 " like " + "'%" +
                                                 //ToolsOfSystem.ISOToGBK(values[j++]) + "%' ");
                                                 values[j++] + "%' ");
                            }

                            break;
                        case 3:
                            sql_where.append(" AND  " + col_info.getColumnname() + "=" +
                                             "to_date('" +
                                             values[j++] +
                                             "','yyyymmdd HH24:MI:SS') ");
//                            //to sybase 仅仅比较yyyymmdd
//                            sql_where.append(" AND  " +"subString(convert(VARCHAR,"+col_info[2]+",112),1,8)" + "=" + "'" +
//                                             values[j++].substring(0,8)+"'");
                            break;

                    }
                }
                else
                {
                    j++;
                }

            }
        }
        
        // 定义存储select sql语句的
        String sql_str = "";
        if(localDataTable.getIsconfig().equals("2")){//审核数据!
        	///把order by 取出，放后面
        	sql_str = makeSelectAllStatementForQuery() + " where 1=1 " + sql_where + " union all" +      	
        	makeSelectAllStatementForQuery("0") + " where 1=1 " + sql_where ;//+ getOrderString();
        }else{
            sql_str = makeSelectAllStatementForQuery() +
            " where 1=1 " + sql_where ;//+ getOrderString();
        }

        // 从生成的语句中去除掉最后一个字符，即“，”后，返回，函数执行完毕
        return sql_str ;
    }
    
	/**
	 * 
	 * @param tableid
	 * @return
	 */
	public List getlocalDataTableCols(String tableid) {
		//减少查询数据库次数，insert,query之后localDataTableCols是有值的
		if(localDataTableCols != null)
			return localDataTableCols;
		
		List returnVal = new ArrayList();		
        String strSql = " select TABLEID , COLUMNSEQ , upper(COLUMNNAME) ," +
        " COLUMNTYPE , COLUMNCOMMENT , ISINDEX , ISQUERY ," +
        " CONFIGRATION , LINKTABLEID , ISORDER , STYLE , ISNULL , ISMUTI,HELPID,DEFAULTVALUE,EDITTYPE from T_LOCAL_DATA_TABLES_COLS " +
        " where TABLEID = " + tableid + " order by COLUMNSEQ ";
	    try
	    {
	
	        List al = (List) dbConnection.queryNotBind(strSql);
	
	        if (!al.isEmpty() && al.size() > 0)
	        {
	
	
	            for (int i = 0; i < al.size(); i++)
	            {
	                String[] temp = (String[]) al.get(i);
	                LocalDataTableCol tableCol = new LocalDataTableCol();
	                tableCol.setTableid(temp[0]);
	                tableCol.setColumnseq(temp[1]);
	                tableCol.setColumnname(temp[2]);
	                tableCol.setColumntype(temp[3]);
	                tableCol.setColumncomment(temp[4]);
	                tableCol.setIsindex(temp[5]);
	                tableCol.setIsquery(temp[6]);
	                tableCol.setConfigration(temp[7]);
	                tableCol.setLinktableid(temp[8]);
	                tableCol.setIsorder(temp[9]);
	                tableCol.setStyle(temp[10]);
	                tableCol.setIsnull(temp[11]);
	                tableCol.setIsmuti(temp[12]);
	                tableCol.setHelpid(temp[13]);
	                tableCol.setDefaultvalue(temp[14]);
	                tableCol.setEdittype(temp[15]);
	                returnVal.add(tableCol);
	            }
	        }
	
	    }
	    catch (Exception e)
	    {
	        System.out.println("Error In getAllColumnsForTable() : "+e.getMessage());
	
	        returnVal = null;
	    }	    
	    return returnVal;
	}

	public LocalDataTable getLocalDataTable(String tableid) {
		//减少查询数据库次数，insert,query之后localDataTable是有值的
		if(localDataTable != null)
			return localDataTable;
		LocalDataTable table = null;
	    String strSql = " select TABLEID , TABLENAME , TABLECOMMENT , ISCONFIG , ISHELP ,DATASOURCE,TABLEPARENT from T_LOCAL_DATA_TABLES "+" where TABLEID = " + tableid;
		try
		{		
		    List al = (List) dbConnection.queryNotBind(strSql);
		    if (!al.isEmpty() && al.size() > 0)
		    {
		        String[] temp = (String[]) al.get(0);
		        table = new LocalDataTable();
		        table.setTableid(temp[0]);
		        table.setTablename(temp[1]);
		        table.setTablecomment(temp[2]);
		        table.setIsconfig(temp[3]);
		        table.setIshelp(temp[4]);
		        table.setDatasource(temp[5]);
		        table.setTableparent(temp[6]);
		    }
		
		}
		catch (Exception e)
		{
		    logger.error("Error In getInfoOfTable() : " + e.getMessage());		
		}
		
		return table;
	}
	
	
    /**
     * 执行局数据表记录的删除操作。
     * @param 表id int 类型
     * @param 记录值，字符串数组（一维）
     * @return 返回布尔值，删除成功，返回真，否则返回假
     */
    public boolean removeLocalData(String tableid, String[] value) {
        boolean flag = false;
        // 得到表信息
        localDataTable = getLocalDataTable(tableid);
        // 得到指定表的所有字段信息
        localDataTableCols = getlocalDataTableCols(tableid);
        String strSql ="";
    	if ("DCI".equals(localDataTable.getDatasource())){//操作内存数据库
    		strSql = makeDCIDeleteStatement(value);
    		flag = DCI.delete(strSql);
    		if (flag) WriteLog.dbLog("D","内存数据库局数据","1","删除数据:"+strSql);//记录日志
    		return flag;
    	}
        strSql = makeDeleteStatement(value);
        try
        {
        	flag = dbConnection.deleteNotBind(strSql,localDataTable.getDatasource());
        	if (flag) WriteLog.dbLog("D","局数据","1","删除数据:"+strSql);//记录日志
            return flag;
        }
        catch (Exception e)
        {
        	logger.error("in insertLocalData" +e.getMessage());
            return false;
        }

    }
	
    
    /**
     * 产生局数据表记录的删除sql语句。
     * @param 表id ， String 类型
     * @param 删除记录的索引值，字符串数组
     * @return 返回产生的sql语句
     */
    public String makeDeleteStatement(String[] values) {

        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str = new StringBuffer();

        // 定义插入语句中的语句头
        if(localDataTable.getIsconfig().equals(AUDIT_TABLE)){
            sql_str.append(" DELETE " + localDataTable.getTablename() + "_TEMP WHERE ");  	
        }else{
            sql_str.append(" DELETE " + localDataTable.getTablename() + " WHERE ");  
        }

        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段

            for (int i = 0 ,j=0; i < localDataTableCols.size(); i++)
            {
            	LocalDataTableCol localDataTableCol = (LocalDataTableCol) localDataTableCols.get(i);

                // 循环判断字段类型,添加字段值到WHERE子句中
                if (localDataTableCol.getIsindex().equals("1"))
                { // if column is index
                    switch (Integer.parseInt(localDataTableCol.getColumntype()))
                    {

                        case 1:
                            sql_str.append(localDataTableCol.getColumnname() + "=" + values[j++] +
                                           " AND ");
                            break;
                        case 2:
                            sql_str.append(localDataTableCol.getColumnname() + "=" + "'" +
                            			   values[j++].replaceAll("'", "''") +
                                           "' AND ");
                            break;
                        case 3:
                            sql_str.append(localDataTableCol.getColumnname() + "=" + "to_date('" +
                                           values[j++] +
                                           "','yyyymmdd HH24:MI:SS') AND ");

//                            //to sybase 仅仅比较yyyymmdd
//                            sql_str.append("subString(convert(VARCHAR,"+col_info[2]+",112),1,8)" + "=" + "'" +
//                                             values[j++].substring(0,8) +
//                                             "' AND ");
                            break;
                    }
                }
            }
        }

        // 从生成的语句中去除掉最后一个字符，即“AND”后，返回，函数执行完毕
        return sql_str.substring(0, sql_str.length() - 4);
    }
    
    public String save(String tableid,String query){
    	
        // 得到表信息
        localDataTable = getLocalDataTable(tableid);
        // 得到指定表的所有字段信息
        localDataTableCols = getlocalDataTableCols(tableid);
		JSONArray jsonArr = JSONArray.fromObject( query );
		String[][] allsql = new String[jsonArr.size()][2];
		for(int i=0;i<jsonArr.size();i++){
			JSONObject jo = (JSONObject)jsonArr.get(i);
			if ("".equals(jo.get("primaryKey"))){//组装添加语句
				String[] values = new String[localDataTableCols.size()];
				for (int j = 0; j < localDataTableCols.size(); j++)
	            {
					LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(j);
					values[j] = jo.get(col_info.getColumnname()).toString();
	            }
				if ("DCI".equals(localDataTable.getDatasource())){//内存数据库操作
					allsql[i][0]=makeDCIInsertStatement(values);
				}else{
					allsql[i][0]=makeInsertStatement(values);
				}
				System.out.println("save===="+allsql[i][0]);
				allsql[i][1]="1";
			}else{//组装修改语句
				String[] values = new String[localDataTableCols.size()];
				for (int j = 0; j < localDataTableCols.size(); j++)
	            {
					LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(j);
					values[j] = jo.get(col_info.getColumnname()).toString();
	            }
				String[] pKey = jo.get("primaryKey").toString().split("\\|");
				if ("DCI".equals(localDataTable.getDatasource())){//内存数据库操作
					allsql[i][0]=makeDCIUpdateStatement(values,pKey);
				}else{
					allsql[i][0]=makeUpdateStatement(values,pKey);
				}
				System.out.println("save===="+allsql[i][0]);
				allsql[i][1]="2";
			}
		}

		if ("DCI".equals(localDataTable.getDatasource())){//内存数据库操作
			String[] sql = new String[allsql.length];
			for (int i=0;i<allsql.length;i++){
				sql[i] = allsql[i][0];
				WriteLog.dbLog("U","内存数据库局数据","1","添加/修改数据:"+sql[i]);
			}
			boolean flag = DCI.excute(sql);
			if (flag) 
				return "0";
			else 
				return "1";
		}
		
		
		String strResult = dbConnection.userJdbcTrsaction(allsql,localDataTable.getDatasource());//支持事务添加
		if ("0".equals(strResult.substring(0,1))){
			for(int i=0;i<allsql.length;i++){
				String sq = allsql[i][0];
				if ("1".equals(allsql[i][1])){
					WriteLog.dbLog("C","局数据","1","添加数据:"+sq);
				}
				if ("2".equals(allsql[i][1])){
					WriteLog.dbLog("U","局数据","1","修改数据:"+sq);
				}
				
			}
			
		}
    	return strResult;
    }
    
    /**
     * 产生局数据表记录的插入sql语句。
     * @param 表id ， String 类型
     * @param 插入值，字符串数组
     * @return 返回产生的sql语句
     */
    public String makeInsertStatement(String[] values) {

        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str = new StringBuffer();

        String s = "";

        // 定义插入语句中的语句头
        if(!localDataTable.getIsconfig().equals(AUDIT_TABLE))
        	sql_str.append(" INSERT INTO " + localDataTable.getTablename() +"(");
        else
        	//审核表，往临时表里插
        	sql_str.append(" INSERT INTO " + localDataTable.getTablename() +"_TEMP(");
        
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段

            for (int i = 0; i < localDataTableCols.size(); i++)
            {

            	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);
                sql_str.append(col_info.getColumnname()+", ");
            }
        }
        
        sql_str.delete(sql_str.length()-2,sql_str.length());
        sql_str.append(") VALUES ( ");

        // 循环插入表中的值，以表字段为循环基数
        for (int i = 0; i < localDataTableCols.size(); i++)
        {
        	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);

            // 循环判断字段类型,添加字段值到语句中
            switch (Integer.parseInt(col_info.getColumntype()))
            {
                case 1:
                    sql_str.append(ToolsOfSystem.transStr2OracleNullStr(values[i]) +" , ");
                    break;
                case 2:
                	if (values[i] == null || values[i].equals(""))
                		sql_str.append("null, ");
                	else
                		sql_str.append("'" +
                                values[i].replaceAll("'", "''") +
                 				"' , ");
                    break;
                case 3:
                	if (values[i] == null || values[i].equals(""))
                        sql_str.append("null , ");
                	else
                		sql_str.append("to_date('" + values[i] +
                                   "','yyyymmdd HH24:MI:SS') , ");
                    break;
            }

        }
        s = sql_str.substring(0, sql_str.length() - 2) + ")";

        // 从生成的语句中去除掉最后一个字符，即“，”后，连接“）”返回，函数执行完毕
        //	return sql_str.substring( 0 , sql_str.length() - 1 ) + ")" ;
        return s;
    }
    
    /**
     * 产生局数据表记录的更新sql语句。
     * @param 表id ， String 类型
     * @param 更新值，字符串数组
     * @return 返回产生的sql语句
     *
     */
    public String makeUpdateStatement(String[] values,String[] pk_values){
    	
        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str = new StringBuffer();
        // 定义存储sql语句中where子句的缓冲对象
        StringBuffer sql_where = new StringBuffer();

        String tablename = "";
        //是审核表，则是对临时表操作
        if(localDataTable.getIsconfig().equals(AUDIT_TABLE))
        	tablename = localDataTable.getTablename() + "_TEMP";
        else
        	tablename = localDataTable.getTablename();

        // 定义更新语句中的语句头
        sql_str.append(" UPDATE " + tablename + " SET ");

        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段
            for (int i = 0,j = 0; i < localDataTableCols.size(); i++){
            	
            	LocalDataTableCol localDataTableCol = (LocalDataTableCol) localDataTableCols.get(i);
                // 循环判断字段类型,添加字段值到语句中
                switch (Integer.parseInt(localDataTableCol.getColumntype()))
                {

                    case 1:
                            sql_str.append(localDataTableCol.getColumnname() + "=" + ToolsOfSystem.transStr2OracleNullStr(values[i]) +" , ");
                    		
                        break;
                    case 2: 
                    	if(values[i] == null || values[i].equals("")|| values[i].equals("null")){
                            sql_str.append(localDataTableCol.getColumnname() + "=null" +
                            " , ");                  		
                    	}else{
                            sql_str.append(localDataTableCol.getColumnname() + "=" + "'" +
                            		values[i].replaceAll("'", "''") +
                                    "' , ");
                    	}
                        break;
                    case 3:
                    	if(values[i] == null || values[i].equals("")){
                            sql_str.append(localDataTableCol.getColumnname() + "=null" +
                            " , ");                  		
                    	}else{
                            sql_str.append(localDataTableCol.getColumnname() + "=" + "to_date('" +
                                    values[i] +
                                    "','yyyy-mm-dd HH24:MI:SS') , ");
                    	}
                        break;
                }      
                // 循环判断字段类型,添加字段值到WHERE子句中
                // 如果存在索引字段且条件缓冲变量中为空，则添加where到条件缓冲变量中

                if (sql_where.length() == 0)
                {
                    sql_where.append(" WHERE ");
                }

                // 循环判断字段类型,添加字段值到WHERE子句中
                if (localDataTableCol.getIsindex().equals("1"))
                { // if column is index
                    switch (Integer.parseInt(localDataTableCol.getColumntype()))
                    {

                        case 1:
                        	sql_where.append(localDataTableCol.getColumnname() + "=" + pk_values[j++] +
                                           " AND ");
                            break;
                        case 2:
                        	sql_where.append(localDataTableCol.getColumnname() + "=" + "'" +
                        			pk_values[j++].replaceAll("'", "''") +
                                           "' AND ");
                            break;
                        case 3:
                        	sql_where.append(localDataTableCol.getColumnname() + "=" + "to_date('" +
                        			pk_values[j++] +
                                           "','yyyymmdd HH24:MI:SS') AND ");
                            break;
                    }
                }
            }
        }
        // 从生成的语句中去除掉最后一个字符，即“，”后，返回，函数执行完毕
        return sql_str.substring(0, sql_str.length() - 2) +
            sql_where.substring(0, sql_where.length() - 4);
    }
    
    /**
     * 执行局数据表记录的审核操作。
     * @param 表id int 类型
     * @param 记录值，字符串数组（一维）
     * @return 返回布尔值，删除成功，返回真，否则返回假
     */
    public boolean auditLocalData(String tableid, String[] value) {

        // 得到表信息
        localDataTable = getLocalDataTable(tableid);
        // 得到指定表的所有字段信息
        localDataTableCols = getlocalDataTableCols(tableid);
        
        String[][] sqlAr = makeAuditStatement(tableid, value);
        boolean flag = false;
        //System.out.println( "delete statement = [ " +  strSql + "]" );

        try
        {
            //////////////////////////////////////////////////////////////
            if(dbConnection.userJdbcTrsaction(sqlAr, localDataTable.getDatasource()).substring(0,1).equals("0"))
            	flag = true;
            
    		if (flag){//写日志
    			String sq="";
    			for(int i=0;i<sqlAr.length;i++){
    				sq +=sqlAr[i][0]+"/n";
    			}
    			WriteLog.dbLog("A","局数据","1","审核数据:"+sq);
    		}
    		
            return flag;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return false;
        }

    }

    /**
     * 产生局数据表审核sql语句。
     * @param 表id ， String 类型
     * @param 删除记录的索引值，字符串数组
     * @return 返回产生的sql[][]语句
     */
    public String[][] makeAuditStatement(String tableid, String[] values) {
    	
        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str_insert = new StringBuffer();
        StringBuffer sql_str_delete = new StringBuffer();
        StringBuffer sql_where = new StringBuffer();
        String sqlAr[][] = new String[2][2];

        // 定义语句中的语句头
        sql_str_insert.append(" INSERT INTO " + localDataTable.getTablename() + " " + "select * from " + localDataTable.getTablename() + "_temp  WHERE  ");
        sql_str_delete.append(" DELETE "+localDataTable.getTablename() + "_temp  where ");
        
        
        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段

            for (int i = 0, j = 0; i < localDataTableCols.size(); i++)
            {

            	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);

                // 循环判断字段类型,添加字段值到WHERE子句中
                if (col_info.getIsindex().equals("1"))
                { // if column is index
                    switch (Integer.parseInt(col_info.getColumntype()))
                    {

                        case 1:
                        	if (values[j] == null || values[j].equals("")){
                        			sql_where.append(col_info.getColumnname() + " is null AND ");  
                        			j++;
                        	}
                        	else
	                        	sql_where.append(col_info.getColumnname() + "=" + values[j++] +
	                                           " AND ");
                            break;
                        case 2:
                        	if (values[j] == null || values[j].equals("")){
                        		sql_where.append(col_info.getColumnname() + " is null AND ");
                        		j++;
                        	}
                        	else
                        		sql_where.append(col_info.getColumnname() + "=" + "'" +
                                           //ToolsOfSystem.ISOToGBK(values[j++]) +
                        			       values[j++] +
                                           "' AND ");
                            break;
                        case 3:
                        	if (values[j] == null || values[j].equals("")){
                        		sql_where.append(col_info.getColumnname() + " is null AND ");
                        		j++;
                        	}
                        	else
                        		sql_where.append(col_info.getColumnname() + "=" + "to_date('" +
                                           values[j++] +
                                           "','yyyymmdd HH24:MI:SS') AND ");

//                            //to sybase 仅仅比较yyyymmdd
//                            sql_str.append("subString(convert(VARCHAR,"+col_info[2]+",112),1,8)" + "=" + "'" +
//                                             values[j++].substring(0,8) +
//                                             "' AND ");
                            break;
                    }
                }

            }
        }
        // 从生成的语句中去除掉最后一个字符，即“AND”后，返回，函数执行完毕
        String sqlwhere = sql_where.substring(0, sql_where.length() - 4);
        sql_str_insert.append(sqlwhere);
        //System.out.println("++++++++++++++++++"+sql_str_insert);
        sql_str_delete.append(sqlwhere);
        //System.out.println("++++++++++++++++++"+sql_str_delete);
        sqlAr[0][0] = sql_str_insert.toString();
        sqlAr[0][1] = "1";//insert
        
        sqlAr[1][0] = sql_str_delete.toString();
        sqlAr[1][1] = "3";//delete

        return sqlAr;
        
    }

    /**
    *
    * @param tableid
    * @param type
    * @return
    */

   public String GenerateLogString(String tableid, String logString,
                                   String[] values) {

       //System.out.print("this tablename :"+tableInfo.length);

       logString = logString + localDataTable.getTablename() + "的记录";
       //int flag = 0;
       for (int i = 0; i < localDataTableCols.size(); i++)
       {
    	   LocalDataTableCol temp = (LocalDataTableCol) localDataTableCols.get(i);

           logString = logString + " " + temp.getColumncomment() + "为:" +
           	values[i]==null?"null":values[i];
           
/*            if (++flag > 3)
           {
               break;
           }*/

       }

       //System.out.print("this tablename :"+tableInfo.length);
       return logString;

   }

   /**
    * 执行局数据表记录的审核操作。
    * @param 表id int 类型
    * @param 记录值，字符串数组（一维）
    * @return 返回布尔值，删除成功，返回真，否则返回假
    */
   public boolean cancelAuditLocalData(String tableid, String[] value) {

       // 得到表信息
       localDataTable = getLocalDataTable(tableid);
       // 得到指定表的所有字段信息
       localDataTableCols = getlocalDataTableCols(tableid);
       
   	String sqlAr[][] = makeCancelAuditStatement(tableid, value);
       boolean flag = false;
       //System.out.println( "delete statement = [ " +  strSql + "]" );

       try
       {
           //////////////////////////////////////////////////////////////
           if(dbConnection.userJdbcTrsaction(sqlAr, localDataTable.getDatasource()).substring(0,1).equals("0"))
           	flag = true;
           
   		if (flag){//写日志
			String sq="";
			for(int i=0;i<sqlAr.length;i++){
				sq +=sqlAr[i][0]+"/n";
			}
			WriteLog.dbLog("A","局数据","1","取消审核数据:"+sq);
		}
   		
           return flag;
       }
       catch (Exception e)
       {
    	   e.printStackTrace();
           return false;
       }

   }

    /**
     * 产生局数据表取消审核sql语句。
     * @param 表id ， String 类型
     * @param 删除记录的索引值，字符串数组
     * @return 返回产生的sql[][]语句
     */
    public String[][] makeCancelAuditStatement(String tableid, String[] values) {

        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str_insert = new StringBuffer();
        StringBuffer sql_str_delete = new StringBuffer();
        StringBuffer sql_where = new StringBuffer();
        String sqlAr[][] = new String[2][2];

        // 定义语句中的语句头
        sql_str_insert.append(" INSERT INTO " + localDataTable.getTablename() + "_temp " + "select * from " + localDataTable.getTablename() + " WHERE  ");
        sql_str_delete.append(" DELETE "+localDataTable.getTablename() + " where ");
        
        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段

            for (int i = 0, j = 0; i < localDataTableCols.size(); i++)
            {

            	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);

                // 循环判断字段类型,添加字段值到WHERE子句中
                if (col_info.getIsindex().equals("1"))
                { // if column is index
                    switch (Integer.parseInt(col_info.getColumntype()))
                    {

                        case 1:
                        	if (values[j] == null || values[j].equals("")){
                    			sql_where.append(col_info.getColumnname() + " is null AND "); 
                    			j++;
                        	}
                        	else
                        		sql_where.append(col_info.getColumnname() + "=" + values[j++] +
                                           " AND ");
                            break;
                        case 2:
                        	if (values[j] == null || values[j].equals("")){
                    			sql_where.append(col_info.getColumnname() + " is null AND "); 
                    			j++;
                        	}
                        	else
                        		sql_where.append(col_info.getColumnname() + "=" + "'" +
                                           //ToolsOfSystem.ISOToGBK(values[j++]) +
                        					values[j++] +
                                           "' AND ");
                            break;
                        case 3:
                        	if (values[j] == null || values[j].equals("")){
                    			sql_where.append(col_info.getColumnname() + " is null AND "); 
                    			j++;
                        	}
                        	else
                        		sql_where.append(col_info.getColumnname() + "=" + "to_date('" +
                                           values[j++] +
                                           "','yyyymmdd HH24:MI:SS') AND ");

//                            //to sybase 仅仅比较yyyymmdd
//                            sql_str.append("subString(convert(VARCHAR,"+col_info[2]+",112),1,8)" + "=" + "'" +
//                                             values[j++].substring(0,8) +
//                                             "' AND ");
                            break;
                    }
                }

            }
        }
        // 从生成的语句中去除掉最后一个字符，即“AND”后，返回，函数执行完毕
        String sqlwhere = sql_where.substring(0, sql_where.length() - 4);
        sql_str_insert.append(sqlwhere);
        //System.out.println("++++++++++++++++++"+sql_str_insert);
        sql_str_delete.append(sqlwhere);
        //System.out.println("++++++++++++++++++"+sql_str_delete);
        sqlAr[0][0] = sql_str_insert.toString();
        sqlAr[0][1] = "1";//insert
        
        sqlAr[1][0] = sql_str_delete.toString();
        sqlAr[1][1] = "3";//delete

        return sqlAr;

    }
    
    /**
     * 产生局数据表记录的插入sql语句。
     * @param 插入值，字符串数组
     * @return 返回产生的sql语句
     */
    private String makeDCIInsertStatement(String[] values) {

        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str = new StringBuffer();

        String s = "";

        // 定义插入语句中的语句头
        	sql_str.append(" INSERT INTO " + localDataTable.getTablename() +"(");
        
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段

            for (int i = 0; i < localDataTableCols.size(); i++)
            {

            	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);
                sql_str.append(col_info.getColumnname()+", ");
            }
        }
        
        sql_str.delete(sql_str.length()-2,sql_str.length());
        sql_str.append(") VALUES ( ");

        // 循环插入表中的值，以表字段为循环基数
        for (int i = 0; i < localDataTableCols.size(); i++)
        {
        	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);

            // 循环判断字段类型,添加字段值到语句中
            switch (Integer.parseInt(col_info.getColumntype()))
            {
                case 1:
                    sql_str.append(ToolsOfSystem.transStr2OracleNullStr(values[i]) + " , ");
                    break;
                case 2:
                	if (values[i] == null || values[i].equals(""))
                		sql_str.append("null, ");
                	else
                		sql_str.append("'" +
                                values[i].replaceAll("'", "''") +
                 				"' , ");
                    break;
                case 3:
                	if (values[i] == null || values[i].equals(""))
                		sql_str.append("null, ");
                	else
                		sql_str.append("'" +values[i].replaceAll(" ", "").replaceAll(":", "")+"' , ");
                    break;
            }

        }
        s = sql_str.substring(0, sql_str.length() - 2) + ")";

        // 从生成的语句中去除掉最后一个字符，即“，”后，连接“）”返回，函数执行完毕
        //	return sql_str.substring( 0 , sql_str.length() - 1 ) + ")" ;
        return s;
    }
    
    /**
     * 产生局数据表记录的更新sql语句。
     * @param 更新值，字符串数组
     * @return 返回产生的sql语句
     *
     */
    private String makeDCIUpdateStatement(String[] values,String[] pk_values){
    	
        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str = new StringBuffer();
        // 定义存储sql语句中where子句的缓冲对象
        StringBuffer sql_where = new StringBuffer();

        String tablename = localDataTable.getTablename();

        // 定义更新语句中的语句头
        sql_str.append(" UPDATE " + tablename + " SET ");

        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段
            for (int i = 0,j = 0; i < localDataTableCols.size(); i++){
            	
            	LocalDataTableCol localDataTableCol = (LocalDataTableCol) localDataTableCols.get(i);
                // 循环判断字段类型,添加字段值到语句中
                switch (Integer.parseInt(localDataTableCol.getColumntype()))
                {

                    case 1:
                            sql_str.append(localDataTableCol.getColumnname() + "=" + ToolsOfSystem.transStr2OracleNullStr(values[i])+ " , ");
                        break;
                    case 2: 
                    	if(values[i] == null || values[i].equals("")|| values[i].equals("null")){
                            sql_str.append(localDataTableCol.getColumnname() + "=null" +
                            " , ");                  		
                    	}else{
                            sql_str.append(localDataTableCol.getColumnname() + "=" + "'" +
                            		values[i].replaceAll("'", "''") +
                                    "' , ");
                    	}
                        break;
                    case 3:
                    	if(values[i] == null || values[i].equals("")){
                            sql_str.append(localDataTableCol.getColumnname() + "=null" +
                            " , ");                  		
                    	}else{
                            sql_str.append(localDataTableCol.getColumnname() + "=" + "'" +
                            		values[i].replaceAll(" ", "").replaceAll(":", "") +
                                           "' ,");
                    	}
                        break;
                }      
                // 循环判断字段类型,添加字段值到WHERE子句中
                // 如果存在索引字段且条件缓冲变量中为空，则添加where到条件缓冲变量中

                if (sql_where.length() == 0)
                {
                    sql_where.append(" WHERE ");
                }

                // 循环判断字段类型,添加字段值到WHERE子句中
                if (localDataTableCol.getIsindex().equals("1"))
                { // if column is index
                    switch (Integer.parseInt(localDataTableCol.getColumntype()))
                    {

                        case 1:
                        	sql_where.append(localDataTableCol.getColumnname() + "=" + pk_values[j++] +
                                           " AND ");
                            break;
                        case 2:
                        	sql_where.append(localDataTableCol.getColumnname() + "=" + "'" +
                        			pk_values[j++].replaceAll("'", "''") +
                                           "' AND ");
                            break;
                        case 3:
                        	sql_where.append(localDataTableCol.getColumnname() + "='" +
                        			pk_values[j++].replaceAll(" ", "").replaceAll(":", "") +
                                           "' AND ");
                            break;
                    }
                }
            }
        }
        // 从生成的语句中去除掉最后一个字符，即“，”后，返回，函数执行完毕
        return sql_str.substring(0, sql_str.length() - 2) +
            sql_where.substring(0, sql_where.length() - 4);
    }
    /**
     * 产生局数据表记录的删除sql语句。
     * @param 表id ， String 类型
     * @param 删除记录的索引值，字符串数组
     * @return 返回产生的sql语句
     */
    private String makeDCIDeleteStatement(String[] values) {

        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str = new StringBuffer();

        // 定义插入语句中的语句头

        sql_str.append(" DELETE " + localDataTable.getTablename() + " WHERE ");  

        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段

            for (int i = 0; i < localDataTableCols.size(); i++)
            {
            	LocalDataTableCol localDataTableCol = (LocalDataTableCol) localDataTableCols.get(i);

                // 循环判断字段类型,添加字段值到WHERE子句中
                if (localDataTableCol.getIsindex().equals("1"))
                { // if column is index
                    switch (Integer.parseInt(localDataTableCol.getColumntype()))
                    {

                        case 1:
                    		String vs = values[i];
                    		if (Integer.parseInt(values[i])<0){
                    			vs="0"+vs;
                    			sql_str.append(localDataTableCol.getColumnname() + " < 0 AND ");
                    		}
                    		sql_str.append(localDataTableCol.getColumnname() + "=" + vs +" AND ");

                            break;
                        case 2:
                            sql_str.append(localDataTableCol.getColumnname() + "=" + "'" +
                            			   values[i].replaceAll("'", "''") +
                                           "' AND ");
                            break;
                        case 3:
                            sql_str.append(localDataTableCol.getColumnname() + "=" + "'" +
                            		values[i].replaceAll(" ", "").replaceAll(":", "") +
                                           "' AND ");
                            break;
                    }
                }
            }
        }

        // 从生成的语句中去除掉最后一个字符，即“AND”后，返回，函数执行完毕
        return sql_str.substring(0, sql_str.length() - 4); 
    }
    private String makeDCISelectAllStatementForQuery() {

        // 定义存储sql语句的缓冲对象
        StringBuffer sql_str = new StringBuffer();

        // 定义插入语句中的语句头
        sql_str.append(" SELECT ");

        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段

            for (int i = 0; i < localDataTableCols.size(); i++)
            {

            	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);

                // 循环判断字段类型,添加字段值到查询语句中
                switch (Integer.parseInt(col_info.getColumntype()))
                {

                    case 1:
                    case 2:
                        sql_str.append(col_info.getColumnname() + " , ");
                        break;
                    case 3:
                        sql_str.append(col_info.getColumnname() + " , ");
                        break;
                }
            }

        }
        String tableName = localDataTable.getTablename();

        // 从生成的语句中去除掉最后一个字符，即“AND”后，返回，函数执行完毕
        return sql_str.substring(0, sql_str.length() - 2) +" FROM " + tableName;
    }    
    /**
     */
    private String makeDCISelectQueryStatement(String[] values) {
               
        // 定义存储sql语句中where子句的缓冲对象
        StringBuffer sql_where = new StringBuffer();

        // 循环插入表中的值，以表字段为循环基数
        if (localDataTableCols.size() > 0)
        { // 如果表中存在字段
            for (int i = 0, j = 0; i < localDataTableCols.size(); i++)
            {
            	LocalDataTableCol col_info = (LocalDataTableCol) localDataTableCols.get(i);
                if (col_info.getIsquery().equals("1"))
                { // if column is able to query 循环判断字段类型,添加字段值到WHERE子句中

                    if (values[j] == null || values[j].equals("") ||
                        values[j].equals("null"))

                    {
                        j++;
                        continue;
                    }

                    switch (Integer.parseInt(col_info.getColumntype()))
                    {

                        case 1:
                            sql_where.append(" AND  " + col_info.getColumnname() + "=" +
                                             values[j++]);
                            break;
                        case 2:
                            {
                                sql_where.append(" AND  " + col_info.getColumnname() +
                                                 " like " + "'%" +
                                                 //ToolsOfSystem.ISOToGBK(values[j++]) + "%' ");
                                                 values[j++] + "%' ");
                            }

                            break;
                        case 3:
                            sql_where.append(" AND  " + col_info.getColumnname() + "='" +
                                             values[j++].replaceAll(" ", "").replaceAll(":", "") +
                                             "'");
                            break;
                    }
                }
                else
                {
                    j++;
                }

            }
        }
        
        // 定义存储select sql语句的
        String sql_str = "";
        sql_str = makeSelectAllStatementForQuery() + " where 1=1 " + sql_where ;
        return sql_str ;
    }

	/**
	 * @return the localDataTable
	 */
	public LocalDataTable getLocalDataTable() {
		return localDataTable;
	}

	/**
	 * @param localDataTable the localDataTable to set
	 */
	public void setLocalDataTable(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	/**
	 * @return the localDataTableCols
	 */
	public List getLocalDataTableCols() {
		return localDataTableCols;
	}

	/**
	 * @param localDataTableCols the localDataTableCols to set
	 */
	public void setLocalDataTableCols(List localDataTableCols) {
		this.localDataTableCols = localDataTableCols;
	}

}
