package com.funtalk.pojo.rightmanage;

import java.util.List;
import java.util.Map;

public interface IlogDAO {
	void save(com.funtalk.pojo.rightmanage.Log transientInstance);
	
	int getLogCount(Map params) ;
	
	List getLog(int start, int limit, Map params, String dir, String sort) ;

}