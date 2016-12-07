// Tom Haines, Ryan Stapp
// MAT/CSC 483 Class Project
// n x n Hill cipher matrix generator

import java.util.Random;
import java.util.Scanner;
import java.io.*;
import Jama.Matrix;

public class KeyGenAlt 
{
	public static void main(String[] args) throws IOException
	{
		Scanner in = new Scanner(System.in);	//scans for input
		int n = 0, fileNumber = 0;
		long attempts = 0, detStart = 0, detFinish = 0, det = 0, detUnModded = 0;
		
		System.out.println("This program generates an n x n Hill cipher key that has a legal determinant.");
		System.out.println("");
		
		do
		{
			attempts = 0;//reset number of iterations for the matrix generation loop
			fileNumber = 0;//reset file number for file name generation
			System.out.print("Enter Hill key dimension, 0 to stop: ");
			n = in.nextInt();
			
			if (n != 0)	//only generate a matrix & file if the user doesnt choose to exit
			{
				Matrix matrix;//new matrix JAMA object
		
				do
				{
					matrix = new Matrix(n, n);	//initialize matrix JAMA object with dimension n
					fillMatrix(matrix, n);
					
					detStart = System.currentTimeMillis();	//start time of the determinant calc
					detUnModded = (long) matrix.det();
					det = detUnModded % 26;
					detFinish = System.currentTimeMillis();	//end time
					
					attempts++;
					
				} while (det != 1 && det != 3 && det != 5 && det != 7 && det != 9 && det != 11 && det != 15 
						&& det != 17 && det != 19 && det != 21 && det != 23 && det != 25);
				
				//formatted console output
				if (n < 11)
				{
					System.out.println("Proper " + n + " x " + n + " Hill matrix:\n");
					
					//print matrix
					for (int i = 0; i < n; i++)
					{
						for (int j = 0; j < n; j++)
						{
							System.out.print( (int) matrix.get(i, j) + "\t");	//pulls matrix i,j value from Matrix object using Matrix.get()
		
						}
						System.out.println("\n");
						
					}
				}
				else	//too large to view in console window
				{
					System.out.println("");
					System.out.println(n + " x " + n + " Hill matrix generated.");
					System.out.println("Matrix too large to print. View file.\n");
				}
				
				double elapsedTime = (double) (detFinish - detStart) / 1000.00;	//output elapsed time in sec
				
				System.out.println("Determinant: " + detUnModded);
				System.out.println("Determinant Mod 26: " + det);
				System.out.println("Number of iterations: " + attempts);
				System.out.println("Determinant elapsed time: " + elapsedTime);
				
				//file output
				String fileName;
				File outFile;
				
				do 
				{ 
					//increases file number until the generated filename does not yet exist, to avoid overwrites
					fileName = "HillKey" + n + "x" + n + "_" + ++fileNumber + ".txt";
					outFile = new File(fileName); 
				} while (outFile.isFile());	
	
				PrintWriter pw = new PrintWriter(outFile);	//create printwriter object for output
				
				//formatted file output
				pw.println("Proper " + n + " x " + n + " Hill matrix:");
				pw.println("");
				
				for (int i = 0; i < n; i++)	//loops through all elements
				{
					for (int j = 0; j < n; j++)
					{
						pw.print( (int) matrix.get(i, j) + "\t");	//get matrix i,j element using Matrix.get()
						
					}
					pw.println("");
					pw.println("");
					
				}			
				
				//some more file output
				pw.println("");
				pw.println("Determinant: " + detUnModded);
				pw.println("Determinant Mod26: " + det);
				pw.println("Number of iterations: " + attempts);
				pw.println("Determinant elapsed time: " + elapsedTime + "s");
				pw.close();	//close the printwriter
				
				//notifies user of save location
				System.out.println("Saved as: " + outFile.getAbsolutePath());	//output save location
				System.out.println("");
				
			}
			
		} while (n != 0); //loop wont run if user chooses to exit
		
		in.close();	//close the scanner object

	}
	
	//receives a Matrix object 'matrix' and an order 'n' and fills it 
	//with random values from 1-26 using the Matrix.set() function
	public static void fillMatrix(Matrix matrix, int n)
	{
		Random g = new Random();//random number generator object
		
		for (int i = 0; i < n; i++)//loop through array elements
		{
			for (int j = 0; j < n; j++)
			{
				double s = (double) g.nextInt(26) + 1;	//random value (0 - 25) + 1 for [i,j]
				matrix.set(i, j, s);	//sets [i,j] to value of s

			}
		}
	}
}
