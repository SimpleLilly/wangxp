package com.funtalk.bean;
 
import java.awt.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.entity.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.category.*;
import org.jfree.data.general.*;  
import org.jfree.data.xy.*;   
import org.jfree.data.time.*; 
import org.jfree.util.TableOrder;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;

import java.io.*;
import java.util.*; 
import java.text.SimpleDateFormat;

import com.funtalk.bean.ToolsOfSystem;

public class ChartBean
{
    public ChartBean()
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
                if(!(vFileList[i].isFile()))  continue;
                str = vFileList[i].getName().substring(0,14);
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

    /**
     *  生成饼状图（多图）
     * @param   data 数据
     * @param   FileName 图片文件名
     * @param   Width 宽度
     * @param   Height 高度
     * @param   Title 图片标题
     * @return  是否成功
     */
    public static boolean createMultiPiesChart(java.util.List data, String FileName,
                      int Width, int Height, String Title, PrintWriter pw)
    {
        int i, j;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sr[] = (String [])data.get(0);
        int seriesCount = sr.length - 1;
        for(i=0; i<seriesCount; i++) 
        {
            for(j=1; j<data.size(); j++)
            {
                String vv[] = (String [])data.get(j);
                dataset.addValue(Double.parseDouble(vv[i+1]),sr[i+1],vv[0]);
            }
        }

        JFreeChart chart = null;
        CategoryPlot plot = null;
        chart = ChartFactory.createMultiplePieChart(Title, dataset, TableOrder.BY_ROW, true, true, false);
        MultiplePiePlot multiplepieplot = (MultiplePiePlot)chart.getPlot();
        JFreeChart chart1 = multiplepieplot.getPieChart();
        PiePlot pieplot = (PiePlot)chart1.getPlot();
        //{0}标签 ({1}值 {2}百分比)
        pieplot.setLabelGenerator(new StandardPieItemLabelGenerator("{2}"));
        pieplot.setLabelFont(new Font("SansSerif", 0, 10));

        //绘图区外背景颜色
        //chart.setBackgroundPaint(Color.white);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0,1000, Color.blue));
        try
        {
            //ChartUtilities.saveChartAsJPEG(new File(FileName), 100, chart, Width, Height);
            ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
            ChartUtilities.saveChartAsPNG((new File(FileName)), chart,Width, Height,info, true, 100);
            ChartUtilities.writeImageMap(pw, FileName, info, false);
            pw.flush();
        }
        catch (Exception exz)
        {
            System.out.print("....Cant't Create image File!!" + exz.toString());
            return false;
        }
        return true;
    }

