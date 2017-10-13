package com.fibbery.commons.sequence.snowflake;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by jiangnenghua on 17/10/13.   desc :
 */
public class SequenceContiPerfTest {

    @Rule
    public ContiPerfRule r = new ContiPerfRule();

    Sequence sequence = new Sequence();

    /*性能测试*/
    @Test
    @PerfTest(invocations = 200000000, threads = 12)
    public void testContiPerf() throws Exception {
        sequence.nextID();
    }


}
