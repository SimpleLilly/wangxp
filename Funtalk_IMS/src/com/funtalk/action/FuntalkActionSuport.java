/**
 * <p>Title:FuntalkActionSuport.java</p>
 * <p>Description: </p>
 * <p>Company: funtalk</p>
 * @author wangxp
 * Jan 24, 2016
 */
package com.funtalk.action;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.ActionSupport;



public abstract class  FuntalkActionSuport extends   ActionSupport	
                      implements ServletRequestAware,SessionAware{

	protected String funccode;
	
	protected HttpServletRequest request;
	
	protected Map session;
	
	public String getFunccode() {
		return funccode;
	}
	
	public void setFunccode(String funccode) {
		this.funccode = funccode;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setSession(Map session) {
		this.session = session;
	}
}