    /**
     *  生成图
     * @param   data 数据
     * @param   FileName 图片文件名
     * @param   chartType 图片类型  0柱状，1线
     * @param   Width 宽度
     * @param   Height 高度
     * @param   Title 图片标题
     * @param   YLable Y轴标题
     * @return  是否成功
     */
     public static boolean createCharts(java.util.List data, String[] filedIdx, String FileName, String chartType,
              String XType, int Width, int Height, String Title, String YLable, PrintWriter pw)
    {
        int i, j;
        int TType = -1;
        SimpleDateFormat sf = null;
        Dataset dataset = null;
        JFreeChart chart = null;
        Plot plot = null;
        String sr[] = (String [])data.get(0);
        //sr[] is 字段的别名
        for(int k = 0; k < sr.length; k++){
        	System.out.println("sr["+k+"]="+sr[k]);
        }
        int seriesCount = 0; //sr.length - 1;
        String[] numIdx = null;
        //filedIdx[0] 分类字段的序号
        //filedIdx[1] 数值字段的序号
        if(filedIdx[1].equals(""))
        	seriesCount = sr.length - 1;
        else{
        	numIdx = filedIdx[1].split(",");
        	seriesCount = numIdx.length;
        }
        //分类*************************************************
        if(XType.equals("Category"))
        {
            dataset = new DefaultCategoryDataset();
            //增加指定series
            if(filedIdx[0].equals(""))
            {
	            for(i=0; i<seriesCount; i++)
	            {
	                for(j=1; j<data.size(); j++)
	                {
	                    String vv[] = (String [])data.get(j);
	                    ((DefaultCategoryDataset)dataset).addValue(Double.parseDouble(vv[i+1]),sr[i+1],vv[0]);
	                }
	            }
	
	            if(chartType.equals("0"))  //柱状图
	            {
	                chart = ChartFactory.createBarChart3D(Title, sr[0], YLable, ((DefaultCategoryDataset)dataset), PlotOrientation.VERTICAL, true, true, false);
	                plot = chart.getCategoryPlot();
	                //chart.getTitle().setFont(new Font("Dialog", 0, 30));
	            }
	            else if(chartType.equals("1"))  //线图
	            {
	                chart = ChartFactory.createLineChart(Title, sr[0], YLable, ((DefaultCategoryDataset)dataset), PlotOrientation.VERTICAL, true, true, false);
	                plot = chart.getCategoryPlot();
	                LineAndShapeRenderer renderer = (LineAndShapeRenderer)((CategoryPlot)plot).getRenderer();
	                renderer.setShapesVisible(true);
	                renderer.setDefaultShapesFilled(true);
	            }
            }
            else
            {
            	//System.out.println("filedIdx[0] "+filedIdx[0]);
            	//System.out.println("filedIdx[1] "+filedIdx[1]);
            	//System.out.println("numIdx[0] "+numIdx[0]);

                for(j=1; j<data.size(); j++)
                {
                    String vv[] = (String [])data.get(j);
                    ((DefaultCategoryDataset)dataset).addValue(Double.parseDouble(vv[Integer.parseInt(numIdx[0])]),vv[Integer.parseInt(filedIdx[0])],vv[0]);
                }

	            if(chartType.equals("0"))  //柱状图
	            {
	                chart = ChartFactory.createBarChart3D(Title, sr[0], YLable, ((DefaultCategoryDataset)dataset), PlotOrientation.VERTICAL, true, true, false);
	                plot = chart.getCategoryPlot();
	                //chart.getTitle().setFont(new Font("Dialog", 0, 30));
	            }
	            else if(chartType.equals("1"))  //线图
	            {
	                chart = ChartFactory.createLineChart(Title, sr[0], YLable, ((DefaultCategoryDataset)dataset), PlotOrientation.VERTICAL, true, true, false);
	                plot = chart.getCategoryPlot();
	                LineAndShapeRenderer renderer = (LineAndShapeRenderer)((CategoryPlot)plot).getRenderer();
	                renderer.setShapesVisible(true);
	                renderer.setDefaultShapesFilled(true);
	            }
            	
            }
            CategoryAxis domainAxis = ((CategoryPlot)plot).getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.4D));

        }
        //数值************************************************************
        else if(XType.equals("Number"))
        {
            dataset = new XYSeriesCollection();
            for(i = 0; i <seriesCount; i++)
            {
                XYSeries xyseries = new XYSeries(sr[i+1]);
                for(j =1; j <data.size(); j++)
                {
                    String vv[] = (String [])data.get(j);
                    xyseries.add(Double.parseDouble(vv[0]), Double.parseDouble(vv[i+1]));
                }
                ((XYSeriesCollection)dataset).addSeries(xyseries);
            }

            if(chartType.equals("0"))  //柱状图
            {
                chart = ChartFactory.createXYBarChart(Title, sr[0], true, YLable, ((XYSeriesCollection)dataset), PlotOrientation.VERTICAL, true, true, false);
                plot = (XYPlot)chart.getPlot();
                ((XYPlot)plot).setRenderer(new ClusteredXYBarRenderer());
            }
            else if(chartType.equals("1"))  //线图
            {
                chart = ChartFactory.createXYLineChart(Title, sr[0], YLable, ((XYSeriesCollection)dataset), PlotOrientation.VERTICAL, true, true, false);
                plot = (XYPlot)chart.getPlot();
                XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)((XYPlot)plot).getRenderer();
                renderer.setShapesVisible(true);
                renderer.setDefaultShapesFilled(true);
           }
        }
        else //日期************************************************************
        {
            Class c = null;
            if(XType.equals("yyyy"))
            {
                TType = 0;  c = Year.class;   sf = new SimpleDateFormat("yyyy");
            }
            else if(XType.equals("yyyy-MM"))
            {
                TType = 1;  c = Month.class;  sf = new SimpleDateFormat("yyyy-MM");
            }
            else if(XType.equals("yyyy-MM-dd"))
            {
                TType = 2;  c = Day.class;    sf = new SimpleDateFormat("MM-dd");
            }
            else if(XType.equals("yyyy-MM-dd-HH-MI")) /////200811271515
            {
            	TType = 4; c = Minute.class;	  sf = new SimpleDateFormat("HH:mm");
            }
            else ////yyyy-MM-dd-HH
            {
            	TType = 3; c = Hour.class;	  sf = new SimpleDateFormat("HH:00");
            }

            dataset = new TimeSeriesCollection();
            
            //增加指定series
            if(filedIdx[0].equals(""))
            {
            	if(filedIdx[1].equals(""))
            	{
                    for(i = 0; i <seriesCount; i++)
                    {
                        TimeSeries ts= new TimeSeries(sr[i+1], c);
                        for(j =1; j <data.size(); j++)
                        {
                            String vv[] = (String [])data.get(j);
                            String t = vv[0];

                            RegularTimePeriod rtp=null;
                            if(TType==0)
                                rtp = new Year(Integer.parseInt(t));
                            else if(TType==1)
                                rtp = new Month(Integer.parseInt(t.substring(4)),Integer.parseInt(t.substring(0,4)));
                            else if(TType==2)
                                rtp = new Day(Integer.parseInt(t.substring(6)),Integer.parseInt(t.substring(4,6)),Integer.parseInt(t.substring(0,4)));
                            else if(TType==4)  /////////200811271515
                            {
                            	/////////////System.out.println("==================>t="+t);
                                rtp = new Minute(Integer.parseInt(t.substring(10,12)), Integer.parseInt(t.substring(8,10)), Integer.parseInt(t.substring(6,8)), Integer.parseInt(t.substring(4,6)), Integer.parseInt(t.substring(0,4)));
                            }
                            else
                            	rtp = new Hour(Integer.parseInt(t.substring(8)),Integer.parseInt(t.substring(6,8)),Integer.parseInt(t.substring(4,6)),Integer.parseInt(t.substring(0,4)));
                            

                                ///////////ts.add(rtp, Double.parseDouble(vv[i+1]) );
                                ts.addOrUpdate(rtp, Double.parseDouble(vv[i+1]) );
                        }
                        
                        ((TimeSeriesCollection)dataset).addSeries(ts);
                        
                    }
            	}
            	else
            	{
                	numIdx = filedIdx[1].split(",");
                	seriesCount = numIdx.length;
                    for(i = 0; i <seriesCount; i++)
                    {
                        TimeSeries ts= new TimeSeries(sr[Integer.parseInt(numIdx[i])], c);
                        for(j =1; j <data.size(); j++)
                        {
                            String vv[] = (String [])data.get(j);
                            String t = vv[0];

                            RegularTimePeriod rtp=null;
                            if(TType==0)
                                rtp = new Year(Integer.parseInt(t));
                            else if(TType==1)
                                rtp = new Month(Integer.parseInt(t.substring(4)),Integer.parseInt(t.substring(0,4)));
                            else if(TType==1)
                                rtp = new Day(Integer.parseInt(t.substring(6)),Integer.parseInt(t.substring(4,6)),Integer.parseInt(t.substring(0,4)));
                            else if(TType==4)  /////////200811271515
                                rtp = new Minute(Integer.parseInt(t.substring(10,12)), Integer.parseInt(t.substring(8,10)), Integer.parseInt(t.substring(6,8)), Integer.parseInt(t.substring(4,6)), Integer.parseInt(t.substring(0,4)));
                            else
                            	rtp = new Hour(Integer.parseInt(t.substring(8)),Integer.parseInt(t.substring(6,8)),Integer.parseInt(t.substring(4,6)),Integer.parseInt(t.substring(0,4)));
                            
                            ts.add(rtp, Double.parseDouble(vv[Integer.parseInt(numIdx[i])]) );
                        }
                        
                        ((TimeSeriesCollection)dataset).addSeries(ts);
                        
                    }
            	}

            }
            else
            {
            	//指定了series后，只可以是一个num_,并且肯定有num_
            	numIdx = filedIdx[1].split(",");
          		Set seriesSet = new HashSet();

          		Set timeSet = new HashSet();

      			for (j = 1; j < data.size(); j++)
      			{
      				String vv[] = (String [])data.get(j);
      				seriesSet.add( vv[ Integer.parseInt(filedIdx[0]) ] );
      				timeSet.add( vv[0] );
      			}

      			Iterator iter = seriesSet.iterator();

                RegularTimePeriod rtp=null;

      			while(iter.hasNext()){

      				String seriesKey = (String) iter.next();
                    TimeSeries ts= new TimeSeries(seriesKey, c);
          			Iterator timeIter = timeSet.iterator();
  					while(timeIter.hasNext())
  					{

  	          			double sumDou = 0;
  						String timeKey = (String) timeIter.next();
  						
                        if(TType==0)
                            rtp = new Year(Integer.parseInt(timeKey));
                        else if(TType==1)
                            rtp = new Month(Integer.parseInt(timeKey.substring(4)),Integer.parseInt(timeKey.substring(0,4)));
                        else if(TType==2)
                            rtp = new Day(Integer.parseInt(timeKey.substring(6)),Integer.parseInt(timeKey.substring(4,6)),Integer.parseInt(timeKey.substring(0,4)));
                        else
                        	rtp = new Hour(Integer.parseInt(timeKey.substring(8)),Integer.parseInt(timeKey.substring(6,8)),Integer.parseInt(timeKey.substring(4,6)),Integer.parseInt(timeKey.substring(0,4)));
	          			for (j = 1; j < data.size(); j++)
	          			{
	          				String vv[] = (String [])data.get(j);
	
	          				if(seriesKey.equals( vv[ Integer.parseInt(filedIdx[0]) ] ) )
	          				{
	                            String t = vv[0];
	
	          						if(timeKey.equals(t))
	          						{
	          							sumDou += Double.parseDouble(vv[Integer.parseInt(numIdx[0])]);
	        
	          						}
	          				}
	          			}
	                    ts.add(rtp, sumDou );
  					}

                    ((TimeSeriesCollection)dataset).addSeries(ts);
      			}

            }

            ((TimeSeriesCollection)dataset).setDomainIsPointsInTime(true);

            if(chartType.equals("0"))  //柱状图
            {
                chart = ChartFactory.createXYBarChart(Title, sr[0], true, YLable, ((IntervalXYDataset)dataset), PlotOrientation.VERTICAL, true, true, false);
                plot = (XYPlot)chart.getPlot();
                ((XYPlot)plot).setRenderer(new ClusteredXYBarRenderer());
            }
            else if(chartType.equals("1"))  //线图
            {
                chart = ChartFactory.createTimeSeriesChart(Title, sr[0], YLable, (TimeSeriesCollection)dataset, true, true, false);
                plot = (XYPlot)chart.getPlot();
                XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)((XYPlot)plot).getRenderer();
                renderer.setShapesVisible(true);
                renderer.setDefaultShapesFilled(true);
            }
            
            
            DateAxis axis = (DateAxis)((XYPlot)plot).getDomainAxis();
            axis.setDateFormatOverride(sf);
            if(TType!=2 && TType!=3 && TType!=4)
                axis.setTickUnit(new DateTickUnit(TType, 1, sf));

        }

        //绘图区外背景颜色
        //chart.setBackgroundPaint(Color.white);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0,1000, Color.blue));

        try
        {
            //ChartUtilities.saveChartAsJPEG(new File(FileName), 100, chart, Width, Height);
            ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
            ChartUtilities.saveChartAsPNG((new File(FileName)), chart,Width, Height,info, true, 100);
            EntityCollection enc = info.getEntityCollection();
            if(TType != -1)
            {
                for(i=0; i<enc.getEntityCount(); i++)
                {
                    String tmpStr = enc.getEntity(i).getToolTipText();
                    if(tmpStr==null)    continue;
                    int bi = tmpStr.indexOf(": (");
                    int ei = tmpStr.indexOf(", ");
                    if((bi>=0)&&(ei>=0))
                    {
                        String tmpDate1 = tmpStr.substring(bi+3,ei);
                        String tmpDate2 = sf.format((new SimpleDateFormat()).parse(tmpDate1));
                        tmpStr = ToolsOfSystem.replace(tmpStr, tmpDate1, tmpDate2);
                        enc.getEntity(i).setToolTipText(tmpStr);
                    }
                }
            }
            ChartUtilities.writeImageMap(pw, FileName, info, false);
            pw.flush();
        }
        catch (Exception exz)
        {
            System.out.print("....Cant't Create image File!!" + exz.toString());
            return false;
        }
        return true;
    }

    public static void main(String[] args)
    {
        ChartBean te=new ChartBean();
    }

}
