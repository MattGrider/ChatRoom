import java.io.IOException;
import java.net.ServerSocket;

class ConnectionThread extends Thread {
    server gui;
    String userName;
/*
 * esablishes connection thread for server allowing clients to connect
 */
    public ConnectionThread(server es3) {
        gui = es3;
        start();
    }

    /*
     * handles all connections for the server with the users
     */
    public void run()
    {
        gui.serverContinue = true;

        try
        {
            gui.serverSocket = new ServerSocket(0);
            gui.portInfo.setText("Listening on Port: " + gui.serverSocket.getLocalPort());
            System.out.println ("Connection Socket Created");
            try {
                while (gui.serverContinue)
                {
                    System.out.println ("Waiting for Connection");
                    gui.ssButton.setText("Stop Listening");
                    CommunicationThread CT = new CommunicationThread (gui.serverSocket.accept(), gui);
                    //gui.updateOnlineList();
                }
            }
            catch (IOException e)
            {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port: 10008.");
            System.exit(1);
        }
        finally
        {
            try {
                gui.serverSocket.close();
            }
            catch (IOException e)
            {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }
}