import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

public class SnakePanel extends JPanel
{
   private BufferedImage myImage;
   private boolean keyUp,keyDown,keyLeft,keyRight,isPlaying;
   private int startPos,snakeLength,high,fruits,fruitWorth,score,ms,direction,cells,cellWidth,difficulty;
   private Snake spencer;
   private Apple apple;
   private DecimalFormat deci;
   private Border defaultBorder;
   private Timer timer;
   private Scanner infile;
   private JButton speed1, speed2, speed3, speed4, speed5, speed6;
   private AudioPlayer player;
   private String audioFilePath;
   
   public SnakePanel()
   {
      this.setLayout(null);
      myImage = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB); //size doesn't matter?
      keyUp = isPlaying = true;    //starts game moving upwards
      keyDown = keyLeft = keyRight = false;
      fruits = score = direction = 0;  //direction: 0 = up, 1 = right, 2 = down, 3 = left
      fruitWorth = 100; 
      snakeLength = 6;
      cells = 25;    //# of cells in grid
      cellWidth = 20;   //dimension of cell
      startPos = 1+(cells/2)*cellWidth;   //starting position is center of grid

      //*create an algorithm that SOLVES/beats SNEK (most efficient path while surviving)
      //based on cur pos & apple pos, finds optimal path ONCE, store solution in data structure,
      //if not empty: follow solution;
      //hard part: optimal path algorithm

      //problem 1: not v responsive at slower time controls
      //problem 2: after dying, if you change time control, time increments by one frame, affecting score

      //feature add 1: size scalability - drag compatibility
      //feature add 2: custom m x n grid

      audioFilePath = "./media/Interstellar OST - No Time for Caution.wav";
      // audioFilePath = "./media/Serge Quadrado - Dramatic Piano.wav";
      player = new AudioPlayer();

      // URL urlToImg = this.getClass().getResource("/media/snail.png"); //alternative way to access img in diff folder
      // speed1 = new JButton(new ImageIcon(urlToImg));
      
