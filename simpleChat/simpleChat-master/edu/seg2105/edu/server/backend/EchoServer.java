package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.EOFException;
import java.io.IOException;

import edu.seg2105.client.backend.ServerConsole;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  private boolean isStopped = false;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  if(msg.toString().startsWith("#login")) {
		  
		  
		  if (client.getInfo("loginId") == null) {
			  String msgNoHash = msg.toString().substring(1);
			  String[] msgArray = msgNoHash.split(" ");
			  client.setInfo("loginId", msgArray[1]);
			  System.out.println(client.getInfo("loginId") + " has logged on.");
		  } else {
			  try {
				client.sendToClient("User already logged in, terminating connection");
				client.close();
			} catch (IOException e) {
				System.out.println("Failed to close server when user sends double login request");
			}
		  }
		  
	  }
		  System.out.println("Message received: " + msg + " from " + client.getInfo("loginId"));
		  if(!msg.toString().startsWith("#login")) {
			  this.sendToAllClients(client.getInfo("loginId") + ": " + msg.toString());
		  }
		  
	  
	  
  }
  
  
  
  
  public void handleHashCommand(String msg) {
	  String msgNoHash = msg.substring(1);
	  String[] msgArray = msgNoHash.split(" ");
	  
	  switch ( msgArray[0]) {
		
		case "quit":
			try {
				isStopped = true;
				System.out.println("Successfully quit server");
				this.stopListening();
				System.exit(this.getPort());;
			} catch (Exception e) {
				System.out.println("Failed to quit server");
			}
			break;
		case "stop":
			isStopped = true;
			this.stopListening();
			break;
		case "close" :
			isStopped = true;
			this.stopListening();
			this.sendToAllClients("The server has shut down");
		try {
			this.close();
		} catch (IOException e) {
			System.out.println("Failed to close server");
		}
		break;
		case "setport" :
			setPort(Integer.parseInt(msgArray[1]));
			System.out.println("Port set to " + msgArray[1]);
			break;
		case "start":
			if(isStopped) {
				isStopped = false;
				try {
					this.listen();
				} catch (IOException e) {
					System.out.println("Failed to start listening");
				}
			} else {
				System.out.println("Cannot start the server because it has already started");
			}
			break;
		case "getport":
			System.out.println(this.getPort());
			break;
			
		
	  }
	  

  }
  
  
  
  
  public void handleMessageFromConsole
  (Object msg)
{
	  if(msg.toString().startsWith("#")) {
		  handleHashCommand(msg.toString());
		  
	  } else {
		  if (!isStopped) {
			  System.out.println("Message received: " + '"' + msg + '"'+ " from server console");
			  this.sendToAllClients("SERVER MSG> " + msg);
		  }
		  
	  }
}
  
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  
  //Class methods ***************************************************
  
  @Override
protected void clientConnected(ConnectionToClient client) {
	System.out.println("A new client has connected to the server!");
}


@Override
protected synchronized void clientDisconnected(ConnectionToClient client) {
	System.out.println(client.getInfo("loginId") + " has disconnected.");
}




@Override
protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
	if (exception instanceof EOFException) {
		clientDisconnected(client); //if user closes client terminal, server displays disconnect message
	} else {
		exception.printStackTrace();
	}
}


/**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
      ServerConsole chat = new ServerConsole(sv);
      chat.accept();
      
      
      
      
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
    
  }
}
//End of EchoServer class
