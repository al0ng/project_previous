import java.util.Collections;
import java.util.List;


public class Search {
    //一个n*n的矩阵表示棋盘
    public static void main(String[] args){
        System.out.println("start:");
        Node start = new Node();
        //start.random_init();
        start.init_from_file("C:\\project\\IdeaProjects\\heuristic-search\\src\\input.txt");
        start.print();
        System.out.println("gaol:");
        Node goal = new Node();
        goal.fixed_init();
        goal.print();

        Astar astar = new Astar();

        while (! astar.algorithm(start, goal)){
            astar.printInfo();
            System.out.println("no solution!");
            start.random_init();
            start.print();
            goal.print();
        }

        System.out.println("----------------------------\npath:");
        List<Node> path = astar.reconstruct_path();
        Collections.reverse(path);
        int count = 0;
        for (Node i: path){
            System.out.println("step" + Integer.toString(count++));
            i.print();
        }
        astar.printInfo();
        System.out.println("路径长度："+path.size());
    }
}
