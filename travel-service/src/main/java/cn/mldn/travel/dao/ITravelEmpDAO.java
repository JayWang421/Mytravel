package cn.mldn.travel.dao;

import cn.mldn.travel.vo.TravelEmp;
import cn.mldn.util.dao.IBaseDAO;

public interface ITravelEmpDAO extends IBaseDAO<Long, TravelEmp> {
	public Long getEidByTid(Long tid) ;
	public boolean doCreateTravelEmp(TravelEmp vo) ;
	
	public Integer getEcount(Long tid) ;
}
