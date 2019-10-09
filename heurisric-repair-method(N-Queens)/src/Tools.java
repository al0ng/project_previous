import java.util.BitSet;
import java.util.Random;

public class Tools {
    //随机返回一个Bitset中值true的元素所对应的下标,返回-1表示没找到
    static public int randomGetIndexFromBitset(BitSet set){
        int size = set.size();
        Random random = new Random();
        int index = random.nextInt(size);
        for (int i = 0; i < size; i++) {
            index = (i + index)%size;
            if (set.get(index))
                return index;
        }
        return -1;
    }


}
