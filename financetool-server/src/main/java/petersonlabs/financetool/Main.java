package petersonlabs.financetool;

import com.google.inject.Guice;
import com.google.inject.Injector;
import petersonlabs.financetool.api.*;
import petersonlabs.financetool.injection.DatabaseModule;
import petersonlabs.financetool.injection.JavalinModule;

public class Main {
	public static void main(String[] args)
	{
		Injector injector = Guice.createInjector(
			new DatabaseModule(),
			new JavalinModule()
			);

		injector.getInstance(TransactionResource.class).setup();
		injector.getInstance(CategoryResource.class).setup();
		injector.getInstance(CategoryMatcherResource.class).setup();
		injector.getInstance(TypeResource.class).setup();
		injector.getInstance(SourceResource.class).setup();
	}
}
