import java.util.Iterator;

public class MyArrayList<E> extends java.util.ArrayList{
    private E pointer;//指向contain中包含的Node（调用contains()并返回ture时后更新）
    private boolean pointer_flag = false;//当前是否可使用pointer（必须在调用一次contanis()并返回ture时使用）
    public E getPointer(){
        if (pointer_flag){
            pointer_flag = false;//改为false，保证在调用一次contanis()并返回ture时使用
            return pointer;
        }else {
            return null;
        }
    }
    @Override
    public boolean contains(Object o) {
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()){
            E item = iterator.next();
            if (item.equals(o)){
                pointer = item;
                pointer_flag = true;//flag改为ture表示可以调用
                return true;
            }
        }
        return false;
    }
}