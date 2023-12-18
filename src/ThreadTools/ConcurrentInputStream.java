package ThreadTools;

import java.io.ObjectInputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.io.IOException;

public class ConcurrentInputStream
{
    ObjectInputStream s;
    ReentrantLock l;

    public ConcurrentInputStream (ObjectInputStream s) throws Exception
    {
        this.s= s;
        this.l= new ReentrantLock();
    }

    public Object readObject () throws IOException, ClassNotFoundException
    {
        try
        {
            this.l.lock();
            return this.s.readObject();
        }
        finally
        {
            this.l.unlock();
        }
    }
}