	var mexWebSocket = {};
	
	mexWebSocket.init = function(uri) {
		this.wsUri = uri;
		this.lastHeartBeat = new Date().getTime();
		this.overtime = 18000;
	
		mexWebSocket.websocket = new WebSocket(mexWebSocket.wsUri);
	
		mexWebSocket.websocket.onopen = function(evt) {
			onMexOpen(evt)
		};
		mexWebSocket.websocket.onclose = function(evt) {
			onMexClose(evt)
		};
		mexWebSocket.websocket.onmessage = function(evt) {
			onMexMessage(evt)
		};
		mexWebSocket.websocket.onerror = function(evt) {
			onMexError(evt)
		};
	
		setInterval(checkMexConnect,11000);
	}
	
	function checkMexConnect() {
		console.log("发送Mex ping 心跳检查...");
		doMexSend('ping');
		if ((new Date().getTime() - mexWebSocket.lastHeartBeat) > mexWebSocket.overtime) {
			console.log("Mex socket 连接断开，正在尝试重新建立连接");
			var text = '<span style="color: red;">断开</span> ' ;
			showMexStatus(text);
			mexWebSocket.init("wss://www.bitmex.com/realtime");
		}
	}
	
	function onMexOpen(evt) {
		//console.log("onOpen--ok!");
		//doMexSend('{"op":"subscribe","args":["trade:XBTUSD","instrument:XBTUSD"]}');
		//doMexSend('{"op":"subscribe","args":["orderBookL2:XBTUSD"]}');
		//doMexSend('{"op":"subscribe","args":["trade:XBTUSD"]}');
		var text = '<span style="color: green;">已连接</span> ' ;
		showMexStatus(text);
		initAddSubscribe(); //在jsp中设置定义
	}
	
	function onMexClose(evt) {
		var text = '<span style="color: red;">断开</span> ' ;
		showMexStatus(text);
	}
	
	function onMexError(evt) {
		var text = '<span style="color: red;">连接异常</span> ' ;
		showMexStatus(text);
	}
	
	function onMexMessage(e) {
		if('pong' == e.data){
			console.log("onMessage: " + e.data);
			var text = '<span style="color: green;">已连接</span> ' ;
			showMexStatus(text);
			mexWebSocket.lastHeartBeat = new Date().getTime();
			return;
		}
		var json = JSON.parse(e.data);
		var table = json.table;
		if ("instrument" == table) {
			// 产品的买卖价格,指数及交易量更新
			var data = json.data[0];
			showMexPriceAndIndex(data);
		}
	
		//if ("orderBook10" == table) {
			// 委托列表首10层
		//	var data = json.data[0];
		//	getMexPriceData(data);
		//}
	
	}
	
	// 显示价格,指数
	function showMexPriceAndIndex(data) {
		if (undefined != data.lastPrice && "undefined" != data.lastPrice
				&& null != data.lastPrice) {
			// 当前价格
			var mexPrice = document.getElementById("mexPrice");
			var balance_mex = document.getElementById("balance_mex");
			var available_mex = document.getElementById("available_mex");
			mexPrice.innerHTML = data.lastPrice;
			var btcValue = available_mex.innerHTML;
			var showUsd = data.lastPrice*btcValue
			balance_mex.innerHTML = decimal(showUsd, 2);
		}
		if (undefined != data.indicativeSettlePrice
				&& "undefined" != data.indicativeSettlePrice
				&& null != data.indicativeSettlePrice) {
			// 指数
			var mexIndex = document.getElementById("mexIndex");
			mexIndex.innerHTML = data.indicativeSettlePrice;
		}
		/* if(undefined != data.fairPrice && "undefined" !=data.fairPrice && null != data.fairPrice){
			// 合理价格
			console.log("fairPrice : " + data.fairPrice);
		} */
	}
	
	// Mex连接状态
	function showMexStatus(status){
		var mexStatus = document.getElementById("mexStatus");
		mexStatus.innerHTML = status;
	}
	
	function doMexSend(message) {
		//console.log("doMexSend: " + message);
		mexWebSocket.websocket.send(message);
	}
	
	function getMexPriceData(data) {
		//asks(array):卖单
		var asksArr = data.asks;
		var asks_len = asksArr.length;
		for ( var i = 0; i < asks_len; i++) {
			var ask = asksArr[i];
			if (0 == i) {
				var price = document.getElementById("mex_sell_price1");
				var number = document.getElementById("mex_sell_number1");
				price.innerHTML = ask[0];
				number.innerHTML = ask[1];
			}
			if (1 == i) {
				var price = document.getElementById("mex_sell_price2");
				var number = document.getElementById("mex_sell_number2");
				price.innerHTML = ask[0];
				number.innerHTML = ask[1];
			}
			if (2 == i) {
				var price = document.getElementById("mex_sell_price3");
				var number = document.getElementById("mex_sell_number3");
				price.innerHTML = ask[0];
				number.innerHTML = ask[1];
			}
			if (3 == i) {
				var price = document.getElementById("mex_sell_price4");
				var number = document.getElementById("mex_sell_number4");
				price.innerHTML = ask[0];
				number.innerHTML = ask[1];
			}
			if (4 == i) {
				var price = document.getElementById("mex_sell_price5");
				var number = document.getElementById("mex_sell_number5");
				price.innerHTML = ask[0];
				number.innerHTML = ask[1];
			}
		}
	
		//bids(array):买单
		var bidsArr = data.bids;
		var bids_len = bidsArr.length;
		for ( var i = 0; i < bids_len; i++) {
			var bid = bidsArr[i];
			if (0 == i) {
				var price = document.getElementById("mex_buy_price1");
				var number = document.getElementById("mex_buy_number1");
				price.innerHTML = bid[0];
				number.innerHTML = bid[1];
			}
			if (1 == i) {
				var price = document.getElementById("mex_buy_price2");
				var number = document.getElementById("mex_buy_number2");
				price.innerHTML = bid[0];
				number.innerHTML = bid[1];
			}
			if (2 == i) {
				var price = document.getElementById("mex_buy_price3");
				var number = document.getElementById("mex_buy_number3");
				price.innerHTML = bid[0];
				number.innerHTML = bid[1];
			}
			if (3 == i) {
				var price = document.getElementById("mex_buy_price4");
				var number = document.getElementById("mex_buy_number4");
				price.innerHTML = bid[0];
				number.innerHTML = bid[1];
			}
			if (4 == i) {
				var price = document.getElementById("mex_buy_price5");
				var number = document.getElementById("mex_buy_number5");
				price.innerHTML = bid[0];
				number.innerHTML = bid[1];
			}
		}
	}