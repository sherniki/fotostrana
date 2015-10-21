package fotostrana.ru.network.requests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;

import fotostrana.ru.network.Request;

/**
 * Тестовый запрос
 * 
 */
public class TestRequest extends Request {

	public TestRequest() {
		typeRequest = TYPE_POST;
	}

	@Override
	public void setResult(String result) {
		System.out.println("Result = " + result);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/contest/teamajax/getcommandusers/";
	}

	@Override
	public HttpEntity getRequestData() {
		String dataV = "team=3&ajax=1&page=1&limit=90";
		InputStreamEntity requestData = null;
		try {
			InputStream dataVote = new ByteArrayInputStream(
					dataV.getBytes("UTF-8"));
			requestData = new InputStreamEntity(dataVote,
					ContentType.APPLICATION_FORM_URLENCODED);

		} catch (UnsupportedEncodingException e) {
		}
		return requestData;
	}

}
