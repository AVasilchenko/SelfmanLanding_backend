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


		