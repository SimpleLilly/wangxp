package com.funtalk.service.rightmanage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import com.funtalk.common.MD5;
import com.funtalk.common.SpringContextUtil;
import com.funtalk.common.WriteLog;
import com.funtalk.dao.rightmanage.RoleDao;
import com.funtalk.dao.rightmanage.UserDao;
import com.funtalk.exception.BusinessException;
import com.funtalk.pojo.rightmanage.CityList;
import com.funtalk.pojo.rightmanage.Func;
import com.funtalk.pojo.rightmanage.IlogDAO;
import com.funtalk.pojo.rightmanage.Node;
import com.funtalk.pojo.rightmanage.Role;
import com.funtalk.pojo.rightmanage.RoleFunc;
import com.funtalk.pojo.rightmanage.RoleFuncDAO;
import com.funtalk.pojo.rightmanage.RoleFuncId;
import com.funtalk.pojo.rightmanage.RoleLevel;
import com.funtalk.pojo.rightmanage.RoleLevelId;
import com.funtalk.pojo.rightmanage.User;
import com.funtalk.pojo.rightmanage.UserPwdHistory;
import com.funtalk.pojo.rightmanage.UserPwdHistoryDAO;
import com.funtalk.pojo.rightmanage.UserPwdHistoryId;
import com.funtalk.pojo.rightmanage.UserRole;
import com.funtalk.pojo.rightmanage.UserRoleId;
import com.funtalk.common.json.DateJsonValueProcessor;


public class RightManageServiceImpl implements RightManageService {
	
	private static final Log log = LogFactory.getLog(RightManageServiceImpl.class);

	UserDao userDao;
	
	RoleDao roleDao;
	
	RoleFuncDAO roleFuncDao;
	
	UserPwdHistoryDAO userPwdHistoryDAO;

	List userFuncs;
	
