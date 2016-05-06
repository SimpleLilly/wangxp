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
    String preMonth = new SimpleDateFormat("yyyy-MM").format(calendar.getTime());

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>产品统计</title> 

<link rel="stylesheet" type="text/css" href="<%=path%>/ext-4.2.1/css/ext-theme-neptune-all.css" />
<script type="text/javascript" src="<%=path%>/ext-4.2.1/bootstrap.js"></script>
<script type="text/javascript" src="<%=path%>/ext-4.2.1/ext-lang-zh_CN.js"></script>

<style type="text/css">
.my-row .x-grid-cell {  font-weight:bold;color:red;  }
</style>

<script type="text/javascript">


Ext.require([
    'Ext.form.*',
    'Ext.data.*',
    'Ext.chart.*',
    'Ext.grid.Panel',
    'Ext.layout.container.Fit',
    'Ext.layout.container.Column'
]);


 
var preMonth="<%=preMonth%>";

Ext.onReady(function(){


var bd = Ext.getBody(),
        form = false,
        rec = false,
        selectedStoreItem = false,
        barChart;

var searchMonth=preMonth;

var minCycle='';
var prodIds=[];
var cycleIds=[];
var hashMap = new Ext.util.HashMap();
var prodMap = new Ext.util.HashMap();

    hashMap.add('mon_jihuo','激活用户');
    hashMap.add('mon_tingji','停机用户');
    hashMap.add('mon_users','出账用户');
    hashMap.add('mon_billtwo','出账总额1');  
    hashMap.add('mon_arpu','ARPU值');
    hashMap.add('mon_yuyin','语音');
    hashMap.add('mon_liuliang','流量');
    hashMap.add('mon_duanxin','短信');
    hashMap.add('mon_laixian','来显');
    hashMap.add('mon_dixiao','低消');
    hashMap.add('mon_xzxianjin','现金销帐');
    hashMap.add('mon_xzzengfee','赠费销帐');
    hashMap.add('mon_owe','欠费');
    hashMap.add('mon_settle','结算');

    
 // mon_billone：行业卡按40%统计的账单；   mon_billtwo：未处理行业卡的账单；
 
function initPieStore(records, options, success){   ///  records : Ext.data.Record[]   *  options: Options object from the load call   * success: Boolean
	
	var data=[];
	var prodCycDatas=[];
    var mySeries = [];    
    var hashMap = new Ext.util.HashMap();  
    var statMonth=Ext.util.Format.date(searchMonth,'Ym');
    
    var gridArr=[];
    
    
    minCycle=store1.min('cycle_id');
    
    prodIds.splice(0,prodIds.length);
    cycleIds.splice(0,cycleIds.length);
    
    prodIds.push('cycleId');

    
    store1.each(function(record){ 
    	
    	
        if(prodIds.lastIndexOf(record.get('stat_id'))==-1){
        	
        	prodIds.push(record.get('stat_id'));
        	
        	if(record.get('stat_id')=='all')
        	prodMap.add(record.get('stat_id'),'汇总');
        	else
        		prodMap.add(record.get('stat_id'),record.get('stat_name'));       	
         }
        
        if(cycleIds.lastIndexOf(record.get('cycle_id'))==-1){  cycleIds.push(record.get('cycle_id'));  }
        
        /// 为gridStore取数据
        if(record.get('cycle_id')==statMonth){ 	gridArr.push(record.copy());  }  /// 必须使用copy才能确保grid的数据是真实的，利润如果为负值，则图表展示为0，grid则为负值；
        
        
        if(record.get('mon_settle')==0){
        	
        	record.set('mon_profit1',0);
        	record.set('mon_profit2',0);
        	record.set('mon_rate1','0%');
        	record.set('mon_rate2','0%');  	
        }    
        
       if(record.get('mon_profit1')<0){ 	record.set('mon_profit1',0);  }
       if(record.get('mon_profit2')<0){ 	record.set('mon_profit2',0);  }
       
    });
     
     for(m=0;m<cycleIds.length;m++){
    	 
    	 prodCycDatas.push({cycleId:cycleIds[m].toString()});	 
  	 
     }
     
   // 通过修改原先的store3来初始化数据是行不通的，改为如下新建对象的方式问题解决；    store3.fields=prodIds;  store3.loadData(prodCycDatas); 
   var  store3 = Ext.create('Ext.data.JsonStore', { fields: prodIds, data:prodCycDatas });

   ///为store3填充 mon_billtwo 数据；
    var  arrProd; 
    store1.group('stat_id');
  
    for(j=0;j<store1.getGroups().length;j++){
    	    	 
    	     cycleIds.forEach(function(eleCycle, idx, arr){
    		 
    		     arrProd =store1.getGroups()[j].children.filter(function(element){ return (element.get('cycle_id')==eleCycle);  });	 
    		 					      	     
    	     if(arrProd.length>0){
    	    	 store3.findRecord('cycleId',eleCycle).set(store1.getGroups()[j].name, arrProd[0].get('mon_billtwo'));
    	    	     	
    	     }else{
    	    	 store3.findRecord('cycleId',eleCycle).set(store1.getGroups()[j].name,0);
    	     }
    		    
    	 });
    	     
     store3.sort('cycleId', 'ASC');  
  	 
     mySeries.push(
     {type: 'line', 
      axis: 'left',
      highlight: {  size: 3,  radius: 3 },  
      markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },
      xField: 'cycleId', 
      yField: store1.getGroups()[j].name,
      title: prodMap.get(store1.getGroups()[j].name),
      tips: {trackMouse: true,  width: 130,height: 20,
    	       renderer: function(storeItem, item) {
    	       
     	    	var n=1;
    	        var arrLine=[];
    	        var stat1=0,mudValue=0;
    	        var str1=storeItem.get('cycleId') + ':' + item.value[1];
    	        var nowMonth=storeItem.get('cycleId');
       		    var yearStr=Ext.util.Format.substr(nowMonth,0,4);
       		    var monthStr=Ext.util.Format.substr(nowMonth,4,2);
    	        var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(monthStr+'/01/'+yearStr), Ext.Date.MONTH, -1),'Ym');
      
    	        if(store3.findRecord('cycleId',preMonth) != null ){
    	        	
    	         n=3;
    	         stat1=store3.findRecord('cycleId',preMonth).get(item.series.yField);
    	         
    	         mudValue=(item.value[1]-stat1);         
    	         str1+=('<br>环比增长:'+mudValue+'<br>环比增长率:'+transToPerc(item.value[1],stat1));	
    	        
    	        }
                 	        
    	        this.setTitle(str1);
    	        this.setHeight(n*20);     
    	     }}, 
	   listeners: {'itemmouseup': function(item) {
	    	 
 		   	 var stattype='huanbi'; 	
   		     var yearStr=Ext.util.Format.substr(item.storeItem.get('cycleId'),0,4);
   		     var monthStr=Ext.util.Format.substr(item.storeItem.get('cycleId'),4,2);
	         var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(yearStr,monthStr), Ext.Date.MONTH, -2),'Ym');  //  要取前一个月，但要-2才能实现，为什么不是-1呢？
   		   	   	 
   		   	 cityColumnChart.series.items[0].title=[preMonth,item.storeItem.get('cycleId')];
   		     cityStore.removeAll(); 
   	    	 cityStore.proxy.extraParams.query='stat_time='+item.storeItem.get('cycleId')+'&stat_type='+stattype+'&stat_id='+item.series.yField+'&stat_id1=mon_billtwo';
   	    	 cityStore.load(); 		
   	    	 
   	    	 win.setTitle(item.series.title);    //win.title 不能变换标题，win.setTtile() 可以，为什么？
   	    	 win.show();
 	    	 
            } } 
         }
       ); 
     }
    
     barChart = Ext.create('Ext.chart.Chart', {       
    	 flex: 1,
    	 shadow: true,
    	 animate: true,
    	 //theme: 'Category5',
    	 legend: { position: 'bottom'},
    	 store: store3, 
    	 axes: [{ type: 'Numeric', position: 'left', fields: prodIds,minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}}, 
    	                           label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
    	        {type: 'Category', position: 'bottom',fields: ['cycleId'], font: '10px Arial',  rotate: { degrees: 0 } }],
    	         
    	 series: mySeries  
    	 });
                          
        Ext.getCmp('barChart').add(barChart);//将chart添加到panel中 测了好久，最后通过设置ID解决问题
             
        store1.clearGrouping(); 
        ///store1.sort('product_id', 'DESC');
        
        Ext.getCmp('mon_billtwo').setValue(true);
        
        
        if(gridArr.length>0){
        	
            gridStore.loadData(gridArr);    
            gridStore.getAt(gridStore.find('stat_id','all')).set('stat_name','汇总');
            gridPanel.getStore().commitChanges(); /// 去掉grid中左上角的红色修改 标志
            gridPanel.getSelectionModel().select(0,true);
        	
        }else{
        	Ext.Msg.alert('提示',preMonth+'账期无数据！');
        	return;       	
        }
        
        Ext.MessageBox.hide();

}

