package hu.edudroid.droidlabportal.user;

import com.google.appengine.api.datastore.Key;

public class User {
	private String familyName;
	private String givenName;
	private String email;
	private Key key;
	private String role;
	
	public User(String familyName, String givenName, String email, Key key, String role) {
		this.familyName = familyName;
		this.givenName = givenName;
		this.email = email;
		this.key = key;
		this.role = role;
	}
	
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
}
