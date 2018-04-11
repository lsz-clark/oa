$(function(){
	//操作栏样式
	$("#nav_accounting").parent().addClass("active");
	
	// 实例化一个校验类，id选择器
    var checkout1 = new AKCheckout();
    checkout1.addField({id:'submitStaffName',isRequired:true,validType:'AK_LC[1,20]',emptyMsg:'请输入姓名',invalidMsg:'由数字、字母、下划线、横线、小数点组成，长度范围在{0}-{1}之间。'});
    
//	// 触发时间选择事件
//    $(".glyphicon-calendar").parent().click(function(){
//    	$(this).prev("input").focus();
//    });
//    

    
    // 加载表格
    var recordTable = new MyTableUtil();
    loadDefaultTable(recordTable);
    
    // 查询按钮绑定事件
    $("#queryBtn").click(function(){
//    	if(checkout1.validate()){
////    		alert("校验成功！");
//    		return false;
//    	}
    	
    	var dataParam = {};
//    	$("input[mkey]").each(function(){
//    		dataParam[$(this).attr("mkey")] = $(this).val();
//    	});
		dataParam["staffName"] = $("#staffName").val();
		dataParam["state"] = $("#state").val();
    	recordTable.reload(dataParam);
    });

    // 述职按钮绑定事件
    $("#reportBtn").click(function(){
    	window.location.href=rootPath +'/portal/reportwork/reportedit.html';
	});
	
});


function loadDefaultTable(recordTable){
	recordTable.init({
		id:"reportWorkTable",
		url: "/report/queryReportList",
		type: "pc",
		isLoading: false,
		callback: function(row){
			
			row.reportState = $.i18n.prop(Constant.stateMap[row.state]);
			
//			var handleFlag = row.handleFlag;
			
//			row.handleFlag = $.i18n.prop(Constant.handleFlagMap[handleFlag]);
			//获取当前操作用户信息
			var userSession = JSON.parse(sessionStorage.getItem("userSession"));
			//审核状态 1-编辑中2-待审批 3-审核通过 4-驳回
			if((Constant.stateType.edit == row.state || Constant.stateType.reject == row.state)
					&& (userSession && row.staffId == userSession.employeeId)){
				row.operation = '<a maction="edit">'+ $.i18n.prop("edit") +'</a>';
			}else{
				row.operation = '<a maction="view">'+ $.i18n.prop("view") +'</a>';
			}
			
			// 审核通过状态-显示归档
			if(row.state == Constant.stateType.pass){
				row.operation = row.operation + '&nbsp;|&nbsp;<a maction="archive">'+ $.i18n.prop("archive") +'</a>';
			}
			
			return {mid:[row.szId]}
		},
		maction:{
			edit: function(mid, type, event){
		    	window.location.href=rootPath +'/portal/reportwork/reportedit.html?'+mid;
			},
			archive: function(mid, event){
				// 归档
			    $.myajax({data:{id:mid}, url:"/archive/sz", callback:function(response){
			              if(parseResult(response)){
			               window.location.href = encodeURI(rootPath+"/archive/download?fileName="+response.fileName);
			              }
			          }});
			},
			view: function(mid, event){
		    	window.location.href=rootPath +'/portal/reportwork/reportview.html?'+mid;
			}
		}
	});
	recordTable.load({});
}