package com.atb.hypermedia.api.config.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.ObjectUtils;

/**
 * Re-implemented the string to enum {@link ConverterFactory} from spring ({@line StringToEnumConverterFactory}) using
 * the {@link ObjectUtils#caseInsensitiveValueOf} method.
 */
public class StringToEnumCaseInsensitiveConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnum<T>(targetType);
    }

    private class StringToEnum<T extends Enum<?>> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source.length() == 0) {
                return null;
            }
            return ObjectUtils.caseInsensitiveValueOf(enumType.getEnumConstants(), source.trim());
        }
    }
}
