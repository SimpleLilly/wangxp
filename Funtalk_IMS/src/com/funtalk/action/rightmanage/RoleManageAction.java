package com.funtalk.action.rightmanage;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.funtalk.action.FuntalkActionSuport;
import com.funtalk.service.rightmanage.RightManageService;
import com.opensymphony.webwork.ServletActionContext;

public class RoleManageAction extends FuntalkActionSuport {

	private Log logger = LogFactory.getLog(RoleManageAction.class);
	
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
	
	private List userList;
	
	private List roleList;

	private List topMenu;
	
	private String roleNodeDataJs;
	
	private String roleid;
	
	public String getRoleNodeDataJs() {
		return roleNodeDataJs;
	}

	public void setRoleNodeDataJs(String roleNodeDataJs) {
		this.roleNodeDataJs = roleNodeDataJs;
	}

	public String list() throws Exception {
		userList = rightManageService.getAllUsers();
		roleList = rightManageService.getAllRoles();
		//topMenu = rightManageService.getTopMenu();
		return SUCCESS;
	}
	
	public String getRolesJsonData() throws Exception {

		if (query!=null){
			query=java.net.URLDecoder.decode(query, "UTF-8");//中文乱码问题解决
		}
		logger.info("getRolesJsonData====");
		logger.info("limit:"+limit);
		logger.info("start:"+start);
		logger.info("sort:"+sort);
		logger.info("dir:"+dir);
		logger.info("query:"+query);
		if(limit == null)
			limit = "150";
		if(start == null)
			start = "0";
		
		if("roleid".equals(sort))
			sort = "r.roleid";
		if("rolename".equals(sort))
			sort = "r.rolename";
		if("memo".equals(sort))
			sort = "r.memo";
		if("roletype".equals(sort))
			sort = "r.roletype";
		
		Map params = new HashMap();
		
		if (query !=null&&!"".equals(query)){
			String[] aquery = query.split("&");
			for (int i=0; i < aquery.length; i++){
				String[] aqueryArr = aquery[i].split("=");
				if (aqueryArr.length == 2){
					if(aqueryArr[0].equals("roleid"))
						params.put("r."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("rolename"))
						params.put("r."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("memo"))
						params.put("r."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("roletype"))
						params.put("r."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("username"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("parentrole"))
						params.put("roleLevel.id.PRoleid", aqueryArr[1]);
				}
			}
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		out.print(rightManageService.getRoleListJson(Integer.parseInt(start),Integer.parseInt(limit),params,dir,sort));
		out.close();

		return "";
	}

	public String addRole() throws Exception {
		
		String rolename = request.getParameter("rolename");

		String memo = request.getParameter("memo");
		
		String userNames = request.getParameter("userNames");
		
		String proleselector = request.getParameter("proleselector");
		
		String roletype = request.getParameter("roletype");
		
		logger.info("addRole====");
		logger.info("rolename:"+rolename);
		logger.info("memo:"+memo);
		logger.info("userNames:"+userNames);
		logger.info("roletype:"+roletype);
		logger.info("proleselector:"+proleselector);
		//
		String[] userNameArr = null;
		if(userNames != null && !"".equals(userNames)){
			userNameArr = userNames.split(",");
		}
		//上级角色
		String[] proles = null;
		if(proleselector != null && !"".equals(proleselector)){
			proles = proleselector.split(",");
		}
		boolean flag = true;
		try {
			rightManageService.addRole(rolename, memo,roletype, userNameArr,proles);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		out.print("{success:"+flag+"}");
		//out.print("{success:true,sql:\""+jsonSql+"\"}");
		//out.print("{success:false,errors:{voicefav_cfee_plan_1_system_type:'voicefav_cfee_plan_1_system_type'}}");

		out.close();
			
		return "";
	}
	
	public String editRole() throws Exception {
		
		String roleid = request.getParameter("roleid");
		String rolename = request.getParameter("rolename");
		String userNames = request.getParameter("userNames");
		String proleselector = request.getParameter("proleselector");
		String memo = request.getParameter("memo");
		String roletype = request.getParameter("roletype");
		
		logger.info("editRole====");
		logger.info("roleid:"+roleid);
		logger.info("rolename:"+rolename);
		logger.info("userNames:"+userNames);
		logger.info("proleselector:"+proleselector);
		logger.info("memo:"+memo);
		logger.info("roletype:"+roletype);
		//
		boolean flag = true;
		try {
			rightManageService.editRole(roleid,rolename,memo,roletype,userNames,proleselector);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		out.print("{success:"+flag+"}");
		//out.print("{success:true,sql:\""+jsonSql+"\"}");
		//out.print("{success:false,errors:{voicefav_cfee_plan_1_system_type:'voicefav_cfee_plan_1_system_type'}}");

		out.close();
			
		return "";
	}
	
	public String delRoles() throws Exception {
		
		String ids = request.getParameter("ids");
		logger.info("del ids:"+ids);
		boolean flag = true;
		try {
			rightManageService.deleteRoles(ids.split(","));
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		out.print("{success:"+flag+"}");
		//out.print("{success:true,sql:\""+jsonSql+"\"}");
		//out.print("{success:false,errors:{voicefav_cfee_plan_1_system_type:'voicefav_cfee_plan_1_system_type'}}");

		out.close();
			
		return "";
	}
	
	public String rightConfig() throws Exception {
/*		Map params = request.getParameterMap();
		Set set = params.keySet();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			System.out.println(element+":"+request.getParameter(element));
		}*/
		roleid = request.getParameter("roleid");
		logger.info("roleid:"+roleid);
/*		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		out.print(rightManageService.getNodeDataJson(roleid));
		//out.print(rightManageService.getNodeDataJson(node,roleid));
		//out.print("{success:true,sql:\""+jsonSql+"\"}");
		//out.print("{success:false,errors:{voicefav_cfee_plan_1_system_type:'voicefav_cfee_plan_1_system_type'}}");

		out.close();*/
		roleNodeDataJs = rightManageService.getNodeDataJson(roleid);
		return SUCCESS;
	}
	
	public String saveRight() throws Exception {
		String rights = request.getParameter("rights");
		String roleid = request.getParameter("roleid");
		logger.info("rights:"+rights);
		logger.info("roleid:"+roleid);
		
		
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		//out.print(rightManageService.getNodeDataJson(node,roleid));
		out.print("{success:"+rightManageService.saveRight(roleid,rights)+"}");
		//out.print("{success:false,errors:{voicefav_cfee_plan_1_system_type:'voicefav_cfee_plan_1_system_type'}}");
	
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

	
	public List getUserList() {
		return userList;
	}

	public void setUserList(List userList) {
		this.userList = userList;
	}

	public List getTopMenu() {
		return topMenu;
	}

	public void setTopMenu(List topMenu) {
		this.topMenu = topMenu;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public List getRoleList() {
		return roleList;
	}

	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}

}
