package pl.newsler.components.receiver.dto;

public record ReceiverCreateRequest(String userUuid, String email, String nickname, String firstName, String lastName) {
}
