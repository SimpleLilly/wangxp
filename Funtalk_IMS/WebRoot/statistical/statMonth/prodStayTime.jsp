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
	
	//Ext.Msg.alert('提示','records.lenght---->'+records[0].get('stat_date')+'---backstatus--->');	 //records[0].data.stat_date =records[0].get('stat_date')

	var data=[];
	var prodIds=[];
	var diffMonths=[];
	var prodMonDatas=[];
	var colArray=[];
    var mySeries = [];    
    var hashMap = new Ext.util.HashMap();
    var totalMap = new Ext.util.HashMap();
        
    
    prodIds.push('diffMonths');
    
    for (i = 0 ; i < records.length; i++) {
	   	
        if(prodIds.lastIndexOf(records[i].get('product_id'))==-1){
        	
        	prodIds.push(records[i].get('product_id'));       	
        	hashMap.add(records[i].get('product_id'),records[i].get('product_name'));
        	
         }
        
        if(diffMonths.lastIndexOf(records[i].get('diff_months'))==-1){
        	diffMonths.push(records[i].get('diff_months'));
         }
        
       }
     
     
     for(m=0;m<diffMonths.length;m++){
    	 
    	 prodMonDatas.push({diffMonths:diffMonths[m].toString()});	 
  	 
     }

     
   // 通过修改原先的store3来初始化数据是行不通的，改为如下新建对象的方式问题解决；    store3.fields=prodIds;  store3.loadData(prodCycDatas); 
    var store4 = Ext.create('Ext.data.JsonStore', { fields: prodIds, data:prodMonDatas }); 
     
   // Ext.Msg.alert('提示','cycleid.length---->'+store3.getCount()+'-----------ele---->'+store3.getAt(0).get('cycleId'));	
   
    /* store3.each(function(record) {   Ext.Msg.alert('提示','cycleid.length---->'+store3.getCount()+'-----------ele---->'+record.get('cycleId'));	   });  */
     
