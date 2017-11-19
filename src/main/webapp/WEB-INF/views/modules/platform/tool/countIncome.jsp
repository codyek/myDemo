<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>计算收益</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/hedge/common.js" type="text/javascript"></script>
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
	
		function countFunction(){
			var openPriceA = $("#openPriceA").val();
			var closePriceA = $("#closePriceA").val();
			var openPriceB = $("#openPriceB").val();
			var closePriceB = $("#closePriceB").val();
			var all = $("#all").val();
			var baojin = $("#baojin").val();
			var fee = $("#fee").val();
			var income = $("#income");
			var shouyi = $("#shouyi");
			var shouyilv = $("#shouyilv");
			
			var amountA = all / openPriceA;
			var amountB = all / openPriceB;
			
			var revenueA = all - (amountA * closePriceA);
			var revenueB = (amountB * closePriceB) - all;
			
			var totalRevenue = revenueA + revenueB;
			var useFee =((amountA * closePriceA)+(amountB * closePriceB)) * fee*0.01;
			
			var jinsy = totalRevenue - useFee;
			income.val(decimal(totalRevenue,2));
			shouyi.val(decimal(jinsy,2));
			shouyilv.val(decimal((jinsy/(all*2))*100,2));
		}
		
	
	 
	</script>
</head>
<body>
	<table id="" class="table table-striped table-bordered table-condensed">
			<tr>
				<td >做空币种A开仓价：<input id="openPriceA"  type="text" value="0" /></td>
				<td >做空币种A平仓价：<input id="closePriceA"  type="text" value="0" /></td>
			</tr>
			<tr>
				<td >做多币种B开仓价：<input id="openPriceB"  type="text" value="0" /></td>
				<td >做多币种B平仓价：<input id="closePriceB"  type="text" value="0" /></td>
			</tr>
			<tr>
				<td >头寸：<input id="all"  type="text" value="0" /></td>
				<td >保证金：<input id="baojin"  type="text" value="0" /></td>
			</tr>
			<tr>
				<td >费用率%：<input id="fee"  type="text" value="0.08" /></td>
				<td >收益：<input id="income"  type="text" value="0" /></td>
			</tr>
			<tr>
				<td >净收益：<input id="shouyi"  type="text" value="0" /></td>
				<td >净收益率：<input id="shouyilv"  type="text" value="0" /></td>
			</tr>
	</table>
	<td ><input id="countBtn" class="btn" type="button" value="计 算" onclick="countFunction()"/></td>
</body>
</html>