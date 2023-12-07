package edu.eci.arsw.paintit.repositories;

import edu.eci.arsw.paintit.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PaintItRepository {

    private final RedisTemplate<String, Game> redisTemplate;

    @Autowired
    public PaintItRepository(RedisTemplate<String, Game> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveGame(Game game) {
        redisTemplate.opsForValue().set(String.valueOf(game.getId()), game);
    }

    public Iterable<Game> findAll() {
        Set<String> keys = redisTemplate.keys("*");
        List<Game> games = new ArrayList<>();
        assert keys != null;
        for (String key : keys) {
            Game game = redisTemplate.opsForValue().get(key);
            if (game != null) {
                games.add(game);
            }
        }
        return games;
    }

    public Optional<Game> findById(int id) {
        Game game = redisTemplate.opsForValue().get(String.valueOf(id));
        return Optional.ofNullable(game);
    }

    public void deleteById(int id) {
        redisTemplate.opsForValue().getAndDelete(String.valueOf(id));
    }

    public void deleteAll() {
        Set<String> keys = redisTemplate.keys("*");
        assert keys != null;
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

}
