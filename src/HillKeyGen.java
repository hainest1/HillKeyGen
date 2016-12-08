import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import Jama.Matrix;

public class HillKeyGen extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private JFrame frame;
	private int v, fileNumber = 0;
	private long attempts, detStart, detFinish, det, detUnModded;
    private double elapsedTime = 0;
	
	protected static JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//HillKeyGen window = new HillKeyGen();
					//window.frame.setVisible(true);
					
					createAndShowGUI();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HillKeyGen() {
        super(new GridBagLayout());
        
        JTextPane title = new JTextPane();
        title.setText("Hill Key Generator V1\nBy Tom Haines and Ryan Stapp\nPlease enter a dimension below to generate a legal Hill matrix. Files are automatically saved.");
        title.setEditable(false);
        
        textField = new JTextField();
        textField.addActionListener(this);	//probably use this for button too?
 
        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
 
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();

        c.gridwidth = GridBagConstraints.REMAINDER;
 
        c.fill = GridBagConstraints.HORIZONTAL;
        add(title, c);
        add(scrollPane, c);
 
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(textField, c);
	}

    public void actionPerformed(ActionEvent evt) {    	
    	try 
    	{
    		v = getIntegerInput();
    		if (v > 0)
    		{
	            textArea.setText(v + " x " + v + " Hill matrix:\n\n");
	            
	            Matrix matrix = generateMatrix(v);
	    		outputMatrix(v, matrix);
	    		
	    		textArea.append("Determinant: " + detUnModded + newline);
	    		textArea.append("Determinant Mod 26: " + det + newline);
	    		textArea.append("Number of iterations: " + attempts + newline);
	    		textArea.append("Determinant elapsed time: " + elapsedTime + newline);
	            
	    		try { outputToFile(matrix, v); } catch (IOException ioe) {
	    			textArea.append("Trouble writing to file: " + ioe.getMessage());
	    		}
    		}
	    	else
    		{
    			textArea.setText("Please enter a positive number.");
    		}
        } catch (Exception e) {
            textArea.setText("Please enter an integer.");
        }
        
        // //textArea.removeAll();
        textField.selectAll();
        
 
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
	
	
    private void outputToFile(Matrix matrix, int v) throws IOException {
    	String fileName;
		File outFile;
		fileNumber = 0;
		
		//generate current date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		do 
		{ 
			//increases file number until the generated filename does not yet exist, to avoid overwrites
			fileName = "HillKey" + v + "x" + v + "_" + ++fileNumber + ".txt";
			outFile = new File(fileName); 
		} while (outFile.isFile());	

		PrintWriter pw = new PrintWriter(outFile);	//create printwriter object for output
		
		//formatted file output
		pw.println("Proper " + v + " x " + v + " Hill matrix:");
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
		pw.println("Determinant: " + detUnModded);
		pw.println("Determinant Mod 26: " + det);
		pw.println("Number of iterations: " + attempts);
		pw.println("Determinant elapsed time: " + elapsedTime + "s");
		pw.println("Generated " + dateFormat.format(date));
		pw.close();	//close the printwriter
		
		textArea.append("Saved as: " + outFile.getAbsolutePath());	//output save location
	}

	private void outputMatrix(int v, Matrix matrix) {
    	for (int i = 0; i < v; i++)
		{
			for (int j = 0; j < v; j++)
			{
				if (j < v - 1) textArea.append((int)matrix.get(i, j) + "\t");	//pulls matrix i,j value from Matrix object using Matrix.get()
				else textArea.append((int)matrix.get(i, j) + "");
			}
			textArea.append("\n\n");;
		}
	}

	private int getIntegerInput() {
        return Integer.valueOf(textField.getText());	
	}

	private Matrix generateMatrix(int n) {
		Matrix matrix;//new matrix JAMA object
		attempts = 0; 
		
		do
		{
			detStart = 0; detUnModded = 0; det = 0; detFinish = 0; elapsedTime = 0;
			
					
			matrix = new Matrix(n, n);	//initialize matrix JAMA object with dimension n
			Random g = new Random();	//random number generator object
			
			for (int i = 0; i < n; i++)//loop through array elements
			{
				for (int j = 0; j < n; j++)
				{
					double s = (double) g.nextInt(26) + 1;	//random value (0 - 25) + 1 for [i,j]
					matrix.set(i, j, s);	//sets [i,j] to value of s
				}
			}
			
			detStart = System.currentTimeMillis();	//start time of the determinant calc in ms
			detUnModded = getDeterminant(matrix);
			det = detUnModded % 26;
			detFinish = System.currentTimeMillis();	//end time in ms
			elapsedTime = (double) (detFinish - detStart) / 1000.00; //elapsed time in s
			
			attempts++;
			
		} while (det != 1 && det != 3 && det != 5 && det != 7 && det != 9 && det != 11 && det != 15 
				&& det != 17 && det != 19 && det != 21 && det != 23 && det != 25);
		
		return matrix;
	}

	private long getDeterminant(Matrix matrix) {
		return (long) matrix.det();
	}

	private static void createAndShowGUI() {
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
