package util;

/**
 * Created by root on 17-2-22.
 */
 public enum State {
    AAA(1, true ),
    BBB(2, true),
    CCC(3, true);
    
    State(int index, boolean is){
        this.index = index;
        this.is = is;
    }
    
    private final int index;
    private final boolean is;
    
    public int getIndex() {
        return index;
    }
    
    public boolean is() {
        return is;
    }
}