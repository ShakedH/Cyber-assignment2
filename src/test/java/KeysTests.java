import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Ron Michaeli on 25-Apr-17.
 */
public class KeysTests
{
    @Test
    public void CompareKeys()
    {
        String originalKeyPath = "C:\\Users\\user\\Desktop\\New\\key.txt";
        String newKeyPath = "C:\\Users\\user\\Desktop\\New\\test_out.txt";
        try
        {
            Map<Character, Character> originalKey = CommonFunctions.ReadKeyFromFile(originalKeyPath);
            Map<Character, Character> newKey = CommonFunctions.ReadKeyFromFile(newKeyPath);
            int diff = 0;
            for (Character c : originalKey.keySet())
                if (!originalKey.get(c).equals(newKey.get(c)))
                    diff++;
            Assert.assertEquals(0, diff);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}