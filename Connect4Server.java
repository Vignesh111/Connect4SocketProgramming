import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *   Names: Eric Cen and Vignesh Kumar and Hunter Read
 *   Course: ISTE-121-01
 *   Description: A recreation in Java of the board game, "Connect Four" 
*/
//Main class that will create gui and Server Connection.
public class Connect4Server {

   // Attributes
   private JFrame frame;
   
   private JPanel panel;
   private JPanel panel2;
   private JPanel panel3;
   private JPanel panel4;
   Message text;
   private final int ROW_TILES = 6;
   private final int COL_TILES = 7;
   int printControl = 0;
   private static int[][] grid = new int[6][7];
   
   private int row, col = 0;
   private int pTurn = 0;
   
   private boolean booWin = false;
   private int controller = 0;
   private JButton[][] button = new JButton[ROW_TILES][COL_TILES];
   
   private JLabel winner;
   
   private final ImageIcon c1 = new ImageIcon("p1.png");
   private final ImageIcon c2 = new ImageIcon("p2.png");
   
   private JTextField textArea = new JTextField();
   private JTextArea textArea2 = new JTextArea("LOBBY");
   private JScrollPane scrollPane = new JScrollPane(textArea2);
   
   private JButton send = new JButton("Send to Client");
   
   private JMenuBar menuBar = new JMenuBar();
   private JMenu menu = new JMenu("Menu");
   private JMenuItem exit = new JMenuItem("Exit");
   private JMenuItem about = new JMenuItem("About");
   private JMenuItem help = new JMenuItem("Help");
   private JMenuItem clear = new JMenuItem("Clear Board");
   
   private Color blue = new Color(30, 144, 255);
//Message text;
   private  ServerSocket serverSocket = null;
   public  final int SERVER_PORT = 16734;
   private Object serverMsg;
   public int i=0;
   private String username;
   boolean update = true;
   private ArrayList<ConnectServer> csArray = new ArrayList<ConnectServer>();

   public Connect4Server() {
      /* 
         Creates the GUI
      */
      frame = new JFrame("Connect Four - Server");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      panel = new JPanel();
      panel.setLayout(new GridLayout(6,7));
      
      panel2 = new JPanel();
      panel2.setLayout(new BorderLayout());
      
      panel3 = new JPanel();
      
      panel4 = new JPanel();
      panel4.setLayout(new BorderLayout());
      scrollPane.setPreferredSize(new Dimension(250, 100));
      textArea.setToolTipText("Input text to send to client here...");
      textArea2.setToolTipText("Text sent from server will appear here");
      textArea2.setEditable(false);
      panel4.add(textArea, BorderLayout.NORTH);
      panel4.add(scrollPane, BorderLayout.CENTER);
      panel4.add(send, BorderLayout.SOUTH);
      
      winner = new JLabel("");
      winner.setPreferredSize(new Dimension(550, 80));
      winner.setHorizontalAlignment(JLabel.CENTER);
      winner.setOpaque(true);
      winner.setBackground(blue);
   
      for (int x = ROW_TILES - 2; x >= 0; x--) {
         for (int y = COL_TILES - 1; y >= 0; y--) {
            grid[x][y] = -1;
         }
      }
   
      /*
         Adds a button and its respective button listener to the panel, filling
         in all the rows and columns of the game board.
      */
      for (row = 0; row <= ROW_TILES - 1; row++) {
         for (col = 0; col <= COL_TILES - 1; col++) {
            button[row][col] = new JButton();
            button[row][col].addActionListener(new ButtonListener());
            button[row][col].setPreferredSize(new Dimension(75,75));
            panel.add(button[row][col]);
         }
      }
      
      panel2.add(panel, BorderLayout.NORTH);
      panel3.add(winner);
      panel2.add(panel3, BorderLayout.CENTER);
      panel2.add(panel4, BorderLayout.SOUTH);
      
      frame.add(panel2);
      
      menuBar.add(menu);
      menu.add(exit);
      menu.add(about);
      menu.add(help);
      menu.add(clear);
      menuBar.setBackground(blue);
      frame.setJMenuBar(menuBar);
      
      /* 
         Gives the GUI interface a different look
      */
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
         System.out.println("empty catch statements hurt my heart");
      }
      frame.pack();
      frame.setVisible(true);
      frame.setLocationRelativeTo(null);  
      
