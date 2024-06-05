package dev.mmaksymko.gateway.configs.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class PrefixedStringRedisSerializer implements RedisSerializer<String> {

    private final String prefix;
    private final RedisSerializer<String> delegate;

    public PrefixedStringRedisSerializer(String prefix) {
        this.prefix = prefix;
        this.delegate = new StringRedisSerializer();
    }

    @Override
    public byte[] serialize(String string) throws SerializationException {
        return delegate.serialize(prefix + string);
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        String deserializedString = delegate.deserialize(bytes);
        if (deserializedString != null && deserializedString.startsWith(prefix)) {
            return deserializedString.substring(prefix.length());
        }
        return deserializedString;
    }
}

