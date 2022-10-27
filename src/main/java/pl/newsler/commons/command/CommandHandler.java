package pl.newsler.commons.command;

import java.lang.reflect.ParameterizedType;

@FunctionalInterface
@SuppressWarnings({"unchecked"})
public interface CommandHandler<T extends Command> {
    default Class<T> handlingCommandClass() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    void handle(T command);
}
