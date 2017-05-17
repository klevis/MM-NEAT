package edu.utexas.cs.nn.tasks.interactive;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.evolution.mutation.tweann.ActivationFunctionRandomReplacement;
import edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.BooleanUtil;
import edu.utexas.cs.nn.util.CombinatoricUtilities;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;

import java.util.HashSet;
import java.util.Hashtable;



/**
 * Implementation of picbreeder that uses Java Swing components for graphical interface
 * 
 * @author Isabel Tweraser
 *
 * @param <T>
 */
public abstract class InteractiveEvolutionTask<T extends Network> implements SinglePopulationTask<T>, ActionListener, ChangeListener, NetworkTask {

	//Global static final variables
	public static final int NUM_COLUMNS	= 3;
	public static final int MPG_DEFAULT = 1;// Starting number of mutations per generation (on slider)	

	//private static final Variables
	//includes indices of buttons for action listener
	private static final int IMAGE_BUTTON_INDEX = 0;
	private static final int EVOLVE_BUTTON_INDEX = -1;
	private static final int SAVE_BUTTON_INDEX = -2;
	private static final int RESET_BUTTON_INDEX = -3;
	private static final int CLOSE_BUTTON_INDEX	= -4;
	private static final int LINEAGE_BUTTON_INDEX = -5;
	private static final int NETWORK_BUTTON_INDEX = -6;
	private static final int UNDO_BUTTON_INDEX = -7;
	private static final int SIGMOID_CHECKBOX_INDEX = -8;
	private static final int GAUSSIAN_CHECKBOX_INDEX = -9;
	private static final int SINE_CHECKBOX_INDEX = -10;
	private static final int SAWTOOTH_CHECKBOX_INDEX = -11;
	private static final int ABSVAL_CHECKBOX_INDEX = -12;
	private static final int HALF_LINEAR_CHECKBOX_INDEX = -13;
	private static final int TANH_CHECKBOX_INDEX = -14;
	private static final int ID_CHECKBOX_INDEX = -15;
	private static final int FULLAPPROX_CHECKBOX_INDEX = -16;
	private static final int APPROX_CHECKBOX_INDEX = -17;
	private static final int STRETCHTANH_CHECKBOX_INDEX = -18;
	private static final int RELU_CHECKBOX_INDEX = -19;//TODO
	private static final int SOFTPLUS_CHECKBOX_INDEX = -20;
	private static final int LEAKY_RELU_CHECKBOX_INDEX = -21;
	private static final int FULL_SAWTOOTH_CHECKBOX_INDEX = -22;
	private static final int TRIANGLE_WAVE_CHECKBOX_INDEX = -23;
	private static final int SQUARE_WAVE_CHECKBOX_INDEX = -24;
	private static final int BORDER_THICKNESS = 4;
	private static final int MPG_MIN = 0;//minimum # of mutations per generation
	private static final int MPG_MAX = 10;//maximum # of mutations per generation
	
	// Activation Button Widths and Heights
	private static final int ACTION_BUTTON_WIDTH = 80;
	private static final int ACTION_BUTTON_HEIGHT = 60;	
	
	//Private final variables
	private static int NUM_ROWS;
	private static int PIC_SIZE;
	private static int NUM_BUTTONS;

	//Private graphic objects
	private JFrame frame;
	private ArrayList<JPanel> panels;
	private ArrayList<JButton> buttons;
	private ArrayList<Score<T>> scores;
	private ArrayList<Score<T>> previousScores;

	//private helper variables
	private boolean showLineage;
	private boolean showNetwork;
	private boolean waitingForUser;
	private final boolean[] chosen;
	private final boolean[] activation;
	private static double[] inputMultipliers = new double[4];

	private JPanel topper;
	
