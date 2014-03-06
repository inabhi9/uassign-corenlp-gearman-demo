package org.abhi9.gearman;

import java.io.PrintStream;
import org.abhi9.gearman.Worker;

public class Runner {
	
	public static void main(String[] args){
		
		// Default values
		String host = "localhost";
		Integer port = 4730;
		String module = new String();
		String filename = null;
		
		// Parsing arguments
		for (String arg : args) {
            if (arg.startsWith("-h")) {
                host = arg.substring(2);
            } else if (arg.startsWith("-p")) {
                port = Integer.parseInt(arg.substring(2));
            // filename for Client - job sumitter
            } else if (arg.startsWith("-f")) {
                filename = arg.substring(2);
            } else if (arg.charAt(0) == '-') {
                usage(System.out);
            } else if (arg.trim().equalsIgnoreCase("worker")){
            	module = arg.trim();
            } else if (arg.trim().equalsIgnoreCase("client")){
            	module = arg.trim();
            }
		}
		
		// Starting modules
		if (module.equalsIgnoreCase("worker")){
			System.out.println("Starting worker...");
        	Worker worker = new Worker();
        	worker.work(host, port);
		} else if (module.equalsIgnoreCase("client")){
			System.out.println("Running client...");
        	Client client = new Client();
        	int jobs = client.createJobs(filename, host, port);
        	System.out.println(jobs + " processed successfully");
		} else {
			usage(System.out);
		}
	}
	
	public static void usage(PrintStream out) {
        String[] usage = {
            "usage: corenlp-gearman-demo.jar [-h<host>] [-p<port>] " +
                    "moduleName",
            "\t-h<host> - job server host",
            "\t-p<port> - job server port",
            "\n\tExample: java -jar corenlp-gearman-demo.jar " +
                    "worker",
            "\tExample: java -jar corenlp-gearman-demo.jar " +
            "-h127.0.0.1 -p4730 client",
        };

        for (String line : usage) {
            out.println(line);
        }
    }
}
