package com.example.ClientSocket_Service.controllers;

import com.example.ClientSocket_Service.dto.RideRequestDto;
import com.example.ClientSocket_Service.dto.RideResponseDto;
import com.example.ClientSocket_Service.dto.UpdateBookingRequestDto;
import com.example.ClientSocket_Service.dto.UpdateBookingResponseDto;
import com.example.UberProject_EntityService.models.BookingStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/api/socket")
public class DriverRequestController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RestTemplate restTemplate;

    public DriverRequestController(SimpMessagingTemplate simpMessagingTemplate,RestTemplate restTemplate){
        this.simpMessagingTemplate=simpMessagingTemplate;
        this.restTemplate=restTemplate;
    }

    @PostMapping("/newride")
    public ResponseEntity<Boolean> raiseRideRequest(@RequestBody RideRequestDto requestDto){
        sendDriversNewRideRequest(requestDto);
        return new ResponseEntity<>(Boolean.TRUE,HttpStatus.OK);
    }

    public void sendDriversNewRideRequest(RideRequestDto requestDto){
        //Ideally the request should go to only the nearby drivers, but for simplicity we send it to every one
        simpMessagingTemplate.convertAndSend("/topic/rideRequest",requestDto);
    }

    @MessageMapping("/rideResponse/{userId}")
    public void rideResponseHandler(@DestinationVariable String userId, RideResponseDto rideResponseDto){
        UpdateBookingRequestDto requestDto=UpdateBookingRequestDto.builder()
                .status(BookingStatus.SCHEDULED)
                .driverId(Long.parseLong(userId))
                .build();
        System.out.println(rideResponseDto.getResponse()+" "+userId);
        ResponseEntity<UpdateBookingResponseDto> result = this.restTemplate.postForEntity("http://localhost:7480/api/v1/booking/"+rideResponseDto.getBookingId(),requestDto, UpdateBookingResponseDto.class);
        System.out.println(result.getStatusCode());
    }
}
