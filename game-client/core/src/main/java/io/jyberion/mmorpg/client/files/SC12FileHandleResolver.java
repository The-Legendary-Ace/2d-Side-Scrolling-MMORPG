package io.jyberion.mmorpg.client.files;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.crypto.CipherInputStream;

public class SC12FileHandleResolver implements FileHandleResolver {

    private HashMap<String, FileEntry> fileEntries = new HashMap<>();
    private RandomAccessFile sc12File;
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY = "MySecretKey12345";

    public SC12FileHandleResolver(String sc12FilePath) {
        try {
            sc12File = new RandomAccessFile(sc12FilePath, "r");
            readHeaderAndIndex();
        } catch (Exception e) {
            throw new GdxRuntimeException("Failed to open SC12 file: " + sc12FilePath, e);
        }
    }

    private void readHeaderAndIndex() throws Exception {
        // Read Magic Number
        byte[] magicBytes = new byte[4];
        sc12File.readFully(magicBytes);
        String magic = new String(magicBytes, StandardCharsets.US_ASCII);
        if (!magic.equals("SC12")) {
            throw new IOException("Invalid SC12 file format.");
        }

        // Read Version Number
        int version = sc12File.readInt();

        // Read Number of Files
        int numFiles = sc12File.readInt();

        // Read File Entries
        for (int i = 0; i < numFiles; i++) {
            // Read Path Length and Path
            int pathLength = sc12File.readInt();
            byte[] pathBytes = new byte[pathLength];
            sc12File.readFully(pathBytes);
            String relativePath = new String(pathBytes, StandardCharsets.UTF_8);

            // Read File Size
            long fileSize = sc12File.readLong();

            // Record the position of the file data
            long fileDataPosition = sc12File.getFilePointer();

            // Skip over the file data
            sc12File.seek(fileDataPosition + fileSize);

            // Store the file entry
            FileEntry entry = new FileEntry(relativePath, fileDataPosition, fileSize);
            fileEntries.put(relativePath, entry);
        }
    }

    @Override
    public FileHandle resolve(String fileName) {
        FileEntry entry = fileEntries.get(fileName);
        if (entry == null) {
            throw new GdxRuntimeException("File not found in SC12 archive: " + fileName);
        }
        return new SC12FileHandle(sc12File, entry);
    }

    // Inner class to represent a file entry
    private static class FileEntry {
        String path;
        long position;
        long size;

        public FileEntry(String path, long position, long size) {
            this.path = path;
            this.position = position;
            this.size = size;
        }
    }

    // Inner class for FileHandle
    private static class SC12FileHandle extends FileHandle {
        private RandomAccessFile sc12File;
        private FileEntry entry;

        public SC12FileHandle(RandomAccessFile sc12File, FileEntry entry) {
            super(entry.path);
            this.sc12File = sc12File;
            this.entry = entry;
        }

        @Override
        public InputStream read() {
            try {
                sc12File.seek(entry.position);
                InputStream is = new BoundedInputStream(new FileInputStream(sc12File.getFD()), entry.size);
                Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
                return new CipherInputStream(is, cipher);
            } catch (Exception e) {
                throw new RuntimeException("Error reading file: " + entry.path, e);
            }
        }

        // Implement other methods as needed

        private Cipher getCipher(int mode) throws Exception {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(mode, keySpec);
            return cipher;
        }
    }

    // Utility class to limit InputStream to a certain size
    private static class BoundedInputStream extends InputStream {
        private InputStream in;
        private long remaining;

        public BoundedInputStream(InputStream in, long size) {
            this.in = in;
            this.remaining = size;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int result = in.read();
            if (result != -1) {
                remaining--;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int toRead = (int) Math.min(len, remaining);
            int result = in.read(b, off, toRead);
            if (result != -1) {
                remaining -= result;
            }
            return result;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}
