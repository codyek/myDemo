<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>API授权管理</title>
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
		<li class="active"><a href="${ctx}/platform/apiauthor/aPIAuthorize/">API授权列表</a></li>
		<shiro:hasPermission name="platform:apiauthor:aPIAuthorize:edit"><li><a href="${ctx}/platform/apiauthor/aPIAuthorize/form">API授权添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="author" action="${ctx}/platform/apiauthor/aPIAuthorize/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>平台：</label>
				<form:select path="platform" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('bit_platform')}"  itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>appkey：</label>
				<form:input path="appkey" htmlEscape="false" maxlength="120" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>平台</th>
				<th>appkey</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="platform:apiauthor:aPIAuthorize:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="author">
			<tr>
				<td>
					${fns:getDictLabel(author.platform, 'bit_platform', '')}
				</td>
				<td>
					<a href="${ctx}/platform/apiauthor/aPIAuthorize/form?id=${author.id}">
					${author.appkey}
					</a>
				</td>
				<td>
					<fmt:formatDate value="${author.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${author.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${author.remarks}
				</td>
				<shiro:hasPermission name="platform:apiauthor:aPIAuthorize:edit"><td>
    				<a href="${ctx}/platform/apiauthor/aPIAuthorize/form?id=${author.id}">修改</a>
    				<a href="${ctx}/platform/apiauthor/aPIAuthorize/getInfo?id=${author.id}">获取账户余额</a>
					<a href="${ctx}/platform/apiauthor/aPIAuthorize/delete?id=${author.id}" onclick="return confirmx('确认要删除该API授权吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>