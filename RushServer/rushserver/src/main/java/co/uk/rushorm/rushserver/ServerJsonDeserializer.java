package co.uk.rushorm.rushserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushMetaData;
import co.uk.rushorm.core.RushObjectDeserializer;
import co.uk.rushorm.core.implementation.ReflectionUtils;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerJsonDeserializer implements RushObjectDeserializer {

    @Override
    public <T extends Rush> List<T> deserialize(String string, String idName, String versionName, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Class<T> clazz, Callback callback) {

        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(string).getAsJsonObject();

            List<T> objects = new ArrayList<>();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String className = entry.getKey();
                Class<? extends Rush> objectClazz = classFromString(className, annotationCache);
                if(clazz != null) {
                    JsonArray jsonArray = entry.getValue().getAsJsonArray();
                    List<? extends Rush> objectList = deserializeJSONArray(jsonArray, idName, versionName, objectClazz, ArrayList.class, rushColumns, annotationCache, callback);
                    objects.addAll((List<? extends T>) objectList);
                }
            }
            
            return objects;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class classFromString(String name, Map<Class, AnnotationCache> annotationCache) {
        for (Map.Entry<Class, AnnotationCache> entry : annotationCache.entrySet()) {
            if(entry.getValue().getSerializationName().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private <T extends Rush> List<T> deserializeJSONArray(JsonArray jsonArray, String idName, String versionName, Class<T> clazz, Class<? extends List> listClazz, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if(jsonArray.size() < 1){
            return null;
        }

        List<T> objects;
        try {
            Constructor<? extends List> constructor = listClazz.getConstructor();
            objects = constructor.newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            objects = new ArrayList<>();
        }

        for (int i = 0; i < jsonArray.size(); i ++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            objects.add(deserializeJSONObject(jsonObject, idName, versionName, clazz, rushColumns, annotationCache, callback));
        }
        return objects;
    }

    private <T extends Rush> T deserializeJSONObject(JsonObject object, String idName, String versionName, Class<T> clazz, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, clazz);

        T rush = (T) clazz.getConstructor().newInstance();

        for (Field field : fields) {
            if (!annotationCache.get(clazz).getFieldToIgnore().contains(field.getName()) && object.has(field.getName())) {
                field.setAccessible(true);
                if (Rush.class.isAssignableFrom(field.getType())) {
                    JsonObject childJSONObject = object.getAsJsonObject(field.getName());
                    Rush child = deserializeJSONObject(childJSONObject, idName, versionName, (Class<? extends Rush>) field.getType(), rushColumns, annotationCache, callback);
                    field.set(rush, child);
                } else if (annotationCache.get(clazz).getListsClasses().containsKey(field.getName())) {
                    Class childClazz = annotationCache.get(clazz).getListsClasses().get(field.getName());
                    Class listClazz = annotationCache.get(clazz).getListsTypes().get(field.getName());
                    JsonArray jsonArray = object.getAsJsonArray(field.getName());
                    List<Rush> children = deserializeJSONArray(jsonArray, idName, versionName, childClazz, listClazz, rushColumns, annotationCache, callback);
                    field.set(rush, children);
                } else if (rushColumns.supportsField(field)) {
                    String value = object.getAsJsonPrimitive(field.getName()).getAsString();
                    rushColumns.setField(rush, field, value);
                }
            }
        }
        if(object.has(idName)) {
            String id = object.getAsJsonPrimitive(idName).getAsString();
            long version = 1;
            if(object.has(versionName)) {
                version = object.getAsJsonPrimitive(versionName).getAsLong();
            }
            RushMetaData rushMetaData = new RushMetaData(id, version);
            callback.addRush(rush, rushMetaData);
        }
        return rush;
    }
}
