<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>监控管理管理</title>
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
		<li class="active"><a href="${ctx}/platform/trade/bitMonitor/">监控管理列表</a></li>
		<shiro:hasPermission name="platform:trade:bitMonitor:edit"><li><a href="${ctx}/platform/trade/bitMonitor/form">监控管理添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bitMonitor" action="${ctx}/platform/trade/bitMonitor/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>编码：</label>
				<form:input path="code" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>开始时间：</label>
				<input name="openTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bitMonitor.openTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>结束时间：</label>
				<input name="closeTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bitMonitor.closeTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>状态：1 运行中, 0 结束：</label>
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
				<th>编码</th>
				<th>开始时间</th>
				<th>结束时间</th>
				<th>状态：1 运行中, 0 结束</th>
				<th>用户id</th>
				<th>线程id</th>
				<th>监控最新运行时间</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="platform:trade:bitMonitor:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bitMonitor">
			<tr>
				<td><a href="${ctx}/platform/trade/bitMonitor/form?id=${bitMonitor.id}">
					${bitMonitor.code}
				</a></td>
				<td>
					<fmt:formatDate value="${bitMonitor.openTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bitMonitor.closeTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bitMonitor.statusFlag}
				</td>
				<td>
					${bitMonitor.user.name}
				</td>
				<td>
					${bitMonitor.threadId}
				</td>
				<td>
					<fmt:formatDate value="${bitMonitor.newTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bitMonitor.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bitMonitor.remarks}
				</td>
				<shiro:hasPermission name="platform:trade:bitMonitor:edit"><td>
    				<a href="${ctx}/platform/trade/bitMonitor/form?id=${bitMonitor.id}">修改</a>
					<a href="${ctx}/platform/trade/bitMonitor/delete?id=${bitMonitor.id}" onclick="return confirmx('确认要删除该监控管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>