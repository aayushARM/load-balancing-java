import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Worker {
    public static void main(String[] args) {
        try {
            // Load MySQL JDBC Driver.
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/students", "root", "xxxxxx");

            // Open socket for this worker.
            ServerSocket workerSocket = new ServerSocket(Integer.valueOf(args[0]));
            while(true){
                // Accept connection from Load Balancer.
                Socket loadBalancerSocket = workerSocket.accept();

                // Start a new thread to service this request.
                Thread workerTask = new Thread(new WorkerTask(loadBalancerSocket, conn));
                workerTask.start();
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