	/**
	 * Default Constructor
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public InteractiveEvolutionTask() throws IllegalAccessException {		
		MMNEAT.registerFitnessFunction("User Preference");
		//sets mu to a divisible number
		if(Parameters.parameters.integerParameter("mu") % InteractiveEvolutionTask.NUM_COLUMNS != 0) { 
			Parameters.parameters.setInteger("mu", InteractiveEvolutionTask.NUM_COLUMNS * ((Parameters.parameters.integerParameter("mu") / InteractiveEvolutionTask.NUM_COLUMNS) + 1));
			System.out.println("Changing population size to: " + Parameters.parameters.integerParameter("mu"));
		}

		//Global variable instantiations
		NUM_BUTTONS	= Parameters.parameters.integerParameter("mu");
		NUM_ROWS = NUM_BUTTONS / NUM_COLUMNS;
		PIC_SIZE = Parameters.parameters.integerParameter("imageSize");
		chosen = new boolean[NUM_BUTTONS];
		showLineage = false;
		showNetwork = false;
		waitingForUser = false;
		activation = new boolean[Math.abs(SQUARE_WAVE_CHECKBOX_INDEX) + 1];//magic number is number of activation functions
		Arrays.fill(activation, true);
		if(MMNEAT.browseLineage) {
			// Do not setup the JFrame if browsing the lineage
			return;
		}            

		//Graphics instantiations
		frame = new JFrame(getWindowTitle());
		panels = new ArrayList<JPanel>();
		buttons = new ArrayList<JButton>();

		//sets up JFrame
		
		
		//frame.setSize(PIC_SIZE * NUM_COLUMNS + 200, PIC_SIZE * NUM_ROWS + 700);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		PIC_SIZE = frame.getWidth() / NUM_COLUMNS;
		frame.setLocation(300, 100);//magic #s 100 correspond to relocating frame to middle of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(NUM_ROWS + 1, 0));// the + 1 includes room for the title panel
		frame.setVisible(true);

		//instantiates helper buttons
		topper = new JPanel();
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		
		// Gets the Button Images from the Picbreeder data Folder and re-scales them for use on the smaller Action Buttons
		ImageIcon reset = new ImageIcon("data\\picbreeder\\reset.png");
		Image reset2 = reset.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		ImageIcon save = new ImageIcon("data\\picbreeder\\save.png");
		Image save2 = save.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		ImageIcon evolve = new ImageIcon("data\\picbreeder\\arrow.png");
		Image evolve2 = evolve.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		ImageIcon close = new ImageIcon("data\\picbreeder\\quit.png");
		Image close2 = close.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);
		
		ImageIcon lineage = new ImageIcon("data\\picbreeder\\lineage.png");
		Image lineage2 = lineage.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		ImageIcon network = new ImageIcon("data\\picbreeder\\network.png");
		Image network2 = network.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);
		
		ImageIcon undo = new ImageIcon("data\\picbreeder\\undo.png");
		Image undo2 = undo.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);
		
		JButton resetButton = new JButton(new ImageIcon(reset2));
		JButton saveButton = new JButton(new ImageIcon(save2));
		JButton evolveButton = new JButton(new ImageIcon(evolve2));
		JButton closeButton = new JButton(new ImageIcon(close2));
		JButton lineageButton = new JButton(new ImageIcon(lineage2));
		JButton networkButton = new JButton(new ImageIcon(network2));
		JButton undoButton = new JButton( new ImageIcon(undo2));

		//to make it work on my mac
		resetButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		saveButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		evolveButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		lineageButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		networkButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		undoButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		closeButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		
		resetButton.setText("Reset");
		saveButton.setText("Save");
		evolveButton.setText("Evolve!");
		lineageButton.setText("Lineage");
		networkButton.setText("Network");
		undoButton.setText("Undo");
		closeButton.setText("Close");
		
		

		
		//instantiates activation function checkboxes
		JCheckBox sigmoid = new JCheckBox("sigmoid", CommonConstants.includeSigmoidFunction);
		activation[Math.abs(SIGMOID_CHECKBOX_INDEX)] = CommonConstants.includeSigmoidFunction;
		JCheckBox tanh = new JCheckBox("tanh", CommonConstants.includeTanhFunction);
		activation[Math.abs(TANH_CHECKBOX_INDEX)] = CommonConstants.includeTanhFunction;
		JCheckBox id = new JCheckBox("ID", CommonConstants.includeIdFunction);
		activation[Math.abs(ID_CHECKBOX_INDEX)] = CommonConstants.includeIdFunction;
		JCheckBox fullApprox = new JCheckBox("full_approximate", CommonConstants.includeFullApproxFunction);
		activation[Math.abs(FULLAPPROX_CHECKBOX_INDEX)] = CommonConstants.includeFullApproxFunction;
		JCheckBox approx = new JCheckBox("approximate", CommonConstants.includeApproxFunction);
		activation[Math.abs(APPROX_CHECKBOX_INDEX)] = CommonConstants.includeApproxFunction;
		JCheckBox gaussian = new JCheckBox("gaussian", CommonConstants.includeGaussFunction);
		activation[Math.abs(GAUSSIAN_CHECKBOX_INDEX)] = CommonConstants.includeGaussFunction;
		JCheckBox sine = new JCheckBox("sine", CommonConstants.includeSineFunction);
		activation[Math.abs(SINE_CHECKBOX_INDEX)] = CommonConstants.includeSineFunction;
		JCheckBox sawtooth = new JCheckBox("sawtooth", CommonConstants.includeSawtoothFunction);
		activation[Math.abs(SAWTOOTH_CHECKBOX_INDEX)] = CommonConstants.includeSawtoothFunction;
		JCheckBox absVal = new JCheckBox("absolute_value", CommonConstants.includeAbsValFunction);
		activation[Math.abs(ABSVAL_CHECKBOX_INDEX)] = CommonConstants.includeAbsValFunction;
		JCheckBox halfLinear = new JCheckBox("half_linear", CommonConstants.includeHalfLinearPiecewiseFunction);
		activation[Math.abs(HALF_LINEAR_CHECKBOX_INDEX)] = CommonConstants.includeHalfLinearPiecewiseFunction;
		JCheckBox stretchTanh = new JCheckBox("stretched_tanh", CommonConstants.includeStretchedTanhFunction);
		activation[Math.abs(STRETCHTANH_CHECKBOX_INDEX)] = CommonConstants.includeStretchedTanhFunction;
		JCheckBox ReLU = new JCheckBox("ReLU", CommonConstants.includeReLUFunction);
		activation[Math.abs(RELU_CHECKBOX_INDEX)] = CommonConstants.includeReLUFunction;
		JCheckBox Softplus = new JCheckBox("softplus", CommonConstants.includeSoftplusFunction);
		activation[Math.abs(SOFTPLUS_CHECKBOX_INDEX)] = CommonConstants.includeSoftplusFunction;
		JCheckBox LeakyReLU = new JCheckBox("leaky_ReLU", CommonConstants.includeLeakyReLUFunction);
		activation[Math.abs(LEAKY_RELU_CHECKBOX_INDEX)] = CommonConstants.includeLeakyReLUFunction;
		JCheckBox fullSawtooth = new JCheckBox("full_sawtooth", CommonConstants.includeFullSawtoothFunction);
		activation[Math.abs(FULL_SAWTOOTH_CHECKBOX_INDEX)] = CommonConstants.includeFullSawtoothFunction;
		JCheckBox triangleWave = new JCheckBox("triangle_wave", CommonConstants.includeTriangleWaveFunction);
		activation[Math.abs(TRIANGLE_WAVE_CHECKBOX_INDEX)] = CommonConstants.includeTriangleWaveFunction;
		JCheckBox squareWave = new JCheckBox("square_wave", CommonConstants.includeSquareWaveFunction);
		activation[Math.abs(SQUARE_WAVE_CHECKBOX_INDEX)] = CommonConstants.includeSquareWaveFunction;
		
		//adds slider for mutation rate change
		JSlider mutationsPerGeneration = new JSlider(JSlider.HORIZONTAL, MPG_MIN, MPG_MAX, MPG_DEFAULT);

		Hashtable labels = new Hashtable();
		//set graphic names and toolTip titles
		evolveButton.setName("" + EVOLVE_BUTTON_INDEX);
		evolveButton.setToolTipText("Evolve button");
		saveButton.setName("" + SAVE_BUTTON_INDEX);
		saveButton.setToolTipText("Save button");
		resetButton.setName("" + RESET_BUTTON_INDEX);
		resetButton.setToolTipText("Reset button");
		closeButton.setName("" + CLOSE_BUTTON_INDEX);
		closeButton.setToolTipText("Close button");
		lineageButton.setName("" + LINEAGE_BUTTON_INDEX);
		lineageButton.setToolTipText("Lineage button");
		networkButton.setName("" + NETWORK_BUTTON_INDEX);
		networkButton.setToolTipText("Network button");
		undoButton.setName("" + UNDO_BUTTON_INDEX);
		undoButton.setToolTipText("Undo button");
		sigmoid.setName("" + SIGMOID_CHECKBOX_INDEX);
		tanh.setName("" + TANH_CHECKBOX_INDEX);
		absVal.setName("" + ABSVAL_CHECKBOX_INDEX);
		id.setName("" + ID_CHECKBOX_INDEX);
		gaussian.setName("" + GAUSSIAN_CHECKBOX_INDEX);
		fullApprox.setName("" + FULLAPPROX_CHECKBOX_INDEX);
		sine.setName("" + SINE_CHECKBOX_INDEX);
		approx.setName("" + APPROX_CHECKBOX_INDEX);
		sawtooth.setName("" + SAWTOOTH_CHECKBOX_INDEX);
		halfLinear.setName("" + HALF_LINEAR_CHECKBOX_INDEX);
		stretchTanh.setName("" + STRETCHTANH_CHECKBOX_INDEX);
		ReLU.setName("" + RELU_CHECKBOX_INDEX);//TODO
		Softplus.setName("" + SOFTPLUS_CHECKBOX_INDEX);
		LeakyReLU.setName("" + LEAKY_RELU_CHECKBOX_INDEX);
		fullSawtooth.setName("" + FULL_SAWTOOTH_CHECKBOX_INDEX);
		triangleWave.setName("" + TRIANGLE_WAVE_CHECKBOX_INDEX);
		squareWave.setName("" + SQUARE_WAVE_CHECKBOX_INDEX);
		
		mutationsPerGeneration.setMinorTickSpacing(1);
		mutationsPerGeneration.setPaintTicks(true);
		labels.put(0, new JLabel("Fewer Mutations"));
		labels.put(10, new JLabel("More Mutations"));
		mutationsPerGeneration.setLabelTable(labels);
		mutationsPerGeneration.setPaintLabels(true);
		mutationsPerGeneration.setPreferredSize(new Dimension(350, 40));

		//add action listeners to buttons
		resetButton.addActionListener(this);
		saveButton.addActionListener(this);
		evolveButton.addActionListener(this);
		closeButton.addActionListener(this);
		lineageButton.addActionListener(this);
		networkButton.addActionListener(this);
		undoButton.addActionListener(this);
		sigmoid.addActionListener(this);
		gaussian.addActionListener(this);
		sine.addActionListener(this);
		sawtooth.addActionListener(this);
		absVal.addActionListener(this);
		halfLinear.addActionListener(this);
		tanh.addActionListener(this);
		id.addActionListener(this);
		fullApprox.addActionListener(this);
		approx.addActionListener(this);
		stretchTanh.addActionListener(this);
		ReLU.addActionListener(this);
		Softplus.addActionListener(this);
		LeakyReLU.addActionListener(this);
		fullSawtooth.addActionListener(this);
		triangleWave.addActionListener(this);
		squareWave.addActionListener(this);

		mutationsPerGeneration.addChangeListener(this);
		
		//set checkbox colors to match activation function color
		sigmoid.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_SIGMOID));
		absVal.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_ABSVAL));
		approx.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_APPROX));
		fullApprox.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_FULLAPPROX));
		gaussian.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_GAUSS));
		halfLinear.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_HLPIECEWISE));
		id.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_ID));
		sawtooth.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_SAWTOOTH));
		sine.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_SINE));
		tanh.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_TANH));
		stretchTanh.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_STRETCHED_TANH));
		ReLU.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_RE_LU));
		Softplus.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_SOFTPLUS));
		LeakyReLU.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_LEAKY_RE_LU));
		fullSawtooth.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_FULLSAWTOOTH));
		triangleWave.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_TRIANGLEWAVE));
		squareWave.setForeground(CombinatoricUtilities.colorFromInt(ActivationFunctions.FTYPE_SQUAREWAVE));
		
		
		//add graphics to title panel
		top.add(lineageButton);
		top.add(resetButton);
		top.add(networkButton);
		top.add(evolveButton);
		top.add(saveButton);
		top.add(undoButton);
		top.add(closeButton);
		top.add(mutationsPerGeneration);


		topper.add(top);
		
		bottom.add(halfLinear);
		bottom.add(absVal);
		bottom.add(sawtooth);
		bottom.add(sine);
		bottom.add(gaussian);
		bottom.add(sigmoid);
		bottom.add(tanh);
		bottom.add(id);
		bottom.add(fullApprox);
		bottom.add(approx);
		bottom.add(stretchTanh);
		bottom.add(ReLU);
		bottom.add(Softplus);
		bottom.add(LeakyReLU);
		bottom.add(fullSawtooth);
		bottom.add(triangleWave);
		bottom.add(squareWave);

		topper.add(bottom);
		panels.add(topper);
		//adds button panels
		addButtonPanels();

	

		
		
		//adds panels to frame
		for(JPanel panel: panels) frame.add(panel);

		//adds buttons to button panels
		int x = 0;//used to keep track of index of button panel
		addButtonsToPanel(x++);
		
	}

	protected abstract String getWindowTitle();

	/**
	 * adds buttons to a JPanel
	 * @param x size of button array
	 */
	private void addButtonsToPanel(int x) {
		for(int i = 1; i <= NUM_ROWS; i++) {
			for(int j = 0; j < NUM_COLUMNS; j++) {
				if(x < NUM_BUTTONS) {
					JButton image = getImageButton(GraphicsUtil.solidColorImage(Color.BLACK, PIC_SIZE,( frame.getHeight() - topper.getHeight())/NUM_ROWS), "x");
					image.setName("" + x);
					image.addActionListener(this);
					panels.get(i).add(image);
					buttons.add(image);

				}
			}
		}
	}

