package org.abhi9.gearman;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobEventCallback;
import org.gearman.GearmanJoin;
import org.gearman.GearmanServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Client implements GearmanJobEventCallback<String>{
	
	Logger outputLogger = Logger.getLogger(Client.class.getName());
	
	public Integer createJobs(String filename, String host, Integer port) {
		
		JSONParser parser = new JSONParser();
		Gearman gearman = Gearman.createGearman();
		GearmanJoin<String> join = null;
		int cnt = 0; // jobs counter
		
        // The client is used to submit requests the job server.
        GearmanClient client = gearman.createGearmanClient();
        /* 
         * Create the job server object. This call creates an object represents a remote job server. 
         * A job server receives jobs from clients and distributes them to registered workers.
         */
        GearmanServer server = gearman.createGearmanServer(host, port);

        // Tell the client that it may connect to this server when submitting jobs
        client.addServer(server);
		 
        // feeding jobs to gearman from JSON file
		try {
			// reading and parsing file
			Object obj = parser.parse(new FileReader(filename));
			
			// json file contains array of json objects
			JSONArray article = (JSONArray) obj;
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = article.iterator();
			
			// iterating json array
			while (iterator.hasNext()) {
				cnt++;
				String articleId = String.valueOf(cnt);
				String description = (String) iterator.next().get("description");
				join = client.submitJob(
						Worker.WORKER_FUNCTION_NAME, (articleId + "|" + description).getBytes(),
						Worker.WORKER_FUNCTION_NAME, new Client());
			}
			
			// Block the current thread until all events have been processed.
			join.join();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			
		}
        

        /*
         * Close the gearman service after it's no longer needed. (closes all
         * sub-services, such as the client)
         * 
         * It's suggested that you reuse Gearman and GearmanClient instances
         * rather recreating and closing new ones between submissions
         */
		try{
			gearman.shutdown();
		} catch (Exception e) {}
		
        return cnt;
	}

	/**
     * This method is called by the client when an event is received
     */
	@Override
	public void onEvent(String attachment, GearmanJobEvent event) { 
        
        switch (event.getEventType()) {
        	// Job completed successfully
	        case GEARMAN_JOB_SUCCESS:
	        	outputLogger.info(new String(event.getData()));
	        	break;
	        // The job submit operation failed
	        case GEARMAN_SUBMIT_FAIL:
	        // The job's execution failed
	        case GEARMAN_JOB_FAIL:
	        	System.err.println(event.getEventType() + ": "
	        							+ new String(event.getData()));
	        default:
        }
	}
	
	public static void main(String[] args){
    	Client client = new Client();
    	client.createJobs("/tmp/article_dump.json", "localhost", 4730);
	}
}
