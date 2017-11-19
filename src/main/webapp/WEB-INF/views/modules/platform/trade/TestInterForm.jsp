<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易主表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					//loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	
		function cancelMexOrder(){
			var url = "cancelMexOrder" ;
			var orderID = $("#orderID").val();
			var text = $("#text").val();
			var obj = new Object();
			obj.orderID = orderID;
			obj.text = text;
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
	<ul class="nav nav-tabs">
	</ul><br/>
	<form:form id="inputForm" modelAttribute="TradeTaskReq" action="${ctx}/inter/test/saveOrders" method="post" class="form-horizontal">
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">币种：</label>
			<div class="controls">
				<form:input path="symbolA" htmlEscape="false" maxlength="20" class="input-xlarge required"  value="btc_usd" />
				<span class="help-inline"><font color="red">*</font>btc_usd \ XBTUSD</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">杠杆：</label>
			<div class="controls">
				<form:input path="leverA" htmlEscape="false" class="input-xlarge "  value="10" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">数量张：</label>
			<div class="controls">
				<form:input path="amountA" htmlEscape="false" class="input-xlarge " value="1" />
				ok 1张=100usd, mex 1张=1usd
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">买卖方向：</label>
			<div class="controls">
				<form:input path="openDirA" htmlEscape="false" class="input-xlarge " value="1" />
				（1开多2开空3平多4平空） mex Buy 1开多4平空  Sell 2开空3平多
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<form:input path="type" htmlEscape="false" class="input-xlarge " value="mark_info_XX" />
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	<input id="orderID"  type="text" value="" />
	<input id="text"  type="text" value="mark_info_YY" />
	<input class="btn" type="button" value="mex平仓" onclick="cancelMexOrder()"/>&nbsp;
	<div id="show_json"></div>
</body>
</html>