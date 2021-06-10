package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public Integer calcSum(String filepath) throws IOException {
        BufferedReader br = null;

        try {
            // 한 줄씩 읽기 편하게 BufferedReader로 파일을 가져온다.
            br = new BufferedReader(new FileReader(filepath));
            Integer sum = 0;
            String line = null;

            // 마지막 라인까지 한 줄씩 읽어가면서 숫 자를 더한다.
            while ((line = br.readLine()) != null) {
                sum += Integer.valueOf(line);
            }

            return sum;
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
