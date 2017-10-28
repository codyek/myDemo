<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易主表管理</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript" ></script>
	<script type="text/javascript">
	function getOkexOrders(){
		var url = "getOkexOrders" ;
		var sy = $("#okSymbol").val();
		var status = $("#status").val();
		var obj = new Object();
		obj.symbol = sy;
		obj.status = status;
		$.ajax({
	        url: url,  
		    data:obj,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	$("#Okjson").html(data);
	    		top.$.jBox.tip('处理成功！','success');
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求网络异常！','error');
		    }
		});
	}
	
	function getMexOrders(){
		var url = "getMexOrders" ;
		var sy = $("#mexSymbol").val();
		var obj = new Object();
		obj.symbol = sy;
		$.ajax({
	        url: url,  
		    data:obj,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	$("#Mexjson").html(data);
	    		top.$.jBox.tip('处理成功！','success');
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求网络异常！','error');
		    }
		});
	}
	
	</script>
</head>
<body>
	<p>okex:
	<input id="okSymbol"  type="text"  value="btc_usd" />
	<input id="status"   type="text" value="1" />   1或2
	</p>
	<p>Mex:
	<input id="mexSymbol"  type="text" value="XBT" />
	</p>
	<input class="btn" type="button" value="获取Okex订单Json"  onclick="getOkexOrders()"/>
	<input class="btn" type="button" value="获取Mex订单"  onclick="getMexOrders()"/>
	<div id="Okjson"></div>
	<div id="Mexjson"></div>
</body>
</html>