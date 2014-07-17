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



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jd.survey.GlobalSettings;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Department;
import com.jd.survey.domain.settings.Sector;
import com.jd.survey.domain.settings.SurveyTemplate;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.settings.SurveyDefinitionStatus;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.ApplicationSettingsService;
import com.jd.survey.service.settings.SurveySettingsService;
import com.jd.survey.service.util.JsonHelperService;



@RequestMapping("/settings/surveyDefinitions")
@Controller
public class SurveyDefinitionController {
	private static final Log log = LogFactory.getLog(SurveyDefinitionController.class);	

	
	//private static final String EXTERNAL_SITE_BASE_URL="external_site_base_url";
	private static final long SURVEY_INVITATION_EMAIL_TEMPLATE_ID = 4;
	private static final long SURVEY_COMPLETED_PAGE_CONTENT_TEMPLATE_ID = 5;
	private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4.xml"; 
	
	@Autowired	private ApplicationSettingsService applicationSettingsService;
	@Autowired  private VelocityEngine velocityEngine;
	@Autowired	private SecurityService securityService;
	@Autowired	private UserService userService;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private JsonHelperService jsonHelperService;
	@Autowired	private MessageSource messageSource;
	
	@Value("${external.base.url}") String externalBaseUrl;
	
	  
	        	
	
	/**
	 * Returns the survey logo image binary  
	 * @param departmentId
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/qr/{id}", produces = "text/html")
	public void getSurveyQRCode(@PathVariable("id") Long surveyDefinitionId, 
								 Model uiModel, 
								 Principal principal,
								 HttpServletRequest httpServletRequest,
								 HttpServletResponse response) {
		try {
			uiModel.asMap().clear();
			User user = userService.user_findByLogin(principal.getName());
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				throw(new RuntimeException("Unauthorized access to logo"));
			}
			
			SurveyDefinition surveyDefinition =surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
			//String surveyLink =messageSource.getMessage(EXTERNAL_SITE_BASE_URL, null, LocaleContextHolder.getLocale());
			String surveyLink = externalBaseUrl;
			if (surveyDefinition.getIsPublic()) {
				if (surveyLink.endsWith("/")) {surveyLink = surveyLink +"open/" + surveyDefinitionId + "?list";}	else {surveyLink = surveyLink +"/open/" + surveyDefinitionId + "?list" ;}
			}
			else{
				if (surveyLink.endsWith("/")) {surveyLink = surveyLink +"private/"+ surveyDefinitionId + "?list";}	else {surveyLink = surveyLink +"/private/"+ surveyDefinitionId + "?list";}	
			}
			
			response.setContentType("image/png");
			ServletOutputStream servletOutputStream = response.getOutputStream();
			
			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix bitMatrix = null;
			try {
			    bitMatrix = writer.encode(surveyLink, BarcodeFormat.QR_CODE, 600, 600);
			    MatrixToImageWriter.writeToStream(bitMatrix, "png", servletOutputStream);
			 
			} catch (WriterException e){
			        e.printStackTrace();
			  } catch (IOException e){
			        e.printStackTrace();
			  }
			
			servletOutputStream.flush();
			
			
			
			
					
					
		} 
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	/**
	 * Returns the survey logo image binary  
	 * @param departmentId
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/logo/{id}", produces = "text/html")
	public void getSurveyLogo(@PathVariable("id") Long surveyDefinitionId, 
								 Model uiModel, 
								 Principal principal,
								 HttpServletRequest httpServletRequest,
								 HttpServletResponse response) {
		try {
			uiModel.asMap().clear();
			User user = userService.user_findByLogin(principal.getName());
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				throw(new RuntimeException("Unauthorized access to logo"));
			}
					SurveyDefinition surveyDefinition =surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
					//response.setContentType("image/png");
					ServletOutputStream servletOutputStream = response.getOutputStream();
					servletOutputStream.write(surveyDefinition.getLogo());
					servletOutputStream.flush();	
		} 
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}

	
	/**
	 * Prepares the page to update the survey logo 
	 * @param departmentId
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/logo",method = RequestMethod.GET,  produces = "text/html")
	public String updateLogoPrepare(@RequestParam(value = "id", required = false) Long surveyDefinitionId,
									 Model uiModel, 
									 Principal principal,
									 HttpServletRequest httpServletRequest) {
		try {
			User user = userService.user_findByLogin(principal.getName());
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(surveyDefinitionId));
			return "settings/surveyDefinitions/logo";
		} 

		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	/**
	 * Updates the survey logo
	 * @param file
	 * @param surveyDefinitionId
	 * @param proceed
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/logo",method = RequestMethod.POST, produces = "text/html")
	public String updateLogo(@RequestParam("file") MultipartFile file,
			@RequestParam("id") Long surveyDefinitionId,
			@RequestParam(value = "_proceed", required = false) String proceed,
			Principal principal,
			Model uiModel, HttpServletRequest httpServletRequest) {
		try {
			User user = userService.user_findByLogin(principal.getName());
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			GlobalSettings globalSettings = applicationSettingsService.getSettings();
			
			//validate content type
			if (file.isEmpty() || !globalSettings.getValidImageTypesAsList().contains(file.getContentType().toLowerCase())) {
				uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(surveyDefinitionId));
				uiModel.addAttribute("invalidFile", true);
				return "settings/surveyDefinitions/logo";
			} 
			
			SurveyDefinition surveyDefinition =surveySettingsService.surveyDefinition_updateLogo(surveyDefinitionId,file.getBytes());
			uiModel.asMap().clear();
			return "settings/surveyDefinitions/saved";
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	

	}

	
	
	

	/**
	 * Renders the page for the survey definition import from a JSON file 
	 * @param departmentId
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/upload", produces = "text/html")
	public String uploadFromJson(@RequestParam(value = "id", required = false) Long departmentId,
								 Model uiModel, 
								 Principal principal,
								 HttpServletRequest httpServletRequest) {
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);

			//Check if the user is authorized
			if(departmentId != null && !securityService.userBelongsToDepartment(departmentId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			Set<Department> departments =  surveySettingsService.department_findAll(user);
			uiModel.addAttribute("departments", departments);
			uiModel.addAttribute("departmentId", departmentId);
			uiModel.addAttribute("jsonTemplates",surveySettingsService.surveyTemplate_findAll());
			
			return "settings/surveyDefinitions/upload";
		} 

		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/importtemplate", produces = "text/html")
	public String importTemplate(@RequestParam(value = "id", required=false) Long departmentId,
			 @RequestParam(value = "templateId", required=false) Long templateId,
			 Model uiModel, 
			 Principal principal,
			 HttpServletRequest httpServletRequest) {
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			
			//Check if the user is authorized
			if(departmentId != null && !securityService.userBelongsToDepartment(departmentId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";
			}
			uiModel.addAttribute("departments", surveySettingsService.department_findAll(user));
			uiModel.addAttribute("departmentId", departmentId);
			uiModel.addAttribute("sectors", surveySettingsService.sector_findAll());
			return "settings/surveyDefinitions/importtemplate";
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/importtemplate",method = RequestMethod.POST, produces = "text/html")
	public String importTemplatePost(@RequestParam("id") Long departmentId,
									  @RequestParam("secId") Long sectorId,
									  @RequestParam(value = "_proceed", required = false) String proceed,
									  Principal principal, Model uiModel, HttpServletRequest httpServletRequest){
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userBelongsToDepartment(departmentId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			if(proceed != null){
				uiModel.addAttribute("department",surveySettingsService.department_findById(departmentId));
				uiModel.addAttribute("sector",surveySettingsService.sector_findById(sectorId));
				uiModel.addAttribute("templates",surveySettingsService.surveyTemplate_findBySectorId(sectorId)); 
				return "redirect:/settings/surveyDefinitions/browsetemplate?id="+ encodeUrlPathSegment(departmentId.toString(), httpServletRequest)+"&sid="+ encodeUrlPathSegment(sectorId.toString(), httpServletRequest);
			}
			else{
				//Cancel button
				return "settings/surveyDefinitions";	
			}
			
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/browsetemplate", produces = "text/html")
	public String browseTemplate(@RequestParam(value = "id", required = false) Long departmentId,
								 @RequestParam(value="sid", required = false) Long sectorId,
								 Principal principal, Model uiModel, HttpServletRequest httpServletRequest){
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login); 
			
			//Check if the user is authorized
			if(!securityService.userBelongsToDepartment(departmentId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			uiModel.addAttribute("department",surveySettingsService.department_findById(departmentId));
			uiModel.addAttribute("sector",surveySettingsService.sector_findById(sectorId));
			uiModel.addAttribute("templates",surveySettingsService.surveyTemplate_findBySectorId(sectorId));
			return "settings/surveyDefinitions/browsetemplate";
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/browsetemplate", method = RequestMethod.POST, produces = "text/html")
	public String browseTemplatePost(@RequestParam(value="id", required=false) Long departmentId,
									 @RequestParam(value="secId", required=false) Long sectorId,
									 @RequestParam(value="tempId", required=false) Long templateId,
									 @RequestParam("name") String surveyName,
									 @RequestParam(value = "_proceed", required = false) String proceed,
									 Principal principal, Model uiModel, HttpServletRequest httpServletRequest){
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userBelongsToDepartment(departmentId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			if(proceed != null){
				Department department = surveySettingsService.department_findById(departmentId);
				SurveyDefinition sdef = new SurveyDefinition(department,surveyName);
				
				if (!surveySettingsService.surveyDefinition_ValidateNameIsUnique(sdef)) {
					uiModel.addAttribute("department",surveySettingsService.department_findById(departmentId));
					uiModel.addAttribute("sector",surveySettingsService.sector_findById(sectorId));
					uiModel.addAttribute("templates",surveySettingsService.surveyTemplate_findBySectorId(sectorId));
					uiModel.addAttribute("nameDuplicateError", true);
					return "settings/surveyDefinitions/browsetemplate";
				}
				if (surveyName!=null &&surveyName.trim().length()>0 && surveyName.trim().length()<250){
					SurveyTemplate st = surveySettingsService.surveyTemplate_findById(templateId);
					String jsonString = st.getJson();
					SurveyDefinition surveyDefinition  = jsonHelperService.deSerializeSurveyDefinition(jsonString);
					surveyDefinition.setName(surveyName);
					surveyDefinition = surveySettingsService.surveyDefinition_create(surveyDefinition, departmentId);
					uiModel.asMap().clear();
					surveyDefinition = surveySettingsService.surveyDefinition_merge(surveyDefinition);
					return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinition.getId().toString(), httpServletRequest );
				}
				else{
					uiModel.addAttribute("department",surveySettingsService.department_findById(departmentId));
					uiModel.addAttribute("sector",surveySettingsService.sector_findById(sectorId));
					uiModel.addAttribute("templates",surveySettingsService.surveyTemplate_findBySectorId(sectorId));
					uiModel.addAttribute("nameError", true);
					return "settings/surveyDefinitions/browsetemplate";
				}
			}
			uiModel.addAttribute("departments", surveySettingsService.department_findAll(user));
			uiModel.addAttribute("departmentId", departmentId);
			uiModel.addAttribute("sectors", surveySettingsService.sector_findAll());
			return "settings/surveyDefinitions/importtemplate";
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/{id}", params = "savetemplate", produces = "text/html")
	public String saveTemplate(@PathVariable("id") Long surveyDefinitionId,
								Principal principal,
								Model uiModel,
								HttpServletRequest httpServletRequest,
								HttpServletResponse response) {
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				response.sendRedirect("../../accessDenied");
				
			}
			Set<Sector> sectors;
			sectors=surveySettingsService.sector_findAll();
			if(sectors.size() <=0){
				uiModel.addAttribute("noSectors", true);
			}
			uiModel.addAttribute("sectors",sectors);
			uiModel.addAttribute("sid",surveyDefinitionId);
			return "settings/surveyDefinitions/savetemplate";
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/savetemplate", method = RequestMethod.POST, produces = "text/html")
	public String saveTemplatePost(@RequestParam("sid") Long surveyDefinitionId,
									@RequestParam("secId") Long sectorId,
									@RequestParam("name") String templateName,
									@RequestParam("description") String templateDesc,
									@RequestParam(value = "_proceed", required = false) String proceed,
									Principal principal,
									Model uiModel,
									HttpServletRequest httpServletRequest,
									HttpServletResponse response) {
		try{
			if(proceed != null){
				if (templateName!=null &&templateName.trim().length()>0 && templateName.trim().length()<250){
					Sector sector = surveySettingsService.sector_findById(sectorId);
					SurveyTemplate st = new SurveyTemplate();
					SurveyDefinition surveyDefinition =  surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
					//Set the exported survey definition status to Inactive
					//problem: sets the published survey to incomplete.
					surveyDefinition.setStatus(SurveyDefinitionStatus.I);
					String json= jsonHelperService.serializeSurveyDefinition(surveyDefinition);
					st.setName(templateName);
					st.setSector(sector);	
					st.setDescription(templateDesc);
					st.setJson(json);
					//Returning the original survey's status to Published.
					surveyDefinition.setStatus(SurveyDefinitionStatus.P);
					
					if (!surveySettingsService.surveyTemplate_ValidateNameIsUnique(st)) {
						uiModel.addAttribute("sectors",surveySettingsService.sector_findAll());
						uiModel.addAttribute("sid",surveyDefinitionId);
						uiModel.addAttribute("nameDuplicateError", true);
						return "settings/surveyDefinitions/savetemplate";
					}
					else{
	
					st= surveySettingsService.surveyTemplate_merge(st);
					uiModel.asMap().clear();
					return "settings/surveyDefinitions/saved";
					}
				}
				else{
					uiModel.addAttribute("sectors",surveySettingsService.sector_findAll());
					uiModel.addAttribute("sid",surveyDefinitionId);
					uiModel.addAttribute("nameError", true);
					return "settings/surveyDefinitions/savetemplate";
				}
			}
			//Cancel
			return "settings/surveyDefinitions/";
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	


	
	/**
	 * Imports survey definition from a JSON file, This feature may be used to migrate survey definitions from different environments,
	 * For example from a test environment to a production environment   
	 * @param file
	 * @param departmentId
	 * @param surveyName
	 * @param proceed
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/upload",method = RequestMethod.POST, produces = "text/html")
	public String uploadFromJsonPost (@RequestParam("file") MultipartFile file,
									  @RequestParam("id") Long departmentId,
									  @RequestParam("name") String surveyName,
									  @RequestParam(value = "_proceed", required = false) String proceed,
									  Principal principal,
									  Model uiModel, HttpServletRequest httpServletRequest){
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userBelongsToDepartment(departmentId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			if(proceed != null){
				Long surveyDefinitionId = null;
				try {	
					if (!surveySettingsService.surveyDefinition_ValidateNameIsUnique(new SurveyDefinition(surveySettingsService.department_findById(departmentId),surveyName))) {
						uiModel.addAttribute("departments", surveySettingsService.department_findAll(user));
						uiModel.addAttribute("nameDuplicateError", true);
						return "settings/surveyDefinitions/upload";
					}
					
					
					if (!file.isEmpty()) {
						if (surveyName!=null &&surveyName.trim().length()>0 && surveyName.trim().length()<250){
							String jsonString = new String(file.getBytes(), "UTF8");
							SurveyDefinition surveyDefinition  = jsonHelperService.deSerializeSurveyDefinition(jsonString);
							surveyDefinition.setName(surveyName);
							surveyDefinition = surveySettingsService.surveyDefinition_create(surveyDefinition, departmentId);
							surveyDefinitionId = surveyDefinition.getId();
						}
						else{
							uiModel.addAttribute("departments", surveySettingsService.department_findAll(user));
							uiModel.addAttribute("nameError", true);
							return "settings/surveyDefinitions/upload";
						}
					}
					else
					{
						uiModel.addAttribute("departments", surveySettingsService.department_findAll(user));
						uiModel.addAttribute("emptyFileError", true);
						return "settings/surveyDefinitions/upload";
					}

				}

				catch (Exception e) {
					log.error(e.getMessage(),e);
					uiModel.addAttribute("departments", surveySettingsService.department_findAll(user));
					uiModel.addAttribute("importError", true);
					return "settings/surveyDefinitions/upload";
				}
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinitionId.toString(), httpServletRequest);
			}
			else{
				return "redirect:/settings/surveyDefinitions?page=1&size=25";

			}


		}

		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}


	}



 
	/**
	 * Exports the survey definition as a JSON file
	 * @param surveyDefinitionId
	 * @param response
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "export", produces = "text/html")
	public void exportToJson(@PathVariable("id") Long surveyDefinitionId, 
							Principal principal,
							HttpServletRequest httpServletRequest,
							HttpServletResponse response) {
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				response.sendRedirect("../../accessDenied");
				
			}
			
			SurveyDefinition surveyDefinition =  surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
			//set the exported survey definition status to Inactive
			surveyDefinition.setStatus(SurveyDefinitionStatus.I);
			
			
			String json= jsonHelperService.serializeSurveyDefinition(surveyDefinition);
			//response.setContentType("text/html; charset=utf-8");
			response.setContentType("application/octet-stream");
			// Set standard HTTP/1.1 no-cache headers.
			response.setHeader("Cache-Control", "no-store, no-cache,must-revalidate");
			// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			// Set standard HTTP/1.0 no-cache header.
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Content-Disposition", "inline;filename=surveyDef" + surveyDefinitionId + ".jsn");
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(json.getBytes("UTF-8"));
			servletOutputStream.flush();
			//Returning the original survey's status to Published.
			surveyDefinition.setStatus(SurveyDefinitionStatus.P);
		} 
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}



	//Exports the survey definition  
	
	/**
	 * Publishes the survey definition, this will make the survey available from the external site.   
	 * @param surveyDefinitionId
	 * @param response
	 * @param httpServletRequest
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "publish", produces = "text/html")
	public String publishSurveyDefinition(@PathVariable("id") Long surveyDefinitionId, 
										  HttpServletResponse response,
										  Principal principal,		
										  Model uiModel,
										  HttpServletRequest httpServletRequest){
		try{ 
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			//Validate Page, questions and questions options are not empty
			if (!surveySettingsService.surveyDefinition_ValidateSurveydefinitionForPublishing(surveyDefinitionId)){
				uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(surveyDefinitionId) ); 
				uiModel.addAttribute("isNotPublishReady", true);
				return "settings/surveyDefinitions/show";
			}
			else //All Pages, Questions and Question Options are valid
			{
				SurveyDefinition surveyDefinition=surveySettingsService.surveyDefinition_publish(surveyDefinitionId);
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinitionId.toString(), httpServletRequest);
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	/**
	 * changes the state of a previously published survey to inactive, Survey participant can no longer create new surveys of this type,
	 * but they may however finish an existing incomplete survey      
	 * @param surveyDefinitionId
	 * @param response
	 * @param httpServletRequest
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "unpublish", produces = "text/html")
	public String DeactivateSurveyDefinition(@PathVariable("id") Long surveyDefinitionId, 
											HttpServletResponse response,
											Principal principal,
											Model uiModel,
											HttpServletRequest httpServletRequest){

		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}

			SurveyDefinition surveyDefinition=surveySettingsService.surveyDefinition_deactivate(surveyDefinitionId);
			return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinitionId.toString(), httpServletRequest);

		}catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	/**
	 * Prepares the page for creating a new survey definition 
	 * @param departmentId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "create", produces = "text/html")
	public String createGet(@PathVariable("id") Long departmentId, 
							Principal principal,
							Model uiModel,
							HttpServletRequest httpServletRequest) {
		
		
		if (surveySettingsService.department_getCount()>0){
			try {
				
				String login = principal.getName();
				User user = userService.user_findByLogin(login);
				SurveyDefinition surveyDefinition;
				
				if (departmentId!=null && departmentId!=0 ){
					
					
					//Check if the user is authorized
					if(!securityService.userBelongsToDepartment(departmentId, user)) {
						log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
						return "accessDenied";	
					}
					
					Department department =  surveySettingsService.department_findById( departmentId);
					surveyDefinition = new SurveyDefinition(department, 
															surveySettingsService.velocityTemplate_findById(SURVEY_INVITATION_EMAIL_TEMPLATE_ID).getDefinition(),
															surveySettingsService.velocityTemplate_findById(SURVEY_COMPLETED_PAGE_CONTENT_TEMPLATE_ID).getDefinition());
										
				}
				else{
					surveyDefinition = new SurveyDefinition(surveySettingsService.velocityTemplate_findById(SURVEY_INVITATION_EMAIL_TEMPLATE_ID).getDefinition(),
															surveySettingsService.velocityTemplate_findById(SURVEY_COMPLETED_PAGE_CONTENT_TEMPLATE_ID).getDefinition());
					
				}
				
				populateEditForm(uiModel, surveyDefinition,user);
				
				
				return "settings/surveyDefinitions/create";
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				throw (new RuntimeException(e));
			}	
		}
		else{
			
			return "redirect:/settings/surveyDefinitions/";
		}
	}
	

	
	/**
	 * creates a new survey definition
	 * @param proceed
	 * @param surveyDefinition
	 * @param bindingResult
	 * @param uiModel
	 * @param httpServletRequest
	 * @param principal
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String createPost (@RequestParam(value = "_proceed", required = false) String proceed,
							  @Valid SurveyDefinition surveyDefinition, 
							  BindingResult bindingResult, 
							  Principal principal,
							  Model uiModel, 
							  HttpServletRequest httpServletRequest){
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userBelongsToDepartment(surveyDefinition.getDepartment().getId(), user) &&
				!securityService.userIsAuthorizedToManageSurvey(surveyDefinition.getId(), user)	) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}

			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, surveyDefinition, user);
					return "settings/surveyDefinitions/create";
				}	
				if (!surveySettingsService.surveyDefinition_ValidateNameIsUnique(surveyDefinition)) {
					bindingResult.rejectValue("name", "field_unique");
					populateEditForm(uiModel, surveyDefinition, user);
					return "settings/surveyDefinitions/create";
				}
				
				//if(surveyDefinition.getSendAutoReminders() == true){
					//bindingResult.rejectValue("autoRemindersWeeklyOccurrence", "field_unique");
					
			//	}	
				
				
				Policy emailTemplatePolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
				AntiSamy emailAs = new AntiSamy();
				CleanResults crEmail = emailAs.scan(surveyDefinition.getEmailInvitationTemplate(), emailTemplatePolicy);
				surveyDefinition.setEmailInvitationTemplate(crEmail.getCleanHTML());
				
				Policy completedSurveyPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
				AntiSamy completedSurveyAs = new AntiSamy();
				CleanResults crCompletedSurvey = completedSurveyAs.scan(surveyDefinition.getCompletedSurveyTemplate(), completedSurveyPolicy);
				surveyDefinition.setCompletedSurveyTemplate(crCompletedSurvey.getCleanHTML());
				
			
				
				uiModel.asMap().clear();
				surveyDefinition = surveySettingsService.surveyDefinition_merge(surveyDefinition);
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinition.getId().toString(), httpServletRequest );
			}
			else{
				return "redirect:/settings/surveyDefinitions";
			}
		}

		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	/**
	 * Shows a survey definition
	 * @param id
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id, 
						Principal principal,
						Model uiModel, 
						 HttpServletRequest httpServletRequest) {
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			SurveyDefinition surveyDefinition =surveySettingsService.surveyDefinition_findById(id);
			//String surveyLink =messageSource.getMessage(EXTERNAL_SITE_BASE_URL, null, LocaleContextHolder.getLocale());
			String surveyLink = externalBaseUrl;
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(id, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}

			
			if (surveyDefinition.getIsPublic()) {
				if (surveyLink.endsWith("/")) {surveyLink = surveyLink +"open/" + id + "?list";}	else {surveyLink = surveyLink +"/open/" + id + "?list" ;}
			}
			else{
				if (surveyLink.endsWith("/")) {surveyLink = surveyLink +"private/"+ id + "?list";}	else {surveyLink = surveyLink +"/private/"+ id + "?list";}	
			}
			
			for (SurveyDefinitionPage page: surveyDefinition.getPages()) {
				for (Question question: page.getQuestions()) {
					//if (question.getType()== QuestionType.DATASET_DROP_DOWN){
						//DataSet dataset = surveySettingsService.dataset_findByName(question.getDataSetCode());
						//.addAttribute("datasetItems" + "p"+ page.getOrder() + "q"+ question.getOrder(),surveySettingsService.datasetItem_findByDataSetId(dataset.getId(), 0, 10));

					//}

				}
			}
			
			
			uiModel.addAttribute("surveyLink" , surveyLink);
			uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(id));
			/*uiModel.addAttribute("isPublishReady", true);*/
			uiModel.addAttribute("isShow", true);
			uiModel.addAttribute("itemId", id);
			return "settings/surveyDefinitions/show";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	/**
	 * Lists survey definitions
	 * @param page
	 * @param size
	 * @param uiModel
	 * @param principal
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "s", required = false) Integer showPublishedSurveyDeleteFailMessage,
					  @RequestParam(value = "page", required = false) Integer page, 
					  @RequestParam(value = "size", required = false ) Integer size, 
					  Principal principal,
					  Model uiModel) {
		
		try {
			

			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			log.info(login);
			if (page != null || size != null ) {

				int sizeNo = size == null ? 10 : size.intValue();
				final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;

				uiModel.addAttribute("surveyDefinitions", surveySettingsService.surveyDefinition_findAllInternal(user,firstResult, sizeNo));

				float nrOfPages = (float) surveySettingsService.surveyDefinition_getCount() / sizeNo;
				uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			} else {

				uiModel.addAttribute("surveyDefinitions", surveySettingsService.surveyDefinition_findAllInternal(user));
			}
			
			
			if (showPublishedSurveyDeleteFailMessage != null && showPublishedSurveyDeleteFailMessage.equals(1)) {
				uiModel.addAttribute("showPublishedSurveyDeleteFailMessage",true); 
			}
			
			if (surveySettingsService.department_getCount() <= 0) { uiModel.addAttribute("noDepartments", true); }
			
			return "settings/surveyDefinitions/list";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	/**
	 * Updates a survey definition   
	 * @param proceed
	 * @param surveyDefinition
	 * @param bindingResult
	 * @param uiModel
	 * @param httpServletRequest
	 * @param principal
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						 @Valid SurveyDefinition surveyDefinition, 
						 BindingResult bindingResult,
						 Principal principal,
						 Model uiModel, 
						 HttpServletRequest httpServletRequest) {
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinition.getId(), user) &&
			   !securityService.userBelongsToDepartment(surveyDefinition.getDepartment().getId(), user)	) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, surveyDefinition, user);
					return "settings/surveyDefinitions/update";
				}
				if (!surveySettingsService.surveyDefinition_ValidateNameIsUnique(surveyDefinition)) {
					bindingResult.rejectValue("name", "field_unique");
					populateEditForm(uiModel, surveyDefinition, user);
					return "settings/surveyDefinitions/update";
				}
				
				Policy emailTemplatePolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
				AntiSamy emailAs = new AntiSamy();
				CleanResults crEmail = emailAs.scan(surveyDefinition.getEmailInvitationTemplate(), emailTemplatePolicy);
				surveyDefinition.setEmailInvitationTemplate(crEmail.getCleanHTML());
				
				Policy completedSurveyPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
				AntiSamy completedSurveyAs = new AntiSamy();
				CleanResults crCompletedSurvey = completedSurveyAs.scan(surveyDefinition.getCompletedSurveyTemplate(), completedSurveyPolicy);
				surveyDefinition.setCompletedSurveyTemplate(crCompletedSurvey.getCleanHTML());
							
				
				uiModel.asMap().clear();
				surveyDefinition = surveySettingsService.surveyDefinition_merge(surveyDefinition);
				return "settings/surveyDefinitions/saved";
				

			}else{
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinition.getId().toString(), httpServletRequest);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	/**
	 * 
	 * @param id
	 * @param uiModel
	 * @param principal
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") Long id, 
							Principal principal,				
							Model uiModel) {
		log.info("updateForm(): id=" + id);
		try{
			User user = userService.user_findByLogin(principal.getName());
			populateEditForm(uiModel, surveySettingsService.surveyDefinition_findById(id), user);
			return "settings/surveyDefinitions/update";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	/**
	 * Deletes a survey definition 
	 * @param id
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") Long id,
						Principal principal,
						Model uiModel, 
						HttpServletRequest httpServletRequest) {
		log.info("delete(): id=" + id);
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			SurveyDefinition surveyDefinition = surveySettingsService.surveyDefinition_findById(id);
			Long departmentId = surveyDefinition.getDepartment().getId();
			
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(id, user)) { 
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			if (surveyDefinition.getStatus().equals(SurveyDefinitionStatus.P) || surveyDefinition.getStatus().equals(SurveyDefinitionStatus.D) ) {  
				//you may not delete a survey that has been published  
				log.warn("Attempt to delete a survey that has been published path:" + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";
			}
		
			surveySettingsService.surveyDefinition_remove(surveyDefinition);
			uiModel.asMap().clear();
			return "redirect:/settings/surveyDefinitions/";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	

	/**
	 * Populates the model with the survey definition and the list of departments  
	 * @param uiModel
	 * @param surveyDefinition
	 * @param user
	 * @param day 
	 * @param day 
	 */
	void populateEditForm(Model uiModel, SurveyDefinition surveyDefinition, User user) {
		log.info("populateEditForm()");
		try{
			if (surveyDefinition.getDepartment() !=null) {
				surveyDefinition.setDepartment(surveySettingsService.department_findById(surveyDefinition.getDepartment().getId()));
			}
			uiModel.addAttribute("surveyDefinition", surveyDefinition);
			uiModel.addAttribute("departments", surveySettingsService.department_findAll(user));
			uiModel.addAttribute("autoRemindersDays", surveySettingsService.day_findAll());
			uiModel.addAttribute("socrataPublishDays", surveySettingsService.day_findAll()); 
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}

	
	/**
	 * Encodes a string path suing the  httpServletRequest Character Encoding
	 * @param pathSegment
	 * @param httpServletRequest
	 * @return
	 */
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

	
	/*
	 * Handles any runtime exceptions on this controller
	*/ 
	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		log.error(ex);
		log.error("redirect to /uncaughtException");
		return "redirect:/uncaughtException";
	}

}
