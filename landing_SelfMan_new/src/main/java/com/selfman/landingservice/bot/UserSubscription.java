package com.selfman.landingservice.bot;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class UserSubscription {

	
	@Id
    private String id;
    private Long userId;
    
	public UserSubscription(Long userId) {
		this.userId = userId;
	}
	
	
    
}


		


//	    public static void subscribeUser(Long userId) {
//	    	chatIds = getSubscribedUsers();
//	        chatIds.add(userId);
//	        saveChatIdsToFile();
//	    
//	    }
//
//	    public static void unsubscribeUser(Long userId) {
//	    	chatIds = getSubscribedUsers();
//	        chatIds.remove(userId);
//	        saveChatIdsToFile();
//	    }
//
//	    public static Set<Long> getSubscribedUsers() {
//	    	loadChatIdsFromFile();
//	    	return chatIds;
//	        
//	    }
//	    
//
//	    private static void saveChatIdsToFile() {
//	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
//	            for (Long chatId : chatIds) {
//	                writer.write(chatId.toString());
//	                writer.newLine();
//	            }
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    public static void loadChatIdsFromFile() {
//	        try {
//	            Files.lines(Paths.get(FILE_NAME)).forEach(line -> {
//	                try {
//	                    chatIds.add(Long.parseLong(line));
//	                } catch (NumberFormatException e) {
//	                    e.printStackTrace();
//	                }
//	            });
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }

