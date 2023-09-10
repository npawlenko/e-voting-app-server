package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.USER_NOT_LOGGED_IN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {PollService.class})
@ExtendWith(SpringExtension.class)
class PollServiceTest {
    @MockBean
    private AuthenticatedUserUtility authenticatedUserUtility;

    @MockBean
    private AuthorizationUtility authorizationUtility;

    @MockBean
    private PollMapper pollMapper;

    @MockBean
    private PollRepository pollRepository;

    @Autowired
    private PollService pollService;

    /**
     * Method under test: {@link PollService#accessibleForUserPolls()}
     */
    @Test
    void testAccessibleForUserPolls_ReturnsEmptyList() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(new User());
        when(pollRepository.findAccessibleForUserPolls(Mockito.<Long>any())).thenReturn(new ArrayList<>());
        assertTrue(pollService.accessibleForUserPolls().isEmpty());
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollRepository).findAccessibleForUserPolls(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link PollService#accessibleForUserPolls()}
     */
    @Test
    void testAccessibleForUserPolls_ThrowsApiRequestException_UserNotLoggedIn() {
        when(authenticatedUserUtility.getLoggedUser()).thenThrow(new ApiRequestException(USER_NOT_LOGGED_IN));
        assertThrows(ApiRequestException.class, () -> pollService.accessibleForUserPolls());
        verify(authenticatedUserUtility).getLoggedUser();
    }

    /**
     * Method under test: {@link PollService#accessibleForUserPolls()}
     */
    @Test
    void testAccessibleForUserPolls_ReturnsPollList() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(User.builder().id(1L).build());
        when(pollRepository.findAccessibleForUserPolls(Mockito.<Long>any())).thenReturn(new ArrayList<>(){{
            add(new Poll());
        }});
        assertThat(pollService.accessibleForUserPolls()).hasSize(1);
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollRepository).findAccessibleForUserPolls(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link PollService#userPolls()}
     */
    @Test
    void testUserPolls_ReturnsEmptyList() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(new User());
        when(pollRepository.findPollByCreatorId(Mockito.<Long>any())).thenReturn(new ArrayList<>());
        assertTrue(pollService.userPolls().isEmpty());
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollRepository).findPollByCreatorId(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link PollService#userPolls()}
     */
    @Test
    void testUserPolls_ThrowsApiRequestException_UserNotLoggedIn() {
        when(authenticatedUserUtility.getLoggedUser())
                .thenThrow(new ApiRequestException(USER_NOT_LOGGED_IN));
        assertThrows(ApiRequestException.class, () -> pollService.userPolls());
        verify(authenticatedUserUtility).getLoggedUser();
    }

    /**
     * Method under test: {@link PollService#userPolls()}
     */
    @Test
    void testUserPolls_ReturnsPollList() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(new User());
        when(pollRepository.findPollByCreatorId(Mockito.<Long>any())).thenReturn(new ArrayList<>(){{
            add(new Poll());
        }});
        assertThat(pollService.userPolls()).hasSize(1);
        verify(authenticatedUserUtility).getLoggedUser();
        verify(pollRepository).findPollByCreatorId(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link PollService#createPoll(PollRequest)}
     */
    @Test
    void testCreatePoll_CreatesPoll() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(new User());
        PollResponse pollResponse = new PollResponse(1L, null, null, null, true, null, null, null);
        when(pollMapper.pollToPollResponse(Mockito.any())).thenReturn(pollResponse);
        when(pollMapper.pollRequestToPoll(Mockito.any())).thenReturn(new Poll());
        when(pollRepository.save(Mockito.any())).thenReturn(new Poll());
        assertSame(pollResponse, pollService.createPoll(new PollRequest("Question", LocalDateTime.MAX, true)));
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
        assertThrows(ApiRequestException.class, () -> pollService.createPoll(new PollRequest("Question", LocalDateTime.MAX, true)));
        verify(authenticatedUserUtility).getLoggedUser();
    }

    /**
     * Method under test: {@link PollService#updatePoll(Long, PollRequest)}
     */
    @Test
    void testUpdatePoll_UpdatesPoll() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(new User());
        doNothing().when(authorizationUtility).requireAdminOrOwnerPermission(Mockito.any(), Mockito.any());
        PollResponse pollResponse = new PollResponse(1L, null, null, null, true, null, null, null);
        when(pollMapper.pollToPollResponse(Mockito.any())).thenReturn(pollResponse);
        when(pollMapper.pollRequestToPoll(Mockito.any())).thenReturn(new Poll());
        when(pollRepository.save(Mockito.any())).thenReturn(new Poll());
        when(pollRepository.findById(Mockito.<Long>any())).thenReturn(Optional.of(new Poll()));
        assertSame(pollResponse, pollService.updatePoll(1L, new PollRequest("Question", LocalDateTime.MAX, true)));
        verify(authenticatedUserUtility).getLoggedUser();
        verify(authorizationUtility).requireAdminOrOwnerPermission(Mockito.any(), Mockito.any());
        verify(pollMapper).pollRequestToPoll(Mockito.any());
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
        assertThrows(ApiRequestException.class, () -> pollService.updatePoll(1L, new PollRequest("Question", LocalDateTime.MAX, true)));
        verify(authenticatedUserUtility).getLoggedUser();
    }


    /**
     * Method under test: {@link PollService#deletePoll(Long)}
     */
    @Test
    void testDeletePoll_DeletesPoll() {
        when(authenticatedUserUtility.getLoggedUser()).thenReturn(new User());
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

