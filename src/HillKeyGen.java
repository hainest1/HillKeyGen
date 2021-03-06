// Tom Haines, Ryan Stapp
// MAT/CSC 483 Class Project - Fall 2016
// n x n Hill cipher matrix generator

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import Jama.Matrix;

public class HillKeyGen extends JPanel implements ActionListener {

	private static final long serialVersionUID = -993630192967534413L;
	protected static final String newline = System.lineSeparator();
	protected int v, fileNumber, det;
	protected long attempts, detStart, detFinish, elapsedTime;
	protected static JTextField textField;
	protected JTextArea textArea;
	
	public boolean testing = false;	// files wont output if set to true

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try { createAndShowGUI(); } 
				catch (Exception e) { e.printStackTrace(); }
			}
		});
	}

	/**
	 * Create the application. Default constructor. Calls JPanel constructor with a GridBagLayout.
	 */
	public HillKeyGen() {
		super(new GridBagLayout());	//JPanel constructor with LayoutManager GridBagLayout
        
		JTextPane title = new JTextPane();
		title.setText("Hill Key Generator V1\nBy Tom Haines and Ryan Stapp\nPlease enter a dimension below to generate a legal Hill matrix. Files are automatically saved.");
		title.setEditable(false);
        
		textField = new JTextField("2");
		textField.addActionListener(this);
		textField.selectAll();

		textArea = new JTextArea(20, 50);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);

		//Add Components to this panel.
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		add(title, c);
		add(scrollPane, c);
 
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(textField, c);
	}

    public void actionPerformed(ActionEvent evt) {    	
    	try {
    		v = getIntegerInput();
    		if (v > 0)
    		{
    			detStart = 0; detFinish = 0; elapsedTime = 0;
    			
    			textArea.setText("Generating Matrix...");
    			Matrix matrix = generateMatrix(v);	//matrix object stores random 1-26
	    		
	    		textArea.setText(v + " x " + v + " Hill matrix:\n\n");
	    		outputMatrix(v, matrix);	//print to text area
	    		
				detFinish = System.currentTimeMillis();	//end time in ms
				elapsedTime = detFinish - detStart; //elapsed time in ms
	    		
	    		textArea.append("Determinant Mod 26: " + det + " " + newline);
	    		textArea.append("Number of iterations: " + attempts + newline);
	    		textArea.append("Elapsed time: " + elapsedTime + " ms" + newline);
	            
	    		try { if (!testing) outputToFile(matrix, v); } catch (IOException ioe) {
	    			textArea.append("Error writing to file: " + ioe.getMessage());
	    		}
    		}
    		else textArea.setText("Please enter a positive number.");
    	} catch (IllegalArgumentException e) {	textArea.setText("Please enter an integer.");	}
        
    	textField.selectAll();
        
    	//Make sure the new text is visible, even if there
    	//was a selection in the text area.
    	textArea.setCaretPosition(textArea.getDocument().getLength());
    }
	
	/**
	 * Output matrix of order v to an automatically generated filename
	 * in the root directory of the app.
	 */
    protected void outputToFile(Matrix matrix, int v) throws IOException {
    	String fileName, fullFileName;
		File outFile;
		fileNumber = 0;
		
		//generate current date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		do 
		{ 
			//increases file number until the generated filename does not yet exist, to avoid overwrites
			fileName = "HillKey" + v + "x" + v + "_" + ++fileNumber;
			fullFileName = fileName + ".txt";
			outFile = new File(fullFileName); 
		} while (outFile.isFile());	

		PrintWriter pw = new PrintWriter(outFile);	//create printwriter object for output
		
		//formatted file output
		pw.println(v + " x " + v + " Hill matrix:");
		pw.println("Generated by HillKeyGen V1");
		pw.println("" + dateFormat.format(date));
		pw.println("");
		
		for (int i = 0; i < v; i++)	//loops through all elements
		{
			for (int j = 0; j < v; j++)
			{
				if (j < v - 1) pw.print( (int) matrix.get(i, j) + "\t");	//get matrix i,j element using Matrix.get()	
				else pw.print( (int) matrix.get(i, j) + "");
			}
			pw.println("");
			pw.println("");	
		}			
		
		//some more file output
		pw.println("Determinant Mod 26: " + det);
		pw.println("Number of iterations: " + attempts);
		pw.println("Elapsed time: " + elapsedTime + " ms");
		pw.print("Key ID: " + fileName);
		pw.close();	//close the printwriter
		
		textArea.append("Saved as: " + outFile.getAbsolutePath());	//output save location
	}
	
    /**
	 * Output matrix to textArea.
	 */
    protected void outputMatrix(int v, Matrix matrix) {
		for (int i = 0; i < v; i++) {
			for (int j = 0; j < v; j++) {
				if (j < v - 1) textArea.append((int)matrix.get(i, j) + "\t");	//pulls matrix i,j value from Matrix object using Matrix.get()
				else textArea.append((int)matrix.get(i, j) + "");
			}
			textArea.append("\n\n");;
		}
	}

	protected int getIntegerInput() {
		return Integer.valueOf(textField.getText());	
	}

	/**
	 * Generate a matrix of order n that has a determinant%26
	 * that is relatively prime to 26.
	 * @return Matrix
	 */
	protected Matrix generateMatrix(int n) {
		Matrix matrix;	//new matrix JAMA object
		attempts = 0; 
		
		do {
			detStart = System.currentTimeMillis();	//start time of matrix gen in ms
			
			matrix = new Matrix(n, n);	//initialize matrix JAMA object with dimensions n
			Random g = new Random();	//random number generator object
			
			for (int i = 0; i < n; i++)	//loop through matrix elements
				for (int j = 0; j < n; j++)
				{
					double s = (double) g.nextInt(26) + 1;	//random value (0 - 25) + 1 for [i,j]
					matrix.set(i, j, s);	//sets [i,j] to value of s
				}
			
			det = matrix.detBig().remainder(new BigDecimal(26)).intValue();
			attempts++;
			
		} while (det != 1 && det != 3 && det != 5 && det != 7 && det != 9 && det != 11 && det != 15 
				&& det != 17 && det != 19 && det != 21 && det != 23 && det != 25);	//determinant is not relatively prime to 26
		
		return matrix;
	}
	
	/**
	 * Set up and show the window/GUI.
	 */
	protected static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Hill Key Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add contents to the window.
        frame.add(new HillKeyGen());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        //Disable window resize and maximize
        frame.setResizable(false);
        
        //Center the window
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        frame.setLocation(screenWidth / 4, screenHeight / 4);
        
        //Focus the textArea on start
        frame.addWindowListener( new WindowAdapter() {
            public void windowOpened( WindowEvent e ){
                textField.requestFocus();
            }
        }); 
    }
}
