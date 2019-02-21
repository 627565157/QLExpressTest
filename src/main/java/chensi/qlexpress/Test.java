package chensi.qlexpress;

import chensi.qlexpress.util.AnalyzeMediaExpressUtil;

/**
 * @author chensi 2019/2/21 0021
 */
public class Test {
    public static void main(String[] args) throws Exception {
//        String json = "{vod_id: 2292}";
//        String express = "vod_id 包含 ['2876','2292']";
        String json = "{'pay_countrys':['NG'], 'free_countrys':['GH']}";
        String express = "is_pay == 'country_is_pay_1'";
        try {
            boolean result = AnalyzeMediaExpressUtil.getExpressResult(express,json,"NG");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
