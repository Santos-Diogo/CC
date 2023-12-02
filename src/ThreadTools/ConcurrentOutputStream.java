package ThreadTools;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.io.IOException;

public class ConcurrentOutputStream
{
    ObjectOutputStream s;
    ReentrantLock l;

    public ConcurrentOutputStream (ObjectOutputStream s) throws Exception
    {
        this.s= s;
        this.l= new ReentrantLock();
    }

    public void writeObject (Object o) throws IOException
    {
        try
        {
            this.l.lock();
            this.s.writeObject(o);
        }
        finally
        {
            this.l.unlock();
        }
    }

    public void flush () throws IOException
    {
        try
        {
            this.l.lock();
            this.s.writeObject(l);
        }
        finally
        {
            this.l.lock();
        }
    }
}