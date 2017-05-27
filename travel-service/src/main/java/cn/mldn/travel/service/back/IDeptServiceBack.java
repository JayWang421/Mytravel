package cn.mldn.travel.service.back;

import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import cn.mldn.travel.vo.Dept;


public interface IDeptServiceBack {
	
	public Map<String ,Object> list()throws Exception  ;
	
	public boolean edit(Dept vo)throws Exception ;
	
	@RequiresRoles(value = {"emp"}, logical = Logical.OR) 
	@RequiresPermissions(value = {"dept:edit","emp:edit"}, logical = Logical.AND)
	public boolean editLevel(Long did ,String eid)throws Exception ;
}
