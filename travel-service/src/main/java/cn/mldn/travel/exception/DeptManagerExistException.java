package cn.mldn.travel.exception;

//建立一个新的异常类，该异常类主要的功能是描述部门存在有领导所以无法追加：
@SuppressWarnings("serial")
public class DeptManagerExistException extends RuntimeException {
	public DeptManagerExistException(String msg){
		super(msg) ;
	}
}
