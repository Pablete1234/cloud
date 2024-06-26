//
// MIT License
//
// Copyright (c) 2024 Incendo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package org.incendo.cloud.parser.aggregate;

import io.leangen.geantyref.TypeToken;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.component.TypedCommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.type.tuple.Triplet;

import static java.util.Objects.requireNonNull;

public final class AggregateParserTripletBuilder<C, U, V, Z, O> {

    /**
     * Creates the default mapper that simply returns a {@link Triplet} of the three input values.
     *
     * @param <C> the command sender type
     * @param <U> the first type
     * @param <V> the second type
     * @param <Z> the third type
     * @return a default mapper that returns a {@link Triplet} of the three input values
     */
    public static <C, U, V, Z> Mapper<C, U, V, Z, Triplet<U, V, Z>> defaultMapper() {
        return (ctx, u, v, z) -> ArgumentParseResult.successFuture(Triplet.of(u, v, z));
    }

    private final Mapper<C, U, V, Z, O> mapper;
    private final TypeToken<O> outType;
    private final TypedCommandComponent<C, U> first;
    private final TypedCommandComponent<C, V> second;
    private final TypedCommandComponent<C, Z> third;

    /**
     * Creates a new {@link AggregateParserTripletBuilder} with the given components and mapper.
     *
     * @param first   the first component
     * @param second  the second component
     * @param third   the third component
     * @param mapper  the mapper
     * @param outType the output type
     */
    public AggregateParserTripletBuilder(
            final TypedCommandComponent<C, U> first,
            final TypedCommandComponent<C, V> second,
            final TypedCommandComponent<C, Z> third,
            final Mapper<C, U, V, Z, O> mapper,
            final TypeToken<O> outType
    ) {
        this.mapper = mapper;
        this.outType = outType;
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Creates a new {@link AggregateParserTripletBuilder} with the given mapper.
     *
     * @param outType the new output type
     * @param mapper  the mapper
     * @param <O1>    the new output type
     * @return a new {@link AggregateParserTripletBuilder} with the given mapper
     */
    public <O1> AggregateParserTripletBuilder<C, U, V, Z, O1> withMapper(
            final @NonNull TypeToken<O1> outType,
            final @NonNull Mapper<C, U, V, Z, O1> mapper
    ) {
        return new AggregateParserTripletBuilder<>(this.first, this.second, this.third, mapper, outType);
    }

    /**
     * Creates a new {@link AggregateParserTripletBuilder} with the given mapper.
     *
     * @param outType the new output type
     * @param mapper  the mapper
     * @param <O1>    the new output type
     * @return a new {@link AggregateParserTripletBuilder} with the given mapper
     */
    public <O1> AggregateParserTripletBuilder<C, U, V, Z, O1> withDirectMapper(
            final @NonNull TypeToken<O1> outType,
            final Mapper.@NonNull DirectSuccessMapper<C, U, V, Z, O1> mapper
    ) {
        return this.withMapper(outType, mapper);
    }

    /**
     * Builds an {@link AggregateParser} from the current state of the builder.
     *
     * @return the built {@link AggregateParser}
     */
    public AggregateParser<C, O> build() {
        return new AggregateParserBuilder<>(Arrays.asList(this.first, this.second, this.third)).withMapper(
                this.outType,
                (commandContext, aggregateContext) -> {
                    final U firstResult = aggregateContext.get(this.first.name());
                    final V secondResult = aggregateContext.get(this.second.name());
                    final Z thirdResult = aggregateContext.get(this.third.name());
                    return this.mapper.map(commandContext, firstResult, secondResult, thirdResult);
                }
        ).build();
    }

    /**
     * Helper function to create a direct success mapper.
     *
     * @param mapper mapper
     * @param <C>    command sender type
     * @param <U>    first component type
     * @param <V>    second component type
     * @param <Z>    third component type
     * @param <O>    output type
     * @return mapper
     */
    public static <C, U, V, Z, O> @NonNull Mapper<C, U, V, Z, O> directMapper(
            final Mapper.@NonNull DirectSuccessMapper<C, U, V, Z, O> mapper) {
        return requireNonNull(mapper, "mapper");
    }

    public interface Mapper<C, U, V, Z, O> {

        /**
         * Maps the results of the child parsers to the output type.
         *
         * @param commandContext the command context
         * @param firstResult    the first result
         * @param secondResult   the second result
         * @param thirdResult    the third result
         * @return the mapped result
         */
        @NonNull CompletableFuture<ArgumentParseResult<O>> map(
                @NonNull CommandContext<C> commandContext,
                @NonNull U firstResult,
                @NonNull V secondResult,
                @NonNull Z thirdResult
        );

        interface DirectSuccessMapper<C, U, V, Z, O> extends Mapper<C, U, V, Z, O> {

            /**
             * Maps the results of the child parsers to the output type.
             *
             * @param commandContext the command context
             * @param firstResult    the first result
             * @param secondResult   the second result
             * @param thirdResult    the third result
             * @return the mapped result
             */
            @NonNull O mapSuccess(
                    @NonNull CommandContext<C> commandContext,
                    @NonNull U firstResult,
                    @NonNull V secondResult,
                    @NonNull Z thirdResult
            );

            @Override
            default @NonNull CompletableFuture<ArgumentParseResult<O>> map(
                    @NonNull CommandContext<C> commandContext,
                    @NonNull U firstResult,
                    @NonNull V secondResult,
                    @NonNull Z thirdResult
            ) {
                return ArgumentParseResult.successFuture(this.mapSuccess(commandContext, firstResult, secondResult, thirdResult));
            }
        }
    }
}
