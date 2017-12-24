<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>监控记录表管理</title>
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
		<li class="active"><a href="${ctx}/log/bitMonitorLog/">监控记录表列表</a></li>
		<shiro:hasPermission name="log:bitMonitorLog:edit"><li><a href="${ctx}/log/bitMonitorLog/form">监控记录表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bitMonitorLog" action="${ctx}/log/bitMonitorLog/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户id：</label>
				<form:input path="useId" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>类型：monitor监控,trade交易：</label>
				<form:input path="typeFlag" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>状态：1 成功, 0 失败：</label>
				<form:input path="statusFlag" htmlEscape="false" maxlength="2" class="input-medium"/>
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
				<th>类型：monitor监控,trade交易</th>
				<th>日志内容</th>
				<th>状态：1 成功, 0 失败</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="log:bitMonitorLog:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bitMonitorLog">
			<tr>
				<td><a href="${ctx}/log/bitMonitorLog/form?id=${bitMonitorLog.id}">
					${bitMonitorLog.useId}
				</a></td>
				<td>
					${bitMonitorLog.typeFlag}
				</td>
				<td>
					${bitMonitorLog.logContent}
				</td>
				<td>
					${bitMonitorLog.statusFlag}
				</td>
				<td>
					<fmt:formatDate value="${bitMonitorLog.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bitMonitorLog.remarks}
				</td>
				<shiro:hasPermission name="log:bitMonitorLog:edit"><td>
    				<a href="${ctx}/log/bitMonitorLog/form?id=${bitMonitorLog.id}">修改</a>
					<a href="${ctx}/log/bitMonitorLog/delete?id=${bitMonitorLog.id}" onclick="return confirmx('确认要删除该监控记录表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>