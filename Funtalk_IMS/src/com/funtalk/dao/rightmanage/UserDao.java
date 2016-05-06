package com.funtalk.dao.rightmanage;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.funtalk.pojo.rightmanage.User;
import com.funtalk.pojo.rightmanage.UserRole;


public interface UserDao {

	/**
	 * �����û�ID,��ȡuser����,��½��
	 * @param userName
	 * @return
	 */
	User findById(String userName);

	
	/**
	 * ��ȡ�����޸�����
	 * @param userName
	 * @return
	 */
	int getPwdDate(String userName);
	
	/**
	 * �û�����½ʱ��
	 * @param userName
	 * @return
	 */
	String getLastLoginInfo(String userName);
	
	/**
	 * ��ȡ�û��ɲ�������func
	 * @param userName
	 * @return
	 */
	List getUserFunc(String userName);
	/**
	 * ��ȡϵͳ�����û�������ҳ
	 * @return
	 */
	List getAllUser();
	
	/**
	 * ��ȡϵͳ�����û�����ҳ
	 * @param start
	 * @param limit
	 * @param params
	 * @param dir
	 * @param sort
	 * @return
	 */
	List getUsers(int start, int limit, Map params, String dir, String sort);

	/**
	 * ��ȡϵͳ�����û�������ҳ�á�
	 * @param params
	 * @return
	 */
	int getUsersCount(Map params);
	
	/**
	 * ��ȡ��ʡcityList
	 * @param provCode
	 * @return
	 */
	List getProvCityList(String provCode);
	
	/**
	 * ����û�
	 * @param user
	 */
	void saveUser(User user);
	
	/**
	 * ����û���ɫ��ϵ
	 * @param userRole
	 */
	void saveUserRole(UserRole userRole);
	
	/**
	 * �����û�
	 * @param user
	 */
	void updateUser(User user);
	
	/**
	 * �����û���ɫ��ϵ
	 * @param userRole
	 */
	void deleteUserRole(User user);
	/**
	 * ɾ���û�
	 * @param ids
	 */
	void deleteUsers(String[] ids);
	
	/**
	 * ɾ���û���ɫ��ϵ
	 * @param ids
	 */
	void deleteUserRoles(String[] ids);
	
	/**
	 * ɾ���û�������ʷ
	 * @param ids
	 */
	void deleteUserHisPwd(String[] ids);

	/**
	 * ��ȡ�����û�
	 * @return
	 */
	List getAllUsers();


	
}
