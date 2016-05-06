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


var bd = Ext.getBody(),form = false, rec = false,selectedStoreItem = false;

var minCycle='';
var searchMonth=preMonth;
var hashMap = new Ext.util.HashMap();

// mon_billone：行业卡按40%统计的账单；   mon_billtwo：未处理行业卡的账单；
    hashMap.add('mon_jihuo','激活用户');
    hashMap.add('mon_tingji','停机用户');
    hashMap.add('mon_users','出账用户');
    hashMap.add('mon_billone','账单总额A');  
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

    
    /// 后台传递15个月的数据，前台剔除最早的一个月后进行展示(为了让第一个月也能展示环比已经明细对比)
    
    function initPieStore(records, options, success){   ///  records : Ext.data.Record[]   *  options: Options object from the load call   * success: Boolean
	
	var data=[];
    var mySeries =[];
    
    var statMonth=Ext.util.Format.date(searchMonth,'Ym');
     
    var  arrProd;
  
    minCycle=store1.min('cycle_id');
   /// Ext.Msg.alert('提示','records------length-----------'+minCycle);
    
    store1.group('cycle_id');
  
    for(j=0;j<store1.getGroups().length;j++){
    	    	 
   	 if(store1.getGroups()[j].name==statMonth){   gridStore.loadData(store1.getGroups()[j].children);   }
  	 
     }
    

    
    store1.clearGrouping();
    
    store1.group('stat_type');
    
    var  arrLine='';
    
    for(m=0;m<store1.getGroups().length;m++){
   	 
      	 if(store1.getGroups()[m].name=='2'){   
      		 
      		arrLine=store1.getGroups()[m].children.filter(function(element){  
     			 return (element.get('cycle_id') != minCycle); 
 			 });		 
       }
     }
    
    lineStore.loadData(arrLine);  
    
    store1.clearGrouping();
    
    var  str='';
    
    for(n=0;n<lineStore.getAt(0).fields.length;n++){
    	
    	str=lineStore.getAt(0).fields.getAt(n).name;
    	    	
    if(str =='mon_jihuo'||str =='mon_tingji'||str =='mon_users'||str =='mon_billone'||str =='mon_yuyin'||str =='mon_liuliang'||
    		str =='mon_duanxin'||str =='mon_laixian'||str =='mon_dixiao'||str =='mon_xzxianjin'||str =='mon_xzzengfee'||str =='mon_owe'||str =='mon_arpu'||str=='mon_settle' ){
    	
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
	    	    	        var stat1=0,mudValue=0;
	    	    	        var str1=storeItem.get('cycle_id') + ':' + item.value[1];
	    	    	        var nowMonth=storeItem.get('cycle_id');
	               		    var yearStr=Ext.util.Format.substr(nowMonth,0,4);
	               		    var monthStr=Ext.util.Format.substr(nowMonth,4,2);
	            	        var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(monthStr+'/01/'+yearStr), Ext.Date.MONTH, -1),'Ym');
	            	        
     	        
	            	/*         var str2='';
	            	        for(param in item.series.chart){
	            	        	
	            	        	str2+=('paramname='+param+',value='+item.series.chart[param]+'<br>');
	            	        }
		            	    Ext.Msg.alert('提示','records------length-------------<br>'+str2);
		            	     */
		            	   
		                    store1.filterBy(function(element){ return (element.get('stat_type')=='2');  });       
	            	        if(store1.findRecord('cycle_id',preMonth) != null ){
	            	        	
	            	         n=3;
	            	         stat1=store1.findRecord('cycle_id',preMonth).get(item.series.yField);
	            	         
	            	         if(item.series.yField =='mon_arpu')
	            	        		mudValue=Ext.util.Format.number((item.value[1]-stat1),'0.0');
	            	        	else
	            	        		mudValue=(item.value[1]-stat1);
	            	         
	            	         str1+=('<br>环比增长:'+mudValue+'<br>环比增长率:'+transToPerc(item.value[1],stat1));	
	            	        
	            	        }
        
		            	     
		            	    store1.clearFilter();
        	                 	        
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
    	 axes: [{ type: 'Numeric', position: 'left', fields: ['mon_jihuo','mon_tingji','mon_users','mon_billone','mon_yuyin','mon_liuliang','mon_duanxin','mon_laixian','mon_dixiao','mon_xzxianjin','mon_xzzengfee','mon_owe','mon_settle','mon_arpu'],
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

        if(gridStore.getCount()>0){
        
        gridStore.getAt(gridStore.find('stat_id','all')).set('stat_name','汇总');
        gridPanel.getStore().commitChanges(); /// 去掉grid中左上角的红色修改 标志
        
        var   rec = gridStore.getAt(0);	              
        data.push({name:'语音',data1:rec.get('mon_yuyin') });
        data.push({name:'流量',data1:rec.get('mon_liuliang')});
        data.push({name:'短信',data1:rec.get('mon_duanxin')});
        data.push({name:'来显',data1:rec.get('mon_yzlaixian')});
        data.push({name:'低消',data1:rec.get('mon_dixiao')});
        
        store2.loadData(data);	
        
        gridPanel.getSelectionModel().select(0,true);
        
        }else{        	        	
        	Ext.Msg.alert('提示',preMonth+'账期无数据！');       
        	return; 	
        }
        
        Ext.MessageBox.hide();

}


var gridStore= Ext.create( Ext.data.JsonStore,{
    fields: ['cycle_id','stat_type','stat_id','stat_name','mon_billone','mon_billtwo','mon_yuyin','mon_liuliang','mon_duanxin',
             'mon_yzyuyin','mon_yzliuliang','mon_laixian','mon_dixiao','mon_xzxianjin','mon_xzzengfee','mon_owe','mon_settle','mon_users','mon_jihuo','mon_tingji','mon_arpu','mon_profit1','mon_profit2','mon_rate1','mon_rate2']
    });
    
var lineStore= Ext.create( Ext.data.JsonStore,{
    fields: ['cycle_id','stat_type','stat_id','stat_name','mon_billone','mon_billtwo','mon_yuyin','mon_liuliang','mon_duanxin',
             'mon_yzyuyin','mon_yzliuliang','mon_laixian','mon_dixiao','mon_xzxianjin','mon_xzzengfee','mon_owe','mon_settle','mon_users','mon_jihuo','mon_tingji','mon_arpu','mon_profit1','mon_profit2','mon_rate1','mon_rate2']
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
              {name: 'mon_tingji', type: 'int'},
              {name: 'mon_arpu', type: 'number'},
              {name: 'mon_profit1', type: 'int'},
              {name: 'mon_profit2', type: 'int'},
              {name: 'mon_rate1', type: 'string'},
              {name: 'mon_rate2', type: 'string'}
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
        

function transToDiv(v1,v2) {
		
	if(v2==0) v2=1;		

    return Ext.util.Format.number(v1/v2,'0.0');
}


 

var gridPanel = new Ext.grid.Panel({
    
    title: '地市明细【'+preMonth+'】',
    flex: 0.7, 
    split:true,
    autoheight:true, 
    autowidth:true,
    margins:'0 0 0 0',
    cmargins:'0 5 5 5',    
    store: gridStore,
    viewConfig : {
        ///rowOverCls : 'my-row-over',//鼠标移过的行样式
        ///selectedRowClass : "my-row-selected",//选中行的样式
        getRowClass : function(record,rowIndex,rowParams,store){  //指定行的样式
          if(record.get('stat_id')=='all'){
           return "my-row";
          }
         }
       },
    columnLines: true,
    columns:[   { header: '地市', width:100,align: 'center', dataIndex:'stat_name',locked:true },  
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
                { header: '利润B(B-C)', width:115,align: 'right', dataIndex:'mon_profit2'}, 
                { header: '利润率A', width:105,align:'right', dataIndex:'mon_rate1' },
                { header: '利润率B', width:105,align:'right', dataIndex:'mon_rate2' }
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
 	            data.push({name:'语音',data1:rec.get('mon_yuyin')});
 	            data.push({name:'流量',data1:rec.get('mon_liuliang')});
 	            data.push({name:'短信',data1:rec.get('mon_duanxin')});
 	            data.push({name:'来显',data1:rec.get('mon_laixian')});
 	            data.push({name:'低消',data1:rec.get('mon_dixiao')});
 	            
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
            },
            listeners : {  
                itemclick : function(o) {  
                    
                	var selCityCode='';
                	var selCityName='';
                	var pieStoreData=[];
                	var mySeries=[];
                    var rs=gridPanel.getSelectionModel().getSelection();
                    
                    if(rs.length==0){
                    	Ext.Msg.alert('提示','从右侧列表中获取选中地市失败！');	
                    }else{
                    	    
                    	selCityCode=rs[0].get('stat_id');
                    	selCityName=rs[0].get('stat_name');
                    //	Ext.Msg.alert('提示','从右侧列表中获取选中地市:'+selCityCode+'-----'+selCityName);
                    	
                    if(selCityCode=='all') return;
                    	
                        store1.each(function(record){   	     	
                            if(record.get('stat_id')==selCityCode&&record.get('cycle_id')!=minCycle)
                            	pieStoreData.push(record);        	
                        });
                        
                        pieDetailStore.loadData(pieStoreData);
                        
                      
                        var  str='';
                        
                        for(n=0;n<pieDetailStore.getAt(0).fields.length;n++){
                        	
                        str=pieDetailStore.getAt(0).fields.getAt(n).name;
                        	    	
                        if(str =='mon_users'||str =='mon_jihuo'||str =='mon_tingji'||str =='mon_billone'||str =='mon_xzxianjin'||str =='mon_owe'||str =='mon_settle'||str =='mon_arpu'  ){
                        	
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
                    	    	    	        var stat1=0,mudValue=0;
                    	    	    	        var str1=storeItem.get('cycle_id') + ':' + item.value[1];
                    	    	    	        var nowMonth=storeItem.get('cycle_id');
                    	    	    	        var cityCode=storeItem.get('stat_id');
                    	               		    var yearStr=Ext.util.Format.substr(nowMonth,0,4);
                    	               		    var monthStr=Ext.util.Format.substr(nowMonth,4,2);
                    	            	        var preMonth=Ext.util.Format.date(Ext.Date.add(new Date(monthStr+'/01/'+yearStr), Ext.Date.MONTH, -1),'Ym');
		            	       
                    		                    store1.filterBy(function(element){ return (element.get('stat_type')=='1'&&element.get('stat_id')==cityCode);  });   ///此处的 cityCode不能使用上面的 selCityCode,这个值只传递一层；
                    	            	        if(store1.findRecord('cycle_id',preMonth) != null ){
                    	            	        	
                       	            	         n=3;
                    	            	         stat1=store1.findRecord('cycle_id',preMonth).get(item.series.yField);  
                    	            	        	
                    	            	         if(item.series.yField =='mon_arpu')
                    	            	        		mudValue=Ext.util.Format.number((item.value[1]-stat1),'0.0');
                    	            	        	else
                    	            	        		mudValue=(item.value[1]-stat1);
                 	            	         
                    	            	         str1+=('<br>环比增长:'+mudValue+'<br>环比增长率:'+transToPerc(item.value[1],stat1));	                    	            	        
                    	            	        }
                    		            	     
                    	            	        
                    		            	    store1.clearFilter();
                    		            	    
                    		            	     var arrpieShow11=[];
                                 		         var rec = item.storeItem;	              
                                 		           arrpieShow11.push({stat_name:'语音',stat_value:rec.get('mon_yuyin') });
                                 		           arrpieShow11.push({stat_name:'流量',stat_value:rec.get('mon_liuliang')});
                                 		           arrpieShow11.push({stat_name:'短信',stat_value:rec.get('mon_duanxin')});
                                 		           arrpieShow11.push({stat_name:'来显',stat_value:rec.get('mon_laixian')});
                                 		           arrpieShow11.push({stat_name:'低消',stat_value:rec.get('mon_dixiao')});
                            
                                 		           pieDetailStore1.removeAll();
                                 		           pieDetailStore1.loadData(arrpieShow11);
                                             	   pieDetailPanel.setTitle('账单构成【'+item.storeItem.get('cycle_id')+'】');
                    	            	              
                    	            	                 	        
                    	            	        this.setTitle(str1);
                    	            	        this.setHeight(n*20);
                    	    	    	       
                    	    	    	     }} 
                    	    	      });    
                              }
                        } 
                        
                    	
                      var  pieDetailChart = Ext.create('Ext.chart.Chart', {       
                        	 flex: 1,
                        	 shadow: true,
                        	 animate: true,
                        	 legend: { position: 'bottom'},
                        	 store: pieDetailStore, 
                        	 axes: [{ type: 'Numeric', position: 'left', fields: ['mon_users','mon_jihuo','mon_tingji','mon_billone','mon_xzxianjin','mon_owe','mon_settle','mon_arpu'],
                        		      minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}},
                        	                           label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
                        	        {type: 'Category', position: 'bottom',fields: ['cycle_id'], font: '10px Arial',  rotate: { degrees: 0 } }],
                        	         
                        	 series: mySeries  
                        	 });
                                              
                            Ext.getCmp('pieDetailChart').add(pieDetailChart);//将chart添加到panel中 测了好久，最后通过设置ID解决问题；           
                                       
        		              		           
        		           var arrpieShow1=[];
        		           
        		           rec = pieDetailStore.getAt(0);	              
        		           arrpieShow1.push({stat_name:'语音',stat_value:rec.get('mon_yuyin')+rec.get('mon_yzyuyin') });
        		           arrpieShow1.push({stat_name:'流量',stat_value:rec.get('mon_liuliang')+rec.get('mon_yzliuliang')});
        		           arrpieShow1.push({stat_name:'短信',stat_value:rec.get('mon_duanxin')});
        		           arrpieShow1.push({stat_name:'来显',stat_value:rec.get('mon_laixian')});
        		           arrpieShow1.push({stat_name:'低消',stat_value:rec.get('mon_dixiao')});
   
        		           pieDetailStore1.removeAll();
        		           pieDetailStore1.loadData(arrpieShow1);
        		                      
         		           pieDetailPanel.setTitle('账单构成【'+rec.get('cycle_id')+'】');
         		           
                           pieDetailWin.setTitle(selCityName+'连续14个月走势');	
             		       pieDetailWin.show();	
                    	
                    }
                }  
            } 
        }]
    });
    

