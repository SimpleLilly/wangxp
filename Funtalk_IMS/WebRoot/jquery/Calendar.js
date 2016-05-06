/**
 *使用方法：
 * (1)只选择日期   <input type="text" name="date"   readOnly onClick="setDay(this);">
 * (2)选择日期和小时  <input type="text" name="dateh"  readOnly onClick="setDayH(this);">
 * (3)选择日期和小时及分钟 <input type="text" name="datehm" readOnly onClick="setDayHM(this);">
 * (4)选择日期和小时及分钟,秒 <input type="text" name="datehm" readOnly onClick="setDayHMS(this);">
 *设置参数的方法
 * (1)设置日期分隔符    setDateSplit(strSplit);默认为""，setDateSplit('-')
 * (2)设置日期与时间之间的分隔符  setDateTimeSplit(strSplit);默认为" "
 * (3)设置时间分隔符    setTimeSplit(strSplit);默认为":"
 * (4)设置(1),(2),(3)中的分隔符  setSplit(strDateSplit,strDateTimeSplit,strTimeSplit);
 * (5)设置开始和结束年份    setYearPeriod(intDateBeg,intDateEnd)
 *说明：
 */
//------------------ 样式定义 ---------------------------//
//功能按钮同样样式
var s_turn_base = "height:20px;font-size:12px;color:white;border:0 solid #CCCCCC;cursor:hand;background-color:#2650A6;";
//翻年、月等的按钮
var s_turn = "width:28px;" + s_turn_base;
//关闭、清空等按钮样式
var s_turn2 = "width:22px;" + s_turn_base;
//年选择下拉框
var s_select = "width:64px;display:none;";
//月、时、分选择下拉框
var s_select2 = "width:50px;display:none;";
//日期选择控件体的样式
var s_body = "width:200;background-color:#2650A6;display:none;z-index:9998;position:absolute;" +
  "border-left:1 solid #CCCCCC;border-top:1 solid #CCCCCC;border-right:1 solid #999999;border-bottom:1 solid #999999;";
//显示日的td的样式
var s_day = "width:28px;height:22px;background-color:#D8F0FC;font-size:12px";
//字体样式
var s_font = "color:#FFCC00;font-size:12px;cursor:hand;";
//链接的样式
var s_link = "text-decoration:none;font-size:12px;color:#2650A6;";
//横线
var s_line = "border-bottom:1 solid #6699CC";
//------------------ 变量定义 ---------------------------//
var YearSt = 2002;//可选择的开始年份
var YearEnd = 2010;//可选择的结束年份
var DateNow = new Date();//选择时设值
var Year = DateNow.getFullYear(); //定义年的变量的初始值
var Month = DateNow.getMonth()+1; //定义月的变量的初始值
var Day = DateNow.getDate();
var Hour = DateNow.getHours();
var Minute = DateNow.getMinutes();
var Second = DateNow.getSeconds(); //************************定义秒的变量的初始值
var ArrDay=new Array(42);          //定义写日期的数组
var DateSplit = "";     //日期的分隔符号
var DateTimeSplit = " ";    //日期与时间之间的分隔符
var TimeSplit = ":";     //时间的分隔符号
var OutObject;      //接收日期时间的对象
var arrHide = new Array();//被强制隐藏的标签
var m_bolShowHour = false;//是否显示小时
var m_bolShowMinute = false;//是否显示分钟
var m_bolShowSecond = false;//***********************是否显示秒
var m_aMonHead = new Array(12);         //定义阳历中每个月的最大天数
    m_aMonHead[0] = 31; m_aMonHead[1] = 28; m_aMonHead[2] = 31; m_aMonHead[3] = 30; m_aMonHead[4]  = 31; m_aMonHead[5]  = 30;
    m_aMonHead[6] = 31; m_aMonHead[7] = 31; m_aMonHead[8] = 30; m_aMonHead[9] = 31; m_aMonHead[10] = 30; m_aMonHead[11] = 31;

