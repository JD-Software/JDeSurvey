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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Invitation;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;


@RequestMapping("/settings/invitations")
@Controller
public class InvitationController {
	private static final Log log = LogFactory.getLog(InvitationController.class);	
	@Autowired	private MessageSource messageSource;
	@Autowired	private UserService userService;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private SecurityService securityService;
	
	
	
	
	private static final String FIRST_NAME_MESSAGE = "com.jd.survey.domain.settings.invitation.firstname_label";
	private static final String MIDDLE_NAME_MESSAGE = "com.jd.survey.domain.settings.invitation.middlename_label";
	private static final String LAST_NAME_MESSAGE = "com.jd.survey.domain.settings.invitation.lastname_label";
	private static final String EMAIL_MESSAGE = "com.jd.survey.domain.settings.invitation.email_label";
	
	
	private static final String INVITATION_EMAIL_TITLE="invitation_email_title";
	
	private static final String INVITEE_FULLNAME_PARAMETER_NAME="invitee_fullname_parameter_name";
	private static final String SURVEY_NAME="survey_name";
	
	private static final String INVITE_FILL_SURVEY_LINK_PARAMETER_NAME="invite_fill_survey_link_parameter_name";
	private static final String INVITE_FILL_SURVEY_LINK_LABEL="invite_fill_survey_link_label";

	//private static final String INTERNAL_SITE_BASE_URL="internal_site_base_url";
	//private static final String EXTERNAL_SITE_BASE_URL="external_site_base_url";
	@Value("${external.base.url}") String externalBaseUrl;
	@Value("${internal.base.url}") String internalBaseUrl;
	
