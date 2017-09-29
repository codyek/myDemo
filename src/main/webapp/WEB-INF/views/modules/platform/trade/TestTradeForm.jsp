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
	
		
	
	 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/platform/trade/bitTrade/">交易主表列表</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="TradeTaskReq" action="${ctx}/trade/startJob" method="post" class="form-horizontal">
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">类型：1 宽开窄平, 2 窄开宽平, 3 两者：</label>
			<div class="controls">
				<form:input path="type" htmlEscape="false" maxlength="2" class="input-xlarge required"  value="1"  />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最大差价：</label>
			<div class="controls">
				<form:input path="maxAgio" htmlEscape="false" class="input-xlarge "  value="80" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最小差价：</label>
			<div class="controls">
				<form:input path="minAgio" htmlEscape="false" class="input-xlarge "  value="5" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">币种A：</label>
			<div class="controls">
				<form:input path="symbolA" htmlEscape="false" maxlength="20" class="input-xlarge required"  value="btc_usd" />
				<!--<form:input path="symbolA" htmlEscape="false" maxlength="20" class="input-xlarge required"  value="ltc_usd" />
				-->
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">币种B：</label>
			<div class="controls">
				<form:input path="symbolB" htmlEscape="false" maxlength="20" class="input-xlarge required"  value="XBTUSD" />
				<!--<form:input path="symbolB" htmlEscape="false" maxlength="20" class="input-xlarge required"  value="LTCU17" />
				-->
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">A保证金：</label>
			<div class="controls">
				<form:input path="depositA" htmlEscape="false" class="input-xlarge "  value="20" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">B保证金：</label>
			<div class="controls">
				<form:input path="depositB" htmlEscape="false" class="input-xlarge "  value="20" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">A杠杆：</label>
			<div class="controls">
				<form:input path="leverA" htmlEscape="false" class="input-xlarge "  value="10" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">B杠杆：</label>
			<div class="controls">
				<form:input path="leverB" htmlEscape="false" class="input-xlarge " value="10" />
			</div>
		</div>
		<%--
		<div class="control-group">
			<label class="control-label">A数量：</label>
			<div class="controls">
				<form:input path="amountA" htmlEscape="false" class="input-xlarge " value="5" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">B数量：</label>
			<div class="controls">
				<form:input path="amountB" htmlEscape="false" class="input-xlarge "  value="5" />
			</div>
		</div>

		--%>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>