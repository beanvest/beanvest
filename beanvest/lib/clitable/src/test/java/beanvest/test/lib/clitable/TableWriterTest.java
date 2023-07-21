package beanvest.test.lib.clitable;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.lib.clitable.TableWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableWriterTest {

    public static final String ADDRESS = "address";

    @Test
    void writingTable() throws IOException {
        var people = List.of(
                new Person("John", LocalDate.parse("1988-01-01"), 22, 2),
                new Person("Simon", LocalDate.parse("1987-04-29"), 32, 2)
        );
        var id = new AtomicInteger();
        var columns = List.of(
                new Column<Person>("id", ColumnPadding.RIGHT, (person) -> String.valueOf(id.incrementAndGet())),
                new Column<Person>("name", ColumnPadding.LEFT, (person) -> person.name),
                new Column<Person>("birthYear", ColumnPadding.RIGHT, (person) -> String.valueOf(person.birthDate.getYear())),
                new Column<Person>("room", ColumnPadding.RIGHT, (person) -> String.valueOf(person.roomNo))
        );
        var writer = new StringWriter();
        new TableWriter().writeTable(writer, people, columns);

        assertEquals("""
                id  name   birthYear  room
                 1  John        1988    22
                 2  Simon       1987    32
                 """, writer.toString());
    }


    @Test
    void writingTableWithGroupedColumns() throws IOException {
        var people = List.of(
                new Person("John", LocalDate.parse("1988-01-01"), 22, 1),
                new Person("Simon", LocalDate.parse("1987-04-29"), 32, 2)
        );
        var id = new AtomicInteger();
        var columns = List.of(
                new Column<Person>("id", ColumnPadding.RIGHT, (person) -> String.valueOf(id.incrementAndGet())),
                new Column<Person>("name", ColumnPadding.LEFT, (person) -> person.name),
                new Column<Person>("birth", "year", ColumnPadding.RIGHT, (person) -> String.valueOf(person.birthDate.getYear())),
                new Column<Person>("birth", "month", ColumnPadding.RIGHT, (person) -> String.valueOf(person.birthDate.getMonth())),
                new Column<Person>(ADDRESS, "room", ColumnPadding.RIGHT, (person) -> String.valueOf(person.roomNo)),
                new Column<Person>(ADDRESS, "floor", ColumnPadding.RIGHT, (person) -> String.valueOf(person.floor))
        );
        var writer = new StringWriter();
        new TableWriter().writeTable(writer, people, columns);

        assertEquals("""
                          ╷ birth         ╷ address     ╷
                id  name  │ year  month   │ room  floor │
                 1  John  │ 1988  JANUARY │   22      1 │
                 2  Simon │ 1987    APRIL │   32      2 │
                 """, writer.toString());
    }

    @Test
    void writingTableWithLongerGroupNameThanColumn() throws IOException {
        var people = List.of(
                new Person("John", LocalDate.parse("1988-01-01"), 22, 1),
                new Person("Simon", LocalDate.parse("1987-04-29"), 32, 2)
        );
        var id = new AtomicInteger();
        var columns = List.of(
                new Column<Person>("id", ColumnPadding.RIGHT, (person) -> String.valueOf(id.incrementAndGet())),
                new Column<Person>("PERSONAL_DETAILS", "name", ColumnPadding.LEFT, (person) -> person.name)
        );
        var writer = new StringWriter();
        new TableWriter().writeTable(writer, people, columns);

        assertEquals("""
                   ╷ PERSO ╷
                id │ name  │
                 1 │ John  │
                 2 │ Simon │
                """, writer.toString());
    }

    public record Person(String name, LocalDate birthDate, int roomNo, int floor) {
    }
}