      speed1 = new JButton(new ImageIcon("./media/snail.png"));
      speed1.setBounds(640, 320, 45, 45);
      defaultBorder = speed1.getBorder();
      speed1.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            difficulty = 200;
            startTimer();
            defaultBorders();
            buttonsOff();
            speed1.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            player.play(audioFilePath);
         }
      });
      add(speed1);
      
      speed2 = new JButton(new ImageIcon("./media/turtle.png"));
      speed2.setBounds(695, 320, 45, 45);
      speed2.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            difficulty = 150;
            startTimer();
            defaultBorders();
            buttonsOff();
            speed2.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            player.play(audioFilePath);
         }
      });
      add(speed2);
      
      speed3 = new JButton(new ImageIcon("./media/elephant.png"));
      speed3.setBounds(750, 320, 45, 45);
      speed3.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            difficulty = 100;
            startTimer();
            defaultBorders();
            buttonsOff();
            speed3.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            player.play(audioFilePath);
         }
      });
      add(speed3);
      
      speed4 = new JButton(new ImageIcon("./media/rabbit.png"));
      speed4.setBounds(640, 375, 45, 45);
      speed4.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            difficulty = 75;
            startTimer();
            defaultBorders();
            buttonsOff();
            speed4.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            player.play(audioFilePath);
         }
      });
      add(speed4);
      
      speed5 = new JButton(new ImageIcon("./media/horse.png"));
      speed5.setBounds(695, 375, 45, 45);
      speed5.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            difficulty = 50;
            startTimer();
            defaultBorders();
            buttonsOff();
            speed5.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            player.play(audioFilePath);
         }
      });
      add(speed5);
      
      speed6 = new JButton(new ImageIcon("./media/cheetah.png"));
      speed6.setBounds(750, 375, 45, 45);
      speed6.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            difficulty = 25;
            startTimer();
            defaultBorders();
            buttonsOff();
            speed6.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            player.play(audioFilePath);
         }
      });
      add(speed6);
      
      
      try{
         infile = new Scanner(new File("snakeScore.txt"));   		
      }
         catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null,"snakeScore.txt could not be found.");
            System.exit(0);
         }
      high = infile.nextInt();

      deci = new DecimalFormat("0.00");
      spencer = new Snake(startPos, snakeLength);
      apple = new Apple(spencer);

      addKeyListener(new Key());
      setFocusable(true);
   }

   public void startTimer(){
      timer = new Timer(difficulty, new Listener());
      timer.start();
   }
   public void buttonsOff(){
      speed1.setEnabled(false);
      speed2.setEnabled(false);
      speed3.setEnabled(false);
      speed4.setEnabled(false);
      speed5.setEnabled(false);
      speed6.setEnabled(false);
   }
   public void buttonsOn(){
      speed1.setEnabled(true);
      speed2.setEnabled(true);
      speed3.setEnabled(true);
      speed4.setEnabled(true);
      speed5.setEnabled(true);
      speed6.setEnabled(true);
   }
   public void defaultBorders(){
      speed1.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed2.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed3.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed4.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed5.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed6.setBorder(BorderFactory.createTitledBorder(defaultBorder));   
   }

   public class Listener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         ms+=difficulty;      //counting time
         if(fruitWorth>10){   //depreciation of apple's worth over time
            fruitWorth-=1;
         }
         repaint();  //update graphics every "difficulty" milliseconds
      }
   }
   public boolean hitWall(int x, int y){
      if(x < 0 || x > 481 || y < 0 || y > 481)
         return true;
      return false;
   }
   public void moveSnake(int x, int y, Graphics g){
      if(ms > difficulty){ //only move after game has started - no preemptive moving
         if(spencer.head.xcor + x == apple.getxcor() && spencer.head.ycor + y == apple.getycor()){   //snake eats apple
            fruits++;
            score += fruitWorth;    //25*25-6=619, tiers of worth after 50, 100, 150, etc.
            if(fruits>50){
               fruitWorth = (fruits/50)*50+100;
            }else{
               fruitWorth = 100;
            }
            spencer.move(x, y, true);  //grow
            apple = new Apple(spencer);
         }else{
            if(hitWall(spencer.head.xcor+x,spencer.head.ycor+y)){  //wall check
               endGame();
            }
            else if(spencer.ateSelf(x, y) == true && ms > 2*difficulty){ //self check, 2x bc 1x is first move
               endGame();
            }
            else{
               spencer.move(x, y, false); //move
            }
         }
      }

      g.setColor(Color.red); //draw Apple
      g.fillOval(apple.getxcor(), apple.getycor(), 20, 20); 


      Snake.Node temp = spencer.head;
      while(temp != null){ //draw snake body
         g.setColor(Color.black);   //borders of segments
         g.drawRect(temp.xcor, temp.ycor, 20, 20);
         g.setColor(Color.green);   //body
         g.fillRect(temp.xcor + 1, temp.ycor + 1, 19, 19);
         temp = temp.next;
      }

      temp = spencer.head; //drawing eyes and tongue
      if(direction == 0){ //up
         g.setColor(Color.black);
         g.fillRect(temp.xcor + 6, temp.ycor + 3, 1, 6);
         g.fillRect(temp.xcor + 14, temp.ycor + 3, 1, 6);
         g.setColor(Color.red);
         g.drawLine(temp.xcor + 10, temp.ycor, temp.xcor + 10, temp.ycor - 3);
         g.drawLine(temp.xcor + 10, temp.ycor - 3, temp.xcor + 8, temp.ycor - 5);
         g.drawLine(temp.xcor + 10, temp.ycor - 3, temp.xcor + 12, temp.ycor - 5);
      }
      else if(direction == 2){ //down
         g.setColor(Color.black);
         g.fillRect(temp.xcor + 6, temp.ycor + 11, 1, 6);
         g.fillRect(temp.xcor + 14, temp.ycor + 11, 1, 6);
         g.setColor(Color.red);
         g.drawLine(temp.xcor + 10, temp.ycor + 20, temp.xcor + 10, temp.ycor + 23);
         g.drawLine(temp.xcor + 10, temp.ycor + 23, temp.xcor + 8, temp.ycor + 25);
         g.drawLine(temp.xcor + 10, temp.ycor + 23, temp.xcor + 12, temp.ycor + 25);
      }
      else if(direction == 1){ //right
         g.setColor(Color.black);
         g.fillRect(temp.xcor + 11, temp.ycor + 6, 6, 1);
         g.fillRect(temp.xcor + 11, temp.ycor + 14, 6, 1);
         g.setColor(Color.red);
         g.drawLine(temp.xcor + 20, temp.ycor + 10, temp.xcor + 23, temp.ycor + 10);
         g.drawLine(temp.xcor + 23, temp.ycor + 10, temp.xcor + 25, temp.ycor + 8);
         g.drawLine(temp.xcor + 23, temp.ycor + 10, temp.xcor + 25, temp.ycor + 12);
      }
      else{ //left
         g.setColor(Color.black);
         g.fillRect(temp.xcor + 3, temp.ycor + 6, 6, 1);
         g.fillRect(temp.xcor + 3, temp.ycor + 14, 6, 1);
         g.setColor(Color.red);
         g.drawLine(temp.xcor, temp.ycor + 10, temp.xcor - 3, temp.ycor + 10);
         g.drawLine(temp.xcor - 3, temp.ycor + 10, temp.xcor - 5, temp.ycor + 8);
         g.drawLine(temp.xcor - 3, temp.ycor + 10, temp.xcor - 5, temp.ycor + 12);
      }
   }
   
   public void paintComponent(Graphics g)
   { 
      g.drawImage(myImage, 0, 0, getWidth(), getHeight(), null);

      for (int y = 1; y < cellWidth * cells; y += cellWidth){   //draw grid
         for (int x = 1; x < cellWidth * cells; x += cellWidth){
            g.drawRect(x, y, cellWidth, cellWidth);
         }
      }

      if(keyDown){   //going down (2), can't up (0)
         if(direction == 0){
            moveSnake(0, -20, g);
         }else{
            direction = 2;
            moveSnake(0, 20, g);
         }
      }
      if(keyUp){  //going up (0), can't down (2)
         if(direction == 2){
            moveSnake(0, 20, g);
         }else{
            direction = 0;
            moveSnake(0, -20, g);
         }   
      }
      if(keyLeft){ //going left (3), can't right (1)
         if(direction == 1){
            moveSnake(20, 0, g);
         }else{
            direction = 3;
            moveSnake(-20, 0, g);
         }
      }
      if(keyRight){ //going right (1), can't left (3)
        if(direction == 3){
            moveSnake(-20, 0, g);
         }else{
            direction = 1;
            moveSnake(20, 0, g);
         }
      }

      g.setColor(Color.yellow);
      g.setFont(new Font("Monospaced", Font.BOLD, 30));;
      g.drawString("Snek", 620, 80);

      g.setColor(Color.white);
      g.setFont(new Font("Monospaced", Font.PLAIN, 18));
      g.drawString("Score:" + score, 530, 120);
      g.drawString("Fruits Eaten:" + fruits, 530, 150);
      g.drawString("Fruit Value:" + fruitWorth, 530, 180);
      g.drawString("Time:" + deci.format((double)ms/1000), 530, 210);
      g.drawString("High Score:" + high, 530, 240);

      g.drawString("Up/W", 530, 330);
      g.drawString("Down/S", 530, 360);
      g.drawString("Left/A", 530, 390);
      g.drawString("Right/D", 530, 420);
      g.drawString("Pause:P", 530, 450);
      g.setFont(new Font("Monospaced", Font.BOLD, 18));
      g.drawString("Controls:", 530, 300);
      g.drawString("Difficulty:", 660, 300);

      if(isPlaying == false){
         buttonsOn();
         if(score>high){
            high = score;
            g.setFont(new Font("Monospaced", Font.BOLD, 35));
            g.setColor(Color.orange);
            g.drawString("NEW HIGH SCORE!!!", 85, 180);
            g.setColor(Color.red);
            g.drawString("GAME OVER", 160, 255);
            g.setFont(new Font("Monospaced", Font.PLAIN, 35));
            g.drawString("Press Enter to Restart", 25, 330);
         } else{
            g.setColor(Color.red);
            g.setFont(new Font("Monospaced", Font.BOLD, 35));
            g.drawString("GAME OVER", 160, 200);
            g.drawString("Press Enter to Restart", 25, 275);
         }
      }
   }

   public class Key extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
         if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {  
            keyUp = true;
            keyDown = keyLeft = keyRight = false;
         }
         if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) { 
            keyDown = true;
            keyUp = keyLeft = keyRight = false;
         }
         if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) { 
            keyLeft = true;
            keyUp = keyDown = keyRight = false;
         }
         if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) { 
            keyRight = true;
            keyDown = keyLeft = keyUp = false;
         }
         if(e.getKeyCode() == KeyEvent.VK_P && timer != null){   //pausing game
            if(timer.isRunning())
               timer.stop();
            else
               if(isPlaying)
                  timer.start();
         }
         if(isPlaying == false){    //restarting or exiting game
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
               score = ms = fruits = direction = 0;
               fruitWorth = 100;        
               keyUp = isPlaying = true;
               keyDown = keyRight = keyLeft = false;
               buttonsOff();
               spencer = new Snake(startPos, snakeLength);
               apple = new Apple(spencer);
               startTimer();
               player.play(audioFilePath);
            }
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
               System.exit(0);
            }
         }
      }
   }
   public void endGame(){
      timer.stop();
      player.stop();
      isPlaying = false;

      if(score>high){   
         PrintStream outfile = null;
         try{
            outfile = new PrintStream(new FileOutputStream(("snakeScore.txt")));
         }
            catch(FileNotFoundException f){
               JOptionPane.showMessageDialog(null,"The file could not be created.");
            }
         outfile.println(score);
         outfile.println(fruits);
         outfile.println(deci.format((double)ms/1000));
         outfile.close();
      }
   }
}