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



import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.util.Base64;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.jd.survey.GlobalSettings;
import com.jd.survey.domain.security.PasswordResetRequest;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Invitation;
import com.jd.survey.service.email.MailService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.ApplicationSettingsService;
import com.jd.survey.service.settings.SurveySettingsService;





@RequestMapping("/public")
@Controller
public class LoginController {
	private static final Log log = LogFactory.getLog(LoginController.class);	
	private static final String DATE_FORMAT = "date_format";
	
	@Autowired	private UserService userService;
	@Autowired	private MailService mailService;
	@Autowired  private VelocityEngine velocityEngine;
	@Autowired	private MessageSource messageSource;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private ApplicationSettingsService applicationSettingsService;
	
	private static final long FORGOT_LOGIN_VELOCITY_EMAIL_TEMPLATE_ID = 1;
	private static final long FORGOT_PASSWORD_VELOCITY_EMAIL_TEMPLATE_ID = 2;
	
	private static final String LOGIN_PARAMETER_NAME="login_parameter_name";
	private static final String RESET_PASSWORD_LINK_PARAMETER_NAME="reset_password_link_parameter_name";
	private static final String RESET_PASSWORD_LINK_LABEL="reset_password_link_label";
	//private static final String INTERNAL_SITE_BASE_URL="internal_site_base_url";
	//private static final String EXTERNAL_SITE_BASE_URL="external_site_base_url";
	@Value("${external.base.url}") String externalBaseUrl;
	@Value("${internal.base.url}") String internalBaseUrl;
	