      username = JOptionPane.showInputDialog("Please Enter a User: ");
      System.out.println("Hello I'm " + username);
   
      if (username.equals("") || username == null) {
         username = "Anonymous";
         JOptionPane.showMessageDialog(null, "No username was entered. No name will be displayed in Lobby.");
      }
      
      exit.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               System.exit(0);
            }
         });
      
      about.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               JOptionPane.showMessageDialog(null, "About \n Program: Connect Four \n Created By: Eric Cen, Vignesh Kumar, and Hunter Read");
            }
         });
      
      help.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               JOptionPane.showMessageDialog(null, "Welcome to our Java program representation of the classical game, 'Connect Four'.\nIn order to play, ... Add More Info Here");
            }
         });
      
      clear.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               for (ConnectServer connect : csArray ) {
                  connect.doClear();
               }
            }
         });
         //this is the action listener for the send button,
         
         //This action listener updates the Lobby chat for the server.
      send.addActionListener(
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent ae)
                  {
                  text = new Message(username + ": " + textArea.getText());
                     
                     textArea2.append("\n" + text.getM());
                     for (ConnectServer connect : csArray) {
                        connect.doSend();
                      
                     }
                 
                     
                  }
               });
      
      try 
      {
         serverSocket = new ServerSocket(SERVER_PORT);
      } 
      catch (IOException ioe) 
      {
         System.out.println(ioe.toString());
      }
      
      // this is the MultiThreaded server controller used to connect as many clients as we may need.
      while (true) 
      {
         try 
         {
            ConnectServer cs = null;
            Socket sock = null;
            sock = serverSocket.accept();
            cs = new ConnectServer(sock);
            csArray.add(cs);
            cs.start();
         }
         catch (IOException ioe)
         {
            System.out.println(ioe.toString());
         }
         catch (NullPointerException ne)
         {
            System.out.println(ne.toString());
         }
            
      }
   }
   
   /*
      ActionListener class for the buttons when they are pressed by the user. Either a slot is 
      filled in by a player, or an error message is shown that the move trying to be made is 
      invalid.
      
      When a button is pressed, the listener checks whose turn it is (Player 1 or Player 2, based
      on who last went) and either fills in the slot with the respective player piece or displays
      an error message that a move cannot be made. If an entire column is filled up, the program
      also displayed a message that the top of the column has been reached. It also calls the 
      checkWin() method which checks whether or not a winner has been declared. If a winner
      has been declared, a congratulation message is printed out and the text field is changed
      accordingly.
   */
   
   class ButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent event) {
         for (row = ROW_TILES-1; row >= 0; row--) {
            for (col = COL_TILES-1; col >= 0; col--) {
               if (button[row][col] == event.getSource()) {
                  if (pTurn % 2 == 0 && grid[row][col] == 0) {
                  
                     Packet pack = new Packet(row, col);
                     
                     updateBoard(pack);
                     
                     for (ConnectServer connect : csArray) {
                        connect.sendCoordinates(pack);
                     }
                  
                     if (checkWin()) {
                        System.out.println("Player 1 has won!");
                        winner.setText("Player 1 Wins!");
                        for (int x = ROW_TILES - 1; x >=0; x--) {
                           for (int y = COL_TILES - 1; y >= 0; y--) {
                              grid[x][y] = -1;
                           }
                        }
                     }
                     pTurn = pTurn + 1;
                     break;
                  }
                  else if (pTurn % 2 == 1) {
                     
                     System.out.println("It is not your turn ");                   
                     break;
                  }
                  else {
                     JOptionPane.showMessageDialog(null, "That move cannot be made.");
                  }
               }
            }
         }
      }
      
      /*
       *  This updates the board according to the Packet pack object that was sent to this server
       *  from the client. Then, it checks if anyone has won.
       *
       *  @param Packet pack - packet sent from client to update board
      */
      public void updateBoard(Packet pack)
      {
         button[pack.getN1()][pack.getN2()].setFocusPainted(false);
         button[pack.getN1()][pack.getN2()].setIcon(c1);
         grid[pack.getN1()][pack.getN2()] = 1;  
         
         try {
            grid[pack.getN1() - 1][pack.getN2()] = 0;
         }
         catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Reached Top of Column");
         }
                         
         checkWin();
      
      }
      
   }
   
   /** 
    *   Method that checks the board to see if anybody has won
    *
    *   @return win Returns true if a winner is found, false if winner is not found
    */
   public boolean checkWin() {
   	
      /*
         This checks the board horizontally whether or not a player has won yet
      */
      for (int x = 0; x < 6; x++) {
         for (int y = 0; y < 4; y++) {
            if (grid[x][y] != 0 && grid[x][y] != -1 &&
            grid[x][y] == grid[x][y+1] &&
            grid[x][y] == grid[x][y+2] &&
            grid[x][y] == grid[x][y+3]) {
               booWin = true;
            }
         }
      }
   	
      /*
         This checks the board vertically whether or not a player has won yet
      */
      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 7; y++) {
            if (grid[x][y] != 0 && grid[x][y] != -1 &&
            grid[x][y] == grid[x+1][y] &&
            grid[x][y] == grid[x+2][y] &&
            grid[x][y] == grid[x+3][y]) {
               booWin = true;
            }
         }
      }
      
   	/*
         This checks the board diagonally whether or not a player has won yet
         (Diagonal from bottom left to top right)
      */
      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 4; y++) {
            if (grid[x][y] != 0 && grid[x][y] != -1 &&
            grid[x][y] == grid[x+1][y+1] &&
            grid[x][y] == grid[x+2][y+2] &&
            grid[x][y] == grid[x+3][y+3]) {
               booWin = true;
            }
         }
      }
   	
      /*
         This checks the board diagonally whether or not a player has won yet
         (Diagonal from bottom right to top left)
      */
      for (int x = 3; x < 6; x++) {
         for (int y = 0; y < 4; y++) {
            if (grid[x][y] != 0 && grid[x][y] != -1 &&
            grid[x][y] == grid[x-1][y+1] &&
            grid[x][y] == grid[x-2][y+2] &&
            grid[x][y] == grid[x-3][y+3]) {
               booWin = true;
            }
         }
      }
      return booWin;
   }
