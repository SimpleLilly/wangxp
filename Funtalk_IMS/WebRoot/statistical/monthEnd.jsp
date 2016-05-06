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
        barChart,
        
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



function initPieStore(records, options, success){   ///  records : Ext.data.Record[]   *  options: Options object from the load call   * success: Boolean
	
	var data=[];
	var prodIds=[];
	var cycleIds=[];
	var prodCycDatas=[];
    var mySeries = [];    
    var hashMap = new Ext.util.HashMap();
    
    var statMonth=Ext.util.Format.date(preMonth,'Ym');
     
    var  arrProd;
    
    hashMap.add('mon_jihuo','激活用户');
    hashMap.add('mon_tingji','停机用户');
    hashMap.add('mon_users','出账用户');
    hashMap.add('mon_billtwo','出账总额');
    hashMap.add('mon_yuyin','语音');
    hashMap.add('mon_liuliang','流量');
    hashMap.add('mon_duanxin','短信');
    hashMap.add('mon_laixian','来显');
    hashMap.add('mon_dixiao','低消');
    hashMap.add('mon_xzxianjin','现金销帐');
    hashMap.add('mon_xzzengfee','赠费销帐');
    hashMap.add('mon_owe','欠费');
    hashMap.add('mon_settle','结算');
  
    store1.group('cycle_id');
  
    for(j=0;j<store1.getGroups().length;j++){
    	    	 
   	 if(store1.getGroups()[j].name==statMonth){   gridStore.loadData(store1.getGroups()[j].children);   }
  	 
     }
    
    store1.clearGrouping();
    
    store1.group('stat_type');
    
    for(m=0;m<store1.getGroups().length;m++){
   	 
      	 if(store1.getGroups()[m].name=='2'){   lineStore.loadData(store1.getGroups()[m].children);   }
     	 
        }
    
    store1.clearGrouping();
    
    
    var  str='';
    
    for(n=0;n<lineStore.getAt(0).fields.length;n++){
    	
    	str=lineStore.getAt(0).fields.getAt(n).name;
    	    	
    if(str =='mon_jihuo'||str =='mon_tingji'||str =='mon_users'||str =='mon_billtwo'||str =='mon_yuyin'||str =='mon_liuliang'||
    		str =='mon_duanxin'||str =='mon_laixian'||str =='mon_dixiao'||str =='mon_xzxianjin'||str =='mon_xzzengfee'||str =='mon_owe' ){
    	
	    mySeries.push({
	    	      type: 'line', 
	    	      axis: 'left',
	    	      highlight: {  size: 3,  radius: 3 },  
	    	      markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },
	    	      xField: 'cycle_id', 
	    	      yField: str,
	    	      title: hashMap.get(str),
	    	      tips: {trackMouse: true,  width: 130,height: 20,
	    	    	       renderer: function(storeItem, item) {
	    	    	       
	    	    	    	var n=1;
	    	    	        var arrLine=[];
	    	    	        var stat1=0;
	    	    	        var str1=storeItem.get('cycle_id') + ':' + item.value[1];
	    	    	        var nowMonth=storeItem.get('cycle_id');
	               		    var yearStr=Ext.util.Format.substr(nowMonth,0,4);
	               		    var monthStr=Ext.util.Format.substr(nowMonth,4,2);
	            	        var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(monthStr+'/01/'+yearStr), Ext.Date.MONTH, -1),'Ym');
	            	        
	            	        
	                     /* store1.filterBy(function(element){ return (element.get('stat_type')=='2'&&element.get('cycle_id')==preMonth);  });
	               	        
	            	        if(store1.getCount()>0){ stat1=store1.getAt(0).get(barChart.series.get(0).yField);   } 
	            	        
	            	        store1.clearFilter();
	            	        
	            	        
	            	        var str2='';
	            	        for(param in item.series.chart){
	            	        	
	            	        	str2+=('paramname='+param+',value='+item.series.chart[param]+'<br>');
	            	        }
		            	    Ext.Msg.alert('提示','records------length-------------<br>'+str2);
		            	    
		            	    */
		            	       
	            	        if(lineStore.findRecord('cycle_id',preMonth) != null ){
	            	        	
	            	         n=3;
	            	         stat1=lineStore.findRecord('cycle_id',preMonth).get(item.series.yField);
	            	         
	            	         str1+=('<br>环比增长:'+(item.value[1]-stat1)+'<br>环比增长率:'+transToPerc(item.value[1],stat1));	
	            	        
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
    	 store: lineStore, 
    	 axes: [{ type: 'Numeric', position: 'left', fields: ['mon_jihuo','mon_tingji','mon_users','mon_billtwo','mon_yuyin','mon_liuliang','mon_duanxin','mon_laixian','mon_dixiao','mon_xzxianjin','mon_xzzengfee','mon_owe'],
    		      minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}},
    	                           label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
    	        {type: 'Category', position: 'bottom',fields: ['cycle_id'], font: '10px Arial',  rotate: { degrees: 0 } }],
    	         
    	 series: mySeries  
    	 });
                          
        Ext.getCmp('barChart').add(barChart);//将chart添加到panel中 测了好久，最后通过设置ID解决问题；        
        
        
        
        hashMap.each(function(key,value){
	        
            for(m=0;m<barChart.series.length;m++){
            		
            		if(barChart.series.get(m).yField==key){
            			
            			barChart.series.get(m).addListener('itemmouseup',function(item){	    	    		   
               		   	 var stattype='huanbi';
               		   	 
               		   	 ///item.storeItem.fields.get(j).name     Ext.Array.indexOf(item.storeItem, item)
               		   	// Ext.Msg.alert('提示','records------length--------->');  ///+item.value[0] :index   item.value[1]: y_value 
               		    /// return;
             		   	var citys=[]; 		   	
               		   	var records=[];
               		    var nowMap = new Ext.util.HashMap();
               		    var preMap = new Ext.util.HashMap();
               		    var nowMonth=item.storeItem.get('cycle_id');
               		    
               		    var yearStr=Ext.util.Format.substr(item.storeItem.get('cycle_id'),0,4);
               		    var monthStr=Ext.util.Format.substr(item.storeItem.get('cycle_id'),4,2);
            	        var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(monthStr+'/01/'+yearStr), Ext.Date.MONTH, -1),'Ym');      
            	       
            	        
            	       store1.each(function(record){ 	
            	    	   
	    	    		   if(record.get('stat_type')=='1' && (record.get('cycle_id')==nowMonth||record.get('cycle_id')==preMonth)){
	    	    			   
	    	    			   if(citys.lastIndexOf(record.get('stat_name'))==-1){	    	    		        	
	    	    				   citys.push(record.get('stat_name'));   	
	    	    		         }
	    	    			   
	    	    			   if(record.get('cycle_id')==nowMonth){
	    	    				   nowMap.add(record.get('stat_name'),record.get(key));		    	    				   
	    	    			   }else{
	    	    				   preMap.add(record.get('stat_name'),record.get(key));    				   
	    	        		   }	   
	    	    		    } 	   
	    	    	   });
            	       
            	 	  citys.forEach(function(element, idx, arr){    			  
    	    			  records.push({stat_name:element,pre_nums:preMap.get(element),now_nums:nowMap.get(element)});    	        		    
    	        	 });
            	       
            	       
            	       
               		   	 cityColumnChart.series.items[0].title=[preMonth,item.storeItem.get('cycle_id')];
               		     cityStore.removeAll(); 
               	    	 cityStore.loadData(records); 		
               	    	 cityStore.sort('now_nums', 'ASC');
               	    	
               	    	 win.setTitle(value);    //win.title 不能变换标题，win.setTtile() 可以，为什么？
               	    	 win.show();
               	    	 
                      }); 
            		}
                 }      
            });   

        var   rec = gridStore.getAt(0);	              
        data.push({name:'语音',data1:rec.get('mon_yuyin')+rec.get('mon_yzyuyin') });
        data.push({name:'流量',data1:rec.get('mon_liuliang')+rec.get('mon_yzliuliang')});
        data.push({name:'短信',data1:rec.get('mon_duanxin')});
        data.push({name:'来显',data1:rec.get('mon_yzlaixian')});
        
        store2.loadData(data);	
        
        gridPanel.getSelectionModel().select(0,true);
        
        Ext.MessageBox.hide();

}


