import java.net.ServerSocket;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.function.Function;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class OperateServer
{
    private HashMap<Integer, ClientData> m_client_list;

    private ServerSocketChannel serverSocketChannel;
    private Reactor[] reactors;

    private Selector selector;
    private int noOfReactors;

    public OperateServer(String host, int port, int noOfWorkerThreads)
    {
        try {
            selector = Selector.open();
            reactors = new Reactor[noOfWorkerThreads];
            this.noOfReactors = noOfWorkerThreads;
            for (int i = 0; i < noOfWorkerThreads; i++) {
                reactors[i] = new Reactor();
                Thread thread = new Thread(reactors[i]);
                thread.start();
            }

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, 9000));
            serverSocketChannel.configureBlocking(false);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            //handle exceptions
        }
    }

    public void start() throws IOException
    {

        int i = 0;

        while (true)
        {

            int readyChannels = selector.select();
            if (readyChannels == 0)
                continue;

            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext())
            {

                SelectionKey key = keyIterator.next();

                if (key.isAcceptable())
                {

                    ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
                    SocketChannel socket = serverSocket.accept();
                    reactors[i % noOfReactors].addChannel(socket);
                    i++;

                }

                keyIterator.remove();
            }
        }
    }

    public static void main(String[] args)
    {
        OperateServer acceptor = new OperateServer("localhost", 9000, 4);
        try {
            acceptor.start();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }
}



