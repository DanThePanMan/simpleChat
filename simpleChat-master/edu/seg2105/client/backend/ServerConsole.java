package edu.seg2105.client.backend;

import java.io.IOException;
import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

public class ServerConsole implements ChatIF{
	
	
	  Scanner fromConsole; 
	  EchoServer server;

	  
	  public ServerConsole(EchoServer server) 
	  {
	    this.server = server;
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
	  }


	
	
	 public void display(String message) 
	  {
	    System.out.println("SERVER MSG> " + message);
	  }
	 
	 
	 //accept not done yet
	 public void accept() 
	  {
		 
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromConsole(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.print
	        ("");
	    }
	  }


}

