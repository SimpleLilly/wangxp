package com.funtalk.pojo.rightmanage;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.opensymphony.xwork.ActionContext;

public class LogDAO  extends HibernateDaoSupport implements IlogDAO {
	private static final org.apache.commons.logging.Log log = LogFactory.getLog(LogDAO.class);

	public void save(com.funtalk.pojo.rightmanage.Log transientInstance) {
		log.debug("saving Log instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}
	
	public int getLogCount(Map params) {
		
		ActionContext ctx = ActionContext.getContext();
        Map session = ctx.getSession();
        User currentUser=(User)session.get("currentUser");
        String roleInfo ="";
         for (Iterator iter = currentUser.getTUserRoles().iterator(); iter.hasNext();) {
        	 UserRole userRole = (UserRole) iter.next();
        	 roleInfo +=userRole.getId().getTRole().getRoleid();
				if(iter.hasNext())
					roleInfo+=",";
         }
		String hql = " select count(*) from t_log u where u.username in( select distinct username from t_user_role a where a.roleid in "+
							" ( select distinct roleid from t_role_level a  connect by prior a.roleid=a.p_roleid start with a.roleid in("+roleInfo+") ) ) ";
		Set keySet = params.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if(params.get(key) != null){
				if ("u.memo".equals(key)){
					hql += " and "+key+" like '%"+(String)params.get(key)+"%'";
				}else if("u.operate_time".equals(key)){
					hql += " and  to_char("+key+",'yyyymmdd') ='"+(String)params.get(key)+"' ";
				}else{
					hql += " and "+key+"='"+(String)params.get(key)+"'";
				}
				
			}
		}
		String result =getSession().createSQLQuery(hql).uniqueResult().toString();
		return Integer.parseInt(result);
		 
	}
	
	public List getLog(int start, int limit, Map params, String dir, String sort) {
		
		ActionContext ctx = ActionContext.getContext();
        Map session = ctx.getSession();
        User currentUser=(User)session.get("currentUser");
        String roleInfo ="";
         for (Iterator iter = currentUser.getTUserRoles().iterator(); iter.hasNext();) {
        	 UserRole userRole = (UserRole) iter.next();
        	 roleInfo +=userRole.getId().getTRole().getRoleid();
				if(iter.hasNext())
					roleInfo+=",";
         }
         
		List logs = null;
		
		String hql = " select SEQ,USERNAME,OPERATE_TIME ,OPERATE_TYPE,OPERATE_SRC,OPERATE_DEST,MEMO   from t_log u where u.username in( select distinct username from t_user_role a where a.roleid in "+
		" ( select distinct roleid from t_role_level a  connect by prior a.roleid=a.p_roleid start with a.roleid in("+roleInfo+") ) ) ";
		Set keySet = params.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if(params.get(key) != null){
				if ("u.memo".equals(key)){
					hql += " and "+key+" like '%"+(String)params.get(key)+"%'";
				}else if("u.operate_time".equals(key)){
					hql += " and  to_char("+key+",'yyyymmdd') ='"+(String)params.get(key)+"' ";
				}else{
					hql += " and "+key+"='"+(String)params.get(key)+"'";
				}
				
			}
		}
		if(sort != null){
			hql += "order by "+sort+" "+dir ;
		}
		
		
		logs = getSession().createSQLQuery(hql).addEntity("u", Log.class).setFirstResult(start).setMaxResults(limit)
				.list();
		
		return logs;
	}

	public static void main(String[] args) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		//RightManageService rightManageService = (RightManageService)ctx.getBean("rightManageService");

		IlogDAO dao = (IlogDAO)ctx.getBean("logDao");
        Log log = new Log();
        log.setUsername("xuyd");
        log.setOperateTime(new Date());
        log.setOperateType("C");
        log.setOperateSrc("abc");
        log.setOperateDest("1");
        String memoStr = "INSERT INTO t_pp_billprcplan(BILLPRCID, BILLPRCTYPE, BILLPRCSUBTYPE, BILLTYPE, PRIORITY, PRCEXPR, ATTR, EFFDATE, EXPDATE, NOTE, AUDIT_FLAG) VALUES(''test1'',  ''1'', ''vc'', ''1'', 50, ''test1'', ''1'', to_date(''20081208'',''yyyymmdd''),to_date(''20500101'',''yyyymmdd''), ''1'', ''0'')brINSERT INTO t_pp_subprcplan(SUBBILLPRCID, TYPE, SUBOPERATION, FEEPLANID,  NOTE, AUDIT_FLAG) VALUES(''test1'',''1'', ''vc,vp,vn'', ''F101'',  ''本地被叫免费'', ''0'')brINSERT INTO t_pp_subprcparam(SUBBILLPRCID, PARAMID, PARAMVALUE,  NOTE, AUDIT_FLAG) VALUES(''test1'', ''billrebillflag'',  ''1'', ''11'', ''0'')brINSERT INTO t_pp_subprcparam(SUBBILLPRCID, PARAMID, PARAMVALUE,  NOTE, AUDIT_FLAG) VALUES(''test1'', ''feetypes'',  ''10000000'', ''11'', ''0'')brINSERT INTO t_pp_subprcparam(SUBBILLPRCID, PARAMID, PARAMVALUE,  NOTE, AUDIT_FLAG) VALUES(''test1'', ''favortype'',  ''1'', ''11'', ''0'')br";//INSERT INTO t_pp_subprcparam(SUBBILLPRCID, PARAMID, PARAMVALUE,  NOTE, AUDIT_FLAG) VALUES(''test1'', ''units'',  ''60'', ''11'', ''0'')br";//INSERT INTO t_pp_subprcparam(SUBBILLPRCID, PARAMID, PARAMVALUE,  NOTE, AUDIT_FLAG) VALUES(''test1'', ''favorcap'',  ''0'', ''11'', ''0'')br";
        log.setMemo("xuyd  -  "+memoStr);
        dao.save(log);
	}
}
