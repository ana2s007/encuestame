/*
 ************************************************************************************
 * Copyright (C) 2001-2011 encuestame: system online surveys Copyright (C) 2011
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.test.business.service;

import junit.framework.Assert;
import org.encuestame.business.service.AbstractSurveyService;
import org.encuestame.core.service.ISurveyService;
import org.encuestame.persistence.domain.question.Question;
import org.encuestame.persistence.domain.question.QuestionAnswer;
import org.encuestame.persistence.domain.security.Account;
import org.encuestame.persistence.domain.security.UserAccount;
import org.encuestame.persistence.domain.survey.Survey;
import org.encuestame.persistence.domain.survey.SurveySection;
import org.encuestame.test.business.config.AbstractSpringSecurityContext;
import org.encuestame.util.exception.EnMeException;
import org.encuestame.util.exception.EnMeNoResultsFoundException;
import org.encuestame.utils.categories.test.DefaultTest;
import org.encuestame.utils.enums.QuestionPattern;
import org.encuestame.utils.enums.TypeSearch;
import org.encuestame.utils.json.FolderBean;
import org.encuestame.utils.json.QuestionBean;
import org.encuestame.utils.web.QuestionAnswerBean;
import org.encuestame.utils.web.SurveyBean;
import org.encuestame.utils.web.UnitSurveySection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test of {@link AbstractSurveyService}
 * @author Picado, Juan juanATencuestame.org
 * @since 05/12/2009 15:04:56
 */
@Category(DefaultTest.class)
public class TestSurveyService  extends  AbstractSpringSecurityContext{

    /** {@link AbstractSurveyService} */
    @Autowired
    private ISurveyService surveyService;

    /** {@link Question} */
    private Question question;

    /** {@link Account} **/
    private Account user;

    private UserAccount userSecondary;

    private List<QuestionAnswerBean> answers;

    /** {@link QuestionBean} **/
    private QuestionBean questionBean;

    /** Creation date of the survey. **/
    private Calendar mySurveyDate = Calendar.getInstance();

    /** Max value to show items**/
    private Integer MAX_RESULTS = 10;

    /** Start value **/
    private Integer START_RESULTS = 0;

    /** **/
    private String myUsername;
    
    /**
     * Mock HttpServletRequest.
     */
    MockHttpServletRequest request;

     /**
     *
     */
    @Before
    public void serviceInit(){
         this.user = createUser("testEncuesta", "testEncuesta123");
         this.userSecondary = createUserAccount("user", this.user);
         this.question = createQuestion("Why the sky is blue?","html");
         createQuestionAnswer("Yes", this.question,"SSSA");
         //this.questionBean = createUnitQuestionBean("", 1L, 1L, listAnswers, pattern)
         answers = new ArrayList<QuestionAnswerBean>();
         answers.add(createAnswersBean("2DFAAS", "Yes", question.getQid()));
         answers.add(createAnswersBean("4DSWGK", "No", question.getQid()));
         questionBean = createUnitQuestionBean("questionName", 1L, this.user.getUid(),
                    this.answers);
         this.myUsername = getSpringSecurityLoggedUserAccount().getUsername();
         request = new MockHttpServletRequest();
		 request.addPreferredLocale(Locale.ENGLISH);  
    }

    /**
     * Test Load All Questions.
     * @throws EnMeException exception
     */
    @Test
    public void testloadAllQuestions() throws EnMeException{
        final List<QuestionBean> alist = surveyService.loadAllQuestions();
        assertEquals("Should be equals", 1, alist.size());
    }

    /**
     * Test Create Question.
     * @throws EnMeException
     **/
    @Test
    public void testCreateQuestion() throws EnMeException {
        this.surveyService.createQuestion(this.questionBean);
        assertNotNull(questionBean);
    }

    /**
     *
     * @throws EnMeException
     */
    @Test(expected = OutOfMemoryError.class)
    @Ignore
    public void testCreateQuestionException() throws EnMeException {
        this.surveyService.setRandomQuestionKey(1);
        this.surveyService.createQuestion(this.questionBean);
        assertNotNull(questionBean);
    }

    @Test(expected = Exception.class)
    public void testCreateQuestionException2() throws EnMeException {
        this.surveyService.setRandomQuestionKey(Integer.valueOf("tres"));
        this.surveyService.createQuestion(this.questionBean);
        assertNotNull(questionBean);
    }

    /**
    * Test Save Answers.
     * @throws EnMeException
    **/
    @Test
    @Ignore
    public void testSaveAnswers() throws EnMeException{
        final QuestionAnswerBean answersBean = createAnswersBean("ASJKE", "Yes", this.question.getQid());
        surveyService.createQuestionAnswer(answersBean, this.question);
        assertNotNull(answersBean.getAnswerId());
    }

