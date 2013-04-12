/* ============================================================
 * ObservatoryDao.java
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

package ca.ualberta.physics.cssdp.catalogue.dao;

import java.util.List;

import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.dao.Dao;
import ca.ualberta.physics.cssdp.domain.catalogue.Observatory;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.model.Mnemonic;

public class ObservatoryDao extends AbstractJpaDao<Observatory> implements
		Dao<Observatory> {

	@SuppressWarnings("unchecked")
	public List<Observatory> find(Project project, List<Mnemonic> externalKeys) {

		StringBuffer qlString = new StringBuffer(
				"select o from Observatory o where o.project = :project");
		if (externalKeys != null && !externalKeys.isEmpty()) {
			qlString.append(" and o.externalKey in (:externalKeys)");
		}

		Query q = em.createQuery(qlString.toString());
		q.setParameter("project", project);
		if (externalKeys != null && !externalKeys.isEmpty()) {
			q.setParameter("externalKeys", externalKeys);
		}

		List<Observatory> result = q.getResultList();

		return result;
	}

}
