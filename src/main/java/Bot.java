import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Bot extends TelegramLongPollingBot {

	@Override
	public String getBotToken() {
		return "";
	}

	@Override
	public void onUpdateReceived(Update update) {
		Message message = update.getMessage();
		try {
			sendMsg(message.getChatId(), find_synonym(message.getText()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getBotUsername() {
		return "synonym";
	}

	private void sendMsg(Long chatId, String text) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(text);
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private String find_synonym(String text) throws IOException {
		HttpPost post = new HttpPost("https://rustxt.ru/api/index.php");

		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("method", "getSynText"));
		urlParameters.add(new BasicNameValuePair("text", text));
		urlParameters.add(new BasicNameValuePair("Content-Type", "application/json; charset=utf-8"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters, HTTP.UTF_8));

		try (CloseableHttpClient httpClient = HttpClients.createDefault();
			 CloseableHttpResponse response = httpClient.execute(post)) {

			JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
			System.out.println(jsonObject);
			String ans = jsonObject.getString("modified_text");
			ans = ans.substring(0,ans.indexOf("<"));
			return ans;
		}
	}
}
