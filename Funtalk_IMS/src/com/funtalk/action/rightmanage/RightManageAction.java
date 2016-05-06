package com.funtalk.action.rightmanage;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.funtalk.action.FuntalkActionSuport;
import com.funtalk.common.WriteLog;
import com.funtalk.exception.BusinessException;
import com.funtalk.pojo.rightmanage.User;
import com.funtalk.service.rightmanage.RightManageService;


public class RightManageAction extends FuntalkActionSuport {

	private Log logger = LogFactory.getLog(RightManageAction.class);
	
	String userName;
	
	String passWord;
	
	String agentIp;
	
	String extMenuInfo;
	
	List topMenu;
	
	private RightManageService rightManageService;
	
	public RightManageService getRightManageService() {
		return rightManageService;
	}

	public void setRightManageService(RightManageService rightManageService) {
		this.rightManageService = rightManageService;
	}

	public String login() throws Exception {
		try {
			
			User user ;
			/*if ( session.get("currentUser") != null && session.get("ww_locale") != null  ){
				user = ((User)session.get("currentUser"));
				userName = user.getUsername();
			}else{
				
			user = rightManageService.checkUser(userName, passWord);
			}*/
			
			user = rightManageService.checkUser(userName, passWord);

		/*	if(user.getPwdDate() >= 80){
				addActionMessage("您的密码还有"+(90 - user.getPwdDate())+"天过期,请及时修改密码!");
			} */
			//ExtMenuInfo
			extMenuInfo = rightManageService.getExtMenuInfo(userName,request.getContextPath());
			//TopMenu 
			topMenu = rightManageService.getTopMenu(userName);
			List userFunc = rightManageService.getUserFuncList(userName);
			
			session.put("currentUser", user);
			session.put("userFunc", userFunc);
			
			WriteLog.dbLog("L", "用户登陆", "1", "");

			return SUCCESS;
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			addActionMessage(e.getMessage());
		}
		catch (Exception e) {
			logger.error("in login: error: "+e.getMessage());
			addActionMessage(e.getMessage());
		}

		return INPUT;
	}
	public String logout() throws Exception {
		session = null;
		request.getSession().invalidate();
		return SUCCESS;
	}
	
/*	public String userList() throws Exception {
		// TODO Auto-generated method stub
		return super.execute();
	}*/

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAgentIp() {
		return agentIp;
	}

	public void setAgentIp(String agentIp) {
		this.agentIp = agentIp;
	}

	public String getExtMenuInfo() {
		return extMenuInfo;
	}

	public void setExtMenuInfo(String extMenuInfo) {
		this.extMenuInfo = extMenuInfo;
	}

	public List getTopMenu() {
		return topMenu;
	}

	public void setTopMenu(List topMenu) {
		this.topMenu = topMenu;
	}


}
