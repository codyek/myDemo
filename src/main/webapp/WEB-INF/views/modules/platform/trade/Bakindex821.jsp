<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<head>
	<meta charset="utf-8" />
	<title>WebSocket中心</title>
	<meta name="decorator" content="default"/>
	<link href="${ctxStatic}/trade/page.css" rel="stylesheet" />
	<link href="${ctxStatic}/echarts/jquery.mloading.css" rel="stylesheet" type="text/css" />
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/echarts/jquery.mloading.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/echarts/echarts.min.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/trade/okex.js" type="text/javascript"></script>
	<script src="${ctxStatic}/trade/mex.js" type="text/javascript"></script>
	<style type="text/css">
	
	</style>

</head>
<body>
<div id="main" class="">
	<div id="content" class="widthAll">
		<div id="entrust_okex" class="floatLeft">
			<div class="widthAll">
				<span>　OKEX委托信息</span>
			</div>
			合约指数：<span id="okexIndex"></span>  连接状态：<span id="okexStatus">待连接</span>
			<ul class="widthAll">
				<li class="titlePart">
					<span class="order"> 　</span>
					<span class="title">出价</span>
					<span class="title">数量</span>
				</li>
			</ul>
			<ul id="sell">
				<li id="depthli00" class="listCont clear"><span id="sell_order5" class="order">卖5</span><span id="okex_sell_price5" class="price">--</span><span id="okex_sell_number5" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="sell_order4" class="order">卖4</span><span id="okex_sell_price4" class="price">--</span><span id="okex_sell_number4" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="sell_order3" class="order">卖3</span><span id="okex_sell_price3" class="price">--</span><span id="okex_sell_number3" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="sell_order2" class="order">卖2</span><span id="okex_sell_price2" class="price">--</span><span id="okex_sell_number2" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="sell_order1" class="order">卖1</span><span id="okex_sell_price1" class="price">--</span><span id="okex_sell_number1" class="number">--</span></li>
			</ul>
			<div id="okex_price" class="widthAll">
				<span> 当前价格  <span id="okexPrice"></span></span>
			</div>
			<ul id="buy" class="widthAll">
				<li id="depthli00" class="listCont clear"><span id="buy_order1" class="order">买1</span><span id="okex_buy_price1" class="price">--</span><span id="okex_buy_number1" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="buy_order2" class="order">买2</span><span id="okex_buy_price2" class="price">--</span><span id="okex_buy_number2" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="buy_order3" class="order">买3</span><span id="okex_buy_price3" class="price">--</span><span id="okex_buy_number3" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="buy_order4" class="order">买4</span><span id="okex_buy_price4" class="price">--</span><span id="okex_buy_number4" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="buy_order5" class="order">买5</span><span id="okex_buy_price5" class="price">--</span><span id="okex_buy_number5" class="number">--</span></li>
			</ul>
		</div>
		<div id="trade" class="floatLeft">
			  查询时间段：<input id="sdate" class="Wdate" style="width: 140px;" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,maxDate:'#F{$dp.$D(\'edate\')}',isShowClear:false})" />  
	    	- <input id="edate" class="Wdate" style="width: 140px;" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,maxDate:'%y-%M-{%d+1}',isShowClear:false})"/>
	    	<input type="button" id="click" value="查询" onclick="getAllData()"/>
	    	<input type="button" id="click" value="计算最大最小差" onclick="totalMaxAndMin()"/>
			<div id="container" style="height: 320px"></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>OKEX与XBTUSD</th>
					<th id="OKEXAndXBTUSD"></th>
				</tr>
				<tr>
					<th>OKEX与XBTU17</th>
					<th id="OKEXAndXBTU17"></th>
				</tr>
				<tr>
					<th>XBTUSD与XBTU17</th>
					<th id="XBTUSDAndXBTU17"></th>
				</tr>
			</thead>
			</table>
		</div>
		<div id="entrust_mex" class="floatLeft">
			<div class="widthAll">
				<span>　BITMEX委托信息</span>
			</div>
			合约指数：<span id="mexIndex"></span>  连接状态：<span id="mexStatus">待连接</span>
			<ul class="widthAll">
				<li class="titlePart">
					<span class="order"> 　</span>
					<span class="title">出价</span>
					<span class="title">数量</span>
				</li>
			</ul>
			<ul id="sell">
				<li id="depthli00" class="listCont clear"><span id="sell_order5" class="order">卖5</span><span id="mex_sell_price5" class="price">--</span><span id="mex_sell_number5" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="sell_order4" class="order">卖4</span><span id="mex_sell_price4" class="price">--</span><span id="mex_sell_number4" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="sell_order3" class="order">卖3</span><span id="mex_sell_price3" class="price">--</span><span id="mex_sell_number3" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="sell_order2" class="order">卖2</span><span id="mex_sell_price2" class="price">--</span><span id="mex_sell_number2" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="sell_order1" class="order">卖1</span><span id="mex_sell_price1" class="price">--</span><span id="mex_sell_number1" class="number">--</span></li>
			</ul>
			<div id="mex_price" class="widthAll">
				<span> 当前价格 <span id="mexPrice"></span></span>
			</div>
			<ul id="buy" class="widthAll">
				<li id="depthli00" class="listCont clear"><span id="buy_order1" class="order">买1</span><span id="mex_buy_price1" class="price">--</span><span id="mex_buy_number1" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="buy_order2" class="order">买2</span><span id="mex_buy_price2" class="price">--</span><span id="mex_buy_number2" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="buy_order3" class="order">买3</span><span id="mex_buy_price3" class="price">--</span><span id="mex_buy_number3" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="buy_order4" class="order">买4</span><span id="mex_buy_price4" class="price">--</span><span id="mex_buy_number4" class="number">--</span></li>
				<li id="depthli00" class="listCont clear"><span id="buy_order5" class="order">买5</span><span id="mex_buy_price5" class="price">--</span><span id="mex_buy_number5" class="number">--</span></li>
			</ul>
		</div>
	</div>
	<div id="order" class="">
		<div id="order_okex" class=""></div>
		<div id="order_mex" class=""></div>
	</div>
