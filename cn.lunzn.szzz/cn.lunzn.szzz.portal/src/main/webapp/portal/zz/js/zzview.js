$(function() {
	$("#nav_worker").parent().addClass("active");
	var zzId = $.getUrlParam("zzId");    
    // 查询当前编辑信息
	$.myajax({data:{zzId:zzId}, url:"/zz/viewquery", callback:function(response){
		if(parseResult(response)){
			// 渲染 - 登录员工信息
			// $("#department").text(response.staffInfo.department);
			// $("#position").text(response.staffInfo.position);
			
			var zzInfo = response.result[0];
			// 填表日期
			$("#zz_form_date").text(zzInfo['submitDate']);
			
			// 渲染 - 转正申请信息
			$("*[mkey]").each(function(){
				var mkey = $(this).attr("mkey");
				if(zzInfo[mkey]){
					$(this).val(zzInfo[mkey]);
				}
			});
			
			// 渲染审核人
			var auditor = zzInfo['auditor'];
			if(auditor){
				renderAuditor($("#auditorTags"),JSON.parse(auditor), true, zzId);
			}
			
			// 渲染抄送人
			var cc = zzInfo['cc'];
			if(cc){
				renderAuditor($("#copyAuditorTags"),JSON.parse(cc), false);
			}
			
			// 显示驳回次数
			$.myajax({data:{state:2, auditType:2, auditId:zzId}, url:"/auditflow/querytotal", isLoading:false, callback:function(response){
				if(response.code == 0){
					if(response.total > 0){
						$("#rejectLabel").text(response.total).show().prev().show();
					}
				}
			}});
		}
	}});
    
    // 加载历史评定信息，如果有则显示按钮并统计历史评定次数，如果没有则不显示此按钮
	$.myajax({data:{handleFlag:Constant.handleFlag.handle_ok, auditType:2, auditId:zzId}, url:"/auditflow/querytotal", isLoading:false, callback:function(response){
		if(response.code == 0){
			var badgeNum = response.total;
			if(badgeNum > 0){
				$("hr").show();
				$("#historyAuditDiv").show();
				// 加上徽章
				//$("#historyAuditBtn").append('<span class="badge" style="margin-bottom: 4px;">'+ badgeNum +'</span>');
				
				// 绑定历史评定加载事件
				$("#historyAuditBtn").bind("click", function(){
					historyAuditFlow(zzId);
				});
			}
		}
	}});
    
    /**
     * 取消
     */
    $("#backBtn").click(function(){
    	history.back();
    });
    
    // 撤销
	$("#undoBtn").click(function(){
		var dataParam = {};
		dataParam["auditId"] = zzId;
		dataParam["auditType"] = 2;
		$.myajax({
			url: "/auditflow/undo",
			data:dataParam,
			callback: function(response){
				if(parseResult(response)){
					//提示操作成功，跳转到列表页
					$.modalTips({context:$.i18n.prop("5200"), 
						isShowCloseBtn:false, type:"success", 
						buttons:[{
						text: $.i18n.prop("confirm"),
						callfunc: function(){
							window.history.back();
						}
						}]
					});
				}
			}
		});
	});
});

/**
 * 历史评定
 * @param zzId 转正业务id
 */
