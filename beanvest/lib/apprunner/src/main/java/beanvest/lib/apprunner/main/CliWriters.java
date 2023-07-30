package beanvest.lib.apprunner.main;

import java.io.ByteArrayOutputStream;

public record CliWriters(ByteArrayOutputStream out, ByteArrayOutputStream err) {
}