//starts program.
   public static void main(String[] args) 
   {
      new Connect4Server();	
   }
   
   /*
   *  This is the class that reads and writes to the client based on what is written and read
   *  back to this server. It can receive either an instance of the Message class, or an 
   *  instance of the Packet class.
   */ 
   public class ConnectServer extends Thread 
   {
      // Attributes  
      private Socket sock;
      private ObjectOutputStream oos = null;
      private ObjectInputStream ois = null;
      
      /**
       * Constructor for thread class that accepts a socket as a parameter
       *
       * @param sock - The socket used for connection
       */
      public ConnectServer(Socket sock)
      {
         this.sock = sock;    
      }
      
      public void run()
      {
         try
         {
         //Creates object output streams and Object Input Streams for connections.
         
         //Object output stream was used 
         //so we could have one connection to send multiple types of messages (int,int) or String through the single connection
            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());
            
            
           /* This while loop waits for objects from the A particular Client or Spectator,
           then will split either based on whether it is a lobby Messages or  a Packet,
           The Messages and Packets are sent back to the Client and the Spectator(s),  
           as well as updating on the Servers interface.
             */
            while ((serverMsg=ois.readObject()) != null)
            {
               if(serverMsg instanceof Message)
               {
                  
                  Message m = (Message)serverMsg;
                  String msg = m.getM();
                  
                  //This is a special case 
                  if (msg.equals("4gVRmVxHzob18h5vqtxA"))
                  {
                     for (int x = ROW_TILES - 1; x >=0; x--) {
                        for (int y = COL_TILES - 1; y >= 0; y--) {
                           grid[x][y] = 0;
                           button[x][y].setIcon(null);
                        }
                     }
                     
                     for (int x = ROW_TILES - 2; x >= 0; x--) {
                        for (int y = COL_TILES - 1; y >= 0; y--) {
                           grid[x][y] = -1;
                        }
                     }
                     break;
                  
                  }
                  
                  textArea.setText(msg);
                  
                   printControl = csArray.size();
                  textArea2.append("\n" + msg);
                  for (ConnectServer connect : csArray) {
                  
                     connect.doSendA(update);
                     update = false;
                 }
                  
                  update = true;
                   
                  textArea.setText("");             
               }
                  
                  //Statement for catching Packets, also checks for winner after sending back to Clients.
               if(serverMsg instanceof Packet)
               {
               
                  Packet p = (Packet)serverMsg;
                  System.out.println("Clicked + " + p.getN1() + "," + p.getN2());
                  button[p.getN1()][p.getN2()].setFocusPainted(false);
               
                  button[p.getN1()][p.getN2()].setIcon(c2);
                  grid[p.getN1()][p.getN2()] = 2;                                                     
               
                  try {
                     grid[p.getN1() - 1][p.getN2()] = 0;
                  }
                  catch (ArrayIndexOutOfBoundsException e) {
                     System.out.println("Reached Top of Column");
                  }
                  for (ConnectServer connect : csArray) {
                     connect.sendCoordinates(p);
                  }
                     
                  if (checkWin()) {
                     System.out.println("Player 2 has won!");
                     winner.setText("Player 2 Wins!");
                     for (int x = ROW_TILES - 1; x >=0; x--) {
                        for (int y = COL_TILES - 1; y >= 0; y--) {
                           grid[x][y] = -1;
                        }
                     }
                  }
                  pTurn += 1;                
               }
            }
         
         }
         catch(UnknownHostException uhe) 
         {
            System.out.println("Error: " + uhe.toString());
         }
         catch(IOException ioe) 
         {
            System.out.println(ioe.toString());
         }
         catch(Exception e)
         {
            
         }
           
      }
      //Method used for Sending messages through the Object Stream without Adding a User : infront of the message.
      public void doSendA(boolean _update)
      {
         try{
     
            text = new Message(textArea.getText());
            String tempText = text.getM();
            
            System.out.println(tempText);
      
          
            if(update)
            {
           
            
            printControl = 0;
            update = false;
            }
            System.out.println(update);
            oos.writeObject(text);
            oos.flush();
       
         }
         catch(Exception e)
         {
            
         }
      
      }
      
      //do Send method sends a method while adding the username of the server to the Message object
      public void doSend()
      {
      int sendControl = 0;
     
         try
         {   
         if(sendControl == 0)
         {
            text = new Message(username + ": " + textArea.getText());
            sendControl = 1;
            }
            else
            {
            
            sendControl = 0;
            }
            
            String tempText = text.getM();
                       
           // textArea2.append("\n" + tempText);
                 
            System.out.println(tempText);
           
            oos.writeObject(text);
            
            oos.flush();
            if(csArray.size()==1)
            {
            
             textArea.setText("");
            } 
            
        
         }
            
         catch (IOException ioe)
         {
            System.out.println(ioe.toString());
         }
         catch (NullPointerException e)
         {
            System.out.println("Please connect to a server before sending messages");
         }
      }
      //Method clears board but does not reset game.
      public void doClear() {
         try {
            Message text = new Message("4gVRmVxHzob18h5vqtxA");
            oos.writeObject(text);
            oos.flush();
            
            for (int x = ROW_TILES - 1; x >=0; x--) {
               for (int y = COL_TILES - 1; y >= 0; y--) {
                  grid[x][y] = 0;
                  button[x][y].setIcon(null);
               }
            }
            
            for (int x = ROW_TILES - 2; x >= 0; x--) {
               for (int y = COL_TILES - 1; y >= 0; y--) {
                  grid[x][y] = -1;
               }
            }
            
            pTurn += 1;
         }
         catch (IOException ioe) {}
      }
      
      public void clearText() {
         textArea.setText("");
      }
      //Sends Packet objects through the output stream. also keeps track of if a red or yellow tile was placed last
      public void sendCoordinates(Packet pack)
      {
         System.out.println(controller);
         
         if(controller < 1)
         {
            try
            {
               oos.writeObject(pack);
               oos.flush();
               controller ++;             
            }
            catch(Exception e)
            {
            }
         }
         else{
            try{
            
               oos.writeObject(pack);
               oos.flush();
               controller = 0;
            }
            catch(Exception e)
            {
            }
         }
      }
        
   }
   
}

