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
package org.encuestame.mvc.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.encuestame.utils.web.UnitTweetPoll;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

/**
 * TweetPoll Published Atom Feed View.
 * @author Picado, Juan juanATencuestame.org
 * @since Jul 3, 2010 2:31:28 AM
 * @version $Id:$
 */
public final class TweetPollAtomFeedView extends AbstractEnMeAtomFeedView {


    /**
     * Build Feed Meta Data.
     */
    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed,
            HttpServletRequest request) {
        setAtomTitle(model.get("feedTitle") == null ? "TweetPoll Published " : model.get("feedTitle").toString());
        feed.setId(getAtomTitle());
        feed.setTitle(getAtomTitle());
    }

    /**
     * Build Feed Entries.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<UnitTweetPoll> contentList = (List<UnitTweetPoll>) model.get("tweetPolls");
        List<Entry> entries = new ArrayList<Entry>(contentList.size());
        for (UnitTweetPoll content : contentList) {
            final Entry entry = new Entry();
            String date = String.format("%1$tY-%1$tm-%1$td", new Date());
            entry.setId(content.getQuestionBean().getQuestionName());
            entry.setTitle(String.format("On %s, %s publish", date, content.getQuestionBean().getQuestionName()));
            entry.setUpdated(new Date());
            final List<Link> links = new ArrayList<Link>();
            //TODO: need work in this details.
            links.add(FeedUtils.createLink("http://www.encuestame.org","title"));
            entry.setAlternateLinks(links);
            entries.add(entry);
        }
        return entries;
    }
}