Ext.define('Task', {
    extend: 'Ext.data.Model',
   /// idProperty: 'product_id',
    fields: [ {name: 'cycle_id', type: 'string'},
              {name: 'stat_type', type: 'string'},
              {name: 'stat_id', type: 'string'},
              {name: 'stat_name', type: 'string'},           
              {name: 'mon_billone', type:'int'},
              {name: 'mon_billtwo', type:'int'},
              {name: 'mon_yuyin', type:'int'},
              {name: 'mon_liuliang', type:'int'},
              {name: 'mon_duanxin', type:'int'},
              {name: 'mon_yzyuyin', type:'int'},
              {name: 'mon_yzliuliang', type:'int'},
              {name: 'mon_laixian', type: 'int'},
              {name: 'mon_dixiao', type: 'int'},
              {name: 'mon_xzxianjin', type:'int'},
              {name: 'mon_xzzengfee', type:'int'},
              {name: 'mon_owe', type:'int'},
              {name: 'mon_settle', type: 'int'},
              {name: 'mon_users', type: 'int'},
              {name: 'mon_jihuo', type: 'int'},
              {name: 'mon_tingji', type: 'int'},
              {name: 'mon_arpu', type: 'number'},
              {name: 'mon_profit1', type: 'int'},
              {name: 'mon_profit2', type: 'int'},
              {name: 'mon_rate1', type: 'string'},
              {name: 'mon_rate2', type: 'string'}
    ]
});



