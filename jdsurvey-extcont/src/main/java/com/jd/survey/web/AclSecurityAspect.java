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
package com.jd.survey.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AclSecurityAspect {
	private static final Log log = LogFactory.getLog(AclSecurityAspect.class);	
	
	/*
	@Before("execution(* com.jd.survey.web.survey.SurveyController.showSurveyPage(..))")
	public void securitycheckBefore(JoinPoint joinPoint) {
		//AccessDeniedException
		log.info("***qqqq---------------qqqq***");
		throw new AccessDeniedException ("Access Denied");
	}
	*/

}
