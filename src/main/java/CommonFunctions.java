import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CommonFunctions
{
    public static Map<Character, Character> ReadKeyFromFile(String keyFilePath) throws Exception
    {
        Map<Character, Character> key = new HashMap<Character, Character>();
        File file = new File(keyFilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null)
        {
            String[] split = st.split(" ");
            key.put(split[0].charAt(0), split[1].charAt(0));
        }
        return key;
    }
    
    public static byte XorByte(byte a, byte b)
    {
        return (byte)(a ^ b);
    }
    
    public static void WriteStringToFile(String filePath, String text, boolean append) throws IOException
    {
        File file = new File(filePath);
        FileUtils.write(file, text, "UTF-8", append);
    }
    
    public static String ReadStringFromFile(String filePath) throws IOException
    {
        File file = new File(filePath);
        return FileUtils.readFileToString(file, "UTF-8");
    }
    
    public static byte[] ReadBytesFromFile(String filePath) throws Exception
    {
        return org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(filePath));
    }
}
