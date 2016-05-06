package com.funtalk.pojo.rightmanage;

/**
 * RoleFuncId generated by MyEclipse Persistence Tools
 */

public class RoleFuncId implements java.io.Serializable {

	// Fields

	private Role TRole;

	private Long functypeid;

	private String funccode;

	private String buttons;

	// Constructors

	/** default constructor */
	public RoleFuncId() {
	}

	/** minimal constructor */
	public RoleFuncId(Role TRole) {
		this.TRole = TRole;
	}

	/** full constructor */
	public RoleFuncId(Role TRole, Long functypeid, String funccode,
			String buttons) {
		this.TRole = TRole;
		this.functypeid = functypeid;
		this.funccode = funccode;
		this.buttons = buttons;
	}

	// Property accessors

	public Role getTRole() {
		return this.TRole;
	}

	public void setTRole(Role TRole) {
		this.TRole = TRole;
	}

	public Long getFunctypeid() {
		return this.functypeid;
	}

	public void setFunctypeid(Long functypeid) {
		this.functypeid = functypeid;
	}

	public String getFunccode() {
		return this.funccode;
	}

	public void setFunccode(String funccode) {
		this.funccode = funccode;
	}

	public String getButtons() {
		return this.buttons;
	}

	public void setButtons(String buttons) {
		this.buttons = buttons;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof RoleFuncId))
			return false;
		RoleFuncId castOther = (RoleFuncId) other;

		return ((this.getTRole() == castOther.getTRole()) || (this.getTRole() != null
				&& castOther.getTRole() != null && this.getTRole().equals(
				castOther.getTRole())))
				&& ((this.getFunctypeid() == castOther.getFunctypeid()) || (this
						.getFunctypeid() != null
						&& castOther.getFunctypeid() != null && this
						.getFunctypeid().equals(castOther.getFunctypeid())))
				&& ((this.getFunccode() == castOther.getFunccode()) || (this
						.getFunccode() != null
						&& castOther.getFunccode() != null && this
						.getFunccode().equals(castOther.getFunccode())))
				&& ((this.getButtons() == castOther.getButtons()) || (this
						.getButtons() != null
						&& castOther.getButtons() != null && this.getButtons()
						.equals(castOther.getButtons())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getTRole() == null ? 0 : this.getTRole().hashCode());
		result = 37
				* result
				+ (getFunctypeid() == null ? 0 : this.getFunctypeid()
						.hashCode());
		result = 37 * result
				+ (getFunccode() == null ? 0 : this.getFunccode().hashCode());
		result = 37 * result
				+ (getButtons() == null ? 0 : this.getButtons().hashCode());
		return result;
	}

}