var gridStore= Ext.create( Ext.data.JsonStore,{
    fields: ['cycle_id','stat_type','stat_id','stat_name','mon_billone','mon_billtwo','mon_yuyin','mon_liuliang','mon_duanxin',
             'mon_yzyuyin','mon_yzliuliang','mon_laixian','mon_dixiao','mon_xzxianjin','mon_xzzengfee','mon_owe','mon_settle','mon_users']
    });
    
var lineStore= Ext.create( Ext.data.JsonStore,{
    fields: ['cycle_id','stat_type','stat_id','stat_name','mon_billone','mon_billtwo','mon_yuyin','mon_liuliang','mon_duanxin',
             'mon_yzyuyin','mon_yzliuliang','mon_laixian','mon_dixiao','mon_xzxianjin','mon_xzzengfee','mon_owe','mon_settle','mon_users']
    });


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
              {name: 'mon_tingji', type: 'int'}
        ///   {name: 'due', type: 'date', dateFormat:'m/d/Y'}
    ]
});



var  query1='stat_time='+Ext.util.Format.date(preMonth,'Ym')+'&stat_type=huanbi&stat_id=1';

var store1 = Ext.create( Ext.data.Store,{
    model:'Task',
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlMonthEndData.jspa',
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
                
                                    
var gridPanel = new Ext.grid.Panel({
    
    title: 'Details',
    flex: 0.7, 
    split:true,
    autoheight:true, 
    autowidth:true,
    margins:'0 0 0 0',
    cmargins:'0 5 5 5',    
    store: gridStore,
    columnLines: true,
    columns: [   { header: '地市', width:100,align: 'center', dataIndex:'stat_name',locked:true },  
                 { header: '激活用户', width:80,align: 'right', dataIndex:'mon_jihuo'},
                 { header: '停机用户', width:80,align: 'right', dataIndex:'mon_tingji'},
                 { header: '出账用户', width:80,align: 'right', dataIndex:'mon_users'},  
                 { header: '出账金额', width:80,align: 'right', dataIndex:'mon_billtwo'}, 
                 { header: '语音', width:80,align: 'right', renderer:function(value,cellmeta,record) { return (record.data['mon_yzyuyin']+record.data['mon_yuyin']);  }},  
                 { header: '流量', width:80,align: 'right', renderer:function(value,cellmeta,record) { return (record.data['mon_yzliuliang']+record.data['mon_liuliang']);  }},     
                 { header: '短信', width:80,align: 'right', dataIndex:'mon_duanxin'},
                 { header: '来显', width:80,align: 'right', dataIndex:'mon_laixian'},
                 { header: '低消', width:80,align: 'right', dataIndex:'mon_dixiao'},
                 { header: '现金销帐', width:80,align: 'right', dataIndex:'mon_xzxianjin'},  
                 { header: '赠款销帐', width:80,align: 'right', dataIndex:'mon_xzzengfee'},  
                 { header: '欠费金额', width:80,align: 'right', dataIndex:'mon_owe'}, 
                 { header: '结算金额', width:80,align: 'right', dataIndex:'mon_settle'}, 
                 { header: '毛利(出账-结算)', width:105,align: 'right', renderer:function(value,cellmeta,record) { return (record.data['mon_billtwo']-record.data['mon_settle']);  }}, 
                 { header: '毛利(现金-结算)', width:105,align: 'right', renderer:function(value,cellmeta,record) { return (record.data['mon_xzxianjin']-record.data['mon_settle']);  }}, 
                 { header: '毛利率(现金-结算)', width:105,align:'right', renderer:function(value,cellmeta,record) { return transToPerc(record.data['mon_xzxianjin'],record.data['mon_settle']);  } }
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
                font: '10px Arial',
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

title: '账期各指标走势',     
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
          title: 'Company Details',               
          items: [{ margin: '1',flex: 0.4,xtype: 'fieldset', title:'',                  
                   defaults: {width: 200, labelWidth: 60},                   
                   items: [
                         // {xtype: 'tbspacer',height: 30, width:90},
                          /* {xtype:'radiogroup',
                        	  id: 'stattype',
                	        fieldLabel:'统计方式',
                	        items:[{name:'stat_type',boxLabel:'环比',inputValue: 'huanbi',checked:true}, 
                                   {name:'stat_type',boxLabel:'同比',inputValue: 'tongbi' }
                	        ]
                           ,listeners : {'change' : function(view, newValue, oldValue) {
                         	           	    	     
            	    	     var statRadio = view.items;
            	    	                          
            	    	     for(var i = 0; i < statRadio.length; i++){
            	    	     if(statRadio.get(i).checked == true){
            	    	    	 Ext.getCmp('stattypehid').value = statRadio.get(i).inputValue;
            	    	         break;
            	    	       }
            	    	     }
            	    	                 	    	     
            	    	     var jobValue = loginForm.getForm().findField("job").getGroupValue(); //此处获取到的是inputValue的值
            	    	     var job;
            	    	     for(var i = 0; i < jobRadio.length; i++){
            	    	     if(jobRadio.get(i).inputValue == jobValue){
            	    	       job = jobRadio.get(i).boxLabel;
            	    	       break;
            	    	     } 
            	    	                 	        		
                         Ext.getCmp('stattype').getChecked()[0].inputValue;

                          }} 
                          },  
                          
                        {xtype: 'hiddenfield', id: 'stattypehid', value: 'huanbi'},   */               
                       	{xtype: 'tbspacer',height: 30, width:90},
                        {fieldLabel: '统计月份',name: 'stat_time',id:'stat_time',value: preMonth, format:'Y-m', xtype:'datefield' },
                       	{xtype: 'tbspacer',height: 30, width:90},
                    	{text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:120,align:'center',handler:function(){  querydata();  }}
                    	 	]
                    },{height: 200,layout: 'fit',margin: '0 0 0 0',items: [pieChart] }  ]
          },gridPanel]
        
}],
        
renderTo: bd
});



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
    
    preMonth=stat_time;
    Ext.getCmp('barChart').removeAll();
	store1.removeAll();  
	store2.removeAll();
	lineStore.removeAll();
	gridStore.removeAll();
	store1.proxy.extraParams.query='stat_time='+Ext.util.Format.date(Ext.getCmp('stat_time').value,'Ym')+'&stat_type='+stattype+'&stat_id=1';
	store1.load({callback: initPieStore});   
	
	}else{
	       Ext.Msg.alert('提示','输入项格式错误,请检查输入项格式！');     
        }
	
}




