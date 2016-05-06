package com.funtalk.common;


/*用于连接思特奇内存数据库，实现增删改查的操作*/

import java.net.*;
import java.io.*;
import java.util.*;

public class DCI_MMDB
{
    private Socket server;
    private DataInputStream socket_in;
    private DataOutputStream socket_out;
    private byte mem_buf[] = new byte[2000];
    
    public String DCI_ver = "1.0.0.1";   /*版本号*/    
    public String DCI_error;             /*错误信息*/
    public String DCI_info;              /*操作结果*/
    public int    DCI_errno;             /*错误编号*/
    public int    DCI_rowno;             /*操作记录数*/

    public DCI_MMDB()
    {
    }

    /*byte转换为int*/
    private int uint3korr(byte buf[], int pos)
    {
        short aa,bb,cc;
        aa = (short)(buf[pos+0]&0xff);
        bb = (short)(buf[pos+1]&0xff);
        cc = (short)(buf[pos+2]&0xff);
        return aa + (bb<<8) + (cc<<16);
    }
    
    /*填充字符串*/
    private String FitStr(String str, int len)
    {
        int slen = str.length();
        String outStr = str;
        for(int i=0; i<len-slen; i++)
            outStr = outStr + " ";
        return outStr;
    }

    /*发送信息*/
    private int DCI_SendMsg(String Str, byte type)
    {
        int len=Str.length() - 1;
        try
        {
            len = Str.length()+1;
            mem_buf[0] = (byte) len;
            mem_buf[1] = (byte) (len>>8);
            mem_buf[2] = (byte) (len>>16);
            mem_buf[3] = 0;
            mem_buf[4] = type;
            
            socket_out.write(mem_buf,0, 5);
            socket_out.write(Str.getBytes(),0,len-1);
            socket_out.flush();
            return 0;
        }
        catch(Exception e)
        {
            DCI_error = e.toString();
            DCI_errno = -1;
            return -1;
        }
    }

    /*接收信息*/
    private int DCI_RecvMsg()
    {
        int len;
        byte flag;        
        try
        {
            socket_in.read(mem_buf, 0, 5);
            len = uint3korr(mem_buf, 0);
            flag = mem_buf[4];
            socket_in.read(mem_buf, 0, len-1);
            String str = new String(mem_buf, 0, len-1);
            if(flag == -1)
            {
                DCI_info = str;
                return 0;
            }
            else if(flag == -2)
            {
                DCI_error = str.substring(6);
                return Integer.parseInt(str.substring(0,6).trim());
            }
            else
            {
                DCI_error = "other error "+ str;
                return -1;
            }
        }
        catch(Exception e)
        {
            DCI_error = e.toString();
            return -1;
        }
    }

    /**********************   登陆  *********************************/
    /*主机IP, 端口号, 用户名, 密码*/
    public int DCI_Connect(String host, int port, String user, String pass)
    {   
        int flag;     
        try
        {
            server=new Socket(host, port);
            socket_in=new DataInputStream(server.getInputStream()); 
            socket_out=new DataOutputStream(server.getOutputStream());             
            String Str = "CONNECT " + FitStr(DCI_ver, 16) + FitStr("BILLING", 16) + 
                         FitStr(user, 16) + FitStr(pass, 16) + FitStr("y0", 16);
            
            flag = DCI_SendMsg(Str, (byte)0);
            if(flag != 0)
                return -1;
            flag = DCI_RecvMsg();
            if(flag != 0)
            {
                DCI_errno = flag;
                return -1;
            }
            return 0;
        }
        catch(Exception e)
        {
            DCI_error = e.toString();
            DCI_errno = -1;
            return -1;
        }
    }
    
    /*断开连接*/
    public void DCI_Disconnect()
    {
        try
        {
            DCI_SendMsg("EXIT", (byte)0);
            server.close();
        }
        catch(Exception e)
        {
        }
    }

    /*执行SQL语句*/
    public int DCI_Excute(String sql)
    {
        int flag;
        String Str;
        try
        {
            flag = DCI_SendMsg(sql, (byte)0);
            if(flag != 0)
                return -1;
            flag = DCI_RecvMsg();
            if(flag != 0)
            {
                DCI_errno = flag;
                return -1;
            }
            DCI_rowno = Integer.parseInt(DCI_info.substring(0,12).trim());
            DCI_info = DCI_info.substring(12);//测试环境是旧版本需要将12改成6
            //DCI_rowno = Integer.parseInt(DCI_info.substring(0,6).trim());
            //DCI_info = DCI_info.substring(6);
            return 0;
        }
        catch(Exception e)
        {
            DCI_error = e.toString();
            DCI_errno = -1;
            return -1;
        }
    }

