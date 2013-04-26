import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class JChat extends JFrame implements Runnable{
	JButton openChat,chatWith,send;
	JLabel pickPort,desPort;
	JTextField pickText,desText,chatText;
	JTextArea chatField;
	PrintWriter out;
	BufferedReader in;
	
	public JChat(){
		this.setSize(500, 600);
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2,1));
		
		//set up buttons
		openChat = new JButton("Open to chat");
		openChat.addActionListener(new OpenChat());
		chatWith = new JButton("Chat with");
		chatWith.addActionListener(new ChatWith());
		send = new JButton("send");
		send.addActionListener(new Send());
		send.setEnabled(false);
		InputMap inputMap = send.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW);
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		inputMap.put(enter, "ENTER");
		send.getActionMap().put("ENTER", new ClickAction(send));

		
		
		//set up labels
		pickPort = new JLabel();
		pickPort.setText("Pick your port number:");
		desPort = new JLabel();
		desPort.setText("Or enter a destinaltion port number:");
		
		//set up text fields
		pickText = new JTextField();
		pickText.setPreferredSize(new Dimension(150,30));
		desText = new JTextField();
		desText.setPreferredSize(new Dimension(150,30));
		chatText = new JTextField();
		chatText.setPreferredSize(new Dimension(400,30));
		chatText.setEnabled(false);
		
		JPanel top1 = new JPanel();
		top1.add(pickPort);
		top1.add(pickText);
		top1.add(openChat);
		
		JPanel top2 = new JPanel();
		top2.add(desPort);
		top2.add(desText);
		top2.add(chatWith);
		
		topPanel.add(top1);
		topPanel.add(top2);
		
		chatField = new JTextArea();
		chatField.setAutoscrolls(true);
		chatField.setDragEnabled(true);
		chatField.setEditable(false);
		chatField.setAlignmentY(TOP_ALIGNMENT);

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(chatText);
		bottomPanel.add(send);
		
		this.add(topPanel,BorderLayout.NORTH);
		this.add(chatField,BorderLayout.CENTER);
		this.add(bottomPanel,BorderLayout.SOUTH);
		
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void enableChat(){
		openChat.setEnabled(false);
		chatWith.setEnabled(false);
		pickText.setEnabled(false);
		desText.setEnabled(false);
		send.setEnabled(true);
		chatText.setEnabled(true);
		new Thread(this).start();
	}
	
	public static void main(String [] args){
		JChat mainFrame = new JChat();
	}
	
	class OpenChat implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(pickText.getText().isEmpty())	return;
			int port = Integer.parseInt(pickText.getText());
			new Server(port);
		}
		
	}
	
	class ChatWith implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(desText.getText().isEmpty()) return;
			int port = Integer.parseInt(desText.getText());
			new Client(port);
		}
		
	}
	
	class Send implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String line = chatText.getText();
			chatText.setText("");
			out.println(line);
			line = "\nYou says          >> "+line;
			chatField.append(line);
		}
		
	}
	
	class Server{
		private Socket clientSocket;
		boolean serverContinue = true;
		
		public Server(int port){
			ServerSocket serverSocket = null;
			try{
				serverSocket = new ServerSocket(port);
				clientSocket = serverSocket.accept();
				chatField.setText("------CONNECTION ESTABLISHED------");
				out = new PrintWriter(clientSocket.getOutputStream(),true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				enableChat();
			}catch(IOException e){
				chatField.setText("Connection Fail");
			}
		}
	}

	class Client{
		public Client(int port){
			Socket socket = null;
			try{
				socket = new Socket("127.0.0.1",port);
				chatField.setText("------CONNECTION ESTABLISHED------");
				out = new PrintWriter(socket.getOutputStream(),true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				enableChat();
			}catch(Exception e){
				chatField.setText("Connection Failed");
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String inputLine;
		 try {
			while ((inputLine = in.readLine()) != null) {
				inputLine = "\nYour peer says >> "+inputLine;
				chatField.append(inputLine);
			}
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class ClickAction extends AbstractAction {
		private JButton button;

		public ClickAction(JButton button) {
			this.button = button;
		}

		public void actionPerformed(ActionEvent e) {
			button.doClick();
		}
	}
}
