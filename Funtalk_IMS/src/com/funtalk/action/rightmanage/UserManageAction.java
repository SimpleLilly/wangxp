/**
 * <p>Title:UsermanageAction.java</p>
 * <p>Description: </p>
 * <p>Company: si-tech</p>
 * @author xuyadong
 * Jul 29, 2008
 */
package com.funtalk.action.rightmanage;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.funtalk.action.FuntalkActionSuport;
import com.funtalk.common.MD5;
import com.funtalk.exception.BusinessException;
import com.funtalk.pojo.rightmanage.CityList;
import com.funtalk.pojo.rightmanage.User;
import com.funtalk.service.rightmanage.RightManageService;
import com.opensymphony.webwork.ServletActionContext;
/**
 * @author xuyadong
 *
 */
public class UserManageAction extends FuntalkActionSuport {

	private Log logger = LogFactory.getLog(UserManageAction.class);
	
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

	
	private List cityList;
	
	private List roleList;
	
	public String list() throws Exception {
		cityList = rightManageService.getProvCityList("100");//////////北京联通 tiantao
		roleList = rightManageService.getAllRoles();
		return SUCCESS;
	}
	
	public String getUsersJsonData() throws Exception {

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
		
		if("username".equals(sort))
			sort = "u.username";
		if("usernamecn".equals(sort))
			sort = "u.usernamecn";
		if("phone".equals(sort))
			sort = "u.phone";
		//email
		if("email".equals(sort))
			sort = "u.email";
		//state
		if("state".equals(sort))
			sort = "u.state";
		//memo
		if("memo".equals(sort))
			sort = "u.memo";
		//cityName
		if("cityName".equals(sort))
			sort = "cList.cityName";
		//rolename
		if("rolename".equals(sort))
			sort = "role.rolename";
		
		Map params = new HashMap();
		if (query !=null&&!"".equals(query)){
			String[] aquery = query.split("&");
			for (int i=0; i < aquery.length; i++){
				String[] aqueryArr = aquery[i].split("=");
				if (aqueryArr.length == 2){
					if(aqueryArr[0].equals("username"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("usernamecn"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("longCode"))
						params.put("cList."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("roleid"))
						params.put("role."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("phone"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("email"))
						params.put("u."+aqueryArr[0], aqueryArr[1]);
					else if(aqueryArr[0].equals("state"))
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
		out.print(rightManageService.getUserListJson(Integer.parseInt(start),Integer.parseInt(limit),params,dir,sort));
		out.close();
			
		return "";
	}

	public String addUser() throws Exception {
		
		String username = request.getParameter("username");
		String usernamecn = request.getParameter("usernamecn");
		String userpwd = request.getParameter("userpwd");
		String repwd = request.getParameter("repwd");
		String longCode = request.getParameter("longCode");
		
		String roleids = request.getParameter("roleids");
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		String memo = request.getParameter("memo");
		
		//
		boolean flag = true;
		try {
			rightManageService.addUser(username, usernamecn, userpwd, longCode, roleids, phone, email, memo);
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
	
	public String editUser() throws Exception {
		
		String username = request.getParameter("username");
		String usernamecn = request.getParameter("usernamecn");
		String userpwd = request.getParameter("userpwd");
		String repwd = request.getParameter("repwd");
		String longCode = request.getParameter("longCode");
		
		String roleids = request.getParameter("roleids");
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		String state = request.getParameter("state");
		String memo = request.getParameter("memo");
		
		//
		boolean flag = true;
		try {
			rightManageService.editUser(username, usernamecn, userpwd, longCode, roleids, phone, email, state, memo);
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
	
	public String delUsers() throws Exception {
		
		String ids = request.getParameter("ids");
		logger.info("del ids:"+ids);
		boolean flag = true;
		try {
			rightManageService.deleteUsers(ids.split(","));
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
	
	public String changepwd() throws Exception {
		return SUCCESS;
	}
	
	public String validpwd() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		
		User user = ((User)session.get("currentUser"));
		MD5 md5 = new MD5();
		String username = request.getParameter("txt_username");
		String pwd      = request.getParameter("txt_pass");
		
		logger.info("====================>user="+user);
		
		logger.info("====================>username="+username);
		logger.info("====================>pwd="+pwd);
		
		if (user == null)
		{
			//处理session失效的问题
			logger.info("======================>in user is null!!!!!");
			
			try{
				user = rightManageService.checkUser(username, pwd);
				session.put("currentUser", user);
			}catch( BusinessException be ){
				out.print("{success:false,message:'"+be.getMessage()+"'}");
				out.close();
				return "";							
			}
			
			logger.info("======================now user is ="+user);
		}
		
		if (!user.getUserpwd().equals( md5.getMD5ofStr(pwd) ) ){
			
			logger.info("======================>ok now!");
			
			out.print("{success:false,message:'密码验证错误!'}");
			out.close();
			return "";
		}else{
			
			logger.info("======================>fail now!");
			
			out.print("{success:true,message:'正确!'}");
			out.close();
			return "";			
		}
	}
	
	public String tochangepwd() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=GB2312");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out=response.getWriter();
		MD5 md5 = new MD5();
		
		User user = ((User)session.get("currentUser"));
		String oldPwd = request.getParameter("txt_pass");
		String newPwd = request.getParameter("txt_pass1");
		
		if (!user.getUserpwd().equals( md5.getMD5ofStr(oldPwd) ) ){
			out.print("{success:false,message:'旧密码输入不正确!'}");
			out.close();
			return "";
		}
		if (oldPwd.equals(newPwd) ){
			out.print("{success:false,message:'新密码不能与旧密码相同!'}");
			out.close();
			return "";
		}
		if ( !rightManageService.isValidPwd(newPwd, user) ){
			out.print("{success:true,message:'新密码设置重复,请重新选择密码!'}");
			out.close();
			return "";
		}
		rightManageService.changePwd(oldPwd,newPwd, user);
		out.print("{success:true,message:'密码修改成功!'}");
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
	
	public List getCityList() {
		return cityList;
	}

	public void setCityList(List cityList) {
		this.cityList = cityList;
	}

	public String getQuery() {
		return query; 
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List getRoleList() {
		return roleList;
	}

	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}

}
