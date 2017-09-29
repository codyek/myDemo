	var okCoinWebSocket = {};
	
	okCoinWebSocket.init = function(uri) {
		this.wsUri = uri;
		this.lastHeartBeat = new Date().getTime();
		this.overtime = 18000;
	
		okCoinWebSocket.websocket = new WebSocket(okCoinWebSocket.wsUri);
	
		okCoinWebSocket.websocket.onopen = function(evt) {
			onOpen(evt)
		};
		okCoinWebSocket.websocket.onclose = function(evt) {
			onClose(evt)
		};
		okCoinWebSocket.websocket.onmessage = function(evt) {
			onMessage(evt)
		};
		okCoinWebSocket.websocket.onerror = function(evt) {
			onError(evt)
		};
	
		setInterval(checkConnect, 10000);
	}
	
	function checkConnect() {
		console.log("发送ping 心跳检查...");
		doSend("{'event':'ping'}");
		if ((new Date().getTime() - okCoinWebSocket.lastHeartBeat) > okCoinWebSocket.overtime) {
			console.log("socket 连接断开，正在尝试重新建立连接");
			var text = '<span style="color: red;">断开</span> ' ;
			showOkexStatus(text);
			okCoinWebSocket.init("wss://real.okex.com:10440/websocket/okexapi");
		}
	}
	
	function onOpen(evt) {
		//print(">>　websocket opened .....");
		var text = '<span style="color: green;">已连接</span> ' ;
		showOkexStatus(text);
		initAddChannel(); // 在jap中定义
	}
	
	function onClose(evt) {
		//print(">> websocket close ----");
		var text = '<span style="color: red;">断开</span> ' ;
		showOkexStatus(text);	
	}
	
	function onMessage(msg) {
		var array = JSON.parse(msg.data);
		if (array.event == 'pong') {
			console.log(new Date().getTime() + ": " + msg.data);
			var text = '<span style="color: green;">已连接</span> ' ;
			showOkexStatus(text);
			okCoinWebSocket.lastHeartBeat = new Date().getTime();
		} else {
			var channel = array[0].channel;
			//print(">> channel = "+channel);
			if ("ok_sub_futureusd_btc_depth_quarter_20" == channel) {
				getPriceData(array);
			}else if ("ok_sub_futureusd_btc_ticker_quarter" == channel) {
				var pData = array[0].data;
				var price = pData.last;
				showOkexPrice(price);
			}else if ("ok_sub_futureusd_btc_index" == channel) {
				var iData = array[0].data;
				var index = iData.futureIndex;
				showOkexIndex(index);
			}
		}
	}
	
	function onError(evt) {
		//print('<span style="color: red;">ERROR:</span> ' + evt.data);
		var text = '<span style="color: red;">连接异常</span> ' ;
		showOkexStatus(text);	
	}
	
	function doSend(message) {
		//print("SENT: " + message);
		okCoinWebSocket.websocket.send(message);
	}
	
	function print(message) {
		var status = document.getElementById("order_okex");
		status.style.wordWrap = "break-word";
		status.innerHTML = message + "<br/>";
	}
	
	// ok当前价格
	function showOkexPrice(price){
		var okexPrice = document.getElementById("okexPrice");
		okexPrice.innerHTML = price;
	}
	
	// ok当前指数
	function showOkexIndex(index){
		var okexIndex = document.getElementById("okexIndex");
		okexIndex.innerHTML = index;
	}
	
	// ok连接状态
	function showOkexStatus(status){
		var okexStatus = document.getElementById("okexStatus");
		okexStatus.innerHTML = status;
	}
	
	function getPriceData(array) {
		var obj = array[0];
		//asks(array):卖单
		var asksArr = obj.data.asks;
		var asks_len = asksArr.length;
		for ( var i = asks_len - 1; i > 0; i--) {
			var ask = asksArr[i];
			if ((asks_len - 1) == i) {
				var price = document.getElementById("okex_sell_price1");
				var number = document.getElementById("okex_sell_number1");
				price.innerHTML = ask[0];
				number.innerHTML = ask[3];
			}
			if ((asks_len - 2) == i) {
				var price = document.getElementById("okex_sell_price2");
				var number = document.getElementById("okex_sell_number2");
				price.innerHTML = ask[0];
				number.innerHTML = ask[3];
			}
			if ((asks_len - 3) == i) {
				var price = document.getElementById("okex_sell_price3");
				var number = document.getElementById("okex_sell_number3");
				price.innerHTML = ask[0];
				number.innerHTML = ask[3];
			}
			if ((asks_len - 4) == i) {
				var price = document.getElementById("okex_sell_price4");
				var number = document.getElementById("okex_sell_number4");
				price.innerHTML = ask[0];
				number.innerHTML = ask[3];
			}
			if ((asks_len - 5) == i) {
				var price = document.getElementById("okex_sell_price5");
				var number = document.getElementById("okex_sell_number5");
				price.innerHTML = ask[0];
				number.innerHTML = ask[3];
			}
		}
	
		//bids(array):买单
		var bidsArr = obj.data.bids;
		var bids_len = bidsArr.length;
		for ( var i = 0; i < bids_len; i++) {
			var bid = bidsArr[i];
			if (0 == i) {
				var price = document.getElementById("okex_buy_price1");
				var number = document.getElementById("okex_buy_number1");
				price.innerHTML = bid[0];
				number.innerHTML = bid[3];
			}
			if (1 == i) {
				var price = document.getElementById("okex_buy_price2");
				var number = document.getElementById("okex_buy_number2");
				price.innerHTML = bid[0];
				number.innerHTML = bid[3];
			}
			if (2 == i) {
				var price = document.getElementById("okex_buy_price3");
				var number = document.getElementById("okex_buy_number3");
				price.innerHTML = bid[0];
				number.innerHTML = bid[3];
			}
			if (3 == i) {
				var price = document.getElementById("okex_buy_price4");
				var number = document.getElementById("okex_buy_number4");
				price.innerHTML = bid[0];
				number.innerHTML = bid[3];
			}
			if (4 == i) {
				var price = document.getElementById("okex_buy_price5");
				var number = document.getElementById("okex_buy_number5");
				price.innerHTML = bid[0];
				number.innerHTML = bid[3];
			}
		}
	}