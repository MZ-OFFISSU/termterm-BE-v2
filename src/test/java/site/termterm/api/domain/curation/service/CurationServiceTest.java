package site.termterm.api.domain.curation.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.global.dummy.DummyObject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static site.termterm.api.domain.curation.dto.CurationRequestDto.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CurationServiceTest extends DummyObject {

    @InjectMocks
    private CurationService curationService;

    @Mock
    private CurationRepository curationRepository;

}