var cityStore = Ext.create( Ext.data.JsonStore,{
    fields: ['stat_name','pre_nums','now_nums']
    });




var cityColumnChart = Ext.create('Ext.chart.Chart', {

    animate: true,
    shadow: true,
    theme:'Category2',  // 没有解决设置柱子的颜色，只能使用主题
    legend: { position: 'bottom',labelColor:'#000'}, //  legend 和  series的title 共同完成图例的配置
    store: cityStore,
    axes: [{
        type: 'Numeric',
        position: 'left',
        fields: ['pre_nums','now_nums'],
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
     //   stacked: true,
        tips: {
          trackMouse: true,
          width: 120,
          height: 20,
          renderer: function(storeItem, item) {   
        	  
        	var str1='';
        	var n=1;   
        	var statPre=0;
        	var statNow=0;
        	
        	///// Ext.Msg.alert('提示','month--->'+item.yField);
          
        switch(item.yField){
    	case 'pre_nums' : 
    		  str1=storeItem.get('stat_name') + ': ' + storeItem.get('pre_nums'); 
    		  break;
    	case 'now_nums' : 
    		  n=3;
    		  statPre=storeItem.get('pre_nums');
    		  statNow=storeItem.get('now_nums');
    		  str1=storeItem.get('stat_name') + ':' + statNow+'<br>环比增长:'+(statNow-statPre)+'<br>环比增长率:'+transToPerc(statNow,statPre);	
    		  break;
    	} 
 	          	                     	        
 	        this.setTitle(str1);
 	        this.setHeight(n*20);
    	
        }     
       },
        xField: 'stat_name',
        yField: ['pre_nums','now_nums']
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
