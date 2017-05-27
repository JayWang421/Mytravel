package cn.mldn.travel.service.back;

import java.util.Map;
import java.util.Set;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import cn.mldn.travel.vo.Emp;

public interface IEmpServiceBack {
	/**
	 * 根据雇员id获得雇员完整信息
	 * @param eid 雇员编号
	 * @return 包括如下内容：<br>
	 * 1、key = emp、value = 雇员对象，如果该雇员不存在返回null<br>
	 * 2、如果雇员信息可以查询成功，则查询对应级别信息。key = level
	 */
	public Map<String,Object> get(String eid,String password) ;
	/**
	 * 根据雇员编号取得雇员对应的角色与权限数据信息，该操作主要执行如下功能：<br>
	 * 1、调用IRoleDAO.findAllIdByEmp()查询出所有的角色标记信息；<br>
	 * 2、调用IActionDAO.findAllIdByEmp()查询出所有的权限标记信息；<br>
	 * @param eid 要查询角色和权限的雇员编号
	 * @return 返回Map集合数据包含有如下内容：<br>
	 * 1、key = allRoles、value = 所有的角色标记信息；<br>
	 * 2、key = allActions、value = 所有的权限标记信息；<br>
	 */
	public Map<String,Set<String>> listRoleAndAction(String eid) ;
	
	public Emp listByDept(Long did)throws Exception ;
	
	public Map<String,Object> get(String eid) ;
	
	public Map<String ,Object> list(Long currentPage, Long lineSize, String column, String keyWord) throws Exception ;
	
	public Map<String,Object> addPre()throws Exception ;
	public boolean add(Emp vo)throws Exception ;
	
	public Map<String,Object> editPre(String eid) throws Exception  ;
	
	@RequiresRoles(value = {"emp"}, logical = Logical.OR) 
	@RequiresPermissions(value = {"dept:edit","emp:edit"}, logical = Logical.AND)
	public boolean edit(Emp vo) throws Exception  ;
	
	public boolean delete(Set<String> ids,String heid) throws Exception ;
}
