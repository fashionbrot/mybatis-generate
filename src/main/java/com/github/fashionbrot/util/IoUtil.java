package com.github.fashionbrot.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author fashionbrot
 */
public class IoUtil {

    /**
     * Writes bytes from a <code>byte[]</code> to an <code>OutputStream</code>.
     *
     * @param data the byte array to write, do not modify during output,
     * null ignored
     * @param output the <code>OutputStream</code> to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    public static void write(final byte[] data, final OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

}
