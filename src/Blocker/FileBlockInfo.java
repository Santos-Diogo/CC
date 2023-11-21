package Blocker;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.*;

public class FileBlockInfo 
{
    private ReentrantReadWriteLock rwLock;
    private Map<String, BlockInfo> filesBlockInfo;

    /**
     * @param dir Directory from wich we read the files
     */
    public FileBlockInfo(String dir) 
    {
        this.rwLock= new ReentrantReadWriteLock();
        this.filesBlockInfo= new HashMap<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dir))) 
        {
            for (Path filePath : directoryStream) 
            {
                String fileName = filePath.getFileName().toString();
                Pattern pattern = Pattern.compile("^(.+)\\.fsblk\\.(\\d+)$");
                Matcher matcher = pattern.matcher(fileName);

                if (matcher.matches())
                {
                    fileName = matcher.group(1);

                    if (!filesBlockInfo.containsKey(fileName))
                        filesBlockInfo.put(fileName, new BlockInfo(null, new ArrayList<>()));
                    filesBlockInfo.get(fileName).add_block(Integer.parseInt(matcher.group(2)));
                }
                else
                {
                    Long fileSize = Files.size(filePath);
                    filesBlockInfo.put(fileName, new BlockInfo(fileSize, null));
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * Add a file to the Structure
     * 
     * @param name   name of the file
     * @param size   size of the file in blocks
     * @param blocks blocks owned. (null for all blocks)
     */
    public void add_file(String name, long size, List<Integer> blocks) 
    {
        this.rwLock.writeLock().lock();
        try
        {
            filesBlockInfo.put(name, new BlockInfo(size, blocks));
        }
        finally
        {
            this.rwLock.writeLock().unlock();
        }
    }

    /**
     * Remove a file with a given name from the node info
     * 
     * @param name name of the file
     */
    public void rm_file (String name) 
    {
        this.rwLock.writeLock().lock();
        try
        {
            filesBlockInfo.remove(name);
        }
        finally
        {
            this.rwLock.writeLock().unlock();
        }
    }

    /**
     * Remove a block from a file
     * @param name
     * @param block
     */
    public void rm_block (String name, int block)
    {
        this.rwLock.writeLock().lock();
        try
        {
            filesBlockInfo.get(name).get_filesBlocks().remove(block);
        }
        finally
        {
            this.rwLock.writeLock().unlock();
        }
    }

    public BlockInfo get_BlockInfo (String file)
    {
        this.rwLock.readLock().lock();
        try
        {
            return filesBlockInfo.get(file);
        }
        finally
        {
            this.rwLock.readLock().unlock();
        }
    }
}