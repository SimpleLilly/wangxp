package com.funtalk.bean;


/**
 * file name  : ToolsOfSystem.java
 * authors    : elan huang
 * created    : 2003-03-28
 * copyright (c) 2002 by SI-TECH , All Rights Reserved
 *
 * modifications:  elan huang
 *
 */

import java.util.*;

public class ToolsOfSystem
{

    /**
     * ½«×Ö·û´®ÓÉGBK×ªÎªISO±àÂë
     * @param strIn  ÊäÈë×Ö·û´®
     * @return Ìæ»»ºóµÄ×Ö·û´®
     */
    public static String GBKToISO(String strIn)
    {
        byte[] b;
        String strOut = "";
        if (strIn == null || (strIn.trim()).equals(""))
            return "";
        try
        {
            b = strIn.getBytes("GB2312");
            strOut = new String(b, "ISO8859_1");
        }
        catch (Exception e)
        {}
        return strOut;
    }

    public static String ISOToGBK(String strIn)
    {
        String strOut = "";
        if (strIn == null || (strIn.trim()).equals(""))
            return "";
        try
        {
            byte[] b = strIn.getBytes("ISO8859_1");
            //strOut = new String(b, "GB2312");
            strOut = new String(b, "GBK");
        }
        catch (Exception e)
        {}
        return strOut;
    }

    /**
     *  Ìæ»»×Ö·û´®
     * @param   str ÊäÈëµÄÔ­Ê¼×Ö·û´®
     * @param   problemStr ÐèÒªÌæ»»µÄ¾É×Ö·û´®
     * @param   replace ÐèÒªÌæ»»µÄÐÂ×Ö·û´®
     * @return  Ìæ»»ºóµÄ×Ö·û´®
     */
    public static String replace(String str, String problemStr, String repStr)
    {
        int len = problemStr.length();
        for(int i=str.lastIndexOf(problemStr); i>=0; i=str.lastIndexOf(problemStr, i-1))
        {
            if(i==0)
                str = repStr+str.substring(i+len);
            else
                str = str.substring(0, i)+repStr+str.substring(i+len);
        }
        return str;
    }

    public static String[] split(String s) {
        String[] return_val = null;
        if (s.indexOf(",") != -1)
        {
            StringTokenizer st = new StringTokenizer(s, ",");
            return_val = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens())
            {
                return_val[i++] = new String(st.nextToken());
            }
        }
        else
        {
            return_val = new String[1];
            return_val[0] = s;
        }
        return return_val;
    }

    public static String[] splitString(String s) {
        String[] return_val = null;
        if (s.indexOf("|") != -1)
        {
            StringTokenizer st = new StringTokenizer(s, "|");
            return_val = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens())
            {
                return_val[i++] = new String(st.nextToken());
            }
        }
        else
        {
            return_val = new String[1];
            return_val[0] = s;
        }
        return return_val;
    }

    //Ìæ´újdk1.4µÄsplitº¯Êý
    public static String[] mySplit(String instr, String intoken)
    {
        if (instr == null ) instr = "" ;
        Vector vc=new Vector();
        int len = -1 ;
        do
        {
            if ( instr == null) instr = "" ;
            len=instr.indexOf(intoken);
            if ( len == -1)
            {
                vc.add(instr) ;
            }
            else
            {
                String tmp = instr.substring(0,len) ;
                if (tmp == null) tmp = "" ;
                vc.add(tmp) ;
                instr = instr.substring(len + intoken.length(), instr.length()) ;
            }
        }while (len > -1) ;

        String arrret[] = new String[vc.size()];
        for(int i=0;i<vc.size();i++)
        {
            arrret[i]=(String)vc.elementAt(i);
        }
        return arrret ;
    }


    /************** mainº¯Êý,²âÊÔÓÃ **********************************************/
    public static void main(String args[])
    {
        ToolsOfSystem aa = new ToolsOfSystem();
        //System.out.println(aa.replace("sasa234fd234fs", "234", "1234"));
        System.out.println(aa.replace("aaaa;;safdsfdsg;;sdgfdg;;",";;","@@@"));
        String ss = "´ó¼ÒºÃ";
        //System.out.println(aa.GBKToISO(ss));
        //System.out.println(aa.ISOToGBK(aa.GBKToISO(ss)));

        String str = "12323@@3,22323@@";
        /*
        String spStr[] = aa.splitString(str);
        System.out.println("@@@@@@@@@@@@@@@@@@  " + spStr.length);
        */
        //String spStr[] = str.split("23");
        //String spStr[] = mySplit(str,"||");
        String zz =str.substring(0,str.length()-2);
        System.out.println("aa="+zz);
        String spStr[] = str.split("@@");
        for(int i=0; i<spStr.length; i++)
            System.out.println("spStr["+i+"]="+spStr[i]);

    }


}
