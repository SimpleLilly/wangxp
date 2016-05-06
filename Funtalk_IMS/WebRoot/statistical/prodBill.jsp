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



function initPieStore(records, options, success){   ///  records : Ext.data.Record[]   *  options: Options object from the load call   * success: Boolean
	
	//Ext.Msg.alert('提示','records.lenght---->'+records[0].get('stat_date')+'---backstatus--->');	 //records[0].data.stat_date =records[0].get('stat_date')


		
	var data=[];
	var prodIds=[];
	var cycleIds=[];
	var prodCycDatas=[];
    var mySeries = [];    
    var hashMap = new Ext.util.HashMap();
        
    
    prodIds.push('cycleId');
    
    for (i = 0 ; i < records.length; i++) {
	   	
        if(prodIds.lastIndexOf(records[i].get('product_id'))==-1){
        	
        	prodIds.push(records[i].get('product_id'));       	
        	hashMap.add(records[i].get('product_id'),records[i].get('product_name'));
        	
         }
        
        if(cycleIds.lastIndexOf(records[i].get('cycle_id'))==-1){
        	cycleIds.push(records[i].get('cycle_id'));
         }
        
       }
     
     
     for(m=0;m<cycleIds.length;m++){
    	 
    	 prodCycDatas.push({cycleId:cycleIds[m].toString()});	 
  	 
     }
     
   // 通过修改原先的store3来初始化数据是行不通的，改为如下新建对象的方式问题解决；    store3.fields=prodIds;  store3.loadData(prodCycDatas); 
   var  store3 = Ext.create('Ext.data.JsonStore', { fields: prodIds, data:prodCycDatas }); 

     
   // Ext.Msg.alert('提示','cycleid.length---->'+store3.getCount()+'-----------ele---->'+store3.getAt(0).get('cycleId'));	
   
    /* store3.each(function(record) {   Ext.Msg.alert('提示','cycleid.length---->'+store3.getCount()+'-----------ele---->'+record.get('cycleId'));	   });  */
     
/*    for(m=0;m<cycleIds.length;m++){    
     for(n=0;n<prodIds.length;n++){ 	 
	        store3.getAt(m).set(prodIds[n].toString(),0);      
 	     }
   } */
   
  //// Ext.Msg.alert('提示','records------length--------->'+store1.getGroups()[2].children[0].get('mon_bill')); // [0].get(prodIds[1].toString())

    var  arrProd;
  
    store1.group('product_id');
  
    for(j=0;j<store1.getGroups().length;j++){
    	    	 
    	     cycleIds.forEach(function(eleCycle, idx, arr){
    		 
    		     arrProd =store1.getGroups()[j].children.filter(function(element){
    		
    			 return (element.get('cycle_id')==eleCycle);
    		 });	 
    		 					      	     
    	     if(arrProd.length>0){
    	    	 store3.findRecord('cycleId',eleCycle).set(store1.getGroups()[j].name, arrProd[0].get('mon_bill'));
    	    	     	
    	     }else{
    	    	 store3.findRecord('cycleId',eleCycle).set(store1.getGroups()[j].name,0);
    	     }
    		    
    	 });
  	 
     mySeries.push(
     {type: 'line', 
      axis: 'left',
      highlight: {  size: 3,  radius: 3 },  
      markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },
      xField: 'cycleId', 
      yField: store1.getGroups()[j].name,
      title: hashMap.get(store1.getGroups()[j].name),
      tips: {trackMouse: true,  width: 130,height: 20,
    	       renderer: function(storeItem, item) {this.setTitle(storeItem.get('cycleId') + ' : ' + item.value[1]);
    	     }} }
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
                          
        Ext.getCmp('barChart').add(barChart);//将chart添加到panel中 测了好久，最后通过设置ID解决问题；        
               
       ///  添加监控
       hashMap.each(function(key,value){
        	        
        for(m=0;m<barChart.series.length;m++){
        		
        		if(barChart.series.get(m).yField==key){
        			
        			barChart.series.get(m).addListener('itemmouseup',function(item){	    	    		   
           		   	 var stattype='huanbi';
           		   	 
           		   	 ///item.storeItem.fields.get(j).name     Ext.Array.indexOf(item.storeItem, item)
           		   	// Ext.Msg.alert('提示','records------length--------->');  ///+item.value[0] :index   item.value[1]: y_value 
           		    /// return;
           		   	
           		   var yearStr=Ext.util.Format.substr(item.storeItem.get('cycleId'),0,4);
           		   var monthStr=Ext.util.Format.substr(item.storeItem.get('cycleId'),4,2);
        	       var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(yearStr,monthStr), Ext.Date.MONTH, -2),'Ym');  //  要取前一个月，但要-2才能实现，为什么不是-1呢？
           		   	   	 
           		   	 cityColumnChart.series.items[0].title=[preMonth,item.storeItem.get('cycleId')];
           		     cityStore.removeAll(); 
           	    	 cityStore.proxy.extraParams.query='stat_time='+item.storeItem.get('cycleId')+'&stat_type='+stattype+'&stat_id='+key;
           	    	 cityStore.load(); 		
           	    	 
           	    	 win.setTitle(value);    //win.title 不能变换标题，win.setTtile() 可以，为什么？
           	    	 win.show();
           	    	 
                  }); 
        		}
             }      
        });
             
        store1.clearGrouping();        
        store1.sort('product_id', 'DESC');
        
    	var storeDatas=[];       
        store1.each(function(record) {   
        	     	
            if(record.get('cycle_id')==Ext.util.Format.date(Ext.getCmp('stat_time').value,'Ym'))
             	storeDatas.push(record);        	
        });
        
        
    	if(storeDatas.length==0){
            Ext.MessageBox.hide();
    		Ext.Msg.alert('提示','当前查询账期无收入账单和结算账单数据！');	
    		
    	    store1.removeAll();	
    		return;
    	}
        
        
       store1.removeAll();
       store1.loadData(storeDatas);
        
        ///  下面的代码位置很重要，放在方法的底部可以解决页面初始化后定位错误的问题；
        rec = store1.getAt(0);	              
        data.push({name:'语音',data1:rec.get('mon_yuyin')+rec.get('mon_yzyuyin') });
        data.push({name:'流量',data1:rec.get('mon_liuliang')+rec.get('mon_yzliuliang')});
        data.push({name:'短信',data1:rec.get('mon_duanxin')});
        data.push({name:'来显',data1:rec.get('mon_yzlaixian')});
        
        store2.loadData(data);	
        
        gridPanel.getSelectionModel().select(0,true);
        
        Ext.MessageBox.hide();
    

}


