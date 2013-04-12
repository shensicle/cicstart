/* ============================================================
 * PersistentLocalDateTime.java
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
package ca.ualberta.physics.cssdp.dao.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.joda.time.LocalDateTime;

public class PersistentLocalDateTime implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { java.sql.Types.TIMESTAMP };
	}

	@Override
	public Class<LocalDateTime> returnedClass() {
		return LocalDateTime.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {

		if (x == y) {
			return true;
		}

		if (x == null || y == null) {
			return false;
		}

		LocalDateTime ldt1 = (LocalDateTime) x;
		LocalDateTime ldt2 = (LocalDateTime) y;

		return ldt1.equals(ldt2);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		LocalDateTime ldt = (LocalDateTime) x;
		return ldt.hashCode();
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		Timestamp timestamp = rs.getTimestamp(names[0]);
		if (timestamp != null) {
			LocalDateTime localDateTime = new LocalDateTime(timestamp);
			return localDateTime;
		} else {
			return null;
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		if (value != null) {
			LocalDateTime localDateTime = (LocalDateTime) value;
			Timestamp timestamp = new Timestamp(localDateTime.toDate()
					.getTime());
			st.setTimestamp(index, timestamp);
		} else {
			st.setObject(index, null);
		}
	}

}
