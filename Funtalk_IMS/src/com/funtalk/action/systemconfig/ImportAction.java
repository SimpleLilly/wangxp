package com.funtalk.action.systemconfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.funtalk.service.opertable.OperTableBean;
import com.funtalk.service.systemconfig.ImportBean;
import com.funtalk.action.FuntalkActionSuport;
import com.funtalk.common.DataConnection;
import com.funtalk.pojo.rightmanage.User;
import com.funtalk.pojo.rightmanage.LocalDataTableCol;


/**
 * 
 * @author tiantao
 *
 */
public class ImportAction  extends FuntalkActionSuport{
	private Log logger = LogFactory.getLog(ImportAction.class);

	// userinfo
	private String userId;

	//private String roleId;

	private String userLocal;

//	 存放上传文件的目录

	//String url = "E:\\工作\\h黑龙江项目\\局数据\\import\\";
	//String errorUrl = "E:\\工作\\h黑龙江项目\\局数据\\import\\";

	

//	 存放上传文件的目录

	String url = "D:\\workspace\\newCM\\bjunicom\\WebRoot\\opertable\\bacth_import\\temp\\";
	
	String errorUrl = "D:\\workspace\\newCM\\bjunicom\\WebRoot\\opertable\\bacth_import\\error\\";


	String errorInfo;
	
	String askType;

	String tableId;

	Map updownList;

	String fenge;

	String otherFen;

	String headNum;
	
	List colsList;
	
	String fileContentType;
	
	String dateModel;
	
	String otherModel;
	
	int errorNum;
	
	int recNum;
	
	private File file;

	private String fileFileName;
	
