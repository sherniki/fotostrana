package fotostrana.ru.network.requests.fotostrana.uploadAvatar;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Загружает аватарку
 * 
 */
public class UploadAvatar extends RequestFotostrana {

	public final static String BOUNDARY = "----WebKitFormBoundaryk3ywyYvazxa3mEpI";
	public final static byte[] DATA_TOKEN = new byte[] { 0x30 };
	public final static byte[] DATA_AVATARUPLOAD = new byte[] { 0x31 };
	public final static byte[] DATA_REASON = new byte[] { 0x32, 0x34 };
	public final static byte[] DATA_FIELD = new byte[] { 0x35, 0x30 };
	public final static byte[] DATA_SOURCE = new byte[] { 0x30 };

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
	public UploadAvatar(RequestFotostrana parentRequest, File avatar) {
		super(parentRequest);
		fileAvatar = avatar;
		typeRequest = TYPE_POST;
	}

	@Override
	public void setResult(String result) {
//		System.out.println(result);

	}

	@Override
	public HttpEntity getRequestData() {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.setBoundary(BOUNDARY);

		builder.addBinaryBody("attachphoto[]", fileAvatar,
				ContentType.create("image/jpeg"), fileAvatar.getName());
		builder.addBinaryBody("token", DATA_TOKEN);
		builder.addBinaryBody("avatarUpload", DATA_AVATARUPLOAD);
		builder.addBinaryBody("reason", DATA_REASON);
		builder.addBinaryBody("field", DATA_FIELD);
		builder.addBinaryBody("source", DATA_SOURCE);

		HttpEntity entity = builder.build();
		return entity;
	}

	@Override
	public String getURL() {
		return URL_FOTOSTRANA + "userphoto/ajax/uploadAvatar";
	}

}
