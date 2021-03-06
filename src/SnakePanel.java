package src;
import java.awt.Color;
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
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class SnakePanel extends JPanel implements Runnable {
   private final int snakeLength = 6; // initial length of Snake
   private final int cells = 25;
   private final int cellWidth = 20;
   public static int direction, score, high, fruits, fruitWorth, ms;
   private BufferedImage myImage;
   private boolean isPaused, isPlaying, startScreen, keyUp, keyDown, keyLeft, keyRight;
   private int startPos, difficulty;
   private Snake spencer;
   private Apple apple;
   private Border defaultBorder;
   private Scanner infile;
   private JButton speed1, speed2, speed3, speed4, speed5, speed6;
   private AudioPlayer musicPlayer, effectPlayer, losePlayer;
   private String audioFilePath, audioLose, audioBite1;
   private SidePanel sidePanel;
   Thread gameThread;
   
   public SnakePanel() {
      this.setLayout(null);
      this.setDoubleBuffered(true);
      this.addKeyListener(new KeyHandler());
      this.setFocusable(true);
      myImage = new BufferedImage(825, 541, BufferedImage.TYPE_INT_RGB);
      startScreen = true;
      isPaused = isPlaying = false;
      fruitWorth = 100;
      startPos = 1 + (cells / 2) * cellWidth;   // Snake starting position: center of grid
      spencer = new Snake(startPos, snakeLength);
      sidePanel = new SidePanel();

      try {
         infile = new Scanner(new File("./src/SnakeScore.txt"));   		
      } catch(FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,"SnakeScore.txt could not be found.");
            System.exit(0);
      }
      high = infile.nextInt();

      audioFilePath = "./media/Serge Quadrado - Dramatic Piano.wav"; // Music from freemusicarchive.org
      audioBite1 = "./media/apple bite.wav";  // Sound from Zapsplat.com
      audioLose = "./media/lose 1.wav";   // Sound from Zapsplat.com
      musicPlayer = new AudioPlayer(audioFilePath);
      effectPlayer = new AudioPlayer(audioBite1);
      losePlayer = new AudioPlayer(audioLose);


      ImageIcon icon1 = new ImageIcon(getClass().getResource("/media/snail.png"));
      speed1 = new JButton(icon1);
      //speed1 = new JButton(new ImageIcon("./media/snail.png"));
      speed1.setBounds(640, 320, 45, 45);
      defaultBorder = speed1.getBorder(); // save default border look
      speed1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            defaultBorders();
            speed1.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            difficulty = 4;   // = 4 fps = Snake moves 4 times per second
            newGame();
         }
      });
      add(speed1);
      
      ImageIcon icon2 = new ImageIcon(getClass().getResource("/media/turtle.png"));
      speed2 = new JButton(icon2);
      speed2.setBounds(695, 320, 45, 45);
      speed2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            defaultBorders();
            speed2.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            difficulty = 6;
            newGame();
         }
      });
      add(speed2);
      
      ImageIcon icon3 = new ImageIcon(getClass().getResource("/media/elephant.png"));
      speed3 = new JButton(icon3);
      speed3.setBounds(750, 320, 45, 45);
      speed3.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            defaultBorders();
            speed3.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            difficulty = 10;
            newGame();
         }
      });
      add(speed3);
      
      ImageIcon icon4 = new ImageIcon(getClass().getResource("/media/rabbit.png"));
      speed4 = new JButton(icon4);
      speed4.setBounds(640, 375, 45, 45);
      speed4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            defaultBorders();
            speed4.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            difficulty = 15;
            newGame();
         }
      });
      add(speed4);
      
      ImageIcon icon5 = new ImageIcon(getClass().getResource("/media/horse.png"));
      speed5 = new JButton(icon5);
      speed5.setBounds(695, 375, 45, 45);
      speed5.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            defaultBorders();
            speed5.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            difficulty = 20;
            newGame();
         }
      });
      add(speed5);
      
      ImageIcon icon6 = new ImageIcon(getClass().getResource("/media/cheetah.png"));
      speed6 = new JButton(icon6);
      speed6.setBounds(750, 375, 45, 45);
      speed6.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            defaultBorders();
            speed6.setBorder(BorderFactory.createLineBorder(Color.green, 3));
            difficulty = 40;
            newGame();
         }
      });
      add(speed6);
   }

   public void startGameThread() {
      gameThread = new Thread(this);
      gameThread.start();
   }

   @Override
   public void run() {
      double delta = 0;
      long lastTime = System.nanoTime(); // 1 nanosec = 10^-9 second
      long currentTime;

      while(gameThread != null) {
         Thread.yield();
         while(isPlaying && !isPaused && !startScreen) {
            double drawInterval = 1000000000/difficulty; // difficulty dictates FPS
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval; 
            lastTime = currentTime;
            if(delta >= 1) {  
               update();
               repaint();
               delta = 0;
            }
         }
      }
   }

   public void update() {
      ms += 1000 / difficulty; // incrementing time stat

      if(fruitWorth > (50 + (fruits / 10) * 10)) {   // depreciation of apple's worth over time; value floor scaling w fruits eaten
         fruitWorth -= 1;
      }

      if(keyDown) {   // going down (2), can't go up (0)
         if(direction == 0) { // if going up, continue going up
            moveSnake(0, -20);
            } else {
               direction = 2;
               moveSnake(0, 20);
            }
         }
      if(keyUp) {  // going up (0), can't go down (2)
         if(direction == 2) {
            moveSnake(0, 20);
         } else {
            direction = 0;
            moveSnake(0, -20);
         }   
      }
      if(keyLeft) { // going left (3), can't go right (1)
         if(direction == 1) {
               moveSnake(20, 0);
            } else {
               direction = 3;
               moveSnake(-20, 0);
            }
      }
      if(keyRight) { // going right (1), can't go left (3)
        if(direction == 3) {
            moveSnake(-20, 0);
         } else {
            direction = 1;
            moveSnake(20, 0);
         }
      }  
   }
   
   public void paintComponent(Graphics g) { 
      g.drawImage(myImage, 0, 0, getWidth(), getHeight(), null);

      for (int y = 1; y < cellWidth * cells; y += cellWidth) {   // draw grid
         for (int x = 1; x < cellWidth * cells; x += cellWidth) {
            g.drawRect(x, y, cellWidth, cellWidth);
         }
      }      

     sidePanel.draw(g); // main panel
      
      if(startScreen == true) { // start screen
         sidePanel.drawStartScreen(g);
         return;
      }

      spencer.drawSnake(g);
      apple.drawApple(g);

      if(!isPlaying && !startScreen) { // end screen
         sidePanel.drawEndScreen(g);
         buttonsOn();
      }
   }

   public void moveSnake(int x, int y) {
      if(ms > (1000 / difficulty)) { // only move after game has started
         if(spencer.head.xcor + x == apple.getxcor() && spencer.head.ycor + y == apple.getycor()) { // snake eats apple
            effectPlayer.play();
            fruits++;
            score += fruitWorth;
            fruitWorth = 100 + (fruits / 10) * 10; // fruitWorth scales as you eat more
            spencer.move(x, y, true);  // grow
            apple = new Apple(spencer);
         } else {
            int outerBound = (cells - 1) * cellWidth + 1;
            if(spencer.head.xcor + x < 0 || spencer.head.xcor + x > outerBound || spencer.head.ycor + y < 0 || spencer.head.ycor + y > outerBound) { // wall check
               endGame();
            } else if(spencer.ateSelf(x, y) == true && ms > 2 * (1000 / difficulty)) { // self check, 2x b/c 1x is first move
               endGame();
            } else {
               spencer.move(x, y, false); // move
            }
         }
      }
   }

   public void newGame() {
      buttonsOff();
      score = ms = fruits = direction = 0; // direction: 0 = up, 1 = right, 2 = down, 3 = left
      fruitWorth = 100;        
      keyUp = isPlaying = true; // direction = 0 and keyUp = true -> starts game moving upwards
      keyDown = keyRight = keyLeft = startScreen = isPaused = false;
      spencer = new Snake(startPos, snakeLength);
      apple = new Apple(spencer);
      if(losePlayer.hasClip() == true)
         losePlayer.stop();
      musicPlayer.play();
      musicPlayer.loop();
   }

   public void endGame() {
      musicPlayer.stop();
      losePlayer.play();
      isPlaying = false;

      if(score > high) {   
         PrintStream outfile = null;
         try {
            outfile = new PrintStream(new FileOutputStream(("./src/SnakeScore.txt")));
         }
            catch(FileNotFoundException f) {
               JOptionPane.showMessageDialog(null,"The file could not be created.");
            }
         outfile.println(score);
         outfile.println(fruits);
         outfile.println((double) ms / 1000);
         outfile.close();
      }
   }

   public void buttonsOff() {
      speed1.setEnabled(false);
      speed2.setEnabled(false);
      speed3.setEnabled(false);
      speed4.setEnabled(false);
      speed5.setEnabled(false);
      speed6.setEnabled(false);
   }

   public void buttonsOn() {
      speed1.setEnabled(true);
      speed2.setEnabled(true);
      speed3.setEnabled(true);
      speed4.setEnabled(true);
      speed5.setEnabled(true);
      speed6.setEnabled(true);
   }

   public void defaultBorders() {
      speed1.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed2.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed3.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed4.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed5.setBorder(BorderFactory.createTitledBorder(defaultBorder));
      speed6.setBorder(BorderFactory.createTitledBorder(defaultBorder));   
   }

   public class KeyHandler extends KeyAdapter {
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
         if(e.getKeyCode() == KeyEvent.VK_P) {   // pausing game
            if(isPlaying) {
               if(isPaused) {
                  musicPlayer.resume();
                  isPaused = false;
               }
               else {
                  musicPlayer.stop();
                  isPaused = true;
               }
            }
         }
         if(!isPlaying && !startScreen) {    // restarting or exiting game
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
               newGame();
            }
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
               System.exit(0);
            }
         }
      }
   }
}