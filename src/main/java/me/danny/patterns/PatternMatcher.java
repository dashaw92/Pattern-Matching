package me.danny.patterns;

import me.danny.patterns.test.DataImpl;
import me.danny.patterns.test.EnumImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static me.danny.patterns.PatternMatcher.PatternBlock.run;
import static me.danny.patterns.PatternMatcher.PatternSpec.specOf;

/**
 * Takes a {@link PatternSpec} and {@link PatternBlock} to be used to match against the parameter.
 * The recommended way of consuming this API is to statically import the following methods:
 * {@link PatternSpec#specOf(Class[])} {@link PatternBlock#run(Function[])} {@link PatternMatcher#match(PatternSpec, PatternBlock)}
 * @see PatternMatcher#match(PatternSpec, PatternBlock)
 * @see PatternMatcher#with(T) 
 */
public final class PatternMatcher<T> {

    /**
     * @param patternSpec The patterns this matcher accepts
     * @param block Associated blocks of code to be run by class type
     * @param <U> The base class of the provided {@link PatternSpec}
     * @return The new {@link PatternMatcher} constructed from the arguments
     */
    public static <U> @NotNull PatternMatcher<U> match(@NotNull PatternSpec<? extends U> patternSpec, @NotNull PatternBlock<? super U> block) {
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

    /**
     * Match against the provided {@code instance}
     * @param instance Object to match against
     * @return The value the block of code associated with the type of the instance, or {@link Optional#empty()}
     */
    @SuppressWarnings("unchecked")
    public @NotNull Optional<?> with(@NotNull T instance) {
        for(int patIdx = 0; patIdx < spec.patterns.size(); patIdx++) {
            var pattern = spec.patterns.get(patIdx);
            if(pattern.equals(instance.getClass())) {
                Function<? super T, ?> assocBlock = (Function<? super T, ?>) block.blocks.get(patIdx);
                return Optional.ofNullable(assocBlock.apply(instance));
            }
        }

        return Optional.empty();
    }

    /**
     * The mold for the pattern matcher.
     * @param <T> Base class of all provided Classes
     */
    public static class PatternSpec<T> {

        /**
         * Factory method to create a PatternSpec.
         * @param patternClasses List of classes
         * @param <U> Base class of all provided classes
         * @return A new PatternSpec instance
         */
        @SafeVarargs
        public static <U> @NotNull  PatternSpec<U> specOf(@NotNull Class<? extends U>... patternClasses) {
            if(patternClasses == null || patternClasses.length == 0) throw new IllegalArgumentException("patternClasses must not be null or empty");
            return new PatternSpec<>(Arrays.asList(patternClasses));
        }

        protected final List<Class<? extends T>> patterns;

        private PatternSpec(@NotNull List<Class<? extends T>> patterns) {
            this.patterns = patterns;
        }
    }

    /**
     * List of lambdas to run per associated type
     * @param <T> Base class of objects passed to the lambdas
     */
    public static final class PatternBlock<T> {

        /**
         * Factory method to create a PatternBlock.
         * @param blocks List of lambdas
         * @param <U> Base class of all provided classes
         * @return A new PatternBlock instance
         */
        @SafeVarargs
        public static <U> @NotNull PatternBlock<? extends U> run(@NotNull Function<? extends U, ?>... blocks) {
            if(blocks == null || blocks.length == 0) throw new IllegalArgumentException("blocks must not be null or empty");
            return new PatternBlock<>(Arrays.asList(blocks));
        }

        protected final List<Function<? extends T, ?>> blocks;

        private PatternBlock(@NotNull  List<Function<? extends T, ?>> blocks) {
            this.blocks = blocks;
        }
    }


    public static void main(String[] args) {
        var dataTest = new DataImpl("cool string!");
        var enumImpl = EnumImpl.TWO;

        var pattern = match(
                specOf(
                        DataImpl.class,
                        EnumImpl.class
                ),
                run(
                        (DataImpl data) -> data.value().length(),
                        (Function<EnumImpl, Integer>) Enum::ordinal
                )
        );

        var dataOut = pattern.with(dataTest);
        var enumOut = pattern.with(enumImpl);

        System.out.println("Got this from dataOut! " + dataOut.get());
        System.out.println("And this from enumOut! " + enumOut.get());
    }
}
