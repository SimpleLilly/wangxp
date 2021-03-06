package com.funtalk.pojo.rightmanage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Func generated by MyEclipse Persistence Tools
 */

public class Func implements java.io.Serializable {

	// Fields

	private String funccode;

	private Long funcgroupid;

	private Long functypeid;

	private String funcname;

	private String funcpcode;

	private String pagecode;

	private String remark;

	private List childFunc = new ArrayList(0);
	// Constructors

	/** default constructor */
	public Func() {
	}

	/** minimal constructor */
	public Func(String funccode, String funcname, String funcpcode,
			String pagecode) {
		this.funccode = funccode;
		this.funcname = funcname;
		this.funcpcode = funcpcode;
		this.pagecode = pagecode;
	}

	/** full constructor */
	public Func(String funccode, Long funcgroupid, Long functypeid,
			String funcname, String funcpcode, String pagecode, String remark) {
		this.funccode = funccode;
		this.funcgroupid = funcgroupid;
		this.functypeid = functypeid;
		this.funcname = funcname;
		this.funcpcode = funcpcode;
		this.pagecode = pagecode;
		this.remark = remark;
	}

	// Property accessors

	public String getFunccode() {
		return this.funccode;
	}

	public void setFunccode(String funccode) {
		this.funccode = funccode;
	}

	public Long getFuncgroupid() {
		return this.funcgroupid;
	}

	public void setFuncgroupid(Long funcgroupid) {
		this.funcgroupid = funcgroupid;
	}

	public Long getFunctypeid() {
		return this.functypeid;
	}

	public void setFunctypeid(Long functypeid) {
		this.functypeid = functypeid;
	}

	public String getFuncname() {
		return this.funcname;
	}

	public void setFuncname(String funcname) {
		this.funcname = funcname;
	}

	public String getFuncpcode() {
		return this.funcpcode;
	}

	public void setFuncpcode(String funcpcode) {
		this.funcpcode = funcpcode;
	}

	public String getPagecode() {
		return this.pagecode;
	}

	public void setPagecode(String pagecode) {
		this.pagecode = pagecode;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List getChildFunc() {
		return childFunc;
	}

	public void setChildFunc(List childFunc) {
		this.childFunc = childFunc;
	}

}