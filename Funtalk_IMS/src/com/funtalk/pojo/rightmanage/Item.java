/**
 * <p>Title:Item.java</p>
 * <p>Description: </p>
 * <p>Company: si-tech</p>
 * @author xuyadong
 * Mar 28, 2007
 */
package com.funtalk.pojo.rightmanage;

/**
 *
 */
public class Item implements java.io.Serializable{
	
	private String key;
	
	private String comment;

	public Item(String key, String comment) {
		super();
		this.key = key;
		this.comment = comment;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