    /**
     * Test Retrieve Answer By Question Id.
     **/
    @Test
    public void testRetrieveAnswerByQuestionId(){
           final List<QuestionAnswerBean> listUnitAnswerBean = surveyService.retrieveAnswerByQuestionId(this.question.getQid());
            assertEquals("Should be equals",1, listUnitAnswerBean.size());
    }

    /**
     * Test Update Answer By Answer Id.
     * @throws EnMeException
     */
    @Test
    public void testUpdateAnswersByAnswerId() throws EnMeException{
        final String expectedResponse = "Quizas";
        final QuestionAnswer questionAnswers = createQuestionAnswer("No", this.question, "HASH");
        surveyService.updateAnswerByAnswerId(questionAnswers.getQuestionAnswerId(), expectedResponse);
        assertEquals(questionAnswers.getAnswer(), expectedResponse);
    }

    /**
     * Test Suggestion Question List.
     * @throws EnMeNoResultsFoundException
     */
    @Test
    public void testSuggestionQuestionList() throws EnMeNoResultsFoundException{
         List<QuestionBean> unitQuestionBean;
        final String keyword = "sky";
        createQuestion("sky sky sky", "");
        createQuestion("sky sky sky 2", "");
        createQuestion("the sky should be blue or not?", "");
        flushIndexes();
        unitQuestionBean = surveyService.listSuggestQuestion(keyword, this.userSecondary.getUsername());
        //FIXME: in progress QuestionDao MUST retrieve 1 here
        assertEquals("should be equals", 0, unitQuestionBean.size());
    }

    /**
     * Test search surveys by today date.
     * @throws EnMeException
     */
    @Test
    public void testSearchSurveysToday() throws EnMeException {
        final SurveyBean surveyBean = createSurveyBean("My first survey",
                getSpringSecurityLoggedUserAccount().getUsername(),
                this.mySurveyDate.getTime());
        surveyService.createSurvey(surveyBean, this.request);
        final List<SurveyBean> surveyBeanList = surveyService.filterSurveyItemsByType(
                TypeSearch.LASTDAY, null, this.MAX_RESULTS,
                this.START_RESULTS);
        assertEquals("should be equals", 1, surveyBeanList.size());
    }

    /**
     * Test search surveys from last week.
     * @throws EnMeException
     */
    @Test
    public void testSearchSurveysLastWeek() throws EnMeException {
        mySurveyDate.add(Calendar.DATE, -3);
        final SurveyBean surveyBean = createSurveyBean("My first survey",
                getSpringSecurityLoggedUserAccount().getUsername(),
                this.mySurveyDate.getTime());
        surveyService.createSurvey(surveyBean, this.request);
        final List<SurveyBean> surveyBeanList = surveyService
                .searchSurveysLastWeek(getUsernameLogged(), this.MAX_RESULTS,
                        this.START_RESULTS);
       assertEquals("should be equals", 1, surveyBeanList.size());
    }

    /**
     * Test search all surveys by owner.
     * @throws EnMeException
     */
    @Test
    public void testSearchAllSurveys() throws EnMeException {
        final Calendar lastMonth = Calendar.getInstance();
        lastMonth.add(Calendar.MONTH, -1);

        final Calendar lastWeek = Calendar.getInstance();
        lastWeek.add(Calendar.DATE, -8);

        // First Survey Bean created today .
        final SurveyBean surveyBean = createSurveyBean("My first survey",
                getSpringSecurityLoggedUserAccount().getUsername(),
                this.mySurveyDate.getTime());

        // Second Survey Bean created last month.
        final SurveyBean surveyBean2 = createSurveyBean("My Second survey",
                getSpringSecurityLoggedUserAccount().getUsername(),
                lastMonth.getTime());

        // Third Survey Bean created 3 days ago.
        final SurveyBean surveyBean3 = createSurveyBean("My Third survey",
                getSpringSecurityLoggedUserAccount().getUsername(),
                lastWeek.getTime());

        surveyService.createSurvey(surveyBean, this.request);
        surveyService.createSurvey(surveyBean2, this.request);
        surveyService.createSurvey(surveyBean3, this.request);

        final String mykeyword = "";
        final List<SurveyBean> surveyBeanList = surveyService
                .filterSurveyItemsByType(TypeSearch.ALL, mykeyword,
                        this.MAX_RESULTS, this.START_RESULTS);
        assertEquals("should be equals", 3, surveyBeanList.size());
    }

    /**
     * Test search favorite surveys.
     * @throws EnMeException
     */
    @Test
    public void testSearchFavoriteSurveys() throws EnMeException {
        final SurveyBean surveyBean = createSurveyBean("My first survey",
                getSpringSecurityLoggedUserAccount().getUsername(),
                this.mySurveyDate.getTime());
        surveyBean.setFavorites(Boolean.TRUE);
        surveyService.createSurvey(surveyBean, this.request);
        final List<SurveyBean> surveyBeanList = surveyService
                .searchSurveysFavourites(getUsernameLogged(), this.MAX_RESULTS,
                        this.START_RESULTS);
      assertEquals("should be equals", 1, surveyBeanList.size());
    }

