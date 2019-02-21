package chensi.qlexpress.operator;

import chensi.qlexpress.util.AnalyzeExpressUtil;
import com.ql.util.express.Operator;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 不包含运算符
 *
 * @author chensi 2018/8/10 0010
 */
public class ExcludeOperator extends Operator {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object executeInner(Object[] list) throws Exception {
        Object opdata1 = list[0];
        if (AnalyzeExpressUtil.FILTER_CONDITION_RESULT_VALUE_FALSE.equals(String.valueOf(opdata1))) {
            return true;
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
            for (Object obj : (Object[]) opdata2) {
                opList2.add(obj);
            }
        } else {
            opList2 = new ArrayList();
            opList2.add(opdata2);
        }
        if (opdata1 instanceof List) {
            return !IncludeOperator.includeAny(((List) opdata1), opList2);
        } else {
            List opList1 = new ArrayList();
            opList1.add(opdata1);
            return !IncludeOperator.includeAny(opList1, opList2);
        }
    }
}
