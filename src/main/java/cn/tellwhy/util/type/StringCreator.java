package cn.tellwhy.util.type;

/**
 * Created by aaa on 18-3-26.
 */
public class StringCreator {
    private StringBuilder stringBuilder;
    
    public StringCreator(){
        stringBuilder = new StringBuilder();
    }
    
    public StringCreator appendLong(long value){
        stringBuilder.append(value);
        if (value < 0) {
            System.out.println("value:" + value);
        }
        return this;
    }
    
    public StringCreator appendString(String value){
        stringBuilder.append(value);
        if (value.indexOf("-") > -1) {
            System.out.println("value:" + value);
        }
        return this;
    }
    
    public <T> StringCreator append(T value){
        stringBuilder.append(value);
        return this;
    }
    
    public String get(){
        return stringBuilder.toString();
    }
}