    /*获取select字段信息*/
    public Vector DCI_GetField()
    {
        short colNum, cl1, cl2;
        int i, len;

        try
        {
            socket_in.read(mem_buf, 0, 5);
            len = uint3korr(mem_buf, 0);
            if(len>1)  socket_in.read(mem_buf, 0, len-1);
            colNum = (short)(mem_buf[4]&0xff);
            
            /*获取字段名及表名*/
            Vector val = new Vector();
            for(i=0; i<colNum; i++)
            {
                socket_in.read(mem_buf, 0, 5);         
                len = uint3korr(mem_buf, 0);
                cl1 = (short)(mem_buf[4]&0xff);
                socket_in.read(mem_buf, 0, len-1);        
                
                cl2 = (short)(mem_buf[cl1]&0xff);
            
                Vector v = new Vector();
                v.addElement(new String(mem_buf, cl1+1, cl2));
                v.addElement((short)(mem_buf[len-6]&0xff)+"");
                v.addElement(uint3korr(mem_buf, len-10)+"");
                val.addElement(v);
            }
            socket_in.read(mem_buf, 0, 5);
            len = uint3korr(mem_buf, 0);
            if(len>1)  socket_in.read(mem_buf, 0, len-1);
            return val;
        }
        catch(Exception e)
        {
            DCI_error = e.toString();
            DCI_errno = -1;
            return null;
        }
    }

    /*读SELECT结果信息*/
    public Vector DCI_FetchData(int colNum)
    {
        short n, cl2;
        int i, k, len;

        try
        {
            socket_in.read(mem_buf, 0, 5);
            len = uint3korr(mem_buf, 0);
            if(len<=1)
            {
                DCI_error = "No data";
                DCI_errno = 0;
                return null;
            }
                
            Vector val = new Vector();
            n = (short)(mem_buf[4]&0xff);    
            socket_in.read(mem_buf, 0, len-1);        
            k = 0;
            for(i=0; i<colNum; i++)
            {
                val.addElement(new String(mem_buf, k, n));
                k += n;        
                n = (short)(mem_buf[k++]&0xff);
            }
            return val;
        }
        catch(Exception e)
        {
            DCI_error = e.toString();
            DCI_errno = -1;
            return null;
        }
    }

    /***************************  测试 *********************************/
    public static void main(String[] args)throws Exception
    {
        int flag, i;
        String Str;

        DCI_MMDB db = new DCI_MMDB();
        
        /*登陆*/
        if(0 != db.DCI_Connect("172.16.9.170", 4000, "system", "si-tech"))
        {
            System.out.println(db.DCI_errno + "      " + db.DCI_error);
            return;
        }
        
        System.out.println("登陆成功");

        /*增删改SQL*/
        //flag = db.DCI_Excute("insert into bbb(f1) values ('aaa')");
        //flag = db.DCI_Excute("update bbb set f2=190 where f1='aaa'");
        flag = db.DCI_Excute("delete bbb where f1='13910999'");
        if(flag == 0) /*成功*/
        {
            System.out.println("操作记录数:"+db.DCI_rowno + "    结果描述:" + db.DCI_info);
        }
        else  /*失败*/
        {
            System.out.println("错误编号:"+db.DCI_errno + "    错误描述:" + db.DCI_error);
            if(db.DCI_errno == -1)  /*连接中断，需要退出*/
                return;
        }        

        /*查询SQL*/
        flag = db.DCI_Excute("select * from bbb");
        if(flag != 0) /*失败*/
        {
            System.out.println("错误编号:"+db.DCI_errno + "    错误描述:" + db.DCI_error);
            if(db.DCI_errno == -1)  /*连接中断，需要退出*/
                return;
        }     
        
        if(flag == 0)
        {
            /*字段信息*/
            Vector val = db.DCI_GetField();
            int colNum = val.size();
            for(i=0; i<colNum; i++)
            {
                Vector v = (Vector)val.elementAt(i);
                System.out.println(v.elementAt(0).toString() + "\t" + v.elementAt(1).toString() + 
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
                for(i=0; i<colNum; i++)
                    System.out.print(res.elementAt(i).toString()+",\t");
                System.out.println("");
            }
            System.out.println("共select " + rowNum + " 行");
        }

        /*退出内存数据库*/
        db.DCI_Disconnect();
    }
}
