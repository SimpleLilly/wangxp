<%@ page language="java" contentType="text/html; charset=GB2312"
    pageEncoding="GB2312"%>
    <%@page import="java.io.*" %>
    <%@ page import="com.funtalk.bean.ExcelBean" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<title>Insert title here</title>
</head>
<body>
  <%   
  
  String srcFile = (String)request.getAttribute("xlsFile");
  String workDir = srcFile.substring(0,srcFile.indexOf("."));
  ExcelBean.splitSheet(srcFile,workDir);
  ExcelBean.zipFile(workDir+".zip",workDir);
  try   {   
    
  String   url   =   workDir+".zip";//�ļ�·��  
  String   name   =   "����.zip";//��ʾ���û������ļ�������ie�������ؿ�����ʾ������ļ���   
  response.reset();   
  response.setContentType("bin");   
  response.setHeader("Content-Disposition",   "attachment;   filename="+new   String(name.getBytes(),"iso8859-1"));   
    
  ServletOutputStream   os   =   response.getOutputStream();   
  FileInputStream   in   =   new   FileInputStream(url);   
  byte[]   data   =   new   byte[1024];   
  int   temp   =   -1;   
  while((temp=in.read(data))!=   -1){   
  os.write(data,0,temp);   
  os.flush();   
  }//while//   
    
  os.close();   
  }   catch(Exception   e)   {   
  out.print(e.toString());   
  }   
  %>   
</body>
</html>