	Map LoginErrorMap = new HashMap();//存放密码输入错误次数
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public RoleDao getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}
	
	public User checkUser(String userName, String passWord) {
		User user = null;
		try {
			user = userDao.findById(userName);
		} catch (Exception e) {
			throw new BusinessException("系统忙！请稍后再试!");
		}
		
		if(user == null){
			throw new BusinessException("没有此用户，请重新输入用户名和密码!");
		}else{
			
		/*	if(user.getState().equals("1")){
				throw new BusinessException("此用户已被停用，请联系管理员!");
			}*/
			
			MD5 md5 = new MD5();
			if(!user.getUserpwd().equals(md5.getMD5ofStr(passWord))){
				
			/*	int loginErrorNum =1;
				if  ( LoginErrorMap.containsKey(user.getUsername())){
					loginErrorNum=Integer.parseInt(LoginErrorMap.get(user.getUsername()).toString())+1;
				}
				log.debug("getUsername="+user.getUsername()+" 登陆密码输入错误次数:"+loginErrorNum);
				LoginErrorMap.put(user.getUsername(), new Integer(loginErrorNum));
				if (loginErrorNum>=3){//密码输入次数超过三次锁定用户
					user.setState("1");
					userDao.updateUser(user);
					
					throw new BusinessException("密码输入错误次数超过限制，此用户已被停用，请联系管理员!");
				}*/
				throw new BusinessException("密码错，请重新输入密码!");
			}
			
			int pwdDate = userDao.getPwdDate(userName);
			user.setPwdDate(pwdDate);
			//设置密码到期日期
			Calendar calendar =  Calendar.getInstance();
			calendar.add(Calendar.DATE, 90-pwdDate);
			user.setPwdLastModifyDate(calendar.getTime() );
			//设置最后登陆日期
			user.setLastLoginInfo(userDao.getLastLoginInfo(userName));
			
		/*	if(pwdDate >= 90){
				throw new BusinessException("此用户密码已过期，请联系管理员!");
			}*/
		}
		
		//// LoginErrorMap.remove(user.getUsername());
		
		
		return user;
	}

	public List getUserFuncList(String username) {
    	if(userFuncs == null){
        	userFuncs = userDao.getUserFunc(username);
    	}
    	return userFuncs;
	}
	
	private List getUserFunc(String userName){
		userFuncs = userDao.getUserFunc(userName);
		return getUserRightFuncInfo("-1", userFuncs);
	}
	
    /**
     * 组装菜单
     * @param funcPCode 上级编号
     * @param funcList 菜单集合
     * @return
     */
	private List getUserRightFuncInfo(String funcPCode, List funcList) {
		ArrayList al = new ArrayList();
		for (Iterator iter = funcList.iterator(); iter.hasNext();) {
			Func func = (Func) iter.next();
			if(funcPCode.equals(func.getFuncpcode())){
				func.setChildFunc(getUserRightFuncInfo(func.getFunccode(),funcList));
				al.add(func);
			}

		}
		return al;
	}
	
	/**
	 * 输出ext菜单树
	 * @param roleId
	 * @return
	 */
	public String getExtMenuInfo(String userName,String contextPath){
		String content ="";
		List topMenu = getUserFunc(userName);
		for (int i = 0; i < topMenu.size(); i++) {
			Func funcBean = (Func)topMenu.get(i);
			String children ="";
			String leaf=" leaf:true \n";
			if (funcBean.getChildFunc() != null && funcBean.getChildFunc().size() > 0){
				children=" children:["+getChildinfo(funcBean.getChildFunc(),contextPath)+"], \n";
				leaf=" leaf:false \n";
			}
			content += " var "+funcBean.getFunccode()+" = new Ext.tree.AsyncTreeNode({ \n"+
					" id:'"+funcBean.getFunccode()+"',\n"+
					" text:'"+funcBean.getFuncname()+"',\n"+
					" expanded:true,\n"+
					" singleClickExpand:true,\n"+
					" edit:false,\n"+
					children+
					leaf+
					"  });\n";
		}
		return content;
	}
	
	
	/**
	 * 递归调用生成子树
	 * @param childList
	 * @return
	 */
	private String getChildinfo( List childList,String contextPath){
		String content = " ";
		for (int i = 0; i < childList.size(); i++) {
			Func funcBean = (Func)childList.get(i);
			String children ="";
			String leaf="\t leaf:true \n";
			String href="";
			
			///////////////2009-5-20 liaoxb对于/的url不生成url
			if (funcBean.getPagecode()!=null && funcBean.getPagecode().trim().length()==1 && funcBean.getPagecode().charAt(0)=='/' )
			{
				href=" ";
			}
			else if (funcBean.getPagecode().indexOf("?")>=0)
				href="\t href:'"+ contextPath + funcBean.getPagecode()+"&funccode="+funcBean.getFunccode()+"', \n";
			else
				href="\t href:'"+ contextPath + funcBean.getPagecode()+"?funccode="+funcBean.getFunccode()+"', \n";
			
			
			if (funcBean.getChildFunc() != null && funcBean.getChildFunc().size() > 0){
				children="\t children:[ \n "+getChildinfo(funcBean.getChildFunc(),contextPath)+"], \n";
				leaf=" singleClickExpand:true,\n"+"\t leaf:false \n";
				href="";//菜单单击可以收缩
			}
			
			content +=" {  \n"+
				href+
				"\t text:'"+funcBean.getFuncname()+"', \n"+
				"\t id:'"+funcBean.getFunccode()+"', \n"+
				children+
				leaf+
				" },";
		}
		content = content.substring(0, content.length()-1);//去掉最后一个","号
		content +=" \n";
		return content;
	}
	
    public List getTopMenu(String userName){
    	if(userFuncs == null){
        	userFuncs = userDao.getUserFunc(userName);
    	}
    	List ls = new ArrayList();
    	for (Iterator iter = userFuncs.iterator(); iter.hasNext();) {
			Func func = (Func) iter.next();
			if(func.getFuncpcode().equals("-1")){
				ls.add(func);
			}
		}
    	return ls;
    }
    
	public String getUserListJson(int start, int limit, Map params, String dir, String sort) {
    	
    	//List ls = userDao.getAllUser();
    	 
    	List ls = userDao.getUsers(start, limit, params, dir, sort);
    	//去除重复
    	List users = new ArrayList();
    	for (Iterator iter = ls.iterator(); iter.hasNext();) {
    		boolean addFlag = true;
			User user = (User) iter.next();
			for(int i = 0; i < users.size(); i++){
				if(user.getUsername().equals(((User)users.get(i)).getUsername())){
					addFlag = false;
					break;
				}
			}
			if(addFlag){
				users.add(user);
			}

		}
    	JsonConfig jsonConfig = new JsonConfig();

/*		jsonConfig.registerJsonBeanProcessor(org.hibernate.proxy.HibernateProxy.class,
				new HibernateJsonBeanProcessor());
		jsonConfig.setJsonBeanProcessorMatcher(new HibernateJsonBeanProcessorMatcher());*/
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (name.equals("username") || name.equals("usernamecn")
                		||name.equals("phone") ||name.equals("email") 
                		||name.equals("state") ||name.equals("memo")
                		||name.equals("cityList") ||name.equals("longCode") 
                		||name.equals("provCode") ||name.equals("cityName")
                		||name.equals("TUserRoles") ||name.equals("rolename")
                		||name.equals("TRole") ||name.equals("roleid")) {
                    return false;
                }
                return true;
            }
        });
    	
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\"totalCount\":\""+userDao.getUsersCount(params)+"\",\"rows\":");
		buffer.append(JSONSerializer.toJSON(users, jsonConfig));
		buffer.append("}");
		return buffer.toString();
    }

	public List getProvCityList(String provCode) {
		return userDao.getProvCityList(provCode);
	}
	
	public List getAllRoles() {
		return roleDao.getAllRoles();
	}

	public void addUser(User user) {
/*		MD5 md5 = new MD5();
		md5.getMD5ofStr(userpwd);
		userDao.saveUser(user);*/
	}

	public void addUser(String username, String usernamecn, String userpwd, String longCode, String roleids, String phone, String email, String memo) {
		
		User user = new User();
		user.setUsername(username);
		user.setUsernamecn(usernamecn);
		MD5 md5 = new MD5();
		user.setUserpwd(md5.getMD5ofStr(userpwd));
		CityList city = new CityList();
		city.setLongCode(longCode);
		user.setCityList(city);
		user.setPhone(phone);
		user.setEmail(email);
		user.setState("0");
		user.setMemo(memo);
		userDao.saveUser(user);
		String[] arr = roleids.split(",");
		for (int i = 0; i < arr.length; i++) {
			UserRole userRole = new UserRole();
			UserRoleId userRoleId = new UserRoleId(new Role(new Long(arr[0])),new User(user.getUsername()));
			userRole.setId(userRoleId);
			userDao.saveUserRole(userRole);
		}
		WriteLog.dbLog("C", "用户管理", "1", "添加用户:"+user.getUsername());
	}

	public void deleteUsers(String[] ids) {
		userDao.deleteUserRoles(ids);
		userDao.deleteUserHisPwd(ids);
		userDao.deleteUsers(ids);
		WriteLog.dbLog("D", "用户管理", "1", "删除用户:"+ids);
	}

	public void editUser(String username, String usernamecn, String userpwd, String longCode, String roleids, String phone, String email, String state, String memo) {
		
		User user = userDao.findById(username);
		user.setUsernamecn(usernamecn);
		if(!userpwd.equals("******")){
			MD5 md5 = new MD5();

			UserPwdHistory userPwdHistory = new UserPwdHistory();
			UserPwdHistoryId userPwdHistoryId = new UserPwdHistoryId(user, user.getUserpwd(), new Date());
			userPwdHistory.setId(userPwdHistoryId);
			userPwdHistoryDAO.save(userPwdHistory);
			
			user.setUserpwd(md5.getMD5ofStr(userpwd));
		}
		CityList city = new CityList();
		city.setLongCode(longCode);
		user.setCityList(city);
		user.setPhone(phone);
		user.setEmail(email);
		user.setState(state);
		user.setMemo(memo);
		userDao.updateUser(user);
		String[] arr = roleids.split(",");
		userDao.deleteUserRole(user);
		for (int i = 0; i < arr.length; i++) {
			UserRole userRole = new UserRole();
			UserRoleId userRoleId = new UserRoleId(new Role(new Long(arr[0])),user);
			userRole.setId(userRoleId);
			userDao.saveUserRole(userRole);
		}
		WriteLog.dbLog("U", "用户管理", "1", "修改用户信息:"+user.getUsername());
	}

	public String getRoleListJson(int start, int limit, Map params, String dir, String sort) {
   	 
    	List ls = roleDao.getRoles(start, limit, params, dir, sort);
    	//去除重复
    	List roles = new ArrayList();
    	for (Iterator iter = ls.iterator(); iter.hasNext();) {
    		boolean addFlag = true;
			Role role = (Role) iter.next();
			for(int i = 0; i < roles.size(); i++){
				if(role.getRoleid().equals(((Role)roles.get(i)).getRoleid())){
					addFlag = false;
					break;
				}
			}
			if(addFlag){
				//加入上下级信息开始
				Object[] proles = role.getTRoleLevels().toArray();
				Set pset = new HashSet();
				for (int i=0;i<proles.length;i++){
					RoleLevel prole = (RoleLevel)proles[i];
					log.debug("prole.getId().getPRoleid())===="+prole.getId().getPRoleid());
					Role rr = new Role();
					rr.setRoleid(prole.getTRole().getRoleid());
					rr.setRoletype(prole.getTRole().getRoletype());
					rr.setMemo(prole.getTRole().getMemo());
					rr.setRolename(prole.getTRole().getRolename());
					pset.add(rr);
				}
				role.setTRoleLevels(pset);
				//	加入上下级信息结束
				roles.add(role);
			}

		}
    	JsonConfig jsonConfig = new JsonConfig();

        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (name.equals("roleid") || name.equals("rolename")
                		||name.equals("memo") ||name.equals("userRoles") 
                		||name.equals("state") ||name.equals("memo")||name.equals("roletype")
                		||name.equals("TUser")||name.equals("TRoleLevels")   ||name.equals("username")) {
                    return false;
                }
                return true;
            }
        });
    	
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\"totalCount\":\""+roleDao.getRolesCount(params)+"\",\"rows\":");
		buffer.append(JSONSerializer.toJSON(roles, jsonConfig));
		buffer.append("}");
		log.debug("====buffer.toString()========"+buffer.toString());
		return buffer.toString();
	}

	public List getAllUsers() {
		return userDao.getAllUsers();
	}

	public void addRole(String rolename, String memo,String roletype, String[] userNameArr, String[] prole) {
		Role role = new Role();
		
		role.setRolename(rolename);
		role.setMemo(memo);
		role.setRoletype(roletype);
		roleDao.saveRole(role);
		if(userNameArr != null){
			for (int i = 0; i < userNameArr.length; i++) {
				UserRole userRole = new UserRole();
				UserRoleId userRoleId = new UserRoleId(role,new User(userNameArr[i]));
				userRole.setId(userRoleId);
				roleDao.saveUserRole(userRole);
			}	
		}
		if(prole != null){
			for (int i = 0; i < prole.length; i++) {
				RoleLevel roleLevel = new RoleLevel();
				RoleLevelId userRoleId = new RoleLevelId(Long.valueOf(prole[i]),role.getRoleid());
				roleLevel.setId(userRoleId);
				roleDao.saveRoleLevel(roleLevel);
			}	
		}
		WriteLog.dbLog("C", "角色管理", "1", "添加角色:"+role.getRolename());

	}

	public void deleteRoles(String[] ids) { 
		Long[] idsLong = new Long[ids.length];
		for (int i = 0; i < ids.length; i++) {
			idsLong[i] = new Long(ids[i]);
			WriteLog.dbLog("D", "角色管理", "1", "删除角色:"+idsLong[i]);
		}
		roleDao.deleteRoleLevel(idsLong);
		roleDao.deleteUserRoles(idsLong);
		roleDao.deleteRoleFunc(idsLong);
		roleDao.deleteRoles(idsLong);
	}

	public void editRole(String roleid, String rolename, String memo,String roletype, String userNames, String proles) {
		Role role = new Role();
		role.setRoleid(new Long(roleid));
		role.setRolename(rolename);
		role.setMemo(memo);
		role.setRoletype(roletype);
		roleDao.updateRole(role);

		String[] arr = userNames.split(",");
		if(arr.length > 0){
			roleDao.deleteUserRole(role);
			for (int i = 0; i < arr.length; i++) {
				UserRole userRole = new UserRole();
				UserRoleId userRoleId = new UserRoleId(role,new User(arr[i]));
				userRole.setId(userRoleId);
				
				roleDao.saveUserRole(userRole);
			}
		}
		String[] prolearr = proles.split(",");
		if(prolearr.length > 0){
			roleDao.deleteRoleLevel(role);
			for (int i = 0; i < prolearr.length; i++) {
				if (!"".equals(prolearr[i])){
					RoleLevel roleLevel = new RoleLevel();
					RoleLevelId userRoleId = new RoleLevelId(Long.valueOf(prolearr[i]),role.getRoleid());
					roleLevel.setId(userRoleId);
					roleDao.saveRoleLevel(roleLevel);
				}
			}
		}
		WriteLog.dbLog("U", "角色管理", "1", "修改角色:"+role.getRolename());
	}
	///不用这个方法了
