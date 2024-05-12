package http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter writer, LocalDateTime value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else {
            writer.value(formatter.format(value));
        }
    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        return LocalDateTime.parse(reader.nextString(), formatter);
    }
}
