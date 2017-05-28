package cn.mldn.travel.dao;

import java.util.List;
import java.util.Map;

import cn.mldn.travel.vo.Travel;
import cn.mldn.util.dao.IBaseDAO;

public interface ITravelDAO extends IBaseDAO<Long, Travel> {
	public List<Travel> findSplit(Map<String ,Object> map) ;
	public Long getCountSplit(Map<String ,Object> map) ;
	
	public boolean doRemoveSelf(Travel vo) ;
	
	public boolean doUpdateSubmit(Travel vo) ;
}
