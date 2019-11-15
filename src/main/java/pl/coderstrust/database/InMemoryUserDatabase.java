package pl.coderstrust.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-memory")
public class InMemoryUserDatabase implements UserDatabase {

    private Map<Long, User> database;
    private AtomicLong nextId = new AtomicLong(0);

    public InMemoryUserDatabase(Map<Long, User> database) {
        if (database == null) {
            throw new IllegalArgumentException("Database is empty.");
        }
        this.database = database;
    }

    @Override
    public synchronized User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (user.getId() == null || !database.containsKey(user.getId())) {
            return insertUser(user);
        }
        return updateUser(user);
    }

    private User insertUser(User user) {
        Long id = nextId.incrementAndGet();
        User insertedUser = User.builder()
            .withUser(user)
            .withId(id)
            .build();
        database.put(id, insertedUser);
        return insertedUser;
    }

    private User updateUser(User user) {
        User updatedUser = User.builder()
            .withUser(user)
            .build();
        database.put(user.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null.");
        }
        if (!database.containsKey(id)) {
            throw new DatabaseOperationException(String.format("No user with id: %s", id));
        }
        database.remove(id);
    }

    @Override
    public void deleteByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("User email cannot be null.");
        }
       this.database=database.entrySet().stream().filter(s->s.getValue().getEmail().equals(email)).collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));
    }

    @Override
    public Optional<User> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null.");
        }
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public Optional<User> getByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }
        return database.values()
            .stream()
            .filter(user -> user.getEmail().equals(email))
            .findFirst();
    }

    @Override
    public Collection<User> getAll() {
        return database.values();
    }

    @Override
    public synchronized void deleteAll() {
        database.clear();
    }

    @Override
    public boolean userExistsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        return database.containsKey(id);
    }


    @Override
    public boolean userExistsByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        return database.entrySet().stream().anyMatch(s->s.getValue().getEmail().equals(email));
    }

    @Override
    public long count() {
        return database.size();
    }
}
