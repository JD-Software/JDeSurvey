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



import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.DataSet;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;


@RequestMapping("/settings/datasets")
@Controller
public class DataSetController {
	private static final Log log = LogFactory.getLog(DataSetController.class);	
	private static final String VALUE_FIELD_NAME_MESSAGE = "com.jd.survey.domain.settings.datasetitem.value_label";
	private static final String TEXT_FIELD_NAME_MESSAGE = "com.jd.survey.domain.settings.datasetitem.text_label";

			
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;
	@Autowired	private MessageSource messageSource;
		
	
	/**
	 * exports a sample dataset items a comma delimited file
	 * @param dataSetId
	 * @param principal
	 * @param response
	 */
	
	@RequestMapping(value="/example", produces = "text/html")
	public void getExampleCsvFile(Principal principal,
								  HttpServletResponse response) {
		try {
			StringBuilder stringBuilder  = new StringBuilder();
			stringBuilder.append(messageSource.getMessage(VALUE_FIELD_NAME_MESSAGE, null, LocaleContextHolder.getLocale()).replace(",", ""));
			stringBuilder.append(",");
			stringBuilder.append(messageSource.getMessage(TEXT_FIELD_NAME_MESSAGE, null, LocaleContextHolder.getLocale()).replace(",", ""));
			stringBuilder.append("\n");
			stringBuilder.append("B,Boston\n");
			stringBuilder.append("N,New York\n");
			//response.setContentType("text/html; charset=utf-8");
			response.setContentType("application/octet-stream");
			// Set standard HTTP/1.1 no-cache headers.
			response.setHeader("Cache-Control", "no-store, no-cache,must-revalidate");
			// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			// Set standard HTTP/1.0 no-cache header.
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Content-Disposition", "inline;filename=datasetExample.csv");
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(stringBuilder.toString().getBytes("UTF-8"));
			servletOutputStream.flush();

		} 

		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	

	}


		
		/**
		 * exports a dataset to a comma delimited file
		 * @param dataSetId
		 * @param principal
		 * @param response
		 */
		
		@RequestMapping(value = "/{id}", params = "export", produces = "text/html")
		public void export(@PathVariable("id") Long dataSetId,
						   Principal principal,
						   HttpServletResponse response) {
			try {
				
				String commaDelimtedString= surveySettingsService.exportDatasetItemsToCommaDelimited(dataSetId);
				//response.setContentType("text/html; charset=utf-8");
				response.setContentType("application/octet-stream");
			    // Set standard HTTP/1.1 no-cache headers.
			    response.setHeader("Cache-Control", "no-store, no-cache,must-revalidate");
			    // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
			    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			    // Set standard HTTP/1.0 no-cache header.
			    response.setHeader("Pragma", "no-cache");
				response.setHeader("Content-Disposition", "inline;filename=dataSetItems" + dataSetId + ".csv");
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(commaDelimtedString.getBytes("UTF-8"));
				servletOutputStream.flush();
				
			} 
			
			catch (Exception e) {
				log.error(e.getMessage(),e);
				throw (new RuntimeException(e));
			}	
		}
	
	   //renders the page for the survey definition import from json file
		
		/**
		 * prepares the page to import a dataset from a csv file		 
		 * @param dataSetId
		 * @param principal
		 * @param uiModel
		 * @param httpServletRequest
		 * @return
		 */
		@Secured({"ROLE_ADMIN"})
		@RequestMapping(value = "/{id}", params = "import", produces = "text/html")
		public String prepareForImport(@PathVariable("id") Long dataSetId,
									 Principal principal,
									 Model uiModel,
									 HttpServletRequest httpServletRequest) {
			try {
				String login = principal.getName();
				User user = userService.user_findByLogin(login);
				if (!user.isAdmin()){
					log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
					return "accessDenied";	
						}
				uiModel.addAttribute("dataSet", surveySettingsService.dataSet_findById(dataSetId));
				return "settings/datasets/upload";
			} 
			
			catch (Exception e) {
				log.error(e.getMessage(),e);
				throw (new RuntimeException(e));
			}	
		}
		
		
		
