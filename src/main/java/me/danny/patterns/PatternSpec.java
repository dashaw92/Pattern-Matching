package me.danny.patterns;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternSpec<T> {

    @SafeVarargs
    public static <U> @NotNull  PatternSpec<U> of(@NotNull Class<? extends U>... patternClasses) {
        if(patternClasses == null || patternClasses.length == 0) throw new IllegalArgumentException("patternClasses must not be null or empty");
        return new PatternSpec<>(Arrays.asList(patternClasses));
    }

    protected final List<Class<? extends T>> patterns;

    private PatternSpec(@NotNull List<Class<? extends T>> patterns) {
        this.patterns = patterns;
    }
}
