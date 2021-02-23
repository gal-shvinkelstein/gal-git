import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Reactor implements Runnable {

    private Queue queue = new ConcurrentLinkedQueue<SocketChannel>();

    private Selector selector;

    public Reactor() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    public void addChannel(SocketChannel socketChannel) {
        queue.add(socketChannel);
    }

    public void RemoveChannel(SocketChannel socketChannel) {
        queue.remove(socketChannel);
    }

    @Override
    public void run() {

        while (true) {

            try {
                SocketChannel socketChannel = (SocketChannel) queue.poll();
                if (socketChannel == null)
                    continue;

                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);

                int readyChannels = selector.select();

                if (readyChannels == 0)
                    continue;

                Set<SelectionKey> selectedKeys = selector.selectedKeys();

                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {

                    SelectionKey key = keyIterator.next();

                    if (key.isReadable()) {
                        // Read the channel, read handler

                    }

                    keyIterator.remove();
                }
            } catch (IOException e) {
                //handle IOExceptions
            }
        }
    }
}