// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;
import java.rmi.server.ServerCloneException;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String id;
  
  Boolean isLoggedOff = false;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String id, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.id = id;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  @Override
protected void connectionClosed() {
	  if (!isLoggedOff) {
		  System.out.println("Connection closed");
	  }
	  
	
}


@Override
protected void connectionException(Exception exception) {
	if (exception instanceof java.net.SocketException || exception instanceof java.io.EOFException) {
		try {
			closeConnection();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		System.exit(0);
		
	} else {
		exception.printStackTrace();
	}
}


@Override
protected void connectionEstablished() {
	try {
		sendToServer("#login " + id);
	} catch (IOException e) {
		System.out.println("Failed to send #login to server");
	}
	System.out.println("Connected to server");
}

private void handleHashCommand(String msg) {
	String msgNoHash = msg.substring(1);
	String[] msgArray = msgNoHash.split(" ");
	
	switch ( msgArray[0]) {
	
	case "quit":
		quit();
		break;
	case "logoff":
		isLoggedOff = true;
		try {
			closeConnection();
			System.out.println("Connection closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		break;
	case "sethost":
		if (isLoggedOff) {
			if (msgArray.length == 1) {
				System.out.println("Please enter a valid host");
			} else {
				setHost(msgArray[1]);
			}
		} else {
			System.out.println("Client is logged in, please log off before setting host");
		}
		break;
	case "setport":
		if (isLoggedOff) {
			if (msgArray.length == 1) {
				System.out.println("Please enter a valid port");
			} else {
				setPort(Integer.parseInt(msgArray[1]));

			}
		} else {
			System.out.println("Client is logged in, please log off before setting port");
		}
		break; 
	case "login":
		if (isLoggedOff) {
			isLoggedOff = false;
			try {
				this.openConnection();
			} catch (Exception e) {
				System.out.println("Login failed, please check host and port");
				System.out.println("Current host: " + getHost());
				System.out.println("Current port: " + getPort());


				isLoggedOff = true;
			}
		} else {
			System.out.println("Client is logged in already");
		}
		break;
	case "gethost":
		System.out.println(getHost());
		break;
	case "getport":
		System.out.println(getPort());
		break;
	}
	
	
}


/**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {

		  
	  clientUI.display(msg.toString());
	  
   
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  if(message.startsWith("#")){
		  handleHashCommand(message);
		  
	  } else {
	  
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
    	if (isLoggedOff) {
    		clientUI.display("Not logged in, message is not sent.");
    	} else {
    		clientUI.display
            ("Could not send message to server.  Terminating client.");
          quit();
    	}
      
    }
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
