<%@ page language="java"   import="java.util.*,java.text.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="ww" uri="webwork"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	
	Calendar calendar=Calendar.getInstance();
	calendar.add(Calendar.MONTH, -1);
    String month = new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>用户量统计</title> 

<link rel="stylesheet" type="text/css" href="../ext-4.2.1/css/ext-theme-neptune-all.css" />
<script type="text/javascript" src="../ext-4.2.1/bootstrap.js"></script>
<script type="text/javascript" src="../ext-4.2.1/ext-lang-zh_CN.js"></script>

<script type="text/javascript">


Ext.require([
    'Ext.form.*',
    'Ext.data.*',
    'Ext.chart.*',
    'Ext.grid.Panel',
    'Ext.layout.container.Column'
]);

var month="<%=month %>";  

Ext.onReady(function(){



var bd = Ext.getBody(),
        form = false,
        rec = false,
        selectedStoreItem = false,
        
selectItem = function(storeItem) {        
   var name = storeItem.get('city_name'),series = barChart.series.get(0),i,items,l;
   
       series.highlight = true;
       series.unHighlightItem();
       series.cleanHighlights();
            
for (i = 0, items = series.items, l = items.length; i < l; i++) {
                
if (name == items[i].storeItem.get('city_name')) {
                    
        selectedStoreItem = items[i].storeItem;                   
        series.highlightItem(items[i]);
        break;
   }
 }           
series.highlight = false;        
};


   
var ds = Ext.create('Ext.data.ArrayStore',{
	     proxy:Ext.create( Ext.data.proxy.Ajax,{ url:'<%=path%>/statistics/SqlGenerateData.jspa?query=stat_time='+month+'&stat_type=statUsers' }),
         fields: [ {name: 'prov_name'},{name: 'city_name'}, {name: 'month_jihuo',  type: 'float'},{name: 'month_stop',  type: 'float'} ],
         autoLoad:true  });
    

//create a grid that will list the dataset items.
    

var gridPanel = Ext.create('Ext.grid.Panel', {   
id: 'company-form',       
flex: 0.7,       
store: ds,
title:'Company Data', //collapsible : true,
columns: [ {text: '省份',width:200,sortable :false,align: 'center',dataIndex: 'prov_name'},        
           {text: '地市',width:150,sortable :false,align: 'center',dataIndex: 'city_name'},            
           {text:'激活量',width:150,sortable : true,align: 'right',dataIndex: 'month_jihuo'},
           {text:'停机量',width:150,sortable : true,align: 'right',dataIndex: 'month_stop'} ],

listeners: {
         
	selectionchange: function(model, records) {

		if (records[0]) 
		{
         rec = records[0];		  
		 selectItem(rec);
		  }
		
	 }
}
});

    

//create a bar series to be at the top of the panel.
    
var barChart = Ext.create('Ext.chart.Chart', {       
flex: 1,
shadow: true,
animate: true,
//theme: 'Category5',
legend: { position: 'bottom'},
store: ds, 
axes: [{ type: 'Numeric', position: 'left', fields: ['month_jihuo','month_stop'],minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}}, 
                          label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
       {type: 'Category', position: 'bottom',fields: ['city_name'],
                          label: { renderer: function(v) { return Ext.String.ellipsis(v, 15, false);}, font: '10px Arial',  rotate: { degrees: 90 } }}],
        
series: [
  {type: 'line', axis: 'left',    ///    highlight: true,         type: 'column'  
   style: {fill: 'rgb(69,109,159)'}, //highlightCfg: {fill: '#a2b5ca'},  // smooth: true, 
   highlight: {  size: 3,  radius: 3 },  markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },  //  type: 'circle'  cross
  // label: {display: 'over',field:['yesday_jihuo'],color: 'rgb(69,109,159)', orientation: 'vertical','text-anchor': 'right' },        
   listeners: {'itemmouseup': function(item) {
	                 var series = barChart.series.get(0), index = Ext.Array.indexOf(series.items, item), selectionModel = gridPanel.getSelectionModel();             
	                 selectedStoreItem = item.storeItem;  selectionModel.select(index); }  },
   xField: 'city_name', 
   yField: ['month_jihuo'],
   title: ['激活量'],
   tips: {trackMouse: true,  width: 90,height: 20,
       renderer: function(storeItem, item) {this.setTitle(storeItem.get('city_name') + ' : ' + storeItem.get('month_jihuo'));
       }} },
  
  {type: 'line', axis: 'left',    ///    highlight: true,         type: 'column'  
	   style: {fill: '#CD0000'},  //highlightCfg: {fill: '#a2b5ca'},  // smooth: true, 
	   highlight: {  size: 3,  radius: 3 },  markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },  //  type: 'circle'  cross
	   //label: {display: 'over',field:['yesday_stop'],color: '#CD0000', orientation: 'vertical','text-anchor': 'right' },        
	   listeners: {'itemmouseup': function(item) {
		                 var series = barChart.series.get(0), index = Ext.Array.indexOf(series.items, item), selectionModel = gridPanel.getSelectionModel();             
		                 selectedStoreItem = item.storeItem;  selectionModel.select(index); }  },
	   xField: 'city_name', 
	   yField: ['month_stop'],
	   title: ['停机量'], 
	   tips: {trackMouse: true,  width: 90,height: 20,
           renderer: function(storeItem, item) {this.setTitle(storeItem.get('city_name') + ' : ' + storeItem.get('month_stop'));
           }} 
    }
  ]   
});
    
    
//disable highlighting by default.
//barChart.series.get(0).highlight = false;

