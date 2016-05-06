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
    'Ext.layout.container.Fit',
    'Ext.layout.container.Column'
]);

var nowDay="<%=nowDay%>";
var paramDay="<%=nowDay%>"; 
var before30Day="<%=before30Day%>";  

Ext.onReady(function(){


var bd = Ext.getBody();

var arrStaeDateAll;
 
 
function transToPerc(v1,v2) {
	
	if(v2==0) v2=1;
	
	var data=Ext.util.Format.number(v1*100/v2,'0.0');
	
    return data + '%';
}


function transToPercOne(v1,v2) {
	
	var data;
	
	if(v2==0){		
		data=v1*100;		
	}else{	
		 data=Ext.util.Format.number((v1-v2)*100/v2,'0.0');
	}
	
	
    return data + '%';
}


function initPieStore(records, options, success){   ///  records : Ext.data.Record[]   *  options: Options object from the load call   * success: Boolean
	    
    var  arrColShow=[];
    var  arrLineShow=[];
    var  arrBarShow=[];
    
    var  arrStatDate=[];
    
    var  cityIdsArray=[];
    var  proIdsArray=[];
    
    var  cityJhArray=[];
    var  cityTjArray=[];
    var  proJhArray=[];
    var  proTJArray=[];
    
    var  groupName;
    var  cityBegin=0;  
    var  cityStop=0;
    var  proBegin=0;  
    var  proStop=0;   ///  必须初始化赋值，否则第一个产品的汇总为 NAN，展示会有问题；
    
    var  cityAllBegin=0;
    var  cityAllStop=0;
    var  proAllBegin=0;
    var  proAllStop=0;

    var cityBeginMap = new Ext.util.HashMap();
    var cityStopMap = new Ext.util.HashMap();
    var proBeginMap = new Ext.util.HashMap();
    var proStopMap = new Ext.util.HashMap();
    var idToNameMap = new Ext.util.HashMap();
    
    var cityBeginAllMap = new Ext.util.HashMap();
    var cityStopAllMap = new Ext.util.HashMap();
    var proBeginAllMap = new Ext.util.HashMap();
    var proStopAllMap = new Ext.util.HashMap();
    
	var stayType=Ext.getCmp('stattypehid').value;
    var statDate=Ext.util.Format.date(paramDay,'Ymd');
    var statBeginDate;
    var statEndDate=Ext.util.Format.date(paramDay,'Ymd');
    
    var yearStr=Ext.util.Format.substr(statDate,0,4);
    var monthStr=Ext.util.Format.substr(statDate,4,2);
    var dayStr=Ext.util.Format.substr(statDate,6,2);

     
     
     for(i=0;i<20;i++){
     
      if(i==19){  statBeginDate=statDate; }
      
      arrStatDate.push(statDate);
      statDate=Ext.util.Format.date(new Date(new Date(Ext.util.Format.substr(statDate,4,2)+'/'+Ext.util.Format.substr(statDate,6,2)+'/'+Ext.util.Format.substr(statDate,0,4)).getTime() - 1000 * 60 * 60 * 24),'Ymd');
          
     }
     
     arrStaeDateAll=arrStatDate;
     
     for (j = 0 ; j < records.length; j++) {
    	 
    	 if(records[j].get('stat_type')==1||records[j].get('stat_type')==2){
    		 
    	       if(cityIdsArray.lastIndexOf(records[j].get('stat_id'))==-1){
    	         	
    	    	    cityIdsArray.push(records[j].get('stat_id'));       	
    	         	idToNameMap.add(records[j].get('stat_id'),records[j].get('stat_name'));  	
    	         } 
    		 
    	 }else{
    		 
  	       if(proIdsArray.lastIndexOf(records[j].get('stat_id'))==-1){
	         	
  	    	    proIdsArray.push(records[j].get('stat_id'));       	
	         	idToNameMap.add(records[j].get('stat_id'),records[j].get('stat_name'));
	         	
	         } 	 
    	 }	   	
     }
     

     
     store1.group('stat_type');
     
     
     for(m=0;m<store1.getGroups().length;m++){

    	 
    	 if(store1.getGroups()[m].name==1){ 		 
    		  cityJhStore.loadData(store1.getGroups()[m].children);
    	 
    	 }else if(store1.getGroups()[m].name==2){		 
    		  cityTjStore.loadData(store1.getGroups()[m].children);
    	 
    	 }else if(store1.getGroups()[m].name==3){
   		      proJhStore.loadData(store1.getGroups()[m].children);
    	
    	 }else if(store1.getGroups()[m].name==4){
 		     proTjStore.loadData(store1.getGroups()[m].children);
    	
    	 }
   	 
      }
     
     cityJhStore.group('stat_date');
     
     for(p=0;p<cityJhStore.getGroups().length;p++){
    	 
    	 cityJhStore.getGroups()[p].children.forEach(function(ele, idx, arr){

    		 cityBegin+=ele.get('stat_value');
		 });	      	     
     		    
      cityBeginMap.add(cityJhStore.getGroups()[p].name,cityBegin);  	
      cityBegin=0;
      
      if(cityJhStore.getGroups()[p].name==statEndDate){

    	  cityJhStore1.loadData(cityJhStore.getGroups()[p].children);
      }
   	 
      }
     
	 
     cityTjStore.group('stat_date');
     
     for(r=0;r<cityTjStore.getGroups().length;r++){
   	 
    	 groupName=cityTjStore.getGroups()[r].name;
    	 
    	 cityTjStore.getGroups()[r].children.forEach(function(ele, idx, arr){

    		 cityStop+=ele.get('stat_value');
		 });	      	     
     		    
      cityStopMap.add(cityTjStore.getGroups()[r].name,cityStop);  	
      cityStop=0;
      
      if(cityTjStore.getGroups()[r].name==statEndDate){
    	  
    	  cityTjStore1.loadData(cityTjStore.getGroups()[r].children);
      }
   	 
      }
     
     
     proJhStore.group('stat_date');
     
     for(s=0;s<proJhStore.getGroups().length;s++){
   	 
    	 groupName=proJhStore.getGroups()[s].name;
    	 
    	 proJhStore.getGroups()[s].children.forEach(function(ele, idx, arr){

    		 proBegin+=ele.get('stat_value');
		 });	      	     
     		    
    	 proBeginMap.add(proJhStore.getGroups()[s].name,proBegin);  	
         proBegin=0;
         
         if(proJhStore.getGroups()[s].name==statEndDate){
       	  
        	 proJhStore1.loadData(proJhStore.getGroups()[s].children);
         }
   	 
      }
     
     
     proTjStore.group('stat_date');
     
     for(t=0;t<proTjStore.getGroups().length;t++){
   	 
    	 groupName=proTjStore.getGroups()[t].name;
    	 
    	 proTjStore.getGroups()[t].children.forEach(function(ele, idx, arr){

    		 proStop+=ele.get('stat_value');
		 });	      	     
     		    
    	 proStopMap.add(proTjStore.getGroups()[t].name,proStop);  	
    	 proStop=0;
    	 
         if(proTjStore.getGroups()[t].name==statEndDate){
          	  
        	 proTjStore1.loadData(proTjStore.getGroups()[t].children);

         }
   	 
      }
     
     
     cityJhStore.clearGrouping();
     cityTjStore.clearGrouping();
     proJhStore.clearGrouping();
     proTjStore.clearGrouping();
     
     
     cityJhStore.group('stat_id');
     
     for(d=0;d<cityJhStore.getGroups().length;d++){
    	 
    	 cityJhStore.getGroups()[d].children.forEach(function(ele, idx, arr){

    		 cityAllBegin+=ele.get('stat_value');
		 });	      	     
     		    
      cityBeginAllMap.add(cityJhStore.getGroups()[d].name,cityAllBegin);  	
      cityAllBegin=0;
   	 
      }
     
     
     cityTjStore.group('stat_id');
     
     for(v=0;v<cityTjStore.getGroups().length;v++){
    	 
    	 cityTjStore.getGroups()[v].children.forEach(function(ele, idx, arr){

    		 cityAllStop+=ele.get('stat_value');
		 });	      	     
     		    
      cityStopAllMap.add(cityTjStore.getGroups()[v].name,cityAllStop);  	
      cityAllStop=0;
      
      }
     
     proJhStore.group('stat_id');
     
     for(l=0;l<proJhStore.getGroups().length;l++){
    	 
    	 proJhStore.getGroups()[l].children.forEach(function(ele, idx, arr){

    		 proAllBegin+=ele.get('stat_value');
		 });	      	     
     		    
    	 proBeginAllMap.add(proJhStore.getGroups()[l].name,proAllBegin);  	
    	 proAllBegin=0;
   	 
      }
     
     
     
     proTjStore.group('stat_date');
     
     for(w=0;w<proTjStore.getGroups().length;w++){
   	     	 
    	 proTjStore.getGroups()[w].children.forEach(function(ele, idx, arr){

    		 proAllStop+=ele.get('stat_value');
		 });	      	     
     		    
    	 proStopAllMap.add(proTjStore.getGroups()[w].name,proAllStop);  	
    	 proAllStop=0;
   	 
      }
     
     cityJhStore.clearGrouping();
     cityTjStore.clearGrouping();
     proJhStore.clearGrouping();
     proTjStore.clearGrouping();
     
     proJhStore.sort('stat_value', 'ASC');
     proTjStore.sort('stat_value', 'ASC');
     
     
     var jhValue=0;
     var tjValue=0;

     
     if(stayType=='city'){
    	  	 
    	 
         for(n=0;n<arrStatDate.length;n++){
        	 
        	 arrLineShow.push({stat_date:arrStatDate[n],day_jihuo:cityBeginMap.get(arrStatDate[n]),day_stop:cityStopMap.get(arrStatDate[n]) });
      	 
         }
         
         for(a=0;a<cityIdsArray.length;a++){
        	 
        	 if(cityJhStore1.findRecord('stat_id',cityIdsArray[a])==null){ 
        		 
        		 jhValue=0;
        		 
        	 }else{
        		 jhValue=cityJhStore1.findRecord('stat_id',cityIdsArray[a]).get('stat_value');
        	 }
        	 
        	 if(cityTjStore1.findRecord('stat_id',cityIdsArray[a])==null){ 
        		 
        		 tjValue=0;
        		 
        	 }else{
        		 
        		 tjValue=cityTjStore1.findRecord('stat_id',cityIdsArray[a]).get('stat_value');
        	 }
        	 
        	 arrColShow.push({stat_name:idToNameMap.get(cityIdsArray[a]),day_jihuo:jhValue,day_stop:tjValue});
        	 
        	 arrBarShow.push({stat_name:idToNameMap.get(cityIdsArray[a]),day_jihuo:cityBeginAllMap.get(cityIdsArray[a]),day_stop:cityStopAllMap.get(cityIdsArray[a])});
         }
         
     }else{
    	 
    	 
         for(b=0;b<arrStatDate.length;b++){
        	 
        	 arrLineShow.push({stat_date:arrStatDate[b],day_jihuo:proBeginMap.get(arrStatDate[b]),day_stop:proBeginMap.get(arrStatDate[b]) });
      	 
         }
         
         for(c=0;c<proIdsArray.length;c++){
        	 
        	 
        	 if(proJhStore1.findRecord('stat_id',proIdsArray[c])==null){ 
        		 
        		 jhValue=0;
        		 
        	 }else{
        		 jhValue=proJhStore1.findRecord('stat_id',proIdsArray[c]).get('stat_value');
        	 }
        	 
        	 if(proTjStore1.findRecord('stat_id',proIdsArray[c])==null){ 
        		 
        		 tjValue=0;
        		 
        	 }else{
        		 
        		 tjValue=proTjStore1.findRecord('stat_id',proIdsArray[c]).get('stat_value');
        	 }
        	 
        	 arrColShow.push({stat_name:idToNameMap.get(proIdsArray[c]),day_jihuo:jhValue,day_stop:tjValue });
        	 
        	 arrBarShow.push({stat_name:idToNameMap.get(proIdsArray[c]),day_jihuo:proBeginAllMap.get(proIdsArray[c]),day_stop:proStopAllMap.get(proIdsArray[c])});

         }    	 
    	 
     }
     
     
     gridForm.setTitle('地市日发展量统计【'+statEndDate+'】');
     barPanel.setTitle('地市发展总量排名【'+Ext.util.Format.substr(statBeginDate,4,4)+'---'+Ext.util.Format.substr(statEndDate,4,4)+'】');
     Ext.getCmp('daysDevelop').setTitle('日发展总量走势【'+Ext.util.Format.substr(statBeginDate,4,4)+'---'+Ext.util.Format.substr(statEndDate,4,4)+'】');
     
     lineStore.loadData(arrLineShow);
     columnStore.loadData(arrColShow);
     barStore.loadData(arrBarShow);
     
     store1.clearGrouping();
     
     Ext.MessageBox.hide();
}



var cityJhStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','stat_type','stat_id','stat_name','stat_value']
    });

var cityTjStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','stat_type','stat_id','stat_name','stat_value']
    });
var proJhStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','stat_type','stat_id','stat_name','stat_value']
    });
    
var proTjStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','stat_type','stat_id','stat_name','stat_value']
    });
    

var cityJhStore1 = Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','stat_type','stat_id','stat_name','stat_value']
    });

var cityTjStore1 = Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','stat_type','stat_id','stat_name','stat_value']
    });
var proJhStore1 = Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','stat_type','stat_id','stat_name','stat_value']
    });
    
var proTjStore1 = Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','stat_type','stat_id','stat_name','stat_value']
    });


Ext.define('Task', {
    extend: 'Ext.data.Model',
   /// idProperty: 'stat_date',   /// 如果返回的数据中id不唯一，一定要将此行注释掉，否则只会从返回的数据中取id相同的记录中的一条；
    fields: [ {name: 'stat_date',  type: 'string'},
              {name: 'stat_type', type: 'string'},
              {name: 'stat_id', type: 'string'},
              {name: 'stat_name', type: 'string'},
              {name: 'stat_value', type: 'int'}
    ]
});



var  query1='stat_time='+Ext.util.Format.date(nowDay,'Ymd')+'&stat_type=city&stat_id=1';

var store1 = Ext.create( Ext.data.Store,{
    model:'Task',
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlDayUsersDataNew.jspa',
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
    

    

var columnStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_name','day_jihuo','day_stop'],
    sorters: { property: 'day_jihuo', direction: 'ASC' }
    });



