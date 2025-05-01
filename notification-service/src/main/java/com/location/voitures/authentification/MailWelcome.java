package com.location.voitures.authentification;

public class MailWelcome {
    private String UserName;
    
    private String UserEmail;

	public String getUserName() {
		return UserName;
	}

	
	public MailWelcome() {
    }
	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserEmail() {
		return UserEmail;
	}

	public void setUserEmail(String userEmail) {
		UserEmail = userEmail;
	}

	public MailWelcome(String userName, String userEmail) {
		super();
		UserName = userName;
		UserEmail = userEmail;
	}
	
}