	/**
	 * Adds all necessary button panels 
	 */
	private void addButtonPanels() { 
		for(int i = 1; i <= NUM_ROWS; i++) {
			JPanel row = new JPanel();
			row.setSize(frame.getWidth(), PIC_SIZE);
			row.setSize(frame.getWidth(), PIC_SIZE);
			row.setLayout(new GridLayout(1, NUM_COLUMNS));
			panels.add(row);
		}
	}

	/**
	 * Gets JButton from given image
	 * @param image image to put on button
	 * @param s title of button
	 * @return JButton
	 */
	protected JButton getImageButton(BufferedImage image, String s) {
		JButton button = new JButton(new ImageIcon(image));
		button.setName(s);
		return button;
	}

	/**
	 * Score for an evaluated individual
	 * @return array of scores
	 */
	public double[] evaluate() {
		return new double[]{1.0};
	}

	/**
	 * Number of objectives for task
	 * @return number of objectives
	 */
	@Override
	public int numObjectives() {
		return 1;
	}

	/**
	 * minimum score for an individual
	 * @return 0
	 */
	@Override
	public double[] minScores() {
		return new double[]{0};
	}

	/**
	 * this method makes no sense in 
	 * scope of this task
	 */
	@Override
	public double getTimeStamp() {
		return 0.0;
	}

