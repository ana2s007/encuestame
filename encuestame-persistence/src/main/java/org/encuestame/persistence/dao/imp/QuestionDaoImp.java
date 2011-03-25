/*
 ************************************************************************************
 * Copyright (C) 2001-2009 encuestame: system online surveys Copyright (C) 2009
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.persistence.dao.imp;

import java.util.List;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.util.Version;
import org.encuestame.persistence.dao.IQuestionDao;
import org.encuestame.persistence.domain.question.Question;
import org.encuestame.persistence.domain.question.QuestionAnswer;
import org.encuestame.persistence.domain.question.QuestionPattern;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

/**
 * Question Dao.
 * @author Picado, Juan Carlos juanATencuestame.org
 * @since June 02, 2009
 */
@Repository("questionDaoImp")
public class QuestionDaoImp extends AbstractHibernateDaoSupport implements IQuestionDao {

    /**
     * Constructor.
     * @param sessionFactory {@link SessionFactory}.
     */
    @Autowired
    public QuestionDaoImp(final SessionFactory sessionFactory) {
             setSessionFactory(sessionFactory);
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#createQuestion(org.encuestame.persistence.domain.question.Question)
     */
    //@CacheFlush(modelId="createQuestion")
    public final void createQuestion(final Question question){
        saveOrUpdate(question);
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#retrieveQuestionsByName(java.lang.String, java.lang.Long)
     */
    //@Cacheable(modelId="retrieveQuestionsByName")
    @SuppressWarnings("unchecked")
    public final List<Question> retrieveQuestionsByName(final String keyword, final Long userId){
        final DetachedCriteria criteria = DetachedCriteria.forClass(Question.class);
        criteria.add(Restrictions.like("question", keyword, MatchMode.ANYWHERE));
        return getHibernateTemplate().findByCriteria(criteria);
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#retrieveIndexQuestionsByKeyword(java.lang.String, java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public final List<Question> retrieveIndexQuestionsByKeyword(final String keyword, final Long userId){
        log.info("keyword "+keyword);
        log.info("userId "+userId);
        @SuppressWarnings("rawtypes")
        List<Question> searchResult = (List<Question>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(org.hibernate.Session session) {
                        try {
                            final FullTextSession fullTextSession = Search.getFullTextSession(session);
                            final MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_30,
                                    new String[]{"question"},
                                    new SimpleAnalyzer());
                            final org.apache.lucene.search.Query query = parser.parse(keyword);
                            final FullTextQuery hibernateQuery = fullTextSession.createFullTextQuery(query, Question.class);
                            final Criteria criteria = session.createCriteria(Question.class);
                            criteria.createAlias("accountQuestion", "accountQuestion");
                            criteria.add(Restrictions.eq("accountQuestion.uid", userId));
                            hibernateQuery.setCriteriaQuery(criteria);
                            final List<Question> result = hibernateQuery.list();
                            log.info("result LUCENE "+result.size());
                            return result;
                        } catch (ParseException ex) {
                            log.error("Index Search Erro "+ex.getMessage());
                            return null;
                        }
                    }
                });
        return searchResult;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#retrieveQuestionById(java.lang.Long)
     */
    public final Question retrieveQuestionById(final Long questionId){
        return (Question) getHibernateTemplate().get(Question.class, questionId);
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#retrieveAnswerById(java.lang.Long)
     */
    public final QuestionAnswer retrieveAnswerById(final Long answerId){
       return (QuestionAnswer) getHibernateTemplate().get(QuestionAnswer.class, answerId);
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#getAnswersByQuestionId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public final List<QuestionAnswer> getAnswersByQuestionId(final Long questionId) throws HibernateException {
        return getHibernateTemplate().findByNamedParam("from QuestionAnswer where questions.id =:questionId ",
                                                       "questionId", questionId);
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#loadAllQuestions()
     */
    @SuppressWarnings("unchecked")
    public final List<Question> loadAllQuestions() throws HibernateException {
        return getHibernateTemplate().find("from Question");
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#loadAllQuestionPattern()
     */
    @SuppressWarnings("unchecked")
    public final List<QuestionPattern> loadAllQuestionPattern()
            throws HibernateException {
        return getHibernateTemplate().find("from QuestionPattern");

    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.persistence.dao.IQuestionDao#loadPatternInfo(java.lang.Long)
     */
    public final QuestionPattern loadPatternInfo(final Long patternId) throws HibernateException{
        return (QuestionPattern) getHibernateTemplate().get(QuestionPattern.class, patternId);
    }
}
