package org.newdawn.spaceinvaders;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputKey;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputMouseMove;
import org.newdawn.spaceinvaders.loop.Loop;
import org.newdawn.spaceinvaders.loop.MainMenuLoop;
import org.newdawn.spaceinvaders.map_load.MapList;
import org.newdawn.spaceinvaders.singleton.PlayerSetting;
import org.newdawn.spaceinvaders.singleton.SystemTimer;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic.
 *
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 *
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 *
 * @author Kevin Glass
 */
public class Game extends Canvas
{
    public final long fixedFPS;
    public final long fixedDeltaTime;

    private Loop loop = new MainMenuLoop(this);

    private MapList mapList;

	/** The stragey that allows us to use accelerate page flipping */
	private BufferStrategy strategy;
    /** True if the game is currently "running", i.e. the game loop is looping */
    private boolean gameRunning = true;

	/** The last time at which we recorded the frame rate */
	private long lastFpsTime;
	/** The current number of frames recorded */
	private int fps;
	/** The normal title of the game window */
	private String windowTitle = "Space Invaders 102";
	/** The game window that we'll update with the frame count */
	private JFrame container;

    private ArrayList<GameLoopInput> queuedInputs = new ArrayList<>();

	/**
	 * Construct our game and set it running.
	 */
	public Game(long fixedFPS) {
        this.fixedFPS = fixedFPS;
        this.fixedDeltaTime = FixedPointUtil.div(FixedPointUtil.ONE, fixedFPS);

        mapList = new MapList();

		// create a frame to contain our game
		container = new JFrame("Space Invaders 102");
		
		// get hold the content of the frame and set up the resolution of the game
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800,600));
		panel.setLayout(null);
		
		// setup our canvas size and put it into the content of the frame
		setBounds(0,0,800,600);
		panel.add(this);
		
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);
		
		// finally make the window visible 
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
		addKeyListener(new KeyInputHandler());

        addMouseListener(new MouseInputHandler());

        addMouseMotionListener(new MouseInputHandler());
		
		// request the focus so key events come to us
		requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

	}

	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:
	 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 */
	public void loop() {
		long lastLoopTime = SystemTimer.getTime();
		
		// keep looping round til the game ends
		while (gameRunning) {
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = SystemTimer.getTime() - lastLoopTime;
			lastLoopTime = SystemTimer.getTime();

			// update the frame counter
			lastFpsTime += delta;
			fps++;
			
			// update our FPS counter if a second has passed since
			// we last recorded
			if (lastFpsTime >= 1000) {
				container.setTitle(windowTitle+" (FPS: "+fps+")");
				lastFpsTime = 0;
				fps = 0;
			}
			
			// Get hold of a graphics context for the accelerated 
			// surface and blank it out
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,800,600);

            if(queuedInputs.isEmpty()){
                loop.process();
            }
            else{
                // process() 작동 중 들어오는 입력을 놓치지 않기 위하여
                // process() 이전에 미리 교체
                ArrayList<GameLoopInput> inputsForThisFrame = queuedInputs;
                queuedInputs = new ArrayList<GameLoopInput>();

                loop.process(inputsForThisFrame);
            }

            loop.draw(g);

            // finally, we've completed drawing so clear up the graphics
            // and flip the buffer over
            g.dispose();
            strategy.show();
			
			// we want each frame to take 10 milliseconds, to do this
			// we've recorded when we started the frame. We add 10 milliseconds
			// to this and then factor in the current time to give 
			// us our final value to wait for
			SystemTimer.sleep(lastLoopTime+(long)(FixedPointUtil.toDouble(fixedDeltaTime)*1000)-SystemTimer.getTime());
		}
	}
	
	/**
	 * A class to handle keyboard input from the user. The class
	 * handles both dynamic input during game play, i.e. left/right 
	 * and shoot, and more static type input (i.e. press any key to
	 * continue)
	 * 
	 * This has been implemented as an inner class more through 
	 * habbit then anything else. Its perfectly normal to implement
	 * this as seperate class if slight less convienient.
	 * 
	 * @author Kevin Glass
	 */
	private class KeyInputHandler extends KeyAdapter {
		/** The number of key presses we've had while waiting for an "any key" press */
		private int pressCount = 1;
		
		/**
		 * Notification from AWT that a key has been pressed. Note that
		 * a key being pressed is equal to being pushed down but *NOT*
		 * released. Thats where keyTyped() comes in.
		 *
		 * @param e The details of the key that was pressed 
		 */
		public void keyPressed(KeyEvent e) {
            PlayerSetting playerSetting = PlayerSetting.getInstance();

            String inputName = playerSetting.KeyToInputName(e.getKeyCode());
            if(inputName == null) return;

            queuedInputs.add(new GameLoopInputKey(inputName, true));
		} 
		
		/**
		 * Notification from AWT that a key has been released.
		 *
		 * @param e The details of the key that was released 
		 */
		public void keyReleased(KeyEvent e) {
            PlayerSetting playerSetting = PlayerSetting.getInstance();

            String inputName = playerSetting.KeyToInputName(e.getKeyCode());
            if(inputName == null) return;

            queuedInputs.add(new GameLoopInputKey(inputName, false));
		}

		/**
		 * Notification from AWT that a key has been typed. Note that
		 * typing a key means to both press and then release it.
		 *
		 * @param e The details of the key that was typed. 
		 */
		public void keyTyped(KeyEvent e) {
		}
	}

    private class MouseInputHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);

            PlayerSetting playerSetting = PlayerSetting.getInstance();

            int buttonCode;
            if(e.getButton() == MouseEvent.BUTTON1){
                buttonCode = playerSetting.MOUSE_BUTTON_LEFT;
            }
            else if(e.getButton() == MouseEvent.BUTTON3){
                buttonCode = playerSetting.MOUSE_BUTTON_RIGHT;
            }
            else{
                buttonCode = -9999;
            }

            String inputName = playerSetting.KeyToInputName(buttonCode);
            if(inputName == null) return;

            queuedInputs.add(new GameLoopInputKey(inputName, true));
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);

            PlayerSetting playerSetting = PlayerSetting.getInstance();

            int buttonCode;
            if(e.getButton() == MouseEvent.BUTTON1){
                buttonCode = playerSetting.MOUSE_BUTTON_LEFT;
            }
            else if(e.getButton() == MouseEvent.BUTTON3){
                buttonCode = playerSetting.MOUSE_BUTTON_RIGHT;
            }
            else{
                buttonCode = -9999;
            }

            String inputName = playerSetting.KeyToInputName(buttonCode);
            if(inputName == null) return;

            queuedInputs.add(new GameLoopInputKey(inputName, false));
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);

            queuedInputs.add(new GameLoopInputMouseMove(e.getX(), e.getY()));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);

            queuedInputs.add(new GameLoopInputMouseMove(e.getX(), e.getY()));
        }
    }



    public void changeLoop(Loop loop) {
        this.loop = loop;
    }


    /**
     * The entry point into the game. We'll simply create an
     * instance of class which will start the display and game
     * loop.
     *
     * @param argv The arguments that are passed into our games
     */
	public static void main(String[] argv) {
        SpriteStore.get().getSprite("sprites/ship.gif", (16 << 16) + FixedPointUtil.ZERO_5, (11 << 16) + FixedPointUtil.ZERO_5);
        SpriteStore.get().getSprite("sprites/shot.gif", 6L << 16, (11 << 16) + FixedPointUtil.ZERO_5);
        SpriteStore.get().getSprite("sprites/alien.gif", (21 << 16) + FixedPointUtil.ZERO_5, (14 << 16) + FixedPointUtil.ZERO_5);
        SpriteStore.get().getSprite("sprites/alien2.gif", (21 << 16) + FixedPointUtil.ZERO_5, (14 << 16) + FixedPointUtil.ZERO_5);
        SpriteStore.get().getSprite("sprites/alien3.gif", (21 << 16) + FixedPointUtil.ZERO_5, (14 << 16) + FixedPointUtil.ZERO_5);

		Game g = new Game(60L << 16);

		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		g.loop();
	}
}
