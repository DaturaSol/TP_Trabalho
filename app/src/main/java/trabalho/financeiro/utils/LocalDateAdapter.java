package trabalho.financeiro.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    // Define a standard format for dates in your JSON file
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // e.g., "2024-05-21"

    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        // Convert the LocalDate object to a formatted string
        return new JsonPrimitive(formatter.format(date));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        // Convert the string from the JSON file back into a LocalDate object
        return LocalDate.parse(json.getAsString(), formatter);
    }
}
