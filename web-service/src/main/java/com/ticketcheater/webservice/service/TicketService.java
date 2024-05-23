package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.GameRepository;
import com.ticketcheater.webservice.repository.GradeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final GameRepository gameRepository;
    private final GradeRepository gradeRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void createTickets(Long gameId, Long gradeId, int quantity, int price) {
        if(quantity <= 0) {
            throw new WebApplicationException(ErrorCode.INVALID_TICKET_QUANTITY, String.format("ticket quantity cannot be %d", quantity));
        }
        if(price <= 0) {
            throw new WebApplicationException(ErrorCode.INVALID_TICKET_PRICE, String.format("ticket price cannot be %d", price));
        }

        findGameById(gameId);
        findGradeById(gradeId);

        int batchSize = 1000;
        List<Object[]> batchArgs = new ArrayList<>();

        for(int i=0; i<quantity; i++) {
            batchArgs.add(new Object[]{gameId, gradeId, price, false});
            if(batchArgs.size() == batchSize) {
                executeBatchInsert(batchArgs);
                batchArgs.clear();
            }
        }

        if(!batchArgs.isEmpty()) {
            executeBatchInsert(batchArgs);
        }

        log.info("create tickets method executed successfully: game id={}, grade id={}, quantity={}, price={}", gameId, gradeId, quantity, price);
    }

    private void executeBatchInsert(List<Object[]> batchArgs) {
        String sql = "INSERT INTO ticket (game_id, member_id, grade_id, price, is_reserved) VALUES (?, null, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void findGameById(Long gameId) {
        gameRepository.findByIdAndDeletedAtIsNull(gameId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GAME_NOT_FOUND, String.format("game id %d not found", gameId))
        );
    }

    private void findGradeById(Long gradeId) {
        gradeRepository.findByIdAndDeletedAtIsNull(gradeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GRADE_NOT_FOUND, String.format("grade id %d not found", gradeId))
        );
    }

}
