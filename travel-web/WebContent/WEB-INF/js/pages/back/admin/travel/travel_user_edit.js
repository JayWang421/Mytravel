did=1 ;

$(function(){
	$(addBtn).on("click",function(){
		// Ajax异步读取用户信息
		// 将异步加载信息填充到模态窗口的组件之中
		$("#userInfo").modal("toggle") ;	// 显示模态窗口
	}) ;
	
	$("#did").on("change",function(){
		did=$(this).val() ;
		loadData() ;
	}) ;
	
})

function loadData() {	// 该函数名称一定要固定，不许修改
	// 如果要想进行分页的处理列表前首先查询出部门编号
	did = $("#did").val() ;	// 取得指定组件的value
	//tid = $("#tid").val() ;
	console.log("部门编号：" + did) ;
	$.post("pages/back/admin/travel/user_edit_list.action", {
		"did" : did,
		"cp" : jsCommonCp, 
		"ls" : jsCommonLs,
		//"tid" : tid
	}, function(data) {
		console.log(data);
		$("#empTable tr:gt(0)").remove() ;
		for (var x = 0 ; x < data.allEmps.length ; x ++) {
			addTableRow(data.allEmps[x].photo,data.allEmps[x].eid,data.allEmps[x].ename,data.allEmps[x].sal,data.allEmps[x].lid) ;
		}
		createSplitBar(data.allRecorders) ;	// 创建分页控制项
	}, "json");
}

function addTableRow(photo,eid,ename,sal,lid){
	level="普通员工" ;
	if(lid=="manager"){
		level="部门经理" ;
	}
	if(lid=="chief"){
		level="总监" ;
	}
	empInfo="						<tr id='travel-"+eid+"'>" +
	"									<td class='text-center'>"+
	"										<img src=upload/member/"+photo+" style='width:20px;'/> "+
	"									</td>"+
	"									<td class='text-center'>"+eid+"</td>"+
	"									<td class='text-center'>"+ename+"</td>"+
	"									<td class='text-center'>￥"+sal+"</td>"+
	"									<td class='text-center'>"+level+"</td>"+
	"									<td class='text-center'>"+
	"										<button class='btn btn-danger btn-xs' id='addEmp-"+eid+"'>"+
	"											<span class='glyphicon glyphicon-remove'></span>&nbsp;添加</button>"+
	"									</td>"+
	"								</tr>" ;
	$("#empTable tbody").append(empInfo) ;
	
	$("#addEmp-"+eid).on("click",function(){
		tid=$("#tid").val() ;
		$.post("pages/back/admin/travel/user_edit_add.action",{"eid":eid,"tid":tid},function(data){
			addEmpInfo="								<tr id='travel-"+data.emp.eid+"'>"+
						"									<td class='text-center'>"+
						"										<img src='upload/member/"+data.emp.photo+"' style='width:20px;'/> "+
						"									</td>"+
						"									<td class='text-center'>"+data.emp.eid+"</td>"+
						"									<td class='text-center'>"+data.emp.ename+"</td>"+
						"									<td class='text-center'>￥"+data.emp.sal+"</td>"+
						"									<td class='text-center'>"+data.level.title+"</td>"+
						"									<td class='text-center'>"+data.dept.dname+"</td>"+
						"									<td class='text-center'>"+
						"										<button class='btn btn-danger btn-xs' id='remove-"+data.emp.eid+"-"+tid+"'>"+
						"											<span class='glyphicon glyphicon-remove'></span>&nbsp;移除</button>"+
						"									</td>"+
						"								</tr> " ;
			$("#addEmpTable tbody").append(addEmpInfo) ;
		},"json") ;
	})
}









