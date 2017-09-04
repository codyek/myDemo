<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易主表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/platform/trade/bitTrade/">交易主表列表</a></li>
		<shiro:hasPermission name="platform:trade:bitTrade:edit"><li><a href="${ctx}/platform/trade/bitTrade/form">交易主表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bitTrade" action="${ctx}/platform/trade/bitTrade/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>编码：</label>
				<form:input path="code" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>监控编码：</label>
				<form:input path="monitorCode" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>开仓时间：</label>
				<input name="openBarnTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bitTrade.openBarnTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>平仓时间：</label>
				<input name="closeBarnTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bitTrade.closeBarnTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>是否已平仓：1 是， 0 否：</label>
				<form:input path="ifClose" htmlEscape="false" maxlength="2" class="input-medium"/>
			</li>
			<li><label>是否爆仓：1 是， 0 否：</label>
				<form:input path="ifBurstBarn" htmlEscape="false" maxlength="2" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>编码</th>
				<th>监控编码</th>
				<th>类型：1 宽开窄平, 2 窄开宽平, 3 两者</th>
				<th>最大差价</th>
				<th>最小差价</th>
				<th>币种A</th>
				<th>币种B</th>
				<th>A保证金比率</th>
				<th>B保证金比率</th>
				<th>开仓时间</th>
				<th>平仓时间</th>
				<th>收入</th>
				<th>费用</th>
				<th>净收入</th>
				<th>是否已平仓：1 是， 0 否</th>
				<th>是否爆仓：1 是， 0 否</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="platform:trade:bitTrade:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bitTrade">
			<tr>
				<td><a href="${ctx}/platform/trade/bitTrade/form?id=${bitTrade.id}">
					${bitTrade.code}
				</a></td>
				<td>
					${bitTrade.monitorCode}
				</td>
				<td>
					${bitTrade.typeFlag}
				</td>
				<td>
					${bitTrade.maxAgio}
				</td>
				<td>
					${bitTrade.minAgio}
				</td>
				<td>
					${bitTrade.symbolA}
				</td>
				<td>
					${bitTrade.symbolB}
				</td>
				<td>
					${bitTrade.depositRateA}
				</td>
				<td>
					${bitTrade.depositRateB}
				</td>
				<td>
					<fmt:formatDate value="${bitTrade.openBarnTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bitTrade.closeBarnTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bitTrade.revenue}
				</td>
				<td>
					${bitTrade.fee}
				</td>
				<td>
					${bitTrade.profit}
				</td>
				<td>
					${bitTrade.ifClose}
				</td>
				<td>
					${bitTrade.ifBurstBarn}
				</td>
				<td>
					<fmt:formatDate value="${bitTrade.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bitTrade.remarks}
				</td>
				<shiro:hasPermission name="platform:trade:bitTrade:edit"><td>
    				<a href="${ctx}/platform/trade/bitTrade/form?id=${bitTrade.id}">修改</a>
					<a href="${ctx}/platform/trade/bitTrade/delete?id=${bitTrade.id}" onclick="return confirmx('确认要删除该交易主表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>