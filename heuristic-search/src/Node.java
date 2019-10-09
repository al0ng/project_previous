import java.io.*;
import java.util.*;

public class Node {
    //数码矩阵
    private int[][] matix;
    //数码问题的规格
    private int rows = 5, cols = 5;
    //记录滑块的位置
    private int x, y;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node){
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (matix[i][j] != ((Node) obj).matix[i][j])
                        return false;
                }
            }
            return true;
        }
        else {
            return  false;
        }
    }

    @Override
    protected Node clone(){
        Node node = new Node();
        node.x = x;
        node.y = y;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                node.matix[i][j] = matix[i][j];
            }
        }
        return node;
    }

    public Node(){
        matix = new int[rows][cols];
        x = y = 0;
        //self = null;
    }
    public List<Node> get_neighbor(){
        List<Node> neighbors = new ArrayList <>();
        //左右上下
        Node left = null, right= null, up= null, down= null;
        if (y - 1 >= 0) {
            left = clone();
            left.matix[x][y] = matix[x][y-1];
            left.matix[x][y-1] = matix[x][y];
            left.y = y - 1;
            neighbors.add(left);
        }
        if (y + 1 < cols) {
            right = clone();
            right.matix[x][y] = matix[x][y+1];
            right.matix[x][y+1] = matix[x][y];
            right.y = y + 1;
            neighbors.add(right);
        }
        if (x - 1 >= 0) {
            up = clone();
            up.matix[x][y] = matix[x-1][y];
            up.matix[x-1][y] = matix[x][y];
            up.x = x - 1;
            neighbors.add(up);
        }
        if (x + 1 < rows) {
            down = clone();
            down.matix[x][y] = matix[x + 1][y];
            down.matix[x+1][y] = matix[x][y];
            down.x = x + 1;
            neighbors.add(down);
        }
        return neighbors;
    }
    public Integer heuristic_cost_estimate(){
        //广度优先、深度优先直接返回1即可
        //return 1;
        //启发式搜索
        int score = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                score += Math.abs(matix[i][j] - (i*cols + j));
            }
        }
        return score;
    }

    //随机生成一个24数码矩阵，并保证可解
    public void random_init(){
        List<Integer> number_list = new ArrayList<Integer>();
        for (int i = 0; i < rows*cols; i++)
            number_list.add(new Integer(i));
        Collections.shuffle(number_list);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matix[i][j] = number_list.get(i*cols + j);
                if (matix[i][j] == 0){
                    x = i;
                    y= j;
                }
            }
        }
    }
    public void fixed_init(){
        matix = new int[rows][cols];
        x = y = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matix[i][j] = i*cols + j;
            }
        }

    }
    public void init_from_file(String path){
        BufferedReader br;
        {
            try {
                br = new BufferedReader(new FileReader(new File(path)));
                //int numbers[][] = new int[rows][cols];
                matix = new int[rows][cols];
                int row_count = 0;
                String line;
                line = br.readLine();
                while (line != null){
                    String[] strs = line.split(" ");
                    for (int i = 0; i < cols; i++) {
                        matix[row_count][i] = Integer.parseInt(strs[i]);
                        if (Integer.parseInt(strs[i]) == 0){
                            x = row_count;
                            y = i;
                        }
                    }
                    row_count++;
                    line = br.readLine();
                }
                //System.out.println(x + ";" + y);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void print(){
        System.out.println("-----------------------");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(matix[i][j]);
                System.out.print("\t");
            }
            System.out.print("\n");
        }
        System.out.println("-----------------------");
    }

    public static void main(String[] args){
        Node node = new Node();
        node.init_from_file("C:\\project\\IdeaProjects\\heuristic-search\\src\\input.txt");
        node.print();
        }
}