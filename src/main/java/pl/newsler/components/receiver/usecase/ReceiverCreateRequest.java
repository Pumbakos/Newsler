package pl.newsler.components.receiver.usecase;

public record ReceiverCreateRequest(String userUuid, String email, String nickname, String firstName, String lastName) {
}
