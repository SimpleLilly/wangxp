package com.funtalk.bean;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;


public class ExcelBean
{
    public ExcelBean()
    {
    }

    /**
     *  删除指定路径path下hours小时以前的临时文件
     * @param   path 文件路径
     * @param   hours 小时
     * @return  无
     */
    public static void DeleteTempFile(String path, int hours)
    {
        String str,dateStr;
        SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
        try
        {
            dateStr = form.format(new Date());
            Date d = form.parse(dateStr);
            long ld = d.getTime()/1000;
            d.setTime( (ld - 3600*hours)*1000);
            dateStr = form.format(d);

            File[] vFileList=(new File(path)).listFiles();
            for(int i=0; i<vFileList.length; i++)
            {

                str = vFileList[i].getName().substring(0,14);
                if(!(vFileList[i].isFile()) && str.compareTo(dateStr)<0){ //continue;
                	File[] sub_vFileList = vFileList[i].listFiles();
                	for(int j = 0; j < sub_vFileList.length; j++){
                		sub_vFileList[j].delete();
                	}
                }
                if(str.compareTo(dateStr)<0)
                    vFileList[i].delete();
            }
            return;
        }
        catch(Exception e)
        {
            return;
        }
    }

    //字符串替换
    private static String StrReplace(String str, String problemStr, String replace)
    {
        int len = problemStr.length();
        for(int i=str.lastIndexOf(problemStr); i>=0; i=str.lastIndexOf(problemStr, i-1))
        {
            if(i==0)
                str = replace+str.substring(i+len);
            else
                str = str.substring(0, i)+replace+str.substring(i+len);
        }
        return str;
    }

    //参数替换
    public static String ReplaceByParam(String str, List param)
    {
        int i;
        String name, value;
        i=0;
        if((str==null)||str.equals(""))
            return "";
        if(param==null)
            return str;
        for(i=0; i<param.size(); i++)
        {
            String v[] = (String [])param.get(i);
            name = v[0];
            value = v[1]+"";
            str = StrReplace(str, "${"+ name + "}", value);
        }
        return str;
    }

    //获取数据起始位置
    private static int[] GetBeginEndData(String str, List data)
    {
        int i, b=-1, e=-1;
        int val[] = new int[2];
        boolean flag = false;

        for(i=0; i<data.size(); i++)
        {
            String v[] = (String [])data.get(i);
            if(v[0]==null)
                v[0]="";
            String tmpStr = v[0].trim();
            if(str.equals(tmpStr))
            {
                if(!flag) {  b=i;  flag = true;  }
            }
            else
            {
                if(flag)  {  e=i-1; break;  }
            }
        }
        if((flag)&&(e==-1))
            e = data.size()-1;
        val[0] = b;
        val[1] = e;
        return val;
    }

