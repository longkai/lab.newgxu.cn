package cn.newgxu.lab.apps.notty;

import cn.newgxu.lab.core.util.Resources;
import cn.newgxu.lab.core.util.ResourcesCallback;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author longkai
 * @version 0.1.0.13-8-2
 * @email im.longkai@gmail.com
 * @since 13-8-2
 */
// @Component
public class Notty {

	private static JsonObject json;

	static {
		try {
			InputStream inputStream = Resources.getInputStream("classpath:config/notty.json");
			json = Json.createReader(inputStream).readObject();
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot initialize NOTTY application configuration!", e);
		}
	}

//	public Notty(ObjectMapper objectMapper) {
//		Notty notty;
//		InputStream inputStream;
//		try {
////			System.err.println(objectMapper == null);
//			inputStream = Resources.getInputStream("classpath:config/notty.json");
//			notty = objectMapper.readValue(inputStream, Notty.class);
//		} catch (IOException e) {
//			throw new RuntimeException("Cannot initialize NOTTY application configuration!", e);
//		}
//		this.APP = notty.APP;
//		this.SESSION_USER = notty.SESSION_USER;
//		this.UPLOAD_RELATIVE_DIR = notty.UPLOAD_RELATIVE_DIR;
//		this.UPLOAD_ABSOLUTE_DIR = notty.UPLOAD_ABSOLUTE_DIR;
//		this.MAX_FILE_SIZE = notty.MAX_FILE_SIZE;
//		this.MAX_USERS_COUNT = notty.MAX_USERS_COUNT;
//		this.DEFAULT_USERS_COUNT = notty.DEFAULT_USERS_COUNT;
//		this.MAX_NOTICES_COUNT = notty.MAX_NOTICES_COUNT;
//		this.DEFAULT_NOTICES_COUNT = notty.DEFAULT_NOTICES_COUNT;
//		this.MIN_ACCOUNT_LENGTH = notty.MIN_ACCOUNT_LENGTH;
//		this.MAX_ACCOUNT_LENGTH = notty.MAX_ACCOUNT_LENGTH;
//		this.MIN_PASSWORD_LENGTH = notty.MIN_PASSWORD_LENGTH;
//		this.MAX_PASSWORD_LENGTH = notty.MAX_PASSWORD_LENGTH;
//		this.PASSWORD_PRIVATE_KEY = notty.PASSWORD_PRIVATE_KEY;
//		this.ACCEPT_FILE_TYPE = notty.ACCEPT_FILE_TYPE;
//		this.PASSWORD_RANGE = notty.PASSWORD_RANGE;
//		this.ACCOUNT_RANGE = notty.ACCOUNT_RANGE;
//		this.PASSWORDS_NOT_EQUALS = notty.PASSWORDS_NOT_EQUALS;
//		this.OLD_PASSWORD_NOT_MATCHES = notty.OLD_PASSWORD_NOT_MATCHES;
//		this.ACCOUNT_PASSWORD_NOT_MATCHS = notty.ACCOUNT_PASSWORD_NOT_MATCHS;
//		this.AUTHED_NAME_NOT_NULL = notty.AUTHED_NAME_NOT_NULL;
//		this.ORG_NOT_NULL = notty.ORG_NOT_NULL;
//		this.CANNOT_CREATE_DIR = notty.CANNOT_CREATE_DIR;
//		this.ACCOUNT_EXISTS = notty.ACCOUNT_EXISTS;
//		this.ACCOUNT_BLOCKED = notty.ACCOUNT_BLOCKED;
//		this.USERS_ARG_ERROR = notty.USERS_ARG_ERROR;
//		this.NOTICES_ARG_ERROR = notty.NOTICES_ARG_ERROR;
//		this.USER_NOT_NULL = notty.USER_NOT_NULL;
//		this.TITLE_NOT_NULL = notty.TITLE_NOT_NULL;
//		this.CONTENT_NOT_NULL = notty.CONTENT_NOT_NULL;
//		this.FILE_NAME_NOT_NULL = notty.FILE_NAME_NOT_NULL;
//		this.FILE_URL_NOT_NULL = notty.FILE_URL_NOT_NULL;
//		this.NOT_SUPPORT = notty.NOT_SUPPORT;
//		this.NOT_FOUND = notty.NOT_FOUND;
//		this.NO_PERMISSION = notty.NO_PERMISSION;
//		this.REQUIRED_LOGIN = notty.REQUIRED_LOGIN;
//		this.FILE_SIZE_OVERFLOW = notty.FILE_SIZE_OVERFLOW;
//		this.FILE_TYPE_NOT_ALLOW = notty.FILE_TYPE_NOT_ALLOW;
//		this.FILE_UPLOAD_FAIL = notty.FILE_UPLOAD_FAIL;
//		this.DELETE_FILE_ERROR = notty.DELETE_FILE_ERROR;
//
//		if (inputStream != null) {
//			try {
//				inputStream.close();
//			} catch (IOException e) {
//				// do nothing.
//			}
//		}
//	}

