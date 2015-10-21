package fotostrana.ru.network.requests.fotostrana.uploadAvatar;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;

import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Подготовительный запрос загрузки аватарки
 * 
 */
public class PreparationRequestUploadAvatar extends RequestFotostrana {
	public static final String data = "computer=1&vk=0&mail=0&facebook=0&closeButton=0&view=1";

	public PreparationRequestUploadAvatar(RequestFotostrana parrentRequest) {
		super(parrentRequest);
		typeRequest = TYPE_POST;
		isUnanswered = true;
	}

	@Override
	public void setResult(String result) {
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/userphoto/ajax/avastat/";
	}

	@Override
	public HttpEntity getRequestData() {
		InputStreamEntity requestData = null;
		try {
			InputStream dataVote = new ByteArrayInputStream(
					data.getBytes("UTF-8"));
			requestData = new InputStreamEntity(dataVote,
					ContentType.APPLICATION_FORM_URLENCODED);

		} catch (UnsupportedEncodingException e) {
		}
		return requestData;
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> heads = new HashMap<String, String>();
		heads.put("Referer", "http://fotostrana.ru/user/" + user.id
				+ "/?from=header.menu");
		return heads;
	}

}
