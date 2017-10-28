<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<head>
	<meta charset="utf-8" />
	<title>BTC与XBTUSD</title>
	<meta name="decorator" content="default"/>
	<link href="${ctxStatic}/hedge/page.css" rel="stylesheet" />
	<link href="${ctxStatic}/echarts/jquery.mloading.css" rel="stylesheet" type="text/css" />
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/echarts/jquery.mloading.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/echarts/echarts.min.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/hedge/okex.js" type="text/javascript"></script>
	<script src="${ctxStatic}/hedge/mex.js" type="text/javascript"></script>
	<script src="${ctxStatic}/hedge/common.js" type="text/javascript"></script>
	<style type="text/css">
	
	</style>

</head>
<body>
<div id="main" class="">
	<div id="content" class="widthAll">
		<div id="priceInfo" class="floatLeft">
			  查询时段:<input id="sdate" class="Wdate" style="width: 140px;" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,maxDate:'#F{$dp.$D(\'edate\')}',isShowClear:false})" />  
	    	- <input id="edate" class="Wdate" style="width: 140px;" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,maxDate:'%y-%M-{%d+1}',isShowClear:false})"/>
	    	<input type="button" id="click" value="查询" onclick="getAllData()"/>
			<div id="container" style="height: 320px"></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th id="OKEXAndXBTUSD" colspan="3">加载中...</th>
				</tr>
			</thead>
			<tfoot id="zailist"></tfoot>
			<tbody id="kuanlist"></tbody>
			</table>
		</div>
		<div id="tradeInfo" class="floatLeft">
			<div id="realPraceDiv" class="">
				<div id="realPraceDiv_okex" class="floatLeft">
					<b>&nbsp;&nbsp;OKEX--BTC</b>   <span class="floatRight">连接状态：<span id="okexStatus" >待连接 </span>&nbsp;&nbsp;</span>
					<p>&nbsp;&nbsp; 当前价格：<span id="okexPrice"></span><span class="floatRight"> 合约指数：<span id="okexIndex"></span>&nbsp;&nbsp;</span></p>
					<div>
						<table id="contentTable" class="table table-striped table-bordered table-condensed">
							<tr>
								<td width="70px">可用保证金(USD)</td>
								<td id="balance_ok">--</td>
								<td width="60px">可用保证金(BTC)</td>
								<td id="available_ok" >--</td>
							</tr>
						</table>
					</div>
				</div>
				<div id="realPraceDiv_mex" class="floatLeft">
					<b>&nbsp;&nbsp;BITMEX--XBTUSD</b>   <span class="floatRight">连接状态：<span id="mexStatus" >待连接 </span>&nbsp;&nbsp;</span>
					<p>&nbsp;&nbsp; 当前价格：<span id="mexPrice"></span><span class="floatRight">合约指数：<span id="mexIndex"></span>&nbsp;&nbsp;</span></p>
					<div>
						<table id="contentTable" class="table table-striped table-bordered table-condensed">
							<tr>
								<td width="70px">可用保证金(USD)</td>
								<td id="balance_mex">--</td>
								<td width="60px">账户余额(XBT)</td>
								<td id="available_mex" >--</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
			<div id="inputFormDiv" class="">
			<form:form id="inputForm" modelAttribute="TradeTaskReq" action="${ctx}/trade/startJob" method="post" class="form-horizontal">
				<form:hidden path="type"  value="1"  />
				<form:hidden path="symbolA"  value="btc_usd" />
				<form:hidden path="symbolB"  value="XBTUSD" />
				<sys:message content="${message}"/>	
				<div class="control-group">
					<div>
						<span style="width: 320px; display: block;" class="floatLeft">
							<label class="marginLeft">最大差价：</label>
							<form:input path="maxAgio" htmlEscape="false" class="inputWidth"  value="80" />
						</span>
						<span style="width: 260px; display: block;" class="floatLeft">
							<label class="marginLeft">最小差价：</label>
							<form:input path="minAgio" htmlEscape="false" class="inputWidth"  value="5" />
						</span>
					</div>
				</div>
				<div class="control-group">
					<div>
						<span style="width: 320px; display: block;" class="floatLeft">
							<label class="marginLeft">BTC保证金比率%：</label>
							<form:input path="depositA" htmlEscape="false" class="inputWidth"  value="50"  onchange="changeOk(this.value)"/>
						</span>
						<span style="width: 260px; display: block;" class="floatLeft">
							<label class="marginLeft">XBTUSD保证金比率%：</label>
							<form:input path="depositB" htmlEscape="false" class="inputWidth"  value="50" onchange="changeMex(this.value)"/>
						</span>
					</div>
				</div>	
				<div class="control-group">
					<div>
						<span style="width: 320px; display: block;" class="floatLeft">
							<label class="marginLeft">BTC杠杆：</label>
							<form:input path="leverA" htmlEscape="false" class="inputWidth "  readonly="true"  value="10" />
							头寸：<span id="pstA">0</span>
						</span>
						<span style="width: 280px; display: block;" class="floatLeft">
							<label class="marginLeft">XBTUSD杠杆：</label>
							<form:input path="leverB" htmlEscape="false" class="inputWidth" readonly="true"  value="10" />
							头寸：<span id="pstB">0</span>
						</span>
					</div>
				</div>
				<div class="form-actions-local ">
					<input id="btnSubmit" class="btn btn-primary" type="button" value="开始监控" onclick="startJob()"/>&nbsp;
					<input id="btnUpdate" class="btn" type="button" value="修 改" onclick="updateData()"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="停 止" onclick="stopJob()"/>
				</div>
				<div >
					<table id="contentTable" class="table table-striped table-bordered table-condensed">
						<th>
							<td colspan="4">监控数据</td>
							<td >最新监听时间</td>
							<td >--</td>
							<td ><input class="btn" type="button" value="刷新" onclick="getTradeList()"/></td>
						</th>
						<tr>
							<td >开始时间</td>
							<td >结束时间</td>
							<td >最大差价</td>
							<td >最小差价</td>
							<td >是否爆仓</td>
							<td >收入</td>
							<td >费用</td>
							<td >净收益</td>
						</tr>
						<tfoot id="showTradeTr"></tfoot>
					</table>
				</div>
			</form:form>
			</div>
		</div>
	</div>
