package com.funtalk.dao.rightmanage;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.lob.SerializableClob;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;



import com.funtalk.pojo.rightmanage.User;
import com.funtalk.pojo.rightmanage.UserRole;
import com.funtalk.pojo.rightmanage.CityList;


public class UserDaoImpl extends HibernateDaoSupport implements UserDao{

	private static final Log logger = LogFactory.getLog(UserDaoImpl.class);
	

	/**
	 * 根据用户名找到User
	 */
	public User findById(String userName) {
		User user=null;
		try {
			
			///////////String hql="from User a where a.username='"+userName+"'";
			///////////user=(User)this.getHibernateTemplate().getSessionFactory().openSession().createQuery(hql).uniqueResult();
			user = (User)this.getHibernateTemplate().get(com.funtalk.pojo.rightmanage.User.class, userName);
			
			//String hql2="from CityList a where a.longCode='"+user.get+"'";
			//CityList vo=(CityList)this.getHibernateTemplate().getSessionFactory().openSession().createQuery(hql2).uniqueResult();
			
			//user.setCityList(vo);
			
			logger.info("=================>user="+user);
		} catch (Exception e) {
			logger.error("in  findById, error: "+e);
		}
		return user;
	}

	public int getPwdDate(String userName) {
		BigDecimal pwdDate = (BigDecimal)getSession().createSQLQuery("select trunc(sysdate-max(u.modify_date)) as modifyDate from t_user_pwd_history u " +
		"where u.username= :userName").setString("userName", userName).uniqueResult();
		if(pwdDate == null){
			return 0;  
		}
		return pwdDate.intValue();
	}
	
	public String getLastLoginInfo(String userName){
		Object  info = getSession().createSQLQuery("select '时间:'||to_char(operate_time,'yyyy-mm-dd HH24:MI:SS') ||to_char(memo) "+
				" from t_log where username=:userName "+
				" and operate_time = (select max(operate_time) from t_log where username=:userName and operate_type='L' and operate_src='用户登陆')").setString("userName", userName).setMaxResults(1).uniqueResult();
		String ret ="";
		try{
			logger.debug("---------========"+info.getClass());
			ret =(String) info;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return ret;
	}
	
	public List getUserFunc(String userName) {
		List funcs = getSession().createQuery("from Func f where f.funccode in (" +
				"select funcs.id.funccode from UserRole as c " +
		"right join c.TRole.TRoleFuncs as funcs where c.TUser.username= :userName) " +
		"order by f.funcgroupid, f.funccode")
		.setString("userName", userName).list();
		return funcs;
	}
	
	public List getAllUser(){
		List users = getSession().createQuery("from User u left join fetch u.TUserRoles as userRole left join fetch userRole.TRole left join fetch u.cityList ").list();
		//session.close(); 
		return users;
/*		Criteria crit = this.getSession().createCriteria(User.class);
		crit.setFetchMode("TUserRoles", FetchMode.JOIN)
		.setFetchMode("cityList", FetchMode.JOIN)
		.setFirstResult(1)
		.setMaxResults(2);
		return crit.list();*/
		//return this.getHibernateTemplate().find("from User u left join fetch u.TUserRoles left join fetch u.cityList ");
	}
	
	public int getUsersCount(Map params) {
		
		String hql = "select count(distinct u.username) from User u left join u.TUserRoles as userRole " +
		"left join userRole.TRole as role " +
		"left join u.cityList cList where 1=1 ";
		Set keySet = params.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if(params.get(key) != null){
				hql += " and "+key+" like '%"+(String)params.get(key)+"%'";
			}
		}
		
		Long ret = (Long)getSession().createQuery(hql).uniqueResult();
		return ret.intValue();
		 
	}
	
	public List getUsers(int start, int limit, Map params, String dir, String sort) {
		
		List users = null;
		
		String hql = "from User u left join fetch u.TUserRoles as userRole " +
		"left join fetch userRole.TRole as role " +
		"left join fetch u.cityList cList where 1=1 ";
		
		Set keySet = params.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if(params.get(key) != null){
				hql += " and "+key+" like '%"+(String)params.get(key)+"%'";
			}			
		}
		
