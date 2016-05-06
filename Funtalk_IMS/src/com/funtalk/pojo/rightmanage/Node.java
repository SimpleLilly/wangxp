/**
 * <p>Title:Node.java</p>
 * <p>Description: </p>
 * <p>Company: si-tech</p>
 * @author xuyadong
 * Aug 20, 2008
 */
package com.funtalk.pojo.rightmanage;

import java.util.List;


/**
 * @author xuyadong
 *
 */
public class Node{
	
	private String funcPcode;
	
	private String id;
	
	private String text;
	
	private String cls;
	
	private boolean leaf;
	
	private boolean checked;
	
	private String buttons;
	
	private String buttons_def;
	
	private List children;
	
/*	private boolean expanded;

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}*/

	public String getButtons() {
		return buttons;
	}

	public void setButtons(String buttons) {
		this.buttons = buttons;
	}

	public String getButtons_def() {
		return buttons_def;
	}

	public void setButtons_def(String buttons_def) {
		this.buttons_def = buttons_def;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFuncPcode() {
		return funcPcode;
	}

	public void setFuncPcode(String funcPcode) {
		this.funcPcode = funcPcode;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