// ---------------------- 用户可调用的函数 -----------------------------//
//用户主调函数－只选择日期
function setDay(obj){
 DateNow = new Date();
 var myobj = document.getElementById("divDate");
 myobj.innerHTML = strHtml;
 OutObject = obj;
 //如果标签中有值，则将日期初始化为当前值
 var strValue = myTrim(OutObject.value);
 if( strValue != "" ){
  InitDate(strValue);
 }
 PopCalendar();
}
//用户主调函数－选择日期和小时
function setDayH(obj){
 DateNow = new Date();
 var myobj = document.getElementById("divDate");
 myobj.innerHTML = strHtml;
    OutObject = obj;
    m_bolShowHour = true;
    //如果标签中有值，则将日期和小时及分钟
    if( OutObject.value != "" ){
		if(DateSplit.length==1)
		{
			InitDate(OutObject.value.substring(0,10)); //***************0-10为日期
			var hour = OutObject.value.substring(11,13); //********************11-13为小时,14-16为分钟
			if( Hour < 10 ) Hour = hour.substring(1,2);
		}else{
			InitDate(OutObject.value.substring(0,8)); //***************0-8为日期
			var hour = OutObject.value.substring(9,11); //********************11-13为小时,
  			if( hour < 10 ) Hour = hour.substring(1,2);
		}

    }
    PopCalendar();
}
//用户主调函数－选择日期和小时及分钟
function setDayHM(obj){
 var myobj = document.getElementById("divDate");
 myobj.innerHTML = strHtml;
    OutObject = obj;
    m_bolShowHour = true;
    m_bolShowMinute = true;
    //如果标签中有值，则将日期和小时及分钟
    if( OutObject.value != "" &&(OutObject.value.length==16 || OutObject.value.length==14)){
		if(DateSplit.length==1)
		{
			InitDate(OutObject.value.substring(0,10)); //***************0-10为日期
			var time = OutObject.value.substring(11,16); //********************11-13为小时,14-16为分钟
			var arr = time.split(TimeSplit);
			Hour = arr[0];
			Minute = arr[1];
			if( Hour < 10 ) Hour = Hour.substring(1,2);
			if( Minute < 10 ) Minute = Minute.substring(1,2);
		}else{
			InitDate(OutObject.value.substring(0,8)); //***************0-8为日期
			var time = OutObject.value.substring(9,14); //********************11-13为小时,14-16为分钟,17-19为秒??
			var arr = time.split(TimeSplit);
			Hour = arr[0];
			Minute = arr[1];
			if( Hour < 10 ) Hour = Hour.substring(1,2);
			if( Minute < 10 ) Minute = Minute.substring(1,2);
		}

    }else{
		DateNow = new Date();
		Year = DateNow.getFullYear(); //定义年的变量的初始值
		Month = DateNow.getMonth()+1; //定义月的变量的初始值
		Day = DateNow.getDate();
		Hour = DateNow.getHours();
		Minute = DateNow.getMinutes();
		Second = DateNow.getSeconds(); //************************定义秒的变量的初始值
	}
    PopCalendar();
}
//用户主调函数－选择日期和小时及分钟，秒
function setDayHMS(obj){
 var myobj = document.getElementById("divDate");
 myobj.innerHTML = strHtml;
    OutObject = obj;
    m_bolShowHour = true;
    m_bolShowMinute = true;
    m_bolShowSecond = true;//*************************************
    //如果标签中有值，则将日期和小时及分钟********************秒初始化为当前值
    if( OutObject.value != ""  &&(OutObject.value.length==19 || OutObject.value.length==17)){
		if(DateSplit.length==1)
		{
			InitDate(OutObject.value.substring(0,10)); //***************0-10为日期
			var time = OutObject.value.substring(11,19); //********************11-13为小时,14-16为分钟,17-19为秒??
			var arr = time.split(TimeSplit);
			Hour = arr[0];
			Minute = arr[1];
			Second = arr[2];//***************************
			if( Hour < 10 ) Hour = Hour.substring(1,2);
			if( Minute < 10 ) Minute = Minute.substring(1,2);
			if( Second < 10 ) Second = Second.substring(1,2); //*********************************
		}else{
			InitDate(OutObject.value.substring(0,8)); //***************0-10为日期
			var time = OutObject.value.substring(9,17); //********************11-13为小时,14-16为分钟,17-19为秒??
			var arr = time.split(TimeSplit);
			Hour = arr[0];
			Minute = arr[1];
			Second = arr[2];//***************************
			if( Hour < 10 ) Hour = Hour.substring(1,2);
			if( Minute < 10 ) Minute = Minute.substring(1,2);
			if( Second < 10 ) Second = Second.substring(1,2); //*********************************
		}

    }else{
		DateNow = new Date();
		Year = DateNow.getFullYear(); //定义年的变量的初始值
		Month = DateNow.getMonth()+1; //定义月的变量的初始值
		Day = DateNow.getDate();
		Hour = DateNow.getHours();
		Minute = DateNow.getMinutes();
		Second = DateNow.getSeconds(); //************************定义秒的变量的初始值
	}
    PopCalendar();
}
//设置开始日期和结束日期
function  setYearPeriod(intDateBeg,intDateEnd){
 YearSt = intDateBeg;
 YearEnd = intDateEnd;
}
//设置日期分隔符。默认为"-"
function setDateSplit(strDateSplit){
 DateSplit = strDateSplit;
}
//设置日期与时间之间的分隔符。默认为" "
function setDateTimeSplit(strDateTimeSplit){
 DateTimeSplit = strDateTimeSplit;
}
//设置时间分隔符。默认为":"
function setTimeSplit(strTimeSplit){
 TimeSplit = strTimeSplit;
}
//设置分隔符
function setSplit(strDateSplit,strDateTimeSplit,strTimeSplit){
 DateSplit(strDateSplit);
 DateTimeSplit(strDateTimeSplit);
 TimeSplit(strTimeSplit);
}
//设置默认的日期。格式为：YYYY-MM-DD
function setDefaultDate(strDate){
 Year = strDate.substring(0,4);
 Month = strDate.substring(5,7);
 Day = strDate.substring(8,10);
}
//设置默认的时间。格式为：HH24:MI
function setDefaultTime(strTime){
 Hour = strTime.substring(0,2);
 Minute = strTime.substring(3,5);
}
// ---------------------- end 用户可调用的函数 -----------------------------//
//------------------ begin 页面显示部分 ---------------------------//

	var weekName = new Array("日","一","二","三","四","五","六");