var  query1='stat_time='+Ext.util.Format.date(preMonth,'Ym')+'&stat_type=huanbi&stat_id=1';

var store1 = Ext.create( Ext.data.Store,{
    model:'Task',
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlMonProUserBillData.jspa',
        extraParams:{query:query1},
        reader: {
            type: 'json',
            totalProperty: 'totalCount',
            root: 'rows'
        }
    },
    autoLoad:{ callback: initPieStore },
    listeners:{  beforeload:function(){	 Ext.MessageBox.wait('正在加载数据......','Please wait');   }    }

    });
    
    
function transToPerc(v1,v2) {
	
	if(v2==0){		
		data=v1*100;		
	}else{	
		 data=Ext.util.Format.number((v1-v2)*100/v2,'0.0');
	}
		
    return data + '%';
}
    

var gridStore= Ext.create( Ext.data.JsonStore,{
    fields: ['cycle_id','stat_type','stat_id','stat_name','mon_billone','mon_billtwo','mon_yuyin','mon_liuliang','mon_duanxin',
             'mon_yzyuyin','mon_yzliuliang','mon_laixian','mon_dixiao','mon_xzxianjin','mon_xzzengfee','mon_owe','mon_settle','mon_users','mon_jihuo','mon_tingji','mon_arpu','mon_profit1','mon_profit2','mon_rate1','mon_rate2']
    });
                                    
