package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    // 템플릿
    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback)
        throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            int ret = callback.doSomethingWithReader(br);
            return ret;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    // 곱셈 계산 콜백
    public Integer calcMultiply(String filepath) throws IOException {
        BufferedReaderCallback multiplyCallback =
            new BufferedReaderCallback() {
                public Integer doSomethingWithReader(BufferedReader br) throws IOException {
                    Integer multiply = 1;
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        multiply *= Integer.valueOf(line);
                    }
                    return multiply;
                }
            };

        return fileReadTemplate(filepath, multiplyCallback);
    }

    // 덧셈 계산 콜백
    public Integer calcSum(String filepath) throws IOException {
        BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
            @Override
            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
                Integer sum = 0;
                String line = null;
                while ((line = br.readLine()) != null) {
                    sum += Integer.valueOf(line);
                }
                return sum;
            }
        };

        return fileReadTemplate(filepath, sumCallback);
    }
}
