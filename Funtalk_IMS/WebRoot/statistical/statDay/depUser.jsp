<%@ page language="java"   import="java.util.*,java.text.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="ww" uri="webwork"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	
	Calendar calendar=Calendar.getInstance();
	
	calendar.add(Calendar.DAY_OF_YEAR, -1);
    String nowDay = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    
    calendar.add(Calendar.MONTH, -1);
    String before30Day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>用户发展量统计</title> 

<link rel="stylesheet" type="text/css" href="<%=path%>/ext-4.2.1/css/ext-theme-neptune-all.css" />
<script type="text/javascript" src="<%=path%>/ext-4.2.1/bootstrap.js"></script>
<script type="text/javascript" src="<%=path%>/ext-4.2.1/ext-lang-zh_CN.js"></script>

<script type="text/javascript">


Ext.require([
    'Ext.form.*',
    'Ext.data.*',
    'Ext.chart.*',
    'Ext.grid.Panel',
    'Ext.layout.container.Column'
]);

var nowDay="<%=nowDay%>";  
var before30Day="<%=before30Day%>";  

Ext.onReady(function(){



var bd = Ext.getBody(),
        form = false,
        rec = false,
        selectedStoreItem = false,
        
selectItem = function(storeItem) {        
   var name = storeItem.get('stat_date'),series = barChart.series.get(0),i,items,l;
   
       series.highlight = true;
       series.unHighlightItem();
       series.cleanHighlights();
            
for (i = 0, items = series.items, l = items.length; i < l; i++) {
                
if (name == items[i].storeItem.get('stat_date')) {
                    
        selectedStoreItem = items[i].storeItem;                   
        series.highlightItem(items[i]);
        break;
   }
 }           
series.highlight = false;        
};  


Ext.define('Task', {
    extend: 'Ext.data.Model',
    idProperty: 'stat_date',
    fields: [ {name: 'stat_date',  type: 'string'},
              {name: 'day_jihuo', type: 'int'},
              {name: 'day_stop', type: 'int'}
        ///   {name: 'due', type: 'date', dateFormat:'m/d/Y'}
    ]
});



var  query1='stat_time='+Ext.util.Format.date(nowDay,'Ymd')+'&stat_type=huanbi&stat_id=1';

var store1 = Ext.create( Ext.data.Store,{
    model:'Task',
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlDayUsersData.jspa',
        extraParams:{query:query1},
        reader: {
            type: 'json',
            totalProperty: 'totalCount',
            root: 'rows'
        }
    },
    autoLoad:true
    });
    
    

                
                                    
var gridPanel = new Ext.grid.Panel({
    
    title: 'Details',
    flex: 0.7, 
    split:true,
    autoheight:true, 
    autowidth:true,
    margins:'0 0 0 0',
    cmargins:'0 5 5 5',    
    store: store1,
    columns: [   { header: '日期',   width:150,align: 'center', dataIndex:'stat_date' },           
                 { header: '激活用户', width:150,align: 'center', dataIndex:'day_jihuo'},
                 { header: '停机用户', width:150,align: 'center', dataIndex:'day_stop' }],
                 
    listeners: {
        
    	selectionchange: function(model, records) {   // records.length 选择的行数      model.getSelection()=records   第一个参数：Ext.selection.Model    第二个参数：selected : Ext.data.Model[]
    		
    		var  stat_time=model.getSelection()[0].get('stat_date');
    		
    		///Ext.Msg.alert('提示','records.lenght---->'+model.getSelection()[0].get('stat_date'));		
    	    //records[0].fields.each(function(item,index,len){Ext.Msg.alert('提示','item---->'+item+'------index---->'+index+'-----len-->'+len); });	
    		///Ext.Msg.alert('提示','records[0]---->'+records[0].fields.getAt(1)+'------id---->'+records[0].getId());

  /*   		if (records[0]){
    			
                rec = records[0];		  
    		    selectItem(rec);
    		  } */
    		
    	 }
    }
    
});  

    
var barChart = Ext.create('Ext.chart.Chart', {       
flex: 1,
shadow: true,
animate: true,
//theme: 'Category5',
legend: { position: 'bottom'},
store: store1, 
axes: [{ type: 'Numeric', position: 'left', fields: ['day_jihuo','day_stop'],minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}}, 
                          label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
       {type: 'Category', position: 'bottom',fields: ['stat_date'],
                          label: { renderer: function(v) { return Ext.util.Format.substr(v,4,4);}, font: '10px Arial',  rotate: { degrees: 0 } }}],
        
series: [
  {type: 'line', axis: 'left',    ///    highlight: true,         type: 'column'  
   style: {fill: 'rgb(69,109,159)'}, //highlightCfg: {fill: '#a2b5ca'},  // smooth: true, 
   highlight: {  size: 3,  radius: 3 },  
   markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },  //  type: 'circle'  cross
  // label: {display: 'over',field:['yesday_jihuo'],color: 'rgb(69,109,159)', orientation: 'vertical','text-anchor': 'right' },        
   listeners: {'itemmouseup': function(item) {
	   
	                // var series = barChart.series.get(0), index = Ext.Array.indexOf(series.items, item), selectionModel = gridPanel.getSelectionModel();             
	                 selectedStoreItem = item.storeItem; /// selectionModel.select(index); 
	                 var stattype= Ext.getCmp('stattypehid').value;                 
	    		   //  Ext.Msg.alert('提示','item.storeItem.get(day_jihuo)---->'+item.storeItem.get('stat_date'));  //item.storeItem 为被选中的一行的 data.Model, data.Model可以根据get(fieldname)取单元格的数据	                 
	    	    	 
	    		     cityColumnChart.series.items[0].title='激活量';
	    		     cityStore.removeAll();  
	    	    	 cityStore.proxy.extraParams.query='stat_time='+item.storeItem.get('stat_date')+'&stat_type='+stattype+'&stat_id=2';
	    	    	 cityStore.load();
	    	    		
	    	    	 win.show();	
   }  },
   xField: 'stat_date', 
   yField: ['day_jihuo'],
   title: ['激活量'],
   tips: {trackMouse: true,  width: 90,height: 20,
       renderer: function(storeItem, item) {this.setTitle(Ext.util.Format.substr(storeItem.get('stat_date'),4,4) + ' : ' + storeItem.get('day_jihuo'));
       }} },
  
  {type: 'line', axis: 'left',    ///    highlight: true,         type: 'column'  
	   style: {fill: '#CD0000'},  //highlightCfg: {fill: '#a2b5ca'},  // smooth: true, 
	   highlight: {  size: 3,  radius: 3 },  markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },  //  type: 'circle'  cross
	   //label: {display: 'over',field:['yesday_stop'],color: '#CD0000', orientation: 'vertical','text-anchor': 'right' },        
	   listeners: {'itemmouseup': function(item) {
		   
		                 cityColumnChart.series.items[0].title='停机量';
		                 var stattype= Ext.getCmp('stattypehid').value;
		                 
		                /// Ext.Msg.alert('提示','item.storeItem.getLegendColor---->'+cityColumnChart.series.items[0].getLegendColor(0)); 
	    	    	     cityStore.removeAll();  
		    	    	 cityStore.proxy.extraParams.query='stat_time='+item.storeItem.get('stat_date')+'&stat_type='+stattype+'&stat_id=3';
		    	    	 cityStore.load();
		    	    		
		    	    	 win.show();	
	   }  },
	   xField: 'stat_date', 
	   yField: ['day_stop'],
	   title: ['停机量'], 
	   tips: {trackMouse: true,  width: 90,height: 20,
           renderer: function(storeItem, item) {this.setTitle(Ext.util.Format.substr(storeItem.get('stat_date'),4,4) + ' : ' + storeItem.get('day_stop'));
           }} 
    }
       
  ]   
});
    


