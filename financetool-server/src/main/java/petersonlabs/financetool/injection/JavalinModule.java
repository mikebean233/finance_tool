package petersonlabs.financetool.injection;

import com.google.inject.AbstractModule;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import petersonlabs.financetool.SystemProperties;

import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class JavalinModule extends AbstractModule {

	@Override
	protected void configure() {

		ExceptionHandler<Exception> exceptionHandler = (exception, context) -> {
			context.result(exception.getLocalizedMessage());
			context.status(500);
			System.err.println(exception.getLocalizedMessage());
		};

		OpenApiOptions openApiOptions = new OpenApiOptions(
				new Info()
					.version(getClass().getPackage().getImplementationVersion())
					.description("Finance Tool Api")
					)
			.path(SystemProperties.API_DOC_PATH)
			.swagger(new SwaggerOptions("/swagger").title("Swagger Title"));

		Consumer<JavalinConfig> config = c -> c
				.registerPlugin(new OpenApiPlugin(openApiOptions))
				.addStaticFiles(SystemProperties.WEB_ROOT, Location.EXTERNAL);

		Javalin javalin = Javalin.create(config)
			.exception(Exception.class, exceptionHandler)
			.start(SystemProperties.WS_PORT);

		bind(Javalin.class).toInstance(javalin);
	}
}
