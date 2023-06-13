package newproject;


import javax.swing.JOptionPane;

public class mainMenu {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Object[] options = {"Server","Client"};
		String initialselection="Server";
		Object selection = JOptionPane.showInputDialog(null, "Login as : ", "WECHAT", JOptionPane.QUESTION_MESSAGE, null, options, initialselection);
		
		if(selection.equals("Server"))
		{
            String[] arguments = new String[] {};
            new multithreadingServer().main(arguments);
		}
		else if(selection.equals("Client"))
		{
			String IPServer = "127.0.0.1";
			String[] arguments = new String[] {IPServer};
			new ChatFrame().main(arguments);
	}		
	}

}
