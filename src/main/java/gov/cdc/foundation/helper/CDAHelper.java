package gov.cdc.foundation.helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CDAHelper {

	private static CDAHelper instance;

	private String appVersion;

	public CDAHelper(@Value("${version}") String version) {
		this.appVersion = version;
		instance = this;
	}

	public static CDAHelper getInstance() {
		return instance;
	}

	public JSONObject parse(String msg) throws Exception {
		XmlJsonDataFormat xmlJsonDataFormat = new XmlJsonDataFormat();
		xmlJsonDataFormat.setEncoding("UTF-8");
		xmlJsonDataFormat.setForceTopLevelObject(true);
		xmlJsonDataFormat.setTrimSpaces(false);
		xmlJsonDataFormat.setSkipNamespaces(true);
		xmlJsonDataFormat.setRemoveNamespacePrefixes(true);

		xmlJsonDataFormat.start();
		InputStream stream = new ByteArrayInputStream(msg.getBytes(StandardCharsets.UTF_8));
		String json = xmlJsonDataFormat.getSerializer().readFromStream(stream).toString();

		JSONObject obj = new JSONObject();
		JSONObject message = new JSONObject();
		JSONObject cda = new JSONObject();
		obj.put("message", message);
		message.put("type", "CDA");
		message.put("CDA", cda);
		cda.put("version", "1");
		cda.put("source", new JSONObject(json));

		// Add extractor information
		JSONObject extractor = new JSONObject();
		extractor.put("version", getVersion());
		extractor.put("hash", generateHash(msg));
		JSONObject ucid = new JSONObject();
		extractor.put("UCID", ucid);
		ucid.put("id", "EMPTY");
		ucid.put("hash", generateHash("EMPTY"));
		extractor.put("timestamp", Calendar.getInstance().getTimeInMillis());

		obj.put("extractor", extractor);

		return obj;
	}

	public String getVersion() {
		return appVersion;
	}

	private static String generateHash(String msg) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(msg.getBytes());

		byte byteData[] = md.digest();

		// convert the byte to hex format method 1
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

}