/*	public String getNodeDataJson(String nodeStr, String roleid) {
		List ls = roleDao.getNodeInfo(nodeStr, new Long(roleid));
		List retList = new ArrayList();
		for (Iterator iter = ls.iterator(); iter.hasNext();) {
			Object[] element = (Object[])iter.next();
			Node node = new Node();
			node.setId((String)element[0]);
			node.setText((String)element[1]);
			//node.setCls((String)element[2]);
			node.setLeaf((((String)element[2]).equals("0"))? false : true);
			node.setChecked((((String)element[3]).equals("0"))? false : true);
			
			node.setButtons((String)element[4]);
			node.setButtons_def((String)element[5]);
			retList.add(node);
		}

		return JSONArray.fromObject(retList).toString();
	}*/

	public String getNodeDataJson(String roleid) {
		//List ls = roleDao.getNodeInfo(Long.parseLong(roleid));
		List ls = getRoleFunc(roleid);
		String retStr = "";
		String items = "";
		for (Iterator iter = ls.iterator(); iter.hasNext();) {
			
			Node node = (Node) iter.next();
			retStr += "var treePanel"+node.getId()+" = new Ext.tree.TreePanel({" +
			"id:'"+node.getId()+"'," +
			//"renderTo:'treePanel"+node.getId()+"'," +
			"title:'"+node.getText()+"'," +
			"border : false," +
			//"autoShow : true," +
			"rootVisible:true," +
			"autoScroll:true," +
			//"collapseFirst :false," +
			//"collapsed:false," +
			 //onlyLeafCheckable: true,
			//"deferredRender:false," +
			//"layoutOnTabChange:true,"+
			"checkModel: 'cascade'," +
			"containerScroll: true," +
			"animate:false," +
			"loader: new Ext.tree.TreeLoader({" +
			  			//dataUrl:'<%=path%>/rightmanage/rightConfig.jspa?roleid='+roleid,
			           "baseAttrs: { uiProvider: Ext.ux.TreeCheckNodeUI }" + //添加 uiProvider 属性
			       "})" +//,
			  //collapseFirst:true
			"});"+
			// set the root node
			"var root"+node.getId()+" = new Ext.tree.AsyncTreeNode({" +
					"uiProvider: Ext.ux.TreeCheckNodeUI," +
					//"expanded:true," +
			JSONObject.fromObject( node ).toString().substring(1) +
							");" +
							//"root"+node.getId()+".expandChildNodes();"+
			"treePanel"+node.getId()+".setRootNode(root"+node.getId()+");";
			//var root<ww:property value="#rowstatus.index"/> = new Ext.tree.AsyncTreeNode(
			items += "treePanel"+node.getId()+",";
		}
		retStr +=
			"var treeTabPanel = new Ext.TabPanel({" +//主内容区域
			    "id:'treeTabPanel'," +
			    //autoHeight: false,
			    "renderTo:'treeTabPanel'," +
			    "resizeTabs:true, " +// turn on tab resizing
			    //"enableTabScroll:true," +
			    //"hideMode:'visibility'," +
			    //"resizeTabs:true," +
			    //autoScroll :true,
			  //containerScroll :true,
			    "height:350," +
			    //bodyStyle:'padding:5px',
		 	   // "listeners:{\"tabchange\":function(thisObj,tabPanel){" +
			    		//FeePlanTabActive(thisObj,tabPanel);
			    		//alert(tabPanel.getRootNode().getPath());
			    		//alert(tabPanel.getRootNode().childNodes.length);
			    		//"tabPanel.expandPath(tabPanel.getId());" +
			    		//"alert(tabPanel.getChecked().length);
		 	    		//"tabPanel.expandAll();"+
			    //	"}" +
			    //"}," +
			    //"activeTab: 0," +
			    "tbar:[{pressed:true,text: '保存',handler:rightsave}]," +
			    "items:["+items.substring(0,items.length()-1)+"]," +
			    //minTabWidth: 90,
			    "tabWidth : 100" +
			"});";
		//retStr += "treeTabPanel.doLayout();" +
				//"treeTabPanel.render();";
		for (Iterator iter = ls.iterator(); iter.hasNext();) {
			Node node = (Node) iter.next();
			//retStr += "treeTabPanel.add(treePanel"+node.getId()+");";
			//retStr += "root"+node.getId()+".expandChildNodes();";
			//retStr += "treePanel"+node.getId()+".expandAll();";
			retStr += "treeTabPanel.setActiveTab(treePanel"+node.getId()+");";
			//retStr += "treePanel"+node.getId()+".render(treePanel"+node.getId()+");";
			//retStr += "treePanel"+node.getId()+".doLayout();";
			retStr += "treePanel"+node.getId()+".expandAll();";
			//retStr += "treePanel"+node.getId()+".collapseAll();";
			
		}
		retStr += "treeTabPanel.setActiveTab(0);";
		return retStr;
	}
	
	private List getRoleFunc(String roleid){
		List ls = roleDao.getNodeInfo(Long.parseLong(roleid));
		List roleFuncs = new ArrayList();
		for (Iterator iter = ls.iterator(); iter.hasNext();) {
			Object[] element = (Object[])iter.next();
			Node node = new Node();
			node.setFuncPcode((String)element[0]);
			node.setId((String)element[1]);
			node.setText((String)element[2]);
			//node.setCls((String)element[2]);
			node.setLeaf((((String)element[3]).equals("0"))? false : true);
			node.setChecked((((String)element[4]).equals("0"))? false : true);
			//node.setExpanded(true);
			node.setButtons((String)element[5]);
			node.setButtons_def((String)element[6]);
			roleFuncs.add(node);
		}
		return getRoleRightFuncInfo("-1", roleFuncs);
	}
	
    /**
     * 组装菜单
     * @param funcPCode 上级编号
     * @param funcList 菜单集合
     * @return
     */
	private List getRoleRightFuncInfo(String funcPCode, List funcList) {
		ArrayList al = new ArrayList();
		for (Iterator iter = funcList.iterator(); iter.hasNext();) {
			Node node = (Node) iter.next();
			if(funcPCode.equals(node.getFuncPcode())){
				node.setChildren(getRoleRightFuncInfo(node.getId(),funcList));
				al.add(node);
			}

		}
		return al;
	}
	
