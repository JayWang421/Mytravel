package cn.mldn.travel.action.back;

import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.mldn.travel.exception.DeptManagerExistException;
import cn.mldn.travel.exception.LevelNotEnoughException;
import cn.mldn.travel.service.back.IEmpServiceBack;
import cn.mldn.travel.util.ActionSplitPageUtil;
import cn.mldn.travel.vo.Emp;
import cn.mldn.util.action.abs.AbstractBaseAction;
import cn.mldn.util.enctype.PasswordUtil;
import cn.mldn.util.web.FileUtils;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/pages/back/admin/emp/*")
public class EmpActionBack extends AbstractBaseAction {
	private static final String FLAG = "雇员";
	
	@Resource
	private IEmpServiceBack empServiceBack ;

	@RequestMapping("add_pre")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	public ModelAndView addPre() throws Exception{
		ModelAndView mav = new ModelAndView(super.getUrl("emp.add.page"));
		Map<String ,Object> map=this.empServiceBack.addPre() ;
		mav.addAllObjects(map) ;
		return mav;
	}
 
	@RequestMapping("add")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	public String add(Emp vo,MultipartFile pic, HttpServletRequest request) throws Exception{
		FileUtils fu=null ;
		if(!(pic==null || pic.isEmpty())){
			fu=new FileUtils(pic) ;
			vo.setPhoto(fu.createFileName()) ;
			vo.setHiredate(new Date());
			vo.setLocked(0);
			vo.setIneid(super.getEid()) ;
		}
		try{
			if(this.empServiceBack.add(vo)){
				if(!(pic==null || pic.isEmpty())){
					fu.saveFile(request, "upload/member/", vo.getPhoto()) ;
				}
				super.setUrlAndMsg(request, "emp.add.action", "vo.add.success", FLAG);
			}else{
				super.setUrlAndMsg(request, "emp.add.action", "vo.add.faliure", FLAG);
			}
		}catch (DeptManagerExistException e) {
			super.setUrlAndMsg(request, "emp.add.action", "emp.add.dept.mgr.failure");
		}catch (LevelNotEnoughException e) {
			super.setUrlAndMsg(request, "emp.add.action", "level.not.enough.failure");
		}
		return super.getMsg("back.forward.page");
	}

	@RequestMapping("edit_pre")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	public ModelAndView editPre(String eid) throws Exception{
		ModelAndView mav = new ModelAndView(super.getUrl("emp.edit.page"));
		Map<String ,Object> map=this.empServiceBack.editPre(eid) ;
		mav.addAllObjects(map) ;
		return mav;
	}

	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	public String edit(Emp vo, MultipartFile pic, HttpServletRequest request) throws Exception{
		FileUtils fu=null ;
		if(!(pic==null || pic.isEmpty())){
			fu=new FileUtils(pic) ;
			vo.setPhoto(fu.createFileName()) ;
			vo.setIneid(super.getEid()) ;
			if (!(vo.getPassword() == null || "".equals(vo.getPassword()))) { // 要修改密码 
				vo.setPassword(PasswordUtil.getPassword(vo.getPassword())); // 密码加密处理 
			} else { 
				vo.setPassword(null); // “”字符串问题 
			}
		}
		
		System.out.println(vo);
		try{
			if(this.empServiceBack.edit(vo)){
				if(!(pic==null || pic.isEmpty())){
					fu.saveFile(request, "upload/member/", vo.getPhoto()) ;
				}
				super.setUrlAndMsg(request, "emp.list.action", "vo.edit.success", FLAG);
			}else{
				super.setUrlAndMsg(request, "emp.list.action", "vo.edit.faliure", FLAG);
			}
		}catch (LevelNotEnoughException e) {
			super.setUrlAndMsg(request, "emp.list.action", "level.not.enough.failure");
		}
		return super.getMsg("back.forward.page");
	}

	@RequestMapping("get")
	@RequiresUser
	@RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
	@RequiresPermissions(value = { "emp:get", "empshow:get" }, logical = Logical.OR)
	public void get(String eid, HttpServletResponse response) {
		JSONObject obj=new JSONObject() ;
		Map<String,Object> map=this.empServiceBack.get(eid) ;
		obj.put("emp", map.get("emp")) ;
		obj.put("level", map.get("level")) ;
		obj.put("dept", map.get("dept")) ;
		super.print(response, obj);
	}

	@RequestMapping("list")
	@RequiresUser
	@RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
	@RequiresPermissions(value = { "emp:list", "empshow:list" }, logical = Logical.OR)
	public ModelAndView list(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView(super.getUrl("emp.list.page"));
		ActionSplitPageUtil aspu=new ActionSplitPageUtil(request, "雇员编号:eid|雇员姓名:ename|联系电话:phone", super.getMsg("emp.list.action")) ;
		Map<String ,Object> map=this.empServiceBack.list(aspu.getCurrentPage(),  aspu.getLineSize(), aspu.getColumn(), aspu.getKeyWord());
		mav.addAllObjects(map) ;
		return mav;
	}

	@RequestMapping("delete")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:delete")
	public ModelAndView delete(String ids, HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		System.out.println(super.handleStringIds(ids));
		if(this.empServiceBack.delete(super.handleStringIds(ids), super.getEid())) {
			super.setUrlAndMsg(request, "emp.list.action", "vo.delete.success", FLAG);
		}else{
			super.setUrlAndMsg(request, "emp.list.action", "vo.delete.success", FLAG);
		}
		return mav;
	}
}
