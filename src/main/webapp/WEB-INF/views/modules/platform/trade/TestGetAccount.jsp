<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易主表管理</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript" ></script>
	<script type="text/javascript">
	function getOkexAccount(){
		var url = "getOkexAccount" ;
		//var sy = $("#okSymbol").val();
		//var status = $("#status").val();
		var obj = new Object();
		//obj.symbol = sy;
		//obj.status = status;
		$.ajax({
	        url: url,  
		    data:obj,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	$("#show_json").html(data);
	    		top.$.jBox.tip('处理成功！','success');
		    },
		    error:function(xhr,errorText,errorType){
		    	top.$.jBox.tip('请求网络异常！','error');
		    }
		});
	}
	
	function getMexAccount(){
		var url = "getMexAccount" ;
		//var sy = $("#mexSymbol").val();
		var obj = new Object();
		//obj.symbol = sy;
		$.ajax({
	        url: url,  
		    data:obj,  
		    type: 'POST',
		    cache: false,
		    success: function (data) {  
		    	$("#show_json").html(data);
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
	<input class="btn" type="button" value="获取Okex账户信息"  onclick="getOkexAccount()"/>
	<input class="btn" type="button" value="获取Mex账户信息"  onclick="getMexAccount()"/>
	<div id="show_json"></div>
</body>
</html>