package newproject;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

public class multithreadingClient extends Thread {

	private String clientname=null;
	private DataInputStream input=null;
	private PrintStream print=null;
	private Socket clientSocket = null;
	private final multithreadingClient[] threads;
	private int maxClientsCount;
	static ArrayList<ChatFrame> myFiles=new ArrayList<>(); //for files
	
	public multithreadingClient(Socket clientSocket, multithreadingClient[] threads){
		this.clientSocket = clientSocket;
	    this.threads = threads;
	    maxClientsCount = threads.length;
	}
	@SuppressWarnings("deprecation")
	public void run() {
		int maxClientsCount = this.maxClientsCount;
		 multithreadingClient[] threads = this.threads;
		 try {
			 input=new DataInputStream(clientSocket.getInputStream());
			 print=new PrintStream(clientSocket.getOutputStream());
			 String name;
			 while(true) {
				 print.println("Please Enter your Name : ");
				 name = input.readLine().trim();
				 if(name.indexOf('@')==-1) {
					 break;
				 }
				 else {
			          print.println("You name should not contain '@' ");//because of private chats
			        }
				 
			 }
			 print.println("Welcome "+ name 
					 + " to this group chat! \n If you want to exit, Please type /quit");
			 synchronized (this) {
			        for (int i = 0; i < maxClientsCount; i++) {
			          if (threads[i] != null && threads[i] == this) { //for first client
			            clientname = "@" + name ;
			            break;
			          }
			        }
			        for (int i = 0; i < maxClientsCount; i++) { 
			          if (threads[i] != null && threads[i] != this) { //so on
			            threads[i].print.println("Server: A new user " + name
			                + " entered the chat room !!!\n");
			          }
			        }
			      }
			 while(true) {
				 String line=input.readLine();
				 if(line.startsWith("/quit")) { //quitting chat
					 break;
				 }
				 if(line.startsWith("@")) //sending private message
				 {
					  String[] words = line.split("\\s", 2);
			          if (words.length > 1 && words[1] != null) {
			            words[1] = words[1].trim();
			            if (!words[1].isEmpty()) {
			              synchronized (this) {
			                for (int i = 0; i < maxClientsCount; i++) {
			                  if (threads[i] != null && threads[i] != this
			                      && threads[i].clientname != null
			                      && threads[i].clientname.equals(words[0])) //finding that exact client name
			                	  {
			                    threads[i].print.println("<" + name + "> " + words[1]);
			                    this.print.println("<" + name + "> " + words[1]);
			                    break;
			                  }
			                }
			              }
			            }
			          }
				 }
				 else { // sending msg to all clients
			          synchronized (this) {
			            for (int i = 0; i < maxClientsCount; i++) {
			              if (threads[i] != null && threads[i].clientname != null) 
			              {
			                threads[i].print.println("<" + name + "> " + line);
			                
			              }
			            }
			          }
			        }
				 
			 }
			 synchronized (this) {
			        for (int i = 0; i < maxClientsCount; i++) {
			          if (threads[i] != null && threads[i] != this
			              && threads[i].clientname != null) {
			            threads[i].print.println("Server: <" + name
			                + "> left the group chat !!!");
			          }
			        }
			      }
			  JOptionPane.showMessageDialog(null,"You have left the group");
		      clientSocket.close();
		      print.println("*** Bye " + name + " ***");
		      synchronized (this) { //clearing thread of that existing client
		          for (int i = 0; i < maxClientsCount; i++) {
		            if (threads[i] == this) {
		              threads[i] = null;
		            }
		          }
		        }
		      input.close();
		      print.close();
		      clientSocket.close();
		 }catch(IOException e){
		 }
	}

}
