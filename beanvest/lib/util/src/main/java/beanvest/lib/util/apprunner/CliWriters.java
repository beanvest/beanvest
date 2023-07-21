package beanvest.lib.util.apprunner;

import java.io.ByteArrayOutputStream;

public record CliWriters(ByteArrayOutputStream out, ByteArrayOutputStream err) {
}
