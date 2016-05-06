package com.funtalk.common;

import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DCIConnection {
	
	private Log logger = LogFactory.getLog(DCIConnection.class);
	private DCI_MMDB db;
	
	public void connectDB(){
		db = new DCI_MMDB();
        ResourceBundle bundle = ResourceBundle.getBundle("DataConnection");
        String ip = bundle.getString("DCI.ip");
        int port = Integer.parseInt(bundle.getString("DCI.port"));
        String user = bundle.getString("DCI.user");
        String pass = bundle.getString("DCI.pass");
        /*登陆*/
        if(0 != db.DCI_Connect(ip, port, user, pass))
        {
        	logger.debug(db.DCI_errno + "      " + db.DCI_error);
        	db = null;
        }
	}
	
	public void disconnectDB(){
		if ( db != null ){
			db.DCI_Disconnect();
		}
	}
	
	public boolean delete(String sql){
		return excute(sql);
	}
	public boolean insert(String sql){
		return excute(sql);
	}
	public boolean update(String sql){
		return excute(sql);
	}
	
	public boolean excute(String sql){
		String[] sqls = new String[1];
		sqls[0] = sql;
		return excute(sqls);
	}
	/**
	 * 增删改执行
	 * @param sql
	 * @return
	 */
	public boolean excute(String[] sql){
		boolean flag = false;
		connectDB();
		if ( db == null ){
			return false;
		}
        //由于不支持事务,暂时这么操作
		for(int i=0;i<sql.length;i++){
			int ret = db.DCI_Excute(sql[i]);
	        if(ret == 0) /*成功*/
	        {
	        	logger.debug("操作记录数:"+db.DCI_rowno + "    结果描述:" + db.DCI_info);
	        	flag = true;
	        }
	        else  /*失败*/
	        {
	        	logger.debug("错误编号:"+db.DCI_errno + "    错误描述:" + db.DCI_error);
	            if(db.DCI_errno == -1)  /*连接中断，需要退出*/
	            	flag = false;
	        }
		}
        /*退出内存数据库*/
        disconnectDB();
		return flag;
	}
	
	public List select(String sql){
		ArrayList list = new ArrayList();
		connectDB();
        /*查询SQL*/
		int ret = db.DCI_Excute(sql);
        if(ret != 0) /*失败*/
        {
        	logger.debug("错误编号:"+db.DCI_errno + "    错误描述:" + db.DCI_error);
            if(db.DCI_errno == -1)  /*连接中断，需要退出*/
                return list;
        }     
        
        if(ret == 0)
        {
            /*字段信息*/
            Vector val = db.DCI_GetField();
            int colNum = val.size();
            for(int i=0; i<colNum; i++)
            {
                Vector v = (Vector)val.elementAt(i);
                logger.debug(v.elementAt(0).toString() + "\t" + v.elementAt(1).toString() + 
                             "\t" + v.elementAt(2).toString());
            }
            
            /*每行数据*/
            Vector res;
            int rowNum = 0;
            while(true)
            {
                res = db.DCI_FetchData(colNum);
                if(null == res)
                    break;
                rowNum++;
                String[] temp = new String[colNum];
                for(int i=0; i<colNum; i++){
                	temp[i] = res.elementAt(i).toString();
                }
                list.add(temp);
            }
            logger.debug("共select " + rowNum + " 行");
        }
        /*退出内存数据库*/
        disconnectDB();
		return list;
	}
    public static void main(String[] args)throws Exception
    {
    	DCIConnection dciConnection = new DCIConnection();
    	dciConnection.select("");
    }

}
