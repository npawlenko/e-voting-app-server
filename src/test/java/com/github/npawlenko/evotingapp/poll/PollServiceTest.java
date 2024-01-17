package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import com.github.npawlenko.evotingapp.utils.EmailUtility;
import com.github.npawlenko.evotingapp.voteToken.VoteTokenRepository;
import jakarta.annotation.Resource;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.USER_NOT_LOGGED_IN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PollServiceTest {
    @Mock
    private AuthenticatedUserUtility authenticatedUserUtility;

    @Mock
    private AuthorizationUtility authorizationUtility;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailUtility emailUtility;

    @Mock
    private PollMapper pollMapper;

    @Mock
    private VoteTokenRepository voteTokenRepository;

    @Mock
    private PollRepository pollRepository;

    @InjectMocks
    private PollService pollService;

    @Before
    public void setUp() throws Exception {
        // Initialize mocks created above
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Method under test: {@link PollService#accessibleForUserPolls(int, int)}
     */
    @Test
    void testAccessibleForUserPolls_ReturnsEmptyList() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(Optional.of(new User()));
        when(pollRepository.findAccessibleForUserPolls(Mockito.<Long>any(), Mockito.any())).thenReturn(new ArrayList<>());
        assertTrue(pollService.accessibleForUserPolls(10, 0).isEmpty());
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollRepository).findAccessibleForUserPolls(Mockito.<Long>any(), Mockito.any());
    }

    /**
     * Method under test: {@link PollService#accessibleForUserPolls(int, int)}
     */
    @Test
    void testAccessibleForUserPolls_ThrowsApiRequestException_UserNotLoggedIn() {
        when(authenticatedUserUtility.getLoggedUser()).thenThrow(new ApiRequestException(USER_NOT_LOGGED_IN));
        assertThrows(ApiRequestException.class, () -> pollService.accessibleForUserPolls(10, 0));
        verify(authenticatedUserUtility).getLoggedUser();
    }

    /**
     * Method under test: {@link PollService#accessibleForUserPolls(int, int)}
     */
    @Test
    void testAccessibleForUserPolls_ReturnsPollList() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(pollRepository.findAccessibleForUserPolls(Mockito.<Long>any(), Mockito.any())).thenReturn(new ArrayList<>() {{
            add(new Poll());
        }});
        assertThat(pollService.accessibleForUserPolls(10, 0)).hasSize(1);
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollRepository).findAccessibleForUserPolls(Mockito.<Long>any(), Mockito.any());
    }

    /**
     * Method under test: {@link PollService#userPolls(int, int)}
     */
    @Test
    void testUserPolls_ReturnsEmptyList() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(Optional.of(new User()));
        when(pollRepository.findPollByCreatorId(Mockito.<Long>any(), Mockito.any())).thenReturn(new ArrayList<>());
        assertTrue(pollService.userPolls(10, 0).isEmpty());
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollRepository).findPollByCreatorId(Mockito.<Long>any(), Mockito.any());
    }

    /**
     * Method under test: {@link PollService#userPolls(int, int)}
     */
    @Test
    void testUserPolls_ThrowsApiRequestException_UserNotLoggedIn() {
        when(authenticatedUserUtility.getLoggedUser())
                .thenThrow(new ApiRequestException(USER_NOT_LOGGED_IN));
        assertThrows(ApiRequestException.class, () -> pollService.userPolls(10, 0));
        verify(authenticatedUserUtility).getLoggedUser();
    }

    /**
     * Method under test: {@link PollService#userPolls(int, int)}
     */
    @Test
    void testUserPolls_ReturnsPollList() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(Optional.of(new User()));
        when(pollRepository.findPollByCreatorId(Mockito.<Long>any(), Mockito.any())).thenReturn(new ArrayList<>() {{
            add(new Poll());
        }});
        assertThat(pollService.userPolls(10, 0)).hasSize(1);
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollRepository).findPollByCreatorId(Mockito.<Long>any(), Mockito.any());
    }

    /**
     * Method under test: {@link PollService#createPoll(PollRequest)}
     */
    @Test
    void testCreatePoll_CreatesPoll() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(Optional.of(new User()));
        PollResponse pollResponse = new PollResponse(1L, null, null, null, true, null, null, null, null, false);
        when(pollMapper.pollToPollResponse(Mockito.any())).thenReturn(pollResponse);
        when(pollMapper.pollRequestToPoll(Mockito.any())).thenReturn(new Poll());
        when(pollRepository.save(Mockito.any())).thenReturn(new Poll());
        assertSame(pollResponse, pollService.createPoll(new PollRequest("Question", LocalDateTime.MAX, new ArrayList<>(), new ArrayList<>(), true, new ArrayList<>())));
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollMapper).pollRequestToPoll(Mockito.any());
        verify(pollMapper).pollToPollResponse(Mockito.any());
        verify(pollRepository).save(Mockito.any());
    }

    /**
     * Method under test: {@link PollService#createPoll(PollRequest)}
     */
    @Test
    void testCreatePoll_ThrowsApiRequestException_UserNotLoggedIn() {
        when(authenticatedUserUtility.getLoggedUser())
                .thenThrow(new ApiRequestException(USER_NOT_LOGGED_IN, "Args"));
        assertThrows(ApiRequestException.class, () -> pollService.createPoll(new PollRequest("Question", LocalDateTime.MAX, new ArrayList<>(), new ArrayList<>(), true, new ArrayList<>())));
        verify(authenticatedUserUtility).getLoggedUser();
    }

    /**
     * Method under test: {@link PollService#updatePoll(Long, PollRequest)}
     */
    @Test
    void testUpdatePoll_UpdatesPoll() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(Optional.of(new User()));
        doNothing().when(authorizationUtility).requireAdminOrOwnerPermission(Mockito.any(), Mockito.any());
        PollResponse pollResponse = new PollResponse(1L, null, null, null, true, null, null, null, null, false);
        when(pollMapper.pollToPollResponse(Mockito.any())).thenReturn(pollResponse);
        when(pollMapper.pollRequestToPoll(Mockito.any())).thenReturn(new Poll());
        when(pollRepository.save(Mockito.any())).thenReturn(new Poll());
        when(pollRepository.findById(Mockito.<Long>any())).thenReturn(Optional.of(new Poll()));
        assertSame(pollResponse, pollService.updatePoll(1L, new PollRequest("Question", LocalDateTime.MAX, new ArrayList<>(), new ArrayList<>(), true, new ArrayList<>())));
        verify(authenticatedUserUtility).getLoggedUser();
        verify(authorizationUtility).requireAdminOrOwnerPermission(Mockito.any(), Mockito.any());
        verify(pollMapper).updatePoll(Mockito.any(), Mockito.any());
        verify(pollMapper).pollToPollResponse(Mockito.any());
        verify(pollRepository).save(Mockito.any());
        verify(pollRepository).findById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link PollService#updatePoll(Long, PollRequest)}
     */
    @Test
    void testUpdatePoll_ThrowsApiRequestException_UserNotLoggedIn() {
        when(authenticatedUserUtility.getLoggedUser())
                .thenThrow(new ApiRequestException(USER_NOT_LOGGED_IN));
        assertThrows(ApiRequestException.class, () -> pollService.updatePoll(1L, new PollRequest("Question", LocalDateTime.MAX, new ArrayList<>(), new ArrayList<>(), true, new ArrayList<>())));
        verify(authenticatedUserUtility).getLoggedUser();
    }


    /**
     * Method under test: {@link PollService#deletePoll(Long)}
     */
    @Test
    void testDeletePoll_DeletesPoll() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(Optional.of(new User()));
        doNothing().when(authorizationUtility).requireAdminOrOwnerPermission(Mockito.any(), Mockito.any());
        doNothing().when(pollRepository).deleteById(Mockito.<Long>any());
        when(pollRepository.findById(Mockito.<Long>any())).thenReturn(Optional.of(new Poll()));
        pollService.deletePoll(1L);
        verify(authenticatedUserUtility).getLoggedUser();
        verify(authorizationUtility).requireAdminOrOwnerPermission(Mockito.any(), Mockito.any());
        verify(pollRepository).findById(Mockito.<Long>any());
        verify(pollRepository).deleteById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link PollService#deletePoll(Long)}
     */
    @Test
    void testDeletePoll_ThrowsApiRequestException_UserNotLoggedIn() {
        when(authenticatedUserUtility.getLoggedUser())
                .thenThrow(new ApiRequestException(USER_NOT_LOGGED_IN));
        assertThrows(ApiRequestException.class, () -> pollService.deletePoll(1L));
        verify(authenticatedUserUtility).getLoggedUser();
    }
}

