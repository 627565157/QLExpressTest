package chensi.qlexpress.util;

import chensi.qlexpress.operator.*;
import com.alibaba.fastjson.JSON;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表达式解析工具类
 *
 * @author chensi 2018/8/10 0010
 */
public class AnalyzeExpressUtil {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AnalyzeExpressUtil.class);
    public static final String FILTER_CONDITION_RESULT_VALUE_FALSE = "filterCondition_false";

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

    private static ExpressRunner getInstance() {
        return ExpressRunnerSingleton.instance;
    }

    /**
     * 解析用户条件判定表达式
     *
     * @param express            公共组件生成的表达式
     * @param userStateAndTagMap 从user-profile中查询到的用户标签
     * @return
     * @throws Exception
     */
    public static boolean getExpressResult(String express, Map<String, Object> userStateAndTagMap) throws Exception {
        if (StringUtils.isBlank(express) || userStateAndTagMap == null || userStateAndTagMap.keySet().size() == 0) {
            return false;
        }
        logger.info("express: " + express + " map: " + userStateAndTagMap.toString());
        express = express.replaceAll("\\{", "(");
        express = express.replaceAll("}", ")");
        ExpressRunner runner = AnalyzeExpressUtil.getInstance();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        getFilterConditionOfExpress(express, context, userStateAndTagMap);
        Object r = runner.execute(express, context, null, false, false);
        return ((boolean) r);
    }

    /**
     * 解析用户条件判定表达式
     *
     * @param express 公共组件生成的表达式
     * @param mapJson  用户标签map jsonString格式
     * @return
     * @throws Exception
     */
    public static boolean getExpressResult(String express, String mapJson) throws Exception {
        if (StringUtils.isBlank(express) || StringUtils.isBlank(mapJson)) {
            return false;
        }
        logger.info("express: " + express + " mapJson: " + mapJson);
        express = express.replaceAll("\\{", "(");
        express = express.replaceAll("}", ")");
        Map<String, Object> map = jsonToMap(mapJson);
        if(map == null || map.entrySet().isEmpty()) {
            logger.info("map is null");
            return false;
        }
        ExpressRunner runner = AnalyzeExpressUtil.getInstance();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        getFilterConditionOfExpress(express, context, map);
        Object r = runner.execute(express, context, null, false, false);
        return ((boolean) r);
    }

    private static Map<String, Object> jsonToMap(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json);
    }

    private static List<String> getFilterConditionOfExpress(String express, DefaultContext<String, Object> context, Map<String, Object> map) {
        if (StringUtils.isBlank(express)) {
            return new ArrayList<>();
        }
        List<String> filterLinkAndOperatorAndBuckets = new ArrayList<>();
        filterLinkAndOperatorAndBuckets.add("&&");
        filterLinkAndOperatorAndBuckets.add("||");
        filterLinkAndOperatorAndBuckets.add("包含");
        filterLinkAndOperatorAndBuckets.add("不包含");
        filterLinkAndOperatorAndBuckets.add("==");
        filterLinkAndOperatorAndBuckets.add("!=");
        filterLinkAndOperatorAndBuckets.add(">=");
        filterLinkAndOperatorAndBuckets.add("<=");
        filterLinkAndOperatorAndBuckets.add(">");
        filterLinkAndOperatorAndBuckets.add("<");
        filterLinkAndOperatorAndBuckets.add("(");
        filterLinkAndOperatorAndBuckets.add(")");
        filterLinkAndOperatorAndBuckets.add("((");
        filterLinkAndOperatorAndBuckets.add("))");
        List<String> result = new ArrayList<>();
        List<String> filterConditionAndValue = Arrays.stream(express.split(" ")).filter(str -> StringUtils.isNotBlank(str) &&
                !filterLinkAndOperatorAndBuckets.contains(str))
                .collect(Collectors.toList());
        for (int i = 0; i < filterConditionAndValue.size(); i++) {
            if ((i + 1) % 2 != 0) {
                if (!result.contains(filterConditionAndValue.get(i))) {
                    if (map.containsKey(filterConditionAndValue.get(i))) {
                        context.put(filterConditionAndValue.get(i), map.get(filterConditionAndValue.get(i)));
                    } else {
                        context.put(filterConditionAndValue.get(i), FILTER_CONDITION_RESULT_VALUE_FALSE);
                    }
                    result.add(filterConditionAndValue.get(i));
                }
            }
        }
        return result;
    }
}