var  columnChart = Ext.create('Ext.chart.Chart', {

    animate: true,
    shadow: true,
    theme:'Category3',  // 没有解决设置柱子的颜色，只能使用主题
    legend: { position: 'bottom',labelColor:'#000'}, //  legend 和  series的title 共同完成图例的配置
    store: columnStore,
    axes: [{
        type: 'Numeric',
        position: 'left',
        fields: ['day_jihuo','day_stop'],
        label: {  renderer: Ext.util.Format.numberRenderer('0,0')   },
        grid: true,
        minimum: 0
    }, {
        type: 'Category',
        position: 'bottom',
        fields: ['stat_name'],
        label: {rotate: { degrees: 90 }}
    }],
    series: [{
        type: 'column',
        axis: 'left',
        highlight: true,       
        tips: {
            trackMouse: true,
            width: 100,
            height: 24,
            renderer: function(storeItem, item) {   this.setTitle(storeItem.get('stat_name') + ':' + item.value[1]); 
            
          }     
         },  
       listeners: {'itemmouseup': function(item) {
		   
   	       var records = [];   
    	   var statName=item.storeItem.get('stat_name');
    	   
    	   cityJhStore.filterBy(function(record){  return (record.get('stat_name')==statName); }); 
    	   
    	   cityTjStore.filterBy(function(record){  return (record.get('stat_name')==statName); }); 
    	    
    	   var jhValue=0;
    	   var tjValue=0;
    	   var arrColShow1=[];
    	   
    	   var str1='';
           for(b=0;b<arrStaeDateAll.length;b++){
          	      	   
     	    if(cityJhStore.findRecord('stat_date',arrStaeDateAll[b])==null){  jhValue=0;
               }else{  jhValue=cityJhStore.findRecord('stat_date',arrStaeDateAll[b]).get('stat_value');  
               }
     	     
      	    if(cityTjStore.findRecord('stat_date',arrStaeDateAll[b])==null){ 	tjValue=0;  		 
               }else{ tjValue=cityTjStore.findRecord('stat_date',arrStaeDateAll[b]).get('stat_value');  }
     	            
      	    arrColShow1.push({stat_date:arrStaeDateAll[b],day_jihuo:jhValue,day_stop:tjValue });
        	 
           }
           
           
           cityJhStore.removeFilter();
           cityTjStore.removeFilter();
           
      	   colDetailStore.removeAll();
     	   colDetailStore.loadData(arrColShow1);
     	   
     	   colDetailStore1.removeAll();
     	   colDetailStore1.proxy.extraParams.query='stat_time='+Ext.util.Format.date(paramDay,'Ymd')+'&stat_name='+statName+'&stat_id=2';
     	   colDetailStore1.load({callback: initColDetailStore12}); 	
    	        		    	    	 
		   colDetailWin.setTitle(item.storeItem.get('stat_name')+'连续20天走势');	
	       colDetailWin.show();	
}  },
        xField: 'stat_name',
        yField: ['day_jihuo','day_stop'],
  	   title: ['激活量','停机量']
    }]
});