    //读取模板, 0填充方式，1填充坐标的Vector
    public static Vector ReadExcelModel(HSSFWorkbook wb)
    {
        int i,j,k,m;
        int sheetNum = wb.getNumberOfSheets();
        Vector XYP = new Vector();

        for(i=0; i<sheetNum; i++)
        {
            HSSFSheet sheet = wb.getSheetAt(i);
            int top = sheet.getFirstRowNum();
            int buttom = sheet.getLastRowNum();

            int FillType = -1;            //填充方式，-1:无; 0:纵向; 1横向
            Vector tag = new Vector();    //填充坐标
            Vector par = new Vector();  //参数坐标
            Vector sheetV = new Vector();

            for(j=top; j<=buttom; j++)
            {
                HSSFRow r = sheet.getRow(j);
                if(r==null)    continue;
                short left = r.getFirstCellNum();
                short right = r.getLastCellNum();
                for(k=left; k<=right; k++)
                {
                    HSSFCell cell = r.getCell((short)k);
                    if(cell==null)     continue;
                    int CellType = cell.getCellType();
                    if(CellType!=1)   continue;
                    String cv = cell.getStringCellValue();
                    if(cv==null)    continue;
                    cv = cv.trim();

                    //记录特殊标记
                    if(cv.equals("#$"))     //纵向
                    {
                        if(FillType==1)
                        {
                            System.out.println("模板中不能同时出现#$和$#");
                            return null;
                        }
                        FillType = 0;
                        Vector v = new Vector();
                        v.addElement(k+"");
                        v.addElement(j+"");
                        tag.addElement(v);
                    }
                    else if(cv.equals("$#"))   //横向
                    {
                        if(FillType==0)
                        {
                            System.out.println("模板中不能同时出现#$和$#");
                            return null;
                        }
                        FillType = 1;
                        Vector v = new Vector();
                        v.addElement(j+"");
                        v.addElement(k+"");
                        tag.addElement(v);
                    }
                    else if(cv.equals("&"))
                    {
                        if(FillType==-1)
                        {
                            System.out.println("模板在未确定填充方式时出现了填充符");
                            return null;
                        }

                        if(FillType==0)
                        {
                            for(m=0; m<tag.size(); m++)
                            {
                                Vector v = (Vector)tag.elementAt(m);
                                if(v.elementAt(0).toString().equals(k+""))
                                {
                                    v.addElement(j+"");
                                    break;
                                }
                            }
                        }
                        else
                        {
                            for(m=0; m<tag.size(); m++)
                            {
                                Vector v = (Vector)tag.elementAt(m);
                                if(v.elementAt(0).toString().equals(j+""))
                                {
                                    v.addElement(k+"");
                                    break;
                                }
                            }
                        }
                    }
                    else if(cv.indexOf("${")>=0)  //此处需要替换参数
                    {
                        Vector v = new Vector();
                        v.addElement(j+"");
                        v.addElement(k+"");
                        v.addElement(cv+"");
                        par.addElement(v);
                    }
                }
            }
            sheetV.addElement(FillType+"");
            sheetV.addElement(par);
            sheetV.addElement(tag);
            XYP.addElement(sheetV);
        }
        return XYP;
    }

