package petersonlabs.financetool.api;

import io.javalin.http.Context;

public interface Resource {
	static void ok(Context context) {
		context.status(200);
		context.result("OK");
	}
}
