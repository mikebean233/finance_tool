package petersonlabs.financetool;

public final class SystemProperties {

	// DATABASE
	public static final String DB_HOST      = getString("DB_HOST", "finance-db");
	public static final int DB_PORT         = getInteger("DB_PORT", 1433);
	public static final String DB_USERNAME  = getString("DB_USERNAME", "finance-ws");
	public static final String DB_PASSWORD  = getString("DB_PASSWORD", "");
	public static final String DB_NAME      = getString("DB_NAME", "finance");
	public static final String API_DOC_PATH = getString("API_DOC_PATH", "/api_doc");

	// JAVALIN
	public static final int WS_PORT         = getInteger("WS_PORT", 8080);

	public static final String DATE_FORMAT  = getString("DATE_FORMAT", "MM/dd/yy");

	private static String getString(String name, String defaultValue) {
		return System.getProperty(name, defaultValue);
	}

	private static int getInteger(String name, int defaultValue) {
		int returnValue = defaultValue;

		try {
			returnValue = Integer.parseInt(System.getProperty(name));
		} catch (Exception ex) {
			System.err.println(String.format("problem parsing int property %s: \r\n%s", name, ex.getMessage()));
		}
		return returnValue;
	}
}
