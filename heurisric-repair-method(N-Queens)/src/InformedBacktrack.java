import java.io.*;
import java.util.*;

public class InformedBacktrack {
    int n;
    //记录总冲突数目
    int initConflicts_counter;
    //记录棋盘上每个棋子的位置：index表示行，location[index]表示列
    // 因为采用一维数组存放，所以不会出现“行冲突”，
    // 如果保证数组中的元素从1...n每个只出现一次则不会出现“列冲突”，只需关心对角线冲突即可
    int[] location;
    //记录斜对角线上冲突的位置,相同对角线上的冲突list起来
    List<Integer>[] leftDiagonal_conflicts;
    List<Integer>[] rightDiagonal_conflicts;
    //Set<Integer>[] leftDiagonal_conflicts;
    //Set<Integer>[] rightDiagonal_conflicts;
    //将leftDiagonal_conflicts有冲突位置的下标记录下来
    //Set<Integer> left_index_indicator;
    List<Integer> left_index_indicator;
    //将right_index_indicatoe有冲突位置的下标记录下来
    //Set<Integer> right_index_indicator;
    List<Integer> right_index_indicator;

    InformedBacktrack(int size){
        n = size;
        location = new int[n];
        initConflicts_counter = 0;
        //对于一个 n*n 的矩阵其对应斜对角线个数为 2*n-1
        leftDiagonal_conflicts = new List[2*n -1];
        rightDiagonal_conflicts = new List[2*n -1];
        for (int i = 0; i < (2*n-1); i++) {
            leftDiagonal_conflicts[i] = null;
            rightDiagonal_conflicts[i] = null;
        }
//        left_index_indicator = new HashSet <>();
//        right_index_indicator = new HashSet <> ();
        left_index_indicator = new ArrayList <> ();
        right_index_indicator = new ArrayList <> ();
    }