Ext.define('Task', {
    extend: 'Ext.data.Model',
   /// idProperty: 'product_id',
    fields: [ {name: 'cycle_id', type: 'string'},
              {name: 'product_id', type: 'string'},
              {name: 'product_name', type: 'string'},           
              {name: 'mon_bill', type:'int'},
              {name: 'mon_settle', type:'int'},
              {name: 'mon_yuyin', type:'int'},
              {name: 'mon_liuliang', type:'int'},
              {name: 'mon_duanxin', type:'int'},
              {name: 'mon_yzyuyin', type:'int'},
              {name: 'mon_yzliuliang', type:'int'},
              {name: 'mon_yzall', type: 'int'},
              {name: 'mon_laixian', type: 'int'},
              {name: 'mon_dixiao', type: 'int'},
              {name: 'mon_profit', type: 'int'}
        ///   {name: 'due', type: 'date', dateFormat:'m/d/Y'}
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
                
                                    
var gridPanel = new Ext.grid.Panel({
    
    title: 'Details',
    flex: 0.7, 
    split:true,
    autoheight:true, 
    autowidth:true,
    margins:'0 0 0 0',
    cmargins:'0 5 5 5',    
    store: store1,
    columnLines: true,
    columns: [   { header: '产品', width:100,align: 'center', dataIndex:'product_name',locked:true },                   
                 { header: '语音', width:80,align: 'right', dataIndex:'mon_yuyin'},  
                 { header: '流量', width:80,align: 'right', dataIndex:'mon_liuliang'},  
                 { header: '短信', width:80,align: 'right', dataIndex:'mon_duanxin'},  
                 { header: '月租语音', width:80,align: 'right', dataIndex:'mon_yzyuyin'},  
                 { header: '月租流量', width:80,align: 'right', dataIndex:'mon_yzliuliang'},  
                 { header: '来电显示', width:80,align: 'right', dataIndex:'mon_laixian'},  
                 { header: '低消考核', width:80,align: 'right', dataIndex:'mon_dixiao'},     
                 { header: '账单总额', width:80,align: 'right', dataIndex:'mon_bill'},
                 { header: '结算金额', width:80,align: 'right', dataIndex:'mon_settle'},  
                 { header: '毛利润', width:80,align: 'right', dataIndex:'mon_profit'},  ////,renderer:function(value,cellmeta,record) { return record.data['day_bill']-record.data['day_settle'];} 
                 { header: '毛利率(%)', width:80,align: 'right' ,renderer:function(value,cellmeta,record) { return transToPerc(record.data['mon_bill'],record.data['mon_settle']);  } }
              ],
                 
    listeners: {
        
    	selectionchange: function(model, records) {   // records.length 选择的行数      model.getSelection()=records   第一个参数：Ext.selection.Model    第二个参数：selected : Ext.data.Model[]
    		
    		///Ext.Msg.alert('提示','records.lenght---->'+model.getSelection()[0].get('stat_date'));		
    	    //records[0].fields.each(function(item,index,len){Ext.Msg.alert('提示','item---->'+item+'------index---->'+index+'-----len-->'+len); });	
    		///Ext.Msg.alert('提示','records[0]---->'+records[0].fields.getAt(1)+'------id---->'+records[0].getId());
           
    		var data = [];
    		
 		    if (records[0]){
 		    	
 				var data=[];
 	            rec = records[0];	              
 	            data.push({name:'语音',data1:rec.get('mon_yuyin')+rec.get('mon_yzyuyin') });
 	            data.push({name:'流量',data1:rec.get('mon_liuliang')+rec.get('mon_yzliuliang')});
 	            data.push({name:'短信',data1:rec.get('mon_duanxin')});
 	            data.push({name:'来显',data1:rec.get('mon_laixian')});
 	            
 	            store2.loadData(data);	
                     
    		  }		
    	 }
    }
    
});  


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
                font: '14px Arial',
                renderer : function(v){//自定义标签渲染函数  
                    return  '[' +v+']' ;  
                } 
            }
        }]
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

