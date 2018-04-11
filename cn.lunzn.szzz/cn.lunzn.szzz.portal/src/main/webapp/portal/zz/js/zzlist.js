$(function(){
	$("#nav_worker").parent().addClass("active");
	
    // 转正申请入口事件
    $("#zzApplyBtn").click(function(){
    	// 已经转正过的员工 不可以再次填写申请
		$.myajax({data:{}, url:"/zz/applycheck", callback:function(response){
			if(parseResult(response)){
				window.location.href = "./zzadd.html";
			}
		}});
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
		id:"zzTable",
		url: "/zz/query",
		type: "pc",
		isLoading: false,
		callback: function(row){
			var state = row.state;
			row.state = $.i18n.prop(Constant.stateMap[state]);
			
			//获取当前操作用户信息
			var userSession = JSON.parse(sessionStorage.getItem("userSession"));
			// 编辑中、驳回的状态-显示编辑
			if((state == Constant.stateType.edit || state == Constant.stateType.reject)
				&& (userSession && row.staffId == userSession.employeeId)){
				row.operation = '<a maction="edit">'+ $.i18n.prop("edit") +'</a>';
			}else{
				// 审核通过、审核中的状态-显示查看
				row.operation = '<a maction="view">'+ $.i18n.prop("view") +'</a>';
			}
			
			// 审核通过状态-显示归档
			if(state == Constant.stateType.pass){
				row.operation = row.operation + '&nbsp;|&nbsp;<a maction="archive">'+ $.i18n.prop("archive") +'</a>';
			}
			
			return {mid:row.zzId}
		},
		maction:{
            edit: function(mid, event){
            	// 编辑
            	window.location.href = "./zzedit.html?zzId=" + mid;
			},
			archive: function(mid, event){
				// 归档
				$.myajax({data:{id:mid}, url:"/archive/zz", callback:function(response){
		            if(parseResult(response)){
		            	window.location.href = encodeURI(rootPath+"/archive/download?fileName="+response.fileName);
		            }
		        }});
			},
			view: function(mid, event){
				// 查看
				window.location.href = "./zzview.html?zzId=" + mid;
			}
		}
	});
	recordTable.load({});
}