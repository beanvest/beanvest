package beanvest.lib.apprunner.wiring;

import java.io.ByteArrayOutputStream;

public record CliWriters(ByteArrayOutputStream out, ByteArrayOutputStream err) {
}