	/**
	 * this method also makes no sense in 
	 * scope of this task
	 */
	@Override
	public void finalCleanup() {
	}

	/**
	 * Resets image on button
	 * @param gmi replacing image
	 * @param buttonIndex index of button 
	 */
	private void setButtonImage(BufferedImage gmi, int buttonIndex){ 
		ImageIcon img = new ImageIcon(gmi);
		buttons.get(buttonIndex).setName("" + buttonIndex);
		buttons.get(buttonIndex).setIcon(img);

	}

	/**
	 * Saves image from button utilizing drawingPanel save functionality
	 * @param i index of button
	 * @param button button
	 */
	private void save(int i) {
		
		// TODO: needs to be generalized so it can save either images or audio files
		
//                // Use of imageHeight and imageWidth allows saving a higher quality image than is on the button
//		BufferedImage toSave = GraphicsUtil.imageFromCPPN((Network)scores.get(i).individual.getPhenotype(), Parameters.parameters.integerParameter("imageWidth"), Parameters.parameters.integerParameter("imageHeight"), inputMultipliers);
//		DrawingPanel p = GraphicsUtil.drawImage(toSave, "" + i, toSave.getWidth(), toSave.getHeight());
//		JFileChooser chooser = new JFileChooser();//used to get save name 
//		chooser.setApproveButtonText("Save");
//		FileNameExtensionFilter filter = new FileNameExtensionFilter("BMP Images", "bmp");
//		chooser.setFileFilter(filter);
//		int returnVal = chooser.showOpenDialog(frame);
//		if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
//			System.out.println("You chose to call the image: " + chooser.getSelectedFile().getName());
//			p.save(chooser.getCurrentDirectory() + "\\" + chooser.getSelectedFile().getName() + (showNetwork ? "network" : "image") + ".bmp");
//			System.out.println("image " + chooser.getSelectedFile().getName() + " was saved successfully");
//			p.setVisibility(false);
//		} else { //else image dumped
//			p.setVisibility(false);
//			System.out.println("image not saved");
//		}
	}

