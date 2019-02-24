import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Worker {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/students", "root", "aayush119tg");
            ServerSocket workerSocket = new ServerSocket(Integer.valueOf(args[0]));
            while(true){
                Socket loadBalancerSocket = workerSocket.accept();
                Thread workerTask = new Thread(new WorkerTask(loadBalancerSocket, conn));
                workerTask.start();
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}