var gridPanel = new Ext.grid.Panel({
    
    title: '产品明细【'+preMonth+'】',
    split:true,
    autoheight:true, 
    autowidth:true,
    autoScroll:true,
    flex: 0.7,  //columnWidth:1,
    viewConfig : {
        ///rowOverCls : 'my-row-over',//鼠标移过的行样式
        ///selectedRowClass : "my-row-selected",//选中行的样式
        getRowClass : function(record,rowIndex,rowParams,store){  //指定行的样式
          if(record.get('stat_id')=='all'){
           return "my-row";
          }
         }
       },
    store: gridStore,
    columnLines: true,
    columns: [   { header: '产品', width:100,align: 'center', dataIndex:'stat_name',locked:true },                   
                 { header: '激活用户', width:80,align: 'right', dataIndex:'mon_jihuo'},
                 { header: '停机用户', width:80,align: 'right', dataIndex:'mon_tingji'},
                 { header: '语音', width:80,align: 'right', dataIndex:'mon_yuyin'},  
                 { header: '流量', width:80,align: 'right',dataIndex:'mon_liuliang'},     
                 { header: '短信', width:80,align: 'right', dataIndex:'mon_duanxin'},
                 { header: '来显', width:80,align: 'right', dataIndex:'mon_laixian'},
                 { header: '低消', width:80,align: 'right', dataIndex:'mon_dixiao'},
                 { header: '账单总额', width:80,align: 'right', dataIndex:'mon_billtwo'}, /// 100%统计行业卡
                 { header: '出账用户', width:80,align: 'right', dataIndex:'mon_users'},
                 { header: 'ARPU值', width:80,align: 'right', dataIndex:'mon_arpu'},
                 { header: '账单总额A', width:80,align: 'right', dataIndex:'mon_billone'},///40%统计行业卡
                 { header: '现金销帐B', width:80,align: 'right', dataIndex:'mon_xzxianjin'},  
                 { header: '赠款销帐', width:80,align: 'right', dataIndex:'mon_xzzengfee'},
                 { header: '欠费', width:80,align: 'right', dataIndex:'mon_owe'},
                 { header: '结算金额C', width:80,align: 'right', dataIndex:'mon_settle'}, 
                 { header: '利润A(A-C)', width:115,align: 'right', dataIndex:'mon_profit1'}, 
                 { header: '毛润B(B-C)', width:115,align: 'right', dataIndex:'mon_profit2'}, 
                 { header: '利润率A', width:105,align:'right', dataIndex:'mon_rate1' },
                 { header: '利润率B', width:105,align:'right', dataIndex:'mon_rate2' }
                  ]
});

    
    

