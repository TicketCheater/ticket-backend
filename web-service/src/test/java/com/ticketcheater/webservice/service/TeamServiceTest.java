package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.entity.Team;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.TeamFixture;
import com.ticketcheater.webservice.repository.TeamRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Business Logic - 팀")
@SpringBootTest
class TeamServiceTest {

    @Autowired
    TeamService sut;

    @MockBean
    TeamRepository teamRepository;

    @DisplayName("팀 생성 정상 동작")
    @Test
    void givenTeam_whenCreate_thenCreatesTeam() {
        String name = "기아타이거즈";
        Team team = TeamFixture.get(name);

        when(teamRepository.save(any())).thenReturn(team);
        when(teamRepository.findByName(name)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> sut.createTeam(name));
    }

    @DisplayName("이미 존재하는 팀 이름으로 생성 시 오류 발생")
    @Test
    void givenExistentTeam_whenCreate_thenThrowsError() {
        String name = "기아타이거즈";
        Team team = TeamFixture.get(name);

        when(teamRepository.findByName(name)).thenReturn(Optional.of(team));

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.createTeam(name)
        );

        Assertions.assertEquals(ErrorCode.TEAM_ALREADY_EXISTS, exception.getCode());
    }

    @DisplayName("부적절한 팀 이름으로 생성 시 오류 발생")
    @Test
    void givenInvalidTeam_whenCreate_thenThrowsError() {
        String name = "KIATigers";

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.createTeam(name)
        );

        Assertions.assertEquals(ErrorCode.INVALID_TEAM, exception.getCode());
    }

    @DisplayName("팀 수정 정상 동작")
    @Test
    void givenTeamIdAndName_whenUpdate_thenUpdatesTeam() {
        Long teamId = 1L;
        String name = "기아타이거즈";
        Team team = TeamFixture.get("기아타이거즈");

        when(teamRepository.findByIdAndDeletedAtIsNull(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.saveAndFlush(any())).thenReturn(team);

        Assertions.assertDoesNotThrow(() -> sut.updateTeam(teamId, name));
    }

    @DisplayName("없는 팀 수정 시 오류 발생")
    @Test
    void givenNonExistentTeam_whenUpdate_thenThrowsError() {
        Long teamId = 1L;
        String name = "기아타이거즈";

        when(teamRepository.findByIdAndDeletedAtIsNull(teamId)).thenReturn(Optional.empty());

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.updateTeam(teamId, name)
        );

        Assertions.assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getCode());
    }

    @DisplayName("부적절한 팀 이름으로 수정 시 오류 발생")
    @Test
    void givenInvalidTeamName_whenUpdate_thenThrowsError() {
        Long teamId = 1L;
        String name = "KiaTigers";

        Team team = TeamFixture.get("KiaTigers");

        when(teamRepository.findByIdAndDeletedAtIsNull(teamId)).thenReturn(Optional.of(team));

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.updateTeam(teamId, name)
        );

        Assertions.assertEquals(ErrorCode.INVALID_TEAM, exception.getCode());
    }

    @DisplayName("팀 삭제 정상 동작")
    @Test
    void givenTeamId_whenDelete_thenDeletesTeam() {
        Long teamId = 1L;
        Team team = TeamFixture.get("KiaTigers");

        when(teamRepository.findByIdAndDeletedAtIsNull(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.saveAndFlush(any())).thenReturn(team);

        Assertions.assertDoesNotThrow(() -> sut.deleteTeam(teamId));
    }

    @DisplayName("없는 팀 삭제 시 오류 발생")
    @Test
    void givenNonExistentTeam_whenDelete_thenThrowsError() {
        Long teamId = 1L;

        when(teamRepository.findByIdAndDeletedAtIsNull(teamId)).thenReturn(Optional.empty());

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.deleteTeam(teamId)
        );

        Assertions.assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getCode());
    }

    @DisplayName("팀 복구 정상 동작")
    @Test
    void givenDeletedTeamId_whenRestore_thenRestoresTeam() {
        Long teamId = 1L;
        Team team = TeamFixture.get("KiaTigers");
        team.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        when(teamRepository.findByIdAndDeletedAtIsNotNull(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.saveAndFlush(any())).thenReturn(team);

        Assertions.assertDoesNotThrow(() -> sut.restoreTeam(teamId));
    }

    @DisplayName("있는 팀 복구 시 오류 발생")
    @Test
    void givenExistentTeam_whenRestore_thenThrowsError() {
        Long teamId = 1L;

        when(teamRepository.findByIdAndDeletedAtIsNotNull(teamId)).thenReturn(Optional.empty());

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.restoreTeam(teamId)
        );

        Assertions.assertEquals(ErrorCode.TEAM_ALREADY_EXISTS, exception.getCode());
    }

}
