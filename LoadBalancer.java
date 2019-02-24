import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LoadBalancer {
    private static void startLoadBalancer(String schedAlgo) {
        try {
            // List of WorkerInfo objects. WorkerInfo class has two fields: host, port.
            ArrayList<WorkerInfo> workers = new ArrayList<>();
            BufferedReader workerFile = new BufferedReader(new FileReader(new File("worker_list.txt")));

            // Populate worker list from worker_list.txt.
            while (workerFile.read() != -1) {
                String[] info = workerFile.readLine().split(",");
                workers.add(new WorkerInfo(info[0], Integer.valueOf(info[1])));
            }

            // WorkerLoads object consists of a list of worker loads, one int load for each worker.
            WorkerLoads workerLoads = new WorkerLoads(workers.size());

            // Open Load Balancer Socket. This socket acts as a single entry point for all incoming request from Clients.
            ServerSocket balancerSocket = new ServerSocket(12345);
            int currentWorker = 0;
            while (!Thread.interrupted()) {

                // Accept a new client connection.
                Socket clientSocket = balancerSocket.accept();
                if (schedAlgo.equals("RR")) {
                    // When Round Robin" selected, select Workers in a circular fashion.
                    currentWorker = (currentWorker + 1) % workers.size();
                    System.out.println("Selected worker " + currentWorker + ".");
                }
                else if (schedAlgo.equals("LC")) {
                    // When Least Connections selected, select Worker with least active connections/requests, and
                    // increment its load.
                    currentWorker = workerLoads.getMinLoadServer();
                    int currLoad = workerLoads.getLoad(currentWorker);
                    System.out.println("Selected worker " + currentWorker + " with load: " + currLoad + ".");
                    workerLoads.incrementLoad(currentWorker);
                }

                // Open connection to selected worker.
                Socket workerSocket = new Socket(workers.get(currentWorker).getHost(), workers.get(currentWorker).getPort());

                // Start a new thread to serve this request.
                Thread lbRequestServer = new Thread(new LBRequestServer(clientSocket, workerSocket, workerLoads, currentWorker));
                lbRequestServer.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // args[0] has the scheduling algorithm parameter(RR and LC, for Round-Robin and Least-Connections respectively).
        String schedAlgo = args[0];
        startLoadBalancer(schedAlgo);
    }
}


class WorkerLoads {
    private ArrayList<Integer> workerLoads = new ArrayList<>();

    WorkerLoads(int num_servers) {
        // Initialize loads of all workers to 0.
        for (int i = 0; i < num_servers; i++)
            workerLoads.add(0);
    }

    int getLoad(int index){
        return workerLoads.get(index);
    }

    // Find worker with minimum load.
    synchronized int getMinLoadServer() {
        int minLoad = workerLoads.get(0), min_ind = 0;
        for (int i = 1; i < workerLoads.size(); i++) {
            int thisLoad = workerLoads.get(i);
            if (thisLoad < minLoad) {
                minLoad = thisLoad;
                min_ind = i;
            }
        }
        return min_ind;
    }

    synchronized void incrementLoad(int index){
        workerLoads.set(index, workerLoads.get(index) + 1);
    }

    synchronized void decrementLoad(int index){
        workerLoads.set(index, workerLoads.get(index) - 1);
    }

}