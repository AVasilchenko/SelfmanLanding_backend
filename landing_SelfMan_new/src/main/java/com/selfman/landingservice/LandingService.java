package com.selfman.landingservice;

import org.springframework.http.ResponseEntity;

import com.selfman.landingservice.dto.AddCompanyDataDto;
import com.selfman.landingservice.dto.AddContactDto;

public interface LandingService {
	void addContact(AddContactDto addContactDto, String spreadsheetId);
	void addCompanyData(AddCompanyDataDto addCompanyDataDto, String spreadsheetId);
}
	