document.write('<div id="divDate" style="'+s_body+'">');
document.write('</div>');
var strHtml = "";
strHtml+='<div align="center" id="divDateText" style="padding-top:2px;height:30px;font-size:12px;" Author="">';
strHtml+='<span id="YearHead"  Author="" style="'+s_font+'" '+
    'onclick="spanYearCEvent();">&nbsp;年</span>';
strHtml+='<select id="selYear"  Author="" style="'+s_select+'"  '+
    ' onChange="Year=this.value;SetDay(Year,Month);document.all.YearHead.style.display=\'\';'+
    'this.style.display=\'none\';">';
for(var i=YearSt;i <= YearEnd;i ++){
 strHtml+='<option value="' + i + '">' + i + '年</option>';
}
strHtml+='</select>';
strHtml+='<span id="MonthHead" Author="" style="'+s_font+'" '+
    'onclick="spanMonthCEvent();">&nbsp;&nbsp;月</span>';
strHtml+='<select id="selMonth" Author="" style="'+s_select2+'" '+
    'onChange="Month=this.value;SetDay(Year,Month);document.all.MonthHead.style.display=\'\';'+
    'this.style.display=\'none\';">';
for(var i=1;i <= 12;i ++){
 strHtml+='<option value="' + i + '">' + i + '月</option>';
}
strHtml+='</select>';

strHtml+='<span id="HourHead" Author="" style="'+s_font+'display:none;" '+
    'onclick="spanHourCEvent();">&nbsp;时</span>';
strHtml+='<select id="selHour" Author="" style="'+s_select2+'display:none;" '+
    ' onChange="Hour=this.value;SetDay(Year,Month);document.all.HourHead.style.display=\'\';' +
    'this.style.display=\'none\';">';
