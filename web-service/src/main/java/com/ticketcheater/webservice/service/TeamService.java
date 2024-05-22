package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.TeamDTO;
import com.ticketcheater.webservice.entity.Team;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.TeamRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.regex.Pattern;

@Slf4j
@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    @Transactional
    public TeamDTO createTeam(String name) {
        teamRepository.findByName(name).ifPresent(it -> {
            throw new WebApplicationException(ErrorCode.TEAM_ALREADY_EXISTS, String.format("team with name %s already exists", name));
        });

        if(isInvalidTeam(name)) {
            throw new WebApplicationException(ErrorCode.INVALID_TEAM, String.format("team with name %s is not valid", name));
        }

        Team team = teamRepository.save(Team.of(name));

        log.info("create team method executed successfully for team: team id={}", team.getId());

        return TeamDTO.toDTO(team);
    }

    @Transactional
    public TeamDTO updateTeam(Long teamId, String name) {
        Team team = teamRepository.findByIdAndDeletedAtIsNull(teamId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.TEAM_NOT_FOUND, String.format("team id %d not found", teamId))
        );

        if(isInvalidTeam(name)) {
            throw new WebApplicationException(ErrorCode.INVALID_TEAM, String.format("team with name %s is not valid", name));
        }

        team.setName(name);
        teamRepository.saveAndFlush(team);

        log.info("update team method executed successfully for team: team id={}", teamId);

        return TeamDTO.toDTO(team);
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findByIdAndDeletedAtIsNull(teamId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.TEAM_NOT_FOUND, String.format("team id %d not found", teamId))
        );

        team.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        teamRepository.saveAndFlush(team);

        log.info("delete team method executed successfully for team: team id={}", teamId);
    }

    @Transactional
    public void restoreTeam(Long teamId) {
        Team team = teamRepository.findByIdAndDeletedAtIsNotNull(teamId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.TEAM_ALREADY_EXISTS, String.format("team id %d already exists", teamId))
        );

        team.setDeletedAt(null);
        teamRepository.saveAndFlush(team);

        log.info("restore team method executed successfully for team: team id={}", teamId);
    }


    private boolean isInvalidTeam(String name) {
        return !Pattern.matches("^[가-힇]*$", name);
    }

}
