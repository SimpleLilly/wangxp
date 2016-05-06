package com.funtalk.pojo.rightmanage;

/**
 * FuncButtonId generated by MyEclipse Persistence Tools
 */

public class FuncButtonId implements java.io.Serializable {

	// Fields

	private Long functypeid;

	private String funccode;

	private String buttons;

	private String memo;

	// Constructors

	/** default constructor */
	public FuncButtonId() {
	}

	/** full constructor */
	public FuncButtonId(Long functypeid, String funccode, String buttons,
			String memo) {
		this.functypeid = functypeid;
		this.funccode = funccode;
		this.buttons = buttons;
		this.memo = memo;
	}

	// Property accessors

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

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof FuncButtonId))
			return false;
		FuncButtonId castOther = (FuncButtonId) other;

		return ((this.getFunctypeid() == castOther.getFunctypeid()) || (this
				.getFunctypeid() != null
				&& castOther.getFunctypeid() != null && this.getFunctypeid()
				.equals(castOther.getFunctypeid())))
				&& ((this.getFunccode() == castOther.getFunccode()) || (this
						.getFunccode() != null
						&& castOther.getFunccode() != null && this
						.getFunccode().equals(castOther.getFunccode())))
				&& ((this.getButtons() == castOther.getButtons()) || (this
						.getButtons() != null
						&& castOther.getButtons() != null && this.getButtons()
						.equals(castOther.getButtons())))
				&& ((this.getMemo() == castOther.getMemo()) || (this.getMemo() != null
						&& castOther.getMemo() != null && this.getMemo()
						.equals(castOther.getMemo())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getFunctypeid() == null ? 0 : this.getFunctypeid()
						.hashCode());
		result = 37 * result
				+ (getFunccode() == null ? 0 : this.getFunccode().hashCode());
		result = 37 * result
				+ (getButtons() == null ? 0 : this.getButtons().hashCode());
		result = 37 * result
				+ (getMemo() == null ? 0 : this.getMemo().hashCode());
		return result;
	}

}