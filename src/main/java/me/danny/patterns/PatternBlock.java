package me.danny.patterns;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PatternBlock<T> {

    @SafeVarargs
    public static <U> @NotNull PatternBlock<? extends U> block(@NotNull Function<? extends U, Object>... blocks) {
        if(blocks == null || blocks.length == 0) throw new IllegalArgumentException("blocks must not be null or empty");
        return new PatternBlock<>(Arrays.asList(blocks));
    }

    protected final List<Function<? extends T, Object>> blocks;

    private PatternBlock(@NotNull  List<Function<? extends T, Object>> blocks) {
        this.blocks = blocks;
    }
}
