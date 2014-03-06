CoreNLP Sample using Gearman
---------------

As per your requirements, I've created a prototype project which iterate through the sample JSON file and send them to the Gearman job server. Output will be printed to console.


### Installation
Gearman comprised Client, Worker and Server. Client and Worker are built in Java while server can be installed as binary using `apt` under Ubuntu 12.04+.

#### Gearman daemon:
    
You can easily install gearman daemon under Ubuntu using Gearman's official PPA. Follow this instructions

    sudo apt-get install python-software-properties
    sudo apt-add-repository ppa:gearman-developers/ppa
    sudo apt-get update
    sudo apt-get install gearman-job-server gearman-tools
    
This will install job server and start it up.

#### Application:

Download latest binary: [corenlp-gearman-demo.jar][1]  
Download application dependency: [corenlp-gearman-demo_lib.zip][2].  
Stanford CoreNLP NER models are not included in the dependecy package. You can grab them from following sources and copy to same dependency directory i.e. `corenlp-gearman-demo_lib`

* [stanford-ner-models-1.3.5.jar][3]
* [stanford-postag-models-1.3.5.jar][4]


### Running on local machine

Before runing worker / client make sure that Gearman daemon is up and running.

#### Worker

Worker is resposible for picking up the task, processing it and sending result back to job server. You can start worker by the following command
    
    java -jar corenlp-gearman-demo.jar worker -h<Host> -p<Port>

* Host: Job server host address
* Port: Job server port

You can run as many worker as you want.

#### Client

Client will take JSON sample articles file as input, sending each article to job server, collect ouput processed by the worker and writing it to console. To start client run following command
    
    java -jar corenlp-gearman-demo.jar client -h<Host> -p<Port> -f<JsonFilePath>
    
sample:
    
    java -jar corenlp-gearman-demo.jar client -h127.0.0.1 -p4730 -f/tmp/articles.json
    
To collect output into file append ` > output.txt ` to above command

  [1]: https://bitbucket.org/inabhi9/uassign-corenlp-gearman-demo/downloads/corenlp-gearman-demo.jar
  [2]: https://bitbucket.org/inabhi9/uassign-corenlp-gearman-demo/downloads/corenlp-gearman-demo_lib.zip
  [3]: http://repo1.maven.org/maven2/edu/washington/cs/knowitall/stanford-corenlp/stanford-ner-models/1.3.5/stanford-ner-models-1.3.5.jar
  [4]: http://repo1.maven.org/maven2/edu/washington/cs/knowitall/stanford-corenlp/stanford-postag-models/1.3.5/stanford-postag-models-1.3.5.jar