title: '产品各指标走势',     
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
	      layout:'form',                           
          title: '查询条件',               
          items: [{xtype: 'tbspacer',height: 20},
                  {  height: 30,
        	         xtype: 'fieldset',
        	         layout: {type: 'hbox', align:'top'},                                
                     defaults: {width: 200, labelWidth: 60},
                     border:0,
                     items: [
    	                      {fieldLabel: '统计月份',name: 'stat_time',id:'stat_time',value: preMonth, format:'Y-m',flex:1, xtype:'datefield'},
    	                      {xtype: 'tbspacer',height: 30,flex: 0.1},
    	                      {text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:80,align:'center',flex:0.6,handler:function(){  querydata();  }}
                    ]},             
                     {  
                    	   xtype: 'fieldset',
                           title: '展示指标',
                           layout: 'form',
                           defaults: {width: 200, labelWidth: 60},
                           collapsible: true,
                           items: [ {
                                     xtype: 'radiogroup',
                                     fieldLabel: '营业',
                                     cls: 'x-check-group-alt',
                                     columns: 3,
                                     items: [ {boxLabel: '激活', name: 'rb-horiz-1', inputValue:'mon_jihuo'},
                                              {boxLabel: '停机', name: 'rb-horiz-1', inputValue:'mon_tingji', checked: true},
                                              {boxLabel: 'Item3', name: 'rb-horiz-1',hidden:true, inputValue: 3}
                                             ],
                                    listeners : {'change' :raidoAction}
                                  },{
                                      xtype: 'radiogroup',
                                      fieldLabel: '账务',
                                      cls: 'x-check-group-alt',
                                      columns: 3,
                                      items: [ {boxLabel: '账单总额', name: 'rb-horiz-1', inputValue:'mon_bill'},
                                               {boxLabel: '出账用户', name: 'rb-horiz-1', inputValue:'mon_users'},
                                               {boxLabel: 'ARPU',  name: 'rb-horiz-1', inputValue:'mon_arpu'},
                                               {boxLabel: '现金销帐', name: 'rb-horiz-1', inputValue:'mon_xzxianjin'},
                                               {boxLabel: '赠款销帐', name: 'rb-horiz-1', inputValue:'mon_xzzengfee'},
                                               {boxLabel: '欠费',    name: 'rb-horiz-1', inputValue:'mon_owe'},
                                               {boxLabel: '结算',    name: 'rb-horiz-1', inputValue:'mon_settle'},
                                               {boxLabel: '利润1',   name: 'rb-horiz-1', inputValue:'mon_profit1'},
                                               {boxLabel: '利润2',   name: 'rb-horiz-1', inputValue:'mon_profit2'}
                                              ],
                                       listeners : {'change' :raidoAction}
                                   },{
                                      xtype: 'radiogroup',
                                      fieldLabel: '账单明细',
                                      cls: 'x-check-group-alt',
                                      columns: 3,
                                      items: [ {boxLabel: '语音', name: 'rb-horiz-1', inputValue:'mon_yuyin'},
                                               {boxLabel: '流量', name: 'rb-horiz-1', inputValue:'mon_liuliang'},
                                               {boxLabel: '短信', name: 'rb-horiz-1', inputValue:'mon_duanxin'},
                                               {boxLabel: '来显', name: 'rb-horiz-1', inputValue:'mon_laixian'},
                                               {boxLabel: '低消', name: 'rb-horiz-1', inputValue:'mon_dixiao'}
                                              ],
                                     listeners : {'change' :raidoAction}
                                   } 
                                   ]
                       }////, {height: 150,layout: 'fit',margin: '0 0 0 0',items: [pieChart] }
                       ]
          },gridPanel]
        
}],
        
