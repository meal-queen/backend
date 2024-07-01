package com.threlease.base.functions.pay;

import com.threlease.base.entities.*;
import com.threlease.base.enums.OrderStatus;
import com.threlease.base.functions.auth.AuthService;
import com.threlease.base.functions.company.CompanyService;
import com.threlease.base.functions.pay.dto.PayDto;
import com.threlease.base.functions.restaurant.RestaurantService;
import com.threlease.base.utils.responses.BasicResponse;
import com.threlease.base.utils.responses.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/pay")
@Tag(name = "pay API")
@AllArgsConstructor
public class PayController {
    private final PayService payService;
    private final AuthService authService;
    private final CompanyService companyService;
    private final RestaurantService restaurantService;

    @PostMapping("/")
    @Operation(summary = "결제", description = "결제")
    private ResponseEntity<BasicResponse> pay(
            @RequestBody @Valid PayDto dto,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);
        Optional<CompanyEntity> company = companyService.findOneByUuid(dto.getCompany());
        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        if (company.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .build()
            );
        Optional<CompanyConnectEntity> connect = companyService.findOneByConnectAuthor(company.get(), user.get());
        if (connect.isEmpty())
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        if (connect.get().getPoint() < dto.getPoint())
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of("소지하고 계신 포인트가 비용보다 적습니다."))
                            .build()
            );

        Optional<RestaurantEntity> restaurant = restaurantService.findOneByUuid(dto.getRestaurant());
        if (restaurant.isPresent()) {
            connect.get().setPoint(connect.get().getPoint() - dto.getPoint());

            OrderEntity pay = OrderEntity.builder()
                    .company(company.get())
                    .status(OrderStatus.WAIT)
                    .author(user.get())
                    .point(dto.getPoint())
                    .createdAt(LocalDateTime.now())
                    .build();

            payService.save(pay);
            companyService.connectSave(connect.get());

            return ResponseEntity.status(201).body(
                    BasicResponse.builder()
                            .success(true)
                            .build()
            );
        } else
            return ResponseEntity.status(403).body(
                BasicResponse.builder()
                        .success(false)
                        .message(Optional.of(Messages.NOT_FOUND_RESTAURANT))
                        .build()
            );
    }
}
