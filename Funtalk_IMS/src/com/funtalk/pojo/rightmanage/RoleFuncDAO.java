package com.funtalk.pojo.rightmanage;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Data access object (DAO) for domain model class RoleFunc.
 * 
 * @see com.funtalk.pojo.rightmanage.RoleFunc
 * @author MyEclipse Persistence Tools
 */

public class RoleFuncDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(RoleFuncDAO.class);

	// property constants

	public void save(RoleFunc transientInstance) {
		log.debug("saving RoleFunc instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(RoleFunc persistentInstance) {
		log.debug("deleting RoleFunc instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public RoleFunc findById(com.funtalk.pojo.rightmanage.RoleFuncId id) {
		log.debug("getting RoleFunc instance with id: " + id);
		try {
			RoleFunc instance = (RoleFunc) getSession().get(
					"com.sitech.pojo.rightmanage.RoleFunc", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(RoleFunc instance) {
		log.debug("finding RoleFunc instance by example");
		try {
			List results = getSession().createCriteria(
					"com.sitech.pojo.rightmanage.RoleFunc").add(
					Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding RoleFunc instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from RoleFunc as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all RoleFunc instances");
		try {
			String queryString = "from RoleFunc";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public RoleFunc merge(RoleFunc detachedInstance) {
		log.debug("merging RoleFunc instance");
		try {
			RoleFunc result = (RoleFunc) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(RoleFunc instance) {
		log.debug("attaching dirty RoleFunc instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RoleFunc instance) {
		log.debug("attaching clean RoleFunc instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public List getRight(String username, String funccode){
		try {
			String sql ="select  roleid,functypeid,funccode,buttons from t_role_func where funccode=:funccode "
					+"and roleid in(select roleid from t_user_role where username=:username)";
			List list = getSession().createSQLQuery(sql).setString("funccode", funccode).setString("username", username).list();
			return list;
		} catch (RuntimeException re) {
			log.error("getRight failed", re);
			throw re;
		}
	}
	public List getRoleRight(String funccode,String role){
		try {
			String sql ="select  roleid,functypeid,funccode,buttons  from t_role_func "+
				" where functypeid = (select functypeid from t_func where funccode=:funccode) and roleid=:role";
			List list = getSession().createSQLQuery(sql).setString("funccode", funccode).setString("role", role).list();
			return list;
		} catch (RuntimeException re) {
			log.error("getRoleRight failed", re);
			throw re;
		}
	}
}