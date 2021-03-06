<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>币种参数配置管理</title>
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
		<li class="active"><a href="${ctx}/platform/trade/bitSymbolConfig/">币种参数配置列表</a></li>
		<shiro:hasPermission name="platform:trade:bitSymbolConfig:edit"><li><a href="${ctx}/platform/trade/bitSymbolConfig/form">币种参数配置添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bitSymbolConfig" action="${ctx}/platform/trade/bitSymbolConfig/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>编码：</label>
				<form:input path="code" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>币种：</label>
				<form:input path="symbol" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>平台：</label>
				<form:input path="platform" htmlEscape="false" maxlength="20" class="input-medium"/>
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
				<th>币种</th>
				<th>平台</th>
				<th>费率%</th>
				<th>一张合约面值</th>
				<th>面值单位</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="platform:trade:bitSymbolConfig:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bitSymbolConfig">
			<tr>
				<td><a href="${ctx}/platform/trade/bitSymbolConfig/form?id=${bitSymbolConfig.id}">
					${bitSymbolConfig.code}
				</a></td>
				<td>
					${bitSymbolConfig.symbol}
				</td>
				<td>
					${bitSymbolConfig.platform}
				</td>
				<td>
					${bitSymbolConfig.feeRate}
				</td>
				<td>
					${bitSymbolConfig.parValue}
				</td>
				<td>
					${bitSymbolConfig.unit}
				</td>
				<td>
					<fmt:formatDate value="${bitSymbolConfig.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bitSymbolConfig.remarks}
				</td>
				<shiro:hasPermission name="platform:trade:bitSymbolConfig:edit"><td>
    				<a href="${ctx}/platform/trade/bitSymbolConfig/form?id=${bitSymbolConfig.id}">修改</a>
					<a href="${ctx}/platform/trade/bitSymbolConfig/delete?id=${bitSymbolConfig.id}" onclick="return confirmx('确认要删除该币种参数配置吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>