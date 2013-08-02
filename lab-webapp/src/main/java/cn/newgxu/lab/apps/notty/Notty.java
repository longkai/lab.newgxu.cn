package cn.newgxu.lab.apps.notty;

import cn.newgxu.lab.core.util.Resources;
import cn.newgxu.lab.core.util.ResourcesCallback;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author longkai
 * @version 0.1.0.13-8-2
 * @email im.longkai@gmail.com
 * @since 13-8-2
 */
public enum Notty {
	APP,
	SESSION_USER,
	MAX_FILE_SIZE,
	UPLOAD_RELATIVE_DIR,
	UPLOAD_ABSOLUTE_DIR,
	MAX_USERS_COUNT,
	DEFAULT_USERS_COUNT,
	MAX_NOTICES_COUNT,
	DEFAULT_NOTICES_COUNT,
	MIN_ACCOUNT_LENGTH,
	MAX_ACCOUNT_LENGTH,
	MIN_PASSWORD_LENGTH,
	MAX_PASSWORD_LENGTH,
	PASSWORD_PRIVATE_KEY,

	MESSAGES,
	PASSWORD_RANGE,
	PASSWORDS_NOT_EQUALS,
	ACCOUNT_RANGE,
	ORG_NOT_NULL,
	AUTHED_NAME_NOT_NULL,
	ACCOUNT_EXISTS,
	ACCOUNT_PASSWORD_NOT_MATCHS,
	ACCOUNT_BLOCKED,
	USERS_ARG_ERROR,
	NOTICES_ARG_ERROR,
	USER_NOT_NULL,
	TITLE_NOT_NULL,
	CONTENT_NOT_NULL,
	FILE_NAME_NOT_NULL,
	FILE_URL_NOT_NULL,
	NOT_SUPPORT,
	NOT_FOUND,
	NO_PERMISSION,
	REQUIRED_LOGIN,
	FILE_SIZE_OVERFLOW,
	FILE_TYPE_NOT_ALLOW,
	FILE_UPLOAD_FAIL,
	DELETE_FILE_ERROR;

	public static final JsonObject R
		 = (JsonObject) Resources.getJsonValue("classpath:config/notty.json", null);

	public static String getMessage(Notty name) {
		return R.getJsonObject(MESSAGES.name()).getString(name.name());
	}

}
