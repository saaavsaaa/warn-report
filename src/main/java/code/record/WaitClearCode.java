package code.record;

import java.util.Optional;

/**
 * Created by aaa on 17-6-26.
 */
public class WaitClearCode {
    
    public void createBiz(InputPara inputPara, BO bo) {
        String list = "";
        if ("1".equals(inputPara.getA())) {
            if (0 == Optional.ofNullable(bo).map(account -> account.getA()).orElse(0)) {
                list = "A,";
            }
        }
        if ("1".equals(inputPara.getAb())) {
            if (0 == Optional.ofNullable(bo).map(account -> account.getAb()).orElse(0)) {
                list = list + "AB,";
            }
        }
        if ("1".equals(inputPara.getAbc())) {
            if (0 == Optional.ofNullable(bo).map(account -> account.getAbc()).orElse(0)) {
                list = list + "ABC,";
            }
        }
        if ("1".equals(inputPara.getAbca())) {
            if (0 == Optional.ofNullable(bo).map(account -> account.getAbca()).orElse(0)) {
                list = list + "ABCA,";
            }
        }
        if ("1".equals(inputPara.getAbcab())) {
            if (0 == Optional.ofNullable(bo).map(account -> account.getAbcab()).orElse(0)) {
                list = list + "ABCAB,";
            }
        }
        list = list.trim().substring(0, list.lastIndexOf(","));
        System.out.println(list);
    }
    
    public void updateChannel(InputPara inputPara, BO bo) {
        String list = "";
        if ("0".equals(inputPara.getA())) {
            if (1 == Optional.ofNullable(bo).map(b -> b.getA()).orElse(0)) {
                list = "A,";
                bo.setA(0);
            }
        }
        if ("0".equals(inputPara.getAb())) {
            if (1 == Optional.ofNullable(bo).map(b -> b.getAb()).orElse(0)) {
                list = list + "AB,";
                bo.setAb(0);
            }
        }
        if ("0".equals(inputPara.getAbc())) {
            if (1 == Optional.ofNullable(bo).map(b -> b.getAbc()).orElse(0)) {
                list = list + "ABC,";
                bo.setAbc(0);
            }
        }
        if ("0".equals(inputPara.getAbca())) {
            if (1 == Optional.ofNullable(bo).map(b -> b.getAbca()).orElse(0)) {
                list = list + "ABCA,";
                bo.setAbca(0);
            }
        }
        if ("0".equals(inputPara.getAbcab())) {
            if (1 == Optional.ofNullable(bo).map(b -> b.getAbcab()).orElse(0)) {
                list = list + "ABCAB,";
                bo.setAbcab(0);
            }
        }
        list = list.trim().substring(0, list.lastIndexOf(","));
        System.out.println(list);
    }
    
    private void update(String callback, BO bo) {
        String[] slist = callback.split(",");
        for (String str : slist) {
            if ("A".equals(str)) {
                bo.setA(1);
            } else if ("AB".equals(str)) {
                bo.setAb(1);
            } else if ("ABC".equals(str)) {
                bo.setAbc(1);
            } else if ("ABCA".equals(str)) {
                bo.setAbca(1);
            } else if ("ABCAB".equals(str)) {
                bo.setAbcab(1);
            } else {
                continue;
            }
        }
    }
}
