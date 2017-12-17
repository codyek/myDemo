<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信发送管理</title>
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
		<li class="active"><a href="${ctx}/sms/bitSms/">短信发送列表</a></li>
		<shiro:hasPermission name="sms:bitSms:edit"><li><a href="${ctx}/sms/bitSms/form">短信发送添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bitSms" action="${ctx}/sms/bitSms/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户id：</label>
				<form:input path="useId" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>发送号码：</label>
				<form:input path="mobile" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>是否发送成功：1 是， 0 否：</label>
				<form:input path="sendFlag" htmlEscape="false" maxlength="2" class="input-medium"/>
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
				<th>发送号码</th>
				<th>内容</th>
				<th>是否发送成功：1 是， 0 否</th>
				<th>更新时间</th>
				<shiro:hasPermission name="sms:bitSms:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bitSms">
			<tr>
				<td><a href="${ctx}/sms/bitSms/form?id=${bitSms.id}">
					${bitSms.useId}
				</a></td>
				<td>
					${bitSms.mobile}
				</td>
				<td>
					${bitSms.content}
				</td>
				<td>
					${bitSms.sendFlag}
				</td>
				<td>
					<fmt:formatDate value="${bitSms.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:bitSms:edit"><td>
    				<a href="${ctx}/sms/bitSms/form?id=${bitSms.id}">修改</a>
					<a href="${ctx}/sms/bitSms/delete?id=${bitSms.id}" onclick="return confirmx('确认要删除该短信发送吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>