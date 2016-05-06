package com.funtalk.pojo.rightmanage;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Data access object (DAO) for domain model class UserPwdHistory.
 * 
 * @see com.funtalk.pojo.rightmanage.UserPwdHistory
 * @author MyEclipse Persistence Tools
 */

public class UserPwdHistoryDAO extends HibernateDaoSupport{
	private static final Log log = LogFactory.getLog(UserPwdHistoryDAO.class);

	// property constants

	public void save(UserPwdHistory transientInstance) {
		log.debug("saving UserPwdHistory instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(UserPwdHistory persistentInstance) {
		log.debug("deleting UserPwdHistory instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UserPwdHistory findById(
			com.funtalk.pojo.rightmanage.UserPwdHistoryId id) {
		log.debug("getting UserPwdHistory instance with id: " + id);
		try {
			UserPwdHistory instance = (UserPwdHistory) getSession().get(
					"com.sitech.pojo.rightmanage.UserPwdHistory", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(UserPwdHistory instance) {
		log.debug("finding UserPwdHistory instance by example");
		try {
			List results = getSession().createCriteria(
					"com.sitech.pojo.rightmanage.UserPwdHistory").add(
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
		log.debug("finding UserPwdHistory instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from UserPwdHistory as model where model."
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
		log.debug("finding all UserPwdHistory instances");
		try {
			String queryString = "from UserPwdHistory";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public UserPwdHistory merge(UserPwdHistory detachedInstance) {
		log.debug("merging UserPwdHistory instance");
		try {
			UserPwdHistory result = (UserPwdHistory) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(UserPwdHistory instance) {
		log.debug("attaching dirty UserPwdHistory instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(UserPwdHistory instance) {
		log.debug("attaching clean UserPwdHistory instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	public List getUserHisList(String username){
		String sql = "select * from t_user_pwd_history where username=:username";
		return getSession().createSQLQuery(sql).addEntity(UserPwdHistory.class).setString("username", username).list();
	}
}