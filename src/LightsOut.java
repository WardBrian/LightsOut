import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;

public class LightsOut extends JPanel{
   private static JFrame frame;
   private static final ImageIcon on = new ImageIcon("on.png"),off = new ImageIcon("off.png");
   //The orthognal basis for computing if a 5x5 board is solveable
   public static final int[] n1 = {0,1,1,1,0,1,0,1,0,1,1,1,0,1,1,1,0,1,0,1,0,1,1,1,0};
   public static final int[] n2 = {1,0,1,0,1,1,0,1,0,1,0,0,0,0,0,1,0,1,0,1,1,0,1,0,1};
   
   private TypeListener type;
   private Scoreboard scoreboard;
   private boolean isDeluxe, locked, freeplay;
   private JPanel game;
   private JButton exit, reset,toggle;
   private JButton[][] spaces;
   private boolean[][] data;



   public static void main(String[] args){   
      frame = new JFrame("Lights Out!");
      frame.setContentPane(new LightsOut());
      frame.setSize(300,400);
      frame.setLocation(200,200);
      frame.setIconImage(on.getImage());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setResizable(false);
      frame.setVisible(true);
   }

   
   public LightsOut(){
      isDeluxe = false;
   
      setLayout(new BorderLayout());
      type = new TypeListener();
      addKeyListener(type);
      reset(); //add scoreboard and gameboard
      
      JPanel buttons = new JPanel();
      buttons.setLayout(new FlowLayout());
      reset = new JButton("New Game");
      reset.addActionListener(new ResetListener());
      exit = new JButton("Exit");
      exit.addActionListener(new ExitListener());
      toggle = new JButton("Toggle Mode");
      toggle.addActionListener(new ToggleListener());
      buttons.add(reset);
      buttons.add(exit);
      buttons.add(toggle);
      add(buttons,BorderLayout.SOUTH);
   
   
   
   }
   
   //post: resets the board, including on size change. Size is determined by value of isDeluxe
   private void reset(){
      int size = isDeluxe ? 6 : 5;
      
      if(data == null || !(data.length == size)){ //meaning the size as changed or this is the first run
         
         if(game != null){ //Remove existing frame content on reset
            this.remove(game);
            this.remove(scoreboard);
         }
         
         data = new boolean[size][size];
         spaces = new JButton[size][size];
         
         //Construct the gameboard
         game = new JPanel();
         game.setLayout(new GridLayout(size,size));
         for(int r =0; r < size; r++){
            for(int c=0; c < size; c++){
               spaces[r][c] = new JButton();
               spaces[r][c].addActionListener(new GameplayListener(r,c));
               spaces[r][c].addKeyListener(type);
               game.add(spaces[r][c]);  
               spaces[r][c].setHorizontalAlignment(SwingConstants.CENTER);
            }
         }
         this.add(game,BorderLayout.CENTER);
      
         scoreboard = new Scoreboard();
         this.add(scoreboard,BorderLayout.NORTH);
      }
      
      //generate a random board
      do{
         for(int r =0; r < size; r++){
            for(int c=0; c < size; c++){
               data[r][c] = ((int)(Math.random()*2) == 0); //Set each square to a random value
               setIcon(r,c,data[r][c]);
            }
         }      
      }while(!isSolvable());
      
      locked = false;
      freeplay = false;
      scoreboard.update(true,false); //set moves back to 0
   }
   
   //pre: data is populated and either 5x5 or 6x6
   //post: returns whether or not the board represented in data is possible to solve
   private boolean isSolvable(){
      //All 6x6 boards are mathematically solveable
      if(data.length == 6){
         return true;      
      }
      //Turn our board into a 25 - length vector in Z2
      int[] bVector = new int[25];
      for(int r =0; r < data.length; r++){
         for(int c=0; c < data[0].length; c++){
            bVector[(5*r)+c] = data[r][c] ? 1 : 0;
         }
      }
      //return true if bVector forms an orthogonal basis for null(E), where E is the space of solveable boards
      //basically, linear algebra gibberish says the board will work
      if(innerProduct(bVector,n1) == 0 && innerProduct(bVector,n2) == 0){
         return true;
      }
      return false;
   }
   
