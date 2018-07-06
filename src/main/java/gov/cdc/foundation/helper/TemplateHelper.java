package gov.cdc.foundation.helper;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TemplateHelper {

	@Value("${template.cda.dateFormat}")
	private String dateFormat;
	@Value("#{'${template.cda.surname}'.split(',')}")
	private List<String> surnames;
	@Value("#{'${template.cda.givenname}'.split(',')}")
	private List<String> givennames;
	@Value("#{'${template.cda.sex}'.split(',')}")
	private List<String> sexes;
	@Value("#{'${template.cda.maritalStatus}'.split(',')}")
	private List<String> maritalStatuses;
	@Value("#{'${template.cda.maritalStatus.label}'.split(',')}")
	private List<String> maritalStatusLabels;
	@Value("#{'${template.cda.reasonForStudy.code}'.split(',')}")
	private List<String> reasonForStudyCodes;
	@Value("#{'${template.cda.reasonForStudy.name}'.split(';')}")
	private List<String> reasonForStudyNames;
	@Value("#{'${template.cda.jurisdictionCode}'.split(',')}")
	private List<String> jurisdictionCodes;

	public Map<String, Object> getModel() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("surname", surnames.get(ThreadLocalRandom.current().nextInt(surnames.size())));
		data.put("givenname", givennames.get(ThreadLocalRandom.current().nextInt(givennames.size())));
		LocalDate dob = LocalDate.now().minus(Period.ofDays((new Random().nextInt(365 * 80))));
		data.put("dob", dob.format(DateTimeFormatter.ofPattern(dateFormat)));
		data.put("sex", sexes.get(ThreadLocalRandom.current().nextInt(sexes.size())));

		int maritalStatusIdx = ThreadLocalRandom.current().nextInt(maritalStatuses.size());
		data.put("maritalStatus", maritalStatuses.get(maritalStatusIdx));
		data.put("maritalStatusLabel", maritalStatusLabels.get(maritalStatusIdx));
		
		int indexReason = ThreadLocalRandom.current().nextInt(reasonForStudyCodes.size());
		data.put("reasonForStudyCode", reasonForStudyCodes.get(indexReason));
		data.put("reasonForStudyName", reasonForStudyNames.get(indexReason));

		int jurisdictionIdx = ThreadLocalRandom.current().nextInt(jurisdictionCodes.size());
		data.put("jurisdictionCode", jurisdictionCodes.get(jurisdictionIdx));
		
		return data;
	}
}
