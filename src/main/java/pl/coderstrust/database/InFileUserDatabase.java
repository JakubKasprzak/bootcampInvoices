package pl.coderstrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-file")
public class InFileUserDatabase implements UserDatabase {

    private String filePath;
    private ObjectMapper mapper;
    private FileHelper fileHelper;
    private AtomicLong nextId;

    @Autowired
    public InFileUserDatabase(InFileDatabaseProperties inFileDatabaseProperties, ObjectMapper mapper, FileHelper fileHelper) throws IOException {
        this.filePath = inFileDatabaseProperties.getFilePath();
        this.mapper = mapper;
        this.fileHelper = fileHelper;
        initFile();
    }

    private void initFile() throws IOException {
        if (! fileHelper.exists(filePath)) {
            fileHelper.createFile(filePath);
        }
        nextId = new AtomicLong(getLastUserId());
    }

    private long getLastUserId() throws IOException {
        String lastLine = fileHelper.readLastLine(filePath);
        if (lastLine == null) {
            return 0;
        }
        User user = deserializeJsonToUser(lastLine);
        if (user == null) {
            return 0;
        }
        return user.getId();
    }

    private User deserializeJsonToUser(String json) {
        try {
            return mapper.readValue(json, User.class);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean userExistsByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("User email cannot be null.");
        }
        try {
            return getUsers()
                .stream()
                .anyMatch(user -> user.getId().equals(email));
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while checking if user exists in database");
        }
    }

    @Override
    public boolean userExistsById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null.");
        }
        try {
            return getUsers()
                .stream()
                .anyMatch(user -> user.getId().equals(id));
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while checking if user exists in database");
        }
    }

    @Override
    public Optional<User> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null.");
        }
        try {
            return getUsers().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
        } catch (IOException e) {
            throw new DatabaseOperationException(String.format("An error occurred while getting user with id: %d from database", id));
        }
    }

    @Override
    public Optional<User> getByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("User id cannot be null.");
        }
        try {
            return getUsers().stream()
                .filter(s -> s.getEmail().equals(email))
                .findFirst();
        } catch (IOException e) {
            throw new DatabaseOperationException(String.format("An error occurred while getting user with id: %d from database", email));
        }
    }

    @Override
    public Collection<User> getAll() throws DatabaseOperationException {
        try {
            return getUsers();
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while getting all users from database");
        }
    }

    @Override
    public User save(User user) throws DatabaseOperationException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        try {
            if (user.getId() == null || ! userExistsById(user.getId())) {
                return insertUser(user);
            }
            return updateUser(user);
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while saving user to database");
        }
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null.");
        }
        try {
            fileHelper.removeLine(filePath, getPositionInDatabase(id));
        } catch (IOException e) {
            throw new DatabaseOperationException(String.format("An error occurred while deleting user with id: %d from database", id));
        }
    }

    @Override
    public void deleteByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("User email cannot be null.");
        }
        try {
            fileHelper.removeLine(filePath, getPositionInDatabaseBasedOnUserEmail(email));
        } catch (IOException e) {
            throw new DatabaseOperationException(String.format("An error occurred while deleting user with email: %s from database", email));
        }
    }

    @Override
    public synchronized void deleteAll() throws DatabaseOperationException {
        try {
            fileHelper.clear(filePath);
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while deleting all users from database");
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return getUsers().size();
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred during getting number of users.");
        }
    }

    private int getPositionInDatabaseBasedOnUserEmail(String email) throws IOException, DatabaseOperationException {
        List<User> users = getUsers();
        Optional<User> user = users.stream()
            .filter(s -> s.getEmail().equals(email))
            .findFirst();
        if (user.isEmpty()) {
            throw new DatabaseOperationException(String.format("No user with email: %s", email));
        }
        return users.indexOf(user.get()) + 1;
    }

    private int getPositionInDatabase(Long id) throws IOException, DatabaseOperationException {
        List<User> users = getUsers();
        Optional<User> user = users.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst();
        if (user.isEmpty()) {
            throw new DatabaseOperationException(String.format("No user with id: %s", id));
        }
        return users.indexOf(user.get()) + 1;
    }

    private List<User> getUsers() throws IOException {
        return fileHelper.readLines(filePath).stream()
            .map(user -> deserializeJsonToUser(user))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private User insertUser(User user) throws IOException {
        Long id = nextId.incrementAndGet();
        User insertedUser = User.builder()
            .withUser(user)
            .withId(id)
            .build();
        fileHelper.writeLine(filePath, mapper.writeValueAsString(insertedUser));
        return insertedUser;
    }

    private User updateUser(User user) throws IOException, DatabaseOperationException {
        User updatedUser = User.builder()
            .withUser(user)
            .build();
        fileHelper.replaceLine(filePath, getPositionInDatabase(user.getId()), mapper.writeValueAsString(updatedUser));
        return updatedUser;
    }
}
