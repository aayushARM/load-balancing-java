import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONObject;

public class WorkerTask implements Runnable {
    // Table column names.
    private static String[] columns = {"name", "dob", "major", "level", "year"};
    private Socket loadBalancerSocket;
    private Connection conn;
    WorkerTask(Socket loadBalancerSocket, Connection conn){
        this.loadBalancerSocket = loadBalancerSocket;
        this.conn = conn;
    }

    @Override
    public void run() {
        try {
            BufferedWriter lbWriter = new BufferedWriter(new OutputStreamWriter(loadBalancerSocket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader lbReader = new BufferedReader(new InputStreamReader(loadBalancerSocket.getInputStream(), StandardCharsets.UTF_8));

            // Get student ID sent by client(via Load Balancer).
            String sid = lbReader.readLine();

            // Query student data from database.
            String query = "SELECT name, dob, major, level, year FROM studentinfo WHERE sid="+sid;
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.first();

            // Create json from data.
            JSONObject json = new JSONObject();
            for(int i=0; i<5; i++)
                json.put(columns[i], rs.getString(i+1));
            rs.close();
            System.out.println("Sending info for Student with ID: "+sid);

            // Send json response to load balancer.
            lbWriter.write(json.toString()+"\n");
            lbWriter.flush();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
