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
package org.springframework.samples.studentmanager.controller;

import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.samples.studentmanager.model.Gender;
import org.springframework.samples.studentmanager.model.Student;
import org.springframework.samples.studentmanager.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
class StudentController {

	private static final String VIEWS_STUDENT_CREATE_OR_UPDATE_FORM = "students/createOrUpdateStudentForm";

	private final StudentRepository students;

	public StudentController(StudentRepository clinicService) {
		this.students = clinicService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("genders")
	public Collection<Gender> populateGenders() {
		return this.students.findGenders();
	}

	@GetMapping("/students/new")
	public String initCreationForm(Map<String, Object> model) {
		Student student = new Student();
		model.put("student", student);
		return VIEWS_STUDENT_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/students/new")
	public String processCreationForm(@Valid Student student, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_STUDENT_CREATE_OR_UPDATE_FORM;
		}
		else {
			this.students.save(student);
			return "redirect:/students/" + student.getId();
		}
	}

	@GetMapping("/students/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("student", new Student());
		return "students/findStudents";
	}

	@GetMapping("/students")
	public String processFindForm(Student student, BindingResult result, Map<String, Object> model) {

		// allow parameterless GET request for /owners to return all records
		if (student.getLastName() == null) {
			student.setLastName(""); // empty string signifies broadest possible
										// search
		}

		// find owners by last name
		Collection<Student> results = this.students.findByLastName(student.getLastName());
		if (results.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "students/findStudents";
		}
		else if (results.size() == 1) {
			// 1 student found
			student = results.iterator().next();
			return "redirect:/students/" + student.getId();
		}
		else {
			// multiple owners found
			model.put("selections", results);
			return "students/studentsList";
		}
	}

	@GetMapping("/students/list")
	public String listAllStudent(Map<String, Object> model) {
		Collection<Student> results = this.students.findAll();
		model.put("selections", results);
		return "students/allList";
	}

	@GetMapping("/students/{id}/edit")
	public String initUpdateStudentForm(@PathVariable("id") int id, Model model) {
		Student student = this.students.findById(id);
		model.addAttribute(student);
		return VIEWS_STUDENT_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/students/{id}/edit")
	public String processUpdateStudentForm(@Valid Student student, BindingResult result, @PathVariable("id") int id) {
		if (result.hasErrors()) {
			return VIEWS_STUDENT_CREATE_OR_UPDATE_FORM;
		}
		else {
			student.setId(id);
			this.students.save(student);
			return "redirect:/students/{id}";
		}
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param id the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/students/{id}")
	public ModelAndView showStudent(@PathVariable("id") int id) {
		ModelAndView mav = new ModelAndView("students/studentDetails");
		Student student = this.students.findById(id);
		mav.addObject(student);
		return mav;
	}

	@GetMapping("/students/{id}/delete")
	public String deleteStudent(@PathVariable("id") int id) {
		students.deleteById(id);
		return "welcome";
	}

}
