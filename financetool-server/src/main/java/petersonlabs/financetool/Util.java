package petersonlabs.financetool;

import org.eclipse.jetty.util.thread.ExecutionStrategy;

import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public class Util {

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
