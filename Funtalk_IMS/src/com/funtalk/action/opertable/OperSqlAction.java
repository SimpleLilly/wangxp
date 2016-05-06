/**
 * <p>Title:OperSqlAction.java</p>
 * <p>Description: </p>
 * <p>Company: si-tech</p>
 * @author xuyadong
 * Jun 24, 2008
 */
package com.funtalk.action.opertable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.xwork.ActionSupport;
import com.funtalk.common.DBConnection;
import com.funtalk.common.Page;
import com.funtalk.common.SpringContextUtil;
import com.funtalk.common.ToolsOfSystem;

/**
 * @author xuyadong
 *
 */
public class OperSqlAction extends ActionSupport implements ServletRequestAware{

	private Log logger = LogFactory.getLog(OperTableAction.class);
	
	HttpServletRequest request;
	
	Map session;
	
	//每页记录数
	private String limit;
	//开始记录数
	private String start;
	//排序字段
	private String sort;
	//asc或desc
	private String dir;
	
	//combobox 查询字符串
	private String query;
	
	private String querySql;
	
	//返回json字符串
	private String jsonString;
	
	private DBConnection dbConnection = (DBConnection)SpringContextUtil.getBean("DBConnection");
	
	public String generateJsonData() throws Exception {
		Enumeration enu = request.getParameterNames();
		while(enu.hasMoreElements()){
			System.out.println(enu.nextElement().toString());
		}
		if (query!=null){
			query=java.net.URLDecoder.decode(query, "UTF-8");//中文乱码问题解决
		}
		logger.info("generateJsonData====");
		logger.info("limit:"+limit);
		logger.info("start:"+start);
		logger.info("sort:"+sort);
		logger.info("dir:"+dir);
		logger.info("query:"+query);
		logger.info("querySql:"+querySql);
		if(limit == null)
			limit = "20";
		if(start == null)
			start = "0";
		if (querySql !=null && !"".equals(querySql)){
/*			select fav_type "FAV_TYPE", note "NOTE" from (select fav_type fav_type,max(note) note 
			from fav_index group by fav_type )where fav_type like \'${value}%\' */
			List fields = findFields(querySql);
			String newquerySql = ToolsOfSystem.replace$String(querySql, query);
			logger.info(newquerySql);
	
			logger.debug("start:"+start+",start+limit:"+( Integer.parseInt(start) + Integer.parseInt(limit)));
            Page pv = dbConnection.queryBindPageVo(newquerySql, Integer.parseInt(start), Integer.parseInt(start) + Integer.parseInt(limit));
    		int totalCount = pv.getRowCount();
    		
			StringBuffer buffer = new StringBuffer();
			buffer.append("{\"totalCount\":\""+totalCount+"\",\"rows\":[");
			for (Iterator iter = pv.getList().iterator(); iter.hasNext();) {
				String[] array = (String[]) iter.next();
/*				for (int i = 0; i < arr.length; i++) {
					System.out.print(arr[i]+",");
				}
				System.out.println();*/
				buffer.append("{");
				for(int i = 0; i < fields.size(); i++){
					if (array[i]==null) array[i]="";
					String fieldName = (String) fields.get(i);
					buffer.append("\""+fieldName+"\":"+"\""+array[i].replaceAll("\"", "\\\\\"")+"\"");
					if(i != fields.size() -1)
					buffer.append(",");
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
			logger.debug(buffer.toString());
			out.print(buffer.toString());
			out.close();
			
		}
		return "";
	}

	private List findFields(String sql) {
		List fields = new ArrayList();
		String i_select = querySql.substring(0, querySql.toUpperCase().indexOf("FROM"));
		String[] arr = i_select.split(",");
		Pattern pattern = Pattern.compile("\".*\"",Pattern.CASE_INSENSITIVE);
		for (int i = 0; i < arr.length; i++) {
			Matcher matcher = pattern.matcher(arr[i]);
			String match = "";
			if (matcher.find())
			{
				int start = matcher.start();
				int end = matcher.end();
				match = arr[i].substring(start+1,end-1);
				fields.add(match);
			}
		}
		return fields;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
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

	public String getQuerySql() {
		return querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	
}
