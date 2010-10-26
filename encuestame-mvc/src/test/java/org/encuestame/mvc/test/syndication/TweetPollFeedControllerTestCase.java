/*
 ************************************************************************************
 * Copyright (C) 2001-2010 encuestame: system online surveys Copyright (C) 2009
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.mvc.test.syndication;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.encuestame.mvc.test.config.AbstractJsonMvcUnitBeans;
import org.encuestame.mvc.view.TweetPollAtomFeedView;
import org.encuestame.mvc.view.TweetPollRssFeedView;
import org.encuestame.utils.web.UnitTweetPoll;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Test Cases for Feed Controller, RSS and ATOM.
 * @author Picado, Juan juanATencuestame.org
 * @since Oct 24, 2010 8:57:54 PM
 * @version $Id:$
 */
public class TweetPollFeedControllerTestCase extends AbstractJsonMvcUnitBeans{

    /** TweetPollAtomFeedView. **/
    private  TweetPollAtomFeedView tweetPollAtomFeedView;

    /** TweetPollRssFeedView. **/
    private TweetPollRssFeedView tweetPollRssFeedView;

    /**
     * Before.
     */
    @Before
    public void initView(){
        Assert.assertNotNull(this.tweetPollAtomFeedView);
        Assert.assertNotNull(this.tweetPollRssFeedView);
    }

    /**
     * Test for TweetPollRssFeedView.
     * @throws Exception exception
     */
    @Test
    public void testTweetPollRssFeedView() throws Exception{
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("tweetPolls", new ArrayList<UnitTweetPoll>());
        this.tweetPollRssFeedView.render(model, request, response);
        log.debug(response.getContentType());
        log.debug(response.getContentAsString());
        System.out.println(response.getContentAsString());
        Assert.assertEquals("application/rss+xml", response.getContentType());
        assertXpathExists("//rss", response.getContentAsString());
        assertXpathExists("//description", response.getContentAsString());
        assertXpathExists("//copyright", response.getContentAsString());
        assertXpathExists("//pubDate", response.getContentAsString());
    }

    /**
     * Test TweetPollAtomFeedView.
     * @throws Exception exception.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testTweetPollAtomFeedView() throws Exception{
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("tweetPolls", new ArrayList<UnitTweetPoll>());
        this.tweetPollAtomFeedView.render(model, request, response);
        log.debug(response.getContentType());
        log.debug(response.getContentAsString());
        System.out.println(response.getContentAsString());
        Assert.assertEquals("application/atom+xml", response.getContentType());
        //assertXpathExists("//feed", response.getContentAsString());
        final SAXBuilder builder = new SAXBuilder();
        org.jdom.Document document = builder.build(new StringReader(response.getContentAsString()));
        final Element root = document.getRootElement();
        final Namespace name = root.getNamespace();
        Assert.assertEquals(name.getURI(), "http://www.w3.org/2005/Atom");
        final List<Element> list = root.getChildren();
        final Element title = (Element) root.getChildren("title", name).get(0);
        Assert.assertEquals(title.getName(), "title");
        Assert.assertEquals(title.getValue(), "TweetPoll Published");
        final Element id = (Element) root.getChildren("id", name).get(0);
        Assert.assertEquals(id.getName(), "id");
        Assert.assertEquals(id.getValue(), "TweetPoll Published");
    }

    /**
     * @return the tweetPollAtomFeedView
     */
    public TweetPollAtomFeedView getTweetPollAtomFeedView() {
        return tweetPollAtomFeedView;
    }

    /**
     * @param tweetPollAtomFeedView the tweetPollAtomFeedView to set
     */
    @Autowired
    public void setTweetPollAtomFeedView(final TweetPollAtomFeedView tweetPollAtomFeedView) {
        this.tweetPollAtomFeedView = tweetPollAtomFeedView;
    }

    /**
     * @return the tweetPollRssFeedView
     */
    public TweetPollRssFeedView getTweetPollRssFeedView() {
        return tweetPollRssFeedView;
    }

    /**
     * @param tweetPollRssFeedView the tweetPollRssFeedView to set
     */
    @Autowired
    public void setTweetPollRssFeedView(final TweetPollRssFeedView tweetPollRssFeedView) {
        this.tweetPollRssFeedView = tweetPollRssFeedView;
    }
}