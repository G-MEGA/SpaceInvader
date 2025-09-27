package serializer.awt_serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.awt.geom.AffineTransform;

public class AffineTransformSerializer extends Serializer<AffineTransform> {
    @Override
    public void write(Kryo kryo, Output output, AffineTransform transform) {
        // AffineTransform을 구성하는 6개의 double 값을 배열로 가져옵니다.
        double[] matrix = new double[6];
        transform.getMatrix(matrix);

        // 6개의 double 값을 순서대로 직렬화합니다.
        for (int i = 0; i < 6; i++) {
            output.writeDouble(matrix[i]);
        }
    }

    @Override
    public AffineTransform read(Kryo kryo, Input input, Class<AffineTransform> aClass) {
        // 직렬화된 6개의 double 값을 배열로 읽어옵니다.
        double[] matrix = new double[6];
        for (int i = 0; i < 6; i++) {
            matrix[i] = input.readDouble();
        }

        // 읽어온 값으로 AffineTransform 객체를 복원합니다.
        return new AffineTransform(matrix);
    }
}