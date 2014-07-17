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
package com.jd.survey.service.settings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


public class SchedulerService {

	private static final Log log = LogFactory.getLog(SchedulerService.class);
	@Autowired private SurveySettingsService surveySettingsService;

	
	
	@Scheduled(cron="0 0 22 * * ?")
	public void fireScheduledJobs() {
	
		try{
			log.debug("<-------------------------EMAIL REMINDERS----------------------------------->");
			surveySettingsService.sendEmailReminders();

		}catch (Exception e) {
			log.error("Unable To send Email Reminder" + e.getMessage() , e);
		}
	}

}
