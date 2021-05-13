import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Random;

//commit

public class GamePanel extends JPanel implements ActionListener {
    //Variabelen declareren
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten; //(begint op nul)
    int appleX;
    int appleY;
    char direction = 'R'; //begint met naar rechts bewegen
    boolean running = false;
    Timer timer;
    Random random;


    //Constructor
    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    //Methods:
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this); //nu doet hij het wel; vergeten om "implements ActionListener" aan R10 toe te voegen
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Er een grid "tekenen" van maken met deze methode
        if(running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            //De appel "tekenen"
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            //Hoofd van de slang "tekenen en body ("else")
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else { //body
                    //1 kleurige slang:
                    //g.setColor(new Color(45, 180, 0)); //andere kleur groen
                    //Disco slang:
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);

                }
            }
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score:" + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score:" + applesEaten)) /2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }


    }

    public void newApple() {
        //Genereren van de coordinaten van de nieuwe appel
        appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for(int i = bodyParts; i>0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        //Method for grabbing the apple
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for(int i = bodyParts; i >0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) { //Hier aan elkaar gelijk; als dit true is dan is de head met de body gebotst
                running = false; //en is het dus "Game Over".
            }
            //checks if head touches left border
            if(x[0] < 0) {
                running = false;
            }
            //check if head touches right border
            if(x[0] > SCREEN_WIDTH) {
                running = false;
            }
            //check if head touches top border
            if(y[0] < 0) {
                running = false;
            }
            //check if head touches bottom border
            if(y[0] > SCREEN_HEIGHT) {
                running = false;
            }

            if(!running) {
                timer.stop();
            }

        }
    }

    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score:" + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score:" + applesEaten)) /2, g.getFont().getSize());

        //Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) /2, SCREEN_HEIGHT / 2);

    }

    //(Adding the unimplemented method)
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    //Create inner class
    public class MyKeyAdapter extends KeyAdapter {
        //Omdat hier een methode in gaat, ga je dus een methode overriden; toevoegen @Override
        @Override
        public void keyPressed(KeyEvent e) {
            //control the snake
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') { //Limit user to 90 degree turns only
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') { //Limit user to 90 degree turns only
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') { //Limit user to 90 degree turns only
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') { //Limit user to 90 degree turns only
                        direction = 'D';
                    }
                    break;
            }

        }
    }
}

