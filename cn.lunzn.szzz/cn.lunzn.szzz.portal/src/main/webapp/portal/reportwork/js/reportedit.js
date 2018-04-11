//员工id
var staffId ;
// 校验类
var checkout1 ;
// szId
var szIdTmp;

$(function(){
	//获取界面传递述职id
	var szId=window.location.search.slice(window.location.search.lastIndexOf("?")+1);
	if(szId){
		szIdTmp = szId;
		//设置驳回次数
		setRejectTime(szId);
		//查询年度述职信息，判断状态，如果是编辑则展示编辑界面，如果是查看则展示查看界面
		queryReportWorkInfo(szId);
		initAuditHistory(szId);
	}else{
		//新增
		addReportWork();
	}
	
	// 初始化删除审核人事件
    auditorDelFuc();
    
    // 初始化添加审核人事件
    addAuditor();
});

/**
 * 初始化历史审核记录控件
 * @param szId
 * @returns
 */
function initAuditHistory(szId){
    // 加载历史评定信息，如果有则显示按钮并统计历史评定次数，如果没有则不显示此按钮
	$.myajax({data:{handleFlag:Constant.handleFlag.handle_ok, auditId:szId,auditType:1}, url:"/auditflow/querytotal", isLoading:false, callback:function(response){
		if(response.code == 0){
			var badgeNum = response.total;
			if(badgeNum > 0){
//				$("hr").show();
				$("#historyAuditDiv").show();
//				// 加上徽章
//				$("#historyAuditBtn").append('<span class="badge" style="margin-bottom: 4px;">'+ badgeNum +'</span>');
				
				// 绑定历史评定加载事件
				$("#historyAuditBtn").bind("click", function(){
					historyAuditFlow(szId);
				});
			}
		}
	}});
}
function initCheckOut(){
	// 实例化一个校验类，id选择器
    checkout1 = new AKCheckout();
    checkout1.addField({id:'staffName', isRequired:true, emptyMsg:$.i18n.prop('check_full_name')});
    checkout1.addField({id:'department', isRequired:true, emptyMsg:$.i18n.prop('check_department')});
    checkout1.addField({id:'position', isRequired:true, emptyMsg:$.i18n.prop('check_position')});
    checkout1.addField({id:'entrydate', isRequired:true, emptyMsg:$.i18n.prop('check_entrydate')});
    checkout1.addField({id:'education', isRequired:true, emptyMsg: $.i18n.prop('check_education')});
    checkout1.addField({id:'gradDate', isRequired:true, emptyMsg: $.i18n.prop('check_grad_date')});
    checkout1.addField({id:'reportdate', isRequired:true, emptyMsg: $.i18n.prop('check_sz_date')});
	checkout1.addField({id:'summaryText', isRequired:true, emptyMsg: $.i18n.prop('check_sz_summary')});
	checkout1.addField({id:'appraisalText', isRequired:true, emptyMsg: $.i18n.prop('check_sz_appraisal')});
	checkout1.addField({id:'betterText', isRequired:true, emptyMsg: $.i18n.prop('check_sz_better')});
	checkout1.addField({id:'planText', isRequired:true, emptyMsg: $.i18n.prop('check_sz_plan')});
}

/**
 * 查询驳回次数，并且展示
 * @returns
 */
function setRejectTime(szId){
	var dataParam = {};
	dataParam["szId"] = szId;
	$.myajax({
		url: "/report/queryRejectTime",
		data:dataParam,
		callback: function(response){
			if(response.msg == 'success' && response.result[0].rejectTime && response.result[0].rejectTime>0){
				$("#rejectTime").show();
				$("#rejectTime").text($.i18n.prop("reject_time")+response.result[0].rejectTime);
			}
		}
	});
}

/**
 * 隐藏提示控件
 * @returns
 */
function hideQuestionSign(){
	//隐藏提示控件
	$(".glyphicon-question-sign").each(function(){
		$(this).css("display", "none");
	});
}

