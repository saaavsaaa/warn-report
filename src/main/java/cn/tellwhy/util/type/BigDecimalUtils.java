package cn.tellwhy.util.type;



import java.math.BigDecimal;
import java.text.DecimalFormat;

/***
 * BigDecimal工具类
 * @author
 * @date 2014年12月11日
 */
public class BigDecimalUtils {
	
	
	/**
	 * 根据传入的Bigdecimal 得到String    1,000,205.1  的格式
	 *@author oliver
	 *@time 2015年4月11日 下午4:35:04
	 */
    public static String decimalFormat(BigDecimal beforeFmtNumber){
    	if(null != beforeFmtNumber){
 //   		BigDecimal decimalNum = beforeFmtNumber.stripTrailingZeros();
	    	String decimalNumber = beforeFmtNumber.toString();
	        DecimalFormat df = null;
	        int docIndex = decimalNumber.indexOf(".");
	        int decimalIndex = decimalNumber.length() - decimalNumber.indexOf(".") - 1;
	        if(0 < docIndex){	
	        	switch(decimalIndex){
	            	case 0:
	            		df = new DecimalFormat("###,##0");  
	            		break;
	            	case 1:
	            		df = new DecimalFormat("###,##0.0");
	            		break;
	            	default:
	            		df = new DecimalFormat("###,##0.00");
	            		break;
	            }
	        }else{  
	            df = new DecimalFormat("###,##0");  
	        }
	        double number = 0.00;  
	        try {  
	             number = Double.parseDouble(decimalNumber);  
	        } catch (Exception e) {  
	            number = 0.00;  
	        }
	        return df.format(number);
	    	}
    	return null;
    }
    
    public static String decimalBonus(BigDecimal beforeFmtNumber){
    	if(null != beforeFmtNumber){
 //   		BigDecimal decimalNum = beforeFmtNumber.stripTrailingZeros();
	    	String decimalNumber = beforeFmtNumber.toString();
	        DecimalFormat df = null;
	        int docIndex = decimalNumber.indexOf(".");
	        int decimalIndex = decimalNumber.length() - decimalNumber.indexOf(".") - 1;
	        int index = decimalNumber.length();
	        if(0 < docIndex){
	        	if(isInteger(decimalNumber, docIndex, index)){
	        		df = new DecimalFormat("#####0");  
	        	}else{
		        	switch(decimalIndex){
		            	case 0:
		            		df = new DecimalFormat("#####0");  
		            		break;
		            	case 1:
		            		df = new DecimalFormat("#####0.0");
		            		break;
		            	default:
		            		df = new DecimalFormat("#####0.00");
		            		break;
		            }
	        	}
	        }else{  
	            df = new DecimalFormat("#####0");  
	        }
	        double number = 0;  
	        try {  
	             number = Double.parseDouble(decimalNumber);  
	        } catch (Exception e) {  
	            number = 0;  
	        }
	        return df.format(number);
	    	}
    	return null;
    }
    
	
    
    public static BigDecimal NullToZero(BigDecimal bigDecimal){
        if(bigDecimal==null){
            return new BigDecimal("0.00");
        }
        return bigDecimal;
    }
    public static double NullToDoubleZero(BigDecimal bigDecimal){
        if(bigDecimal==null){
            return NullToZero(bigDecimal).doubleValue();
        }
        return bigDecimal.doubleValue();
    }
    
    // 默认除法运算精度  
    private static final int DEF_DIV_SCALE = 10;  
  
    // 这个类不能实例化  
    private BigDecimalUtils() {  
          
    }  
  
    public static String format (double num){
        DecimalFormat   df   = new   DecimalFormat("#0.00");
        return df.format(num);
    }
    public static String formatDouble (double num){
        DecimalFormat   df   = new   DecimalFormat("#0.00");
        return  df.format(num);  
    }
    public static double formatBigDecimal(BigDecimal bigDecimal){
        double doubleZero = NullToDoubleZero(bigDecimal);
        return Double.parseDouble(formatDouble(doubleZero));
    }
    
