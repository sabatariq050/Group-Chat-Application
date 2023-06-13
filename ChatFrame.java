package newproject;
import javax.swing.*;
import javax.swing.border.MatteBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.net.Socket;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

// Class to manage Client chat Box.
@SuppressWarnings("deprecation")
public class ChatFrame {
    /** Chat client access */
    static class ChatAccess extends Observable {
    	
        private Socket socket;
        private OutputStream outputStream;
        @SuppressWarnings("deprecation")
		@Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        /** Create socket, and receiving thread */
       
        public void InitSocket(String server, int port) throws IOException {
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();

            Thread receivingThread = new Thread() {
                @Override
                public void run() {
                    try {
                    	
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null)
                            notifyObservers(line);
                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            };
            receivingThread.start();
        }
        private static final String CRLF = "\r\n"; // newline
        /** Send a line of text */
        public void send(String text,File[] file) {
            try {
            	
            	if(file[0]!=null) {
            		FileInputStream fileinputstream=new FileInputStream(file[0].getAbsoluteFile());
            		String filename=file[0].getName();
            		byte[] fileNameBytes =filename.getBytes();
            		byte[] fileContentBytes=new byte[(int)file[0].length()];
            		fileinputstream.read(fileContentBytes);
            		outputStream.write(fileNameBytes.length);
            		outputStream.write(fileNameBytes);
            		outputStream.flush();
            	}

            	if(text!=null)
            	{ outputStream.write((text  + CRLF).getBytes());
            	}
            	outputStream.flush();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
        /** Close the socket */
        public void close() {
            try {
                socket.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }
    /** Chat client UI */
    static class ChatClient extends JFrame implements Observer {
    	File[] fileToSend=new File[1];
        private JTextArea textArea;
        private JTextField inputTextField;
        private JButton sendButton;
        private ChatAccess chatAccess;
        private JButton choosefile;
        public ChatClient(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI(); 
        }
        /** Builds the user interface */
        private void buildGUI() {
        	
        	textArea = new JTextArea(15,50);
    		textArea.setForeground(new Color(255, 255, 255));
    		textArea.setLineWrap(true);
    		textArea.setBackground(new Color(0, 0, 102));
    		textArea.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
    		textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
    		textArea.setBounds(0, 0, 410, 296);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            add(new JScrollPane(textArea), BorderLayout.CENTER);
            
            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            choosefile=new JButton("Choose file");
        	
        	choosefile.setBorder(new MatteBorder(2,2 , 2, 2, (Color) new Color(0, 0, 102)));
        	choosefile.setBackground(new Color(240, 240, 240));
    		choosefile.setBounds(0, 301, 36, 34);
    		choosefile.setFont(new Font("Monospaced", Font.BOLD, 12));
    		choosefile.setForeground(new Color(255, 255, 255));
    		choosefile.setBackground(new Color(0, 0, 102));
    		box.add(choosefile);
    		
            inputTextField = new JTextField();      
    		inputTextField.setForeground(new Color(255, 255, 255));
    		inputTextField.setBackground(new Color(0, 0, 102));
    		inputTextField.setFont(new Font("Monospaced", Font.PLAIN, 12));
    		inputTextField.setBounds(44, 209, 340, 52);
    		inputTextField.setColumns(10);
    		
            sendButton = new JButton("Send");
           sendButton.setBorder(new MatteBorder(2,2 , 2, 2, (Color) new Color(0, 0, 102)));
    		sendButton.setBounds(0, 209, 44, 52);
    		sendButton.setFont(new Font("Monospaced", Font.BOLD, 12));
    		sendButton.setForeground(new Color(255, 255, 255));
    		sendButton.setBackground(new Color(0, 0, 102));
            box.add(inputTextField);
            box.add(sendButton);
            
            
            
            inputTextField.requestFocus();
            
           ActionListener filelistener=new ActionListener() {
        	 public void actionPerformed(ActionEvent e) {
        		 JFileChooser jFileChooser=new JFileChooser();
             	jFileChooser.setDialogTitle("Choose a file to send");
             	
             	if(jFileChooser.showOpenDialog(null)== JFileChooser.APPROVE_OPTION)
             		
             		{fileToSend[0]=jFileChooser.getSelectedFile();
             		}
             		}
           };
            // Action for the inputTextField and the goButton
            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	   String str = inputTextField.getText();
                	if(inputTextField.getText()==null) {
                      	chatAccess.send(str,fileToSend);
   
                	}
                	else {
                 
                    if (str != null && str.trim().length() > 0 )                    	
                    {
                    	chatAccess.send(str,fileToSend);
                    	}
                    }
                	//fileToSend=null;
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
                }
            };
            choosefile.addActionListener(filelistener);
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chatAccess.close();
                }
            });
        }
        /** Updates the UI depending on the Object argument */
        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(finalArg.toString());
                    textArea.append("\n");
                }
            });
        }
    }
    public static void main(String[] args) {
        String server = args[0];
        int port =2222;
        ChatAccess access = new ChatAccess();
        JFrame frame = new ChatClient(access);
        frame.getContentPane().setBackground(Color.GRAY);
        frame.setTitle("WECHAT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);

        try {
            access.InitSocket(server,port);
        } catch (IOException ex) {
            System.out.println("Cannot connect to " + server + ":" + port);
            ex.printStackTrace();
            System.exit(0);
       }
    }
}
