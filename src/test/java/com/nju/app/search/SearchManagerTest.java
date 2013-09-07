package com.nju.app.search;

import java.util.List;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


/**
 * @brief unit test for SearchManager
 */
public class SearchManagerTest {
    
    @Test
    public void testSearch(){
        SearchManager manager = new SearchManager("chubby");
        List results = manager.search();
        assertTrue("find key words in the files", (results.size() > 0));
    }
}
