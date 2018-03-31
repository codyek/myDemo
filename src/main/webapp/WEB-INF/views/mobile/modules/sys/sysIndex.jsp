<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<section id="index_section">
	<header>
        <h1 class="title">${fns:getConfig('productName')}</h1>
        <link rel="stylesheet" href="${ctxStatic}/jingle/css/Jingle.css">
    	<link rel="stylesheet" href="${ctxStatic}/jingle/css/app.css">
        <nav class="right">
            <a data-icon="arrow-down-left-2" href="#" id="btnLogout">退出</a>
        </nav>
    </header>
    <article class="active" data-scroll="true">
        <div style="padding: 10px 0 20px;">
        <div class="input-row">
                   <label>停止机器人:</label>
                   <input id="btnCancel" type="button" value="确定" onclick="stopJob()"/>
        </div>
        
        <%--<ul class="list inset demo-list">
            <li data-icon="next" data-selected="selected">
                <span class="icon user"></span>
                
                <a href="#user_section?test=abc" data-target="section">
                    <strong>停止机器人</strong>
                </a>
            </li>
        </ul>--%>
        </div>
    </article>
    <script type="text/javascript">
   		$('#btnLogout').tap(function(){
   			J.confirm('确认提示','确认要退出吗？',function(){
   				$.get("${ctx}/logout", function(){
   					sessionid = '';
   					J.showToast('退出成功！', 'success');
   					J.Router.goTo('#login_section');
   				});
   			});
   		});
   		
   	// 停止Job
   		function stopJob(){
   			var url = "${ctx}/hedge/stopJob" ;
   			var obj = new Object();
   			var symbolA = "btc_usd";
   			var symbolB = "XBTM18";
   			obj.symbolA=symbolA;
   			obj.symbolB=symbolB;
   			$.ajax({
   		        url: url,  
   			    data:obj,  
   			    type: 'POST',
   			    cache: false,
   			    success: function (data) {  
   			    	if("success" == data){
   			    		top.$.jBox.tip('处理成功！','success');
   			    	}else{
   			    		top.$.jBox.tip('处理失败！','error');
   			    	}
   			    },
   			    error:function(xhr,errorText,errorType){
   			    	top.$.jBox.tip('请求网络异常！','error');
   			    }
   			});
   		}
    </script>
</section>