function initColDetailStore12(records, options, success){
	
    var arrColShow12=[];
    
    for (j = 0 ; j < records.length; j++){
      
      if(records[j].get('stat_date')==Ext.util.Format.date(paramDay,'Ymd')&&records[j].get('stat_type')=='3'){
                
         arrColShow12.push({stat_name:records[j].get('stat_name'),stat_value:records[j].get('stat_value')});             
        
      }     
    }
   
    colDetailPanel.setTitle('激活产品构成【'+Ext.util.Format.substr(Ext.util.Format.date(paramDay,'Ymd'),4,4)+'】');

    colDetailStore12.removeAll();
    colDetailStore12.loadData(arrColShow12);
	
}


    
var barStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_name','day_jihuo','day_stop'],
    sorters: { property: 'day_jihuo', direction: 'ASC' }
    });


var barChart = Ext.create('Ext.chart.Chart', {
    style: 'background:#fff',
    animate: true,
    shadow: true,
    store: barStore,
    legend: { position: 'top' },
    axes: [{
        type: 'Numeric',
        position: 'bottom',
        fields: ['day_jihuo','day_stop'],
        minimum: 0,
        grid: true
       }, {
        type: 'Category',
        position: 'left',
        fields: ['stat_name']
    }],
    series: [{
        type: 'bar',
        axis: 'bottom',
        tips: {
            trackMouse: true,
            width: 100,
            height: 24,
            renderer: function(storeItem, item) {   this.setTitle(storeItem.get('stat_name') + ':' + item.value[1]); 
            
          }     
         },
         listeners: {'itemmouseup': function(item) {
  		   
     	   var records = [];   
     	   var str='';
     	   var statId;
      	   var statName=item.storeItem.get('stat_name');
   	   
       	   if(item.yField=='day_jihuo'){
          		str='激活';
          		statId=3;
          	   }else{
          		str='停机';
        		statId=4;
          	   }
       	   barDetailStore.removeAll();
       	   barDetailStore.proxy.extraParams.query='stat_time='+Ext.util.Format.date(paramDay,'Ymd')+'&stat_name='+statName+'&stat_id='+statId;
       	   barDetailStore.load(); 	
      	        		    	    	
  		   barDetailWin.setTitle('【'+item.storeItem.get('stat_name')+'】'+str+'产品构成：');	
  	       barDetailWin.show();	
  }  },
        xField: 'stat_name',
        yField: ['day_jihuo','day_stop'],
   	   title: ['激活量','停机量']
    }]
});


var barPanel = new Ext.panel.Panel({
    
    title: '地市发展总量排名',
    flex: 0.3,
    height:1000, 
    autoScroll:true,  /// 要想让chart完全展示，此层次不能添加layout: 'fit'  参数
    margins:'0 0 0 0',
    cmargins:'0 5 5 5',
    items: [{height: 1000,layout: 'fit',margin: '0 0 0 0',items: [barChart] } ]  ///barChart
    
});

var gridPanel = new Ext.grid.Panel({
    
    title: 'Details',
    flex: 0.3, 
    split:true,
    autoheight:true, 
    autowidth:true,
    margins:'0 0 0 0',
    cmargins:'0 5 5 5',    
    store: columnStore,
    columns: [   { header: '统计名称',   width:100,align: 'center', dataIndex:'stat_name' },           
                 { header: '激活用户', width:100,align: 'center', dataIndex:'day_jihuo'},
                 { header: '停机用户', width:100,align: 'center', dataIndex:'day_stop' }]
    
});


var lineStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','day_jihuo','day_stop'],
    sorters: { property: 'stat_date', direction: 'ASC' }
    });

