package com.sakura.bot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class TxtReader {

    private TxtReader() {
    }

    public static List<String> readTxtFile(String file) throws IOException {
        try (InputStream is = TxtReader.class.getResourceAsStream(file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr)) {
            return readAllLines(br);
        } catch (IOException | IllegalArgumentException e) {
            throw new IOException("Failed to load configuration", e);
        }
    }

    private static List<String> readAllLines(BufferedReader reader) throws IOException {
        List<String> result = new ArrayList<>();
        for (; ; ) {
            String line = reader.readLine();
            if (line == null)
                break;
            result.add(line);
        }
        return result;
    }
}
