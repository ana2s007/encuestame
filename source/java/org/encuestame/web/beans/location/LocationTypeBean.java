/**
 * encuestame: system online surveys Copyright (C) 2005-2009 encuestame
 * Development Team
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of version 3 of the GNU General Public License as published by the
 * Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, writes to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.encuestame.web.beans.location;

import java.io.Serializable;

import org.encuestame.web.beans.MasterBean;

/**
 * Description.
 * @author Morales, Diana Paola paola@encuestame.org
 * @since  16/11/2009 21:25:07
 * File name: $HeadURL$
 * Revision: $Revision$
 * Last modified: $Date$
 * Last modified by: $Author$
 */
public class LocationTypeBean extends MasterBean implements Serializable{

    private static final long serialVersionUID = -9098305021342831224L;
    private String description;
    private Integer level;
    private Long locationTypeId;


    /**
     * @return
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return
     */
    public Integer getLevel() {
        return level;
    }
    /**
     * @param level
     */
    public void setLevel(Integer level) {
        this.level = level;
    }
    /**
     * @return the locationTypeId
     */
    public Long getLocationTypeId() {
        return locationTypeId;
    }
    /**
     * @param locationTypeId the locationTypeId to set
     */
    public void setLocationTypeId(Long locationTypeId) {
        this.locationTypeId = locationTypeId;
    }



}
