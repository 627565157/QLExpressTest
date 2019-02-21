package chensi.qlExpress;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

public class QLExpress {
    public static final String test_a = "1 + 2 > 0";
    public static final String test_b = "4 + 5 == 9";

    @Test
    public void testA() throws Exception {
        long start = System.currentTimeMillis();
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        String express = test_a;
        for (int i = 0; i < 10000; i++) {
            double a = Math.random();
            double b = Math.random();
            context.put("a", Math.random());
            context.put("b", Math.random());
            Object r = runner.execute(express, context, null, true, false);
//            System.out.println(r);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }

    @Test
    public void testB() throws Exception {
        long start = System.currentTimeMillis();
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        String express = test_b;
        for (int i = 0; i < 10000; i++) {
            Object r = runner.execute(express, context, null, true, false);
//            System.out.println(r);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }

    @Test
    public void testC() throws Exception {
        long start = System.currentTimeMillis();
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.addOperator("join", new JoinOperator());
        for (int i = 0; i < 10000; i++) {
            Object r = runner.execute("1 join 2 join 3", context, null, false, false);
//            System.out.println(r);
//            System.out.println(r);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }

    @Test
    public void testD() throws Exception {
        long start = System.currentTimeMillis();
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",
                new String[]{"double"}, null);
        runner.addFunctionOfClassMethod("转换为大写", BeanExample.class.getName(),
                "upper", new String[]{"String"}, null);

        runner.addFunctionOfServiceMethod("打印", System.out, "println", new String[]{"String"}, null);
        runner.addFunctionOfServiceMethod("contains", new BeanExample(), "anyContains",
                new Class[]{String.class, String.class}, null);
        String exp = "取绝对值(-100);转换为大写(\"hello world\");打印(\"你好吗？\");contains(\"helloworld\",\"aeiou\")";
        String exp_a =  "取绝对值(-100)";
        String exp_b =  "转换为大写(\"hello world\");";
        String exp_c =  "打印(\"你好吗？\");";
        String exp_d =  "contains(\"helloworld\",\"aeiou\");";
        for (int i = 0; i < 10000; i++) {
            runner.execute(exp, context, null, false, false);
//            runner.execute(exp_a, context, null, false, false);
//            runner.execute(exp_b, context, null, false, false);
//            runner.execute(exp_c, context, null, false, false);
//            runner.execute(exp_d, context, null, false, false);

        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }

}