var lineChart = Ext.create('Ext.chart.Chart', {       
	flex: 1,
	shadow: true,
	animate: true,
	//theme: 'Category5',
	legend: { position: 'bottom'},
	store: lineStore, 
	axes: [{ type: 'Numeric', position: 'left', fields: ['day_jihuo','day_stop'],minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}}, 
	                          label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
	       {type: 'Category', position: 'bottom',fields: ['stat_date'],
	                          label: { renderer: function(v) { return Ext.util.Format.substr(v,4,4);}, font: '10px Arial',  rotate: { degrees: 0 } }}],
	        
	series: [
	  {type: 'line', axis: 'left',    ///    highlight: true,         type: 'column'  
	  // style: {fill: 'rgb(69,109,159)'}, //highlightCfg: {fill: '#a2b5ca'},  // smooth: true, 
	  gutter: 80,
	  // highlight: {  size: 3,  radius: 3 },  
	  // markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },  //  type: 'circle'  cross
	   listeners: {'itemmouseup': function(item) {
  	                       
		                   var citys = [];
		    	  	       var records = [];
		    	  	       var proRecords = [];
		    	  	       var preDaysMap = new Ext.util.HashMap();
		    	  	       var nowDaysMap = new Ext.util.HashMap();
		    	    	   var statDate=item.storeItem.get('stat_date');
		    	    	   
		    	    	   var yearStr=Ext.util.Format.substr(statDate,0,4);
		           		   var monthStr=Ext.util.Format.substr(statDate,4,2);
		           		   var dayStr=Ext.util.Format.substr(statDate,6,2);
                           // 创建日期对象传递的参数格式为  04/07/2016
		        	       var preDay=Ext.util.Format.date(new Date(new Date(monthStr+'/'+dayStr+'/'+yearStr).getTime() - 1000 * 60 * 60 * 24),'Ymd');
		        	       		  
		        	       var preMonthStr=Ext.util.Format.substr(preDay,4,2);
		        	       var preDayStr=Ext.util.Format.substr(preDay,6,2);
		        	       
		        	       cityJhStore.each(function(record){ 	
 
		    	    		   if(record.get('stat_date')==statDate||record.get('stat_date')==preDay){
		    	    			   
		    	    			   if(citys.lastIndexOf(record.get('stat_name'))==-1){	    	    		        	
		    	    				   citys.push(record.get('stat_name'));   	
		    	    		         }
		    	    			   
		    	    			   if(record.get('stat_date')==statDate){
		    	    				   nowDaysMap.add(record.get('stat_name'),record.get('stat_value'));		    	    				   
		    	    			   }else{
		    	    				   preDaysMap.add(record.get('stat_name'),record.get('stat_value'));    				   
		    	        		   }	   
		    	    		    } 	   
		    	    	   });
		        	       	        	       
	    	    		  citys.forEach(function(element, idx, arr){    			  
	    	    			  records.push({stat_name:element,yesday_nums:preDaysMap.get(element),nowday_nums:nowDaysMap.get(element)});    	        		    
	    	        	 });
	    	    		  
		    	    	   lineDetailChart.series.items[0].title=[preMonthStr+'/'+preDayStr,monthStr+'/'+dayStr];
		    	    	   lineDetailStore.removeAll();
		    	    	   lineDetailStore.loadData(records);
		    	    	     	    	   
		    	    	///   Ext.Msg.alert('提示','cycleid.length---->'+records.length+'-----------ele---->'+lineDetailStore.getAt(0).get('nowday_nums'));
		    	    	        		    	    	 
		    			   lineDetailWin.setTitle('激活连续两天走势统计');	
		    		       lineDetailWin.show();	    		       
	   }  },
	   xField: 'stat_date', 
	   yField: ['day_jihuo'],
	   title: ['激活量'],
	   tips: {trackMouse: true,  width: 200,height: 250,dismissDelay: 60000,
           renderer: function(storeItem, item) {
        	      var n=0;
        	      var statDate=storeItem.get('stat_date');    		  
        	      var str=Ext.util.Format.substr(storeItem.get('stat_date'),4,4) + '激活总量:' + storeItem.get('day_jihuo')+',明细如下:<BR>';
        	      
        		    str+='<style type=\"text/css\">';	
        		    str+='.Tab{ border-collapse:collapse; width:95%;text-align:right;font-family: 微软雅黑}';
        		    str+='.Tab td{ border:solid 1px #000000;word-break: keep-all;white-space:nowrap;font-size:8pt;}';
        		    str+='</style>';
        		      		    
        	      str+='<table class=\"Tab\"><tr><td align=center>产品名称</td><td align=center>数量</td><td align=center>占比</td></tr>';
        	      
	    		  proJhStore.each(function(record){    			  
	   	    		   if(record.get('stat_date')==statDate){
	   	    			   n+=1;
	   	        	      str+=('<tr><td align=center>'+record.get('stat_name')+'</td><td>'+record.get('stat_value')+'</td><td>'+transToPerc(record.get('stat_value'),storeItem.get('day_jihuo'))+'</td></tr>');
	   	    		    }	  
   	    	     });
	    		  
	    		  str+='</table>';
        	   
        	   this.setTitle(str);
        	   this.setHeight(20*(n+2));
           }}
	  },
	  
	  {type: 'line', axis: 'left',
		   style: {fill: '#CD0000'}, 
		   highlight: {  size: 3,  radius: 3 },  markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },
		   listeners: {'itemmouseup': function(item) {
			   
               var citys = [];
	  	       var records = [];
	  	       var preDaysMap = new Ext.util.HashMap();
	  	       var nowDaysMap = new Ext.util.HashMap();
	    	   var statDate=item.storeItem.get('stat_date');
	    	   
	    	   var yearStr=Ext.util.Format.substr(statDate,0,4);
       		   var monthStr=Ext.util.Format.substr(statDate,4,2);
       		   var dayStr=Ext.util.Format.substr(statDate,6,2);
               // 创建日期对象传递的参数格式为  04/07/2016
    	       var preDay=Ext.util.Format.date(new Date(new Date(monthStr+'/'+dayStr+'/'+yearStr).getTime() - 1000 * 60 * 60 * 24),'Ymd');
    	       		  
    	       var preMonthStr=Ext.util.Format.substr(preDay,4,2);
    	       var preDayStr=Ext.util.Format.substr(preDay,6,2);
    	       
    	       cityTjStore.each(function(record){ 	

	    		   if(record.get('stat_date')==statDate||record.get('stat_date')==preDay){
	    			   
	    			   if(citys.lastIndexOf(record.get('stat_name'))==-1){        	
	    				   citys.push(record.get('stat_name'));   	
	    		         }
	    			   
	    			   if(record.get('stat_date')==statDate){
	    				   nowDaysMap.add(record.get('stat_name'),record.get('stat_value'));
	    				   
	    			   }else{
	    				   preDaysMap.add(record.get('stat_name'),record.get('stat_value'));	   
	        		   }	   
	    		    }
	    	   });
    	       
    		  citys.forEach(function(element, idx, arr){	  
    			  records.push({stat_name:element,yesday_nums:preDaysMap.get(element),nowday_nums:nowDaysMap.get(element)});	      	    
        		    
        	 });
    		     
	    	   lineDetailChart.series.items[0].title=[preMonthStr+'/'+preDayStr,monthStr+'/'+dayStr];
	    	   lineDetailStore.removeAll();
	    	   lineDetailStore.loadData(records);
	    	        		    	    	 
			   lineDetailWin.setTitle('停机连续两天走势统计');	
		       lineDetailWin.show();	
			   		   
		   }  },
		   xField: 'stat_date', 
		   yField: ['day_stop'],
		   title: ['停机量'], 
		   tips: {trackMouse: true,  width: 200,height: 200, dismissDelay: 60000,
	           renderer: function(storeItem, item) {
	        	      var n=0; 
	        	      var statDate=storeItem.get('stat_date');
	        	      var str=Ext.util.Format.substr(storeItem.get('stat_date'),4,4) + '停机总量:' + storeItem.get('day_stop')+',明细如下:<BR>';
	        	      
	        		    str+='<style type=\"text/css\">';	
	        		    str+='.Tab{ border-collapse:collapse; width:95%;text-align:right;font-family: 微软雅黑}';
	        		    str+='.Tab td{ border:solid 1px #000000;word-break: keep-all;white-space:nowrap;font-size:8pt;}';
	        		    str+='</style>';
	        		    
	        		    
	        	      str+='<table class=\"Tab\"><tr><td align=center>产品名称</td><td align=center>数量</td><td align=center>占比</td></tr>';

	        	      proTjStore.each(function(record){    			  
		   	    		   if(record.get('stat_date')==statDate){
		   	    			   n+=1;
		   	        	      str+=('<tr><td align=center>'+record.get('stat_name')+'</td><td>'+record.get('stat_value')+'</td><td>'+transToPerc(record.get('stat_value'),storeItem.get('day_stop'))+'</td></tr>');
		   	    		    }	  
	   	    	     });
		    		  
		    		  str+='</table>';
		    		          	   
	        	   this.setTitle(str);
	        	          	   
	        	   this.setHeight(20*(n+2));
	           }}
	    }
	       
	  ]   
	});
	


