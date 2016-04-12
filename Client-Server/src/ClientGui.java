import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientGui extends JFrame implements ActionListener{
    // GUI items
    ArrayList<String> userList;
    JButton sendButton;
    JButton connectButton;
    JTextField machineInfo;
    JTextField portInfo;
    JTextField message;
    JTextField userName;
    JTextArea history,userNameList;

    // Network Items
    boolean connected;
    Socket echoSocket;
    ObjectOutputStream out;
    ObjectInputStream in;

    boolean firstTime = true;

    // set up GUI
    public ClientGui()
    {    	
    	
        super( "Twitch Chat 3.0 VapeNation" );    	
        
        userList = new ArrayList<>();
        // get content pane and set its layout
        Container container = getContentPane();
        container.setLayout (new BorderLayout ());
        container.setBackground(Color.BLACK);

        // set up the North panel
        JPanel upperPanel = new JPanel ();
        upperPanel.setBackground(Color.DARK_GRAY);
        upperPanel.setForeground(Color.WHITE);
        upperPanel.setLayout (new GridLayout (5,2));
        container.add (upperPanel, BorderLayout.NORTH);

        // create buttons
        connected = false;

        JLabel temp = new JLabel ("UserName: ", JLabel.RIGHT);
        temp.setForeground(Color.LIGHT_GRAY);
        
        upperPanel.add ( temp );
        userName = new JTextField ("");
        userName.addActionListener( this );
        upperPanel.add( userName );

        sendButton = new JButton( "Send Message" );
        sendButton.addActionListener( this );
        sendButton.setEnabled (false);
        upperPanel.add( sendButton );

        connectButton = new JButton( "Connect to Server" );
        connectButton.addActionListener( this );
        upperPanel.add( connectButton );

        temp = new JLabel ("Server Address: ", JLabel.RIGHT);
        temp.setForeground(Color.LIGHT_GRAY);
        
        upperPanel.add ( temp );
        machineInfo = new JTextField ("");
        upperPanel.add( machineInfo );

        temp = new JLabel ("Server Port: ", JLabel.RIGHT);
        temp.setForeground(Color.LIGHT_GRAY);
        
        upperPanel.add ( temp );
        portInfo = new JTextField ("");
        upperPanel.add( portInfo );

        temp = new JLabel("Message: ", JLabel.RIGHT);
        temp.setForeground(Color.LIGHT_GRAY);
        
        upperPanel.add(temp);
        message = new JTextField ("");
        message.addActionListener( this );
        message.setEditable(false);
        upperPanel.add( message );

        history = new JTextArea ( 10, 40 );
        history.setEditable(false);
        container.add( new JScrollPane(history) ,  BorderLayout.CENTER);

        userNameList = new JTextArea(10,15);
        userNameList.setEditable(false);
        container.add( new JScrollPane(userNameList), BorderLayout.EAST);

        

    	
    	sendButton.setBackground(Color.DARK_GRAY);
    	sendButton.setForeground(Color.LIGHT_GRAY);
    	connectButton.setBackground(Color.DARK_GRAY);
    	connectButton.setForeground(Color.LIGHT_GRAY);
    	machineInfo.setBackground(Color.DARK_GRAY);
    	machineInfo.setForeground(Color.LIGHT_GRAY);
    	portInfo.setBackground(Color.DARK_GRAY);
    	portInfo.setForeground(Color.LIGHT_GRAY);
    	message.setBackground(Color.DARK_GRAY);
    	message.setForeground(Color.LIGHT_GRAY);
    	userName.setBackground(Color.DARK_GRAY);
    	userName.setForeground(Color.LIGHT_GRAY);
    	history.setBackground(Color.BLACK);
    	history.setForeground(Color.LIGHT_GRAY);
    	userNameList.setBackground(Color.BLACK);
    	userNameList.setForeground(Color.LIGHT_GRAY);
    	
        
        setSize( 650, 448 );
        setResizable(false);
        setVisible( true );

    } // end CountDown constructor

    public static void main( String args[] )
    {
        ClientGui application = new ClientGui();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    // handle button event
    public void actionPerformed( ActionEvent event )
    {
        if ( connected &&(event.getSource() == sendButton ||event.getSource() == message )){
            doSendMessage(message.getText());
        }
        else if (event.getSource() == connectButton){
            doManageConnection();
        }
    }

    /*
     * sends a message to the server
     */
    public void doSendMessage(String str)
    {
        try {
            out.writeObject(str);
            message.setText("");
        }
        catch(IOException e){

        }
    }

    
    /*
     * sets up connection or turns off connection
     */
    public void doManageConnection()
    {
        if (connected == false)
        {
            history.setText("");
            String machineName = null;
            String user = null;
            int portNum = -1;
            //turns on connection
            try {
                machineName = machineInfo.getText();
                portNum = Integer.parseInt(portInfo.getText());
                user = userName.getText();
                echoSocket = new Socket(machineName, portNum );
                out = new ObjectOutputStream(echoSocket.getOutputStream());
                in = new ObjectInputStream(echoSocket.getInputStream());
                doSendMessage(user);
                sendButton.setEnabled(true);
                connected = true;
                connectButton.setText("Disconnect from Server");
                new listenServer();
                message.setEditable(true);
                userName.setEditable(false);
            } catch (NumberFormatException e) {
                history.insert ( "Server Port must be an integer\n", 0);
            } catch (UnknownHostException e) {
                history.insert("Don't know about host: " + machineName , 0);
            } catch (IOException e) {
                history.insert ("Couldn't get I/O for "
                        + "the connection to: " + machineName , 0);
            }

        }
        //turns off connection
        else{
            try
            {
                message.setEditable(false);
                userName.setEditable(true);
                out.close();
                in.close();
                echoSocket.close();
                sendButton.setEnabled(false);
                connected = false;
                connectButton.setText("Connect to Server");
            }
            catch (IOException e)
            {
                history.insert ("Error in closing down Socket ", 0);
            }
        }
    }
    //adds words to text box
    public void append(String s){
        history.append(s+"\n");
        history.setCaretPosition(history.getText().length() - 1);
    }
    //adds names to user list
    public void parseUserList(ArrayList<String> userList){
        userNameList.setText("");
        for(String s : userList){
            userNameList.append(s+"\n");
        }
    }
    
    /*
    * new thread that listens for server info like new messages and userupdates
    */
    class listenServer extends Thread{
        public listenServer(){
            start();
        }
        public void run(){
            while(true){
                Object o;
                try{
                    o = in.readObject();
                    if(o instanceof String){
                        String message = (String) o;
                        append(message);
                    }
                    else if(o instanceof ArrayList){
                        ArrayList<String> list = (ArrayList<String>) o;
                        parseUserList(list);
                        System.out.println("AGHAHAHAHAHAH Updating on line List\n");
                        for(String s: list){
                            System.out.println(s);
                        }
                    }

                }
                catch(IOException e){
                    append("Your connection has been closed " + e);
                    message.setEditable(false);
                    userName.setEditable(true);
                    sendButton.setEnabled(false);
                    connected = false;
                    connectButton.setText("Connect to Server");
                    break;
                }
                catch(ClassNotFoundException e2){}
            }
        }
    }
}