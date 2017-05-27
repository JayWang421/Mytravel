package cn.mldn.travel.action.back;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.mldn.travel.service.back.IDeptServiceBack;
import cn.mldn.travel.vo.Dept;
import cn.mldn.util.action.abs.AbstractBaseAction;

@Controller
@RequestMapping("/pages/back/admin/dept/*")
public class DeptActionBack extends AbstractBaseAction {

	@Resource
	private IDeptServiceBack deptServiceBack;

	@RequestMapping("list")
	@RequiresUser
	@RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
	@RequiresPermissions(value = { "dept:list", "deptshow:list" }, logical = Logical.OR)
	public ModelAndView list() throws Exception {
		ModelAndView mav = new ModelAndView(super.getUrl("dept.list.page"));
		Map<String, Object> map = this.deptServiceBack.list();
		mav.addAllObjects(map);
		return mav;
	}

	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("dept:edit")
	public void edit(Dept vo, HttpServletResponse response) throws Exception {
			super.print(response, this.deptServiceBack.edit(vo));
		
	}
	
	@RequestMapping("mgr")
	@RequiresUser
	@RequiresRoles(value = {"emp"}, logical = Logical.OR) 
	@RequiresPermissions(value = {"dept:edit","emp:edit"}, logical = Logical.AND)
	public ModelAndView editLevel(HttpServletResponse response,Long did) throws Exception {
		super.print(response, this.deptServiceBack.editLevel(did, super.getEid()));
		return null;
	}
	
}