    /**
     *  生成Excel文件
     * @param   data 报表数据
     * @param   param 参数条件，0条件名, 1条件值
     * @param   model 模块文件名
     * @param   xlsName 生成的Excel文件名
     * @param   isSheet 是否匹配Sheet名称，0不匹配,1匹配
     * @return  是否成功
     */
    public static boolean CreateXlsFile(List data, List param, String model, String xlsName, String isSheet)
    {
        int i,j,k;
        Vector MultiSheet=new Vector();
        Vector tmpV=null;
        boolean AutoFlag = model.substring(model.length()-5).equals("@.xls");  //是否自动扩展行

        try
        {
            FileInputStream finput = new FileInputStream(model);
            POIFSFileSystem fs = new POIFSFileSystem( finput );
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            int sheetNum = wb.getNumberOfSheets();

            //获取模板中的动态sheet信息
            for(i=0; i<sheetNum; i++)
            {
                String str = wb.getSheetName(i);
                if((str.length()>=2)&&(str.substring(0,1).equals("@")))
                {
                    tmpV = new Vector();
                    tmpV.addElement(i+"");
                    tmpV.addElement("");  tmpV.addElement("");   tmpV.addElement("");
                    MultiSheet.addElement(tmpV);
                }
            }
            if(MultiSheet.size()>0)
            {
                j=0;
                String FirstStr="";
                tmpV= (Vector)MultiSheet.elementAt(j);
                for(i=0; i<data.size(); i++)
                {
                    String v[] = (String [])data.get(i);
                    if(v[0]==null) continue;
                    String tmpStr = v[0].trim();
                    if((tmpStr=="")||(tmpStr.charAt(0)!='@')) continue;
                    if(!(tmpStr.equals(FirstStr)))
                    {
                        tmpV.setElementAt(tmpStr,1);
                        //String spStr[] = tmpStr.split("@");
                        String spStr[] = ToolsOfSystem.mySplit(tmpStr,"@");
                        tmpV.setElementAt(spStr[1],2);
                        tmpV.setElementAt(spStr[2],3);
                        FirstStr = tmpStr;
                        j++;
                        if(j>=MultiSheet.size()) break;
                        tmpV= (Vector)MultiSheet.elementAt(j);
                    }
                }
            }

            //取模板信息
            Vector XYP = ReadExcelModel(wb);
            if(XYP==null)
            {
                finput.close();
                return false;
            }

            //循环处理各个sheet
            for(i=0; i<sheetNum; i++)
            {
                Vector sheetV = (Vector)XYP.elementAt(i);
                HSSFSheet sheet = wb.getSheetAt(i);

                //查看本sheet是否为动态sheet
                int Mi=-1;
                for(j=0; j<MultiSheet.size(); j++)
                {
                    tmpV= (Vector)MultiSheet.elementAt(j);
                    if(Integer.parseInt(tmpV.elementAt(0).toString())==i)
                    {
                        Mi=j;
                        break;
                    }
                }

                //此sheet需要删除
                if((Mi!=-1)&&(tmpV.elementAt(1).toString().length()==0))
                {
                    wb.setSheetName(i, "@delete"+i);
                    continue;
                }

                //替换参数
                Vector par = (Vector)sheetV.elementAt(1);
                for(j=0; j<par.size(); j++)
                {
                    Vector v = (Vector)par.elementAt(j);
                    HSSFRow r = sheet.getRow(Integer.parseInt(v.elementAt(0).toString()));
                    HSSFCell cell = r.getCell((short)Integer.parseInt(v.elementAt(1).toString()));
                    String cp = ReplaceByParam(v.elementAt(2).toString(), param);
                    if(Mi!=-1)
                    {
                        cp = StrReplace(cp, "${SHEET_CODE}", tmpV.elementAt(2).toString());
                        cp = StrReplace(cp, "${SHEET_NAME}", tmpV.elementAt(3).toString());
                    }
                 //   cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellValue(cp);
                }

                //填充数据
                int FillType = Integer.parseInt(sheetV.elementAt(0).toString());
                if(FillType == -1)  continue;
                int beg=-1, end=-1, begK=0;
                if(isSheet.equals("0"))   //不匹配Sheet
                {
                    if(i>0)    break;
                    beg = 0;
                    end = data.size()-1;
                    begK = 0;
                }
                else                      //匹配Sheet
                {
                    String str;
                    if(Mi!=-1)
                        str = tmpV.elementAt(1).toString();
                    else
                        str = wb.getSheetName(i);
                    int pos[] = GetBeginEndData(str, data);
                    beg = pos[0];
                    end = pos[1];
                    begK = 1;
                }

                if(beg==-1)    continue;
                Vector tag = (Vector)sheetV.elementAt(2);

                //自动扩充数据wangjs2006-06-28加入
                if(AutoFlag)
                {
                    Vector ttV = new Vector((Vector)tag.elementAt(0));
                    int y = Integer.parseInt(ttV.elementAt(0).toString());
                    HSSFRow ry = sheet.getRow(y);
                    for(j=beg; j<=end; j++)  //逐行读取源数据
                    {
                        String v[] = (String [])data.get(j);
                        for(k=begK; k<v.length; k++)
                        {
                            if(k-begK+1 >= ttV.size())   break;
                            int x = Integer.parseInt(ttV.elementAt(k-begK+1).toString());
                            HSSFRow r = sheet.getRow(y+j-beg);
                            if(r==null)
                               r = sheet.createRow(y+j-beg);
                            HSSFCell cell = r.getCell((short)x);
                            if(cell==null)
                               cell= r.createCell((short)x);
                           // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                            cell.setCellStyle(ry.getCell((short)x).getCellStyle());
                            try
                            {
                               double cv = Double.parseDouble(v[k]);
                               cell.setCellValue(cv);
                            }
                            catch (Exception ex)
                            {
                                cell.setCellValue(v[k]);
                            }
                        }
                    }
                    continue;
                }

                //非自动扩展报表
                for(j=beg; j<=end; j++)  //逐行读取源数据
                {
                    String v[] = (String [])data.get(j);
                    if(j-beg >= tag.size())  break;
                    Vector vv = (Vector)tag.elementAt(j-beg);
                    int xy = Integer.parseInt(vv.elementAt(0).toString());
                    for(k=begK; k<v.length; k++)
                    {
                        if(k-begK+1 >= vv.size())   break;
                        int x = Integer.parseInt(vv.elementAt(k-begK+1).toString());
                        int y = xy;

                        if(FillType==0)  //纵向填充方式
                        {
                            int ti = x;  x = y;  y = ti;
                        }

                        //判断是否含特殊字符@@@@@@@@@@@@@@@@@@@@@@
                        if( (v[k]!=null)&&(v[k].equals("@^@")) )
                        {
                            HSSFRow r = sheet.getRow(y);
                            if(FillType==0)  //纵向填充方式
                            {
                                sheet.setColumnWidth((short)x,(short)0);
                            }
                            else
                            {
                                r.setHeight((short)0);
                            }
                            HSSFCell cell = r.getCell((short)x);
                            cell.setCellValue(v[k]);
                            continue;
                        }
                        //判断结束@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                        HSSFRow r = sheet.getRow(y);
                        HSSFCell cell = r.getCell((short)x);
                        ///cell.setEncoding(HSSFCell.ENCODING_UTF_16);

                        try
                        {
                           if( ((v[k]!=null)&&(v[k].charAt(0)=='0')&&(v[k].length()>1))||
                               ((v[k]!=null)&&(v[k].length()>11)) )
                           {
                               cell.setCellValue(v[k]);
                           }
                           else
                           {
                               double cv = Double.parseDouble(v[k]);
                               cell.setCellValue(cv);
                           }
                        }
                        catch (Exception ex)
                        {
                            cell.setCellValue(v[k]);
                        }
                    }
                }
                //将没有填充的行或列置为隐藏
                //2006-05-23 wanjs修改
                for(j=end-beg+1; j<tag.size(); j++)
                {
                    k = Integer.parseInt( ((Vector)tag.elementAt(j)).elementAt(0).toString());
                    if(FillType==0)  //纵向填充方式
                    {
                        sheet.setColumnWidth((short)k,(short)0);
                    }
                    else
                    {
                        HSSFRow r = sheet.getRow(k);
                        r.setHeight((short)0);
                    }
                }
            }

            //改名未使用的sheet
            for(i=MultiSheet.size()-1; i>=0; i--)
            {
                tmpV=(Vector)MultiSheet.elementAt(i);
                j = Integer.parseInt(tmpV.elementAt(0).toString());
                if(tmpV.elementAt(1).toString().length()>0)
                    wb.setSheetName(j, tmpV.elementAt(2).toString());
            }

            //保存文件
            FileOutputStream fileOut = new FileOutputStream(xlsName);
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();
            finput.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            return false;
        }
    }

