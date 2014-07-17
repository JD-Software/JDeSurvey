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
package com.jd.survey.web.settings;



import java.io.UnsupportedEncodingException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.VelocityTemplate;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;
import org.owasp.validator.html.*;



@RequestMapping("/admin/templates")
@Controller
public class VelocityTemplateController {
	private static final Log log = LogFactory.getLog(VelocityTemplateController.class);	

	private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4.xml"; 
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(@RequestParam(value = "_proceed", required = false) String proceed,
						@Valid VelocityTemplate velocityTemplate, 
						BindingResult bindingResult, 
						Principal principal,
						Model uiModel, HttpServletRequest httpServletRequest) {
		log.info("create(): handles " + RequestMethod.POST.toString());
		try {
			User user = userService.user_findByLogin(principal.getName());
			if (!user.isAdmin()){
				return "accessDenied";
				}
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, velocityTemplate,user);
					return "admin/templates/create";
				}
				uiModel.asMap().clear();
				velocityTemplate = surveySettingsService.velocityTemplate_merge(velocityTemplate);
				log.info("redirecting to: " +  "redirect:/admin/templates/" + encodeUrlPathSegment(velocityTemplate.getId().toString(), httpServletRequest));
				return "redirect:/admin/templates";
			}
			else{
				return "redirect:/admin/templates";
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(params = "create", produces = "text/html")
	public String createForm(Principal principal,Model uiModel) {
		log.info("createForm(): handles param form");
		try {
			User user = userService.user_findByLogin(principal.getName());	
			populateEditForm(uiModel, new VelocityTemplate(),user);
			return "admin/templates/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id,
			 			Principal principal,
						Model uiModel) {
		log.info("show(): id=" + id);
		
		try {
			User user = userService.user_findByLogin(principal.getName());
			if (!user.isAdmin()){
				return "accessDenied";
				}
			uiModel.addAttribute("velocityTemplate", surveySettingsService.velocityTemplate_findById(id));
			uiModel.addAttribute("itemId", id);
			return "admin/templates/show";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	@Secured({"ROLE_ADMIN"})
	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) Integer page, 
					   @RequestParam(value = "size", required = false) Integer size, 
					   Principal principal,
					   Model uiModel) {
		
		try {
			User user = userService.user_findByLogin(principal.getName());
			log.error("list():");
			if (!user.isAdmin()){
				return "accessDenied";
				}
			if (page != null || size != null) {
				int sizeNo = size == null ? 10 : size.intValue();
				final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
				uiModel.addAttribute("velocityTemplates", surveySettingsService.velocityTemplate_findAll(firstResult, sizeNo));
				float nrOfPages = (float) surveySettingsService.velocityTemplate_getCount() / sizeNo;
				uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			} else {
				uiModel.addAttribute("velocityTemplates", surveySettingsService.velocityTemplate_findAll());
			}
			return "admin/templates/list";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						@Valid VelocityTemplate velocityTemplate, 
						BindingResult bindingResult, 
						Principal principal,
						Model uiModel, HttpServletRequest httpServletRequest) {
		log.info("update(): handles PUT");
		try{
			User user = userService.user_findByLogin(principal.getName());	
			if (!user.isAdmin()){
				return "accessDenied";
				}
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, velocityTemplate,user);
					return "velocityTemplates/update";
				}
				
				Policy policy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
				AntiSamy as = new AntiSamy();
				CleanResults cr = as.scan(velocityTemplate.getDefinition(), policy);
				velocityTemplate.setDefinition(cr.getCleanHTML());
				
				uiModel.asMap().clear();
				velocityTemplate = surveySettingsService.velocityTemplate_merge(velocityTemplate);
				log.info("redirecting to: " +  "redirect:/admin/templates/" + encodeUrlPathSegment(velocityTemplate.getId().toString(), httpServletRequest));
				return "redirect:/admin/templates?page=1&size=25";
			}
			else{
				return "redirect:/admin/templates?page=1&size=25";

			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") Long id,
			 				Principal principal,
							Model uiModel) {
		log.info("updateForm(): id=" + id);
		try{
			User user = userService.user_findByLogin(principal.getName());	
			if (!user.isAdmin()){
				return "accessDenied";
				}
			populateEditForm(uiModel, surveySettingsService.velocityTemplate_findById(id),user);
			return "admin/templates/update";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") Long id, 
						@RequestParam(value = "page", required = false) Integer page, 
						@RequestParam(value = "size", required = false) Integer size, 
						Principal principal,
						Model uiModel) {
		log.info("delete(): id=" + id);
		try {
			User user = userService.user_findByLogin(principal.getName());	
			if (!user.isAdmin()){
				return "accessDenied";
				}
			VelocityTemplate surveyDefinition = surveySettingsService.velocityTemplate_findById(id);
			surveySettingsService.velocityTemplate_remove(surveyDefinition);
			uiModel.asMap().clear();
			uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
			uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
			return "redirect:/admin/templates";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	void populateEditForm(Model uiModel, VelocityTemplate velocityTemplate, User user) {
		try{
			uiModel.addAttribute("velocityTemplate", velocityTemplate);
			uiModel.addAttribute("surveyDefinitions", surveySettingsService.surveyDefinition_findAllInternal(user));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
		log.info("encodeUrlPathSegment()");
		try{
			String enc = httpServletRequest.getCharacterEncoding();
			if (enc == null) {
				enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
			}
			try {
				pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
			} catch (UnsupportedEncodingException uee) {log.error(uee);}
			return pathSegment;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		log.error(ex);
		log.error("redirect to /uncaughtException");
		return "redirect:/uncaughtException";
	}
	



}
