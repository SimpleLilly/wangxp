package com.funtalk.service.rightmanage;

import java.util.List;
import java.util.Map;

import com.funtalk.pojo.rightmanage.User;


public interface RightManageService {
	/**
	 * ��֤�û�
	 * @param userName
	 * @param passWord
	 * @return
	 */
	User checkUser(String userName, String passWord);

	/**
	 * ��ȡleft���ܲ˵�
	 * @param userName
	 * @param contextPath
	 * @return
	 */
	String getExtMenuInfo(String userName, String contextPath);

	/**
	 * top�˵�
	 * @param userName
	 * @return
	 */
	List getTopMenu(String userName);
	
	/**
	 * ��ȡ�û���Ϣ�б�Json����
	 * @param start
	 * @param limit
	 * @param params
	 * @param dir
	 * @param sort
	 * @return
	 */
	String getUserListJson(int start, int limit, Map params, String dir, String sort);
	
	/**
	 * ��ȡ��ʡcityList
	 * @param provCode
	 * @return
	 */
	List getProvCityList(String provCode);
	
	/**
	 * ��ȡϵͳ�ڽ�ɫ�б�
	 * @return
	 */
	List getAllRoles();

	void addUser(User user);

	/**
	 * ����û�
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
	 * ɾ��ѡ���û�
	 * @param ids
	 */
	void deleteUsers(String[] ids);

	/**
	 * �����û�
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
	 * ��ȡ��ɫ��Ϣ�б�json����
	 * @param start
	 * @param limit
	 * @param params
	 * @param dir
	 * @param sort
	 * @return
	 */
	String getRoleListJson(int start, int limit, Map params, String dir, String sort);

	/**
	 * ��ȡϵͳ���û��б�
	 * @return
	 */
	List getAllUsers();

	/**
	 * ��ӽ�ɫ
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
	 * ���е�top�˵�
	 * @return
	 */
/*	List getTopMenu();*/

	boolean saveRight(String roleid, String rights);
	
	/**
	 * �õ��û����пɷò��˵�
	 * @param username
	 * @return
	 */
	List getUserFuncList(String username);
	
	/**
	 * �õ��û��Ըò˵���Ȩ��
	 * @param username
	 * @param funccode
	 * @return
	 */
	Map getUserFuncRight(String username, String funccode);
	
	/**
	 * ��֤��ĳ����ť�Ƿ���Ȩ��
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