/*	public List getTopMenu() {
		return roleDao.getTopMenu();
	}*/

	public boolean saveRight(String roleid, String rights) {
		boolean returnFlag = false;
		String[] checkboxArr = rights.split(";");
		//roleDao.getRoleFunc(roleid);
		
		Map funcMap = new LinkedHashMap();
		try {
			for (int i = 0; i < checkboxArr.length; i++) {
				String[] arr = checkboxArr[i].split(",");
				if(arr.length == 1){
					funcMap.put(arr[0], new ArrayList());
				}else{
					//只有节点checkbox选中时,才加功能按钮
					if(funcMap.containsKey(arr[0])){
						((List)funcMap.get(arr[0])).add(arr[1]);
					}
					
				}
			}
/*			Set keySet = funcMap.keySet();
			for (Iterator iter = keySet.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				List ls = (List)funcMap.get(key);
				System.out.print(key+":");
				for (Iterator iterator = ls.iterator(); iterator.hasNext();) {
					String element = (String) iterator.next();
					System.out.print(element+",");
					
				}
				System.out.println();
			}*/
			Set keySet = funcMap.keySet();
			List funcList = new ArrayList();
			for (Iterator iter = keySet.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				List ls = (List)funcMap.get(key);
				String buttons = "";
				for (Iterator iterator = ls.iterator(); iterator.hasNext();) {
					String element = (String) iterator.next();
					buttons += element;
					if(iterator.hasNext())
						buttons+=",";
				}
				RoleFunc func = new RoleFunc();
				RoleFuncId id = new RoleFuncId();
				id.setTRole(new Role(new Long(roleid)));
				id.setFunccode(key);
				id.setButtons(buttons);
				func.setId(id);
				funcList.add(func);
			}
			roleDao.deleteRoleFunc(Long.parseLong(roleid));
			roleDao.addRoleFunc(Long.parseLong(roleid),funcList);
			WriteLog.dbLog("U", "角色管理", "1", "修改角色权限,roleid="+roleid);
			returnFlag = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("配置角色权限失败!");
		}

		return returnFlag;
	}

	public String getNodeDataJson(String node, String roleid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Map getUserFuncRight(String username, String funccode){
		Map rightMap = new LinkedHashMap();
		List rightlist = roleFuncDao.getRight(username,funccode);//先找特例情况
		for(int i=0 ;i<rightlist.size();i++)
		{
			Object[] o =(Object[]) rightlist.get(i);
			if ( o[3] == null ){//未单独定义权限,查找所属菜单类型权限
				log.debug(o[0].toString());
				List rightFuncTypeList = roleFuncDao.getRoleRight(funccode,o[0].toString() );
				for(int j=0 ;j<rightFuncTypeList.size();j++){
					Object[] oj =(Object[]) rightFuncTypeList.get(j);
					if( oj[3] != null ){
						String[] sr = oj[3].toString().split(",");
						for (int k=0;k<sr.length;k++){
							rightMap.put(sr[k], sr[k]);
						}
					}
				}
			}
			else
			{
				//单独定义权限,优先级高于菜单类型权限
				String[] sr = o[3].toString().split(",");
				for (int j=0;j<sr.length;j++){
					rightMap.put(sr[j], sr[j]);
				}
			}
			
		}
		return rightMap;
	}
	
	public boolean isRight(String username, String funccode, String button){
		Map rights = getUserFuncRight(username,funccode);
		return rights.containsKey(button);
	}
	
	public boolean isValidPwd(String newpwd,User user){
		MD5 md5 = new MD5();
		Iterator itr = userPwdHistoryDAO.getUserHisList(user.getUsername()).iterator();
		while (itr.hasNext()) {
			UserPwdHistory UserPwdHistory = (UserPwdHistory) itr.next();
			if (md5.getMD5ofStr(newpwd).equals(UserPwdHistory.getId().getPwd())){
				return false;
			}
		}
		return true;
	}
	
	public void changePwd(String oldpwd,String newpwd,User user){
		MD5 md5 = new MD5();
		user.setUserpwd(md5.getMD5ofStr(newpwd));
		userDao.updateUser(user);
		UserPwdHistory userPwdHistory = new UserPwdHistory();
		UserPwdHistoryId userPwdHistoryId = new UserPwdHistoryId(user, md5.getMD5ofStr(oldpwd), new Date());
		userPwdHistory.setId(userPwdHistoryId);
		userPwdHistoryDAO.save(userPwdHistory);
		WriteLog.dbLog("U", "修改密码", "1", user.getUsername()+"修改密码");
	}
	
	public String getLogListJson(int start, int limit, Map params, String dir, String sort) {
		IlogDAO logDao=(IlogDAO)SpringContextUtil.getBean("logDao");
    	List ls = logDao.getLog(start, limit, params, dir, sort);

    	JsonConfig jsonConfig = new JsonConfig();
    	jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
    	
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\"totalCount\":\""+logDao.getLogCount(params)+"\",\"rows\":");
		buffer.append(JSONSerializer.toJSON(ls, jsonConfig));
		buffer.append("}");
		return buffer.toString();
    }

	public RoleFuncDAO getRoleFuncDao() {
		return roleFuncDao;
	}

	public void setRoleFuncDao(RoleFuncDAO roleFuncDao) {
		this.roleFuncDao = roleFuncDao;
	}

	public UserPwdHistoryDAO getUserPwdHistoryDAO() {
		return userPwdHistoryDAO;
	}

	public void setUserPwdHistoryDAO(UserPwdHistoryDAO userPwdHistoryDAO) {
		this.userPwdHistoryDAO = userPwdHistoryDAO;
	}


}
