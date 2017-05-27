package cn.mldn.travel.dao;

import java.util.List;
import java.util.Map;

import cn.mldn.travel.vo.Emp;
import cn.mldn.util.dao.IBaseDAO;

public interface IEmpDAO extends IBaseDAO<String, Emp> {
	public Emp findByDept(Long did) ;

	public List<Emp> findAllBySplitLocked(Map<String ,Object> param)throws Exception ;
	public Long getAllCountByLocked(Map<String ,Object> param)throws Exception ;
	
	public Emp findByDid(Long did) ;
	
	public Emp findById(String eid) ;
	
	public List<Emp> findByLidAndDid(Map<String ,Object> map) ;
	
	public void doUpdateLidById(Long did) ;
	
	public  String findByLid(String lid) ;
	
	public boolean doUpdateLevel(Emp vo) ;
	
	public List<Emp> findAllByIds(Object ids[]) ;
	public boolean doUpdateLocked(Emp vo) ;
	
	public Emp findByTravel(long tid) ;
	public List<Emp> findAllByTravelEmp(long tid) ;
}
