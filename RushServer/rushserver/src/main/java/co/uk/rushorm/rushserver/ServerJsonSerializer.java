package co.uk.rushorm.rushserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushMetaData;
import co.uk.rushorm.core.RushObjectSerializer;
import co.uk.rushorm.core.RushStringSanitizer;
import co.uk.rushorm.core.implementation.ReflectionUtils;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerJsonSerializer implements RushObjectSerializer {
    private static final RushStringSanitizer rushStringSanitizer = new RushStringSanitizer() {
        @Override
        public String sanitize(String string) {
            return string;
        }
    };

    @Override
    public String serialize(List<? extends Rush> objects, String idName, String versionName, RushColumns rushColumns, Map<Class<? extends Rush>, AnnotationCache> annotationCache, Callback callback) {

        Map<Class, JsonArray> arraysMap = new HashMap<>();

        for(Rush rush : objects) {
            try {
                if(!arraysMap.containsKey(rush.getClass())) {
                    arraysMap.put(rush.getClass(), new JsonArray());
                }
                arraysMap.get(rush.getClass()).add(serializeToJSONObject(rush, idName, versionName, rushColumns, annotationCache, rushStringSanitizer, callback));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<Class, JsonArray> entry : arraysMap.entrySet())
        {
            String name = annotationCache.get(entry.getKey()).getSerializationName();
            jsonObject.add(name, entry.getValue());
        }

        return jsonObject.toString();
    }

    private JsonArray serializeToJSONArray(List<? extends Rush> objects, String idName, String versionName, RushColumns rushColumns, Map<Class<? extends Rush>, AnnotationCache> annotationCache, RushStringSanitizer rushStringSanitizer, Callback callback) {

        JsonArray jsonArray = new JsonArray();
        if(objects != null) {
            for (Rush rush : objects) {
                try {
                    jsonArray.add(serializeToJSONObject(rush, idName, versionName, rushColumns, annotationCache, rushStringSanitizer, callback));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;
    }

    private JsonObject serializeToJSONObject(Rush rush, String idName, String versionName, RushColumns rushColumns, Map<Class<? extends Rush>, AnnotationCache> annotationCache, RushStringSanitizer rushStringSanitizer, Callback callback) throws IllegalAccessException {

        if(rush == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        RushMetaData rushMetaData = callback.getMetaData(rush);
        if (rushMetaData != null) {
            jsonObject.addProperty(idName, rushMetaData.getId());
            jsonObject.addProperty(versionName, rushMetaData.getVersion());
        }

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, rush.getClass());

        for (Field field : fields) {
            if (!annotationCache.get(rush.getClass()).getFieldToIgnore().contains(field.getName())) {
                field.setAccessible(true);
                if (Rush.class.isAssignableFrom(field.getType())) {
                    JsonObject object = serializeToJSONObject((Rush) field.get(rush), idName, versionName, rushColumns, annotationCache, rushStringSanitizer, callback);
                    jsonObject.add(field.getName(), object);
                } else if (annotationCache.get(rush.getClass()).getListsClasses().containsKey(field.getName())) {
                    JsonArray array = serializeToJSONArray((List<Rush>) field.get(rush), idName, versionName, rushColumns, annotationCache, rushStringSanitizer, callback);
                    jsonObject.add(field.getName(), array);
                } else if (rushColumns.supportsField(field)) {
                    String value = rushColumns.valueFromField(rush, field, rushStringSanitizer);
                    jsonObject.addProperty(field.getName(), value);
                }
            }
        }
        return jsonObject;
    }
}
