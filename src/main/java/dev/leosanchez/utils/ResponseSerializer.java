package dev.leosanchez.utils;

import java.io.IOException;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ResponseSerializer extends StdSerializer<Response> {

    public ResponseSerializer() {
        super(Response.class);
    }

    @Override
    public void serialize(Response value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
        // we write the status on the status field
        gen.writeNumberField("status", value.getStatus());
        // we call the serializer provider to delegate the responsability of serializing the internal entity
        provider.defaultSerializeField("entity", value.getEntity(), gen);
    }

    @Override
    public void serializeWithType(Response value, JsonGenerator gen, SerializerProvider provider,
      TypeSerializer typeSer) throws IOException {
        // We explicitly identify the type of the serialized object because Response is an abstract class
        // and the object received will be a specific implementation of it.
        WritableTypeId typeId;
        try {
          typeId = typeSer.typeId(value, Class.forName("javax.ws.rs.core.Response"), JsonToken.START_OBJECT);
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        // we open the object
        typeSer.writeTypePrefix(gen, typeId);
        // we call the serialize method
        serialize(value, gen, provider);
        // we close the object
        typeSer.writeTypeSuffix(gen, typeId);
    }
}