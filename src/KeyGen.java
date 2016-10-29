import java.util.*;

public class KeyGen 
{
	public static void main(String[] args) 
	{
		Scanner in = new Scanner(System.in);
		int n = 0, det = 0;
		long attempts = 0, detStart = 0, detFinish = 0;
		
		System.out.println("This program generates an n x n Hill cipher key that has a legal determinant.");
		System.out.println("");
		
		do
		{
			attempts = 0;
			System.out.print("Enter Hill key dimension, 0 to stop: ");
			n = in.nextInt();
			int[][] matrix = new int[n][n];
	
			do
			{
				fillMatrix(matrix, n);
				
				detStart = System.currentTimeMillis();
				det = determinant(matrix, n) % 26;
				detFinish = System.currentTimeMillis();
				
				attempts++;
			} while(det != 1 && det != 3 && det != 5 && det != 7 && det != 9 && det != 11 && det != 15 
					&& det != 17 && det != 19 && det != 21 && det != 23 && det != 25);
			
			
			System.out.println("Proper " + n + " x " + n + " Hill matrix:\n");
			for (int i = 0; i < n; i++)
			{
				for (int j = 0; j < n; j++)
				{
					System.out.print(matrix[i][j] + "\t");
				}
				System.out.println("\n");
			}
			
			double elapsedTime = (double) (detFinish - detStart) / 100.0;
			
			System.out.println("Determinant: " + det);
			System.out.println("Number of iterations: " + attempts);
			System.out.println("Determinant elapsed time: " + elapsedTime);
			System.out.println("");
		} while (n != 0);
		
		in.close();

	}
	
	public static void fillMatrix(int[][] matrix, int n)
	{
		Random g = new Random();
		
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				matrix[i][j] = g.nextInt(26) + 1;

			}
		}
	}
	
    public static int determinant(int A[][],int N)
    {
        int det = 0;
        if(N == 1)
        {
            det = A[0][0];
        }
        else if (N == 2)
        {
            det = A[0][0]*A[1][1] - A[1][0]*A[0][1];
        }
        else
        {
            det=0;
            for(int j1=0;j1<N;j1++)
            {
                int[][] m = new int[N-1][];
                for(int k=0;k<(N-1);k++)
                {
                    m[k] = new int[N-1];
                }
                for(int i=1;i<N;i++)
                {
                    int j2=0;
                    for(int j=0;j<N;j++)
                    {
                        if(j == j1)
                            continue;
                        m[i-1][j2] = A[i][j];
                        j2++;
                    }
                }
                det += Math.pow(-1.0,1.0+j1+1.0)* A[0][j1] * determinant(m,N-1);
            }
        }
        return det;
    }
}
