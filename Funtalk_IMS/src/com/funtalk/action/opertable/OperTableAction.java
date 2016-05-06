/**
 * <p>Title:GenerateData.java</p>
 * <p>Description: </p>
 * <p>Company: si-tech</p>
 * @author xuyadong
 * May 13, 2008
 */
package com.funtalk.action.opertable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.funtalk.service.opertable.CheckConfigBean;
import com.funtalk.service.opertable.OperTableBean;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.funtalk.pojo.rightmanage.User;
import com.funtalk.action.FuntalkActionSuport;
import com.funtalk.common.DBConnection;
import com.funtalk.common.SpringContextUtil;
import com.funtalk.common.Page;
import com.funtalk.common.ToolsOfSystem;
import com.funtalk.pojo.rightmanage.Item;
import com.funtalk.pojo.rightmanage.LocalDataTable;
import com.funtalk.pojo.rightmanage.LocalDataTableCol;
import com.funtalk.service.rightmanage.RightManageService;


public class OperTableAction extends FuntalkActionSuport{

	private Log logger = LogFactory.getLog(OperTableAction.class);

	//table ID
	private String askType;
	
	//每页记录数
	private String limit;
	//开始记录数
	private String start;
	//排序字段
	private String sort;
	//asc或desc
	private String dir;
	
    private OperTableBean local = new OperTableBean();
    
    private String[] params;
    
	List primaryKeys;
	
	private LocalDataTable localDataTable;

	private List localDataTableCols;
	
	private String result;
	
	//combobox 查询字符串
	private String query;
	
	//返回json字符串
	private String jsonString;
	
	Map linkTables = new HashMap();

	private RightManageService rightManageService;
	
	public RightManageService getRightManageService() {
		return rightManageService;
	}

	public void setRightManageService(RightManageService rightManageService) {
		this.rightManageService = rightManageService;
	}
	
