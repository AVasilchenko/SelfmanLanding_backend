package com.selfman.landingservice.bot;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class SelfmanBot extends TelegramLongPollingBot {


		@Value("${bot.token}")
	    private String botToken;

	    @Value("${bot.username}")
	    private String botUsername;
	    
	
	 @Override
	    public void onUpdateReceived(Update update) {
		 
	        if (update.hasMessage() && update.getMessage().hasText()) {
	            Long userId = update.getMessage().getChatId();
	            String messageText = update.getMessage().getText();

	            if (messageText.equals("/start")) {
	                UserSubscription.subscribeUser(userId);
	                sendTextMessage(userId, "You have subscribed to updates. To unsubscribe send '/stop'.");
	            } else if (messageText.equals("/stop")) {
	                UserSubscription.unsubscribeUser(userId);
	                sendTextMessage(userId, "You have unsubscribed from updates. To subscribe send '/start'.");
	            }
	        }
	    }

	    @Override
	    public String getBotUsername() {
	        return botUsername;
	    }

	    @Override
	    public String getBotToken() {
	        return botToken;
	    }

	    public void sendTextMessage(Long chatId, String text) {
	        SendMessage message = new SendMessage();
	        message.setChatId(chatId.toString());
	        message.setText(text);

	        try {
	            execute(message);
	        } catch (TelegramApiException e) {
	            e.printStackTrace();
	        }
	    }

	    public void broadcastMessage(String text) {
	        for (Long userId : UserSubscription.getSubscribedUsers()) {
	            sendTextMessage(userId, text);
	        }
	    }
	}
	

	