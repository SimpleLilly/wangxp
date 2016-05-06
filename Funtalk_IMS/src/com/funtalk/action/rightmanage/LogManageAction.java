package com.funtalk.action.rightmanage;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.funtalk.action.FuntalkActionSuport;
import com.funtalk.service.rightmanage.RightManageService;
import com.opensymphony.webwork.ServletActionContext;


public class LogManageAction extends FuntalkActionSuport {

	private Log logger = LogFactory.getLog(LogManageAction.class);
	
	private RightManageService rightManageService;
	
	//每页记录数
	private String limit;
	//开始记录数
	private String start;
	//排序字段
	private String sort;
	//asc或desc
	private String dir;
	
	private String query;

	
	public String list() throws Exception {
		return SUCCESS;
	}
	
	public String getLogJsonData() throws Exception {

		if (query!=null){
			query=java.net.URLDecoder.decode(query, "UTF-8");//中文乱码问题解决
		}
		logger.info("getUsersJsonData====");
		logger.info("limit:"+limit);
		logger.info("start:"+start);
		logger.info("sort:"+sort);
		logger.info("dir:"+dir);
		logger.info("query:"+query);
		if(limit == null)
			limit = "15";
		if(start == null)
			start = "0";
		
		if("seq".equals(sort))
			sort = "u.seq";
		if("username".equals(sort))
			sort = "u.username";
		if("operateTime".equals(sort))
			sort = "u.operate_time";
		if("operateType".equals(sort))
			sort = "u.operate_type";
		if("operateSrc".equals(sort))
			sort = "u.operate_src";
		if("operateDest".equals(sort))
			sort = "u.operate_dest";
		if("memo".equals(sort))
			sort = "u.memo";
		
		Map params = new HashMap();
		if (query !=null&&!"".equals(query)){
			String[] aquery = query.split("&");
			for (int i=0; i < aquery.length; i++){
				String[] aqueryArr = aquery[i].split("=");
				if (aqueryArr.length == 2){
					if(aqueryArr[0].equals("seq"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("username"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("operate_time"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("operate_type"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("operate_src"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("operate_dest"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("memo"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
				}
			}
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		String str=rightManageService.getLogListJson(Integer.parseInt(start),Integer.parseInt(limit),params,dir,sort);
		out.print(str);
		out.close();
			
		return "";
	}
	public RightManageService getRightManageService() {
		return rightManageService;
	}

	public void setRightManageService(RightManageService rightManageService) {
		this.rightManageService = rightManageService;
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

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getQuery() {
		return query; 
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
