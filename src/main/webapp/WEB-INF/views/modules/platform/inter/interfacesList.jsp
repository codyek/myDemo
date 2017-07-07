<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>接口信息管理</title>
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
		<li class="active"><a href="${ctx}/platform/inter/interfaces/">接口信息列表</a></li>
		<shiro:hasPermission name="platform:inter:interfaces:edit"><li><a href="${ctx}/platform/inter/interfaces/form">接口信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="interfaces" action="${ctx}/platform/inter/interfaces/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>平台：</label>
				<form:select path="platform" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('bit_platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('bit_inter_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>编码：</label>
				<form:input path="code" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>编号</th>
				<th>平台</th>
				<th>类型</th>
				<th>编码</th>
				<th>接口地址</th>
				<th>接口描述</th>
			<!-- <th>创建时间</th>  -->	
				<th>更新时间</th>
				<shiro:hasPermission name="platform:inter:interfaces:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="interfaces">
			<tr>
				<td><a href="${ctx}/platform/inter/interfaces/form?id=${interfaces.id}">
					${interfaces.id}
				</a></td>
				<td>
					${fns:getDictLabel(interfaces.platform, 'bit_platform', '')}
				</td>
				<td>
					${fns:getDictLabel(interfaces.type, 'bit_inter_type', '')}
				</td>
				<td>
					${interfaces.code}
				</td>
				<td>
					${interfaces.url}
				</td>
				<td>
					${interfaces.describes}
				</td>
				<!--
				<td>
					<fmt:formatDate value="${interfaces.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			 	 -->	
				<td>
					<fmt:formatDate value="${interfaces.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="platform:inter:interfaces:edit"><td>
    				<a href="${ctx}/platform/inter/interfaces/form?id=${interfaces.id}">修改</a>
					<a href="${ctx}/platform/inter/interfaces/delete?id=${interfaces.id}" onclick="return confirmx('确认要删除该接口信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>