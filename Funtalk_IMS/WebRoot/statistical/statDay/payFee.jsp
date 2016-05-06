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

		<title>用户缴费统计</title> 

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



var bd = Ext.getBody(),form = false,rec = false,selectedStoreItem = false;

Ext.define('Task', {
    extend: 'Ext.data.Model',
    idProperty: 'stat_date',
    fields: [ {name: 'stat_date',type: 'string'},
              {name: 'agentadd', type: 'int'},
              {name: 'agentsub', type: 'int'},
              {name: 'userfee', type: 'int'},
              {name: 'miaoyhk', type: 'float'},
              {name: 'miaoczk', type: 'float'},
              {name: 'weixin', type: 'float'},
              {name: 'kefuczk', type: 'float'},
              {name: 'nanjinggt', type: 'float'},
              {name: 'gaoyang19e', type: 'float'},
              {name: 'otherfee', type: 'float'},
              {name: 'zengfee', type: 'float'},
              {name: 'returnAllFee', type: 'float'},
              {name: 'returnZsFee', type: 'float'},
              {name: 'returnXJFee', type: 'float'}
        ///   {name: 'due', type: 'date', dateFormat:'m/d/Y'}
    ]
});



var  query1='stat_time='+Ext.util.Format.date(nowDay,'Ymd')+'&stat_type=huanbi&stat_id=1';

