package chensi.qlexpress.util;

import com.alibaba.fastjson.JSON;
import com.ql.util.express.ExpressRunner;
import chensi.qlexpress.operator.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author chensi 2018/9/20 0020
 */
public abstract class AnalyzeUtil {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AnalyzeUtil.class);
    public static final String FILTER_CONDITION_RESULT_VALUE_FALSE = "filterCondition_false";
    public static final String COUNTRY_IS_PAY = "is_pay";
    public static final String COUNTRY_IS_PAY_VALUE = "country_is_pay_1";
    public static final String COUNTRY_IS_NOT_PAY_VALUE = "country_is_pay_0";

    private static class ExpressRunnerSingleton {
        public static ExpressRunner instance = new ExpressRunner();

        static {
            try {
                instance.addOperator("包含", new IncludeOperator());
                instance.addOperator("不包含", new ExcludeOperator());
            } catch (Exception e) {
                logger.error("add operator error....");
            }
            instance.replaceOperator("==", new EqualOperator());
            instance.replaceOperator("!=", new UnEqualOperator());
            instance.replaceOperator(">", new GreaterThanOperator());
            instance.replaceOperator(">=", new GreaterEqualOperator());
            instance.replaceOperator("<", new LessThanOperator());
            instance.replaceOperator("<=", new LessEqualOperator());
        }
    }

    protected static ExpressRunner getInstance() {
        return AnalyzeUtil.ExpressRunnerSingleton.instance;
    }

    protected static Map<String, Object> jsonToMap(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json);
    }
}
