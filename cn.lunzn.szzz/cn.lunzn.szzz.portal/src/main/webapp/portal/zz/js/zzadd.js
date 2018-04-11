$(function() {
    $("#nav_worker").parent().addClass("active");

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
    
    // 给文本输入框设置默认值
    initText();
    
    // 初始化删除审核人事件
    auditorDelFuc();
    
    // 初始化添加审核人事件
    addAuditor();

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
     * 给所有的文本输入框设置默认值
     */
    function initText () {
        // 查询后台用户登录信息
        $.myajax({data:{}, url:"/zz/applyquery", callback:function(response){
            if(response.code == 0){
                $("#staffName").val(response.data.name);
                $("#department").val(response.data.department);
                $("#position").val(response.data.position);
                
                // 填表日期
                $("#zz_form_date").text(response.data.formDate);
            }
        }});
    };

    /**
     * 提交审核
     */
    $("#submitBtn").click(function(){
		PNotify.removeAll();// 移除提示框
		
		// 审核人校验
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
            if(copyAuditors.length > 0){
            	 dataParam.cc = copyAuditors;
            }
            
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
        // 准备数据
        var dataParam = {};
        $(".form-horizontal *[mkey]").each(function(){
            dataParam[$(this).attr('mkey')] = $(this).val();
        });
        
        // 审核人
        var auditors = getAuditor($("#auditorTags")); 
        if(auditors.length > 0){
        	dataParam.auditor = auditors;
        }
        
        // 抄送人
        var copyAuditors = getAuditor($("#copyAuditorTags")); 
        if(copyAuditors.length > 0){
       	 dataParam.cc = copyAuditors;
        }
        
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

    /**
     * 取消申请
     */
    $("#backBtn").click(function(){
        history.back();
    });
});

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
			
			// 获取页面已选的审核人
			/*var $selectEle = $(".auditor_selected");
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
	    	})*/
	    	
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