$(function() {
	$("#nav_worker").parent().addClass("active");
	
	 // 初始化删除审核人事件
    auditorDelFuc();
    
    // 初始化添加审核人事件
    addAuditor();
	
	// 样例
	$(".glyphicon-question-sign").each(function(){
		new Tippy('#'+$(this).attr("id"), {
			  html: '#'+$(this).next().attr("id"),
			  arrow: true,
			  animation: 'fade',
			  trigger:'click',
			  position:'top',
			  theme:'light',
			  shown: function() {
				  $(".tippy-tooltip").css("visibility","visible");
			  }
		});
	});
	
	// 实例化一个校验类，id选择器
    var checkout1 = new AKCheckout();
    checkout1.addField({id:'staffName', isRequired:true, emptyMsg:$.i18n.prop('check_full_name')});
    checkout1.addField({id:'department', isRequired:true, emptyMsg:$.i18n.prop('check_department')});
    checkout1.addField({id:'position', isRequired:true, emptyMsg:$.i18n.prop('check_position')});
    checkout1.addField({id:'entrydate', isRequired:true, emptyMsg:$.i18n.prop('check_entrydate')});
    checkout1.addField({id:'education', isRequired:true, emptyMsg: $.i18n.prop('check_education')});
    checkout1.addField({id:'gradDate', isRequired:true, emptyMsg: $.i18n.prop('check_grad_date')});
    checkout1.addField({id:'zzDate', isRequired:true, emptyMsg: $.i18n.prop('check_zz_date')});
	checkout1.addField({id:'effort', isRequired:true, emptyMsg: $.i18n.prop('check_zz_effort')});
	checkout1.addField({id:'gain', isRequired:true, emptyMsg: $.i18n.prop('check_zz_gain')});
	checkout1.addField({id:'suggest', isRequired:true, emptyMsg: $.i18n.prop('check_zz_suggest')});
	
	var zzId = $.getUrlParam("zzId");
	
	// 查询当前编辑信息
	$.myajax({data:{zzId:zzId}, url:"/zz/editquery", callback:function(response){
		if(parseResult(response)){
			// 渲染 - 登录员工信息
			// $("#department").val(response.staffInfo.department);
			// $("#position").val(response.staffInfo.position);
			
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
				renderAuditor($("#auditorTags"),JSON.parse(auditor));
			}
			
			// 渲染抄送人
			var cc = zzInfo['cc'];
			if(cc){
				renderAuditor($("#copyAuditorTags"),JSON.parse(cc));
			}
			
			// 显示驳回次数
			$.myajax({data:{state:2, auditId:zzId}, url:"/auditflow/querytotal", isLoading:false, callback:function(response){
				if(response.code == 0){
					if(response.total > 0){
						$("#rejectLabel").text(response.total).show().prev().show();
					}
				}
			}});
		}
	}});

    // 初始化日历插件
    // 设置入职日期
    $("#entryDatePicker").datetimepicker({
        format: "YYYY-MM-DD",
        locale: moment.locale(__lang),
        showTodayButton:true,
        showClear:true
    });
    // 设置毕业日期
    $("#gradDatePicker").datetimepicker({
        format: "YYYY-MM-DD",
        locale: moment.locale(__lang),
        showTodayButton:true,
        showClear:true
    });
    // 设置毕业日期
    $("#zzDatePicker").datetimepicker({
        format: "YYYY-MM-DD",
        locale: moment.locale(__lang),
        showTodayButton:true,
        showClear:true
    });

    // 快速清除当前文本框（disabled文本框除外）
    $(".clear-data").click(function(){
        if (!$(this).prev().attr("value")) {
            $(this).prev().val("").focus();
        }
    });

    /**
     * 提交审核
     */
    $("#submitBtn").click(function(){
		PNotify.removeAll();// 移除提示框
		
		var isChooseAuditor = chooseAuditorCheck(true);
    	if(checkout1.validate() && isChooseAuditor){
    		// 校验成功
    		PNotify.removeAll();// 移除提示框
    		
    		// 准备数据
    		var dataParam = {};
    		$(".form-horizontal *[mkey]").each(function(){
        		dataParam[$(this).attr('mkey')] = $(this).val();
        	});
        	
        	// 审核人
            var auditors = getAuditor($("#auditorTags")); 
            dataParam.auditor = auditors;
            // 抄送人
            var copyAuditors = getAuditor($("#copyAuditorTags")); 
            dataParam.cc = copyAuditors;
        	
        	$.myajax({data:dataParam, url:"/zz/saveandsubmit", callback:function(response){
    			if(parseResult(response)){
    				// 提示成功
    				$.modalTips({context:$.i18n.prop("operation_success"), isShowCloseBtn:false, type:"success", buttons:[{
    					text: $.i18n.prop("confirm"),
    					callfunc: function(){
    						window.location.href = "zzlist.html";
    					}
    				}]
    				});
    			}
    		}});
    	}else{
    		// 校驗失敗，提示
    		new PNotify({
    		    title: $.i18n.prop('check_failed_title'),
    		    text: $.i18n.prop('check_failed_text'),
    		    type: 'error',
    		    styling: 'brighttheme'
    		});
    	}
    });

    /**
     * 保存草稿
     */
    $("#saveBtn").click(function(){
    	PNotify.removeAll();// 移除提示框
    	// 准备数据
    	var dataParam = {};
    	$(".form-horizontal *[mkey]").each(function(){
    		dataParam[$(this).attr('mkey')] = $(this).val();
    	});
    	
    	// 审核人
        var auditors = getAuditor($("#auditorTags")); 
        dataParam.auditor = auditors;
        // 抄送人
        var copyAuditors = getAuditor($("#copyAuditorTags")); 
        dataParam.cc = copyAuditors;
    	
		$.myajax({data:dataParam, url:"/zz/savedraft", callback:function(response){
			if(parseResult(response)){
				// 提示成功
				$.modalTips({context:$.i18n.prop("operation_success"), isShowCloseBtn:false, type:"success", buttons:[{
					text: $.i18n.prop("confirm"),
					callfunc: function(){
						window.location.href = "zzlist.html";
					}
				}]
				});
			}
		}});
    });
    
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
     * 取消申请
     */
    $("#backBtn").click(function(){
    	history.back();
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
					darr.push('<img src="' + getAvatar(flowInfo.staffId) + '" style="width:40px;height:40px;"><br/>');
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
 * 审核人删除图标事件
 */
function auditorDelFuc(){
	 $(".auditor_selected_del").unbind("click");
	 $(".auditor_selected_del").click(function(){
			var $ele = $(this).parent().parent();
			$($ele).remove();
			var $parentEle = $ele.parent();
			var auditors = getAuditor($parentEle);
	    	
	    	renderAuditor($parentEle,auditors);
		})
}

/**
 * 审核人添加事件
 */
function addAuditor(){
	$(".auditor_add").unbind("click");
	$(".auditor_add").click(function(){
		
		// 父级元素
		var $parentEle = $(this).parent();
		
		// 已选审核人信息
		var selectedAuditors = getAuditor($parentEle);
		var auditorIds = [];
		// 编号列表
		$.each(selectedAuditors,function(i,auditor){
			auditorIds.push(auditor.staffId);
		})
		
		chooseAudit({data:{"selectData":auditorIds}, callback:function(staffId, staffName, avatar){
			var auditParam = {};
			auditParam["staffId"] = staffId;// 人事审核人编号
			auditParam["staffName"] = staffName;// 人事审核人名称
			auditParam["avatar"] = avatar;// 人事审核人头像
			
	    	// 页面返回已选的审核人
	    	selectedAuditors.push(auditParam);
	    	
			// 重新渲染审核人
	    	renderAuditor($parentEle,selectedAuditors);
		}});
	})
}

/**
 * 渲染审核人
 */
function renderAuditor($parentEle,auditors){
	if(auditors.length > 0){
		var tagHtml = [];
		$.each(auditors,function(i,auditor){
			tagHtml.push('<div class="auditor_selected" style="width: 70px;">');
			tagHtml.push('<div style="width:45px;margin-left:0px;">');
			tagHtml.push('<img src="'+ auditor.avatar +'" class="auditorImg">');
			tagHtml.push('<span style="display: inline-block;font-size: 12px;" value="'+ auditor.staffId +'">'+auditor.staffName+'</span>');
			tagHtml.push('<span class="auditor_selected_del"></span>');
			tagHtml.push('</div>');
			tagHtml.push('<img src="../../theme/images/auditor/multiselect_btn.png" style="position: relative;top: -50px;margin-left: 50px;border-radius: 4px;">');
			tagHtml.push('</div>');
		});
		
		// 拼接选择审核人的图标
		tagHtml.push('<div class="auditor_add" style="width: 45px;">');
		tagHtml.push('<div style="width:45px;margin-left:0px;">');
		tagHtml.push('<img src="../../theme/images/auditor/selectadd.png" style="width:40px;height: 40px;display: inline-block">');
		tagHtml.push('</div>');
		tagHtml.push('</div>');
		// 清空审核人区域标签
		//$("#auditorTags").empty();
		//$("#auditorTags").append(tagHtml.join(""));
		
		$parentEle.empty();
		$parentEle.append(tagHtml.join(""));
		
		// 重新渲染tag事件
		auditorDelFuc();
		
		addAuditor();
	}
	
	chooseAuditorCheck(false);
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
 * 选择审核人校验
 */
function chooseAuditorCheck(showFlag){
	var auditors = getAuditor($("#auditorTags"));
	if(auditors.length <= 0){
		if(showFlag){
			$("#chooseAuditorTips").show();
		}
		return false;
	}else{
		$("#chooseAuditorTips").hide();
		return true;
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