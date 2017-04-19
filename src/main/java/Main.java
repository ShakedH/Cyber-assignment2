/**
 * Created by user on 13/04/2017.
 */

import org.apache.commons.cli.*;

public class Main
{
    public static void main(String[] args)
    {
        // CLI Setup
        Options options = GetCLIOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd;
        try
        {
            cmd = parser.parse(options, args);
            String command = cmd.getOptionValue("command").toLowerCase();
            if (command.equals("encryption"))
                Encrypt(cmd);
            else if (command.equals("decryption"))
                Decrypt(cmd);
            else if (command.equals("attack"))
                Attack(cmd);
            else
                throw new IllegalArgumentException("Unknown Command");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            // TODO: 17-Apr-17
            e.printStackTrace();
            if (e instanceof ParseException)
                helpFormatter.printHelp("Cyber", options);
            System.exit(1);
        }
    }
    
    private static void Decrypt(CommandLine cmd) throws Exception
    {
        if (!cmd.hasOption('t'))
            throw new IllegalArgumentException("Missing text file path");
        if (!cmd.hasOption('v'))
            throw new IllegalArgumentException("Missing Initial Vector file path");
        if (!cmd.hasOption('k'))
            throw new IllegalArgumentException("Missing key file path");
        if (!cmd.hasOption('a'))
            throw new IllegalArgumentException("Missing Algorithm");
        
        String textFilePath = cmd.getOptionValue('t');
        byte[] cipher = CommonFunctions.ReadBytesFromFile(textFilePath);
        
        String vectorFilePath = cmd.getOptionValue('v');
        byte[] vector = CommonFunctions.ReadBytesFromFile(vectorFilePath);
        
        String algorithm = cmd.getOptionValue("algorithm");
        int blockSize;
        if (algorithm.equals("sub_cbc_10"))
            blockSize = 10;
        else if (algorithm.equals("sub_cbc_52"))
            blockSize = 52;
        else
            throw new IllegalArgumentException("Unknown Algorithm.");
        
        String keyFilePath = cmd.getOptionValue('k');
        Decryptor decryptor = new Decryptor(keyFilePath, vector, blockSize);
        String plainText = decryptor.Decrypt(cipher);
        if (cmd.hasOption('o'))
            CommonFunctions.WriteStringToFile(cmd.getOptionValue('o'), plainText, false);
        else
            System.out.println("Plain Text:\n" + plainText);
    }
    
    private static void Encrypt(CommandLine cmd) throws Exception
    {
        if (!cmd.hasOption('t'))
            throw new IllegalArgumentException("Missing text file path");
        if (!cmd.hasOption('v'))
            throw new IllegalArgumentException("Missing Initial Vector file path");
        if (!cmd.hasOption('k'))
            throw new IllegalArgumentException("Missing key file path");
        if (!cmd.hasOption('a'))
            throw new IllegalArgumentException("Missing Algorithm");
        
        String textFilePath = cmd.getOptionValue('t');
        byte[] plainText = CommonFunctions.ReadBytesFromFile(textFilePath);
        
        String vectorFilePath = cmd.getOptionValue('v');
        byte[] vector = CommonFunctions.ReadBytesFromFile(vectorFilePath);
        
        String algorithm = cmd.getOptionValue("algorithm");
        int blockSize;
        if (algorithm.equals("sub_cbc_10"))
            blockSize = 10;
        else if (algorithm.equals("sub_cbc_52"))
            blockSize = 52;
        else
            throw new IllegalArgumentException("Unknown Algorithm.");
        
        String keyFilePath = cmd.getOptionValue('k');
        Encryptor encryptor = new Encryptor(keyFilePath, vector, blockSize);
        String cipher = encryptor.Encrypt(plainText);
        
        if (cmd.hasOption('o'))
            CommonFunctions.WriteStringToFile(cmd.getOptionValue('o'), cipher, false);
        else
            System.out.println("Cipher:\n" + cipher);
    }
    
    private static void Attack(CommandLine cmd) throws Exception
    {
        // Arguments validity check
        if (!cmd.hasOption('a'))
            throw new IllegalArgumentException("Missing algorithm");
        if (!cmd.hasOption('t'))
            throw new IllegalArgumentException("Missing cipher text path");
        if (!cmd.hasOption('v'))
            throw new IllegalArgumentException("Missing initial vector path");
        if (!cmd.hasOption('o'))
            throw new IllegalArgumentException("Missing output path");
        
        String algorithm = cmd.getOptionValue("algorithm");
        String cipherTextPath = cmd.getOptionValue("text");
        String ivPath = cmd.getOptionValue("vector");
        String outputPath = cmd.getOptionValue("output");
        
        if (algorithm.equals("sub_cbc_10"))
        {
            Attacker10 attacker10 = new Attacker10(cipherTextPath, ivPath, outputPath);
            attacker10.Attack();
        }
        else if (algorithm.equals("sub_cbc_52"))
        {
            if (!cmd.hasOption("kp"))
                throw new IllegalArgumentException("Missing known plain text path");
            if (!cmd.hasOption("kc"))
                throw new IllegalArgumentException("Missing known cipher path");
            
            String knownPlainText = cmd.getOptionValue("KnownPlainText");
            String knownCipherText = cmd.getOptionValue("KnownCipherText");
            
            Attacker52 attacker52 = new Attacker52(cipherTextPath, knownPlainText, knownCipherText, ivPath, outputPath);
            attacker52.Attack();
        }
        else
            throw new IllegalArgumentException("Invalid algorithm. Algorithm must be 'sub_cbc_10' or 'sub_cbc_52'");
    }
    
    private static Options GetCLIOptions()
    {
        Options options = new Options();
        
        Option algorithm = new Option("a", "algorithm", true, "Encryption algorithm");
        algorithm.setArgs(1);
        algorithm.setRequired(true);
        options.addOption(algorithm);
        
        Option command = new Option("c", "command", true, "Command to preform");
        command.setArgs(1);
        command.setRequired(true);
        options.addOption(command);
        
        Option text = new Option("t", "text", true, "Text file path");
        text.setArgs(1);
        text.setRequired(true);
        options.addOption(text);
        
        Option key = new Option("k", "key", true, "Key file path");
        key.setArgs(1);
        key.setRequired(false);
        options.addOption(key);
        
        Option vector = new Option("v", "vector", true, "Initial vector file path");
        vector.setArgs(1);
        vector.setRequired(false);
        options.addOption(vector);
        
        Option output = new Option("o", "output", true, "Output file path");
        output.setArgs(1);
        output.setRequired(false);
        options.addOption(output);
        
        Option knownPlainText = new Option("kp", "KnownPlainText", true, "Known plain text file path");
        knownPlainText.setArgs(1);
        knownPlainText.setRequired(false);
        options.addOption(knownPlainText);
        
        Option KnownCipherText = new Option("kc", "KnownCipherText", true, "Known cipher text file path");
        KnownCipherText.setArgs(1);
        KnownCipherText.setRequired(false);
        options.addOption(KnownCipherText);
        
        return options;
    }
}
