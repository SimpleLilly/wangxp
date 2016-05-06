/**
 * <p>Title:HibernateJsonBeanProcessorMatcher.java</p>
 * <p>Description: </p>
 * <p>Company: si-tech</p>
 * @author xuyadong
 * Jul 30, 2008
 */
package com.funtalk.common.json;

import java.util.Set;

import net.sf.json.processors.JsonBeanProcessorMatcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

/**
 * @author xuyadong
 *
 */
public class HibernateJsonBeanProcessorMatcher extends JsonBeanProcessorMatcher {

	private static Log logger = LogFactory.getLog(HibernateJsonBeanProcessorMatcher.class);
	/* (non-Javadoc)
	 * @see net.sf.json.processors.JsonBeanProcessorMatcher#getMatch(java.lang.Class, java.util.Set)
	 */
    public Object getMatch(Class target, Set set) {
    	System.out.println(target.getName());
    	if (target.getName().indexOf("$$EnhancerByCGLIB$$") != -1) { 
    		logger.warn("Found Lazy-References in Hibernate object "
    				+ target.getName());
    		return org.hibernate.proxy.HibernateProxy.class; 
    	}

    	return DEFAULT.getMatch(target, set); 
    }

/*    protected Object proxyCheck(Object bean) {
        System.out.println("Class is " + bean.getClass().getName());
        if(bean instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy)bean).getHibernateLazyInitializer();
            if(lazyInitializer.isUninitialized()) {
                System.out.println(">>>>>lazyInitializer.getIdentifier()="+ lazyInitializer.getIdentifier());
                return lazyInitializer.getIdentifier();
            }
        }
        if(bean instanceof PersistentSet) {
            return new String[] {}; //ºöÂÔhibernate one-to-many
        }
        return bean;
    }*/
}
