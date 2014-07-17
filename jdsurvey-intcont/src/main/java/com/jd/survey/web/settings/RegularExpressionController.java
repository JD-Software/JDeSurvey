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
import com.jd.survey.domain.settings.RegularExpression;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;


@RequestMapping("/admin/masks")
@Controller
public class RegularExpressionController {

	private static final Log log = LogFactory.getLog(RegularExpressionController.class);	

	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;
	@Autowired	private SecurityService securityService;


	@Secured({"ROLE_ADMIN"})
	@RequestMapping(params = "create", produces = "text/html")
	public String createRegularExpression(Principal principal,
			Model uiModel,
			HttpServletRequest httpServletRequest) {
		log.info("createForm(): handles param form");
		try {
			User user = userService.user_findByLogin(principal.getName());
			RegularExpression regularExpression = new RegularExpression();
			populateEditForm(uiModel, regularExpression,user);
			return "admin/masks/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}


	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String createPost(@RequestParam(value = "_proceed", required = false) String proceed,
			@Valid RegularExpression regularExpression, 
			BindingResult bindingResult,
			Principal principal,
			Model uiModel, 
			HttpServletRequest httpServletRequest) {
		log.info("create(): handles " + RequestMethod.POST.toString());
		try {			
			User user = userService.user_findByLogin(principal.getName());	
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, regularExpression,user);
					return "admin/masks/create";
				}
				if (surveySettingsService.regularExpression_findByName(regularExpression.getName()) != null  ) {
					bindingResult.rejectValue("name", "field_unique");
					populateEditForm(uiModel, regularExpression,user);
					return "admin/masks/create";
				}
				uiModel.asMap().clear();
				regularExpression =surveySettingsService.regularExpression_merge(regularExpression);
				return "redirect:/admin/masks" ;//+ encodeUrlPathSegment(regularExpression.getId().toString(), httpServletRequest);
			}
			else {
				return "redirect:/admin/masks";
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}


	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id, 
			@RequestParam(value = "page", required = false) Integer page, 
			@RequestParam(value = "size", required = false) Integer size,
			Principal principal,
			Model uiModel) {
		log.info("show(): id=" + id);
		try {
			uiModel.addAttribute("regularExpression", surveySettingsService.regularExpression_findById(id));

			int sizeNo = size == null ? 10 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("regularExpressions", surveySettingsService.regularExpression_findAll(firstResult, sizeNo));
			float nrOfPages = (float) surveySettingsService.datasetItem_getCount(id) / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));

			return "admin/masks/show";
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
			if (page != null || size != null) {
				int sizeNo = size == null ? 10 : size.intValue();
				final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
				uiModel.addAttribute("regularExpressions", surveySettingsService.regularExpression_findAll(firstResult, sizeNo));
				float nrOfPages = (float) surveySettingsService.regularExpression_getCount() / sizeNo;
				uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			} else {
				uiModel.addAttribute("regularExpressions", surveySettingsService.regularExpression_findAll());
			}
			return "admin/masks/list";
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
			populateEditForm(uiModel, surveySettingsService.regularExpression_findById(id), user);
			return "admin/masks/update";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	
	
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						@Valid RegularExpression regularExpression, 
						BindingResult bindingResult,
						Principal principal,
						Model uiModel, 
						HttpServletRequest httpServletRequest) {
		log.info("update(): handles PUT");
		try{
			User user = userService.user_findByLogin(principal.getName());	
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, regularExpression,user);
					return "admin/masks/update";
				}
			
				if (surveySettingsService.regularExpression_findByName(regularExpression.getName()) != null &&
					!surveySettingsService.regularExpression_findByName(regularExpression.getName()).getId().equals(regularExpression.getId())) {
					bindingResult.rejectValue("name", "field_unique");
					populateEditForm(uiModel, regularExpression,user);
					return "admin/masks/update";
				}
				uiModel.asMap().clear();
				regularExpression =surveySettingsService.regularExpression_merge(regularExpression);
				return "redirect:/admin/masks"; // + encodeUrlPathSegment(regularExpression.getId().toString(), httpServletRequest);
			}else{
				return "redirect:/admin/masks";
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") Long id,
						 Principal principal,
						 Model uiModel, 
						 HttpServletRequest httpServletRequest) {
		log.info("delete(): id=" + id);
		try {
			RegularExpression regularExpression = surveySettingsService.regularExpression_findById(id);
			surveySettingsService.regularExpression_remove(regularExpression);
			return "redirect:/admin/masks";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	


	void populateEditForm(Model uiModel, 
			RegularExpression regularExpression,
			User user) {
		log.info("populateEditForm()");
		try{
			uiModel.addAttribute("regularExpression", regularExpression);
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