var store1 = Ext.create( Ext.data.Store,{
    model:'Task',
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlPayFeeData.jspa',
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
	
	var data;
	
	if(v2==0){		
		data=v1*100;		
	}else{	
		 data=Ext.util.Format.number((v1-v2)*100/v2,'0.0');
	}
	
	
    return data + '%';
}
    
    
function initPieStore(records, options, success){
	
	if(success){
        Ext.MessageBox.hide();
	}else{
		
		Ext.Msg.alert('提示','数据加载失败！');	
	}
	
	if(store1.getCount()==0){
		
		Ext.Msg.alert('提示','后台查询无数据！');	
	}
		
	
	
    var mySeries = [];    
    var hashMap = new Ext.util.HashMap();
     
    var  arrProd;
    
    hashMap.add('userfee','用户充值总额');
    hashMap.add('miaoyhk','网厅银行卡');
    hashMap.add('miaoczk','网厅充值卡');
    hashMap.add('weixin','微信');
    hashMap.add('kefuczk','客服充值卡');
    hashMap.add('nanjinggt','南京国通');
    hashMap.add('gaoyang19e','高阳19e');
    
    
    var  str='';
    
    for(n=0;n<store1.getAt(0).fields.length;n++){
    	
    	str=store1.getAt(0).fields.getAt(n).name;
    	    	
    if(str =='userfee'||str =='miaoyhk'||str =='miaoczk'||str =='weixin'||str =='kefuczk'||str =='nanjinggt'||str =='gaoyang19e' ){
    	
	    mySeries.push({
	    	      type: 'line', 
	    	      axis: 'left',
	    	      highlight: {  size: 3,  radius: 3 },  
	    	      markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },
	    	      xField: 'stat_date', 
	    	      yField: str,
	    	      title: hashMap.get(str),
	    	      tips: {trackMouse: true,  width: 120,height: 20,
	    	    	       renderer: function(storeItem, item) {
	    	    	               
	            	        var n=1,statPre=0,statNow=0;
	            	        var statDate=storeItem.get('stat_date');
	            	        var str1= Ext.util.Format.substr(statDate,4,4)+':'+ item.value[1];
	                        var yearStr=Ext.util.Format.substr(statDate,0,4);
	            	        var monthStr=Ext.util.Format.substr(statDate,4,2);
	            	        var dayStr=Ext.util.Format.substr(statDate,6,2);
	            	               // 创建日期对象传递的参数格式为  04/07/2016
	            	        var preDay=Ext.util.Format.date(new Date(new Date(monthStr+'/'+dayStr+'/'+yearStr).getTime() - 1000 * 60 * 60 * 24),'Ymd');
	            	             
	            	        if(store1.findRecord('stat_date',preDay) != null ){          
	            	                   n=3;
	            	                   statPre=store1.findRecord('stat_date',preDay).get(item.series.yField);              
	            	                   str1+=('<br>环比增长:'+(item.value[1]-statPre)+'<br>环比增长率:'+transToPerc(item.value[1],statPre));              
	            	                 }
	            	                                                
	            	             this.setTitle(str1);
	            	             this.setHeight(n*20);
	    	    	       
	    	    	     }} 
	    	      });    
          }
    } 
    
	
  var  barChart = Ext.create('Ext.chart.Chart', {       
    	 flex: 1,
    	 shadow: true,
    	 animate: true,
    	 //theme: 'Category5',
    	 legend: { position: 'bottom'},
    	 store: store1, 
    	 axes: [{ type: 'Numeric', position: 'left', fields: ['userfee','miaoyhk','miaoczk','weixin','kefuczk','nanjinggt','gaoyang19e'],
    		      minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}},
    	                           label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
    	        {type: 'Category', position: 'bottom',fields: ['stat_date'],
    	        label: { renderer: function(v) { return Ext.util.Format.substr(v,4,4);}, font: '10px Arial',  rotate: { degrees: 0 } } }],
    	         
    	 series: mySeries  
    	 });
                          
        Ext.getCmp('barChart').add(barChart);//将chart添加到panel中 测了好久，最后通过设置ID解决问题；        
           
        hashMap.each(function(key,value){
	        
            for(m=0;m<barChart.series.length;m++){
            		
            		if(barChart.series.get(m).yField==key){
            			
            			barChart.series.get(m).addListener('itemmouseup',function(item){
		    	           
		    	    	   var statDate=item.storeItem.get('stat_date');
		    	    	   var stattype= Ext.getCmp('stattypehid').value;
		    	    	   
		    	    	   var yearStr=Ext.util.Format.substr(statDate,0,4);
		           		   var monthStr=Ext.util.Format.substr(statDate,4,2);
		           		   var dayStr=Ext.util.Format.substr(statDate,6,2);
		        	       var preDay=Ext.util.Format.date(new Date(new Date(monthStr+'/'+dayStr+'/'+yearStr).getTime() - 1000 * 60 * 60 * 24),'Ymd');
		        	       		  
		        	       var preMonthStr=Ext.util.Format.substr(preDay,4,2);
		        	       var preDayStr=Ext.util.Format.substr(preDay,6,2);
		        	       
		        	       
		    	    	   cityStore.removeAll();  
			    	       cityStore.proxy.extraParams.query='stat_time='+item.storeItem.get('stat_date')+'&stat_type='+stattype+'&stat_id='+key;
			    	       cityStore.load({callback: initCityStoreDetail});
		    	    	

			    	 	   cityStoreDetail.removeAll();
	    	    		   cityColumnChart.series.items[0].title=[preMonthStr+'/'+preDayStr,monthStr+'/'+dayStr];
        		    	    	 
		    			   win.setTitle(item.series.title);		    	    		
		    	    	   win.show();	
               	    	 
                      }); 
            		}
                 }      
            });
	
        var data=[];
        rec = records[0];
        
        data.push({name:'网厅充值卡',data1:rec.get('miaoczk') });
        data.push({name:'网厅银行卡',data1:rec.get('miaoyhk')});
        data.push({name:'微信',data1:rec.get('weixin')});
        data.push({name:'客服充值卡',data1:rec.get('kefuczk')});
        data.push({name:'南京国通',data1:rec.get('nanjinggt')}); 
        data.push({name:'高阳19e',data1:rec.get('gaoyang19e')}); 
        data.push({name:'其它',data1:rec.get('otherfee')}); 
        
        store2.loadData(data);	
        
        gridPanel.getSelectionModel().select(0,true);
 
 
 }
 
 

function initCityStoreDetail(records, options, success){
	
    var citys=[],records=[],preMap = new Ext.util.HashMap(),nowMap = new Ext.util.HashMap();
 
    var  nowDay=cityStore.max('stat_date');
    var  preDay=cityStore.min('stat_date');
    
  ///  Ext.Msg.alert('提示','cycleid.length---->'+nowDay+'-----------ele---->'+preDay);
    
    cityStore.each(function(record){
			   
			   if(citys.lastIndexOf(record.get('stat_name'))==-1){	    	    		        	
				   citys.push(record.get('stat_name'));
				   
		         }
			   if(record.get('stat_date')==nowDay){
				   nowMap.add(record.get('stat_name'),record.get('day_nums'));		    	    				   
			   }else if(record.get('stat_date')==preDay){
				   preMap.add(record.get('stat_name'),record.get('day_nums'));    				   
    		   }	   
	   });
    
	   citys.forEach(function(element, idx, arr){    			  
			  records.push({stat_name:element,yesday_nums:preMap.get(element),nowday_nums:nowMap.get(element)});    	        		    
 	   });
	   
      cityStoreDetail.loadData(records);
	
}
 
 
var store2 = Ext.create('Ext.data.JsonStore', {
    fields: ['name', 'data1']
   // data:[{name:'语音',data1:20},{name:'流量',data1:40 },{name:'短信',data1:40 }]   //  第二个字段为数值类型，不能是字符串类型；
});

