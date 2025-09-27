package serializer;

import com.esotericsoftware.kryo.Kryo;
import org.reflections.Reflections;
import serializer.awt_serializer.AffineTransformSerializer;
import serializer.awt_serializer.ColorSerializer;
import serializer.awt_serializer.FontSerializer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType; // 제네릭 타입 분석을 위해 import
import java.lang.reflect.Type;             // 제네릭 타입 분석을 위해 import
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class KryoRecursiveRegistrar {

    private final Kryo kryo;
    private final Reflections reflections;
    private final Set<Class<?>> registeredClasses = new HashSet<>();
    private final Queue<Class<?>> registrationQueue = new LinkedList<>();
    private int nextId;

    public KryoRecursiveRegistrar(Kryo kryo, Reflections reflections, int startingId) {
        this.kryo = kryo;
        this.reflections = reflections;
        this.nextId = startingId;
    }

    public void register(Class<?> rootClass) {
        addToQueue(rootClass);

        while (!registrationQueue.isEmpty()) {
            Class<?> currentClass = registrationQueue.poll();

            if (!currentClass.isInterface() && !Modifier.isAbstract(currentClass.getModifiers())) {
                kryo.register(currentClass, nextId);
                System.out.println("Registered: " + currentClass.getName() + " with ID: " + nextId);
                nextId++;
            }

            scanFields(currentClass);
            scanSubclasses(currentClass);
        }

        kryo.register(java.awt.Color.class, new ColorSerializer());
        kryo.register(AffineTransform.class, new AffineTransformSerializer());
        kryo.register(Font.class, new FontSerializer());
    }

    private void scanFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            // <<< [수정] getGenericType()을 사용하여 제네릭 정보까지 가져옴
            parseAndAddType(field.getGenericType());
        }
    }

    /**
     * [추가] Type 객체를 재귀적으로 분석하여 큐에 추가하는 헬퍼 메서드
     */
    private void parseAndAddType(Type type) {
        // 타입이 ParameterizedType인 경우 (e.g., List<UserData>)
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            // Raw Type을 먼저 큐에 추가 (e.g., List 자체)
            addToQueue((Class<?>) pType.getRawType());
            // 제네릭 타입 인자들을 순회하며 재귀적으로 분석 (e.g., UserData)
            for (Type argType : pType.getActualTypeArguments()) {
                parseAndAddType(argType);
            }
            // 타입이 일반 Class인 경우
        } else if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            // 배열이라면 내부 컴포넌트 타입을 큐에 추가 (e.g., UserData[] -> UserData)
            while (clazz.isArray()) {
                clazz = clazz.getComponentType();
            }
            addToQueue(clazz);
        }
        // (WildcardType 등 다른 복잡한 제네릭 타입은 필요 시 추가 확장 가능)
    }

    // (scanSubclasses, addToQueue 메서드는 이전과 동일)
    private void scanSubclasses(Class<?> clazz) {
        Set<Class<?>> subTypes = (Set<Class<?>>) reflections.getSubTypesOf(clazz);
        if (!subTypes.isEmpty()) {
            for (Class<?> subType : subTypes) {
                addToQueue(subType);
            }
        }
    }

    private void addToQueue(Class<?> clazz) {
        if (registeredClasses.contains(clazz) || clazz.isPrimitive()) {
            return;
        }
        String packageName = clazz.getPackage() == null ? "" : clazz.getPackage().getName();
        if (packageName.startsWith("java.") || packageName.startsWith("javax.")) {
            return;
        }
        registrationQueue.add(clazz);
        registeredClasses.add(clazz);
        System.out.println("  -> Added to queue: " + clazz.getName());
    }
}