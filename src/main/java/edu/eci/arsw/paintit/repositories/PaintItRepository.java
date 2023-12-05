package edu.eci.arsw.paintit.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PaintItRepository {
    private final RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    public PaintItRepository(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveGame(int gameCode) {
        redisTemplate.opsForValue().set(String.valueOf(gameCode), gameCode);
    }

    public Iterable<Integer> findAll() {
        Set<String> keys = redisTemplate.keys("*");
        List<Integer> gameCodes = new ArrayList<>();
        assert keys != null;
        for (String key : keys) {
            Integer gameCode = redisTemplate.opsForValue().get(key);
            if (gameCode != null) gameCodes.add(gameCode);
        }
        return gameCodes;
    }

    public Optional<Integer> findById(int gameCode) {
        Integer gameCodeFound = redisTemplate.opsForValue().get(String.valueOf(gameCode));
        return Optional.ofNullable(gameCodeFound);
    }

    public void deleteById(int gameCode) {
        redisTemplate.opsForValue().getAndDelete(String.valueOf(gameCode));
    }

    public void deleteAll() {
        Set<String> keys = redisTemplate.keys("*");
        assert keys != null;
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

}
