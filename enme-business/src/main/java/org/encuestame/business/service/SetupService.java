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
package org.encuestame.business.service;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.encuestame.business.setup.install.InstallDatabaseOperations;
import org.encuestame.business.setup.install.demo.CSVParser;
import org.encuestame.config.startup.EnMePlaceHolderConfigurer;
import org.encuestame.config.startup.XMLConfigurationFileSupport;
import org.encuestame.core.admin.AdministratorProfile;
import org.encuestame.core.filter.RequestSessionMap;
import org.encuestame.core.service.MailServiceOperations;
import org.encuestame.core.service.SecurityOperations;
import org.encuestame.core.service.SetupOperations;
import org.encuestame.core.util.EnMeUtils;
import org.encuestame.core.util.WidgetUtil;
import org.encuestame.util.exception.EnMeException;
import org.encuestame.utils.DateUtil;
import org.encuestame.utils.ShortUrlProvider;
import org.encuestame.utils.enums.TypeDatabase;
import org.encuestame.utils.social.SocialNetworkBean;
import org.encuestame.utils.web.UserAccountBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Define all setup operations.
 * @author Picado, Juan juanATencuestame.org
 * @since Sep 9, 2011
 */
@Service(value = "setupService")
@Transactional
public class SetupService extends AbstractBaseService implements SetupOperations {

    /** Log. **/
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * Install database operations.
     */
    @Autowired
    private InstallDatabaseOperations install;

    /**
     *
     */
    @Autowired
    private CSVParser csvParser;

    /**
     *
     */
    @Autowired
    private SecurityOperations securityOperations;

    /**
     *  {@link MailService}.
     */
    @Resource(name= "mailService")
    private MailServiceOperations mailService;

