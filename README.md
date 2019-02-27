# Application-level Load Balancer in Java #
This project implements a simple load balancing program written in Java, with a Client and a Server program demonstrating its functionality. The functionality is as below:
* All clients connect to the Load Balancer, which is a single entry point for all requests. The requests consist of a string- an ID- the data corresponding to which is to be obtained from Workers and displayed on terminal.
* The Load Balancer accepts requests from clients and redirects them to appropriate workers selected using Round-Robin or Least-Connections scheduling, whichever is specifed by user via command-line.
* The Workers(Servers) extract information corresponding to recieved requests(IDs) from a MySQL Database, and return the data in the form of JSON responses to the Load Balancer, which forwards it to the clients as mentioned above.
* The Client, Load Balancer and Workers are all Multithreaded, and hence capable of Sending/Serving multiple requests at once.

#### To Dos: ####
* Add multiple, more complex requests/tasks.
* Add functionality to forward requests to workers based on their priority(high/medium/low).
## Steps to replicate for anyone interested: ##
* Prerequisites:
  * JDK 8 or above
  * MySQL Server(+ MySQL Workbench recommended)
  * Necessary Jar files(included in /jars/ directory)
 * Import students.sql file to replicate the schema and table used with project. If using MySQL Workbench: Server->Data Import-> Import from Self-Contained file.
 * The hostname:port pairs are extracted from worker_list.txt, modify if needed. The scheduling algorithm is given as a command-line argument to LoadBalancer.class file. To launch all processes:
   * If using a linux distro with gnome-terminal, just execute the included bash script launch_all.sh. 
   * If using other terminal(ex. Xterm, Konsole), replace "gnome-terminal" with "terminal_name" everywhere(should work with most terminals). 
   * If not using linux, manually copy and paste all javac and java commands from launch_all.sh into terminal. Note: the port numbers must match those in worker_list.txt.
   * In all cases, modify the command-line argument for LoadBalancer as needed. Valid values: "RR" for Round-Robin and "LC" for Least Connections.
 * Once launched, windows corresponding to Workers, Load Balancer, and Client will show appropriate outputs on terminal.
  
  
 