    //初始化位置数组，使冲突尽量的少
    public void initLocation(){
        //预测冲突数
        int[] predict_conflicts = new int[n];
        int[] left_offset_conflicts = new int[n];
        int[] right_offset_conflicts = new int[n];
        for (int i = 0; i < n; i++) {
            predict_conflicts[i] = 0;
            left_offset_conflicts[i] = 0;
            right_offset_conflicts[i] = 0;
        }
        //初始化每一行中皇后摆放的列的位置
        for (int row = 0; row < n; row++) {
            //选取冲突数最小的一个位置的列标存放 (row, col)
            int col = getMiniNmbIndex(predict_conflicts);
            location[row] = col;

            int leftDiagonal_index = transformIndex_left(row, col);
            int rightDiagonal_index = transformIndex_right(row,col);
            if (leftDiagonal_conflicts[leftDiagonal_index] == null){
                leftDiagonal_conflicts[leftDiagonal_index] = new LinkedList();
                //leftDiagonal_conflicts[leftDiagonal_index] = new HashSet <>();
                leftDiagonal_conflicts[leftDiagonal_index].add(row);
            }else {
                leftDiagonal_conflicts[leftDiagonal_index].add(row);
                left_index_indicator.add(leftDiagonal_index);
            }
            if (rightDiagonal_conflicts[rightDiagonal_index] == null){
                rightDiagonal_conflicts[rightDiagonal_index] = new LinkedList();
                //rightDiagonal_conflicts[rightDiagonal_index] = new HashSet <>();
                rightDiagonal_conflicts[rightDiagonal_index].add(row);
            }else {
                rightDiagonal_conflicts[rightDiagonal_index].add(row);
                right_index_indicator.add(rightDiagonal_index);
            }

            left_offset_conflicts[col] ++;
            right_offset_conflicts[col] ++;
            predict_conflicts[col] = Integer.MAX_VALUE;//将该列标为最大值（意味着该列已存在皇后不可再存放其他元素）

            //下一轮偏移量移动
            for (int offset = 1; offset < n-1; offset++)
                left_offset_conflicts[offset - 1] = left_offset_conflicts[offset];
            for (int offset = n - 1; offset > 0; offset--)
                right_offset_conflicts[offset] = right_offset_conflicts[offset-1];
            for (int offset = 0; offset < n; offset++)
                if (predict_conflicts[offset] != Integer.MAX_VALUE)
                    predict_conflicts[offset] = left_offset_conflicts[offset] + right_offset_conflicts[offset];
            left_offset_conflicts[n - 1] = 0;
            right_offset_conflicts[0] = 0;
            //优化方法（不用遍历整个数组做数据的移动只需要改变两个偏移数组起始位置即可）
        }
        for (int i = 0; i < 2*n - 1; i++) {
            if(leftDiagonal_conflicts[i] != null){
                if (leftDiagonal_conflicts[i].size() > 1){
                    for (int j = 0; j < leftDiagonal_conflicts[i].size(); j++) {
                        initConflicts_counter++;
                    }
                    initConflicts_counter--;
                }
            }
        }
        for (int i = 0; i < 2*n - 1; i++) {
            if(rightDiagonal_conflicts[i] != null){
                if (rightDiagonal_conflicts[i].size() > 1){
                    for (int j = 0; j < rightDiagonal_conflicts[i].size(); j++) {
                        initConflicts_counter++;
                    }
                    initConflicts_counter--;
                }
            }
        }
//        for (int i = 0; i < n; i++) {
//            System.out.print(left_offset_conflicts[i]+"|");
//        }
//        System.out.println();
//        for (int i = 0; i < n; i++) {
//            System.out.print(right_offset_conflicts[i]+"|");
//        }
//        System.out.println();
    }
    //改进版的初始化棋盘，程序执行反而变得更慢？？（难道是因为几条计算语句太费时间？？）
    public void initLocation_version2(){
        //预测冲突数
        int[] predict_conflicts = new int[n];
        int[] left_offset_conflicts = new int[n];
        int[] right_offset_conflicts = new int[n];

        for (int i = 0; i < n; i++) {
            predict_conflicts[i] = 0;
            left_offset_conflicts[i] = 0;
            right_offset_conflicts[i] = 0;
        }
        //记录两个数组的逻辑始末位置
        int headIndex_left = 0;
        int rearIndex_left = n-1;
        int headIndex_right = 0;
        int rearIndex_right = n-1;

        //初始化每一行中皇后摆放的列的位置
        for (int i = 0; i < n; i++) {
            //选取冲突数最小的一个位置存放
            int index = getMiniNmbIndex(predict_conflicts);
            location[i] = index;

            predict_conflicts[index] = Integer.MAX_VALUE;//将该列标为最大值（意味着该列已存在皇后不可再存放其他元素）

            left_offset_conflicts[(headIndex_left + index)%n] ++;
            right_offset_conflicts[(headIndex_right + index)%n] ++;

            //优化方法（不用遍历整个数组做数据的移动只需要改变两个偏移数组起始位置即可）
            //更新逻辑首尾的值
            headIndex_left += 1;
            rearIndex_left = (rearIndex_left + 1)%n;
            rearIndex_right = (rearIndex_right + 1)%n;
            headIndex_right = (n-1) - rearIndex_right;
//            System.out.println("headIndex_left: " + headIndex_left + ";rearIndex_left"+rearIndex_left);
//            System.out.println("headIndex_right: " + headIndex_right + ";rearIndex_right"+rearIndex_right);
            left_offset_conflicts[rearIndex_left] = 0;
            right_offset_conflicts[headIndex_right] = 0;


            for (int offset = 0; offset < n; offset++)
                if (predict_conflicts[offset] != Integer.MAX_VALUE)
                    predict_conflicts[offset] = left_offset_conflicts[(headIndex_left + offset)%n] + right_offset_conflicts[(headIndex_right + offset)%n];

        }
    }

