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

package org.springframework.samples.studentmanager.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.studentmanager.model.Student;
import org.springframework.samples.studentmanager.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test of the Service and the Repository layer.
 * <p>
 * ClinicServiceSpringDataJpaTests subclasses benefit from the following services provided
 * by the Spring TestContext Framework:
 * </p>
 * <ul>
 * <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li>
 * <li><strong>Dependency Injection</strong> of test fixture instances, meaning that we
 * don't need to perform application context lookups. See the use of
 * {@link Autowired @Autowired} on the <code>{@link
 * ClinicServiceTests#clinicService clinicService}</code> instance variable, which uses
 * autowiring <em>by type</em>.
 * <li><strong>Transaction management</strong>, meaning each test method is executed in
 * its own transaction, which is automatically rolled back by default. Thus, even if tests
 * insert or otherwise change database state, there is no need for a teardown or cleanup
 * script.
 * <li>An {@link org.springframework.context.ApplicationContext ApplicationContext} is
 * also inherited and can be used for explicit bean lookup if necessary.</li>
 * </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Dave Syer
 */
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class ClinicServiceTests {

	@Autowired
	protected StudentRepository owners;

	@Test
	void shouldFindOwnersByLastName() {
		Collection<Student> students = this.owners.findByLastName("Davis");
		assertThat(students).hasSize(2);

		students = this.owners.findByLastName("Daviss");
		assertThat(students).isEmpty();
	}

	@Test
	@Transactional
	void shouldInsertOwner() {
		Collection<Student> students = this.owners.findByLastName("Schultz");
		int found = students.size();

		Student student = new Student();
		student.setFirstName("Sam");
		student.setLastName("Schultz");
		student.setHometown("4, Evans Street");
		student.setDepartment("Wollongong");
		student.setStudentId("4444444444");
		this.owners.save(student);
		assertThat(student.getId().longValue()).isNotEqualTo(0);

		students = this.owners.findByLastName("Schultz");
		assertThat(students.size()).isEqualTo(found + 1);
	}

	@Test
	@Transactional
	void shouldUpdateOwner() {
		Student student = this.owners.findById(1);
		String oldLastName = student.getLastName();
		String newLastName = oldLastName + "X";

		student.setLastName(newLastName);
		this.owners.save(student);

		// retrieving new name from database
		student = this.owners.findById(1);
		assertThat(student.getLastName()).isEqualTo(newLastName);
	}

}