/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.studentmanager.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.studentmanager.model.Gender;
import org.springframework.samples.studentmanager.model.Student;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for <code>Student</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public interface StudentRepository extends Repository<Student, Integer> {

	/**
	 * Retrieve all {@link Gender}s from the data store.
	 * @return a Collection of {@link Gender}s.
	 */
	@Query("SELECT gender FROM Gender gender ORDER BY gender.name")
	@Transactional(readOnly = true)
	List<Gender> findGenders();

	/**
	 * Retrieve {@link Student}s from the data store by last name, returning all students
	 * whose last name <i>starts</i> with the given name.
	 * @param lastName Value to search for
	 * @return a Collection of matching {@link Student}s (or an empty Collection if none
	 * found)
	 */
	@Query("SELECT DISTINCT student FROM Student student WHERE student.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Collection<Student> findByLastName(@Param("lastName") String lastName);

	/**
	 * Retrieve an {@link Student} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Student} if found
	 */
	@Query("SELECT student FROM Student student WHERE student.id =:id")
	@Transactional(readOnly = true)
	Student findById(@Param("id") Integer id);

	@Query("SELECT student FROM Student student WHERE 1 = 1")
	@Transactional(readOnly = true)
	Collection<Student> findAll();

	/**
	 * Save an {@link Student} to the data store, either inserting or updating it.
	 * @param student the {@link Student} to save
	 */
	void save(Student student);

	void deleteById(@Param("id") Integer id);

}
