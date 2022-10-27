package pl.newsler.components.user.event;

import org.springframework.data.jpa.repository.JpaRepository;

interface NLUserEventStreamRepositoryJpa extends JpaRepository<NLUserEventStream, Long> {
}
