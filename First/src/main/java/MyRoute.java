import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * Created by delhivery on 15/11/16.
 */
public class MyRoute extends RouteBuilder {

    public void configure() throws Exception{
        int x = 5;
        from("file:/tmp/source")

//                .setBody().constant("FAAAAAAAADDD //////////////////////")
                .setProperty("X", constant(x))
                .log("X")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println(exchangeProperty("X"));
                    }
                })
                .loopDoWhile(exchangeProperty("X").isGreaterThan(0))
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println(exchangeProperty("X"));
                    }
                })
                .end()
                .to("direct:firstEnd");
        from("direct:firstEnd")
                .split(body(String.class).tokenize("\n"))
                .log("${body}")
                .to("file:/tmp/target");
    }
}
