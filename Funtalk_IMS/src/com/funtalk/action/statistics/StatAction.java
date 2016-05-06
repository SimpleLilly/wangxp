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
import com.funtalk.service.statistics.StatService;

public class StatAction  extends ActionSupport implements ServletRequestAware{

	private Log logger = LogFactory.getLog(StatAction.class);
	
	HttpServletRequest request;
	private String query;
	
	private String dir;
	//每页记录数
	private String limit;
	//开始记录数
	private String start;
	//排序字段
	private String sort;
	
	private StatService statService;
	
	public String generateData() throws Exception {

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
			
		out.print(statService.getDevelopUsers(params));
		
		out.close();
			
		return "";
	}
	
	
	public String getDayDevUsers() throws Exception {

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
			
	    out.print(statService.getDayDevUsers(params));
		
		out.close();
			
		return "";
	}
	
	public String getDayDevUsersNew() throws Exception {

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
			
	    out.print(statService.getDayDevUsersNew(params));
		
		out.close();
			
		return "";
	}
	
	
	
	public String getDayPayFees() throws Exception {

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
			
	    out.print(statService.getDayPayFees(params));
		
		out.close();
			
		return "";
	}
	
	
	public String getDayUserBills() throws Exception {

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
			
	    out.print(statService.getDayUserBills(params));
		
		out.close();
			
		return "";
	}	
	
	
	public String getMonProUserBills() throws Exception {

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
			
	    out.print(statService.getMonProUserBills(params));
		
		out.close();
			
		return "";
	}
	
	
	
	public String getMonProUserStayMons() throws Exception {

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
			
	    out.print(statService.getMonProUserStayMons(params));
		
		out.close();
			
		return "";
	}
	
	public String getMonthEnd() throws Exception {

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
			
	    out.print(statService.getMonthEnd(params));
		
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
	
	public void setStatService(StatService statService) {
		this.statService = statService;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