</div>

	<script type="text/javascript">
	var dom = document.getElementById("container");
	var myChart = echarts.init(dom);
	var symbolA = "btc_usd";
	var symbolB = "XBTUSD";
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
		
	    // 初始化websocket 显示实时价格和指数
	    //  okex 
	    okCoinWebSocket.init("wss://real.okex.com:10440/websocket/okexapi");
	    //  mex
		mexWebSocket.init("wss://www.bitmex.com/realtime");
	    
	    $("#btnSubmit").attr("disabled", true);
	    getMonitorStatus();
	    getOkAccount();
	    getMexAccount();
	    getTradeList();
	});
	
	function getMonitorStatus(){
		var obj = new Object();
		var url ="${ctx}/platform/trade/bitMonitor/getStatus";
		$.ajax({
	        url: url,  
		    data:obj,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	if("success" == data){
		    		$("#btnSubmit").attr("disabled", false);
		    	}else if("running" == data){
		    		$("#btnSubmit").attr("disabled", true);
		    	}else{
		    		top.$.jBox.tip('请求监控状态失败！','error');
		    	}
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求监控状态网络异常！','error');
		    }
		});
	}
	
	function getTradeList(){
		var obj = new Object();
		var url ="${ctx}/platform/trade/bitTrade/getTradeList";
		$.ajax({
	        url: url,  
		    data:obj,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	showTradeData(data);
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求监控状态网络异常！','error');
		    }
		});
	}
	
	function showTradeData(data){
		if(null != data){
			var showText = "";
			for (var i = 0; i < data.length; i++) {
		    	var obj = data[i];
		    	var closeTime = "";
		    	var revenue = "";
		    	var fee = "";
		    	var profit = "";
		    	if(null != obj.closeBarnTime && typeof(obj.closeBarnTime) != "undefined"){
		    		closeTime = obj.closeBarnTime;
		    	}
		    	if(null != obj.revenue && typeof(obj.revenue) != "undefined"){
		    		revenue = obj.revenue;
		    	}
		    	if(null != obj.fee && typeof(obj.fee) != "undefined"){
		    		fee = obj.fee;
		    	}
		    	if(null != obj.profit && typeof(obj.profit) != "undefined"){
		    		profit = obj.profit;
		    	}
		    	showText += "<tr><td>"+obj.openBarnTime+"</td><td>"+closeTime
							    	+"</td><td>"+obj.maxAgio+"</td>"
							    	+"</td><td>"+obj.minAgio+"</td>"
							    	+"</td><td>"+obj.ifBurstBarn+"</td>"
							    	+"</td><td>"+revenue+"</td>"
							    	+"</td><td>"+fee+"</td>"
		    					 	+"</td><td>"+profit+"</td></tr>";
			}
			$("#showTradeTr").html(showText);
		}
	}
	
	function getOkAccount(){
		var obj = new Object();
		obj.type = symbolA;
		var url ="${ctx}/platform/account/bitOkAccount/getAccount";
		$.ajax({
	        url: url,  
		    data:obj,  
		    dataType: 'json',
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	$("#balance_ok").html(data.bond);
		    	$("#available_ok").html(data.accountBalance);
		    	 changeOk(50);
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求A币种账户信息网络异常！','error');
		    }
		});
	}
	
	function getMexAccount(){
		var obj = new Object();
		obj.type = symbolB;
		var url ="${ctx}/platform/account/bitMexAccount/getAccount";
		$.ajax({
	        url: url,  
		    data:obj,  
		    dataType: 'json',
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	$("#balance_mex").html(data.bond);
		    	$("#available_mex").html(data.accountBalance);
		    	changeMex(50);
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求B币种账户信息异常！','error');
		    }
		});
	}
	
	function changeOk(deposit){
		var value = $("#balance_ok").html();
		var lever = $("#leverA").val();
		var pst = lever * value;
		pst = pst * (deposit/100);
		$("#pstA").html(decimal(pst,2));
	}
	function changeMex(deposit){
		var value = $("#balance_mex").html();
		var lever = $("#leverB").val();
		var pst = lever * value;
		pst = pst * (deposit/100);
		$("#pstB").html(decimal(pst,2));
	}
	// 开始Job
	function startJob(){
		$("#tradeInfo").mLoading("show");//显示loading组件
		var url = "${ctx}/trade/startJob" ;
		// from 转Json
		var $form=$("#inputForm");  
		var postData=$form.serialize();
		$.ajax({
	        url: url,  
		    data:postData,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	if("success" == data){
		    		top.$.jBox.tip('处理成功！','success');
		    	}else{
		    		top.$.jBox.tip('处理失败！','error');
		    	}
		    	$("#btnSubmit").attr("disabled", true);
		    	$("#tradeInfo").mLoading("hide");//隐藏loading组件
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求网络异常！','error');
		    	$("#tradeInfo").mLoading("hide");//隐藏loading组件
		    }
		});
	}
	// 修改最大小值、比率
	function updateData(){
		$("#tradeInfo").mLoading("show");//显示loading组件
		var url = "${ctx}/trade/updateData" ;
		// from 转Json
		var $form=$("#inputForm");  
		var postData=$form.serialize();
		$.ajax({
	        url: url,  
		    data:postData,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	if("success" == data){
		    		top.$.jBox.tip('处理成功！','success');
		    	}else{
		    		top.$.jBox.tip('处理失败！','error');
		    	}
		    	$("#tradeInfo").mLoading("hide");//隐藏loading组件
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求网络异常！','error');
		    	$("#tradeInfo").mLoading("hide");//隐藏loading组件
		    }
		});
	}
	
	// 停止Job
	function stopJob(){
		$("#tradeInfo").mLoading("show");//显示loading组件
		var url = "${ctx}/trade/stopJob" ;
		var obj = new Object();
		$.ajax({
	        url: url,  
		    data:obj,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	if("success" == data){
		    		top.$.jBox.tip('处理成功！','success');
		    	}else{
		    		top.$.jBox.tip('处理失败！','error');
		    	}
		    	$("#tradeInfo").mLoading("hide");//隐藏loading组件
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求网络异常！','error');
		    	$("#tradeInfo").mLoading("hide");//隐藏loading组件
		    }
		});
	}
	
	// okex 定义订阅信息
	function initAddChannel() {
		// 基本信息： {'event':'addChannel','channel':'ok_sub_futureusd_btc_ticker_quarter'}
		// 委托信息： {'event':'addChannel','channel':'ok_sub_futureusd_btc_depth_quarter_20'}
		// 指数信息： {'event':'addChannel','channel':'ok_sub_futureusd_btc_index'}
		// 批量注册
		doSend("[ {'event':'addChannel','channel':'ok_sub_futureusd_btc_ticker_quarter'},"
				+ "{'event':'addChannel','channel':'ok_sub_futureusd_btc_index'}]");
	}
	// BitMex 定义订阅信息
	function initAddSubscribe() {
		doMexSend('{"op":"subscribe","args":["instrument:XBTUSD","orderBook10:XBTUSD"]}');
	}

	
	// 间隔时间，20分钟内出现只算一次
	var intervalTime = 20;
	//统计数据
	function totalMaxAndMin(){
		var data = priceData;
		var zaiText = "";
		var kuanText = "";
		var intZ = 1;
		var intK = 1;
		var zaiTime = 0;
		var kuanTime = 0;
		var maxValue =  $("#maxValue").val();
		var minValue =  $("#minValue").val();
		var tempTime = 0;
		for (var i = 0; i < data.length; i++) {
	    	var obj = data[i];
			var agio = obj.okToUSDAgio;
			var time = obj.time;
			// 窄
			if(agio < minValue){
				tempTime = timeInterval(time,intervalTime);
				if(tempTime > zaiTime){
					//console.log("tempTime= "+tempTime+"  ,time="+time+"  ,zaiTime="+zaiTime);
					zaiText += "<tr><td>"+agio+"</td><td>"+timeToString(time)+"</td><td>"+intZ+"</td></tr>";
					zaiTime = time;
					intZ ++;
				}
			}
			
			// 宽	
			if(agio > maxValue){
				tempTime = timeInterval(time,intervalTime);
				if(tempTime > kuanTime){
					//console.log("tempTime= "+tempTime+"  ,time="+time+"  ,KuanTime="+kuanTime);
					kuanText += "<tr><td>"+agio+"</td><td>"+timeToString(time)+"</td><td>"+intK+"</td></tr>";
					kuanTime = time;
					intK ++;
				}
			}
		}
		
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
		    var okToUSDAgio = [];
		    for (var i = 0; i < rawData.length; i++) {
		    	var obj = rawData[i];
		    	PriceTime.push(obj.time);
		    	okdata.push(obj.okPrice);
		    	usddata.push(obj.mexUSDPrice);
		    	okToUSDAgio.push(obj.okToUSDAgio);
		    }

		    return {
		    	PriceTime: PriceTime,
		    	okdata: okdata,
		    	usddata: usddata,
		    	okToUSDAgio: okToUSDAgio
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
			var text = "平均差价:"+average+" ,最大差价:";
			text += "<input type='text' id='maxValue' value='"+showMax+"' style='width:50px' />";
			text += " ,最小差价:";
			text += "<input type='text' id='minValue' value='"+showMin+"' style='width:50px' /> ";
			text += " <input type='button' id='click' value='统计最大最小次数' onclick='totalMaxAndMin()'/>";
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
				    legend: {
				        data:['OKEX','XBTUSD']
				    },
				    tooltip: {
			            trigger: 'axis',
			            axisPointer: {
			                type: 'cross'
			            },
			            backgroundColor: 'rgba(245, 245, 245, 0.8)',
			            borderWidth: 1,
			            borderColor: '#ccc',
			            padding: 10,
			            textStyle: {
			                color: '#000'
			            },
			            position: function (pos, params, el, elRect, size) {
			                var obj = {top: 10};
			                obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 30;
			                return obj;
			            },
			            extraCssText: 'width: 210px'
			        },
			        axisPointer: {
			            link: {xAxisIndex: 'all'},
			            label: {
			                backgroundColor: '#777'
			            }
			        },
			        brush: {
			            xAxisIndex: 'all',
			            brushLink: 'all',
			            outOfBrush: {
			                colorAlpha: 0.1
			            }
			        },
			        visualMap: {
						show:false,
			            seriesIndex: 2,
			            dimension: 2,
			            pieces: [{
			                value: 1,
			                color: '#2f4554'
			            }, {
			                value: -1,
			                color: '#c23531'
			            }]
			        },
				    grid: [
						{
						    left: '8%',
						    right: '3%',
						    height: '50%'
						},
						{
						    left: '8%',
						    right: '3%',
						    top: '66%',
						    height: '16%'
						}
			        ],
				    xAxis: [
			            {
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
			            {
			                type: 'category',
			                gridIndex: 1,
			                data: data.PriceTime,
			                scale: true,
			                boundaryGap : false,
			                axisLine: {onZero: false},
			                axisTick: {show: false},
			                splitLine: {show: false},
			                axisLabel: {show: false},
			                splitNumber: 20,
			                min: 'dataMin',
			                max: 'dataMax',
			                axisPointer: {
			                    label: {
			                        formatter: function (params) {
			                            var seriesValue = (params.seriesData[0] || {}).value;
			                            return params.value
			                            + (seriesValue != null
			                                ? '\n' + echarts.format.addCommas(seriesValue)
			                                : ''
			                            );
			                        }
			                    }
			                }
			            }
					],
				    yAxis:[
				         {
					    	 scale: true,
					         splitArea: {
					             show: true
					         }
					    },
					    {
			                scale: true,
			                gridIndex: 1,
			                splitNumber: 2,
			                axisLabel: {show: false},
			                axisLine: {show: false},
			                axisTick: {show: false},
			                splitLine: {show: false}
			            }
				    ],
				    dataZoom: [
			              {
			                  type: 'inside',
			                  xAxisIndex: [0, 1],
			                  start: 70,
			                  end: 100
			              },
			              {
			                  show: true,
			                  xAxisIndex: [0, 1],
			                  type: 'slider',
			                  y: '86%',
			                  start: 70,
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
			                name: 'BTC与XBTUSD差价',
			                type: 'bar',
			                xAxisIndex: 1,
			                yAxisIndex: 1,
			                data: data.okToUSDAgio
			            }
				    ]	
				}, true);
			//统计数据
			$("#OKEXAndXBTUSD").html(total(data.okToUSDAgio));
		}
			
		function getAllData(){
			var sdate = $("#sdate").val();
		    var edate = $("#edate").val();
			var obj = new Object();
			obj.startDt = sdate;
			obj.startDt = "2017-10-12 21:20:00";
			obj.endDt = edate;
			$("#container").mLoading("show");//显示loading组件
			var url = "${ctx}/analysis/getBtcXbtusdPriceData";
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