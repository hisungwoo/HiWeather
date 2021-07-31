package org.ganache.hiweather;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void apiTest() throws IOException {


        List<String> test = new ArrayList<>();
        test.add("0000");
        test.add("0000");
        test.add("0000");
        test.add("0300");
        test.add("0600");
        test.add("0900");
        test.add("1200");
        test.add("1500");
        test.add("1800");
        test.add("2100");
        test.add("0000");
        test.add("0000");

        List<String> result = new ArrayList<>();

        LinkedHashSet<String> linked = new LinkedHashSet<>();

        for(int i = 0 ; i < test.size(); i++) {
            System.out.println(test.get(i));
            linked.add(test.get(i));
        }


        System.out.println("linked = " + linked);





    }
}