var pieChart = Ext.create('Ext.chart.Chart', {

        animate: true,
        store: store2,
        shadow: false,
        insetPadding: 20,   //图形距离边界的距离
        legend: { position: 'right' },
        theme: 'Category3',
        series: [{
            type: 'pie',
            field: 'data1',
            showInLegend: true, //  此字段和上面的legend联合发挥作用；   
            tips: {
                trackMouse: true,
                width: 60,
                height: 25,
                renderer: function(storeItem, item) {
                  var total = 0;
                  store2.each(function(rec) {
                      total += rec.get('data1');
                  });
                  this.setTitle( Math.round(storeItem.get('data1') / total * 100) + '%');
                }
              },
             label: {
                field: 'name',
                display: 'rotate',
                contrast: true,
                font: '11px Arial',
                renderer : function(v){//自定义标签渲染函数  
                    return  v;  
                } 
            }
        }]
    });

                
                                    
var gridPanel = new Ext.grid.Panel({
    
    title: '每日充值明细',
    flex: 0.7, 
    split:true,
    autoheight:true, 
    autowidth:true,
    margins:'0 0 0 0',
    cmargins:'0 5 5 5',    
    store: store1,
    columns: [   { header: '日期',width:100, align: 'center', dataIndex:'stat_date',locked:true,
    	           renderer:function(v) { return Ext.util.Format.substr(v,0,4)+'/'+Ext.util.Format.substr(v,4,2)+'/'+Ext.util.Format.substr(v,6,2);} },     
                 { header: '代理商+',  width:100, align: 'right', dataIndex:'agentadd'},
                 { header: '代理商-',  width:100, align: 'right', dataIndex:'agentsub'},
                 { header: '用户充值总额',width:100, align: 'right', dataIndex:'userfee' },           
                 { header: '网厅银行卡', width:100, align: 'right', dataIndex:'miaoyhk'},
                 { header: '网厅充值卡', width:100, align: 'right', dataIndex:'miaoczk'},
                 { header: '微信充值',   width:100, align: 'right', dataIndex:'weixin'},
                 { header: '客服充值卡',  width:100, align: 'right', dataIndex:'kefuczk'},
                 { header: '南京国通',   width:100, align: 'right', dataIndex:'nanjinggt'},
                 { header: '高阳19e',  width:100, align: 'right', dataIndex:'gaoyang19e'},
                 { header: '其它充值',   width:100, align: 'right', dataIndex:'otherfee'},
                 { header: '赠送话费',   width:100, align: 'right', dataIndex:'zengfee'},
                 { header: '返销总话费',  width:100, align: 'right', dataIndex:'returnAllFee'},
                 { header: '返销非现金',  width:100, align: 'right', dataIndex:'returnZsFee'},
                 { header: '返销现金',   width:100, align: 'right', dataIndex:'returnXJFee'}],                 
                 
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
    		
 		    if (records[0]){
 		    	
 				var data=[];
 	            rec = records[0];	              
 	            data.push({name:'网厅充值卡',data1:rec.get('miaoczk') });
 	            data.push({name:'网厅银行卡',data1:rec.get('miaoyhk')});
 	            data.push({name:'微信',data1:rec.get('weixin')});
 	            data.push({name:'客服充值卡',data1:rec.get('kefuczk')});
 	            data.push({name:'南京国通',data1:rec.get('nanjinggt')}); 
 	            data.push({name:'高阳19e',data1:rec.get('gaoyang19e')}); 
 	            data.push({name:'其它',data1:rec.get('otherfee')}); 
 	            
 	            store2.loadData(data);	
                     
    		  }	
    		
    	 }
    }
    
});
    

