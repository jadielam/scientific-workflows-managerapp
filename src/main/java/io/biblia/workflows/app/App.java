package io.biblia.workflows.app;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

import io.biblia.workflows.Configuration;
import io.biblia.workflows.ConfigurationKeys;
import io.biblia.workflows.manager.action.ActionManager;
import io.biblia.workflows.manager.action.ActionPersistance;
import io.biblia.workflows.manager.action.MongoActionPersistance;
import io.biblia.workflows.manager.dataset.DatasetManager;
import io.biblia.workflows.manager.dataset.DatasetPersistance;
import io.biblia.workflows.manager.dataset.MongoDatasetPersistance;
import io.biblia.workflows.manager.decision.DatasetLogDao;
import io.biblia.workflows.manager.decision.MongoDatasetLogDao;

/**
 * Hello world!
 *
 */
public class App implements ConfigurationKeys {
    
	public static void main( String[] args ) {
		
		//Shutdown hook up to clean all the states.
		Runtime.getRuntime().addShutdownHook(new Thread() {
    		@Override
    		public void run() {
    			try {
    				Thread.sleep(200);
    				System.out.println("Shutting down managers ...");
    				ActionManager.stop();
    				DatasetManager.stop();	
    			}
    			catch(InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	});
    	
		//0. Initialize mongo client
		MongoClient mongo = initializeMongoClient();
    	
    	//1. Initialize the Action Manager thread
    	ActionPersistance aPersistance = new MongoActionPersistance(mongo);
    	ActionManager.start(aPersistance);
    	
    	//2. Initialize the Dataset Manager thread
    	DatasetPersistance dPersistance = new MongoDatasetPersistance(mongo);
    	DatasetLogDao dLogDao = MongoDatasetLogDao.getInstance(mongo);
    	DatasetManager.start(dPersistance, dLogDao);
    	
    	//3. TODO: Initialize the Decision Manager thread
    	//TODO: This is TODO, because it requires Hadoop initialization
    	//and I don't have Hadoop set up here.
    	
    	//4. Join all the initialized threads
    	ActionManager.join();
    	DatasetManager.join();
    	
    	
    }
	
	/**
	 * Creates a MongoClient that supports multithreading.
	 * @return
	 */
	private static MongoClient initializeMongoClient() {
		String mongo_host = Configuration.getValue(MONGODB_HOST, "192.168.99.100");
		int mongo_port = Integer.parseInt(Configuration.getValue(MONGODB_PORT, "27017"));
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.threadsAllowedToBlockForConnectionMultiplier(50000);
		builder.socketKeepAlive(true);
		builder.connectionsPerHost(10000);
		builder.minConnectionsPerHost(2500);
		MongoClientOptions options = builder.build();
		
		MongoClient mongo = new MongoClient(new ServerAddress(mongo_host, mongo_port), options);
		return mongo;
	}
}
