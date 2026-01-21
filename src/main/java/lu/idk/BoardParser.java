package lu.idk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.NumberFormatException;

class BoardParser {
    public static Board parse(String inPath) throws Exception, IOException, BoardParseException {
        try (
            FileReader fr = new FileReader(inPath);
            BufferedReader br = new BufferedReader(fr)
        ) {
            boolean foundN = false;
            int n = 0;

            String line = br.readLine();
            int lineNum = 1;

            int[] array = null;
            int arrayIdx = 0;

            while (line != null) {
                line = line.split("#")[0].trim();
                if (line.isEmpty()) {
                    line = br.readLine();
                    lineNum++;
                    continue;
                }

                if (!foundN) {
                    try {
                        n = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        throw new BoardParseException(lineNum, "N must be a number");
                    }
                    if (n < 2 || n > 42) {
                        throw new BoardParseException(lineNum, "N must in range [2, 42]");
                    }
                    array = new int[n * n];
                    foundN = true;
                } else {
                    String[] parts = line.split("\\s+");
                    if (parts.length != n) {
                        throw new BoardParseException(lineNum, "line must contain N numbers");
                    }
                    for (String part: parts) {
                        int num;
                        try {
                            num = Integer.parseInt(part);
                        } catch (NumberFormatException e) {
                            throw new BoardParseException(lineNum,
                                    "line contains invalid number: '" + part + "'");
                        }
                        if (arrayIdx >= array.length) {
                            throw new BoardParseException(lineNum, "too many lines");
                        }
                        array[arrayIdx++] = num;
                    }
                }

                line = br.readLine();
                lineNum++;
            }

            if (!foundN) {
                throw new BoardParseException(lineNum, "file does not contain a board");
            }
            if (arrayIdx != n * n) {
                throw new BoardParseException(lineNum, "not enough lines");
            }

            return new Board(n, array);
        }
    }
}
