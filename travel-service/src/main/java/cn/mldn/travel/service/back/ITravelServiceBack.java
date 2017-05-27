package cn.mldn.travel.service.back;

import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import cn.mldn.travel.vo.Travel;
import cn.mldn.travel.vo.TravelCost;
import cn.mldn.travel.vo.TravelEmp;

public interface ITravelServiceBack {
	
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:add" }, logical = Logical.OR)
	public Map<String ,Object> listItem() ;
	
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:add" }, logical = Logical.OR)
	public boolean add(Travel vo) ;
	
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public Map<String ,Object> editPre(Long tid) ;
	
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:edit" }, logical = Logical.OR)
	public boolean edit(Travel vo) ;
	
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:self" }, logical = Logical.OR)
	public Map<String ,Object> list(String seid,long currentPage,long lineSize,String column,String keyWord) ;
	
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:delete" }, logical = Logical.OR)
	public boolean delete(Travel vo) ;
	
	@RequiresRoles(value = {"travel"}, logical = Logical.OR) 
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public Map<String ,Object> listType(long tid) ;
	
	@RequiresRoles(value = {"travel"}, logical = Logical.OR) 
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public Map<String,Object> addTravelCost(TravelCost vo) ;

	public boolean deleteTravelCost(long tcid) ;
	
	@RequiresRoles(value = {"travel"}, logical = Logical.OR) 
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public Map<String ,Object> listEmpSplit(long did,long currentPage, long lineSize, String column, String keyWord) throws Exception;
	
	@RequiresRoles(value = {"travel"}, logical = Logical.OR) 
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR) 
	public Map<String,Object> addTravelEmp(TravelEmp vo) ;
	
	@RequiresRoles(value = {"travel"}, logical = Logical.OR) 
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR) 
	public Map<String ,Object> listEmp(long tid) ;
}
