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

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.jd.survey.GlobalSettings;
import com.jd.survey.domain.security.User;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.ApplicationSettingsService;



@RequestMapping("/account")
@Controller
public class AccountController {
	private static final Log log = LogFactory.getLog(AccountController.class);
	
	@Autowired private UserService userService;
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private ApplicationSettingsService applicationSettingsService;
	
	private GlobalSettings globalSettings;
	
	@PostConstruct
	public void initIt() throws Exception{
		globalSettings = applicationSettingsService.getSettings();
	}
	 
	/**
	 * Shows the logged in user information 
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_SURVEY_ADMIN" })
	@RequestMapping(value = "/show", produces = "text/html")
	public String show(Principal principal,
					   Model uiModel) {
		try {
			User loggedInUser = userService.user_findByLogin(principal.getName());
			uiModel.addAttribute("user", loggedInUser);
			return "account/show";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	/**
	 * prepares for user update information 
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/update", produces = "text/html")
	public String updateForm(Principal principal,Model uiModel) {
		
		try{
			User loggedInUser = userService.user_findByLogin(principal.getName());
			uiModel.addAttribute("user", loggedInUser);
			return "account/update";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	
	
	/**
	 * Updates logged in user information
	 * @param proceed
	 * @param user
	 * @param bindingResult
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_SURVEY_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
			@Valid User user, 
			BindingResult bindingResult, 
			Principal principal,
			Model uiModel, 
			HttpServletRequest httpServletRequest) {
		log.info("update(): handles PUT");
		try{
			User loggedInUser = userService.user_findByLogin(principal.getName());
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					uiModel.addAttribute("user", user);
					return "account/update";
				}
				if (userService.user_findByLogin(user.getLogin()) != null && userService.user_ValidateLoginIsUnique(user) == true) {
					bindingResult.rejectValue("login", "field_unique");
					uiModel.addAttribute("user", user);
					return "account/update";
				}
				if (userService.user_findByEmail(user.getEmail()) != null && userService.user_ValidateEmailIsUnique(user) == true){
					bindingResult.rejectValue("email", "field_unique");
					uiModel.addAttribute("user", user);
					return "account/update";
				}
				uiModel.asMap().clear();
				user = userService.user_updateInformation(user);
				return "redirect:/account/show";

			}
			else {
				return "redirect:/account/show";
			}

	} catch (Exception e) {
		log.error(e.getMessage(),e);
		throw (new RuntimeException(e));
	}
}
		
	
	
	
	
	
	/**
	 * Prepares to update  logged in user password
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_SURVEY_ADMIN" })
	@RequestMapping(value = "/rpass", produces = "text/html")
	public String userUpdatePassword(Principal principal,
			Model uiModel) {
		try {
			User loggedInUser = userService.user_findByLogin(principal.getName());
			uiModel.addAttribute("user", loggedInUser);
			return "account/rpass";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	

	/**
	 * Updates  logged in user password
	 * @param oldPassword
	 * @param newPassword
	 * @param newPasswordConfirm
	 * @param proceed
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/rpass", method = RequestMethod.POST, produces = "text/html")
	public String updatePasswordPost(@RequestParam(value = "password", required = true) String oldPassword,
									@RequestParam(value = "nPassword", required = true) String newPassword,
									@RequestParam(value = "cPassword", required = true) String newPasswordConfirm,
									@RequestParam(value = "_proceed", required = false) String proceed,
									Principal principal,
									Model uiModel, 
									HttpServletRequest httpServletRequest) {
		try{
			if(proceed != null){
			
				//check that the old password is correct
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal.getName(), oldPassword);
				authenticationToken.setDetails(new WebAuthenticationDetails(httpServletRequest));
				try {
					Authentication auth = authenticationManager.authenticate(authenticationToken);
					if (auth== null || !auth.isAuthenticated()){
						//invalid password enetered
						uiModel.asMap().clear();
						uiModel.addAttribute("status", "E"); //Unmatching Passwords
						return "account/rpass";
					}

				} catch (AuthenticationException e) {
					uiModel.asMap().clear();
					uiModel.addAttribute("status", "E"); //Unmatching Passwords
					return "account/rpass";
				}
				//Check new password strenght 
				if(!GenericValidator.matchRegexp(newPassword,globalSettings.getPasswordEnforcementRegex())){
					uiModel.asMap().clear();
					uiModel.addAttribute("status", "I"); //Unmatching Passwords
					return "account/rpass";
				}
				//check that passwords match 	
				if (!newPassword.equals(newPasswordConfirm)) {
					uiModel.asMap().clear();
					
					uiModel.addAttribute("status", "U"); //Unmatching Passwords
					return "account/rpass";
				}
				User loggedInUser = userService.user_findByLogin(principal.getName());
				//All validations passed, save the HASH of the password in the database
				loggedInUser.setPassword(newPassword);
				userService.user_updatePassword(loggedInUser);
				uiModel.addAttribute("status", "S");//success
				return "account/rpass";	
			}
			else {
				return "redirect:/account/show";
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * helper function for encoding paths 
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

	/**
	 * Helper function to handle unhandled runtime exception on the controller 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		log.error(ex);
		log.error("redirect to /uncaughtException");
		return "redirect:/uncaughtException";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
