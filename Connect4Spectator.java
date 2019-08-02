import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *   Names: Eric Cen,Hunter Read and Vignesh Kumar
 *   Course: ISTE-121-01
 *   Description: A recreation in Java of the board game, "Connect Four" 
*/

public class Connect4Spectator {

   // All the required attributes
   private JFrame frame;
   
   private JPanel panel;
   private JPanel panel2;
   private JPanel panel3;
   private JPanel panel4;
   
   private final int ROW_TILES = 6;//total no of rows
   private final int COL_TILES = 7;//total no of colums
   
   private static int[][] grid = new int[6][7];
   private boolean controller = true;
   private boolean control = true;
   private int row, col = 0;
   private int pTurn = 0;// used to update a player's turn
   public int colorChooser = 0;
   private boolean booWin = false;
   
   private JButton[][] button = new JButton[ROW_TILES][COL_TILES];
	
   
   private JLabel winner;
   public ConnectClient cc = null;
   private final ImageIcon c1 = new ImageIcon("p1.png");//red piece
   private final ImageIcon c2 = new ImageIcon("p2.png");//yellow piece
   
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
   
   private ObjectOutputStream oos;
   private ObjectInputStream ois;

   private Socket sock = null;
   
   public  final int SERVER_PORT = 16734;
   public  final String SERVER_NAME = "localhost"; 
   
   private String username;