    /** 
     * 提供精确的加法运算     
     * @param v1           被加数 
     * @param v2            加数 
     * @return 两个参数的和 
     */  
    public static double add(double v1, double v2) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.add(b2).doubleValue();  
    }  
  
    /** 
     * 提供精确的减法运算 
     
     * @param v1            被减数 
     * @param v2            减数 
     * @return 两个参数的差 
     */  
    public static double sub(double v1, double v2) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.subtract(b2).doubleValue();  
    }  
  
    /** 
     * 提供精确的乘法运算     
     * @param v1            被乘数 
     * @param v2            乘数 
     * @return 两个参数的积 
     */  
    public static double mul(double v1, double v2) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.multiply(b2).doubleValue();  
    }  
  
    /** 
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入   
     * @param v1            被除数 
     * @param v2            除数 
     * @return 两个参数的商 
     */  
    public static double div(double v1, double v2) {  
        return div(v1, v2, DEF_DIV_SCALE);  
    }  
  
    /** 
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。     
     * @param v1            被除数 
     * @param v2            除数 
     * @param scale         表示表示需要精确到小数点以后几位。 
     * @return 两个参数的商 
     */  
    public static double div(double v1, double v2, int scale) {  
        if (scale < 0) {  
            throw new IllegalArgumentException(  
                    "The scale must be a positive integer or zero");  
        }  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }
    
    /**
     * 传入取的方式   来获得结果
     *@author oliver
     *@time 2015年4月7日 下午3:11:16
     */
    public static double div(double v1, double v2, int scale, int divType) {  
        if (scale < 0) {  
            throw new IllegalArgumentException(  
                    "The scale must be a positive integer or zero");  
        }  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.divide(b2, scale, divType).doubleValue();  
    }  
  
    /** 
     * 提供精确的小数位四舍五入处理。   
     * @param v           需要四舍五入的数字 
     * @param scale       小数点后保留几位 
     * @return 四舍五入后的结果 
     */  
    public static double round(double v, int scale) {  
        if (scale < 0) {  
            throw new IllegalArgumentException(  
                    "The scale must be a positive integer or zero");  
        }  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        BigDecimal one = new BigDecimal("1");  
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }  
    
    public static String roundFormatter(double douVal){
       return format(round(douVal,2));
    }
    
    /**
     * 根据decimal字符串 小数点位置 和总长  判断是否是整数
     * @param numberStr
     * @param docIndex
     * @param index
     * @return
     */
    public static boolean isInteger(String numberStr, int docIndex,int index){
    	boolean flag = true;
		for(int i = docIndex + 1;i < index;i ++){
			int asciiCode = numberStr.charAt(i);
			if(asciiCode != 48){
				flag = false;
				break;
			}
		}
		return flag;
		
    }
    
    /**
	 * 将钱数转换为 每三位以逗号隔开
	 * @param money
	 * @return
	 */
	public static String getMoney(BigDecimal money){
		String mm = "";
		String mm1 = "";
		if(money.toString().indexOf(".")>0){
			mm=money.toString().substring(0, money.toString().length()-3);
			mm1 = money.toString().substring(money.toString().length()-3, money.toString().length());
			if(mm.equals("0") && mm1.equals(".00")){
				mm1 = "";
			}
		}else{
			mm=money.toString();
		}
		String mm2="";
		int len=mm.length();
		int flag=0;
		if(len<=3){
			mm2=mm;
		}else{
			int max=(int) Math.ceil((double)len/3);
			for(int i=0;i<max;i++){
				
				flag=len-3;
				
				if(i+1==max){
					mm2=mm.substring(0,len)+","+mm2;
					continue;
				}else if(i==0){
					mm2=mm.substring(flag, len);
				}else{
					mm2=mm.substring(flag, len)+","+mm2;
				}
				
				len=len-3;
			}
		}
		return mm2+mm1;
	}
}
