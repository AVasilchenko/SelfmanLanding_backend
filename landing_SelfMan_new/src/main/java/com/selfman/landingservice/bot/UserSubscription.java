package com.selfman.landingservice.bot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class UserSubscription {
	 private static Set<Long> chatIds = new HashSet<>();
	 private static final String FILE_NAME = "chat_ids.txt";

	    public static void subscribeUser(Long userId) {
	    	chatIds = getSubscribedUsers();
	        chatIds.add(userId);
	        saveChatIdsToFile();
	    }

	    public static void unsubscribeUser(Long userId) {
	    	chatIds = getSubscribedUsers();
	        chatIds.remove(userId);
	        saveChatIdsToFile();
	    }

	    public static Set<Long> getSubscribedUsers() {
	    	loadChatIdsFromFile();
	    	return chatIds;
	        
	    }
	    

	    private static void saveChatIdsToFile() {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
	            for (Long chatId : chatIds) {
	                writer.write(chatId.toString());
	                writer.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public static void loadChatIdsFromFile() {
	        try {
	            Files.lines(Paths.get(FILE_NAME)).forEach(line -> {
	                try {
	                    chatIds.add(Long.parseLong(line));
	                } catch (NumberFormatException e) {
	                    e.printStackTrace();
	                }
	            });
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
