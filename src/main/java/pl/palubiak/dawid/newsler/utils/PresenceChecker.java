package pl.palubiak.dawid.newsler.utils;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PresenceChecker<S extends DBModel> {
    /**
     * Checks if given object is present in given repository if not saves it. <br>
     * Method is used while saving new object to check if object is present in repository.
     *
     * @param repository repository that extends JpaRepository and stores given type of object S
     * @param object     object that is checked if it is present in repository
     * @throws IllegalArgumentException if object has wrong ID
     * @see JpaRepository
     */
    public Optional<S> checkIfPresent(JpaRepository<S, Long> repository, S object) throws IllegalArgumentException {
        if (object == null)
            throw new NullPointerException("Object is null");

        if (object.getId() == 0)
            return Optional.of(repository.save(object));

        Optional<S> optionalObject = repository.findById(object.getId());
        if (optionalObject.isEmpty())
            throw new IllegalArgumentException("Object " + object + "has wrong ID\nIt cannot be identified");


        return Optional.empty();
    }

    /**
     * Checks if given list of objects is present in given repository if not saves it. <br>
     * Method is used while saving new object to check if some objects are present in repository.
     *
     * @param repository repository that extends JpaRepository and stores given type of object S
     * @param objects    list of objects that is checked if they are present in repository
     * @param clazz      class of objects in list - helpful for exception message
     * @throws IllegalArgumentException if object has wrong ID
     */
    public boolean checkIfPresents(JpaRepository<S, Long> repository, List<S> objects, Class<S> clazz) throws IllegalArgumentException {
        if (objects == null || objects.isEmpty())
            throw new NullPointerException("List of " + clazz + " is blank or empty");

        for (S object : objects) {
            if (object == null)
                throw new NullPointerException("Object is null");

            if (object.getId() == 0)
                repository.save(object);

            Optional<S> optionalObject = repository.findById(object.getId());
            if (optionalObject.isEmpty())
                throw new IllegalArgumentException("Object " + object + " at " + object.getClass().getPackageName() + "has wrong ID\nIt cannot be identified");
        }

        return true;
    }
}