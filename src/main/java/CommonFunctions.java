import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 16/04/2017.
 */
public class CommonFunctions
{
    public static String EncryptDecryptSegmentByKey(String segment, Map<Character, Character> key)
    {
        String result = "";
        for (int i = 0; i < segment.length(); i++)
        {
            char current = segment.charAt(i);
            if (key.containsKey(current))
                result += key.get(current);
            else
                result += current;
        }
        return result;
    }
    
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
    
    public static Map<Character, Character> ReadKeyReversedFromFile(String keyFilePath) throws Exception
    {
        Map<Character, Character> key = new HashMap<Character, Character>();
        File file = new File(keyFilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null)
        {
            String[] split = st.split(" ");
            key.put(split[1].charAt(0), split[0].charAt(0));
        }
        return key;
    }
    
    public static String XOR(String a, String b)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.length(); i++)
            sb.append((char)(a.charAt(i) ^ b.charAt(i)));
        return sb.toString();
    }
    
    public static void WriteToFile(String filePath, String text, boolean append) throws IOException
    {
        File file = new File(filePath);
        FileUtils.write(file, text, "UTF-8", append);
    }
    
    public static String ReadFromFile(String filePath) throws IOException
    {
        File file = new File(filePath);
        return FileUtils.readFileToString(file, "UTF-8");
    }
}