var gridForm = Ext.create('Ext.form.Panel', {

title: '账期指标走势【账单总额A】',     
frame: true,
autoWidth:true,
height: 720, //closable:true,
fieldDefaults: {labelAlign: 'left',msgTarget: 'side' },
layout: {type: 'vbox', align: 'stretch' },   
items: [{height: 350, id:'barChart', layout: 'fit',margin: '0 0 0 0'},          
    {flex: 3,
	layout:{type: 'hbox', align: 'stretch'},
    bodyStyle: 'background-color: transparent',
    items: [ 
        { flex: 0.3,
	      layout:'form',                           
          title: '查询条件',               
          items: [{xtype: 'tbspacer',height: 20},
                  {  height: 30,
        	         xtype: 'fieldset',
        	         layout:'column',// {type: 'hbox', align:'left'},                                
                     //defaults: {width: 200, labelWidth: 60},
                     border:0,
                     items: [
    	                      {fieldLabel: '统计月份',labelWidth: 60,width:180,name: 'stat_time',id:'stat_time',value: preMonth, format:'Y-m', xtype:'datefield'},
    	                      {xtype: 'tbspacer',height: 30,width:15},
    	                      {text: '查询',xtype:'button', id:'qrybot',width:90,handler:function(){  querydata();  }},
    	                      {xtype: 'tbspacer',height: 30,columnWidth:1}  ///自动扩展部分
                    ]},             
                     {  
                    	   xtype: 'fieldset',
                           title: '展示指标',
                           layout: 'form',
                           height: 220, 
                           defaults: {width: 200,bodyPadding:10},
                           collapsible: true,
                           items: [ {  xtype: 'radiogroup',
                                       //fieldLabel: '123',
                                       layout:{type:'table',columns:4},
                                       defaultType: 'container',
                                       items: [
                                               {xtype: 'component',width:50, html: '营业'},
                                               {xtype: 'radiofield',boxLabel: '激活', name: 'rb-horiz-1', inputValue:'mon_jihuo'},
                                               {xtype: 'radiofield',boxLabel: '停机', name: 'rb-horiz-1', inputValue:'mon_tingji'},
                                               {xtype: 'component',width:50, html: ''},
                                               {xtype: 'component',width:50, html: '账务'},
                                               {xtype: 'radiofield',boxLabel: '账单总额A', name: 'rb-horiz-1', id:'mon_billtwo', inputValue:'mon_billtwo',checked: true},
                                               {xtype: 'radiofield',boxLabel: '出账用户', name: 'rb-horiz-1', inputValue:'mon_users'},
                                               {xtype: 'radiofield',boxLabel: 'ARPU值',  name: 'rb-horiz-1', inputValue:'mon_arpu'},
                                               {xtype: 'component',width:50, html: ''},
                                               {xtype: 'radiofield',boxLabel: '现金销帐', name: 'rb-horiz-1', inputValue:'mon_xzxianjin'},
                                               {xtype: 'radiofield',boxLabel: '赠款销帐', name: 'rb-horiz-1', inputValue:'mon_xzzengfee'},
                                               {xtype: 'radiofield',boxLabel: '欠费',    name: 'rb-horiz-1', inputValue:'mon_owe'},
                                               {xtype: 'component',width:50, html: ''},
                                               {xtype: 'radiofield',boxLabel: '结算',    name: 'rb-horiz-1', inputValue:'mon_settle'},
                                               {xtype: 'radiofield',boxLabel: '利润A',   name: 'rb-horiz-1', inputValue:'mon_profit1'},
                                               {xtype: 'radiofield',boxLabel: '利润B',   name: 'rb-horiz-1', inputValue:'mon_profit2'},
                                               {xtype: 'component',width:50, html: '账单'},
                                               {xtype: 'radiofield',boxLabel: '语音', name: 'rb-horiz-1', inputValue:'mon_yuyin'},
                                               {xtype: 'radiofield',boxLabel: '流量', name: 'rb-horiz-1', inputValue:'mon_liuliang'},
                                               {xtype: 'radiofield',boxLabel: '短信', name: 'rb-horiz-1', inputValue:'mon_duanxin'},
                                               {xtype: 'component',width:50, html: ''},
                                               {xtype: 'radiofield',boxLabel: '来显', name: 'rb-horiz-1', inputValue:'mon_laixian'},
                                               {xtype: 'radiofield',boxLabel: '低消', name: 'rb-horiz-1', inputValue:'mon_dixiao'}
                                          ],
                                          listeners : {'change' :raidoAction}
                                       }]
                                   }]
                       },gridPanel]
          }],
        
renderTo: bd
});



