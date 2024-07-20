package com.selfman.landingservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.selfman.landingservice.bot.BotStatus;
import com.selfman.landingservice.bot.BotStatusRepository;
import com.selfman.landingservice.bot.SelfmanBot;
import com.selfman.landingservice.bot.UserSubscription;
import com.selfman.landingservice.dto.AddCompanyDataDto;
import com.selfman.landingservice.dto.AddContactDto;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LandingServiceImpl implements LandingService, CommandLineRunner {
	private static final String APPLICATION_NAME = "Selfman_landing";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	final SelfmanBot selfmanBot;
	final UserSubscription userSubscription;
	final BotStatusRepository botStatusRepository;
	@Value("${bot.statusId}")
    private String botStatusId;
//	private Credential credential;

	@Override
	@EventListener
	public void run(String... args) {
//		NetHttpTransport HTTP_TRANSPORT;
//		try {
//			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//			credential = getCredentials(HTTP_TRANSPORT);
//		} catch (GeneralSecurityException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		 BotStatus botStatus = botStatusRepository.findById(botStatusId).orElse(new BotStatus(botStatusId, false));
		 if (!botStatus.isRunning()) {
	        	try {
					TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
					botsApi.registerBot(selfmanBot);
					botStatus.setRunning(true);
					botStatusRepository.save(botStatus);
					

				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
		}

	}

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

		InputStream in = LandingServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
//		LocalServerReceiver receiver = new LocalServerReceiver.Builder()
//				.setHost("landing-selfman-new.fly.dev")
//		        .setPort(8888)
//		        .build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	private Sheets getSheetsService() throws IOException, GeneralSecurityException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
	}

	public String getRange(Sheets service, String sheetName, String spreadsheetId, String format) {
		try {
			ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName + "!A:A").execute();
			List<List<Object>> values = response.getValues();
			int numRows = (values == null) ? 0 : values.size();
			return String.format(format, sheetName, numRows + 1, numRows + 1);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public void addContact(AddContactDto addContactDto, String spreadsheetId) {
		String sheetName = "Contacts";
		try {
			Sheets service = getSheetsService();

			List<List<Object>> data = new ArrayList<>();
			List<Object> row = new ArrayList<>();
			row.add(addContactDto.getName());
			row.add(addContactDto.getEmail());
			data.add(row);
			ValueRange body = new ValueRange().setValues(data);
			String range = getRange(service, sheetName, spreadsheetId, "%s!A%d:B%d");
			service.spreadsheets().values().update(spreadsheetId, range, body).setValueInputOption("RAW").execute();
			selfmanBot.broadcastMessage("Add contact " + addContactDto.getName() + " " + addContactDto.getEmail());

		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addCompanyData(AddCompanyDataDto addCompanyDataDto, String spreadsheetId) {
		String sheetName = "CompanyData";
		try {
			Sheets service = getSheetsService();
			String range = getRange(service, sheetName, spreadsheetId, "%s!A%d:H%d");
			List<List<Object>> data = new ArrayList<>();
			List<Object> row = new ArrayList<>();
			row.add(addCompanyDataDto.getFirstName());
			row.add(addCompanyDataDto.getLastName());
			row.add(addCompanyDataDto.getCompany());
			row.add(addCompanyDataDto.getCountry());
			row.add(addCompanyDataDto.getPhone());
			row.add(addCompanyDataDto.getEmail());
			row.add(addCompanyDataDto.getUserType());
			row.add(addCompanyDataDto.getMessage());
			data.add(row);
			ValueRange body = new ValueRange().setValues(data);
			service.spreadsheets().values().update(spreadsheetId, range, body).setValueInputOption("RAW").execute();
			selfmanBot.broadcastMessage("Add data " + addCompanyDataDto.getFirstName() + " "
					+ addCompanyDataDto.getLastName() + ", company name " + addCompanyDataDto.getCompany());
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}


}