function historyAuditFlow(zzId){
	// 防止快速重复点击，这里直接解绑click事件
	$("#historyAuditBtn").unbind("click");
	
	// 面板头部显示边框线
	$("#historyAuditBtn").removeAttr("style").css({cursor: "pointer"});
	
	// 加载过一次，下次直接show出来
	if($("#historyAuditDiv").find(".panel-body").length > 0){
		$("#historyAuditBtn").nextAll().show();
		// 折叠
		$("#history-Un-Fold").removeClass("glyphicon-plus").addClass("glyphicon-minus");
		// 重新绑定click事件，折叠
		$("#historyAuditBtn").bind("click", function(){
			// 面板头部不显示边框线
			$("#historyAuditBtn").css({cursor: "pointer",border: "0px"});
			// 隐藏历史评定
			$(this).nextAll().hide();
			// 展开
			$("#history-Un-Fold").removeClass("glyphicon-minus").addClass("glyphicon-plus");
			
			// 重新绑定click事件，展开
			$("#historyAuditBtn").unbind("click").click(function(){
				historyAuditFlow(zzId);
			});
		});
		
		return;
	}
	
	// 显示加载图标
	$("#historyAuditDiv").append('<div id="historyLoading" align="center" class="panel-body"><img src="'+ rootPath +'/theme/images/loading.gif"></div>');
	
	// 查询历史评定数据
	$.myajax({data:{handleFlag:Constant.handleFlag.handle_ok, auditType:2, auditId:zzId}, 
		url:"/auditflow/queryall", isLoading:false, callback:function(response){
		// 删除加载图标
		$("#historyLoading").remove();
		
		if(parseResult(response)){
			// 折叠
			$("#history-Un-Fold").removeClass("glyphicon-plus").addClass("glyphicon-minus");
			// 重新绑定click事件，折叠
			$("#historyAuditBtn").bind("click", function(){
				// 展开
				$("#history-Un-Fold").removeClass("glyphicon-minus").addClass("glyphicon-plus");
				// 面板头部不显示边框线
				$("#historyAuditBtn").css({cursor: "pointer",border: "0px"});
				// 隐藏历史评定
				$(this).nextAll().hide();
				
				// 重新绑定click事件，展开
				$("#historyAuditBtn").unbind("click").bind("click", function(){
					historyAuditFlow(zzId);
				});
			});
			
			// 数组反转
			var resultRe = response.result.reverse();
			// 循环渲染数据
			$.each(resultRe, function(i){
				var flowInfo = resultRe[i];
				var state = flowInfo.state == 1? 3:4;
				var stateStyle = flowInfo.state == 1? 'style="color: green;"':'style="color: red;"';
				state = $.i18n.prop(Constant.stateMap[state]);
				var darr = [];
				// 部门
				if(flowInfo.auditStep == 1){
					// 头部
					darr.push('<div class="panel-heading" align="right" style="border: 0px;">');
					darr.push('<label '+stateStyle+'>'+state+'</label>&nbsp;&nbsp;');
					darr.push('<label>'+flowInfo.finishTime.substring(0,10)+'</label>&nbsp;&nbsp;');
					darr.push('<img src="'+getAvatar(flowInfo.staffId)+'" style="width:40px;height:40px;"><br/>');
					darr.push('<label>'+flowInfo.staffName+'</label>');
					darr.push('</div>');
					
					// 内容
					darr.push('<div class="panel-body">');
					
					var deptInfo = eval('(' + flowInfo.auditValue + ')');
					
					// 等级评定
					darr.push('<div class="row">');
					darr.push('<div class="col-xs-12 col-md-12" style="padding-left: 0px;padding-right: 0px;">');
					darr.push('<div class="col-md-11">');
					darr.push('<h3>'+$.i18n.prop("zz_level_check")+'</h3>');
					darr.push('</div>');
					darr.push('<div class="col-md-1"></div>');
					darr.push('</div>');
					darr.push('</div>');
					darr.push('<div class="row">');
					darr.push('<div class="col-xs-12 col-md-12" style="padding-left: 0px;padding-right: 0px;">');
					darr.push('<div class="col-md-1"></div>');
					darr.push('<div class="col-md-10">');
					darr.push('<div class="view-control" style="font-size: 25px;">');
					// 渲染 ”星星“
					for(var i=1; i<6; i++){
						if(deptInfo.level >= i){
							darr.push('<span class="glyphicon glyphicon-star"></span>&nbsp;&nbsp;');
						}else{
							darr.push('<span class="glyphicon glyphicon-star-empty"></span>&nbsp;&nbsp;');
						}
					}
					darr.push('<label value="4">'+$.i18n.prop("level_"+deptInfo.level)+'</label>');
					darr.push('</div></div></div></div>');
					
					// 转正类型
					darr.push('<div class="row">');
					darr.push('<div class="col-xs-12 col-md-12" style="padding-left: 0px;padding-right: 0px;">');
					darr.push('<div class="col-md-11">');
					darr.push('<h3>'+$.i18n.prop("zz_type_check")+'</h3>');
					darr.push('</div>');
					darr.push('<div class="col-md-1"></div>');
					darr.push('</div>');
					darr.push('</div>');
					darr.push('<div class="row">');
					darr.push('<div class="col-xs-12 col-md-12" style="padding-left: 0px;padding-right: 0px;">');
					darr.push('<div class="col-md-1"></div>');
					darr.push('<div class="col-md-11">');
					darr.push('<div class="view-textare-control">');
					darr.push('<div>');
					if(deptInfo.type == 1){
						darr.push('<input type="radio" value="1" checked="checked" disabled="disabled">&nbsp;');
					}else{
						darr.push('<input type="radio" value="1" disabled="disabled">&nbsp;');
					}
					darr.push('<label>'+$.i18n.prop("zz_type_normal")+'</label>');
					darr.push('</div>');
					darr.push('<div style="margin-top: 10px;">');
					darr.push('<div style="float: left; margin-top: 10px; margin-right: 10px;">');
					if(deptInfo.type == 2){
						darr.push('<input type="radio" value="2" checked="checked" disabled="disabled">&nbsp;');
					}else{
						darr.push('<input type="radio" value="2" disabled="disabled">&nbsp;');
					}
					darr.push('<label>'+$.i18n.prop("zz_type_advance")+'</label>');
					darr.push('</div>');
					darr.push('<div style="float: left;">');
					if(deptInfo.type == 2){
						darr.push('<input type="text" class="form-control width_150" readonly="readonly" value="'+deptInfo.typeVal+'">');
					}else{
						darr.push('<input type="text" class="form-control width_150" readonly="readonly">');
					}
					darr.push('</div>');
					darr.push('</div>');
					darr.push('<div style="clear: both;"></div>');
					darr.push('<div style="margin-top: 10px;">');
					darr.push('<div style="float: left; margin-top: 10px; margin-right: 10px;">');
					if(deptInfo.type == 3){
						darr.push('<input type="radio" value="3" checked="checked" disabled="disabled">&nbsp;');
					}else{
						darr.push('<input type="radio" value="3" disabled="disabled">&nbsp;');
					}
					darr.push('<label>'+$.i18n.prop("zz_type_later")+'</label>');
					darr.push('</div>');
					darr.push('<div style="float: left;">');
					if(deptInfo.type == 3){
						darr.push('<input type="text" class="form-control width_150" readonly="readonly" value="'+deptInfo.typeVal+'">');
					}else{
						darr.push('<input type="text" class="form-control width_150" readonly="readonly">');
					}
					darr.push('</div>');
					darr.push('</div>');
					darr.push('<div style="clear: both;"></div>');
					darr.push('<div style="margin-top: 10px;">');
					darr.push('<div style="float: left; margin-top: 10px; margin-right: 36px;">');
					if(deptInfo.type == 4){
						darr.push('<input type="radio" value="4" checked="checked" disabled="disabled">&nbsp;');
					}else{
						darr.push('<input type="radio" value="4" disabled="disabled">&nbsp;');
					}
					darr.push('<label>'+$.i18n.prop("zz_type_dismiss")+'</label>');
					darr.push('</div>');
					darr.push('<div style="float: left;">');
					if(deptInfo.type == 4){
						darr.push('<input type="text" class="form-control width_150" readonly="readonly" value="'+deptInfo.typeVal+'">');
					}else{
						darr.push('<input type="text" class="form-control width_150" readonly="readonly">');
					}
					darr.push('</div>');
					darr.push('</div>');
					darr.push('<div style="clear: both;"></div>');
					darr.push('<div style="margin-top: 10px;">');
					darr.push('<div style="float: left; margin-top: 10px; margin-right: 36px;">');
					if(deptInfo.type == 5){
						darr.push('<input type="radio" value="5" checked="checked" disabled="disabled">&nbsp;');
					}else{
						darr.push('<input type="radio" value="5" disabled="disabled">&nbsp;');
					}
					darr.push('<label>'+$.i18n.prop("zz_type_transfer")+'</label>');
					darr.push('</div>');
					darr.push('<div style="float: left;">');
					if(deptInfo.type == 5){
						darr.push('<input type="text" class="form-control width_150" readonly="readonly" value="'+deptInfo.typeVal+'">');
					}else{
						darr.push('<input type="text" class="form-control width_150" readonly="readonly">');
					}
					darr.push('</div></div></div></div></div></div>');
					// 评语
					darr.push('<div class="row">');
					darr.push('<div class="col-xs-12 col-md-12" style="padding-left: 0px;padding-right: 0px;">');
					darr.push('<div class="col-md-11">');
					darr.push('<h3>'+$.i18n.prop("zz_comment")+'</h3>');
					darr.push('</div>');
					darr.push('<div class="col-md-1"></div>');
					darr.push('</div>');
					darr.push('</div>');
					darr.push('<div class="row">');
					darr.push('<div class="col-xs-12 col-md-12" style="padding-left: 0px;padding-right: 0px;">');
					darr.push('<div class="col-md-1"></div>');
					darr.push('<div class="col-md-10">');
					darr.push('<textarea class="form-control" rows="5" readonly="readonly">'+deptInfo.comment+'</textarea>');
					darr.push('</div></div></div></div>');
				}else{
					// 人事
					darr.push('<div class="panel-heading" align="right" style="border: 0px;">');
					darr.push('<label '+stateStyle+'>'+state+'</label>');
					darr.push('<label>'+flowInfo.staffName+'</label>');
					darr.push('<label>'+flowInfo.finishTime.substring(0,10)+'</label>');
					darr.push('</div>');
					darr.push('<div class="panel-body" >');
					darr.push('<div class="row">');
					darr.push('<div class="col-xs-12 col-md-12" style="padding-left: 0px;padding-right: 0px;">');
					darr.push('<div class="col-md-11">');
					darr.push('<h3>'+$.i18n.prop("zz_comment")+'</h3>');
					darr.push('</div>');
					darr.push('<div class="col-md-1"></div>');
					darr.push('</div>');
					darr.push('</div>');
					darr.push('<div class="row">');
					darr.push('<div class="col-xs-12 col-md-12" style="padding-left: 0px;padding-right: 0px;">');
					darr.push('<div class="col-md-1"></div>');
					darr.push('<div class="col-md-10">');
					darr.push('<textarea class="form-control" readonly="readonly" rows="5">'+flowInfo.auditValue+'</textarea>');
					darr.push('</div>');
					darr.push('</div>');
					darr.push('</div>');
					darr.push('</div>');
				}
				$("#historyAuditDiv").append(darr.join(" "));
			});
		}
	}});
}

