package com.funtalk.dao.rightmanage;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.funtalk.pojo.rightmanage.User;
import com.funtalk.pojo.rightmanage.UserRole;


public interface UserDao {

	/**
	 * 根据用户ID,获取user对象,登陆用
	 * @param userName
	 * @return
	 */
	User findById(String userName);

	
	/**
	 * 获取密码修改天数
	 * @param userName
	 * @return
	 */
	int getPwdDate(String userName);
	
	/**
	 * 用户最后登陆时间
	 * @param userName
	 * @return
	 */
	String getLastLoginInfo(String userName);
	
	/**
	 * 获取用户可操作功能func
	 * @param userName
	 * @return
	 */
	List getUserFunc(String userName);
	/**
	 * 获取系统所用用户，不分页
	 * @return
	 */
	List getAllUser();
	
	/**
	 * 获取系统所有用户，分页
	 * @param start
	 * @param limit
	 * @param params
	 * @param dir
	 * @param sort
	 * @return
	 */
	List getUsers(int start, int limit, Map params, String dir, String sort);

	/**
	 * 获取系统所有用户数，分页用。
	 * @param params
	 * @return
	 */
	int getUsersCount(Map params);
	
	/**
	 * 获取本省cityList
	 * @param provCode
	 * @return
	 */
	List getProvCityList(String provCode);
	
	/**
	 * 添加用户
	 * @param user
	 */
	void saveUser(User user);
	
	/**
	 * 添加用户角色关系
	 * @param userRole
	 */
	void saveUserRole(UserRole userRole);
	
	/**
	 * 更新用户
	 * @param user
	 */
	void updateUser(User user);
	
	/**
	 * 更新用户角色关系
	 * @param userRole
	 */
	void deleteUserRole(User user);
	/**
	 * 删除用户
	 * @param ids
	 */
	void deleteUsers(String[] ids);
	
	/**
	 * 删除用户角色关系
	 * @param ids
	 */
	void deleteUserRoles(String[] ids);
	
	/**
	 * 删除用户密码历史
	 * @param ids
	 */
	void deleteUserHisPwd(String[] ids);

	/**
	 * 获取所有用户
	 * @return
	 */
	List getAllUsers();


	
}
