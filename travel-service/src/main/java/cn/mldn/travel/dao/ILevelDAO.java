package cn.mldn.travel.dao;

import java.util.List;

import cn.mldn.travel.vo.Level;
import cn.mldn.util.dao.IBaseDAO;

public interface ILevelDAO extends IBaseDAO<String,Level> {
	
	public List<Level> findAllByEmp() ; 
}
