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
	      // ������δ�����Ҫ��Ϊ���ʹ��hibernate��createSQLQuery��ѯʱ�ֶ�����Ϊchar(n)���ֶη��ص��ַ�������1������
	      registerHibernateType(Types.CHAR, 255, Hibernate.STRING.getName()); 
	 }
}
