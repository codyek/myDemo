<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>mex 账户信息表管理</title>
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
		<li class="active"><a href="${ctx}/platform/account/bitMexAccount/">mex 账户信息表列表</a></li>
		<shiro:hasPermission name="platform:account:bitMexAccount:edit"><li><a href="${ctx}/platform/account/bitMexAccount/form">mex 账户信息表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bitMexAccount" action="${ctx}/platform/account/bitMexAccount/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户id：</label>
				<form:input path="useId" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>币种：</label>
				<form:input path="symbol" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>账户余额：</label>
				<form:input path="accountBalance" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>合约可用：</label>
				<form:input path="available" htmlEscape="false" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>币种</th>
				<th>账户余额</th>
				<th>合约可用</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="platform:account:bitMexAccount:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bitMexAccount">
			<tr>
				<td><a href="${ctx}/platform/account/bitMexAccount/form?id=${bitMexAccount.id}">
					${bitMexAccount.symbol}
				</a></td>
				<td>
					${bitMexAccount.accountBalance}
				</td>
				<td>
					${bitMexAccount.available}
				</td>
				<td>
					<fmt:formatDate value="${bitMexAccount.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bitMexAccount.remarks}
				</td>
				<shiro:hasPermission name="platform:account:bitMexAccount:edit"><td>
    				<a href="${ctx}/platform/account/bitMexAccount/form?id=${bitMexAccount.id}">修改</a>
					<a href="${ctx}/platform/account/bitMexAccount/delete?id=${bitMexAccount.id}" onclick="return confirmx('确认要删除该mex 账户信息表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>