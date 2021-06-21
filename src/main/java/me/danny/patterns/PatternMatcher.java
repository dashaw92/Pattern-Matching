package me.danny.patterns;

import me.danny.patterns.test.DataImpl;
import me.danny.patterns.test.EnumImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public final class PatternMatcher<T> {

    public static <U> @NotNull PatternMatcher<U> with(@NotNull PatternSpec<? extends U> patternSpec, @NotNull PatternBlock<? super U> block) {
        if(patternSpec.patterns.size() != block.blocks.size()) {
            throw new IllegalArgumentException("patternSpec.size() != block.size()! Cannot create a PatternMatcher with invalid arguments.");
        }

        return new PatternMatcher<>(patternSpec, block);
    }

    private final PatternSpec<? extends T> spec;
    private final PatternBlock<? super T> block;

    private PatternMatcher(PatternSpec<? extends T> spec, PatternBlock<? super T> block) {
        this.spec = spec;
        this.block = block;
    }

    @SuppressWarnings("unchecked")
    public @NotNull Optional<Object> against(@NotNull T instance) {
        for(int patIdx = 0; patIdx < spec.patterns.size(); patIdx++) {
            var pattern = spec.patterns.get(patIdx);
            if(pattern.equals(instance.getClass())) {
                Function<T, Object> assocBlock = (Function<T, Object>) block.blocks.get(patIdx);
                return Optional.ofNullable(assocBlock.apply(instance));
            }
        }

        return Optional.empty();
    }

    public static void main(String[] args) {
        var dataTest = new DataImpl("cool string!");
        var enumImpl = EnumImpl.TWO;

        var pattern = PatternMatcher.with(PatternSpec.of(
                DataImpl.class,
                EnumImpl.class
                ),
                PatternBlock.block(
                        (Function<DataImpl, Object>) data -> data.value().length(),
                        (Function<EnumImpl, Object>) Enum::ordinal
                ));

        var dataOut = pattern.against(dataTest);
        var enumOut = pattern.against(enumImpl);

        System.out.println("Got this from dataOut! " + dataOut.get());
        System.out.println("And this from enumOut! " + enumOut.get());
    }
}
