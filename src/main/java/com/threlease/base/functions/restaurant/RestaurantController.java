package com.threlease.base.functions.restaurant;

import com.threlease.base.entities.AuthEntity;
import com.threlease.base.entities.PayLogEntity;
import com.threlease.base.entities.RestaurantEntity;
import com.threlease.base.functions.auth.AuthService;
import com.threlease.base.functions.pay.PayService;
import com.threlease.base.functions.restaurant.dto.GetRestaurantPayLogDto;
import com.threlease.base.functions.restaurant.dto.GetRestaurnatList;
import com.threlease.base.utils.responses.BasicResponse;
import com.threlease.base.utils.responses.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/restaurant")
@Tag(name = "restaurant api", description = "가맹점 관련 API")
@AllArgsConstructor
public class RestaurantController {
    private final AuthService authService;
    private final RestaurantService restaurantService;
    private final PayService payService;

    @GetMapping("/{uuid}")
    @Operation(summary = "가맹점 정보", description = "가맹점 정보 GET")
    private ResponseEntity<BasicResponse> getRestaurant(
            @PathVariable("uuid") String uuid,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        Optional<RestaurantEntity> restaurant = restaurantService.findOneByUuid(uuid);

        return restaurant.map(restaurantEntity -> ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(restaurantEntity))
                        .build()
        )).orElseGet(() -> ResponseEntity.status(404).body(
                BasicResponse.builder()
                        .success(false)
                        .message(Optional.of(Messages.NOT_FOUND_RESTAURANT))
                        .data(Optional.empty())
                        .build()
        ));
    }

    @GetMapping("/search")
    @Operation(summary = "가맹점 검색 혹은 리스트 가져오기")
    private ResponseEntity<BasicResponse> getRestaurantList(
            @ParameterObject @Valid @RequestParam GetRestaurnatList dto
    ) {
        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(
                                    restaurantService.findByNameLikeOrLikeAddress(
                                            PageRequest.of(dto.getPage(), dto.getTake()),
                                            dto.getSearch().get(),
                                            dto.getSearch().get()
                                    )
                                )
                        )
                        .build()
        );
    }

    @GetMapping("/pay/log")
    @Operation(summary = "가맹점 내 결제 정보", description = "가맹점 결제 정보")
    private ResponseEntity<BasicResponse> getRestaurantPayLog(
            @ParameterObject @Valid @RequestParam GetRestaurantPayLogDto dto,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .build()
            );

        Optional<RestaurantEntity> restaurant = restaurantService.findOneByUuid(dto.getRestaurant());

        if (restaurant.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_RESTAURANT))
                            .build()
            );

        if (restaurant.get().getAuthor() != user.get())
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        Page<PayLogEntity> logs = payService.findByRestaurantPagination(PageRequest.of(dto.getPage(), dto.getTake()), restaurant.get());

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(logs))
                        .build()
        );
    }
}