var gridForm = Ext.create('Ext.form.Panel', {

title: '账期指标走势',     
frame: true,
autoWidth:true,
height: 720, //closable:true,
fieldDefaults: {labelAlign: 'left',msgTarget: 'side' },
layout: {type: 'vbox', align: 'stretch' },   
items: [{height: 350, id:'barChart', layout: 'fit',margin: '0 0 0 0'},          
  {layout: {type: 'hbox', align: 'stretch'},
  flex: 3,
  //border: false,
  bodyStyle: 'background-color: transparent',                   
  items: [ 
        { flex: 0.3,
	      layout: {type: 'vbox', align:'stretch'},                           
          title: '查询条件',               
          items: [{ margin: '1',flex: 0.4,xtype: 'fieldset', title:'',                  
                   defaults: {width: 200, labelWidth: 60},                   
                   items: [             
                       	{xtype: 'tbspacer',height: 20, width:10},
                        {fieldLabel: '统计月份',name: 'stat_time',id:'stat_time',value: preMonth, format:'Y-m', xtype:'datefield' },
                       	{xtype: 'tbspacer',height: 10, width:10},
                    	{text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:120,align:'center',handler:function(){  querydata();  }}
                    	 	]
                    },{height: 180,layout: 'fit',margin: '0 0 0 0',items: [pieChart] }  ]
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

	if(gridForm.getForm().isValid()){
    
    searchMonth=stat_time;
    gridPanel.setTitle('地市明细【'+searchMonth+'】');

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



var pieDetailStore= Ext.create( Ext.data.JsonStore,{
    fields: ['cycle_id','stat_type','stat_id','stat_name','mon_billone','mon_billtwo','mon_yuyin','mon_liuliang','mon_duanxin',
             'mon_yzyuyin','mon_yzliuliang','mon_laixian','mon_dixiao','mon_xzxianjin','mon_xzzengfee','mon_owe','mon_settle','mon_users','mon_jihuo','mon_tingji','mon_arpu','mon_profit1','mon_profit2','mon_rate1','mon_rate2']
    });




var pieDetailStore1= Ext.create( Ext.data.JsonStore,{
    fields: ['stat_name','stat_value']
    });

 var pieDetailChart1 = Ext.create('Ext.chart.Chart', {

        animate: true,
        store: pieDetailStore1,
        shadow: false,
        insetPadding:4,   //图形距离边界的距离
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
                font: '12px Arial',
                renderer : function(item){//自定义标签渲染函数  
                    return   '['+item+']';  
                } 
            }
        }]
    }); 


 
 var pieDetailPanel = new Ext.panel.Panel({
        title:'产品构成',
	    flex: 0.25,
	    //autoScroll:true,  /// 要想让chart完全展示，此层次不能添加layout: 'fit'  参数
	    margins:'0 0 0 0',
	    cmargins:'0 5 5 5',
	    items: [{height: 300,layout: 'fit',margin: '0 0 0 0',items: [pieDetailChart1] } ]
	    
	});
    
var pieDetailWin = Ext.create('Ext.window.Window', {
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
    layout: {type: 'hbox', align: 'stretch' }, 
    items: [{flex:0.75,layout: 'fit',margin: '0 0 0 0',id:'pieDetailChart'},  /// 此处的 layout 必须定义，否则无法展示    {flex:0.25,layout: 'fit',margin: '0 0 0 0',items: [colDetailChart1] }
            pieDetailPanel] 
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
