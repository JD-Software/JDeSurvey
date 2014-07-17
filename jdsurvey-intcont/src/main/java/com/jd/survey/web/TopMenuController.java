  /*Copyright (C) 2014  JD Software, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package com.jd.survey.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/")
public class TopMenuController {
	private static final Log log = LogFactory.getLog(TopMenuController.class);

	
	/*
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="home", method = RequestMethod.GET)
	public String homeForm(ModelMap uiModel) {
		uiModel.addAttribute("homeActive", true);
		return "home/index";
	}
	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="surveys", method = RequestMethod.GET)
	public String surveysForm(ModelMap uiModel) {
		uiModel.addAttribute("surveysActive", true);
		return "settings/index";;
	}
	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="reports", method = RequestMethod.GET)
	public String reportsForm(ModelMap uiModel) {
		uiModel.addAttribute("reportsActive", true);
		return "settings/index";
	}
	*/
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="settings", method = RequestMethod.GET)
	public String settingsForm(Model uiModel) {
		uiModel.addAttribute("settingsActive", true);
		return "settings/index";
	}
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value="admin", method = RequestMethod.GET)
	public String templatesForm(ModelMap uiModel) {
		return "admin/index";
	}
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value="security", method = RequestMethod.GET)
	public String securityForm(ModelMap uiModel) {
		return "security/index";
	}
	

	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		log.error(ex);
		log.error("redirect to /uncaughtException");
		return "redirect:/uncaughtException";
	}
	
	
}
