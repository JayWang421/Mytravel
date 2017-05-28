package cn.mldn.travel.service.back.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Service;

import cn.mldn.travel.dao.IDeptDAO;
import cn.mldn.travel.dao.IEmpDAO;
import cn.mldn.travel.dao.IItemDAO;
import cn.mldn.travel.dao.ILevelDAO;
import cn.mldn.travel.dao.ITravelCostDAO;
import cn.mldn.travel.dao.ITravelDAO;
import cn.mldn.travel.dao.ITravelEmpDAO;
import cn.mldn.travel.dao.ITypeDAO;
import cn.mldn.travel.service.back.ITravelServiceBack;
import cn.mldn.travel.vo.Emp;
import cn.mldn.travel.vo.Travel;
import cn.mldn.travel.vo.TravelCost;
import cn.mldn.travel.vo.TravelEmp;
@Service
public class TravelServiceImpl implements ITravelServiceBack {

	@Resource
	private IEmpDAO empDAO ;
	@Resource
	private ILevelDAO levelDAO ;
	@Resource
	private IDeptDAO deptDAO ;
	@Resource
	private IItemDAO itemDAO ;
	@Resource
	private ITravelDAO travelDAO ;
	@Resource
	private ITravelEmpDAO travelEmpDAO ;
	@Resource
	private ITypeDAO typeDAO ;
	@Resource
	private ITravelCostDAO travelCostDAO ;
	
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:add" }, logical = Logical.OR)
	@Override
	public Map<String ,Object> listItem() {
		Map<String ,Object> map=new HashMap<String ,Object>() ;
		map.put("allItems", this.itemDAO.findAll()) ;
		return map;
	}
	
	@RequiresRoles(value = { "travel" }, logical = Logical.OR)
	@RequiresPermissions(value = { "travel:add" }, logical = Logical.OR)
	@Override
	public boolean add(Travel vo) {
		if(vo.getSdate().before(vo.getEdate())){
			vo.setAudit(9);
			return this.travelDAO.doCreate(vo);
		}
		return false ;
	}
	
	@Override
	public Map<String, Object> editPre(Long tid) {
		Map<String,Object> map=new HashMap<>() ;
		Travel travel=this.travelDAO.findById(tid) ;
		if(travel.getAudit().equals(9)){
			map.put("travel", travel) ;
		}
		map.put("allItems", this.itemDAO.findAll()) ;
		return map;
	}
	
	@Override
	public boolean edit(Travel vo) {
		if(vo.getSdate().before(vo.getEdate())){
			return this.travelDAO.doUpdate(vo);
		}
		return false ;
	}
	
	@Override
	public Map<String ,Object> list(String seid,long currentPage, long lineSize, String column, String keyWord) {
		Map<String ,Object> map=new HashMap<>() ;
		//Travel travel=this.travelDAO.findById(id)
		//map.put("total", this.travelEmpDAO.getEidByTid(tid)) ;
		if(column== null || keyWord==null || "".equals(keyWord) || "".equals(column)){
			Map<String ,Object> map1=new HashMap<>() ;
			map1.put("seid", seid) ;
			map1.put("start", (currentPage-1)*lineSize) ;
			map1.put("lineSize", lineSize) ;
			map.put("allTravels", this.travelDAO.findSplit(map1)) ;
			map.put("allRecorders",this.travelDAO.getCountSplit(map1)) ;
		}else{
			Map<String ,Object> map1=new HashMap<>() ;
			map1.put("start", (currentPage-1)*lineSize) ;
			map1.put("seid", seid) ;
			map1.put("lineSize", lineSize) ;
			map1.put("column", column) ;
			map1.put("keyWord", keyWord) ;
			map.put("allTravels", this.travelDAO.findSplit(map1)) ;
			map.put("allRecorders",this.travelDAO.getCountSplit(map1)) ;
		}
		return map ;
	}
	
	@Override
	public boolean delete(Travel vo) {
		return this.travelDAO.doRemoveSelf(vo);
	}
	
	@Override
	public Map<String, Object> listType(long tid) {
		Map<String ,Object> map=new HashMap<>() ;
		map.put("allTypes", this.typeDAO.findAll()) ;
		map.put("allCosts", this.travelCostDAO.findAllByTid(tid)) ;
		return map;
	}
	
