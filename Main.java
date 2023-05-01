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
    
    static Point[] P;//to store the coordinates of the input 2D points
    static double C;//penalty for extra segment    
    static int n;//number of points in P
    static double[] M; //to store the optimal value of the cost in an iterative matter
    static double[][] Err; // to store the error for each pair of points i,j
    static int[] Pos; 
    static double[][] slope; // to store the slope of the line segment in interval of point i,j
    static double[][] intercept; // to store the intercept of the line segment in interval of point i,j


    public static void Eij() //to calculate error for all pairs of index i to index j in P
    {
        double cumulativeX[] = new double[n+1]; //to store the cumulativc sum of x-coordinate of points till the given index
        double cumulativeY[] = new double[n+1]; //to store the cumulativc sum of y-coordinate of points till the given index
        double cumulativeXY[] = new double[n+1]; //to store the cumulativc sum of product of x-coordinate and y-coordinate of points till the given index
        double cumulativeX2[] = new double[n+1]; //to store the cumulativc sum of square of x-coordinate of points till the given index
        double sumX, sumY, sumXY, sumX2, num, denom;// num is temporary variable to calculate and store the numerator of the error function. denom is temporary variable to calculate and store the denomenator of the error function
        int diff; //to store the difference between two indices i and j

        cumulativeX[0] = 0;
        cumulativeY[0] = 0;
        cumulativeXY[0] = 0;
        cumulativeX2[0] = 0;

        for(int j = 1; j<= n; j++)//calculating error eij, slope and intercept for all pairs i,j
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

    public static void sortP() // sort array of input points P according to values of x-coordinate of points in ascending order using bubble sort
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

    public static double segLeastSq()//to calculate least segmented square errors and corresponding output line segments
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
    public static void main(String args[]) throws Exception
    {

        try {
            File myObj = new File("input.txt");
            Scanner Sc = new Scanner(myObj);
            C = Sc.nextDouble();
            n = Sc.nextInt();
            P = new Point[n+1];
            for(int i = 1; i<=n; i++)
            {
                double a = Sc.nextDouble();
                double b = Sc.nextDouble();
                P[i] = new Point(a,b);
            }
            Sc.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
          
        BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
          
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
        for(int i = 0; i<=n; i++) //assigning zero to all error values by default before we change the error values according to out need
        {
            for(int j = 0; j<=n; j++)
            {
                Err[i][j] = 0;
            }
        }
        
        sortP(); //sort input points in ascending order of their x-coordinates
        /*for(int i = 1; i<=n; i++)
        {
            System.out.println(P[i].x + " " + P[i].y);
        }*/

        double optval = segLeastSq(); //optval is the cost generated for the optimal solution, which is basically the minimum cost possible for the given input and penalty
        //System.out.println(optval);

        System.out.println("Cost for optimal solution : " + optval); //printing the cost of the optimal solution
        
	    // find the optimal solution
	    Stack<Integer> lines = new Stack<>(); //creating stack to store the indices i,j for our output line segments to be used later to print the output
        int j = Pos[n];
        int i = n;
	    while(i > 0)	
        {
            j = Pos[i];
	    	lines.push(i);
	    	lines.push(j);
            i = j-1;
            
	    }
    
	    System.out.println("\nOptimal solution :");//printing the lines generated by our optimal solution
	    while (!lines.empty())	{
	    	i = lines.peek(); lines.pop();
	    	j = lines.peek(); lines.pop();
	    	System.out.println("Line Segment (y =" + slope[i][j] + " * x + " + intercept[i][j] + ") from points " +i+ " to " +j+ " with error " + Err[i][j]);

            writer.write(slope[i][j] + " " + intercept[i][j] + "\n");
            
	    }
        writer.close();
    }
}