    /**
     *
     * @return
     */
    @Deprecated
    private String getTypeDatabase() {
        final String typeDatabase = EnMePlaceHolderConfigurer
                .getConfigurationManager().getProperty("database.type");
        return typeDatabase;
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.SetupOperations#validateInstall()
     */
    public void validateInstall() {
        final XMLConfigurationFileSupport config = EnMePlaceHolderConfigurer.getConfigurationManager();
        config.getXmlConfiguration().addProperty("install.date", DateUtil.getCurrentFormatedDate());
        config.getXmlConfiguration().addProperty("install.uuid", RandomStringUtils.randomAlphanumeric(50));
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.SetupOperations#finishInstall()
     */
    public void finishInstall(){
        EnMePlaceHolderConfigurer.setSystemInstalled(Boolean.TRUE);
    }

    /**
     * @throws IOException
     *
     */
    @Override
    public String installDatabase() {
        log.debug("installDatabase.....");
        try {
            this.install.initializeDatabase(TypeDatabase.getTypeDatabaseByString(this.getTypeDatabase()));
        } catch (Exception e) {
            log.fatal(e);
            RequestSessionMap.setErrorMessage(e.getMessage());
            //e.printStackTrace();
            return "fail";
        }
        return "ok"; //TODO: replace by enum in the future.
    }

    /**
     * Check the required
     * @return
     */
    public String preCheckSetup(){
        final String shortD = EnMePlaceHolderConfigurer.getProperty("short.default");
        final ShortUrlProvider provider = ShortUrlProvider.get(shortD);
        final String oURL = "http://www.google.es";
        final String shorterUrl = WidgetUtil.createShortUrl(provider, oURL);
        if (shorterUrl == oURL) {
            return "no";
        }
        try {
            if (EnMePlaceHolderConfigurer.getBooleanProperty("application.email.enabled")) {
                getMailService().sendStartUpNotification("testing email installation");
            }
        } catch (Exception ex) {
            RequestSessionMap.setErrorMessage(ex.getMessage());
            //ex.printStackTrace();
            log.error(ex);
            return "no";
        }
        return "yes";
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.SetupOperations#checkDatabase()
     */
    public Boolean checkDatabase() {
        log.info("******** check database **********");
        final boolean check = this.install.checkDatabase();
        log.info("******** "+check+" **********");
        log.info("******** check database **********");
        return check;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.encuestame.core.service.SetupOperations#upgradeDatabase()
     */
    @Override
    public void upgradeDatabase() {
        // TODO Auto-generated method stub

    }

    /**
     * Check status version.
     * @return the status.
     * @throws EnMeException
     */
    public String checkStatus() throws EnMeException {
        //TODO: replace by ENUMs
        String status = "install";
        final String currentVersion = EnMePlaceHolderConfigurer.getProperty("app.version");
        final String installedVersion = EnMePlaceHolderConfigurer.getConfigurationManager().getInstalledVersion();
        if (installedVersion != null) {
            if (currentVersion != null) {
                final int[] versionAsArrayCurrent = EnMeUtils.cleanVersion(currentVersion);
                final int[] versionAsArrayInstalled = EnMeUtils.cleanVersion(installedVersion);
                if (versionAsArrayCurrent[0] > versionAsArrayInstalled[0]) {
                    status = "upgrade";
                } else if (versionAsArrayCurrent[0] == versionAsArrayInstalled[0]) {
                    if (versionAsArrayCurrent[1] > versionAsArrayInstalled[1]) {
                        status = "upgrade";
                    } else if (versionAsArrayCurrent[1] == versionAsArrayInstalled[1]) {
                        if (versionAsArrayCurrent[2] > versionAsArrayInstalled[2]) {
                            status = "upgrade";
                        }
                    }
                }
            }
        }
        return status;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.SetupOperations#createAdmon(org.encuestame
     * .core.config.AdministratorProfile)
     */
    @Override
    public UserAccountBean createUserAdministration(
            AdministratorProfile administratorProfile) {
        log.debug("===============CREATE ADMON==============");
        final UserAccountBean account = this.securityOperations.createAdministrationUser(administratorProfile);
        return account;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.encuestame.core.service.SetupOperations#removeTables()
     */
    @Override
    public Boolean removeTables() {
         try {
             this.install.dropAll();
             return true;
         } catch (Exception e) {
             //e.printStackTrace();
             log.fatal(e);
             RequestSessionMap.setErrorMessage(e.getMessage());
             return false;
         }
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.SetupOperations#demoInstall()
     */
    @Override
    public void demoInstall() {
        try {
            this.csvParser.executeCSVDemoInstall(EnMePlaceHolderConfigurer
                    .getIntegerProperty("demo.votes.by.tppoll"),
                    EnMePlaceHolderConfigurer
                    .getIntegerProperty("demo.votes.by.poll"),
                    EnMePlaceHolderConfigurer
                    .getIntegerProperty("demo.votes.by.survey"));
        } catch (Exception e) {
            //e.printStackTrace();
            log.fatal(e);
            RequestSessionMap.setErrorMessage(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.SetupOperations#loadInstallParameters()
     */
    @Override
    public List<String> loadInstallParameters() {
        final List<String> parameters = new ArrayList<String>();
        parameters.add(this.createParameter("database", EnMePlaceHolderConfigurer.getProperty("datasource.database")));
        parameters.add(this.createParameter("jdbc-url", EnMePlaceHolderConfigurer.getProperty("datasource.urldb")));
        parameters.add(this.createParameter("jdbc-driver", EnMePlaceHolderConfigurer.getProperty("datasource.classname")));
        parameters.add(this.createParameter("username", EnMePlaceHolderConfigurer.getProperty("datasource.userbd")));
        parameters.add(this.createParameter("password", EnMePlaceHolderConfigurer.getProperty("datasource.pass")));
        parameters.add(this.createParameter("dialect", EnMePlaceHolderConfigurer.getProperty("datasource.dialect")));
        return parameters;
    }

    /**
     *
     * @return
     */
    private String createParameter(final String value, final String paramValue){
        final StringBuffer param = new StringBuffer();
        param.append(value);
        param.append(" : ");
        param.append(paramValue);
        return param.toString();
    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.SetupOperations#addNewSocialNetworkConfiguration()
     */
    @Override
    public void addNewSocialNetworkConfiguration() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.SetupOperations#removeSocialNetworkConfiguration()
     */
    @Override
    public void removeSocialNetworkConfiguration() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see org.encuestame.core.service.SetupOperations#listAllNetworkConfigurationSocial()
     */
    @Override
    public List<SocialNetworkBean> listAllNetworkConfigurationSocial() {

        return null;
    }

    @Override
    public void checkSocialNetworks() {
        // TODO Auto-generated method stub
    }
}