function raidoAction(view, newValue, oldValue){
   	     
	     Ext.getCmp('barChart').remove(barChart);
	     
	     ///  var radioValue = gridForm.getForm().getValues()['rb-horiz-1'];  /// 获取radiogroup的值
	     var radioValue = newValue['rb-horiz-1']; 
         var radioName='';
      
 	     for(i=0;i<view.items.items.length;i++){
	    	
	    	if(view.items.items[i].checked==true){
	    		radioName=view.items.items[i].boxLabel;	
	    	}	    	    
	     }
	     
	     var  mySeries=[];
	     var  prodCycDatas=[];
     
	     for(m=0;m<cycleIds.length;m++){	 prodCycDatas.push({cycleId:cycleIds[m].toString()});	     }

	     var  store3 = Ext.create('Ext.data.JsonStore', { fields: prodIds, data:prodCycDatas }); 

	     var  arrProd;   
	     store1.group('stat_id');
	   
	     for(j=0;j<store1.getGroups().length;j++){
	     	    	 
	     	     cycleIds.forEach(function(eleCycle, idx, arr){
	     		 
	     		     arrProd =store1.getGroups()[j].children.filter(function(element){  return (element.get('cycle_id')==eleCycle);   });	 
	     		 					      	     
	     	     if(arrProd.length>0){
	     	    	 store3.findRecord('cycleId',eleCycle).set(store1.getGroups()[j].name, arrProd[0].get(radioValue));
	     	    	     	
	     	     }else{
	     	    	 store3.findRecord('cycleId',eleCycle).set(store1.getGroups()[j].name,0);
	     	     }     		    
	     	 });
	     	     
	      store3.sort('cycleId', 'ASC');  
	   	 
	      mySeries.push(
	      {type: 'line', 
	       axis: 'left',
	       highlight: {  size: 3,  radius: 3 },  
	       markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },
	       xField: 'cycleId', 
	       yField: store1.getGroups()[j].name,
	       title: prodMap.get(store1.getGroups()[j].name),
	       tips: {trackMouse: true,  width: 130,height: 20,
	     	       renderer: function(storeItem, item) {
	     	    	   
	        	    	var n=1;
	        	        var arrLine=[];
	        	        var stat1=0,mudValue=0;
	        	        var str1=storeItem.get('cycleId') + ':' + item.value[1];
	        	        var nowMonth=storeItem.get('cycleId');
	           		    var yearStr=Ext.util.Format.substr(nowMonth,0,4);
	           		    var monthStr=Ext.util.Format.substr(nowMonth,4,2);
	        	        var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(monthStr+'/01/'+yearStr), Ext.Date.MONTH, -1),'Ym');
	          
	        	        if(store3.findRecord('cycleId',preMonth) != null ){
	        	        	
	        	         n=3;
	        	         stat1=store3.findRecord('cycleId',preMonth).get(item.series.yField);
	        	         
	        	         if(radioValue =='mon_arpu'){
	        	        	 mudValue=Ext.util.Format.number((item.value[1]-stat1),'0.0');
	        	        		
	        	         }else{
	        	        		mudValue=(item.value[1]-stat1);
	        	         }
	        	         
	        	         str1+=('<br>环比增长:'+mudValue+'<br>环比增长率:'+transToPerc(item.value[1],stat1));	
	        	        
	        	        }
	                     	        
	        	        this.setTitle(str1);
	        	        this.setHeight(n*20); 
	     	     }},
	       
		   listeners: {'itemmouseup': function(item) {
  	    	 
   		   	 var stattype='huanbi'; 	
   		     var yearStr=Ext.util.Format.substr(item.storeItem.get('cycleId'),0,4);
   		     var monthStr=Ext.util.Format.substr(item.storeItem.get('cycleId'),4,2);
	         var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(yearStr,monthStr), Ext.Date.MONTH, -2),'Ym');  //  要取前一个月，但要-2才能实现，为什么不是-1呢？
   		   	   	 
   		   	 cityColumnChart.series.items[0].title=[preMonth,item.storeItem.get('cycleId')];
   		     cityStore.removeAll(); 
   	    	 cityStore.proxy.extraParams.query='stat_time='+item.storeItem.get('cycleId')+'&stat_type='+stattype+'&stat_id='+item.series.yField+'&stat_id1='+radioValue;
   	    	 cityStore.load(); 		
   	    	 
   	    	 win.setTitle(item.series.title);    //win.title 不能变换标题，win.setTtile() 可以，为什么？
   	    	 win.show();
   	    	 
              } }	      
	      }); 
	      }
	     
	     store1.clearGrouping();  
	  	
	      barChart = Ext.create('Ext.chart.Chart', {       
	     	 flex: 1,
	     	 shadow: true,
	     	 animate: true,
	     	 //theme: 'Category5',
	     	 legend: { position: 'bottom'},
	     	 store: store3, 
	     	 axes: [{ type: 'Numeric', position: 'left', fields: prodIds,minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}}, 
	     	                           label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
	     	        {type: 'Category', position: 'bottom',fields: ['cycleId'], font: '10px Arial',  rotate: { degrees: 0 } }],
	     	         
	     	 series: mySeries  
	     	 });
	                           
	      gridForm.setTitle('账期指标走势【'+radioName+'】');
	      Ext.getCmp('barChart').add(barChart);//将chart添加到panel中 测了好久，最后通过设置ID解决问题
	           
     }



