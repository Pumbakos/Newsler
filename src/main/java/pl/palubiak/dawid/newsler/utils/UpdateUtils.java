package pl.palubiak.dawid.newsler.utils;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.Basic;
import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.Optional;

@Service
public class UpdateUtils<S extends DBModel> {
    /**
     * Returns true if given field is updatable, false otherwise
     *
     * @param field field to check update-ability
     * @return true if given field is updatable, false otherwise
     */
    private static boolean isColumnUpdatable(Field field) {
        return field.getAnnotation(Column.class) != null
                && field.getAnnotation(Column.class).insertable()
                && field.getAnnotation(Column.class).updatable();
    }

    /**
     * Updates object under given ID with params from given object in given repository
     *
     * @param repository repository that extends JpaRepository
     * @param object     object from which data is taken
     * @param id         object's ID in repository
     * @return true if object was updated, false otherwise
     * @see JpaRepository
     */
    public boolean update(JpaRepository<S, Long> repository, S object, Long id) {
        Optional<S> optionalObject = repository.findById(id);

        if (optionalObject.isPresent()) {
            S updatableObject = optionalObject.get();

            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                try {
                    if (field.getName().equalsIgnoreCase("id"))
                        continue;

                    Field updatableObjectField = updatableObject.getClass().getDeclaredField(field.getName());
                    Field objectField = object.getClass().getDeclaredField(field.getName());

                    updatableObjectField.setAccessible(true);
                    objectField.setAccessible(true);

                    if (field.getAnnotation(Basic.class) != null && objectField.get(object) == null || isColumnUpdatable(field)) {
                        continue;
                    }

                    updatableObjectField.set(updatableObject, objectField.get(object));

                    updatableObjectField.setAccessible(false);
                    objectField.setAccessible(false);
                } catch (Exception e) {
                    return false;
                }
            }

            repository.save(updatableObject);
            return true;
        }
        return false;
    }
}
