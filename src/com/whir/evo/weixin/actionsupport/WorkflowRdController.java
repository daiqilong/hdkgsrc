package com.whir.evo.weixin.actionsupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workflowRd")
public class WorkflowRdController {
	@RequestMapping(value = "/menu")
	public String menu(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String menu = "/rd/workflowMenu";
		return menu;
	}
	
	@RequestMapping(value = "/newProcess")
	public String newProcess(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String menu = "/rd/newProcess";
		return menu;
	}
	
	
}