   public Connect4Spectator() {
      /* 
         Creates the GUI
      */
      frame = new JFrame("Connect Four - Spectator");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
     
      
      panel = new JPanel();
      panel.setLayout(new GridLayout(6,7));
      
      panel2 = new JPanel();
      panel2.setLayout(new BorderLayout());
      
      panel3 = new JPanel();
      
      panel4 = new JPanel();
      panel4.setLayout(new BorderLayout());
      scrollPane.setPreferredSize(new Dimension(250, 100));
      textArea.setToolTipText("Input text to send to server here...");
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
      
            
      /*
         Creates a grid that tracks the game board. If a grid position has the value of 
         -1, that means no pieces are allowed to be put in that position (invalid move). 
         0 means that the slot is empty and a valid move.
         1 is a slot filled by Player 1
         2 is a slot filled by Player 2.
      */
      
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
      /*
      menu bar is added, which comprises of the folowing menu items
       ->Exit
       ->About
       ->Help
       ->Clear
      */
      menuBar.add(menu);
      menu.add(exit);
      menu.add(about);
      menu.add(help);
      menu.add(clear);
      menuBar.setBackground(blue);
      frame.setJMenuBar(menuBar);
      
      try
      {  //makes the GUi look better than usual
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
      }
      //making the GUI visible
      frame.pack();
      frame.setVisible(true);
      frame.setLocationRelativeTo(null);
      
      /*
       When the GUI starts, a JOption dialog box appears intimating
       the user to type in his name, which would be his username for rest of the game.
       If no name is given, you will be given "Anonymous" as your username. 
      */
   
      username = JOptionPane.showInputDialog("Please Enter a User: ");
      if (username.equals("") || username == null) {
         username = "Anonymous";
         JOptionPane.showMessageDialog(null, "No username was entered. No name will be displayed in Lobby.");
      }
      
      // If the exit menu item is clicked, it closes the GUI.
      exit.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               System.exit(0);
            }
         });
      // If the about menu item is clicked, it pops up a dialog box that gives credit to the creators of the program.
      about.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               JOptionPane.showMessageDialog(null, "About \n Program: Connect Four \n Created By: Eric Cen, Vignesh Kumar, and Hunter Read");
            }
         });
      
      /*
      If the menu item "help" is clicked, it pops up a JOption dialog box
      which explains the rules of the game.
      */
   
      help.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               JOptionPane.showMessageDialog(null, "Welcome to our Java program representation of the classical game, 'Connect Four'.\nIn order to play, Simply click from the bottom row up and try to connect four pieces horizontally,\n vertically or diagonally before your opponent does. The server is player one and goes first.\n Each player takes turns putting down pieces, and only one piece can be placed at a time.");
            }
         });
      
       /*
      If the menu item "clear" is clicked, it pops up a JOption dialog box
      which instructs the spectator that he/she won't be able to clear the board.
      */
      clear.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  JOptionPane.showMessageDialog(null, "Your are a spectator and cannot clear the board.");
               }
            });
      
      try {
         /*
         Instantiation of client socket.
         Initialises object input and output strams.
         */
         sock = new Socket(SERVER_NAME, SERVER_PORT);
         oos = new ObjectOutputStream(sock.getOutputStream());
         ois = new ObjectInputStream(sock.getInputStream());
      
      }
      catch(IOException ioe)
      {
      }
      
      /*
      An object of inner class ConnectClient is started
      Thread starts running
      */
      cc = new ConnectClient();
      Thread t = new Thread(cc);
      t.start();
      
      
      System.out.println("past client");
   }//end of Connect4Spectator constructor
   
   /*
      ActionListener class for the buttons when they are pressed by the user. Either a slot is 
      filled in by a player, or an error message is shown that the move trying to be made is 
      invalid.
      
      When a button is pressed, the listener  whose turn it is (Player 1 or Player 2, based
      on who last went) and either fills in the slot with the respective player pieec or displays
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
                  if (pTurn % 2 == 1 && grid[row][col] == 0) {
                  
                     Packet pack = new Packet(row, col);
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
                  else if (pTurn % 2 == 0) {
                  
                  
                  
                     System.out.println("It is not your turn");
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
      The updateBoard method is responsible for setting the icon on
      the GUI button(i.e it marks the pieces) for player 2.
      */
      
      
      public void updateBoard(Packet pack)
      {
      
      
         button[pack.getN1()][pack.getN2()].setFocusPainted(false);
         button[pack.getN1()][pack.getN2()].setIcon(c2);
         grid[pack.getN1()][pack.getN2()] = 2;   
            
         try {
            grid[pack.getN1() - 1][pack.getN2()] = 0;
         }
         catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Reached Top of Column");
         }
               
         checkWin();
      
      
      }//end of updateBoard method
      
   }//end of ButtonListener
   
   /*
      ActionListener class for the clear button. After a player gets 4 pieces in a row 
      (horizontal, vertical, or diagonal) the winner is displayed in a text field,
      where the user may then choose to close the program or clear the board and start a new game. 
   */
   class clearListener implements ActionListener {
      public void actionPerformed(ActionEvent event) {
         for (int x = ROW_TILES - 1; x >= 0; x--) {
            for (int y = COL_TILES - 1; y >= 0; y--) {
               grid[x][y] = -1;
               button[x][y].setIcon(null);
            }
         }
         for (int y = COL_TILES - 1; y >= 0; y--) {
            grid[5][y] = 0;
         }
      
         booWin = false;
      }
   }//end of ClearListener

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
   }//end of checkWin method

   public static void main(String[] args) 
   {
      new Connect4Spectator();// Calls the Conect4 Spectator	
   }//end of main method

  /*
  The inner class ConnectClient implements runnable
  The thread functionality is implemented inside the run method of
  the ConnectClient class
  */ 
  
   public class ConnectClient implements Runnable
   {  
      private  Vector<ObjectOutputStream> vector = new Vector<ObjectOutputStream>();
   
   
      private Object serverMsg;
   
      public void run()
      {
         try
         {
            /*
            It reads the objet sent from the server,
            It also checks if the object is an instance of Message class
            or an instance of Packet class.
            */
            while ((serverMsg= ois.readObject()) != null)
            {  /*
               If it is an instance of message class, it gets the message and appends to it's text area.
               */
               if(serverMsg instanceof Message)
               {
                  Message m = (Message)serverMsg;
                  String msg = m.getM();
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
                     pTurn++;//player's turn is updated
                     break;
                  
                  }
                  textArea2.append("\n" + msg);//appends the message
               }
               /*
               If it is an instance of Packet class, it updates the piece on the board, 
               red for player-1 and yellow for player-2.
                
               */
            
               if(serverMsg instanceof Packet)
               {
               
                  Packet p = (Packet)serverMsg;
                  System.out.println("Clicked + " + p.getN1() + "," + p.getN2());
                   
                   
                   
                  button[p.getN1()][p.getN2()].setFocusPainted(false);
                 
               /*
                If the boolean variable controller is true, it marks the clicked button with a red piece
                otherwise it marks it with yellow piece. The varable controller is used to decide which
                colored piece to update in the board
                
               */
                  if(controller)
                  {
                     button[p.getN1()][p.getN2()].setFocusPainted(false);
                     button[p.getN1()][p.getN2()].setIcon(c1);
                     grid[p.getN1()][p.getN2()] = 1; 
                     controller = false;
                  }
                  else{
                  
                     button[p.getN1()][p.getN2()].setFocusPainted(false);
                     button[p.getN1()][p.getN2()].setIcon(c2);
                     grid[p.getN1()][p.getN2()] = 2; 
                     controller = true;
                  }
               
                  try {
                     grid[p.getN1() - 1][p.getN2()] = 0;
                  }
                  catch (ArrayIndexOutOfBoundsException e) {
                     System.out.println("Reached Top of Column");
                  }
               
                  //calls the checkWin method to check if any of the player has won
                  if (checkWin()) {
                     //displays player 2 as winner if controller is true
                     if(controller)
                     
                     {System.out.println("Player 2 has won!");
                        winner.setText("Player 2 Wins!");
                        for (int x = ROW_TILES - 1; x >=0; x--) {
                           for (int y = COL_TILES - 1; y >= 0; y--) {
                              grid[x][y] = -1;
                           }
                        }
                     }
                     
                     //Player 1 is declared as winner if controller is false.
                     else if(!(controller))
                     {System.out.println("Player 1 has won!");
                        winner.setText("Player 1 Wins!");
                        for (int x = ROW_TILES - 1; x >=0; x--) {
                           for (int y = COL_TILES - 1; y >= 0; y--) {
                              grid[x][y] = -1;
                           }
                        }
                     }
                  }     
                  if(control)
                  {    
                     control = false;// control is instantiated to false if it was true
                  }  
                  else{
                     control = true;//control is instantiated to true if it was false
                  
                  }
                  pTurn += 1;//updates player's turn
               }
            }
         }   
         catch(UnknownHostException uhe) 
         {
            System.out.println("Error: " + uhe.toString());
         }
         catch(IOException ioe) 
         {
         
         
            System.out.println("The server has been disconnected. No more communications allowed.");
         }
         catch(Exception e)
         {
         
         }
            
                   
               
            
         
      }//end of run method
   
      public ConnectClient()// constructor of inner class ConnectClient
      {
      
            
      /*
         Attempts to establish a connection, displays text received from server into
         lobby text area.
      */
         /*
         Once the send button is clicked,
         doSend method is called
         */
         send.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent ae)
               {
                  doSend();
               }
            });
      
            
        
            
         
        
      }//end of connectClient constructor
   
   /***
    * Send method that takes the text the user has entered into the JTextField and sends
    * this to the server. 
    */
    
      public void doSend()
      {
         try
         {
           
         
            Message text = new Message(username + ": " + textArea.getText());
            String tempText = text.getM();
            oos.writeObject(text);//text is sent to server
         
            oos.flush();
           
            textArea.setText("");
            
            
         }
         catch (IOException ioe)
         {
            System.out.println(ioe.toString());
         }
         catch (NullPointerException e)
         {
            System.out.println("Please connect to a server before sending messages");
         }
      }//end of doSend method
      public void sendCoordinates(Packet pack)
      {
      
      }
        // Exit method that simply exits the program.
      public void doExit()
      {
         try{
         
         }
         catch(Exception e)
         {
         
         }
         System.exit(0);
      }
   
   }//end of ConnectClient   

}//end of Connect4Spectator