package beanvest.lib.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GsonFactory {
    public static GsonBuilder builderWithProjectDefaults() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
                .serializeNulls();
    }

    public static Gson createWithProjectDefaults() {
        return builderWithProjectDefaults().create();
    }
}