	@Override
	public Map<String,Object> addTravelCost(TravelCost vo) {
		Map<String,Object> map=new HashMap<>() ;
		boolean status=this.travelCostDAO.doCreate(vo) ;
		if(status){
			map.put("travelCost", vo) ;
			map.put("type", this.typeDAO.findById(vo.getTpid())) ;
		}
		map.put("status", status) ;
		return map;
	}
	
	@Override
	public boolean deleteTravelCost(long tcid) {
		return this.travelCostDAO.doRemove(tcid);
	}
	
	@Override
	public Map<String, Object> listEmpSplit(long did, long currentPage, long lineSize, String column, String keyWord) {
		Map<String, Object> map = new HashMap<>();
		map.put("allDepts", this.deptDAO.findAll());
		map.put("allLevels", this.levelDAO.findAll());
		try{
		if (column == null || keyWord == null || "".equals(keyWord) || "".equals(column)) {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("did", did) ;
			map1.put("start", (currentPage - 1) * lineSize);
			map1.put("lineSize", lineSize);
			map.put("allEmps", this.empDAO.findAllBySplitLocked(map1));
			map.put("allRecorders", this.empDAO.getAllCountByLocked(map1));
		} else {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("did", did) ;
			map1.put("start", (currentPage - 1) * lineSize);
			map1.put("lineSize", lineSize);
			map1.put("column", column);
			map1.put("keyWord", "%" + keyWord + "%");
			map.put("allEmps", this.empDAO.findAllBySplitLocked(map1));
			map.put("allRecorders", this.empDAO.getAllCountByLocked(map1));
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public Map<String,Object> addTravelEmp(TravelEmp vo) {
		Map<String,Object> map=new HashMap<>() ;
		boolean status=this.travelEmpDAO.doCreateTravelEmp(vo) ;
		if(status){
			Emp emp=this.empDAO.findById(vo.getEid()) ;
			map.put("emp", emp) ;
			map.put("dept", this.deptDAO.findById(emp.getDid())) ;
			map.put("level", this.levelDAO.findById(emp.getLid())) ;
		}
		map.put("status", status) ;
		return map;
	}
	
	@Override
	public Map<String, Object> listEmp(long tid) {
		Map<String ,Object> map=new HashMap<>() ;
		map.put("emp", this.empDAO.findByTravel(tid)) ;
		map.put("allEmps", this.empDAO.findAllByTravelEmp(tid)) ;
		map.put("allDepts", this.deptDAO.findAll()) ;
		map.put("allLevels", this.levelDAO.findAll()) ;
		return map;
	}
	
//	@Override
//	public Map<String, Object> getEcountAndTotal(long tid) {
//		Map<String ,Object> map=new HashMap<>() ;
//		map.put("ecount", this.travelEmpDAO.getEcount(tid)) ;
//		List<Double> all=this.travelCostDAO.getTotal(tid) ;
//		Iterator<Double> iter=all.iterator() ;
//		double total=0.0 ;
//		while(iter.hasNext()){
//			total+=iter.next() ;
//		}
//		map.put("total", total) ;
//		return map;
//	}
	
	@Override
	public boolean editTotal(long tid) {
		Travel vo=new Travel() ;
		vo.setAudit(9);
		vo.setTid(tid);
		List<Double> all=this.travelCostDAO.getTotal(tid) ;
		Iterator<Double> iter=all.iterator() ;
		double total=0.0 ;
		while(iter.hasNext()){
			total+=iter.next() ;
		}
		vo.setTotal(total);
		return this.travelDAO.doUpdateSubmit(vo);
	}

	@Override
	public boolean editEcount(long tid) {
		Travel vo=new Travel() ;
		vo.setAudit(9);
		vo.setTid(tid);
		vo.setEcount(this.travelEmpDAO.getEcount(tid));
		return this.travelDAO.doUpdateSubmit(vo);
	}
	
	@Override
	public boolean editUpdate(long tid) {
		Travel vo=this.travelDAO.findById(tid) ;
		vo.setAudit(0);
		vo.setSubdate(new Date());
		return this.travelDAO.doUpdateSubmit(vo);
	}
}
