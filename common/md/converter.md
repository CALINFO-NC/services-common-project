# Description

 Le *converter* est un mécanisme permettant de convertir un objet en un autre.

# Usage

 * Création d'un premier *converter* permettant de convertir une classe *A* en une classe *B* et inversement

```java
import com.calinfo.api.common.converter.InstanceConverter;
import org.springframework.stereotype.Component;
import com.calinfo.api.common.converter.ContextConverter;

@Component
public class AtoBConverter implements InstanceConverter {


    @Override
    public <T> T convert(Object source, T dest, ContextConverter contextConverter) {

        if (source instanceof A) {
            return (T)convertAToB((A) source, (B) dest);
        }
        else{
            return (T)convertBToA((B) source, (A) dest);
        }
    }

    @Override
    public boolean accept(Class<?> source, Class<?> dest) {

        boolean aToB = A.class.isAssignableFrom(source) && B.class.isAssignableFrom(dest);
        boolean bToA = B.class.isAssignableFrom(source) && A.class.isAssignableFrom(dest);

        return aToB || bToA;
    }

    private B convertAToB(A a, B b){

        return ...;
    }

    private A convertBToA(B b, A a){

        return ...;
    }
}
```

 * Création d'un second *converter* permettant de convertir un *Integer* en *Long* et inversement.

```java
import com.calinfo.api.common.converter.ClassConverter;
import org.springframework.stereotype.Component;
import com.calinfo.api.common.converter.ContextConverter;

@Component
public class InttoLongConverter implements ClassConverter {


    @Override
    public <T> T convert(Object source, Class<T> dest, ContextConverter contextConverter) {

        if (source instanceof Long){
            return (T)Integer.valueOf(((Long)source).intValue());
        }
        else{
            return (T)Long.valueOf(((Integer)source).intValue());
        }

    }

    @Override
    public boolean accept(Class<?> source, Class<?> dest) {

        boolean intToLong = Integer.class.isAssignableFrom(source) && Long.class.isAssignableFrom(dest);
        boolean longToInt = Long.class.isAssignableFrom(source) && Integer.class.isAssignableFrom(dest);

        return intToLong || longToInt;
    }
}
```

 * Création d'un registre

```java
import com.calinfo.api.common.converter.AbstractConvertManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ConvertManager extends AbstractConvertManager {

    @Autowired
    private AtoBConverter atoBConverter;

    @Autowired
    private InttoLongConverter inttoLongConverter;

    @PostConstruct
    @Override
    public void postConstruct() {
        addConverter(atoBConverter);
        addConverter(inttoLongConverter);
    }
}
```

 * Comment utiliser le *connverter*

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.calinfo.api.common.converter.ContextConverter;

@Component
public class Example {

   @Autowired
   private ConvertManager cm;

   public void callWithoutContext() {

       Long myLong = cm.convert(1, Long.class);
       Integer myInteger = cm.convert(1l, Integer.class);
       A myA1 = cm.convert(new B(), A.class);
       A myA2 = cm.convert(new B(), new A());
       B myB1 = cm.convert(new A(), B.class);
       B myB2 = cm.convert(new A(), new B());
   }

   // Ou encore

   public void callWithContext() {

        ContextConverter context = ...

       Long myLong = cm.convert(1, Long.class);
       Integer myInteger = cm.convert(1l, Integer.class);
       A myA1 = cm.convert(new B(), A.class, context);
       A myA2 = cm.convert(new B(), new A(), context);
       B myB1 = cm.convert(new A(), B.class, context);
       B myB2 = cm.convert(new A(), new B(), context);

   }
}
```
