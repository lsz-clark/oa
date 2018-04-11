$(function(){
	/*// 从这里开始
	$("#startHere").click(function(){
		window.location.href = rootPath + "/portal/accounting/record.html?type=" + Constant.accountType.income;
	});*/
	
	// 语言切换
	$("#languageBtn").next().find("a").click(function(){
		var htext = $(this).text();
		$("#languageBtnText").text(htext);
		
		// 设置到会话中
		localStorage.setItem("language", $(this).attr("ivalue"));
		
		location.reload();
	});
	
	// 语言切换后显示
	if(localStorage.getItem("language") && localStorage.getItem("language") != "undefined"){
		var ivalue = localStorage.getItem("language");
		$("#languageBtnText").text($("#languageBtn").next().find('a[ivalue="'+ ivalue +'"]').text());
	}
	
	// 待我审核
	$("#letMeCheckBody").append('<div id="letMecheckLoading" style="width:100%;" align="center"><img src="'+ rootPath +'/theme/images/loading.gif"></div>');
	$.myajax({data:{handleFlag:1,page:{pageIndex:1,pageSize:2}}, url:"/auditflow/query",isLoading:false, callback:function(response){
		$("#letMeCheckBody #letMecheckLoading").remove();
		// 没登录不报错
		if(response && response.code == 0 && response.total > 0){
			$("#moreCheckBtn").show().click(function(){
				window.location.href = "./auditflow.html";
			});
			
			var darr = [];
			darr.push('<table class="table table-hover">');
			$.each(response.result, function(i){
				var ahref = "";
				var typeName = "";
				if(response.result[i].auditType == Constant.auditType.sz){
					// 年度述职
					ahref = "./reportwork/reportaudit.html?flowId="+response.result[i].flowId+"&szId="+response.result[i].auditId;
					typeName = $.i18n.prop("sz");
				}else{
					// 员工转正
					ahref = "./zz/zzaudit.html?flowId="+response.result[i].flowId+"&zzId="+response.result[i].auditId;
					typeName = $.i18n.prop("zz");
				}
				darr.push('<tr>');
				darr.push('<td style="border-top-width: 0px;">'+ response.result[i].submitStaffName +'</td>');
				darr.push('<td style="border-top-width: 0px;">'+ typeName +'</td>');
				darr.push('<td style="border-top-width: 0px;"><a href="'+ahref+'">'+$.i18n.prop("audit")+'</a></td>');
				darr.push('</tr>');
			});
			darr.push('</table>');
			$("#letMeCheckBody").append(darr.join(" "));
		}else{
			$("#letMeCheckNotFound").show();
		}
	}});
	
	// 我的年度述职
	$("#szBody").append('<div id="szLoading" style="width:100%;" align="center"><img src="'+ rootPath +'/theme/images/loading.gif"></div>');
	$.myajax({data:{page:{pageIndex:1,pageSize:1}}, url:"/report/queryme",isLoading:false, callback:function(response){
		// 没登录不报错
		if(response && response.code == 0 && response.total > 0){
			$("#moreSzBtn").show().click(function(){
				window.location.href = "./reportwork/reportList.html";
			});
			
			$("#szBody #szId").text(response.result[0].szId);
			$("#szBody #szQuantum").text(response.result[0].timeQuantum);
			$("#szBody #szState").text($.i18n.prop(Constant.stateMap[response.result[0].state]));
			var szState = response.result[0].state;
			// 查询审核记录
			$.myajax({data:{auditId:response.result[0].szId}, url:"/auditflow/queryall",isLoading:false, callback:function(responseI){
				$("#szBody #szLoading").remove();
				$("#szBodyContent").show();
				
				var darr = [];
				darr.push('<hr><div>');
				// 没登录不报错
				if(responseI && responseI.code == 0 && responseI.total > 0){
					var resultRe = responseI.result.reverse();
					
					$.each(resultRe, function(i){
						var staffName_ = resultRe[i].staffName?resultRe[i].staffName : $.i18n.prop("unknown");
						if(resultRe[i].state && resultRe[i].state == 2){
							darr.push('<div style="color:red;float: left;width: 65px;border:#ddd solid 1px;text-align: center;padding: 4px 4px;margin-top: 5px;">'+staffName_+'</div>');
						}else{
							darr.push('<div style="float: left;width: 65px;border:#ddd solid 1px;text-align: center;padding: 4px 4px;margin-top: 5px;">'+staffName_+'</div>');
						}
						
						darr.push('<div style="float: left;padding: 5px 5px;margin-top: 5px;color:#999;">···</div>');
					});
					
					if(szState != 3){
						darr.push('<div style="float: left;width: 65px;border:#ddd solid 1px;text-align: center;padding: 4px 4px;margin-top: 5px;">?</div>');
					}else{
						darr.pop();
					}
				}else{
					darr.push('<div style="float: left;width: 65px;border:#ddd solid 1px;text-align: center;padding: 4px 4px;margin-top: 5px;">?</div>');
				}
				darr.push('</div>');
				$("#szBodyContent").append(darr.join(" "));
			}});
			
			var darr = [];
			darr.push('');
			$("#szBody").append(darr.join(" "));
		}else{
			$("#szBody #szLoading").remove();
			$("#szNotFound").show();
		}
	}});
	
	// 我的转正申请
	$("#zzBody").append('<div id="zzLoading" style="width:100%;" align="center"><img src="'+ rootPath +'/theme/images/loading.gif"></div>');
	$.myajax({data:{page:{pageIndex:1,pageSize:1}}, url:"/zz/queryme",isLoading:false, callback:function(response){
		// 没登录不报错
		if(response && response.code == 0 && response.total > 0){
			$("#moreZzBtn").show().click(function(){
				window.location.href = "./zz/zzlist.html";
			});
			
			$("#zzBody #zzId").text(response.result[0].zzId);
			$("#zzBody #zzDate").text(response.result[0].zzDate);
			$("#zzBody #zzState").text($.i18n.prop(Constant.stateMap[response.result[0].state]));
			var zzState = response.result[0].state;
			// 查询审核记录
			$.myajax({data:{auditId:response.result[0].zzId}, url:"/auditflow/queryall",isLoading:false, callback:function(responseI){
				$("#zzBody #zzLoading").remove();
				$("#zzBodyContent").show();
				
				var darr = [];
				darr.push('<hr><div>');
				// 没登录不报错
				if(responseI && responseI.code == 0 && responseI.total > 0){
					var resultRe = responseI.result.reverse();
					
					$.each(resultRe, function(i){
						var staffName_ = resultRe[i].staffName?resultRe[i].staffName : $.i18n.prop("unknown");
						if(resultRe[i].state && resultRe[i].state == 2){
							darr.push('<div style="color:red;float: left;width: 65px;border:#ddd solid 1px;text-align: center;padding: 4px 4px;margin-top: 5px;">'+staffName_+'</div>');
						}else{
							darr.push('<div style="float: left;width: 65px;border:#ddd solid 1px;text-align: center;padding: 4px 4px;margin-top: 5px;">'+staffName_+'</div>');
						}
						
						darr.push('<div style="float: left;padding: 5px 5px;margin-top: 5px;color:#999;">···</div>');
					});
					
					if(zzState != 3){
						darr.push('<div style="float: left;width: 65px;border:#ddd solid 1px;text-align: center;padding: 4px 4px;margin-top: 5px;">?</div>');
					}else{
						darr.pop();
					}
				}else{
					darr.push('<div style="float: left;width: 65px;border:#ddd solid 1px;text-align: center;padding: 4px 4px;margin-top: 5px;">?</div>');
				}
				darr.push('</div>');
				$("#zzBodyContent").append(darr.join(" "));
			}});
		}else{
			$("#zzBody #zzLoading").remove();
			$("#zzNotFound").show();
		}
	}});
	
	/*// 验证员工是否登录
	$.myajax({data:{}, url:"/staff/auto", isLoading:false, callback:function(responseStaff){
		if(!$.isEmptyObject(responseStaff)){
			
		}else{
			$("#szNotFound").show();
			$("#zzNotFound").show();
		}
	}});*/
});