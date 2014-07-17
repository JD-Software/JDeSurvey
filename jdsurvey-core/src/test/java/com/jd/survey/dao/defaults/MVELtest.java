package com.jd.survey.dao.defaults;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.settings.QuestionDAO;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyPage;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.service.survey.SurveyService;



public class MVELtest {
	
	//@Autowired 	private SurveyService surveyService;
	
	public static void main(String args[]) {
		ParserContext ctx = new ParserContext();
		ctx.setStrongTyping(true); 
		ctx.addInput("page", SurveyPage.class);

		//compile an expression 'str.toUpperCase()'
		String texpression = "e= page.questionAnswers[0].booleanAnswerValue;e==null? false:true;";
		
		
		try {
			ExecutableStatement ce = (ExecutableStatement) MVEL.compileExpression(texpression, ctx);
			Class returnType = ce.getKnownEgressType(); // returns java.lang.String in this case.
			System.out.println(returnType.toString());
			if (returnType.equals(Boolean.class)) {
				System.out.println("Pass");}
			else{
				System.out.println("No Pass");
				}
		}
		catch (Exception e) {
			System.out.println("Invalid Expression");
			System.out.println(e);
			
		} 
		
		
							  
		//String texpression = "survey.pages[0].questions[0].booleanAnswerValue";
		//Survey  survey =surveyService.Survey_findById((long) 5);
		Survey  survey = new Survey();
		
		//System.out.println(survey.getTypeName());
		//System.out.println(survey.getPages().first().getTitle());
		
		/*
		System.out.println("---------------->" + survey.getPages().first().getQuestions().get(0).getBooleanAnswerValue());				
		Map map = new HashMap();
		map.put("survey", survey);
		
		
		for (SurveyPage surveyPage : survey.getPages()){

				System.out.println(surveyPage.getOrder());
				Boolean pageVisibility = (Boolean) MVEL.eval(surveyPage.getVisibilityExpression(), map);
				System.out.println(pageVisibility);
				if (pageVisibility) {
					System.out.println(surveyPage.getOrder());
				}
		}
		
		*/
		
		
		
		/*
		 String expression = "age > 25 ? 2000 : 1000;";
          
		  		  
		  Map vars = new HashMap();
          vars.put("age", new Integer(24));

          // We know this expression should return a boolean.
          Integer result = (Integer) MVEL.eval(expression, vars);
          
          System.out.println(result);
            	  
     
          
          //compiled mode
          String expression2 = "foobar3 > 99";
          // Compile the expression.
          Serializable compiled = MVEL.compileExpression(expression2);

          Map vars2 = new HashMap();
          vars2.put("foobar", new Integer(100));

          // Now we execute it.
          Boolean result2 = (Boolean) MVEL.executeExpression(compiled, vars2);
          
          if (result2.booleanValue()) {
              System.out.println("It works!");
          }
          else{
        	  System.out.println("It does not work!");
          }
                    
          */
          
		
	}
}
