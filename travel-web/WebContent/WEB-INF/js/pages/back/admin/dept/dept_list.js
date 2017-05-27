$(function(){

	$(levelBtn).on("click",function(){
		did = $(this).attr("alt") ;
		// console.log(did) ;
		$.post("pages/back/admin/dept/mgr.action",{"did":did},function(data){
			$("#mgr-" + did).empty() ;	// 清空已有的领导信息
			operateAlert(data.trim() == "true","部门领导更新成功！","部门领导更新失败！") ;
			$("#userInfo").modal("toggle") ;
		},"text") ;
	}) ;
	
	$("button[id^=edit-]").each(function(){
		$(this).on("click",function(){
			did = parseInt(this.id.split("-")[1]) ;
			console.log("部门编号：" + did) ;
			dname = $("#dept-" + did).val() ;
			if (dname == "") { 			
				operateAlert(false,"","部门名称不允许为空，请确认后再提交更新！") ;
			} else {
				$.post("pages/back/admin/dept/edit.action",{did:did,dname:dname},function(data){
					operateAlert(data.trim()=="true", "部门名称更新成功！","部门名称更新失败！") ;
				},"text") ;	
			}
		}) ;
	}) ;
	$("span[id^=eid-]").each(function(){
		$(this).on("click",function(){
			eid = this.id.substring(4) ;
			console.log("雇员编号：" + eid) ;
			$.post("pages/back/admin/emp/get.action" ,{eid:eid},function(data){
				console.log(data) ;
				$("#model-photo").attr("src","upload/member/"+data.emp.photo) ;
				$("#levelBtn").attr("alt",data.emp.did) ;
				$("#modal-name").text(data.emp.ename) ;
				$("#modal-level").text(data.level.title) ;
				$("#modal-dept").text(data.dept.dname) ;
				$("#modal-phone").text(data.emp.phone) ;
				$("#modal-hiredate").text(new Date(data.emp.hiredate.time).format("yyyy-MM-dd")) ;
				$("#modal-note").text(data.emp.note) ;
			},"json")
			$("#userInfo").modal("toggle") ;
		}) ;
	}) ;
}) ;