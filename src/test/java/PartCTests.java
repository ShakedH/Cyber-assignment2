import org.junit.Assert;
import org.junit.Test;

public class PartCTests
{
    @Test
    public void TestEncryptionWithFoundKey()
    {
        String textFilePath = "C:\\Users\\Ron Michaeli\\Desktop\\text.txt";
        String keyFilePath = "C:\\Users\\Ron Michaeli\\Desktop\\key.txt";
        String vectorFilePath = "C:\\Users\\Ron Michaeli\\Desktop\\vector.txt";
        String expectedResultFilePath = "C:\\Users\\Ron Michaeli\\Desktop\\expected.txt";
        try
        {
            Encryptor encryptor = new Encryptor(keyFilePath, CommonFunctions.ReadBytesFromFile(vectorFilePath), 52);
            String cipher = encryptor.Encrypt(CommonFunctions.ReadBytesFromFile(textFilePath));
            String expected = CommonFunctions.ReadStringFromFile(expectedResultFilePath);
            Assert.assertEquals(expected, cipher);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
