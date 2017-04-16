import org.junit.Assert;
import org.junit.Test;

/**
 * Created by user on 16/04/2017.
 */
public class EncryptorTest
{
    @Test
    public void testEncryption()
    {
        String directory = "C:\\Users\\user\\Dropbox\\לימודים\\שנה ג' סמסטר ב'\\אבטחה\\מטלה 2\\Files\\";
        String keyFilePath = directory + "key_example.txt";
        String PlainTextFilePath = directory + "plainMsg_example.txt";
        String vectorFilePath = directory + "IV_example.txt";
        String ExpectedResultFilePath = directory + "cipherMsg_example.txt";
        try
        {
            Encryptor encryptor = new Encryptor(keyFilePath, CommonFunctions.ReadFromFile(vectorFilePath));
            String cipher = encryptor.Encrypt(PlainTextFilePath);
            String expected = CommonFunctions.ReadFromFile(ExpectedResultFilePath);
            Assert.assertEquals(cipher, expected);
        } catch (Exception e)
        {
            e.getStackTrace();
            Assert.fail();
        }
    }
    
}