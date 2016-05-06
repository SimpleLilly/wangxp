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

		<title>用户账单统计</title> 

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
var before30Day="<%=before30Day%>";  

Ext.onReady(function(){



var bd = Ext.getBody(),
        form = false,
        rec = false,
        selectedStoreItem = false;



function initPieStore(records, options, success){   ///  records : Ext.data.Record[]   *  options: Options object from the load call   * success: Boolean
	
	//Ext.Msg.alert('提示','records.lenght---->'+records[0].get('stat_date')+'---backstatus--->');	 //records[0].data.stat_date =records[0].get('stat_date')

	if(success){
        Ext.MessageBox.hide();
	}else{
		
		Ext.Msg.alert('提示','数据加载失败！');	
	}
	
	
    var mySeries = [];    
    var hashMap = new Ext.util.HashMap();
     
    var  arrProd;
    
    hashMap.add('day_bill','账单总额');
    hashMap.add('day_settle','结算金额');
    hashMap.add('day_yuyin','语音');
    hashMap.add('day_liuliang','流量');
    hashMap.add('day_duanxin','短信');
    hashMap.add('day_laixian','来电显示');
    hashMap.add('day_yzall','月租');
	
    
    var  str='';
    
    for(n=0;n<store1.getAt(0).fields.length;n++){
    	
    	str=store1.getAt(0).fields.getAt(n).name;
    	    	
    if(str =='day_bill'||str =='day_settle'||str =='day_yuyin'||str =='day_liuliang'||str =='day_duanxin'||str =='day_laixian'||str =='day_yzall' ){
    	
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
   	 axes: [{ type: 'Numeric', position: 'left', fields: ['day_bill','day_settle','day_yuyin','day_liuliang','day_duanxin','day_laixian','day_yzall'],
   		      minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}},
   	                           label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
   	        {type: 'Category', position: 'bottom',fields: ['stat_date'],
   	        label: { renderer: function(v) { return Ext.util.Format.substr(v,4,4);}, font: '10px Arial',  rotate: { degrees: 0 } } }],
   	         
   	 series: mySeries  
   	 });
                         
       Ext.getCmp('barChart').add(barChart);//将chart添加到panel中 测了好久，最后通过设置ID解决问题；        
          
	        
           for(m=0;m<barChart.series.length;m++){
           		
           		if(barChart.series.get(m).yField=='day_bill'||barChart.series.get(m).yField=='day_settle' ){
           			
           			barChart.series.get(m).addListener('itemmouseup',function(item){
		    	    	   
			                 selectedStoreItem = item.storeItem;                                
			    	    	    		   
			    		   	 var stattype='huanbi'; 		   
			    		   	 cityColumnChart.series.items[0].title=['结算成本','毛利润'];
			    		     cityStore.removeAll();  
			    	    	 cityStore.proxy.extraParams.query='stat_time='+item.storeItem.get('stat_date')+'&stat_type='+stattype+'&stat_id=2';
			    	    	 cityStore.load();
			    	    		
			    	    	 win.show();
              	    	 
                     }); 
           		}
            }      

	
	
	var data=[];
	  
    rec = records[0];
    
    data.push({name:'语音',data1:rec.get('day_yuyin') });
    data.push({name:'流量',data1:rec.get('day_liuliang')});
    data.push({name:'短信',data1:rec.get('day_duanxin')});
    data.push({name:'月租',data1:rec.get('day_yzall')});
    data.push({name:'来显',data1:rec.get('day_yzlaixian')}); 
    
    store2.loadData(data);	
    
    
    ////Ext.Msg.alert('提示','records.lenght---->'+store2.getAt(0).get('data1'));	
      
    gridPanel.getSelectionModel().select(0,true);

}


Ext.define('Task', {
    extend: 'Ext.data.Model',
    idProperty: 'stat_date',
    fields: [ {name: 'stat_date',  type: 'string'},
              {name: 'day_bill', type: 'int'},
              {name: 'day_settle', type: 'int'},
              {name: 'day_yuyin', type: 'int'},
              {name: 'day_liuliang', type: 'int'},
              {name: 'day_duanxin', type: 'int'},
              {name: 'day_yzyuyin', type: 'int'},
              {name: 'day_yzliuliang', type: 'int'},
              {name: 'day_yzall', type: 'int'},
              {name: 'day_laixian', type: 'int'},
              {name: 'day_dixiao', type: 'int'},
              {name: 'day_profit', type: 'int'}
        ///   {name: 'due', type: 'date', dateFormat:'m/d/Y'}
    ]
});



var  query1='stat_time='+Ext.util.Format.date(nowDay,'Ymd')+'&stat_type=huanbi&stat_id=1';

var store1 = Ext.create( Ext.data.Store,{
    model:'Task',
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlUserBillData.jspa',
        extraParams:{query:query1},
        reader: {
            type: 'json',
            totalProperty: 'totalCount',
            root: 'rows'
        }
    },
    autoLoad:{ callback: initPieStore },
    listeners:{  beforeload:function(){	 Ext.MessageBox.wait('正在加载数据......','Please wait');   }   }

    });
    
    
