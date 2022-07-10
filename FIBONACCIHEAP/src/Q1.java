import java.util.Timer;

public class Q1 {
  private static FibonacciHeap build(int m, FibonacciHeap.HeapNode[] nodes){
    FibonacciHeap q1 = new FibonacciHeap();
    for (int i = m-1; i>-2; i--){
      if (i>-1)
        nodes[i] = q1.insert(i);
      else
        q1.insert(i);
    }
    q1.deleteMin();
    return q1;
  }

  private static FibonacciHeap decreaseKeyQ1(FibonacciHeap q1, int m, FibonacciHeap.HeapNode[] nodes){
    for (int i = (int) Math.ceil(Math.log(m)/Math.log(2)); i > 0; i--){
      q1.decreaseKey(nodes[(int) (m- Math.pow(2,i)+1)], m+1);
    }
    return q1;
  }

  private static FibonacciHeap decreaseKeyQ1ddddd(FibonacciHeap q1, int m, FibonacciHeap.HeapNode[] nodes) {
    /*for (int i = (int) Math.ceil(Math.log(m)/Math.log(2)); i > 0; i--)
      q1.decreaseKey(nodes[ m-2], m+1);*/
    q1.decreaseKey(nodes[m-2], m+1);

    return q1;
  }

  public static void main(String[] args) {


    //Runtime runtime

    for (int i = 10; i<30; i += 5){
      FibonacciHeap.totalLinks = 0;
      FibonacciHeap.numOfCut = 0;
      long startTime1 = System.nanoTime();

      int m = (int) Math.pow(2,i);
      //m = 16;//todo delete
      FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[m];
      FibonacciHeap q1 = build(m,nodes);
      decreaseKeyQ1(q1, m, nodes);
      //decreaseKeyQ1ddddd(q1, m, nodes);//for 1f



      long endTime1   = System.nanoTime();
      long totalTime1= endTime1 - startTime1;
      totalTime1 = (long) (totalTime1 / Math.pow(10,6));
      System.out.println("-----------m = 2^"+ i + "---------- ");
      System.out.println(totalTime1 + " [milliseconds]");
      System.out.println("total links:   " + FibonacciHeap.totalLinks);
      System.out.println("total cuts:    " + FibonacciHeap.numOfCut);
      System.out.println("Potential:     " + q1.potential());
    }



  }





}
