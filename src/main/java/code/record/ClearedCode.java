package code.record;

import org.apache.commons.lang3.StringUtils;
import util.type.BeanUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by aaa on 17-6-26.
 */
public class ClearedCode {
    
    public void createBiz(InputPara inputPara, BO bo) {
        ChangeBO changeBO = (b, attribute) -> {
            if(Integer.valueOf(0).equals(BeanUtil.get(b, attribute))){
                String grant = BizEnum.valueOf(attribute).getValue();
                return grant;
            }
            return "";
        };
        String result = this.changeValueLogic(inputPara, bo, "1", changeBO);
        System.out.println(result);
    }
    
    public void updateChannel(InputPara inputPara, BO bo) {
        ChangeBO changeBO = (b, attribute) -> {
            if(Integer.valueOf(1).equals(BeanUtil.get(b, attribute))){
                BeanUtil.set(b, attribute, 0);
                String grant = BizEnum.valueOf(attribute).getValue();
                return grant;
            }
            return "";
        };
        String result = this.changeValueLogic(inputPara, bo, "0", changeBO);
        System.out.println(result);
    }
    
    private String changeValueLogic(InputPara inputPara, BO bo, String change, ChangeBO changeBO){
        StringBuilder stringBuilder = new StringBuilder();
        for (BizEnum aEnum : BizEnum.values()){
            String string = (String) BeanUtil.get(inputPara, aEnum.name());
            if(change.equals(string)){
                String grant = changeBO.updateBO(bo, aEnum.name());
                if(StringUtils.isNotEmpty(grant)){
                    stringBuilder.append(grant);
                    stringBuilder.append(",");
                }
            }
        }
        if(stringBuilder.length() == 0){
            return "";
        }
        String result = stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(",")).toString();
        return result;
    }
    
    private void update(String callback, BO bo) {
        Map<String, Consumer<BO>> map = this.getBs();
        String[] sList = callback.split(",");
        for (String str : sList){
            Consumer<BO> hkcgAccountConsumer =  map.get(str);
            if(null == hkcgAccountConsumer){
                continue;
            }
            hkcgAccountConsumer.accept(bo);
        }
    }
    
    private Map getBs(){
        Map<String, Consumer<BO>> map = new HashMap<>();
        map.put(BizEnum.a.getValue(), (b) -> b.setA(1));
        map.put(BizEnum.ab.getValue(), (b) -> b.setAb(1));
        map.put(BizEnum.abc.getValue(), (b) -> b.setAbc(1));
        map.put(BizEnum.abca.getValue(), (b) -> b.setAbca(1));
        map.put(BizEnum.abcab.getValue(), (b) -> b.setAbcab(1));
        
        return map;
    }
}

interface ChangeBO{
    String updateBO(BO bo, String attribute);
}
