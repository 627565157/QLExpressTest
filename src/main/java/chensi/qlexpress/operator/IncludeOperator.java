package chensi.qlexpress.operator;

import chensi.qlexpress.util.AnalyzeExpressUtil;
import com.ql.util.express.Operator;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含运算符
 *
 * @author chensi 2018/8/10 0010
 */
public class IncludeOperator extends Operator {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object executeInner(Object[] list) throws Exception {
        Object opdata1 = list[0];
        if (AnalyzeExpressUtil.FILTER_CONDITION_RESULT_VALUE_FALSE.equals(String.valueOf(opdata1))) {
            return false;
        }
        Object opdata2 = list[1];
        if (opdata1 == null || opdata2 == null) {
            logger.error("EqualOperator error operator param is null...");
            return false;
        }
        List opList2;
        if (opdata2 instanceof List) {
            opList2 = (List) opdata2;
        } else if (opdata2 instanceof Object[]) {
            opList2 = new ArrayList();
            for (Object str : (Object[]) opdata2) {
                opList2.add(String.valueOf(str));
            }
        } else {
            opList2 = new ArrayList();
            opList2.add(opdata2);
        }
        if (opdata1 instanceof List) {
            return includeAny(((List) opdata1), opList2);
        } else {
            List opList1 = new ArrayList();
            opList1.add(opdata1);
            return includeAny(opList1, opList2);
        }
    }

    public static boolean includeAny(List list1, List list2) {
        if (list2.isEmpty()) {
            return true;
        }
        if (list1.isEmpty()) {
            return false;
        }
        for (Object str : list2) {
            for (Object str1 : list1) {
                if (String.valueOf(str1).equalsIgnoreCase(String.valueOf(str))) {
                    return true;
                }
            }
        }
        return false;
    }
}
