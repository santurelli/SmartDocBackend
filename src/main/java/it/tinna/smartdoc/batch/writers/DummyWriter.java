package it.tinna.smartdoc.batch.writers;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class DummyWriter implements ItemWriter<Object>
{

    @Override
    public void write(Chunk<? extends Object> items) throws Exception
    {
        for ( Object obj : items )
        {
            System.out.println(obj);
        }
    }

}

