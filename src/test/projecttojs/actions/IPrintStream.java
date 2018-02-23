package test.projecttojs.actions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class IPrintStream extends PrintStream {
    public IPrintStream() {
        super(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
    }

    public void write(int b) {
        super.write(b);
        Helpers.log(new String(ByteBuffer.allocate(1).array()));
    }

    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        Helpers.log(new String(buf));
    }
}
