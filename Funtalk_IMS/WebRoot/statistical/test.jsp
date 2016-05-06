<%@ page language="java"   import="java.util.*,java.text.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="ww" uri="webwork"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	
	Calendar calendar=Calendar.getInstance();
	calendar.add(Calendar.MONTH, -3);
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

	//  Ext.Msg.alert('提示','records------length---->'+store1.isGrouped());
	//Ext.Msg.alert('提示','records------length---->'+store1.getGroups()[0].name);
	//Ext.Msg.alert('提示','records------length---->'+store1.getGroups()[0].children.length);
///Ext.Msg.alert('提示','records------length---->'+data2.pop());  
	//Ext.Msg.alert('提示','records------length---->'+store1.group('stat_date').getGroups()[0].name);
	
	
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
},

//updates a record modified via the form
        
updateRecord = function(rec) {
            
var name,series,i,l,items,json = [

 {'Name': '昨日激活','Data': rec.get('yesday_jihuo') }, 
 {'Name': '昨日停机', 'Data': rec.get('yesday_stop') },
 {'Name': '本月激活','Data': rec.get('month_jihuo')},
 {'Name': '本月停机', 'Data': rec.get('month_stop')} ];
 
 chs.loadData(json);
 selectItem(rec);   
 
},
        

createListeners = function() { return {
                
buffer: 200,
change: function(field, newValue, oldValue, listener) {
                  
if (rec && form) {
if (newValue > field.maxValue) {
    field.setValue(field.maxValue);
}else{
    form.updateRecord(rec);
    updateRecord(rec);
   }
  }
 }
};  
};


//create data store to be shared among the grid and bar series.
   
var ds = Ext.create('Ext.data.ArrayStore',{
	     proxy:new Ext.data.HttpProxy({url:'<%=path%>/statistics/SqlGenerateData.jspa'}),
         fields: [ {name: 'prov_name'},{name: 'city_name'}, {name: 'yesday_jihuo',  type: 'float'},{name: 'yesday_stop',  type: 'float'},{name: 'month_jihuo',  type: 'float'},{name: 'month_stop',  type: 'float'} ],
         autoLoad:true  });
    
    

//create radar dataset model.
    
var chs = Ext.create('Ext.data.JsonStore', {
        
fields: ['Name', 'Data'],        
data: [{'Name': '昨日激活','Data': 3000}, {'Name': '昨日停机', 'Data': 3000}, 
       {'Name': '当月激活','Data': 3000}, {'Name': '当月停机','Data': 3000}]  
});
    
    

//Radar chart will render information for a selected company in the  
//list. Selection can also be done via clicking on the bars in the series.
    

var radarChart = Ext.create('Ext.chart.Chart', {
        
margin: '0 0 0 0',     
insetPadding: 20,      
flex: 1.2,      
animate: true,      
store: chs,      
axes: [{ steps: 5, type: 'Radial', position: 'radial', maximum: 3000 }],
series: [{type: 'radar',xField: 'Name',yField: 'Data',showInLegend: false,showMarkers: true,
          markerConfig: {  radius: 4, size: 4,fill: 'rgb(69,109,159)'},           
          style: { fill: 'rgb(194,214,240)',  opacity: 0.5, 'stroke-width': 0.5}
   }]   
});
    
    

//create a grid that will list the dataset items.
    

