package com.cmall.groupcenter.baidupush.core.filter;

import java.lang.reflect.Field;
import java.util.Map;

import com.cmall.groupcenter.baidupush.core.annotation.HttpPathKeyName;
import com.cmall.groupcenter.baidupush.core.annotation.R;

public class HttpPathKeyNameFilter implements IFieldFilter {

    
    public void validate(Field field, Object req) throws Exception {

        if (field.isAnnotationPresent(HttpPathKeyName.class)) {
            Object obj = field.get(req);
            if (obj == null) {
                HttpPathKeyName annotation = field
                        .getAnnotation(HttpPathKeyName.class);
                if (annotation.param() == R.REQUIRE) {
                    throw new Exception(field.getName()
                            + " is null, default require");
                }
            }
        }

    }

    
    public void mapping(Field field, Object obj, Map<String, String> map) {

    }

}