function querydata(){
	
	 var stat_time=Ext.util.Format.date(Ext.getCmp('stat_time').value,'Y-m');
	     if(stat_time>preMonth){
		     Ext.Msg.alert('提示','当前最大查询月份为'+preMonth);
		     return;
	     }
	     
	     if(stat_time<'2015-10'){
		     Ext.Msg.alert('提示','当前最小查询月份为2015-10');
		     return;
	     }
	     
	     var stattype='huanbi';
	     searchMonth=stat_time;

	if(gridForm.getForm().isValid()){

    gridPanel.setTitle('明细数据【'+searchMonth+'】');
    Ext.getCmp('barChart').remove(barChart);
	store1.removeAll();
	gridStore.removeAll();
	store1.proxy.extraParams.query='stat_time='+Ext.util.Format.date(Ext.getCmp('stat_time').value,'Ym')+'&stat_type='+stattype+'&stat_id=1';
	store1.load({callback: initPieStore});   
	
	}else{
	       Ext.Msg.alert('提示','输入项格式错误,请检查输入项格式！');     
        }
	
}

var cityStore= Ext.create( Ext.data.Store,{
    fields: [ {name: 'city_name', type: 'string'},  
              {name: 'nowday_bills', type: 'number'},
              {name: 'yesday_bills', type: 'number'}],
    sorters: { property: 'nowday_bills', direction: 'ASC' },  //展示之前排序
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlMonProUserBillData.jspa',
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

    animate: true,
    shadow: true,
    theme:'Category3',  // 没有解决设置柱子的颜色，只能使用主题
    legend: { position: 'bottom',labelColor:'#000'}, //  legend 和  series的title 共同完成图例的配置
    store: cityStore,
    axes: [{
        type: 'Numeric',
        position: 'left',
        fields: ['yesday_bills','nowday_bills'],
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
        highlight: true,
     //   stacked: true,
        tips: {
          trackMouse: true,
          width: 130,
          height: 28,
          renderer: function(storeItem, item) {   
          	
        	var str1='';
        	var n=1;   
        	var statPre=0;
        	var statNow=0;
          
            switch(item.yField){
      	    case 'yesday_bills' : 
      		     str1=storeItem.get('city_name') + ': ' + storeItem.get('yesday_bills'); 
      		     break;
      	    case 'nowday_bills' : 
      		     n=3;
      		     statPre=storeItem.get('yesday_bills');
      		     statNow=storeItem.get('nowday_bills');
      		     str1=storeItem.get('city_name') + ':' + statNow+'<br>环比增长:'+(statNow-statPre)+'<br>环比增长率:'+transToPerc(statNow,statPre);	
      		     break;
      	  } 
          
	        this.setTitle(str1);
 	        this.setHeight(n*20);
        }     
       },
        xField: 'city_name',
        yField: ['yesday_bills','nowday_bills']
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


//Ext封装的resize事件
Ext.EventManager.onWindowResize(function(width, height){
	gridForm.setWidth(width);
});

});
  
    </script>
	</head>

	<body>
		<div id="mydiv" ></div>
	</body>
</html>
