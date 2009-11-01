/**
 * encuestame:  system online surveys
 * Copyright (C) 2009  encuestame Development Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.encuestame.core.persistence.dao.imp;

import java.util.Collection;

import org.encuestame.core.persistence.pojo.SecGroups;
import org.hibernate.HibernateException;
/**
 * Id: ISecGroups.java Date: 11/05/2009 10:45:30
 * @author juanpicado
 * package: org.encuestame.core.persistence.dao.imp
 * @version 1.0
 */
public interface ISecGroups extends IBaseDao {
    public Collection<SecGroups> findAllGroups() throws HibernateException;
    public SecGroups getGroupById(Long groupId) throws HibernateException;
}

