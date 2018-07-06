package gov.cdc.foundation.helper;

import gov.cdc.helper.AbstractMessageHelper;

public class MessageHelper extends AbstractMessageHelper {

	public static final String CONST_WARNING = "warning";
	public static final String CONST_VALID = "valid";

	public static final String METHOD_INDEX = "index";
	public static final String METHOD_TRANSFORMCDATOJSON = "transformCDAtoJSON";
	public static final String METHOD_GENERATE = "generate";

	public static final String ERROR_EMPTY_MESSAGE = "Empty message";

	private MessageHelper() {
		throw new IllegalAccessError("Helper class");
	}

}
