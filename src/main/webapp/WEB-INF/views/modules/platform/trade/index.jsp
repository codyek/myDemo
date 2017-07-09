<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易中心</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.d_left{
	width:49%;
	height:360px;
	float:left;
	
	}
	.d_right{
	width:49%;
	height:360px;
	float:right;
	border:1px solid #ddd;
	}
	.l_left{
	width:59%;
	height:100%;
	float:left;
	border:1px solid #ddd;
	}
	.l_right{
	width:39%;
	height:100%;
	float:right;
	border:1px solid #ddd;
	}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
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
	<div class="d_left">
		<div class="l_left">
		<ul class="">
			<li>123</li>
			<li>312</li>
		</ul>
			<%--<table>
				<thead>
					<tr>
						<th>买</th>
						<th>卖</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>买一</td>
						<td>卖一</td>
					</tr>
				</tbody>
		--%>
		</div>
		<div class="l_right">bb</div>
	</div>
	<div class="d_right">22</div>
</body>
</html>