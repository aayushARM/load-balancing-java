import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LoadBalancer {

    private static void startLoadBalancer(String schedAlgo) {
        try {
            ArrayList<WorkerInfo> workers = new ArrayList<>();
            BufferedReader workerFile = new BufferedReader(new FileReader(new File("worker_list.txt")));
            while (workerFile.read() != -1) {
                String[] info = workerFile.readLine().split(",");
                workers.add(new WorkerInfo(info[0], Integer.valueOf(info[1])));
            }
            WorkerLoads workerLoads = new WorkerLoads(workers.size());

            ServerSocket balancerSocket = new ServerSocket(12345);
            int currentServer = 0;
            while (!Thread.interrupted()) {
                Socket clientSocket = balancerSocket.accept();
                if (schedAlgo.equals("RR"))
                    currentServer = (currentServer + 1) % workers.size();
                else if (schedAlgo.equals("LC")) {
                    currentServer = workerLoads.getMinLoadServer();
                    int newLoad = workerLoads.getLoad(currentServer);
                    System.out.println("Current load on server " + currentServer + ": " + newLoad);
                    workerLoads.incrementLoad(currentServer);
                }
                Socket workerSocket = new Socket(workers.get(currentServer).getHost(), workers.get(currentServer).getPort());
                Thread lbRequestServer = new Thread(new LBRequestServer(clientSocket, workerSocket, workerLoads, currentServer));
                lbRequestServer.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String schedAlgo = args[0];
        startLoadBalancer(schedAlgo);
    }
}


class WorkerLoads {
    private ArrayList<Integer> workerLoads = new ArrayList<>();

    WorkerLoads(int num_servers) {
        for (int i = 0; i < num_servers; i++)
            workerLoads.add(0);
    }

    int getLoad(int index){
        return workerLoads.get(index);
    }

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