for(var i=0;i <= 23;i ++){
 strHtml+='<option value="' + i + '">' + i + '时</option>';
}
strHtml+='</select>';
strHtml+='<span id="MinuteHead" Author="" style="'+s_font+'display:none;" '+
    'onclick="spanMinuteCEvent();">&nbsp;&nbsp;分</span>';
strHtml+='<select id="selMinute" Author="" style="'+s_select2+'display:none;" '+
    '  onChange="Minute=this.value;SetDay(Year,Month);document.all.MinuteHead.style.display=\'\';'+
    'this.style.display=\'none\';">';
//*************************修改页面分的显示
for(var i=0;i <= 59;i ++){
    strHtml+='<option value="' + i + '">' + i + '分</option>';
}
strHtml+='</select>';
strHtml+='<span id="SecondHead" Author="" style="'+s_font+'display:none;" '+
                'onclick="spanSecondCEvent();">&nbsp;&nbsp;秒</span>';
strHtml+='<select id="selSecond" style="'+s_select2+'display:none;" Author="" '+
                '  onChange="Second=this.value;SetDay(Year,Month);document.all.SecondHead.style.display=\'\';'+
                'this.style.display=\'none\';"">'


//*************************************页面加秒的显示
for(var i=0;i <= 59;i ++){
    strHtml+='<option value="' + i + '">' + i + '秒</option>';
}

strHtml+='</select>';
strHtml+='</div>';
//输出一条横线
strHtml+='<div style="'+s_line+'"></div>';
strHtml+='<div align="center" id="divTurn" style="border:0;" Author="">';
strHtml+='<input type="button" style="'+s_turn+'" value="年↑" title="上一年" onClick="PrevYear();">';
strHtml+='<input type="button" style="'+s_turn+'" value="年↓" title="下一年" onClick="NextYear();">&nbsp;';
strHtml+='<input type="button" style="'+s_turn+'" value="月↑" title="上一月" onClick="PrevMonth();">';
strHtml+='<input type="button" style="'+s_turn+'" value="月↓" title="下一月" onClick="NextMonth();">';
strHtml+='</div>';
//输出一条横线
strHtml+='<div style="'+s_line+'"></div>';
strHtml+='<table border=0 cellspacing=0 cellpadding=0  bgcolor=white onselectstart="return false">';
strHtml+=' <tr style="background-color:#2650A6;font-size:12px;color:white;height:22px;">';
for(var i =0;i < weekName.length;i ++){
 //输出星期
strHtml+='<td width="28" align="center">' + weekName[i] + '</td>';
}
strHtml+=' </tr>';
strHtml+='</table>';
//输出天的选择
strHtml+='<table border=0 cellspacing=1 cellpadding=0  bgcolor=white onselectstart="return false">';
var n = 0;
for (var i=0;i<5;i++) { 
 strHtml+=' <tr align=center id="trDay' + i + '" >';
 for (var j=0;j<7;j++){
  strHtml+='<td align="center" id="tdDay' + n + '" '+
    'onClick="Day=this.innerText;SetDay(Year,Month)" ondblclick="SetValue(true);"' 
   +' style="' + s_day + '">&nbsp;</td>';
  n ++;
 }
 strHtml+=' </tr>';
}
strHtml+=' <tr align=center id="trDay5" >';
strHtml+='<td align="center" id="tdDay35" onClick="Day=this.innerText;SetDay(Year,Month);" ' 
    +' style="' + s_day + '">&nbsp;</td>';
strHtml+='<td align="center" id="tdDay36" onClick="Day=this.innerText;SetDay(Year,Month);" ' 
    +' style="' + s_day + '">&nbsp;</td>';
strHtml+='<td align="right" colspan="5"><a href="javascript:Clear();" style="' + s_link + '">清空</a>'+
    '&nbsp;<a href="javascript:HideControl();" style="' + s_link + '">关闭</a>' +
    '&nbsp;<a href="javascript:SetValue(true);" style="' + s_link + '">确定</a>&nbsp;' +
    '</td>';