	/**
	 *  exports a sample invitations comma delimited file
	 * @param dataSetId
	 * @param principal
	 * @param response
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/example", produces = "text/html")
	public void getExampleCsvFile(Principal principal,
					   			   HttpServletResponse response) {
		try {
			StringBuilder stringBuilder  = new StringBuilder();
			stringBuilder.append(messageSource.getMessage(FIRST_NAME_MESSAGE, null, LocaleContextHolder.getLocale()).replace(",", ""));
			stringBuilder.append(",");
			stringBuilder.append(messageSource.getMessage(MIDDLE_NAME_MESSAGE, null, LocaleContextHolder.getLocale()).replace(",", ""));
			stringBuilder.append(",");
			stringBuilder.append(messageSource.getMessage(LAST_NAME_MESSAGE, null, LocaleContextHolder.getLocale()).replace(",", ""));
			stringBuilder.append(",");
			stringBuilder.append(messageSource.getMessage(EMAIL_MESSAGE, null, LocaleContextHolder.getLocale()).replace(",", ""));
			stringBuilder.append("\n");
			stringBuilder.append("a,b,c,abc@jdsoft.com\n");
			//response.setContentType("text/html; charset=utf-8");
			response.setContentType("application/octet-stream");
		    // Set standard HTTP/1.1 no-cache headers.
		    response.setHeader("Cache-Control", "no-store, no-cache,must-revalidate");
		    // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		    // Set standard HTTP/1.0 no-cache header.
		    response.setHeader("Pragma", "no-cache");
			response.setHeader("Content-Disposition", "inline;filename=Invitationsexample.csv");
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
	 * Shows a list of completed Survey Definitions for the user 
	 * @param surveyId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(produces = "text/html",method = RequestMethod.GET)
	public String listSurveys(Model uiModel, Principal principal) {
		try{
			User user = userService.user_findByLogin(principal.getName());
			Set<SurveyDefinition> surveyDefinitions= surveySettingsService.surveyDefinition_findAllCompletedInternal(user);
			uiModel.addAttribute("surveyDefinitions", surveyDefinitions);
			return "settings/invitations/invitations";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	/**
	 * Shows the list of Survey Invitations for a Survey Definition, Supports Paging 
	 * @param surveyId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/list", produces = "text/html",method = RequestMethod.GET)
	public String listSurveyInvitations(@RequestParam(value = "id", required = true) Long surveyDefinitionId,
										@RequestParam(value = "page", required = false) Integer page, 
						  				@RequestParam(value = "size", required = false) Integer size, 
						  				@RequestParam(value = "fileContentError", required = false) boolean contentError,
										Model uiModel,
										Principal principal,
										HttpServletRequest httpServletRequest) {
		try{
			User user = userService.user_findByLogin(principal.getName());
			
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}				
			Set<SurveyDefinition> surveyDefinitions= surveySettingsService.surveyDefinition_findAllCompletedInternal(user);
			uiModel.addAttribute("surveyDefinitions", surveyDefinitions);
			uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(surveyDefinitionId));
			Long surveyInvitationsCount = surveySettingsService.invitation_getSurveyCount(surveyDefinitionId);
			uiModel.addAttribute("surveyInvitationsCount",surveyInvitationsCount );
			uiModel.addAttribute("surveyInvitationsOpenedCount", surveySettingsService.invitation_getSurveyOpenedCount(surveyDefinitionId));
			
			int sizeNo = size == null ? 25 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			Set<Invitation> invitations= surveySettingsService.invitation_findSurveyAll(surveyDefinitionId,firstResult,sizeNo);
			float nrOfPages = (float) surveyInvitationsCount / sizeNo;
			int maxPages = (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages);
			uiModel.addAttribute("maxPages", maxPages);
			uiModel.addAttribute("invitations", invitations);
			uiModel.addAttribute("fileContentError", contentError);// Used to carry error from file upload over and make it available to the view
			return "settings/invitations/invitations";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	
	
	
		
	/**
	 * prepares the page to import invitations from a csv file		 
	 * @param dataSetId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/import", produces = "text/html")
	public String prepareForImport(@RequestParam(value = "id", required = false) Long surveyDefinitionId,
								   Principal principal,
								   Model uiModel,
								   HttpServletRequest httpServletRequest) {
		try {
			User user = userService.user_findByLogin(principal.getName());
			Set<SurveyDefinition> surveyDefinitions= surveySettingsService.surveyDefinition_findAllCompletedInternal(user);
			uiModel.addAttribute("surveyDefinitions", surveyDefinitions);
			if (surveyDefinitionId != null) {
				if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
					log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
					return "accessDenied";	
				}
				uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(surveyDefinitionId));
			}
			return "settings/invitations/upload";
		} 
		
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	/**
	 * Sends email invitations to the list of invitees from csv file.  
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
	@RequestMapping(value = "/upload",method = RequestMethod.POST, produces = "text/html")
	public String sendInvitations(@RequestParam("file") MultipartFile file,
								@RequestParam("id") Long surveyDefinitionId,
								@RequestParam(value = "_proceed", required = false) String proceed,
								Principal principal,
								Model uiModel, HttpServletRequest httpServletRequest) {
		try {
			short firstNameFieldIndex = 0;
			short middleNameFieldIndex = 1;
			short lastNameFieldIndex = 2;
			short emailNameFieldIndex = 3;

			if (proceed != null){
				//prepare the base url links
				String emailSubject = messageSource.getMessage(INVITATION_EMAIL_TITLE, null, LocaleContextHolder.getLocale());
				//String surveyLink =messageSource.getMessage(EXTERNAL_SITE_BASE_URL, null, LocaleContextHolder.getLocale());
				String surveyLink = externalBaseUrl;
				//String trackingImageLink =messageSource.getMessage(INTERNAL_SITE_BASE_URL, null, LocaleContextHolder.getLocale());
				String trackingImageLink = internalBaseUrl;
				
				SurveyDefinition surveyDefinition =surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
				
				User user = userService.user_findByLogin(principal.getName());
				Set<SurveyDefinition> surveyDefinitions= surveySettingsService.surveyDefinition_findAllCompletedInternal(user);
				//Check if the user is authorized
				if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
					log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
					return "accessDenied";	
				}
				
				if (trackingImageLink.endsWith("/")) {trackingImageLink = trackingImageLink +"public/w/";}	else {trackingImageLink = trackingImageLink +"/public/w/";}
				
				if (surveyDefinition.getIsPublic()) {
					if (surveyLink.endsWith("/")) {surveyLink = surveyLink +"open/";}	else {surveyLink = surveyLink +"/open/";}
				}
				else{
					if (surveyLink.endsWith("/")) {surveyLink = surveyLink +"private/";}	else {surveyLink = surveyLink +"/private/";}	
				}
				
				
		
				
				String emailContent;
				
				if (!file.isEmpty() && ((file.getContentType().equalsIgnoreCase("application/vnd.ms-excel")) || (file.getContentType().equalsIgnoreCase("text/csv")) || ( file.getContentType().equalsIgnoreCase("text/plain")))) {
					CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
					String [] nextLine;
					nextLine = csvReader.readNext(); //skip the first row the continue on with loop
					
					while ((nextLine = csvReader.readNext()) != null) {
						emailContent="";
						StringWriter sw = new StringWriter();
						
						// Prevents IndexOutOfBoundException by skipping line if there are not enough elements in the array nextLine. 
						if(nextLine.length < 4){
							//Continues if a blank line is present without setting an error message (if a new line character is present CSVReader will return and array with one empty string at index 0).
							if(nextLine.length == 1 && nextLine[0].isEmpty()){
								continue;
							}
							uiModel.addAttribute("fileContentError", true);
							continue;
						} 
						//Prevents exception from attempting to send an email with an empty string for an email address.
						if (nextLine[3].isEmpty()){
							uiModel.addAttribute("fileContentError", true);
							continue;
						}
						//creating the Invitation
						Invitation invitation = new Invitation(nextLine[firstNameFieldIndex].trim(),
								nextLine[middleNameFieldIndex].trim(),
								nextLine[lastNameFieldIndex].trim(),
								nextLine[emailNameFieldIndex].trim(),
								surveyDefinition);
						
						Map model = new HashMap();
						//survey name
						model.put(messageSource.getMessage(SURVEY_NAME, null, LocaleContextHolder.getLocale()).replace("${", "").replace("}", ""), 
								surveyDefinition.getName());
						//full name
						model.put(messageSource.getMessage(INVITEE_FULLNAME_PARAMETER_NAME, null, LocaleContextHolder.getLocale()).replace("${", "").replace("}", ""), 
								invitation.getFullName());
						//survey link
						model.put(messageSource.getMessage(INVITE_FILL_SURVEY_LINK_PARAMETER_NAME, null, LocaleContextHolder.getLocale()).replace("${", "").replace("}", ""), 
								"<a href='" + surveyLink + surveyDefinition.getId() + "?list'>" + messageSource.getMessage(INVITE_FILL_SURVEY_LINK_LABEL, null, LocaleContextHolder.getLocale())  +"</a>");
						
						
					
						VelocityContext velocityContext = new VelocityContext(model);
						Velocity.evaluate(velocityContext, sw, "velocity-log" , 
								 		  surveyDefinition.getEmailInvitationTemplate());
						emailContent =  sw.toString().trim();
						
						if (emailContent.length() > 14
							&& emailContent.substring(emailContent.length()-14).toUpperCase().equalsIgnoreCase("</BODY></HTML>")) {
							emailContent = emailContent.substring(0,emailContent.length()-14)  +"<img src='" + trackingImageLink + invitation.getUuid() + "'/></BODY></HTML>";
							emailContent = "<BODY><HTML>" + emailContent;
						}
						else{
							// template is incorrect or not html do not include tracking white gif
							emailContent = emailContent + "<img src='" +  trackingImageLink + invitation.getUuid() + "'/></BODY></HTML>";
							
						}
						
						
						surveySettingsService.invitation_send(invitation, emailSubject, emailContent);	
					}
					
					
					return "redirect:/settings/invitations/list?id=" + encodeUrlPathSegment(surveyDefinitionId.toString(), httpServletRequest);
				} 
				else 
				{
					uiModel.addAttribute("surveyDefinitions", surveyDefinitions);
					uiModel.addAttribute("surveyDefinition",surveyDefinition );
					uiModel.addAttribute("emptyFileError", true);
					return "settings/invitations/upload";
				}
			}
			else{
				return "redirect:/settings/invitations/list?id=" + encodeUrlPathSegment(surveyDefinitionId.toString(), httpServletRequest);
			}
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
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
