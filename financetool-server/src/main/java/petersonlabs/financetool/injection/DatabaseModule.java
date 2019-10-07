package petersonlabs.financetool.injection;

import com.google.inject.AbstractModule;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import petersonlabs.financetool.SystemProperties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DatabaseModule extends AbstractModule {
	@Override
	protected void configure()
	{
		SQLServerDataSource dataSource = new SQLServerDataSource();
		dataSource.setServerName(SystemProperties.DB_HOST);
		dataSource.setDatabaseName(SystemProperties.DB_NAME);
		dataSource.setPortNumber(SystemProperties.DB_PORT);
		dataSource.setUser(SystemProperties.DB_USERNAME);
		dataSource.setPassword(SystemProperties.DB_PASSWORD);

		bind(DataSource.class).toInstance(dataSource);
	}
}
