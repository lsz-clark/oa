$(function(){
	$("#nav_audit").parent().addClass("active");
	
	// 开始时间
    $("#starttime").datetimepicker({
        format: "YYYY-MM-DD",
        locale: moment.locale(__lang),
        showTodayButton:true,
        showClear:true
    });
    
    // 结束时间
    $("#endtime").datetimepicker({
        format: "YYYY-MM-DD",
        locale: moment.locale(__lang),
        showTodayButton:true,
        showClear:true
    });
    
	// 触发时间选择事件
    $(".glyphicon-calendar").parent().click(function(){
    	$(this).prev("input").focus();
    });
    
    // 加载表格
    var recordTable = new MyTableUtil();
    loadDefaultTable(recordTable);
    
    // 查询按钮绑定事件
    $("#queryBtn").click(function(){
    	var dataParam = {};
    	$("input[mkey]").each(function(){
    		dataParam[$(this).attr("mkey")] = $(this).val();
    	});
    	$("select[mkey]").each(function(){
    		if($(this).val() != 0){
    			dataParam[$(this).attr("mkey")] = $(this).val();
    		}
    	});
    	
    	recordTable.reload(dataParam);
    });
    
    // 绑定enter键
    bindEnterKey($("#queryBtn"));
});

function loadDefaultTable(recordTable){
	recordTable.init({
		id:"auditFlowTable",
		url: "/auditflow/query",
		type: "pc",
		isLoading: false,
		callback: function(row){
			var auditType = row.auditType;
			if(row.state){
				row.state = row.state == 1? 3:4;
				row.state = $.i18n.prop(Constant.stateMap[row.state]);
			}
			
			row.beginTime = row.beginTime.substring(0,10);
			
			row.auditType = $.i18n.prop(Constant.auditTypeMap[row.auditType]);
			
			var handleFlag = row.handleFlag;
			
			row.handleFlag = $.i18n.prop(Constant.handleFlagMap[handleFlag]);
			
			if(handleFlag == Constant.handleFlag.handle_no){
				row.operation = '<a maction="audit">'+ $.i18n.prop("audit") +'</a>';
			}else{
				row.operation = '<a maction="view">'+ $.i18n.prop("view") +'</a>';
			}
			
			return {mid:'{flowId:'+row.flowId+', auditId:'+row.auditId+', auditType:'+auditType+'}'};
		},
		maction:{
			audit: function(dataStr, event){
				var data = eval('(' + dataStr + ')');
				if(data.auditType == Constant.auditType.sz){
					// 年度述职
					window.location.href = "./reportwork/reportaudit.html?flowId="+data.flowId+"&szId="+data.auditId;
				}else{
					// 员工转正
					window.location.href = "./zz/zzaudit.html?flowId="+data.flowId+"&zzId="+data.auditId;
				}
			},
			view: function(dataStr, event){
				var data = eval('(' + dataStr + ')');
				if(data.auditType == Constant.auditType.sz){
					// 年度述职
					window.location.href = "./reportwork/reportview.html?"+data.auditId;
				}else{
					// 员工转正
					window.location.href = "./zz/zzview.html?zzId="+data.auditId;
				}
			}/*,
			view: function(mid, event){
				// 删除确认
				$.modalTips({
					context: $.i18n.prop("are_you_sure_delete"),
					buttons:[{text:$.i18n.prop("confirm"), callfunc:function(){
						$.myajax({
							data: {id: mid},
							url: "/account/record/del",
							callback: function(response){
								if(parseResult(response)){
									$.modalTips({type:"success"});
									// 重新刷新表格
									recordTable.refresh();
								}
							}
						});
					}}]
				});
			}*/
		}
	});
	recordTable.load({});
}