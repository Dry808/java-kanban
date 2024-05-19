package http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration value) throws IOException {
        if (value == null) {
            jsonWriter.nullValue();
        } else  {
            jsonWriter.value(value.toMinutes());
        }
    }

    @Override
    public Duration read(JsonReader read) throws IOException {
        return Duration.ofMinutes(read.nextLong());
    }
}

