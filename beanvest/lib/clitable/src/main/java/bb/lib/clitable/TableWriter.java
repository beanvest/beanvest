package bb.lib.clitable;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableWriter {
    private int minColumnWidth = 0;

    public TableWriter() {
    }

    public <T> void writeTable(Writer writer, List<T> rows, List<Column<T>> columns) throws IOException {
        var maxColumnWidths = new ArrayList<Integer>(columns.size());
        columns.forEach(c -> maxColumnWidths.add(0));

        final ArrayList<List<String>> calculatedValues = calculateCellsContentsAndWidths(rows, columns, maxColumnWidths);

        var headerNeeded = columns.stream().anyMatch(c -> c.group().isPresent());
        var buf = new StringBuffer();
        if (headerNeeded) {
            Column<T> prevCol = null;
            for (var i = 0; i < columns.size(); i++) {
                var col = columns.get(i);
                if (prevCol != null && prevCol.group().isEmpty() && col.group().isEmpty()) {
                    buf.append("  ");
                }
                if (col.group().isEmpty()) {
                    buf.append(" ".repeat(maxColumnWidths.get(i)));
                } else if (prevCol == null || !prevCol.group().equals(col.group())) {
                    var groupName = col.group().get();
                    int length = maxColumnWidths.get(i);
                    for (var k = i + 1; k < columns.size(); k++) {
                        var nextColumn = columns.get(k);
                        if (nextColumn.group().equals(col.group())) {
                            length += 2 + maxColumnWidths.get(k);
                        }
                    }
                    if (prevCol.group().isEmpty()) {
                        buf.append(" ╷");
                    }
                    var groupTitle = groupName.length() > length ? groupName.substring(0, length) : groupName;
                    buf.append(" " + groupTitle + " ".repeat(length - groupTitle.length()) + " ╷");
                }
                prevCol = col;
            }
            writer.write(buf.toString().stripTrailing() + "\n");
        }

        writeHeaders(writer, columns, maxColumnWidths);
        writeRows(writer, columns, calculatedValues, maxColumnWidths);
    }

    public TableWriter setMinColumnWidth(int minColumnWidth) {
        this.minColumnWidth = minColumnWidth;

        return this;
    }

    private <T> ArrayList<List<String>> calculateCellsContentsAndWidths(List<T> rows, List<Column<T>> columns, ArrayList<Integer> maxColumnWidths) {
        var calculatedValues = new ArrayList<List<String>>();
        for (var i = 0; i < columns.size(); i++) {
            maxColumnWidths.set(i, Math.max(columns.get(i).name().length(), minColumnWidth));
        }
        for (T allocation1 : rows) {
            var row = columns.stream().map(column -> column.extractor().apply(allocation1)).toList();
            for (int i = 0; i < row.size(); i++) {
                var cellLength = row.get(i).length();
                if (cellLength > maxColumnWidths.get(i)) {
                    maxColumnWidths.set(i, cellLength);
                }
            }

            calculatedValues.add(row);
        }
        return calculatedValues;
    }

    private <T> void writeRows(final Writer writer,
                               final List<Column<T>> columns,
                               final ArrayList<List<String>> calculatedValues,
                               final ArrayList<Integer> maxColumnWidths) throws IOException {
        for (final List<String> row : calculatedValues) {
            var stringBuffer = new StringBuffer();
            for (int k = 0; k < row.size(); k++) {
                var column = columns.get(k);
                var width = maxColumnWidths.get(k);
                stringBuffer.append(String.format("%" + (column.padding() == ColumnPadding.LEFT ? "-" : "") + width + "s", row.get(k)));
                writeColDelimiter(stringBuffer, columns, k);
            }
            writer.write(stringBuffer.toString().stripTrailing() + "\n");
        }
    }

    private <T> void writeColDelimiter(StringBuffer buffer, List<Column<T>> columns, int i) {
        var column = columns.get(i);
        if (i < columns.size() - 1 || column.group().isPresent()) {
            var nextGroup = i + 1 < columns.size() ? columns.get(i + 1).group() : Optional.empty();
            if (!column.group().equals(nextGroup)) {
                buffer.append(" │ ");
            } else {
                buffer.append("  ");
            }
        }
    }

    private <T> void writeHeaders(Writer writer, List<Column<T>> columns, ArrayList<Integer> maxColumnWidth) throws IOException {
        var stringBuffer = new StringBuffer();
        for (int i = 0; i < columns.size(); i++) {
            Column<T> column = columns.get(i);
            var width = maxColumnWidth.get(i);
            stringBuffer.append(String.format("%-" + width + "s", column.name()));
            writeColDelimiter(stringBuffer, columns, i);

        }
        writer.write(stringBuffer.toString().stripTrailing());
        writer.write("\n");
    }
}