	/**
	 * used to reset image on button using given genotype
	 * @param individual genotype used to replace button image
	 * @param x index of button in question
	 */
	private void resetButton(Genotype<T> individual, int x) { 
		scores.add(new Score<T>(individual, new double[]{0}, null));
		setButtonImage(showNetwork ? getNetwork(individual) : getButtonImage(individual.getPhenotype(),  PIC_SIZE, PIC_SIZE, inputMultipliers), x);
		chosen[x] = false;
		buttons.get(x).setBorder(BorderFactory.createLineBorder(Color.lightGray, BORDER_THICKNESS));
	}

	protected abstract BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers);

	/**
	 * Used to get the image of a network using a drawing panel
	 * @param tg genotype of network
	 * @return
	 */
	private BufferedImage getNetwork(Genotype<T> tg) {
		T pheno = tg.getPhenotype();
		DrawingPanel network = new DrawingPanel(PIC_SIZE,( frame.getHeight() - topper.getHeight())/NUM_ROWS, "network");
		((TWEANN) pheno).draw(network);
		network.setVisibility(false);
		return network.image;

	}

	/**
	 * evaluates all genotypes in a population
	 * @param population of starting population
	 * @return score of each member of population
	 */
	@Override
        public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		waitingForUser = true;
		scores = new ArrayList<Score<T>>();
		if(population.size() != NUM_BUTTONS) {
			throw new IllegalArgumentException("number of genotypes doesn't match size of population! Size of genotypes: " + population.size() + " Num buttons: " + NUM_BUTTONS);
		}	
		for(int x = 0; x < buttons.size(); x++) {
			resetButton(population.get(x), x);
		}
		while(waitingForUser){
			try {//waits for user to click buttons before evaluating
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return scores;
	}

	/**
	 * sets all relevant features if button at index is pressed  
	 * @param scoreIndex index in arrays
	 */
	private void buttonPressed(int scoreIndex) {
		if(chosen[scoreIndex]) {//if image has already been clicked, reset
			chosen[scoreIndex] = false;
			buttons.get(scoreIndex).setBorder(BorderFactory.createLineBorder(Color.lightGray, BORDER_THICKNESS));
			scores.get(scoreIndex).replaceScores(new double[]{0});
		} else {//if image has not been clicked, set it
			chosen[scoreIndex] = true;
			buttons.get(scoreIndex).setBorder(BorderFactory.createLineBorder(Color.BLUE, BORDER_THICKNESS));
			scores.get(scoreIndex).replaceScores(new double[]{1.0});
		}
	}

	/**
	 * Resets to a new random population
	 */
	@SuppressWarnings("unchecked")
	private void reset() { 
		ArrayList<Genotype<T>> newPop = ((SinglePopulationGenerationalEA<T>) MMNEAT.ea).initialPopulation(scores.get(0).individual);
		scores = new ArrayList<Score<T>>();
		ActivationFunctionRandomReplacement frr = new ActivationFunctionRandomReplacement();
		for(int i = 0; i < newPop.size(); i++) {
			frr.mutate((Genotype<TWEANN>) newPop.get(i));
			resetButton(newPop.get(i), i);
		}	
	}

	/**
	 * Saves all currently clicked images
	 */
	private void saveAll() { 
		for(int i = 0; i < chosen.length; i++) {
			boolean choose = chosen[i];
			if(choose) {//loops through and any image  clicked automatically saved
				save(i);
			}
		}
	}

	/**
	 * Shows network on button if network button pressed
	 * replaces images on buttons otherwise
	 */
	private void setNetwork() { 
		if(showNetwork) {//puts images back on buttons
			showNetwork = false;
			for(int i = 0; i < scores.size(); i++) {
				setButtonImage(getButtonImage(scores.get(i).individual.getPhenotype(), PIC_SIZE, PIC_SIZE, inputMultipliers), i);
			}
		} else {//puts networks on buttons
			showNetwork = true;
			for(int i = 0; i < buttons.size(); i++) {
				BufferedImage network = getNetwork(scores.get(i).individual);
				setButtonImage(network, i);
			}
		}
	}
	
	/**
	 * Sets the activation functions as true or false based on whether or
	 * not they were pressed
	 * @param act whether or not function is active
	 * @param index index of function in boolean array
	 * @param title title of function in parameters set
	 */
	private void setActivationFunctionCheckBox(boolean act, int index, String title) { 
		if(act) { 
			activation[Math.abs(index)] = false;
			Parameters.parameters.setBoolean(title, false);
		} else {
			activation[Math.abs(index)] = true;
			Parameters.parameters.setBoolean(title, true);
		}
		ActivationFunctions.resetFunctionSet();
	}

	/**
	 * Handles the changing of the input multipliers when an Effect Checkbox is clicked
	 * 
	 * @param index Index of the effect being changed
	 */
	private void setEffectCheckBox(int index){
		
		// Generalize depending on number of inputs
		
//		if(inputMultipliers[index] == 1.0){ // Effect is currently ON
//			inputMultipliers[index] = 0.0;
//		}else{ // Effect is currently OFF
//			inputMultipliers[index] = 1.0;
//		}
//		resetButtons();
	}

	/**
	 * Used to reset the buttons when an Effect CheckBox is clicked
	 */
	private void resetButtons(){
		for(int i = 0; i < scores.size(); i++) {
			setButtonImage(getButtonImage(scores.get(i).individual.getPhenotype(),  PIC_SIZE, PIC_SIZE, inputMultipliers), i);
		}		
	}
	
	/**
	 * Contains actions to be performed based
	 * on specific events
	 * @param event that occurred
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		//open scanner to read which button was pressed
		Scanner s = new Scanner(event.toString());
		s.next();
		s.next();
		int scoreIndex = s.nextInt();
		s.close();
		if(scoreIndex == SIGMOID_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(SIGMOID_CHECKBOX_INDEX)], SIGMOID_CHECKBOX_INDEX, "includeSigmoidFunction");
			System.out.println("param sigmoid now set to: " + Parameters.parameters.booleanParameter("includeSigmoidFunction"));
		} else if(scoreIndex ==GAUSSIAN_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(GAUSSIAN_CHECKBOX_INDEX)], GAUSSIAN_CHECKBOX_INDEX, "includeGaussFunction");
			System.out.println("param Gauss now set to: " + Parameters.parameters.booleanParameter("includeGaussFunction"));
		} else if(scoreIndex == SINE_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(SINE_CHECKBOX_INDEX)], SINE_CHECKBOX_INDEX, "includeSineFunction");
			System.out.println("param Sine now set to: " + Parameters.parameters.booleanParameter("includeSineFunction"));
		}else if(scoreIndex == SAWTOOTH_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(SAWTOOTH_CHECKBOX_INDEX)], SAWTOOTH_CHECKBOX_INDEX, "includeSawtoothFunction");
			System.out.println("param sawtooth now set to: " + Parameters.parameters.booleanParameter("includeSawtoothFunction"));
		}else if(scoreIndex == ABSVAL_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(ABSVAL_CHECKBOX_INDEX)], ABSVAL_CHECKBOX_INDEX, "includeAbsValFunction");
			System.out.println("param abs val now set to: " + Parameters.parameters.booleanParameter("includeAbsValFunction"));
		}else if(scoreIndex == HALF_LINEAR_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(HALF_LINEAR_CHECKBOX_INDEX)], HALF_LINEAR_CHECKBOX_INDEX, "includeHalfLinearPiecewiseFunction");
			System.out.println("param half linear now set to: " + Parameters.parameters.booleanParameter("includeHalfLinearPiecewiseFunction"));
		}else if(scoreIndex == TANH_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(TANH_CHECKBOX_INDEX)], TANH_CHECKBOX_INDEX, "includeTanhFunction");
			System.out.println("param tanh now set to: " + Parameters.parameters.booleanParameter("includeTanhFunction"));
		} else if(scoreIndex == ID_CHECKBOX_INDEX) { 
			setActivationFunctionCheckBox(activation[Math.abs(ID_CHECKBOX_INDEX)], ID_CHECKBOX_INDEX, "includeIdFunction");
			System.out.println("param ID now set to: " + Parameters.parameters.booleanParameter("includeIdFunction"));
		} else if(scoreIndex == FULLAPPROX_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(FULLAPPROX_CHECKBOX_INDEX)], FULLAPPROX_CHECKBOX_INDEX, "includeFullApproxFunction");
			System.out.println("param activation now set to: " + Parameters.parameters.booleanParameter("includeFullApproxFunction"));
		} else if(scoreIndex == APPROX_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(APPROX_CHECKBOX_INDEX)], APPROX_CHECKBOX_INDEX, "includeApproxFunction");
			System.out.println("param approximate now set to: " + Parameters.parameters.booleanParameter("includeApproxFunction"));
		} else if(scoreIndex == STRETCHTANH_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(STRETCHTANH_CHECKBOX_INDEX)], STRETCHTANH_CHECKBOX_INDEX, "includeStretchedTanhFunction");
			System.out.println("param stretchTanh now set to: " + Parameters.parameters.booleanParameter("includeStretchedTanhFunction"));
		} else if(scoreIndex == RELU_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(RELU_CHECKBOX_INDEX)], RELU_CHECKBOX_INDEX, "includeReLUFunction");
			System.out.println("param ReLU now set to: " + Parameters.parameters.booleanParameter("includeReLUFunction"));
		} else if(scoreIndex == SOFTPLUS_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(SOFTPLUS_CHECKBOX_INDEX)], SOFTPLUS_CHECKBOX_INDEX, "includeSoftplusTanhFunction");
			System.out.println("param softplus now set to: " + Parameters.parameters.booleanParameter("includeSoftplusTanhFunction"));
		} else if(scoreIndex == LEAKY_RELU_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(LEAKY_RELU_CHECKBOX_INDEX)], LEAKY_RELU_CHECKBOX_INDEX, "includeLeakyReLUFunction");
			System.out.println("param LeakyReLU now set to: " + Parameters.parameters.booleanParameter("includeLeakyReLUFunction"));
		} else if(scoreIndex == FULL_SAWTOOTH_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(FULL_SAWTOOTH_CHECKBOX_INDEX)], FULL_SAWTOOTH_CHECKBOX_INDEX, "includeFullSawtoothFunction");
			System.out.println("param full sawtooth now set to: " + Parameters.parameters.booleanParameter("includeFullSawtoothFunction"));
		} else if(scoreIndex == TRIANGLE_WAVE_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(TRIANGLE_WAVE_CHECKBOX_INDEX)], TRIANGLE_WAVE_CHECKBOX_INDEX, "includeTriangleWaveFunction");
			System.out.println("param triangle wave now set to: " + Parameters.parameters.booleanParameter("includeTriangleWaveFunction"));
		} else if(scoreIndex == SQUARE_WAVE_CHECKBOX_INDEX) {
			setActivationFunctionCheckBox(activation[Math.abs(SQUARE_WAVE_CHECKBOX_INDEX)], SQUARE_WAVE_CHECKBOX_INDEX, "includeSquareWaveFunction");
			System.out.println("param square wave now set to: " + Parameters.parameters.booleanParameter("includeSquareWaveFunction"));
		}else if(scoreIndex == CLOSE_BUTTON_INDEX) {//If close button clicked
			System.exit(0);
		} else if(scoreIndex == RESET_BUTTON_INDEX) {//If reset button clicked
			reset();
		} else if(scoreIndex == SAVE_BUTTON_INDEX && BooleanUtil.any(chosen)) { //If save button clicked
			saveAll();
		} else if(scoreIndex == LINEAGE_BUTTON_INDEX) {//If lineage button clicked
			setLineage();
		} else if(scoreIndex == NETWORK_BUTTON_INDEX) {//If network button clicked
			setNetwork();
		} else if(scoreIndex == UNDO_BUTTON_INDEX) {//If undo button clicked
			// Not implemented yet
			setUndo();
		}else if(scoreIndex == EVOLVE_BUTTON_INDEX && BooleanUtil.any(chosen)) {//If evolve button clicked
			previousScores = new ArrayList<Score<T>>();
			previousScores.addAll(scores);
			waitingForUser = false;//tells evaluateAll method to finish
		} else if(scoreIndex >= IMAGE_BUTTON_INDEX) {//If an image button clicked
			assert (scores.size() == buttons.size()) : 
				"size mismatch! score array is " + scores.size() + " in length and buttons array is " + buttons.size() + " long";
			buttonPressed(scoreIndex);
		} 
	}
	//used for lineage and undo button
	private static HashSet<Long> drawnOffspring = null;
	private static HashMap<Integer, Integer> savedLineage = null;
	private static ArrayList<DrawingPanel> dPanels = null;

	/**
	 * resets lineage drawer if button pressed multiple
	 * times
	 */
	private static void resetLineageDrawer() { 
		if(dPanels != null) {
			for(int i = 0; i < dPanels.size(); i++) {
				dPanels.get(i).setVisibility(false);
			}
		}
		dPanels = null;
		drawnOffspring = null;
	}


	/**
	 * gets lineage from offspring object
	 */
	@SuppressWarnings("rawtypes")
	private void setLineage() {
		if(!showLineage) {
			showLineage = true;
			resetLineageDrawer();
			String base = Parameters.parameters.stringParameter("base");
			String log =  Parameters.parameters.stringParameter("log");
			int runNumber = Parameters.parameters.integerParameter("runNumber");
			String saveTo = Parameters.parameters.stringParameter("saveTo");
			String prefix = base + "/" + saveTo + runNumber + "/" + log + runNumber + "_";
			String originalPrefix = base + "/" + saveTo + runNumber + "/" + log + runNumber + "_";

			drawnOffspring = new HashSet<Long>();
			savedLineage = new HashMap<Integer, Integer>();
			dPanels = new ArrayList<DrawingPanel>();

			try {
				Offspring.reset();
				Offspring.lineage = new ArrayList<Offspring>();
				PopulationUtil.loadLineage();
				System.out.println("Lineage loaded from file");
				// Also adds networks
				Offspring.addAllScores(prefix, "parents_gen", ((SinglePopulationGenerationalEA) MMNEAT.ea).currentGeneration(), true, originalPrefix);
				System.out.println("Scores added");
				for(int i = 0; i < chosen.length; i++) {
					boolean choose = chosen[i];
					if(choose) {//loops through and any image  clicked automatically saved
						Score<T> s = scores.get(i);
						Genotype<T> network = s.individual;
						long id = network.getId();
						for(Offspring o : SelectiveBreedingEA.offspring) {
							if(o.offspringId == id) {
								// Magic number here: 600 is start y-coord for drawing lineage
								drawLineage(o, id, 0, 600);						
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("Lineage browser failed");
				e.printStackTrace();
			}
		} else {
			resetLineageDrawer();
			showLineage = false;
		}
	}

	/**
	 * Draws lineage of image recursively
	 * @param o offspring object (used to retrieve lineage)
	 * @param id id of image
	 * @param x x-coord of image
	 * @param y y-coord of image
	 */
	private void drawLineage(Offspring o, long id, int x, int y) { 
		int depth = 0;
		if(o.parentId1 > -1) {
			drawLineage(o.parentId1, id, x, y - PIC_SIZE/4, depth++);
		}
		if(o.parentId2 > -1) {
			drawLineage(o.parentId2, id, x, y + PIC_SIZE/4, depth++);
		}	
	}

	/**
	 * draws lineage of an image
	 * @param <T> phenotype of network
	 * @param id id of image
	 * @param childId id of child image
	 * @param x x-coord
	 * @param y y-coord
	 * @param depth depth of the recursive call and this the
	 *              distance in generations from the child.
	 */
	@SuppressWarnings("unchecked")
	public void drawLineage(long id, long childId, int x, int y, int depth) {
		Offspring o = Offspring.lineage.get((int) id);
		if(o != null && !drawnOffspring.contains(id)) { // Don't draw if already drawn
			Genotype<T> g = (Genotype<T>) Offspring.getGenotype(o.xmlNetwork);
			BufferedImage bi = getButtonImage(g.getPhenotype(), PIC_SIZE/2, PIC_SIZE/2, inputMultipliers);
			DrawingPanel p = GraphicsUtil.drawImage(bi, id + " -> " + childId, PIC_SIZE/2, PIC_SIZE/2);
			p.setLocation(x, y);
			savedLineage.put(depth, savedLineage.get(depth) == null ? 0 : savedLineage.get(depth) + 1);
			drawLineage(o, id, x + PIC_SIZE/2, y);
			p.setTitle(id + "ancestor" + depth + savedLineage.get(depth));
			p.save(p.getFrame().getTitle());
			dPanels.add(p);
		}
		drawnOffspring.add(id); // don't draw again
	}

	/**
	 * undoes previous evolution call
	 * NOT COMPLETE
	 */
	private void setUndo() {
		scores = new ArrayList<Score<T>>();
		for(int i = 0; i < previousScores.size(); i++) {
			System.out.println("score size " + scores.size() + " previousScores size " + previousScores.size() + " buttons size " + buttons.size() + " i " + i);
			resetButton(previousScores.get(i).individual, i);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		SelectiveBreedingEA.MUTATION_RATE = source.getValue();

	}
}