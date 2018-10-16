package cn.tellwhy.algorithm;

/*
* 在各种条件B_i下A的发生概率
*/
class ConditionProbability {
}

public class Bayes {


}


class DishOrdered {
    // 初始情况，该对象被选择或不被选择在什么也不知道的情况下都是50%
    private final double Init_Probability = 0.5;
    private double priorProbability = Init_Probability;
    private double postProbability = Init_Probability;

    /**
     * @param chosen 主体属于bias类型为true，否则是中立主体
     * @param target 参与选择的主体中倾向于选择的概率
     */
    // 选择或不选的概率和是1，既选又不选就算两次计算了
    public void calculateProbability(final boolean chosen, final TargetCalculated target){
        // 先验概率是倾向选的主体选了+不倾向选择的主体选了，选择范围中主体选择了的概率
        // 这里不选的选择，如果是博客里那种选B(相当于不选)的情况，则bias和neutral的概率分别为：
        // (1 - target.getBiasChoiceProbability()) 和 (1 - target.getNeutralChoiceProbability())
        double bias = priorProbability * target.getBiasChoiceProbability();
        double neutral = (1 - priorProbability) * target.getNeutralChoiceProbability();
        double totalProbability = bias + neutral;

        double choice = chosen ? bias * target.getBiasChoiceProbability() : neutral * target.getNeutralChoiceProbability();
        this.postProbability = choice / totalProbability;

        // 下一次bias类主体选择的先验概率
        this.priorProbability = postProbability;
    }

    public double getPostProbability() {
        return postProbability;
    }
}


class TargetCalculated {

    // 对同一对象的同一属性，有不同倾向的群体选择该对象的概率
    private double biasChoiceProbability;
    private double neutralChoiceProbability;

    TargetCalculated(){
        this(0.9, 0.3);
    }

    TargetCalculated(final double biasChoiceProbability, final double neutralChoiceProbability){
        this.biasChoiceProbability = biasChoiceProbability;
        this.neutralChoiceProbability = neutralChoiceProbability;
    }

    public double getBiasChoiceProbability() {
        return biasChoiceProbability;
    }

    public double getNeutralChoiceProbability() {
        return neutralChoiceProbability;
    }
}