/*    for(m=0;m<cycleIds.length;m++){    
     for(n=0;n<prodIds.length;n++){ 	 
	        store3.getAt(m).set(prodIds[n].toString(),0);      
 	     }
   } */
   
  //// Ext.Msg.alert('提示','records------length--------->'+store1.getGroups()[2].children[0].get('mon_bill')); // [0].get(prodIds[1].toString())

    var  arrProd;
    var  totalNums=0;   ///  必须初始化赋值，否则第一个产品的汇总为 NAN，展示会有问题；
    
    store1.group('product_id');
  
    for(j=0;j<store1.getGroups().length;j++){

    	    	 
    	diffMonths.forEach(function(eleDiffMonths, idx, arr){
    		 
    		     arrProd =store1.getGroups()[j].children.filter(function(element){
    		
    			 return (element.get('diff_months')==eleDiffMonths);
    		 });	 
    		 					      	     
    	     if(arrProd.length>0){
    	    	 store4.findRecord('diffMonths',eleDiffMonths).set(store1.getGroups()[j].name, arrProd[0].get('diff_nums'));
    	    	 
    	    	 totalNums+=arrProd[0].get('diff_nums');
    	    	     	
    	     }else{
    	    	 store4.findRecord('diffMonths',eleDiffMonths).set(store1.getGroups()[j].name,0);
    	     }
    		    
    	 });
    	
    	
     totalMap.add(store1.getGroups()[j].name,totalNums);  	
     totalNums=0;
  	 
     mySeries.push(
     {type: 'line', 
      axis: 'left',
      highlight: {  size: 3,  radius: 3 },  
      markerConfig: { type: 'circle', size: 3, radius: 3,'stroke-width': 0 },
      xField: 'diffMonths', 
      yField: store1.getGroups()[j].name,
      title: hashMap.get(store1.getGroups()[j].name),
      tips: {trackMouse: true,  width: 90,height: 20,
    	       renderer: function(storeItem, item) {this.setTitle(storeItem.get('diffMonths') + ' : ' + item.value[1]+'%');
    	     }} }
       ); 
     }
    
    
    var records = [];   
    store4.each(function(r){ records.push(r.copy()); }); 
     
    var store5 = Ext.create('Ext.data.JsonStore', { fields: prodIds, data:records });
    
    store5.filterBy(function(record){
    	
    	return (record.get('diffMonths')!='-9');
    
    }); 
    
     ///store5中需要展示的数据按%展示
    store5.each(function(record){
    	
    for(l=0;l<record.fields.length;l++){
    	
    	if(record.fields.getAt(l).name!='diffMonths' &&  record.fields.getAt(l).name!='id'){
    		
        record.set(record.fields.getAt(l).name, Ext.util.Format.round(record.get(record.fields.getAt(l).name)*100/totalMap.get(record.fields.getAt(l).name),2) );
        
    	}
     }   	
            
   });
 	
  var  barChart = Ext.create('Ext.chart.Chart', {       
    	 flex: 1,
    	 shadow: true,
    	 animate: true,
    	 //theme: 'Category5',
    	 legend: { position: 'bottom'},
    	 store: store5, 
    	 axes: [{ type: 'Numeric', position: 'left', fields: prodIds,minimum: 0, grid: { odd: { opacity: 1, fill: '#ddd', stroke: '#bbb','stroke-width': 0.5}}, 
    	                           label: {renderer: Ext.util.Format.numberRenderer('0','0')}  }, //,hidden: true
    	        {type: 'Category', position: 'bottom',fields: ['diffMonths'], font: '10px Arial',  rotate: { degrees: 0 } }],
    	         
    	 series: mySeries  
    	 });
                          
        Ext.getCmp('barChart').add(barChart);//将chart添加到panel中 测了好久，最后通过设置ID解决问题；        
        
        
        colArray.push({ header: '在网时长(月份)', width:110,align: 'center', dataIndex:'diffMonths',locked:true });
               
       ///  添加监控
       hashMap.each(function(key,value){
    	   
    	   
    	colArray.push({header:value,width:90,align: 'right',dataIndex:key});
        	        
        for(m=0;m<barChart.series.length;m++){
        		
        		if(barChart.series.get(m).yField==key){
        			
        		  barChart.series.get(m).addListener('itemmouseup',function(item){	    	    		   
           		   var stattype='huanbi';      		   	
           		   var diffMonths=item.storeItem.get('diffMonths');
           		   var statTime =Ext.util.Format.date(Ext.getCmp('stat_time').value,'Ym')
  
           		   	 cityColumnChart.series.items[0].title=['离网'+diffMonths+'个月地市'];
           		     cityStore.removeAll(); 
           	    	 cityStore.proxy.extraParams.query='stat_time='+statTime+'&stat_type='+stattype+'&stat_id='+key+'&stat_diffmons='+diffMonths;
           	    	 cityStore.load(); 		
           	    	 
           	    	 win.setTitle(value+'离网'+diffMonths+'个月地市统计');    //win.title 不能变换标题，win.setTtile() 可以，为什么？
           	    	 win.show();
           	    	 
                  }); 
        		}
             }      
        });

    
    var recstr1='{diffMonths:\'<span style="color:red;font-weight:bold">汇总</span>\',';
    
        totalMap.each(function(key,value){
        	
     	   recstr1+=(key+':'+value+',');
        });
      
      recstr1=recstr1.substr(0, recstr1.lastIndexOf(','))+'}';
      
      store4.add(Ext.decode(recstr1));   //字符串对象必须经过decode才能正常填充到store中
      ///Ext.Msg.alert('提示','cols4------length-------------'+store4.getAt(store4.getCount()-1).get('diffMonths')); 
    
      
      var recstr2='{diffMonths:\'<span style="color:green;font-weight:bold">在网占比</span>\',';
      
      totalMap.each(function(key,value){
      	
    	   recstr2+=(key+':\''+Ext.util.Format.round((store4.getAt(store4.find('diffMonths','-9')).get(key))*100/value,2)+'%\',');  //  因为此处加了%,所以要加单引号；
       });
      
      recstr2=recstr2.substr(0, recstr2.lastIndexOf(','))+'}';
      
      store4.add(Ext.decode(recstr2)); 
      
      
      var recstr3='{diffMonths:\'<span style="color:red;font-weight:bold">离网占比</span>\',';
      
      totalMap.each(function(key,value){
      	
    	   recstr3+=(key+':\''+Ext.util.Format.round((1-(store4.getAt(store4.find('diffMonths','-9')).get(key))/value)*100,2)+'%\',');  //  因为此处加了%,所以要加单引号；
       });
      
      recstr3=recstr3.substr(0, recstr3.lastIndexOf(','))+'}';
      
      store4.add(Ext.decode(recstr3)); 
      
      
      store4.getAt(store4.find('diffMonths','-9')).set('diffMonths','仍然在网');   // 位置很重要，不要放到store5的前面，否则会导致store5的数据混乱

    
    var gridPanel = new Ext.grid.Panel({
        
        title: '明细数据',
        flex: 0.7, 
        split:true,
        autoheight:true, 
        autowidth:true,
        margins:'0 0 0 0',
        cmargins:'0 5 5 5',    
        store: store4,
        columnLines: true,
        columns:colArray    /// 这是一个数组！！！！！！！！！！
        
    });

    
    gridPanel.getStore().commitChanges(); /// 去掉grid中左上角的红色修改 标志
   
    Ext.getCmp('gridPal1').add(gridPanel);
    
    Ext.MessageBox.hide();

}


Ext.define('Task', {
    extend: 'Ext.data.Model',
   /// idProperty: 'product_id',
    fields: [ {name: 'product_id', type: 'string'},
              {name: 'product_name', type: 'string'},
              {name: 'diff_months', type: 'int'},           
              {name: 'diff_nums', type:'int'}
        ///   {name: 'due', type: 'date', dateFormat:'m/d/Y'}
    ]
});



var  query1='stat_time='+Ext.util.Format.date(preMonth,'Ym')+'&stat_type=huanbi&stat_id=1';

var store1 = Ext.create( Ext.data.Store,{
    model:'Task',
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlMonProUserStayMonData.jspa',
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
    
    

var gridForm = Ext.create('Ext.form.Panel', {

title: '产品周期走势',     
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
                       	{xtype: 'tbspacer',height: 30, width:90},
                        {fieldLabel: '统计月份',name: 'stat_time',id:'stat_time',value: preMonth, format:'Y-m', xtype:'datefield' },
                       	{xtype: 'tbspacer',height: 30, width:90},
                    	{text: '查 询',xtype:'button', id:'qrybot',name:'qrybot', width:120,align:'center',handler:function(){  querydata();  }}
                    	 	]
                    } ]
          }, { flex: 0.7,
    	      layout: {type: 'vbox', align:'stretch'}, 
    	      id:'gridPal1',            
              items: []   }]
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
              {name: 'stat_nums', type: 'int'}],
   /// sorters: { property: 'yesday_bills', direction: 'ASC' },  //展示之前排序
    proxy: {
        type: 'ajax',
        url: '<%=path%>/statistics/SqlMonProUserStayMonData.jspa',
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
        fields: ['stat_nums'],
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
        yField: ['stat_nums']
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
