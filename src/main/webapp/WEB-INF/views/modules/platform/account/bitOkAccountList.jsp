<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Ok账户信息表管理</title>
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
		<li class="active"><a href="${ctx}/platform/account/bitOkAccount/">Ok账户信息表列表</a></li>
		<shiro:hasPermission name="platform:account:bitOkAccount:edit"><li><a href="${ctx}/platform/account/bitOkAccount/form">Ok账户信息表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bitOkAccount" action="${ctx}/platform/account/bitOkAccount/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户id：</label>
				<form:input path="useId" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>币种：</label>
				<form:input path="symbol" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>合约类别：</label>
				<form:input path="contractType" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户id</th>
				<th>币种</th>
				<th>账户余额</th>
				<th>合约可用</th>
				<th>账户(合约)余额</th>
				<th>固定保证金</th>
				<th>冻结</th>
				<th>已实现盈亏</th>
				<th>未实现盈亏</th>
				<th>账户权益</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="platform:account:bitOkAccount:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bitOkAccount">
			<tr>
				<td><a href="${ctx}/platform/account/bitOkAccount/form?id=${bitOkAccount.id}">
					${bitOkAccount.useId}
				</a></td>
				<td>
					${bitOkAccount.symbol}
				</td>
				<td>
					${bitOkAccount.accountBalance}
				</td>
				<td>
					${bitOkAccount.available}
				</td>
				<td>
					${bitOkAccount.balance}
				</td>
				<td>
					${bitOkAccount.bond}
				</td>
				<td>
					${bitOkAccount.freeze}
				</td>
				<td>
					${bitOkAccount.profit}
				</td>
				<td>
					${bitOkAccount.unprofit}
				</td>
				<td>
					${bitOkAccount.rights}
				</td>
				<td>
					<fmt:formatDate value="${bitOkAccount.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bitOkAccount.remarks}
				</td>
				<shiro:hasPermission name="platform:account:bitOkAccount:edit"><td>
    				<a href="${ctx}/platform/account/bitOkAccount/form?id=${bitOkAccount.id}">修改</a>
					<a href="${ctx}/platform/account/bitOkAccount/delete?id=${bitOkAccount.id}" onclick="return confirmx('确认要删除该Ok账户信息表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>