var gridForm = Ext.create('Ext.form.Panel', {

title: '地市日发展量统计',     
frame: true,
autoWidth:true,  
height: 720, //closable:true,
fieldDefaults: {labelAlign: 'left',msgTarget: 'side' },
layout: {type: 'vbox', align: 'stretch' },   
items: [{height: 300,layout: 'fit',margin: '0 0 0 0',items: [columnChart] },          
  {layout: {type: 'hbox', align: 'stretch'},
  flex: 1,
  bodyStyle: 'background-color: transparent',                   
  items: [
        { flex: 0.7,
	      layout: {type: 'vbox', align:'stretch'},    
	      id:'daysDevelop',
          title: '日发展总量走势',    
          items: [{xtype: 'tbspacer',height: 20, width:90},
                  {layout: {type: 'hbox', align:'top'},  
                    defaults: {width: 200, labelWidth: 60},                   
                    items: [{xtype: 'tbspacer',height: 20, flex:0.2},
                           {xtype:'radiogroup',id: 'stattype',fieldLabel:'统计方式',flex:2.5,
                                   items:[{name:'stat_type',boxLabel:'地市',inputValue: 'city',checked:true} 
                                          /////,{name:'stat_type',boxLabel:'产品',inputValue: 'product' }
                            ]},
                        {xtype: 'hiddenfield', id: 'stattypehid', value: 'city'},
                        {fieldLabel: '统计日期',name: 'stat_time',id:'stat_time',value: nowDay,flex:2.5, format:'Y-m-d', xtype:'datefield' },
                        {xtype: 'tbspacer',height: 20,flex:1},
                    	{text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:80,align:'right',flex:1.5, handler:function(){  querydata();  }},
                    	{xtype: 'tbspacer',height: 20, flex:1}
                       	]
                    },{xtype: 'tbspacer',height: 20, width:90},lineChart]
          },  barPanel ]  ///
        
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
    
    paramDay=stat_time;

	if(gridForm.getForm().isValid()){

	store1.removeAll();  
    barStore.removeAll();
    lineStore.removeAll();
    columnStore.removeAll();
	store1.proxy.extraParams.query='stat_time='+Ext.util.Format.date(Ext.getCmp('stat_time').value,'Ymd')+'&stat_type='+stattype+'&stat_id=1';
	store1.load({callback: initPieStore});  // 再次加载必选重新设置回调函数
	
	}else{
	       Ext.Msg.alert('提示','输入项格式错误,请检查输入项格式！');     
        }
	
}


var colDetailStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_date','day_jihuo','day_stop'],
    sorters: { property: 'stat_date', direction: 'ASC' }
    });
    
    
var colDetailChart = Ext.create('Ext.chart.Chart', {       
	flex: 1,
	shadow: true,
	animate: true,
	//theme: 'Category5',
	legend: { position: 'bottom'},
	store: colDetailStore, 
	axes: [{ type: 'Numeric', position: 'left', fields: ['day_jihuo','day_stop'],minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}}, 
	                          label: {renderer: Ext.util.Format.numberRenderer('0','0')}  },
	       {type: 'Category', position: 'bottom',fields: ['stat_date'],
	                          label: { renderer: function(v) { return Ext.util.Format.substr(v,4,4);}, font: '10px Arial',  rotate: { degrees: 0 } }}],
	        
	series: [
	  {type: 'line', axis: 'left', 
	   style: {fill: 'rgb(69,109,159)'},
	   highlight: {  size: 3,  radius: 3 },  
	   markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },  
	   xField: 'stat_date', 
	   yField: ['day_jihuo'],
	   title: ['激活量'],
	   tips: {trackMouse: true,  width: 130,height: 20,
	         renderer: function(storeItem, item) {
     	 
		       var n=1,statPre=0,statNow=0;
		       
	    	   var statDate=item.storeItem.get('stat_date');
	    	   var str1= Ext.util.Format.substr(statDate,4,4)+':'+ item.value[1];
	    	   
	    	   var yearStr=Ext.util.Format.substr(statDate,0,4);
       		   var monthStr=Ext.util.Format.substr(statDate,4,2);
       		   var dayStr=Ext.util.Format.substr(statDate,6,2);
               // 创建日期对象传递的参数格式为  04/07/2016
    	       var preDay=Ext.util.Format.date(new Date(new Date(monthStr+'/'+dayStr+'/'+yearStr).getTime() - 1000 * 60 * 60 * 24),'Ymd');
    	       
   	           if(colDetailStore.findRecord('stat_date',preDay) != null ){        	
   	              n=3;
   	              statPre=colDetailStore.findRecord('stat_date',preDay).get('day_jihuo');   	         
   	              str1+=('<br>环比增长:'+(item.value[1]-statPre)+'<br>环比增长率:'+transToPercOne(item.value[1],statPre));	  	        
   	            }
	 	          	                     	        
	 	        this.setTitle(str1);
	 	        this.setHeight(n*20);
	 	        
	           var arrColShow12=[];
	           
	           colDetailStore1.each(function(record){   
	        
	             if(record.get('stat_date')==storeItem.get('stat_date')&&record.get('stat_type')=='3'){
	                       
	                    arrColShow12.push({stat_name:record.get('stat_name'),stat_value:record.get('stat_value')});         
	               }     
	           });	           
	           colDetailPanel.setTitle('激活产品构成【'+Ext.util.Format.substr(storeItem.get('stat_date'),4,4)+'】');
	           colDetailStore12.removeAll();
	           colDetailStore12.loadData(arrColShow12);
	             
	         }}   
	  },
	  
	  {type: 'line', axis: 'left',
		   style: {fill: '#CD0000'}, 
		   highlight: {  size: 3,  radius: 3 },  markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },
		   xField: 'stat_date', 
		   yField: ['day_stop'],
		   title: ['停机量'],
		   tips: {trackMouse: true,  width: 130,height: 20,
		         renderer: function(storeItem, item) {
		        	 
		        	   var n=1,statPre=0,statNow=0;
			    	   var statDate=item.storeItem.get('stat_date');
			    	   var str1= Ext.util.Format.substr(statDate,4,4)+':'+ item.value[1];
			    	   
			    	   var yearStr=Ext.util.Format.substr(statDate,0,4);
		       		   var monthStr=Ext.util.Format.substr(statDate,4,2);
		       		   var dayStr=Ext.util.Format.substr(statDate,6,2);
		               // 创建日期对象传递的参数格式为  04/07/2016
		    	       var preDay=Ext.util.Format.date(new Date(new Date(monthStr+'/'+dayStr+'/'+yearStr).getTime() - 1000 * 60 * 60 * 24),'Ymd');
		    	       
		   	           if(colDetailStore.findRecord('stat_date',preDay) != null ){        	
		   	              n=3;
		   	              statPre=colDetailStore.findRecord('stat_date',preDay).get('day_stop');   	         
		   	              str1+=('<br>环比增长:'+(item.value[1]-statPre)+'<br>环比增长率:'+transToPercOne(item.value[1],statPre));	  	        
		   	            }
		   	           
			 	        this.setTitle(str1);
			 	        this.setHeight(n*20);     
		   	           
		           var arrColShow12=[];
		           
		           colDetailStore1.each(function(record){   
		        
		             if(record.get('stat_date')==storeItem.get('stat_date')&&record.get('stat_type')=='4'){
		                       
		                    arrColShow12.push({stat_name:record.get('stat_name'),stat_value:record.get('stat_value')});         
		               } 
		     
		           });
		           
		           colDetailPanel.setTitle('停机产品构成【'+Ext.util.Format.substr(storeItem.get('stat_date'),4,4)+'】');
		     
		           colDetailStore12.removeAll();
		           colDetailStore12.loadData(arrColShow12);
		             
		         }} 
	 
	    }
	       
	  ]   
	});