	String errorFileName;

	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}


	public String goOption() {
		
		
		OperTableBean local = new OperTableBean();
		List localDataTableCols = local.getlocalDataTableCols(askType);
		LocalDataTableCol tableCol = null;
		updownList = new LinkedHashMap();
		for (int i = 0; i < localDataTableCols.size(); i++) {
			tableCol = (LocalDataTableCol)localDataTableCols.get(i);
			if(tableCol.getEdittype()!=null&&tableCol.getEdittype().equals("1")){
				continue;
			}
			updownList.put(String.valueOf((new Integer(localDataTableCols.size()- Integer.valueOf(tableCol.getColumnseq()).intValue()).intValue())), tableCol.getColumnname()
					+ "______" + tableCol.getColumncomment());
		}

		return "go";
	}
	
	

	public String upLoad() {
		
		errorFileName = ImportBean.getFileName("error");
		
		String dataSource = "";
		
		String tableName = "";
		
		List cols = null;
		
		String fileName = "";
		
		 File target = new File(url+fileFileName);
         if (target.exists()) {
             target.delete();
         }
         
		file.renameTo(new File(url+fileFileName));
	
		dataSource = getDataSource(askType);
		
		tableName = getTableName(askType);
		
		
		fileName = url + fileFileName;
		
		OperTableBean local = new OperTableBean();
		cols = local.getlocalDataTableCols(askType);
		
		if(fenge.equals("other")){
			fenge = otherFen;
		}
		System.out.println("fenfenfenfen:"+fenge);
		if(dateModel==null){
			dateModel = "yyyymmdd HH:MI:SS";
		}
		if(dateModel.equals("other")){
			dateModel = otherModel;
		}
		
		doImport(dataSource,tableName,cols,colsList,fenge,fileName,headNum);
		

		
		return "ok";
	}

	public String doImport(String db, String tableName, List cols,List colsList,String fenge, String file,String headNum) {
		
		
		initSession();
		//ImportBean importBean = new ImportBean(conn,userId,tableName,cols);

		ArrayList dataBuffer = new ArrayList();
		java.io.FileReader fileReader;
		BufferedReader bufferReader;
		try {
			fileReader = new FileReader(file);
			bufferReader = new BufferedReader(fileReader);
			
			//忽略文本的前Ｎ行

			for(int i = 0 ; i < Integer.valueOf(headNum).intValue(); i ++){
				bufferReader.readLine();
			}
			recNum = 0; 
			
			String addValue = "";
			
			for(int i = 0 ; i < cols.size() ; i ++){
				LocalDataTableCol temp = (LocalDataTableCol)cols.get(i);
				if(temp.getEdittype()!=null&&temp.getEdittype().equals("1")){
					addValue = "0";
				}
			}
			
			buffer: while (true) {
				recNum ++;
				String oneLine = null;
				String[] temp = null;
				oneLine = bufferReader.readLine();
				if(oneLine != null&&!oneLine.trim().equals("")&&!oneLine.trim().equals("\t")){
					if(!addValue.equals("")){
						oneLine+=fenge+addValue;
					}
				}
				if (oneLine != null&&!oneLine.trim().equals("")&&!oneLine.trim().equals("\t")) {
					
					System.out.println(oneLine);
					if(fenge.equals("|")){
						temp = oneLine.split("\\|");
					}else{
						temp = oneLine.split(fenge);
					}
					
					if(temp.length>=3&&askType.equals("38")){
						temp[2] = userLocal;
					}
					dataBuffer.add(temp);
					if(dataBuffer.size() == 1000){
						for(int i = 0 ; i < 5 ; i ++){
							ArrayList data = new ArrayList();
							for(int j = i*200 ; j < (i+1)*200 ; j++){
								data.add(dataBuffer.get(j));
							}
							
							Connection conn = DataConnection.getConnection(db);
							ImportBean importBean = new ImportBean(conn,data,userId,tableName,cols,colsList,dateModel,errorUrl+errorFileName+"sub_"+(recNum+i));
							importBean.start();
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dataBuffer.clear();
					}
				
				}else{
					
					for(int i = 0 ; i < dataBuffer.size()/200+1; i++){
						
						ArrayList data = new ArrayList();
						for(int j = i*200 ; j < (dataBuffer.size()>=(i+1)*200?(i+1)*200:dataBuffer.size()) ; j++){
							if(dataBuffer.get(j)!=null){
								data.add(dataBuffer.get(j));
							}else{
								break;
							}
						}
						Connection conn = DataConnection.getConnection(db);
						ImportBean importBean = new ImportBean(conn,data,userId,tableName,cols,colsList,dateModel,errorUrl+errorFileName+"sub_"+(recNum+i));
						importBean.start();
					}
					dataBuffer.clear();
					bufferReader.close();
					fileReader.close();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break buffer;
				}
			}
			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(recNum+1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("errorUrl:"+errorUrl+"errorFileName:"+errorFileName);
		try {
		//等待一秒，等待sub错误文件生产完毕。
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		errorNum = ImportBean.unionFile(errorUrl, errorFileName);
		
		recNum -= (errorNum+1);
		File f = new File(file);
		//可以在这删除上传的文件
		return "ok";
	} 
	
	public List getCols(String tableId){
		List cols = null;
		
		if (tableId != null) {
			String colsSql = "select columnseq,columnname,columncomment,columntype  from t_local_data_tables_cols where tableid  = '"
					+ tableId + "' order by columnseq ";
			cols  = DataConnection.queryNotBind(colsSql);
		}
		
		return cols;
	}
	
	public String getTableName(String tableId){
		String tableName = "";
		String tableSql = "select tablename from t_local_data_tables where tableid = '"
			+ tableId + "' ";
		List temp = DataConnection.queryNotBind(tableSql);
		tableName = ((String[])temp.get(0))[0];
		return tableName;
	}
	
	public String getDataSource(String tableId){
		String datasource = "";
		String tableSql = "select datasource  from t_local_data_tables where tableid = '"
			+ tableId + "' ";
		List temp = DataConnection.queryNotBind(tableSql);
		datasource = ((String[])temp.get(0))[0];
		return datasource;
	}
	

	private void initSession() {
		User currentUser = (User) session.get("currentUser");
		userId = currentUser.getUsername();
		//roleId = currentUser.getTUserRoles();
		userLocal = currentUser.getCityList().getLongCode();
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public Map getUpdownList() {
		return updownList;
	}

	public void setUpdownList(Map updownList) {
		this.updownList = updownList;
	}

	public String getFenge() {
		return fenge;
	}

	public void setFenge(String fenge) {
		
		this.fenge = fenge;
	}

	public String getOtherFen() {
		return otherFen;
	}

	public void setOtherFen(String otherFen) {
		this.otherFen = otherFen;
	}

	public List getColsList() {
		return colsList;
	}

	public void setColsList(List colsList) {
		this.colsList = colsList;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public void setHeadNum(String headNum) {
		this.headNum = headNum;
	}

	public String getDateModel() {
		return dateModel;
	}

	public void setDateModel(String dateModel) {
		this.dateModel = dateModel;
	}

	public String getOtherModel() {
		return otherModel;
	}

	public void setOtherModel(String otherModel) {
		this.otherModel = otherModel;
	}

	public String getHeadNum() {
		return headNum;
	}

	public String getErrorFileName() {
		return errorFileName;
	}

	public void setErrorFileName(String errorFileName) {
		this.errorFileName = errorFileName;
	}

	public String getAskType() {
		return askType;
	}

	public void setAskType(String askType) {
		this.askType = askType;
	}

	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getRecNum() {
		return recNum;
	}

	public void setRecNum(int recNum) {
		this.recNum = recNum;
	}


}
