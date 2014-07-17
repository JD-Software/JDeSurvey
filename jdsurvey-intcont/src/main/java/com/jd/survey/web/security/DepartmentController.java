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
package com.jd.survey.web.security;

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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Department;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;



@RequestMapping("/security/departments")
@Controller
public class DepartmentController {
	private static final Log log = LogFactory.getLog(DepartmentController.class);	


	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(params = "create", produces = "text/html")
	public String createGet(Model uiModel,
							Principal principal) {
		try {
			User user = userService.user_findByLogin(principal.getName());	
			populateEditForm(uiModel, new Department(),user);
			return "security/departments/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String createPost(@RequestParam(value = "_proceed", required = false) String proceed,
							 @Valid Department department, 
							 BindingResult bindingResult, 
							 Principal principal,
							 Model uiModel, 
							 HttpServletRequest httpServletRequest) {
		try {
			User user = userService.user_findByLogin(principal.getName());	
			
			if(proceed != null){

				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, department,user);
					return "security/departments/create";
				}
				if (surveySettingsService.department_findByName(department.getName()) != null) {
					bindingResult.rejectValue("name", "field_unique");
					populateEditForm(uiModel, department,user);
					return "security/departments/create";
				}
				uiModel.asMap().clear();
				department = surveySettingsService.department_merge(department);
				return "redirect:/security/departments/" + encodeUrlPathSegment(department.getId().toString(), httpServletRequest);
			}
			else{

				return "redirect:/security/departments?page=1&size=10";

			}


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
		try {
			uiModel.addAttribute("department", surveySettingsService.department_findById(id));
			uiModel.addAttribute("isShow", true);
			uiModel.addAttribute("itemId", id);
			return "security/departments/show";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}




	@Secured({"ROLE_ADMIN"})
	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "s", required = false) Integer showDeleteFailMessage, 
					   @RequestParam(value = "page", required = false) Integer page, 
					   @RequestParam(value = "size", required = false) Integer size, 
					   Principal principal,
					   Model uiModel) {
		try {
			if (page != null || size != null) {
				int sizeNo = size == null ? 10 : size.intValue();
				final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
				uiModel.addAttribute("departments", surveySettingsService.department_findAll(firstResult, sizeNo));
				float nrOfPages = (float) surveySettingsService.department_getCount() / sizeNo;
				uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			} else {
				uiModel.addAttribute("departments", surveySettingsService.department_findAll());
			}
			if (showDeleteFailMessage != null && showDeleteFailMessage.equals(1)) {
				uiModel.addAttribute("showDeletedFailed",true); 
			}
			
			
			return "security/departments/list";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						 @Valid Department department, 
						 BindingResult bindingResult, 
						 Principal principal,
						 Model uiModel, 
						 HttpServletRequest httpServletRequest) {
		log.info("update(): handles PUT");
		try{
			User user = userService.user_findByLogin(principal.getName());	
			if(proceed != null){


				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, department,user);
					return "security/departments/update";
				}
				if (surveySettingsService.department_findByName(department.getName()) != null &&
						!surveySettingsService.department_findByName(department.getName()).getId().equals(department.getId())) {
					bindingResult.rejectValue("name", "field_unique");
					populateEditForm(uiModel, department,user);
					return "security/departments/update";
				}
				uiModel.asMap().clear();
				department = surveySettingsService.department_merge(department);
				return "redirect:/security/departments/" + encodeUrlPathSegment(department.getId().toString(), httpServletRequest);

			}else{

				return "redirect:/security/departments?page=1&size=10";

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
			populateEditForm(uiModel, surveySettingsService.department_findById(id),user);
			return "security/departments/update";
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
						 Principal principal,User user,
						 Model uiModel) {
		log.info("delete(): id=" + id);
		try {
			Department department = surveySettingsService.department_findById(id);
			if(department.getSurveyDefinitions() != null && department.getSurveyDefinitions().size() > 0 ){
				return "redirect:/security/departments?s=1";
			}
			
			else{
			
			surveySettingsService.department_remove(department);
			return "redirect:/security/departments";
			}
			
			} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	void populateEditForm(Model uiModel, 
						 Department department,
						 User user) {
		log.info("populateEditForm()");
		try{
			uiModel.addAttribute("department", department);
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
