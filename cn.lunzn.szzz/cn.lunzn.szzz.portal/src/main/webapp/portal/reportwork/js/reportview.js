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
		
		// 显示历史审核记录
		initAuditHistory(szId);
	}
	
	// 撤销
	$("#undoBtn").click(function(){
		var dataParam = {};
		dataParam["auditId"] = szId;
		dataParam["auditType"] = 1;
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
 * 查看
 * @returns
 */
function showReportWork(reportWorkInfo){
	//述职信息
	setReportWorkInfo(reportWorkInfo);
	//提交按钮隐藏
	$("#submitBtn").css('display','none');
	$("#saveDraftBtn").css('display','none');
	//禁用所有输入框
	$("*[mkey]").each(function(){
		$(this).attr("readonly", "readonly");
	});
	//隐藏提示控件
	hideQuestionSign();
}

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
				//查看
				showReportWork(reportWorkInfo);
				// 审核人
				if(reportWorkInfo.auditor){
					renderAuditorView($("#auditorTags"), JSON.parse(reportWorkInfo.auditor), true, szId);
				}
				// 抄送人
				if(reportWorkInfo.cc){
					renderAuditorView($("#copyAuditorTags"), JSON.parse(reportWorkInfo.cc), false);
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
				darr.push('<img src="'+ auditorAvatar +'" style="width:40px;height:40px;"><br/>');
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
 * 渲染审核人
 */
function renderAuditorView($parentEle, auditors, isAudit, auditId){
	if(auditors.length > 0){
		var tagHtml = [];
		
		if(isAudit){
			$.myajax({data:{auditId:auditId,auditType:1}, 
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
							tagHtml.push('<img src="'+ auditor.avatar +'" imgStaffId="'+ auditor.staffId +'" class="auditorImg">');
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
			$.each(auditors, function(i,auditor){
				tagHtml.push('<div class="auditor_selected" style="width: 70px;">');
				tagHtml.push('<div style="width:45px;margin-left:0px;">');
				tagHtml.push('<img src="'+ auditor.avatar +'" imgStaffId="'+ auditor.staffId +'" class="auditorImg">');
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