	private static final String FORGOT_LOGIN_EMAIL_TITLE="forgot_login_email_title";
	private static final String FORGOT_PASSWORD_EMAIL_TITLE="forgot_password_email_title";
	
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/w/{uuid}", produces = "image/gif")
	public void getWhiteGif(@PathVariable("uuid") String uuid, 
			Principal principal,
			HttpServletRequest httpServletRequest,
			HttpServletResponse response) {
	
		try {
			
			Invitation invitation   = surveySettingsService.invitation_findByUuid(uuid);
			if (invitation!=null) {
				surveySettingsService.invitation_updateAsRead(invitation.getId());
			}
			
			//white 1 x 1 pixel gif binary 
			byte[] trackingGif = { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte)  0xff, (byte)  0xff,  (byte) 0xff, 0x0, 0x0, 0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b };
			
			response.setContentType("image/gif");
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(trackingGif);
			servletOutputStream.flush();
			
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}

	

	
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/", params = "flogin", produces = "text/html")
	public String forgotLoginGet(Model uiModel,HttpServletRequest httpServletRequest) {
		try {
			uiModel.addAttribute("status", "N");
			return "public/flogin";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}

	
	@RequestMapping(method = RequestMethod.POST, value = "/", params = "flogin", produces = "text/html")
	public String forgotLoginPost(@RequestParam(value = "email", required = true) String email,
								  @RequestParam(value = "_proceed", required = false) String proceed,
								  Model uiModel,HttpServletRequest httpServletRequest) {
		try {
			log.info("post");
			if(proceed != null){ //Proceed
				User user  = userService.user_findByEmail(email);
				if (user !=null) {
					StringWriter sw = new StringWriter();
					Map model = new HashMap();
					model.put(messageSource.getMessage(LOGIN_PARAMETER_NAME, null, LocaleContextHolder.getLocale()).replace("${", "").replace("}", ""), 
							  user.getLogin());
					VelocityContext velocityContext = new VelocityContext(model);
					Velocity.evaluate(velocityContext, sw, "velocity-log" , 
									  surveySettingsService.velocityTemplate_findById(FORGOT_LOGIN_VELOCITY_EMAIL_TEMPLATE_ID).getDefinition());
				
					mailService.sendEmail(email, 
										messageSource.getMessage(FORGOT_LOGIN_EMAIL_TITLE, null, LocaleContextHolder.getLocale()),
										sw.toString().trim());
					uiModel.addAttribute("status", "S");
					return "public/flogin";
				} 
				else
				{
					log.info("no match");
					uiModel.addAttribute("status", "I");
					return "public/flogin";
				}
			}
			else{ //Cancel button
				
				return "public/login";	
			}


		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	
	
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/", params = "fpass", produces = "text/html")
	public String forgotPasswordGet(Model uiModel,HttpServletRequest httpServletRequest) {
		try {
			uiModel.addAttribute("dateFormat", messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
			uiModel.addAttribute("status", "N");
			return "public/fpass";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	

	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/", params = "fpass", produces = "text/html")
	public String forgotPasswordPost(@RequestParam(value = "login", required = true) String login,
									@RequestParam(value = "dob", required = true) String dob,
			  						@RequestParam(value = "_proceed", required = false) String proceed,
			  						Model uiModel,HttpServletRequest httpServletRequest) {
		try {
			if(proceed != null){
				String resetPasswordLink;
				String dateFormat = messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale());
				
				//Validate date and login entries (sanitize) 
				if (login == null || login.isEmpty() || login.length() > 100 || 
					dob	 == null || dob.isEmpty() || 
					!GenericValidator.isDate(dob,dateFormat, true)) {
					uiModel.addAttribute("status", "I");
					return "public/fpass";
				}
				
				//Check if provided DOB and login match
				if (!userService.user_validateDateofBirthAndLogin(login,DateValidator.getInstance().validate(dob))) {
					uiModel.addAttribute("status", "I");
					return "public/fpass";
				}
				
				User user  = userService.user_findByLogin(login);
				if (httpServletRequest.getRequestURI().contains("external")) {
					//resetPasswordLink =messageSource.getMessage(EXTERNAL_SITE_BASE_URL, null, LocaleContextHolder.getLocale());
					resetPasswordLink = externalBaseUrl;
				} 
				else {
					//resetPasswordLink =messageSource.getMessage(INTERNAL_SITE_BASE_URL, null, LocaleContextHolder.getLocale());
					resetPasswordLink = internalBaseUrl;
				}
				if (resetPasswordLink.endsWith("/")) {resetPasswordLink = resetPasswordLink +"public/rpass?key=";}	else {resetPasswordLink = resetPasswordLink +"/public/rpass?key=";}
					
			
				
				StringWriter sw = new StringWriter();
				Map model = new HashMap();
				model.put(messageSource.getMessage(RESET_PASSWORD_LINK_PARAMETER_NAME, null, LocaleContextHolder.getLocale()).replace("${", "").replace("}", ""), 
						  "<a href='"+ resetPasswordLink + userService.user_prepareForgotPasswordMessage(user.getId())+ "'>" + 
						   messageSource.getMessage(RESET_PASSWORD_LINK_LABEL, null, LocaleContextHolder.getLocale()) +"</a>");
				VelocityContext velocityContext = new VelocityContext(model);
				Velocity.evaluate(velocityContext, sw, "velocity-log" , 
								  surveySettingsService.velocityTemplate_findById(FORGOT_PASSWORD_VELOCITY_EMAIL_TEMPLATE_ID).getDefinition());
				
				mailService.sendEmail(user.getEmail(), 
									messageSource.getMessage(FORGOT_PASSWORD_EMAIL_TITLE, null, LocaleContextHolder.getLocale()),
									sw.toString());
				uiModel.addAttribute("status", "S");
				return "public/fpass";
			}
			else { //cancel button
				return "public/login";	
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/rpass", produces = "text/html")
	public String forgotPasswordGet(@RequestParam(value = "key", required = true) String key,
									Model uiModel,HttpServletRequest httpServletRequest) {
		try {
			if (userService.user_validateForgotPasswordKey(key)) {
				uiModel.addAttribute("key", key);
				uiModel.addAttribute("status", "v");
				return "public/rpass";
			}
			else
			{
				log.warn("Attempt to reset password with invalid key, Not successful");
				uiModel.addAttribute("status", "E");//Error
				throw (new RuntimeException("Attempt to reset password with invalid key, Not successful"));
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/rpass", produces = "text/html")
	public String forgotPasswordPost(@RequestParam(value = "password", required = true) String password,
									@RequestParam(value = "cpassword", required = true) String cpassword,
									@RequestParam(value = "key", required = true) String key,
									@RequestParam(value = "_proceed", required = false) String proceed,
									Model uiModel,HttpServletRequest httpServletRequest) {
		try {
			if(proceed != null){
				//validate the passed key
				if (! userService.user_validateForgotPasswordKey(key)) {
					log.warn("Attempt to reset password with invalid key, Not successful");
					uiModel.addAttribute("status", "E"); //Error
					throw (new RuntimeException("Attempt to reset password with invalid key, Not successful"));
				}
				
				//check that passwords match 	
				if (!password.equals(cpassword)) {
					uiModel.asMap().clear();
					uiModel.addAttribute("key", key);
					uiModel.addAttribute("status", "U"); //Unmatching Passwords
					return "public/rpass";
				}
				
				GlobalSettings globalSettings = applicationSettingsService.getSettings();
				
				//Check new password strength 
				if(!GenericValidator.matchRegexp(password,globalSettings.getPasswordEnforcementRegex())){
					uiModel.asMap().clear();
					uiModel.addAttribute("key", key);
					uiModel.addAttribute("status", "I"); //Unmatching Passwords
					uiModel.addAttribute("passwordPolicyMsg", globalSettings.getPasswordEnforcementMessage());
					return "public/rpass";
				}
								
				//All validations passed, save the HASH of the password in the database
				PasswordResetRequest passwordResetRequest =userService.passwordResetRequest_findByHash(key);
				User user = userService.user_findByLogin(passwordResetRequest.getLogin());
				user.setPassword(password);
				userService.user_updatePassword(user,passwordResetRequest);
				uiModel.addAttribute("status", "S");//success
				return "public/rpass";
			}
			else{
				//cancel button
				return "public/login";	
			}
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


	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10));
	}


	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		log.error("handling RuntimeException");
		log.error("redirect to /err");
		return "redirect:/err";
	}



}
