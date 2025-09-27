package serializer.awt_serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.awt.Font;

public class FontSerializer extends Serializer<Font> {
    @Override
    public void write(Kryo kryo, Output output, Font font) {
        // 폰트의 이름, 스타일, 크기를 직렬화합니다.
        output.writeString(font.getName());
        output.writeInt(font.getStyle(), true);
        output.writeInt(font.getSize(), true);
    }

    @Override
    public Font read(Kryo kryo, Input input, Class<Font> aClass) {
        // 직렬화된 이름, 스타일, 크기를 읽어옵니다.
        String name = input.readString();
        int style = input.readInt(true);
        int size = input.readInt(true);

        // 읽어온 정보로 Font 객체를 복원합니다.
        return new Font(name, style, size);
    }
}