	public String generateJsonData() throws Exception {
		Enumeration enu = request.getParameterNames();
		while(enu.hasMoreElements()){
			System.out.println(enu.nextElement().toString());
		}
		if (query!=null){
			query=java.net.URLDecoder.decode(query, "UTF-8");//中文乱码问题解决
		}
		logger.info("generateJsonData====");
		logger.info("askType:"+askType);
		logger.info("limit:"+limit);
		logger.info("start:"+start);
		logger.info("sort:"+sort);
		logger.info("dir:"+dir);
		logger.info("query:"+query);
		
		if(limit == null)
			limit = "20";
		if(start == null)
			start = "0";
		
		if (query !=null&&!"".equals(query)){
			//解析查询字符串"xxx=&xxxxx=yyyy&xxx="
			if(query.indexOf("&") != -1){
				String[] aquery = query.split("&");
				for (int i=0;i<aquery.length;i++){
					if (aquery[i].split("=").length==2)
						aquery[i] = aquery[i].split("=")[1];
					else
						aquery[i] = null;
				}
				params=aquery;
			}else{
				String[] paramArr = query.split("=");
				String paramName = paramArr[0];
				String paramValue = paramArr.length == 2 ? paramArr[1] : null;
				List cols = local.getlocalDataTableCols(askType);
				String[] aquery = new String[cols.size()];
				for(int i = 0; i < cols.size(); i++){
					LocalDataTableCol col = (LocalDataTableCol)cols.get(i);
					if(col.getColumnname().equalsIgnoreCase(paramName)){
						aquery[i] = paramValue;
					}
				}
				params = aquery;
			}

		}
		
		Page page = local.getAllLocalDataByQuery(askType, params, Integer.parseInt(start), Integer.parseInt(limit), sort, dir);
        localDataTable = local.getLocalDataTable();        /////表信息
        localDataTableCols = local.getLocalDataTableCols();/////列信息
        
        //加入主键标识
        primaryKeys = new ArrayList();
        
        ////////////遍历纪录集合
        for(int i = 0; i < page.getList().size(); i++){
        	StringBuffer strbuf = new StringBuffer();
        	
        	String[] values = (String[])page.getList().get(i);///////取出一条记录
        	
        	/////////////对记录的每一列遍历,减1的原因select * from ( select row_.*, rownum rownum_ from ( SELECT FUNCGROUPID , FUNCTYPEID , FUNCNAME , FUNCPCODE , FUNCCODE , PAGECODE , REMARK  FROM t_func order by 1 asc ) row_ where rownum <= ?) where rownum_ > ?
        	for(int j = 0; j < values.length-1; j++){
        		//如果是审核表，记录数组会多一位.判断一下，跳出
        		if(localDataTable.getIsconfig()!=null && localDataTable.getIsconfig().equals("2") && j == values.length - 1)       			
        			break;
        		
        		if( ((LocalDataTableCol)localDataTableCols.get(j)).getIsindex()!=null && ((LocalDataTableCol)localDataTableCols.get(j)).getIsindex().equals("1")){
        			strbuf.append(values[j]==null?"|":values[j]+"|");
        		}
        	}

        	strbuf.delete(strbuf.length()-1, strbuf.length());
        	primaryKeys.add(strbuf.toString());
        }
        
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\"totalCount\":\""+page.getRowCount()+"\",\"rows\":[");
		Iterator pkiter = primaryKeys.iterator();
		for (Iterator iter = page.getList().iterator(); iter.hasNext();) {
			String[] array = (String[]) iter.next();
			buffer.append("{primaryKey:\""+pkiter.next().toString()+"\",");
			for (int i = 0; i < localDataTableCols.size(); i++) {
				if (array[i]==null) array[i]="";
				LocalDataTableCol tableCol = (LocalDataTableCol) localDataTableCols.get(i);
				buffer.append("\""+tableCol.getColumnname()+"\":"+"\""+array[i].replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"")+"\"");
				if(i != localDataTableCols.size() -1)
				buffer.append(",");
			}
			if (localDataTable.getIsconfig().equals("2")){//加入审核标志
				buffer.append(",\"t_audit_flag\":\""+array[array.length-1]+"\"");
			}
			buffer.append("}");
			if(iter.hasNext())
				buffer.append(",");
		}
		
		buffer.append("]}");
		//jsonString = "{\"totalCount\":\"572\",\"rows\":[{\"FUNCGROUPID\":\"2\",\"FUNCNAME\":\"帐务处理\",\"FUNCPCODE\":\"S2\",\"FUNCCODE\":\"S204\",\"PAGECODE\":\"/\",\"REMARK\":\"帐务处理\"},{\"FUNCGROUPID\":\"2\",\"FUNCNAME\":\"稽核平衡\",\"FUNCPCODE\":\"S2\",\"FUNCCODE\":\"S202\",\"PAGECODE\":\"/\",\"REMARK\":\"稽核平衡\"},]}";
		//jsonString = "{\"totalCount\":\"30936\",\"rows\":[" +
		//		"{\"threadid\":\"35004\",\"forumid\":\"11\",\"forumtitle\":\"Ext: Premium Help\",\"title\":\"best way to reduce custom duplicate xtypes\",\"author\":\"dirtdevil\",\"lastposter\":\"dirtdevil\",\"lastpost\":\"1210296108\",\"excerpt\":\"Right now I'm dupl\",\"replycount\":\"7\"}," +
		//		"{\"threadid\":\"35010\",\"forumid\":\"11\",\"forumtitle\":\"Ext: Premium Help\",\"title\":\"ajax request params\",\"author\":\"varsos\",\"lastposter\":\"varsos\",\"lastpost\":\"1210294198\",\"excerpt\":\"adffdfdf\",\"replycount\":\"3\"}" +
		//		"]}";
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		//logger.debug(buffer.toString());
		out.print(buffer.toString());
		out.close();
		return "";
	}
	
	/**
	 * 导出excel方法
	 * @return
	 * @throws Exception
	 */
	public String toExcel() throws Exception{
		if (query!=null){
			query=java.net.URLDecoder.decode(query, "UTF-8");//中文乱码问题解决
		}
		logger.info("generateJsonData====");
		logger.info("askType:"+askType);
		logger.info("limit:"+limit);
		logger.info("start:"+start);
		logger.info("sort:"+sort);
		logger.info("dir:"+dir);
		logger.info("query:"+query);
		if(limit == null)
			limit = "20";
		if(start == null)
			start = "0";
		if (query !=null&&!"".equals(query)){//解析查询字符串"xxx=&xxxxx=yyyy&xxx="
			if(query.indexOf("&") != -1){
				String[] aquery = query.split("&");
				for (int i=0;i<aquery.length;i++){
					if (aquery[i].split("=").length==2)
						aquery[i] = aquery[i].split("=")[1];
					else
						aquery[i] = null;
				}
				params=aquery;
			}else{
				String[] paramArr = query.split("=");
				String paramName = paramArr[0];
				String paramValue = paramArr.length == 2 ? paramArr[1] : null;
				List cols = local.getlocalDataTableCols(askType);
				String[] aquery = new String[cols.size()];
				for(int i = 0; i < cols.size(); i++){
					LocalDataTableCol col = (LocalDataTableCol)cols.get(i);
					if(col.getColumnname().equalsIgnoreCase(paramName)){
						aquery[i] = paramValue;
					}
				}
				params = aquery;
			}

		}
		
		Page page = local.getAllLocalDataByQuery(askType, params, 0, 65535, sort, dir);
		logger.debug("toecel========"+page.getList().size());
        localDataTable = local.getLocalDataTable();
        localDataTableCols = local.getLocalDataTableCols();
        
        ActionContext ctx = ActionContext.getContext();
		Map request = ctx.getContextMap();
		request.put("exportList", page.getList());
		request.put("localDataTableCols", localDataTableCols);
        
		return "exportExcel";
	}
	
	public String list() throws Exception {
		prepareTableInfo();
        //权限信息
		ActionContext ctx = ActionContext.getContext();
		Map request = ctx.getContextMap();
		User user = ((User)session.get("currentUser"));
		Map rightmap =  rightManageService.getUserFuncRight(user.getUsername(), funccode);
		if (rightmap.containsKey("1")){
			request.put("add", "添加");
		}
		if (rightmap.containsKey("2")){
			request.put("update", "修改");
		}
		if (rightmap.containsKey("3")){
			request.put("del", "删除");
		}
		if (rightmap.containsKey("4")){
			request.put("audit", "审核");
		}
		if (rightmap.containsKey("5")){
			request.put("import", "导入");
		}
		return SUCCESS;
	}

	/**
	 * 利用局数据配置进行特殊处理的通用模块
	 * 新进程调度使用该模块,简化开发过程
	 * @return
	 * @throws Exception
	 */
	public String speclist() throws Exception {
		prepareTableInfo();
		return this.result;
	}
	
	/**
	 * insert,update,reproduce-insert都要用的准备数据方法
	 * @throws Exception
	 */
    private void prepareTableInfo() throws Exception {
		//获取要表的信息和字段信息
		localDataTable = local.getLocalDataTable(askType);
		localDataTableCols = local.getlocalDataTableCols(askType);
		//获取链接表信息
		for(int i = 0; i < localDataTableCols.size(); i++){
			LocalDataTableCol localDataTableCol = (LocalDataTableCol)localDataTableCols.get(i);
			String linkTablId = localDataTableCol.getLinktableid();
			if(!(linkTablId == null || linkTablId.equals("") || linkTablId.equals("0"))){
				List linktableList = getLinkTableData(linkTablId);
				String arrayLink="[";
				for (int j = 0; j< linktableList.size(); j++){
					Item item = (Item)linktableList.get(j);
					arrayLink +="['"+item.getKey()+"','"+item.getComment()+"'],";
				}
				arrayLink= arrayLink.substring(0, arrayLink.length()-1)+"]";
				linkTables.put(new Integer(i),arrayLink );
			}
		}
    }
    public String delete() throws Exception {
		query=java.net.URLDecoder.decode(query, "UTF-8");//中文乱码问题解决
		System.out.println("query====="+query);
		if (query !=null&&!"".equals(query)){//解析查询字符串"xxx=&xxxxx=yyyy&xxx="
			String[] deleteKey = query.split("\\|");
			params=deleteKey;
		}
		local.removeLocalData(askType, params);
		return "";
    }
    
    public String save() throws Exception {
    	System.out.println("----------> save() , query1 =  "+query);
    	query=java.net.URLDecoder.decode(query, "UTF-8");//中文乱码问题解决
		System.out.println("----------> save() , query2 =  "+query);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		String result =local.save(askType,query);
		out.print(result);
		out.close();
		return "";
    }
    
	/**
	 * Action 执行方法 审核
	 * 
	 * @return
	 * @throws Exception
	 */
	public String audit() throws Exception {
		
		boolean flag = false;
		logger.info("audit primaryKey: "+ query);
		String[] params = query.split("\\|");
		flag = local.auditLocalData(askType, params);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		out.print(flag);
		out.close();
		return "";

	}

	/**
	 * Action 执行方法 取消审核
	 * 
	 * @return
	 * @throws Exception
	 */
	public String cancelAudit() throws Exception {
		
		boolean flag = false;
		logger.info("audit primaryKey: "+ query);
		String[] params = query.split("\\|");
		flag = local.cancelAuditLocalData(askType, params);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		out.print(flag);
		out.close();
		return "";
		
	}
    
    /**
     * 2009-5-26 liaoxb
     * 链接表不一定要选一个值，可以选空，现在是一定要选一个值
     **************************************************
     * 获取链接表数据
     * 
	 * 1,设置表ID　21
	 * 2,设置表ID和关联字段　21${1,3}
     * 3,设置SQL语句 select name, comment from table
     * 4,key-value jf-计费,DBSETTLE-结算,ACCOUNT-帐务
     * @param linkTableId
     * @return
     */
    private List getLinkTableData(String linkTableId){
		List ls = new ArrayList();
		List list = null;
		if(linkTableId.toUpperCase().indexOf("SELECT") != -1){
			list =( (DBConnection)SpringContextUtil.getBean("DBConnection")).queryNotBind(linkTableId);
			for(int i = 0; i < list.size(); i++){
				String[] arr = (String[])list.get(i);
				ls.add(new Item(arr[0],arr[1]));
			}
		}else if(linkTableId.indexOf("${") !=-1){
	        String[] arr = ToolsOfSystem.get$String(linkTableId).split(",");
	        list = local.getAllLocalData(ToolsOfSystem.escape$String(linkTableId));
	        for(int i = 0; i < list.size(); i++){
	        	String [] tempArr = (String[])list.get(i);
	        	ls.add(new Item(tempArr[Integer.parseInt(arr[0])],tempArr[Integer.parseInt(arr[1])]));        	
	        }
		}else if(linkTableId.indexOf("-") !=-1){
			String[] arr = linkTableId.split(",");
			for(int i = 0; i < arr.length; i++){
				String[] temp = arr[i].split("-");
				ls.add(new Item(temp[0],temp[1]));
			}
		}else{
	        list = local.getAllLocalData(linkTableId);
			for(int i = 0; i < list.size(); i++){
				String[] arr = (String[])list.get(i);
				ls.add(new Item(arr[0],arr[1]));
			}
		}
        return ls;
    }
    
	public String checkTable() throws Exception{
		String errorInfo="";
		CheckConfigBean check = new CheckConfigBean(Integer.parseInt(askType));
		
		HashMap checkCols = check.checkCols();
		HashMap checkIndex = check.checkIndex();
		
		if(!check.isThere()){
			errorInfo = "表不存在！请查看是否是数据源配置有问题！";
		}else if(checkCols.size() > 0){
			errorInfo = "字段配置有问题：";
			
			List temp = (List)checkCols.get("moreCols");
			if(temp!=null){
				errorInfo +="表中的以下字段没有配置：";
				for(int j = 0 ; j < temp.size() ; j++){
					String tt = (String)temp.get(j);
					errorInfo += tt+";";
				}
			}
			temp = (List)checkCols.get("lessCols");
			if(temp!=null){
				errorInfo +="表中并没有以下字段：";
				for(int j = 0 ; j < temp.size() ; j++){
					String tt = (String)temp.get(j);
					errorInfo += tt+";";
				}
			}
			temp = (List)checkCols.get("defferTypeCols");
			if(temp!=null){
				errorInfo +="以下字段配置有错误，可能是字段类型错误请查看！";
				for(int j = 0 ; j < temp.size() ; j++){
					String tt = (String)temp.get(j);
					errorInfo += tt+";";
				}
			}
		}else if(checkIndex.size() > 0){
			errorInfo = "唯一索引配置有问题：";
			List temp = (List)checkIndex.get("moreIndexes");
			if(temp!=null){
				errorInfo +="以下字段不是索引字段!";
				for(int i = 0 ; i < temp.size(); i ++){
					String tt = (String)temp.get(i);
					errorInfo += tt+";";
				}
			}
			temp = (List)checkIndex.get("lessIndexes");
			if(temp!=null){
				errorInfo +="以下字段也是索引字段!";
				for(int i = 0 ; i < temp.size(); i ++){
					String tt = (String)temp.get(i);
					errorInfo += tt+";";
				}
			}
		}else{
			errorInfo = "0此局数据配置没有问题！";
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		out.print(errorInfo);
		out.close();
		return "";
	}
    
	public String toImport(){
		return "toImport";
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getAskType() {
		return askType;
	}

	public void setAskType(String askType) {
		this.askType = askType;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public LocalDataTable getLocalDataTable() {
		return localDataTable;
	}

	public void setLocalDataTable(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	public List getLocalDataTableCols() {
		return localDataTableCols;
	}

	public void setLocalDataTableCols(List localDataTableCols) {
		this.localDataTableCols = localDataTableCols;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	/**
	 * @return the linkTables
	 */
	public Map getLinkTables() {
		return linkTables;
	}
	/**
	 * @param linkTables the linkTables to set
	 */
	public void setLinkTables(Map linkTables) {
		this.linkTables = linkTables;
	}
	
	/**
	 * @return the primaryKeys
	 */
	public List getPrimaryKeys() {
		return primaryKeys;
	}

	/**
	 * @param primaryKeys the primaryKeys to set
	 */
	public void setPrimaryKeys(List primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
