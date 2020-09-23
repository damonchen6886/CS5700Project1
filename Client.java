import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    int port;
    String server;
    String NUID;
    String messageHeader;
    boolean SSL;
    public Client(){
        this.port = 27995;
        this.server = "cs5700fa20.ccs.neu.edu";
        this.NUID = "001969763";
        this.messageHeader = "cs5700fall2020";
        this.SSL = false;

    }
    public void setPort(String port){
        try {
            int p = Integer.parseInt(port);
            this.port = p;
            System.out.println("Port has been set to " + p);
        }catch(NumberFormatException e){
            System.err.println("the port should be number");
        }


    }
    public void setSSL(boolean SSL){
        this.SSL = SSL;
        System.out.println("SSL has been set to " + SSL);
    }

    public void setNUID(String NUID) {
        System.out.println("NUID has been set to " + NUID);
        this.NUID = NUID;
    }

    public void setServer(String server) {
        System.out.println("Host has been set to " + server);
        this.server = server;
    }

    public void createConnection(){

        Socket socket;
        // read input (comes in)
        BufferedReader bufferedReader;
        // read output (send out)
        PrintWriter printWriter;
        try{
//             SSL socket
            if(SSL){

                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                socket = (SSLSocket) sslSocketFactory.createSocket(this.server,this.port);
                System.out.println("SSL socket initialized, port is "+ this.port);


            }
            else{

                socket = new Socket(this.server,this.port);
                System.out.println("regular socket initialized");
            }
            System.out.println("connected");
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            String secretFlag;

            printWriter = new PrintWriter(out,true);
            bufferedReader =  new BufferedReader(new InputStreamReader(in));

            // first message:
            printWriter.println(messageHeader+ " HELLO " + NUID + "\n");
            System.out.println("Sending message to server :" + messageHeader+ " HELLO " + NUID);
            printWriter.flush();

            // Second message: receive math expression:
            String message = bufferedReader.readLine();
            System.out.println("Server response message is :" + message);
            String[] splitMessage= message.split(" ");
            // validate the message received from server
            while(validateResponse(splitMessage)){

                int mathResult = computeResult(splitMessage);

                //third message:
                System.out.println("Calculated math result is :" + mathResult);
                System.out.println("sending math result to server :" + messageHeader + " " + mathResult);
                printWriter.println(messageHeader + " " + mathResult);
                printWriter.flush();
                // update the new message
                message= bufferedReader.readLine();
                System.out.println("Server repose message is :" + message);
                splitMessage = message.split(" ");
            }
            // last message
            if(checkByeMessage(splitMessage)){
                secretFlag = splitMessage[1];
                System.out.println("secretFlag is :" + secretFlag);
                System.out.println("exiting");
                bufferedReader.close();
                printWriter.close();
                socket.close();
            }

        } catch (UnknownHostException e) {
            System.err.println("Server does not recognized" + server);
            System.exit(1);
        }catch(SSLException e){
            e.printStackTrace();
            System.err.println("ssl error");
            System.exit(2);
        }
        catch (IOException e) {
            System.err.println("IO error");
            System.exit(3);
        }

    }

    private int computeResult(String[] message){

        String operator = message[3];
        System.out.println("Integer.parseInt(message[2]) = "+ Integer.parseInt(message[2]) );
        System.out.println("Integer.parseInt(message[2]) = "+ Integer.parseInt(message[4]) );
        System.out.println("operator = " + operator);

        if(operator.equals("+")){
            return Integer.parseInt(message[2]) + Integer.parseInt(message[4]);
        }
        if(operator.equals("-")){
            return Integer.parseInt(message[2]) - Integer.parseInt(message[4]);
        }
        if(operator.equals("*")){
            return Integer.parseInt(message[2]) * Integer.parseInt(message[4]);
        }
        if(operator.equals("/")){
            return Integer.parseInt(message[2]) / Integer.parseInt(message[4]);
        }
        return -1;

    }

    /**
     * check if the server's response message is a math expression
     * @param response  split response messages, separated by space
     * @return boolean indicate if it is a math expression
     */
    private boolean validateResponse(String[] response){

        return response[1].equals("STATUS") && response.length == 5;
    }

    private boolean checkByeMessage(String[] response){
        return response[2].equals("BYE");

    }
    private boolean validateCommandLine(String[] args){
        return args.length>5 || args.length <2;
    }


    public static void main(String[] args) {
        //TODO: add SSL
        Client c= new Client();
        int argumentLength = args.length;
        //validated input
        if(c.validateCommandLine(args)){
            System.err.println("Invalid input, please check your command input");
        }
        // set files based on given parameter
        String port = "-1";
        for(int i =0; i < argumentLength;i++){
            if(args[i].equals("-p")){

                c.setPort(args[i+1]);
                System.out.println("args[i+1]"+ args[i+1]);
                port = args[i+1];
            }
            else if(args[i].equals("-s")) {
                c.setSSL(true);
                // if port does not specified, set default ssl port 27996;
                if(port.equals("-1")){
                    c.setPort("27996");
                }
            }
        }
        c.setServer(args[argumentLength-2]);
        c.setNUID(args[argumentLength-1]);
        c.createConnection();

    }
}

