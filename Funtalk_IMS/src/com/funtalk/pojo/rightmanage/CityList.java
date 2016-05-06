package com.funtalk.pojo.rightmanage;

import java.util.HashSet;
import java.util.Set;

/**
 * CityList generated by MyEclipse Persistence Tools
 */

public class CityList implements java.io.Serializable {

	// Fields

	private String longCode;

	private String provCode;

	private String cityName;

	private Set TUsers = new HashSet(0);

	// Constructors

	/** default constructor */
	public CityList() {
	}



	public String getCityName() {
		return cityName;
	}



	public void setCityName(String cityName) {
		this.cityName = cityName;
	}



	public String getLongCode() {
		return longCode;
	}



	public void setLongCode(String longCode) {
		this.longCode = longCode;
	}



	public String getProvCode() {
		return provCode;
	}



	public void setProvCode(String provCode) {
		this.provCode = provCode;
	}



	public Set getTUsers() {
		return this.TUsers;
	}

	public void setTUsers(Set TUsers) {
		this.TUsers = TUsers;
	}
	
	public String toString()
	{
		return longCode+","+

		provCode+","+

		cityName;
		
	}

}