import java.net.Socket;
import java.util.ArrayList;

public class OnlineList  {
    public ArrayList<CommunicationThread> onlineList;
    static private ArrayList<String> userNameList;
    private int size;

    /*
     * manages all things about username list for the server
     */
    public OnlineList(){
        onlineList = new ArrayList(); //empty array list of users online
        userNameList = new ArrayList<>();
        size = 0;

    }

    //adds a new user
    public void add(CommunicationThread newUser)
    {
        onlineList.add(newUser);
        userNameList.add(newUser.getUserName());
        size++;
    }

    //checks if list contains user
    public boolean contains(String name)
    {
        for(CommunicationThread CT: onlineList)
        {
            if(CT.getUserName().equals(name))
                return true;

        }
        return false;
    }
    
    //prints users online at the moment
    public void printOnline()
    {
        for(CommunicationThread CT: onlineList)
            System.out.println(CT.getUserName());
    }
    
    //remove a user from the connection
    public void remove(CommunicationThread CT)
    {
        onlineList.remove(CT);
        userNameList.remove(CT.getUserName());
        size--;
    }

    //gets a new user from a new connection
    public CommunicationThread getUserThread(String userName)
    {
        for(CommunicationThread CT: onlineList)
            if(CT.getUserName().equals(userName))
                return CT;
        return null;

    }
    
    //list of usernames
    public ArrayList<String> getUserNameList(){
        return userNameList;
    }
}