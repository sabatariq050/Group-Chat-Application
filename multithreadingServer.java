package newproject;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;

import javax.swing.JOptionPane;

public class multithreadingServer {

	private static ServerSocket serverSocket=null;
	private static Socket clientSocket=null;
	
	private static final int maxClientsCount = 10;
	private static final multithreadingClient[] threads = new multithreadingClient[maxClientsCount];
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int portno=2222;
		 if (args.length < 1) {
		    	JOptionPane.showMessageDialog(null,"Server started successfully!!!");
		    } else {
		      portno = Integer.valueOf(args[0]).intValue();
		    }
		 try {
		      serverSocket = new ServerSocket(portno);
		    } catch (IOException e) {
		      System.out.println(e);
		    }

		 while (true) {
		      try {
		        clientSocket = serverSocket.accept();
		        int i = 0;
		        
		        for (i = 0; i < maxClientsCount; i++) {
		          if (threads[i] == null) {
		            (threads[i] = new multithreadingClient(clientSocket, threads)).start();
		            break;
		          }
		        }
		        if (i == maxClientsCount) {
		          PrintStream os = new PrintStream(clientSocket.getOutputStream());
		          JOptionPane.showMessageDialog(null, "Server is full, Try again later!!");
		          os.close();
		          clientSocket.close();
		        }
		      } catch (IOException e) {
		        System.out.println(e);
		      }
		    }
	}

}
