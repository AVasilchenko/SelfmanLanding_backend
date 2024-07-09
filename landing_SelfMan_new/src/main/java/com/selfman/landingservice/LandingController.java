package com.selfman.landingservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.selfman.landingservice.dto.AddCompanyDataDto;
import com.selfman.landingservice.dto.AddContactDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LandingController {
	@Value("${spreadsheetId}")
	private String spreadsheetId;
	
	final LandingService landingService;

	@PostMapping("/addContact")
	public void addContact(@RequestBody AddContactDto addContactDto) {
		landingService.addContact(addContactDto, spreadsheetId);
	}

	@PostMapping("/addCompany")
	public void addCompanyData(@RequestBody AddCompanyDataDto addCompanyDataDto) {
		landingService.addCompanyData(addCompanyDataDto, spreadsheetId);
	}
	
	

}
