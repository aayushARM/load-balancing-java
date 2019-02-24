#!/bin/bash
javac -cp ./jars/json-20180813.jar:./jars/mysql-connector-java-8.0.15.jar *.java
count=0
while IFS=, read _ port;do
	gnome-terminal --title="Worker $count" -e "bash -c 'java -cp ./jars/json-20180813.jar:./jars/mysql-connector-java-8.0.15.jar:./ Worker $port'"
	count=$((count+1))
done < worker_list.txt	
sleep 0.5s
#Commandline parameter options for LoadBalancer: "RR" for Round Robin scheduling, "LC" for Least Connections scheduling.
gnome-terminal --title="Load Balancer" -e "bash -c 'java -cp ./jars/json-20180813.jar:./jars/mysql-connector-java-8.0.15.jar:./ LoadBalancer LC'" 
gnome-terminal --title="Client" -e "bash -c 'java -cp ./jars/json-20180813.jar:./jars/mysql-connector-java-8.0.15.jar:./ Client'"
