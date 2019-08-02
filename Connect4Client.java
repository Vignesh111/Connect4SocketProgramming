import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *   Names: Eric Cen and Vignesh Kumar and Hunter Read
 *   Group: 07
 *   Course: ISTE-121-01
 *   Description: A recreation in Java of the board game, "Connect Four" 
*/

public class Connect4Client {

   // Attributes
   private JFrame frame;
   
   private JPanel panel;
   private JPanel panel2;
   private JPanel panel3;
   private JPanel panel4;
   
   private final int ROW_TILES = 6; //total rows
   private final int COL_TILES = 7; //total columns
   
   private static int[][] grid = new int[6][7];
   
   private int row, col = 0;
   private int pTurn = 0; //keeps track of whose turn
   
   private boolean booWin = false;
   private boolean controller = true;
   private JButton[][] button = new JButton[ROW_TILES][COL_TILES];
   public boolean control = true;
   private JLabel winner;
   public ConnectClient cc = null;
   private final ImageIcon c1 = new ImageIcon("p1.png");
   private final ImageIcon c2 = new ImageIcon("p2.png");
   
   private JTextField textArea = new JTextField();
   private JTextArea textArea2 = new JTextArea("LOBBY");
   private JScrollPane scrollPane = new JScrollPane(textArea2);
   
   private JButton send = new JButton("Send to Server");
   
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

   public Connect4Client() {
      /* 
         Creates the GUI
      */
      frame = new JFrame("Connect Four - Client");
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
      /* Creates Menu bar with multiple items */
      menuBar.add(menu);
      menu.add(exit);
      menu.add(about);
      menu.add(help);
      menu.add(clear);
      menuBar.setBackground(blue);
      frame.setJMenuBar(menuBar);
      
      /* Changes the visual interface of the GUI*/
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
      }
      
      frame.pack();
      frame.setVisible(true);
      frame.setLocationRelativeTo(null);
      
