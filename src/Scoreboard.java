import java.awt.*;
import javax.swing.*;

public class Scoreboard extends JPanel{
   private int moves, lowScore, wins;
   private JLabel mLabel, lLabel,wLabel;
   
   public Scoreboard(){
      setLayout(new FlowLayout());
      moves = wins = 0;
      lowScore = Integer.MAX_VALUE;
      wLabel = new JLabel("Wins: 0");
      lLabel = new JLabel("Low Score:  ");
      mLabel = new JLabel("Current Move: 0");
      add(wLabel);
      add(new JLabel("     "));
      add(lLabel);
      add(new JLabel("     "));
      add(mLabel);
   }
   //Increments moves
   //If it is the end of the game (win, reset), reset moves
   //If it was a win, increment wins and checks min
   public void update(boolean isEnd, boolean isWin){
      moves++;
      if(isEnd){
         if(isWin){
            wins++;
            if (moves < lowScore){
               lowScore = moves;
               lLabel.setText("Low Score: " + lowScore);
            }
         }
         wLabel.setText("Wins: " + wins);
         moves = 0;
      }
      mLabel.setText("Current Move: " + moves);
   }

}