public class Q2 {




    private static FibonacciHeap insertQ2(FibonacciHeap q1, int m) {
        for (int k = 0; k <= m; k++) {
            //nodes[k] = q1.insert(k);//todo check of nodes[location]
            q1.insert(k);
        }

        return q1;
    }

    private static FibonacciHeap deleteMinQ2(FibonacciHeap q1, int m) {
        for (int i = 1; i <= (3*m)/4; i++)//todo check
            q1.deleteMin();

        return q1;
    }








    public static void main(String[] args) {
        for (int i = 6; i < 16; i += 2) {

            FibonacciHeap.totalLinks = 0;
            FibonacciHeap.numOfCut = 0;
            long startTime1 = System.nanoTime();

            int m = (int) Math.pow(3, i) - 1;
            
            //m = 32;//todo delete
            FibonacciHeap q2 = new FibonacciHeap();
            insertQ2(q2, m);
            deleteMinQ2(q2,m);


            long endTime1   = System.nanoTime();
            long totalTime1= endTime1 - startTime1;
            totalTime1 = (long) (totalTime1 / Math.pow(10,6));

            //FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[m];
            System.out.println("-----------m = 3^("+ i + ")-1 = " + m+ "---------- ");
            System.out.println(totalTime1 + " [milliseconds]");
            System.out.println("total links:   " + FibonacciHeap.totalLinks);
            System.out.println("total cuts:    " + FibonacciHeap.numOfCut);
            System.out.println("Potential:     " + q2.potential());
        }

    }
}

