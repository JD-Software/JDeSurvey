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
package com.jd.survey.web.help;

import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.survey.service.settings.SurveySettingsService;



@RequestMapping("/help")
@Controller
public class HelpController {
	private static final Log log = LogFactory.getLog(HelpController.class);	
	private static final long HELP_VELOCITY_TEMPLATE_ID = 3;

	@Autowired	private SurveySettingsService surveySettingsService;

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(produces = "text/html")
	public String getHelpPage(Model uiModel) {
		try {
				
				StringWriter sw = new StringWriter();
				VelocityContext velocityContext = new VelocityContext();
				Velocity.evaluate(velocityContext, sw, "velocity-log" , 
								  surveySettingsService.velocityTemplate_findById(HELP_VELOCITY_TEMPLATE_ID).getDefinition());
				
				
				uiModel.addAttribute("helpContent", sw.toString().trim());
				
				
				return "help/index";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/about", produces = "text/html",method = RequestMethod.GET)
	public String getAboutPage(Model uiModel) {
		try {
				return "help/about";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	

}
