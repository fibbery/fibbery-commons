package com.fibbery.commons.sequence.snowflake;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jiangnenghua on 17/10/13.   desc :
 */
public class SequenceRepeatedTest {

    Sequence sequence = new Sequence();

    /*重复性测试*/
    @Test
    public void testRepeated() {
        Set<Long> ids = new HashSet<Long>();
        long maxTime = 10000 * 10;
        for (int i = 0; i < maxTime; i++) {
            ids.add(sequence.nextID());
        }
        Assert.assertEquals(maxTime, ids.size());

    }
}
