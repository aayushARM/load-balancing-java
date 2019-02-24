import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Client {

    public static void main(String[] args) {
        try {
            while (true){
                Socket loadBalancerSocket = new Socket("localhost", 12345);
                Thread requestSender = new Thread(new RequestSender(loadBalancerSocket));
                requestSender.start();
                // To clearly observe print statements.
                //Thread.sleep(80);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class RequestSender implements Runnable{
    private Socket loadBalancerSocket;
    RequestSender(Socket loadBalancerSocket){
        this.loadBalancerSocket = loadBalancerSocket;
    }
    @Override
    public void run() {
        try {
            BufferedWriter lbWriter = new BufferedWriter(new OutputStreamWriter(loadBalancerSocket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader lbReader = new BufferedReader(new InputStreamReader(loadBalancerSocket.getInputStream(), StandardCharsets.UTF_8));

            int sid = new Random().nextInt(7) + 1;
            lbWriter.write(sid + "\n");
            lbWriter.flush();

            String jsonString = lbReader.readLine();
            JSONObject json = new JSONObject(jsonString);
            String result = "Information received for Student with ID="+sid+":"+
                            "\nName: "+json.getString("name")+
                            "\nDate of Birth: "+json.getString("dob")+
                            "\nMajor of Study: "+json.getString("major")+
                            "\nEducation Level: "+json.getString("level")+
                            "\nYear of Study: "+json.getString("year");
            System.out.println(result+"\n\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
