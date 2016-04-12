import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CommunicationThread extends Thread {
    // private variables
    Socket clientSocket;
    static private server gui;
    boolean first = true;
    private String userName;
    ObjectOutputStream objOut;
    ObjectInputStream objIn;

    /*
     * creates new thread that sets up connection for server 
     */
    public CommunicationThread(Socket clientSoc, server ec3) {
        clientSocket = clientSoc;
        gui = ec3;
        try {
            objIn = new ObjectInputStream(clientSocket.getInputStream());
            objOut = new ObjectOutputStream(clientSocket.getOutputStream());
        }
        catch(IOException e){

        }
        start();

    }

    /*
     * performs all functions of the the server with respect to messages and usernames
     */
    public void run() {
        System.out.println("New Communication Thread Started");

        try {

            String inputLine;
            boolean exists = false;
            while (true) {
                try{
                    inputLine = (String) objIn.readObject();
                }
                catch(IOException e){
                    break;
                }
                catch(ClassNotFoundException e2){
                    break;
                }

                if (first == true) {
                    System.out.println("User entered: " + inputLine);



                    while(gui.online.contains(inputLine))
                    {
                       objOut.writeObject("Usenrame is taken, please try again");
                        exists = true;
                        break;
                    }
                    if(exists == true)
                        break;

                    setUserName(inputLine);
                    String s =  inputLine + " has connected to the Chatroom";
                    gui.online.add(this);
                    gui.sendToAll(s);
                    first = false;
                    gui.online.printOnline();
                    gui.updateUserName();
                    gui.updateOnlineList();
                }
                else {
                    if (inputLine.startsWith("@"))
                        gui.sendToSome(userName, inputLine);
                    else
                        gui.sendToAll(userName + ": " + inputLine);

                    System.out.println("Server: " + inputLine);
                    gui.history.insert(userName + ": " + inputLine + "\n", 0);


                    if (inputLine.equals("End Server."))
                        gui.serverContinue = false;

                }
            }

            objOut.close();
            objIn.close();
            if(!exists)
                gui.sendToAll(this.userName + " left the room");
            gui.online.remove(this);
            gui.online.printOnline();
            gui.updateUserName();

            clientSocket.close();
        } catch (IOException e) {
            gui.sendToAll(this.userName + " left the room");
            System.err.println("Problem with Communication Server");
            gui.online.remove(this);
            gui.online.printOnline();
            gui.updateOnlineList();
            gui.updateUserName();
        }
    }

    
    /*
     * prints sent message
     */
    public void display(String str){
        try{
            objOut.writeObject(str);
        }
        catch(IOException e){
            System.out.println("Can not send to this user");
        }
    }
    
    /*
     * shows users on line 
     */
    public void display(ArrayList<String> arr){
        try{
            System.out.println("List of people in the chatroom: ");;
            for(String s: arr){
                System.out.println(s);
            }
            objOut.writeObject(arr);
        }
        catch(IOException e){
            System.out.println("Can not send to this user");
        }
    }
    
    //gets the username of a connected user
    public String getUserName() {
        return userName;
    }

    //sets a username of a connected user
    public void setUserName(String userName) {
        this.userName = userName;
    }
}