	public static final String APP = json.getString("APP");
	public static final String SESSION_USER = json.getString("SESSION_USER");
	public static final long MAX_FILE_SIZE = json.getJsonNumber("MAX_FILE_SIZE").longValue();
	public static final String UPLOAD_RELATIVE_DIR = json.getString("UPLOAD_RELATIVE_DIR");
	public static final String UPLOAD_ABSOLUTE_DIR = json.getString("UPLOAD_ABSOLUTE_DIR");
	public static final int MAX_USERS_COUNT = json.getInt("MAX_USERS_COUNT");
	public static final int DEFAULT_USERS_COUNT = json.getInt("DEFAULT_USERS_COUNT");
	public static final int MAX_NOTICES_COUNT = json.getInt("MAX_NOTICES_COUNT");
	public static final int DEFAULT_NOTICES_COUNT = json.getInt("DEFAULT_NOTICES_COUNT");
	public static final int MIN_ACCOUNT_LENGTH = json.getInt("MIN_ACCOUNT_LENGTH");
	public static final int MAX_ACCOUNT_LENGTH = json.getInt("MAX_ACCOUNT_LENGTH");
	public static final int MIN_PASSWORD_LENGTH = json.getInt("MIN_PASSWORD_LENGTH");
	public static final int MAX_PASSWORD_LENGTH = json.getInt("MAX_PASSWORD_LENGTH");
	public static final String PASSWORD_PRIVATE_KEY = json.getString("PASSWORD_PRIVATE_KEY");
	public static final JsonArray ACCEPT_FILE_TYPE = json.getJsonArray("ACCEPT_FILE_TYPE");

	public static final String PASSWORD_RANGE = json.getString("PASSWORD_RANGE");
	public static final String PASSWORDS_NOT_EQUALS = json.getString("PASSWORDS_NOT_EQUALS");
	public static final String OLD_PASSWORD_NOT_MATCHES = json.getString("OLD_PASSWORD_NOT_MATCHES");
	public static final String ACCOUNT_RANGE = json.getString("ACCOUNT_RANGE");
	public static final String ORG_NOT_NULL = json.getString("ORG_NOT_NULL");
	public static final String CANNOT_CREATE_DIR = json.getString("CANNOT_CREATE_DIR");
	public static final String AUTHED_NAME_NOT_NULL = json.getString("AUTHED_NAME_NOT_NULL");
	public static final String ACCOUNT_EXISTS = json.getString("ACCOUNT_EXISTS");
	public static final String ACCOUNT_PASSWORD_NOT_MATCHS = json.getString("ACCOUNT_PASSWORD_NOT_MATCHS");
	public static final String ACCOUNT_BLOCKED = json.getString("ACCOUNT_BLOCKED");
	public static final String USERS_ARG_ERROR = json.getString("USERS_ARG_ERROR");
	public static final String NOTICES_ARG_ERROR = json.getString("NOTICES_ARG_ERROR");
	public static final String USER_NOT_NULL = json.getString("USER_NOT_NULL");
	public static final String TITLE_NOT_NULL = json.getString("TITLE_NOT_NULL");
	public static final String CONTENT_NOT_NULL = json.getString("CONTENT_NOT_NULL");
//	public static final String FILE_NAME_NOT_NULL = json.getString("FILE_NAME_NOT_NULL");
//	public static final String FILE_URL_NOT_NULL = json.getString("FILE_URL_NOT_NULL");
	public static final String NOT_SUPPORT = json.getString("NOT_SUPPORT");
	public static final String NOT_FOUND = json.getString("NOT_FOUND");
	public static final String NO_PERMISSION = json.getString("NO_PERMISSION");
	public static final String REQUIRED_LOGIN = json.getString("REQUIRED_LOGIN");
	public static final String FILE_SIZE_OVERFLOW = json.getString("FILE_SIZE_OVERFLOW");
	public static final String FILE_TYPE_NOT_ALLOW = json.getString("FILE_TYPE_NOT_ALLOW");
	public static final String FILE_UPLOAD_FAIL = json.getString("FILE_UPLOAD_FAIL");
	public static final String DELETE_FILE_ERROR = json.getString("DELETE_FILE_ERROR");

}