      /** 
       *  Dialog box asking for username. This username is what will be displayed across the Lobby when
       *  the user sends a message. If no username is sent or "Cancel" is pressed, the name "Anonymous" is given
       *  to you.
       */
      username = JOptionPane.showInputDialog("Please Enter a User: ");
      if (username.equals("") || username == null) {
         username = "Anonymous";
         JOptionPane.showMessageDialog(null, "No username was entered. No name will be displayed in Lobby.");
      }
      /**
       *  Menu item that exits the program 
       */
      exit.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               System.exit(0);
            }
         });
      
      /**
       * Menu item that shows information about the program itself
       */
      about.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               JOptionPane.showMessageDialog(null, "About \n Program: Connect Four \n Created By: Eric Cen, Vignesh Kumar, and Hunter Read");
            }
         });
      
      /**
       * Menu item that shows users how to play the game of Connect Four
       */
      help.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               JOptionPane.showMessageDialog(null, "Welcome to our Java program representation of the classical game, 'Connect Four'.\nIn order to play, Simply click from the bottom row up and try to connect four pieces horizontally,\n vertically or diagonally before your opponent does. The server is player one and goes first.\n Each player takes turns putting down pieces, and only one piece can be placed at a time.");
            }
         });
      
      /**
       * Creates socket with parameters as Server Name and Server Port. Also instantiates object output/input
       * stream
       */
      try {
         sock = new Socket(SERVER_NAME, SERVER_PORT);
         oos = new ObjectOutputStream(sock.getOutputStream());
         ois = new ObjectInputStream(sock.getInputStream());
      }
      catch(IOException ioe)
      {
      }
      
      /**
       * Creates new ConnectClient object using default constructor, creates new thread
       * with ConnectClient and runs it.
       */
      cc = new ConnectClient();
      Thread t = new Thread(cc);
      t.start();
   }
   
   /*
      ActionListener class for the buttons when they are pressed by the user. Either a slot is 
      filled in by a player, or an error message is shown that the move trying to be made is 
      invalid.
      
      When a button is pressed, the listener checks whose turn it is (Player 1 or Player 2, based
      on who last went) and either fills in the slot with the respective player piece or displays
      an error message that a move cannot be made. It also calls the checkWin() method which checks 
      whether or not a winner has been declared. If a winner has been declared, a congratulation message 
      is printed out and the text field is changed accordingly.
      
      If a button is pressed, the program sends a packet with the row/col to the Server and pTurn is
      incremented by one.  
   */
   class ButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent event) {
         for (row = ROW_TILES-1; row >= 0; row--) {
            for (col = COL_TILES-1; col >= 0; col--) {
               if (button[row][col] == event.getSource()) {
                  if (pTurn % 2 == 1 && grid[row][col] == 0) {
                     
                     Packet pack = new Packet(row, col);
                     pTurn = pTurn + 1;
                     cc.sendCoordinates(pack);
                     
                     if (checkWin()) {
                        System.out.println("Player 2 has won!");
                        winner.setText("Player 2 Wins!");
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
      
      /**
       * Method that updates the board based on second player. FocusPainted is set to false in order to
       * make the UI more appealing and grid is assigned accordingly. Then the method checks if anyone
       * has won.
       *
       * @param pack   Packet that holds the row/col of button pressed/piece placed
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
      }
      
   }
   /** 
    *   Method that checks the board to see if anybody has won
    *
    *   @return boolean Returns true if a winner is found, false if winner is not found
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

   public static void main(String[] args) 
   {
      Connect4Client c4c = new Connect4Client();	
   }

   /** 
    * Inner class ConnectClient that implements Runnable and runs threads. 
    */
   public class ConnectClient implements Runnable
   {  
      private  Vector<ObjectOutputStream> vector = new Vector<ObjectOutputStream>();
      private Object serverMsg;
   
      public void run()
      {
         /**
          * If the client receives an object that isnt null, it first checks whether the object is 
          * an instance of the Message or Packet class.
          */
         try
         {
            while ((serverMsg= ois.readObject()) != null)
            {
               /** 
                * If it is an instance of the Message class, it retrieves the message and then 
                * appends it to the lobby. If it is an instance of the string "4gVRmVxHzob18h5vqtxA"
                * then the board is cleared. This string is only sent when the clear button is clicked.
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
                     pTurn++;
                     break;
                  
                  }
                  textArea2.append("\n" + msg);
               }
               /** 
                * If it is an instance of the Packet
                * class, it updates the board.
                */
                
               if(serverMsg instanceof Packet)
               {
               
                  Packet p = (Packet)serverMsg;
                  System.out.println("Clicked + " + p.getN1() + "," + p.getN2());
               
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
               
                  if (checkWin()) {
                     if(controller)
                     {System.out.println("Player 2 has won!");
                        winner.setText("Player 2 Wins!");
                        for (int x = ROW_TILES - 1; x >=0; x--) {
                           for (int y = COL_TILES - 1; y >= 0; y--) {
                              grid[x][y] = -1;
                           }
                        }
                     }
                     
                     
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
                     control = false;
                  }  
                  else{
                     control = true;
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
            System.out.println("The server has been disconnected. No more communications allowed.");
         }
         catch(Exception e)
         {
         }
                   
      }
      
      /**
       *  ConnectClient Constructor that adds action listeners for the send
       *  and clear button. 
       */
      public ConnectClient()
      {
         /**
          * If the send button is clicked, doSend() is called
          */
         send.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent ae)
               {
                  doSend();
               }
            }); 
         
         /**
          * If the clear button is clicked, doClear() is called
          */
         clear.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  doClear();
               }
            });
      }
   
   /***
    * Send method that takes the text the user has entered into the JTextField and sends
    * this to the server in the form of a Message object. Text area is then cleared.
    */
      public void doSend()
      {
         try
         {
            Message text = new Message(username + ": " + textArea.getText());
            String tempText = text.getM();
            oos.writeObject(text);
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
      }
      
       /**
        * Clear method that clears the board by resetting all icons to null and changing
        * all grids to 0. The top rows are also given the value of -1 since you can
        * not reach those in the beginning of the game. Player turn is incremented by 1.
        */
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
      
      /**
       * Method that sends the coordinates of the button that is clicked. checkWin() is
       * called to see if anyone has won.
       *
       * @param pack   Packet that holds the row/col of button pressed/piece placed
       */
      public void sendCoordinates(Packet pack)
      {
         try
         {
            oos.writeObject(pack);
            oos.flush();
            
            try {
               grid[pack.getN1() - 1][pack.getN2()] = 0;
            }
            catch (ArrayIndexOutOfBoundsException e) {
               System.out.println("Reached Top of Column");
            }
               
            checkWin();
           
         }
         catch(Exception e)
         {
         }
      }
      
      /**
       * Method that exits the program
       */
      public void doExit()
      {
         try{
         
         }
         catch(Exception e)
         {
         
         }
         System.exit(0);
      }
   
   }   

}