import java.util.*;

public class Tools {

    /**
     * 将map按照 value值进行排序 （升序）
     * @param map
     * @param <K>
     * @param <V>
     * @return
     * srouce: http://www.cnblogs.com/liu-qing/p/3983496.html
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <K, V> void printMap(Map<K, V> map){
        Set<K> keys = map.keySet();
        for (K key: keys){
            V value = map.get(key);
            System.out.println("key:" + key + ";value" + value);
        }
    }
    public static <K, V> K getFirstItemKey(Map<K, V> map){
        Map.Entry<K,V> entry = map.entrySet().iterator().next();
        return entry.getKey();
    }
}

