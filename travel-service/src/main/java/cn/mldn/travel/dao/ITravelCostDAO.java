package cn.mldn.travel.dao;

import java.util.List;

import cn.mldn.travel.vo.TravelCost;
import cn.mldn.util.dao.IBaseDAO;

public interface ITravelCostDAO extends IBaseDAO<Long, TravelCost> {
	public List<TravelCost> findAllByTid(Long tid) ;
}
