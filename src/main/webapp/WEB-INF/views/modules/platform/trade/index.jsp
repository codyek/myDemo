<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<head>
	<meta charset="utf-8" />
	<title>WebSocket中心</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/trade/okex.js" type="text/javascript"></script>
	<script src="${ctxStatic}/trade/mex.js" type="text/javascript"></script>
	<link href="${ctxStatic}/trade/page.css" rel="stylesheet" />
	<style type="text/css">
	
	</style>
	<script type="text/javascript">
		
		window.onload = okCoinWebSocket.init("wss://real.okex.com:10440/websocket/okexapi");
		mexWebSocket.init("wss://www.bitmex.com/realtime");
	</script>
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
			买入价:
			<br/>
			买入量:
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
	
</body>
</html>