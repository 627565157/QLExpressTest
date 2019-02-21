package chensi.qlexpress.operator;

import chensi.qlexpress.util.AnalyzeExpressUtil;
import com.ql.util.express.Operator;

/**
 * @author chensi 2018/8/14 0014
 */
public class GreaterThanOperator extends Operator {

    @Override
    public Object executeInner(Object[] list) throws Exception {
        Object opdata1 = list[0];
        Object opdata2 = list[1];
        if (AnalyzeExpressUtil.FILTER_CONDITION_RESULT_VALUE_FALSE.equals(String.valueOf(opdata1))) {
            return false;
        }
        if(opdata1 instanceof Number) {
            if (opdata1 instanceof Integer) {
                return ((Integer) opdata1).intValue() > (Integer.valueOf(String.valueOf(opdata2)).intValue());
            } else if (opdata1 instanceof Float) {
                return ((Float) opdata1).floatValue() > (Float.valueOf(String.valueOf(opdata2)).floatValue());
            } else if (opdata1 instanceof Double) {
                return ((Double) opdata1).doubleValue() > (Double.valueOf(String.valueOf(opdata2)).doubleValue());
            } else {
                return ((Long) opdata1).longValue() > (Long.valueOf(String.valueOf(opdata2)).doubleValue());
            }
        } else {
            return false;
        }
    }
}
