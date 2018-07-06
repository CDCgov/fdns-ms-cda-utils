package gov.cdc.foundation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { 
		"logging.fluentd.host=localhost", 
		"logging.fluentd.port=24224",
		"proxy.hostname=localhost",
		"security.oauth2.resource.user-info-uri=",
		"security.oauth2.protected=",
		"security.oauth2.client.client-id=",
		"security.oauth2.client.client-secret=",
		"ssl.verifying.disable=false"})
public class CdaApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	private JacksonTester<JsonNode> json;
	private String baseUrlPath = "/api/1.0/";

	@Before
	public void setup() {
		ObjectMapper objectMapper = new ObjectMapper();
		JacksonTester.initFields(this, objectMapper);
	}

	@Test
	public void indexPage() {
		ResponseEntity<String> response = this.restTemplate.getForEntity("/", String.class);
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody(), CoreMatchers.containsString("FDNS CDA Utilities Microservice"));
	}

	@Test
	public void indexAPI() {
		ResponseEntity<String> response = this.restTemplate.getForEntity(baseUrlPath, String.class);
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody(), CoreMatchers.containsString("version"));
	}

	@Test
	public void transformCDAtoJSON() throws Exception {
		ResponseEntity<JsonNode> response = restTemplate.exchange(
				baseUrlPath + "/json", 
				HttpMethod.POST, 
				getCDAEntity("01.txt"), 
				JsonNode.class);
		JsonContent<JsonNode> body = this.json.write(response.getBody());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(body).hasJsonPathValue("@.extractor");
		assertThat(body).hasJsonPathValue("@.message.CDA.source");
		assertThat(body).extractingJsonPathStringValue("@.message.CDA.version").isEqualTo("1");
	}

	private InputStream getResource(String path) throws IOException {
		return getClass().getClassLoader().getResourceAsStream(path);
	}

	private String getResourceAsString(String path) throws IOException {
		return IOUtils.toString(getResource(path));
	}

	private HttpEntity<String> getEntity(String content, MediaType mediaType) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		HttpEntity<String> entity = new HttpEntity<String>(content, headers);
		return entity;
	}

	private String getCDA(String filename) throws IOException {
		return getResourceAsString("junit/cda/" + filename);
	}

	private HttpEntity<String> getCDAEntity(String filename) throws IOException {
		return getEntity(getCDA(filename), MediaType.TEXT_PLAIN);
	}


}
