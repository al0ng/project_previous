/*
* A*算法
* */
import java.util.*;
import java.util.List;

public class Astar {
    //记录迭代次数、和算法耗时
    private int counter;
    private long time_begin, time_end;
    private Node start;
    private Node goal;
    //已经遍历过的点
    private MyArrayList<Node> close_set;
    //即将遍历的点
    private MyArrayList<Node> open_set;
    //目前为止最好路径中, <A，B>:A come from B
    private Map<Node, Node> come_from;
    //到目前为止从start到该key值所表示节点的的最优代价为value的值
    private Map<Node, Integer> g_score;
    // For each node, the total cost of getting from the start node to the goal
    // by passing by that node. That value is partly known, partly heuristic.
    //<Node, 当前节点的g_score + heuristic_cost_estimate>，value值作为启发式搜索的选取下一个遍历节点的评分值
    private Map<Node, Integer> f_score;

    public boolean algorithm(Node come, Node end){
        //初始化
        counter = 0;
        time_begin = time_end = 0;
        time_begin = System.currentTimeMillis();

        start = come;
        goal  = end;
        close_set = new MyArrayList<> ();
        open_set = new MyArrayList<>  ();
        come_from = new HashMap <>();
        g_score = new HashMap <>();
        f_score = new HashMap <>();

        open_set.add(start);
        g_score.put(start, 0);
        f_score.put(start, start.heuristic_cost_estimate());
        come_from.put(start, null);

        while (!open_set.isEmpty()) {
            counter++;
            System.out.println("counter:" + counter);
            //深度优先：取open_set的末端的节点
            //广度优先：取open_set的前端的节点
            //启发式搜索：选择open表上的具有最优（最小value）f_score值的节点
            Node current = null;
            int min_fscore = Integer.MAX_VALUE;
            Node temp_ptr = null;
            int temp_score = 0;
            for (int i = 0; i < open_set.size(); i++) {
                temp_ptr = (Node) open_set.get(i);
                temp_score = f_score.get(temp_ptr);
                if (temp_score < min_fscore){
                    min_fscore = temp_score;
                    current = temp_ptr;
                }
            }
            //判断是否到达goal
            if (current.equals(goal)) {
                open_set.contains(goal);
                goal =  open_set.getPointer();

                time_end = System.currentTimeMillis();
                return true;
            }

            open_set.remove(current);
            close_set.add(current);

            List <Node> neighbors = current.get_neighbor();
            for (Node neighbor : neighbors) {

                if (close_set.contains(neighbor))// Ignore the neighbor which is already evaluated.
                {
                    continue;
                }
                //close_set 没有包含
                if (!open_set.contains(neighbor)) // Discover a new node
                {
                    open_set.add(neighbor);
                }else {
                    //open_set中已经包含与该neighbor相同值的Node，取出该节点的引用，而不是使用现在的neighbor（因为get_neighbor返回的是新的Node对象）
                    //如果不做此替换则在map中会put一个新的节点而不是，改变原有key所对应的value
                    neighbor = open_set.getPointer();
                }
                // The distance from start to a neighbor
                //the "dist_between" function may vary as per the solution requirements.
                if(!g_score.containsKey(neighbor))
                    g_score.put(neighbor, Integer.MAX_VALUE);//g_score初始化值为Infinite
                int tentative_score = g_score.get(current) + 1; //'1' : dist_between(current, neighbor)
                if (tentative_score >= g_score.get(neighbor)) {
                    continue;// This is not a better path.
                }
                // This path is the best until now. Record it!
                come_from.put(neighbor, current);//neighbor-->current
                g_score.put(neighbor, tentative_score);
                //f(n) = g(n) + h(n)
                f_score.put(neighbor, g_score.get(neighbor) + neighbor.heuristic_cost_estimate());
            }
        }
        //没有找到，从该start到goal无解
        time_end = System.currentTimeMillis();
        return false;
    }
    //返回倒序路径List
    public List<Node> reconstruct_path() {
        List<Node> total_path = new ArrayList <>();
        Node current = goal;
        while (!(current==null)){
            total_path.add(current);
            current = come_from.get(current);
        }
        return  total_path;
    }
    //
    public void printInfo(){
        System.out.println("当前程序耗时：" + (time_end - time_begin) + "ms");
        System.out.println("遍历节点数目：" + counter + "个");
    }
}