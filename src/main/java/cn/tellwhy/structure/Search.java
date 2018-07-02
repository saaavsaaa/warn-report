package cn.tellwhy.structure;

/**
 * Created by root on 17-2-8.
 */
public class Search {
    
    public static int binarySearch(char target, char[] array) {
        int low = 0;
        int upper = array.length - 1;
        while (low <= upper) {
            int mid = (low + upper) / 2;
            
            if (array[mid] < target) {
                low = mid + 1;
            }  else if (target < array[mid]) {
                upper = mid - 1;
            } else if (array[mid] == target){
                int index = mid + 1;
                if (target < array[index]){
                    return mid;
                }
                while (array[index] == target){
                    index++;
                }
                return index;
            }
        }
        return -1;
    }
}
