package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.TeamDTO;
import com.ticketcheater.webservice.entity.Team;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    @Transactional
    public TeamDTO createTeam(String name) {
        teamRepository.findByNameAndDeletedAtIsNull(name).ifPresent(it -> {
            throw new WebApplicationException(ErrorCode.TEAM_ALREADY_EXISTS, String.format("team is %s", name));
        });

        if(isInvalidTeam(name)) {
            throw new WebApplicationException(ErrorCode.INVALID_TEAM, String.format("team is %s", name));
        }

        Team team = teamRepository.save(Team.of(name));

        return TeamDTO.toDTO(team);
    }

    @Transactional
    public TeamDTO updateTeam(Long teamId, String name) {
        Team team = teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.TEAM_NOT_FOUND));

        if(isInvalidTeam(name)) {
            throw new WebApplicationException(ErrorCode.INVALID_TEAM, String.format("team is %s", name));
        }

        team.setName(name);
        teamRepository.saveAndFlush(team);

        return TeamDTO.toDTO(team);
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.TEAM_NOT_FOUND));

        team.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        teamRepository.saveAndFlush(team);
    }

    @Transactional
    public void restoreTeam(Long teamId) {
        Team team = teamRepository.findByIdAndDeletedAtIsNotNull(teamId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.TEAM_ALREADY_EXISTS));

        team.setDeletedAt(null);
        teamRepository.saveAndFlush(team);
    }


    private boolean isInvalidTeam(String name) {
        return !Pattern.matches("^[가-힇]*$", name);
    }

}