//add listener to (re)select bar item after sorting or refreshing the dataset.
/* barChart.addListener('beforerefresh', (function() {       
var timer = false;     
return function() {            
clearTimeout(timer);           
if (selectedStoreItem) {
timer = setTimeout(function() {selectItem(selectedStoreItem);}, 100);
 }
};
})() ); */
    
    

var gridForm = Ext.create('Ext.form.Panel', {

title: '用户发展量走势',     
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
                   items: [{xtype: 'tbspacer',height: 30, width:90},
                           {xtype:'radiogroup',id: 'stattype',fieldLabel:'统计方式',
                                   items:[{name:'stat_type',boxLabel:'环比',inputValue: 'huanbi',checked:true}, 
                                          {name:'stat_type',boxLabel:'同比',inputValue: 'tongbi' }
                            ]},
                        {xtype: 'hiddenfield', id: 'stattypehid', value: 'huanbi'}, 
                       	{xtype: 'tbspacer',height: 30, width:90},
                        {fieldLabel: '统计日期',name: 'stat_time',id:'stat_time',value: nowDay, format:'Y-m-d', xtype:'datefield' },
                       	{xtype: 'tbspacer',height: 20, width:90},
                    	{text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:120,align:'center',handler:function(){  querydata();  }} 
                       	]
                    }]
          },gridPanel]
        
}],
        
