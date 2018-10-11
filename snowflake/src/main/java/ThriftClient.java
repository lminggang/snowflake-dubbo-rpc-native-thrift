import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import com.xintiaotime.yoy.thrift.snowflake.SnowFlake;

public class ThriftClient {
    public static void  main(String[] args) throws TException{
        TSocket socket = new TSocket("localhost", 30882, 10000);
        TFramedTransport transport = new TFramedTransport(socket);
        TProtocol protocol1 = new TCompactProtocol(transport);
        TProtocol protocol = new TMultiplexedProtocol(
                protocol1, "com.xintiaotime.yoy.thrift.snowflake.SnowFlake$Iface");
        transport.open();  // 建立连接PassPort

        SnowFlake.Client client = new SnowFlake.Client(protocol);


        long num = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < (1 << 12); i++){
            Long id = client.getSnowFlakeID();
            System.out.println(id);
            num++;
        }
        long end = System.currentTimeMillis();
        System.out.println(num);
        System.out.println("耗时为: " + (end - start) + "ms");
    }
}
