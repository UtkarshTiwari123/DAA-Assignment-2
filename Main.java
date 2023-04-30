import java.util.*;
import java.io.*;
class Point
{
    double x,y;
    Point(double a, double b)
    {
        x = a;
        y = b;
    }
}

public class Main {
    
    static Point[] P;
    static double C;//penalty for extra segment    
    static int n;//number of points in P
    static double[] M;
    static double[][] Err;
    static int[] Pos;
    static double[][] slope;
    static double[][] intercept;


    public static void Eij() //to calculate error for all pairs of index i to index j in P
    {
        double cumulativeX[] = new double[n+1];
        double cumulativeY[] = new double[n+1];
        double cumulativeXY[] = new double[n+1];
        double cumulativeX2[] = new double[n+1];
        double sumX, sumY, sumXY, sumX2, num, denom;
        int diff;

        cumulativeX[0] = 0;
        cumulativeY[0] = 0;
        cumulativeXY[0] = 0;
        cumulativeX2[0] = 0;

        for(int j = 1; j<= n; j++)
        {
            cumulativeX[j] = cumulativeX[j-1] + P[j].x;
		    cumulativeY[j] = cumulativeY[j-1] + P[j].y;
		    cumulativeXY[j] = cumulativeXY[j-1] + P[j].x * P[j].y;
		    cumulativeX2[j] = cumulativeX2[j-1] + P[j].x * P[j].x;

            for(int i = 1; i<=j; i++)
            {
                diff = j - i + 1;
                sumX = cumulativeX[j] - cumulativeX[i-1];
			    sumY = cumulativeY[j] - cumulativeY[i-1];
			    sumXY = cumulativeXY[j] - cumulativeXY[i-1];
			    sumX2 = cumulativeX2[j] - cumulativeX2[i-1];
                
			    
			    num = diff * sumXY - sumX * sumY;
			    if (num == 0)
                {
			    	slope[i][j] = 0.0;
                }
                else 
                {
			    	denom = diff * sumX2 - sumX * sumX;
			    	slope[i][j] = (denom == 0) ? Double.POSITIVE_INFINITY : (num / (double)denom);				
			    }
                intercept[i][j] = (sumY - slope[i][j] * sumX) / (double)diff;

           	    for (int k = i; k <= j; k++)	
                {
                	double temp = P[k].y - slope[i][j] * P[k].x - intercept[i][j];
                	Err[i][j] += temp * temp;
                }
            }
        }

    }

    public static void sortP() // sort P according to x values in ascending order using bubble sort
    {
        for (int i = 1; i <= n - 1; i++)
        {
            for (int j = 1; j <= n - i - 1; j++)
            {
                if (P[j].x > P[j + 1].x) 
                {
                    Point temp = P[j];
                    P[j] = P[j + 1];
                    P[j + 1] = temp;
                }
            }
        }               
    }

    public static double segLeastSq()//to calculate least segmented square errors and corresponding line segments
    {
        M[0] = 0;
        Pos[0] = 0;
        Eij();
        double min_val = Double.POSITIVE_INFINITY;
        for (int j = 1; j <= n; j++)	
        {
            int k = 0;
            min_val = Double.POSITIVE_INFINITY;
            for (int i = 1; i <= j; i++)	
            {
                double temp = Err[i][j] + M[i-1];
                if (temp < min_val)	{
                    min_val = temp;
                    k = i;
                }
            }
            M[j] = min_val + C;
            Pos[j] = k;
        }

        return M[n];
    }
    public static void main(String args[])
    {

        try {
            File myObj = new File("input.txt");
            Scanner Sc = new Scanner(myObj);
            C = Sc.nextInt();
            n = Sc.nextInt();
            P = new Point[n+1];
            for(int i = 1; i<=n; i++)
            {
                int a = Sc.nextInt();
                int b = Sc.nextInt();
                P[i] = new Point(a,b);
            }
            Sc.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        //System.out.println("hello");
        //n = 11;//number of points
        //P = new Point[n+1];
       // 
        //P[1] = new Point(-2,1);
        //P[2] = new Point(-1,0);
        //P[3] = new Point(0,0);
        //P[4] = new Point(1,1);
        //P[5] = new Point(2,3);
        //P[6] = new Point(4,5);
        //P[7] = new Point(3,2);
        //P[8] = new Point(5,4);
        //P[9] = new Point(7,5);
        //P[10] = new Point(6,5);
        //P[11] = new Point(8,5);

        M = new double[n+1];
        Pos = new int[n+1];
        Err = new double[n+1][n+1];
        slope = new double[n+1][n+1];
        intercept = new double[n+1][n+1];
        for(int i = 0; i<=n; i++)
        {
            for(int j = 0; j<=n; j++)
            {
                Err[i][j] = 0;
            }
        }
        
        sortP();
        /*for(int i = 1; i<=n; i++)
        {
            System.out.println(P[i].x + " " + P[i].y);
        }*/

        double optval = segLeastSq();
        //System.out.println(optval);

        System.out.println("Cost for optimal solution : " + optval);
	
	    // find the optimal solution
	    Stack<Integer> lines = new Stack<>();
        int j = Pos[n];
        int i = n;
	    while(i > 0)	
        {
            j = Pos[i];
	    	lines.push(i);
	    	lines.push(j);
            i = j-1;
            
	    }
    
	    System.out.println("\nOptimal solution :");
	    while (!lines.empty())	{
	    	i = lines.peek(); lines.pop();
	    	j = lines.peek(); lines.pop();
	    	System.out.println("Line Segment (y =" + slope[i][j] + " * x + " + intercept[i][j] + ") from points " +i+ " to " +j+ " with error " + Err[i][j]);
	    }
    }
}
