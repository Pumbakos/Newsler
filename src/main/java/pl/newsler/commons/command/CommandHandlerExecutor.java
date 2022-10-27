package pl.newsler.commons.command;

@FunctionalInterface
public interface CommandHandlerExecutor {
    void execute(Command command);
}
