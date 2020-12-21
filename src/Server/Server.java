package Server;

import java.io.*; //for all IO
import java.text.*;
import java.util.*; //for all collections
import java.net.*; //for the socket n/w connection

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

//import GUI.Handler;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import javax.swing.JTextPane;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class Server extends JFrame {

	private JPanel contentPane;
	static JLabel guiprint; // for adding gui comments
	static String name; // for checking the client name for uniqueness
	static int i = 1;
	// String dirname;
	static List<String> namelist = new ArrayList<>(); // creating arraylist of unique usernames created
	static List<Socket> ports = new ArrayList<>(); // creating socket arraylist of connected sockets

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// System.out.println("s:Server started");
		// guiprint.setText("Server Started");
		ServerSocket Ssocket = new ServerSocket(9008); // socket to accept the client request and create a new socket
		// guiprint.setText("Waiting for client");
		// System.out.println("s:Waiting for client");
		while (true) // having this in a loop for many client connections
		{
			System.out.println("client entered");
			Socket clientaccept = Ssocket.accept(); // accepting client cononection

			InputStream is = clientaccept.getInputStream(); // reading client name for uniqueness check
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			name = br.readLine();

			System.out.println(name + " entered server for name check");
			// guiprint.setText(name+" entered server for name check");
			if (namelist.contains(name) == true) // if name exists in the arraylist
			{
				System.out.println(namelist);

				clientaccept.close(); // close connection cause name is duplicate
				JOptionPane.showMessageDialog(null, "Change name"); // username exists,re enter user name

			} else {
				namelist.add(name); // add name to arraylist to keep check of uniqueness
				JOptionPane.showMessageDialog(null, name + " is Connected"); // display connection message on GUI
				// connect1.setText(name+" is connected");
				// ports.add(clientaccept.getLocalAddress(),clientaccept.getPort(),clientaccept.getLocalPort());
				// //adding port number to arraylist to keep tab
				ports.add(clientaccept); // adding the socket details to the ports arraylist
				String update = "connected";
				PrintWriter pr = new PrintWriter(clientaccept.getOutputStream()); // this adds the data to the sending
																					// path
				pr.println(update);
				// pr.write(update); //sending connection successful to client
				pr.flush();

			}

			Thread thread = new Thread(new Handler(clientaccept)); // calling a new thread for recieving and sending
																	// files
			thread.start();

		}
	}

	public Server() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 842, 493);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnNewButton = new JButton("Kill");
		btnNewButton.setFont(new Font("Arial", Font.BOLD, 25));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
		btnNewButton.setBounds(594, 342, 192, 61);
		contentPane.add(btnNewButton);

		guiprint = new JLabel(" ");
		guiprint.setFont(new Font("Arial", Font.PLAIN, 14));
		guiprint.setBounds(130, 68, 486, 261);
		contentPane.add(guiprint);

	}
}

class Handler extends Server implements Runnable // calling the handler class that sends file to server
{

	Socket clientaccept; // passing the client socket for connection

	// Constructor
	public Handler(Socket clientaccept) {
		this.clientaccept = clientaccept;
	}

	public void run() {

		try {
			// receiving a file input

			System.out.println("server entered");
			InputStream is = clientaccept.getInputStream();
			InputStreamReader isr = new InputStreamReader(is); // taking ip of client name
			BufferedReader br = new BufferedReader(isr);
			String dirname = br.readLine(); // saving clientname in dirname
			System.out.println("name of client is " + dirname);
			byte bsoc[] = new byte[2019];
			is.read(bsoc, 0, bsoc.length); // reading contents of the file
			// String asd=new String(bsoc);
			// System.out.println(asd);
			System.out.println("recieved file");
			// guiprint.setText("Recieved File from client "+dirname);
			// System.out.println("file is sent from "+clientaccept.getPort());
			System.out.println("file is sent from " + clientaccept); // contains localhost port.c,sending client port
																		// and server port.c

			// sendind file back to client

			for (int i = 0; i < ports.size(); i++) {
				if (clientaccept != ports.get(i)) // checking port of sending client and checking.To send to other
													// clients but sender
				{
					clientaccept = ports.get(i);
					dirname = namelist.get(i);
					// System.out.println("the changed port is "+clientaccept);
					// guiprint.setText("Sending file to "+dirname);
					// System.out.println("the changed clinet name is "+dirname);
					PrintWriter pr = new PrintWriter(clientaccept.getOutputStream()); // this creates path to server
					// pr.println(dirp); //sending dirpath
					pr.println(dirname); // sending new client name
					pr.flush();
					OutputStream os = clientaccept.getOutputStream();
					os.write(bsoc, 0, bsoc.length); // seding contents of file
					String asd = new String(bsoc);
					System.out.println("the data written back to server is " + asd);
					System.out.println("sent file from server");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
