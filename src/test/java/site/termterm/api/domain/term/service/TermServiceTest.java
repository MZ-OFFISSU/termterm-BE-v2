package site.termterm.api.domain.term.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.dummy.DummyObject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static site.termterm.api.domain.member.dto.MemberInfoDto.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TermServiceTest extends DummyObject {
    @InjectMocks
    TermService termService;

    @Mock
    private TermRepository termRepository;


}