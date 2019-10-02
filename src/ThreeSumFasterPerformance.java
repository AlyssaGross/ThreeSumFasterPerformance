import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.Arrays;

public class ThreeSumFasterPerformance {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 120;
    static int MAXINPUTSIZE = (int) Math.pow(2,15);
    static int MININPUTSIZE = 1;

    //set up variable to hold folder path and FileWriter/PrintWriter for printing results to a file
    static String ResultsFolderPath = "/home/alyssa/Results/"; // pathname to results folder 
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;


    public static void main (String[] args)
    {
        verifyThreeSumFaster();                             //verify that ThreeSumFaster is working correctly

        // run the whole experiment five times, and expect to throw away the data from the earlier runs, before java has fully optimized 
        System.out.println("Running first full experiment...");
        runFullExperiment("ThreeSumFaster-ExpRun1-ThrowAway.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("ThreeSumFaster-ExpRun2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("ThreeSumFaster-ExpRun3.txt");
        System.out.println("Running fourth full experiment...");
        runFullExperiment("ThreeSumFaster-ExpRun4.txt");
        System.out.println("Running fifth    full experiment...");
        runFullExperiment("ThreeSumFaster-ExpRun5.txt");

    }

    //ThreeSumFaster finds triples that sum to zero and returns the number of triples found
    // The faster algorithm goes through each element starting at the left. For each element, finds the first
    // element after that element (start) and the last element in the list (end)... if the sum of the current element,
    // start, and end is 0, then count is incremented and start is incremented and end is decremented. If the sum is
    // negative, start is incremented, if sum is positive, end is decremented.
    public static int threeSumFaster(long [] testList) {
        Arrays.sort(testList);

        int count = 0;
        int len = testList.length;
        long a,b,c;
        int start, end;

        for(int i = 0; i < len-2; i++)                  //control index of first number (a)
        {
            a = testList[i];
            start = i +1;                               // calculate starting index of second number (b) based on current index of a
            end = len -1;                               // set starting index for third number (c) to the last element of the array
            while(start < end)                          // allows execution until the second (b) and third number (c) meet up
            {
                b = testList[start];
                c = testList[end];
                if (a+b+c == 0) {                       // if sum is 0, increment count
                    count++;                            // increment start and decrement end
                    start = start + 1;                      // because all numbers are unique, neither b or c with form a triple with a again
                    end = end - 1;
                }
                else if (a+b+c > 0)                     //if sum is positive, a value included in the triple must have a smaller value
                    end = end -1;                           // end is the only index that can decrement in this set up, and therefore result in a smaller value
                else                                    //if sum is negative, a value included in the triple must have a larger value
                    start = start + 1;                      // start is the only value that can increment in this set up, and therefore result in a larger value
            }

        }
        return count;
    }

    //create a random list of integers of a specified length
    static long[] createRandomIntegerList(int size)
    {
        long [] newList = new long[size];
        for(int j=0; j<size; j++)
        {
            newList[j] = (long)(MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }

    //verifies the ThreeSumFaster function works as is expected
    //creates three lists with known amount of triples that sum to zero, calls the threeSumFaster function and if the
    // result matches the number of known triples, the function is properly working.
    static void verifyThreeSumFaster()
    {
        int count;
        System.out.println("Verification for ThreeSum");
        //contains 5 triples that sum to 0
        long [] verifyList1 = {-33, 4333, 335, -540, 8274, 483, -2300, -450, 9843, -2668, 6000, 736, 1120, -9010, 2333, -5460};
        System.out.println("Verification 1 :");
        System.out.println("List : " + Arrays.toString(verifyList1));
        count = threeSumFaster(verifyList1);
        System.out.println("  ThreeSum count:   " + count + "\n");
        //contains 3 triples that sum to 0
        long [] verifyList2 = {786, 122, -934, 39048, 2304, 324, 23422,  1022,-558, 9830, 929, 901, 394, -24351, 33, 234};
        System.out.println("Verification 2 :");
        System.out.println("List : " + Arrays.toString(verifyList2));
        count = threeSumFaster(verifyList2);
        System.out.println("  ThreeSum count:   " + count + "\n");
        //contains 7 triples that some to 0
        long [] verifyList3 = {-39, 10, -405, 27, 540, 2111, 378, -598, -1706, 12, 9202, -3114, 3102, -12304, 1003, 2935};
        System.out.println("Verification 3 :");
        System.out.println("List : " + Arrays.toString(verifyList3));
        count = threeSumFaster(verifyList3);
        System.out.println("  ThreeSum count:   " + count + "\n");
    }

    //runs the threeSumFaster function for every input size for the specified number of trials
    //times the amount of time each trial took, and calculates the average for the input size
    //prints the input size along with the average time taken to run threeSumFaster
    static void runFullExperiment(String resultsFileName){

        int count;
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return;
        }


        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch();                                   // create stopwatch for timing an individual trial 

        resultsWriter.println("#InputSize    AverageTime");                                              // # marks a comment in gnuplot data 
        resultsWriter.flush();

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*= 2) {                        // for each size of input we want to test: in this case starting small and doubling the size each time

            System.out.println("Running test for input size "+inputSize+" ... ");                       // progress message... 
            System.out.print("    Running trial batch...");
            long batchElapsedTime = 0;                                                                  // reset elapsed time for the batch to 0

            System.gc();                                                                                // force garbage collection before each batch of trials run so it is not included in the time

                                                                                                        // repeat for desired number of trials (for a specific size of input)...
            for (long trial = 0; trial < numberOfTrials; trial++) {                                     // run the trials 

                long[] testList = createRandomIntegerList(inputSize);                                   // generate a list to use for input in the threeSumFaster function 

                TrialStopwatch.start();                                                                 // begin timing
                count = threeSumFaster(testList);                                                       // run the threeSumFaster on the trial input
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();                     // stop timer and add to the total time elapsed for the batch of trials
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials;     // calculate the average time taken for each trial of the batch

            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch);              // print data for this size of input
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }


}
