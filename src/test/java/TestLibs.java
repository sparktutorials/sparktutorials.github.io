
import com.smatt.cc.db.DatabaseHelper;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author smatt
 */
public class TestLibs {
    
    @Test
    public void testDB() {
        
        new DatabaseHelper();
        
    }
    
}