/**
 * 编辑
 * @returns
 */
function editReportWork(reportWorkInfo){
	//驳回次数
	//述职信息
	setReportWorkInfo(reportWorkInfo);
	//初始化日期插件
	initDate();
	//提示信息初始化
	initTips();
	//绑定提交按钮事件
	boundSubmitBtn();
	//初始化校验器
	initCheckOut();
}

/**
 * 新增
 * @returns
 */
function addReportWork(){
	//驳回次数
	$("#rejectTime").text($.i18n.prop("reject_time")+"0");
	//设置员工基本信息
	queryEmploeeInfo();
	//填表日期
	$("#date").text($.i18n.prop("report_form_date")+getDate());
	//初始化日期插件
	initDate();
	//提示信息初始化
	initTips();
	//绑定提交按钮事件
	boundSubmitBtn();
	//初始化校验器
	initCheckOut();
}
/**
 * 绑定提交按钮事件
 */
function boundSubmitBtn(){
	$("#submitBtn").click(function(){
		PNotify.removeAll();// 移除提示框
		// 审核人校验
		var isChooseAuditor = chooseAuditorCheck(true);
		if(checkout1.validate() && isChooseAuditor){
    		// 校验成功
    		PNotify.removeAll();// 移除提示框
			saveData(true);
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
	$("#saveDraftBtn").click(function(){
		saveData(false);
	});
}
function saveData(isSubmit){
	var dataParam = {};
//	$("*[mkey]").each(function(){
//		dataParam[$(this).attr('mkey')] = $(this).val();
//	});
	//编辑才有szId
	if(szIdTmp){
		dataParam["szId"] = szIdTmp;
	}else{
		//填表日期---只有新增时才需要该参数
		dataParam["submitdate"] = getDate();
	}
	//是否需要提交电子流
	dataParam["isSubmit"] = isSubmit;
	//员工id
	dataParam["staffId"] = staffId;
	//姓名
	dataParam["staffName"] = $("#staffName").val();
	//部门
	dataParam["department"] = $("#department").val();
	//职位
	dataParam["position"] = $("#position").val();
	//入职日期
	dataParam["entryDate"] = $("#entrydate").val();
	//学历
	dataParam["education"] = $("#education").val();
	//毕业日期
	dataParam["gradDate"] = $("#gradDate").val();
	//述职年份
	dataParam["timeQuantum"] = $("#reportdate").val();
	//工作总结
	dataParam["workresults"] = $("#summaryText").val();
	//自我评价
	dataParam["evaluation"] = $("#appraisalText").val();
	//不足&改进
	dataParam["deficiency"] = $("#betterText").val();
	//成长规划
	dataParam["plan"] = $("#planText").val();
	//审核人
	var chooseAuditors = getAuditor($("#auditorTags"));
	if(chooseAuditors.length > 0){
		dataParam["auditor"] = chooseAuditors;
	}
	//抄送人
	var chooseCcs = getAuditor($("#copyAuditorTags"));
	if(chooseCcs.length > 0){
		dataParam["cc"] = chooseCcs;
	}
	
	$.myajax({
		url: "/report/saveReportWorkInfo",
		data:dataParam,
		callback: function(response){
			if(parseResult(response)){
				//提示操作成功，跳转到列表页
				$.modalTips({context:$.i18n.prop("operation_success"), 
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
}
/**
 * 初始化日期插件
 */
function initDate () {
    // 设置入职日期
    $("#entryDatePicker").datetimepicker({
        format: "YYYY-MM-DD",
        locale: moment.locale('zh-cn'),
        showTodayButton:true,
        showClear:true
    });
    // 设置毕业日期
    $("#gradDatePicker").datetimepicker({
        format: "YYYY-MM-DD",
        locale: moment.locale('zh-cn'),
        showTodayButton:true,
        showClear:true
    });
    // 设置述职年份
    $("#reportDatePicker").datetimepicker({
        format: "YYYY",
        locale: moment.locale('zh-cn'),
        showTodayButton:true,
        showClear:true
    });
};
/**
 * 查询述职基本信息
 * @returns
 */
function queryReportWorkInfo(szId){
	var dataParam = {};
	dataParam["szId"] = szId;
	dataParam["audit"] = "audit";
	$.myajax({
		url: "/report/queryReportList",
		data:dataParam,
		callback: function(response){
			if(parseResult(response)){
				var reportWorkInfo = response.result[0];
				if(!reportWorkInfo){
					//请求正确，但是没有查到数据，异常场景，直接返回
					return;
				}
				if(reportWorkInfo.state==1 || reportWorkInfo.state==4){
					//编辑
					editReportWork(reportWorkInfo);
					
					// 渲染审核人、抄送人
					if(reportWorkInfo.auditor){
						renderAuditor($("#auditorTags"), JSON.parse(reportWorkInfo.auditor));
					}
					if(reportWorkInfo.cc){
						renderAuditor($("#copyAuditorTags"), JSON.parse(reportWorkInfo.cc));
					}
				}
				//设置员工基本信息
				setBaseInfo(reportWorkInfo);
				disableBaseInfo();
			}else{
				//提示无法获取用户信息
				return;
			}
		}
	});
}
function setReportWorkInfo(reportWorkInfo){
	//填表日期
	$("#date").text($.i18n.prop("report_form_date")+reportWorkInfo.submitdate);
	//入职日期
	$("#entrydate").val(reportWorkInfo.entryDate);
	//学历
	$("#education").val(reportWorkInfo.education);
	//毕业日期
	$("#gradDate").val(reportWorkInfo.gradDate);
	//述职年份
	$("#reportdate").val(reportWorkInfo.timeQuantum);
	//工作总结
	$("#summaryText").val(reportWorkInfo.workresults);
	//自我评价
	$("#appraisalText").val(reportWorkInfo.evaluation);
	//不足&改进
	$("#betterText").val(reportWorkInfo.deficiency);
	//成长规划
	$("#planText").val(reportWorkInfo.plan);
}
/**
 * 查询员工信息
 * @returns
 */
function queryEmploeeInfo(){
	$.myajax({
		url: "/report/queryEmployeeInfo",
		callback: function(response){
			if(response.employeeId){
				setBaseInfo(response);
				disableBaseInfo();
			}else{
				//提示无法获取用户信息
			}
		}
	});
}

/**
 * 基本信息给值
 * @returns
 */
function setBaseInfo(baseInfo){
	//员工id
	if(baseInfo.employeeId){
		staffId = baseInfo.employeeId;
	}else{
		staffId = baseInfo.staffId;
	}
	//姓名
	if(baseInfo.name){
		$("#staffName").val(baseInfo.name);
	}else{
		$("#staffName").val(baseInfo.staffName);
	}
	//部门
	$("#department").val(baseInfo.department);
	//职位
	$("#position").val(baseInfo.position);
}

/**
 * 基本信息输入框置灰
 * @returns
 */
function disableBaseInfo(){
	//姓名
	$("#staffName").attr("readonly", "readonly");
	//部门
	$("#department").attr("readonly", "readonly");
	//职位
	$("#position").attr("readonly", "readonly");
}

/**
 * 获取当前日期
 * @returns
 */
function getDate(){
//   var mydate = new Date();
//   var str = "" + mydate.getFullYear() + "-";
//   str += (mydate.getMonth()+1) + "-";
//   str += mydate.getDate();
//   return str;
	return new Date().Format("yyyy-MM-dd");
}
//对Date的扩展，将 Date 转化为指定格式的String
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
//例子： 
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function (fmt) { //author: meizz 
 var o = {
     "M+": this.getMonth() + 1, //月份 
     "d+": this.getDate(), //日 
     "h+": this.getHours(), //小时 
     "m+": this.getMinutes(), //分 
     "s+": this.getSeconds(), //秒 
     "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
     "S": this.getMilliseconds() //毫秒 
 };
 if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
 for (var k in o)
 if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
 return fmt;
}

/**
 * 提示信息初始化
 */
function initTips(){
//工作总结
//$("#tipsText").text("工作总结---dsfd     \r\n" +
//		"满意我对我的工作非常的满意我对我的工作非常的满意我对我的" +
//		"工作非常的满意我对我的工作非常的满意我对我的工作非常的满" +
//		"意我对我的工作非常的满意");
new Tippy('#summaryTips', {
    html: '#exmple--gain', 
    arrow: true,
    animation: 'fade',
    position:'top',
    theme:'light',
    trigger:'click',
    shown: function() {
        $(".tippy-tooltip").css("visibility","visible");
       }
 });
//自我评价
new Tippy('#appraisalTips', {
    html: '#exmple--gain1', 
    arrow: true,
    animation: 'fade',
    position:'top',
    theme:'light',
    trigger:'click',
    shown: function() {
        $(".tippy-tooltip").css("visibility","visible");
       }
 });
//不足&改进
new Tippy('#betterTips', {
    html: '#exmple--gain2', 
    arrow: true,
    animation: 'fade',
    position:'top',
    theme:'light',
    trigger:'click',
    shown: function() {
        $(".tippy-tooltip").css("visibility","visible");
       }
 });
//成长规划
new Tippy('#planTips', {
    html: '#exmple--gain3', 
    arrow: true,
    animation: 'fade',
    position:'top',
    theme:'light',
    trigger:'click',
    shown: function() {
     $(".tippy-tooltip").css("visibility","visible");
    }
 });
}

/**
 * 历史评定
 * @param szId 转正业务id
 */
function historyAuditFlow(szId){
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
				historyAuditFlow(szId);
			});
		});
		
		return;
	}
	
	// 显示加载图标
	$("#historyAuditDiv").append('<div id="historyLoading" align="center" class="panel-body"><img src="'+ rootPath +'/theme/images/loading.gif"></div>');
	
	// 查询历史评定数据
	$.myajax({data:{handleFlag:Constant.handleFlag.handle_ok, auditId:szId,auditType:1}, 
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
					historyAuditFlow(szId);
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
				
				var auditorAvatar = $('img.auditorImg[imgStaffId="'+ flowInfo.staffId +'"]').attr("src");

				var darr = [];
				darr.push('<div class="panel-heading" align="right" style="border: 0px;">');
				
				darr.push('<label '+stateStyle+'>'+state+'</label>&nbsp;&nbsp;');
				darr.push('<label>'+flowInfo.finishTime.substring(0,10)+'</label>&nbsp;&nbsp;');
				darr.push('<img src="'+auditorAvatar+'" style="width:40px;height:40px;"><br/>');
				darr.push('<label>'+flowInfo.staffName+'</label>');
				
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
			//var $selectEle = $(".auditor_selected");
			var $parentEle = $ele.parent();
			var auditors = getAuditor($parentEle);
			/*$.each($selectEle,function(i,item){
	    		var imgPath = $(item).find("img").attr("src");
	    		var auditorId = $(item).find("span").eq(0).attr("value");
	    		var auditorName = $(item).find("span").eq(0).text();
	    		auditors.push({
	    			"avatar":imgPath,
	    			"staffId":auditorId,
	    			"staffName":auditorName
	    		});
	    	})*/
	    	
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
			tagHtml.push('<img src="'+ auditor.avatar +'" imgStaffId="'+ auditor.staffId +'" class="auditorImg">');
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
		
		$parentEle.empty();
		$parentEle.append(tagHtml.join(""));
		
		// 重新渲染tag事件
		auditorDelFuc();
		
		addAuditor();
	}
	// 校验是否选择了审核人
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