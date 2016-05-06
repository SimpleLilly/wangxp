/**
 * file name  : ToolsOfSystem.java
 * authors    : elan huang
 * created    : 2003-03-28
 * copyright (c) 2002 by SI-TECH , All Rights Reserved
 *
 * modifications:  elan huang
 *
 */

package com.funtalk.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import java.util.Vector;

public class ToolsOfSystem {
    /**
     * 获取${}中的字符串
     * @param str
     * @return
     */
    public static String get$String(String input){
		Pattern pattern = Pattern.compile("\\$\\{[^}]*\\}",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		String match = "";
		while (matcher.find())
		{
			int start = matcher.start();
			int end = matcher.end();
			match = input.substring(start+2,end-1);
		}
    	return match;
    }
    
    /**
     * 去除字符串中的符合${}形式的子串
     * @param str
     * @return
     */
    public static String escape$String(String input){
    	return input.replaceAll("\\$\\{[^}]*\\}","");
    }
    

    public static String replace$String(String input, String newStr){
    	return input.replaceAll("\\$\\{[^}]*\\}", newStr);
    }
    
    public static String replace$SpecifyString(String input, String str, String newStr){
    	return input.replaceAll("\\$\\{"+str+"\\}", newStr);
    }
    
    /**
     * 将字符串由GBK转为ISO编码
     * @param strIn  输入字符串
     * @return 替换后的字符串
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
     *  替换字符串
     * @param   str 输入的原始字符串
     * @param   problemStr 需要替换的旧字符串
     * @param   replace 需要替换的新字符串
     * @return  替换后的字符串
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

    //替代jdk1.4的split函数
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
    
    public static String transStr2OracleNullStr(String sTempStr) {
        if (sTempStr == null || sTempStr.equals("") ||
            sTempStr.trim().length() == 0)
        {
            return "null";
        }
        else
        {
            return sTempStr.trim();
        }
    }
    
}
