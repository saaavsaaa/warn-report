package cn.tellwhy.code.visit;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.signature.SignatureReader;
import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;
import jdk.internal.org.objectweb.asm.signature.SignatureWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aaa on 18-2-28.
 */
public class SignatureAdapter {
    public static void main(String[] args){
        String s = "Ljava/util/HashMap<TK;TV;>.HashIterator<TK;>;";
        Map<String, String> renaming = new HashMap<String, String>();
        renaming.put("java/util/HashMap", "A");
        renaming.put("java/util/HashMap.HashIterator", "B");
        SignatureWriter sw = new SignatureWriter();
        SignatureVisitor sa = new RenameSignatureAdapter(sw, renaming);
        SignatureReader sr = new SignatureReader(s);
        sr.acceptType(sa);
        System.out.println(sw.toString());
    }
}
    
// 除 visitClassType和 visitInnerClassType 方法之外,它将自己接收到的所有其他方法调用都不加修改地加以转发
// LA<TK;TV;>.B<TK;>; 书上的例子visitTypeArgument return this是错的返回LA<>.B<>;
class RenameSignatureAdapter extends SignatureVisitor {
    private SignatureVisitor sv;
    private Map<String, String> renaming;
    private String oldName;
    public RenameSignatureAdapter(SignatureVisitor sv, Map<String, String> renaming) {
        super(Opcodes.ASM5);
        this.sv = sv;
        this.renaming = renaming;
    }
    public void visitFormalTypeParameter(String name) {
        sv.visitFormalTypeParameter(name);
    }
    public SignatureVisitor visitClassBound() {
        sv.visitClassBound();
        return this;
    }
    public SignatureVisitor visitInterfaceBound() {
        sv.visitInterfaceBound();
        return this;
    }

    public void visitClassType(String name) {
        oldName = name;
        String newName = renaming.get(oldName);
        sv.visitClassType(newName == null ? name : newName);
    }
    public void visitInnerClassType(String name) {
        oldName = oldName + "." + name;
        String newName = renaming.get(oldName);
        sv.visitInnerClassType(newName == null ? name : newName);
    }
    public void visitTypeArgument() {
        sv.visitTypeArgument();
    }
    public SignatureVisitor visitTypeArgument(char wildcard) {
        return sv.visitTypeArgument(wildcard);
//        return this;
    }
    public void visitEnd() {
        sv.visitEnd();
    }
}