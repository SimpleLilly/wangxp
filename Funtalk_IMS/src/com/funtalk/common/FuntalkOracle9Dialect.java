package com.funtalk.common;

import org.hibernate.dialect.Oracle9Dialect;
import java.sql.Types;
import org.hibernate.Hibernate;

/**
 *
 * @author yangl
 *
 */
public class FuntalkOracle9Dialect extends Oracle9Dialect{
	public FuntalkOracle9Dialect()
	 {
	      super();
	      // 加入这段代码主要是为解决使用hibernate的createSQLQuery查询时字段类型为char(n)的字段返回的字符长度是1的问题
	      registerHibernateType(Types.CHAR, 255, Hibernate.STRING.getName()); 
	 }
}