    /**
     * Test search surveys by Name
     * @throws EnMeException
     */
    @Test
    public void testSearchbySurveyName() throws EnMeException {
        final String keyWord = "first";
        final SurveyBean surveyBean = createSurveyBean("My first survey",
                getSpringSecurityLoggedUserAccount().getUsername(),
                this.mySurveyDate.getTime());

        final SurveyBean surveyBean2 = createSurveyBean("My Second survey",
                getSpringSecurityLoggedUserAccount().getUsername(),
                this.mySurveyDate.getTime());
        surveyService.createSurvey(surveyBean, this.request);
        surveyService.createSurvey(surveyBean2, this.request);
        final List<SurveyBean> surveyBeanList = surveyService
                .searchSurveysbyKeywordName(keyWord,
                        getSpringSecurityLoggedUserAccount().getUsername(),
                        this.MAX_RESULTS, this.START_RESULTS);
        assertEquals("should be equals", 1, surveyBeanList.size());
    }

    /**
     * Test create survey folder.
     * @throws EnMeNoResultsFoundException
     */
    @Test
    public void testCreateSurveyFolder() throws EnMeNoResultsFoundException {
        final FolderBean fbean = surveyService.createSurveyFolder(
                "My First Folder", getSpringSecurityLoggedUserAccount()
                        .getUsername());
        Assert.assertNotNull(fbean);
    }

    /**
     * Test get surveys by folder.
     * @throws EnMeException
     */
    @Test
    public void testGetSurveysByFolder() throws EnMeException{
        final FolderBean fbean = surveyService.createSurveyFolder(
                "My First Folder", this.myUsername);
        Assert.assertNotNull(fbean);
        final SurveyBean mySurveyBean = createSurveyBean("My Second survey",
                this.myUsername, this.mySurveyDate.getTime());
        final Survey mySurvey = surveyService.createSurvey(mySurveyBean, this.request);
        surveyService.addSurveyToFolder(fbean.getId(), this.myUsername,
                mySurvey.getSid());
        final List<Survey> listSurvey = surveyService.retrieveSurveyByFolder(
                getSpringSecurityLoggedUserAccount().getAccount().getUid(),
                fbean.getId());
        assertEquals("should be equals", 1, listSurvey.size());
    }

    
    @Test
    public void testCreateSurvey() throws EnMeException{
    	// Create survey with section default
    	final SurveyBean surveyBean = createSurveyBean("default survey", getSpringSecurityLoggedUserAccount().toString(), new Date());
    	final Survey newSurvey = surveyService.createSurvey(surveyBean, this.request);   
    	
    	// Create new Section
    	final UnitSurveySection unitSection = createSurveySection("default 2", "default 2", newSurvey);
    	surveyService.createSurveySection(unitSection, newSurvey);
    	 
    	
    	// Add another section to survey
    	final List<UnitSurveySection> ssection = surveyService.retrieveSectionsBySurvey(newSurvey); 
    	assertEquals("should be equals", 2, ssection.size());
      
    }
    
    /**
     * Test get {@link Survey} by id.
     * @throws EnMeNoResultsFoundException
     */
    @Test
    public void testGetSurveybyId() throws EnMeNoResultsFoundException{
    	final Survey mySurvey = createDefaultSurvey(this.user); 
    	final Survey surveyById = surveyService.getSurveyById(mySurvey.getSid());
    	assertEquals("should be equals", mySurvey.getSid(), surveyById.getSid()); 
    }
    
    /**
     * Test Add {@link Question} to {@link SurveySection}. 
     * @throws EnMeException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
	@Test
	public void testAddQuestionToSurveySection() throws EnMeException,
			NoSuchAlgorithmException, UnsupportedEncodingException {
		final String questionName = "What is your favorite superhero?";
		final Survey defaultSurvey = createDefaultSurvey(this.user);
		final SurveySection defaultSection = createDefaultSection("Comics",
				defaultSurvey);

		final Question questionAdded = surveyService
				.addQuestionToSurveySection(questionName, userSecondary,
						defaultSection, QuestionPattern.MULTIPLE_SELECTION,
						null);

		assertEquals("should be equals", defaultSection.getSsid(),
				questionAdded.getSection().getSsid());
	}
    /**
     * @param surveyService the surveyService to set
     */
    public void setSurveyService(final ISurveyService surveyService) {
        this.surveyService = surveyService;
    }
    /**
     * @return the question
     */
    public Question getQuestion() {
        return question;
    }
}
