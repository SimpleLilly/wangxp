//------首页中部效果
$(function() {
	var z_index = 0;
	
	$(".box").hover(function(){
		var box_width = $(".box").outerWidth();
		z_index = $(this).css("zIndex");
		$(this).css({zIndex:6});
		$(this).animate({width:box_width,height:"355px"},300);
	},function(){
		var box_width = $(".box").outerWidth();
		$(this).css({zIndex:z_index});
		$(this).animate({width:box_width,height:"350px"},300);
	});
});
//--------------二级导航下拉效果  		
$(function() {
	$("#nav ul li").hover(function(){
		$(this).children(".nav_1").css({backgroundColor:"#838383"});
		$(this).children(".second-nav").fadeIn(300);
	},function(){
		$(this).children(".nav_1").css({backgroundColor:"#ff9900"});
		$(this).children(".second-nav").fadeOut(300);
	});
});

//-----------首页幻灯片效果
$(function() {
    var bannerSlider = new Slider($('#banner_tabs'), {
        time: 5000,
        delay: 400,
        event: 'hover',
        auto: true,
        mode: 'fade',
        controller: $('#bannerCtrl'),
        activeControllerCls: 'active'
    });
    $('#banner_tabs .flex-prev').click(function() {
        bannerSlider.prev()
    });
    $('#banner_tabs .flex-next').click(function() {
        bannerSlider.next();
    });
});
//----------初始化页面某些元素样式
$(function() {
   		$("#left-nav > ul > li:last-child > a").css({borderBottom:"none"});
   		$(".search > div").css({float:"left"});
   	});
window.onresize = function(){
	var container_width = $(".container").outerWidth();
	if(container_width==1200){
		$(".box").css("width","400px");
	}else if(container_width==960){
		$(".box").css("width","320px");
	}
}

//首页顶部显示大图二维码扫码
$(function() {
	$(".QR-codes,.wechat").mouseover(
		function(){
			$(".wechat").show();
		}).mouseout(
		function(){
			$(".wechat").hide()
		});
});