		@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
		@RequestMapping(value = "/upload",method = RequestMethod.POST, produces = "text/html")
		public String importDatasetItems(@RequestParam("file") MultipartFile file,
				@RequestParam("id") Long dataSetId,
				@RequestParam("ignoreFirstRow") Boolean ignoreFirstRow,
				@RequestParam(value = "_proceed", required = false) String proceed,
				Principal principal,
				Model uiModel, HttpServletRequest httpServletRequest) {
			
			try {
				String login = principal.getName();
				User user = userService.user_findByLogin(login);
				
				if (!user.isAdmin()){
					log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
					return "accessDenied";	
					}
				if (proceed != null){
					log.info(file.getContentType());
					//if file is empty OR the file type is incorrect the upload page is returned with an error message.
					if (file.isEmpty() || !((file.getContentType().equalsIgnoreCase("text/csv")) || (file.getContentType().equals("application/vnd.ms-excel"))
							|| (file.getContentType().equals("text/plain")))) {
						uiModel.addAttribute("dataSet", surveySettingsService.dataSet_findById(dataSetId));
						uiModel.addAttribute("emptyFileError", true);
						return "settings/datasets/upload";
					}
					try {
						CSVReader csvReader;
						csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
						surveySettingsService.importDatasetItems (csvReader,dataSetId,ignoreFirstRow); 
						//done Redirect to the set view page
						return "redirect:/settings/datasets/" + encodeUrlPathSegment(dataSetId.toString(), httpServletRequest) + "?page=1&size=15";
					} 
					
					catch  (Exception e) {
						log.error(e.getMessage(), e);
						uiModel.addAttribute("dataSet", surveySettingsService.dataSet_findById(dataSetId));
						uiModel.addAttribute("emptyFileError", true);
						return "settings/datasets/upload";
					}
					
					
				}
				else{
					
					return "redirect:/settings/datasets/" + encodeUrlPathSegment(dataSetId.toString(), httpServletRequest);
				}
			}
			catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	
	
	
	
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(params = "create", produces = "text/html")
	public String createDataSet(Principal principal,
								Model uiModel,
								HttpServletRequest httpServletRequest) {
		log.info("createForm(): handles param form");
		try {
			User user = userService.user_findByLogin(principal.getName());
			if (!user.isAdmin()){
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
					}
			DataSet dataSet = new DataSet();
			populateEditForm(uiModel, dataSet,user);
			return "settings/datasets/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String createPost(@RequestParam(value = "_proceed", required = false) String proceed,
							 @Valid DataSet dataSet, 
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
					populateEditForm(uiModel, dataSet,user);
					return "settings/datasets/create";
				}
				
				if (surveySettingsService.dataset_findByName(dataSet.getName()) != null
						 &&
						!surveySettingsService.dataset_findByName(dataSet.getName()).getId().equals(dataSet.getId())){
						bindingResult.rejectValue("name", "field_unique");
						populateEditForm(uiModel, dataSet,user);
						return "settings/datasets/update";
					}
				
				uiModel.asMap().clear();
				dataSet =surveySettingsService.dataSet_merge(dataSet);
				return "redirect:/settings/datasets/" + encodeUrlPathSegment(dataSet.getId().toString(), httpServletRequest);
			}
			else {
				return "redirect:/settings/datasets";
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
			uiModel.addAttribute("dataSet", surveySettingsService.dataSet_findById(id));
			
			int sizeNo = size == null ? 10 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("dataSetItems", surveySettingsService.datasetItem_findByDataSetId(id,firstResult, sizeNo));
			float nrOfPages = (float) surveySettingsService.datasetItem_getCount(id) / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			
		return "settings/datasets/show";
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
				uiModel.addAttribute("dataSets", surveySettingsService.dataSet_findAll(firstResult, sizeNo));
				float nrOfPages = (float) surveySettingsService.dataSet_getCount() / sizeNo;
				uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			} else {
				
				uiModel.addAttribute("dataSets", surveySettingsService.dataSet_findAll());
			}
			return "settings/datasets/list";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						@Valid DataSet dataSet, 
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
					populateEditForm(uiModel, dataSet,user);
					return "settings/datasets/update";
				}
				
				if (surveySettingsService.dataset_findByName(dataSet.getName()) != null
					 &&
					!surveySettingsService.dataset_findByName(dataSet.getName()).getId().equals(dataSet.getId())){
					bindingResult.rejectValue("name", "field_unique");
					populateEditForm(uiModel, dataSet,user);
					return "settings/datasets/update";
				}
				uiModel.asMap().clear();
				dataSet =surveySettingsService.dataSet_merge(dataSet);
				return "redirect:/settings/datasets/" + encodeUrlPathSegment(dataSet.getId().toString(), httpServletRequest);
			}else{
				return "redirect:/settings/datasets";
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
			
			populateEditForm(uiModel, surveySettingsService.dataSet_findById(id), user);
			return "settings/datasets/update";
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
			surveySettingsService.dataSet_remove(id);
			return "redirect:/settings/datasets";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	
	void populateEditForm(Model uiModel, 
						  DataSet dataSet,
						  User user) {
		log.info("populateEditForm()");
		try{
			uiModel.addAttribute("dataSet", dataSet);
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
