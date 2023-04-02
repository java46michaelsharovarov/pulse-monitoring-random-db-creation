package telran.monitoring;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import telran.monitoring.model.*;
import telran.monitoring.service.VisitsService;

@SpringBootApplication
@Slf4j
public class PulseMonitoringRandomDbCreationApplication {

	@Value("${app.visits.amount : 50}")
	private int numberOfVisits;	
	
	@Autowired
	VisitsService service;
	
	private String patients[] = { "Abraham", "Sarah", "Itshak", "Rahel", "Asaf", "Yacob", "Rivka", "Yosef", "Benyanim",	"Dan" };
	private String doctors[] = { "Ruben", "Moshe", "Aron" }; 
	private String doctorsEmails[] = { "doctor1@gmail.com", "doctor2@gmail.com", "doctor3@gmail.com" };
	
	private HashMap<Long, String> patientDate = new HashMap<>();
	
	public static void main(String[] args) {
		ConfigurableApplicationContext ac = SpringApplication.run(PulseMonitoringRandomDbCreationApplication.class, args);
		ac.close();
	}
	
	@PostConstruct
	private void dbCreation() {
		patientsCreation();
		doctorsCreation();
		visitsCreation();
		log.info("database was created successfully");
	}
	
	private void patientsCreation() {
		IntStream.range(0, patients.length)
		.forEach(i -> service.addPatient(new PatientDto(i + 1, patients[i])));	
		log.info("all patients are added");
	}
	
	private void doctorsCreation() {
		IntStream.range(0, doctors.length)
		.forEach(i -> service.addDoctor(new DoctorDto(doctorsEmails[i], doctors[i])));
		log.info("all doctors are added");
	}
	
	private void visitsCreation() {
		IntStream.rangeClosed(1, numberOfVisits)
		.forEach(i -> addVisit());	
		log.info("all visits are added");
	}
	
	private void addVisit() {
		long patientId = getRandomNumber(1, patients.length);
		String date = patientDate.merge(patientId, LocalDate.now().toString(), (key, value) -> updateDate(key));
		String doctorEmail = doctorsEmails[getRandomNumber(0, doctors.length - 1)];
		service.addVisit(new VisitDto(patientId, doctorEmail, date));
	}

	private String updateDate(String date) {
		return LocalDate.parse(date).minusDays(2).toString();
	}

	private int getRandomNumber(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

}
