<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<head>
	<meta charset="utf-8" />
	<title>BTC与XBTUSD</title>
	<meta name="decorator" content="default"/>
	<link href="${ctxStatic}/hedge/page_XbtUsd.css" rel="stylesheet" />
	<link href="${ctxStatic}/tab/tab.css" rel="stylesheet" />
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/echarts/jquery.mloading.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/hedge/okex_XbtUsd.js" type="text/javascript"></script>
	<script src="${ctxStatic}/hedge/mex.js" type="text/javascript"></script>
	<script src="${ctxStatic}/hedge/common.js" type="text/javascript"></script>
	<style type="text/css">
	
	</style>

</head>
<body>
<div id="main" class="">
	<div id="content" class="widthAll">
			<div id="" class="">
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
				<div id="realPraceDiv_agio" class="floatLeft"><h5>当前差价</h5>
					<br/><strong><span id="showAgio"></span></strong>
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
						<span style="width: 50%; display: block;" class="floatLeft">
							<label class="marginLeft">最大差价：</label>
							<form:input path="maxAgio" htmlEscape="false" class="inputWidth"  value="80" />
							<label class="marginLeft">BTC保证金比率%：</label>
							<form:input path="depositA" htmlEscape="false" class="inputWidth"  value="50"  onchange="changeOk(this.value)"/>
							<label class="marginLeft">BTC杠杆：</label>
							<form:input path="leverA" htmlEscape="false" class="inputWidth "  readonly="true"  value="10" />
							头寸：<span id="pstA">0</span>
						</span>
						<span style="width: 50%; display: block;" class="floatLeft">
							<label class="marginLeft">最小差价：</label>
							<form:input path="minAgio" htmlEscape="false" class="inputWidth"  value="5" />
							<label class="marginLeft">XBTUSD保证金比率%：</label>
							<form:input path="depositB" htmlEscape="false" class="inputWidth"  value="50" onchange="changeMex(this.value)"/>
							<label class="marginLeft">XBTUSD杠杆：</label>
							<form:input path="leverB" htmlEscape="false" class="inputWidth" readonly="true"  value="10" />
							头寸：<span id="pstB">0</span>
						</span>
				</div>	
				<div class="form-actions-local ">
					<input id="btnSubmit" class="btn btn-primary" type="button" value="开始监控" onclick="startJob()"/>&nbsp;
					<input id="btnUpdate" class="btn" type="button" value="修 改" onclick="updateData()"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="停 止" onclick="stopJob()"/>
				</div>
			</form:form>
		</div>
			
			<!--  选项卡   -->
			<div class="titTabs">
				<ul class="titTab">
					<li class="active">监控主数据</li>
					<li>当前对冲单</li>
				</ul>
				<div class="titInfo">
					<div class="on">
						 <table id="contentTable" class="table table-striped table-bordered table-condensed">
							<tr>
								<td width="120px">最新监听时间</td>
								<td width="120px">--</td>
								<td colspan="5"> </td>
								<td ><input class="btn" type="button" value="刷新" onclick="getTradeList()"/></td>
							</tr>
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
					<div>
						<table id="" class="table table-striped table-bordered table-condensed">
							<tr>
								<td >交易时间</td>
								<td >币种</td>
								<td >杠杆</td>
								<td >数量</td>
								<td >头寸</td>
								<td >交易方向</td>
								<td >成交价格</td>
								<td >爆仓价格</td>
								<td >当前收益率</td>
								<td >操作</td>
							</tr>
							<tfoot id="showDetailTr"></tfoot>
						</table>
					</div>
				</div>
			</div>
		</div>
</div>

	<script type="text/javascript">
	var symbolA = "btc_usd";
	var symbolB = "XBTUSD";
	
	$(document).ready(function() {
		// 选项卡
		$(".titTab li").click(function(){
	          $(".titTab li").eq($(this).index()).addClass("active").siblings().removeClass("active");
	          $(".titInfo div").hide().eq($(this).index()).show();
        });
		
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
	    getTradeDetailList();
	});
	
	// 遮罩效果
	$("#container").mLoading({
	    text:"加载中...",
	    icon:"",
	    html:false,
	    content:"",
	    mask:true
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
							    	+"<td>"+obj.minAgio+"</td>"
							    	+"<td>"+obj.ifBurstBarn+"</td>"
							    	+"<td>"+revenue+"</td>"
							    	+"<td>"+fee+"</td>"
		    					 	+"<td>"+profit+"</td></tr>";
			}
			$("#showTradeTr").html(showText);
		}
	}
	
	// 获取当前对冲明细单
	function getTradeDetailList(){
		var obj = new Object();
		var url ="${ctx}/platform/trade/bitTrade/getTradeDetailList";
		$.ajax({
	        url: url,  
		    data:obj,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	showTradeDetailData(data);
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求监控状态网络异常！','error');
		    }
		});
	}
	
	function showTradeDetailData(data){
		if(null != data){
			var showText = "";
			for (var i = 0; i < data.length; i++) {
		    	var obj = data[i];
		    	showText += "<tr><td>"+obj.createDate+"</td><td>"+obj.symbol
							    	+"</td><td>"+obj.lever+"</td>"
							    	+"<td>"+obj.amount+"</td>"
							    	+"<td>"+obj.position+"</td>"
							    	+"<td>"+obj.direction+"</td>"
							    	+"<td>"+obj.price+"</td>"
		    					 	+"<td>"+obj.burstPice+"</td>"
		    					 	+"<td>"+0+"</td>"
		    					 	+"<td></td></tr>";
			}
			$("#showDetailTr").html(showText);
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
		var url = "${ctx}/hedge/startJob" ;
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
		var url = "${ctx}/hedge/updateData" ;
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
		var url = "${ctx}/hedge/stopJob" ;
		var obj = new Object();
		obj.symbolA=symbolA;
		obj.symbolB=symbolB;
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

	</script>
	</body>
</html>