package net.sourceforge.seqware.queryengine.webservice.model;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

/**
 * <p>BackendPool class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class BackendPool {

	private static BerkeleyDBFactory factory = new BerkeleyDBFactory();
	private static HashMap<String, BerkeleyDBStore> stores = new HashMap<String, BerkeleyDBStore>();
	private static HashMap<String, Semaphore> storeSemaphores = new HashMap<String, Semaphore>();
	/** Constant <code>maxThreads=Integer.parseInt(EnvUtil.getProperty("maxconnections"))</code> */
	protected static int maxThreads = Integer.parseInt(EnvUtil.getProperty("maxconnections"));

	/**
	 * <p>getStore.</p>
	 *
	 * @param filePath a {@link java.lang.String} object.
	 * @param cacheSize a {@link java.lang.Long} object.
	 * @param locks a int.
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore} object.
	 * @throws java.lang.InterruptedException if any.
	 * @throws java.lang.Exception if any.
	 */
	public synchronized static BerkeleyDBStore getStore(String filePath, Long cacheSize, int locks) throws InterruptedException, Exception {
		Semaphore s = storeSemaphores.get(filePath);
		BerkeleyDBStore store = stores.get(filePath);
		if (s == null) {
		  System.out.println("Semaphore null, creating a new one");
		  s = new Semaphore(maxThreads);
		  storeSemaphores.put(filePath, s);
		}
		s.acquire();
		System.out.println("Acquired semaphore "+s.availablePermits()+" are left");
		if (store == null) {
		  System.out.println("Store is null, getting store");
			SeqWareSettings settings = new SeqWareSettings();
			settings.setStoreType("berkeleydb-mismatch-store");
			settings.setFilePath(filePath);
			settings.setCacheSize(cacheSize);
			settings.setMaxLockers(locks);
			settings.setMaxLockObjects(locks);
			settings.setMaxLocks(locks);
			settings.setCreateMismatchDB(true); // FIXME: SET THIS BACK TO FALSE FOR RELEASE!
			settings.setCreateConsequenceAnnotationDB(false);
			settings.setCreateDbSNPAnnotationDB(false);
			settings.setReadOnly(false); //FIXME
			store = factory.getStore(settings);
			stores.put(filePath, store);
			System.out.println("Store placed in hash");
		}
		return(store);
	}

	/**
	 * <p>releaseStore.</p>
	 *
	 * @param filePath a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public synchronized static void releaseStore(String filePath) throws Exception {
		Semaphore s = storeSemaphores.get(filePath);
		BerkeleyDBStore store = stores.get(filePath);
		if (s == null || store == null) { throw (new Exception("Can't free semaphore or store, one or both are null!")); }
		// release the lock
		s.release();
		System.out.println("Released semaphore "+s.availablePermits()+" are left");
		// check to see how many permits are checked out
		if (s.availablePermits() == maxThreads) {
	    // then I can shutdown this store
		  System.out.println("Semaphore available threads = max threads so I can close this");
		  store.close();
		  storeSemaphores.put(filePath, null);
		  stores.put(filePath, null);
		}
	}
}
