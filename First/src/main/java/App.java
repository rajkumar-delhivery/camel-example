import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Created by delhivery on 15/11/16.
 */
class App {
    private static String X ="X";



    public boolean returnTrue(){
        return true;
    }

    public static void main(String arg[]) throws Exception{
        final CamelContext context = new DefaultCamelContext();
        final int x = 5;
        final App app = new App();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("direct:start").streamCaching()
                        //.setProperty(App.X, constant(2))
                        //.setBody().constant("A")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                             //   System.out.println(exchange.getIn().getBody());
                                // exchange.setProperty(App.X , constant(0));
                            }
                        })
                        .log(LoggingLevel.INFO, "refeived = ${body}")
//                        .to("direct:start");
//
//
//                from("direct:start")
                        .loopDoWhile(simple("${body.length} < 5"))
                        //.loop(6)

                        .choice()
                        .when().method(app, "returnTrue")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                System.out.println(exchange.getIn().getBody());
                                // exchange.setProperty(App.X , constant(0));
                            }
                        })
                        .end()
                        .to("mock:loop")
                        .transform(body().append("A"))
                        .end()
                        .to("direct:firstEnd");
                from("direct:firstEnd")
                        .split(body(String.class).tokenize(""))
                        .log("${body}")
                        .to("file:/tmp/target");
            }
        });
        context.start();
        ProducerTemplate template = context.createProducerTemplate();
        template.requestBody("direct:start", "A");
        Thread.sleep(1000);
        context.stop();
    }
}
