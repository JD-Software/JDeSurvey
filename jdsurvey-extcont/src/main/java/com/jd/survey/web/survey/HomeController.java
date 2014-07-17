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
package com.jd.survey.web.survey;





import java.security.Principal;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;



@RequestMapping({"/"})
@Controller
public class HomeController {
	private static final Log log = LogFactory.getLog(HomeController.class);	
	
	/**
	 * Lists all the available survey types
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@RequestMapping(produces = "text/html",method = RequestMethod.GET)
	public String listSurveys(Model uiModel,
					   		  Principal principal,
					   		  HttpServletRequest httpServletRequest) {
		try{
			return "redirect:/private";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
}
