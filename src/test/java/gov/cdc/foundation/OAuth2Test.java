package gov.cdc.foundation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import gov.cdc.helper.RequestHelper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { 
		"logging.fluentd.host=fluentd",
		"logging.fluentd.port=24224",
		"proxy.hostname=",
		"security.oauth2.resource.user-info-uri=http://fdns-ms-stubbing:3002/oauth2/token/introspect",
		"security.oauth2.protected=/api/1.0/**",
		"security.oauth2.client.client-id=test",
		"security.oauth2.client.client-secret=testsecret",
		"ssl.verifying.disable=false"})
public class OAuth2Test {

	@Autowired
	private TestRestTemplate restTemplate;
	private String baseUrlPath = "/api/1.0/";
	private String testToken = "Bearer testtoken";

	@Test
	public void testUnauthenticated() {
		ResponseEntity<String> response = this.restTemplate.getForEntity(baseUrlPath + "generate", String.class);
		assertThat(response.getStatusCodeValue()).isEqualTo(401);
	}

	@Test
	public void testAthenticated() throws Exception {
		setScope("fdns.cda-utils");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", testToken);
		HttpEntity<String> request = new HttpEntity<String>("{}", headers);

		ResponseEntity<String> response = this.restTemplate.exchange(
			baseUrlPath + "generate",
			HttpMethod.GET,
			request,
			String.class
		);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
	}

	private void setScope(String scope) {
		MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
		data.add("scope", scope);
		RequestHelper.getInstance().executePost("http://fdns-ms-stubbing:3002/oauth2/mock", data);
	}

}
