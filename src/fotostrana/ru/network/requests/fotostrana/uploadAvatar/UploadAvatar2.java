package fotostrana.ru.network.requests.fotostrana.uploadAvatar;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import fotostrana.ru.log.Log;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Второй способ загрузки аватарки (использовать без придварительных запросов)
 * 
 */
public class UploadAvatar2 extends RequestFotostrana {
	public final static String BOUNDARY = "----WebKitFormBoundaryk3ywyYvazxa3mEpI";

	/**
	 * Файл с аватаркой
	 */
	private File fileAvatar;

	/**
	 * Загружает аватарку
	 * 
	 * @param user
	 *            пользователь которому будет загружена
	 * @param avatar
	 *            файл с аватаркой
	 */
	public UploadAvatar2(RequestFotostrana parentRequest, File avatar) {
		super(parentRequest);
		fileAvatar = avatar;
		typeRequest = TYPE_POST;
		isUnanswered = true;
	}

	@Override
	public void setResult(String result) {
		Log.LOGGING.addUserLog("Загружена аватарка пользователю " + user.id,
				Log.TYPE_POSITIVE);
	}

	@Override
	public HttpEntity getRequestData() {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.setBoundary(BOUNDARY);

		builder.addBinaryBody("avatar", fileAvatar,
				ContentType.create("image/jpeg"), fileAvatar.getName());
		HttpEntity entity = builder.build();
		return entity;
	}

	@Override
	public String getURL() {
		return URL_FOTOSTRANA + "userphoto/ajax/uploadTempAvatarPhoto";
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> heads = new HashMap<String, String>();
		heads.put("Referer", "http://fotostrana.ru/user/userpic/");
		return heads;
	}

}
