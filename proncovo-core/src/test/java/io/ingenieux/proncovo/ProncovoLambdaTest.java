package io.ingenieux.proncovo;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by aldrin on 18/01/16.
 */
public class ProncovoLambdaTest {
    ProncovoLambda l = new ProncovoLambda();

    @Test
    public void testInvocation() throws Exception {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        l.invokeLookup(new ByteArrayInputStream("\"Calle 45a sur # 39b-226 Envigado\"".getBytes()), outputStream, null);

        System.err.println(new String(outputStream.toByteArray()));
    }
}
