package org.abhi9.gearman;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.gearman.Gearman;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;


public class Worker implements GearmanFunction {
	
	public static final String WORKER_FUNCTION_NAME = "nerer";
	public String workerId = null;
	private CoreNlp corenlp;
	
	public Worker(){
		this.corenlp = new CoreNlp();
		this.workerId = this.generateWorkerId();
	}
	
	public void work(String host, Integer port){
		// Gearman instance for worker
		Gearman gearman = Gearman.createGearman();
		
		// Gearman server on which it run on
		GearmanServer server = gearman.createGearmanServer(host, port);
		
		// Gearman worker instance
		GearmanWorker worker = gearman.createGearmanWorker();
		
		// Adding function to worker
		worker.addFunction(Worker.WORKER_FUNCTION_NAME, new Worker());
		
		// Registering worker to Gearman job server
		worker.addServer(server);
		
	}

	@Override
	public byte[] work(String function, byte[] data, GearmanFunctionCallback arg2)
			throws Exception {
		
		String startTime = this.getCurrentTime();
		// reading byte to string
		String raw = new String(data, "UTF-8");
		// extracting various info
		String[] request = raw.split("\\|", 2);
		String articleId = request[0];
		// parsing text and collecting NERs
		String ners = this.corenlp.parse(request[1]);
		// perparing output
		String f = 
				"Article Id: " + articleId + 
				", Start time: " + startTime + 
				", End time: " + this.getCurrentTime() +
				", Worker Id: " + this.workerId +
				", Number of NERs: " + String.valueOf(ners.split(",").length - 1) +
				", NER: " + ners;
		return f.getBytes();
	}
	
	private String getCurrentTime(){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private String generateWorkerId(){
		Random rand = new Random();
		int myRandomNumber = rand.nextInt(0x100000) + 0x10;
		return Integer.toHexString(myRandomNumber);
	}
	
	public static void main(String[] args){
		Worker worker = new Worker();
    	worker.work("localhost", 4730);
	}
}