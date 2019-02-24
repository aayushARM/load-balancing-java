import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class LBRequestServer implements Runnable{
    private Socket clientSocket, workerSocket;
    private WorkerLoads workerLoads;
    private int currentServer;
    LBRequestServer(Socket clientSocket, Socket workerSocket, WorkerLoads workerLoads, int currentServer){
        this.clientSocket = clientSocket;
        this.workerSocket = workerSocket;
        this.workerLoads = workerLoads;
        this.currentServer = currentServer;
    }

    @Override
    public void run() {
        try {
            BufferedWriter workerWriter = new BufferedWriter(new OutputStreamWriter(workerSocket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader workerReader = new BufferedReader(new InputStreamReader(workerSocket.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

            // Take request from client and forward to selected worker.
            workerWriter.write(clientReader.readLine()+"\n");
            workerWriter.flush();

            // Take response from selected worker and send to client.
            clientWriter.write(workerReader.readLine()+"\n");
            clientWriter.flush();

            workerSocket.close();
            clientSocket.close();

            // Request processed, decrement load of selected worker.
            workerLoads.decrementLoad(currentServer);

        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}