var gridPanel = Ext.create('Ext.grid.Panel', {   
id: 'company-form',       
flex: 0.60,       
store: ds,      
title:'Company Data',
collapsible : true,  
animCollapse : true, 
columns: [ {id :'company', text: '省份',width:75, sortable : true, dataIndex: 'prov_name'},        
           {text: '地市', width:75, sortable :  true,align: 'right',dataIndex: 'city_name'},            
           {text:'昨日激活',width:75,sortable : true,align: 'right',dataIndex: 'yesday_jihuo'},
           {text:'昨日停机',width:75,sortable : true,align: 'right',dataIndex: 'yesday_stop'},
           {text:'本月激活',width:75,sortable : true,align: 'right',dataIndex: 'month_jihuo'},
           {text:'本月停机',width:75,sortable : true,align: 'right',dataIndex: 'month_stop'}   ],

listeners: {
            
selectionchange: function(model, records) {
                
var json, name, i, l, items, series, fields;

if (records[0]) {
	
   rec = records[0];     
   
if (!form){
  form = this.up('form').getForm();
  fields = form.getFields();
  fields.each(function(field){if (field.name != 'city_name') { field.setDisabled(false); }  });
}else{                     
  fields = form.getFields();
}
                    
                    
// prevent change events from firing               
fields.each(function(field){ field.suspendEvents();});
 
form.loadRecord(rec);
updateRecord(rec);
fields.each(function(field){  field.resumeEvents(); });
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
axes: [{ type: 'Numeric', position: 'left', fields: ['yesday_jihuo','yesday_stop'],minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}}, 
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
   yField: ['yesday_jihuo'],
   title: ['昨日激活量'],
   tips: {

	   trackMouse: true,
	   width: 400,
       height:300,      
       layout: 'fit',
       items: {
           xtype: 'container',
           layout: 'hbox',
           items: [lineDetailChart1]
       },
       renderer: function(klass, item) {
    	   
           var arrLineShow1=[]; 
           var storeItem = item.storeItem;    
           this.setTitle("Information for ");
                                      
            	 arrLineShow1 = [ {stat_name: 'data1', stat_value: 20 },{ stat_name: 'data2', stat_value: 10 }];
           
           lineDetailStore1.loadData(arrLineShow1); 
           
           lineDetailChart1.setSize(280, 230);
          //// Ext.Msg.alert('提示','cycleid.length---->'+arrLineShow1.length+'-----------ele---->'+lineDetailStore1.getAt(0).get('stat_name'));
         } 
	                 
   
   } },
  
  {type: 'line', axis: 'left',    ///    highlight: true,         type: 'column'  
	   style: {fill: '#CD0000'},  //highlightCfg: {fill: '#a2b5ca'},  // smooth: true, 
	   highlight: {  size: 3,  radius: 3 },  markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },  //  type: 'circle'  cross
	   //label: {display: 'over',field:['yesday_stop'],color: '#CD0000', orientation: 'vertical','text-anchor': 'right' },        
	   listeners: {'itemmouseup': function(item) {
		                 var series = barChart.series.get(0), index = Ext.Array.indexOf(series.items, item), selectionModel = gridPanel.getSelectionModel();             
		                 selectedStoreItem = item.storeItem;  selectionModel.select(index); }  },
	   xField: 'city_name', 
	   yField: ['yesday_stop'],
	   title: ['昨日停机'], 
	   tips: {trackMouse: true,  width: 90,height: 20,
           renderer: function(storeItem, item) {this.setTitle(storeItem.get('city_name') + ' : ' + storeItem.get('yesday_stop'));
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
     
     
pieModel = [ {stat_name: 'data1', stat_value: 10 },{ stat_name: 'data2', stat_value: 10 }];
var lineDetailStore1= Ext.create( Ext.data.JsonStore,{
         fields: ['stat_name','stat_value'],
         data:pieModel
         });

 var lineDetailChart1 = Ext.create('Ext.chart.Chart', {
         width: 150,
         height:200,
         animate: true,
         store: lineDetailStore1,
         shadow: true,
         insetPadding:5,
         theme: 'Category6',
         series: [{
             type: 'pie',
             field: 'stat_value',
             showInLegend: false,
             label: {
                 field: 'stat_name',
                 display: 'rotate',
                 contrast: true,
                 font: '9px Arial'
             }
         }]
     });
    
var gridForm = Ext.create('Ext.form.Panel', {
title: 'Company data',     
frame: true,
bodyPadding:2,  
autoWidth:true,  
height: 720, //closable:true,
fieldDefaults: {labelAlign: 'left',msgTarget: 'side' },
layout: {type: 'vbox', align: 'stretch' },   
items: [{height: 300,layout: 'fit',margin: '0 0 0 0',items: [barChart] },          
  {layout: {type: 'hbox', align: 'stretch'},
  flex: 3,
  border: false,bodyStyle: 'background-color: transparent',                   
  items: [gridPanel, 
        { flex: 0.4,
	      layout: {type: 'vbox', align:'stretch'},              
          margin: '0 0 0 0',               
          title: 'Company Details',               
          items: [{ margin: '5', xtype: 'fieldset',flex: 1, title:'Company details',                  
                   defaults: {width: 240, labelWidth: 90, disabled: true},                   
                   defaultType: 'numberfield',                   
                   items: [{fieldLabel: '地市',   name: 'city_name',   xtype:'textfield'},
                           {fieldLabel: '昨日激活',name: 'yesday_jihuo', xtype:'textfield'},
                           {fieldLabel: '昨日停机',name: 'yesday_stop',  xtype:'textfield'},
                           {fieldLabel: '本月激活',name: 'month_jihuo',  xtype:'textfield'},
                           {fieldLabel: '本月停机',name: 'month_stop',   xtype:'textfield'}
                          ]
                    }, lineDetailChart1]
          }]
        
}],
        
renderTo: bd
});

var gp = Ext.getCmp('company-form');



});
  
    </script>
	</head>

	<body>
		<div id="mydiv" ></div>
	</body>
</html>