    //冲突处理
    public void repairConflicts(){
        Random rand = new Random();
        Iterator <Integer> iterator_left = left_index_indicator.iterator();
        Iterator <Integer> iterator_right = right_index_indicator.iterator();
        while (initConflicts_counter != 0){

            while (iterator_left.hasNext()) {
                int random_row = rand.nextInt(n);
                int conflicts_inedx = iterator_left.next();
                int conflicts_inedx_row = leftDiagonal_conflicts[conflicts_inedx].get(1);
                while(true){
                    if (exchangeCheck(conflicts_inedx_row, random_row)){
                        System.out.println("当前冲突数目："+ initConflicts_counter);
                        break;
                    }else{
                        random_row = rand.nextInt(n);
                    }
                }
                if (initConflicts_counter == 0){
                    break;
                }
            }
            if (initConflicts_counter == 0){
                break;
            }
            while (iterator_right.hasNext()) {
                int random_row = rand.nextInt(n);
                int conflicts_inedx = iterator_right.next();
                int conflicts_inedx_row = rightDiagonal_conflicts[conflicts_inedx].get(1);
                while(true){
                    if (exchangeCheck(conflicts_inedx_row, random_row)){
                        System.out.println("当前冲突数目："+ initConflicts_counter);
                        break;
                    }else{
                        random_row = rand.nextInt(n);
                    }
                }
                if (initConflicts_counter == 0){
                    break;
                }
            }

        }
    }
    //检查此种交换元素冲突是否变少(看新位置两个斜对角线有没有元素)
    public boolean exchangeCheck(int old_row, int new_row){
        int old_col = location[old_row];
        int new_col = location[new_row];

        if (giagonalExistQueen(new_row, old_col) && giagonalExistQueen(old_row, new_col))
            return false;
        else {
            //两个节点互换位置
            location[old_row] = new_col;
            location[new_row] = old_col;

            int left_index = transformIndex_left(new_row, old_col);
            int right_index = transformIndex_right(new_row, old_col);
            //新位置上添加对应斜对角线数组中的
            leftDiagonal_conflicts[left_index] = new LinkedList();
            leftDiagonal_conflicts[left_index].add(new_row);
            rightDiagonal_conflicts[right_index] = new LinkedList <>();
            rightDiagonal_conflicts[right_index].add(new_row);
            //旧位置上删除对应斜对角线数组中的old_row
            int old_left_index = transformIndex_left(old_row, old_col);
            int old_right_index = transformIndex_right(old_row, old_col);

            for (int i = 0; i < leftDiagonal_conflicts[old_left_index].size(); i++) {
                if(leftDiagonal_conflicts[old_left_index].get(i) == old_row){
                    leftDiagonal_conflicts[old_left_index].remove(i);
                    initConflicts_counter--;
                }
            }
            for (int i = 0; i < rightDiagonal_conflicts[old_right_index].size(); i++) {
                if (rightDiagonal_conflicts[old_right_index].get(i) == old_row) {
                    rightDiagonal_conflicts[old_right_index].remove(i);
                    initConflicts_counter--;
                }
            }

            int a = transformIndex_left(old_row, new_col);
            int b = transformIndex_right(old_row, new_col);
            leftDiagonal_conflicts[a] = new LinkedList();
            leftDiagonal_conflicts[a].add(old_row);
            rightDiagonal_conflicts[b] = new LinkedList <>();
            rightDiagonal_conflicts[b].add(old_row);
            int a_old = transformIndex_left(new_row, new_col);
            int b_old = transformIndex_right(new_row, new_col);
            for (int i = 0; i < leftDiagonal_conflicts[a_old].size(); i++) {
                if (leftDiagonal_conflicts[a_old].get(i) == new_row) {
                    leftDiagonal_conflicts[a_old].remove(i);
                    initConflicts_counter--;
                }
            }
            for (int i = 0; i < rightDiagonal_conflicts[b_old].size(); i++) {
                if (rightDiagonal_conflicts[b_old].get(i) == new_row) {
                    rightDiagonal_conflicts[b_old].remove(i);
                    initConflicts_counter--;
                }
            }
            return true;
        }
    }

    public boolean giagonalExistQueen(int row, int col){

        int left_index = transformIndex_left(row, col);
        int right_index = transformIndex_right(row, col);

        if (leftDiagonal_conflicts[left_index] != null
                || rightDiagonal_conflicts[right_index] != null)
            return true;
        else
            return false;
    }

    //对初始化的location[]进行校验
    public void checkLocation(){
        BitSet hasExist = new BitSet(n);
        hasExist.clear();//初始化全为false
        for (int i = 0; i < n; i++) {
            int col = location[i];
            if (hasExist.get(col)){
                System.out.println("Columns Failure！！！!");
                break;
            }
            hasExist.set(col);//将该处设为true
        }
    }
    //对算法最终输出结果进行判定
    public void checkOutcome(){
        //最终输出的结果应该要满足约束条件
        //行不冲突、列不冲突
        checkLocation();
        //对角线不冲突
        for (int i = 0; i < n; i++) {
            System.out.println();
        }
    }

