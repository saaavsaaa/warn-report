package cn.tellwhy.code.record;

/**
 * Created by aaa on 17-6-26.
 */
public class SundryO {
}
enum BizEnum {
    a("A"),
    ab("AB"),
    abc("ABC"),
    abca("ABCA"),
    abcab("ABCAB");
    
    private String value;
    
    BizEnum(String value){
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

class InputPara{
    private String a;
    private String ab;
    private String abc;
    private String abca;
    private String abcab;
    
    public String getA() {
        return a;
    }
    
    public String getAb() {
        return ab;
    }
    
    public String getAbc() {
        return abc;
    }
    
    public String getAbca() {
        return abca;
    }
    
    public String getAbcab() {
        return abcab;
    }
}

class BO{
    private Integer a;
    private Integer ab;
    private Integer abc;
    private Integer abca;
    private Integer abcab;
    
    public Integer getA() {
        return a;
    }
    
    public Integer getAb() {
        return ab;
    }
    
    public Integer getAbc() {
        return abc;
    }
    
    public Integer getAbca() {
        return abca;
    }
    
    public Integer getAbcab() {
        return abcab;
    }
    
    public void setA(Integer a) {
        this.a = a;
    }
    
    public void setAb(Integer ab) {
        this.ab = ab;
    }
    
    public void setAbc(Integer abc) {
        this.abc = abc;
    }
    
    public void setAbca(Integer abca) {
        this.abca = abca;
    }
    
    public void setAbcab(Integer abcab) {
        this.abcab = abcab;
    }
}
