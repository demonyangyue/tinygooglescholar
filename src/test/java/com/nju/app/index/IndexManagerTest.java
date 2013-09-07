package com.nju.app.index;

import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @brief unit test for IndexManager
 */
public class IndexManagerTest
{
    private IndexManager _manager = new IndexManager();


   @Test
    public void testCreateIndex() throws IOException {
        boolean res = this._manager.createIndex();
        assertTrue("create Index succeed",res);
    }
}


