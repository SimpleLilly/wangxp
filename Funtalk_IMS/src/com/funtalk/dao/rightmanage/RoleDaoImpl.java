package com.funtalk.dao.rightmanage;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.funtalk.pojo.rightmanage.Role;
import com.funtalk.pojo.rightmanage.RoleFunc;
import com.funtalk.pojo.rightmanage.UserRole;
import com.funtalk.pojo.rightmanage.RoleLevel;


public class RoleDaoImpl extends HibernateDaoSupport implements RoleDao {

	/* (non-Javadoc)
	 * @see com.funtalk.dao.rightmanage.RoleDao#getAllRoles()
	 */ 
	public List getAllRoles() {
		
		//return this.getHibernateTemplate().find("from Role r left join r.UserRoles as userRole left join userRole.TUser");
		//return getSession().createQuery("from Role r left join fetch r.UserRoles as userRole left join fetch userRole.TUser").list();
		return getSession().createQuery("from Role r ").list();
	}

	public List getRoles(int start, int limit, Map params, String dir, String sort) {
		
		List roles = null;
		try {
			String hql = "from Role r left join fetch r.UserRoles as userRole left join fetch r.TRoleLevels as roleLevel left join fetch userRole.TUser as u where 1=1 ";
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
			
			
			roles = getSession().createQuery(hql).setFirstResult(start).setMaxResults(limit)
					.list();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return roles;
	}

	public int getRolesCount(Map params) {
		Long ret = new Long(0);
		try {
			String hql = "select count(distinct r.roleid) from Role r left join r.UserRoles as userRole  left join r.TRoleLevels as roleLevel left join userRole.TUser as u where 1=1";
			Set keySet = params.keySet();
			for (Iterator iter = keySet.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				if(params.get(key) != null){
					hql += " and "+key+" like '%"+(String)params.get(key)+"%'";
				}
			}
			ret = (Long)getSession().createQuery(hql).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.intValue();
		 
	}
	


	public void saveRole(Role role) {
		logger.debug("saving Role instance");
		try {
			getSession().save(role);
			logger.debug("save role successful");
		} catch (RuntimeException re) {
			logger.error("save role failed", re);
			throw re;
		}
	}
	
	public void saveUserRole(UserRole userRole) {
		
		logger.debug("saving UserRole instance");
		try {
			getSession().save(userRole);
			logger.debug("save userRole successful");
		} catch (RuntimeException re) {
			logger.error("save userRole failed", re);
			throw re;
		}
	}
	
	public void saveRoleLevel(RoleLevel roleLevel) {
		
		logger.debug("saveRoleLeve instance");
		try {
			getSession().save(roleLevel);
			logger.debug("saveRoleLevesuccessful");
		} catch (RuntimeException re) {
			logger.error("saveRoleLeve failed", re);
			throw re;
		}
	}

	public void deleteRoleLevel(Long[] ids) {
		String hqlDelete = "delete RoleLevel u where u.id.roleid in ( :ids ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setParameterList("ids", ids).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete userRole failed", re);
			re.printStackTrace();
			throw re;
		}
	}
	