//add listener to (re)select bar item after sorting or refreshing the dataset.
barChart.addListener('beforerefresh', (function() {       
var timer = false;     
return function() {            
clearTimeout(timer);           
if (selectedStoreItem) {
timer = setTimeout(function() {selectItem(selectedStoreItem);}, 100);
 }
};
})() );
    
    
/*
     * Here is where we create the Form
     */
    
var gridForm = Ext.create('Ext.form.Panel', {

title: '业务量统计',     
frame: true,
autoWidth:true,  
height: 720, //closable:true,
fieldDefaults: {labelAlign: 'left',msgTarget: 'side' },
layout: {type: 'vbox', align: 'stretch' },   
items: [{height: 300,layout: 'fit',margin: '0 0 0 0',items: [barChart] },          
  {layout: {type: 'hbox', align: 'stretch'},
  flex: 3,
  //border: false,
  bodyStyle: 'background-color: transparent',                   
  items: [ 
        { flex: 0.3,
	      layout: {type: 'vbox', align:'stretch'},                           
          title: 'Company Details',               
          items: [{ margin: '1',flex: 1,xtype: 'fieldset', title:'',                  
                   defaults: {width: 200, labelWidth: 60},                   
                   items: [
                       	{xtype: 'tbspacer',height: 30, width:90},
                        {fieldLabel: '统计月份',name: 'stat_time',id:'stat_time', value: month, format:'Y-m', xtype:'datefield' },
                       	{xtype: 'tbspacer',height: 20, width:90},
                    	{text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:120,align:'center',handler:function(){  querydata();  }} 
                       	]
                    }]
          },gridPanel]
        
}],
        
renderTo: bd
});



function querydata(){
	
	 var stat_time=Ext.util.Format.date(Ext.getCmp('stat_time').value,'Y-m');
	 
	 if(stat_time>month){
		     Ext.Msg.alert('提示','当前最大查询月份为'+month);
		     return;
	     }
	     
	 	
    //var ub ={sort:1,dir:2,query:3};
	//Ext.Msg.alert('提示','month--->'+gridForm.getForm().getValues(true)+'---->'+Ext.urlEncode(ub));


	if(gridForm.getForm().isValid()){
		
	ds.removeAll();
    
    ds.proxy.url='<%=path%>/statistics/SqlGenerateData.jspa?query=stat_time='+stat_time+'&stat_type=statUsers';
   
	ds.reload();
	
	}else{
	       Ext.Msg.alert('提示','输入项格式错误,请检查输入项格式！');     
        }
	
}

var gp = Ext.getCmp('company-form');



});
  
    </script>
	</head>

	<body>
		<div id="mydiv" ></div>
	</body>
</html>
