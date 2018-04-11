$(function(){
	$("#goHome").click(function(){
		goHome();
	});
	
	// 注册按钮事件
	$("#signupBtn").click(function(){
		// 获取参数
		var dataParam = {combiField:$("#account").val(), name:$("#name").val(), password:$("#password").val()};
		
		var param = {data:dataParam, url:"/user/signup", callback:function(response){
			if(response.code == 0){
				var userSession = {};
				userSession["isLogin"] = true;
				// 设置用户信息
				$.extend(userSession, response.data);
				// 会话存储
				sessionStorage.setItem("userSession", JSON.stringify(userSession));
				// 跳转
				goHome();
			}else{
				$("#errorTip").text($.i18n.prop(response.code));
			}
		}};
		
		// 注册
		$.myajax(param);
	});
});