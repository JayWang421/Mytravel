package cn.mldn.travel.service.back.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Service;

import cn.mldn.travel.dao.IActionDAO;
import cn.mldn.travel.dao.IDeptDAO;
import cn.mldn.travel.dao.IEmpDAO;
import cn.mldn.travel.dao.ILevelDAO;
import cn.mldn.travel.dao.IRoleDAO;
import cn.mldn.travel.exception.DeptManagerExistException;
import cn.mldn.travel.exception.LevelNotEnoughException;
import cn.mldn.travel.service.back.IEmpServiceBack;
import cn.mldn.travel.vo.Dept;
import cn.mldn.travel.vo.Emp;

@Service
public class EmpServiceBackImpl implements IEmpServiceBack {
	@Resource
	private IDeptDAO deptDAO;
	@Resource
	private IEmpDAO empDAO;
	@Resource
	private IRoleDAO roleDAO;
	@Resource
	private IActionDAO actionDAO;
	@Resource
	private ILevelDAO levelDAO;

	@Override
	public Map<String, Object> get(String eid, String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		Emp emp = this.empDAO.findById(eid);
		if (emp != null) {
			if (password.equals(emp.getPassword())) {
				map.put("level", this.levelDAO.findById(emp.getLid()));
			}
		}
		map.put("emp", emp);
		return map;
	}

