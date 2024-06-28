package com.threlease.base.functions.restaurant;

import com.threlease.base.entities.AuthEntity;
import com.threlease.base.entities.OrderEntity;
import com.threlease.base.entities.RestaurantEntity;
import com.threlease.base.enums.OrderStatus;
import com.threlease.base.functions.auth.AuthService;
import com.threlease.base.functions.pay.PayService;
import com.threlease.base.functions.restaurant.dto.CheckPayDto;
import com.threlease.base.functions.restaurant.dto.GetRestaurantPayLogDto;
import com.threlease.base.functions.restaurant.dto.GetRestaurantSearchListDto;
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

    @GetMapping("/")
    @Operation(summary = "가맹점")
    private ResponseEntity<BasicResponse> getRestaurnatList(
            @RequestParam("page") int page,
            @RequestParam("take") int take
    ) {
        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(
                                restaurantService.findByPagination(
                                        PageRequest.of(page, take)
                                )
                        ))
                        .build()
        );
    }

    @GetMapping("/search")
    @Operation(summary = "가맹점 검색 혹은 리스트 가져오기")
    private ResponseEntity<BasicResponse> getRestaurantSearchList(
            @ParameterObject @Valid @RequestParam GetRestaurantSearchListDto dto
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
            @RequestParam("page") int page,
            @RequestParam("take") int take,
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

        Page<OrderEntity> logs = payService.findByRestaurantPagination(PageRequest.of(page, take), restaurant.get());

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(logs))
                        .build()
        );
    }

    @PutMapping("/check")
    @Operation(summary = "결제 확인")
    private ResponseEntity<BasicResponse> checkPay(
            @ModelAttribute @Valid CheckPayDto dto,
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

        Optional<OrderEntity> order = payService.findOneByUuid(dto.getOrder());

        if (order.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_ORDER))
                            .build()
            );

        if (order.get().getStatus() != OrderStatus.WAIT)
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of("승인 대기 중이 아니라면 수정할 수 없습니다."))
                            .build()
            );

        order.get().setStatus(dto.getStatus());

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .build()
        );
    }
}
