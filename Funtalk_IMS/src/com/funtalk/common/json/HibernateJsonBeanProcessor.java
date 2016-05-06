/**
 * <p>Title:HibernateJsonBeanProcessor.java</p>
 * <p>Description: </p>
 * <p>Company: si-tech</p>
 * @author xuyadong
 * Jul 30, 2008
 */
package com.funtalk.common.json;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;


/**
 * @author xuyadong
 *
 */
public class HibernateJsonBeanProcessor implements JsonBeanProcessor {

	public JSONObject processBean(Object obj, JsonConfig config) {
		// TODO Auto-generated method stub
		System.out.println("aa");
		return new JSONObject();
	}

}
