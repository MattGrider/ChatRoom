import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class server extends JFrame implements ActionListener{

    // GUI items
    JButton ssButton;
    JLabel machineInfo;
    JLabel portInfo;
    JTextArea history;
    JTextArea onlineBox;
    private boolean running;
    static OnlineList online = new OnlineList();

    // Network Items
    boolean serverContinue;
    ServerSocket serverSocket;

   /*
    * set up GUI
    */
    public server()
    {
        super( "Echo Server" );

        // get content pane and set its layout
        Container container = getContentPane();
        container.setLayout( new BorderLayout() );

        // create buttons
        JPanel upper = new JPanel();
        upper.setLayout(new GridLayout(1, 2));
        container.add(upper, BorderLayout.NORTH);

        running = false;
        ssButton = new JButton( "Start Listening" );
        ssButton.addActionListener( this );
        upper.add( ssButton);

        //online
        onlineBox = new JTextArea(10, 40);
        onlineBox.setEditable(false);
        JScrollPane ChatRoom = new JScrollPane(onlineBox);//
        container.add(ChatRoom, BorderLayout.WEST);


        String machineAddress = null;
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            machineAddress = addr.getHostAddress();
        }
        catch (UnknownHostException e)
        {
            machineAddress = "127.0.0.1";
        }
        machineInfo = new JLabel (machineAddress);
        upper.add( machineInfo );
        portInfo = new JLabel (" Not Listening ");
        upper.add( portInfo );

        history = new JTextArea ( 10, 40 );
        history.setEditable(false);
        container.add( new JScrollPane(history), BorderLayout.EAST );

        setSize( 750, 1000 );
        setVisible( true );

    } // end CountDown constructor

    public void sendToAll(String message)
    {
        for(CommunicationThread CT: online.onlineList) {
            CT.display(message);
        }
    }

    /*
     * Updates username to list of users
     */
    public synchronized void updateUserName(){
        ArrayList<String> list = (ArrayList<String>) online.getUserNameList().clone();
        for(CommunicationThread CT: online.onlineList){
            CT.display(list);
        }
    }
    
    /*
     * recieves message from client
     */
    public void sendToSome(String user, String message) {
        String delims = "[ ]";
        String prefix = "@";
        String[] tokens = message.split(delims);
        ArrayList<String> users = new ArrayList();

        String newMessage = "";
        boolean flag = true;
        for (String s : tokens) {
            if (s.startsWith("@") && flag == true) {
                s=(s.substring(s.indexOf(prefix)+prefix.length()));
                if(online.contains(s))
                    users.add(s);
                else
                    System.out.println("user does not exist");
            }
            else // first time token does not start with @ break out user loop
            {
                flag = false;
                newMessage = user + newMessage + ": " + s;
            }
        }

        for (String s2 : users) {
            CommunicationThread temp = online.getUserThread(s2);
            System.out.println(temp.getUserName());
            temp.display(newMessage);
        }

    }

    
    /*
     * Updates the list of users connected to the server
     */
    public synchronized void updateOnlineList()
    {
        onlineBox.setText("");
        for (CommunicationThread CT: online.onlineList) {
            onlineBox.insert(CT.getUserName() + "\n", 0);
        }
    }



    public static void main( String args[] )
    {
        server application = new server();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

   /*
    * handle button event
    */
    public void actionPerformed( ActionEvent event )
    {
    	//start server
        if (running == false)
        {
            new ConnectionThread (this);
        }
        //turn off server
        else
        {
            serverContinue = false;
            ssButton.setText ("Start Listening");
            portInfo.setText (" Not Listening ");
        }
    }


} // end class server