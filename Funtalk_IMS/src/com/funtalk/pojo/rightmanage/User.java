package com.funtalk.pojo.rightmanage;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;

/**
 * User generated by MyEclipse Persistence Tools
 */

public class User implements java.io.Serializable {

	// Fields

	private String username;

	private String usernamecn;

	private String userpwd;

	private String phone;

	private String email;

	private String state;

	private String memo;
	
	
	
	private CityList cityList=null ;

	private Set TUserRoles = new HashSet(0);

	private Set TLogs = new HashSet(0);

	private Set TUserPwdHistories = new HashSet(0);
	
	
	
	

	private int pwdDate;
	
	private Date pwdLastModifyDate;
	
	private String lastLoginInfo;
	// Constructors

	public int getPwdDate() {
		return pwdDate;
	}

	public void setPwdDate(int pwdDate) {
		this.pwdDate = pwdDate;
	}

	/** default constructor */
	public User() {
	}

	public User(String username){
		this.username = username;
	}
	
	/** minimal constructor */
	public User(String username, CityList cityList, String userpwd) {
		this.username = username;
		this.cityList = cityList;
		this.userpwd = userpwd;
	}

	/** full constructor */
	public User(String username, CityList cityList, String usernamecn,
			String userpwd, String phone, String email, String state,
			String memo, Set TUserRoles, Set TLogs, Set TUserPwdHistories) {
		this.username = username;
		this.cityList = cityList;
		this.usernamecn = usernamecn;
		this.userpwd = userpwd;
		this.phone = phone;
		this.email = email;
		this.state = state;
		this.memo = memo;
		this.TUserRoles = TUserRoles;
		this.TLogs = TLogs;
		this.TUserPwdHistories = TUserPwdHistories;
	}

	// Property accessors

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public CityList getCityList() {
		return this.cityList;
	}

	public void setCityList(CityList cityList) {
		this.cityList = cityList;
	}

	public String getUsernamecn() {
		return this.usernamecn;
	}

	public void setUsernamecn(String usernamecn) {
		this.usernamecn = usernamecn;
	}

	public String getUserpwd() {
		return this.userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Set getTUserRoles() {
		return this.TUserRoles;
	}

	public void setTUserRoles(Set TUserRoles) {
		this.TUserRoles = TUserRoles;
	}

	public Set getTLogs() {
		return this.TLogs;
	}

	public void setTLogs(Set TLogs) {
		this.TLogs = TLogs;
	}

	public Set getTUserPwdHistories() {
		return this.TUserPwdHistories;
	}

	public void setTUserPwdHistories(Set TUserPwdHistories) {
		this.TUserPwdHistories = TUserPwdHistories;
	}

	public Date getPwdLastModifyDate() {
		return pwdLastModifyDate;
	}

	public void setPwdLastModifyDate(Date pwdLastModifyDate) {
		this.pwdLastModifyDate = pwdLastModifyDate;
	}

	public String getLastLoginInfo() {
		return lastLoginInfo;
	}

	public void setLastLoginInfo(String lastLoginInfo) {
		this.lastLoginInfo = lastLoginInfo;
	}

}