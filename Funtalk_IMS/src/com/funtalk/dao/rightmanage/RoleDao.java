package com.funtalk.dao.rightmanage;

import java.util.List;
import java.util.Map;

import com.funtalk.pojo.rightmanage.Role;
import com.funtalk.pojo.rightmanage.RoleLevel;
import com.funtalk.pojo.rightmanage.UserRole;


public interface RoleDao {

	/*
	 * 获取系统所有角色
	 */
	List getAllRoles();

	List getRoles(int start, int limit, Map params, String dir, String sort);

	int getRolesCount(Map params);

	void saveRole(Role role);

	void saveUserRole(UserRole userRole);

	void deleteUserRoles(Long[] idsLong);
	
	void deleteRoleFunc(Long[] ids);
	
	void saveRoleLevel(RoleLevel roleLevel);

	void deleteRoleLevel(Role role) ;
	
	void deleteRoleLevel(Long[] idsLong) ;
	
	void deleteRoles(Long[] idsLong);

	void updateRole(Role role);

	void deleteUserRole(Role role);

	List getNodeInfo(String node, Long roleid);
	
	//List getRoleFunc(String roleid);

	List getNodeInfo(long roleid);

	int deleteRoleFunc(long roleid);

	void addRoleFunc(long roleid, List funcList);
	
}
