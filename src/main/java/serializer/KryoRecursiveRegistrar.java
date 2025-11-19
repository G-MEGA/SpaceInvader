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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    // [리팩토링 1] 메인 흐름을 관리자 역할로 변경 (복잡도: 1)
    public void register(Class<?> rootClass) {
        addToQueue(rootClass);
        processQueue();
        registerCustomSerializers();
    }

    // [추출] 큐 처리 루프 분리 (복잡도: 2 - while loop 1개)
    private void processQueue() {
        while (!registrationQueue.isEmpty()) {
            Class<?> currentClass = registrationQueue.poll();

            tryRegisterCurrentClass(currentClass);

            scanFields(currentClass);
            scanSubclasses(currentClass);
        }
    }

    // [추출] 실제 등록 로직 분리 (복잡도: 2 - if 1개)
    private void tryRegisterCurrentClass(Class<?> currentClass) {
        if (isRegisterable(currentClass)) {
            kryo.register(currentClass, nextId);
            System.out.println("Registered: " + currentClass.getName() + " with ID: " + nextId);
            nextId++;
        }
    }

    // [추출] 등록 가능 여부 판단 로직 (복잡도: 3 - boolean logic)
    private boolean isRegisterable(Class<?> currentClass) {
        return !currentClass.isInterface() && !Modifier.isAbstract(currentClass.getModifiers());
    }

    // [추출] 커스텀 시리얼라이저 등록 분리 (복잡도: 1)
    private void registerCustomSerializers() {
        kryo.register(java.awt.Color.class, new ColorSerializer());
        kryo.register(AffineTransform.class, new AffineTransformSerializer());
        kryo.register(Font.class, new FontSerializer());
    }

    // [리팩토링 2] 필드 스캔 (복잡도: 2 - for loop)
    // Transient 체크를 메서드로 빼면 더 줄일 수 있지만, 이 정도는 허용 범위입니다.
    private void scanFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            parseAndAddType(field.getGenericType());
        }
    }

    // [리팩토링 3] 타입 분석 분기 로직 단순화 (복잡도: 3 - if/else if)
    private void parseAndAddType(Type type) {
        if (type instanceof ParameterizedType) {
            processParameterizedType((ParameterizedType) type);
        } else if (type instanceof Class) {
            processClassType((Class<?>) type);
        }
    }

    // [추출] 제네릭 타입 처리 (복잡도: 2 - for loop)
    private void processParameterizedType(ParameterizedType pType) {
        addToQueue((Class<?>) pType.getRawType());
        for (Type argType : pType.getActualTypeArguments()) {
            parseAndAddType(argType);
        }
    }

    // [추출] 일반 클래스/배열 처리 (복잡도: 2 - while loop)
    private void processClassType(Class<?> clazz) {
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        addToQueue(clazz);
    }

    private void scanSubclasses(Class<?> clazz) {
        Set<? extends Class<?>> subTypes = reflections.getSubTypesOf(clazz);
        if (!subTypes.isEmpty()) {
            for (Class<?> subType : subTypes) {
                addToQueue(subType);
            }
        }
    }

    // [리팩토링 4] 큐 추가 로직 단순화 (복잡도: 2 - if check)
    private void addToQueue(Class<?> clazz) {
        if (shouldIgnore(clazz)) {
            return;
        }

        registrationQueue.add(clazz);
        registeredClasses.add(clazz);
        System.out.println("  -> Added to queue: " + clazz.getName());
    }

    // [추출] 무시 조건 판단 로직 (복잡도: 3~4 -> 논리 연산자 || 로 묶임)
    // 복잡해 보이지만 boolean 식 하나로 처리되면 복잡도가 낮게 측정됩니다.
    private boolean shouldIgnore(Class<?> clazz) {
        if (registeredClasses.contains(clazz) || clazz.isPrimitive()) {
            return true;
        }
        return isSystemPackage(clazz);
    }

    // [추출] 시스템 패키지 확인 (복잡도: 2 - null check + startsWith)
    private boolean isSystemPackage(Class<?> clazz) {
        Package pkg = clazz.getPackage();
        if (pkg == null) return false; // default package

        String packageName = pkg.getName();
        return packageName.startsWith("java.") || packageName.startsWith("javax.");
    }
}