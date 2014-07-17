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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.jd.survey.GlobalSettings;
import com.jd.survey.domain.security.SecurityType;
import com.jd.survey.domain.security.User;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.ApplicationSettingsService;
import com.jd.survey.service.settings.SurveySettingsService;



@RequestMapping("/security/users")
@Controller
public class UserController {
	private static final Log log = LogFactory.getLog(UserController.class);	

	private static final int DEFAULT_NUMBER_OR_RECORDS = 25;	

	@Autowired	private ApplicationSettingsService applicationSettingsService;
	@Autowired	private UserService userService;
	@Autowired	private SurveySettingsService surveySettingsService;
	private GlobalSettings globalSettings;
	
	@PostConstruct
	public void initIt() throws Exception {
		globalSettings = applicationSettingsService.getSettings();
	}

	

	
	/**
	 * Prepares the form to create a new internal user  
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(params = "icreate", produces = "text/html")
	public String createInternalGet(Principal principal,
			Model uiModel,
			HttpServletRequest httpServletRequest) {
		try { 
			
			User loggedInUser = userService.user_findByLogin(principal.getName());
			User user =  new User(SecurityType.I);
			populateEditForm(uiModel, user,loggedInUser);
			return "security/users/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	
	/**
	 * Prepares the form to create a new external user
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(params = "ecreate", produces = "text/html")
	public String createExternalGet(Principal principal,
									Model uiModel,
									HttpServletRequest httpServletRequest) {
		try {
			User loggedInUser = userService.user_findByLogin(principal.getName());
			User user =  new User(SecurityType.E);
			populateEditForm(uiModel, user,loggedInUser);
			return "security/users/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}

	
	
	/**
	 * Creates a new user
	 * @param proceed
	 * @param user
	 * @param bindingResult
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String createPost(@RequestParam(value = "_proceed", required = false) String proceed,
							@Validated({User.UserInfo.class,User.Password.class }) User user, 
							BindingResult bindingResult, 
							Principal principal,
							Model uiModel, 
							HttpServletRequest httpServletRequest) {
		try {
			User loggedInUser = userService.user_findByLogin(principal.getName());
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, user,loggedInUser);
					return "security/users/create";
				}
				//check that login is unique
				if (userService.user_findByLogin(user.getLogin()) != null && userService.user_ValidateLoginIsUnique(user) == true) {
					bindingResult.rejectValue("login", "field_unique");
					populateEditForm(uiModel, user,loggedInUser);
					return "security/users/create";
				}
				//check that email is unique
				if (userService.user_findByEmail(user.getEmail()) != null && userService.user_ValidateEmailIsUnique(user) == true){
					bindingResult.rejectValue("email", "field_unique");
					populateEditForm(uiModel, user,loggedInUser);
					return "security/users/create";
				}
				//check that passwords match
				if (!user.getPassword().equals(user.getConfirmPassword())) {
					bindingResult.rejectValue("confirmPassword", "security_password_reset_confirm_passwords_unmatching");
					populateEditForm(uiModel, user,loggedInUser);
					return "security/users/create";
				}
				if (!user.getConfirmPassword().matches(globalSettings.getPasswordEnforcementRegex())){
					bindingResult.rejectValue("confirmPassword", globalSettings.getPasswordEnforcementMessage(), this.globalSettings.getPasswordEnforcementMessage());
					populateEditForm(uiModel, user,loggedInUser);
					return "security/users/create";
				}
				uiModel.asMap().clear();
				user = userService.user_merge(user);
				return "redirect:/security/users/" + encodeUrlPathSegment(user.getId().toString(), httpServletRequest);
			}
			else{
				if (user.getType().equals(SecurityType.I)) {return "redirect:/security/users/internal";}
				if (user.getType().equals(SecurityType.E)) {return "redirect:/security/users/external";}
			}
			return "redirect:/security";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	

	
	/**
	 * Shows a single user
	 * @param id
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id,
			Principal principal,
			Model uiModel) {
		try {
			User loggedInUser = userService.user_findByLogin(principal.getName());
			populateEditForm(uiModel,userService.user_findById(id),loggedInUser);
			uiModel.addAttribute("isShow", true);
			uiModel.addAttribute("itemId", id);
			return "security/users/show";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	
	/**
	 * lists internal users
	 * @param page
	 * @param size
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/internal" , produces = "text/html")
	public String list_internal(@RequestParam(value = "page", required = false) Integer page, 
								@RequestParam(value = "size", required = false) Integer size, 
								Principal principal,
								Model uiModel) {
		try {
			int sizeNo = size == null ? DEFAULT_NUMBER_OR_RECORDS : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("users", userService.user_findAllInternal(firstResult, sizeNo));
			float nrOfPages = (float) userService.user_getCountInternal() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			uiModel.addAttribute("listmode" ,"internal");
			return "security/users/list";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	

	/**
	 * Lists external users
	 * @param page
	 * @param size
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/external",  produces = "text/html")
	public String list_external(@RequestParam(value = "page", required = false) Integer page, 
								@RequestParam(value = "size", required = false) Integer size, 
								Principal principal,
								Model uiModel) {
		try {
			int sizeNo = size == null ? DEFAULT_NUMBER_OR_RECORDS : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("users", userService.user_findAllExternal(firstResult, sizeNo));
			float nrOfPages = (float) userService.user_getCountExternal() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			uiModel.addAttribute("listmode" ,"external");

			return "security/users/list";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	/**
	 * Prepares the form to search users by first and last name
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/search/n",  produces = "text/html")
	public String search_byName(Principal principal,
			Model uiModel) {
		try {
			uiModel.addAttribute("newSearch",true);
			uiModel.addAttribute("searchType" , "name");
			uiModel.addAttribute("user" , new User());
			return "security/users/search";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	

	/**
	 * Search users by First and Last name
	 * @param proceed
	 * @param user
	 * @param bindingResult
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/search/n", method = RequestMethod.POST, produces = "text/html")
	public String search_byName_post(@RequestParam(value = "_proceed", required = false) String proceed,
									@Validated({User.UserSearchByName.class }) User user, 
									BindingResult bindingResult, 
									Principal principal,
									Model uiModel, 
									HttpServletRequest httpServletRequest) {
		try {
			User loggedInUser = userService.user_findByLogin(principal.getName());
			uiModel.addAttribute("searchType" , "name");
			if(proceed != null){
				if (bindingResult.hasErrors()) {return "security/users/search";}
				if ((user.getFirstName() == null || user.getFirstName().isEmpty()) 
						&& 
					(user.getLastName() == null || user.getLastName().isEmpty())) {
					uiModel.addAttribute("users" , userService.user_findAll());
					return "security/users/search";
				}
				if (user.getFirstName() != null && !user.getFirstName().isEmpty() 
						&&
						user.getLastName() != null && !user.getLastName().isEmpty()) {
					uiModel.addAttribute("users" , userService.user_searchByFirstNameAndLastName(user.getFirstName(), user.getLastName()));
					return "security/users/search";
				}
				if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
					uiModel.addAttribute("users" , userService.user_searchByFirstName(user.getFirstName()));
					return "security/users/search";
				}
				if (user.getLastName() != null && !user.getLastName().isEmpty()) {
					uiModel.addAttribute("users" , userService.user_searchByLastName(user.getLastName()));
					return "security/users/search";
				}
				return null;	
			}
			else {
				//reset
				uiModel.addAttribute("newSearch",true);
				uiModel.addAttribute("user" , new User());
				return "security/users/search";
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}

	
	/**
	 * Prepares the form to search internal users by Login
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/search/l",  produces = "text/html")
	public String search_byLogin(Principal principal,
			Model uiModel) {
		try {
			uiModel.addAttribute("newSearch",true);
			uiModel.addAttribute("searchType" , "login");
			uiModel.addAttribute("user" , new User());
			return "security/users/search";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	/**
	 * Search  Internal users by Login
	 * @param proceed
	 * @param user
	 * @param bindingResult
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/search/l", method = RequestMethod.POST,  produces = "text/html")
	public String search_byLogin(@RequestParam(value = "_proceed", required = false) String proceed,
								@Validated({User.UserSearchByLogin.class }) 
								User user, 
								BindingResult bindingResult, 
								Principal principal,
								Model uiModel, 
								HttpServletRequest httpServletRequest) {
		try {
			User loggedInUser = userService.user_findByLogin(principal.getName());
			uiModel.addAttribute("searchType" , "login");
			if(proceed != null){
				if (bindingResult.hasErrors()) {return "security/users/search";}
				if ((user.getLogin() != null || !user.getLogin().isEmpty())) {
					uiModel.addAttribute("users" , userService.user_searchByLogin(user.getLogin()));
					return "security/users/search";
				}
				if ((user.getLogin() == null || user.getLogin().isEmpty())) {
					uiModel.addAttribute("users" , userService.user_findAll());
					return "security/users/search";
				}
				return null;	
			}
			else {
				//reset
				uiModel.addAttribute("newSearch",true);
				uiModel.addAttribute("user" , new User());
				return "security/users/search";
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	

	/**
	 * Prepares the form to search external users by email
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/search/e",  produces = "text/html")
	public String search_byEmail(Principal principal,
			Model uiModel) {
		try {
			uiModel.addAttribute("newSearch",true);
			uiModel.addAttribute("searchType" , "email");
			uiModel.addAttribute("user" , new User());
			return "security/users/search";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	/**
	 * Search external users by email
	 * @param proceed
	 * @param user
	 * @param bindingResult
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/search/e", method = RequestMethod.POST ,  produces = "text/html")
	public String search_byEmail(@RequestParam(value = "_proceed", required = false) String proceed,
								@Validated({User.UserSearchByEmail.class }) 
								User user, 
								BindingResult bindingResult, 
								Principal principal,
								Model uiModel, 
								HttpServletRequest httpServletRequest) {

		try {
			User loggedInUser = userService.user_findByLogin(principal.getName());
			uiModel.addAttribute("searchType" , "email");
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					//errors
					return "security/users/search";
				}
				if ((user.getEmail() != null || !user.getEmail().isEmpty())) {
					uiModel.addAttribute("users" , userService.user_searchByEmail(user.getEmail()));
					return "security/users/search";
				}
				if ((user.getEmail() == null || user.getEmail().isEmpty())) {
					uiModel.addAttribute("users" , userService.user_findAll());
					return "security/users/search";
				}
				return null;	
			}
			else {
				//reset
				uiModel.addAttribute("newSearch",true);
				uiModel.addAttribute("user" , new User());
				return "security/users/search";
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}


	

	/**
	 * Prepares the form to update user information 
	 * @param id
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateUserPrepare(@PathVariable("id") Long id, 
									Principal principal,
									Model uiModel) {
		log.info("updateForm(): id=" + id);
		try{
			User loggedInUser = userService.user_findByLogin(principal.getName());
			populateEditForm(uiModel, userService.user_findById(id),loggedInUser);
			return "security/users/update";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	
	/**
	 * Updates the user information, except password
	 * @param proceed
	 * @param user
	 * @param bindingResult
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						 @Validated({User.UserInfo.class })  User user, 
						 BindingResult bindingResult, 
						 Principal principal,
						 Model uiModel, 
						 HttpServletRequest httpServletRequest) {
		log.info("update(): handles PUT");
		try{
			User loggedInUser = userService.user_findByLogin(principal.getName());
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, user,loggedInUser);
					return "security/users/update";
				}
				if (userService.user_findByLogin(user.getLogin()) != null && userService.user_ValidateLoginIsUnique(user) == true) {
					bindingResult.rejectValue("login", "field_unique");
					populateEditForm(uiModel, user,loggedInUser);
					return "security/users/update";
				}
				if (userService.user_findByEmail(user.getEmail()) != null && userService.user_ValidateEmailIsUnique(user) == true){
					bindingResult.rejectValue("email", "field_unique");
					populateEditForm(uiModel, user,loggedInUser);
					return "security/users/update";
				}
				uiModel.asMap().clear();
				user = userService.user_merge(user);
				return "redirect:/security/users/" + encodeUrlPathSegment(user.getId().toString(), httpServletRequest);
					
				
			}
			else{

				if (user.getType().equals(SecurityType.I)){
					return "redirect:/security/users/internal" ;
				}

				if (user.getType().equals(SecurityType.E)){
					return "redirect:/security/users/external" ;	
				}
			}
			return "redirect:/security";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	
	/**
	 * Prepares the form to update user password
	 * @param id
	 * @param principal
	 * @param uiModel
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/pass/{id}", produces = "text/html")
	public String updatePasswordPrepare(@PathVariable("id") Long id, 
										Principal principal,
										Model uiModel) {
		log.info("updateForm(): id=" + id);
		try{
			User loggedInUser = userService.user_findByLogin(principal.getName());
			populateEditForm(uiModel, userService.user_findById(id),loggedInUser);
			return "security/users/pass";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	/**
	 * Updates the user information, except password
	 * @param proceed
	 * @param user
	 * @param bindingResult
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/pass", method = RequestMethod.PUT, produces = "text/html")
	public String updatepassword(@RequestParam(value = "_proceed", required = false) String proceed,
						 		 @Validated({User.Password.class})  User user, 
						 		 BindingResult bindingResult, 
						 		 Principal principal,
						 		 Model uiModel, 
						 		 HttpServletRequest httpServletRequest) {
		try{
			User loggedInUser = userService.user_findByLogin(principal.getName());
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					user.refreshUserInfo(userService.user_findById(user.getId()));
					return "security/users/pass";
				}
				//check that passwords match
				if (!user.getPassword().equals(user.getConfirmPassword())) {
					user.refreshUserInfo(userService.user_findById(user.getId()));
					bindingResult.rejectValue("confirmPassword", "security_password_reset_confirm_passwords_unmatching");
					return "security/users/pass";
				}
				//check RegEx
				if (!user.getConfirmPassword().matches(globalSettings.getPasswordEnforcementRegex())){
					user.refreshUserInfo(userService.user_findById(user.getId()));
					bindingResult.rejectValue("confirmPassword", globalSettings.getPasswordEnforcementMessage(), this.globalSettings.getPasswordEnforcementMessage());
					return "security/users/pass";
				}
				user.refreshUserInfo(userService.user_findById(user.getId()));
				user = userService.user_updatePassword(user);
				uiModel.asMap().clear();
				return "redirect:/security/users/" + encodeUrlPathSegment(user.getId().toString(), httpServletRequest);
			}
			else{
				if (user.getType().equals(SecurityType.I)){
					return "redirect:/security/users/internal" ;
				}
				if (user.getType().equals(SecurityType.E)){
					return "redirect:/security/users/external" ;	
				}
			}
			return "redirect:/security";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	/**
	 * Deletes the user
	 * @param id
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") Long id, 
						Principal principal,
						Model uiModel, 
						HttpServletRequest httpServletRequest) {

		log.info("delete(): id=" + id);
		try {
			String login = principal.getName();
			User loggedUser = userService.user_findByLogin(login);
			User user = userService.user_findById(id);

			if (user == loggedUser ){
				uiModel.addAttribute("hasErrors", true);
				return "security/";
			}else{
				User otherUsers = userService.user_findById(id);
				userService.user_remove(otherUsers);
				uiModel.asMap().clear();

				if (user.getType().equals(SecurityType.I)){
					return "redirect:/security/users/internal" ;
				}

				if (user.getType().equals(SecurityType.E)){
					return "redirect:/security/users/external" ;	
				}

			}
			return "redirect:/security/";			

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}

	
	
	/*
	@Secured({"ROLE_ADMIN"})
	@RequestMapping( params = "export", produces = "text/html")
	public ModelAndView userReport(Principal principal,
									Model uiModel,
									HttpServletRequest httpServletRequest)  {
		try{
			Set<User> users = userService.user_findAll();
			ModelAndView modelAndView =new ModelAndView("userList");
			modelAndView.addObject("users",users);
			return modelAndView;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	*/



	/**
	 * Helper function to populate the model 
	 * @param uiModel
	 * @param user
	 * @param loggedInUser
	 */
	void populateEditForm(Model uiModel, User user, User loggedInUser ) {
		try{
			uiModel.addAttribute("user", user);
			uiModel.addAttribute("authorities", userService.authority_findbyUserId(user.getId()));
			uiModel.addAttribute("groups", userService.group_findAll(user));
			uiModel.addAttribute("departments", userService.department_findAll());
			uiModel.addAttribute("surveyDefinitions", surveySettingsService.surveyDefinition_findAllInternal(loggedInUser));

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}
	
	
	
	/**
	 * Helper method that encodes a string
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
	 * Helper method that handles the response in case of on an unhandled runtime error
	 * @param pathSegment
	 * @param httpServletRequest
	 * @return
	 */
	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		log.error(ex);
		log.error("redirect to /uncaughtException");
		return "redirect:/uncaughtException";
	}
}
