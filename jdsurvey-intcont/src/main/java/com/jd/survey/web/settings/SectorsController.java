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
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Sector;
import com.jd.survey.domain.settings.SurveyTemplate;
import com.jd.survey.domain.settings.VelocityTemplate;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;


@RequestMapping("/admin/sectors")
@Controller
public class SectorsController {
	private static final Log log = LogFactory.getLog(DataSetController.class);	
	
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(params = "create", produces = "text/html")
	public String createSector(Principal principal,
								Model uiModel,
								HttpServletRequest httpServletRequest) {
		log.info("createForm(): handles param form");
		try {
			User user = userService.user_findByLogin(principal.getName());
			if (!user.isAdmin()){
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
					}
			Sector sector = new Sector();
			populateEditForm(uiModel, sector,user);
			return "admin/sectors/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String createPost(@RequestParam(value = "_proceed", required = false) String proceed,
							 @Valid Sector sector, 
							 BindingResult bindingResult,
							 Principal principal,
							 Model uiModel, 
							 HttpServletRequest httpServletRequest) {
		log.info("create(): handles " + RequestMethod.POST.toString());
		try {			
			User user = userService.user_findByLogin(principal.getName());	
			if (!user.isAdmin()){
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
					}
			
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, sector,user);
					return "admin/sectors/create";
				}
				
				if (surveySettingsService.dataset_findByName(sector.getName()) != null
						 &&
						!surveySettingsService.dataset_findByName(sector.getName()).getId().equals(sector.getId())){
						bindingResult.rejectValue("name", "field_unique");
						populateEditForm(uiModel, sector,user);
						return "admin/sectors/update";
					}
				
				uiModel.asMap().clear();
				sector =surveySettingsService.sector_merge(sector);
				return "redirect:/admin/sectors/" + encodeUrlPathSegment(sector.getId().toString(), httpServletRequest);
			}
			else {
				return "redirect:/admin/sectors";
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}

	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id, 
					   @RequestParam(value = "page", required = false) Integer page, 
			           @RequestParam(value = "size", required = false) Integer size,
			           Principal principal,
					   Model uiModel) {
		log.info("show(): id=" + id);
		try {
			uiModel.addAttribute("itemId", id);
			uiModel.addAttribute("sector", surveySettingsService.sector_findById(id));
			
			int sizeNo = size == null ? 10 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("surveyTemplates", surveySettingsService.surveyTemplate_findBySectorId(id,firstResult, sizeNo));
			float nrOfPages = (float) surveySettingsService.surveyTemplate_getCount(id) / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			
		return "admin/sectors/show";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	

	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) Integer page, 
					   @RequestParam(value = "size", required = false) Integer size,
					   Principal principal,
					   Model uiModel) {
		try {
			
			if (page != null || size != null) {
				
				int sizeNo = size == null ? 10 : size.intValue();
				final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
				uiModel.addAttribute("sectors", surveySettingsService.sector_findAll(firstResult, sizeNo));
				float nrOfPages = (float) surveySettingsService.sector_getCount() / sizeNo;
				uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			} else {
				
				uiModel.addAttribute("sectors", surveySettingsService.sector_findAll());
			}
			return "admin/sectors/list";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						@Valid Sector sector, 
						BindingResult bindingResult,
						Principal principal,
						Model uiModel, 
						HttpServletRequest httpServletRequest) {
		log.info("update(): handles PUT");
		try{
			User user = userService.user_findByLogin(principal.getName());	

			if (!user.isAdmin()){
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				}
			
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, sector,user);
					return "admin/sectors/update";
				}
				
				if (surveySettingsService.dataset_findByName(sector.getName()) != null
					 &&
					!surveySettingsService.dataset_findByName(sector.getName()).getId().equals(sector.getId())){
					bindingResult.rejectValue("name", "field_unique");
					populateEditForm(uiModel, sector,user);
					return "admin/sectors/update";
				}
				uiModel.asMap().clear();
				sector =surveySettingsService.sector_merge(sector);
				return "redirect:/admin/sectors/" + encodeUrlPathSegment(sector.getId().toString(), httpServletRequest);
			}else{
				return "redirect:/admin/sectors";
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
				//log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			populateEditForm(uiModel, surveySettingsService.sector_findById(id), user);
			return "admin/sectors/update";
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
			User user = userService.user_findByLogin(principal.getName());	
			if (!user.isAdmin()){
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";		
			}
			surveySettingsService.sector_remove(id);
			return "redirect:/admin/sectors";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/surveyTemplates/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String deletess(@PathVariable("id") Long id, 
						 Principal principal,
						 Model uiModel, 
						 HttpServletRequest httpServletRequest) {
		log.info("delete(): id=" + id);
		try {
			User user = userService.user_findByLogin(principal.getName());	
			if (!user.isAdmin()){
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			SurveyTemplate surveyTemplate = (SurveyTemplate) surveySettingsService.surveyTemplate_findById(id);
			surveySettingsService.surveyTemplate_remove(surveyTemplate);
			uiModel.asMap().clear();
			return "redirect:/admin/sectors";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	void populateEditForm(Model uiModel, 
						  Sector sector,
						  User user) {
		log.info("populateEditForm()");
		try{
			uiModel.addAttribute("sector", sector);
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