var colDetailStore1= Ext.create( Ext.data.Store,{
	
    fields: [ {name: 'stat_date',  type: 'string'},
              {name: 'stat_type', type: 'string'},
              {name: 'stat_id', type: 'string'},
              {name: 'stat_name', type: 'string'},
              {name: 'stat_value', type: 'int'}],
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlDayUsersDataNew.jspa',
        //extraParams:{query:cityQuery},
        reader: {
            type: 'json',
            totalProperty: 'totalCount',
            root: 'rows'
        }
    },
    autoLoad:false
    });


var colDetailStore12= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_name','stat_value']
    });

 var colDetailChart1 = Ext.create('Ext.chart.Chart', {

        animate: true,
        store: colDetailStore12,
        shadow: false,
        insetPadding:2,   //图形距离边界的距离
        legend: { position: 'right' },
        theme: 'Category4',
        series: [{
            type: 'pie',
            field: 'stat_value',
            showInLegend: true, //  此字段和上面的legend联合发挥作用；   
            tips: {
                trackMouse: true,
                width: 60,
                height: 25,
                renderer: function(storeItem, item) {
                	
                  this.setTitle(storeItem.get('stat_value'));
                }
              },
             label: {
                field: 'stat_name',
                display: 'rotate',
                contrast: true,
                font: '8px Arial',
                renderer : function(item){//自定义标签渲染函数  
                    return   '';  
                } 
            }
        }]
    }); 


 
 var colDetailPanel = new Ext.panel.Panel({
        title:'产品构成',
	    flex: 0.25,
	    //autoScroll:true,  /// 要想让chart完全展示，此层次不能添加layout: 'fit'  参数
	    margins:'0 0 0 0',
	    cmargins:'0 5 5 5',
	    items: [{height: 300,layout: 'fit',margin: '0 0 0 0',items: [colDetailChart1] } ]
	    
	});
    
