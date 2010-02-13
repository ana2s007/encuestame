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
package org.encuestame.core.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.encuestame.core.persistence.pojo.CatLocation;
import org.encuestame.core.persistence.pojo.CatLocationType;
import org.encuestame.core.persistence.pojo.Project;
import org.encuestame.core.persistence.pojo.QuestionPattern;
import org.encuestame.core.persistence.pojo.SecGroups;
import org.encuestame.core.persistence.pojo.SecPermission;
import org.encuestame.core.persistence.pojo.SecUserSecondary;
import org.encuestame.web.beans.admon.UnitGroupBean;
import org.encuestame.web.beans.admon.UnitPermission;
import org.encuestame.web.beans.admon.UnitUserBean;
import org.encuestame.web.beans.location.UnitLocationBean;
import org.encuestame.web.beans.location.UnitLocationTypeBean;
import org.encuestame.web.beans.project.UnitProjectBean;
import org.encuestame.web.beans.survey.UnitPatternBean;

/**
 * Convert Domain to  Beans.
 * @author Picado, Juan juan@encuestame.org
 * @since 03/12/2009 06:38:32
 * @version $Id$
 */
public class ConvertDomainBean {

    private static Log log = LogFactory.getLog(ConvertDomainBean.class);

    /**
     * Convert Domain user to Bean User.
     * @param domainUser Domain User
     * @return Bean User
     */
    public static UnitUserBean convertUserDaoToUserBean(SecUserSecondary domainUser) {
        final UnitUserBean user = new UnitUserBean();
        try {
            user.setName(domainUser.getCompleteName());
            user.setUsername(domainUser.getUsername());
            user.setEmail(domainUser.getUserEmail());
            user.setId(domainUser.getUid());
            user.setStatus(domainUser.isUserStatus());
            user.setPassword(domainUser.getPassword());
            user.setDateNew(domainUser.getEnjoyDate());
            user.setInviteCode(domainUser.getInviteCode());
            user.setPublisher(domainUser.getPublisher());
        } catch (Exception e) {
            log.error("error user bean converter -" + e.getMessage());
        }
        return user;
    }

    /**
     * Convert {@link SecGroups} to {@link UnitGroupBean}
     * @param groupDomain {@link SecGroups}
     * @return {@link UnitGroupBean}
     */
    public static UnitGroupBean convertGroupDomainToBean(final SecGroups groupDomain) {
        final UnitGroupBean groupBean = new UnitGroupBean();
        groupBean.setId(Integer.valueOf(groupDomain.getGroupId().toString()));
        groupBean.setGroupDescription(groupDomain.getGroupDescriptionInfo());
        groupBean.setStateId(String.valueOf(groupDomain.getIdState()));
        return groupBean;
    }

    /**
     * Convert {@link CatLocation} to {@link UnitLocationBean}
     * @param location {@link CatLocation}
     * @return {@link UnitLocationBean}
     */
    public static UnitLocationBean convertLocationToBean(final CatLocation location){
        final UnitLocationBean locationBean = new UnitLocationBean();
        locationBean.setTid(location.getLocateId());
        locationBean.setActive(location.getLocationActive());
        locationBean.setDescriptionLocation(location.getLocationDescription());
        locationBean.setLatitude(location.getLocationLatitude());
        locationBean.setLevel(location.getLocationLevel());
        locationBean.setLongitude(location.getLocationLongitude());
        locationBean.setLocationTypeId(location.getTidtype().getLocationTypeId());
        return locationBean;

    }

    /**
     * @param locationType {@link CatLocationType}
     * @return {@link UnitLocationTypeBean}
     */
    public static UnitLocationTypeBean convertLocationTypeToBean(final CatLocationType locationType){
        final UnitLocationTypeBean locationTypeBean = new UnitLocationTypeBean();
        locationTypeBean.setIdLocType(locationType.getLocationTypeId());
        locationTypeBean.setLocTypeDesc(locationType.getLocationTypeDescription());
        locationTypeBean.setLevel(locationType.getLocationTypeLevel());
        return locationTypeBean;

    }

    /**
     * Convert {@link Project} to {@link UnitProjectBean}
      * @param project {@link UnitProjectBean}
     * @return {@link UnitProjectBean}
     */
    public static UnitProjectBean convertProjectDomainToBean(final Project project) {
        final UnitProjectBean projectBean = new UnitProjectBean();
        projectBean.setName(project.getProjectDescription());
        projectBean.setDateFinish(project.getProjectDateFinish());
        projectBean.setDateInit(project.getProjectDateStart());
        projectBean.setId(project.getProyectId());
        projectBean.setDescription(project.getProjectInfo());
        projectBean.setState(project.getStateProject().getIdState());
        return projectBean;
    }


    /**
     * Convert {@link SecPermission} to {@link UnitPermission}
     * @param permission permission.
     * @return permBean
     */
    public static UnitPermission convertPermissionToBean(final SecPermission permission){
      final UnitPermission permBean = new UnitPermission();
      permBean.setId(permission.getIdPermission());
      permBean.setDescription(permission.getPermissionDescription());
      permBean.setPermission(permission.getPermission());
      return permBean;
    }


    /**
     * Convert {@link QuestionPattern} to {@link UnitPatternBean}.
     * @param pattern  {@link QuestionPattern}
     * @return {@link UnitPatternBean}
     */
    public static UnitPatternBean convertQuestionPatternToBean(final QuestionPattern pattern){
        final UnitPatternBean patterBean = new UnitPatternBean();
        patterBean.setId(pattern.getPatternId());
        patterBean.setPatronType(pattern.getPatternType());
        patterBean.setLabel(pattern.getLabelQid());
        return patterBean;
    }
}