renderTo: bd
});



function raidoAction(view, newValue, oldValue){
   	     
	     var radioValue = gridForm.getForm().getValues()['rb-horiz-1'];  /// 获取radiogroup的值

	     Ext.Msg.alert('提示','radioValue--->'+radioValue);
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
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

	  /* var stattype=Ext.getCmp('stattype').getChecked()[0].inputValue;
	     Ext.getCmp('stattypehid').value=stattype; 
    
	    // return; */

	if(gridForm.getForm().isValid()){
	//query=gridForm.getForm().getValues(true);
    // Ext.Msg.alert('提示','month--->'+month);
    Ext.getCmp('barChart').removeAll();
	store1.removeAll();  
	store2.removeAll();
	store1.proxy.extraParams.query='stat_time='+Ext.util.Format.date(Ext.getCmp('stat_time').value,'Ym')+'&stat_type='+stattype+'&stat_id=1';
	store1.load({callback: initPieStore});   
	
	}else{
	       Ext.Msg.alert('提示','输入项格式错误,请检查输入项格式！');     
        }
	
}



var cityStore= Ext.create( Ext.data.Store,{
    fields: [ {name: 'city_name', type: 'string'},  
              {name: 'nowday_bills', type: 'int'},
              {name: 'yesday_bills', type: 'int'}],
   /// sorters: { property: 'yesday_bills', direction: 'ASC' },  //展示之前排序
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
          width: 100,
          height: 28,
          renderer: function(storeItem, item) {   this.setTitle(storeItem.get('city_name') + ':' + item.value[1]); 
          
       /*switch(item.yField){
    	case 'creat' : this.setTitle(storeItem.get('name') + ': ' + storeItem.get('creat')+ '% '); break;
    	case 'use' : this.setTitle(storeItem.get('name') + ': ' + storeItem.get('use')+ '% '); break;
    	case 'qua' : this.setTitle(storeItem.get('name') + ': ' + storeItem.get('qua')+ '% '); break; 
    	} */
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




});
  
    </script>
	</head>

	<body>
		<div id="mydiv" ></div>
	</body>
</html>
