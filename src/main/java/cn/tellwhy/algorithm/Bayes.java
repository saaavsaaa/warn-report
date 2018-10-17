package cn.tellwhy.algorithm;

/*
* 在各种条件B_i下A的发生概率
*/
class ConditionProbability {
}

/*
* 先验概率 × B条件下A发生的概率 / A发生的全概率
*/
public class Bayes {

    // 用户咨询了某个产品 enquiry，最终购买了另外一个产品 choice
    // 目标：在用户咨询了 enquiry 但没有购买意愿的时候，推荐 choice
    // 在咨询 enquiry 后购买其他产品或可能的相关产品中，找出概率最大的 choice，求 P(C|E)

    // P(C) 表示 choice 的概率，如果只考虑已有的购买记录，可以使用咨询后购买其他产品的频率
    // 如果已有足够多的购买记录，那最终的购买产品 choice 在所有咨询且未购买 enquiry 后的购买记录中的分布情况，
    // 就相当于它的概率，买的越多，P(C)越大

    // P(E|C) 购买了C的情况中，咨询过E的情况

    // P(C|E) = P(C) * P(E|C) / P(E)

    // 先验概率，初始值根据历史记录已知
    private double priorProbability;

    public Bayes(final double priorProbability) {
        this.priorProbability = priorProbability;
    }
}


class DishOrdered {

    // 初始情况，该对象被选择或不被选择在什么也不知道的情况下都是50%
    private final double Init_Probability = 0.5;
    private double priorProbability = Init_Probability;
    private double postProbability = Init_Probability;

    /*
     * 选择或不选的概率和是1，既选又不选就算两次计算了
     * @param chosen 主体属于bias类型为true，否则是中立主体
     * @param target 参与选择的主体中倾向于选择的概率
     */
    public void calculateProbability(final boolean chosen, final TargetCalculated target){
        // 先验概率是倾向选的主体选了+不倾向选择的主体选了，选择范围中主体选择了的概率
        // 这里没有不选的选择，如果是博客里那种选B(相当于不选)的情况，则bias和neutral的概率分别为：
        // (1 - target.getBiasChoiceProbability()) 和 (1 - target.getNeutralChoiceProbability())
        double bias = priorProbability * target.getBiasChoiceProbability();
        double neutral = (1 - priorProbability) * target.getNeutralChoiceProbability();
        // 全概率，数学期望
        double totalProbability = bias + neutral;

        double choice = chosen ? bias * target.getBiasChoiceProbability() : neutral * target.getNeutralChoiceProbability();
        this.postProbability = choice / totalProbability;

        // 下一次选择的先验概率
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