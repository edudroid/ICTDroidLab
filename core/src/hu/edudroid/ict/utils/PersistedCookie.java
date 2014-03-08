package hu.edudroid.ict.utils;

import java.util.Date;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

public class PersistedCookie implements Cookie {
	
	private static final String NAME = "name";
	private static final String DOMAIN = "domain";
	private static final String PATH = "path";
	private static final String EXPIRATION = "expiration";
	private static final String VALUE = "value";
	private static final String IS_SECURE = "is_secure";

	
	private final String name;
	private final String domain;
	private final String path;
	private final long expiration;
	private final String value;
	private final boolean isSecure;
	
	private PersistedCookie(String name, String domain, String path, long expiration, String value, boolean isSecure) {
		this.name = name;
		this.domain = domain;
		this.path = path;
		this.expiration = expiration;
		this.value = value;
		this.isSecure = isSecure;
	}
	
	public PersistedCookie(Cookie cookie) {
		this.name = cookie.getName();
		this.domain = cookie.getDomain();
		this.path = cookie.getPath();
		this.expiration = cookie.getExpiryDate().getTime();
		this.value = cookie.getValue();
		this.isSecure = cookie.isSecure();
	}
	

	public String toJSON() throws JSONException {
		JSONObject cookie = new JSONObject();
		cookie.put(NAME, getName());
		cookie.put(DOMAIN, getDomain());
		cookie.put(PATH, getPath());
		cookie.put(EXPIRATION, getExpiryDate().getTime());
		cookie.put(VALUE, getValue());
		cookie.put(IS_SECURE, isSecure());
		return cookie.toString();
	}
	
	public static Cookie parseFromJSON(String json) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);		
		return new PersistedCookie(
				jsonObject.getString(NAME),
				jsonObject.getString(DOMAIN),
				jsonObject.getString(PATH),
				jsonObject.getLong(EXPIRATION),
				jsonObject.getString(VALUE),
				jsonObject.getBoolean(IS_SECURE));
	}

	@Override
	public String getComment() {
		return "";
	}

	@Override
	public String getCommentURL() {
		return "";
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public Date getExpiryDate() {
		return new Date(expiration);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public int[] getPorts() {
		return null;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public boolean isExpired(Date date) {
		return date.after(new Date(this.expiration));
	}

	@Override
	public boolean isPersistent() {
		return true;
	}

	@Override
	public boolean isSecure() {
		return isSecure;
	}
	
	@Override
	public int hashCode() {
		int hashCode =  31 * 31 * getDomain().hashCode() + 31 * getPath().hashCode() + getName().hashCode();
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		Cookie otherCookie = (Cookie)o;
		if (!this.getName().equals(otherCookie.getName())) {
			return false;
		}
		if (!this.getDomain().equals(otherCookie.getDomain())) {
			return false;
		}
		if (!this.getPath().equals(otherCookie.getPath())) {
			return false;
		}
		if (!this.getValue().equals(otherCookie.getValue())) {
			return false;
		}
		if (!this.getExpiryDate().equals(otherCookie.getExpiryDate())) {
			return false;
		}
		if (this.isSecure()!= otherCookie.isSecure()) {
			return false;
		}
		return true;
	}
}