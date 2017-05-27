$(function(){
	$(selall).on("click",function(){
		$(":checkbox[id^=eid-]").each(function(){
			$(this).prop("checked",$(selall).prop("checked")) ;
		}) ;
	}) ;
	$(deleteBtn).on("click",function(){
		ids = "" ;
		$(":checkbox[id^=eid-]").each(function(){
			if($(this).prop("checked")) {
			ids += $(this).val() + "," ;
			}	
		}) ;
		if (ids == "") { 
			operateAlert(false,"","您还未选择任何要删除的数据！") ;
		} else {
			window.location = "pages/back/admin/emp/delete.action?ids=" + ids ;
		}
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
})
