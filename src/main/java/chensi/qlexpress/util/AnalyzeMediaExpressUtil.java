package chensi.qlexpress.util;

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
 * 媒介表达式判定工具类
 * 内涵特殊逻辑：是否付费国家业务
 *
 * @author chensi 2018/9/19 0019
 */
public class AnalyzeMediaExpressUtil extends AnalyzeUtil {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AnalyzeMediaExpressUtil.class);

    public static final String CHANNEL_PLATFORM = "channel_platform";
    public static final String OTT_CHANNEL_PLATFORM = "channel_platform_1";
    public static final String DTT_CHANNEL_PLATFORM = "channel_platform_2";
    public static final String DTH_CHANNEL_PLATFORM = "channel_platform_3";

    /**
     * 解析用户条件判定表达式
     *
     * @param express            公共组件生成的表达式
     * @param userStateAndTagMap 从user-profile中查询到的用户标签
     * @return
     * @throws Exception
     */
    public static boolean getExpressResult(String express, Map<String, Object> userStateAndTagMap, String userCountryCode) throws Exception {
        if (StringUtils.isBlank(express) || userStateAndTagMap == null || userStateAndTagMap.keySet().size() == 0) {
            return false;
        }
        logger.info("express: " + express + " map: " + userStateAndTagMap.toString());
        express = express.replaceAll("\\{", "(");
        express = express.replaceAll("}", ")");
        express = express.replaceAll("'" + COUNTRY_IS_PAY_VALUE + "'", COUNTRY_IS_PAY_VALUE);
        express = express.replaceAll("'" + COUNTRY_IS_NOT_PAY_VALUE + "'", COUNTRY_IS_NOT_PAY_VALUE);
        express = express.replaceAll("'" + OTT_CHANNEL_PLATFORM + "'", OTT_CHANNEL_PLATFORM);
        express = express.replaceAll("'" + DTT_CHANNEL_PLATFORM + "'", DTT_CHANNEL_PLATFORM);
        express = express.replaceAll("'" + DTH_CHANNEL_PLATFORM + "'", DTH_CHANNEL_PLATFORM);
        ExpressRunner runner = AnalyzeMediaExpressUtil.getInstance();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        getFilterConditionOfExpress(express, context, userStateAndTagMap, userCountryCode);
        Object r = runner.execute(express, context, null, false, false);
        return ((boolean) r);
    }

    /**
     * 解析用户条件判定表达式
     *
     * @param express 公共组件生成的表达式
     * @param mapJson 用户标签map jsonString格式
     * @return
     * @throws Exception
     */
    public static boolean getExpressResult(String express, String mapJson, String userCountryCode) throws Exception {
        if (StringUtils.isBlank(express) || StringUtils.isBlank(mapJson)) {
            return false;
        }
        logger.info("express: " + express + " mapJson: " + mapJson);
        express = express.replaceAll("\\{", "(");
        express = express.replaceAll("}", ")");
        express = express.replaceAll("'" + COUNTRY_IS_PAY_VALUE + "'", COUNTRY_IS_PAY_VALUE);
        express = express.replaceAll("'" + COUNTRY_IS_NOT_PAY_VALUE + "'", COUNTRY_IS_NOT_PAY_VALUE);
        express = express.replaceAll("'" + OTT_CHANNEL_PLATFORM + "'", OTT_CHANNEL_PLATFORM);
        express = express.replaceAll("'" + DTT_CHANNEL_PLATFORM + "'", DTT_CHANNEL_PLATFORM);
        express = express.replaceAll("'" + DTH_CHANNEL_PLATFORM + "'", DTH_CHANNEL_PLATFORM);
        Map<String, Object> map = jsonToMap(mapJson);
        if (map == null || map.entrySet().isEmpty()) {
            logger.info("map is null");
            return false;
        }
        ExpressRunner runner = AnalyzeMediaExpressUtil.getInstance();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        getFilterConditionOfExpress(express, context, map, userCountryCode);
        Object r = runner.execute(express, context, null, false, false);
        return ((boolean) r);
    }

    private static List<String> getFilterConditionOfExpress(String express, DefaultContext<String, Object> context, Map<String, Object> map, String userCountryCode) {
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
                    } else if (COUNTRY_IS_PAY.equals(filterConditionAndValue.get(i))) {
                        if (StringUtils.isBlank(userCountryCode)) {
                            context.put(filterConditionAndValue.get(i), FILTER_CONDITION_RESULT_VALUE_FALSE);
                        } else {
                            context.put(filterConditionAndValue.get(i), userCountryCode);
                            context.put(COUNTRY_IS_PAY_VALUE, map.get("pay_countrys"));
                            context.put(COUNTRY_IS_NOT_PAY_VALUE, map.get("free_countrys"));
                        }
                    } else if(CHANNEL_PLATFORM.equals(filterConditionAndValue.get(i))) {
                        if(StringUtils.isBlank(userCountryCode)) {
                            context.put(filterConditionAndValue.get(i), FILTER_CONDITION_RESULT_VALUE_FALSE);
                        } else {
                            context.put(filterConditionAndValue.get(i), userCountryCode);
                            context.put(OTT_CHANNEL_PLATFORM, map.get("channel_ott_countrys"));
                            context.put(DTT_CHANNEL_PLATFORM, map.get("channel_dtt_countrys"));
                            context.put(DTH_CHANNEL_PLATFORM, map.get("channel_dth_countrys"));
                        }
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