    //根据模板收集Excel数据
    public static List GetExcelData(String model, String xlsName)
    {
        int i,j,k;
        try
        {
            //取模板信息
            FileInputStream f1 = new FileInputStream(model);
            POIFSFileSystem fs1 = new POIFSFileSystem( f1 );
            HSSFWorkbook wb1 = new HSSFWorkbook(fs1);
            Vector XYP = ReadExcelModel(wb1);
            f1.close();
            if(XYP==null)
                return null;

            //读取Excel文件
            FileInputStream f2 = new FileInputStream(xlsName);
            POIFSFileSystem fs2 = new POIFSFileSystem( f2 );
            HSSFWorkbook wb2 = new HSSFWorkbook(fs2);
            int sheetNum = wb2.getNumberOfSheets();

            List val = new ArrayList();
            for(i=0; i<sheetNum; i++)
            {
                HSSFSheet sheet = wb2.getSheetAt(i);
                String ar[] = new String[2];
                ar[0] = wb2.getSheetName(i);
                Vector sheetV = (Vector)XYP.elementAt(i);
                int FillType = Integer.parseInt(sheetV.elementAt(0).toString());
                if(FillType == -1)  continue;
                Vector tag = (Vector)sheetV.elementAt(2);
                String str = "";
                for(j=0; j<tag.size(); j++)
                {
                    Vector v = (Vector)tag.elementAt(j);
                    int xy = Integer.parseInt(v.elementAt(0).toString());
                    for(k=1; k<v.size(); k++)
                    {
                        int x = xy;
                        int y = Integer.parseInt(v.elementAt(k).toString());
                        if(FillType==1)  //横向填充方式
                        {
                            int ti = x;  x = y;  y = ti;
                        }
                        HSSFRow r = sheet.getRow(y);
                        HSSFCell cell = r.getCell((short)x);
                        String cv = "";
                        if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
                            cv = cell.getNumericCellValue() + "";
                        else
                            cv = cell.getStringCellValue();
                        str = str + ((k==1)?"":"|") + cv;
                    }
                    str += (j==tag.size()-1)?"":":";
                }
                ar[1] = str;
                val.add(ar);
            }

            return val;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * 
     * @param args
     */
	public static boolean splitSheet(String inFile,String workDir){
		POIFSFileSystem fs; 
		POIFSFileSystem sub_fs;
		File file = new File(workDir);
		file.mkdirs();
		try { 
			fs = new POIFSFileSystem(new FileInputStream(inFile));

			HSSFWorkbook wb = new HSSFWorkbook(fs);
			int sheetNum = wb.getNumberOfSheets();
			for(int i = 0; i < sheetNum; i++){
				if(wb.getSheetName(i).indexOf("@delete") != -1)
					continue;
				//取文件名
				String sub_filename = wb.getSheetAt(i).getRow(1).getCell((short)0).getStringCellValue();
				copy(inFile,workDir+"/"+sub_filename+".xls");
				sub_fs = new POIFSFileSystem(new FileInputStream(workDir+"/"+sub_filename+".xls"));
				HSSFWorkbook sub_wb = new HSSFWorkbook(sub_fs);
				
				int sub_sheetNum = sub_wb.getNumberOfSheets();
				String str = "";
				for(int j = 0; j <sub_sheetNum; j++){

					if(!wb.getSheetName(i).equals(sub_wb.getSheetName(j)) ){
						str += sub_wb.getSheetName(j)+",";
					}
				}

				String[] arr = str.split(",");
				for(int k = 0; k < arr.length; k++){
					sub_wb.removeSheetAt(sub_wb.getSheetIndex(arr[k]));
				}
				
			    FileOutputStream fileOut = new FileOutputStream(workDir+"/"+sub_filename+".xls");
			    sub_wb.write(fileOut);
			    fileOut.close();
			}
		    return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	    // Write the output to a file

	}
	/**
	 * 
	 * @param src
	 * @param dist
	 */
	private static boolean copy(String src,String dist){
		BufferedOutputStream out=null;
		BufferedInputStream in=null;
		byte[] b=new byte[1024];
 		try{
 			in=new BufferedInputStream(new FileInputStream(src));
 			out=new BufferedOutputStream(new FileOutputStream(dist));
 			int len;
 			while((len=in.read(b))!=-1){
 				out.write(b,0,len);
 				out.flush();
 			}
 			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
			}
		finally{
			if (out!=null){
				try{
					out.close();
				}
				catch(Exception e){e.printStackTrace();}
			}
			if (in!=null){
				try{
					in.close();
				}
				catch(Exception e){e.printStackTrace();}
			}			
		}
	
	}
	
	 public static boolean zipFile(String zipName,String zipDir) {	 
		 try {			 
			 FileOutputStream f = new FileOutputStream(zipName);     
			 ZipOutputStream out = new ZipOutputStream(new DataOutputStream(f));
			 File dir = new File(zipDir);
				if(dir.isDirectory()){
					File[] files = dir.listFiles();
					for(int i = 0; i < files.length; i++){
						if(!files[i].isDirectory()){
							DataInputStream in = new DataInputStream(new FileInputStream(files[i]));
							 out.putNextEntry(new ZipEntry(files[i].getName()));               
							 int c;
							 while ((c = in.read()) != -1)          
								 out.write(c);             
							 in.close();           
						}
					}
				} 
			 out.close();   
			 return true;
			 } 
	 		catch (Exception e) {  
				 e.printStackTrace();    
				 return false;
	 		}
	 }
    /********   测试   ***************************************************/

    public static void main(String[] args)
    {
    }
}