</div>
	

	<script type="text/javascript">
	var dom = document.getElementById("container");
	var myChart = echarts.init(dom);
	var app = {};
	option = null;
	var priceData = null;
	
	$(document).ready(function() {
		var d = new Date();
	    function addzero(v) {if (v < 10) return '0' + v;return v.toString();}
	    var s = d.getFullYear().toString() + '-'+addzero(d.getMonth() + 1) + '-'+addzero(d.getDate());
	    var e = d.getFullYear().toString() + '-'+addzero(d.getMonth() + 1) + '-'+addzero(d.getDate() + 1);
	    $("#sdate").val(s);
	    $("#edate").val(e);
	    getAllData();
		
	    // 显示实时价格
	    //window.onload = okCoinWebSocket.init("wss://real.okex.com:10440/websocket/okexapi");
	    //  okex 
	    //okCoinWebSocket.init("wss://real.okex.com:10440/websocket/okexapi");
	    //  mex
		//mexWebSocket.init("wss://www.bitmex.com/realtime");
	});
	
	
	
	//统计数据
	function totalMaxAndMin(){
		var data = priceData;
		var zaiText = "";
		var kuanText = "";
		var intZ = 1;
		var intK = 1;
		var zaiTime = 0;
		var kuanTime = 0;
		for (var i = 0; i < data.length; i++) {
	    	var obj = data[i];
			var agio = obj.okToUSDAgio;
			var time = obj.time;
			// 窄
			if(0 < agio && agio < 8){
				// 60秒内出现只算一次
				if(time -60 > zaiTime){
					zaiText += "<tr><td>"+agio+"</td><td>"+time+"</td><td>"+intZ+"</td></tr>";
					zaiTime = time;
					intZ ++;
				}
			}
			
			// 宽	
			if(agio > (max - 5)){
				if(time -60 > kuanTime){
					kuanText += "<tr><td>"+agio+"</td><td>"+time+"</td><td>"+intK+"</td></tr>";
					zaiTime = time;
					intK ++;
				}
			}
		}
		
		$("#showText").html(text);
		$("#zailist").html(zaiText);
		$("#kuanlist").html(kuanText);
		
	}
	
		// 遮罩效果
		$("#container").mLoading({
		    text:"加载中...",
		    icon:"",
		    html:false,
		    content:"",
		    mask:true
		});

		function splitData(rawData) {
		    var PriceTime = [];
		    var okdata = [];
		    var usddata = [];
		    var u17data = [];
		    var okToUSDAgio = [];
		    var okToU17Agio = [];
		    var usdToU17Agio = [];
		    for (var i = 0; i < rawData.length; i++) {
		    	var obj = rawData[i];
		    	PriceTime.push(obj.time);
		    	okdata.push(obj.okPrice);
		    	usddata.push(obj.mexUSDPrice);
		    	u17data.push(obj.mexU17Price);
		    	okToUSDAgio.push(obj.okToUSDAgio);
		    	okToU17Agio.push(obj.okToU17Agio);
		    	usdToU17Agio.push(obj.usdToU17Agio);
		    }

		    return {
		    	PriceTime: PriceTime,
		    	okdata: okdata,
		    	usddata: usddata,
		    	u17data: u17data,
		    	okToUSDAgio: okToUSDAgio,
		    	okToU17Agio: okToU17Agio,
		    	usdToU17Agio: usdToU17Agio
		    };
		} 
		// 数字排序
		function sortNumber(a,b){ 
			return a - b 
		} 
		//统计数据
		function total(arr){
			var min = 0;
			var max = 0;
			var average = 0;
			var count = arr.length;
			if(count > 0){
				arr.sort(sortNumber);
				var sum = 0;
				for(var i =0; i<count; i++){
					sum +=arr[i];
				}
				min = arr[0];
				max = arr[count -1];
				average = sum/count;
				average = fomatFloat(average,2);
			}
			var showMax = max;
			var showMin = min;
			if(max < 0 && max < min){
				showMax = min;
				showMin = max;
			}
			var text = "最大差价:"+showMax+" ,最小差价:"+showMin+" ,平均差价:"+average;
			return text;
		}

		function fomatFloat(src,pos){      
		    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);     
		} 
			
		function showData(rawData){
			var data = splitData(rawData);
			myChart.setOption(option = {
					title: {
						text: '交易价格',
				        left: 0
				    },
				    tooltip: {
				    	trigger: 'axis',
				        axisPointer: {
				            type: 'line'
				        }
				    },
				    legend: {
				        data:['OKEX','XBTUSD','XBTU17']
				    },
				    grid: {
				        left: '10%',
				        right: '3%',
				        bottom: '10%'
				    },
				    xAxis: {
				    	type: 'category',
				        scale: true,
				        boundaryGap: false,
				        axisLine: {onZero: false},//X/Y轴O刻度是否重合
				        splitLine: {show: false},//是否显示分割线
				        splitNumber: 20,//分割数量
				        min: 'dataMin',//坐标轴的最小刻目
				        max: 'dataMax',
				        data: data.PriceTime
				    },
				    yAxis: {
				    	 scale: true,
				         splitArea: {
				             show: true
				         }
				    },
				    dataZoom: [
			              {
			                  type: 'inside',
			                  start: 50,
			                  end: 100
			              },
			              {
			                  show: true,
			                  type: 'slider',
			                  y: '90%',
			                  start: 50,
			                  end: 100
			              }
				    ],
				    series: [
				        {
				            name:'OKEX',
				            type:'line',
				            smooth: true,
				            data:data.okdata,
				            lineStyle: {
				                normal: {opacity: 0.5}
				            }
				        },
				        {
				            name:'XBTUSD',
				            type:'line',
				            smooth: true,
				            data:data.usddata,
				            lineStyle: {
				                normal: {opacity: 0.5}
				            }
				        },
				        {
				            name:'XBTU17',
				            type:'line',
				            smooth: true,
				            data:data.u17data,
				            lineStyle: {
				                normal: {opacity: 0.5}
				            }
				        }
				    ]	
				}, true);
			//统计数据
			$("#OKEXAndXBTUSD").html(total(data.okToUSDAgio));
			$("#OKEXAndXBTU17").html(total(data.okToU17Agio));
			$("#XBTUSDAndXBTU17").html(total(data.usdToU17Agio));
		}
			
		function getAllData(){
			var sdate = $("#sdate").val();
		    var edate = $("#edate").val();
			var obj = new Object();
			//obj.startDt = sdate;
			obj.startDt = "2017-08-20 22:20:00";
			obj.endDt = edate;
			$("#container").mLoading("show");//显示loading组件
			var url = "getBtcPriceData";
			$.ajax({
		        url: url,  
			    data:obj,  
			    dataType: 'json',
			    type: 'GET',
			    cache: false,
			    success: function (data) {  
			    	priceData = data;
			    	//console.log("data show");
			    	showData(data);
					$("#container").mLoading("hide");//隐藏loading组件
			    },
			    error:function(xhr,errorText,errorType){
			    	//console.log("ssdata =" +data);
			    	//console.log("errorText="+errorText+" , errorType="+errorType);
			    	$("#container").mLoading("hide");//隐藏loading组件
			    }
			});
		}
		
	</script>
	</body>
</html>