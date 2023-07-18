package bb.lib.util.apprunner;

import java.io.ByteArrayOutputStream;

public record CliWriters(ByteArrayOutputStream out, ByteArrayOutputStream err) {
}
