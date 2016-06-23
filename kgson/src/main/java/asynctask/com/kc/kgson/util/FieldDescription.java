package asynctask.com.kc.kgson.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class FieldDescription {
    private Type[] types=null;
    private Type basicType=null;
    private Class<?> fieldClass=null;


    public FieldDescription(Type type){
        if (type instanceof ParameterizedType) { //泛型
            ParameterizedType tpe=(ParameterizedType)type;
            types = tpe.getActualTypeArguments();
            basicType= tpe.getRawType();
            if(types!=null&&types.length>0)
            {
                try {
                    fieldClass=(Class<?>)basicType;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else
        {
            try {
                fieldClass=(Class<?>)type;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    public Type[] getTypes() {
        return types;
    }

    public Type getBasicType() {
        return basicType;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }
}