	public void deleteRoleLevel(Role role) {
		String hqlDelete = "delete RoleLevel u where u.id.roleid = :roleid ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setLong("roleid", role.getRoleid().longValue()).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete userRole failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public void deleteUserRoles(Long[] ids) {
		String hqlDelete = "delete UserRole u where u.TRole.roleid in ( :ids ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setParameterList("ids", ids).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete userRole failed", re);
			re.printStackTrace();
			throw re;
		}
	}
	
	public void deleteRoleFunc(Long[] ids) {
		String hqlDelete = "delete RoleFunc u where u.TRole.roleid in ( :ids ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setParameterList("ids", ids).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete userRole failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public void deleteRoles(Long[] ids) {
		String hqlDelete = "delete Role r where r.roleid in ( :ids ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setParameterList("ids", ids).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete role failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public void updateRole(Role role) {
		logger.debug("updating Role instance");
		try {
			getSession().saveOrUpdate(role);
			logger.debug("update role successful");
		} catch (RuntimeException re) {
			logger.error("update role failed", re);
			throw re;
		}
	}

	public void deleteUserRole(Role role) {
		String hqlDelete = "delete UserRole u where u.TRole.roleid = :roleid ) ";
		try {
			Query  query = getSession().createQuery(hqlDelete);
			query.setLong("roleid", role.getRoleid().longValue()).executeUpdate();
		} catch (RuntimeException re) {
			logger.error("delete userRole failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List getNodeInfo(String node, Long roleid) {
		String sql = "select f.funccode, f.funcname, decode(f.pagecode,'/','0','1')," +
		"decode(r.funccode,null,'0','1'), r.buttons, b.buttons_def from t_func f left outer join " +
		"(select * from t_role_func where roleid=:roleid) r on f.funccode = r.funccode " +
		"left outer join t_func_button b on f.funccode = b.funccode " +
		"where f.funcpcode = :node order by f.funccode ";
		logger.info(sql);
		return getSession().createSQLQuery(sql).setLong("roleid", roleid.longValue()).setString("node", node)
		.list();
		
	}
	
	public List getNodeInfo(long roleid) {
		String sql = "select f.funcpcode, f.funccode, f.funcname, decode(f.pagecode,'/','0','1')," +
		"decode(r.funccode,null,'0','1'), r.buttons, b.buttons_def from t_func f left outer join " +
		"(select * from t_role_func where roleid=:roleid) r on f.funccode = r.funccode " +
		"left outer join t_func_button b on f.funccode = b.funccode  or f.functypeid=b.functypeid " +
		"order by f.funccode ";
		logger.info(sql);
		return getSession().createSQLQuery(sql).setLong("roleid", roleid)
		.list();
		
	}
	
/*	public List getRoleFunc(String roleid) {
		return getSession().createQuery("from RoleFunc f where f.id.functypeid is null and f.TRole.roleid =:roleid").setLong("roleid", Long.parseLong(roleid)).list();
	}*/

/*	public List getTopMenu() {
		return getSession().createQuery("from Func f where f.funcpcode = '-1' order by f.funcgroupid")
		.list();
	}*/
	
	public static void main(String[] args) throws Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		//RightManageService rightManageService = (RightManageService)ctx.getBean("rightManageService");

		RoleDao dao = (RoleDao)ctx.getBean("roleDao");
		
/*		List ls = dao.getAllRoles();
		for (Iterator iter = ls.iterator(); iter.hasNext();) {
			Role role = (Role) iter.next();
			System.out.println(role.getRoleid());
			System.out.println(role.getRolename());
			 
		}*/
		//System.out.println(dao.getRolesCount(new HashMap()));
/*		List ls = dao.getNodeInfo("L1", new Long(1));
		for (Iterator iter = ls.iterator(); iter.hasNext();) {
			Object[] element = (Object[])iter.next();
			System.out.println(element[0]);
			System.out.println(element[1]);
			System.out.println(element[2]);
			System.out.println(element[3]);
			System.out.println(element[4]);
			System.out.println(element[5]);
		}*/
		int i = dao.deleteRoleFunc((long)1);
		System.out.println(i);
	}

	public int deleteRoleFunc(long roleid) {
		String hqlDelete = "delete RoleFunc r where r.id.functypeid is null and r.TRole.roleid =:roleid";
		return getSession().createQuery(hqlDelete).setLong("roleid", roleid).executeUpdate();
	}

	public void addRoleFunc(long roleid, List funcList) {
		Session session = getSession();
		//System.out.println(3/0);
		try {
			for(int i = 0; i < funcList.size(); i++){
				session.save((RoleFunc)funcList.get(i));
				if(i % 20 == 0){
					session.flush();
					session.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
