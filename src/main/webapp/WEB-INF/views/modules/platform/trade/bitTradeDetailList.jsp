<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易明细信息管理</title>
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
		<li class="active"><a href="${ctx}/platform/trade/bitTradeDetail/">交易明细信息列表</a></li>
		<shiro:hasPermission name="platform:trade:bitTradeDetail:edit"><li><a href="${ctx}/platform/trade/bitTradeDetail/form">交易明细信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bitTradeDetail" action="${ctx}/platform/trade/bitTradeDetail/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>编码：</label>
				<form:input path="code" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>交易主表编码：</label>
				<form:input path="tradeCode" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>状态：1 已成交, 0 委托中, -1 已撤销：</label>
				<form:input path="statusFlag" htmlEscape="false" maxlength="2" class="input-medium"/>
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
				<th>交易主表编码</th>
				<th>明细类型：1 宽开, 2 窄平, 3 窄开, 4 宽平</th>
				<th>平台</th>
				<th>币种</th>
				<th>杠杆</th>
				<th>数量</th>
				<th>交易方向：1:开多, 2:开空,  3:平多,  4:平空</th>
				<th>头寸</th>
				<th>保证金</th>
				<th>成交价格</th>
				<th>监控时价格</th>
				<th>爆仓价格</th>
				<th>状态：1 已成交, 0 委托中, -1 已撤销</th>
				<th>是否爆仓：1 是， 0 否</th>
				<th>更新时间</th>
				<shiro:hasPermission name="platform:trade:bitTradeDetail:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bitTradeDetail">
			<tr>
				<td><a href="${ctx}/platform/trade/bitTradeDetail/form?id=${bitTradeDetail.id}">
					${bitTradeDetail.code}
				</a></td>
				<td>
					${bitTradeDetail.tradeCode}
				</td>
				<td>
					${bitTradeDetail.detailType}
				</td>
				<td>
					${bitTradeDetail.platform}
				</td>
				<td>
					${bitTradeDetail.symbol}
				</td>
				<td>
					${bitTradeDetail.lever}
				</td>
				<td>
					${bitTradeDetail.amount}
				</td>
				<td>
					${bitTradeDetail.direction}
				</td>
				<td>
					${bitTradeDetail.position}
				</td>
				<td>
					${bitTradeDetail.deposit}
				</td>
				<td>
					${bitTradeDetail.price}
				</td>
				<td>
					${bitTradeDetail.monitorPice}
				</td>
				<td>
					${bitTradeDetail.burstPice}
				</td>
				<td>
					${bitTradeDetail.statusFlag}
				</td>
				<td>
					${bitTradeDetail.ifBurstBarn}
				</td>
				<td>
					<fmt:formatDate value="${bitTradeDetail.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="platform:trade:bitTradeDetail:edit"><td>
    				<a href="${ctx}/platform/trade/bitTradeDetail/form?id=${bitTradeDetail.id}">修改</a>
					<a href="${ctx}/platform/trade/bitTradeDetail/delete?id=${bitTradeDetail.id}" onclick="return confirmx('确认要删除该交易明细信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>