import java.awt.EventQueue;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.*; //for all input output
import java.net.*; //for socket programming
import java.nio.*; //all nio files are for the Watchservice
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class GUI {

	private JFrame frame;
	private JTextField client1; // username of the first client
	String text1; // used to extract the client name written by client
	static JLabel connect; // this is used to print the connection status of clients
	static String ret; // connected client

	Socket csocket; // this is socket for client side
	String ip = "localhost"; // localhost ip address
	WatchService watcher; // watcher
	WatchKey watchkey; // watch key
	String fname; // name of new file created
	String dirp; // name of directory created
	String clname;// name of the client sent by server for broadcast file transfer
	int port = 9008; // port number of server
	static String name; // using this to check client name for compare
	List<String> namelist = new ArrayList<>(); // creating arraylist of usernames

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
					// callwatcher(corclient);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();

		//

	}

	public String connect(String testString) throws IOException // creating client server connect for name check
	{

		csocket = new Socket(ip, port); // initiating the socket connection
		String s = testString; // saving the username for duplicate check

		// sending username to server
		PrintWriter pr = new PrintWriter(csocket.getOutputStream()); // this creates path to server
		pr.println(s);
		pr.flush(); // flushed out all data
		// receiving data from server
		InputStream is = csocket.getInputStream(); // receiving the connection success message from server saying that
													// the clientname is unique
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		name = br.readLine();
		// System.out.print(name);
		String check = "connected"; // checking the connect for name
		if (name.contentEquals(check)) // checking for confirmation that the username is unique to enter thread
		{
			System.out.println("entered thread1");
			File f = new File("D:\\DS Project\\ClientServerConnection\\src/" + testString); // creating a folder for
																							// every client coming in
			if (!f.exists()) {
				f.mkdir(); // creating folder and set all permissions as true
				f.setExecutable(true);
				f.setWritable(true);
				f.setReadable(true);

			}
			ret = testString; // storing the unique client names
			connect.setText(testString + " is connected"); // printing on gui

		} else {
			csocket.close(); // if the name is not unique,close socket
		}
		System.out.println(ret + " connected and  created");
		return ret;

	}

//Watcher Service Starts
	public void Watcher(String clientname) // watcher starts now
	{
		String testString = clientname; // passing the unique client name

		try {
			watcher = FileSystems.getDefault().newWatchService(); // initilising the watcher service to the File
																	// systems.
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		Path dirpath = Paths.get("D:\\DS Project\\ClientServerConnection\\src\\" + testString); // setting the path for
																								// the watcher service
		dirp = dirpath.toString(); // converting the path from type Path to String,this will be unique for each
									// client

		try {
			watchkey = dirpath.register(watcher, // initiating the key to keep an eye on the directory for changes
					StandardWatchEventKinds.ENTRY_CREATE, // alerts when a new doc is created
					StandardWatchEventKinds.ENTRY_DELETE, // alerts when somehting is deleted
					StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true) {
			try {
				watchkey = watcher.poll(10, TimeUnit.MINUTES); // returns a queued key every 10 seconds if an event has
																// occured

				for (WatchEvent<?> event : watchkey.pollEvents()) { // creating an event ev for any action that happens
																	// in the shared directory
					Kind<?> kind = event.kind();

					WatchEvent<Path> ev = (WatchEvent<Path>) event; // storing the event that takes place
					Path filename = ev.context(); // filename records the path where the new event occured.
					System.out.println("text doc path- " + filename);
					fname = filename.toString(); // stores the name of the new file added to directory
					// System.out.println("file is " + fname);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Watcher service ends
			watchkey.reset();
			transfer(dirp, fname, testString, csocket); // sending file to server by calling transfer method

		}
	}

	public void transfer(String dirp, String fname, String testString, Socket csocket) {
		// System.out.println("file name check "+fname);
		if (fname != "tested.txt") // check to handle exception when a file is added into the folder
		{

			try {

				PrintWriter pr = new PrintWriter(csocket.getOutputStream()); // this creates path to server
				// pr.println(dirp); //sending dirpath
				pr.println(testString); // sending client name to server
				pr.flush();
				System.out.println(testString + "client name sent to server");
				// send clinetname
				String test = dirp.concat("\\").concat(fname); // concaninating the directory as well as the new file to
																// read from and send it to the server
				// System.out.println(test);
				System.out.println("sending file");
				FileInputStream fin = new FileInputStream(test);// sending to server,test iis the path of the new file
																// created
				byte b[] = new byte[2019];
				fin.read(b, 0, b.length); // reading the data in the file
				OutputStream os = csocket.getOutputStream(); // converting it into a stream file and sending it into
																// socket
				os.write(b, 0, b.length);
				fin.close();

			}

			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// receiving file from server
	public void rec() {

		try {

			InputStream input = csocket.getInputStream(); // receiving the connection success message from server saying
															// that the clientname is unique
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
			clname = br.readLine(); // getting new client's name

			// concat the final path here
			System.out.println("received cname from server " + clname);
			// System.out.println("path of dirpath "+dirp);
			String firstsplit = "D:\\DS Project\\ClientServerConnection\\src/"; // concating the coming client name with
																				// the dir path
			String finalpath = firstsplit.concat(clname); // this is the final path into which the file is created.
			// String test1= dirp.concat("/").concat(clname);
			System.out.println("received file from server");
			// InputStream input = csocket.getInputStream();
			FileOutputStream fout = new FileOutputStream(finalpath + "/tested.txt"); // creating the file to write
																						// into....test is the same
																						// location as its sending
																						// from.But the server will send
																						// from aNother client
			byte bsoc[] = new byte[2019];
			input.read(bsoc, 0, bsoc.length);
			String asd = new String(bsoc);

			System.out.print("read file");
			fout.write(bsoc, 0, bsoc.length); // write into file
			// fout.close();

			// csocket.close(); // closing the socket
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 950, 551);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		client1 = new JTextField();

		client1.setBounds(373, 59, 145, 43);
		frame.getContentPane().add(client1);
		client1.setColumns(10);

		JButton connect1 = new JButton("Connect"); // initialises the connnection to server
		connect1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				text1 = client1.getText(); // saving the username in "text1" string

				try {

					ret = connect(text1); // sending the username to the connect method which initiates the socket
					System.out.println(ret);
					callwatcher(ret); // calling the thread to watcher
					System.out.println("watcher thread called for " + ret);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

		);
		connect1.setBounds(397, 187, 97, 25);
		frame.getContentPane().add(connect1);

		JLabel clienta = new JLabel("Client");
		clienta.setFont(new Font("Arial", Font.BOLD, 13));
		clienta.setBounds(278, 72, 56, 16);
		frame.getContentPane().add(clienta);

		JButton kill1 = new JButton("Kill"); // used to cut the socket on button
		kill1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				System.exit(1);

			}
		});
		kill1.setBounds(397, 312, 97, 25);
		frame.getContentPane().add(kill1);

		connect = new JLabel(" ");
		connect.setFont(new Font("Arial", Font.BOLD, 18));
		connect.setForeground(Color.BLACK);
		connect.setBounds(558, 53, 289, 55);
		frame.getContentPane().add(connect);

	}

	public int callwatcher(String user) // used to call the threads of watcher and receiver
	{

		new Thread(new Runnable() {
			@Override
			public void run() {
				Watcher(user);     //calling thread on watcher function for every client

			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {

				rec();    //calling thread on rec function to wait and recieve files/data.
			}
		}).start();
		return 1;
	}

}
