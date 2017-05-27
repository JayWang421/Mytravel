package cn.mldn.travel.service.back.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.mldn.travel.dao.IDeptDAO;
import cn.mldn.travel.dao.IEmpDAO;
import cn.mldn.travel.service.back.IDeptServiceBack;
import cn.mldn.travel.vo.Dept;
import cn.mldn.travel.vo.Emp;

@Service
public class DeptServiceBackImpl implements IDeptServiceBack {

	@Resource
	private IEmpDAO empDAO ;
	@Resource
	private IDeptDAO deptDAO ;
	
	@Override
	public Map<String ,Object> list() throws Exception {
		Map<String ,Object> map=new HashMap<>() ;
		map.put("allDepts", this.deptDAO.findAll()) ;
		map.put("allEmps", this.deptDAO.findEnameByDept()) ;
		return map;
	}
	
	@Override
	public boolean edit(Dept vo) throws Exception {
		return this.deptDAO.doUpdateById(vo);
	}
	
	@Override
	public boolean editLevel(Long did, String ineid) throws Exception {
		Dept dept=this.deptDAO.findById(did) ;
		Emp emp=new Emp() ;
		emp.setEid(dept.getEid());
		if(dept!=null){
			Emp mgr=this.empDAO.findById(ineid) ;
			if("manager".equals(mgr)){
				dept.setEid(null);
				if(this.deptDAO.doUpdateManager(dept)){
					emp.setLid("straff");
					emp.setIneid(ineid);
					return this.empDAO.doUpdateLevel(emp) ;
				}
			}
		}
		return false;
	}
}