    //获取数组中最小元素所在下标,有相等元素随机选取一个
    public int getMiniNmbIndex(int[] array){
        Random rand = new Random();
        int size = array.length;
        int index = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            if (array[i] < min) {
                index = i;
                min = array[i];
            }
        }
        List<Integer> minIndexs = new ArrayList <>();
        for (int i = 0; i < n; i++) {
            if (array[i] == min){
                minIndexs.add(i);
            }
        }
        return minIndexs.get(rand.nextInt(minIndexs.size()));
    }

    //将二维矩阵（row, col）下标，转换为一维'\'对角线下标index
    public int transformIndex_right(int row, int col){
        //人为规定,对于rightDiagonal '\'
            // i=j时，对应index = 0
            //i=0, j=n-1，对应index = n-1
            //i=n-1, j=0，对应index = 2n-2
        int index = 0;
        if (col >= row)
            index = col - row;
        else
            index = n - 1 + (row - col);
        return index;
    }
    //将二维矩阵（row, col）下标，转换为一维'/'对角线下标index
    public int transformIndex_left(int row, int col){
        //人为规定,对于leftDiagonal '/'
            // i-j = n-1时，对应index = 0
            //i=n-1, j=n-1，对应index = n-1
            //i=0 ,j=0，对应index = 2n-2
        int index;
        if ((row + col) >= (n - 1))
            index = (row+col) - (n-1);
        else
            index = (2*n -2) - (row + col);
        return index;
    }

    //将初始化结果存入文件之中
    public void saveAsTxt(){
        String content = "This is the content to write into file";
        Date date = new Date();
        //System.out.println(date.getMinutes() + ";" + date.getHours());
        String fileName = n +"-queen_" + date.getHours() + "_" +date.getMinutes();

        File file = new File(fileName);
        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for (int i = 0; i < n; i++) {
                bw.write(Integer.toString(location[i]));
                bw.write(' ');
            }

            bw.close();
            System.out.println("Write Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void initFromTxt(String path){
        BufferedReader br;
        {
            try {
                br = new BufferedReader(new FileReader(new File(path)));
//                int row_count = 0;
                String line;
                line = br.readLine();
                while (line != null){
                    String[] strs = line.split(" ");
                    for (int i = 0; i < n; i++) {
                       location[i] = Integer.parseInt(strs[i]);
                    }
                    line = br.readLine();
                }
                //System.out.println(x + ";" + y);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        checkLocation();
    }
    public void print(){
        System.out.println("棋盘：");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (location[i] == j) {
                    System.out.print("1\t");
                    continue;
                }
                System.out.print("0\t");
            }
            System.out.println();
        }
        for (int i = 0; i < 2*n - 1; i++) {
            if(leftDiagonal_conflicts[i] != null){
                if (leftDiagonal_conflicts[i].size() > 1){
                    System.out.print("/左斜对角线冲突位置："+ i + "\n冲突元素");
                    for (int j = 0; j < leftDiagonal_conflicts[i].size(); j++) {
                        System.out.print(leftDiagonal_conflicts[i].get(j) +"|");
                    }
                    System.out.println();
                }
            }
        }
        for (int i = 0; i < 2*n - 1; i++) {
            if(rightDiagonal_conflicts[i] != null){
                if (rightDiagonal_conflicts[i].size() > 1){
                    System.out.print("\\右斜对角线冲突位置："+ i + "\n冲突元素");
                    for (int j = 0; j < rightDiagonal_conflicts[i].size(); j++) {
                        System.out.print(rightDiagonal_conflicts[i].get(j) +"|");
                    }
                    System.out.println();
                }
            }
        }
        System.out.println("冲突对的数目："+initConflicts_counter);

//        Iterator<Integer> iterator_left =  left_index_indicator.iterator();
//        while (iterator_left.hasNext()){
//            System.out.print(iterator_left.next() + "|");
//        }
//        System.out.println();
//        Iterator<Integer> iterator_right =  right_index_indicatoe.iterator();
//        while (iterator_right.hasNext()){
//            System.out.print(iterator_right.next() + "|");
//        }
//        for (int i = 0; i < left_index_indicator.size(); i++) {
//            System.out.print(left_index_indicator.get(i) + "|");
//        }
//        System.out.println();
//        for (int i = 0; i < right_index_indicatoe.size(); i++) {
//            System.out.print(right_index_indicatoe.get(i) + "|");
//        }
    }

    public static void main(String[] args){
        long time_begin = 0, time_end = 0;
        time_begin = System.currentTimeMillis();

        int count = 0;
        for (int i = 0; i < 10; i++) {
            InformedBacktrack n_queen_1 = new InformedBacktrack(30000);
            n_queen_1.initLocation();
            System.out.println("冲突数目：" + n_queen_1.initConflicts_counter);
            count+=n_queen_1.initConflicts_counter;
        }
        System.out.println("平均冲突数目：" + count/10);

//        InformedBacktrack n_queen_1 = new InformedBacktrack(10);
//        n_queen_1.initLocation();
//        System.out.println("冲突数目：" + n_queen_1.initConflicts_counter);
//
//        InformedBacktrack n_queen_2 = new InformedBacktrack(10);
//        n_queen_2.initLocation();
//        System.out.println("冲突数目：" + n_queen_2.initConflicts_counter);
//
//        InformedBacktrack n_queen_3 = new InformedBacktrack(10);
//        n_queen_3.initLocation();
//        System.out.println("冲突数目：" + n_queen_3.initConflicts_counter);
////        n_queen.initFromTxt("8-queen_11_48");
//        n_queen.saveAsTxt();
//        n_queen.print();


//        n_queen.initLocation();
//        n_queen.initFromTxt("4-queen_15_15");
//        n_queen.print();
//        n_queen.repairConflicts();
//        n_queen.print();
    }
}