renderTo: bd
});



function querydata(){
	
	 var stat_time=Ext.util.Format.date(Ext.getCmp('stat_time').value,'Y-m-d');
	     if(stat_time>nowDay){
		     Ext.Msg.alert('提示','当前最大查询日期为'+nowDay);
		     return;
	     }
	     
	     if(stat_time<before30Day){
		     Ext.Msg.alert('提示','当前最小查询日期为'+before30Day);
		     return;
	     }
	
    var stattype=Ext.getCmp('stattype').getChecked()[0].inputValue;
    Ext.getCmp('stattypehid').value=stattype;

	if(gridForm.getForm().isValid()){
	//query=gridForm.getForm().getValues(true);
    // Ext.Msg.alert('提示','month--->'+month);
	store1.removeAll();  
	store1.proxy.extraParams.query='stat_time='+Ext.util.Format.date(Ext.getCmp('stat_time').value,'Ymd')+'&stat_type='+stattype+'&stat_id=1';
	store1.load();
	
	}else{
	       Ext.Msg.alert('提示','输入项格式错误,请检查输入项格式！');     
        }
	
}



var cityStore= Ext.create( Ext.data.Store,{
    fields: [ {name: 'city_name', type: 'string'},  
              {name: 'day_nums', type: 'int'}],
    sorters: { property: 'day_nums', direction: 'ASC' },  //展示之前排序
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlDayUsersData.jspa',
        //extraParams:{query:cityQuery},
        reader: {
            type: 'json',
            totalProperty: 'totalCount',
            root: 'rows'
        }
    },
    autoLoad:false
    });


var cityColumnChart = Ext.create('Ext.chart.Chart', {
    style: 'background:#fff',
    animate: true,
    shadow: true,
    theme:'Category1',  // 没有解决设置柱子的颜色，只能使用主题
    legend: { position: 'bottom',labelColor:'#000'}, //  legend 和  series的title 共同完成图例的配置
    store: cityStore,
    axes: [{
        type: 'Numeric',
        position: 'left',
        fields: ['day_nums'],
        label: {  renderer: Ext.util.Format.numberRenderer('0,0')   },
        grid: true,
        minimum: 0
    }, {
        type: 'Category',
        position: 'bottom',
        fields: ['city_name'],
        label: {rotate: { degrees: 90 }}
    }],
    series: [{
        type: 'column',
        axis: 'left',
       // style: {  fill: '#CD0000'   },
        highlight: true,
        tips: {
          trackMouse: true,
          width: 100,
          height: 28,
          renderer: function(storeItem, item) {   this.setTitle(storeItem.get('city_name') + ': ' + storeItem.get('day_nums')); 
          
       /*switch(item.yField){
    	case 'creat' : this.setTitle(storeItem.get('name') + ': ' + storeItem.get('creat')+ '% '); break;
    	case 'use' : this.setTitle(storeItem.get('name') + ': ' + storeItem.get('use')+ '% '); break;
    	case 'qua' : this.setTitle(storeItem.get('name') + ': ' + storeItem.get('qua')+ '% '); break; 
    	} */
        }     
       },
        xField: 'city_name',
        yField: ['day_nums']
    }]
});
    
    
var win = Ext.create('Ext.window.Window', {
    width:1000,
    height: 350,
    minWidth: 400,
    minHeight: 200,
    maximizable: true,
    constrain:true, // 确保拖动窗口时，不超出浏览器边界；
    closable:true,
    closeAction: "hide",  // 在关闭Window的时候，通过配置项closeAction可以控制按钮是销毁（destroy）还是隐藏（hide），默认情况下为销毁, 此项必须配置，否则，再次无法显示此窗口，因为已经销毁
    title: 'city data',
    modal:true, // 模式窗口，弹出窗口后屏蔽掉其他组建
    plain:true,// 将被屏蔽的窗口变为半透明状态
    layout: 'fit',
    items: cityColumnChart
});




});
  
    </script>
	</head>

	<body>
		<div id="mydiv" ></div>
	</body>
</html>