var gridForm = Ext.create('Ext.form.Panel', {

title: '用户充值走势',     
frame: true,
autoWidth:true,  
height: 720, //closable:true,
fieldDefaults: {labelAlign: 'left',msgTarget: 'side' },
layout: {type: 'vbox', align: 'stretch' },   
items: [{height: 300, id:'barChart', layout: 'fit',margin: '0 0 0 0'},          
  {layout: {type: 'hbox', align: 'stretch'},
  flex: 3,
  //border: false,
  bodyStyle: 'background-color: transparent',                   
  items: [ 
        { flex: 0.3,
	      layout: {type: 'vbox', align:'stretch'},                           
          title: '查询条件',               
          items: [{ margin: '1',flex: 1,xtype: 'fieldset', title:'',                  
                   defaults: {width: 200, labelWidth: 60},                   
                   items: [{xtype: 'tbspacer',height: 10, width:90},
                         /*   {xtype:'radiogroup',id: 'stattype',fieldLabel:'统计方式',
                                   items:[{name:'stat_type',boxLabel:'环比',inputValue: 'huanbi',checked:true}, 
                                          {name:'stat_type',boxLabel:'同比',inputValue: 'tongbi' }
                           ]}, */
                        {xtype: 'hiddenfield', id: 'stattypehid', value: 'huanbi'}, 
                       	{xtype: 'tbspacer',height: 10, width:90},
                        {fieldLabel: '统计日期',name: 'stat_time',id:'stat_time',value: nowDay, format:'Y-m-d', xtype:'datefield' },
                       	{xtype: 'tbspacer',height: 20, width:90},
                    	{text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:120,align:'center',handler:function(){  querydata();  }} 
                       	]
                    },{height: 220,layout: 'fit',margin: '0 0 0 0',items: [pieChart] }]
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
	 
/* 	 var stattype=Ext.getCmp('stattype').getChecked()[0].inputValue;
	 Ext.getCmp('stattypehid').value=stattype; */
	
	 stattype='huanbi';

	if(gridForm.getForm().isValid()){
	//query=gridForm.getForm().getValues(true);
    // Ext.Msg.alert('提示','month--->'+month);
	store1.removeAll();  
	store1.proxy.extraParams.query='stat_time='+Ext.util.Format.date(Ext.getCmp('stat_time').value,'Ymd')+'&stat_type='+stattype+'&stat_id=1';
	store1.load({callback: initPieStore});
	
	}else{
	       Ext.Msg.alert('提示','输入项格式错误,请检查输入项格式！');     
        }
	
}



var cityStore= Ext.create( Ext.data.Store,{
    fields: [ {name: 'stat_date', type: 'string'},
              {name: 'stat_name', type: 'string'},  
              {name: 'day_nums', type: 'int'}],
    sorters: { property: 'day_nums', direction: 'ASC' },  //展示之前排序
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlPayFeeData.jspa',
        //extraParams:{query:cityQuery},
        reader: {
            type: 'json',
            totalProperty: 'totalCount',
            root: 'rows'
        }
    },
    autoLoad:false
    });

var cityStoreDetail= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_name','yesday_nums','nowday_nums'],
    sorters: { property: 'nowday_nums', direction: 'ASC' }
    });
    
var cityColumnChart = Ext.create('Ext.chart.Chart', {
    style: 'background:#fff',
    animate: true,
    shadow: true,
    theme:'Category1',  // 没有解决设置柱子的颜色，只能使用主题
    legend: { position: 'bottom',labelColor:'#000'}, //  legend 和  series的title 共同完成图例的配置
    store: cityStoreDetail,
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
       // style: {  fill: '#CD0000'   },
        highlight: true,
        tips: {
          trackMouse: true,
          width: 120,
          height: 20,
          renderer: function(storeItem, item) {   
      	     
        	  var str1='',n=1,statPre=0,statNow=0;
      
          switch(item.yField){
	      case 'yesday_nums' : 
		       str1=storeItem.get('stat_name') + ': ' + storeItem.get('yesday_nums'); 
		       break;
	      case 'nowday_nums' : 
		       n=3;
		       statPre=storeItem.get('yesday_nums');
		       statNow=storeItem.get('nowday_nums');
		       str1=storeItem.get('stat_name') + ':' + statNow+'<br>环比增长:'+(statNow-statPre)+'<br>环比增长率:'+transToPerc(statNow,statPre);	
		       break;
	} 
	          	                     	        
	        this.setTitle(str1);
	        this.setHeight(n*20);      
        }     
       },
        xField: 'stat_name',
        yField: ['yesday_nums','nowday_nums'],
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
   /*  tbar: [ {
        text: '排序',
        handler: function() {  cityStore.sorters.setProperty('day_nums');  }
        }], */
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