var colDetailWin = Ext.create('Ext.window.Window', {
    width:1000,
    height: 350,
    minWidth: 400,
    minHeight: 200,
    maximizable: true,
    constrain:true, // 确保拖动窗口时，不超出浏览器边界；
    closable:true,
    closeAction: "hide",  // 在关闭colDetailWin的时候，通过配置项closeAction可以控制按钮是销毁（destroy）还是隐藏（hide），默认情况下为销毁, 此项必须配置，否则，再次无法显示此窗口，因为已经销毁
    title: 'city data',
    modal:true, // 模式窗口，弹出窗口后屏蔽掉其他组建
    plain:true,// 将被屏蔽的窗口变为半透明状态
    layout: {type: 'hbox', align: 'stretch' }, 
    items: [{flex:0.75,layout: 'fit',margin: '0 0 0 0',items: [colDetailChart] },  /// 此处的 layout 必须定义，否则无法展示    {flex:0.25,layout: 'fit',margin: '0 0 0 0',items: [colDetailChart1] }
            colDetailPanel] 
});



var lineDetailStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_name','yesday_nums','nowday_nums'],
    sorters: { property: 'nowday_nums', direction: 'ASC' }
    });

var  lineDetailChart = Ext.create('Ext.chart.Chart', {

    animate: true,
    shadow: true,
    theme:'Category6',  // 没有解决设置柱子的颜色，只能使用主题
    legend: { position: 'bottom',labelColor:'#000'}, //  legend 和  series的title 共同完成图例的配置
    store: lineDetailStore,
    axes: [{
        type: 'Numeric',
        position: 'left',
        fields: ['yesday_nums','nowday_nums'],
        label: {  renderer: Ext.util.Format.numberRenderer('0,0')   },
        grid: true,
        minimum: 0
    }, {
        type: 'Category',
        position: 'bottom',
        fields: ['stat_name'],
        label: {rotate: { degrees: 90 }}
    }],
    series: [{
        type: 'column',
        axis: 'left',
        highlight: true,
        tips: {
          trackMouse: true,
          width: 130,
          height: 20,
          renderer: function(storeItem, item) {   
        	  
        	  
          	var str1='';
        	var n=1;   
        	var statPre=0;
        	var statNow=0;
          
        switch(item.yField){
    	case 'yesday_nums' : 
    		  str1=storeItem.get('stat_name') + ': ' + storeItem.get('yesday_nums'); 
    		  break;
    	case 'nowday_nums' : 
    		  n=3;
    		  statPre=storeItem.get('yesday_nums');
    		  statNow=storeItem.get('nowday_nums');
    		  str1=storeItem.get('stat_name') + ':' + statNow+'<br>环比增长:'+(statNow-statPre)+'<br>环比增长率:'+transToPercOne(statNow,statPre);	
    		  break;
    	} 
 	          	                     	        
 	        this.setTitle(str1);
 	        this.setHeight(n*20);
          
        }     
       },
        xField: 'stat_name',
        yField: ['yesday_nums','nowday_nums']
    }]
});

var lineDetailWin = Ext.create('Ext.window.Window', {
    width:1100,
    height: 350,
    minWidth: 400,
    minHeight: 200,
    maximizable: true,
    constrain:true, // 确保拖动窗口时，不超出浏览器边界；
    closable:true,
    closeAction: "hide",  // 在关闭colDetailWin的时候，通过配置项closeAction可以控制按钮是销毁（destroy）还是隐藏（hide），默认情况下为销毁, 此项必须配置，否则，再次无法显示此窗口，因为已经销毁
    title: 'city data',
    modal:true, // 模式窗口，弹出窗口后屏蔽掉其他组建
    plain:true,// 将被屏蔽的窗口变为半透明状态
    layout:'fit', 
    items: [lineDetailChart] /// 此处的 layout 必须定义，否则无法展示    {flex:0.25,layout: 'fit',margin: '0 0 0 0',items: [colDetailChart1] }

});



var barDetailStore= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_name','stat_value']
    });
    
    
var barDetailStore= Ext.create( Ext.data.Store,{
	
    fields: [ {name: 'stat_id', type: 'string'},
              {name: 'stat_name', type: 'string'},
              {name: 'stat_value', type: 'int'}],
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlDayUsersDataNew.jspa',
        //extraParams:{query:cityQuery},
        reader: {
            type: 'json',
            totalProperty: 'totalCount',
            root: 'rows'
        }
    },
    autoLoad:false
    });


var barDetailChart = Ext.create('Ext.chart.Chart', {

    animate: true,
    store: barDetailStore,
    shadow: false,
    insetPadding: 20,   //图形距离边界的距离
    legend: { position: 'right' },
    theme: 'Category3',
    series: [{
        type: 'pie',
        field: 'stat_value',
        showInLegend: true, //  此字段和上面的legend联合发挥作用；   
        tips: {
            trackMouse: true,
            width: 60,
            height: 25,
            renderer: function(storeItem, item) {
              this.setTitle(storeItem.get('stat_value') );
            }
          },
         label: {
            field: 'stat_name',
            display: 'rotate',
            contrast: true,
            font: '14px Arial',
            renderer : function(v){//自定义标签渲染函数  
                return  '['+v+']' ;  
            } 
        }
    }]
});



var barDetailWin = Ext.create('Ext.window.Window', {
    width:600,
    height:350,
    minWidth: 400,
    minHeight: 200,
    maximizable: true,
    constrain:true, // 确保拖动窗口时，不超出浏览器边界；
    closable:true,
    closeAction: "hide",  // 在关闭colDetailWin的时候，通过配置项closeAction可以控制按钮是销毁（destroy）还是隐藏（hide），默认情况下为销毁, 此项必须配置，否则，再次无法显示此窗口，因为已经销毁
    title: 'city data',
    modal:true, // 模式窗口，弹出窗口后屏蔽掉其他组建
    plain:true,// 将被屏蔽的窗口变为半透明状态
    layout:'fit', 
    items: [barDetailChart] /// 此处的 layout 必须定义，否则无法展示    {flex:0.25,layout: 'fit',margin: '0 0 0 0',items: [colDetailChart1] }

});

});
  
    </script>
	</head>

	<body>
		<div id="mydiv" ></div>
	</body>
</html>
