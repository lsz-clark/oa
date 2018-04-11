/**
 * 选择审核人
 * 
 * @param option
 */
function chooseAudit(option){
	if(!option.data){
		option["data"] = {};
	}
	$("#modal-audit-person").remove();
	// 渲染选择审核人html
	var hs = [];
	hs.push('<div class="modal fade" id="modal-audit-person" tabindex="-1" role="dialog">');
	hs.push('<div class="modal-dialog modal-md" role="document">');
	hs.push('<div class="modal-content">');
	
	hs.push('<div class="modal-header" style="padding: 10px;">');
	hs.push('<label class="modal-title">'+$.i18n.prop('choose_audit')+'</label>');
	hs.push('<button type="button" class="close" data-dismiss="modal"> <span class="glyphicon glyphicon-remove"></span> </button>');
	hs.push('</div> ');
	
	hs.push('<div class="modal-body"  style="padding: 15px;">');
	hs.push('<div class="row"><div class="col-xs-12 col-md-12">');
	hs.push('<div class="form-group col-md-12">');
	hs.push('<div class="col-md-10" style="margin-bottom: 5px;">');
	hs.push('<div class="input-group">');
	hs.push('<input class="form-control" maxlength="20" type="text" id="name" placeholder="'+$.i18n.prop('full_name')+'">');
	hs.push('<span class="input-group-addon clear-data">');
	hs.push('<span class="glyphicon glyphicon-remove"></span>');
	hs.push('</span>');
	hs.push('</div></div>');
	hs.push('<div class="form-group col-md-2" align="right">');
	hs.push('<button id="chooseAuditQueryBtn" class="btn btn-danger" type="button">'+$.i18n.prop('query')+'</button>');
	hs.push('</div>');
	
	hs.push('</div></div>');
	
	// 表格
	hs.push('<div align="center">');
	hs.push('<table id="chooseAuditTable" class="table table-hover table-striped" style="width:98%;margin-bottom:5px;">');
	hs.push('<thead><tr>');
	hs.push('<th mkey="radio">'+$.i18n.prop('choose')+'</th>');
	hs.push('<th mkey="employeeId" mdisplay="false" style="padding-bottom: 10px;display:none;">'+$.i18n.prop('id')+'</th>');
	hs.push('<th mkey="avatar" mcustomize="myAvatarRender" style="padding-bottom: 10px;">'+$.i18n.prop('avatar')+'</th>');
	hs.push('<th mkey="name" style="padding-bottom: 10px;">'+$.i18n.prop('full_name')+'</th>');
	hs.push('<th mkey="position" style="padding-bottom: 10px;">'+$.i18n.prop('position')+'</th>');
	hs.push('<th mkey="department" style="padding-bottom: 10px;">'+$.i18n.prop('department')+'</th>');
	hs.push('</tr></thead>');
	hs.push('<tbody></tbody>');
	hs.push('</table>');
	hs.push('</div>');
	
	hs.push('</div>');
	
	hs.push('<div class="modal-footer"  style="padding: 10px;">');
	hs.push('<label id="pleaseAuditTips" style="color:red;display:none;">'+ $.i18n.prop('please_choose_audit_person') +'</label>&nbsp;&nbsp;');
	hs.push('<button type="button" id="chooseAuditOk" class="btn btn-danger">'+$.i18n.prop('confirm')+'</button>');
	hs.push('<button type="button" class="btn btn-default" data-dismiss="modal">'+$.i18n.prop('cancel')+'</button>');
	hs.push('</div>');
	hs.push('</div></div></div>');
	
	// 渲染
	$("body").after(hs.join(" "));
	
	// 显示
	$("#modal-audit-person").modal("show");
	
	// 绑定按钮事件
	$("#chooseAuditOk").click(function(){
		// 必须选择一个人
		var staffId = $("span.choose-click[mchecked='checked']").attr("value");
		var staffName = $("span.choose-click[mchecked='checked']").parent().parent().find("td:eq(3)").text();
		var avatar = $("span.choose-click[mchecked='checked']").parent().parent().find("td:eq(2) img").attr("src");
		if(staffId){
			option.callback(staffId, staffName, avatar);
			$("#modal-audit-person").modal("hide");
		}else{
			// 提示错误
			//$.modalTips({context:, type:"error"});
			$("#chooseAuditOk").prev().fadeOut().fadeIn(500);
		}
	});
	
	// 初始化表格
    var chooseAuditTable = new MyTableUtil();
    
    chooseAuditTable.init({
		id:"chooseAuditTable",
		url: "/staff/query",
		type: "pc",
		isLoading: false,
		isLocalPage:true,
		callback: function(row){
			var isSelected = false;
			if(option.data.hasOwnProperty("selectData")){
				$.each(option.data.selectData, function(i, staffId){
					if(staffId == row.employeeId){
						isSelected = true;
					}
				});
			}
			if(isSelected){
				row.radio = '<span class="glyphicon glyphicon-ok-circle" style="font-size: 18px;color: gray;"></span>';
			}else{
				//row.radio = '<input type="radio" name="staffRadio" value="'+ row.employeeId +'">';
				row.radio = '<span class="glyphicon glyphicon-unchecked choose-click" value="'+ row.employeeId +'" mchecked="" style="font-size: 18px;color: red;"></span>';
			}
			return {mid:row.employeeId};
		},
		localQuery: function(response, tableInfo){
			// 姓名模糊查询
			var name = $("#modal-audit-person").find("#name").val();
			var result = [];
			$.each(response.result, function(i){
				if(response.result[i].name.indexOf(name) != -1){
					result.push(response.result[i]);
				}
			});
			
			if(result.length > tableInfo.data.page.pageSize){
				response.total = result.length;
				
				var a = (tableInfo.data.page.pageIndex -1) * tableInfo.data.page.pageSize;
				var b = a + tableInfo.data.page.pageSize;
				result = result.slice(a, b);
				
				response.result = result;
			}else{
				response.total = result.length;
				response.result = result;
			}
			
			return response;
		},
		myAvatarRender:function(data){
			return '<img src="'+ data.avatar +'" style="width:40px;height:40px;border:0px;">';
		}
	});
    
    option.data.page = {};
    option.data.page["pageSize"] = 5;
    option.data.page["pageIndex"] = 1;
	chooseAuditTable.load(option.data);
	
	$("#chooseAuditQueryBtn").click(function(){
		$("#chooseAuditOk").prev().hide();
		option.data.page["pageSize"] = 5;
	    option.data.page["pageIndex"] = 1;
		chooseAuditTable.reload(option.data);
	});
	bindEnterKey($("#chooseAuditQueryBtn"));
	$(".clear-data").click(function(){
    	$(this).prev().val("").focus();
    });
}