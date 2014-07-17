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
package com.jd.survey.service.security;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.security.AuthorityDAO;
import com.jd.survey.dao.interfaces.security.GroupDAO;
import com.jd.survey.dao.interfaces.security.PasswordResetRequestDAO;
import com.jd.survey.dao.interfaces.security.UserDAO;
import com.jd.survey.dao.interfaces.settings.DepartmentDAO;
import com.jd.survey.domain.security.Authority;
import com.jd.survey.domain.security.Group;
import com.jd.survey.domain.security.PasswordResetRequest;
import com.jd.survey.domain.security.SecurityObject;
import com.jd.survey.domain.security.SecurityType;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Department;



@Transactional(readOnly = true)
@Service("UserService")
public class UserService {
	private static final Log log = LogFactory.getLog(UserService.class);	
	@Autowired	private UserDAO userDAO;
	@Autowired	private GroupDAO groupDAO;
	@Autowired	private AuthorityDAO authorityDAO;
	@Autowired	private PasswordResetRequestDAO passwordResetRequestDAO;
	@Autowired private DepartmentDAO departmentDAO;

	private static final String DATE_FORMAT = "MM/dd/yyyy";
	private static final int TIME_INTERVAL_TO_CHANGE_PASSWORD_IN_HOURS=1;

	public String user_prepareForgotPasswordMessage(Long id)  {
		try {
			
			User user = userDAO.findById(id);
			PasswordEncoder encoder = new ShaPasswordEncoder(256);
			String hash = encoder.encodePassword(user.getEmail() + new Date().getTime(), user.getSalt());
			PasswordResetRequest  passwordResetRequest = new PasswordResetRequest(user.getLogin() ,hash);
			passwordResetRequestDAO.merge(passwordResetRequest);
			return hash;
		} 
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	public static boolean isDateValid(String date, String dateFormat) 
	{
	        try {
	            DateFormat df = new SimpleDateFormat(dateFormat);
	            df.setLenient(false); 
	            df.parse(date);
	            return true;
	        } catch (ParseException e) {
	            return false;
	        }
	}
	
	public boolean user_validateDateofBirthAndLogin(String login, Date dob)  {
		try {
			User user = userDAO.findByLogin(login);
			if(user == null){
				return false; //login not found
			}
			//check the provided dob against the database	
			int days = Days.daysBetween(new DateTime(user.getDateOfBirth()), new DateTime(dob)).getDays();
			if (days == 0 ) {return true;}	else {return false;	}
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	
	public boolean user_validateForgotPasswordKey(String hash)  {
		try {

			PasswordResetRequest  passwordResetRequest = passwordResetRequestDAO.findByHash(hash);
			if (passwordResetRequest == null) {log.info("empty return" + hash);}
			if (passwordResetRequest != null && 
				passwordResetRequest.getResetDate() == null) {
				int hours = Hours.hoursBetween(new DateTime(passwordResetRequest.getRequestDate()), new DateTime(new Date())).getHours();
				if (hours <= TIME_INTERVAL_TO_CHANGE_PASSWORD_IN_HOURS ) {
					return true;
				}
			}
			return false; 
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	public PasswordResetRequest passwordResetRequest_findByHash(String hash) {
		return passwordResetRequestDAO.findByHash(hash);
	}
	
	
	
	
	
	
	
	
	
	

	public Set<User> user_findAll() throws DataAccessException {
		return userDAO.findAll();
	}
	public Set<User> user_findAll(int startResult,int maxRows) throws DataAccessException {
		return userDAO.findAll(startResult, maxRows);
	}
	
	
	public Set<User> user_findAllInternal() throws DataAccessException {
		return userDAO.findAllInternal();
	}
	public Set<User> user_findAllInternal(int startResult,int maxRows) throws DataAccessException {
		return userDAO.findAllInternal(startResult, maxRows);
	}
	
	
	
	
	public Set<User> user_findAllExternal() throws DataAccessException {
		return userDAO.findAllExternal();
	}
	public Set<User> user_findAllExternal(int startResult,int maxRows) throws DataAccessException {
		return userDAO.findAllExternal(startResult, maxRows);
	}
	
	
	
	public Long user_getCount() {
		return userDAO.getCount();
	}
	public Long user_getCountInternal() {
		return userDAO.getCountInternal();
	}
	public Long user_getCountExternal() {
		return userDAO.getCountExternal();
	}
	
	public User user_findById(Long id) {
		User user =  userDAO.findById(id);
		user.setPassword("");
		return user; 
	}
	
	
	public User user_findByLogin(String login){
		return userDAO.findByLogin(login);
	}
	public User user_findByEmail(String email){
		return userDAO.findByEmail(email);
	}
	
	
	
	
	public Set<User> user_searchByFirstName(String firstName)  {
		return userDAO.searchByFirstName(firstName);
	}
	public Set<User> user_searchByLastName(String lastName){
		return userDAO.searchByLastName(lastName);
	}
	public Set<User> user_searchByFirstNameAndLastName(String firstName , String lastName){
		return userDAO.searchByFirstNameAndLastName(firstName,lastName);
	}
	public Set<User> user_searchByLogin(String login){
		return userDAO.searchByLogin(login);
	}
	public Set<User> user_searchByEmail(String email){
		return userDAO.searchByEmail(email);
	}
	
	
	@Transactional(readOnly = false)
	public User user_updatePassword(User user) {
			User dbUser = userDAO.findById(user.getId());
			dbUser.setLastUpdateDate(new Date());
			PasswordEncoder encoder = new ShaPasswordEncoder(256);
			dbUser.setPassword(encoder.encodePassword(user.getPassword(), user.getSalt()));
			return userDAO.merge(dbUser);
	}
	
	
	@Transactional(readOnly = false)
	public User user_updatePassword(User user, PasswordResetRequest passwordResetRequest) {
			//update the request
			passwordResetRequest.setResetDate(new Date());
			passwordResetRequestDAO.merge(passwordResetRequest);

			//update password
			User dbUser = userDAO.findById(user.getId());
			dbUser.setLastUpdateDate(new Date());
			PasswordEncoder encoder = new ShaPasswordEncoder(256);
			dbUser.setPassword(encoder.encodePassword(user.getPassword(), user.getSalt()));
			return userDAO.merge(dbUser);
	}

	@Transactional(readOnly = false)
	public User user_updateInformation(User user) {
		User dbUser = userDAO.findById(user.getId());
		dbUser.setLastUpdateDate(new Date());			
		dbUser.setLogin(user.getLogin());
		dbUser.setFirstName(user.getFirstName());
		dbUser.setMiddleName(user.getMiddleName());
		dbUser.setLastName(user.getLastName());
		dbUser.setEmail(user.getEmail());
		return userDAO.merge(dbUser);
	}
	
	
	@Transactional(readOnly = false)
	public User user_merge(User user) {
		//create save the password
		if (user.getId() == null) {
			user.setCreationDate(new Date());
			user.setLastUpdateDate(new Date());
			PasswordEncoder encoder = new ShaPasswordEncoder(256);
			user.setPassword(encoder.encodePassword(user.getPassword(), user.getSalt()));
			return userDAO.merge(user);
		}
		
		else
			//update do not update the password
			{	
			User dbUser = userDAO.findById(user.getId());
			dbUser.setLastUpdateDate(new Date());			
			dbUser.setLogin(user.getLogin());
			dbUser.setFirstName(user.getFirstName());
			dbUser.setDateOfBirth(user.getDateOfBirth());
			dbUser.setMiddleName(user.getMiddleName());
			dbUser.setLastName(user.getLastName());
			dbUser.setEmail(user.getEmail());
			dbUser.setEnabled(user.getEnabled());
			dbUser.setGroups(user.getGroups());
			dbUser.setDepartments(user.getDepartments());
			dbUser.setSurveyDefinitions(user.getSurveyDefinitions());
			return userDAO.merge(dbUser);	
		}
	}

		
	
	
	@Transactional(readOnly = false)
	public void user_remove(User user) {
		userDAO.remove(user);
	}

	
	
		
	
	
	
	
	public Boolean user_ValidateLoginIsUnique(User user) throws DataAccessException {
		Boolean inValid = true;
		//get all users the same login
		User u = userDAO.findByLogin(user.getLogin());

		//Insert case  null id
		if (user.getId() != null) {
			// check that there are no users with same login 
			if (u != null && u.getId().equals(user.getId())) {
				inValid= false;
			}
			if (u.getId().equals(user.getId()) == false) {
				inValid= true;
			}
		}
		else{
			if (u.getId().equals(user.getId()) == false && !u.getLogin().equals(user.getLogin())) {
				inValid= true;
			}
			else {
				if (u.getLogin().equals(user.getLogin()) == true ){
					inValid= true;
				}
			}
		}
		return inValid;
	}

	public Boolean user_ValidateEmailIsUnique(User user) throws DataAccessException {
		Boolean inValid = false;
		//get all users the same login
		User u = userDAO.findByEmail(user.getEmail());

		//Insert case  null id
		if (user.getId() != null) {
			// check that there are no users with same login 
			if (u != null && u.getId().equals(user.getId()) == true) {
				inValid= false;
			}
			else{
				if(u==null){
					return false;
				}
				if (u.getId().equals(user.getId()) == false) {
					inValid= true;
				}
			}
			
		}
		else{
			if (u.getId().equals(user.getId()) == false && !u.getEmail().equals(user.getEmail())) {
				inValid= true;
			}
			else{
				if (u.getId().equals(user.getId()) == false){
					inValid= true;
				}
			}
			
		}
		return inValid;
	}
	
	public Boolean user_ValidateGroupUserEmpty(User user) throws DataAccessException {
		Boolean isValid = false;
		if (user.getGroups().isEmpty() == true) {
			// check that there are no users with same login 
				isValid= false;
		}
		else{
			isValid= true;
		}
		
		return isValid;
	}
	
	public Boolean user_ValidateDepartmentUserEmpty(User user) throws DataAccessException {
		Boolean isValid = false;
		
		if  (user.getDepartments().isEmpty() == true)
		{
			isValid=false;
		}
		else{
			isValid=true;
		}
		return isValid;
	}


	public Boolean user_ValidateSurveyDefinitionUserEmpty(User user) throws DataAccessException {
		Boolean isValid = false;
		
		if  (user.getSurveyDefinitions().isEmpty() == true)
		{
			isValid=false;
		}
		else{
			isValid=true;
		}
		return isValid;
	}
	


	public Set<Group> group_findAll(SecurityObject securityObject) throws DataAccessException {
		if (securityObject != null) {
			if (securityObject.getType().equals(SecurityType.I)){ 
				return groupDAO.findAllInternal();
			}
			else {
				return groupDAO.findAllExternal();
			}
		}
		else {
			return groupDAO.findAll();
		}
	}
	
	public Set<Group> group_findAll(int startResult,int maxRows) throws DataAccessException {
		return groupDAO.findAll(startResult, maxRows);
	}
	public Long group_getCount() {
		return groupDAO.getCount();
	}
	public Group group_findById(Long id) {
		return groupDAO.findById(id);
	}
	public Group group_findByName(String name) {
		return groupDAO.findByName(name);
	}
	
	@Transactional(readOnly = false)
	public Group group_merge(Group group) {
		Group dbGroup = groupDAO.findById(group.getId());
		/*
		for (Authority authority : group.getAuthorities()) {
			authority = authorityDAO.merge(authority);
		
		}
		*/
		group.setUsers(dbGroup.getUsers());
		return groupDAO.merge(group);
	}
	
	@Transactional(readOnly = false)
	public void group_remove(Group group) {
		groupDAO.remove(group);
	}
	

	public Set<User> user_findAll(SecurityObject securityObject ) throws DataAccessException {
		if (securityObject != null){
			if (securityObject.getType().equals(SecurityType.I)){
				return userDAO.findAllInternal();
			}
			else{
				return userDAO.findAllExternal();
			}
		}
		else {
			return userDAO.findAll();
		}
	}
	
	
	
	
	public Boolean group_ValidateNameIsUnique(Group group) throws DataAccessException {
		Boolean isValid = true;
		//get all appplication types with the same name
		Group g = groupDAO.findByName(group.getName());
		//Insert case  null id
		if (group.getId() == null) {
			// check that there are no users with same login 
			if (g != null) {
				isValid= false;
			}
		}
		else{
			if (g != null &&  !g.getId().equals(group.getId())) {
				isValid= false;
			}
		}
		return isValid;
	}

	public boolean group_ValidateGroupEmpty(Group group) {
		Boolean isValid = false;
		//Group g = groupDAO.findByName(group.getName());
		
		if (group.getAuthorities().isEmpty() == true) {
			// check that there are no users with same login 
				isValid= false;
		}
		else{
			isValid= true;
		}
		
		return isValid;
	}




	public Set<Authority> authority_findAll(SecurityObject securityObject ) throws DataAccessException {
		if (securityObject != null){
			if (securityObject.getType().equals(SecurityType.I)){
				return authorityDAO.findAllInternal();
			}
			else{
				return authorityDAO.findAllExternal();
			}
		}
		else {
			return authorityDAO.findAll();
		}
	}
	
	public Set<Authority> authority_findAll(int startResult,int maxRows) throws DataAccessException {
		return authorityDAO.findAll(startResult, maxRows);
	}
	public Long authority_getCount() {
		return authorityDAO.getCount();
	}
	public Authority authority_findById(Long id) {
		return authorityDAO.findById(id);
	}
	
	public Set<Authority> authority_findbyUserId(Long id) {
		return authorityDAO.findbyUserId(id);
	}
	
	
	public SecurityObject authority_findByName(String name) {
		return authorityDAO.findByName(name);
	}
	@Transactional(readOnly = false)
	public Authority authority_merge(Authority authority) {
		return authorityDAO.merge(authority);
	}
	@Transactional(readOnly = false)
	public void authority_remove(SecurityObject authority) {
		authorityDAO.remove(authority);
	}
	public Boolean authority_ValidateNameIsUnique(Authority authority) throws DataAccessException {
		Boolean isValid = true;
		//get all appplication types with the same name
		Authority g = authorityDAO.findByName(authority.getName());
		//Insert case  null id
		if (authority.getId() == null) {
			// check that there are no users with same login 
			if (g != null) {
				isValid= false;
			}
		}
		else{
			if (g != null &&  !g.getId().equals(authority.getId())) {
				isValid= false;
			}
		}
		return isValid;
	}

	
	
	
	
	
	public Set<Department> department_findAll() throws DataAccessException {
		return departmentDAO.findAll();
	}
	public Set<Department> department_findAll(int startResult,int maxRows) throws DataAccessException {
		return departmentDAO.findAll(startResult, maxRows);
	}
	public Long department_getCount() {
		return departmentDAO.getCount();
	}
	public Department department_findById(Long id) {
		return departmentDAO.findById(id);
	}
	public Department department_findByName(String name) {
		return departmentDAO.findByName(name);
	}


	
	
	

}