package io.biblia.workflows.app;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

import io.biblia.workflows.utils.MongoClientBuilder;
import io.biblia.workflows.Configuration;
import io.biblia.workflows.ConfigurationKeys;
import io.biblia.workflows.manager.action.ActionManager;
import io.biblia.workflows.manager.action.ActionPersistance;
import io.biblia.workflows.manager.action.MongoActionPersistance;
import io.biblia.workflows.manager.dataset.DatasetManager;
import io.biblia.workflows.manager.dataset.DatasetPersistance;
import io.biblia.workflows.manager.dataset.MongoDatasetPersistance;
import io.biblia.workflows.manager.decision.ActionRollingWindow;
import io.biblia.workflows.manager.decision.DatasetLogDao;
import io.biblia.workflows.manager.decision.DecisionManager;
import io.biblia.workflows.manager.decision.MongoDatasetLogDao;
import io.biblia.workflows.manager.decision.MostCommonlyUsedDecisionAlgorithm;
import io.biblia.workflows.manager.action.CallbackManager;
import io.biblia.workflows.manager.decision.DecisionAlgorithm;

/**
 * Hello world!
 *
 */
public class App implements ConfigurationKeys {
    
	public static void main( String[] args ) {
		
		//0. Initialize mongo client
		MongoClient mongo = MongoClientBuilder.getMongoClient();
		
		//Shutdown hook up to clean all the states.
		Runtime.getRuntime().addShutdownHook(new Thread() {
    		@Override
    		public void run() {
    			try {
    				Thread.sleep(200);
    				System.out.println("Shutting down managers ...");
    				ActionManager.stop();
    				DatasetManager.stop();
    				mongo.close();
    			}
    			catch(InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	});
    	
    	
    	//1. Initialize the Action Manager thread
    	ActionPersistance aPersistance = new MongoActionPersistance(mongo);
    	ActionManager.start(aPersistance);
    	
    	
   
    	//2. Initialize the Dataset Manager thread
    	DatasetPersistance dPersistance = new MongoDatasetPersistance(mongo);
    	DatasetLogDao dLogDao = MongoDatasetLogDao.getInstance(mongo);
    	DatasetManager.start(dPersistance, dLogDao);
    	
    	//3. Initialize the Callback Manager thread
    	CallbackManager.start(aPersistance);
    	
    	/**
    	//4. Initialize the Decision Manager thread
    	ActionRollingWindow rollingWindow = ActionRollingWindow.getInstance(mongo);
    	DecisionAlgorithm alg = new MostCommonlyUsedDecisionAlgorithm();
    	DecisionManager.start(dPersistance, alg, rollingWindow);
    	
    	//4. Join all the initialized threads
    	ActionManager.join();
    	DatasetManager.join();
    	DecisionManager.join();
    	*/
    	mongo.close();
    }
	
}
