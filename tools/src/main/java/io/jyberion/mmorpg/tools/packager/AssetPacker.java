package io.jyberion.mmorpg.tools.packager;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.crypto.CipherInputStream;

public class AssetPacker {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY = "MySecretKey12345"; // Must be 16 bytes for AES

    public static void packAssets(File baseFolder, ArrayList<File> files, File outputFile) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            // Write Header
            writeHeader(bos, files.size());

            // Initialize Cipher
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

            // Write File Entries
            for (File file : files) {
                writeFileEntry(bos, baseFolder, file, cipher);
            }

            // Optionally write footer (e.g., checksum)
        }
    }

    private static void writeHeader(BufferedOutputStream bos, int numFiles) throws IOException {
        // Magic Number
        bos.write("SC12".getBytes(StandardCharsets.US_ASCII));

        // Version Number (e.g., 1)
        bos.write(intToBytes(1));

        // Number of Files
        bos.write(intToBytes(numFiles));
    }

    private static void writeFileEntry(BufferedOutputStream bos, File baseFolder, File file, Cipher cipher) throws Exception {
        // Get relative path
        String relativePath = baseFolder.toURI().relativize(file.toURI()).getPath();

        // Write Path Length and Path
        byte[] pathBytes = relativePath.getBytes(StandardCharsets.UTF_8);
        bos.write(intToBytes(pathBytes.length));
        bos.write(pathBytes);

        // Write File Size
        long fileSize = file.length();
        bos.write(longToBytes(fileSize));

        // Encrypt and Write File Data
        try (FileInputStream fis = new FileInputStream(file);
             CipherInputStream cis = new CipherInputStream(fis, cipher)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = cis.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
        }
    }

    private static Cipher getCipher(int mode) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ENCRYPTION_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(mode, keySpec);
        return cipher;
    }

    private static byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    private static byte[] longToBytes(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }
}
