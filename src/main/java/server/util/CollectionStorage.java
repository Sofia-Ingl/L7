package server.util;

import server.Server;
import shared.data.Movie;
import shared.serializable.Pair;
import shared.serializable.User;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс-обертка для коллекции, предназначенный для хранения коллекции и вспомогательной информации.
 * Предоставляет методы доступа и обработки, используется командами.
 */
public class CollectionStorage {

    private final DatabaseCollectionHandler databaseCollectionHandler;
    private LinkedHashSet<Movie> collection = null;

    private final Type collectionType = LinkedHashSet.class;
    private final Type contentType = Movie.class;

    private LocalDateTime sortedCollectionUpdateTime = null;
    private ArrayList<Movie> sortedCollection = null;

    private LocalDateTime initTime = null;
    private LocalDateTime updateTime = null;
    private LocalDateTime lastAccessTime = null;

    private Movie maxMovie = null;

    public CollectionStorage(DatabaseCollectionHandler databaseCollectionHandler) {
        this.databaseCollectionHandler = databaseCollectionHandler;
    }

    public void loadCollectionFromDatabase() {
        try {
            collection = databaseCollectionHandler.loadCollectionFromDatabase();
            detectMaxMovie();
            initTime = LocalDateTime.now();
            updateTime = initTime;
            lastAccessTime = updateTime;
        } catch (SQLException e) {
            Server.logger.error("Коллекция не была успешно загружена в память...");
            Server.logger.error("Осуществляется выход из приложения...");
            System.exit(1);
        }
    }

    public void addMovie(Movie movie) {
        collection.add(movie);
        updateTime = LocalDateTime.now();
        lastAccessTime = updateTime;
        if (maxMovie == null || maxMovie.compareTo(movie) < 0) {
            maxMovie = movie;
        }
    }

    public void deleteElementsByUser(User user) {
        String username = user.getLogin();
        collection = collection.stream().filter(x -> !x.getOwner().getLogin().equals(username)).collect(Collectors.toCollection(LinkedHashSet::new));
        if (maxMovie.getOwner().getLogin().equals(username)) {
            detectMaxMovie();
        }
        updateTime = LocalDateTime.now();
        lastAccessTime = updateTime;
    }

    public void detectMaxMovie() {
        maxMovie = collection.stream().max(Comparator.naturalOrder()).orElse(null);
    }


    public String returnGreaterThanGoldenPalms(long goldenPalms) {

        lastAccessTime = LocalDateTime.now();
        StringBuilder builder = new StringBuilder();
        String heading = "Элементы, значение поля goldenPalmsCount у которых больше заданного\n";
        builder.append(heading);
        collection.stream()
                .filter(movie -> movie.getGoldenPalmCount() > goldenPalms)
                .forEach(movie -> builder.append(movie).append("\n"));
        if (builder.toString().equals(heading)) {
            return "В коллекции не было элементов, удовлетворяющих условию";
        }
        return builder.toString();
    }


    public Pair<String, ArrayList<Movie>> getSortedCollection() {
        if (sortedCollection != null && sortedCollectionUpdateTime.isAfter(updateTime)) {
            return new Pair<>("Коллекция не обновлялась со времен последней сортировки", sortedCollection);
        } else {
            ArrayList<Movie> sortedCollection = new ArrayList<>(collection);
            lastAccessTime = LocalDateTime.now();
            sortedCollection.sort(Comparator.naturalOrder());
            this.sortedCollection = sortedCollection;
            sortedCollectionUpdateTime = LocalDateTime.now();
        }
        return new Pair<>("Коллекция обновилась со времен последней сортировки!", sortedCollection);
    }

    public boolean removeByScreenwriter(String screenwriterName, User user) {

        String screenwriter = screenwriterName.trim().toLowerCase();
        int collectionSize = collection.size();

        collection = collection
                .stream()
                .filter(m -> !(m.getScreenwriter().getName().trim().toLowerCase().equals(screenwriter) && m.getOwner().getLogin().equals(user.getLogin())))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        lastAccessTime = LocalDateTime.now();
        if (collectionSize > collection.size()) {
            detectMaxMovie();
            updateTime = lastAccessTime;
            return true;
        }

        return false;
    }

    public boolean deleteElementForId(int id, User user) {

        int collectionSize = collection.size();

        collection = collection
                .stream()
                .filter(movie -> !(movie.getId() == id && movie.getOwner().getLogin().equals(user.getLogin())))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        lastAccessTime = LocalDateTime.now();
        if (collectionSize > collection.size()) {
            detectMaxMovie();
            updateTime = lastAccessTime;
            return true;
        }

        return false;

    }

    public boolean removeGreater(Movie movie, User user) {
        if (maxMovie.compareTo(movie) > 0) {
            collection = collection.stream()
                    .filter(m -> !(m.compareTo(movie) > 0 && m.getOwner().getLogin().equals(user.getLogin())))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            detectMaxMovie();
            updateTime = LocalDateTime.now();
            lastAccessTime = updateTime;
            return true;
        }
        return false;
    }

    public Movie getByUserAndId(int id, User user) {
        lastAccessTime = LocalDateTime.now();
        updateTime = lastAccessTime;
        return collection.stream()
                .filter(m -> (m.getId() == id && m.getOwner().getLogin().equals(user.getLogin())))
                .findFirst()
                .orElse(null);

    }

    public LinkedHashSet<Movie> getCollection() {
        lastAccessTime = LocalDateTime.now();
        return collection;
    }

    public Movie getMaxMovie() {
        return maxMovie;
    }

    public LocalDateTime getInitTime() {
        return initTime;
    }

    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Type[] getTypes() {
        return new Type[] {collectionType, contentType};
    }


}
