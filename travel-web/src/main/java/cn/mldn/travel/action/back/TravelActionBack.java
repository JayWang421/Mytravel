package cn.mldn.travel.action.back;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.mldn.travel.service.back.ITravelServiceBack;
import cn.mldn.travel.util.ActionSplitPageUtil;
import cn.mldn.travel.vo.Dept;
import cn.mldn.travel.vo.Level;
import cn.mldn.travel.vo.Travel;
import cn.mldn.travel.vo.TravelCost;
import cn.mldn.travel.vo.TravelEmp;
import cn.mldn.travel.vo.Type;
import cn.mldn.util.action.abs.AbstractBaseAction;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/pages/back/admin/travel/*")
public class TravelActionBack extends AbstractBaseAction {
	private static final String FLAG = "出差申请";
	
	@Resource
	private ITravelServiceBack travelServiceBack ;

	
	@RequestMapping("add_pre")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:add" }, logical = Logical.OR)
	public ModelAndView addPre() {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.add.page"));
		Map<String ,Object> map=this.travelServiceBack.listItem() ;
		mav.addAllObjects(map) ;
		return mav;
	}

	@RequestMapping("add")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:add" }, logical = Logical.OR)
	public ModelAndView add(HttpServletRequest request,Travel vo) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		vo.setSeid(super.getEid());
		if(this.travelServiceBack.add(vo)){
			super.setUrlAndMsg(request, "travel.add.action", "vo.add.success", FLAG);
		}else{
			super.setUrlAndMsg(request, "travel.add.action", "vo.add.failure",FLAG);
		}
		return mav;
	}
	
	
	@RequestMapping("list_emp")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:self" }, logical = Logical.OR)
	public ModelAndView listEmp(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.emp.page"));
		ActionSplitPageUtil aspu=new ActionSplitPageUtil(request, "申请标题:title", super.getMsg("travel.emp.action")) ;
		Map<String ,Object> map=this.travelServiceBack.list(super.getEid(),aspu.getCurrentPage(),  aspu.getLineSize(), aspu.getColumn(), aspu.getKeyWord());
		mav.addAllObjects(map) ;
		return mav;
	}
	
	
	@RequestMapping("list_self")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:self" }, logical = Logical.OR)
	public ModelAndView listSelf(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.self.page"));
		ActionSplitPageUtil aspu=new ActionSplitPageUtil(request, "申请标题:title", super.getMsg("travel.self.action")) ;
		Map<String ,Object> map=this.travelServiceBack.list(super.getEid(),aspu.getCurrentPage(),  aspu.getLineSize(), aspu.getColumn(), aspu.getKeyWord());
		mav.addAllObjects(map) ;
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("user_edit_pre")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public ModelAndView editUser(long tid) {
		System.out.println(this.travelServiceBack.editEcount(tid)) ;
		ModelAndView mav = new ModelAndView(super.getUrl("travel.user.page"));
		Map<String,Object> map=this.travelServiceBack.listEmp(tid) ;
		List<Dept> allDepts=(List<Dept>) map.get("allDepts") ;
		Iterator<Dept> iter1=allDepts.iterator() ;
		Map<Long ,String > deptMap=new HashMap<>() ;
		while(iter1.hasNext()){
			Dept dept=iter1.next() ;
			deptMap.put(dept.getDid(), dept.getDname()) ;
		}
		map.put("deptMap", deptMap) ;
		List<Level> allLevels=(List<Level>) map.get("allLevels") ;
		Iterator<Level> iter2=allLevels.iterator() ;
		Map<String ,String> levelMap=new HashMap<>() ;
		while(iter2.hasNext()){
			Level level=iter2.next() ;
			levelMap.put(level.getLid(), level.getTitle()) ;
		}
		map.put("levelMap", levelMap) ;
		map.put("tid", tid) ;
		mav.addAllObjects(map) ;
		return mav;
	}

	@RequestMapping("user_edit_list")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:self" }, logical = Logical.OR)
	public ModelAndView listEdit(HttpServletResponse response,HttpServletRequest request,long did)throws Exception {
		ActionSplitPageUtil aspu=new ActionSplitPageUtil(request, "部门名称:dname", super.getMsg("travel.user.action")) ;
		Map<String ,Object> map=this.travelServiceBack.listEmpSplit(did,aspu.getCurrentPage(),  aspu.getLineSize(), aspu.getColumn(), aspu.getKeyWord());
		JSONObject obj=new JSONObject() ;
		obj.put("allRecorders", map.get("allRecorders"));
		obj.put("allEmps", map.get("allEmps"));
		super.print(response, obj);
		return null;
	}
	
	@RequestMapping("user_edit_add")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public ModelAndView addEmp(HttpServletResponse response,TravelEmp vo) {
		JSONObject obj=new JSONObject() ;
		obj.putAll(this.travelServiceBack.addTravelEmp(vo)) ;
		super.print(response, obj);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("add_cost_pre")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public ModelAndView addCostPre(long tid) {
		System.out.println(this.travelServiceBack.editTotal(tid)) ;
		ModelAndView mav = new ModelAndView(super.getUrl("travel.cost.page"));
		Map<String ,Object> map=this.travelServiceBack.listType(tid) ;
		mav.addAllObjects(map) ;
		List<Type> allTypes=(List<Type>) map.get("allTypes") ;
		Iterator<Type> iter=allTypes.iterator() ;
		Map<Long ,String > typeMap=new HashMap<>() ;
		while(iter.hasNext()){
			Type type=iter.next() ;
			typeMap.put(type.getTpid(), type.getTitle()) ;
		}
		mav.addObject("allTypes", typeMap) ;
		mav.addObject("tid", tid) ;
		
		return mav;
	}
	
	@RequestMapping("add_cost")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public ModelAndView addCost(HttpServletResponse response,TravelCost vo) {
		JSONObject obj=new JSONObject() ;
		obj.putAll(this.travelServiceBack.addTravelCost(vo)) ;
		super.print(response, obj);
		return null;
	}
	
	@RequestMapping("edit_cost_pre")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public ModelAndView editCostPre(Long tpid) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.edit.page"));
		//mav.addAllObjects(this.travelServiceBack.editCostPre(tpid)) ;
		return mav;
	}
	
	@RequestMapping("delete_cost")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public ModelAndView deleteCost(HttpServletResponse response,long tcid) {
		super.print(response, this.travelServiceBack.deleteTravelCost(tcid));
		return null;
	}
	
	@RequestMapping("edit_pre")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public ModelAndView editPre(Long tid) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.edit.page"));
		mav.addAllObjects(this.travelServiceBack.editPre(tid)) ;
		return mav;
	}
	 
	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public ModelAndView edit(HttpServletRequest request,Travel vo) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		vo.setSeid(super.getEid());
		System.out.println("++++++++++++++");
		if(this.travelServiceBack.edit(vo)){
			super.setUrlAndMsg(request, "travel.self.action", "vo.edit.success", FLAG);
		}else{
			super.setUrlAndMsg(request, "travel.self.action", "vo.edit.failure",FLAG);
		}
		return mav;
	}
	
	@RequestMapping("delete")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:delete" }, logical = Logical.OR)
	public ModelAndView delete(HttpServletRequest request,Travel vo) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		vo.setSeid(super.getEid());
		if(this.travelServiceBack.delete(vo)){
			super.setUrlAndMsg(request, "travel.self.action", "vo.delete.success", FLAG);
		}else{
			super.setUrlAndMsg(request, "travel.self.action", "vo.delete.failure",FLAG);
		}
		return mav;
	}
	
	@RequestMapping("submit")
	@RequiresUser
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:submit" }, logical = Logical.OR)
	public ModelAndView submit(HttpServletRequest request,long tid) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		if(this.travelServiceBack.editUpdate(tid)){
			super.setUrlAndMsg(request, "travel.self.action", "travel.submit.success");
		}else{
			super.setUrlAndMsg(request, "travel.self.action", "travel.submit.failure");
		}
		return mav;
	}
}
