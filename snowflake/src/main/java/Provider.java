import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Provider {
    public static void main(String[] args) throws Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"/META-INF/spring/provider.xml"});
        context.start();

        System.out.println("server start OK!");
        System.in.read();
    }
}
