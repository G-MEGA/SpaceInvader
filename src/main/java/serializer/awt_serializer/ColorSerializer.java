package serializer.awt_serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.awt.Color;

public class ColorSerializer extends Serializer<Color> {
    @Override
    public void write(Kryo kryo, Output output, Color color) {
        // Color 객체의 RGBA 값을 int 하나로 직렬화합니다.
        output.writeInt(color.getRGB(), true);
    }

    @Override
    public Color read(Kryo kryo, Input input, Class<Color> aClass) {
        // int 값을 읽어서 다시 Color 객체로 복원합니다.
        // true는 알파 채널을 포함하여 복원하라는 의미입니다.
        return new Color(input.readInt(true), true);
    }
}