	@Override
	public Map<String, Set<String>> listRoleAndAction(String eid) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		map.put("allRoles", this.roleDAO.findAllIdByEmp(eid));
		map.put("allActions", this.actionDAO.findAllIdByEmp(eid));
		return map;
	}

	@Override
	public Emp listByDept(Long did) throws Exception {
		return this.empDAO.findByDept(did);
	}

	@Override
	public Map<String, Object> get(String eid) {
		Map<String, Object> map = new HashMap<String, Object>();
		Emp emp = this.empDAO.findById(eid);
		if (emp != null) {
			map.put("level", this.levelDAO.findById(emp.getLid()));
			map.put("dept", this.deptDAO.findById(emp.getDid()));
		}
		map.put("emp", emp);
		return map;
	}

	@Override
	public Map<String, Object> list(Long currentPage, Long lineSize, String column, String keyWord) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("allDepts", this.deptDAO.findAllByEmp());
		map.put("allLevels", this.levelDAO.findAllByEmp());
		if (column == null || keyWord == null || "".equals(keyWord) || "".equals(column)) {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("start", (currentPage - 1) * lineSize);
			map1.put("lineSize", lineSize);
			map.put("allEmps", this.empDAO.findAllBySplitLocked(map1));
			map.put("allRecorders", this.empDAO.getAllCountByLocked(map1));
		} else {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("start", (currentPage - 1) * lineSize);
			map1.put("lineSize", lineSize);
			map1.put("column", column);
			map1.put("keyWord", "%" + keyWord + "%");
			map.put("allEmps", this.empDAO.findAllBySplitLocked(map1));
			map.put("allRecorders", this.empDAO.getAllCountByLocked(map1));
		}
		return map;
	}

	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	@Override
	public Map<String, Object> addPre() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("allDepts", this.deptDAO.findAll());
		map.put("allLevels", this.levelDAO.findAll());
		return map;
	}

	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	@Override
	public boolean add(Emp vo) throws Exception {
		if (this.empDAO.findById(vo.getEid()) != null) {
			return false;
		}
		// 取得操作者的相关信息
		Emp humanEmp = this.empDAO.findById(vo.getIneid());
		// 判断当前的操作者是否是人事部门
		if (humanEmp.getDid().equals(2L)) {
			Dept dept = this.deptDAO.findById(vo.getDid());
			if ("manager".equals(vo.getLid())) {
				if ("manager".equals(humanEmp.getLid())) {
					if (dept.getEid() == null) {
						this.empDAO.doUpdateLidById(vo.getDid());
						Map<String, Object> map = new HashMap<>();
						map.put("eid", vo.getEid());
						map.put("did", vo.getDid());
						if (this.deptDAO.duUpdateEidByDid(map)) {
							return this.empDAO.doCreate(vo);
						}
					} else {
						throw new DeptManagerExistException("该部门已经有经理了，无法进行新任经理的添加！");
					}
				}else {
					throw new LevelNotEnoughException("对不起，您没有操作权限！！");
				}
			} else {
				return this.empDAO.doCreate(vo);
			}
		}
		return false;
	}

	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	@Override
	public Map<String, Object> editPre(String eid) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("emp", this.empDAO.findById(eid));
		map.put("allDepts", this.deptDAO.findAll());
		map.put("allLevels", this.levelDAO.findAll());
		return map;
	}

	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	@Override
	public boolean edit(Emp vo) throws Exception {
		if (this.empDAO.findById(vo.getEid()) == null) {
			return false;
		}
		Emp oldEmp=this.empDAO.findById(vo.getEid()) ;
		// 取得操作者的相关信息
		Emp humanEmp = this.empDAO.findById(vo.getIneid());
		// 判断当前的操作者是否是人事部门
		if (humanEmp.getDid().equals(2L)) {
			if ("manager".equals(vo.getLid())) {
				if ("manager".equals(humanEmp.getLid())) {
					
					if("manager".equals(oldEmp.getLid())){ //一个部门的经理换为别的部门的经理
						if(oldEmp.getDid()!=vo.getDid()){
							Emp emp1=new Emp() ;
							emp1.setEid(this.deptDAO.findById(vo.getDid()).getEid());
							emp1.setLid("staff");
							Dept dept=new Dept() ;//以前的部门
							dept.setDid(oldEmp.getDid());
							Dept dept1=new Dept() ;//现在的部门
							dept1.setDid(vo.getDid());
							dept1.setEid(vo.getEid());
							if(this.empDAO.doUpdateLevel(emp1)){
								if(this.deptDAO.doUpdateManager(dept1) && this.deptDAO.doUpdateManager(dept)){
									return this.empDAO.doUpdate(vo) ;
								}
							}
						}else{
							Emp emp1=new Emp() ;
							emp1.setEid(this.deptDAO.findById(oldEmp.getDid()).getEid());
							emp1.setLid("staff");
							if(this.empDAO.doUpdateLevel(emp1)){
								return this.empDAO.doUpdate(vo) ;
							}
						}
					}else{
						Emp emp1=new Emp() ;
						emp1.setEid(this.deptDAO.findById(vo.getDid()).getEid());
						emp1.setLid("staff");
						Dept dept=new Dept() ;
						dept.setDid(vo.getDid());
						dept.setEid(vo.getEid());
						if(this.empDAO.doUpdateLevel(emp1)){
							if(this.deptDAO.doUpdateManager(dept)){
								return this.empDAO.doUpdate(vo) ;
							}
						}
					}
				}else {
					throw new LevelNotEnoughException("对不起，您没有操作权限！！");
				}
			} else {
				if("manager".equals(oldEmp.getLid())){
					if(oldEmp.getDid()==vo.getDid()){
						Dept dept=new Dept() ;
						dept.setDid(vo.getDid());
						if(this.deptDAO.doUpdateManager(dept)){
							return this.empDAO.doUpdate(vo) ;
						}
					}else{
						Dept dept=new Dept() ;
						dept.setDid(oldEmp.getDid());
						if(this.deptDAO.doUpdateManager(dept)){
							return this.empDAO.doUpdate(vo) ;
						}
					}
				}else{
					return this.empDAO.doUpdate(vo);
				}
			}
		}else {
			throw new LevelNotEnoughException("对不起，您没有操作权限！！");
		}
		return false;
	}

	@RequiresRoles("emp")
	@RequiresPermissions("emp:delete")
	@Override
	public boolean delete(Set<String> ids,String heid) throws Exception {
		// 取得当前的操作者的雇员完整信息
		Emp humanEmp=this.empDAO.findById(heid) ;
		// 1、查询出所有要删除的雇员信息
		List<Emp> allEmps=this.empDAO.findAllByIds(ids.toArray()) ;
		// 2、遍历出所有要进行删除的数据信息，检查是否存在有领导信息
		Iterator<Emp> iter=allEmps.iterator() ;
		while(iter.hasNext()){
			Emp emp=iter.next() ;
			emp.setIneid(heid); // 设置操作者的eid信息
			if("manager".equals(emp.getLid())){
				if("manager".equals(humanEmp.getLid())){
					Dept dept=new Dept() ;
					dept.setDid(emp.getDid());
					emp.setLid("staff");
					if(this.deptDAO.doUpdateManager(dept)){
						if(this.empDAO.doUpdateLevel(emp)){
							emp.setLocked(2);
							return this.empDAO.doUpdateLocked(emp) ;
						}
					}
				}
			}else if("chief".equals(emp.getLid())){
				continue ;
			}else {
				emp.setLocked(2);
				return this.empDAO.doUpdateLocked(emp) ;
			}
		}
		return false;
	}
}