   //pre: v1 and v2 are the same length
   //post: returns the innerProduct in Z2 (x•y = z, where for each k zk = xk*yk % 2)
   private int innerProduct(int[] v1, int[] v2){
      int product = 0;
      for(int i = 0; i < v1.length; i++){
         product += v1[i] * v2[i];
      }
      product = product % 2;
      
      return product;
   }
   
   
   //pre: row, col must be valid location in data
   //post: toggles nearby lights that are in-bounds
   private void toggleAt(int row, int col){
      if(row - 1 >= 0){
         data[row-1][col] = !data[row-1][col];
         setIcon(row-1,col,data[row-1][col]);
      }
      if(col - 1 >= 0){
         data[row][col-1] = !data[row][col-1];
         setIcon(row,col-1,data[row][col-1]);
      }
      if(row + 1 < data.length){
         data[row+1][col] = !data[row+1][col];
         setIcon(row+1,col,data[row+1][col]);
      }
      if(col + 1 < data[0].length){
         data[row][col+1] = !data[row][col+1];
         setIcon(row,col+1,data[row][col+1]);
      }
      
      data[row][col] = !data[row][col];
      setIcon(row,col,data[row][col]);
         
   
   }

   //post: Changes a light at r,c on or off   
   private void setIcon(int r, int c, boolean turnedOn){
      if(turnedOn){
         spaces[r][c].setIcon(on);
      } 
      else {
         spaces[r][c].setIcon(off);
      }
   
   }
      

   //post: returns false if any lights are still on, true otherwise
   private boolean checkWin(){
      for(int r =0; r < data.length; r++){
         for(int c=0; c < data[0].length; c++){
            if(data[r][c]){
               return false;
            }
         }
      }
      return !freeplay;
   }
   
   
   //ActionListeners for all buttons below
   
   private class GameplayListener implements ActionListener{
      private int r,c;
      public GameplayListener(int row, int col){
         r = row;
         c = col;
      }
      public void actionPerformed(ActionEvent e){
         if(!locked){
            toggleAt(r,c);
            boolean won = checkWin();
            scoreboard.update(won, won);
            locked = won;
            if(won){ //Some sort of pomp is required
               JOptionPane.showMessageDialog( new JOptionPane(),
                  "You Won!", "Congratulations",
                  JOptionPane.INFORMATION_MESSAGE);
            }
         }
      }
   }
   private class ResetListener implements ActionListener{
      public void actionPerformed(ActionEvent e){
         reset();
      }
   }
   private class ToggleListener implements ActionListener{
      public void actionPerformed(ActionEvent e){
         isDeluxe = !isDeluxe;
         if(isDeluxe){
            frame.setTitle("Lights Out! Deluxe!");
         } 
         else {
            frame.setTitle("Lights Out!");
         }
         reset();
      }
   }
   private class ExitListener implements ActionListener{
      public void actionPerformed(ActionEvent e){
         System.exit(0);
      }
   }
   
   
   //what is this, I don't know what this is
   //...
   //shhhhhh
   private class TypeListener implements KeyListener{
      private String secret = "";
      private String solution = "uuddlrlrab";
      public void keyPressed(KeyEvent e){
         int key = e.getKeyCode();
         if(key == KeyEvent.VK_ENTER){
            if (solution.equals(secret)){
               freeplay = true;
               locked = false;
               for(int r =0; r < data.length; r++){
                  for(int c=0; c < data[0].length; c++){
                     data[r][c] = false;
                     setIcon(r,c,data[r][c]);
                  }
               }
               JOptionPane.showMessageDialog( new JOptionPane(),
                  "Tricky Tricky...", "Congratulations",
                  JOptionPane.INFORMATION_MESSAGE);
            }
            secret = "";
         }
         if(key == KeyEvent.VK_UP){
            secret += "u";
         }
         if(key == KeyEvent.VK_DOWN){
            secret += "d";
         }
         if(key == KeyEvent.VK_LEFT){
            secret += "l";
         }
         if(key == KeyEvent.VK_RIGHT){
            secret += "r";
         }
         if(key == KeyEvent.VK_A){
            secret += "a";
         }
         if(key == KeyEvent.VK_B){
            secret += "b";
         }
      }
      public void keyTyped(KeyEvent e){}
      
      public void keyReleased(KeyEvent e){
      }
   } //okay, this actually just implements the konami code to unlock free-play mode
}