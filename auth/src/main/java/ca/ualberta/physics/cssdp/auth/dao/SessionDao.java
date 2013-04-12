/* ============================================================
 * SessionDao.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */

package ca.ualberta.physics.cssdp.auth.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.domain.auth.Session;

public class SessionDao extends AbstractJpaDao<Session> {

	public Session find(String token) {

		String qlString = "select s from Session s where s.token = :token";
		Query q = em.createQuery(qlString);
		q.setParameter("token", token);

		Session session = null;

		try {
			session = (Session) q.getSingleResult();
		} catch (NoResultException nre) {
			// ignored
		}

		return session;
	}

	
}
