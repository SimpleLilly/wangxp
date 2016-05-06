package com.funtalk.service.rightmanage;

import java.util.List;
import java.util.Map;

import com.funtalk.pojo.rightmanage.User;


public interface RightManageService {
	/**
	 * 验证用户
	 * @param userName
	 * @param passWord
	 * @return
	 */
	User checkUser(String userName, String passWord);

	/**
	 * 获取left功能菜单
	 * @param userName
	 * @param contextPath
	 * @return
	 */
	String getExtMenuInfo(String userName, String contextPath);

	/**
	 * top菜单
	 * @param userName
	 * @return
	 */
	List getTopMenu(String userName);
	
	/**
	 * 获取用户信息列表Json数据
	 * @param start
	 * @param limit
	 * @param params
	 * @param dir
	 * @param sort
	 * @return
	 */
	String getUserListJson(int start, int limit, Map params, String dir, String sort);
	
	/**
	 * 获取本省cityList
	 * @param provCode
	 * @return
	 */
	List getProvCityList(String provCode);
	
	/**
	 * 获取系统内角色列表
	 * @return
	 */
	List getAllRoles();

	void addUser(User user);

	/**
	 * 添加用户
	 * @param username
	 * @param usernamecn
	 * @param userpwd
	 * @param longCode
	 * @param roleids
	 * @param phone
	 * @param email
	 * @param memo
	 */
	void addUser(String username, String usernamecn, String userpwd, String longCode, String roleids, String phone, String email, String memo);
	
	/**
	 * 删除选中用户
	 * @param ids
	 */
	void deleteUsers(String[] ids);

	/**
	 * 更新用户
	 * @param username
	 * @param usernamecn
	 * @param userpwd
	 * @param longCode
	 * @param roleids
	 * @param phone
	 * @param email
	 * @param state
	 * @param memo
	 */
	void editUser(String username, String usernamecn, String userpwd, String longCode, String roleids, String phone, String email, String state, String memo);

	/**
	 * 获取角色信息列表json数据
	 * @param start
	 * @param limit
	 * @param params
	 * @param dir
	 * @param sort
	 * @return
	 */
	String getRoleListJson(int start, int limit, Map params, String dir, String sort);

	/**
	 * 获取系统内用户列表
	 * @return
	 */
	List getAllUsers();

	/**
	 * 添加角色
	 * @param rolename
	 * @param memo
	 * @param userNameArr
	 */
	void addRole(String rolename, String memo,String roletype, String[] userNameArr, String[] prole);

	void deleteRoles(String[] strings);

	void editRole(String roleid, String rolename, String memo,String roletype, String userNames, String proles);
	
	/**
	 * 
	 * @param node
	 * @param roleid 
	 * @return
	 */
	String getNodeDataJson(String node, String roleid);
	
	String getNodeDataJson(String roleid);

	/**
	 * 所有的top菜单
	 * @return
	 */
/*	List getTopMenu();*/

	boolean saveRight(String roleid, String rights);
	
	/**
	 * 得到用户所有可访部菜单
	 * @param username
	 * @return
	 */
	List getUserFuncList(String username);
	
	/**
	 * 得到用户对该菜单的权限
	 * @param username
	 * @param funccode
	 * @return
	 */
	Map getUserFuncRight(String username, String funccode);
	
	/**
	 * 验证对某个按钮是否有权限
	 * @param username
	 * @param funccode
	 * @param button
	 * @return
	 */
	boolean isRight(String username, String funccode, String button);
	
	void changePwd(String oldpwd,String newpwd,User user);
	
	boolean isValidPwd(String newpwd,User user);
	
	String getLogListJson(int start, int limit, Map params, String dir, String sort) ;
}
