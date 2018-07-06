package gov.cdc.foundation.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import gov.cdc.foundation.helper.CDAHelper;
import gov.cdc.foundation.helper.LoggerHelper;
import gov.cdc.foundation.helper.MessageHelper;
import gov.cdc.foundation.helper.TemplateHelper;
import gov.cdc.helper.ErrorHandler;
import io.swagger.annotations.ApiOperation;

@Controller
@EnableAutoConfiguration
@RequestMapping("/api/1.0/")
public class CDAController {
	
	@Value("${version}")
	private String version;

	private static final Logger logger = Logger.getLogger(CDAController.class);
	
	@Autowired
	private TemplateHelper templateHelper;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> index() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, Object> log = new HashMap<>();
		
		try {
			JSONObject json = new JSONObject();
			json.put("version", version);
			return (new ResponseEntity<JsonNode>(mapper.readTree(json.toString()), HttpStatus.OK));
		} catch (Exception e) {
			logger.error(e);
			LoggerHelper.getInstance().log(MessageHelper.METHOD_INDEX, log);
			
			return ErrorHandler.getInstance().handle(e, log);
		}
	}

	@RequestMapping(value = "json", method = RequestMethod.POST)
	@ApiOperation(value = "Transform CDA to JSON", notes = "Transforms a CDA message to JSON object.")
	@ResponseBody
	public ResponseEntity<?> transformCDAtoJSON(@RequestBody(required = true) String message) {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> log = new HashMap<>();
		log.put(MessageHelper.CONST_METHOD, MessageHelper.METHOD_TRANSFORMCDATOJSON);

		try {
			JSONObject json = CDAHelper.getInstance().parse(message);

			log.put(MessageHelper.CONST_SUCCESS, true);
			LoggerHelper.getInstance().log(MessageHelper.METHOD_TRANSFORMCDATOJSON, log);

			return (new ResponseEntity<JsonNode>(mapper.readTree(json.toString()), HttpStatus.OK));
		} catch (Exception e) {
			logger.error(e);
			LoggerHelper.getInstance().log(MessageHelper.METHOD_TRANSFORMCDATOJSON, log);
			
			return ErrorHandler.getInstance().handle(e, log);
		}
	}

	@RequestMapping(value = "generate", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiOperation(value = "Generate random CDA message", notes = "Generates a random CDA message. All names, characters, and incidents generated when calling this API are fictitious. No identification with actual persons (living or deceased), places, buildings, and products is intended or should be inferred. Any resemblance to real persons, living or dead, is purely coincidental.")
	@ResponseBody
	public ResponseEntity<?> generate() {

		Map<String, Object> log = new HashMap<>();
		log.put(MessageHelper.CONST_METHOD, MessageHelper.METHOD_GENERATE);

		try {

			Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
			cfg.setClassForTemplateLoading(this.getClass(), "/");
			Template template = cfg.getTemplate("templates/cda.ftl");
			StringWriter sw = new StringWriter();
			template.process(templateHelper.getModel(), sw);
			return new ResponseEntity<>(sw.toString(), HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e);
			LoggerHelper.getInstance().log(MessageHelper.METHOD_GENERATE, log);

			return ErrorHandler.getInstance().handle(e, log);
		}
	}

}