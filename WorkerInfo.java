public class WorkerInfo {
    private int port;
    private String host;
    WorkerInfo(String host, int port){
        this.host = host;
        this.port = port;
    }
    String getHost(){
        return host;
    }
    int getPort(){
        return port;
    }
}
