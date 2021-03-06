package com.kitchen.recommend.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.entity.Step;
import com.entity.menu;


public interface menumapper {
	public List<menu> findall();
	public int addmenu(menu menu);
	public int addsteps(@Param("menuid")int menuid,@Param("steps") List<Step> steps);
//	public int lastinsert();
//	public int addlei(@Param("id")int id,@Param("leiid")int leiid);
//	public int addleis(List<xinwenlei> a);
//	public int addxinwenandleis(@Param("id")int id,@Param("leiid")int leiid);
	public int deletemenu(int menu_id);
	public int editmenu(menu menu);
	public int addstep(Step newstep);
	public int deletestep(int step_id);
	public int editstep(Step step);
	public void deletesteps(int menu_id);
	
}
