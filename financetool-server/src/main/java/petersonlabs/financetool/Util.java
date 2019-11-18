package petersonlabs.financetool;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import org.eclipse.jetty.util.thread.ExecutionStrategy;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public class Util {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(SystemProperties.DATE_FORMAT);

	public static <T> T parseJson(String json, Class<T> clazz) throws IOException {
		return OBJECT_MAPPER.readValue(json, clazz);
	}

	public static <T> T parseJson(String json, TypeReference<T> type) throws IOException {
		return OBJECT_MAPPER.readValue(json, type);
	}

	public static <T> String serialize(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch(JsonProcessingException ex){
			return null;
		}
	}

	@FunctionalInterface
	public interface ThrowingRunnable< E extends Exception> {
		void run() throws E;
	}

	@FunctionalInterface
	public interface ThrowingProducer<O, I, E extends Exception> {
		O produce(I in) throws E;
	}

	public static void doWithLock(Lock lock, ThrowingRunnable<SQLException> action) throws SQLException {
		lock.lock();
		try {
			action.run();
		} finally {
			lock.unlock();
		}
	}

	/*
	public static <O> O doWithLock(Lock lock, ThrowingProducer<O, SQLException> function) throws SQLException {
		lock.lock();
		try {
			return function.produce();
		} finally {
			lock.unlock();
		}
	}
	*/

	public static <O> O doWithLock(Lock lock, Supplier<O> supplier) {
		lock.lock();
		try {
			return supplier.get();
		} finally {
			lock.unlock();
		}
	}


}
