package com.gmail.andrewandy.customoregen.util;

import java.io.*;

public class FileUtil {

    public static void copy(InputStream source, File target) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        try (InputStream stream = source; OutputStream outputStream = new FileOutputStream(target)) {
            while ((length = stream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }


}