function transToPerc(v1,v2) {
	
	if(v2==0) {		
		data=v1*100;		
	}else{	
		 data=Ext.util.Format.number((v1-v2)*100/v2,'0.0');
	}
	
    return data + '%';
}
                
                                    
var gridPanel = new Ext.grid.Panel({
    
    title: '每日账单明细',
    flex: 0.7, 
    split:true,
    autoheight:true, 
    autowidth:true,
    margins:'0 0 0 0',
    cmargins:'0 5 5 5',    
    store: store1,
    columnLines: true,
    columns: [   { header: '日期',   width:100,align: 'center', dataIndex:'stat_date',locked:true, 
    	           renderer:function(v) { return Ext.util.Format.substr(v,0,4)+'/'+Ext.util.Format.substr(v,4,2)+'/'+Ext.util.Format.substr(v,6,2);}  },                   
                 { header: '语音', width:80,align: 'right', dataIndex:'day_yuyin'},  
                 { header: '流量', width:80,align: 'right', dataIndex:'day_liuliang'},  
                 { header: '短信', width:80,align: 'right', dataIndex:'day_duanxin'},  
                 { header: '月租语音', width:80,align: 'right', dataIndex:'day_yzyuyin'},  
                 { header: '月租流量', width:80,align: 'right', dataIndex:'day_yzliuliang'},  
                 { header: '来电显示', width:80,align: 'right', dataIndex:'day_laixian'},  
                 { header: '低消考核', width:80,align: 'right', dataIndex:'day_dixiao'},     
                 { header: '账单总额', width:80,align: 'right', dataIndex:'day_bill'},
                 { header: '结算金额', width:80,align: 'right', dataIndex:'day_settle'},  
                 { header: '毛利润', width:80,align: 'right', dataIndex:'day_profit'},  ////,renderer:function(value,cellmeta,record) { return record.data['day_bill']-record.data['day_settle'];} 
                 { header: '毛利率(%)', width:80,align: 'right' ,renderer:function(value,cellmeta,record) { return transToPerc(record.data['day_bill'],record.data['day_settle']);  } }
              ],
                 
    listeners: {
        
    	selectionchange: function(model, records) {   // records.length 选择的行数      model.getSelection()=records   第一个参数：Ext.selection.Model    第二个参数：selected : Ext.data.Model[]
    		
    		///Ext.Msg.alert('提示','records.lenght---->'+model.getSelection()[0].get('stat_date'));		
    	    //records[0].fields.each(function(item,index,len){Ext.Msg.alert('提示','item---->'+item+'------index---->'+index+'-----len-->'+len); });	
    		///Ext.Msg.alert('提示','records[0]---->'+records[0].fields.getAt(1)+'------id---->'+records[0].getId());
           
 		    if (records[0]){
 		    	
 				var data=[];
 	            rec = records[0];	              
 	            data.push({name:'语音',data1:rec.get('day_yuyin') });
 	            data.push({name:'流量',data1:rec.get('day_liuliang')});
 	            data.push({name:'短信',data1:rec.get('day_duanxin')});
 	            data.push({name:'月租',data1:rec.get('day_yzall')});
 	            data.push({name:'来显',data1:rec.get('day_laixian')});
 	            
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

   

var gridForm = Ext.create('Ext.form.Panel', {

title: '用户账单各指标走势',     
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
                        {fieldLabel: '统计日期',name: 'stat_time',id:'stat_time',value: nowDay, format:'Y-m-d', xtype:'datefield' },
                       	{xtype: 'tbspacer',height: 30, width:90},
                    	{text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:120,align:'center',handler:function(){  querydata();  }}
                    	 	]
                    },{height: 200,layout: 'fit',margin: '0 0 0 0',items: [pieChart] }  ]
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
	     
	     var stattype='huanbi';

	  /* var stattype=Ext.getCmp('stattype').getChecked()[0].inputValue;
	     Ext.getCmp('stattypehid').value=stattype; 
    
	    // return; */

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
    fields: [ {name: 'city_name', type: 'string'},  
              {name: 'day_settleBills', type: 'int'},
              {name: 'day_profits', type: 'int'}],
    sorters: { property: 'day_settleBills', direction: 'ASC' },  //展示之前排序
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlUserBillData.jspa',
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
    theme:'Category1',  // 没有解决设置柱子的颜色，只能使用主题
    legend: { position: 'bottom',labelColor:'#000'}, //  legend 和  series的title 共同完成图例的配置
    store: cityStore,
    axes: [{
        type: 'Numeric',
        position: 'left',
        fields: ['day_settleBills','day_profits'],
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
        stacked: true,
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
        yField: ['day_settleBills','day_profits']
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
