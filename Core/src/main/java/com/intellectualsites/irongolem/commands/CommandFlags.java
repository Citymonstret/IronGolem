//
// IronGolem - A Minecraft block logging plugin
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.
//

package com.intellectualsites.irongolem.commands;

import com.google.common.base.Enums;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Handler for command flags (--key value)
 */
public class CommandFlags {

    private final Collection<Flag<?>> flags = new ArrayList<>();

    public void registerFlag(@NotNull final Flag<?> flag) {
        this.flags.add(flag);
    }

    public Map<String, Object> parseFlags(@NotNull final CommandSender sender, @NotNull final String[] args)
        throws IllegalArgumentException {
        if (args.length == 0) {
            return Collections.emptyMap();
        }
        final Map<String, Object> parsedFlags = new HashMap<>();
        for (int i = 0; i < args.length - 1; i += 2) {
            final String flag  = args[i];
            final String value = args[i + 1];
            final Flag<?> flagObject = this.getFlag(flag.toLowerCase().substring(2));
            if (flagObject == null) {
                throw new IllegalArgumentException(String.format("Unknown flag: %s", flag));
            }
            parsedFlags.put(flagObject.getMainAlias(), flagObject.parse(value));
        }
        return parsedFlags;
    }

    public List<String> completeFlags(@NotNull final CommandSender sender, @NotNull final String[] args) {
        if (args.length == 0) {
            return this.flags.stream().map(Flag::getMainAlias).map(flag -> "--" + flag)
                .collect(Collectors.toList());
        } else if (args.length % 2 == 1) {
            // Figure out what flags they've already entered
            final List<Flag<?>> completed = new LinkedList<>();
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].startsWith("--")) {
                    final Flag<?> completedFlag = this.getFlag(args[i].toLowerCase().replace("--", ""));
                    if (completedFlag != null) {
                        completed.add(completedFlag);
                    }
                }
            }
            return this.flags.stream()
                .filter(flag -> !completed.contains(flag))
                .map(Flag::getMainAlias)
                .map(flag -> "--" + flag)
                .filter(flag -> flag.toLowerCase().startsWith(args[args.length - 1]))
                .collect(Collectors.toList());
        } else {
            final String flag = args[args.length - 2];
            final Flag<?> flagObject = this.getFlag(flag.replace("--", ""));
            if (flagObject != null) {
                return flagObject.getSuggestions()
                    .stream()
                    .filter(suggestion -> suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Nullable public Flag<?> getFlag(@NotNull final String arg) {
        for (final Flag<?> flag : this.flags) {
            if (flag.accepts(arg)) {
                return flag;
            }
        }
        return null;
    }

    public abstract static class Flag<T> {

        private final String[] flagAliases;

        protected Flag(@NotNull final String[] flagAliases) {
            this.flagAliases = flagAliases;
        }

        public boolean accepts(@NotNull final String value) {
            for (final String alias : this.flagAliases) {
                if (alias.equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }

        public String getMainAlias() {
            return this.flagAliases[0];
        }

        public List<String> getSuggestions() {
            return Collections.emptyList();
        }

        public abstract T parse(@NotNull final String value) throws IllegalArgumentException;

        @Override public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Flag<?> flag = (Flag<?>) o;
            return Arrays.equals(flagAliases, flag.flagAliases);
        }

        @Override public int hashCode() {
            return Arrays.hashCode(flagAliases);
        }
    }

    public static class IntegerFlag extends Flag<Integer> {

        protected IntegerFlag(@NotNull final String[] flagAliases) {
            super(flagAliases);
        }

        @Override public Integer parse(@NotNull final String value) throws IllegalArgumentException {
            try {
                return Integer.parseInt(value);
            } catch (final Throwable ignored) {
                throw new IllegalArgumentException(String.format("%s is not an integer", value));
            }
        }

        public static IntegerFlag of(@NotNull final String ... aliases) {
            return new IntegerFlag(aliases);
        }

        @Override public List<String> getSuggestions() {
            return IntStream.range(1, 100).mapToObj(Integer::toString).collect(Collectors.toList());
        }
    }

    public static class EnumFlag<E extends Enum<E>> extends Flag<Collection<E>> {

        private final Class<E> clazz;
        private final List<String> permutations = new LinkedList<>();

        protected EnumFlag(@NotNull final Class<E> clazz, @NotNull final String[] flagAliases) {
            super(flagAliases);
            this.clazz = clazz;

            final Set<Set<E>> powerSet = Sets.powerSet(EnumSet.allOf(clazz));
            for (final Set<E> enums : powerSet) {
                if (enums.size() == 1) {
                    permutations.addAll(enums.stream().map(Enum::name).map(String::toLowerCase)
                        .collect(Collectors.toList()));
                } else if (enums.size() > 1) {
                    for (final List<E> list : Collections2.permutations(enums)) {
                        final Iterator<E> iterator = list.iterator();
                        final StringBuilder builder = new StringBuilder();
                        while (iterator.hasNext()) {
                            builder.append(iterator.next().name().toLowerCase());
                            if (iterator.hasNext()) {
                                builder.append(",");
                            }
                        }
                        permutations.add(builder.toString());
                    }
                }
            }
        }

        @Override public Collection<E> parse(@NotNull final String value) throws IllegalArgumentException {
            final String[] parts = value.split(",");
            final Collection<E> values = EnumSet.noneOf(clazz);
            for (final String part : parts) {
                final E parsedValue = Enums.getIfPresent(clazz, part.toUpperCase(Locale.ENGLISH))
                    .toJavaUtil().orElseThrow(() -> new IllegalArgumentException("Unknown value: " + part));
                if (values.contains(parsedValue)) {
                    continue;
                }
                values.add(parsedValue);
            }
            return values;
        }

        public static <E extends Enum<E>> EnumFlag<E> of(@NotNull final Class<E> clazz, final String ... aliases) {
            return new EnumFlag<>(clazz, aliases);
        }

        @Override public List<String> getSuggestions() {
            return Collections.unmodifiableList(this.permutations);
        }

    }

    public static class BooleanFlag extends Flag<Boolean> {

        protected BooleanFlag(@NotNull final String[] flagAliases) {
            super(flagAliases);
        }

        @Override public Boolean parse(@NotNull final String value) throws IllegalArgumentException {
            if (value.equalsIgnoreCase("true")) {
                return true;
            } else if (value.equalsIgnoreCase("false")) {
                return false;
            }
            throw new IllegalArgumentException("Value has to be either 'true' or 'false'");
        }

        public static BooleanFlag of(@NotNull final String ... aliases) {
            return new BooleanFlag(aliases);
        }

    }

}
