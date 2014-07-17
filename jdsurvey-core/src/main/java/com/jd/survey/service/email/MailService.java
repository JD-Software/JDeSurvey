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
package com.jd.survey.service.email;

import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.jd.survey.domain.settings.SurveyDefinition;

@Service("MailService")
public class MailService {
	@Autowired  private JavaMailSender mailSender;
	/**
	 * 
	 * @param toEmailAddress
	 * @param ccEmailAdress
	 * @param emailSubject
	 * @param emailHtmlBodyText
	 */
	public void sendEmail(final String toEmailAddress,
						  final String ccEmailAdress,
    					  final String emailSubject,
    					  final String emailHtmlBodyText) {
    	
    	MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws Exception {
    			MimeMessageHelper message = new MimeMessageHelper(mimeMessage);    			
    			message.setTo(toEmailAddress);
    			message.setCc(ccEmailAdress);
    			message.setSubject(emailSubject);
    			message.setText(emailHtmlBodyText, true);
    		}
    	};
    	mailSender.send(preparator);
    }
	
	
	/**
	 * List of cc required
	 */
	public void sendEmail(final String toEmailAddress,
						  final String[] ccEmailAdress,
    					  final String emailSubject,
    					  final String emailHtmlBodyText) {
    	
    	MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws Exception {
    			MimeMessageHelper message = new MimeMessageHelper(mimeMessage);    			
    			message.setTo(toEmailAddress);
    			message.setCc(ccEmailAdress);
    			message.setSubject(emailSubject);
    			message.setText(emailHtmlBodyText, true);
    		}
    	};
    	mailSender.send(preparator);
    }
	
	
	/**
	 * No cc required
	 */
	public void sendEmail(final String toEmailAddress,			 
						  final String emailSubject,
						  final String emailHtmlBodyText) {

			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);			
				message.setTo(toEmailAddress);				
				message.setSubject(emailSubject);
				message.setText(emailHtmlBodyText, true);
			  }
			};
			mailSender.send(preparator);
	}

	/**
	 * List Of Email Addresses
	 
	public void sendEmail(final InternetAddress toEmailAddress,			 
			final String emailSubject,
			final String emailHtmlBodyText) {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);			
				message.setTo(toEmailAddress);				
				message.setSubject(emailSubject);
				message.setText(emailHtmlBodyText, true);
			}
		};
		mailSender.send(preparator);
	}

*/
	

    
  
    
}