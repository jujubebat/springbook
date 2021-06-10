package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    // 덧셈 계산 기능 콜백
    public Integer calcSum(String filepath) throws IOException {
        LineCallback<Integer> sumCallback =
            new LineCallback<Integer>() {
                public Integer doSomethingWithLine(String line, Integer value) {
                    return value + Integer.valueOf(line);
                }
            };
        return lineReadTemplate(filepath, sumCallback, 0);
    }

    // 곱셈 계산 기능 콜백
    public Integer calcMultiply(String filepath) throws IOException {
        LineCallback<Integer> multiplyCallback =
            new LineCallback<Integer>() {
                public Integer doSomethingWithLine(String line, Integer value) {
                    return value * Integer.valueOf(line);
                }
            };
        return lineReadTemplate(filepath, multiplyCallback, 1);
    }

    // 문자열 연결 기능 콜백
    public String concatenate(String filepath) throws IOException { LineCallback<String> concatenateCallback =
        new LineCallback<String>() {
            public String doSomethingWithLine(String line, String value) {
                return value + line;
            }};
        return lineReadTemplate(filepath, concatenateCallback, "");
    }

    // 템플릿
    private <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal)
        throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            T res = initVal;
            String line = null;
            while ((line = br.readLine()) != null) {
                res = callback.doSomethingWithLine(line, res);
            }
            return res;
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

}
