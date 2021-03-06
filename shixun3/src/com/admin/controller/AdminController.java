package com.admin.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.admin.service.AdminService;
import com.entity.Admin;
import com.entity.menu;
import com.kitchen.recommend.dao.labelmapper;
import com.kitchen.recommend.service.menuservices;

import com.entity.Goods;
import com.entity.MarketComments;
import com.entity.Step;
import com.entity.User;
import com.entity.label;
import com.market.type.service.TypeService;


@Controller
@RequestMapping("admin")
public class AdminController {
	List<menu> menus=new ArrayList<menu>();
	int menunum=-1;
	List<Step> steps= new ArrayList<Step>();
	@Resource
	AdminService adminService;
	@Resource
	private menuservices menuservices;
	@Resource
	TypeService typeService;
	@RequestMapping("login")
	public String login(@RequestParam("username")String username,@RequestParam("password") String password,HttpSession session) {
		
		List<Admin> admins = adminService.find();
		List<User> user = adminService.find1();
		List<Goods> goods = typeService.find3();
		List<MarketComments> comments = typeService.find11();
		
		
		
		
		List<Goods> good = new ArrayList<>();
		
		for(int i = 0; i < goods.size(); i++) {
			if (i == 5) break;
			good.add(goods.get(i));
		}
		for(int i = 0; i < admins.size(); i++) {
			if (admins.get(i).getUsername().equals(username) && admins.get(i).getPassword().equals(password)) {
				session.setAttribute("user", admins.get(i));
				menus=menuservices.findall();
				session.setAttribute("menus",menus);
				session.setAttribute("yonghu", user);
				session.setAttribute("goods",good);
				session.setAttribute("comments", comments);
				session.setAttribute("comments_page",0);
				session.setAttribute("goods_page",0);
				session.setAttribute("goods_id", goods.get(goods.size()-1).getGoodsId());
				return "redirect:../index.jsp";
			}
		}
		
		return null;
		//return "error.html";
	}
//	添加菜单功能
	@RequestMapping(value="menuadd",method=RequestMethod.POST)
	public String menuadd(@RequestParam("menu_name")String menu_name,@RequestParam("menu_photo") MultipartFile photo,@RequestParam("kouwei") String kouwei,@RequestParam("type")String type,HttpSession session,HttpServletRequest request) {
		System.out.println(menu_name+" "+photo.getName()+" "+type+" "+kouwei);
		String [] c=kouwei.split(",");
		List<label> labels=new ArrayList<label>();
		for(String as:c) {
			label l=new label();
			l.setLabel_id(Integer.valueOf(as));
			labels.add(l);
		}
		
//		上传文件
		String rootPath=request.getServletContext().getRealPath("/");
		
		try {
			if(!photo.isEmpty()) {
				FileCopyUtils.copy(photo.getBytes(), 
						new File(rootPath+"/upload",photo.getOriginalFilename()));
			}
			else {
				System.out.println("不存在");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		menu menu=new menu();
		menu.setDate(new Date().toLocaleString());
		menu.setMenu_photo(photo.getOriginalFilename());
		menu.setMenu_name(menu_name);
		if(type.equals("mi")) {
			type="米";
		}
		else if(type.equals("mian")) {
			type="面";
		}
		else{
			type="菜";
		}
		menu.setType(type);
//		添加menu到menu表
		int menuid=this.menuservices.addmenu(menu);
//		添加labelmenu直接添加到labelmenu里
		int panduan=this.menuservices.addlabelmenu(labels,menuid);
		menus=menuservices.findall();
		session.setAttribute("menus",menus);
		return "list";
		
	}
//	修改menu
	@RequestMapping(value="menuedit",method=RequestMethod.GET)
	public String menuedit(@RequestParam("count")int count,HttpSession session) {
		System.out.println(menus.get(count).getMenu_name());
		session.setAttribute("menu", menus.get(count));
		return "redirect:../menu_edit.html";
	}
	@RequestMapping(value="menuedit",method=RequestMethod.POST)
	public String menuedit(@RequestParam("menu_name")String menu_name,@RequestParam("menu_photo") MultipartFile photo,@RequestParam("kouwei") String kouwei,@RequestParam("type")String type,HttpSession session,HttpServletRequest request) {
		menu menu=(com.entity.menu) session.getAttribute("menu");
//		上传文件
		String rootPath=request.getServletContext().getRealPath("/");
		
		try {
			if(!photo.isEmpty()) {
				FileCopyUtils.copy(photo.getBytes(), 
						new File(rootPath+"/upload",photo.getOriginalFilename()));
				menu.setMenu_photo(photo.getOriginalFilename());
			}
			else {
				System.out.println("不存在");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		更改属性
		menu.setDate(new Date().toLocaleString());
		if(!menu_name.equals("")) {
			menu.setMenu_name(menu_name);
		}
		if(type.equals("mi")) {
			type="米";
		}
		else if(type.equals("mian")) {
			type="面";
		}
		else{
			type="菜";
		}
		menu.setType(type);
		int n=menuservices.editmenu(menu);
//		构建label
		String [] c=kouwei.split(",");
		List<label> labels=new ArrayList<label>();
		for(String as:c) {
			label l=new label();
			l.setLabel_id(Integer.valueOf(as));
			labels.add(l);
		}
		
		int m=menuservices.deletelabel(menu.getMenu_id());
		int m1=menuservices.addlabelmenu(labels, menu.getMenu_id());
		menus=menuservices.findall();
		session.setAttribute("menus",menus);
		return "list";
	}
//	删除菜单功能
	@RequestMapping("menudelete")
	public void menudelete(@RequestParam("count")int count,HttpSession session) {
		System.out.println(menus.get(count).getMenu_name()+menus.get(count).getMenu_id());
		menuservices.deletelabel(menus.get(count).getMenu_id());
		menuservices.deletemenu(menus.get(count).getMenu_id());
		menus.remove(count);
		session.setAttribute("menus",menus);
		
	}
	@RequestMapping("menudeletecheck")
	public void menudeletecheck(@RequestParam("value")String value,HttpSession session) {
		if(!value.equals("")) {
			System.out.println(value.substring(0,value.length()));
			String [] count=value.substring(0,value.length()).split(",");
			
			System.out.println("长度 "+count.length);
			for(String a:count) {
				System.out.println("这是字符串"+a);
				menuservices.deletelabel(menus.get(Integer.valueOf(a)).getMenu_id());
				menuservices.deletemenu(menus.get(Integer.valueOf(a)).getMenu_id());
				
			}
			menus=menuservices.findall();
			session.setAttribute("menus",menus);
			
		}
		else {
			System.out.println("字符穿为空");
		}
		
	}
	
	
//	step操作
//	step操作
//	step操作
//	step操作
//	step操作
	
	
//	浏览step
	@RequestMapping("menustep")
	public String menustep(@RequestParam("count")int count,HttpSession session) {
		
		steps=menuservices.findstepbymenuid(menus.get(count).getMenu_id()+"");
		menunum=count;
		session.setAttribute("steps", steps);
		if(steps.size()!=0) {
			for(Step a:steps) {
				System.out.println(a.getContent());
			}
		}
		return "redirect:../stepadmin.jsp";
		
	}
//	添加步骤
	@RequestMapping(value="stepadd",method=RequestMethod.POST)
	public String stepadd(@RequestParam("context")String context,@RequestParam("step_photo") MultipartFile photo,HttpSession session,HttpServletRequest request) {
		
		Step newstep=new Step();
//		上传文件
		String rootPath=request.getServletContext().getRealPath("/");
		
		try {
			if(!photo.isEmpty()) {
				FileCopyUtils.copy(photo.getBytes(), 
						new File(rootPath+"/upload",photo.getOriginalFilename()));
					newstep.setStep_photo(photo.getOriginalFilename());
			}
			else {
				System.out.println("不存在");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(menunum!=-1) {
			System.out.println(context+" "+photo.getOriginalFilename()+" "+menunum);
		}
		menus=(List<menu>) session.getAttribute("menus");
		System.out.println(menus.get(menunum).getMenu_name()+" "+menus.get(menunum).getMenu_id()+" ");
		
		newstep.setMenu_id(menus.get(menunum).getMenu_id());
		
		newstep.setContent(context);
		if(steps.size()!=0) {
			newstep.setStepnum(steps.get(steps.size()-1).getStepnum()+1);
		}
		else {
			newstep.setStepnum(1);
		}
		int m=menuservices.addstep(newstep);
		steps=menuservices.findstepbymenuid(menus.get(menunum).getMenu_id()+"");
		if(steps.size()!=0) {
			for(Step a:steps) {
				System.out.println(a.getContent());
			}
		}
		session.setAttribute("steps", steps);
		return "list";
	}
//	删除步骤功能
	@RequestMapping("stepdelete")
	public void stepdelete(@RequestParam("count")int count,HttpSession session) {
		System.out.println(steps.get(count).getContent()+steps.get(count).getStep_id());
		int m=menuservices.deletestep(steps.get(count).getStep_id());
		steps.remove(count);
		session.setAttribute("steps", steps);
		
	}
//	修改step
	@RequestMapping(value="stepedit",method=RequestMethod.GET)
	public String stepedit(@RequestParam("count")int count,HttpSession session) {
		System.out.println("修改"+steps.get(count).getContent());
		session.setAttribute("step", steps.get(count));
		return "redirect:../step_edit.html";
	}
	@RequestMapping(value="stepedit",method=RequestMethod.POST)
	public String stepedit(@RequestParam("context")String context,@RequestParam("step_photo") MultipartFile photo,HttpSession session,HttpServletRequest request) {
		Step step= (com.entity.Step) session.getAttribute("step");
//		上传文件
		String rootPath=request.getServletContext().getRealPath("/");
		
		try {
			if(!photo.isEmpty()) {
				FileCopyUtils.copy(photo.getBytes(), 
						new File(rootPath+"/upload",photo.getOriginalFilename()));
				step.setStep_photo(photo.getOriginalFilename());
			}
			else {
				System.out.println("不存在");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		更改属性
		step.setContent(context);
		int n=menuservices.editstep(step);
		steps=menuservices.findstepbymenuid(menus.get(menunum).getMenu_id()+"");
		session.setAttribute("steps",steps);
		return "list";
		
	}
	
//	多选删除
	@RequestMapping("stepdeletecheck")
	public void stepdeletecheck(@RequestParam("value")String value,HttpSession session) {
		if(!value.equals("")) {
			System.out.println(value.substring(0,value.length()));
			String [] count=value.substring(0,value.length()).split(",");
			for(String a:count) {
				menuservices.deletestep(steps.get(Integer.valueOf(a)).getStep_id());
			}
			steps= menuservices.findstepbymenuid(menus.get(menunum).getMenu_id()+"");
			session.setAttribute("steps",steps);
		}
		else {
			System.out.println("字符穿为空");
		}
	}


@RequestMapping("/delete")
	@ResponseBody
	public String d(@RequestParam("id1") String id1, @RequestParam("id2") String id2,HttpSession session) {
		typeService.delete(Integer.valueOf(id1), Integer.valueOf(id2));
		List<MarketComments> comments = (List<MarketComments>) session.getAttribute("comments");
		for(int i = 0; i < comments.size(); i++) {
			if(comments.get(i).getUser().getId()== Integer.valueOf(id1) && comments.get(i).getGoodsId() == Integer.valueOf(id2)) {
				comments.remove(i);
				break;
			}
		}
		return "ok";
	}
	@RequestMapping("page")
	public String e(@RequestParam("page") String page,HttpSession session) {
		System.out.print(page);
		int a = Integer.valueOf(page)*8;
		int b = a+8;
		List<MarketComments> comments=typeService.find22(a, b);
		session.setAttribute("comments", comments);
		session.setAttribute("comments_page",Integer.valueOf(page));
		return "redirect:../marekt_comments.jsp";
	}
	@RequestMapping("page1")
	public String f(@RequestParam("page") String page,HttpSession session) {
		System.out.print(page);
		int a = Integer.valueOf(page)*5;
		int b = a+5;
		List<Goods> good=typeService.find33(a, b);
		
		session.setAttribute("goods", good);
		session.setAttribute("goods_page",Integer.valueOf(page));
		return "redirect:../market_goods.jsp";
	}
	@RequestMapping("/delete1")
	@ResponseBody
	public String h(@RequestParam("id") String id,HttpSession session) {
		typeService.deleteGood(Integer.valueOf(id));
		
		List<Goods> goods = (List<Goods>) session.getAttribute("goods");
		for(int i = 0; i < goods.size(); i++) {
			if(goods.get(i).getGoodsId() == Integer.valueOf(id)) {
				goods.remove(i);
				break;
			}
		}
		return "ok";
	}
	 @RequestMapping("/insert")
	 @ResponseBody
	    public String f(HttpServletRequest request,@RequestParam("content") String content,@RequestParam("time")String time,
	    		@RequestParam("user_id")String user_id,@RequestParam("goods_id")String goods_id,
	    		@RequestParam("r1")String r1,@RequestParam("r2")String r2,@RequestParam("r3")String r3,
	    		@RequestParam(value="selectfile")List<MultipartFile> files,HttpSession session) {
	    	String rooString=request.getServletContext().getRealPath("/");
	    	String img="";
	    	try {
				for(int i = 0;i<files.size();i++) {
					FileCopyUtils.copy(files.get(i).getBytes(), new File(rooString+"upload",files.get(i).getOriginalFilename()));
					img= img+files.get(i).getOriginalFilename()+",";
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	    	System.out.println(content+time+img+r1+r2+r3+user_id+goods_id);
	    	typeService.insertComment(content,time,img,r1,r2,r3,Integer.valueOf(user_id),Integer.valueOf(goods_id));
	    	Integer page = (Integer) session.getAttribute("comments_page");
	    	List<MarketComments> comments=typeService.find22(Integer.valueOf(page)*8,Integer.valueOf(page)*8+8);
	    	
	    	session.setAttribute("comments", comments);
	    	
	    	
	    	return "<script>parent.location.reload(); window.close();</script>";
	    }
	 
	 @RequestMapping("/insert1")
	 @ResponseBody
	 public String qq(HttpServletRequest request,@RequestParam("goods_id")String goods_id,@RequestParam("title")String title
			 ,@RequestParam("little_content") String little_content,@RequestParam("price")String price,@RequestParam(value="selectfile")List<MultipartFile> files,
			 @RequestParam("type_id")String type_id,@RequestParam("goods_type") String goods_type,@RequestParam("return_goods")String return_goods,
			 @RequestParam("if_freeshiiping") String if_freeshiiping,HttpSession session) {
		 String rooString=request.getServletContext().getRealPath("/");
	    	String img="";
	    	String a="";
	    	a = files.get(0).getOriginalFilename();
	    	try {
				for(int i = 0;i<files.size();i++) {
					FileCopyUtils.copy(files.get(i).getBytes(), new File(rooString+"upload",files.get(i).getOriginalFilename()));
					img= img+files.get(i).getOriginalFilename()+",";
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		 typeService.insert11(Integer.valueOf(goods_id)+1, title, little_content, 0,Double.parseDouble(price), a, Integer.valueOf(type_id));
		 typeService.insert22(img, goods_type+";"+price, return_goods, if_freeshiiping, "4", Integer.valueOf(goods_id)+1);
		Integer page = (Integer) session.getAttribute("goods_page");
		 List<Goods> good=typeService.find33(Integer.valueOf(page)*5,Integer.valueOf(page)*5+5);
		 session.setAttribute("goods", good);
		 session.setAttribute("goods_id", Integer.valueOf(goods_id)+1);
		 
		 return "<script>parent.location.reload(); window.close();</script>";
	 }
	 
	 
	 
	 
	 
	 
	 
	 //删除用户
	 @RequestMapping(value = "deleteuser")
		public String deleteuser(HttpServletRequest request) {
			String aaString=request.getParameter("test_str");
			String[] aString=aaString.substring(1,aaString.length()-1).split(",");
			System.out.println(aaString);
			
			 int[] ints = new int[aString.length];

			    for(int i=0;i<aString.length;i++){

			        ints[i] = Integer.parseInt(aString[i]);

			    }
			
			for(int i=0;i<ints.length;i++) {
				
				int aaa=adminService.deleteuser(ints[i]);
				//System.out.println(aString[i]);
			}
			return "redirect:../member-list.jsp";
		}
	 
	 //分页查询用户
	 @RequestMapping("userpage")
		public String userpaging(@RequestParam("userpaging") String page,HttpSession session) {
			System.out.print(page);
			int a = Integer.valueOf(page)*5;
			int b = a+5;
			List<User> user=adminService.findUPage(a, b);
			
			session.setAttribute("yonghu", user);
			session.setAttribute("user_page",Integer.valueOf(page));
			return "redirect:../member-list.jsp";
		}
	 
	 //添加用户
	 @RequestMapping("/insertuser")
	 @ResponseBody
	 public String insertuser(HttpServletRequest request,@RequestParam("userid")String userid,@RequestParam("username")String username
			 ,@RequestParam("password") String password,@RequestParam("sex")String sex,@RequestParam(value="photo")List<MultipartFile> files,
			 @RequestParam("profession")String profession,@RequestParam("home") String home,@RequestParam("birthday")String birthday,
			 @RequestParam("label") String label,HttpSession session) {
		 String rooString=request.getServletContext().getRealPath("/");
	    	String img="";
	    	String a="";
	    	a = files.get(0).getOriginalFilename();
	    	try {
				for(int i = 0;i<files.size();i++) {
					FileCopyUtils.copy(files.get(i).getBytes(), new File(rooString+"/pic",files.get(i).getOriginalFilename()));
					img= img+files.get(i).getOriginalFilename();
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	    	System.out.println("aaa"+rooString);
	    	int tid=Integer.parseInt(userid);
	    	adminService.insertuser(tid, username, password, sex, img, profession, home, birthday, label);
	    	
		 
		 return "<script>parent.location.reload(); window.close();</script>";
	 }
}