/**
 * 渲染审核人
 */
function renderAuditor($parentEle,auditors,isAudit,auditId){
	if(auditors.length > 0){
		var tagHtml = [];
		if(isAudit){
			$.myajax({data:{auditId:auditId,auditType:2}, 
				url:"/auditflow/queryall", isLoading:false, callback:function(response){
					if(parseResult(response) && response.result){
						var auditStep = response.result[0].auditStep;
						var handleFlag = response.result[0].handleFlag;
						var state = response.result[0].state;
						if(state != 2){
							for(var i=1; i<=auditStep; i++){
								if(handleFlag == 1 && auditStep == i){
									auditors[i-1]["auditFlag"] = '<span style="display:inline-block;font-size:12px;color:red;">'+$.i18n.prop("option_audit")+'</span>';
								}else{
									auditors[i-1]["auditFlag"] = '<span style="display:inline-block;font-size:12px;color:green;">'+$.i18n.prop("option_pass")+'</span>';
								}
							}
						}
						
						$.each(auditors, function(i,auditor){
							tagHtml.push('<div class="auditor_selected" style="width: 70px;">');
							tagHtml.push('<div style="width:45px;margin-left:0px;">');
							tagHtml.push('<img src="'+ auditor.avatar +'" class="auditorImg">');
							tagHtml.push('<span style="display: inline-block;font-size: 12px;" value="'+ auditor.staffId +'">'+auditor.staffName+'</span>');
							var topMag = "-50px";
							if(auditor.auditFlag){
								topMag = "-70px";
								tagHtml.push(auditor.auditFlag);
							}
							
							tagHtml.push('</div>');
							tagHtml.push('<img src="../../theme/images/auditor/multiselect_btn.png" style="position: relative;top: '+topMag+';margin-left: 50px;border-radius: 4px;">');
							tagHtml.push('</div>');
						});
						$parentEle.empty();
						$parentEle.append(tagHtml.join(""));
						
						//移除最后一个连接图片
					    $parentEle.find(".auditor_selected:last").find("img:last-child").remove();
					}
				}
			});
			
		}else{
			$.each(auditors,function(i,auditor){
				tagHtml.push('<div class="auditor_selected" style="width: 70px;">');
				tagHtml.push('<div style="width:45px;margin-left:0px;">');
				tagHtml.push('<img src="'+ auditor.avatar +'" class="auditorImg">');
				tagHtml.push('<span style="display: inline-block;font-size: 12px;" value="'+ auditor.staffId +'">'+auditor.staffName+'</span>');
				tagHtml.push('</div>');
				tagHtml.push('<img src="../../theme/images/auditor/multiselect_btn.png" style="position: relative;top: -50px;margin-left: 50px;border-radius: 4px;">');
				tagHtml.push('</div>');
			});
			
			$parentEle.empty();
			$parentEle.append(tagHtml.join(""));
			
			//移除最后一个连接图片
		    $parentEle.find(".auditor_selected:last").find("img:last-child").remove();
		}
	}
}

/**
 * 获取页面已选的审核人信息
 */
function getAuditor($parentEle){
	var $selectEle = $parentEle.find(".auditor_selected");
	var auditors = [];
	$.each($selectEle,function(i,item){
		var imgPath = $(item).find("img").attr("src");
		var auditorId = $(item).find("span").eq(0).attr("value");
		var auditorName = $(item).find("span").eq(0).text();
		auditors.push({
			"avatar":imgPath,
			"staffId":auditorId,
			"staffName":auditorName
		});
	})
	
	return auditors;
}

/**
 * 获取审核人头像
 * @param staffId 审核人编号
 */
function getAvatar(staffId){
	var auditors = getAuditor($("#auditorTags"));
	// 头像路径
	var avatar;
	$.each(auditors, function(i, auditor) {
		if (staffId == auditor.staffId) {
			avatar = auditor.avatar;
		}
	})
	return avatar;
}