strHtml+=' </tr>';
strHtml+='</table>';

//------------------ end 页面显示部分 ---------------------------//
//------------------ 显示日期时间的span标签响应事件 ---------------------------//
//单击年份span标签响应
function spanYearCEvent(){
    hideElementsById(new Array("selYear","MonthHead"),false);
    if(m_bolShowHour)    hideElementsById(new Array("HourHead"),false);
    if(m_bolShowMinute)    hideElementsById(new Array("MinuteHead"),false);
 if(m_bolShowSecond)    hideElementsById(new Array("SecondHead"),false);
    hideElementsById(new Array("YearHead","selMonth","selHour","selMinute","selSecond"),true);
}
//单击月份span标签响应
function spanMonthCEvent(){
    hideElementsById(new Array("selMonth","YearHead"),false);
    if(m_bolShowHour)    hideElementsById(new Array("HourHead"),false);
    if(m_bolShowMinute)    hideElementsById(new Array("MinuteHead"),false);
 if(m_bolShowSecond)    hideElementsById(new Array("SecondHead"),false);
    hideElementsById(new Array("MonthHead","selYear","selHour","selMinute","selSecond"),true);
}
//单击小时span标签响应
function spanHourCEvent(){
    hideElementsById(new Array("YearHead","MonthHead"),false);
    if(m_bolShowHour)    hideElementsById(new Array("selHour"),false);
    if(m_bolShowMinute)    hideElementsById(new Array("MinuteHead"),false);
 if(m_bolShowSecond)    hideElementsById(new Array("SecondHead"),false);
    hideElementsById(new Array("HourHead","selYear","selMonth","selMinute","selSecond"),true);
}
//单击分钟span标签响应
function spanMinuteCEvent(){
    hideElementsById(new Array("YearHead","MonthHead"),false);
    if(m_bolShowHour)    hideElementsById(new Array("HourHead"),false);
    if(m_bolShowMinute)    hideElementsById(new Array("selMinute"),false);
 if(m_bolShowSecond)    hideElementsById(new Array("SecondHead"),false);
    hideElementsById(new Array("MinuteHead","selYear","selMonth","selHour","selSecond"),true);
}
//*****************************单击秒span标签响应
function spanSecondCEvent(){
    hideElementsById(new Array("YearHead","MonthHead"),false);
    if(m_bolShowHour)    hideElementsById(new Array("HourHead"),false);
    if(m_bolShowMinute)    hideElementsById(new Array("MinuteHead"),false);
 if(m_bolShowSecond)    hideElementsById(new Array("selSecond"),false);
    hideElementsById(new Array("SecondHead","selYear","selMonth","selHour","selMinute"),true);
}
//根据标签id隐藏或显示标签
function hideElementsById(arrId,bolHide){
 var strDisplay = "";
 if(bolHide) strDisplay = "none";
 for(var i = 0;i < arrId.length;i ++){
  var obj = document.getElementById(arrId[i]);
  obj.style.display = strDisplay;
 }
}
//------------------ end 显示日期时间的span标签响应事件 ---------------------------//
//判断某年是否为闰年
function isPinYear(year){
 var bolRet = false;
 if (0==year%4&&((year%100!=0)||(year%400==0))) {
  bolRet = true;
 }
 return bolRet;
}
//得到一个月的天数，闰年为29天
function getMonthCount(year,month){
 var c=m_aMonHead[month-1];
 if((month==2)&&isPinYear(year)) c++;
 return c;
}
//重新设置当前的日。主要是防止在翻年、翻月时，当前日大于当月的最大日
function setRealDayCount() {
 if( Day > getMonthCount(Year,Month) ) {
  //如果当前的日大于当月的最大日，则取当月最大日
  Day = getMonthCount(Year,Month);
 }
}
//在个位数前加零
function addZero(value){
 if(value < 10 ){
  value = "0" + value;
 }
 return value;
}
//取出空格
function myTrim(str) {
 str=str.replace(/(^\s*)|(\s*$)/g,"");
 return str; 
}
//为select创建一个option
function createOption(objSelect,value,text){
 var option = document.createElement("OPTION");
 option.value = value;
 option.text = text;
 objSelect.options.add(option);
}
//往前翻 Year
function PrevYear() {
 if(Year > 999 && Year <10000){Year--;}
 else{alert("年份超出范围（1000-9999）！");}
 SetDay(Year,Month);
 //如果年份小于允许的最小年份，则创建对应的option
 if( Year < YearSt ) {
  YearSt = Year;
  createOption(document.all.selYear,Year,Year + "年");
 }
 checkSelect(document.all.selYear,Year);
 WriteHead();
}
//往后翻 Year
function NextYear() {
 if(Year > 999 && Year <10000){Year++;}
 else{alert("年份超出范围（1000-9999）！");return;}
 SetDay(Year,Month);
 //如果年份超过允许的最大年份，则创建对应的option
 if( Year > YearEnd ) {
  YearEnd = Year;
  createOption(document.all.selYear,Year,Year + "年");
 }
 checkSelect(document.all.selYear,Year);
 WriteHead();
}
//选择今天
function Today() {
 Year = DateNow.getFullYear();
 Month = DateNow.getMonth()+1;
 Day = DateNow.getDate();
 SetValue(true);
 //SetDay(Year,Month);
 //selectObject();
}
//往前翻月份
function PrevMonth() {
 if(Month>1){Month--}else{Year--;Month=12;}
 SetDay(Year,Month);
 checkSelect(document.all.selMonth,Month);
 WriteHead();
}
//往后翻月份
function NextMonth() {
 if(Month==12){Year++;Month=1}else{Month++}
 SetDay(Year,Month);
 checkSelect(document.all.selMonth,Month);
 WriteHead();
}
//向span标签中写入年、月、时、分\**************************秒等数据
function WriteHead(){
    document.all.YearHead.innerText = Year + "年";
    document.all.MonthHead.innerText = Month + "月";
    if( m_bolShowHour )        document.all.HourHead.innerText = " "+Hour + "时";
    if( m_bolShowMinute )    document.all.MinuteHead.innerText = Minute + "分";
    if( m_bolShowSecond )    document.all.SecondHead.innerText = Second + "秒";
}
//设置显示天
function SetDay(yy,mm) {
 setRealDayCount();//设置当月真实的日
 WriteHead();
 var strDateFont1 = "", strDateFont2 = "" //处理日期显示的风格
 for (var i = 0; i < 37; i++){ArrDay[i]=""};  //将显示框的内容全部清空
 var day1 = 1;
 var firstday = new Date(yy,mm-1,1).getDay();  //某月第一天的星期几
 for (var i = firstday; day1 < getMonthCount(yy,mm)+1; i++){
  ArrDay[i]=day1;day1++;
 }
 //如果用于显示日的最后一行的第一个单元格的值为空，则隐藏整行。
 //if(ArrDay[35] == ""){
 // document.all.trDay5.style.display = "none";
 //} else {
 // document.all.trDay5.style.display = "";
 //}
 for (var i = 0; i < 37; i++){ 
  var da = eval("document.all.tdDay"+i)     //书写新的一个月的日期星期排列
  if (ArrDay[i]!="") { 
   //判断是否为周末，如果是周末，则改为红色字体
   if(i % 7 == 0 || (i+1) % 7 == 0){
   strDateFont1 = "<font color=#f0000>"
   strDateFont2 = "</font>"
   } else {
    strDateFont1 = "";
    strDateFont2 = ""
   }
   da.innerHTML = strDateFont1 + ArrDay[i] + strDateFont2;
   //如果是当前选择的天，则改变颜色
   if(ArrDay[i] == Day ) {
    da.style.backgroundColor = "#CCCCCC";
   } else {
    da.style.backgroundColor = "#EFEFEF";
   }
   da.style.cursor="hand"
  } else {
   da.innerHTML="";da.style.backgroundColor="";da.style.cursor="default"
  }
 }//end for
 SetValue(false);//给文本框赋值，但不隐藏本控件
}//end function SetDay
//根据option的值选中option
function checkSelect(objSelect,selectValue) {
 var count = parseInt(objSelect.length);
 if( selectValue < 10 && selectValue.toString().length == 2) {
  selectValue = selectValue.substring(1,2);
 }
 for(var i = 0;i < count;i ++){
  if(objSelect.options[i].value == selectValue){
   objSelect.selectedIndex = i;
   break;
  }
 }//for
}
//选中年、月、时、分等下拉框
function selectObject(){
 //如果年份小于允许的最小年份，则创建对应的option
 if( Year < YearSt ) {
  for( var i = Year;i < YearSt;i ++  ){
   createOption(document.all.selYear,i,i + "年");
  }
  YearSt = Year;
 }
 //如果年份超过允许的最大年份，则创建对应的option
 if( Year > YearEnd ) {
  for( var i = YearEnd+1;i <= Year;i ++  ){
   createOption(document.all.selYear,i,i + "年");
  }
  YearEnd = Year;
 }
 checkSelect(document.all.selYear,Year);
 checkSelect(document.all.selMonth,Month);
 if( m_bolShowHour )  checkSelect(document.all.selHour,Hour);
 if( m_bolShowMinute ) checkSelect(document.all.selMinute,Minute);
 if( m_bolShowSecond ) checkSelect(document.all.selSecond,Second);
}
//给接受日期时间的控件赋值
//参数bolHideControl - 是否隐藏控件
function SetValue(bolHideControl){
    var value = "";
    if( !Day || Day == "" ){
        OutObject.value = value;
        return;
    }
    var mm = Month;
    var day = Day;
    if( mm < 10 ) mm = "0" + mm;
    if( day < 10 ) day = "0" + day;
    value = Year + DateSplit + mm + DateSplit + day;
    if( m_bolShowHour ){
        var hour = Hour;
        if( hour < 10 ) hour = "0" + hour;
        value += DateTimeSplit + hour;
    }
    if( m_bolShowMinute ){
        var minute = Minute;
        if( minute < 10 ) minute = "0" + minute;
        value += TimeSplit + minute;
    }
 if( m_bolShowSecond ){
        var second = Second;
        if( second < 10 ) second = "0" + second;
        value += TimeSplit + second;
    }
    OutObject.value = value;
    //document.all.divDate.style.display = "none";
 if( bolHideControl ) {
  HideControl();
 }
}
//是否显示时间
function showTime(){
   if( !m_bolShowHour && m_bolShowMinute && m_bolShowSecond ){
        alert("如果要选择分钟和秒，则必须可以选择小时！");
        return;
    }
 if( m_bolShowHour && !m_bolShowMinute && m_bolShowSecond ){
        alert("如果要选择秒，则必须选择分钟！");
        return;
    }
 if( !m_bolShowHour && !m_bolShowMinute && m_bolShowSecond ){
        alert("如果要选择秒，则必须可以选择小时和分！");
        return;
    }
 //************************添加秒
    hideElementsById(new Array("HourHead","selHour","MinuteHead","selMinute","SecondHead","selSecond"),true);
    if( m_bolShowHour ){
        //显示小时
        hideElementsById(new Array("HourHead"),false);
    }
    if( m_bolShowMinute ){
        //显示分钟
        hideElementsById(new Array("MinuteHead"),false);
    }
  if( m_bolShowSecond ){
        //显示秒
        hideElementsById(new Array("SecondHead"),false);
    }
}
//弹出显示日历选择控件，以让用户选择
function PopCalendar(){
 //隐藏下拉框，显示相对应的head
 //hideElementsById(new Array("selYear","selMonth","selHour","selMinute"),true);
 //hideElementsById(new Array("YearHead","MonthHead","HourHead","MinuteHead"),false);
 SetDay(Year,Month);
 WriteHead();
 showTime();
 var dads  = document.all.divDate.style;
 var iX, iY;
  
 var h = document.all.divDate.offsetHeight;
 var w = document.all.divDate.offsetWidth;
 //计算left
 if (window.event.x + h > document.body.offsetWidth - 10    )
  iX = window.event.x - h - 5 ;
 else
  iX = window.event.x + 5;  
 if (iX <0)  
  iX=0;
 //计算top
 iY = window.event.y;
 if (window.event.y + w > document.body.offsetHeight - 10   )
  iY = document.body.scrollTop + document.body.offsetHeight - w - 5 ;
 else
  iY = document.body.scrollTop +window.event.y + 5;  
 if (iY <0)  
  iY=0;
 dads.left = iX;
 dads.top = iY;
 ShowControl();
 selectObject();
}
//隐藏日历控件(同时显示被强制隐藏的标签)
function HideControl(){
 document.all.divDate.style.display = "none";
 ShowObject();
 arrHide = new Array();//将被隐藏的标签对象清空
 var obj = document.getElementById("divDate");
 obj.innerHtml="";
}
//显示日历控件(同时隐藏会遮挡的标签)
function ShowControl(){
 document.all.divDate.style.display = "";
 HideObject("SELECT");
 HideObject("OBJECT");
}
//根据标签名称隐藏标签。如会遮住控件的select，object
function HideObject(strTagName) {
 
 x = document.all.divDate.offsetLeft;
 y = document.all.divDate.offsetTop;
 h = document.all.divDate.offsetHeight;
 w = document.all.divDate.offsetWidth;
 
 for (var i = 0; i < document.all.tags(strTagName).length; i++)
 {
  
  var obj = document.all.tags(strTagName)[i];
  if (! obj || ! obj.offsetParent)
   continue;
  // 获取元素对于BODY标记的相对坐标
  var objLeft   = obj.offsetLeft;
  var objTop    = obj.offsetTop;
  var objHeight = obj.offsetHeight;
  var objWidth = obj.offsetWidth;
  var objParent = obj.offsetParent;
  
  while (objParent.tagName.toUpperCase() != "BODY"){
   objLeft  += objParent.offsetLeft;
   objTop   += objParent.offsetTop;
   objParent = objParent.offsetParent;
  }
  //alert("控件左端:" + x + "select左端" + (objLeft + objWidth) + "控件底部:" + (y+h) + "select高:" + objTop);
  
  var bolHide = true;
  if( obj.style.display == "none" || obj.style.visibility == "hidden" || obj.getAttribute("Author") == "sitech" ){
   //如果标签本身就是隐藏的，则不需要再隐藏。如果是控件中的下拉框，也不用隐藏。
   bolHide = false;
  }
  if(  ( (objLeft + objWidth) > x && (y + h + 20) > objTop && (objTop+objHeight) >  y && objLeft < (x+w) ) && bolHide ){
   //arrHide.push(obj);//记录被隐藏的标签对象
   arrHide[arrHide.length] = obj;
   obj.style.visibility = "hidden";
  }
  
  
 }
}
//显示被隐藏的标签
function ShowObject(){
 for(var i = 0;i < arrHide.length;i ++){
  //alert(arrHide[i]);
  arrHide[i].style.visibility = "";
 }
}
//初始化日期。
function InitDate(strDate){	
 if(DateSplit.length==1){
	 var arr = strDate.split(DateSplit);
	 Year = arr[0];
	 Month = arr[1];
	 Day = arr[2];
	 if(Month<10) Month = Month.substring(1,2);
	 if(Day<10) Day = Day.substring(1,2);
 }else{
	 Year = strDate.substring(0,4);
	 Month = strDate.substring(4,6);
	 Day = strDate.substring(6,9);
	 if(Month<10) Month = Month.substring(1,2);
	 if(Day<10) Day = Day.substring(1,2);
 }
}
//清空
function Clear(){
 OutObject.value = "";
 HideControl();
}
 //任意点击时关闭该控件
//function document.onclick(){ 
//  with(window.event.srcElement){ 
// if (tagName != "INPUT" && getAttribute("Author") != "" )
//    HideControl();
// / }
//}
//按ESC键关闭该控件
 document.onkeypress =function onkeypress(){
 if( event.keyCode == 27 ){
  HideControl();
 }
}