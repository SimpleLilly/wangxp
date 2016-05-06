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
        /*��½*/
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
	 * ��ɾ��ִ��
	 * @param sql
	 * @return
	 */
	public boolean excute(String[] sql){
		boolean flag = false;
		connectDB();
		if ( db == null ){
			return false;
		}
        //���ڲ�֧������,��ʱ��ô����
		for(int i=0;i<sql.length;i++){
			int ret = db.DCI_Excute(sql[i]);
	        if(ret == 0) /*�ɹ�*/
	        {
	        	logger.debug("������¼��:"+db.DCI_rowno + "    �������:" + db.DCI_info);
	        	flag = true;
	        }
	        else  /*ʧ��*/
	        {
	        	logger.debug("������:"+db.DCI_errno + "    ��������:" + db.DCI_error);
	            if(db.DCI_errno == -1)  /*�����жϣ���Ҫ�˳�*/
	            	flag = false;
	        }
		}
        /*�˳��ڴ����ݿ�*/
        disconnectDB();
		return flag;
	}
	
	public List select(String sql){
		ArrayList list = new ArrayList();
		connectDB();
        /*��ѯSQL*/
		int ret = db.DCI_Excute(sql);
        if(ret != 0) /*ʧ��*/
        {
        	logger.debug("������:"+db.DCI_errno + "    ��������:" + db.DCI_error);
            if(db.DCI_errno == -1)  /*�����жϣ���Ҫ�˳�*/
                return list;
        }     
        
        if(ret == 0)
        {
            /*�ֶ���Ϣ*/
            Vector val = db.DCI_GetField();
            int colNum = val.size();
            for(int i=0; i<colNum; i++)
            {
                Vector v = (Vector)val.elementAt(i);
                logger.debug(v.elementAt(0).toString() + "\t" + v.elementAt(1).toString() + 
                             "\t" + v.elementAt(2).toString());
            }
            
            /*ÿ������*/
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
            logger.debug("��select " + rowNum + " ��");
        }
        /*�˳��ڴ����ݿ�*/
        disconnectDB();
		return list;
	}
    public static void main(String[] args)throws Exception
    {
    	DCIConnection dciConnection = new DCIConnection();
    	dciConnection.select("");
    }

}