		if(sort != null){
			hql += "order by "+sort+" "+dir ;
		}
		
		
		users = getSession().createQuery(hql).setFirstResult(start).setMaxResults(limit).list();
		
		return users;
	}
	
	public List getProvCityList(String provCode) {
		List cityList = getSession().createQuery("from CityList c where c.provCode ='"+provCode+"' ").list();
		return cityList;
	}

	public void saveUser(User user) {
		
		logger.debug("saving User instance");
		try {
			getSession().save(user);
			logger.debug("save user successful");
		} catch (RuntimeException re) {
			logger.error("save user failed", re);
			throw re;
		}
	}

	
	


	public void saveUserRole(UserRole userRole) {
	
		logger.debug("saving UserRole instance");
		try {
			getSession().merge(userRole);
			logger.debug("save userRole successful");
		} catch (RuntimeException re) {
			logger.error("save userRole failed", re);
			throw re;
		}
	}


	public void updateUser(User user) {
		logger.debug("updating User instance");
		try {
			
			getSession().saveOrUpdate(user);
			logger.debug("update user successful");

		} catch (RuntimeException re) {
			logger.error("update userRole failed", re);
			throw re;
		}
	}
	
	public void deleteUserRole(User user) {
		try {
			for (Iterator it = user.getTUserRoles().iterator(); it.hasNext();) {
				UserRole userRole = (UserRole)it.next();
				getSession().delete(userRole);
	        }
		} catch (RuntimeException re) {
			logger.error("delete userRole failed", re);
			re.printStackTrace();
			throw re;
		}
	}
	
	public void deleteUsers(String[] ids) {
		String hqlDelete = "delete User u where u.username in ( :ids ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setParameterList("ids", ids).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete user failed", re);
			re.printStackTrace();
			throw re;
		}

	}

	public void deleteUserRoles(String[] ids) {
		String hqlDelete = "delete UserRole u where u.TUser.username in ( :ids ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setParameterList("ids", ids).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete userRole failed", re);
			re.printStackTrace();
			throw re;
		}
		
	}
	
	public void deleteUserHisPwd(String[] ids) {
		String hqlDelete = "delete UserPwdHistory u where u.TUser.username in ( :ids ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setParameterList("ids", ids).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete userRole failed", re);
			re.printStackTrace();
			throw re;
		}
		
	}

	public List getAllUsers() {
		return getSession().createQuery("from User")
		.list();
	}
	
	
	public static void main(String[] args) throws Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		//RightManageService rightManageService = (RightManageService)ctx.getBean("rightManageService");

		UserDao dao = (UserDao)ctx.getBean("userDao");
        User user = dao.findById("admin");
        System.out.println("=========>user="+user);	
		if(user != null){
			System.out.println(user.getUsername());
			System.out.println(user.getUserpwd());				
		}
		
 
		//dao.getPwdDate("admin");
		//dao.getUserFunc("admin");
/*		List ls = dao.getUsers(1, 10, null, "ASC", "username");
		//List ls = dao.getAllUser();
	
		HashSet set = new HashSet(ls);
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			User user = (User) iter.next();
			System.out.println(user.getCityList().getCityName());
			System.out.println(user.getUsername());
			Set userRoles = user.getTUserRoles();
			for (Iterator iterator = userRoles.iterator(); iterator.hasNext();) {
				UserRole userRole = (UserRole) iterator.next();
				System.out.println(userRole.getTRole().getRoleid());
				System.out.println(userRole.getTRole().getRolename());
			}
		}*/
/*		List cityList = dao.getProvCityList("551");
		for (Iterator iter = cityList.iterator(); iter.hasNext();) {
			CityList element = (CityList) iter.next();
			System.out.println(element.getCityName());
			
		}*/
		//dao.getUsersCount(new HashMap());
//		User user = new User();
//		user.setUsername("abcd");
//		user.setUserpwd("aaa");
//		user.setUsernamecn("st");
//		user.setPhone("12131");
//		user.setEmail("232@23.121");
//		dao.saveUser(user);
	}
	
}
