package yuku.dsupport.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {

    /**
     * Entityクラスの protected static final TrackedData<Byte> FLAGS フィールドにアクセスします。
     * フィールド名が "FLAGS" であることを指定します。
     * static フィールドなので、メソッドも static にします。
     */
    @Accessor("FLAGS")
    static TrackedData<Byte> getFLAGS() {
        // このメソッドの中身はMixinによって自動生成されるため、実際には実行されません。
        // コンパイルを通すために例外をスローするか、nullを返すように記述します。
        throw new AssertionError();
    }
}
