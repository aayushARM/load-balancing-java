#!/bin/bash

echo "Compiling files..."
javac -cp ./jars/json-20180813.jar:./jars/mysql-connector-java-8.0.15.jar *.java

echo "Starting Worker processes..."
count=0
while IFS=, read _ port;do
	gnome-terminal --title="Worker $count" -e "bash -c 'java -cp ./jars/json-20180813.jar:./jars/mysql-connector-java-8.0.15.jar:./ Worker $port'" 1>/dev/null
	count=$((count+1))
done < worker_list.txt	

# Note: Small delay needed so as to let Workers launch completely and open Server Sockets.
sleep 0.5s

echo "Starting Load Balancer process..."
#Commandline parameter options for LoadBalancer: "RR" for Round Robin scheduling, "LC" for Least Connections scheduling.
gnome-terminal --title="Load Balancer" -e "bash -c 'java -cp ./jars/json-20180813.jar:./jars/mysql-connector-java-8.0.15.jar:./ LoadBalancer LC'" 1>/dev/null 
echo "Starting Client process..."
gnome-terminal --title="Client" -e "bash -c 'java -cp ./jars/json-20180813.jar:./jars/mysql-connector-java-8.0.15.jar:./ Client'" 1>/dev/null
echo "All processes running."
