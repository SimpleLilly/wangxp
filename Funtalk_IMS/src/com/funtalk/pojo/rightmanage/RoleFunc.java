package com.funtalk.pojo.rightmanage;

/**
 * RoleFunc generated by MyEclipse Persistence Tools
 */

public class RoleFunc implements java.io.Serializable {

	// Fields

	private RoleFuncId id;

	private Role TRole;

	// Constructors

	/** default constructor */
	public RoleFunc() {
	}

	/** full constructor */
	public RoleFunc(RoleFuncId id, Role TRole) {
		this.id = id;
		this.TRole = TRole;
	}

	// Property accessors

	public RoleFuncId getId() {
		return this.id;
	}

	public void setId(RoleFuncId id) {
		this.id = id;
	}

	public Role getTRole() {
		return this.TRole;
	}

	public void setTRole(Role TRole) {
		this.TRole = TRole;
	}

}