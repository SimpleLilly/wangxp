package com.funtalk.action.statistics;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.xwork.ActionSupport;
import com.funtalk.common.DBConnection;
import com.funtalk.common.SpringContextUtil;
import com.funtalk.service.statistics.StatAssistService;

public class StatAssistAction  extends ActionSupport implements ServletRequestAware{

	private Log logger = LogFactory.getLog(StatAssistAction.class);
	
	HttpServletRequest request;
	private String query;
	
	private String dir;
	//每页记录数
	private String limit;
	//开始记录数
	private String start;
	//排序字段
	private String sort;
	
	private StatAssistService statAssistService;
	
	
	public String upLoadSettleExcel() throws Exception {

		if (query!=null){
			query=java.net.URLDecoder.decode(query, "UTF-8");//中文乱码问题解决
		}
		logger.info("=========generateData========");
		logger.info("limit:"+limit);
		logger.info("start:"+start);
		logger.info("query:"+query);
		if(limit == null)
			limit = "15";
		if(start == null)
			start = "0";
    	     
		Map params = new HashMap();
		
		if (query !=null&&!"".equals(query)){
		
		String[] aquery = query.split("&");
			for (int i=0; i < aquery.length; i++){
				String[] aqueryArr = aquery[i].split("=");
				if (aqueryArr.length == 2){
					params.put(aqueryArr[0], aqueryArr[1]);				
				}
			}
		}
				
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
			
	    out.print(statAssistService.upLoadSettleExcel(params));
		
		out.close();
			
		return "";
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

	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public void setStatService(StatAssistService statAssistService) {
		this.statAssistService = statAssistService;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
