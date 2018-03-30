package main;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;


import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

//import server.clientThread;

public class MainController implements Initializable {
	
static ServerSocket ser_socket = null;
static final int total = 1;
static final clientThread[] threads = new clientThread[total];
	  private static Socket cli_socket = null;

	   


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

	public void show(javafx.event.ActionEvent event){
		System.out.println("You clicked on ");
		server_func();
		}
	
	


	  public static void server_func() {
		  int port_no = 2226;

		      System.out.println( "Now using port number=" + port_no);

		    try {
		      ser_socket = new ServerSocket(port_no);
		    } catch (IOException e) {
		      System.out.println(e);
		    }
		    while (true) {
		      try {
		        cli_socket = ser_socket.accept();
		        int i = 0;
		        for (i = 0; i < total; i++) {
		          if (threads[i] == null) {
		            (threads[i] = new clientThread(cli_socket, threads)).start();
		            System.out.println("************");
		            break;
		          }
		        }
		        if (i == total) {
		          PrintStream p_s = new PrintStream(cli_socket.getOutputStream());
		          p_s.println("Server is very Busy. Kindly Try Again later.");
		          p_s.close();
		          cli_socket.close();
		        }
		      } catch (IOException e) {
		        System.out.println(e);
		      }
		    }
		  }
	}


class clientThread extends Thread {
	  private DataInputStream d_i_s = null;
	  private int tot_cli;
	  private String cli_name = null;
	  private Socket cli_socket = null;
	  private final clientThread[] thread;
	  private PrintStream p_s = null;
	  

	  public void run() {
	      String name;
	    int tot_cli = this.tot_cli;
	    clientThread[] thread = this.thread;

	    try {
		    p_s = new PrintStream(cli_socket.getOutputStream());
	    	d_i_s = new DataInputStream(cli_socket.getInputStream());
	    	
	      while (true) {
	        p_s.println("Enter your name.");
	        name = d_i_s.readLine().trim();
	        if (name.indexOf('#') == -1) {
	          break;
	        } else {
	          p_s.println("Name has not '#' character.");
	        }
	      }


	      p_s.println("Welcome " + name + " to our Programme.\n For leave Please Press quit.");
	      synchronized (this) {
	        for (int i = 0; i < tot_cli; i++) {
	          if (thread[i] != null && thread[i] == this) {
	            cli_name = "#" + name;
	            break;
	          }
	        }
	        for (int i = 0; i < tot_cli; i++) {
	          if (thread[i] != null && thread[i] != this) {
	            thread[i].p_s.println("--- Hello...... A new user " + name + " Is being A Part of our Chat ----");
	          }
	        }
	      }
	      while (true) {
	        String line = d_i_s.readLine();
	        if (line.startsWith("/quit")) {
	          break;
	        }
	          synchronized (this) {
	            for (int i = 0; i < tot_cli; i++) {
	              if (thread[i] != null && thread[i].cli_name != null) {
	                thread[i].p_s.println("<" + name + "> " + line);
	              }
	            }
	            System.out.println(line);
	          }
	        
	      }
	      synchronized (this) {
	        for (int i = 0; i < tot_cli; i++) {
	          if (thread[i] != null && thread[i] != this && thread[i].cli_name != null) {
	            thread[i].p_s.println("--- User " + name+ " is Leave ");
	          }
	        }
	      }
	      p_s.println("---Take Care " + name + "  Bye ----");

	      synchronized (this) {
	        for (int i = 0; i < tot_cli; i++) {
	          if (thread[i] == this) {
	            thread[i] = null;
	          }
	        }
	      }
	      d_i_s.close();
	      p_s.close();
	      cli_socket.close();
	    } catch (IOException e) {
	    }
	  }
	  
	  public clientThread(Socket cli_socket, clientThread[] thread) {
		    this.cli_socket = cli_socket;
		    this.thread = thread;
		    tot_cli = thread.length;
		  }
	}